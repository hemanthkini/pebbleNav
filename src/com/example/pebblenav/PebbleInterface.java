package com.example.pebblenav;

import android.content.Context;

import com.example.pebblenav.util.PebbleDictionary;

public class PebbleInterface {

	private final static int STREET_DATA_KEY = 1;
	
	public void sendDataToPebble (Context c, String stringVal) {
		PebbleDictionary data = new PebbleDictionary();
		data.addString(STREET_DATA_KEY, stringVal);
		PebbleKit.sendDataToPebble(c, Constants.selfUUID, data);
	}

}


