package com.dorid.android.rememberit.analytics;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

public class Analytics {

	// Categories
	public final static String CATEGORY_LOGIN = "login";
	public final static String CATEGORY_UI_ACTION = "ui_action";
	public final static String CATEGORY_NOTIFICATION = "notification";	
	
	// Actions
	public final static String ACTION_LOCATION_SELECTED = "location_selected";
	public final static String ACTION_NEW_LOCATION_SELECTED = "new_location_selected";	
	public final static String ACTION_LOCATION_CREATED = "location_created";
	public final static String ACTION_NEW_LOCATION_CREATED = "new_location_created";
	public final static String ACTION_LOCATION_OVERWRITED = "location_overwrited";
	public final static String ACTION_LOCATION_OVERWRITE_CANCEL = "location_overwrite_cancel";	
		
	public final static String ACTION_ADD_REMINDER = "add_reminder";
	public final static String ACTION_EDIT_REMINDER = "edit_reminder";	
	
	public final static String ACTION_REMINDER_LONG_CLICKED = "reminder_long_clicked";	
	public final static String ACTION_REMINDER_DELETED = "reminder_deleted";	
	
	public final static String ACTION_REMINDER_ACTIVATED = "reminder_activated";
	
	public final static String ACTION_NEW_REMINDER_CREATED = "reminder_created";
	public final static String REMINDER_CHANGED = "reminder_changed";
	
	public final static String ACTION_NEW_NOTIFICATION = "new_notification";
	
	public static void reportGAEvent(Context context, String category, String action, String label, Long value)
	{
		  // May return null if a EasyTracker has not yet been initialized with a
		  // property ID.
		  EasyTracker easyTracker = EasyTracker.getInstance(context);		  
		  
		  // MapBuilder.createEvent().build() returns a Map of event fields and values
		  // that are set and sent with the hit.
		  easyTracker.send(MapBuilder
		      .createEvent(category,     // Event category (required)
		                   action,  // Event action (required)
		                   label,   // Event label
		                   value)            // Event value
		      .build()
		  );
	}
	
}
