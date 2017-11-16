package com.example.android.quakereport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/10/17.
 */

public class DataProvider {

    public static List<EarthQuakes> productList = new ArrayList<>();


    static {
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");
        addProduct("6.5", "Mianwali", "1234");

    }
    public static void addProduct(String magnitude, String cityName, String date) {

        EarthQuakes value = new EarthQuakes(magnitude, cityName, date);
        productList.add(value);
    }

}
