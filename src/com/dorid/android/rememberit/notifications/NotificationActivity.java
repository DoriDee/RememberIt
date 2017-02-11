package com.dorid.android.rememberit.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.dorid.android.rememberit.R;

public class NotificationActivity extends Activity
{
    @Override
        public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_layout);

        //setContentView(R.layout.activity_result);
        //String message = getIntent().getStringExtra(CommonConstants.EXTRA_MESSAGE);
        //TextView text = (TextView) findViewById(R.id.result_message);
        //text.setText(message);
    }

    public void onSnoozeClick(View v) {
        //Intent intent = new Intent(getApplicationContext(), PingService.class);
        //intent.setAction(CommonConstants.ACTION_SNOOZE);
        //startService(intent);
    }

    public void onDismissClick(View v) {
        //Intent intent = new Intent(getApplicationContext(), PingService.class);
        //intent.setAction(CommonConstants.ACTION_DISMISS);
        //startService(intent);
    }
}
