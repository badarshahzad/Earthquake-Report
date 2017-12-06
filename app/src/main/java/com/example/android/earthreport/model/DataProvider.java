package com.example.android.earthreport.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/10/17.
 */

public class DataProvider {

    public static List<EarthQuakes> valuesList = new ArrayList<>();

    //sampel data load here
    static {

    }

    public static List<EarthQuakes> getValuesList() {
        return valuesList;
    }

    public static void setValuesList(List<EarthQuakes> valuesList) {
        DataProvider.valuesList = valuesList;
    }

    public static void addProduct(String magnitude, String cityName, String date, String url, double longitude, double latitude) {
        EarthQuakes value = new EarthQuakes(magnitude, cityName, date, url, longitude, latitude);
        valuesList.add(value);

    }

}
