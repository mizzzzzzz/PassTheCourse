package no2.finalproject.skipclasssaver;

import java.io.FileOutputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{

	public static final String SENDER_ID = "599172435668";
	public static int id = 0;
	public GCMIntentService(){
		super(SENDER_ID);
	}
	
	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		Log.v("GCM","receive");
		NotificationManager myNotificationManager = (NotificationManager)arg0.getSystemService(Context.NOTIFICATION_SERVICE);
		  int icon = R.drawable.ic_launcher;
		  
		  String rawCourseMsg = arg1.getStringExtra("message");
		  String courseName = (rawCourseMsg.split("\n"))[1];
		  courseName = courseName.substring(3);
		  
		  CharSequence tickerText = courseName + "點名了";
		  long when = System.currentTimeMillis();
		  Notification myNotification = new Notification(icon,tickerText,when);  
		  Context context = getApplicationContext();
		  CharSequence contentTitle = "二一救星";
		  CharSequence contentText = courseName + "正在點名！";
		  Intent notificationIntent = new Intent(this, Notified.class);
		  notificationIntent.putExtra("content", rawCourseMsg);
		  Log.v("GCM",arg1.getStringExtra("message"));
		  PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
		  myNotification.icon = R.drawable.ic_launcher;
		  myNotification.flags = Notification.FLAG_AUTO_CANCEL;
		  myNotification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		  myNotification.vibrate = new long[]{100, 200, 100, 500};
		  myNotification.defaults |= Notification.DEFAULT_SOUND;
		  myNotificationManager.notify(id++, myNotification);
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		Log.d("try",arg1);
		FileOutputStream out = null;
		try {
		    out = openFileOutput("GCM.txt", Context.MODE_PRIVATE);
		    out.write(arg1.getBytes());
		    out.flush();
		} catch (Exception e) {
		    Log.v("e",e.toString());
		} finally {
		    try {
		        out.close();
		    } catch (Exception e) {
		    	Log.v("ee",e.toString());
		    }
		}
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

}
