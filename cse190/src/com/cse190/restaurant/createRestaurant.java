package com.cse190.restaurant;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class createRestaurant
 * Handles adding restaurants to the database from the user
 */
public class createRestaurant extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
       
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
		
		String name = request.getParameter("name");			//Restaurant name
		String address = request.getParameter("address");	//Restaurant Address
		String phone = request.getParameter("phone");		//Restaurant Phone #
		String website = request.getParameter("website");	//Restaurant Website
		
		//find location
		String city = request.getParameter("city");
		String state = request.getParameter("state");
		//OR
		String zip = request.getParameter("zip");

		//Can be done on android
		//So the android sent all check data to the servlet
		Restaurant rest = new Restaurant(name, address, phone, website, city, state, zip);
		//*******
	
		JsonObject obj = new JsonObject();
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
				//Website is not required
				if (website != null) {
					sql = "INSERT INTO restaurant (name, address, city, state, zip, latitude, longitude, phone, website) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, name);
					stmt.setString(2, rest.getAddress());
					stmt.setString(3, rest.getCity());
					stmt.setString(4, rest.getState());
					stmt.setInt(5, rest.getZip());
					stmt.setDouble(6, rest.getLatitude());
					stmt.setDouble(7, rest.getLongitude());
					stmt.setString(8, phone);
					stmt.setString(9, website);
				}
				else {
					sql = "INSERT INTO restaurant (name, address, city, state, zip, latitude, longitude, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, name);
					stmt.setString(2, rest.getAddress());
					stmt.setString(3, rest.getCity());
					stmt.setString(4, rest.getState());
					stmt.setInt(5, rest.getZip());
					stmt.setDouble(6, rest.getLatitude());
					stmt.setDouble(7, rest.getLongitude());
					stmt.setString(8, phone);
				}

				stmt.executeUpdate();
				obj.addProperty("result", true);
				obj.addProperty("message", "Restaurant register success");
			}
			else
			{
				obj.addProperty("result", false);
				obj.addProperty("message", "Duplicate restaurant");
			}
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
