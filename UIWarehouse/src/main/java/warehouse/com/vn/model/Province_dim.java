package warehouse.com.vn.model;

import java.io.Serializable;

public class Province_dim implements Serializable {
    int id;
    String name;

    public Province_dim() {
    }

    public Province_dim(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Province_dim{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
