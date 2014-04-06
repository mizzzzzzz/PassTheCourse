package no2.finalproject.skipclasssaver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MyC extends Activity {

	Button send;
	String line;
	ListView courseList;
	String cid,str;
	int choose=-1;
	int [] id;
	String [] content;
	private View.OnClickListener myListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(id!=null && choose!=-1) {
				String str = "http://1.semestersaver.appspot.com/receiveAlert.jsp?key=" + line + "&course=" + id[choose];
				Log.v("str",str);
				Thread t = new Thread(new SendGetRunnable(str,2));
				t.start();
			} else
				Toast.makeText(getApplicationContext(), "沒選擇課程", Toast.LENGTH_SHORT).show();
		}
	};
	
	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler()  {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 1:
				String result = null;
				if(msg.obj instanceof String)
					result = (String)msg.obj;
				if(result.contains("Invalid key")) break;
				
				int idx = 0;
				while(result.charAt(idx++) != '#');
				result = result.substring(idx);
				String courseDelimiter = ";";
				String[] rawCourseInfo = result.split(courseDelimiter);
				int length = Integer.valueOf(rawCourseInfo[0]).intValue();
				id = new int[length];
				content = new String[length];
				
				String infoDelimiter = ",";
				for(int i = 0; i < length; i++) {
					String[] courseInfo = rawCourseInfo[i+1].split(infoDelimiter);
					id[i] = Integer.valueOf(courseInfo[0]).intValue();
					content[i] = courseInfo[1];
				}
				courseList.setChoiceMode( ListView.CHOICE_MODE_SINGLE );  
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyC.this, 
				          android.R.layout.simple_list_item_single_choice, content);
		        courseList.setAdapter(adapter);
		        break;
			case 2:
				Toast.makeText(getApplicationContext(), "完成通知", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	class SendGetRunnable implements Runnable {
    	String strTxt;
    	int type;
    	public SendGetRunnable(String strTxt, int type) {
    		this.strTxt = strTxt;
    		this.type = type;
    	}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String result = sendGetData(strTxt);
			Log.d("web",strTxt);
			Log.d("result",result);
			mHandler.obtainMessage(type, result).sendToTarget();
		}
    	
    }
	
	private String sendGetData(String strTxt) {
    	String result;
    	HttpGet req = new HttpGet(strTxt);
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(req);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch (Exception e) {
			Log.v("e",e.toString());
		}
    	return null;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tab);
        //霈��摮絲靘�      	
        FileInputStream in = null;
      	try {
     		in = openFileInput("login.txt");
      	    BufferedReader reader = new BufferedReader(
      	                   new InputStreamReader(in, "utf-8"));
      	    line = reader.readLine();
      	    line = reader.readLine();
      	} catch (Exception e) {
      	    ;
      	} finally {
      	    try {
      	        in.close();
     	    } catch (Exception e) {
    	        ;
      	    }
      	}
      	Log.d("id",line);
        id = null;
        courseList = (ListView)findViewById(R.id.lv_course);
        send = (Button)findViewById(R.id.notify_btn);
        send.setOnClickListener(myListener);
    }
    
    public void onResume() {
    	super.onResume();
		str = "http://1.semestersaver.appspot.com/showClassTable.jsp?key=" + line;
		Thread t = new Thread(new SendGetRunnable(str,1));
		t.start();
		courseList.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				choose = arg2;
			}
			
		});
    }
    
    public void onStart() {
    	super.onStart();
		str = "http://1.semestersaver.appspot.com/showClassTable.jsp?key=" + line;
		Thread t = new Thread(new SendGetRunnable(str,1));
		t.start();
		courseList.setOnItemSelectedListener(new ListView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				choose = arg2;
				System.out.println(choose);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				choose = 0;
			}
			
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_tab, menu);
        return true;
    }
}
