package warehouse.com.vn.model;

import java.io.Serializable;
import java.security.Timestamp;

public class PriceGold implements Serializable {
    int id;
    int idDate;
    int idType,idProvince;
    double priceSell, priceBuy;
    Timestamp dateStart, dateEnd;

    public PriceGold() {
    }

    public PriceGold(int id, int idDate, int idType, int idProvince, double priceSell, double priceBuy, Timestamp dateStart, Timestamp dateEnd) {
        this.id = id;
        this.idDate = idDate;
        this.idType = idType;
        this.idProvince = idProvince;
        this.priceSell = priceSell;
        this.priceBuy = priceBuy;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
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

    public Timestamp getDateStart() {
        return dateStart;
    }

    public void setDateStart(Timestamp dateStart) {
        this.dateStart = dateStart;
    }

    public Timestamp getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Timestamp dateEnd) {
        this.dateEnd = dateEnd;
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
                ", dateStart='" + dateStart + '\'' +
                ", dateEnd='" + dateEnd + '\'' +
                '}';
    }
}
