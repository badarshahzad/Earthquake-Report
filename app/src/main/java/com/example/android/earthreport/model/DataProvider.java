package com.example.android.earthreport.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by root on 11/10/17.
 */

public class DataProvider {

//    sampel data load here to call method with values in parameters
    static {

    }

    //This mehtod convert the date into standard date like : 2017-12-23
    public static String getformateDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(dateObject);
    }

}
