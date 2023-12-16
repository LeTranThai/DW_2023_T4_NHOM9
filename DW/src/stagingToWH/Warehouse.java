package stagingToWH;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.*;

public class Warehouse {

	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/";
	private static final String USER = "root";
	private static final String PASSWORD = "";

	public static void exportWH() {
		try (
				// 1. Kết nối CSDL
				Connection controlConnection = DriverManager.getConnection(JDBC_URL + "data_control", USER, PASSWORD);
				Connection stagingConnection = DriverManager.getConnection(JDBC_URL + "data_staging", USER, PASSWORD);
				Connection warehouseConnection = DriverManager.getConnection(JDBC_URL + "data_warehouse", USER,
						PASSWORD)) {
			// 2. Kiểm tra xem dữ liệu staging đã SuccessStaging chưa
			if (!isStagingSuccess(controlConnection)) {
				System.out.println("Staging has not been completed successfully. Exiting...");
				// 2.1 Ghi log checkpoint note = "error staging" và Đóng kết nối CSDL

				insertCheckpointStatus(controlConnection, "notify staginged");
				controlConnection.close();
				stagingConnection.close();
				warehouseConnection.close();
				return;
			} else {
				// 3. Ghi log checkpoint khi bắt đầu tiến trình
				insertCheckpointStatus(controlConnection, "ST");

				// 4. Kiểm tra note trong bảng checkpointtrong vòng 1h hôm nay = successTranform
				// chưa
				if (isTransformationCompleted(controlConnection)) {
					System.out.println("Transformation has already been completed. Exiting...");
					// 4.1 Ghi log checkpoint note = "notify transformed" và Đóng kết nối csdl

					insertCheckpointStatus(controlConnection, "notify transformed");
					controlConnection.close();
					stagingConnection.close();
					warehouseConnection.close();
					return;
				} else {

					// 5. Ghi log checkpoint trước khi bắt đầu quá trình transform
					insertCheckpointStatus(controlConnection, "Tranforming");
					//6. Gọi hàm processAndInsertData để bắt dầu stranform
					processAndInsertData(stagingConnection, warehouseConnection);

					// 9. Insert dữ liệu vào bảng aggregate
					insertIntoAggregateTable(warehouseConnection);
					//10. Cập nhật giá vàng mua và bán cao nhất và thấp nhất trong bảng aggregate_gold 
					updateMaxMinPrices(warehouseConnection);

					//11. Ghi log checkpoint sau khi hoàn thành quá trình transform
					insertCheckpointStatus(controlConnection, "SuccessTransform");
					//12. Đóng kết nối csdl
					stagingConnection.close();
					warehouseConnection.close();
					controlConnection.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Kiểm tra xem quá trình transform đã hoàn thành chưa
	 * 
	 * @param controlConnection
	 * @return true nếu đã hoàn thành, false nếu chưa
	 * @throws SQLException
	 */
	private static boolean isTransformationCompleted(Connection controlConnection) throws SQLException {
		String checkStatusQuery = "SELECT COUNT(*) FROM data_checkpoints WHERE note = 'SuccessTransform' AND create_at >= NOW() - INTERVAL 1 HOUR";

		try (Statement statement = controlConnection.createStatement();
				ResultSet resultSet = statement.executeQuery(checkStatusQuery)) {
			resultSet.next();
			int count = resultSet.getInt(1);
			return count > 0;
		}
	}

	/**
	 * Kiểm tra xem quá trình staging đã hoàn thành chưa
	 * 
	 * @param controlConnection
	 * @return true nếu đã hoàn thành, false nếu chưa
	 * @throws SQLException
	 */
	private static boolean isStagingSuccess(Connection controlConnection) throws SQLException {
		String checkStatusQuery = "SELECT COUNT(*) FROM data_checkpoints WHERE note = 'SuccessStaging' AND create_at >= NOW() - INTERVAL 1 HOUR";

		try (Statement statement = controlConnection.createStatement();
				ResultSet resultSet = statement.executeQuery(checkStatusQuery)) {
			resultSet.next();
			int count = resultSet.getInt(1);
			return count > 0;
		}
	}

	/**
	 * Xử lý và chèn dữ liệu từ bảng staging vào các bảng dim
	 * 
	 * @param sourceConnection
	 * @param targetConnection
	 */
	private static void processAndInsertData(Connection sourceConnection, Connection targetConnection) {
		try {
			//6.1 Gọi hàm processDistinctData để truy vấn table date_dim db data_warehouse  
			processDistinctData(sourceConnection, targetConnection, "dayUpdate", "date_dim", "date");
			//6.4 gọi hàm processDistinctData để truy vấn table province_dim data_warehouse
			processDistinctData(sourceConnection, targetConnection, "address", "province_dim", "name");
			//6.5 gọi hàm processDistinctData để truy vấn table typegold_dim data_warehouse
			processDistinctData(sourceConnection, targetConnection, "type", "typegold_dim", "typeName");
//			7. Insert dữ liệu mới vào bảng goldprice_fact từ bảng example và các bảng dim
			insertIntoGoldPriceFact(targetConnection);

		} catch (SQLException e) {
			handleError(targetConnection, "Error during data processing: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Xử lý dữ liệu duy nhất từ bảng staging và chèn vào các bảng chi tiết
	 * 
	 * @param sourceConnection
	 * @param targetConnection
	 * @param columnName
	 * @param targetTable
	 * @param targetColumn
	 * @throws SQLException
	 */
	private static void processDistinctData(Connection sourceConnection, Connection targetConnection, String columnName,
			String targetTable, String targetColumn) throws SQLException {
//		6.2 Truy vấn dữ liệu duy nhất từ cột tương ứng trong bảng example của db data_staging
		String selectStagingQuery = "SELECT DISTINCT " + columnName + " FROM example";
	
		String selectQuery = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", targetTable, targetColumn);
		String insertQuery = String.format("INSERT INTO %s (%s) VALUES (?)", targetTable, targetColumn);

		try (Statement sourceStatement = sourceConnection.createStatement();
				ResultSet resultSet = sourceStatement.executeQuery(selectStagingQuery);
				PreparedStatement selectStatement = targetConnection.prepareStatement(selectQuery);
				PreparedStatement insertStatement = targetConnection.prepareStatement(insertQuery)) {
			while (resultSet.next()) {
				String value = resultSet.getString(columnName);
				// 6.3 Kiểm tra giá trị trong bảng dim có giá trị giống với dữ liệu trong bảng example của db data_staging không
				if (!recordExists(selectStatement, value)) {
					// 6.3.1 Chèn dữ liệu duy nhất vào bảng dim tương ứng
					insertRecord(insertStatement, value);
				}
			}
		}
	}

	/**
	 * Chèn dữ liệu vào bảng goldprice_fact
	 * 
	 * @param targetConnection
	 */
	private static void insertIntoGoldPriceFact(Connection targetConnection) {
		// Cập nhật bản ghi có end_date là NULL trong bảng goldprice_fact
		String updateDateExpiredQuery = "UPDATE goldprice_fact SET end_date = NOW() WHERE end_date IS NULL";

		
		String insertIntoGoldPriceFactQuery = "INSERT INTO goldprice_fact (id_date, id_province, id_type, price_buy, price_sell, start_date, end_date) "
				+ "SELECT dd.id, pd.id, td.id, e.priceBuy, e.priceSell, NOW(), NULL " + "FROM data_staging.example e "
				+ "JOIN data_warehouse.date_dim dd ON e.dayUpdate = dd.date "
				+ "JOIN data_warehouse.province_dim pd ON e.address = pd.name "
				+ "JOIN data_warehouse.typegold_dim td ON e.type = td.typeName";

		try (Statement updateStatement = targetConnection.createStatement();
				Statement insertStatement = targetConnection.createStatement()) {
			// 7. cập nhật end_date =now() cho các bản ghi hiện tại là null trong bảng goldprice_fact
			updateStatement.executeUpdate(updateDateExpiredQuery);

//			8. Insert dữ liệu mới vào bảng goldprice_fact từ bảng example và các bảng dim
			int rowsAffected = insertStatement.executeUpdate(insertIntoGoldPriceFactQuery);

			if (rowsAffected > 0) {
				System.out.println(rowsAffected + " record(s) inserted successfully into goldprice_fact");
			} else {
				System.out.println("No records were inserted into goldprice_fact");
			}
		} catch (SQLException e) {
			handleError(targetConnection, "Error during goldprice_fact data insertion: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Cập nhật giá vàng mua và bán cao nhất và thấp nhất trong bảng aggregate_gold
	 * 
	 * @param targetConnection
	 */
	private static void updateMaxMinPrices(Connection targetConnection) {
		String updatePricesQuery = "UPDATE aggregate_gold " + "SET "
				+ "hight_priceBuy = (SELECT MAX(price_buy) FROM goldprice_fact WHERE aggregate_gold.id_date = goldprice_fact.id_date AND aggregate_gold.id_province = goldprice_fact.id_province AND aggregate_gold.id_type = goldprice_fact.id_type), "
				+ "low_priceBuy = (SELECT MIN(price_buy) FROM goldprice_fact WHERE aggregate_gold.id_date = goldprice_fact.id_date AND aggregate_gold.id_province = goldprice_fact.id_province AND aggregate_gold.id_type = goldprice_fact.id_type), "
				+ "hight_priceSell = (SELECT MAX(price_sell) FROM goldprice_fact WHERE aggregate_gold.id_date = goldprice_fact.id_date AND aggregate_gold.id_province = goldprice_fact.id_province AND aggregate_gold.id_type = goldprice_fact.id_type), "
				+ "low_priceSell = (SELECT MIN(price_sell) FROM goldprice_fact WHERE aggregate_gold.id_date = goldprice_fact.id_date AND aggregate_gold.id_province = goldprice_fact.id_province AND aggregate_gold.id_type = goldprice_fact.id_type)";

		try (Statement updateStatement = targetConnection.createStatement()) {
			int rowsAffected = updateStatement.executeUpdate(updatePricesQuery);

			if (rowsAffected > 0) {
				System.out.println(rowsAffected + " record(s) updated successfully in aggregate_gold");
			} else {
				System.out.println("No records were updated in aggregate_gold");
			}
		} catch (SQLException e) {
			handleError(targetConnection, "Error during aggregate_gold data update: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Phương thưc insert dữ liệu vào bảng aggregate_gold
	 * 
	 * @param targetConnection
	 */
	private static void insertIntoAggregateTable(Connection targetConnection) {
		String insertIntoAggregateQuery = "INSERT INTO aggregate_gold (id_date, id_province, id_type, averagePriceBuy, averagePriceSell) "
				+ "SELECT dd.id, pd.id, td.id, AVG(gf.price_buy), AVG(gf.price_sell) " + "FROM goldprice_fact gf "
				+ "JOIN date_dim dd ON gf.id_date = dd.id " + "JOIN province_dim pd ON gf.id_province = pd.id "
				+ "JOIN typegold_dim td ON gf.id_type = td.id " + "GROUP BY dd.id, pd.id, td.id";

		try (Statement insertStatement = targetConnection.createStatement()) {
			int rowsAffected = insertStatement.executeUpdate(insertIntoAggregateQuery);

			if (rowsAffected > 0) {
				System.out.println(rowsAffected + " record(s) inserted successfully into aggregate_gold");
			} else {
				System.out.println("No records were inserted into aggregate_gold");
			}
		} catch (SQLException e) {
			handleError(targetConnection, "Error during aggregate_gold data insertion: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Chèn log checkpoint
	 * 
	 * @param connection
	 * @param newStatusValue
	 * @throws SQLException
	 */
	private static void insertCheckpointStatus(Connection connection, String newStatusValue) throws SQLException {
		String insertQuery = "INSERT INTO data_checkpoints (note, create_at) VALUES (?, NOW())";

		try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
			preparedStatement.setString(1, newStatusValue);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println(rowsAffected + " record(s) inserted successfully into data_checkpoints");
			} else {
				System.out.println("No records were inserted into data_checkpoints");
			}
		} catch (SQLException e) {
			handleError(connection, "Error during data_checkpoints insertion: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Kiểm tra sự tồn tại của một giá trị trong bảng đích
	 * 
	 * @param statement
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	private static boolean recordExists(PreparedStatement statement, String value) throws SQLException {
		statement.setString(1, value);
		try (ResultSet resultSet = statement.executeQuery()) {
			resultSet.next();
			return resultSet.getInt(1) > 0;
		}
	}

	/**
	 * Chèn một bản ghi mới vào bảng đích
	 * 
	 * @param statement
	 * @param value
	 * @throws SQLException
	 */
	private static void insertRecord(PreparedStatement statement, String value) throws SQLException {
		statement.setString(1, value);
		int rowsAffected = statement.executeUpdate();

		if (rowsAffected > 0) {
			System.out.println(rowsAffected + " record(s) inserted successfully");
		} else {
			System.out.println("No records were inserted");
		}
	}

	/**
	 * Xử lý lỗi
	 * 
	 * @param connection
	 * @param errorMessage
	 */
	private static void handleError(Connection connection, String errorMessage) {
		System.err.println(errorMessage);
		// Thực hiện xử lý lỗi (nếu cần)
	}
}
