package edu.upa.tie.dao;

import edu.upa.tie.db.Database;
import edu.upa.tie.model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario login(String email, String password) {
        String sql = "SELECT * FROM usuario WHERE email = ? AND password = ?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean emailExists(String email) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement(
                "SELECT COUNT(*) FROM usuario WHERE email = ?")) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(Usuario u) {
        String sql = "INSERT INTO usuario (nombre, email, password, whatsapp, admin) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getWhatsapp());
            ps.setInt(5, u.isAdmin() ? 1 : 0);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public void update(Usuario u) {
        String sql = "UPDATE usuario SET nombre=?, email=?, password=?, whatsapp=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getWhatsapp());
            ps.setInt(5, u.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Usuario> getAll() {
        List<Usuario> list = new ArrayList<>();
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM usuario ORDER BY nombre")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public void delete(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM usuario WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Usuario map(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getString("whatsapp"),
            rs.getInt("admin") == 1
        );
    }
}
