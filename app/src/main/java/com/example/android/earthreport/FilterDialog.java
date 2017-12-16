package com.example.android.earthreport;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.earthreport.fragments.TimelineFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterDialog extends DialogFragment {


    private String selectedPeriod;
    private String selectedMin;
    private String selectedMax;
    private String selectedregion;


    private Spinner period;
    private Spinner minMagnitude;
    private Spinner maxMagnitude;
    private Spinner region;

    View.OnClickListener donefilterAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), "Test", Toast.LENGTH_LONG).show();

            selectedPeriod = String.valueOf(period.getSelectedItem());
            selectedMin = String.valueOf(minMagnitude.getSelectedItem());
            selectedMax = String.valueOf(maxMagnitude.getSelectedItem());
            selectedregion = String.valueOf(region.getSelectedItem());

            TimelineFragment timelineFragment = new TimelineFragment();
            timelineFragment.filterRefreshList(selectedPeriod, selectedMin, selectedMax, selectedregion);

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

        Button donefilter = view.findViewById(R.id.done_filter);
        donefilter.setOnClickListener(donefilterAction);


        //TODO: Add
        // This hour , Yesterday, weeek, month ,
        // min and max magnitude
        // region

        period = view.findViewById(R.id.period);
        minMagnitude = view.findViewById(R.id.min_of);
        maxMagnitude = view.findViewById(R.id.max_of);
        region = view.findViewById(R.id.region_of);


        return view;
    }


    public String getSelectedPeriod() {
        return selectedPeriod;
    }

    public String getSelectedMin() {
        return selectedMin;
    }

    public String getSelectedMax() {
        return selectedMax;
    }

    public String getSelectedregion() {
        return selectedregion;
    }

}
