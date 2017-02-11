package com.dorid.android.rememberit.reminders;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.dorid.android.rememberit.database.RemindersTable;
import com.dorid.android.rememberit.database.RemindersTable.Condition;

/**
 * Created by Dori on 7/11/13.
 */
public class ReminderData  implements Parcelable {
    private Integer mReminderId = null;
    private String mReminderTxt;
    private int mLocationId;
    private Condition mCondition;
    private boolean mIsActivated;
    private boolean mRecurring;

    public ReminderData()
    {
    	
    }
    public ReminderData(int reminderId, String reminderTxt)
    {
        mReminderId = reminderId;
        mReminderTxt = reminderTxt;
    }

    public ReminderData(String reminderTxt, int locationId, Condition condition, boolean isRecurring)
    {
        mReminderTxt = reminderTxt;
        mLocationId = locationId;
        mCondition = condition;
        mRecurring = isRecurring;
    }

    public ReminderData(int reminderId, String reminderTxt, int locationId, Condition condition, boolean isRecurring)
    {
        mReminderId = reminderId;
        mReminderTxt = reminderTxt;
        mLocationId = locationId;
        mCondition = condition;
        mRecurring = isRecurring;
    }

    public void parseFromCursor(Cursor cursor)
    {
        mReminderId = cursor.getInt(cursor.getColumnIndex(RemindersTable.COLUMN_ID));
        mReminderTxt = cursor.getString(cursor.getColumnIndex(RemindersTable.COLUMN_REMINDER_TEXT));
        mLocationId = cursor.getInt(cursor.getColumnIndex(RemindersTable.COLUMN_LOCATION_ID));
        mCondition = Condition.fromString(cursor.getString(cursor.getColumnIndex(RemindersTable.COLUMN_CONDITION)));
        mRecurring = cursor.getInt(cursor.getColumnIndex(RemindersTable.COLUMN_RECURRING)) == 1;
        mIsActivated = cursor.getInt(cursor.getColumnIndex(RemindersTable.COLUMN_ACTIVATED)) == 1;
    }

    public Boolean isNew() {
        return  mReminderId == null;
    }

    public int getReminderId() {
        return mReminderId;
    }

    public void setReminderId(int mReminderId) {
        this.mReminderId = mReminderId;
    }

    public String getReminderTxt() {
        return mReminderTxt;
    }

    public void setReminderTxt(String mReminderTxt) {
        this.mReminderTxt = mReminderTxt;
    }

    public int getLocationId() {
        return mLocationId;
    }

    public void setLocationId(int mLocationId) {
        this.mLocationId = mLocationId;
    }

    public Condition getCondition() {
        return mCondition;
    }

    public void setCondition(Condition mCondition) {
        this.mCondition = mCondition;
    }

    public boolean isRecurring() {
        return mRecurring;
    }

    public void setRecurring(boolean mRecurring) {
        this.mRecurring = mRecurring;
    }

    public boolean isIsActivated() {
        return mIsActivated;
    }

    public void setIsActivated(boolean mIsActivated) {
        this.mIsActivated = mIsActivated;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ReminderData(Parcel parcel)
	{
		int rId = parcel.readInt();
		mReminderId = (rId == -1 ? null : rId);
		mReminderTxt = parcel.readString();
		mLocationId = parcel.readInt();
		mCondition = Condition.fromString(parcel.readString());
		mIsActivated = (parcel.readByte() == 1);
		mRecurring = (parcel.readByte() == 1);
	}
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		arg0.writeInt(mReminderId != null ? mReminderId : -1);
		arg0.writeString(mReminderTxt);
		arg0.writeInt(mLocationId);
		arg0.writeString(mCondition.getText());
		arg0.writeByte((byte)(mIsActivated ? 1 : 0));		
		arg0.writeByte((byte)(mRecurring ? 1 : 0));
	}
	
	 public static final Parcelable.Creator CREATOR =
	            new Parcelable.Creator() {
	                public ReminderData createFromParcel(Parcel in) {
	                    return new ReminderData(in);
	                }

	                public ReminderData[] newArray(int size) {
	                    return new ReminderData[size];
	                }
	            };


}
