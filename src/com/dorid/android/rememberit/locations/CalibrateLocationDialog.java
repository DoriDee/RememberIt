package com.dorid.android.rememberit.locations;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.dorid.android.rememberit.reminders.RemindersService;

public class CalibrateLocationDialog extends DialogFragment {

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Improve Accuracy")
        	   .setMessage("To improve leaving notification walk outside the selected location")
               .setPositiveButton("I'm Out!", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       //
                	   WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                       WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                       int speed = wifiInfo.getRssi();
                       
                       SharedPreferences settings = getActivity().getSharedPreferences(RemindersService.WIFI_INFO, Context.MODE_PRIVATE);                                              
           	           settings.edit().putBoolean(RemindersService.PENDING_SCAN_RESULTS_TO_STORE, true).commit();
                       
                       wifiManager.startScan(); 
                   }
               })
               .setNeutralButton("Maybe Later", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                	   
                   }
               })        
               .setNegativeButton("Don't show again", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
