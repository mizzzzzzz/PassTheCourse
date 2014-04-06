package no2.finalproject.skipclasssaver;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

public class RegisActivity extends Activity {

	Button enterBtn;
	private EditText account;
	private EditText password;
	private TextView loading;
	private static Map<String, String> params = null;
	static String indexpage = " ";
	static String cookie = "";
	static String str_account = "";
	static String str_password = "";
	static String regisOrNot = "";
	static String loginOrNot = "";
	static String line = "";
	static int loginsuccess = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_regis);

		enterBtn = (Button) findViewById(R.id.enter_btn);
		account = (EditText) findViewById(R.id.et_account);
		password = (EditText) findViewById(R.id.et_password);
		loading = (TextView) findViewById(R.id.tv_loading);

		enterBtn.setVisibility(View.GONE);
		account.setVisibility(View.GONE);
		password.setVisibility(View.GONE);
		enterBtn.setText("Waiting for GCM Key.");
		enterBtn.setEnabled(false);
		FileInputStream in = null;
		try {
			in = openFileInput("GCM.txt");
			// 讀取該檔案的內容
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "utf-8"));
			regisOrNot = reader.readLine();
		} catch (Exception e) {
			regisOrNot = "0";
		} finally {
			try {
				in.close();
				in = openFileInput("login.txt");
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in, "utf-8"));
				loginOrNot = reader.readLine();
				str_account = reader.readLine();
				str_password = reader.readLine();
			} catch (Exception e) {
				loginOrNot = "0";
			}
		}
		if (regisOrNot == "0")
			GCMRegis();
		else {
			StringBuffer data = new StringBuffer();
			try {
				in = openFileInput("data.txt");
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
		if (loginOrNot.contentEquals("1")) {
			Thread t = new Thread(new SendGetRunnable());
			t.start();
		} else {
			loading.setVisibility(View.GONE);
			account.setVisibility(View.VISIBLE);
			password.setVisibility(View.VISIBLE);
			enterBtn.setVisibility(View.VISIBLE);
			final RelativeLayout background = (RelativeLayout) findViewById(R.id.back);
			background.setBackgroundColor(Color.WHITE);

			account.setText("0016305");
			password.setText("080442");
			enterBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					enterBtn.setEnabled(false);
					enterBtn.setText("Waiting for e3 to respond");
					str_account = account.getText().toString();
					str_password = password.getText().toString();
					if (str_account.length() != 0 && str_password.length() != 0) {
						Thread t = new Thread(new SendGetRunnable());
						t.start();
					} else {
						Toast.makeText(RegisActivity.this,
								"account/password cannot be empty",
								Toast.LENGTH_SHORT).show();
					}
				}

			});
		}
	}

	@SuppressWarnings("finally")
	private boolean GCMRegis() {
		GCMRegistrar.checkDevice(RegisActivity.this);
		GCMRegistrar.checkManifest(RegisActivity.this);
		String regId = GCMRegistrar.getRegistrationId(RegisActivity.this);
		if (regId.equals("")) {
			GCMRegistrar.register(RegisActivity.this,
					GCMIntentService.SENDER_ID);
			enterBtn.setText("Login.");
			enterBtn.setEnabled(true);
		}
		return true;
	}

	class SendGetRunnable implements Runnable {
		@Override
		public void run() {
			DefaultHttpClient client = new DefaultHttpClient();
			final HttpParams Httpparams = new BasicHttpParams();
			HttpClientParams.setRedirecting(Httpparams, false);
			client.setParams(Httpparams);
			String buffer = "";
			try {
				if (loadPage(client, "http://dcpc.nctu.edu.tw/")) {
					if (loginPost(client, "http://dcpc.nctu.edu.tw/index.aspx/")) {
						if (loadPage(client, indexpage)) {
							if (loadPage(client, "http://e3.nctu.edu.tw"
									+ indexpage)) {
								buffer = ReadPage(client,
										"http://e3.nctu.edu.tw" + indexpage);
							}
						}
					}
				}
			} catch (ClientProtocolException e1) {
				System.out.println("ClientProtocolException");
			} catch (IOException e1) {
				System.out.println("IOException");
			}
			if (loginsuccess == 1) {
				enterBtn.setText("Login Success");
				System.out.println("Login Success");
				FileOutputStream out = null;
				try {
					out = openFileOutput("login.txt", Context.MODE_PRIVATE);
					String inS = "1" + "\n" + str_account + "\n" + str_password;
					out.write(inS.getBytes());
					out.flush();
				} catch (Exception e) {
					Log.d("debug", e.toString());
				} finally {
					try {
						out.close();
					} catch (Exception e) {
						;
					}
				}
				int time_out = 0;
				while (time_out <= 5) {
					if (ParsetoServer(buffer) == true) {
						Intent intent = new Intent();
						intent.setClass(RegisActivity.this, MyC.class);
						startActivity(intent);
						time_out = 6;
					} else {
						System.out.println(time_out + "times"
								+ "Deliver Failed");
						time_out++;
					}
				}
				finish();
			} else {
				System.out.println("Login Failed");
				enterBtn.setEnabled(true);
				enterBtn.setText("Login.");
			}
		}
	}

	private boolean ParsetoServer(String buffer) {
		// parse name
		String name = " ";
		Matcher m = Pattern.compile("[\\[][\u4E00-\u9fa5]+[\\]]").matcher(
				buffer); // ^否定
		if (m.find()) {
			String v = m.group();
			name = v.substring(1, v.length() - 1);
		}
		System.out.println("name=" + name);

		// parse courses
		m = Pattern.compile("【102\\s下】.{600}").matcher(buffer);
		String course = "";
		int count = 0;
		DefaultHttpClient client = new DefaultHttpClient();
		try {
			while (!loadPage(client, "http://timetable.nctu.edu.tw/"))
				;
		} catch (ClientProtocolException e1) {
			System.out.println("ClientProtocolException");
		} catch (IOException e1) {
			System.out.println("IOException");
		}
		while (m.find()) {
			String v = m.group();
			String info = "";
			Matcher cnum = Pattern.compile("\\d{4}").matcher(v);
			Matcher cname = Pattern.compile("[)][\"][>].+[<][/][a][>]")
					.matcher(v);
			if (cnum.find()) {
				v = cnum.group();
				course += v + ",";
				info = timetable(client,
						"http://timetable.nctu.edu.tw/?r=main/get_cos_list", v);
			}
			if (cname.find()) {
				count++;
				v = cname.group();
				course += v.substring(3, v.length() - 4) + "," + info + ";";
			}
		}
		course = String.valueOf(count) + ";" + course;
		HttpPost post = new HttpPost(
				"http://1.semestersaver.appspot.com/checking");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		System.out.println("line="+line);
		System.out.println(course);
		qparams.add(new BasicNameValuePair("sid", str_account));
		qparams.add(new BasicNameValuePair("gcmkey", line));
		qparams.add(new BasicNameValuePair("course", course));
		try {
			post.setEntity(new UrlEncodedFormEntity(qparams, HTTP.UTF_8));
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("4.statusCode:" + statusCode);
			return (statusCode == 200);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			post.abort();
			return false;
		} catch (ClientProtocolException e) {
			post.abort();
			return false;
		} catch (IOException e) {
			post.abort();
			return false;
		}
	}

	private boolean loadPage(HttpClient client, String url)
			throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		response = client.execute(get);
		int statusCode = response.getStatusLine().getStatusCode();
		Header head = response.getFirstHeader("Location");
		if (head != null)
			indexpage = head.getValue().toString();
		Header[] headers = response.getHeaders("Set-Cookie");
		for (Header header : headers)
			cookie += header.getValue() + " ";
		System.out.println("1.statusCode:" + statusCode);
		HttpEntity entity = response.getEntity();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				entity.getContent()));
		String buffer = null;
		StringBuilder sb = new StringBuilder();
		while ((buffer = reader.readLine()) != null) {
			sb.append(buffer);
		}
		params = getAllInputNames(sb.toString());
		get.abort();
		return (statusCode == 302) || (statusCode == 200);
	}

	private static boolean loginPost(HttpClient client, String url) {
		HttpPost post = new HttpPost(url);
		post.setHeader("Cookie", cookie);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		Log.d("cookie", cookie);
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("__EVENTTARGET", ""));
		qparams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
		qparams.add(new BasicNameValuePair("__VIEWSTATE", params
				.get("__VIEWSTATE")));
		qparams.add(new BasicNameValuePair("__VIEWSTATEENCRYPTED", ""));
		qparams.add(new BasicNameValuePair("__EVENTVALIDATION", params
				.get("__EVENTVALIDATION")));
		qparams.add(new BasicNameValuePair("rblLang", "zh-TW"));
		qparams.add(new BasicNameValuePair("txtAccount", str_account));
		qparams.add(new BasicNameValuePair("txtPwd", str_password));
		qparams.add(new BasicNameValuePair("btnLoginIn", "SIGN+IN"));
		try {
			post.setEntity(new UrlEncodedFormEntity(qparams));
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("2.statusCode:" + statusCode);
			if (statusCode == 302) {
				indexpage = response.getFirstHeader("Location").getValue();
				post.abort();
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			post.abort();
			return false;
		} catch (ClientProtocolException e) {
			post.abort();
			return false;
		} catch (IOException e) {
			post.abort();
			return false;
		}
		return false;
	}

	private static String ReadPage(HttpClient client, String url) {
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		String buffer = null;
		try {
			response = client.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("3.statusCode:" + statusCode);
			HttpEntity entity = response.getEntity();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			StringBuilder sb = new StringBuilder();
			while ((buffer = reader.readLine()) != null) {
				sb.append(buffer);
			}
			buffer = sb.toString();
			loginsuccess = 1;
			get.abort();
		} catch (ClientProtocolException e) {
			buffer = "failed";
		} catch (IOException e) {
			buffer = "failed";
		}
		return buffer;
	}

	private static String timetable(HttpClient client, String url, String cosid) {
		System.out.println("cosid="+cosid);
		String info = "";
		HttpPost post = new HttpPost(url);
		post.setHeader("Accept","application/json, text/javascript, */*");
		post.setHeader("Cookie", cookie);
		post.setHeader("Accept-Encoding","gzip,deflate,sdch");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		//post.setHeader("Content-Length", "173");
		post.setHeader("X-Requested-With", "XMLHttpRequest");
		//Log.d("cookie1", cookie);
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("m_acy", "102"));
		qparams.add(new BasicNameValuePair("m_sem", "2"));
		qparams.add(new BasicNameValuePair("m_degree", "**"));
		qparams.add(new BasicNameValuePair("m_dep_id", "**"));
		qparams.add(new BasicNameValuePair("m_group", "**"));
		qparams.add(new BasicNameValuePair("m_grade", "**"));
		qparams.add(new BasicNameValuePair("m_class", "**"));
		qparams.add(new BasicNameValuePair("m_option", "cos_id"));
		qparams.add(new BasicNameValuePair("m_crsname", "**"));
		qparams.add(new BasicNameValuePair("m_teaname", "**"));
		qparams.add(new BasicNameValuePair("m_cos_id", cosid.toString()));
		qparams.add(new BasicNameValuePair("m_cos_code", "**"));
		qparams.add(new BasicNameValuePair("m_crstime", "**"));
		qparams.add(new BasicNameValuePair("m_crsoutline", "**"));
		try {
			post.setEntity(new UrlEncodedFormEntity(qparams));
			HttpResponse response = client.execute(post);
			int statusCode = response.getStatusLine().getStatusCode();
			System.out.println("5.statusCode:" + statusCode);   /*"cos_time":"2G5CD-ED102"*/
			
			if (statusCode == 200) {
				InputStream inputStream = AndroidHttpClient.getUngzippedContent(response.getEntity());
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				info = reader.readLine();
				//System.out.println("response="+info);
				Matcher m = Pattern.compile("[\"][c][o][s][_][t][i][m][e][\"][:][\"].+[\"][,][\"][m]").matcher(
						info); // ^否定
				if (m.find()) {
					String v = m.group();
					info = v.substring(12,v.length()-4);
					
					Log.d("info",info);
				}
				post.abort();
				return info;
			} else {
				System.out.println("timtable statuscode error");
			}

		} catch (UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException");
			e.printStackTrace();
			post.abort();
			return "";
		} catch (ClientProtocolException e) {
			System.out.println("ClientProtocolException");
			post.abort();
			return "";
		} catch (IOException e) {
			System.out.println("IOException");
			post.abort();
			return "";
		}
		post.abort();
		return "";
	}

	private static String getInputProperty(String input, String property) {
		Matcher m = Pattern.compile(property + "[\\s]*=[\\s]*\"[^\"]*\"")
				.matcher(input);
		if (m.find()) {
			String v = m.group();
			return v.substring(v.indexOf("\"") + 1, v.length() - 1);
		}
		return null;
	}

	private static Map<String, String> getAllInputNames(String body) {
		Map<String, String> parameters = new HashMap<String, String>();
		Matcher matcher = Pattern.compile("<input[^<]*>").matcher(body); 
		while (matcher.find()) {
			String input = matcher.group();
			// Log.d("input",input);
			if (input.contains("name")) {
				parameters.put(getInputProperty(input, "name"),
						getInputProperty(input, "value"));
			}
		}
		return parameters;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.regis, menu);
		return true;
	}

}
