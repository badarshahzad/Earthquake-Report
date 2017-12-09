package com.example.android.earthreport.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.earthreport.Map;
import com.example.android.earthreport.R;
import com.example.android.earthreport.model.DataProvider;
import com.example.android.earthreport.model.EarthQuakes;
import com.example.android.earthreport.network.GetEarthquakeData;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {

    public final static String TODAY = "Today";

    private ListView earthquakeListViewToday;
    private DataProvider dataProvider;
    public TodayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_today, container, false);

            //Find the reference of Listview
            earthquakeListViewToday = (ListView) view.findViewById(R.id.todayList);

            //Data replicate in listview due to this I add for just when view appear listview
            // instance recereate and assigned (check):
            dataProvider = new DataProvider();
            List<EarthQuakes> todayValuesList = new ArrayList<>();
            dataProvider.setValuesList(todayValuesList);


            //Today Url
            String url = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";

            // here we can give the argument in execute the argument could be the `url`
            //to get data from web
            new GetEarthquakeData(getContext(), earthquakeListViewToday).execute(url);

            //Add list view listener to open detail activity of each list view value
            earthquakeListViewToday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //Log.i("URL: ",values.get(position).getUrl());
                    //  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(values.get(position).getUrl()));
                    Intent intent = new Intent(getContext(), Map.class);

                    Bundle bundle = new Bundle();

                    bundle.putDouble("LONGITUDE", dataProvider.getValuesList().get(position).getLongitude());
                    bundle.putDouble("LATITUDE", dataProvider.getValuesList().get(position).getLatitude());
                    bundle.putString("CITY", dataProvider.getValuesList().get(position).getCityname());
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });

            return view;

        }

}
