package datawarehouse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Lớp DataMart được sử dụng để truyền dữ liệu từ cơ sở dữ liệu này sang cơ sở dữ liệu khác.
 * Nó kết nối với cả cơ sở dữ liệu nguồn và đích và truyền dữ liệu từ các bảng được chỉ định.
 */
public class DataMart {

    /**
     * Phương pháp chính thúc đẩy quá trình truyền dữ liệu.
     *
     * @param args Đối số dòng lệnh (không được sử dụng trong chương trình này).
     */
    public static void main(String[] args) {
    	// Thông tin kết nối cơ sở dữ liệu cho nguồn (MySQL)
        String sourceUrl = "jdbc:mysql://localhost:3306/data_warehouse";
        String sourceUsername = "root";
        String sourcePassword = "";
      
        // Thông tin kết nối cơ sở dữ liệu cho đích (PostgreSQL)
        String destinationUrl = "jdbc:mysql://localhost:3306/data_mart";
        String destinationUsername = "root";
        String destinationPassword = "";

        Connection sourceConnection = null;
        Connection destinationConnection = null;

        try {
        	// Kết nối với cơ sở dữ liệu nguồn
            sourceConnection = DriverManager.getConnection(sourceUrl, sourceUsername, sourcePassword);

            // Kết nối với cơ sở dữ liệu đích
            destinationConnection = DriverManager.getConnection(destinationUrl, destinationUsername, destinationPassword);

            // Truyền dữ liệu từ bảng date_dim
            transferTable(sourceConnection, destinationConnection, "date_dim");

            // Truyền dữ liệu từ bảng goldprice_fact
            transferTable(sourceConnection, destinationConnection, "goldprice_fact");

            // Truyền dữ liệu từ bảng Province_dim
            transferTable(sourceConnection, destinationConnection, "province_dim");

            // Truyền dữ liệu từ bảng typegold_dim
            transferTable(sourceConnection, destinationConnection, "typegold_dim");

            System.out.println("Data transfer successful!");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Đóng cả hai kết nối
            try {
                if (sourceConnection != null) {
                    sourceConnection.close();
                }
                if (destinationConnection != null) {
                    destinationConnection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Chuyển dữ liệu từ bảng nguồn sang bảng đích.
     *
     * @param sourceConnection Kết nối tới cơ sở dữ liệu nguồn.
     * @param DestinationConnection Kết nối tới cơ sở dữ liệu đích.
     * @param tableName Tên của bảng để truyền dữ liệu từ đó.
     * @throws SQLException Nếu có vấn đề với việc truy cập cơ sở dữ liệu.
     */
    private static void transferTable(Connection sourceConnection, Connection destinationConnection, String tableName) throws SQLException {
        // Truy vấn để chọn dữ liệu từ bảng nguồn
        String selectQuery = "SELECT * FROM " + tableName;
        PreparedStatement selectStatement = sourceConnection.prepareStatement(selectQuery);
        ResultSet resultSet = selectStatement.executeQuery();

        // Xác định truy vấn chèn dựa trên tên bảng
        String insertQuery;
        switch (tableName) {
            case "date_dim":
                insertQuery = "INSERT INTO date_dim (id, date) VALUES (?, ?)";
                break;
            case "goldprice_fact":
                insertQuery = "INSERT INTO goldprice_fact (id, id_date, id_type, id_province, price_sell, price_buy, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                break;
            case "province_dim":
                insertQuery = "INSERT INTO province_dim (id, name) VALUES (?, ?)";
                break;
            case "typegold_dim":
                insertQuery = "INSERT INTO typegold_dim (id, typeName) VALUES (?, ?)";
                break;
            default:
                throw new SQLException("Invalid table name");
        }

        PreparedStatement insertStatement = destinationConnection.prepareStatement(insertQuery);

        // Xử lý từng hàng của bảng nguồn và chèn nó vào bảng đích
        while (resultSet.next()) {
            // Đặt giá trị cho câu lệnh chèn dựa trên bảng
            switch (tableName) {
                case "date_dim":
                    insertStatement.setInt(1, resultSet.getInt("id"));
                    insertStatement.setDate(2, resultSet.getDate("date"));
                    break;
                case "goldprice_fact":
                    insertStatement.setInt(1, resultSet.getInt("id"));
                    insertStatement.setInt(2, resultSet.getInt("id_date"));
                    insertStatement.setInt(3, resultSet.getInt("id_type"));
                    insertStatement.setInt(4, resultSet.getInt("id_province"));
                    insertStatement.setDouble(5, resultSet.getDouble("price_sell"));
                    insertStatement.setDouble(6, resultSet.getDouble("price_buy"));
                    insertStatement.setDate(7, resultSet.getDate("start_date"));
                    insertStatement.setDate(8, resultSet.getDate("end_date"));
                    break;
                case "province_dim":
                    insertStatement.setInt(1, resultSet.getInt("id"));
                    insertStatement.setString(2, resultSet.getString("name"));
                    break;
                case "typegold_dim":
                    insertStatement.setInt(1, resultSet.getInt("id"));
                    insertStatement.setString(2, resultSet.getString("typeName"));
                    break;
            }

            // Thực hiện lệnh chèn
            insertStatement.executeUpdate();
        }
    }
}
