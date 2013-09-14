package com.example.pebblenav;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.EditText;
import android.location.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		try {
			getLocData();
		} catch (IOException e) {System.out.println("BAD IO");}
		
	}
	
	public void getLocData() throws IOException{
		
		//System.out.println("YOLO");
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		
		EditText textField = (EditText)findViewById(R.id.enterAddress);
		String destAddress = textField.getText().toString();
		
		String jsonQuery="http://maps.googleapis.com/maps/api/directions/json?mode=walking";
		jsonQuery += "&origin=" + latitude + "," + longitude;
		jsonQuery += "&destination=" + destAddress;
		
		
		URL link = null;
		BufferedReader readURL = null;
		
		try {
			link = new URL(jsonQuery);
			readURL = new BufferedReader(new InputStreamReader(link.openStream()));
		} catch (Exception e) {
			System.out.println("Cannot load directions");
		}
		
		String jsonString = "";
		String inputLine;
		
		 while ((inputLine = readURL.readLine()) != null)
			 jsonString += inputLine;

		
		 
		  
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
