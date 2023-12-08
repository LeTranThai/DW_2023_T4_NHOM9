package warehouse.com.vn.model;

import java.io.Serializable;

public class TypeGold_dim implements Serializable {
    int id;
    String typeName;

    public TypeGold_dim() {
    }

    public TypeGold_dim(int id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return "TypeGold_dim{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                '}';
    }
}
