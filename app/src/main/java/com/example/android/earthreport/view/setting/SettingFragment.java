package com.example.android.earthreport.view.setting;


import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.android.earthreport.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

//import android.preference.PreferenceFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends PreferenceFragment {

    public static final String TAG = SettingFragment.class.getSimpleName();

    //Converting preference xml as a fragment

    /*
    * These preferences will automatically save to SharedPreferences as the user interacts with them.
    * To retrieve an instance of SharedPreferences that the preference hierarchy in this fragment will use,
    * call getDefaultSharedPreferences(android.content.Context) with a context in the same package as this fragment.
    * */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        //Fragment protect from destroy and recreate and retain
        // the current instance of the fragment when the activity is recreated.
        // CheckedTodo: The retaininstance helping me on activity orientation change
        setRetainInstance(true);
//        Log.i(TAG, "onCreate: ");
    }

}
