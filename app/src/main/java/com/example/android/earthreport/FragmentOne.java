package com.example.android.earthreport;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.earthreport.network.GetEarthquakeData;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOne extends Fragment {

    private static final String TAG = FragmentOne.class.getSimpleName();
    //main activity root
    private TextView textView;
    private ListView earthquakeListView;

    public FragmentOne() {
        // Required empty public constructor
        Log.i(TAG, "Constructor");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");
    }

    //I created the xml for appearance of fragmentone
    //In order to connect our fragmentone.java with xml we have to override method oncreatevoew
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Find a reference to the {@link ListView} in the layout
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_one, container, false);
        // To get the referance we don't have findviewbyId method in fragment so we use view
        textView = view.findViewById(R.id.textview);
        textView.setText("First Fragment");

        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = view.findViewById(R.id.list);

        // here we can give the argument in execute the argument could be the `url`
        //to get data from web
        new GetEarthquakeData(getContext(), earthquakeListView).execute();

        //Add list view listener to open detail activity of each list view value
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Log.i("URL: ",values.get(position).getUrl());
                //  Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(values.get(position).getUrl()));
                Intent intent = new Intent(getContext(), Map.class);

                Bundle bundle = new Bundle();

                bundle.putDouble("LONGITUDE", DataProvider.valuesList.get(position).getLongitude());
                bundle.putDouble("LATITUDE", DataProvider.valuesList.get(position).getLatitude());
                bundle.putString("CITY", DataProvider.valuesList.get(position).getCityname());
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });


        // container is viewgroup root and return fragment layout root view
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }
}
