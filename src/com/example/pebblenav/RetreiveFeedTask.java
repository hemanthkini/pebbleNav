package com.example.pebblenav;

import android.os.*;
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


public class RetreiveFeedTask extends AsyncTask<String, String, String> {

	private MainActivity M;
	
	public RetreiveFeedTask(MainActivity m)
	{
		super();
		this.M=m;
	}
	
	
    protected String doInBackground(String... urls) {
        try {
        	

        	URL link = null;    		
    		link = new URL(urls[0]);
    		URLConnection connection = link.openConnection();
    		
    		String jsonString = "";
    		String inputLine;
    		
    		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    		
    		System.out.println("banana");
    		
    		while((inputLine = reader.readLine()) != null) {
    			jsonString += inputLine;
    		}
    		
    		System.out.println("apple");
    		
    		reader.close();
        	
    		return jsonString;
        	
        } catch (Exception e) {
            e.printStackTrace();
            return "io fail";
        }
    }
    
    protected void onPostExecute(String result){
    	System.out.println("Post Execute");
    	JsonDirectionParser parser = new JsonDirectionParser();
		try {
			ArrayList<Direction> temp = parser.parse(result);
			M.parse(temp);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	
    }


}