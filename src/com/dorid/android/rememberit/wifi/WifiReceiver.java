package com.dorid.android.rememberit.wifi;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.dorid.android.rememberit.reminders.RemindersService;

/**
 * Created by Dori on 6/14/13.
 */
public class WifiReceiver extends BroadcastReceiver
{
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    
    private WifiInfo lastConnectionInfo;
    
    private static final int SIGNAL_THRESHOLD = 20;//60;
    private static final int MAX_WIFIS_TO_STORE = 5;
    
    public double calculateDistance(double signalLevelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) - signalLevelInDb) / 20.0;
        return Math.pow(10.0, exp);
    }
    
    public int getWifiSignalStrength(int rssi){
        int MIN_RSSI        = -100;
        int MAX_RSSI        = -55;  
        int levels          = 101;
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            return WifiManager.calculateSignalLevel(rssi, levels);
        } else {             
            // this is the code since 4.0.1
            if (rssi <= MIN_RSSI) {
                return 0;
            } else if (rssi >= MAX_RSSI) {
                return levels - 1;
            } else {
                float inputRange = (MAX_RSSI - MIN_RSSI);
                float outputRange = (levels - 1);
                return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
            }
        }
    }//end method
    
    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION))
        {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
            String wifiState = null;
            switch (state) {
                case WifiManager.WIFI_STATE_DISABLED:
                    wifiState = "it is disabled";
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    wifiState = "it is enabled";
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    wifiState = "it is switching off";
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    wifiState = "wifi is getting enabled";
                    break;
                default:
                    wifiState = "wifi state : " + state;
                    break;
            }
            //Log.e("wifi", "state = " + wifiState + " action=" + action);
          //  Toast.makeText(context, wifiState, Toast.LENGTH_LONG).show();
        }

       // Log.e("wifi", "Action " + action);

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        int speed = wifiInfo.getRssi();
    	
        if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION))
        {        	
        	int signalStrength = getWifiSignalStrength(speed);
        
        //	Log.i("RSSI CHANGED!", "speed = " + signalStrength);
        //	Toast.makeText(context, "signalStrength=" + signalStrength, Toast.LENGTH_LONG).show();
        	
        	if (signalStrength < SIGNAL_THRESHOLD)
        	{
        		Intent i = new Intent(context, RemindersService.class);
                i.setAction(RemindersService.ACTION_WIFI_STATE_CHANGED);
                i.putExtra("BSSID", wifiInfo.getBSSID());
                i.putExtra("signalLevel", getWifiSignalStrength(speed));
                context.startService(i);
        	}            
        }
        
        if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) 
        {
            SharedPreferences settings = context.getSharedPreferences(RemindersService.WIFI_INFO, Context.MODE_PRIVATE);
            
            boolean pendingScanResultsToStore = settings.getBoolean(RemindersService.PENDING_SCAN_RESULTS_TO_STORE, false);
            boolean pendingScanResultsToCompare = settings.getBoolean(RemindersService.PENDING_SCAN_RESULTS_TO_COMPARE, false);
            if (pendingScanResultsToStore || pendingScanResultsToCompare)
            {            
//	        	Log.e("DEBUG","SCAN_RESULTS_AVAILABLE_ACTION");
	        	List<ScanResult> li = wifiManager.getScanResults();
	        	String wifis = parseScanResults(context, li);

        		Intent i = new Intent(context, RemindersService.class);
                i.putExtra("BSSID", wifiInfo.getBSSID());
                i.putExtra("WIFIS", wifis);

	        	if (pendingScanResultsToStore)
	        	{	        			        		
	                i.setAction(RemindersService.ACTION_STORE_WIFI_SURROUNDING);
	        	}
	        	else
	        	{
	                i.setAction(RemindersService.ACTION_COMPARE_WIFI_SURROUNDING);
	        	}

                context.startService(i);

	        	settings.edit().putBoolean(RemindersService.PENDING_SCAN_RESULTS_TO_STORE, false).commit();
	        	settings.edit().putBoolean(RemindersService.PENDING_SCAN_RESULTS_TO_COMPARE, false).commit();
            }
        }

        
/*
        if (action.equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {

            SupplicantState supState = wifiInfo.getSupplicantState();

            Log.e("wifi", "SUPPLICANT_STATE : " +  supState);

            String bssid = wifiInfo.getBSSID();

            Intent i = new Intent(context, RemindersService.class);
            i.setAction(RemindersService.ACTION_WIFI_STATE_CHANGED);
            i.putExtra("BSSID", wifiInfo.getBSSID());
            context.startService(i);

        }
*/

        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){

            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            String strNetInfo = String.valueOf(netInfo);

            NetworkInfo.DetailedState detailState = netInfo.getDetailedState();

            //Log.i("wifi", "Wifi info " + detailState + " connected? " + netInfo.isConnected() );
            
            if (netInfo.isConnected())
            {
            	//Log.i("wifi", "Wifi is connected!");
                String bssid = intent.getStringExtra(WifiManager.EXTRA_BSSID);

                Intent i = new Intent(context, RemindersService.class);
                i.setAction(RemindersService.ACTION_WIFI_STATE_CHANGED);
                i.putExtra("BSSID", bssid);
                context.startService(i);
            }
            else if (netInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {

            	//Log.i("wifi", "Wifi is disconnected!");
                Intent i = new Intent(context, RemindersService.class);
                i.setAction(RemindersService.ACTION_WIFI_STATE_CHANGED);
                //i.put("BSSID", null);
                context.startService(i);
            }
            else
            {
            	
            }          
        }
        /*
        else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION))
        {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && ! networkInfo.isConnected()) {
                // Wifi is disconnected
                Intent i = new Intent(context, RemindersService.class);
                i.setAction(RemindersService.ACTION_WIFI_STATE_CHANGED);
                //i.put("BSSID", null);
                context.startService(i);
            }
        }*/

        if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {

            String msg = "SUPPLICANT_CONNECTION_CHANGE_ACTION connected:" + intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
            //Log.e("wifi", msg);
            //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

            SupplicantState supState = wifiInfo.getSupplicantState();
        }

    }

    private String parseScanResults(Context context, List<ScanResult> scanResults)
    {
    	// Sort by signal level
    	ScanResult[] arrResults = new ScanResult[scanResults.size()];
    	scanResults.toArray(arrResults);
    	
    	Arrays.sort(arrResults, new Comparator<ScanResult>() {
    	     @Override
    	     public int compare(ScanResult entry1, ScanResult entry2) {
    	    	 return Integer.valueOf(entry2.level).compareTo(Integer.valueOf(entry1.level));    	        
    	        }
    	     });
    	
    	String toastString = "";
    	
    	String wifis = "";    	
    	
    	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        int speed = wifiInfo.getRssi();

    	int signalStrength = getWifiSignalStrength(speed);

    	wifis += wifiInfo.getBSSID() + "_" + signalStrength + ";";
    	
    	for (int i=0; i< arrResults.length && i < MAX_WIFIS_TO_STORE; i++) 
    	{        		        		
    		//Log.e("WIFI","ssid: "+ arrResults[i].SSID+" bssid: "+ arrResults[i].BSSID + " level:" + arrResults[i].level);
    	
    		if (!arrResults[i].BSSID.equals(wifiInfo.getBSSID()))
    		{
	    		signalStrength = getWifiSignalStrength(arrResults[i].level);
	
	    		toastString += arrResults[i].SSID + " signal : " + signalStrength + "\n";
	    		
	    		wifis += arrResults[i].BSSID + "_" + signalStrength + ";";
    		}
    	}
    	
    	Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();

    	return wifis;    	
    }
    
//    private void makeSomeToast(Context context)
//    {
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        Log.d("wifiInfo", wifiInfo.toString());
//
//        String msg = "WiFi is disconnected";
//
//        if (wifiInfo.getBSSID() != null)
//        {
//            msg = "WiFi is connected=> " + wifiInfo.getBSSID();
//
//            if (wifiInfo.getSSID() != null)
//            {
//                msg += " SSID:" + wifiInfo.getSSID();
//            }
//        }
//
//        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
//        sendNotification2(context, msg);
//
//
//    }


//    private void sendNotification2(Context context, String msg)
//    {
//        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//    // Sets up the Snooze and Dismiss action buttons that will appear in the
//    // expanded view of the notification.
//       /* Intent dismissIntent = new Intent(this, PingService.class);
//        dismissIntent.setAction(CommonConstants.ACTION_DISMISS);
//        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);
//
//        Intent snoozeIntent = new Intent(this, PingService.class);
//        snoozeIntent.setAction(CommonConstants.ACTION_SNOOZE);
//        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);
//*/
//    // Constructs the Builder object.
//    builder =
//        new NotificationCompat.Builder(context)
//        .setSmallIcon(R.drawable.ic_stat_notification)
//    .setContentTitle("i-Remember")//getString(R.string.notification))
//    .setContentText(msg) //getString(R.string.ping))
//    .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission
//                /*
//                 * Sets the big view "big text" style and supplies the
//                 * text (the user's reminder message) that will be displayed
//                 * in the detail area of the expanded notification.
//                 * These calls are ignored by the support library for
//                 * pre-4.1 devices.
//                 */
//                /*        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(msg))
//                        .addAction (R.drawable.ic_stat_dismiss,
//                                getString(R.string.dismiss), piDismiss)
//                        .addAction (R.drawable.ic_stat_snooze,
//                                getString(R.string.snooze), piSnooze); */
//
//    /*
//     * Clicking the notification itself displays ResultActivity, which provides
//     * UI for snoozing or dismissing the notification.
//     * This is available through either the normal view or big view.
//     */
//    Intent resultIntent = new Intent(context, ResultActivity.class);
//    resultIntent.putExtra(CommonConstants.EXTRA_MESSAGE, msg);
//    resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//
//    // Because clicking the notification opens a new ("special") activity, there's
//    // no need to create an artificial back stack.
//    PendingIntent resultPendingIntent =
//            PendingIntent.getActivity(
//                    context,
//                    0,
//                    resultIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT
//            );
//
//        builder.setContentIntent(resultPendingIntent);
//
//        // Including the notification ID allows you to update the notification later on.
//        mNotificationManager.notify(CommonConstants.NOTIFICATION_ID, builder.build());
//    }
}
