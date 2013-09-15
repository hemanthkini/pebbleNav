package com.example.pebblenav;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
		

		setup();

	}

	public void setup(){

		ScheduledThreadPoolExecutor sched = new ScheduledThreadPoolExecutor(2);
		sched.scheduleAtFixedRate(this, 0, refreshRate , TimeUnit.SECONDS);

		tracker = new GPSTracker(getApplicationContext());

		setContentView(R.layout.activity_main);

		PebbleInterface.sendDataToPebble(getApplicationContext(), "sup a", "yo bitches", 0, 3);
		//PebbleInterface.buzzPebble(getApplicationContext());
		//PebbleInterface.buzzPebble(getApplicationContext());
		//PebbleInterface.buzzPebble(getApplicationContext());

		Typeface tf = Typeface.createFromAsset(getAssets(), "font.ttf");
		TextView title = (TextView)findViewById(R.id.title);
		title.setTypeface(tf);

	}

	public void recieveNewCoord(double latitude, double longitude){
		try{
			System.out.println("a");
			final double dist = coordToFt(latitude,longitude,directions.get(0).endlat,directions.get(0).endlong);

			runOnUiThread(new Runnable(){

				public void run(){
					final TextView distPrinted = (TextView)findViewById(R.id.printedDistToScreen);
					distPrinted.setText("Distance to next: "+dist+"");

				}

			});

			System.out.println("Distance:"+dist);
			if(dist<25)
			{
				directions.remove(0);
				if(directions.size()==0)
				{
					throw new Exception("Destroy Universe"); 
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public void getLocData(View v) throws IOException, JSONException, InterruptedException, ExecutionException{

		tracker.getLocation();
		longitude = tracker.getLongitude();
		latitude = tracker.getLatitude();

		//System.out.println("longitude: "+longitude+"\n"+"latitude: "+latitude);


		EditText textField = (EditText)findViewById(R.id.enterAddress);
		String destAddress = textField.getText().toString().replaceAll(" ", "%20");

		String jsonQuery="https://maps.googleapis.com/maps/api/directions/json?mode=walking&sensor=true";
		jsonQuery += "&origin=" + latitude + "," + longitude;
		jsonQuery += "&destination=" + destAddress;

		new RetreiveFeedTask(this).execute(jsonQuery);



	}

	public void parse(ArrayList<Direction> directions) throws JSONException{
		this.directions = directions;


	}

	public double coordToFt(double x1, double y1, double x2, double y2){
		System.out.println("myloc("+x1+","+y1+")");
		System.out.println("destloc("+x2+","+y2+")");

		double a2 = Math.pow(x1-x2, 2);
		System.out.println(a2);
		double b2 = Math.pow(y1-y2, 2);
		System.out.println(b2);
		double c = Math.sqrt(a2+b2);
		System.out.println(c);
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

		if(directions!=null && directions.size()>0)
		{
			runOnUiThread(new Runnable(){

				public void run(){
					TextView displayDir = (TextView)findViewById(R.id.printedDirToScreen);
					String displaytext;
					if(!directions.get(0).maneuver.equals(""))
					{
						displaytext = directions.get(0).maneuver;
					}
					else
					{
						displaytext = directions.get(0).toString();
					}
					displayDir.setText(directions.get(0).toString());
					Log.d("displaytext", displaytext);
					displaytext = displaytext.substring(0,30);
					PebbleInterface.sendTurnToPebble(getApplicationContext(), displaytext, 0);
					
				}

			});
			System.out.println("D:"+directions.get(0));



			System.out.println("in");
			tracker.getLocation();
			recieveNewCoord(tracker.getLatitude(),tracker.getLongitude());
		}
	}

}
