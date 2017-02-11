package com.dorid.android.rememberit.reminders;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.view.Menu;
import com.dorid.android.rememberit.BackListener;
import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.analytics.Analytics;
import com.dorid.android.rememberit.contentprovider.RemindersContentProvider;
import com.dorid.android.rememberit.database.RemindersTable;
import com.dorid.android.rememberit.database.RemindersTable.Condition;
import com.dorid.android.rememberit.locations.CalibrateLocationDialog;
import com.dorid.android.rememberit.locations.LocationData;
import com.flurry.android.FlurryAgent;
//import com.tapreason.sdk.TapReason;
//import com.tapreason.sdk.TapReasonFragment;

/**
 * Created by Dori on 7/3/13.
 */
public class RemindersFragment extends SherlockListFragment implements ReminderUpdater,
                                                                       LoaderManager.LoaderCallbacks<Cursor>,
                                                                       BackListener                                                                       
{
//    private List<LocationData> mLocations;

    private LocationData mLocation;

    private RemindersCursorAdapter adapter;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private Boolean mKeyboardShown = false;
    private LinearLayout mRemindersLayout;
    private EditText mEditReminder;
    private Boolean toolbarOpened = false;

    private ReminderData mCurrReminder = null;

    private static int REQUEST_CODE_ADD = 0;
    private static int REQUEST_CODE_EDIT = 1;
        
    Uri reminderUri = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();        
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
    
    private void promptForCalibration() {    	
    	CalibrateLocationDialog calibrateDialog = new CalibrateLocationDialog();
    	calibrateDialog.show(getFragmentManager(), "CalibrateLocationDialog");    
    }
    
    public void onAddReminderClicked() {
    	    	        
        //promptForCalibration();
        
    	//Log.i("Wifi", "signal strength : " + speed);

        
    	//Toast.makeText(getSherlockActivity(), "signal strength : " + speed, Toast.LENGTH_LONG).show();
    	        
    	// Report add reminder (with the number of current reminders)
    	Analytics.reportGAEvent(getActivity(), 
    							Analytics.CATEGORY_UI_ACTION,
    						    Analytics.ACTION_ADD_REMINDER,
    						    mLocation.getLocationName(),
    						    Long.valueOf(adapter.getCount()));
    	
    	Map<String, String> analParams = new HashMap<String, String>();
        analParams.put("locationName", mLocation.getLocationName());
        analParams.put("remindersNum", String.valueOf(adapter.getCount()));
        FlurryAgent.logEvent("Add_Reminder", analParams);
      
		Intent remindersActivity = new Intent(getSherlockActivity(), EditReminderActivity.class);
        remindersActivity.putExtra("location", mLocation);
        startActivityForResult(remindersActivity, REQUEST_CODE_ADD); 
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
      
      if (data != null)
      {
	      if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_EDIT) {
	        
	    	  if (data.hasExtra("reminder"))
	    	  {	    		  
	    		  ReminderData reminder = data.getParcelableExtra("reminder");
	    		  
	    		  Analytics.reportGAEvent(getSherlockActivity(),
		  				  				  Analytics.CATEGORY_UI_ACTION,
		  				  				  Analytics.REMINDER_CHANGED,
		  				  				  mLocation.getLocationName() + "_" + reminder.getCondition().toString(),
		  				  				  Long.valueOf(adapter.getCount()));

	    		  Map<String, String> analParams = new HashMap<String, String>();
	    	        analParams.put("locationName", mLocation.getLocationName());
	    	        analParams.put("Condition", reminder.getCondition().toString());
	    	        analParams.put("remindersNum", String.valueOf(adapter.getCount()));
	    	      FlurryAgent.logEvent("Reminder_Changed", analParams);
	    	        
	    	        
	    		  updateReminder(reminder);
	    	  }
	      }
	      else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ADD) {
	    	  
	    	  if (data.hasExtra("reminder"))
	    	  {
	    		  ReminderData reminder = data.getParcelableExtra("reminder");

	    		  Analytics.reportGAEvent(getSherlockActivity(),
		  				  Analytics.CATEGORY_UI_ACTION,
		  				  Analytics.ACTION_NEW_REMINDER_CREATED,
						  mLocation.getLocationName() + "_" + reminder.getCondition().toString(),
						  Long.valueOf(adapter.getCount()));

	    		  addReminder(R.drawable.ic_launcher, reminder.getReminderTxt(), reminder.getLocationId(), 
	    				  	  reminder.getCondition(),
	                      true, reminder.isRecurring());
	    	  }    	  
	      }
      }
    } 
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRemindersLayout = (LinearLayout)inflater.inflate(R.layout.reminders_layout, null); //new RemindersLayout(getSherlockActivity(), null, this);

        //Switch switchA = (Switch) mRemindersLayout.findViewById(R.id.switch_a);

        mLocation = (LocationData)getArguments().get("location"); //(List<LocationData>)getArguments().get("locations");

        final ListView listView = (ListView) mRemindersLayout.findViewById(android.R.id.list);
                
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
	            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
	            	Cursor cursor = (Cursor) getListAdapter().getItem(position);
	            	ReminderData reminder = new ReminderData();
	            	reminder.parseFromCursor(cursor);    	
	            	reminderLongClicked(reminder);
	                return true;
	            }
	        });
        
        return mRemindersLayout;
    }

    @Override
    public void onListItemClick(ListView parent, View v, int position, long id) {    	    	    	
    	Cursor cursor = (Cursor) getListAdapter().getItem(position);
    	ReminderData reminder = new ReminderData();
    	reminder.parseFromCursor(cursor);    	
    	reminderClicked(reminder);
    }
  
    @Override
    public void onDestroyView()
    {
        if (mKeyboardShown)
            closeKeyboad();
       // mEditReminder.setText("");
        super.onDestroyView();
    }

    @Override
    public void reminderClicked(ReminderData reminderData) {
    	
    	Analytics.reportGAEvent(getActivity(),	Analytics.CATEGORY_UI_ACTION,
			    				Analytics.ACTION_EDIT_REMINDER,
			    				mLocation.getLocationName(),
			    				Long.valueOf(adapter.getCount()));

    	Map<String, String> analParams = new HashMap<String, String>();
        analParams.put("locationName", mLocation.getLocationName());        
        analParams.put("remindersNum", String.valueOf(adapter.getCount()));
        FlurryAgent.logEvent("Reminder_Clicked", analParams);      
    	
    	Intent remindersActivity = new Intent(getSherlockActivity(), EditReminderActivity.class);
        remindersActivity.putExtra("location", mLocation);
        remindersActivity.putExtra("reminder", reminderData);
        startActivityForResult(remindersActivity, REQUEST_CODE_EDIT);
    }

    @Override
    public void reminderLongClicked(ReminderData reminderData)
    {
        final ReminderData data = reminderData;

        Analytics.reportGAEvent(getActivity(),	Analytics.CATEGORY_UI_ACTION,
        						Analytics.ACTION_REMINDER_LONG_CLICKED,
        						mLocation.getLocationName(),
        						Long.valueOf(adapter.getCount()));
        
        Map<String, String> analParams = new HashMap<String, String>();
        analParams.put("locationName", mLocation.getLocationName());        
        analParams.put("remindersNum", String.valueOf(adapter.getCount()));
        FlurryAgent.logEvent("Reminder_Long_Clicked", analParams);      
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(reminderData.getReminderTxt())
                .setItems(new String[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        if (which == 0)
                        {
                            reminderClicked(data);
                        }
                        else
                        {
                            deleteReminder(data.getReminderId());
                        }
                    }
                });
        builder.show();
    }

    private void showKeyboard()
    {
        // Show the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager)getSherlockActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        boolean keyboardTest = inputMethodManager.showSoftInput(mEditReminder, InputMethodManager.SHOW_IMPLICIT);

        if (!keyboardTest)
        {
            mEditReminder.requestFocus();
            keyboardTest = inputMethodManager.showSoftInput(mEditReminder, InputMethodManager.SHOW_FORCED);
        }
        mKeyboardShown = true;
    }

    private void closeKeyboad() {
        // Hide the keyboard
        InputMethodManager inputMethodManager = (InputMethodManager)getSherlockActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getSherlockActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        mKeyboardShown = false;
    }


    public void onBackPressed() {
        if (mKeyboardShown)
        {
//            showList();
        }
        else
        {
            getSherlockActivity().onBackPressed();
        }
    }


    private void addReminder(int iconId, String reminderText, int locationId, Condition condition, boolean activated, boolean recurring) {

        ContentValues values = new ContentValues();
        values.put(RemindersTable.COLUMN_REMINDER_TEXT, reminderText);
        values.put(RemindersTable.COLUMN_ICON_ID, iconId);
        values.put(RemindersTable.COLUMN_LOCATION_ID, locationId);
        values.put(RemindersTable.COLUMN_CONDITION, condition.getText());
        values.put(RemindersTable.COLUMN_ACTIVATED, activated);
        values.put(RemindersTable.COLUMN_RECURRING, recurring);

        // New reminder
        reminderUri = getSherlockActivity().getContentResolver().insert(RemindersContentProvider.CONTENT_URI, values);
    }

    private void updateReminder(ReminderData reminder)
    {
    	updateReminder(reminder.getReminderId(), 0, reminder.getReminderTxt(), reminder.getLocationId(), reminder.getCondition(), reminder.isIsActivated(), reminder.isRecurring());
    }
    
    private void updateReminder(int reminderId, int iconId, String reminderText, int locationId, Condition condition, boolean activated, boolean recurring)
    {
        ContentValues values = new ContentValues();
        values.put(RemindersTable.COLUMN_REMINDER_TEXT, reminderText);
        values.put(RemindersTable.COLUMN_ICON_ID, iconId);
        values.put(RemindersTable.COLUMN_LOCATION_ID, locationId);
        values.put(RemindersTable.COLUMN_CONDITION, condition.getText());
        values.put(RemindersTable.COLUMN_ACTIVATED, activated);
        values.put(RemindersTable.COLUMN_RECURRING, recurring);

        // Update reminder
        Uri uri = Uri.parse(RemindersContentProvider.CONTENT_URI + "/" + reminderId);
        getSherlockActivity().getContentResolver().update(uri, values, null, null);
    }

    private void updateReminderText(int reminderId, String reminderText)
    {
        ContentValues values = new ContentValues();
        values.put(RemindersTable.COLUMN_REMINDER_TEXT, reminderText);

        // Update reminder
        Uri uri = Uri.parse(RemindersContentProvider.CONTENT_URI + "/" + reminderId);
        getSherlockActivity().getContentResolver().update(uri, values, null, null);
    }

    public void loadData() {

        getLoaderManager().initLoader(0, null, this);

        adapter = new RemindersCursorAdapter(getSherlockActivity(), null, R.layout.reminder_item, this);

        setListAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                IcsAdapterView.AdapterContextMenuInfo info = (IcsAdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(RemindersContentProvider.CONTENT_URI + "/"
                        + info.id);
                getSherlockActivity().getContentResolver().delete(uri, null, null);
                loadData();
                return true;
        }
        return super.onContextItemSelected(item);
    }


    // Creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { RemindersTable.COLUMN_ID,
                                RemindersTable.COLUMN_REMINDER_TEXT,
                                RemindersTable.COLUMN_RECURRING,
                                RemindersTable.COLUMN_ACTIVATED,
                                RemindersTable.COLUMN_CONDITION,
                                RemindersTable.COLUMN_LOCATION_ID };

        // Create the cursor for only this location
        CursorLoader cursorLoader = new CursorLoader(getSherlockActivity(), RemindersContentProvider.CONTENT_URI,
                                                     projection, RemindersTable.COLUMN_LOCATION_ID + "=" + mLocation.getLocationId(), null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        
        ListView ls = getListView();
        View guide = mRemindersLayout.findViewById(R.id.guide);

        if (data.getCount() == 0)
        {
        	ls.setVisibility(View.INVISIBLE);
        	guide.setVisibility(View.VISIBLE);
        	
        	AlphaAnimation aa = new AlphaAnimation(0f,1f);
            aa.setDuration(1000);            
        	guide.startAnimation(aa);        	
        }
        else
        {
        	ls.setVisibility(View.VISIBLE);
        	guide.setVisibility(View.GONE);        	
        }        
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }

    @Override
    public void deleteReminder(int reminderId) {

    	Analytics.reportGAEvent(getActivity(),	Analytics.CATEGORY_UI_ACTION,
								Analytics.ACTION_REMINDER_DELETED,
								mLocation.getLocationName(),
								Long.valueOf(adapter.getCount()));
    	
    	Map<String, String> analParams = new HashMap<String, String>();
        analParams.put("locationName", mLocation.getLocationName());
        analParams.put("remindersNum", String.valueOf(adapter.getCount()));
        FlurryAgent.logEvent("Reminder_Deleted", analParams);
        
        Uri uri = Uri.parse(RemindersContentProvider.CONTENT_URI + "/" + reminderId);
        getSherlockActivity().getContentResolver().delete(uri, null, null);
    }

    @Override
    public void toggleReminderActivation(int reminderId, boolean activate)
    {
//        if (mKeyboardShown)
//        {
//            showList();
//        }
//        else
//        {
        	Analytics.reportGAEvent(getActivity(),
        							Analytics.CATEGORY_UI_ACTION,
									Analytics.ACTION_REMINDER_ACTIVATED,
									mLocation.getLocationName(),
									(activate ? 1l : 0l));
        	
        	Map<String, String> analParams = new HashMap<String, String>();
            analParams.put("locationName", mLocation.getLocationName());        
            analParams.put("activated", (activate ? "ON" : "OFF"));
            FlurryAgent.logEvent("Reminder_Activation", analParams);      
        	
            ContentValues values = new ContentValues();
            values.put(RemindersTable.COLUMN_ACTIVATED, activate);

            // Update reminder
            Uri uri = Uri.parse(RemindersContentProvider.CONTENT_URI + "/" + reminderId);
            getSherlockActivity().getContentResolver().update(uri, values, null, null);          
//        }
    }

    public void updateCondition(Condition c) {

        mCurrReminder.setCondition(c);

        // If this is not a new reminder we will update
        if (!mCurrReminder.isNew())
        {
            ContentValues values = new ContentValues();
            values.put(RemindersTable.COLUMN_CONDITION, c.getText());

            // Update reminder
            Uri uri = Uri.parse(RemindersContentProvider.CONTENT_URI + "/" + mCurrReminder.getReminderId());
            getSherlockActivity().getContentResolver().update(uri, values, null, null);
        }
    }


   // @Override
    public void onLocationSelected(int locationId) {

        mCurrReminder.setLocationId(locationId);

        // If the current reminder is not new we will update the value on the db
        if (!mCurrReminder.isNew())
        {
            // Update the location for that reminder
            ContentValues values = new ContentValues();
            values.put(RemindersTable.COLUMN_LOCATION_ID, locationId);

            // Update reminder
            Uri uri = Uri.parse(RemindersContentProvider.CONTENT_URI + "/" + mCurrReminder.getReminderId());
            getSherlockActivity().getContentResolver().update(uri, values, null, null);
        }
    }
}