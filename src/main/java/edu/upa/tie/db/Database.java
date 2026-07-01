package edu.upa.tie.db;

import java.sql.*;

public class Database {

    private static final String URL = "jdbc:sqlite:volaxis.db";
    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo conectar a la base de datos", e);
        }
        return connection;
    }

    public static void init() {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS estado (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    estado TEXT NOT NULL
                )""");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS condicion (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    condicion TEXT NOT NULL
                )""");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS libro (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    descripcion TEXT,
                    isbn TEXT,
                    fach TEXT,
                    estandarizado INTEGER DEFAULT 0
                )""");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS usuario (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    whatsapp TEXT,
                    admin INTEGER DEFAULT 0
                )""");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS oferta (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    precio REAL NOT NULL,
                    foto TEXT,
                    destacada INTEGER DEFAULT 0,
                    fk_libro INTEGER NOT NULL,
                    fk_usuario INTEGER NOT NULL,
                    fk_estado INTEGER NOT NULL,
                    fk_condicion INTEGER NOT NULL,
                    FOREIGN KEY (fk_libro) REFERENCES libro(id),
                    FOREIGN KEY (fk_usuario) REFERENCES usuario(id),
                    FOREIGN KEY (fk_estado) REFERENCES estado(id),
                    FOREIGN KEY (fk_condicion) REFERENCES condicion(id)
                )""");

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS historial (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha TEXT NOT NULL,
                    fk_oferta INTEGER NOT NULL,
                    fk_usuario INTEGER NOT NULL,
                    FOREIGN KEY (fk_oferta) REFERENCES oferta(id),
                    FOREIGN KEY (fk_usuario) REFERENCES usuario(id)
                )""");

            seedData(stmt);

        } catch (SQLException e) {
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }

    private static void seedData(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM estado");
        if (rs.next() && rs.getInt(1) == 0) {
            stmt.executeUpdate("INSERT INTO estado (estado) VALUES ('Disponible'),('Vendido'),('Reservado')");
        }

        rs = stmt.executeQuery("SELECT COUNT(*) FROM condicion");
        if (rs.next() && rs.getInt(1) == 0) {
            stmt.executeUpdate("INSERT INTO condicion (condicion) VALUES ('Nuevo'),('Bueno'),('Regular'),('Deteriorado')");
        }

        rs = stmt.executeQuery("SELECT COUNT(*) FROM usuario WHERE admin = 1");
        if (rs.next() && rs.getInt(1) == 0) {
            stmt.executeUpdate("""
                INSERT INTO usuario (nombre, email, password, whatsapp, admin)
                VALUES ('Administrador', 'admin@volaxis.edu', 'admin123', NULL, 1)""");
        }
    }
}
