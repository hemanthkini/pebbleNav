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


public class RetreiveFeedTask extends AsyncTask<String, String, String> {

	String jsonURL;
	
	RetreiveFeedTask(String jsonUrl){
		jsonURL = jsonUrl;
	}
	

    protected String doInBackground(String... urls) {
        try {
        	
        	URL link = null;
    		
    		link = new URL(jsonURL);
    		
    		URLConnection connection = link.openConnection();
    		
    		
    		String jsonString = "";
    		String inputLine;
    		
    		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    		
    		while((inputLine = reader.readLine()) != null) {
    			jsonString += inputLine;
    		}
    		
    		reader.close();
        	
    		return jsonString;
        	
        } catch (Exception e) {
            return "IO failed";
        }
    }


}