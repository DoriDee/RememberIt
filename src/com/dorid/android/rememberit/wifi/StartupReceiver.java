package com.dorid.android.rememberit.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {
	 
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("com.dorid.android.rememberit.wifi.WlanLockService");
        context.startService(serviceIntent);
    }
 
}
