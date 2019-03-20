package com.badarshahzad.android.earthreport.controller.favourit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.badarshahzad.android.earthreport.R;
import com.badarshahzad.android.earthreport.model.pojos.FavoriteCountries;
import com.badarshahzad.android.earthreport.model.utilties.ParseFavoritCountriesJsonUtils;
import com.badarshahzad.android.earthreport.controller.home.HomeFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    public static final String TAG = FavouriteActivity.class.getSimpleName();
    private FloatingActionButton fab;
    private ListView favouritListView;
    private List<FavoriteCountries> favoriteCountriesList;
    private SharedPreferences shPref;
    private FavouritListAdapter favouritListAdapter;
    private TextView emptyListMessage;
    private ImageView emptyListMessageImage;
    private SharedPreferences.OnSharedPreferenceChangeListener shPreflistener;

//    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
//        MobileAds.initialize(this, "ca-app-pub-4583713636574908~2197491969");//id

//        adView = findViewById(R.id.adView);

//        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);



        emptyListMessage = findViewById(R.id.emptyListMessage);
        emptyListMessageImage = findViewById(R.id.noFavoriteEntry);

        fab = findViewById(R.id.fabButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //access the already designed Add Alert dialog and reuse in activity to add new item
                HomeFragment homeObj = new HomeFragment();

                //going to show the add alert dialog for user
                homeObj.showAddAlertDialog(getLayoutInflater(),
                        FavouriteActivity.this, FavouriteActivity.this);
            }
        });

        shPreflistener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                favoriteCountriesList = getTheStoreStringAsList();
                favouritListAdapter.clear();
                favouritListAdapter.addAll(favoriteCountriesList);


                Toast.makeText(FavouriteActivity.this,"Change preferance",Toast.LENGTH_SHORT).show();
            }
        };


        //update the update after new entry
        favoriteCountriesList = getTheStoreStringAsList();
//        if(favoriteCountriesList.size()==0) return;

        Log.i(TAG, " size: " + favoriteCountriesList.size());
        favouritListView = findViewById(R.id.favouritList);
        favouritListAdapter = new FavouritListAdapter(this, R.layout.listview_item, favoriteCountriesList);
        favouritListView.setAdapter(favouritListAdapter);


    }

    private List<FavoriteCountries> getTheStoreStringAsList() {

        List<FavoriteCountries> favoriteCountriesList = new ArrayList<>();
        shPref = PreferenceManager.getDefaultSharedPreferences(FavouriteActivity.this);
        String favouritString = shPref.getString(HomeFragment.FAVOURIT_LIST, "NoValue");
        favoriteCountriesList = ParseFavoritCountriesJsonUtils.parseJsonInToList(favouritString);

        if(favoriteCountriesList!=null) Log.i(TAG, "getTheStoreStringAsList: get the fresh list size: "+favoriteCountriesList.size());

        return favoriteCountriesList;
    }

    //Referance: they discuss about the registering and unregistering the
    // shared prefrances listener as it become memory leaks

    @Override
    protected void onResume() {
        super.onResume();

        shPref.registerOnSharedPreferenceChangeListener(shPreflistener);

    }

    @Override
    protected void onPause() {
        super.onPause();

        shPref.unregisterOnSharedPreferenceChangeListener(shPreflistener);

    }



    private void checkListEmptyAndUpdateView(List<FavoriteCountries> list) {


        shPref.edit().remove(HomeFragment.FAVOURIT_LIST).putString(HomeFragment.FAVOURIT_LIST,new Gson().toJson(list).toString()).apply();

        /**
         * This notifyDate set change will update the associated lists
         * such as in adapter re remove the item from favList(list)
         * is auto matically update the set adapter list favoritCounrtiesList*
         * */
        favouritListAdapter.notifyDataSetChanged();
        //favouritListAdapter.addAll(list);

        Log.i(TAG, "size of delete list: "+favoriteCountriesList.size());
        //update the shared preference store string



       // shPref.edit().putString(HomeFragment.FAVOURIT_LIST,new Gson().toJson(favoriteCountriesList).toString()).apply();

        if(favoriteCountriesList.size()==0){

            //show the empty list meesage
            emptyListMessage.setVisibility(View.VISIBLE);
            //show the empty list icon
            emptyListMessageImage.setVisibility(View.VISIBLE);

        }else{

            //hide the empty list meesage
            emptyListMessage.setVisibility(View.INVISIBLE);
            //hide the empty list icon
            emptyListMessageImage.setVisibility(View.INVISIBLE);
        }
    }

    public class FavouritListAdapter extends ArrayAdapter<FavoriteCountries> {

        public final String TAG = FavouritListAdapter.class.getSimpleName();

        private List<FavoriteCountries> favList ;
        public FavouritListAdapter(@NonNull Context context, int resource, @NonNull List<FavoriteCountries> list) {
            super(context, resource, list);
            favList =  list;

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View view = convertView;

            if (view == null) {
                Log.i(TAG, " NUll ");
                view = LayoutInflater.from(getContext()).inflate(R.layout.favorit_country_item, parent, false);
            }

            Log.i(TAG, " Not null");

            FavoriteCountries favoriteCountries = favList.get(position);

            final String country = favoriteCountries.getCountry();
            final double magnitude = favoriteCountries.getMagnitude();

            TextView magnitudeView = view.findViewById(R.id.magnitude);
            magnitudeView.setText(String.valueOf(magnitude));
            TextView countryView = view.findViewById(R.id.country);
            countryView.setText(country);
            ImageButton timeStampOfAddAlert = view.findViewById(R.id.imageButton);
            timeStampOfAddAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //delete the item user tap on delete icon in list view
                    Log.i(TAG, "onClick: size before: "+favList.size());
                    favList.remove(position);
                    Log.i(TAG, "onClick: size after: "+favList.size());

                    //update the adapter of listview and check the list view
                    checkListEmptyAndUpdateView(favList);

                    //Toast.makeText(getContext(),"Deleted: "+ magnitude+"  "+country,Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }

        @Nullable
        @Override
        public FavoriteCountries getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getCount() {
            return super.getCount();
        }
    }



}

