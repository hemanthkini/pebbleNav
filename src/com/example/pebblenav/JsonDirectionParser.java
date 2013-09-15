package com.example.pebblenav;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
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

		JSONObject root = new JSONObject(json);
		JSONArray routes = root.getJSONArray("routes");
		if(routes.length()>0)
		{
			JSONObject route = routes.getJSONObject(0);
			JSONArray legs = route.getJSONArray("legs");
			int count = legs.length();
			if(count>0)
			{
				JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
				for(int i =0; i<steps.length();i++)
				{
					JSONObject s = steps.getJSONObject(i);

					Direction D = new Direction(steps.getJSONObject(i));
					parsedDirections.add(D);
				}
			}
			
		}
		
		return parsedDirections;
	}
	
	
}
