package edu.upa.tie.dao;

import edu.upa.tie.db.Database;
import edu.upa.tie.model.Estado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoDAO {

    public List<Estado> getAll() {
        List<Estado> list = new ArrayList<>();
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, estado FROM estado")) {
            while (rs.next()) {
                list.add(new Estado(rs.getInt("id"), rs.getString("estado")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
