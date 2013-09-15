package com.example.pebblenav;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity  implements Runnable{
	public static GPSTracker tracker;
	public static double longitude;
	public static double latitude;
	public static final int refreshRate = 5;
	public ArrayList<Direction> directions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//PebbleInterface.sendDataToPebble(getApplicationContext(), "sup nigga", "hey", 0, 1);
		tracker = new GPSTracker(getApplicationContext());
		
		setup();
				
	}
	
	public void setup(){
		
		ScheduledThreadPoolExecutor sched = new ScheduledThreadPoolExecutor(2);
		sched.scheduleAtFixedRate(this, 0, refreshRate , TimeUnit.SECONDS);

		tracker = new GPSTracker(getApplicationContext());
		
		setContentView(R.layout.activity_main);

		//PebbleInterface.sendDataToPebble(getApplicationContext(), "sup a", "yo bitches", 0, 3);
		//PebbleInterface.buzzPebble(getApplicationContext());
		//PebbleInterface.buzzPebble(getApplicationContext());
		//PebbleInterface.buzzPebble(getApplicationContext());
				
		Typeface tf = Typeface.createFromAsset(getAssets(), "font.ttf");
		TextView title = (TextView)findViewById(R.id.title);
		title.setTypeface(tf);
		
	}
	
	public void recieveNewCoord(double longitude, double latitude){
		try{
			System.out.println("a");
			System.out.println(coordToFt(latitude,longitude,directions.get(0).endlat,directions.get(0).endlong));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void getLocData(View v) throws IOException, JSONException, InterruptedException, ExecutionException{
		
		
		longitude = tracker.getLongitude();
		latitude = tracker.getLatitude();
		
		//System.out.println("longitude: "+longitude+"\n"+"latitude: "+latitude);
		
		
		EditText textField = (EditText)findViewById(R.id.enterAddress);
		String destAddress = textField.getText().toString().replaceAll(" ", "%20");
		
		String jsonQuery="https://maps.googleapis.com/maps/api/directions/json?mode=walking&sensor=true";
		jsonQuery += "&origin=" + latitude + "," + longitude;
		jsonQuery += "&destination=" + destAddress;
		
		AsyncTask<String, String, String> task = new RetreiveFeedTask(this).execute(jsonQuery);
		
		
		
	}
	
	public void parse(ArrayList<Direction> directions) throws JSONException{
		this.directions = directions;
		
		
	}
	
	public int coordToFt(double x1, double y1, double x2, double y2){
		System.out.println("b");
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
		//System.out.println("D:"+directions);
		if(directions!=null && directions.size()>0)
		{
			System.out.println("in");
			recieveNewCoord(tracker.getLongitude(),tracker.getLatitude());
		}
	}

}
