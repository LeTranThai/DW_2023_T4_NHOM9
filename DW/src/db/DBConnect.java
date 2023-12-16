package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    public DBConnect(String control, String root, String s) {
    }

    public static void main(String[] args) {
        // Thông tin kết nối cơ sở dữ liệu MariaDB
        String url = "jdbc:mysql://localhost:3066/condbstaging";
        String username = "root";
        String password = "";

        try {
            // Đăng ký driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Kết nối cơ sở dữ liệu
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Kết nối thành công!");

            // Thực hiện các thao tác trên cơ sở dữ liệu ở đây (nếu cần)

            // Đóng kết nối sau khi sử dụng
            connection.close();
            System.out.println("Đã đóng kết nối.");
        } catch (ClassNotFoundException e) {
            System.err.println("Lỗi: Không tìm thấy driver JDBC");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
