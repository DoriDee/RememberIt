package com.dorid.android.rememberit.reminders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.dorid.android.rememberit.BackListener;
import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.database.RemindersTable.Condition;
import com.dorid.android.rememberit.locations.LocationData;
import com.flurry.android.FlurryAgent;
import com.google.analytics.tracking.android.EasyTracker;
//import com.tapreason.sdk.TapReason;

/**
 * Created by Dori on 7/3/13.
 */
public class EditReminderActivity extends SherlockActivity implements BackListener
{
//    private List<LocationData> mLocations;

    private LocationData mLocation;

    private RemindersLayout mRemindersLayout;
    private EditText mEditReminder;

    private ReminderData mCurrReminder = null;

    private MenuItem mDoneItem;
    
    Uri reminderUri = null;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);

        mRemindersLayout =  new RemindersLayout(this, null, this);
        setContentView(mRemindersLayout);
        
        mLocation = this.getIntent().getParcelableExtra("location");
        mCurrReminder = this.getIntent().getParcelableExtra("reminder");
            	      	
    	if (mCurrReminder == null)
    	{
    		// This is a new reminder    		
    		getSupportActionBar().setTitle(getString(R.string.add_reminder));  
    		mCurrReminder = new ReminderData("", mLocation.getLocationId(), Condition.Arrival, false);    		
    		    		            

    		//btnDone.setVisibility(View.GONE);
    	}
    	else
    	{    		    		
    		getSupportActionBar().setTitle(getString(R.string.edit_reminder));
    	}
    	        	
        setupEditReminderBox();

        overridePendingTransition(R.anim.anim_in_left, R.anim.anim_out_left);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_action_home_white);
        
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
        getSupportMenuInflater().inflate(R.menu.edit_reminder_menu, menu);

        // set up a listener for the refresh item
        mDoneItem = (MenuItem) menu.findItem(R.id.menu_done);    
        
        if (mCurrReminder.getReminderTxt().length() == 0)
        {
        	mDoneItem.setVisible(false);
        }

//        mDoneItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//            // on selecting show progress spinner for 1s
//            public boolean onMenuItemClick(MenuItem item) {                            	
//            	finish();
//                return false;
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {        
    	// Activity finished ok, return nothing since the user canceled
        setResult(RESULT_OK, null);
        super.finish();
    	
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

    @Override
    public void finish() {
    	    	
      // Prepare data intent 
      Intent data = new Intent();
      data.putExtra("reminder", getReminder());

      // Activity finished ok, return the data
      setResult(RESULT_OK, data);
      super.finish();
      
      overridePendingTransition(R.anim.anim_in_right, R.anim.anim_out_right);
   }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId())
	    {
	        case android.R.id.home:	
	        	onBackPressed();
	            break;	
	        case R.id.menu_done:
	        	finish();
	        	break;
	    }
	    
	    return true;
	}
    
         
    public ReminderData getReminder()
    {
    	return mCurrReminder;
    }
    

    private void showDoneBtn()
    {
        if (!mDoneItem.isVisible())
        {
        	mDoneItem.setVisible(true);
            
//            AlphaAnimation aa = new AlphaAnimation(0.1f,1f);
//            aa.setDuration(400);
//            aa.setFillAfter(true);
//            mDoneItem.startAnimation(aa);
        }
    }

    private void hideDoneBtn()
    {    
        if (mDoneItem.isVisible())
        {
//            AlphaAnimation aa = new AlphaAnimation(1f, 0f);
//            aa.setDuration(400);
//            aa.setFillAfter(true);
//            btnDone.startAnimation(aa);

            mDoneItem.setVisible(false);
        }
    }


    private void setupTextField()
    {
        mEditReminder = (EditText) mRemindersLayout.findViewById(R.id.editReminder);

        if (!mCurrReminder.isNew())
        {
        	mEditReminder.setText(mCurrReminder.getReminderTxt());
        	mEditReminder.setSelection(mEditReminder.getText().length());
        }

        mEditReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                
            }
        });

        mEditReminder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    finish();
                }
                return false;
            }
        });

        mEditReminder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.toString().trim().length() > 0)
                {
                	if (mCurrReminder.getReminderTxt().length() == 0)
                	{
                		showDoneBtn();
                	}                	
                }
                else
                {
            		hideDoneBtn();                	
                }
                
            	mCurrReminder.setReminderTxt(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupWhenConditionButtons()
    {
        final View btnDeparture = mRemindersLayout.findViewById(R.id.viewDeparture);

    	final TwoButtonsFlagViewController repeatViewCtrl = new TwoButtonsFlagViewController(mRemindersLayout, R.id.arrivalSelected, R.id.departureSelected);
    	repeatViewCtrl.setEnabled(mCurrReminder.getCondition() == Condition.Arrival);

    	final View btnArrival = (View)mRemindersLayout.findViewById(R.id.viewArrival);
        btnArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                            	
                repeatViewCtrl.selectTrue();
                updateCondition(Condition.Arrival);
            }
        });

        // Start the default as off
        btnDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	repeatViewCtrl.selectFalse();
                updateCondition(Condition.Departure);
            }
        });
    }

    private void setupRepeatConditionButtons() {

    	final View repeatView = mRemindersLayout.findViewById(R.id.ConditionRepeat);
    	final TwoButtonsFlagViewController repeatViewCtrl = new TwoButtonsFlagViewController(repeatView, R.id.repeatOnSelected, R.id.repeatOffSelected);
    	
    	repeatViewCtrl.setEnabled(mCurrReminder.isRecurring());
    	
		View repeatOnView = repeatView.findViewById(R.id.viewRepeatOn);
    	repeatOnView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					repeatViewCtrl.selectTrue();
	                updateRecurring(true);
				}
			});
    	
    	View repeatOffView = repeatView.findViewById(R.id.viewRepeatOff);
    	repeatOffView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				repeatViewCtrl.selectFalse();
                updateRecurring(false);
			}
		});
    }
    
//    private void setupRepeatConditionButtons_LayoutXmlOption1() {
//    	final View repeatBtn = (View) mRemindersLayout.findViewById(R.id.repeatButton);
//    	final RepeatToggleViewAdaptor repeatAdapt = new RepeatToggleViewAdaptor((ImageView) repeatBtn);
//    	repeatAdapt.setEnabled(mCurrReminder.isRecurring());
//
//        repeatBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateRecurring(!mCurrReminder.isRecurring());
//                repeatAdapt.setEnabled(mCurrReminder.isRecurring());
//            }
//        });
//    }

    private void setupEditReminderBox()
    {
    	
        setupTextField();

        //setupAddButton();

        /*Button btnLocation = (Button)mRemindersLayout.findViewById(R.id.btnLocation);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectLocation();
            }
        });*/

        setupWhenConditionButtons();
        setupRepeatConditionButtons();
    }

    public void updateCondition(Condition c) {
        mCurrReminder.setCondition(c);
    }

    public void updateRecurring(boolean recurring) {
        mCurrReminder.setRecurring(recurring);
    }

}