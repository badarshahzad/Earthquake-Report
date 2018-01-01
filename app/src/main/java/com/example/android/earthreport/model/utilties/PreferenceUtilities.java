/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.earthreport.model.utilties;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.earthreport.model.pojos.EarthQuakes;
import com.google.gson.Gson;

/**
 * This class contains utility methods which update or show the earthquake in list
 */
public final class PreferenceUtilities {

    public static final String KEY_EARTHQUAKES = "earthquakes";
    public static final String TAG = PreferenceUtilities.class.getSimpleName();

    synchronized public static void setEartquakeList(Context context, EarthQuakes earthquakes) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EARTHQUAKES, new Gson().toJson(earthquakes).toString());
        //convert string to gson new Gson().fromJson(listString, CustomObjectsList.class);
        editor.apply();

        Log.i(TAG, "setEartquakeList: " + new Gson().toJson(earthquakes).toString());
    }
}