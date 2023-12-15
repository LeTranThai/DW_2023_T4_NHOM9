package warehouse.com.vn.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Date implements Serializable {
    int id;
    Timestamp date;


    public Date() {
    }

    public Date(int id, Timestamp date) {
        this.id = id;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Date{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
}
