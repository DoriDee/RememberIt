package com.dorid.android.rememberit.locations;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.dorid.android.rememberit.R;
import com.dorid.android.rememberit.database.LocationsTable;

/**
 * Created by Dori on 6/22/13.
 */
public class LocationData implements Parcelable
{
    private int mLocationId;
    private String mLocationName;
    private int mIconId;
    private String mWifiName;
    private String mBssid;
    private String mSurroundingWifis;
    
    public LocationData()
    {

    }

    public LocationData(Parcel parcel)
    {
        mLocationId = parcel.readInt();
        mLocationName = parcel.readString();
        mIconId = parcel.readInt();
        mWifiName = parcel.readString();
        mBssid = parcel.readString();
    }

    public LocationData(int locationId, String locationName, int iconId, String wifiName, String bssid)
    {
        this.setLocationId(locationId);
        this.setLocationName(locationName);
        this.setIconId(iconId);
        this.setWifiName(wifiName);
        this.setBssid(bssid);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mLocationId);
        parcel.writeString(mLocationName);
        parcel.writeInt(mIconId);
        parcel.writeString(mWifiName);
        parcel.writeString(mBssid);
    }

    public void parseFromCursor(Cursor cursor)
    {
        mLocationId = cursor.getInt(cursor.getColumnIndex(LocationsTable.COLUMN_ID));
        mLocationName = cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_NAME));
        mIconId = cursor.getInt(cursor.getColumnIndex(LocationsTable.COLUMN_ICON_ID));
        mWifiName = cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_SSID));
        mBssid = cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_BSSID));
        mSurroundingWifis = cursor.getString(cursor.getColumnIndex(LocationsTable.COLUMN_SURROUNDING_WIFIS)); 
    }

    public String getWifisSurroundings()
    {
    	return mSurroundingWifis;
    }
    
    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public LocationData createFromParcel(Parcel in) {
                    return new LocationData(in);
                }

                public LocationData[] newArray(int size) {
                    return new LocationData[size];
                }
            };

    public String getLocationName() {
        return mLocationName;
    }

    public void setLocationName(String locationName) {
        this.mLocationName = locationName;
    }

    public int getIconId() {

        // Hack for now
        if (mLocationName.equals(LocationsTable.HOME_NAME))
        {
            return R.drawable.ic_action_home;
        }
        else if (mLocationName.equals(LocationsTable.WORK_NAME))
        {
            return R.drawable.ic_action_business;
        }

        return 0;
    }

    public void setIconId(int iconId) {
        this.mIconId = iconId;
    }

    public String getWifiName() {
        return mWifiName;
    }

    public void setWifiName(String wifiName) {
        this.mWifiName = wifiName;
    }

    public String getBssid() {
        return mBssid;
    }

    public void setBssid(String bssid) {
        this.mBssid = bssid;
    }

    public int getLocationId() {
        return mLocationId;
    }

    public void setLocationId(int locationId) {
        this.mLocationId = locationId;
    }

    public static LocationData findLocationByBssid(ArrayList<LocationData> locations, String Bssid)
    {
        for (LocationData locData : locations)
        {
            if (locData.getBssid() != null && locData.getBssid().equals(Bssid))
            {
                return locData;
            }
        }

        return null;
    }

    public static LocationData findLocationById(List<LocationData> locations, int locationId)
    {
        for (LocationData locData : locations)
        {
            if (locData.getLocationId() == locationId)
            {
                return locData;
            }
        }

        return null;
    }
}
