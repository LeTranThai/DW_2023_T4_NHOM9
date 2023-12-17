package Model;


//import lombok.*;

import java.sql.Timestamp;
import java.util.Date;

//@Getter
//@Setter
//@ToString
//@NoArgsConstructor
//@AllArgsConstructor
public class LogModel {
    private int id;
    private int configID;
    private Timestamp date;
    private String ipAddress;
    private String description;
    private String userAgent;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getDate() {
        return date;
    }
}