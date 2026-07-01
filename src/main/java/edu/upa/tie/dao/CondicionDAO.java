package edu.upa.tie.dao;

import edu.upa.tie.db.Database;
import edu.upa.tie.model.Condicion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CondicionDAO {

    public List<Condicion> getAll() {
        List<Condicion> list = new ArrayList<>();
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, condicion FROM condicion")) {
            while (rs.next()) {
                list.add(new Condicion(rs.getInt("id"), rs.getString("condicion")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}
