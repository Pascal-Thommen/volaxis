package edu.upa.tie.model;

public class Condicion {
    private int id;
    private String condicion;

    public Condicion(int id, String condicion) {
        this.id = id;
        this.condicion = condicion;
    }

    public int getId() { return id; }
    public String getCondicion() { return condicion; }

    @Override
    public String toString() { return condicion; }
}
