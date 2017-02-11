package com.dorid.android.rememberit;

//import com.tapreason.sdk.TapReason;
import android.app.Application;

public class RememberItApp extends Application {

	@Override
	public void onCreate()
	{
		super.onCreate();
		
//		TapReason.init( "8A37DA1A5BFA67DCE4B7A89C2D9EFBD8", "lruvmqosygvmuldm", 
//						new WeakReference<Context>(getApplicationContext()), "RememberIt");
//		
//		TapReason.getConf().setSupportEmail("dorid.dev@gmail.com" );
	}	
}
