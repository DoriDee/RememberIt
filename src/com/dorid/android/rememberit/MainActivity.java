package com.dorid.android.rememberit;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TabHost;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.dorid.android.rememberit.locations.LocationData;
import com.dorid.android.rememberit.locations.LocationsActivity;
import com.dorid.android.rememberit.locations.LocationsDataSource;
import com.dorid.android.rememberit.reminders.RemindersFragment;
import com.dorid.android.rememberit.reminders.RemindersService;


public class MainActivity extends SherlockFragmentActivity implements AppNavigator
{
    TabHost mTabHost;
    TabManager mTabManager;

    LocationsDataSource mLocationsDataSource;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.Theme_Sherlock_Light); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
              
//        setContentView(R.layout.fragment_holder);

        //getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //getSupportActionBar().hide();


/*
        final ActionBar actionBar = getSupportActionBar();

        // add tabs
        Tab tab1 = actionBar.newTab()
                .setText("Reminders")
                .setTabListener(new TabListener<LocationsFragment>(
                        this, "reminders", LocationsFragment.class));
        actionBar.addTab(tab1);

        Tab tab2 = actionBar.newTab()
                .setText("Locations")
                .setTabListener(new TabListener<LocationsFragment>(
                        this, "locations", LocationsFragment.class));
        actionBar.addTab(tab2);

        // check if there is a saved state to select active tab
        if( savedInstanceState != null ){
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(ACTIVE_TAB));
        }*/
/*
        for (int i = 1; i <= 3; i++) {
            ActionBar.Tab tab = getSupportActionBar().newTab();
            tab.setText("Tab " + i);
            tab.setTabListener(this);
            getSupportActionBar().addTab(tab);
        }

        // check if there is a saved state to select active tab
        if( savedInstanceState != null ){
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(ACTIVE_TAB));
        }
*/
        WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // Set the current bssid on the shared preferences
        SharedPreferences prefs = getSharedPreferences(RemindersService.WIFI_INFO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(RemindersService.LAST_BSSID, wifiInfo.getBSSID());
        editor.commit();

        Intent locationsIntent = new Intent(this, LocationsActivity.class);
        startActivity(locationsIntent);

        /*
        mLocationsDataSource = new LocationsDataSource(this);
        mLocationsDataSource.open();

        ArrayList<LocationData> locations =  (ArrayList<LocationData>)mLocationsDataSource.getAllLocations();

        mLocationsDataSource.close();




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
            LocationsFragment locationsFragment = new LocationsFragment(this);

            Bundle locationArgs = new Bundle();
            locationArgs.putParcelableArrayList("locations", locations);

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            locationsFragment.setArguments(locationArgs);

            getSupportActionBar().setTitle("Locations");

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, locationsFragment).commit();
        }
*/
        /*
        mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

        Bundle locationArgs = new Bundle();
        locationArgs.putParcelableArrayList("locations", locations);

       // mTabManager.addTab(mTabHost.newTabSpec("reminders").setIndicator("Reminders"), RemindersFragment.class, locationArgs);
        mTabManager.addTab(mTabHost.newTabSpec("locations").setIndicator("Locations"), LocationsFragment.class, locationArgs);

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }*/

/*
        ActionBar.Tab remindersTab = getSupportActionBar().newTab();
        remindersTab.setText(getString(R.string.reminders));
        remindersTab.setTabListener(this);

        getSupportActionBar().addTab(remindersTab);

        ActionBar.Tab locationsTab = getSupportActionBar().newTab();
        locationsTab.setText(getString(R.string.locations));
        locationsTab.setTabListener(this);
        Intent locationsIntent = new Intent(this, LocationsActivity.class);

        getSupportActionBar().addTab(locationsTab); */
/*
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();

        TabSpec remindersSpec = tabHost.newTabSpec("reminders");
        remindersSpec.setIndicator("Reminders");
        Intent remindersIntent = new Intent(this, LocationsActivity.class);
        remindersSpec.setContent(remindersIntent);

        TabSpec locationsSpec = tabHost.newTabSpec("locations");
        locationsSpec.setIndicator("Locations");
        Intent locationsIntent = new Intent(this, LocationsActivity.class);
        locationsSpec.setContent(locationsIntent);

        tabHost.addTab(remindersSpec);
        tabHost.addTab(locationsSpec); */

    }

    @Override
    protected void onStart()
    {
    	super.onStart();    	
    }
    
    @Override
    protected void onStop()
    {
    	super.onStop();    	
    }
    
    @Override
    public void locationSelected(LocationData location)
    {
        getSupportActionBar().setTitle(location.getLocationName());

        // Activate the reminders fragment for that location
        RemindersFragment remindersFragment = new RemindersFragment();

        Bundle args = new Bundle();
        args.putParcelable("location", location);
        remindersFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, remindersFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private Boolean anyAssignedLocations(List<LocationData> locations)
    {
        for (LocationData location : locations)
        {
            if (location.getBssid() != null)
            {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
/*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save active tab
        outState.putInt(ACTIVE_TAB,
                getSupportActionBar().getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }

    public class TabListener<T extends SherlockFragment> implements ActionBar.TabListener{
        private SherlockListFragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = (SherlockListFragment) Fragment.instantiate(
                        mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }

        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                // Detach the fragment, because another one is being attached
                ft.detach(mFragment);
            }
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }



    @Override
    public void onTabReselected(Tab tab, FragmentTransaction transaction) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {
 //       mSelected.setText("Selected: " + tab.getText());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
*/
    /**
     * This is a helper class that implements a generic mechanism for
     * associating fragments with the tabs in a tab host.  It relies on a
     * trick.  Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show.  This is not sufficient for switching
     * between fragments.  So instead we make the content part of the tab host
     * 0dp high (it is not shown) and the TabManager supplies its own dummy
     * view to show as the tab content.  It listens to changes in tabs, and takes
     * care of switch to the correct fragment shown in a separate content area
     * whenever the selected tab changes.
     */
    public static class TabManager implements TabHost.OnTabChangeListener {
        private final FragmentActivity mActivity;
        private final TabHost mTabHost;
        private final int mContainerId;
        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
        TabInfo mLastTab;

        static final class TabInfo {
            private final String tag;
            private final Class<?> clss;
            private final Bundle args;
            private Fragment fragment;

            TabInfo(String _tag, Class<?> _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
            mActivity = activity;
            mTabHost = tabHost;
            mContainerId = containerId;
            mTabHost.setOnTabChangedListener(this);
        }

        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mActivity));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);

            // Check to see if we already have a fragment for this tab, probably
            // from a previously saved state.  If so, deactivate it, because our
            // initial state is that a tab isn't shown.
            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
            if (info.fragment != null && !info.fragment.isDetached()) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.detach(info.fragment);
                ft.commit();
            }

            mTabs.put(tag, info);
            mTabHost.addTab(tabSpec);
        }

        @Override
        public void onTabChanged(String tabId) {
            TabInfo newTab = mTabs.get(tabId);
            if (mLastTab != newTab) {
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                if (mLastTab != null) {
                    if (mLastTab.fragment != null) {
                        ft.detach(mLastTab.fragment);
                    }
                }
                if (newTab != null) {
                    if (newTab.fragment == null) {
                        newTab.fragment = Fragment.instantiate(mActivity,
                                newTab.clss.getName(), newTab.args);
                        ft.add(mContainerId, newTab.fragment, newTab.tag);
                    } else {
                        ft.attach(newTab.fragment);
                    }
                }

                mLastTab = newTab;
                ft.commit();
                mActivity.getSupportFragmentManager().executePendingTransactions();
            }
        }
    }
}