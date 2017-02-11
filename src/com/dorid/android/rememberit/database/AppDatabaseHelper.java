package com.dorid.android.rememberit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dori on 6/28/13.
 */
public class AppDatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "rememberit.db";
    private static final int DATABASE_VERSION = 2;

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during the creation of the database
    @Override
    public void onCreate(SQLiteDatabase database)
    {
        LocationsTable.onCreate(database);
        RemindersTable.onCreate(database);
    }

    //  Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        LocationsTable.onUpgrade(database, oldVersion, newVersion);
        RemindersTable.onUpgrade(database, oldVersion, newVersion);
    }
}
