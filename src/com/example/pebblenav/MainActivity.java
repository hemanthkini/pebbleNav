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
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity  implements Runnable{
	public static GPSTracker tracker;
	public static double longitude;
	public static double latitude;
	public static final int refreshRate = 5;
	public static final int NEXTDIRECTIONLIMIT = 25;
	public static final double OFFPATHLIMIT = 100;

	public int awaycounter = 0;
	public double prevdist = 0;
	
	public String jsonQuery;
	
	public ArrayList<Direction> directions;

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
		EditText edit = (EditText)findViewById(R.id.enterAddress);
		edit.setTypeface(tf);
		title.setTypeface(tf);
		
		((EditText)(findViewById(R.id.enterAddress))).setOnClickListener(new View.OnClickListener() {
		  public void onClick(View v) {
			  if(!(((EditText)v).getText().equals("")))
		          ((EditText)v).setText(" ");
			  
			  
		  }
	});
		
		Button gobutton = ((Button)findViewById(R.id.go));
		gobutton.setOnClickListener(new View.OnClickListener() {
		  public void onClick(View v) {
			  
			  InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			  
			  
		  }});

	}

	public void recieveNewCoord(double latitude, double longitude){
		try{
			System.out.println("a");
			final double dist = DistanceUtils.distance(latitude,longitude,directions.get(0).endlat,directions.get(0).endlong,'K')*1000;

			
			if(dist>prevdist)
				awaycounter++;
			else
				awaycounter--;
			
			if(awaycounter>5)
			{
				new RetreiveFeedTask(this).execute(jsonQuery);
				System.out.println("redoing");
			}
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


		EditText textField = (EditText)findViewById(R.id.enterAddress);
		String destAddress = textField.getText().toString().replaceAll(" ", "%20");

		jsonQuery="https://maps.googleapis.com/maps/api/directions/json?mode=walking&sensor=true";
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
					
					PebbleInterface.sendTurnImageToPebble(getApplicationContext(),turn);
										
					if(displaytext.length()>14)
					{	
						PebbleInterface.sendString1ToPebble(getApplicationContext(), displaytext.substring(0,14)+(displaytext.charAt(14)==' '?"-":" "));
						displaytext=displaytext.substring(14);
						if(displaytext.length()>14)
							PebbleInterface.sendString2ToPebble(getApplicationContext(), displaytext.substring(0,14));
						else
							PebbleInterface.sendString2ToPebble(getApplicationContext(), displaytext);
					}
					else
						PebbleInterface.sendString1ToPebble(getApplicationContext(), displaytext);

				}

			});
			System.out.println("D:"+directions.get(0));

			System.out.println("in");
			Location L = tracker.getLocation();
			System.out.println(L);
			recieveNewCoord(L.getLatitude(),L.getLongitude());
		}
	}
	
	
}
