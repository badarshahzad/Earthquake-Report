package com.example.android.earthreport.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.earthreport.Map;
import com.example.android.earthreport.R;
import com.example.android.earthreport.model.EarthQuakes;
import com.example.android.earthreport.network.GetEarthquakeData;

import java.util.ArrayList;


public class WeekFragment extends Fragment {

    public final static String WEEK = "Week";

    //Week Url
    String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.geojson";
    ArrayList<EarthQuakes> earthQuakesList;
    private  ListView earthquakeListViewWeek;
    public WeekFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_week, container, false);

        //Find the reference of Listview
        earthquakeListViewWeek = view.findViewById(R.id.weekList);

        //Data replicate in listview due to this I add for just when view appear listview
        // instance recereate and assigned (check):
        //  DataProvider.valuesList = new ArrayList<>();
        earthQuakesList = new ArrayList<>();


        // here we can give the argument in execute the argument could be the `url`
        //to get data from web
        // new GetEarthquakeData(getContext(), earthquakeListViewWeek, earthQuakesList).execute(url);
        GetEarthquakeData getEarthquakeData = new GetEarthquakeData(getContext(), earthquakeListViewWeek, earthQuakesList);
        getEarthquakeData.execute(url);

        //Add list view listener to open detail activity of each list view value
        earthquakeListViewWeek.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Log.i("URL: ",values.get(position).getUrl());
                //  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(values.get(position).getUrl()));
                Intent intent = new Intent(getContext(), Map.class);

                Bundle bundle = new Bundle();

                bundle.putDouble("LONGITUDE", earthQuakesList.get(position).getLongitude());
                bundle.putDouble("LATITUDE", earthQuakesList.get(position).getLatitude());
                bundle.putString("CITY", earthQuakesList.get(position).getCityname());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        return view;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Firstly clear the existed menu & and add my own menu
        menu.clear();
        inflater.inflate(R.menu.menu_timline, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            Toast.makeText(getContext(), "Week Update", Toast.LENGTH_SHORT).show();
            //there could be many other ways to update the listview values what I did this below
            //Referesh menu click and values again fetch and update the listview
            new GetEarthquakeData(getContext(), earthquakeListViewWeek, earthQuakesList).execute(url);
        }

        return super.onOptionsItemSelected(item);
    }

}
