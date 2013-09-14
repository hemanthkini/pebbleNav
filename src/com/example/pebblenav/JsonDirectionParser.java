package com.example.pebblenav;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonDirectionParser {
	
	String distancestring = "\"distance\" :";
	String instructionstring = "\"html_instructions\" :";
	
	public JsonDirectionParser()
	{
	}
	
	public ArrayList<Direction> parse(String json) throws JSONException
	{
		ArrayList<Direction> parsedDirections = new ArrayList<Direction>();

		JSONObject jobject = new JSONObject(json);
		Iterator iter = jobject.keys();
		while(iter.hasNext())
			Log.d("JSON",iter.next().toString());
		
		/*JSONArray steps = jobject.getJSONArray("steps");
		for (int i=0; i < steps.length(); i++)
		{
		    try {
		        JSONObject oneObject = steps.getJSONObject(i);
		        // Pulling items from the array
		        String distance = oneObject.getString("distance");
		        String instructions = oneObject.getString("html_instructions");
		        String maneuver = oneObject.getString("maneuver");
		        Direction D = new Direction(distance,instructions,maneuver);
		        parsedDirections.add(D);
		    } catch (JSONException e) {
		        // Oops
		    }
		}*/
		
		return parsedDirections;
	}
	
	
}
