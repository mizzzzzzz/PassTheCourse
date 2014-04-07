package no2.finalproject.skipclasssaver;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class Notified extends Activity {

	TextView tv_name;
	TextView tv_class;
	TextView tv_show;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notified);
		tv_name = (TextView)findViewById(R.id.notify_name);
	}

	public void onStart() {
		super.onStart();
		Intent intent = getIntent();
		String content = intent.getStringExtra("content");
		tv_name.setText(content);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notified, menu);
		return true;
	}

}
