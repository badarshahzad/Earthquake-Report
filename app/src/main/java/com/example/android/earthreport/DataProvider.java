package com.example.android.earthreport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/10/17.
 */

public class DataProvider {

    public static List<EarthQuakes> productList = new ArrayList<>();

    //sampel data load here
    static {

    }

    public static List<EarthQuakes> getProductList() {
        return productList;
    }

    public static void setProductList(List<EarthQuakes> productList) {
        DataProvider.productList = productList;
    }

    public static void addProduct(String magnitude, String cityName, String date, String url, double longitude, double latitude) {
        EarthQuakes value = new EarthQuakes(magnitude, cityName, date, url, longitude, latitude);
        productList.add(value);

    }

}
