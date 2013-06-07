package com.cse190.vote;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.sun.jmx.snmp.Timestamp;

/**
 * Servlet implementation class createVote
 * Adds a vote to the database or updates the currently existing vote/comment
 */
public class createVote extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createVote() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String food_id = request.getParameter("food_id");	//Food ID
		String user_id = request.getParameter("user_id");	//User ID
		String rest_id = request.getParameter("rest_id");	//Rest ID
		String comment = request.getParameter("comment");	//Comment
		JsonObject js = new JsonObject();
		
		if( food_id == null || user_id == null)
		{
			js.addProperty("result", false);
			js.addProperty("message", "Missing parameter");
			out.println(js.toString());
			return;
		}
	
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			//If no results are returned, then the user has no vote in this restaurant
			String sql = "SELECT vote_id, food_id FROM vote WHERE user_id = ? AND food_id in " +
					"(SELECT food_id from food WHERE rest_id = ?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, user_id);
			stmt.setString(2, rest_id);
			ResultSet rs = stmt.executeQuery();
			
			if(!rs.next())
			{
				//If no vote exists from this user, add it with comment
				sql = "INSERT INTO vote (user_id, food_id, comment, time) VALUES (?, ?, ?, NOW())";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, user_id);
				stmt.setString(2, food_id);
				stmt.setString(3, comment);
				stmt.executeUpdate();
				js.addProperty("result", true);
			}
			else
			{
				//Update the food_id and comment if their vote is going to be overwritten
				sql = "UPDATE vote SET comment = ?, food_id = ? , time = NOW() WHERE vote_id = ?";
				int prev_food_id = rs.getInt("food_id");
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, comment);
				stmt.setString(2, food_id);
				stmt.setString(3, rs.getString("vote_id"));
				stmt.executeUpdate();
				
				//Decrement prev food vote 
				sql = "UPDATE food SET vote=(vote-1) WHERE food_id = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, prev_food_id);
				stmt.executeUpdate();
				js.addProperty("result", true);
			}
			
			//Increment new food vote
			sql = "UPDATE food SET vote=(vote+1) WHERE food_id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, food_id);
			stmt.executeUpdate();
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
