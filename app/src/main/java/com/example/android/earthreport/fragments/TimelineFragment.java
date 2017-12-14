package com.example.android.earthreport.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.earthreport.FilterDialog;
import com.example.android.earthreport.R;
import com.example.android.earthreport.ViewPagerAdapter;
import com.example.android.earthreport.model.EarthQuakes;
import com.example.android.earthreport.network.GetEarthquakeData;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {


    private static final int MENU_ITEM_ABOUT = 1000;
    ArrayList<EarthQuakes> earthQuakesList;
    //String[] tabTitle = {"Today", "Yesterday", "Week", "Month"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private View view;
    private View view2;
    private String todayURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_day.geojson";
    private ProgressBar progressBar;
    private ListView earthquakeListView;

    public TimelineFragment() {
        // Required empty public constructor
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
        menu.add(0, MENU_ITEM_ABOUT, 102, R.string.about);
        inflater.inflate(R.menu.menu_timline, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Toast.makeText(getContext(), "Timline refresh", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.action_filter) {
            //Toast.makeText(getContext(), "Timline Filter", Toast.LENGTH_SHORT).show();

            //-------Show dialog-----------
            FragmentManager fm = getFragmentManager();
            FilterDialog filterDialog = new FilterDialog();
            filterDialog.show(fm, "");

            //-------------Show Activity---
            //  Intent intent = new Intent(getContext(), ShowEarthquakeDetails.class);
            //   startActivity(intent);


        }
        return super.onOptionsItemSelected(item);
    }

    //Always add adapter first! Always add adapter first! Always add adapter first!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO:Set the timline view I have two views :Timline Change
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_timeline, container, false);

        //test the one list only
        view2 = inflater.inflate(R.layout.fragment_datalist, container, false);

        earthquakeListView = view2.findViewById(R.id.list);

        progressBar = view2.findViewById(R.id.progress_bar);

        earthQuakesList = new ArrayList<>();

        // Get data from web of this hour earthquakes
        GetEarthquakeData getEarthquakeData = new GetEarthquakeData(getContext(), earthquakeListView, earthQuakesList);
        getEarthquakeData.execute(todayURL);
        viewPager = view.findViewById(R.id.viewPager);
        //viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        //Sudo don't forget ever
        // first add adapter then do somthing! :)
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                viewPager.setCurrentItem(position, false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view2;
    }


    private void setupViewPager(ViewPager viewPager) {

        TodayFragment todayFragment= new TodayFragment();
        YesterdayFragment yesterdayFragment = new YesterdayFragment();
        WeekFragment weekFragment = new WeekFragment();
        MonthFragment monthFragment = new MonthFragment();

        adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(todayFragment,TodayFragment.TODAY);
        adapter.addFragment(yesterdayFragment, YesterdayFragment.YESTERDAY);
        adapter.addFragment(weekFragment, WeekFragment.WEEK);
        adapter.addFragment(monthFragment, MonthFragment.MONTH);
        viewPager.setAdapter(adapter);
    }

    public void displayProgressBar(boolean display) {
        if (display) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }



}
