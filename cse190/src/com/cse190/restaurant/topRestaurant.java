package com.cse190.restaurant;

import java.io.IOException;
import java.io.PrintWriter;
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

/**
 * Servlet implementation class topRestaurant
 * Outputs the top 5 restaurants in the database
 */
public class topRestaurant extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public topRestaurant() {
        super();
    }

	/**
	 * Queries DB for top 5 restaurants and outputs all information
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		JsonObject js = new JsonObject();
		
		String sql;
		sql = "SELECT r.state AS state, r.zip AS zip, r.address AS address, r.city AS city, r.website AS website, r.phone AS phone, r.latitude AS latitude, r.longitude AS longitude, r.rest_id AS rest_id, r.name AS rest_name, (SELECT SUM(f.vote) from cse190.food f WHERE f.rest_id = r.rest_id) as count FROM cse190.food f, cse190.restaurant r WHERE f.rest_id=r.rest_id GROUP BY rest_id, rest_name, count ORDER BY count DESC LIMIT 5;";
		
		Statement stmt = null;
		Connection conn = null;
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
				
			JsonArray jsarr = new JsonArray();
			
			while (rs.next()) {
				int rest_id = rs.getInt("rest_id");
				String rest_name = rs.getString("rest_name");
	            int count = rs.getInt("count");
	            String address = rs.getString("address");
	            String phone = rs.getString("phone");
	            double longitude = rs.getDouble("longitude");
	            double latitude = rs.getDouble("latitude");
	            String website = rs.getString("website");
	            String city = rs.getString("city");
	            String state = rs.getString("state");
	            int zip = rs.getInt("zip");

	            JsonObject obj = new JsonObject();
	            
	            obj.addProperty("rest_id", rest_id);
	            obj.addProperty("rest_name", rest_name);
	            obj.addProperty("count", count);
	            obj.addProperty("address", address);
	            obj.addProperty("city", city);
	            obj.addProperty("website", website);
	            obj.addProperty("phone", phone);
	            obj.addProperty("latitude", latitude);
	            obj.addProperty("longitude", longitude);
	            obj.addProperty("state", state);
	            obj.addProperty("zip", zip);
	            	            	            	            
	            jsarr.add(obj);	            		            
	        }

			js.add("result", jsarr);
			out.println(js.toString());

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