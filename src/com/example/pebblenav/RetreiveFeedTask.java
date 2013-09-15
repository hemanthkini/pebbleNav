package com.example.pebblenav;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONException;

import android.os.AsyncTask;


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