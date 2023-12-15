package warehouse.com.vn.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PriceGold implements Serializable {
    int id;
    int idDate;
    int idType,idProvince;
    double priceSell, priceBuy;
    Timestamp start_date, end_date;

    public PriceGold() {
    }

    public PriceGold(int id, int idDate, int idType, int idProvince, double priceSell, double priceBuy, Timestamp start_date, Timestamp end_date) {
        this.id = id;
        this.idDate = idDate;
        this.idType = idType;
        this.idProvince = idProvince;
        this.priceSell = priceSell;
        this.priceBuy = priceBuy;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdDate() {
        return idDate;
    }

    public void setIdDate(int idDate) {
        this.idDate = idDate;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public int getIdProvince() {
        return idProvince;
    }

    public void setIdProvince(int idProvince) {
        this.idProvince = idProvince;
    }

    public double getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(double priceSell) {
        this.priceSell = priceSell;
    }

    public double getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(double priceBuy) {
        this.priceBuy = priceBuy;
    }

    public Timestamp getStart_date() {
        return start_date;
    }

    public void setStart_date(Timestamp start_date) {
        this.start_date = start_date;
    }

    public Timestamp getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Timestamp end_date) {
        this.end_date = end_date;
    }

    @Override
    public String toString() {
        return "PriceGold{" +
                "id=" + id +
                ", idDate=" + idDate +
                ", idType=" + idType +
                ", idProvince=" + idProvince +
                ", priceSell=" + priceSell +
                ", priceBuy=" + priceBuy +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }
    public static String convertTimestampToString(Timestamp timestamp, String pattern) {
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    public static String formatPrice(Object price) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        return nf.format(price);
    }
}
