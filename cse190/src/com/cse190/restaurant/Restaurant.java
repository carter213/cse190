package com.cse190.restaurant;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Restaurant {
	private int rest_id;
	private String name;
	private String address;
	private String phone;
	private String state;
	private String website;
	private String city;
	private int zip;
	private float distance;
	private double latitude;
	private double longitude;
	
	//from android
	public Restaurant(int inid, String inname, String inaddress, String incity, String instate, int inzip, String inphone, String inwebsite, double inlongitude, double inlatitude, float indistance)
	{
		rest_id = inid;
		name = inname;
		address = inaddress;
		zip = inzip;
		city = incity;
		state = instate;
		phone = inphone;
		website = inwebsite;
		latitude = inlatitude;
		longitude = inlongitude;
		distance = indistance;
	}
	
	public Restaurant(String inname, String inaddress, String inphone, String inwebsite, String incity, String instate, String inzip)
	{
		String key = inaddress;
		
		String newkey = key.replace(" " , "+");

		try{
			URL google = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="+ newkey +"&sensor=false");
			
	        URLConnection yc = google.openConnection();
	        
	
	        BufferedReader in = new BufferedReader(
	                                new InputStreamReader(
	                                yc.getInputStream()));
	        String inputLine;
	        String app ="";
	        while ((inputLine = in.readLine()) != null) 
	        {
	        	app = app + inputLine;
	
	        }
	        in.close();
	
	        //**********
	        JsonParser parser = new JsonParser();
		    
		    JsonObject obj = (JsonObject) parser.parse(app);
		    JsonArray results = (JsonArray) obj.get("results");
		    JsonObject res = (JsonObject) results.get(0);
		    
		    String foradd = ((JsonObject) res.get("formatted_address")).getAsString();
		    
		    String addcom[] = foradd.split(",");
		    
		    name = inname;
		    address = addcom[0];
		    city = addcom[1];
		    
		    String ac[] = addcom[2].split(" ");
		    state = ac[0];
		    zip =  Integer.parseInt(ac[1]);
		    phone = inphone;
		    website = inwebsite;
		    
		 
		    JsonObject geometry = (JsonObject) res.get("geometry");
		    JsonObject location = (JsonObject) geometry.get("location");
		    latitude = location.get("lat").getAsDouble();
		    longitude = location.get("lng").getAsDouble();
		}
		catch (Exception e)
		{
			latitude = 0;
			longitude = 0;
		}
	}
	
	public JsonObject getJson()
	{
      	JsonObject obj = new JsonObject();
    	obj.addProperty("rest_id", rest_id);
    	obj.addProperty("name", name);
    	obj.addProperty("address", address);
    	obj.addProperty("city", city);
    	obj.addProperty("state", state);
    	obj.addProperty("zip", zip);
    	obj.addProperty("latitude", latitude);
    	obj.addProperty("longitude", longitude);
    	obj.addProperty("phone", phone);
    	obj.addProperty("website", website);
    	obj.addProperty("distance", distance);	
    	return obj;
	}
	
	public int compare(Restaurant r)
	{
		if (this.distance < r.distance)
			return 1;
		else
			return 0;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getAddress()
	{
		return address;
	}
	
	public String getPhone()
	{
		return phone;
	}

	public String getCity()
	{
		return city;
	}
	
	public String getState()
	{
		return state;
	}
	
	public String getWebsite()
	{
		return website;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
	
	public int getZip()
	{
		return zip;
	}
}
