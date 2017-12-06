package com.example.android.earthreport.model;

/**
 * Created by root on 12/3/17.
 */

public class EarthQuakesCount {

    //By default -1 if the query will not get the values from site
    public static int TODAY_EARTHQUAKES = -1;
    public static int MONTH_EARTHQUAKES = -1;
    public static int YESTERDAY_EARTHQUAKES = -1;
    public static int WEEK_EARTHQUAKES = -1;

    public static int getTodayEarthquakes() {
        return TODAY_EARTHQUAKES;
    }

    public static void setTodayEarthquakes(int todayEarthquakes) {
        TODAY_EARTHQUAKES = todayEarthquakes;
    }

    public static int getMonthEarthquakes() {
        return MONTH_EARTHQUAKES;
    }

    public static void setMonthEarthquakes(int monthEarthquakes) {
        MONTH_EARTHQUAKES = monthEarthquakes;
    }

    public static int getYesterdayEarthquakes() {
        return YESTERDAY_EARTHQUAKES;
    }

    public static void setYesterdayEarthquakes(int yesterdayEarthquakes) {
        YESTERDAY_EARTHQUAKES = yesterdayEarthquakes;
    }

    public static int getWeekEarthquakes() {
        return WEEK_EARTHQUAKES;
    }

    public static void setWeekEarthquakes(int weekEarthquakes) {
        WEEK_EARTHQUAKES = weekEarthquakes;
    }


}
