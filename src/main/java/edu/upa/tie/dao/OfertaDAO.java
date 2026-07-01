package edu.upa.tie.dao;

import edu.upa.tie.db.Database;
import edu.upa.tie.model.Oferta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OfertaDAO {

    private static final String SELECT_FULL = """
        SELECT o.id, o.precio, o.foto, o.destacada,
               o.fk_libro, o.fk_usuario, o.fk_estado, o.fk_condicion,
               l.titulo AS libro_titulo, l.fach AS libro_fach,
               u.nombre AS usuario_nombre, u.whatsapp AS usuario_whatsapp,
               e.estado, c.condicion
        FROM oferta o
        JOIN libro l ON o.fk_libro = l.id
        JOIN usuario u ON o.fk_usuario = u.id
        JOIN estado e ON o.fk_estado = e.id
        JOIN condicion c ON o.fk_condicion = c.id
        """;

    public List<Oferta> getAll() {
        return query(SELECT_FULL + "ORDER BY o.destacada DESC, l.titulo");
    }

    public List<Oferta> getByUsuario(int usuarioId) {
        List<Oferta> list = new ArrayList<>();
        String sql = SELECT_FULL + "WHERE o.fk_usuario = ? ORDER BY l.titulo";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Oferta> search(String titulo, String fach) {
        List<Oferta> list = new ArrayList<>();
        String sql = SELECT_FULL + "WHERE l.titulo LIKE ? AND (l.fach LIKE ? OR ? = '') ORDER BY o.destacada DESC, l.titulo";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + titulo + "%");
            ps.setString(2, "%" + fach + "%");
            ps.setString(3, fach);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Oferta> getVisibleOffers(String estadoFiltro, String condicionFiltro, String orden) {
        List<Oferta> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_FULL);
        List<String> where = new ArrayList<>();
        where.add("e.estado != 'Vendido'");

        if (estadoFiltro != null && !"Todos".equals(estadoFiltro)) {
            where.add("e.estado = ?");
        }
        if (condicionFiltro != null && !"Todas".equals(condicionFiltro)) {
            where.add("c.condicion = ?");
        }

        if (!where.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", where));
        }

        sql.append(" ORDER BY ");
        if ("Precio ↓".equals(orden)) {
            sql.append("o.precio DESC");
        } else if ("Precio ↑".equals(orden)) {
            sql.append("o.precio ASC");
        } else {
            sql.append("o.destacada DESC, o.precio ASC");
        }

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            if (estadoFiltro != null && !"Todos".equals(estadoFiltro)) {
                ps.setString(index++, estadoFiltro);
            }
            if (condicionFiltro != null && !"Todas".equals(condicionFiltro)) {
                ps.setString(index, condicionFiltro);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Oferta> getVisibleOffersByLibro(int libroId, String estadoFiltro, String condicionFiltro, String orden) {
        List<Oferta> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(SELECT_FULL);
        List<String> where = new ArrayList<>();
        where.add("e.estado != 'Vendido'");
        where.add("o.fk_libro = ?");

        if (estadoFiltro != null && !"Todos".equals(estadoFiltro)) {
            where.add("e.estado = ?");
        }
        if (condicionFiltro != null && !"Todas".equals(condicionFiltro)) {
            where.add("c.condicion = ?");
        }

        sql.append("WHERE ").append(String.join(" AND ", where));
        sql.append(" ORDER BY ");
        if ("Precio ↓".equals(orden)) {
            sql.append("o.precio DESC");
        } else if ("Precio ↑".equals(orden)) {
            sql.append("o.precio ASC");
        } else {
            sql.append("o.destacada DESC, o.precio ASC");
        }

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql.toString())) {
            int index = 1;
            ps.setInt(index++, libroId);
            if (estadoFiltro != null && !"Todos".equals(estadoFiltro)) {
                ps.setString(index++, estadoFiltro);
            }
            if (condicionFiltro != null && !"Todas".equals(condicionFiltro)) {
                ps.setString(index, condicionFiltro);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public int insert(Oferta o) {
        String sql = "INSERT INTO oferta (precio, foto, destacada, fk_libro, fk_usuario, fk_estado, fk_condicion) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setFloat(1, o.getPrecio());
            ps.setString(2, o.getFoto());
            ps.setInt(3, o.isDestacada() ? 1 : 0);
            ps.setInt(4, o.getLibroId());
            ps.setInt(5, o.getUsuarioId());
            ps.setInt(6, o.getEstadoId());
            ps.setInt(7, o.getCondicionId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    public void update(Oferta o) {
        String sql = "UPDATE oferta SET precio=?, foto=?, destacada=?, fk_libro=?, fk_estado=?, fk_condicion=? WHERE id=?";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setFloat(1, o.getPrecio());
            ps.setString(2, o.getFoto());
            ps.setInt(3, o.isDestacada() ? 1 : 0);
            ps.setInt(4, o.getLibroId());
            ps.setInt(5, o.getEstadoId());
            ps.setInt(6, o.getCondicionId());
            ps.setInt(7, o.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("DELETE FROM oferta WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void toggleDestacada(int id, boolean destacada) {
        try (PreparedStatement ps = Database.getConnection().prepareStatement("UPDATE oferta SET destacada=? WHERE id=?")) {
            ps.setInt(1, destacada ? 1 : 0);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void reservar(int id) {
        String sql = "SELECT id FROM estado WHERE estado = 'Reservado'";
        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int estadoId = rs.getInt("id");
                try (PreparedStatement update = Database.getConnection().prepareStatement("UPDATE oferta SET fk_estado=? WHERE id=?")) {
                    update.setInt(1, estadoId);
                    update.setInt(2, id);
                    update.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Oferta> query(String sql) {
        List<Oferta> list = new ArrayList<>();
        try (Statement stmt = Database.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    private Oferta map(ResultSet rs) throws SQLException {
        Oferta o = new Oferta();
        o.setId(rs.getInt("id"));
        o.setPrecio(rs.getFloat("precio"));
        o.setFoto(rs.getString("foto"));
        o.setDestacada(rs.getInt("destacada") == 1);
        o.setLibroId(rs.getInt("fk_libro"));
        o.setUsuarioId(rs.getInt("fk_usuario"));
        o.setEstadoId(rs.getInt("fk_estado"));
        o.setCondicionId(rs.getInt("fk_condicion"));
        o.setLibroTitulo(rs.getString("libro_titulo"));
        o.setLibroFach(rs.getString("libro_fach"));
        o.setUsuarioNombre(rs.getString("usuario_nombre"));
        o.setUsuarioWhatsapp(rs.getString("usuario_whatsapp"));
        o.setEstado(rs.getString("estado"));
        o.setCondicion(rs.getString("condicion"));
        return o;
    }
}
