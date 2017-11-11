package com.example.android.quakereport;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
        String[] value = earthQuakesValue.getCityname().split(",");
        String distance = value[0];
        String cityName = value[1];

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

        //Find the Textview with view ID for distance
        TextView distanceView = listView.findViewById(R.id.distance);
        distanceView.setText(distance);
        
        //Find the TextView with view ID cityName or Location on earh
        TextView cityNameView = listView.findViewById(R.id.cityName);
        //Display the city name in the TextView
        cityNameView.setText(cityName);

        //Find the TextView  with view ID date
        TextView dateView = listView.findViewById(R.id.date);
        //Display the date in the TextView
        dateView.setText(date);

        //Find the TextView  with view ID for time 
        TextView timeView = listView.findViewById(R.id.time);
        timeView.setText(time);


        //Return the list item that is now showing the appropriate data
        return listView;
    }

    /**
     * @param dateObject Take a Date type object
     * @return return the date in human readable format e.g "Nov 11, 2017"
     */
    private String formateDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM DD, yyyy");
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
