package com.dorid.android.rememberit.reminders;

import android.widget.ImageView;

import com.dorid.android.rememberit.R;

/**
 * 
 * @author amit
 */
public class RepeatToggleViewAdaptor {

	final ImageView view;

	public RepeatToggleViewAdaptor(ImageView view) {
		this.view = view;
	}

	public void setEnabled(boolean enabled) {
		if (enabled) {
			view.setImageResource(R.drawable.ic_action_playback_repeat_on);
		}
		else {
			view.setImageResource(R.drawable.ic_action_playback_repeat_off);
		}
	}
}