package DAO;

import Model.ConfigModel;
import db.DBConnect;
//import models.control.ConfigModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

//import static constants.VariableEnv.*;

public class DAOConfig {
    DBConnect connDBControl = null;

    public DAOConfig(DBConnect connDBControl) {
        //2. Connect Database CONTROL
        this.connDBControl = connDBControl;
    }

    /**
     * Insert data config in file VariableEnv into table Control.configs
     * @param configModel
     * @return
     */
    public int insert(ConfigModel configModel) {
        try {
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

            PreparedStatement ps = connDBControl.preStatement("INSERT INTO configs VALUES(null,?,?,?,?,?,?,?,?,?,?,?,?)");
//            ps.setString(1, Name_DB_Control);
//            ps.setString(2, NAME_DB_STAGING);
//            ps.setString(3, NAME_DB_DW);
//            ps.setString(4, NAME_DB_DM);
//            ps.setString(5, configModel.getName());
//            ps.setDate(6, DATE_CRAWL_DATA);
//            ps.setString(7, configModel.getDescription());
//            ps.setString(8, configModel.getSourcePath());
//            ps.setString(9, FORMAT);
//            ps.setTimestamp(10, currentTimestamp);
//            ps.setTimestamp(11, configModel.getUpdateAt());
//            ps.setString(12, configModel.getStatus());
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated ID(s) of the inserted row
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Generated ID: " + generatedId);
                        return generatedId;
                    } else {
                        System.out.println("No generated keys were retrieved.");
                    }
                }
            } else {
                System.out.println("No rows were inserted.");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void updateStatus(int id, String status) {
        String sql = "update configs set status = ? where id = ?";
        try {
            PreparedStatement ps = connDBControl.preStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, id);

            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
}
