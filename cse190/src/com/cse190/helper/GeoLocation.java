package com.cse190.helper;

public class GeoLocation {
	private static double longitude;
	private static double latitude;
	
	public GeoLocation(double lat, double lng)
	{
		latitude = lat;
		longitude = lng;
	}
	
	public double getLatitude()
	{
		return latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}
}
