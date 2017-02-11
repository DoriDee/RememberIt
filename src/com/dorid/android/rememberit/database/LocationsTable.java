package com.dorid.android.rememberit.database;

import android.database.sqlite.SQLiteDatabase;

import com.dorid.android.rememberit.R;

/**
 * Created by Dori on 6/28/13.
 */
public class LocationsTable {

    // Database table
    public static final String TABLE_LOCATIONS = "locations";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SSID = "ssid";
    public static final String COLUMN_BSSID = "bssid";
    public static final String COLUMN_ICON_ID = "iconId";

    public static final String COLUMN_SURROUNDING_WIFIS = "surroundingWifis";
    
    public static final int HOME_ID = 1;
    public static final int WORK_ID = 2;

    public static final String HOME_NAME = "Home";
    public static final String WORK_NAME = "Work";

    public static final int HOME_ICON = R.drawable.ic_action_home;
    public static final int WORK_ICON = R.drawable.ic_action_business;

    private static final int SURROUNDING_WIFIS_VERSION = 2;
    
    private static final String DATABASE_CREATE = "create table "
                    + TABLE_LOCATIONS
                    + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_NAME + " text not null, "
                    + COLUMN_SSID + " text, "
                    + COLUMN_BSSID + " text, "
                    + COLUMN_ICON_ID + " integer"
                    + ");";

    private static final String ADD_WIFIS_DATA_COLUMN = "ALTER TABLE " + TABLE_LOCATIONS + " ADD COLUMN " + COLUMN_SURROUNDING_WIFIS + " text";
    
    private static final String INSERT_HOME = "INSERT INTO " + TABLE_LOCATIONS + "(" + COLUMN_ID + "," + COLUMN_NAME + "," + COLUMN_ICON_ID + ")" +
                                              " VALUES (" + HOME_ID + ",'" + HOME_NAME + "'," + HOME_ICON + ");";

    private static final String INSERT_WORK = "INSERT INTO " + TABLE_LOCATIONS + "(" + COLUMN_ID + "," + COLUMN_NAME + "," + COLUMN_ICON_ID + ")" +
                                              " VALUES (" + WORK_ID + ",'" + WORK_NAME + "'," + WORK_ICON + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
		database.execSQL(ADD_WIFIS_DATA_COLUMN);

        //TODO: REMOVE IT!!! THIS CODE IS FOR SIMULATOR ONLY

        // Insert the first two locations (home and office)
//        database.execSQL(INSERT_HOME);
  //      database.execSQL(INSERT_WORK);

        // THIS CODE IS FOR SIMULATOR ONLY
    }

    // TODO : Thinks if it's the best way to go
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        //Log.w(LocationsTable.class.getName(), "Upgrading database from version "
       //             + oldVersion + " to " + newVersion
       //             + ", which will destroy all old data");
        
    	//database.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        //onCreate(database);
    	
    	if (oldVersion < SURROUNDING_WIFIS_VERSION)
    	{
    		database.execSQL(ADD_WIFIS_DATA_COLUMN);
    	}
    }
}

