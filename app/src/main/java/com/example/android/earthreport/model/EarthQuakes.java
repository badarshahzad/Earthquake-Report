package com.example.android.earthreport.model;

/**
 * Created by root on 11/10/17.
 */

public class EarthQuakes {

    private String magnitude;
    private String cityname;
    private String date;
    private String url;
    private double longitude;
    private double latitude;


    public EarthQuakes(String magnitude, String cityname, String date, String url, double longitude, double latitude) {

        this.url = url;
        this.magnitude = magnitude;
        this.cityname = cityname;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;

    }

    public String getMagnitude() {
        return magnitude;
    }

    public String getCityname() {
        return cityname;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


}