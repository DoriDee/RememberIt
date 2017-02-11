package com.dorid.android.rememberit.locations;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;

import com.dorid.android.rememberit.database.AppDatabaseHelper;
import com.dorid.android.rememberit.database.LocationsTable;

/**
 * Created by Dori on 7/4/13.
 */
public class LocationsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private AppDatabaseHelper dbHelper;
    private String[] allColumns =  { LocationsTable.COLUMN_NAME,
                                     LocationsTable.COLUMN_ICON_ID,
                                     LocationsTable.COLUMN_BSSID,
                                     LocationsTable.COLUMN_SSID,
                                     LocationsTable.COLUMN_ID,
                                     LocationsTable.COLUMN_SURROUNDING_WIFIS};

    public LocationsDataSource(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /*
    public Comment createComment(String comment) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_COMMENT, comment);
        long insertId = database.insert(MySQLiteHelper.TABLE_COMMENTS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_COMMENTS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public void deleteComment(Comment comment) {
        long id = comment.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }*/



    public List<LocationData> getAllLocations() {
        List<LocationData> locations = new ArrayList<LocationData>();

        Cursor cursor = database.query(LocationsTable.TABLE_LOCATIONS, allColumns, null, null, null, null, null);

        if (cursor.moveToFirst())
        {
            do
            {
                LocationData location = new LocationData();
                location.parseFromCursor(cursor);
                locations.add(location);
            } while (cursor.moveToNext());
        }

        // Make sure to close the cursor
        cursor.close();
        return locations;
    }

    public void clearWifiAssignment(int locationId)
    {
        ContentValues values = new ContentValues();
        values.putNull(LocationsTable.COLUMN_BSSID);
        values.putNull(LocationsTable.COLUMN_SSID);

        // Update location
        database.update(LocationsTable.TABLE_LOCATIONS, values, "_id="  + locationId, null);
    }

    public void assignWifiToLocation(WifiInfo wifiInfo, int locationId)
    {
        ContentValues values = new ContentValues();
        values.put(LocationsTable.COLUMN_BSSID, wifiInfo.getBSSID());

        if (wifiInfo.getSSID() != null)
        {
            values.put(LocationsTable.COLUMN_SSID, wifiInfo.getSSID());
        }

        // Update location
        database.update(LocationsTable.TABLE_LOCATIONS, values, "_id="  + locationId, null);
    }

    public LocationData addLocation(String bssid, String ssid, String locationName, int iconId)
    {
        ContentValues values = new ContentValues();
        values.put(LocationsTable.COLUMN_ICON_ID, iconId);
        values.put(LocationsTable.COLUMN_BSSID, bssid);
        values.put(LocationsTable.COLUMN_SSID, ssid);
        values.put(LocationsTable.COLUMN_NAME, locationName);

        long insertId = database.insert(LocationsTable.TABLE_LOCATIONS, null, values);

        Cursor cursor = database.query(LocationsTable.TABLE_LOCATIONS, allColumns, LocationsTable.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        LocationData locationData = new LocationData();
        locationData.parseFromCursor(cursor);
        cursor.close();
        return locationData;
    }

    
    public void updateLocationSurroundings(int locationId, String wifisSurroundings)
    {
    	ContentValues values = new ContentValues();
        values.put(LocationsTable.COLUMN_SURROUNDING_WIFIS, wifisSurroundings);

        database.update(LocationsTable.TABLE_LOCATIONS, values, "_id="  + locationId, null);
    }
}
