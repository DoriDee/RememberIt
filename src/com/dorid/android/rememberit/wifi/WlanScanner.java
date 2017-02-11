package com.dorid.android.rememberit.wifi;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

import com.dorid.android.rememberit.reminders.RemindersService;

public class WlanScanner extends BroadcastReceiver {
	 
    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
 
    private static WifiLock wifiLock;
    private static WakeLock wakeLock;
 
    private static final int SIGNAL_THRESHOLD = 60;

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

    public static void lock() {
        try {
            wakeLock.acquire();
            wifiLock.acquire();
        } catch(Exception e) {
            Log.e("WlanSilencer", "Error getting Lock: "+e.getMessage());
        }
    }
 
    public static void unlock() {
    	
        if(wakeLock != null && wakeLock.isHeld())
            wakeLock.release();
        if(wifiLock != null && wifiLock.isHeld())
            wifiLock.release();
    }
 
    public WlanScanner() 
    {    	
    } // called by the AlarmManager
 
    private void createLocks(Context context)
    {
    	if (wifiLock == null)
    	{
    		wifiLock = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY , "WlanSilencerScanLock");	
    	}
    	
    	if (wakeLock == null)
    	{
            wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE))
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WlanSilencerWakeLock");	
    	}
    }
    
    // this constructor is invoked by WlanLockService (see next code snippet)
    public WlanScanner(Context context, int timeoutInSeconds){
 
        // initialise the locks
        createLocks(context);
        
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
 
        // use this class as the receiver
        Intent intent = new Intent(context, WlanScanner.class);
        
        // create a PendingIntent that can be passed to the AlarmManager        
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
 
        // create a repeating alarm, that goes of every x seconds
        // AlarmManager.ELAPSED_REALTIME_WAKEUP = wakes up the cpu only
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), timeoutInSeconds*1000, pendingIntent);
        //Calendar time = Calendar.getInstance();
        //time.setTimeInMillis(System.currentTimeMillis());
        // Set Alarm for next 10 seconds
        //time.add(Calendar.SECOND, 10);
        //alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),  pendingIntent);
    }
 
    // stop the repeating alarm
    public void stop() {
        alarmMgr.cancel(pendingIntent);
    }
 
    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.v("WlanSilencer", "Alarm received");
     
        createLocks(context);
        
     // use this class as the receiver
//        Intent intent = new Intent(context, WlanScanner.class);        
//        // create a PendingIntent that can be passed to the AlarmManager        
//        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//
//        alarmMgr.cancel(pendingIntent);
        
        WifiManager connManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(connManager.isWifiEnabled()) {
            lock(); // lock wakeLock and wifiLock, then scan.
            // unlock() is then called at the end of the onReceive function of WifiStateReciever
            
            WifiInfo wifiInfo = connManager.getConnectionInfo();

            int signalStrength = getWifiSignalStrength(wifiInfo.getRssi());
                 
            if (wifiInfo.getBSSID() != null && signalStrength < SIGNAL_THRESHOLD)
            {
            	Intent i = new Intent(context, RemindersService.class);
                i.setAction(RemindersService.ACTION_WIFI_STATE_CHANGED);
                i.putExtra("BSSID", wifiInfo.getBSSID());
                i.putExtra("signalLevel", signalStrength);
                context.startService(i);                            	
            }
            else
            {
            	unlock();
            }            
        }
    } 
}
