package warehouse.com.vn.service;

import warehouse.com.vn.db.JDBiConnector;
import warehouse.com.vn.model.PriceGold;
import warehouse.com.vn.model.Province_dim;
import warehouse.com.vn.model.TypeGold_dim;

import java.util.List;
import java.util.stream.Collectors;

public class PriceGoldService {
    public static List<PriceGold> getAllGold() {
        return JDBiConnector.me().withHandle(h ->
                h.createQuery("SELECT * FROM goldprice_fact where end_date is null")
                        .mapToBean(PriceGold.class)
                        .stream()
                        .collect(Collectors.toList())
        );

    }

    public static String getTypeGold(int id) {
        List<TypeGold_dim> typeName = JDBiConnector.me().withHandle(h ->
                h.createQuery("SELECT * FROM typegold_dim WHERE id = ?")
                        .bind(0, id)
                        .mapToBean(TypeGold_dim.class)
                        .stream()
                        .collect(Collectors.toList())
        );

        return typeName.get(0).getTypeName();
    }

    public static String getProvince(int id) {
        List<Province_dim> typeName = JDBiConnector.me().withHandle(h ->
                h.createQuery("SELECT * FROM province_dim WHERE id = ?")
                        .bind(0, id)
                        .mapToBean(Province_dim.class)
                        .stream()
                        .collect(Collectors.toList())
        );

        return typeName.get(0).getName();
    }

    public static List<PriceGold> getAllGoldByIdProvince(int id) {
        return JDBiConnector.me().withHandle(h ->
                h.createQuery("SELECT * FROM goldprice_fact where id_province=? and end_date is null")
                        .bind(0, id)
                        .mapToBean(PriceGold.class)
                        .stream()
                        .collect(Collectors.toList())
        );

    }

    public static void main(String[] args) {
        System.out.println(getAllGold());
    }
}
