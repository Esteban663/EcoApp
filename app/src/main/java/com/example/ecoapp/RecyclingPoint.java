package com.example.ecoapp;

public class RecyclingPoint {
    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public RecyclingPoint(int id, String name, String address, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters para acceder a los datos de la clase
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    
    
    @Override
    public String toString()
        {
            return "Nombre: " + name + "\nDireccion: " + address +"\n";
        }
}