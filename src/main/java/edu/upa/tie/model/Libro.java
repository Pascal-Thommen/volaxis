package edu.upa.tie.model;

public class Libro {
    private int id;
    private String titulo;
    private String descripcion;
    private String isbn;
    private String fach;
    private boolean estandarizado;

    public Libro() {}

    public Libro(int id, String titulo, String descripcion, String isbn, String fach, boolean estandarizado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.isbn = isbn;
        this.fach = fach;
        this.estandarizado = estandarizado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getFach() { return fach; }
    public void setFach(String fach) { this.fach = fach; }
    public boolean isEstandarizado() { return estandarizado; }
    public void setEstandarizado(boolean estandarizado) { this.estandarizado = estandarizado; }

    @Override
    public String toString() { return titulo; }
}
