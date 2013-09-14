package com.example.pebblenav;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
		JSONArray steps = jobject.getJSONArray("steps");
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
		}
		
		return null;
	}
	
	
}
