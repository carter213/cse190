package com.cse190.food;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class findFood
 */
public class findFood extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "cse190";
	static String PASS = "yelp190";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public findFood() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String alg = request.getParameter("alg");
		String rest_id = request.getParameter("rest_id");

		
		if (rest_id == null)
		{
			out.println("Error, parameter not complete");
			return;
		}
		
		
		String sql;
		sql = "SELECT * FROM food WHERE rest_id =" + rest_id;
		
		//show best 3 food in the restaurant
		if (alg != null)
		{
			sql = sql + " ORDER BY vote DESC LIMIT 0, 3";
		}	
		
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
				
			JsonArray jsarr = new JsonArray();
			
			while (rs.next()) {
				int food_id = rs.getInt("food_id");
	            String name = rs.getString("name");
	            String description = rs.getString("description");
	            int vote = rs.getInt("vote");

	            JsonObject obj = new JsonObject();
	            obj.addProperty("food_id", food_id);
	            obj.addProperty("name", name);
	            obj.addProperty("vote", vote);
	            obj.addProperty("description", description);
	            	
	            jsarr.add(obj);	            	
	            
	        }

			out.println(jsarr.toString());

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
