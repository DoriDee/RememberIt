package com.dorid.android.rememberit.reminders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.analytics.Analytics;
import com.dorid.android.rememberit.contentprovider.RemindersContentProvider;
import com.dorid.android.rememberit.database.RemindersTable;
import com.dorid.android.rememberit.locations.LocationData;
import com.dorid.android.rememberit.locations.LocationsDataSource;
import com.dorid.android.rememberit.wifi.WifiData;
import com.dorid.android.rememberit.wifi.WlanScanner;
import com.flurry.android.FlurryAgent;


/**
 * Created by Dori on 7/16/13.
 */
public class RemindersService extends IntentService
{
    //private Handler handler;

	private static final long  MIN_TIME_BETWEEN_REMINDERS = 1000 * 60 * 5; // 5 minutes
	
    public static final String WIFI_INFO = "wifiInfo";
    public static final String LAST_BSSID = "lastBssid";
    public static final String LAST_NO_WIFI = "lastNoWifi";
    public static final String LAST_DEPARTURE_TIME = "lastDepartureTime";
    public static final String PENDING_SCAN_RESULTS_TO_STORE = "pendingScanResultsToStore";
    public static final String PENDING_SCAN_RESULTS_TO_COMPARE = "pendingScanResultsToCompare";

    public static final String NOTIFICATIONS_PREFS = "notificationsPrefs";
    
    public static final String LAST_NOTIFICATION_LOCATION_ID = "lastNotificationLocationId";
    public static final String LAST_NOTIFICATION_CONDITION = "lastNotificationCondition";
    public static final String NOTIFICATIONS_NUMBER = "notificationsNumber";
    
    public static final String ACTION_WIFI_STATE_CHANGED = "com.dorid.android.rememberit.WIFI_STATE_CHANGED";
    
    public static final String ACTION_STORE_WIFI_SURROUNDING = "com.dorid.android.rememberit.STORE_WIFI_SURROUNDING";
    
    public static final String ACTION_COMPARE_WIFI_SURROUNDING = "com.dorid.android.rememberit.COMPARE_WIFI_SURROUNDING";

    
    private static final int LOW_SIGNAL_THRESHOLD = 20;
    
    private static final String ACTION_SNOOZE = "com.dorid.android.rememberit.reminders.ACTION_SNOOZE";
    private static final String ACTION_DISMISS = "com.dorid.android.rememberit.reminders.ACTION_DISMISS";
    //private GoogleAnalytics mGaInstance;
    
    private Handler mHandler;
    private DisplayToast mDisplayToast;
    private WlanScanner scanner;
    
    public RemindersService()
    {
        super("RemindersService");
        mHandler = new Handler();
        //mDisplayToast = new DisplayToast(this, text)
    }
    
//    @Override
//    public void onCreate()
//    {
//    	mGaInstance = GoogleAnalytics.getInstance(this);
//
//        mGaInstance.getLogger().setLogLevel(LogLevel.VERBOSE);
//
//        // Use the GoogleAnalytics singleton to get a Tracker.
//        mGaInstance.getTracker("UA-44971869-1"); // Placeholder tracking ID.
//    }
    
    private class DisplayToast implements Runnable{
    	  String mText;
    	  Context mContext;
    	  
    	  public DisplayToast(Context context, String text){
    	    mText = text;
    	    mContext = context;
    	  }

    	  public void run(){
    	     Toast.makeText(mContext, mText, Toast.LENGTH_LONG).show();
    	  }
    	}
    
    @Override
    protected void onHandleIntent(Intent intent) {

    	if (intent.getAction().equals(ACTION_STORE_WIFI_SURROUNDING))
    	{
            String currBssid = intent.getStringExtra("BSSID");
            String wifiSurroundings = intent.getStringExtra("WIFIS");

            LocationsDataSource locationsDataSource = new LocationsDataSource(this);
            locationsDataSource.open();

            ArrayList<LocationData> locations =  (ArrayList<LocationData>)locationsDataSource.getAllLocations();

            if (currBssid != null)
            {
            	LocationData location = LocationData.findLocationByBssid(locations, currBssid);

            	if (location != null)
            	{
            		locationsDataSource.updateLocationSurroundings(location.getLocationId(), wifiSurroundings);
            	}
            }            
            
            locationsDataSource.close();            
    	}
    	else if (intent.getAction().equals(ACTION_COMPARE_WIFI_SURROUNDING))
    	{
    		String currBssid = intent.getStringExtra("BSSID");
            String wifiSurroundings = intent.getStringExtra("WIFIS");

            LocationsDataSource locationsDataSource = new LocationsDataSource(this);
            locationsDataSource.open();

            ArrayList<LocationData> locations =  (ArrayList<LocationData>)locationsDataSource.getAllLocations();

            locationsDataSource.close();
            
            if (currBssid != null)
            {
            	LocationData location = LocationData.findLocationByBssid(locations, currBssid);

            	if (location != null)
            	{
            		compareWifisSurroundings(location, wifiSurroundings);            		
            	}
            }                                    
            
            WlanScanner.unlock();
    	}
    	else if (intent.getAction().equals(ACTION_WIFI_STATE_CHANGED))
        {
            // Read locations from db
            //
            LocationsDataSource locationsDataSource = new LocationsDataSource(this);
            locationsDataSource.open();

            ArrayList<LocationData> locations =  (ArrayList<LocationData>)locationsDataSource.getAllLocations();

            locationsDataSource.close();

            // We save the last bssid on the shared preferences
            SharedPreferences settings = getSharedPreferences(WIFI_INFO, Context.MODE_PRIVATE);

            String lastBssid = settings.getString(LAST_BSSID, null);

            String currBssid = intent.getStringExtra("BSSID");

            int signalStrength = intent.getIntExtra("signalLevel", 100);
            
            Boolean lowSignal = signalStrength < LOW_SIGNAL_THRESHOLD; 
                                    
            //Log.i("wifi", "CurrBssid=" + currBssid + " lastBssid=" + lastBssid);
            //notifyDebug(this, "CurrBssid=" + currBssid + " lastBssid=" + lastBssid);
            
            // If we had a last bssid and now we don't it means it is a departure
            // Or if we have a low signal
            if ((lastBssid != null && currBssid == null)  || (lowSignal && lastBssid != null && currBssid.equals(lastBssid)))
            {
            	//Log.i("wifi", "Handle departure!");
            
            	if (lowSignal)
                {
                	//Toast.makeText(this, "LOW SIGNAL!", Toast.LENGTH_LONG).show();
                	//notifyDebug(this, "weak signal!");
                }
            	else
            	{
            		//notifyDebug(this, "lost signal!");
            	}
            	            	
                handleDeparture(lastBssid, locations);
            }
            else if (currBssid != null && lastBssid == null)
            {
            	//Log.i("wifi", "Handle arrival!");       
            	//notifyDebug(this, "Handle Arrival!!");
            	
                handleArrival(currBssid, locations);
            }
            else if (lastBssid != null && currBssid != null && !lastBssid.equals(currBssid))
            {
            	//Log.i("wifi", "Handle arrival and departure!");
            	
            	handleDeparture(lastBssid, locations);
            	handleArrival(currBssid, locations);
            }
            // If the signal strength is lower than 50 and we are in a location of interest, we will start the scan
            else if (currBssid != null) 
            {            	            	
                LocationData currLocation = LocationData.findLocationByBssid(locations, currBssid);

                if (currLocation != null && currLocation.getWifisSurroundings() != null)
                {
                	Map<String, Integer> storedWifiData = WifiData.parseStringToWifisData(currLocation.getWifisSurroundings());
                	
                	Integer storedSignal = storedWifiData.get(currBssid);                	
                	
                	if (storedSignal != null && Math.abs(signalStrength - storedSignal) <= 15)
                	{
                		boolean activeReminders = hasActiveReminders(currLocation);
                		
                        if (activeReminders)
                        {
	                		//notifyDebug(this, "low enough begin scan");
	                    	
//	                    	WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);                                   
//	                    	settings.edit().putBoolean(PENDING_SCAN_RESULTS_TO_COMPARE, true).commit();                                        
//	                        wifiManager.startScan();
                        }
                	}                	 
                }
            }

            // Update the current bssid to be the last one
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(RemindersService.LAST_BSSID, currBssid);
            editor.commit();
        }
        else if (intent.getAction().equals(ACTION_SNOOZE))
        {

        }
        else if (intent.getAction().equals(ACTION_DISMISS))
        {

        }
    }

	private boolean hasActiveReminders(LocationData currLocation) {
		// Read all the reminders which defined upon arrival with this bssid
		RemindersDataSource remindersDataSource = new RemindersDataSource(this);

		remindersDataSource.open();

		List<ReminderData> reminders = remindersDataSource.getReminders(RemindersTable.Condition.Departure, currLocation.getLocationId());

		remindersDataSource.close();

		boolean activeReminders = false; 
		for (ReminderData reminder : reminders)
		{
		    if (reminder.isIsActivated())
		    {
		    	activeReminders = true;
		    }
		}
		return activeReminders;
	}

    private void compareWifisSurroundings(LocationData locationData, String currentWifiSurroundings)
    {
    	// Compare between the wifis and magically understand if the user has left his location
    	String locWifiSurroundings = locationData.getWifisSurroundings();
    	
    	Map<String, Integer> storedWifiData = WifiData.parseStringToWifisData(locWifiSurroundings);
    	Map<String, Integer> currWifiData = WifiData.parseStringToWifisData(currentWifiSurroundings);
    	
    	boolean locatedOutside = true;
    	
    	String wifiStringLog = "";
   	    	
    	int inRangeCounter = 0;
    	int totalCompared = 0;
    	
		for (String bssid : storedWifiData.keySet())
    	{
			// Disregard the current bssid - we already know it's ok
			if (!bssid.equals(locationData.getBssid()))
			{
        		// Find the wifi bssid in the current data
        		Integer signalLevel = currWifiData.get(bssid);
        		
        		if (signalLevel != null)
        		{
        			//Log.i("WIFI", "bssid=" + bssid + " recorderLevel=" + storedWifiData.get(bssid) + " currLevel=" + signalLevel);
        			wifiStringLog += bssid + ":   recLevel=" + storedWifiData.get(bssid) + " currLevel=" + signalLevel + "\n";
        
        			totalCompared++;
        			
    	    		// Check if the signal level is in the range of the recorded one
    	    		if (Math.abs(storedWifiData.get(bssid) - signalLevel) <= 15)
    	    		{
    	    			inRangeCounter++;
    	    		}
    	    		else
    				{
//    	    			locatedOutside = false;
//    	    			notifyDebug(this, "signal is off! curr : " + signalLevel + " rec : " + storedWifiData.get(bssid));
    				}
        		}
			}
    	}	    	    	    	    	    	
    	//mHandler.post(new DisplayToast(this, wifiStringLog));
    	
    	makeSomeToast(wifiStringLog);
    	    	
    	Integer storedSignal = storedWifiData.get(locationData.getBssid());                	
    	Integer currSignal = currWifiData.get(locationData.getBssid());
    	
    	//notifyDebug(this, "inRange : " + inRangeCounter + " total : " + totalCompared + " currSig=" + currSignal + " storedSig=" + storedSignal);
    	
    	// If this is close enough we will treat it as a departure
    	if (inRangeCounter == totalCompared)        	
    	{
    		handleDeparture(locationData);
    	}    	
    }
    
    private void handleArrival(String enteringBssid, ArrayList<LocationData> locations)
    {
        LocationData location = LocationData.findLocationByBssid(locations, enteringBssid);

        SharedPreferences settings = getSharedPreferences(WIFI_INFO, Context.MODE_PRIVATE);
        
        Long lastReminderTime = settings.getLong(RemindersService.LAST_DEPARTURE_TIME, 0);
                
        // Make sure that the time difference between reminders is not too much
        if (System.currentTimeMillis() - lastReminderTime > MIN_TIME_BETWEEN_REMINDERS &&
        	location != null)
        {
            // Read all the reminders which defined upon arrival with this bssid
            RemindersDataSource remindersDataSource = new RemindersDataSource(this);

            remindersDataSource.open();

            List<ReminderData> reminders = remindersDataSource.getReminders(RemindersTable.Condition.Arrival, location.getLocationId());

            remindersDataSource.close();

            for (ReminderData reminder : reminders)
            {
                sendNotification(this, reminder, location);
//                makeSomeToast(reminder.getReminderTxt());
            }
        }
    }

    private void handleDeparture(LocationData location)
    {
        if (location != null)
        {
            // Read all the reminders which defined upon arrival with this bssid
            RemindersDataSource remindersDataSource = new RemindersDataSource(this);

            remindersDataSource.open();

            List<ReminderData> reminders = remindersDataSource.getReminders(RemindersTable.Condition.Departure, location.getLocationId());

            remindersDataSource.close();

            for (ReminderData reminder : reminders)
            {
                sendNotification(this, reminder, location);
            }
            
            // Update last departure time
            SharedPreferences settings = getSharedPreferences(WIFI_INFO, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putLong(RemindersService.LAST_DEPARTURE_TIME, System.currentTimeMillis());
            editor.commit();
        }    	
    }
    
    private void handleDeparture(String leavingBssid, ArrayList<LocationData> locations)
    {
        // Read all the reminders which defined upon departure with this bssid
        LocationData location = LocationData.findLocationByBssid(locations, leavingBssid);
        handleDeparture(location);
    }

    private void deactivateReminder(int reminderId)
    {
        ContentValues values = new ContentValues();
        values.put(RemindersTable.COLUMN_ACTIVATED, false);

        // Update reminder
        Uri uri = Uri.parse(RemindersContentProvider.CONTENT_URI + "/" + reminderId);
        getContentResolver().update(uri, values, null, null);
    }


    private void makeSomeToast(final String msg)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                // Display toast and exit
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();


            }
        });
    }

    int debugNotifCounter = 1;
    
    private void notifyDebug(Context context, String debug)
    {    	    
    	// Constructs the Builder object.
	    NotificationCompat.Builder builder=  new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_notify)
	                                                                                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission                        
	    builder.setContentTitle("Debug");
	    	    
        builder.setAutoCancel(true);

        builder.setContentText(debug);
        
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(context, RemindersActivity.class);
        
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	    
        // Adds the back stack
	    stackBuilder.addParentStack(RemindersActivity.class);
	    
	    // Adds the Intent to the top of the stack
	    stackBuilder.addNextIntent(resultIntent);
	     
	    // Gets a PendingIntent containing the entire back stack
	    PendingIntent notifyIntent =
	             stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);	     

        builder.setContentIntent(notifyIntent);

        // Including the notification ID allows you to update the notification later on.        
        notificationManager.notify(debugNotifCounter++, builder.build());        	                                    	
    }
    
    private void sendNotification(Context context, ReminderData reminderData, LocationData location)
    {
        // If the reminder is not recurring - deactivate it (DB-wise) so it doesn't pop in similar cases in the future
        if (!reminderData.isRecurring())
        {
            deactivateReminder(reminderData.getReminderId());
        }        
        
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        SharedPreferences notificationsPrefs = getSharedPreferences(RemindersService.NOTIFICATIONS_PREFS, Context.MODE_PRIVATE);
                
        int lastNotificationLocationId = notificationsPrefs.getInt(RemindersService.LAST_NOTIFICATION_LOCATION_ID, -1);
        String lastNotificationCondition = notificationsPrefs.getString(RemindersService.LAST_NOTIFICATION_CONDITION, null);
        int notificationsNumber = notificationsPrefs.getInt(RemindersService.NOTIFICATIONS_NUMBER, 0);
        
        Boolean appendNotification = false;

        // If the last notification is from the same location and condition we will append this notification to it
        // otherwise we will erase all previous notifications(since they are no longer relevant)
        if (lastNotificationLocationId == location.getLocationId() && 
        	lastNotificationCondition != null && lastNotificationCondition.equals(reminderData.getCondition().toString()))
        {
        	notificationsNumber++;
        	appendNotification = true;
        }
        else
        {
        	notificationsNumber = 1;
        	notificationManager.cancelAll();
        }

        
	    // Sets up the Snooze and Dismiss action buttons that will appear in the
	    // expanded view of the notification.
	/*        Intent dismissIntent = new Intent(this, RemindersService.class);
	        dismissIntent.setAction(ACTION_DISMISS);
	        PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);
	
	        Intent snoozeIntent = new Intent(this, RemindersService.class);
	        snoozeIntent.setAction(ACTION_SNOOZE);
	        PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);
	*/
	    // Constructs the Builder object.
	    NotificationCompat.Builder builder=  new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_stat_notify)
	                                                                                .setDefaults(Notification.DEFAULT_ALL); // requires VIBRATE permission
                /*
                 * Sets the big view "big text" style and supplies the
                 * text (the user's reminder message) that will be displayed
                 * in the detail area of the expanded notification.
                 * These calls are ignored by the support library for
                 * pre-4.1 devices.
                 */
                /*        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg)); */
  /*                      .addAction(R.drawable.ic_launcher,
                                "Done", piDismiss)
                        .addAction(R.drawable.ic_launcher,
                                "Snooze", piSnooze); */
	    
	    Analytics.reportGAEvent(context, Analytics.CATEGORY_NOTIFICATION,
	    						Analytics.ACTION_NEW_NOTIFICATION,
	    						location.getLocationName() + "_" + reminderData.getCondition().toString(), Long.valueOf(notificationsNumber));

	    Map<String, String> analParams = new HashMap<String, String>();
        analParams.put("locationName", location.getLocationName());
        analParams.put("condition", reminderData.getCondition().toString());
        analParams.put("notificationsNumber", String.valueOf(notificationsNumber));
        FlurryAgent.logEvent("New_Notification", analParams);      
    	        
	    builder.setContentTitle(reminderData.getReminderTxt());
	    
	    // If this is a new reminder notification we will just show the name in the notification bar
//	    if (!appendNotification)
//	    {
//	    	Analytics.reportGAEvent(context, Analytics.CATEGORY_NOTIFICATION,
//				    			    Analytics.ACTION_NEW_NOTIFICATION,
//				    			    location.getLocationName() + "_" + reminderData.getCondition().toString(), null);
//
//			builder.setContentTitle(reminderData.getReminderTxt());
//	    }
//	    else
//	    {
//	    	// If this is a notification to append we will add it
//	    	builder.setContentTitle(getString(R.string.app_name));	    	
//	    	builder.setContentText("Remmber to!");
//	    	//					    reminderData.getReminderTxt());
//	    						//getString(R.string.new_reminders));
//	    	builder.setNumber(2);
//	    	
//        	NotificationCompat.InboxStyle inboxStyle =  new NotificationCompat.InboxStyle();
//        	
//        	// Sets a title for the Inbox style big view
//        	inboxStyle.setBigContentTitle("Event tracker details:");
//        	inboxStyle.addLine("blablbabaa");
//        	inboxStyle.addLine(reminderData.getReminderTxt());
//        	inboxStyle.setBigContentTitle("");
//        	inboxStyle.setSummaryText("Summary");
//        	
//        	// Moves the big view style object into the notification object.	
//        	builder.setStyle(inboxStyle);	        
//	    }	    
	    
        /*
         * Clicking the notification itself displays ResultActivity, which provides
         * UI for snoozing or dismissing the notification.
         * This is available through either the normal view or big view.
         */
        Intent resultIntent = new Intent(context, RemindersActivity.class);

        resultIntent.putExtra("location", location);
        
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	    
        // Adds the back stack
	    stackBuilder.addParentStack(RemindersActivity.class);
	    
	    // Adds the Intent to the top of the stack
	    stackBuilder.addNextIntent(resultIntent);
	     
	    // Gets a PendingIntent containing the entire back stack
	    PendingIntent notifyIntent =
	             stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);	     
                
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//        {
//        	resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        }
//        else
//        {
//        	resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);            
//        }
        
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
//        PendingIntent notifyIntent =
//                PendingIntent.getActivity(
//                        context,
//                        0,
//                        resultIntent,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );

        builder.setContentIntent(notifyIntent);
        builder.setAutoCancel(true);
        
        // Including the notification ID allows you to update the notification later on.        
        notificationManager.notify(reminderData.getReminderId(), builder.build());        	                
        
        SharedPreferences.Editor editor = notificationsPrefs.edit();
        
        editor.putInt(RemindersService.LAST_NOTIFICATION_LOCATION_ID, location.getLocationId());
        editor.putInt(RemindersService.NOTIFICATIONS_NUMBER, notificationsNumber);
        editor.putString(RemindersService.LAST_NOTIFICATION_CONDITION, reminderData.getCondition().toString());
        editor.commit();
    }
}
