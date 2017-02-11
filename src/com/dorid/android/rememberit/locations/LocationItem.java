package com.dorid.android.rememberit.locations;

import android.content.Context;
import android.widget.Checkable;
import android.widget.LinearLayout;

public class LocationItem extends LinearLayout implements Checkable {

	public LocationItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private static final int[] STATE_CHECKABLE = {android.R.attr.state_pressed};
	boolean checked = false;


	@Override	
	public void setChecked(boolean checked) {
	    this.checked = checked;
	    refreshDrawableState();
	}


	@Override	
	public boolean isChecked() {
	    return checked;
	}


	@Override	
	public void toggle() {
	    setChecked(!checked);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
	    int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
	    if (checked)
	    	mergeDrawableStates(drawableState, STATE_CHECKABLE);

	    return drawableState;
	}	
}
