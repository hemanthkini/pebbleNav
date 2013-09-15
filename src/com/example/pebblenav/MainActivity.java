package com.example.pebblenav;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	GPSTracker tracker;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PebbleInterface.sendDataToPebble(getApplicationContext(), "sup nigga", "hey", 0, 1);
		tracker = new GPSTracker(getApplicationContext());
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
				
		tracker = new GPSTracker(getApplicationContext());
		setContentView(R.layout.activity_main);

		PebbleInterface.sendDataToPebble(getApplicationContext(), "sup a", "yo bitches", 0, 3);
		PebbleInterface.buzzPebble(getApplicationContext());
		PebbleInterface.buzzPebble(getApplicationContext());
		PebbleInterface.buzzPebble(getApplicationContext());
				
	}
	
	public void getLocData(View v) throws IOException, JSONException{
		
		
		double longitude = tracker.getLongitude();
		double latitude = tracker.getLatitude();
		
		System.out.println("longitude: "+longitude+"\n"+"latitude: "+latitude);
		
		
		EditText textField = (EditText)findViewById(R.id.enterAddress);
		String destAddress = textField.getText().toString().replaceAll(" ", "%20");
		
		String jsonQuery="https://maps.googleapis.com/maps/api/directions/json?mode=walking&sensor=true";
		jsonQuery += "&origin=" + latitude + "," + longitude;
		jsonQuery += "&destination=" + destAddress;
		
			

		URL link = null;
		BufferedReader readURL = null;

		
		System.out.println(jsonQuery);
		
		link = new URL(jsonQuery);
		
		URLConnection connection = link.openConnection();
		
		
		String jsonString = "";
		String inputLine;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		while((inputLine = reader.readLine()) != null) {
			jsonString += inputLine;
		}
		
			reader.close();
		
			JsonDirectionParser parser = new JsonDirectionParser();
			System.out.println(parser.parse(jsonString));
			
		//System.out.println(jsonString);
		 
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
