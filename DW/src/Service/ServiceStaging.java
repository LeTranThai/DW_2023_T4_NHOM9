package Service;

//import com.opencsv.CSVReader;
//import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.IOException;
import DAO.DAOConfig;
//import DAO.DAOLog;
import db.DBConnect;
import Model.ConfigModel;
import Model.LogModel;
import org.apache.poi.util.SystemOutLogger;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class ServiceStaging {
    static DBConnect connDBControl = null;

    PreparedStatement ps = null;
    ResultSet rs = null;

    private static String inputDateFormat = "dd/MM/yyyy"; // Định dạng ngày tháng trong file CSV
    private static String outputDateFormat = "yyyy-MM-dd";// Định dạng ngày tháng cho cơ sở dữ liệu

    private int idKeyConfigGenerate;

    private LogModel log;
    private Object reader;
    private String sql;

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
                + "address TEXT not null,"
                + "pricebuy INT not null,"
                + "pricesell INTnot null ,"
                + "address TEXT not null,"
                + "dayUpdate Date not null,"
                + "timeUpdate Time not null,"
                + "type TEXT not null,"
                + ")";
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableQuery);
        }
    }
    public void insert(String pathFileTemp, String nameDBStaging) {
        try {
            DBConnect connDBStaging = new DBConnect(nameDBStaging, "root", "");
//            CSVReader reader = new CSVReader(new FileReader(pathFileTemp));

//            String[] header = reader.readNext(); // Assuming the first row is the header

//             Prepare the SQL statement for insertion
//            String sql = "INSERT INTO data (" + String.join(",", header) + ") VALUES (" +
//                    "?,".repeat(header.length - 1) + "?)";
            try (PreparedStatement statement = connDBStaging.preStatement(sql)) {
                String[] nextLine;
//                while ((nextLine = reader.readNext()) != null) {
//                    // Đọc từng dòng dữ liệu và thực hiện các thao tác tiếp theo
//                    for (int i = 0; i < nextLine.length; i++) {
//                        statement.setString(i + 1, nextLine[i]);
//                    }
//                    statement.addBatch();
//                }

                // Execute batch insert
                statement.executeBatch();

//                Log
                ps.executeUpdate();

            } catch (Exception e) {
                e.printStackTrace();
                LogModel log = new LogModel();
                log.setDescription("Lấy dữ liệu không thành không !");
                String query1 = "INSERT INTO logs (date,ip_address, description) VALUES (?, ?, ?)";
                PreparedStatement ps = connDBControl.preStatement(query1);
                ps.setString(1, String.valueOf(log.getDate()));
                ps.setString(2, log.getIpAddress());
                ps.setString(3, log.getDescription());
//
//                InetAddress inetAddress = InetAddress.getLocalHost();
//                log.setDate(Timestamp.valueOf(LocalDateTime.now()));
//                log.setIpAddress(inetAddress.getHostAddress());
                log.setDescription("Không kết nối được database Staging");
                String query = "INSERT INTO logs (date ,ip_address, description) VALUES (?, ?, ?)";
                PreparedStatement ps1 = connDBControl.preStatement(query);
                ps1.setString(1, String.valueOf(log.getDate()));
                ps1.setString(2, log.getIpAddress());
                ps1.setString(3, log.getDescription());
                ps1.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processStaging(DAOConfig daoConfig,int idKeyConfigGenerate, String pathFileTemp, String nameDBStaging, String dateGet) {
        DBConnect connDBStaging = new DBConnect(nameDBStaging, "root", "");

        try {
            daoConfig.updateStatus(idKeyConfigGenerate, "Staging");
            if (tableExists(connDBStaging.getConnection(), "data")) {
                System.out.println("Bảng tồn tại.");
            } else {
                createTable(connDBStaging.getConnection(), "data");
                System.out.println("Bảng không tồn tại.");
            }

            if (isDateExists(connDBControl.getConnection(), dateGet)) {
                deleteDataFromStaging(connDBStaging.getConnection(), dateGet);
            }
            insert(pathFileTemp, nameDBStaging);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteDataFromStaging(@NotNull Connection connection, String csvDate) throws SQLException {
        String query = "DELETE FROM data WHERE date_create_data = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, csvDate);
            preparedStatement.executeUpdate();
            System.out.println("b:" + csvDate);
        }
    }

    private boolean isDateExists(Connection connection, String csvDate) throws SQLException, ParseException {
        String query = "SELECT DATE_FORMAT(date, '%d/%m/%Y') AS Ngay_dd_mm_yyyy\n" +
                "FROM logs\n" +
                "WHERE DATE(date) = ?";
        Date date = convertDateFormat(csvDate, inputDateFormat, outputDateFormat);
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, String.valueOf(date));
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("a:" + date);
            return resultSet.next();
        }
    }

    private static Date convertDateFormat(String inputDate, String inputDateFormat, String outputDateFormat)
            throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputDateFormat);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputDateFormat);

        java.util.Date utilDate = inputFormat.parse(inputDate); // Sửa lỗi ở đây
        String formattedDate = outputFormat.format(utilDate);

        return Date.valueOf(formattedDate);
    }

}