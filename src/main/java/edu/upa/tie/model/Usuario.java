package edu.upa.tie.model;

public class Usuario {
    private int id;
    private String nombre;
    private String email;
    private String password;
    private String whatsapp;
    private boolean admin;

    public Usuario() {}

    public Usuario(int id, String nombre, String email, String password, String whatsapp, boolean admin) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.whatsapp = whatsapp;
        this.admin = admin;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
}
