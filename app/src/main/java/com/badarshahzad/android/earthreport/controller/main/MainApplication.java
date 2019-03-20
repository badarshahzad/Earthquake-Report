package com.badarshahzad.android.earthreport.controller.main;

import android.app.Application;

/**
 * Created by root on 9/15/18.
 */

public class MainApplication extends Application {

    private static MainApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MainApplication getmInstance(){
        return mInstance;
    }

}
