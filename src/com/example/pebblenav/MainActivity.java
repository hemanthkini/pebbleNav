package com.example.pebblenav;

import android.os.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import android.location.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import org.json.*;


public class MainActivity extends Activity implements Runnable{
	public static GPSTracker tracker;
	public static double longitude;
	public static double latitude;
	public static final int refreshRate = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setup();
				
	}
	
	public void setup(){
		
		ScheduledThreadPoolExecutor sched = new ScheduledThreadPoolExecutor(2);
		sched.scheduleAtFixedRate(this, 0, refreshRate , TimeUnit.SECONDS);

		tracker = new GPSTracker(getApplicationContext());
		
		setContentView(R.layout.activity_main);
		Typeface tf = Typeface.createFromAsset(getAssets(), "font.ttf");
		TextView title = (TextView)findViewById(R.id.title);
		title.setTypeface(tf);
		
	}
	
	public void recieveNewCoord(double longitude, double latitude){
		
		
		
	}
	
	public void getLocData(View v) throws IOException, JSONException{
		
		
		longitude = tracker.getLongitude();
		latitude = tracker.getLatitude();
		
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
	
	public int coordToFt(double x1, double y1, double x2, double y2){
		
		double a2 = Math.pow(x1-x2, 2);
		double b2 = Math.pow(y1-y2, 2);
		int c = (int)Math.sqrt(a2+b2);
		
		//364320 is number of feet in a unit of longitude/latitude
		return 364320 * c;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void run() {
		
		recieveNewCoord(tracker.getLongitude(),tracker.getLatitude());
		
	}

}
