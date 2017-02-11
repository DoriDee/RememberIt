package com.dorid.android.rememberit.locations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.dorid.android.rememberit.AppNavigator;
import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.analytics.Analytics;
import com.dorid.android.rememberit.analytics.AppRater;
import com.dorid.android.rememberit.locations.EditLocationFragment.LocationCreatedListener;
import com.dorid.android.rememberit.locations.SelectWifiDialog.SelectWifiListener;
import com.dorid.android.rememberit.reminders.RemindersActivity;
import com.dorid.android.rememberit.reminders.RemindersService;
import com.flurry.android.FlurryAgent;


/**
 * Created by Dori on 6/22/13.
 */
public class LocationsFragment extends SherlockListFragment implements LocationListener, SelectLocationDialogFragment.SelectLocationDialogListener,
																		SelectWifiListener, LocationCreatedListener 
{

    private LocationsAdapter adapter;
    private LocationsDataSource mLocationsDataSource;
    private List<LocationData> mLocations;
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private AppNavigator mAppNavigator;
    private SelectLocationDialogFragment mSelectLocationDialog;

    public LocationsFragment()
    {
    }

    @Override
    public void onStart()
    {
    	super.onStart();
//    	TapReason.registerFragment( this, getActivity() );
    }

    @Override
    public void onStop() 
    {
    	super.onStop();
//    	TapReason.unRegisterFragment( this, getActivity() );
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getSherlockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED);

        if (mLocationsDataSource == null)
        {
            mLocationsDataSource = new LocationsDataSource(getSherlockActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.locations_layout, null);

        loadData();
        
//        ImageButton btnAddNewLocation = (ImageButton)view.findViewById(R.id.btnAdd);
//        btnAddNewLocation.setVisibility(View.VISIBLE);
        
        final ListView listView = (ListView) view.findViewById(android.R.id.list);        
        
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        
        mWifiManager = (WifiManager) getSherlockActivity().getSystemService(Context.WIFI_SERVICE);

        return view;
    }

    private void searchForNewLocation()
    {
        // Check if we have a new wifi in town
        mWifiInfo = mWifiManager.getConnectionInfo();

        ConnectivityManager connManager = (ConnectivityManager) getSherlockActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        
        SharedPreferences prefs = getSherlockActivity().getSharedPreferences(RemindersService.WIFI_INFO, Context.MODE_PRIVATE);
        
        // If he returns after there is no wifi report the analytic
        boolean lastNoWifi = prefs.getBoolean(RemindersService.LAST_NO_WIFI, false);
        if (lastNoWifi)
        {
        	long launchCount = AppRater.getLaunchCount(getSherlockActivity());
        	Analytics.reportGAEvent(getSherlockActivity(), Analytics.CATEGORY_LOGIN, "no_wifi_came_back", "launchCount=" + launchCount, null);
            
            Map<String, String> analParams = new HashMap<String, String>();
            analParams.put("launchCount", String.valueOf(launchCount));
            FlurryAgent.logEvent("No_wifi_came_back", analParams);                    	
        }
        
        if (wifi.isConnected()) {

            String bssid = mWifiInfo.getBSSID();

            if (bssid != null)
            {
                if (isUnrecognizedWifi(bssid))
                {
                    // present a dialog asking where he is in
                    if (mSelectLocationDialog == null)
                    {
                        mSelectLocationDialog = new SelectLocationDialogFragment(mWifiInfo.getSSID(), this);
                        mSelectLocationDialog.show(getFragmentManager(), "LocationDialogFragment");
                    }
                    // If there are no locations we will prompt again for the user to select location
                    else if (!mSelectLocationDialog.isDisplayed() && mLocations.size() == 0)
                    {
                    	mSelectLocationDialog.show(getFragmentManager(), "LocationDialogFragment");
                    }
                }
            }
        }
        else
        {
            if (mLocations.size() == 0)
            {
                showWifiNotConnectedDialog();
            }
        }
    }


    private LocationData locationAlreadyExists(String locationName)
    {
        for (int i = 0; i < mLocations.size(); ++i)
        {
            if (mLocations.get(i).getLocationName() != null &&
                mLocations.get(i).getLocationName().equalsIgnoreCase(locationName))
            {
                return mLocations.get(i);
            }
        }

        return null;
    }

    public void locationSelected(int locationId)
    {
        LocationData locationData = LocationData.findLocationById(mLocations, locationId);

        Intent remindersActivity = new Intent(getSherlockActivity(), RemindersActivity.class);
        remindersActivity.putExtra("location", locationData);

        Analytics.reportGAEvent(getSherlockActivity(), Analytics.CATEGORY_UI_ACTION, Analytics.ACTION_LOCATION_SELECTED, locationData.getLocationName(), null);
        
        Map<String, String> analParams = new HashMap<String, String>();
        analParams.put("locationName", locationData.getLocationName());
        FlurryAgent.logEvent("Location_Selected", analParams);
        
        startActivity(remindersActivity);
    }


    public void presentPossibleWifiLocations() {    	
    	SelectWifiDialog selectWifiDialog = new SelectWifiDialog(this);
    	selectWifiDialog.show(getFragmentManager(), "CreateNewLocationFragment");    	
    }
    
	@Override
	public void onWifiSelected(String wifiName) {		
		LocationData locationData = new LocationData();
		locationData.setWifiName(wifiName);
		EditLocationFragment editLocationDialog = new EditLocationFragment(locationData, this);
		editLocationDialog.show(getFragmentManager(), "EditLocationFragment");    	
	}

	@Override
	public void onLocationCreated(LocationData location) {
		Analytics.reportGAEvent(getSherlockActivity(), Analytics.CATEGORY_UI_ACTION,
			    Analytics.ACTION_NEW_LOCATION_CREATED, location.getLocationName(), null);
		Map<String, String> analParams = new HashMap<String, String>();
		analParams.put("locationName", location.getLocationName());
		FlurryAgent.logEvent("New_Location_Created", analParams);            

//		if location.g
		
		// Since the location was received from the history we don't get the bssid		
		LocationData locationData = mLocationsDataSource.addLocation(null, location.getWifiName(), location.getLocationName(), location.getIconId());
        mLocations.add(locationData);
        loadData();
	}
	
    public void onLocationSelected(String locationName, int locationIcon)
    {
        mSelectLocationDialog = null;

        // Check if the location is already assigned - if so present are u sure u want to replace
        LocationData locationExists = locationAlreadyExists(locationName);
        if (locationExists != null)
        {
            showOverwriteWifiDialog(locationExists, mWifiInfo);
        }
        else
        {
        	Analytics.reportGAEvent(getSherlockActivity(), Analytics.CATEGORY_UI_ACTION,
        						    Analytics.ACTION_LOCATION_CREATED, locationName, null);
        	Map<String, String> analParams = new HashMap<String, String>();
            analParams.put("locationName", locationName);
            FlurryAgent.logEvent("Location_Created", analParams);            
            
            LocationData locationData = mLocationsDataSource.addLocation(mWifiInfo.getBSSID(), mWifiInfo.getSSID(), locationName, locationIcon);
            mLocations.add(locationData);
            loadData();
        }
    }

    private Boolean isUnrecognizedWifi(String bssid)
    {
        for (int i = 0; i < mLocations.size(); ++i)
        {
            if (mLocations.get(i).getBssid() == null || bssid.equals(mLocations.get(i).getBssid()))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mLocationsDataSource.open();
        searchForNewLocation();                
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationsDataSource.close();
    }

    public void loadData() {

        if (mLocationsDataSource == null)
        {
            mLocationsDataSource = new LocationsDataSource(getSherlockActivity());
        }

        mLocationsDataSource.open();

        mLocations =  mLocationsDataSource.getAllLocations();

        adapter = new LocationsAdapter(getSherlockActivity(), 0, mLocations, this);
        setListAdapter(adapter);     
    }

    private void clearWifiAssignment(LocationData location)
    {
        getLocationsDataSource().clearWifiAssignment(location.getLocationId());
        location.setWifiName(null);
        location.setBssid(null);
    }

    private void assignWifiToLocation(WifiInfo wifiInfo, LocationData location)
    {
        getLocationsDataSource().assignWifiToLocation(wifiInfo, location.getLocationId());
        location.setWifiName(wifiInfo.getSSID());
        location.setBssid(wifiInfo.getBSSID());
    }

    private void showOverwriteWifiDialog(final LocationData locationData, final WifiInfo wifiInfo)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                    	
                    	Analytics.reportGAEvent(getSherlockActivity(), Analytics.CATEGORY_UI_ACTION,
                    						    Analytics.ACTION_LOCATION_OVERWRITED, locationData.getLocationName(), null);
                    	
                    	Map<String, String> analParams = new HashMap<String, String>();
                        analParams.put("locationName", locationData.getLocationName());
                        FlurryAgent.logEvent("Location_Overwrited", analParams);
                        
                        //clearWifiAssignment(overridenLocation);
                        assignWifiToLocation(wifiInfo, locationData);
                        adapter.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked                    	
                    	Analytics.reportGAEvent(getSherlockActivity(), Analytics.CATEGORY_UI_ACTION,
    						    				Analytics.ACTION_LOCATION_OVERWRITE_CANCEL, locationData.getLocationName(), null);
                    	
                    	analParams = new HashMap<String, String>();
                        analParams.put("locationName", locationData.getLocationName());
                        FlurryAgent.logEvent("Location_Overwrite_Cancel", analParams);
                        
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setTitle("Wifi Already Assigned")
               .setMessage("This will replace the current assignment for " + locationData.getLocationName() + ".\nAre you sure?")
               .setPositiveButton("Yes", dialogClickListener)
               .setNegativeButton("No", dialogClickListener).show();
    }

    private void showWifiNotConnectedDialog()
    {    	
		// Increment launch counter
		long launch_count = AppRater.getLaunchCount(getSherlockActivity());

    	Analytics.reportGAEvent(getSherlockActivity(), Analytics.CATEGORY_LOGIN, "no_wifi", "launch_count=" + launch_count, null);
    	Map<String, String> analParams = new HashMap<String, String>();
        analParams.put("launch_count", String.valueOf(launch_count));
        FlurryAgent.logEvent("No_wifi", analParams);        
        
        // Set the current bssid on the shared preferences
        SharedPreferences prefs = getSherlockActivity().getSharedPreferences(RemindersService.WIFI_INFO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(RemindersService.LAST_NO_WIFI, true);
        editor.commit();
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        builder.setTitle(getString(R.string.no_wifi_title))
               .setMessage(getString(R.string.no_wifi_message))
               .setNeutralButton("OK", null).show();
    }

    /*
    private LocationData isBSSIDAssigned(String bssid)
    {
        for (LocationData location : getLocations())
        {
            if (location.getBssid() != null && location.getBssid().equals(bssid))
            {
                return location;
            }
        }

        return null;
    }


    @Override
    public void updateWifiInfo(LocationData locationData) {

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        if (wifiInfo.getBSSID() == null)
        {
            // TODO : Notify the user that he is no longer connected to wifi
        }
        else
        {
            // Check if this location is already assigned to a hotspot
            if (locationData.getBssid() != null)
            {
                showOverwriteWifiDialog(locationData, locationData, wifiInfo);
            }
            else
            {
                // Make sure that this bssid is not already assigned to a different location
                LocationData assignedLocation = isBSSIDAssigned(wifiInfo.getBSSID());

                if (assignedLocation != null)
                {
                    showOverwriteWifiDialog(assignedLocation, locationData, wifiInfo);
                }
                else
                {
                    assignWifiToLocation(wifiInfo, locationData);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
*/

    public LocationsDataSource getLocationsDataSource() {
        return mLocationsDataSource;
    }

    public void setLocationsDataSource(LocationsDataSource locationsDataSource) {
        mLocationsDataSource = locationsDataSource;
    }

    public List<LocationData> getLocations() {
        return mLocations;
    }

    public void setLocations(List<LocationData> locations) {
        mLocations = locations;
    }

	@Override
	public void locationEdit(int locationId) {
		
		LocationData locationData = LocationData.findLocationById(mLocations, locationId);

		EditLocationFragment editLocationDialog = new EditLocationFragment(locationData, this);
		editLocationDialog.show(getFragmentManager(), "EditLocationFragment");
	}
}

