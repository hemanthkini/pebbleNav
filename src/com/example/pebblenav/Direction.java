package com.example.pebblenav;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

public class Direction {

	public String instructions;

	//Possibly null
	public String maneuver;


	public String distancetext;
	public double distanceval;

	public double startlat;
	public double startlong;
	public double endlat;
	public double endlong;

	public Direction(JSONObject s)
	{
		try{
			instructions = s.getString("html_instructions");
			distanceval = s.getJSONObject("distance").getDouble("value");
			distancetext = s.getJSONObject("distance").getString("text");

			String man="";
			try{
				man = s.getString("maneuver");
			}
			catch(Exception ex){}
			maneuver=man;
			startlat=s.getJSONObject("start_location").getDouble("lat");
			startlong=s.getJSONObject("start_location").getDouble("lng");
			endlat=s.getJSONObject("end_location").getDouble("lat");
			endlong=s.getJSONObject("end_location").getDouble("lng");


			Pattern P = Pattern.compile("(.*)(<div[^<|>]*>)(.*)");
			Matcher M = P.matcher(instructions);

			while(M.matches())
			{
				instructions=M.group(1)+"NEWLINE"+M.group(3);
				M=P.matcher(instructions);
			}

			Pattern P2 = Pattern.compile("(.*)(<[^<|>]*>)(.*)");
			Matcher M2 = P2.matcher(instructions);

			while(M2.matches())
			{
				instructions=M2.group(1)+M2.group(3);
				M2=P2.matcher(instructions);
			}
			instructions = instructions.replaceAll("NEWLINE", "\n");
		}
		catch(Exception ex)
		{
			System.out.println(ex.getStackTrace());
		}
	}

	public String toString()
	{
		return instructions;
	}
}
