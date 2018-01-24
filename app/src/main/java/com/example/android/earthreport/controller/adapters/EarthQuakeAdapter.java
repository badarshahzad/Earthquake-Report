package com.example.android.earthreport.controller.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.earthreport.R;
import com.example.android.earthreport.model.pojos.EarthQuakes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Badar on 11/10/17.
 */

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuakes> {


    private static final String TAG = EarthQuakeAdapter.class.getSimpleName();
    List<EarthQuakes> earthQuakesQuakesValues;

    public EarthQuakeAdapter(@NonNull Context context, int resource, @NonNull List<EarthQuakes> objects) {
        super(context, resource, objects);
        earthQuakesQuakesValues = objects;
    }

    /**
     * Returns  a list item view that displays information  about the earthquakes at the
     * given position in the list of earthquakes.
     */

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if there is an existing list item view ( called convertView) that we can resuse,
        // otherwise, if convertview is null,  then inflate a new list item layout
        View view = convertView;

        if (view == null) {
            Log.i(TAG, "view is null");
            view = LayoutInflater.from(getContext()).inflate(R.layout.listview_item, parent, false);

        }
        Log.i(TAG, "getView: null cross");

        //Find the earthquake at the given positions
        EarthQuakes earthQuakesValue = earthQuakesQuakesValues.get(position);

        //Here I get the values from EarthQuakes model getter
        //from EarthQuakes getter method get the value of magnitude
        String magnitude = earthQuakesValue.getMagnitude();

         /* **************************************************************************
         * Here the distance and location is separated by reges index 0 in Km distance
         * and index 1 is the location
         * ?<=of is the string that just pick string including 'of' also init
         * and remaining will be 2nd index
         * I know that this hard coded but the USGS mantain data with that string :D
         * So, Honesty don't know how to get the name of city  the don't have method
         * in API to get the name of city so i'm doing like this
         *****************************************************************************/

        String[] parseValue = earthQuakesValue.getCityname().split("(?<=of)");
        String distance = null;
        String cityName = null;

        //There is possiblity when the location is not mention so I have to handle it
        if (parseValue.length == 1) {
            distance = parseValue[0];
            cityName = null;
        }
        if (parseValue.length == 2) {
            distance = parseValue[0];
            cityName = parseValue[1];
        }

        //As the time we get from jason in milliseconds (Unix time format)
        // so I converted it into date and time with Date type object
        long unixTime = Long.valueOf(earthQuakesValue.getTimeStamp());

        /*****************************************************************************************
         * Title: DateUtils
         * Author: developer.android.com
         * Date: 2017-12-20
         * Code version: N/A
         * Availability: https://developer.android.com/reference/android/text/format/DateUtils.html
         *****************************************************************************************/

        //Time converted into human readable format
        String relavtiveTimeString = String.valueOf(DateUtils.getRelativeTimeSpanString(unixTime));

        //Find the TextView with view  ID magnitude
        TextView magnitudeView = view.findViewById(R.id.magnitude);
        //Display  the magnitude of the current earthquake in the TextView
        magnitudeView.setText(magnitude);

        //Im not gonna show the values the place and distance is not mention
        if (distance != null && cityName != null) {

            //Find the Textview with view ID for distance
            TextView distanceView = view.findViewById(R.id.distance);
            distanceView.setText(distance);
            //Find the TextView with view ID cityName or Location on earth
            TextView cityNameView = view.findViewById(R.id.cityName);
            //Display the city name in the TextView (sometime city came in results
            // and some time country the results from USGS are not so accurate)
            cityNameView.setText(cityName);

        }

        //Find the TextView  with view ID for time
        TextView timeView = view.findViewById(R.id.timeStamp);
        timeView.setText(relavtiveTimeString);

        //Find the TextView background color of magnitude
        GradientDrawable magnitudeBackground = (GradientDrawable) magnitudeView.getBackground();

        //Get the approperiate  background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(magnitude);

        //set the color on the magnitude value
        //when the color is not empty also like return value 0 in any trouble scenario
        if (magnitudeColor != 0) {
            magnitudeBackground.setColor(magnitudeColor);
        }

        //Return the list item that is now showing the appropriate data
        return view;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public EarthQuakes getItem(int position) {
        return super.getItem(position);
    }


    /*********************************************************************************************
     * Title: Developing Android Applications
     * Author: classroom.udacity.com
     * Date: 2017-11-04
     * Code version: N/A
     * Availability: https://classroom.udacity.com/courses/ud851
     ********************************************************************************************/

    private int getMagnitudeColor(String magnitude) {

        //converting string into integer value
        int magnitudeResourceColorId = 000000;
        if (magnitude != null) {
            int magnitudeFloor = (int) Math.floor(Double.valueOf(magnitude));

            //colors values
            switch (magnitudeFloor) {

                case 0:
                case 1:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude1);
                    break;
                case 2:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude2);
                    break;
                case 3:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude3);
                    break;
                case 4:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude4);
                    break;
                case 5:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude5);
                    break;
                case 6:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude6);
                    break;
                case 7:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude7);
                    break;
                case 8:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude8);
                    break;
                case 9:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude9);
                    break;
                case 10:
                    magnitudeResourceColorId = ContextCompat.getColor(getContext(), R.color.magnitude10plus);
                    break;
            }
        } else {
            return 0;
        }
        return magnitudeResourceColorId;
    }
}
