package com.dorid.android.rememberit.reminders;

import android.view.View;

/**
 * @author amit
 */
public class TwoButtonsFlagViewController {

	final View trueSelectView;
	final View falseSelectView;
	
	public TwoButtonsFlagViewController(View rootView, int resSelectTrue, int resSelectFalse) {
        trueSelectView = rootView.findViewById(resSelectTrue);
        falseSelectView = rootView.findViewById(resSelectFalse);
	}
	
	public void setEnabled(boolean enabled) {
		if (enabled) {
			selectTrue();
		}
		else {
			selectFalse();
		}
	}
	
	public void selectTrue() {
		trueSelectView.setVisibility(View.VISIBLE);
		falseSelectView.setVisibility(View.GONE);
	}
	
	public void selectFalse() {
		trueSelectView.setVisibility(View.GONE);
		falseSelectView.setVisibility(View.VISIBLE);
	}
}