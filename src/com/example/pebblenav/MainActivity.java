package com.example.pebblenav;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;

import com.example.pebblenav.util.DistanceUtils;
import com.example.pebblenav.util.vec2;

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
	public static final int NEXTDIRECTIONLIMIT = 25;

	public ArrayList<Direction> directions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//PebbleInterface.sendDataToPebble(getApplicationContext(), "sup nigga", "hey", 0, 1);


		setup();

	}

	public void setup(){



		//Log.d("dist",distance(40.446833,-79.955964,40.447176,-79.957423,'K')*1000+"");

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

	public void recieveNewCoord(double latitude, double longitude){
		try{
			System.out.println("a");
			final double dist = DistanceUtils.distance(latitude,longitude,directions.get(0).endlat,directions.get(0).endlong,'K')*1000;

			runOnUiThread(new Runnable(){

				public void run(){
					final TextView distPrinted = (TextView)findViewById(R.id.printedDistToScreen);
					distPrinted.setText("Distance to next: "+dist+"");
				}

			});

			System.out.println("Distance:"+dist);
			if(dist<NEXTDIRECTIONLIMIT)
			{
				directions.remove(0);
				if(directions.size()==0)
				{
					directions = null;
					return;
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

					Direction theDir = directions.get(0);
					vec2 A = new vec2(theDir.startlat,theDir.startlong);
					vec2 B = new vec2(theDir.endlat,theDir.endlong);
					vec2 C = new vec2(tracker.getLatitude(), tracker.getLongitude());

					double minDist = DistanceUtils.minimum_distance(A,B,C);

					((TextView)(findViewById(R.id.distOffPath))).setText(minDist+" off path");

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

					int turn =3;
					if(displaytext.contains("left"))
						turn = 1;
					else if(displaytext.contains("right"))
						turn = 2;
					if(displaytext.length()>40)
						displaytext=displaytext.substring(0,40);
/*					final int linelength = 15;
					
					while(displaytext.length()>linelength-1)
					{
						String textpiece = displaytext.substring(0,linelength-1);
						displaytext = displaytext.substring(linelength-1);
						
						PebbleInterface.sendTurnToPebble(getApplicationContext(), textpiece+(textpiece.charAt(linelength-1)==' '?"-":" "), turn);
					}
					if(displaytext.length()>0)
						PebbleInterface.sendTurnToPebble(getApplicationContext(), displaytext, turn);
*/
					PebbleInterface.sendTurnToPebble(getApplicationContext(), displaytext, turn);

					
					


				}

			});
			System.out.println("D:"+directions.get(0));



			System.out.println("in");
			tracker.getLocation();
			recieveNewCoord(tracker.getLatitude(),tracker.getLongitude());
		}
	}

}
