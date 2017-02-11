package com.dorid.android.rememberit.locations;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.analytics.AppRater;
import com.dorid.android.rememberit.reminders.RemindersService;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.Tracker;

/**
 * Created by Dori on 8/2/13.
 */
public class LocationsActivity extends SherlockFragmentActivity{

	LocationsFragment mLocationsFragment;

    private GoogleAnalytics mGaInstance;
    private Tracker mGaTracker;

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        //setTheme(R.style.Theme_Sherlock_Light_DarkActionBar); //Used for theme switching in samples
    	
    	super.onCreate(savedInstanceState);

//    	TapForTap.initialize(this, "eca2453f5df5470067c3ff059434a1b3");
    	
//     	Intent serviceIntent = new Intent();
//        serviceIntent.setAction("com.dorid.android.rememberit.wifi.WlanLockService");
//        startService(serviceIntent);        
        
        setTitle(getString(R.string.locations));

        setContentView(R.layout.fragment_holder);        
                
/* davidi: this is done in the RemindersActivity
        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.ab_layout, null);

        ImageButton back = (ImageButton)v.findViewById(R.id.btnBack);
        back.setVisibility(View.GONE);

        TextView title = (TextView)v.findViewById(R.id.abTitle);
        title.setText(getString(R.string.locations_title));
*/        
    //    forceLTRIfSupported(v);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getSupportActionBar().setDisplayShowCustomEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setDisplayUseLogoEnabled(false);
        //getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#33B5E5")));
        
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(v);

        
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // Set the current bssid on the shared preferences
        SharedPreferences prefs = getSharedPreferences(RemindersService.WIFI_INFO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(RemindersService.LAST_BSSID, wifiInfo.getBSSID());
        editor.commit();

        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of the locations fragment
            if (mLocationsFragment == null)
            {
            	mLocationsFragment = new LocationsFragment();	
            }
            
//            Bundle locationArgs = new Bundle();
//            locationArgs.putParcelableArrayList("locations", locations);

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
//            locationsFragment.setArguments(locationArgs);

            //getSupportActionBar().setTitle("Locations");

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mLocationsFragment).commit();
        }
                
        mGaInstance = GoogleAnalytics.getInstance(this);
        mGaInstance.setDryRun(false);
        
        mGaInstance.getLogger().setLogLevel(LogLevel.VERBOSE);

        // Use the GoogleAnalytics singleton to get a Tracker.
        mGaTracker = mGaInstance.getTracker("UA-44971869-1"); // Placeholder tracking ID.
                
        // Set Logger verbosity.
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStart() {
    	super.onStart();
    	FlurryAgent.onStartSession(this, "23SZ7Q5HSZZCK3K56FV3");
    
//    	TapReason.register(this);
    	
    	AppRater.app_launched(this);
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.locations_menu, menu);

        // set up a listener for the refresh item
        final MenuItem addItem = (MenuItem) menu.findItem(R.id.menu_add);
        
        addItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            // on selecting show progress spinner for 1s
            public boolean onMenuItemClick(MenuItem item) {
            	      
            	mLocationsFragment.presentPossibleWifiLocations();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    private void forceLTRIfSupported(View v)
//    {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
//            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//            v.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//        }
//    }

    @Override
    public void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
        EasyTracker.getInstance(this).activityStop(this);
//    	TapReason.unRegister(this);
    }

}