package com.badarshahzad.android.earthreport.controller.main;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.badarshahzad.android.earthreport.R;
import com.badarshahzad.android.earthreport.controller.adapters.EarthQuakeAdapter;
import com.badarshahzad.android.earthreport.controller.adapters.ViewPagerAdapter;
import com.badarshahzad.android.earthreport.controller.home.HomeFragment;
import com.badarshahzad.android.earthreport.controller.setting.SettingFragment;
import com.badarshahzad.android.earthreport.controller.timeline.TimelineFragment;
import com.badarshahzad.android.earthreport.model.utilties.ReminderUtilities;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**what is context This is an abstract class whose implementation is provided by the
 *  Android system. It allows access to application-specific resources and classes,
 *  as well as up-calls for application-level operations such as launching activities,
 *  broadcasting and receiving intents*/

public class EarthquakeActivity extends AppCompatActivity {

    private static final String TAG = EarthquakeActivity.class.getSimpleName();
    public static ConstraintLayout root;

    private HomeFragment homeFragment;
    private TimelineFragment timelineFragment;
    private SettingFragment settingFragment;

    private static InterstitialAd interstitialAd;
    private ViewPager viewPager;

    private FirebaseAnalytics mFirebaseAnalytics;

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment = new HomeFragment();
        timelineFragment = new TimelineFragment();
        settingFragment = new SettingFragment();
        viewPagerAdapter.addFragment(homeFragment);
        viewPagerAdapter.addFragment(timelineFragment);
        viewPagerAdapter.addFragment(settingFragment);
        viewPager.setAdapter(viewPagerAdapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static void showAd(){

        if(interstitialAd == null){
            interstitialAd = new InterstitialAd(MainApplication.getmInstance());
            interstitialAd.setAdUnitId("ca-app-pub-9307941937011389/6639337180");
            interstitialAd.loadAd(new AdRequest.Builder().build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }

            });
        }

        if(interstitialAd !=null) {

            if (interstitialAd.isLoaded()) {
                Log.i("EarthquakeActivity", "show me");
                interstitialAd.show();
            } else {
                Log.d("TAG", " Interstitial not loaded");
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_earthquake_activity);

        //get the root referance
        root = findViewById(R.id.root);

//          sample id: ca-app-pub-3940256099942544/5224354917
//        real id : ca-app-pub-9307941937011389~2891663869
        MobileAds.initialize(this, "ca-app-pub-9307941937011389~2891663869");//id

        //Sample unit: 	ca-app-pub-3940256099942544/1033173712
        //Real-id: ca-app-pub-9307941937011389/6639337180
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-9307941937011389/6639337180");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

        if(!isConnectedNetwork()){
            Toast.makeText(this,"Internet is not connected",Toast.LENGTH_LONG).show();
        }


        /**The purpose of using viewpager is to mantain the instance of fragment
         * I figure it the viewpage don't throw away the instance when the
         * view change as it mantain it in memory so I don't have to add extra code for
         * fragment mantain on each time menu click and load the fragment ag&ag& (again and again)*/

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);
        setTitle("Home");

        final BottomNavigationView bottomNavg = findViewById(R.id.navBottom);
        bottomNavg.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    //CheckedTODO:whenever the menu item click the fragment replace again and
                    // life cycle its not good try to check before going to replace the fragment
                    //CheckedTODO:Add viewpager to navigate with swap
                    case R.id.home:
                        setTitle("Home");
                        viewPager.setCurrentItem(0);
                        return true;

                    case R.id.timeline:
                        setTitle("Earthquake List");
                        viewPager.setCurrentItem(1);
                        return true;

                    case R.id.setting:
                        setTitle("Setting");
                        viewPager.setCurrentItem(2);
                        return true;
                }
                return false;
            }

        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomNavg.getMenu().getItem(position).setChecked(true);
                setTitle(String.valueOf(bottomNavg.getMenu().getItem(position)));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        checkInternetAvailable();

        scheduleJob(this);

    }

    private   void checkInternetAvailable() {

        try {
            String networkStatus = "";
            final ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            final android.net.NetworkInfo internet = manager.getActiveNetworkInfo();

            final WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {


                if (internet == null || internet.equals("null")) {
                    Snackbar.make(EarthquakeActivity.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                            .setAction("Turn on Wifi", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    wifiManager.setWifiEnabled(true);
                                    Toast.makeText(EarthquakeActivity.this, "WiFi Enabled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                    return;
                }

            }

            if (internet.getType() == ConnectivityManager.TYPE_WIFI) {
                networkStatus = "wifi";
            } else if (internet.getType() == ConnectivityManager.TYPE_MOBILE) {
                networkStatus = "mobileData";
            } else {
                networkStatus = "noNetwork";
            }
        }catch (Exception e){
            Log.e(TAG, "checkInternetAvailable: ",e );
        }
    }

    private void scheduleJob(Context context) {

        Log.i(TAG, "scheduleJob: ");
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        //creating new job and adding it with dispatcher
        ReminderUtilities.scheduleEarthquakeReminder(this);
    }

    public boolean isConnectedNetwork(){
        final ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork !=null && activeNetwork.isConnectedOrConnecting();
    }

}
