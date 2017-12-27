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

    private String selectedPeriod;
    private String selectedMin;
    private String selectedregion;


    private Spinner period ;
    private Spinner minMagnitude ;
    private Spinner region ;

    View.OnClickListener donefilterAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), "Test", Toast.LENGTH_LONG).show();

            selectedPeriod =  String.valueOf(period.getSelectedItem());
            selectedMin = String.valueOf(minMagnitude.getSelectedItem());
            selectedregion  = String.valueOf(region.getSelectedItem());

            setSelectedPeriod(selectedPeriod);
            setSelectedMin(selectedMin);
            setSelectedregion(selectedregion);

            TimelineFragment timelineFragment = new TimelineFragment();
            timelineFragment.filterRefreshList(selectedPeriod,selectedMin,selectedregion);

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
        // region

        period = view.findViewById(R.id.period);
        minMagnitude = view.findViewById(R.id.min_of);
        region = view.findViewById(R.id.region_of);


        return view;
    }



    public void setSelectedPeriod(String selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
    }

    public void setSelectedMin(String selectedMin) {
        this.selectedMin = selectedMin;
    }


    public void setSelectedregion(String selectedregion) {
        this.selectedregion = selectedregion;
    }

    public String getSelectedPeriod() {
        return selectedPeriod;
    }

    public String getSelectedMin() {
        return selectedMin;
    }

    public String getSelectedregion() {
        return selectedregion;
    }

}
