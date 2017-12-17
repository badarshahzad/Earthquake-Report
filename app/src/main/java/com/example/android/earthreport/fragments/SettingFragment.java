package com.example.android.earthreport.fragments;


import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.example.android.earthreport.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

//import android.preference.PreferenceFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    //Converting preference xml as a fragment

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {
        String value = o.toString();
        preference.setSummary(value);
        return true;
    }


    //Couple of testing to make Xml preference to Fragment and at the end I found something new
    //That is not available in android support libraries

   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Fragment a = new SettingPreferenceFragment();

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        //getFragmentManager().beginTransaction().replace(android.R.id.content,new SettingPreferenceFragment()).commit();
       // getFragmentManager().beginTransaction().replace(R.layout.fragment_setting,new SettingPreferenceFragment())
        return null;
    }

    public static class SettingPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.setting);

        }
    }

*/

}
