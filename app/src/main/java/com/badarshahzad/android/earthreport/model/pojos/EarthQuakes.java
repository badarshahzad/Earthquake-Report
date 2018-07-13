package com.badarshahzad.android.earthreport.model.pojos;

import java.io.Serializable;

/**
 * Created by root on 11/10/17.
 */

public class EarthQuakes implements Serializable{

    private String magnitude;
    private String cityname;
    private String timeStamp;
    private String url;
    private double longitude;
    private double latitude;


    public EarthQuakes(String magnitude, String cityname, String timeStamp, String url, double longitude, double latitude) {

        this.url = url;
        this.magnitude = magnitude;
        this.cityname = cityname;
        this.timeStamp = timeStamp;
        this.longitude = longitude;
        this.latitude = latitude;

    }

    public String getMagnitude() {
        return magnitude;
    }

    public String getCityname() {
        return cityname;
    }

    public String getTimeStamp() {
        return timeStamp;
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