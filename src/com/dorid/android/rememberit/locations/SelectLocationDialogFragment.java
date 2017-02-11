package com.dorid.android.rememberit.locations;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.analytics.Analytics;
import com.dorid.android.rememberit.database.LocationsTable;
import com.flurry.android.FlurryAgent;

/**
 * Created by Dori on 7/6/13.
 */
@SuppressLint("ValidFragment")
public class SelectLocationDialogFragment extends DialogFragment{

    private final int MAX_CHAR_NUMBER = 15;


    public interface SelectLocationDialogListener {
        public void onLocationSelected(String locationName, int locationIcon);
    }

    //private List<LocationData> mLocations;
    private SelectLocationDialogListener mListener;
    private String mSSID;
    private Boolean mIsDisplayed;
    
    public SelectLocationDialogFragment()
    {
    	
    }
    
    public SelectLocationDialogFragment(String ssid, SelectLocationDialogListener listener)
    {
//        this.mLocations = locations;
        this.mListener = listener;
        mSSID = ssid;
    }

    @Override
    public void onStart()
    {
    	super.onStart();
    }

    @Override
    public void onStop() 
    {
    	super.onStop();    	
    }
    
    private LocationData[] getPossibleLocations()
    {
        /*CharSequence[] locationNames = new CharSequence[mLocations.size()];

        int i = 0;
        for (LocationData location : mLocations)
        {
            locationNames[i++] = location.getLocationName();
        }

        return locationNames; */

        LocationData home = new LocationData();
        home.setIconId(LocationsTable.HOME_ICON);
        home.setLocationName(LocationsTable.HOME_NAME);

        LocationData work = new LocationData();
        work.setIconId(LocationsTable.WORK_ICON);
        work.setLocationName(LocationsTable.WORK_NAME);

        LocationData none = new LocationData();
        none.setLocationName("Ask me again later");
        
        return new LocationData[]{home, work, none};
    }       
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

    	mIsDisplayed = true;
    	 
        String title = "New hotspot : \"";

        if (mSSID != null)
        {
            if (mSSID.length() > MAX_CHAR_NUMBER)
            {
                mSSID = mSSID.substring(0, MAX_CHAR_NUMBER) + "...";
            }
            title += mSSID + "\"\n";
        }

        title += getString(R.string.select_location);

        final int[] icons = new int[]{LocationsTable.HOME_ICON, LocationsTable.WORK_ICON};
        final CharSequence[] names = new CharSequence[]{LocationsTable.HOME_NAME, LocationsTable.WORK_NAME, "Ask me again later"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item

                    	Analytics.reportGAEvent(getActivity(), Analytics.CATEGORY_UI_ACTION,
    						    				Analytics.ACTION_NEW_LOCATION_SELECTED, names[which].toString(), null);
    	
                    	Map<String, String> analParams = new HashMap<String, String>();
                        analParams.put("locationName", names[which].toString());
                        FlurryAgent.logEvent("New_Location_Selected", analParams);
                        
                    	// Hack!
                    	if (!names[which].toString().equals("Ask me again later"))
                    	{
                    		// Update the listener
                    		mListener.onLocationSelected(names[which].toString(), icons[which]);
                    	}
                    	
                    	mIsDisplayed = false;
                    }                    
                });
        return builder.create();
    }

	public Boolean isDisplayed() {
		return mIsDisplayed;
	}
	
}
