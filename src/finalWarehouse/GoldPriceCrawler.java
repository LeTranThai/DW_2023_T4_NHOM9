package finalWarehouse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class GoldPriceCrawler {
	public static void main(String[] args) throws IOException, SQLException {
		// 1. Kết nối đến cơ sở dữ liệu
		String jdbcUrl = "jdbc:mysql://localhost:3306/warehouse";
		String username = "root";
		String password = "";

		try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
			// 2. Kiểm tra kết nối thành công
			if (connection != null) {
				insertCheckpointStatus(connection, "connect db success");
				System.out.println("Connected to the database");

				// 3. Thực hiện crawling 
				crawlAndWriteToXLSX(connection);
			} else {
				// 2.1 Kết nối không thành công(connect faild)
				insertCheckpointStatus(connection, "CNF");
				System.out.println("Failed to connect to the database");
			}
		}
	}

	private static void crawlAndWriteToXLSX(Connection connection) throws IOException, SQLException {
		String url = "https://giavang.org/";
		Document document = Jsoup.connect(url).get();

		// 4. Truy cập url chọn tiêu đề cột
		Elements headerElements = document.select("table thead th");

		// 5. Chọn dữ liệu trong các ô
		Elements rowElements = document.select("table tbody tr");

		// 6. Tạo tên file dựa trên ngày giờ hiện tại
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String timestamp = dateFormat.format(new Date());
		String fileName = timestamp + ".xlsx";
		String outputFilePath = "D:\\2023_2024_HK1\\data_wahouse\\finalWarehouse\\" + fileName;

		try (Workbook workbook = new XSSFWorkbook()) {
			// 7. Tạo một sheet mới,Ghi tiêu đề vào sheet
			Sheet sheet = workbook.createSheet("Giá vàng");

			Row headerRow = sheet.createRow(0);
			int colNum = 0;
			for (Element header : headerElements) {
				Cell cell = headerRow.createCell(colNum++);
				cell.setCellValue(header.text());
			}

			// 8. Ghi dữ liệu vào sheet
			int rowNum = 1;
			for (Element row : rowElements) {
				Row sheetRow = sheet.createRow(rowNum++);
				Iterator<Element> cellIterator = row.select("th,td").iterator();
				int cellNum = 0;
				while (cellIterator.hasNext()) {
					Cell cell = sheetRow.createCell(cellNum++);
					Element cellElement = cellIterator.next();
					String cellText = cellElement.text();
					cell.setCellValue(cellText);
				}
			}

			// 9. Ghi workbook ra file
			try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
				workbook.write(fileOut);
				System.out.println("Đã ghi file Excel thành công.");
				insertConfigs(connection, url, outputFilePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 11. Insert một dòng mới vào bảng Checkpoint Table
		insertCheckpointStatus(connection, "ES");
		connection.close();

		

	}

	private static void insertCheckpointStatus(Connection connection, String newStatusValue) throws SQLException {
		String insertQuery = "INSERT INTO data_checkpoints (note, create_at) VALUES (?, NOW())";

		try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
			preparedStatement.setString(1, newStatusValue);

			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println(rowsAffected + " record(s) inserted successfully");
			} else {
				System.out.println("No records were inserted");
			}
			preparedStatement.close();
		}
	}

	private static void insertConfigs(Connection connection, String url, String source) throws SQLException {
		String insertQuery = "INSERT INTO data_file_configs (name, source_path, location, format) VALUES (?,?,?,?)";

		try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
			preparedStatement.setString(1, "giavang");
			preparedStatement.setString(2, url);
			preparedStatement.setString(3, source);
			preparedStatement.setString(4, "xlsx");
			int rowsAffected = preparedStatement.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println(rowsAffected + " record(s) inserted successfully");
			} else {
				System.out.println("No records were inserted");
			}
			preparedStatement.close();
		}
	}
}
