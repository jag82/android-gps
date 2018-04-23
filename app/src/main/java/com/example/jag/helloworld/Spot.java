package com.example.jag.helloworld;


import java.io.Serializable;

class Spot {

    double lat;
    double lng;
    String name;

    Spot() {
        this(0, 0, "");
    }

    Spot(double lat, double lng) {
        this(lat, lng, "");
    }

    Spot(double lat, double lng, String name) {
        this.lat = lat;
        this.lng = lng;
        this.name = name;
    }

    public String toString() {
        return lat + "," + lng + "," + name;
    }

    public static Spot fromString(String str) {
        String[] array = str.split(",");
        double lat = Double.parseDouble(array[0]);
        double lng = Double.parseDouble(array[1]);
        String name = array[2];
        return new Spot(lat, lng, name);
    }
}
