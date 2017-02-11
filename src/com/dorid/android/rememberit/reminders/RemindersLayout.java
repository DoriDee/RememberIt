package com.dorid.android.rememberit.reminders;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.dorid.android.rememberit.BackListener;
import com.dorid.android.rememberit.R;

/**
 * Created by Dori on 7/9/13.
 */
public class RemindersLayout extends LinearLayout {

    BackListener mBackListener;

    public RemindersLayout(Context context, AttributeSet attributeSet, BackListener backListener) {
        super(context, attributeSet);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBackListener = backListener;
        inflater.inflate(R.layout.reminder_edit_layout, this);
    }

    /**
     * Overrides the handling of the back key to move back to the
     * previous sources or dismiss the search dialog, instead of
     * dismissing the input method.
     */
    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            KeyEvent.DispatcherState state = getKeyDispatcherState();

            if (state != null)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0)
                {
                    state.startTracking(event, this);
                    return true;
                }
                else if (event.getAction() == KeyEvent.ACTION_UP  && !event.isCanceled() && state.isTracking(event))
                {
                    mBackListener.onBackPressed();
                    return true;
                }
            }
        }

        return super.dispatchKeyEventPreIme(event);
    }
}
