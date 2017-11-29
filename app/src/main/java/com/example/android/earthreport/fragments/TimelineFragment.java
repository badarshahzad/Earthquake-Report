package com.example.android.earthreport.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.earthreport.R;
import com.example.android.earthreport.ViewPagerAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimelineFragment extends Fragment {


    private static final int MENU_ITEM_ABOUT = 1000;
    String[] tabTitle = {"Hour", "Yesterday", "Week", "Month"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private View view;

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
            Toast.makeText(getContext(), "Yes", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    //Always add adapter first! Always add adapter first! Always add adapter first!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_timeline, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        //viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tablayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        //Sudo don't forget ever
        // first add adapter then do somthing! :)
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.addTab(tabLayout.newTab().setText(HourFragment.HOUR));
//        tabLayout.addTab(tabLayout.newTab().setText(PastDayFragment.YESTERDAY));
//        tabLayout.addTab(tabLayout.newTab().setText(WeekFragment.WEEK));
//        tabLayout.addTab(tabLayout.newTab().setText(MonthFragment.MONTH));


     /*   tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/

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

        return view;
    }


    private void setupViewPager(ViewPager viewPager) {

        HourFragment hourFragment = new HourFragment();
        PastDayFragment pastDayFragment = new PastDayFragment();
        WeekFragment weekFragment = new WeekFragment();
        MonthFragment monthFragment = new MonthFragment();

        adapter = new ViewPagerAdapter(getFragmentManager());
        //  adapter.addFragment(hourFragment,hourFragment.HOUR);
        adapter.addFragment(pastDayFragment, PastDayFragment.YESTERDAY);
        adapter.addFragment(weekFragment, WeekFragment.WEEK);
        adapter.addFragment(monthFragment, MonthFragment.MONTH);
        viewPager.setAdapter(adapter);
    }


}
