package com.example.android.quakereport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/10/17.
 */

public class DataProvider {

    public static List<EarthQuakes> productList = new ArrayList<>();

  /*  static {
        addProduct("CityNameMagnitude", "4.5", "San Francisco", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "London", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Tokyo", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Moscow", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mexico City", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "5.6", "Islamabad", "04-04-2015");
        addProduct("CityNameMagnitude", "6.3", "Karachi", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mexico City", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "5.6", "Islamabad", "04-04-2015");
        addProduct("CityNameMagnitude", "6.3", "Karachi", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mexico City", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "5.6", "Islamabad", "04-04-2015");
        addProduct("CityNameMagnitude", "6.3", "Karachi", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mexico City", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "4.5", "Mianwali", "04-04-2015");
        addProduct("CityNameMagnitude", "5.6", "Islamabad", "04-04-2015");
        addProduct("CityNameMagnitude", "6.3", "Karachi", "04-04-2015");
    }*/

    public static void addProduct(String magnitude, String cityName, String date) {

        EarthQuakes value = new EarthQuakes(magnitude, cityName, date);
        productList.add(value);
    }

}
