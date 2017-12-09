package com.example.android.earthreport.model;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/10/17.
 */

public class  DataProvider {

    public List<EarthQuakes> valuesList = new ArrayList<>();

    //sampel data load here to call method with values in parameters
    static {

    }

    public void setValuesList(List<EarthQuakes> valuesList) {
        this.valuesList = valuesList;
    }

    public List<EarthQuakes> getValuesList() {
        return valuesList;
    }

    public void addProduct(String magnitude, String cityName, String date, String url, double longitude, double latitude) {
        EarthQuakes value = new EarthQuakes(magnitude, cityName, date, url, longitude, latitude);
        valuesList.add(value);
    }


}
