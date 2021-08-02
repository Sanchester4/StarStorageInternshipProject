package InternshipDatabase;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DbUtils {
	private String urlFile;
	Vector<StockItem> item = new Vector<StockItem>();

	public DbUtils() {
		this.urlFile = null;
	}

	public void setUrlFile(String url) {
		this.urlFile = url;
	}

	public void getDataJson() {
		Vector<String> categories = new Vector<String>();
		try (FileReader reader = new FileReader(getUrlFile())) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(reader);
			JSONArray stock = (JSONArray) jsonObject.get("stock");
			JSONArray clients = (JSONArray) jsonObject.get("clients");
			DbFunctions transfer = new DbFunctions();
			for (int i = 0; i < stock.size(); i++) {
				JSONObject objects = (JSONObject) stock.get(i);
				String category = (String) objects.get("category");
				String name = (String) objects.get("name");
				long quantity = (long) objects.get("quantity");
				long price = (long) objects.get("price");
				long maxQuantity = (long) objects.get("maxQuantity");
				categories.add(category);
				item.add(new StockItem(category, name, quantity, price, maxQuantity));
			}
			transfer.fillStock(item);
			LinkedHashSet<String> lHSet = new LinkedHashSet<String>(categories);
			categories.clear();
			categories.addAll(lHSet);
			for (String str : categories) {
				transfer.fillCategory(str);
			}
			for (int i = 0; i < clients.size(); i++) {
				JSONObject objects = (JSONObject) clients.get(i);
				String username = (String) objects.get("username");
				long balance = (long) objects.get("balance");
				transfer.addClient(username, balance);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void writeDataJson(String url) throws SQLException {
		JSONObject jsonObject = new JSONObject();
		JSONArray arrayStock = new JSONArray();
		JSONArray arrayClients = new JSONArray();
		DbFunctions stock = new DbFunctions();
		ResultSet rs = stock.retrieveData("SELECT * FROM `stock`");
		ResultSet rs2 = stock.retrieveData("SELECT * FROM `clients`");
		while (rs.next()) {
			JSONObject record = new JSONObject();
			record.put("category", rs.getString("category"));
			record.put("name", rs.getString("name"));
			record.put("quantity", rs.getInt("quantity"));
			record.put("price", rs.getInt("price"));
			record.put("maxQuantity", rs.getInt("maxQuantity"));
			arrayStock.add(record);
		}
		while (rs2.next()) {
			JSONObject record2 = new JSONObject();
			record2.put("username", rs2.getString("username"));
			record2.put("balance", rs2.getInt("balance"));
			arrayClients.add(record2);
		}
		jsonObject.put("stock", arrayStock);
		jsonObject.put("clients", arrayClients);
		try {
			FileWriter file = new FileWriter(url, false);

			file.write(jsonObject.toString());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getUrlFile() {
		return urlFile;
	}

}
