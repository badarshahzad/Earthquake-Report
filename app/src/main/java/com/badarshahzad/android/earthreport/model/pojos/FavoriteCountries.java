package com.badarshahzad.android.earthreport.model.pojos;

/**
 * Created by root on 1/1/18.
 */

public class FavoriteCountries {

    private String country;
    private double magnitude;

    public FavoriteCountries(String country, double magnitude) {
        this.country = country;
        this.magnitude = magnitude;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
