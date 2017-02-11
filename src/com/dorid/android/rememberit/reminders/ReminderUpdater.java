package com.dorid.android.rememberit.reminders;


/**
 * Created by Dori on 6/29/13.
 */
public interface ReminderUpdater
{
    void deleteReminder(int reminderId);
    //void selectLocation(int reminderId);
    void toggleReminderActivation(int reminderId, boolean activate);
    //void updateCondition(int reminderId, Condition c);
    //void setRecurring(int reminderId, boolean recurring);

    void reminderClicked(ReminderData reminderData);
    void reminderLongClicked(ReminderData reminderData);
}