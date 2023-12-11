package data_warehouse;

import datawarehouse.DateNow;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExtractData {

    public static void main(String[] args) {

        String dataLink = "https://www.pnj.com.vn/blog/gia-vang";
        String dateNow = DateNow.getDate();

        List<String[]> hoChiMinhData = fetchData(dataLink + "/?zone=00", "Hồ Chí Minh");
        List<String[]> canThoData = fetchData(dataLink + "/?zone=07", "Cần Thơ");
        List<String[]> HaNoiData = fetchData(dataLink + "/?zone=11", "Hà Nội");
        List<String[]> DaNangData = fetchData(dataLink + "/?zone=13", "Đà Nẵng");
        List<String[]> TayNguyenData = fetchData(dataLink + "/?zone=14", "Tây Nguyên");
        List<String[]> DNBData = fetchData(dataLink + "/?zone=21", "Đông Nam Bộ");
        // Ghi dữ liệu vào cùng một file Excel
        writeToExcel(hoChiMinhData, canThoData, HaNoiData, DaNangData, TayNguyenData, DNBData, dateNow + ".xlsx");

        System.out.println("Dữ liệu đã được ghi vào file Excel.");
        // ghi lại status vô bảng log
    }

    private static List<String[]> fetchData(String url, String cityName) {
        List<String[]> data = new ArrayList<>();

        try {
            // Kết nối với trang web
            Document doc = Jsoup.connect(url).get();

            // Lấy ngày cập nhật
            String ngayCapNhatStr = doc.select("#time-now").text();

            LocalDateTime ngayCapNhat = parseNgayCapNhat(ngayCapNhatStr);

            // Lấy danh sách các hàng trong bảng giá vàng
            Elements rows = doc.select("#content-price tr");

            // Duyệt qua từng hàng trong bảng
            for (Element row : rows) {
                // Lấy dữ liệu từ các ô trong hàng
                Elements columns = row.select("td");
                String loaiVang = columns.get(0).text();
                String giaMua = columns.get(1).select("span").text();
                String giaBan = columns.get(2).select("span").text();

                // Thêm dữ liệu vào danh sách
                String[] rowData = { cityName, ngayCapNhat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy/HH/mm")), loaiVang,
                        giaMua, giaBan };
                data.add(rowData);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    private static LocalDateTime parseNgayCapNhat(String ngayCapNhatStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return LocalDateTime.parse(ngayCapNhatStr, formatter);
    }

    private static void writeToExcel(List<String[]> hoChiMinhData, List<String[]> canThoData, List<String[]> HaNoiData,
                                     List<String[]> DaNangData, List<String[]> TayNguyenData, List<String[]> DNBData, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Tạo một sheet trong workbook
            Sheet sheet = workbook.createSheet("GiaVang");

            // Ghi dữ liệu cho Hồ Chí Minh
            writeDataToSheet(hoChiMinhData, sheet);

            // Ghi dữ liệu cho Cần Thơ
            writeDataToSheet(canThoData, sheet);

            // Ghi dữ liệu cho Hà Nội
            writeDataToSheet(HaNoiData, sheet);

            // Ghi dữ liệu cho Đà Nẵng
            writeDataToSheet(DaNangData, sheet);

            // Ghi dữ liệu cho Tây Nguyên
            writeDataToSheet(TayNguyenData, sheet);

            // Ghi dữ liệu cho Đông Nam Bộ
            writeDataToSheet(DNBData, sheet);

            // Ghi workbook vào file Excel
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private static void writeDataToSheet(List<String[]> data, Sheet sheet) {
        int rowNum = sheet.getLastRowNum() + 1; // Lấy số dòng hiện tại và tăng thêm 1
        for (String[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (String value : rowData) {
                Cell cell = row.createCell(colNum++);
                cell.setCellValue(value);
            }
        }

    }

}
