package com.dorid.android.rememberit.reminders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dorid.android.rememberit.database.AppDatabaseHelper;
import com.dorid.android.rememberit.database.RemindersTable;
import com.dorid.android.rememberit.database.RemindersTable.Condition;

/**
 * Created by Dori on 7/18/13.
 */
public class RemindersDataSource {

    // Database fields
    private SQLiteDatabase database;
    private AppDatabaseHelper dbHelper;

    public RemindersDataSource(Context context) {
        dbHelper = new AppDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getRemindersCursor(int locationId)
    {
        Cursor cursor = database.query(RemindersTable.TABLE_REMINDERS,new String[]{RemindersTable.COLUMN_REMINDER_TEXT,
                RemindersTable.COLUMN_ID},
                RemindersTable.COLUMN_LOCATION_ID + "=" + locationId,
                null, null, null, null);

        return cursor;
    }

    public List<ReminderData> getReminders(Condition condition, int locationId)
    {
        List<ReminderData> reminders = new ArrayList<ReminderData>();

        Cursor cursor = database.query(RemindersTable.TABLE_REMINDERS,new String[]{RemindersTable.COLUMN_REMINDER_TEXT,
                                                                                   RemindersTable.COLUMN_ID,
                                                                                   RemindersTable.COLUMN_LOCATION_ID,
                                                                                   RemindersTable.COLUMN_CONDITION,
                                                                                   RemindersTable.COLUMN_RECURRING,
                                                                                   RemindersTable.COLUMN_ACTIVATED},
                                       RemindersTable.COLUMN_CONDITION + "='" + condition.getText() + "' and " +
                                       RemindersTable.COLUMN_LOCATION_ID + "=" + locationId + " and " +
                                       RemindersTable.COLUMN_ACTIVATED + "=1",
                                       null, null, null, null);

        if (cursor.moveToFirst())
        {
            do
            {
                ReminderData reminderData = new ReminderData();
                reminderData.parseFromCursor(cursor);
                
                reminders.add(reminderData);
            } while (cursor.moveToNext());
        }

        // Make sure to close the cursor
        cursor.close();
        return reminders;
    }
}
