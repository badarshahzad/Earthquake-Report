package com.example.android.quakereport;

/**
 * Created by root on 11/10/17.
 */

public class EarthQuakes {
    private String itemId;
    private String magnitude;
    private String cityname;
    private String date;

    public EarthQuakes(String itemId, String magnitude, String cityname, String date) {
        this.itemId = itemId;
        this.magnitude = magnitude;
        this.cityname = cityname;
        this.date = date;
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

    public String getItemId() {
        return itemId;
    }
}