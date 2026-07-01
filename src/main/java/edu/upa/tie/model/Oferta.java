package edu.upa.tie.model;

public class Oferta {
    private int id;
    private float precio;
    private String foto;
    private boolean destacada;
    private int libroId;
    private int usuarioId;
    private int estadoId;
    private int condicionId;

    // Campos desnormalizados para mostrar en tablas (resultado de JOINs)
    private String libroTitulo;
    private String libroFach;
    private String usuarioNombre;
    private String usuarioWhatsapp;
    private String estado;
    private String condicion;

    public Oferta() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public float getPrecio() { return precio; }
    public void setPrecio(float precio) { this.precio = precio; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public boolean isDestacada() { return destacada; }
    public void setDestacada(boolean destacada) { this.destacada = destacada; }
    public int getLibroId() { return libroId; }
    public void setLibroId(int libroId) { this.libroId = libroId; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public int getEstadoId() { return estadoId; }
    public void setEstadoId(int estadoId) { this.estadoId = estadoId; }
    public int getCondicionId() { return condicionId; }
    public void setCondicionId(int condicionId) { this.condicionId = condicionId; }
    public String getLibroTitulo() { return libroTitulo; }
    public void setLibroTitulo(String libroTitulo) { this.libroTitulo = libroTitulo; }
    public String getLibroFach() { return libroFach; }
    public void setLibroFach(String libroFach) { this.libroFach = libroFach; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getUsuarioWhatsapp() { return usuarioWhatsapp; }
    public void setUsuarioWhatsapp(String usuarioWhatsapp) { this.usuarioWhatsapp = usuarioWhatsapp; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCondicion() { return condicion; }
    public void setCondicion(String condicion) { this.condicion = condicion; }
}
