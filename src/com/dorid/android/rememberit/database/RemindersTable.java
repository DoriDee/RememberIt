package com.dorid.android.rememberit.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Dori on 7/5/13.
 */
public class RemindersTable {

    // Database table
    public static final String TABLE_REMINDERS = "reminders";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ICON_ID = "iconId";
    public static final String COLUMN_ACTIVATED = "activated";
    public static final String COLUMN_REMINDER_TEXT = "reminderText";
    public static final String COLUMN_LOCATION_ID = "locationId";
    public static final String COLUMN_RECURRING = "recurring";
    public static final String COLUMN_CONDITION = "condition";

    public enum Condition
    {
        Arrival("Arrival"),
        Departure("Departure");

        private String mCondition;

        Condition(String condition)
        {
            mCondition = condition;
        }

        public String getText() {
            return mCondition;
        }

        public static Condition fromString(String text)
        {
            if (text != null) {
                for (Condition c : Condition.values()) {
                    if (text.equals(c.getText()))
                    {
                        return c;
                    }
                }
            }

            return null;
        }
    };

    private static final String DATABASE_CREATE = "create table "
            + TABLE_REMINDERS
            + "("
            + COLUMN_ID            + " integer primary key autoincrement, "
            + COLUMN_REMINDER_TEXT + " text not null, "
            + COLUMN_ACTIVATED + " integer not null, " // bool
            + COLUMN_LOCATION_ID   + " integer not null, "
            + COLUMN_RECURRING     + " integer not null, " // bool
            + COLUMN_CONDITION     + " text not null, "
            + COLUMN_ICON_ID       + " integer"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    // TODO : Thinks if it's the best way to go
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        Log.w(RemindersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
//        database.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
//        onCreate(database);
    }
}
