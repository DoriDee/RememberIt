package com.dorid.android.rememberit.analytics;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dorid.android.rememberit.R;
import com.flurry.android.FlurryAgent;

public class AppRater {
	private static String APP_TITLE = "YOUR-APP-NAME";
	private static String APP_PNAME = "PACKAGE NAME";

	private final static int DAYS_UNTIL_PROMPT = 3;
	private final static int LAUNCHES_UNTIL_PROMPT = 7;
	
	public static void app_launched(Context mContext) {
		APP_PNAME = mContext.getPackageName();
		APP_TITLE = mContext.getString(mContext.getApplicationInfo().labelRes);

		String ver = "unknown";
		try {
			ver = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SharedPreferences prefs = mContext.getSharedPreferences("apprater-" + ver, 0);
		SharedPreferences.Editor editor = prefs.edit();
		
		if (prefs.getBoolean("dontshowagain", false)) {
			return;
		}
		
		// Increment launch counter
		long launch_count = prefs.getLong("launch_count", 0) + 1;
		editor.putLong("launch_count", launch_count);

		// Get date of first launch
		Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
		if (date_firstLaunch == 0) {
			date_firstLaunch = System.currentTimeMillis();
			editor.putLong("date_firstlaunch", date_firstLaunch);
		}

		// Wait at least n days before opening
		if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				 showRateDialog(mContext, editor);
			}
		} else if (System.currentTimeMillis() >= date_firstLaunch
				+ (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
			showRateDialog(mContext, editor);
		}
		editor.commit();
	}

	public static long getLaunchCount(Context mContext)
	{
		String ver = "unknown";
		try {
			ver = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SharedPreferences prefs = mContext.getSharedPreferences("apprater-" + ver, 0);		
		
		// Increment launch counter
		return prefs.getLong("launch_count", 0);		
	}
	
	public static void showSendMsg(final Context mContext,
			final SharedPreferences.Editor editor) {
		final Dialog dialog = new Dialog(mContext, R.style.PauseDialog);
		
		Analytics.reportGAEvent(mContext, "app_rater", "dialogmessage", "showdialog", 0L);
		FlurryAgent.logEvent("app_rater_message_showdialog");
		dialog.setTitle("Rate " + APP_TITLE);
		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(mContext);
		tv.setGravity(Gravity.CENTER);
		tv.setText("Your feedback is highly important to us!\nPlease tell us what you don't like so we can fix it and improve the app.\n");
		ll.addView(tv);
		Button b1 = new Button(mContext);
		b1.setText("Contact support");
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				Analytics.reportGAEvent(mContext, "app_rater", "dialogmessage",
						"sendmail", 0L);
				FlurryAgent.logEvent("app_rater_message_sendmail");
				Intent intent = new Intent(Intent.ACTION_SENDTO);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_SUBJECT, " User Feedback for "
						+ APP_TITLE);
				String ver = "unknown";
				try {
					ver = mContext.getPackageManager().getPackageInfo(
							mContext.getPackageName(), 0).versionName;
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				intent.putExtra(Intent.EXTRA_TEXT,
						"General information\nAndroid version : "
								+ android.os.Build.VERSION.RELEASE
								+ "\nDevice : " + android.os.Build.MODEL
								+ "\nApp Version : " + ver
								+ "\n\nYour feedback :\n");

				intent.setData(Uri.parse("mailto:dorid.dev@gmail.com"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(intent);
			}
		});
		ll.addView(b1);

		Button b2 = new Button(mContext);
		b2.setText("Leave me alone");
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Analytics.reportGAEvent(mContext, "app_rater", "dialogmessage",
						"leavemealone", 0L);
				FlurryAgent.logEvent("app_rater_message_leavemealone");
				dialog.dismiss();
			}
		});
		ll.addView(b2);
		dialog.setContentView(ll);
		dialog.show();
	}

	public static void showRateDialog(final Context mContext,
			final SharedPreferences.Editor editor) {
		final Dialog dialog = new Dialog(mContext, R.style.PauseDialog);
		dialog.setTitle("Rate " + APP_TITLE);
		Analytics.reportGAEvent(mContext, "app_rater", "showdialog",
				"showdialog", 0L);
		FlurryAgent.logEvent("app_rater_showdialog");
		LinearLayout ll = new LinearLayout(mContext);
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(mContext);
		tv.setText("If you enjoy using "
				+ APP_TITLE
				+ ", please take a moment to rate it.\nThanks for your support!\n");
		//tv.setTextColor(0xffffffff);
		tv.setGravity(Gravity.CENTER);
		ll.addView(tv);
		Button b1 = new Button(mContext);
		b1.setText("Rate with 5 stars");
		b1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}

				Analytics.reportGAEvent(mContext, "app_rater", "button_press", "ratewithfivestars", 0L);
				FlurryAgent.logEvent("app_rater_ratewithfivestars");

				mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
				try {
					dialog.dismiss();
				} catch (Exception e) {

				}
			}
		});
		ll.addView(b1);

		Button b2 = new Button(mContext);
		b2.setText("Remind me later");
		b2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Analytics.reportGAEvent(mContext, "app_rater", "button_press",
						"remindmelater", 0L);
				FlurryAgent.logEvent("app_rater_remindmelater");
				try {
					dialog.dismiss();
				} catch (Exception e) {

				}
			}
		});
		ll.addView(b2);

		Button b3 = new Button(mContext);
		b3.setText("I don't like it");
		b3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (editor != null) {
					editor.putBoolean("dontshowagain", true);
					editor.commit();
				}
				Analytics.reportGAEvent(mContext, "app_rater", "button_press", "idontlikeit", 0L);
				FlurryAgent.logEvent("app_rater_idontlikeit");
				try {
					dialog.dismiss();
				} catch (Exception e) {

				}
				AppRater.showSendMsg(mContext, editor);
			}
		});
		ll.addView(b3);

		dialog.setContentView(ll);
		dialog.show();
	}
}