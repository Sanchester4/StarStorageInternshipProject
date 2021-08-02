package InternshipDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
	static Connection conn = null;

	public DbConnection() {
	}

	public Connection getDBConnection() throws SQLException, ClassNotFoundException {
		try {

			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/stock_market?autoReconnect=true&useSSL=FALSE", "root", "");
			if (conn != null) {
				System.out.print("");
			}

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());

			e.printStackTrace();
		}
		return conn;
	}

}
