package com.example.android.earthreport;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.android.earthreport.fragments.MonthFragment;
import com.example.android.earthreport.fragments.PastDayFragment;
import com.example.android.earthreport.fragments.WeekFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 11/28/17.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();
    int numberOfTabs;

    public ViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
//            case 0:
//                HourFragment hourFragment = new HourFragment();
//                return hourFragment;
            case 0:
                PastDayFragment pastDayFragment = new PastDayFragment();
                return pastDayFragment;
            case 1:
                WeekFragment weekFragment = new WeekFragment();
                return weekFragment;
            case 2:
                MonthFragment monthFragment = new MonthFragment();
                return monthFragment;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

}
