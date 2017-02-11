package com.dorid.android.rememberit.reminders;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.locations.LocationData;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
//import com.tapreason.sdk.TapReason;

/**
 * Created by Dori on 8/2/13.
 */
public class RemindersActivity extends SherlockFragmentActivity {

	LocationData mLocation;
	RemindersFragment remindersFragment; 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_left);

        mLocation = this.getIntent().getParcelableExtra("location");
        setTitle(mLocation.getLocationName());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.fragment_holder);

        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.ab_layout, null);

        //back.setVisibility(View.GONE);

        ImageButton back = (ImageButton)v.findViewById(R.id.btnBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                //inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                onBackPressed();
            }
        });

//        ImageView logo = (ImageView)v.findViewById(R.id.logo);
//        logo.setImageResource(mLocation.getIconId());
//        logo.setVisibility(View.VISIBLE);
//
//        TextView title = (TextView)v.findViewById(R.id.abTitle);
//        title.setText(mLocation.getLocationName());
//        title.setPadding(0, 0, 0, 0);
//     
//        ImageButton btnAdd = (ImageButton)v.findViewById(R.id.btnAdd);
//        btnAdd.setVisibility(View.VISIBLE);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//
//        /*
//        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
//                ActionBar.LayoutParams.MATCH_PARENT,
//                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER );
//*/
//        getSupportActionBar().setCustomView(v);
      
        getSupportActionBar().setIcon(R.drawable.ic_action_home_white);
        
        // Check whether the activity is using the layout version with
        // the fragment_container FrameLayout. If so, we must add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of the locations fragment
            remindersFragment = new RemindersFragment();

            Bundle locationArgs = new Bundle();
            //locationArgs.putParcelableArrayList("locations", locations);
            locationArgs.putParcelable("location", mLocation);

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            remindersFragment.setArguments(locationArgs);

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, remindersFragment).commit();
        }
        
        // Remove all notifications (if we got here from a notifications we would like to remove all of them)
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        
        EasyTracker.getInstance(this).activityStart(this);
    }
    
    @Override
    public void onStart()
    {
    	super.onStart();
    	FlurryAgent.onStartSession(this, "23SZ7Q5HSZZCK3K56FV3");
    	
//    	TapReason.register(this);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.reminders_menu, menu);

        // set up a listener for the refresh item
        final MenuItem addItem = (MenuItem) menu.findItem(R.id.menu_add);
        

        addItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            // on selecting show progress spinner for 1s
            public boolean onMenuItemClick(MenuItem item) {
            	            	
            	if (remindersFragment != null)
            	{
            		remindersFragment.onAddReminderClicked();
            	}
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
  
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_right);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
        EasyTracker.getInstance(this).activityStop(this);
        
//        TapReason.unRegister(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            	onBackPressed();
                break;
        }
        return true;
    }
}
