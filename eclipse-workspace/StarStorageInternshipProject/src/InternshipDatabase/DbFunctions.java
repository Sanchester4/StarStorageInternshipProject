package InternshipDatabase;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;
import org.apache.commons.lang.StringUtils;

import InternshipMainInterface.MainClass;
import InternshipUtils.RegexClass;

public class DbFunctions extends DbConnection implements DatabaseInterface {
	protected static PrintStream fileOut = null;
	protected static String urlThis;
	protected PrintStream originalOut = System.out;

	public DbFunctions() {
	}

	public void addCategory(String category) {
		Vector<String> categories = new Vector<String>();
		try {
			Connection conn = getDBConnection();
			String query = "SELECT * FROM `categories`";
			PreparedStatement pst = conn.prepareStatement(query);
			ResultSet rs = pst.executeQuery(query);
			String item = null;
			while (rs.next()) {
				item = rs.getString("category");
				categories.add(item);
			}
			for (String str : categories) {
				if (str.equals(category)) {
					getTime();
					System.out.println("There exist such category!");
					return;
				} else {
					continue;
				}
			}

			String query2 = "INSERT INTO `categories`" + "(category)" + " VALUES (?)";
			PreparedStatement pst2 = conn.prepareStatement(query2);
			pst2.setString(1, category);
			pst2.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addNewProd(String name, String category, int quantity, int price) {
		Vector<String> products = new Vector<String>();
		try {
			if ((quantity < 0 & price < 0) || (quantity % 2 != 0 & price % 2 != 0)) {
				getTime();
				System.out.println("The quantity or price is not an integer!");
				return;
			}
			Connection conn = getDBConnection();
			String sql = "SELECT * FROM `categories` WHERE `category`='" + category + "'";
			String sql2 = "SELECT * FROM `stock`";
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			String gettedCategory = "";
			while (rs.next()) {
				gettedCategory = rs.getString("category");
			}
			ResultSet rs2 = stm.executeQuery(sql2);
			while (rs2.next()) {
				products.add(rs2.getString("name"));
			}
			for (String str : products) {
				if (str.equals(name)) {
					getTime();
					System.out.println("There already exists such product!");
					return;
				}
			}
			if (!gettedCategory.equals(category)) {
				getTime();
				System.out.println("There's no such category in stock!");
				return;
			} else {
				String query = "INSERT INTO `stock`" + "(category, name, quantity, price, maxQuantity)"
						+ " VALUES (?,?,?,?,?)";
				PreparedStatement pst = conn.prepareStatement(query);
				pst.setString(1, category);
				pst.setString(2, name);
				pst.setInt(3, (int) quantity);
				pst.setFloat(4, price);
				pst.setFloat(5, 50);
				pst.executeUpdate();
				conn.close();
				getTime();
				System.out.println(quantity + " " + name + " have been added to the " + category);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void removeProd(String name) {
		try {
			Connection conn = getDBConnection();
			Statement pst = conn.createStatement();
			PreparedStatement stm = conn.prepareStatement("SELECT * FROM `stock` WHERE `name`='" + name + "'");
			ResultSet rs = stm.executeQuery();
			Integer quantity = null;
			while (rs.next()) {
				quantity = rs.getInt("quantity");
				if (quantity > 0) {
					getTime();
					System.out.println(
							"Cannot remove " + name + " because quantity is not zero. Quantity is " + quantity);
					return;
				}
			}
			if (quantity == null) {
				System.out.println("There's no such item in database!");
				return;
			}
			String sql = "DELETE FROM `stock` WHERE `name`='" + name + "'";
			pst.executeUpdate(sql);
			System.out.println("Item was deleted!");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void replenishProdQuant(String name, int num) {
		try {
			Connection conn = getDBConnection();
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery("SELECT * FROM `stock` WHERE `name`='" + name + "'");
			int quantity = 0;
			int maxQuantity = 0;
			getTime();
			while (rs.next()) {
				quantity = rs.getInt("quantity");
				maxQuantity = rs.getInt("maxQuantity");
				if (quantity == maxQuantity) {
					System.out.println("The quantity of " + name + " surpass the MaxQuantity available!");
					return;
				} else {
					PreparedStatement pst = conn
							.prepareStatement("UPDATE `stock` SET `quantity`=? WHERE `name`='" + name + "'");
					pst.setInt(1, num + quantity);
					pst.executeUpdate();
					System.out.println(name + " has been replenished by " + num);

				}
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void buyProd(String name, int num, String username) {
		try {
			Connection conn = getDBConnection();
			Connection conn2 = getDBConnection();
			Statement stm = conn2.createStatement();
			ResultSet rs = stm.executeQuery("SELECT * FROM `clients` WHERE `username`='" + username + "'");
			Integer balance = null;
			getTime();
			while (rs.next()) {
				balance = rs.getInt("balance");
			}
			Statement stm1 = conn.createStatement();
			ResultSet rs1 = null;
			rs1 = stm1.executeQuery("SELECT * FROM `stock` WHERE `name`='" + name + "'");
			Integer quantity = null;
			Integer price = null;
			while (rs1.next()) {
				quantity = rs1.getInt("quantity");
				price = rs1.getInt("price");
				if (balance == null) {
					System.out.println("There's no such user!");
					return;
				} else if (quantity <= 0) {
					System.out.println("product is unvailable!");
					return;
				} else if (balance < (price * num)) {
					System.out.println("You don't have enough money!");
					return;
				} else if (quantity < num) {
					System.out.println("Thre's no such quantity of this product!");
					return;
				} else {
					PreparedStatement pst = conn
							.prepareStatement("UPDATE `stock` SET `quantity`=? WHERE `name`='" + name + "'");
					pst.setInt(1, quantity - num);
					pst.executeUpdate();
					PreparedStatement pst1 = conn2
							.prepareStatement("UPDATE `clients` SET `balance`=? WHERE `username`='" + username + "'");
					pst1.setInt(1, balance - (price * num));
					pst1.executeUpdate();
					System.out.println("User " + username + " has bought " + num + " " + name);
				}

			}
			if (quantity == null || price == null) {
				System.out.println("There's no such product!");
				return;
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void printAllProd() {
		try {
			Connection conn = getDBConnection();
			Statement pst = conn.createStatement();
			ResultSet rs = pst.executeQuery("SELECT * FROM `stock`");
			int rowCount = 1;
			while (rs.next()) {
				getTime();
				System.out.println(rowCount + " " + rs.getString("name") + " " + rs.getInt("quantity") + " "
						+ rs.getString("category") + " " + rs.getInt("price"));
				rowCount++;

			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void printProdCategory(String category) {
		try {
			Connection conn = getDBConnection();
			Statement pst = conn.createStatement();
			ResultSet rs = pst.executeQuery("SELECT * FROM `stock` HAVING `category`='" + category + "'");
			int rowCount = 1;
			while (rs.next()) {
				getTime();
				System.out.println(
						rowCount + " " + rs.getString("name") + " " + rs.getInt("quantity") + " " + rs.getInt("price"));
				rowCount++;

			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void printCategory() {
		Vector<String> categories = new Vector<String>();
		try {
			DbConnection connection = new DbConnection();
			Connection conn = connection.getDBConnection();
			Statement pst = conn.createStatement();
			ResultSet rs = pst.executeQuery("SELECT * FROM `categories`");
			getTime();
			while (rs.next()) {
				categories.add(rs.getString("category"));
			}
			System.out.print(categories.get(0));
			for (int i = 1; i < categories.size(); i++) {
				System.out.print(", " + categories.get(i));
			}
			System.out.println("");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void printProdName(String name) {
		try {
			Connection conn = getDBConnection();
			Statement pst = conn.createStatement();
			ResultSet rs = pst.executeQuery("SELECT * FROM `stock` HAVING `name`='" + name + "'");
			getTime();
			while (rs.next()) {
				System.out.println(rs.getString("name") + " " + rs.getInt("quantity") + " " + rs.getInt("price"));
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void createDb() {
		try {
			Connection conn = getDBConnection();
			Statement pst = conn.createStatement();
			pst.executeUpdate("CREATE TABLE stock" + "(id INTEGER not NULL AUTO_INCREMENT, " + "category VARCHAR(50), "
					+ "  name VARCHAR(50), " + " quantity INTEGER, " + " price INTEGER, " + " maxQuantity INTEGER, "
					+ " PRIMARY KEY ( id ))");
			pst.executeUpdate("CREATE TABLE clients" + "(id INTEGER not NULL AUTO_INCREMENT, "
					+ "username VARCHAR(50), " + " balance FLOAT, " + " PRIMARY KEY ( id ))");
			pst.executeUpdate("CREATE TABLE categories" + "(id INTEGER not NULL AUTO_INCREMENT, "
					+ "category VARCHAR(50), " + " PRIMARY KEY ( id ))");
			System.out.println("The system was initialised succesfull!");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void freeDb() {
		try {
			Connection conn = getDBConnection();
			Statement pst = conn.createStatement();
			pst.executeUpdate("DROP TABLE `stock`");
			pst.executeUpdate("DROP TABLE `clients`");
			pst.executeUpdate("DROP TABLE `categories`");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	public void fillStock(Vector<StockItem> item) {
		try {
			Connection conn = getDBConnection();
			String query = "INSERT INTO `stock`" + "(category, name, quantity, price, maxQuantity)"
					+ " VALUES (?,?,?,?,?)";
			for (StockItem object : item) {
				PreparedStatement pst = conn.prepareStatement(query);
				pst.setString(1, object.getCategory());
				pst.setString(2, object.getName());
				pst.setInt(3, (int) object.getQuantity());
				pst.setFloat(4, object.getPrice());
				pst.setInt(5, (int) object.getMaxQuantity());
				pst.executeUpdate();
			}

		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void fillCategory(String category) {
		try {
			Connection conn = getDBConnection();

			String sql = "INSERT INTO `categories`" + "(category)" + " VALUES(?)";

			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, category);
			pst.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addClient(String username, float balance) {
		try {
			Connection conn = getDBConnection();
			String query = "INSERT INTO `clients`" + "(username, balance)" + " VALUES (?,?)";
			PreparedStatement pst = conn.prepareStatement(query);
			pst.setString(1, username);
			pst.setFloat(2, balance);
			pst.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public ResultSet retrieveData(String query) {
		ResultSet rs = null;
		try {
			Connection conn = getDBConnection();
			PreparedStatement pst = conn.prepareStatement(query);
			rs = pst.executeQuery();
		} catch (SQLException | ClassNotFoundException e) {

			e.printStackTrace();
		}

		return rs;

	}

	public void readInput(String url) {
		try {
			String result;
			urlThis = url;
			fileOut = new PrintStream(url);

			System.setOut(fileOut);
			RegexClass regexx = new RegexClass();
			originalOut.println("Please input commands");
			try {
				try (Scanner scanner = new Scanner(System.in)) {
					while (scanner.hasNextLine()) {
						String[] tokens = scanner.nextLine().split("\\s");
						result = StringUtils.join(tokens, " ");
						System.out.println(
								new SimpleDateFormat("[" + "yyyy-MM-dd HH:mm:ss, SSS" + "] ").format(new Date())
										+ result);
						if (result.toUpperCase().equals("SWITCH DISPLAY_MODE CONSOLE")) {
							originalOut.println("Console input was opened: ");
							System.setOut(originalOut);
							MainClass.loopInput();
							break;
						}
						regexx.match(result);
					}
					scanner.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	public void getTime() {
		if (fileOut != null) {
			fileOut.append(new SimpleDateFormat("[" + "yyyy-MM-dd HH:mm:ss, SSS" + "] ").format(new Date()));
		} else {
			return;
		}
	}
}
