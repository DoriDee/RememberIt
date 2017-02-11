package com.dorid.android.rememberit.wifi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WlanLockService extends Service {
	 
    private WlanScanner scanner;
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
    @Override
    public void onCreate() {
        super.onCreate();
        scanner = new WlanScanner(this, 15);
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
 
        scanner.stop();
        scanner = null;
    }
}
