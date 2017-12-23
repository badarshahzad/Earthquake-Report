package com.example.android.earthreport.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by root on 11/10/17.
 */

public class DataProvider {

//        public static void addProduct(String magnitude, String cityName, String date, String url, double longitude, double latitude) {
//        EarthQuakes value = new EarthQuakes(magnitude, cityName, date, url, longitude, latitude);
//        valuesList.add(value);
//    }


    //    public static List<EarthQuakes> valuesList = new ArrayList<>();
    //sampel data load here to call method with values in parameters
    static {

    }

    public static String getformateDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(dateObject);
    }


}
