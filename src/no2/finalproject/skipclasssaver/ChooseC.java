package no2.finalproject.skipclasssaver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.Spinner;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class ChooseC extends Activity {
	Button choose_btn;
	int cid;
	String line;
	ListView courseList;
	Spinner sp;
	String str;
	String department;
	String[] content;
	int numOfCourse;
	
	private View.OnClickListener myListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(department!=null) {
				HashMap<Integer, Boolean> state = courseAdapter.state;  
				String options="";
				numOfCourse=0;
				for(int j=0;j<courseAdapter.getCount();j++){  
					if(state.get(j)!=null){  
						Course map=(Course) courseAdapter.getItem(j);  
						options+=(String.valueOf(map.getId())+",");
						numOfCourse++;
					}  
				} 
				if(numOfCourse!=0) {
					options = options.substring(0, options.length()-1);
					String str = "http://1.semestersaver.appspot.com/takeCourse.jsp?key=" + line + "&department=" + department + "&courses=" + options;
					Log.d("string",str);
					Thread t = new Thread(new SendGetRunnable(str,2));
					t.start();
				} else
					Toast.makeText(getApplicationContext(), "沒選擇課程", Toast.LENGTH_SHORT).show();
			} else
				Toast.makeText(getApplicationContext(), "沒選擇課程", Toast.LENGTH_SHORT).show();
		}
	};
	
	CourseAdapter courseAdapter;
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 1:
				String result = null;
				if(msg.obj instanceof String)
					result = (String)msg.obj;
				if(result!=null) {
					Log.v("result1",result);
					int p = 0;
					char c;
					if(result!="") {
						while((c=result.charAt(++p))!=';');
						while(c==';' && result.charAt(p+1)!='\n' && result.charAt(p+1)!='\r' && result.charAt(p+1)!='\0') {
							String tuples[] = {"","","",""};
							for(int i = 0; i < 4; i++) {
								while((c=result.charAt(++p))!='#' && c!=';') {
									tuples[i] += c;
								}
							}
							Course course = new Course(Integer.valueOf(tuples[0]).intValue(),tuples[1],tuples[2],tuples[3]);
							mStrings.add(course);
						}
					} else
						mStrings.clear();
				}
				courseAdapter = new CourseAdapter(mStrings,ChooseC.this);
		        courseList.setAdapter(courseAdapter);
		        courseList.setTextFilterEnabled(true);
		        break;
			case 2:
				Toast.makeText(getApplicationContext(), String.valueOf(numOfCourse)+"門課程加選成功", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	private static final List<Course> mStrings = new ArrayList<Course>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_tab);
        sp = (Spinner)findViewById(R.id.courseSpinner);
        choose_btn = (Button)findViewById(R.id.choose_btn);
        choose_btn.setOnClickListener(myListener);
        courseList = (ListView)findViewById(R.id.courseList);
        content = new String [6];
        content[0] = "選擇學院";
		content[1] = "資訊學院";
		content[2] = "電機學院";
		content[3] = "管理學院";
		content[4] = "工學院";
		content[5] = "生物科技學院";
		content[5] = "�蝘�摮賊";
		FileInputStream in = null;
		StringBuffer data = new StringBuffer();
		try {
		    //開啟 getFilesDir() 目錄底下名稱為 data.txt 檔案
		    in = openFileInput("data.txt");

		    //讀取該檔案的內容
		    BufferedReader reader = new BufferedReader(
		                   new InputStreamReader(in, "utf-8"));
		    while ((line = reader.readLine()) != null) {
		       data.append(line);
		    }
		    line = data.toString();
		} catch (Exception e) {
		    ;
		} finally {
		    try {
		        in.close();
		    } catch (Exception e) {
		        ;
		    }
		}
    }
    
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
    
    public void onResume() {
    	super.onResume();
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, content);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mStrings.clear();
				switch(arg2) {
				case 0:
					Log.v("d","d");
					department = null;
					str = "";
					break;
				case 1:
					department = "CS";
					break;
				case 2:
					department = "CECE";
					break;
				case 3:
					department = "CM";
					break;
				case 4:
					department = "CE";
					break;
				case 5:
					department = "CBST";
					break;
				}
				if(department != null) str = "http://1.semestersaver.appspot.com/showSyllabusToMobile.jsp?department=" + department;
				if(str!="") {
					Thread t = new Thread(new SendGetRunnable(str,1));
					t.start();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				department = null;
			}
			
		});
    }
    
    public void onStart() {
    	super.onStart();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, content);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mStrings.clear();
				switch(arg2) {
				case 0:
					Log.v("d","d");
					department = null;
					str = "";
					break;
				case 1:
					department = "CCS";
					break;
				case 2:
					department = "CECE";
					break;
				case 3:
					department = "CM";
					break;
				case 4:
					department = "CE";
					break;
				case 5:
					department = "CBST";
					break;
				}
				if(department != null) str = "http://1.semestersaver.appspot.com/showSyllabusToMobile.jsp?department=" + department;
				if(str!="") {
					Thread t = new Thread(new SendGetRunnable(str,1));
					t.start();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				department = null;
			}
			
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_choose_tab, menu);
        return true;
    }
}
