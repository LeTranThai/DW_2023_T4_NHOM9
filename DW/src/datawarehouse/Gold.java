package datawarehouse;

/**
 * Lớp Vàng thể hiện thông tin về giá vàng, bao gồm các chi tiết như khu vực, 
 * hệ thống, 
 * giá mua và bán cũng như thời gian thu thập dữ liệu.
 */
public class Gold {
    private String id;
    private String khuVuc;
    private String heThong;
    private int giaMua;
    private int giaBan;
    private int chenhLech;
    private String timeCrawlData;

    /**
     * Xây dựng một phiên bản Vàng mới.
      *
      * @param id Mã định danh duy nhất cho dữ liệu vàng.
      * @param khuVuc Khu vực áp dụng giá vàng.
      * @param heThong Hệ thống hoặc nguồn dữ liệu giá vàng.
      * @param giaMua Giá mua vàng.
      * @param giaBan Giá bán vàng.
      * @param chenhLech Sự chênh lệch giữa giá mua và giá bán.
      * @param timeCrawlData Thời điểm dữ liệu được thu thập hoặc thu thập.
     */
    public Gold(String id, String khuVuc, String heThong, int giaMua, int giaBan, int chenhLech, String timeCrawlData) {
        this.id = id;
        this.khuVuc = khuVuc;
        this.heThong = heThong;
        this.giaMua = giaMua;
        this.giaBan = giaBan;
        this.chenhLech = chenhLech;
        this.timeCrawlData = timeCrawlData;
    }
    /**
      *	Lấy vùng dữ liệu vàng.
      *
      * @return Vùng dưới dạng Chuỗi.
     */
    public String getKhuVuc() {
        return khuVuc;
    }

    /**
      * Lấy hệ thống hoặc nguồn dữ liệu giá vàng.
      *
      * @return Hệ thống dưới dạng Chuỗi.
     */
    public String getHeThong() {
        return heThong;
    }

    /**
     * Nhận được giá mua vàng.
     * 
     * @return Giá mua là một số nguyên.
     */
    public int getGiaMua() {
        return giaMua;
    }

    /**
     * Nhận được giá bán vàng.
     * 
     * @return Giá bán là số nguyên.
     */
    public int getGiaBan() {
        return giaBan;
    }

    /**
     * Nhận chênh lệch giữa giá mua và giá bán vàng.
     * 
     * @return Sự khác biệt như một số nguyên.
     */
    public int getChenhLech() {
        return chenhLech;
    }

    /**
     * Lấy mã định danh duy nhất của dữ liệu vàng.
     * 
     * @return ID dưới dạng Chuỗi.
     */
    public String getId() {
        return id;
    }

    /**
     * Lấy thời điểm dữ liệu giá vàng được thu thập hoặc thu thập.
     * 
     * @return Thời điểm thu thập dữ liệu dưới dạng Chuỗi.
     */
    public String getTimeCrawlData() {
        return timeCrawlData;
    }
    /**
     * Trả về một chuỗi biểu diễn đối tượng Gold.
     * 
     * @return Một chuỗi chứa chi tiết dữ liệu vàng.
     */
    @Override
    public String toString() {
        return "Gold{" +
                "id=" + id +
                ", khuVuc='" + khuVuc + '\'' +
                ", heThong='" + heThong + '\'' +
                ", giaMua=" + giaMua +
                ", giaBan=" + giaBan +
                ", chenhLech=" + chenhLech +
                ", timeCrawlData='" + timeCrawlData + '\'' +
                '}';
    }

    /**
     * Trả về một chuỗi biểu diễn tùy chỉnh của đối tượng Vàng, được định dạng cho đầu ra CSV.
     * 
     * @return Chuỗi có định dạng CSV chứa dữ liệu vàng.
     */
    public String toString1() {
        char c = '"';
        String s = c + id + c + "," + c + khuVuc + c + "," + c + heThong + c + "," + giaMua + "," + giaBan + "," + chenhLech + "," + timeCrawlData;
        return s;
    }
}
