package com.example.android.earthreport;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterDialog extends DialogFragment {


    View.OnClickListener donefilterAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), "Test", Toast.LENGTH_LONG).show();
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

        return view;
    }

}
