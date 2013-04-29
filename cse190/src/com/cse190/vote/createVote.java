package com.cse190.vote;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class createVote
 */
public class createVote extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "cse190";
	static String PASS = "yelp190";
       
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
		
		String food_id = request.getParameter("food_id");
		String user_id = request.getParameter("user_id");
		String rest_id = request.getParameter("rest_id");
		String comment = request.getParameter("comment");
		
		if( food_id == null || user_id == null)
		{
			out.println("Error, parameter missing");
			return;
		}
	
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
			
			String sql;
			//If no results are returned, then the user has no vote in this restaurant
			sql = "SELECT vote_id, food_id FROM vote WHERE user_id ='" + user_id + "' AND food_id in " +
						"(SELECT food_id from food WHERE rest_id ='" + rest_id + ")";
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			if(rs.getRow() == 0)
			{
				//If no vote exists from this user, add it with comment
				sql = "INSERT INTO vote (user_id, food_id, comment) VALUES ('";
				sql = sql + user_id + "','" + food_id + "','" + comment + "')";
				stmt.executeUpdate(sql);
				out.println("Success");
			}
			else
			{
				//Update the food_id and comment if their vote is going to be overwritten
				sql = "UPDATE vote SET comment ='" + comment + "', food_id='" + food_id + 
						"' WHERE vote_id ="+ rs.getInt("vote_id");
				 int prev_food_id = rs.getInt("food_id");
				stmt.executeUpdate(sql);
				
				//Decrement prev food vote 
				sql = "UPDATE food SET vote=(vote-1) WHERE food_id='" + prev_food_id + "'";
				stmt.executeUpdate(sql);
				out.println("Success");
			}
			
			//Increment new food vote
			sql = "UPDATE food SET vote=(vote+1) WHERE food_id='" + food_id + "'";
			stmt.executeUpdate(sql);			

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
