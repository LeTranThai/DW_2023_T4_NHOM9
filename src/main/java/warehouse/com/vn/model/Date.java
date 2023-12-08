package warehouse.com.vn.model;

import java.io.Serializable;

public class Date implements Serializable {
    int id;
    String datetime;


    public Date() {
    }

    public Date(int id, String datetime) {
        this.id = id;
        this.datetime = datetime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Date{" +
                "id=" + id +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
