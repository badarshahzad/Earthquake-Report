package com.example.android.earthreport.view.filter;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.earthreport.R;
import com.example.android.earthreport.view.timeline.TimelineFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterDialog extends DialogFragment {


    //TODO: Convert this filter class to search class

    private String selectedMin;
    private String selectedCountry;


    private Spinner minMagnitude ;
    private Spinner countery;

    View.OnClickListener donefilterAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), "Test", Toast.LENGTH_LONG).show();

            selectedMin = String.valueOf(minMagnitude.getSelectedItem());
            selectedCountry = String.valueOf(countery.getSelectedItem());

            setSelectedMin(selectedMin);
            setSelectedCountry(selectedCountry);

            TimelineFragment timelineFragment = new TimelineFragment();
            timelineFragment.filterRefreshList(selectedMin, selectedCountry);

            getDialog().dismiss();
        }
    };

    public FilterDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        getDialog().setTitle("Filter Earthquakes");

        Button donefilter = view.findViewById(R.id.done_alert);
        donefilter.setOnClickListener(donefilterAction);


        //TODO: Add
        // This hour , Yesterday, weeek, month ,
        // min and max magnitude
        // countery

        minMagnitude = view.findViewById(R.id.min_of);
        countery = view.findViewById(R.id.country_of);


        return view;
    }

    public String getSelectedMin() {
        return selectedMin;
    }

    public void setSelectedMin(String selectedMin) {
        this.selectedMin = selectedMin;
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

}
