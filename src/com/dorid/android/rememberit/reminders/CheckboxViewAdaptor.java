package com.dorid.android.rememberit.reminders;

import android.widget.TextView;

import com.dorid.android.rememberit.R;

/**
 * @author amit
 */
public class CheckboxViewAdaptor {

	final TextView view;

	public CheckboxViewAdaptor(TextView view) {
		this.view = view;
	}

	public void setChecked(boolean checked) {
		if (checked) {
			view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_checked, 0,0,0);
		}
		else {
			view.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_unchecked, 0,0,0);
		}
	}
}