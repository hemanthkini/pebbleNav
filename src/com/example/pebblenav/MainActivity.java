package com.example.pebblenav;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.location.*;
import java.net.*;
import java.io.*;
import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
	GPSTracker tracker;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
				
		tracker = new GPSTracker(getApplicationContext());
		setContentView(R.layout.activity_main);
	
				
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
		
		
		RetreiveFeedTask async = new RetreiveFeedTask(jsonQuery);
		String jsonString = async.doInBackground(null);
		
		//JsonDirectionParser parser = new JsonDirectionParser();
		
			
		System.out.println("blob >"+jsonString);
		
		 
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
