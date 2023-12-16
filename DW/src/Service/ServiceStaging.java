package Service;
import java.sql.*;

import Model.LogModel;
import db.DBConnect;

public class ServiceStaging {
    static DBConnect connDBControl = null;

    PreparedStatement ps = null;
    ResultSet rs = null;

    private static String inputDateFormat = "dd/MM/yyyy"; // Định dạng ngày tháng trong file CSV
    private static String outputDateFormat = "yyyy-MM-dd";// Định dạng ngày tháng cho cơ sở dữ liệu

    private int idKeyConfigGenerate;

    private LogModel log;

    public ServiceStaging(LogModel log, int idKeyConfigGenerate) {
        //2. Connect Database CONTROL
        connDBControl = new DBConnect("control", "root", "");
        this.log = log;
        this.idKeyConfigGenerate = idKeyConfigGenerate;
    }
    public static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, tableName, null);
        return resultSet.next();
    }
    private static void createTable(Connection connection, String tableName) throws SQLException {
        String createTableQuery = "CREATE TABLE " + tableName + " ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + " TEXT not null,"
                + "date_create_data TEXT not null,"
                + "category TEXT not null ,"
                + "address TEXT not null,"
                + "price TEXT not null,"
                + "area TEXT not null,"
                + "href TEXT not null,"
                + "description TEXT not null,"
                + "create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
        }
    }
}