package com.example.android.earthreport.model.utilties;

import android.util.Log;

import com.example.android.earthreport.model.pojos.FavoriteCountries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 1/1/18.
 */

public class ParseFavoritCountriesJsonUtils {


    public static final String TAG = ParseFavoritCountriesJsonUtils.class.getSimpleName();
    public static List<FavoriteCountries> favritList = new ArrayList<>();

    public static List<FavoriteCountries> parseJsonInToList(String jsonStr) {

        if (jsonStr != null) {

            try {

                JSONArray favoritList = new JSONArray(jsonStr);

                for (int a = 0; a < favoritList.length(); a++) {

                    Log.i(TAG, "parseJsonInToList: ");
                    favoritList.toString();

                    JSONObject indexes = favoritList.getJSONObject(a);
                    String country = indexes.getString("country");
                    double magnitude = Double.valueOf(indexes.getString("magnitude"));
                    favritList.add(new FavoriteCountries(country, magnitude));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return favritList;
    }
}

