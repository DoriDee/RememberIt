package com.dorid.android.rememberit.locations;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dorid.android.rememberit.R;

public class EditLocationFragment extends DialogFragment{
	
//	String mSSID;
	LocationData mLocationData;
	LocationCreatedListener mLocationCreatedListener;
	
	public interface LocationCreatedListener {
		public void onLocationCreated(LocationData location);
	}
	
	public EditLocationFragment(LocationData locationData, LocationCreatedListener locationCreatedListener)
    {
        mLocationData = locationData;
        mLocationCreatedListener = locationCreatedListener;
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

    	 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	    // Get the layout inflater
    	    LayoutInflater inflater = getActivity().getLayoutInflater();

    	    View view = inflater.inflate(R.layout.edit_location, null);
    	    TextView wifiName = (TextView)view.findViewById(R.id.wifiName);
    	    wifiName.setText("Wifi Name: " + mLocationData.getWifiName());
    	    
    	    final EditText locName = (EditText)view.findViewById(R.id.locationName);    	   	
    	    
    	    // Inflate and set the layout for the dialog
    	    // Pass null as the parent view because its going in the dialog layout
    	    builder.setView(view)
    	    // Add action buttons
    	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	               @Override
    	               public void onClick(DialogInterface dialog, int id) {
    	            	   	    	
    	            	   	mLocationData.setIconId(R.drawable.ic_action_wifi);
    	            	   	mLocationData.setLocationName(locName.getText().toString());
    	            	   	mLocationCreatedListener.onLocationCreated(mLocationData);
    	               }
    	           })
    	           .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	               public void onClick(DialogInterface dialog, int id) {
    	                   
    	               }
    	           });      
    	    return builder.create();
    }
}
