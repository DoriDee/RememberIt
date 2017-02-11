package com.dorid.android.rememberit.locations;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectWifiDialog extends DialogFragment{

	    private final int MAX_CHAR_NUMBER = 15;

	    public interface SelectWifiListener {
	        public void onWifiSelected(String wifiName);
	    }

	    //private List<LocationData> mLocations;
	    private SelectWifiListener mListener;
	    private String mSSID;
	    private Boolean mIsDisplayed;
	    
	    public SelectWifiDialog()
	    {
	    	
	    }
	    
	    public SelectWifiDialog(SelectWifiListener listener)
	    {
	        this.mListener = listener;	        
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

	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

	    	mIsDisplayed = true;
	    	 
	    	WifiManager wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
	    	
	    	List<WifiConfiguration> wifis = wifiManager.getConfiguredNetworks();

	        List<String> wifiNames = new ArrayList<String>();
	    
	    	for (WifiConfiguration wifi : wifis) {
	    		if (wifi.SSID != null) {
//	    			Log.d("com.dorid.rememberit", wifi.SSID);
	    			wifiNames.add(wifi.SSID);
	    		}            	    
	    	}     

	    	final CharSequence[] names = wifiNames.toArray(new CharSequence[wifiNames.size()]);

	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Select one of the wifi spots")
	                .setItems(names, new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        // The 'which' argument contains the index position
	                        // of the selected item
	                    	mListener.onWifiSelected(names[which].toString());	                    
	                    }                    
	                });
	        
	        return builder.create();
	    }

		public Boolean isDisplayed() {
			return mIsDisplayed;
		}	
}
