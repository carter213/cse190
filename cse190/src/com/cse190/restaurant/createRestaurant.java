package com.cse190.restaurant;

import com.cse190.helper.GeoLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class createRestaurant
 */
public class createRestaurant extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "cse190";
	static String PASS = "yelp190";
	
	public GeoLocation getCoordinate(String add) //throws Exception
	{
		String newadd = add.replace(" " , "+");

		try{
			URL google = new URL("http://maps.googleapis.com/maps/api/geocode/json?address="+ newadd +"&sensor=false");
			
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
		    JsonObject geometry = (JsonObject) res.get("geometry");
		    
		    JsonObject location = (JsonObject) geometry.get("location");
		    double lat = location.get("lat").getAsDouble();
		    double lng = location.get("lng").getAsDouble();
		    
	        return new GeoLocation(lat, lng);
		}
		catch (Exception e)
		{
			return new GeoLocation(0, 0);
		}
	}
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createRestaurant() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String name = request.getParameter("name");
		String address = request.getParameter("address");
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		String zip = request.getParameter("zip");
		String phone = request.getParameter("phone");
		String website = request.getParameter("website");

		//Can be done on android
		//So the android sent all check data to the servlet
		GeoLocation loc = getCoordinate(address + city + state);
		double latitude = loc.getLatitude();
		double longitude = loc.getLongitude();
		//*******
	
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			String sql = "SELECT * FROM restaurant WHERE name = ? AND phone = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setString(2, phone);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if(rs.getRow() == 0)
			{
				sql = "INSERT INTO restaurant (name, address, city, state, zip, latitude, longitude, phone, website) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, name);
				stmt.setString(2, address);
				stmt.setString(3, city);
				stmt.setString(4, state);
				stmt.setString(5, zip);
				stmt.setDouble(6, latitude);
				stmt.setDouble(7, longitude);
				stmt.setString(8, phone);
				stmt.setString(9, website);

				stmt.executeUpdate();
				out.println("Success");
			}
			else
			{
				out.println("Error, duplicate found");
			}
			

			//STEP 6: Clean-up environment
			stmt.close();
			conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
