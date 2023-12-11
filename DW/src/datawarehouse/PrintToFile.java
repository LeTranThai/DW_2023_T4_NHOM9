package datawarehouse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Lớp PrintToFile cung cấp chức năng ghi dữ liệu vào tệp có dấu thời gian.
 */
public class PrintToFile {

    /**
     * Ghi dữ liệu được cung cấp vào một tệp có dấu thời gian hiện tại.
     *
     * @param data Dữ liệu cần ghi vào tập tin.
     */
    public static void WriteToday(String data) {
        // DateTimeFormatter để định dạng dấu thời gian theo mẫu đã chỉ định
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"); //yyyy/MM-dd.HH-mm-ss  
        LocalDateTime now1 = LocalDateTime.now();  
        System.out.println(dtf1.format(now1));

        // Xây dựng tên tệp bằng dấu thời gian hiện tại
        String fileName = dtf1.format(now1);
        File f = new File("D:\\DataWarehouse\\datamart\\DW" + fileName + ".txt");
        System.out.println(f.getAbsolutePath());

        try {
            // PrintWriter để ghi dữ liệu vào tập tin
            PrintWriter writer = new PrintWriter(new FileOutputStream(f, true), true);
            // DateTimeFormatter cho dấu thời gian nhập dữ liệu
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
            LocalDateTime now = LocalDateTime.now();  

            // Ghi dữ liệu cùng với dấu thời gian hiện tại
            writer.println(data + ", " + dtf.format(now));
            writer.close();
        } catch (FileNotFoundException e) {
            // Xử lý ngoại lệ trong trường hợp không tìm thấy tệp
            e.printStackTrace();
        }
    }
}
