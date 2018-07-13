package com.badarshahzad.android.earthreport.model.utilties;

import com.badarshahzad.android.earthreport.model.pojos.FavoriteCountries;

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

    public static List<FavoriteCountries> parseJsonInToList(String jsonStr) {

        //**Warn** Don't ever try to declare the list as static in class

        /**(Learn What I experiment)**/
        /**
         * I declare the favritList as a static and what it was doing whenever I pass
         * a json in this method the favritList retain the previous instance of list
         * (correct me if im wrong). It was retaining the store instance in existing list in memory.
         * It doesn't create the instance again when I call this class in HomeFragment it
         * just retain previous instance and add in it & return the that list.
         * */

        List<FavoriteCountries> favritList = null;
        if (jsonStr != null) {

            try {

                favritList = new ArrayList<>();
                JSONArray favoritList = new JSONArray(jsonStr);

                for (int a = 0; a < favoritList.length(); a++) {


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

