package com.dorid.android.rememberit.reminders;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.database.RemindersTable;
import com.dorid.android.rememberit.database.RemindersTable.Condition;

/**
 * Created by Dori on 7/5/13.
 */
public class RemindersCursorAdapter extends CursorAdapter implements ViewSwitcher.ViewFactory
{
    private final Context context;
    private  ViewHolder holder;
    private LayoutInflater mInflater;
    private int layoutResource;
    private ReminderUpdater mReminderUpdater;
    //private LocationData mLocation;

    private static int ON_COLOR = 0xFF6eca29; //R.color.holo_green_light;//0xFF33B5E5;
    private static int OFF_COLOR = 0xffd0d1d2;
 
    private ArrayList<String> toggleReminders; 
        
    public RemindersCursorAdapter(Context context, Cursor c, int resourceId, ReminderUpdater reminderUpdater) {
        super(context, c, resourceId);

        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mReminderUpdater = reminderUpdater;
//        this.mLocation = location;
        this.layoutResource=resourceId;        
        
        toggleReminders = new ArrayList<String>();
    }

    private static class ViewHolder {
        protected TextView txtReminder;
        protected TextSwitcher switcher;

        //protected Button   btnLocation;
        //protected Button   btnArrival;
        //protected Button   btnDeparture;
        //protected Button   btnRecurring;
    }

    
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        holder = (ViewHolder) view.getTag();

        final String reminderTxt = cursor.getString(cursor.getColumnIndex(RemindersTable.COLUMN_REMINDER_TEXT));
        holder.txtReminder.setText(reminderTxt);

        final Boolean activated = cursor.getInt(cursor.getColumnIndex(RemindersTable.COLUMN_ACTIVATED)) > 0;
        final int reminderId = cursor.getInt(cursor.getColumnIndex("_id"));
        
        TextView tv = (TextView) holder.switcher.getCurrentView(); 
                                                    
        // If this needs toggling we will toggle the bitch
        if (toggleReminders.contains(String.valueOf(reminderId)))
        {
        	holder.switcher.setCurrentText(!activated ? mContext.getString(R.string.on) : mContext.getString(R.string.off));
        	holder.switcher.setText(activated ? mContext.getString(R.string.on) : mContext.getString(R.string.off));
        	        	
        	System.out.println("Toggled ReminderId : " + reminderId + " text : " + tv.getText());
        	
        	toggleReminders.remove(String.valueOf(reminderId));
        }
        else
        {       
        	//tv.setText(activated ? mContext.getString(R.string.on) : mContext.getString(R.string.off));
        	if (tv.getText().equals(mContext.getString(R.string.on)) && !activated)
			{
        		tv.setText(mContext.getString(R.string.off));
			}
        	else if (tv.getText().equals(mContext.getString(R.string.off)) && activated)
        	{
        		tv.setText(mContext.getString(R.string.on));
        	}                	
        }
        
      	System.out.println("Setting " + reminderTxt + (activated ? " on" : " off"));
  		
        ((TextView)holder.switcher.getChildAt(holder.switcher.getDisplayedChild())).setTextColor(activated ? ON_COLOR : OFF_COLOR);

        holder.switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            	System.out.println("toggling ReminderId: " + reminderId + " to activated : " + !activated);
                
            	toggleReminders.add(String.valueOf(reminderId));
            	
                // Set initial text
            	mReminderUpdater.toggleReminderActivation(reminderId, !activated);
            }
        });

        final int locationId = cursor.getInt(cursor.getColumnIndex(RemindersTable.COLUMN_LOCATION_ID));

        final Condition condition = Condition.fromString(cursor.getString(cursor.getColumnIndex(RemindersTable.COLUMN_CONDITION)));
        final Boolean recurring = cursor.getInt(cursor.getColumnIndex(RemindersTable.COLUMN_RECURRING)) > 0;

//        holder.txtReminder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {            	
//                mReminderUpdater.reminderClicked(new ReminderData(reminderId, reminderTxt, locationId, condition, recurring));                
//            }
//        });
//
//        holder.txtReminder.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                mReminderUpdater.reminderLongClicked(new ReminderData(reminderId, reminderTxt, locationId, condition, recurring));
//                return true;
//            }
//        });

/*            final TextView txtLoc = (TextView)view.findViewById(R.id.txtLocationName);
            final EditText editLoc = (EditText)view.findViewById(R.id.edit_location);

            txtLoc.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    editLoc.setVisibility(View.VISIBLE);
                    editLoc.setText(txtLoc.getText());
                    txtLoc.setVisibility(View.GONE);
                }
            });

            editLoc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        doneEditingLocation(txtLoc, editLoc);
                        return true;
                    }
                    return false;
                }
            });

            editLoc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    Log.e("bla", "onFocusChange=" + hasFocus);
                    // If we have lost focus we will return to the text view
                    // with the new text
                    if (!hasFocus)
                    {
                        doneEditingLocation(txtLoc, editLoc);
                    }
                }
            });
*/
    }

    private void doneEditingLocation(TextView txtView, EditText txtEdit)
    {
        // Check that the inserted text is not empty
        if (!txtEdit.getText().equals(""))
        {
            txtView.setVisibility(View.VISIBLE);
            txtView.setText(txtEdit.getText());
            txtEdit.setVisibility(View.GONE);

            // TODO : Update the location name on the db
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View rowView = mInflater.inflate(layoutResource, null);
        holder = new ViewHolder();
        holder.txtReminder = (TextView)rowView.findViewById(R.id.txtReminder);

        holder.switcher = (TextSwitcher)rowView.findViewById(R.id.textswitcher);
        holder.switcher.setFactory(this);
        

        Animation in = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        in.setDuration(300);

        Animation out = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
        out.setDuration(100);
  
        holder.switcher.setInAnimation(in);
        holder.switcher.setOutAnimation(out);
                
//        holder.switcher.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_from_right_slow));
//        holder.switcher.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_to_left_slow));

        Boolean activated = cursor.getInt(cursor.getColumnIndex(RemindersTable.COLUMN_ACTIVATED)) > 0;

        // Set initial text
        holder.switcher.setCurrentText(activated ? mContext.getString(R.string.on) : mContext.getString(R.string.off));
        
        final int reminderId = cursor.getInt(cursor.getColumnIndex("_id"));

//        rowView.setOnClickListener(new View.OnClickListener() {
//  	      @Override
//  	      public void onClick(View view) {            	
//  	          mReminderUpdater.reminderClicked(new ReminderData(reminderId, "saddsa", 1, Condition.Arrival, false));                
//  	      }
//  	  });

        System.out.println("Setting NEW reminder id : " + reminderId + " to activated " + activated);
//        ((TextView)holder.switcher.getChildAt(holder.switcher.getDisplayedChild())).setTextColor(activated ? ON_COLOR : OFF_COLOR);

        rowView.setTag(holder);
        return rowView;
    }
    
    @Override
    public View makeView() {
        TextView t = new TextView(mContext);
        
        //LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        //params.gravity = Gravity.CENTER_VERTICAL;
        //t.setLayoutParams(params);
        
        t.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);        
        t.setTextSize(25);
        
        FrameLayout.LayoutParams tvp1 = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT, (Gravity.CENTER_VERTICAL | Gravity.RIGHT));
        t.setLayoutParams(tvp1);  
        return t;
    }
}
