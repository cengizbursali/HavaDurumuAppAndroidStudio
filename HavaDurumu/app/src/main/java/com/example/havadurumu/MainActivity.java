package com.example.havadurumu;

import java.text.Collator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat.Action;

import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button goster;
	EditText girdi;
	TextView sonuc;
	String sehir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		goster = (Button) findViewById(R.id.button1);
		girdi = (EditText) findViewById(R.id.editText1);
		sonuc = (TextView) findViewById(R.id.textView1);

		goster.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				new getWeatherCondition().execute();
			}
		});
	}

	private class getWeatherCondition extends AsyncTask<Void, Void, Void> {
		int tempNo;
		String cityName, countrName;
		int flag = 1;

		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected Void doInBackground(Void... params) {

			String a = girdi.getText().toString().trim();
			a = a.replaceAll(" ", "%20");

			String url = "http://api.openweathermap.org/data/2.5/find?q="
					+ a.toString() + "&units=metric";
			JSONObject jsonObject = null;

			try {
				String json = JSONParser.getJSONFromUrl(url);

				try {
					jsonObject = new JSONObject(json);

				} catch (JSONException e) {
					Log.e("JSON Parser",
							"Error creating json object" + e.toString());
				}
				if (jsonObject.get("cod").equals("404")) {
					flag = 0;
				} else {
					JSONArray listArray = jsonObject.getJSONArray("list");
					if (listArray.length() == 0
							|| jsonObject.get("message") == null)
						flag = 0;
					else {
						JSONObject firstObj = listArray.getJSONObject(0);
						JSONObject mainObj = firstObj.getJSONObject("main");
						JSONObject lastObj = firstObj.getJSONObject("sys");
						tempNo = mainObj.getInt("temp");
						cityName = firstObj.getString("name");
						countrName = lastObj.getString("country");
					}
				}
			} catch (JSONException e) {
				Log.e("json", "doInBackground2");
			}
			return null;
		}

		protected void onPostExecute(Void args) {

			if (flag == 1) {

				if (convert(girdi.getText().toString().toLowerCase().trim())
						.equals(convert(cityName.toString().toLowerCase()))) {
					sonuc.setText(" " + cityName.toString() + ": " + tempNo
							+ "\u2103" + "\n Country: " + countrName.toString());
					girdi.setText("");
				} else {
					sonuc.setText(" Location not found.");
					girdi.setText("");
				}
			} else {
				sonuc.setText(" Location not found.");
				girdi.setText("");
			}
		}

		protected String convert(String string) {
			string = string.replaceAll("ı", "i");
			string = string.replaceAll("ğ", "g");
			string = string.replaceAll("ü", "u");
			string = string.replaceAll("ş", "s");
			string = string.replaceAll("ö", "o");
			string = string.replaceAll("ç", "c");

			return string;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
