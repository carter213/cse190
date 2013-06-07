package com.cse190.restaurant;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/**
 * Servlet implementation class findRestaurants
 * Finds the restaurants closest to the user and in the interval min and max
 */
public class findRestaurants extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
	//private static org.apache.log4j.Logger log = Logger.getLogger(findRestaurants.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public findRestaurants() {
        super();
        // TODO Auto-generated constructor stub
    }  
    
    public float calcDistance(double latA, double longA, double latB, double longB) {

        double theDistance = (Math.sin(Math.toRadians(latA)) *
                Math.sin(Math.toRadians(latB)) +
                Math.cos(Math.toRadians(latA)) *
                Math.cos(Math.toRadians(latB)) *
                Math.cos(Math.toRadians(longA - longB)));

        return new Double((Math.toDegrees(Math.acos(theDistance))) * 69.09).floatValue();
    }
    
	/**
	 * Queries DB for restaurants inside the interval that are closest to the user
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String key = request.getParameter("key");
		String inlatitude = request.getParameter("latitude");
		String inlongitude = request.getParameter("longitude");
		String inzip = request.getParameter("zip");
		String incity = request.getParameter("city");
		String radius = request.getParameter("radius");
		String min = request.getParameter("min");
		String max = request.getParameter("max");
		
		if (min==null || max==null) {
			JsonObject js = new JsonObject();
			js.addProperty("result", false);
			js.addProperty("message", "Missing parameter");
			out.println(js.toString());
			return;
		}
		
		float searchSize = 0.5f;
		
		if (key == null)
			key = "";
		
		String sql = "SELECT *, (SELECT SUM(f.vote) FROM cse190.food f WHERE f.rest_id=r.rest_id) AS votes FROM cse190.restaurant r WHERE";
		//adding zip on the query
		
		/* Construct the SQL query */
		if (inzip != null)
		{
			sql += " zip = ? AND";
		}
		
		if (incity != null)
		{
			sql += " city = ? AND";
		}
		
		if (inlatitude != null)
		{
			sql += " latitude >= ? AND latitude <= ? AND";
		}
		
		if (inlongitude != null)
		{
			sql += " longitude >= ? AND longitude <= ? AND";
		}
		float rad = 1;
		if (radius != null)
		{
			rad = Float.parseFloat(radius);
			searchSize = rad * 0.05f;
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			//logging
			//log.debug("SQL connection establish");
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			sql += " name LIKE ?";
			stmt = conn.prepareStatement(sql);
			int index = 1;
			if (inzip != null)
			{
				stmt.setString(index, inzip);
				index++;
			}
			
			if (incity != null)
			{
				stmt.setString(index, incity);
				index++;
			}
			
			if (inlatitude != null)
			{
				stmt.setDouble(index, (Double.parseDouble(inlatitude) - searchSize));
				index++;
				stmt.setDouble(index, (Double.parseDouble(inlatitude) + searchSize));
				index++;
			}
			
			if (inlongitude != null)
			{
				stmt.setDouble(index, (Double.parseDouble(inlongitude) - searchSize));
				index++;
				stmt.setDouble(index, (Double.parseDouble(inlongitude) + searchSize));
				index++;
			}			
			stmt.setString(index, "%"+key+"%");
			ResultSet rs = stmt.executeQuery();
				
			JsonObject obj = new JsonObject();			
			ArrayList<Restaurant> rest = new ArrayList<Restaurant>();
			
			while (rs.next()) {
				int id = rs.getInt("rest_id");
	            String name = rs.getString("name");
	            String address = rs.getString("address");
	            String phone = rs.getString("phone");
	            double longitude = rs.getDouble("longitude");
	            double latitude = rs.getDouble("latitude");
	            String website = rs.getString("website");
	            String city = rs.getString("city");
	            String state = rs.getString("state");
	            int zip = rs.getInt("zip");
	            int votes;
	            try {
	            	votes = rs.getInt("votes");
	            } catch (Exception e) { votes = 0; }

	            if (inlatitude!=null && inlongitude!=null)
	            {
		            float distance = calcDistance(Double.parseDouble(inlatitude), Double.parseDouble(inlongitude), latitude, longitude);
	            	if (distance <= rad) {
		            	rest.add(new Restaurant(id, name, address, city, state, zip, phone, website, latitude, longitude, distance, votes));
	            	}
	            }
	            else {
	            	rest.add(new Restaurant(id, name, address, city, state, zip, phone, website, latitude, longitude, -1, votes));
	            }
	        }

			//log.debug("Get all data");
			
			Collections.sort(rest, new Comparator<Restaurant>(){
				public int compare(Restaurant r1, Restaurant r2) {
					return r2.compare(r1);
				}
			});
			//log.debug("Sort complete");
			obj.addProperty("total", rest.size());
			JsonArray jsarr = new JsonArray();

			int maxr = Integer.parseInt(max);
			int minr = Integer.parseInt(min);
			//log.debug("print data");
			// Check if getting this min -> max interval will go outside of index
			if (minr >= rest.size()) {
				obj.add("result", jsarr);
				out.println(obj.toString());
				return;
			}
			else if (maxr >= rest.size()) {
				// If the max goes outside the array, just return up until the end of the array
				maxr = rest.size()-1;
			}
			
			for(int i = minr; i <= maxr; i++)
			{
				jsarr.add(rest.get(i).getJson());
			}
			obj.add("result", jsarr);
			out.println(obj.toString());

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
