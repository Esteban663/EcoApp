package com.example.ecoapp;

public class Consejo {
    private String titulo;
    private String descripcion;
    private String categoria;
    private String color;

    // Constructor vacío requerido por Firestore
    public Consejo() {}

    public Consejo(String titulo, String descripcion, String categoria, String color) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.color = color;
    }

    // Getters y setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}