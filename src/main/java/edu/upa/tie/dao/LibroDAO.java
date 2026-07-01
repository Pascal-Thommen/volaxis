package edu.upa.tie.dao;

import edu.upa.tie.db.Database;
import edu.upa.tie.model.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public List<Libro> getAll() {
        List<Libro> list = new ArrayList<>();
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM libro ORDER BY titulo")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Libro> search(String query) {
        List<Libro> list = new ArrayList<>();
        String sql = "SELECT * FROM libro WHERE titulo LIKE ? OR fach LIKE ? ORDER BY titulo";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            String q = "%" + query + "%";
            ps.setString(1, q);
            ps.setString(2, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Libro getById(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("SELECT * FROM libro WHERE id = ?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int insert(Libro l) {
        String sql = "INSERT INTO libro (titulo, descripcion, isbn, fach, estandarizado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getDescripcion());
            ps.setString(3, l.getIsbn());
            ps.setString(4, l.getFach());
            ps.setInt(5, l.isEstandarizado() ? 1 : 0);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public void update(Libro l) {
        String sql = "UPDATE libro SET titulo=?, descripcion=?, isbn=?, fach=?, estandarizado=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, l.getTitulo());
            ps.setString(2, l.getDescripcion());
            ps.setString(3, l.getIsbn());
            ps.setString(4, l.getFach());
            ps.setInt(5, l.isEstandarizado() ? 1 : 0);
            ps.setInt(6, l.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM libro WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Libro map(ResultSet rs) throws SQLException {
        return new Libro(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("descripcion"),
            rs.getString("isbn"),
            rs.getString("fach"),
            rs.getInt("estandarizado") == 1
        );
    }
}
