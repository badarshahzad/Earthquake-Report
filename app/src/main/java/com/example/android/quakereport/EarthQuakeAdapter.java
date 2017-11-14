package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Badar on 11/10/17.
 */

public class EarthQuakeAdapter extends ArrayAdapter<EarthQuakes> {


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
        View listView = convertView;
        Log.i("ViewBefore", "" + listView);
        if (listView == null) {
            Log.i("ViewInIf", "null");
            listView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_item, parent, false);

        }

        //Find the earthquake at the given positions
        EarthQuakes earthQuakesValue = earthQuakesQuakesValues.get(position);
        Log.i("EarthQuakes value index", "" + position);

        //Here I get the values from EarthQuakes model getter
        //from EarthQuakes getter method get the value of magnitude
        String magnitude = earthQuakesValue.getMagnitude();

        //Here the distance and location is separated by reges index 0 in Km distance
        // and index 1 is the location
        //?<=of is the string that just pick string including 'of' also init
        // and remaining will beat 2nd index
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
        long unixTime = Long.valueOf(earthQuakesValue.getDate());
        //Date parameters get the unix time
        Date dateObjet = new Date(unixTime);
        //Date converted into human readable format
        String date = formateDate(dateObjet);
        //Time converted into human readable format
        String time = formateTime(dateObjet);


        //Find the TextView with view  ID magnitude
        TextView magnitudeView = listView.findViewById(R.id.magnitude);
        //Display  the magnitude of the current earthquake in the TextView
        magnitudeView.setText(magnitude);

        //Im not gonna show the values the place and distance is not mention
        if (distance != null && cityName != null) {
            //Find the Textview with view ID for distance
            TextView distanceView = listView.findViewById(R.id.distance);
            distanceView.setText(distance);

            //Find the TextView with view ID cityName or Location on earth
            TextView cityNameView = listView.findViewById(R.id.cityName);
            //Display the city name in the TextView
            cityNameView.setText(cityName);
        }

        //Find the TextView  with view ID date
        TextView dateView = listView.findViewById(R.id.date);
        //Display the date in the TextView
        dateView.setText(date);

        //Find the TextView  with view ID for time 
        TextView timeView = listView.findViewById(R.id.time);
        timeView.setText(time);

        //Find the TextView background color of magnitude
        GradientDrawable magnitudeBackground = (GradientDrawable) magnitudeView.getBackground();
        //Get the approperiate  background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(magnitude);
        //set the color on the magnitude value
        //when the color is not empty also like return value 0 in any trouble scenario
        if (magnitudeColor != 0) magnitudeBackground.setColor(magnitudeColor);


        //Return the list item that is now showing the appropriate data
        return listView;
    }

    private int getMagnitudeColor(String magnitude) {

        //converting string into integer value
        int magnitudeResourceColorId = 000000;
        if (magnitude != null) {
            int magnitudeFloor = (int) Math.floor(Double.valueOf(magnitude));

            //color value setting


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

    /**
     * @param dateObject Take a Date type object
     * @return return the date in human readable format e.g "Nov 11, 2017"
     */
    private String formateDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * @param timeObjet Take a Date type object
     * @return return the time in human readable format e.g "04:00 pm"
     */
    private String formateTime(Date timeObjet) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(timeObjet);
    }
}
