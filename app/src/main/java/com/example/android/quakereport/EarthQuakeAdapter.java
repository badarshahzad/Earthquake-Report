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

        //Find the TextView with view  ID magnitude
        TextView magnitude = listView.findViewById(R.id.magnitude);
        //Display  the magnitude of the current earthquake in the TextView
        magnitude.setText(earthQuakesValue.getMagnitude());

        //Find the TextView with view ID cityName or Location on earh
        TextView cityName = listView.findViewById(R.id.cityName);
        //Display the city name in the TextView
        cityName.setText(earthQuakesValue.getCityname());

        //Find the TextView  with view ID date
        TextView date = listView.findViewById(R.id.date);
        //Display the date in the TextView
        date.setText(earthQuakesValue.getDate());


        //Return the list item that is now showing the appropriate data
        return listView;
    }
}
