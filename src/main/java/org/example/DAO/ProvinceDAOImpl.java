package org.example.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.example.model.Province;
import org.example.util.DatabaseConnection;

public class ProvinceDAOImpl implements ProvinceDAO {
	private final DatabaseConnection databaseConnection = new DatabaseConnection();
	
	@Override
    public List<Province> getAll() {
        List<Province> provinces = new ArrayList<>();
        String sql = "SELECT id, name FROM provinces";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Province province = new Province();
                province.setId(rs.getInt("id"));
                province.setName(rs.getString("name"));
                provinces.add(province);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return provinces;
    }

}
