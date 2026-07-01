package edu.upa.tie.model;

public class Estado {
    private int id;
    private String estado;

    public Estado(int id, String estado) {
        this.id = id;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getEstado() { return estado; }

    @Override
    public String toString() { return estado; }
}
