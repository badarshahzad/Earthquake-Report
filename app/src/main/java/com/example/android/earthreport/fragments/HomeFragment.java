package com.example.android.earthreport.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.earthreport.R;
import com.example.android.earthreport.model.EarthQuakesCount;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private TextView todayTxt;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.i("LALA", "today counts 123");
        todayTxt = view.findViewById(R.id.todayEarthquakes);
        todayTxt.setText(EarthQuakesCount.TODAY_EARTHQUAKES + "");
        Log.i("LALA", "today counts" + todayTxt.getText().toString());


        return view;
    }


}
