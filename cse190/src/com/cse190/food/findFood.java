package com.cse190.food;

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
 * Servlet implementation class findFood
 * Returns food from the database for a certain restaurant and min-max interval
 */
public class findFood extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public findFood() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Queries the database to get the foods from rest_id that are within the specified interval
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		int rest_id;
		String rest_id_result = request.getParameter("rest_id"); //Restaurant ID
		String min = request.getParameter("min");				 //Minimum result #
		String max = request.getParameter("max");				 //Maximum result #
		
		JsonObject js = new JsonObject();
		
		if (rest_id_result == null || min == null || max == null)
		{
			js.addProperty("result", false);
			js.addProperty("message", "Missing parameter");
			out.println(js.toString());
			return;
		}
		else {
			rest_id = Integer.parseInt(rest_id_result);
		}
		
		PreparedStatement stmt = null;
		Connection conn = null;
		try{
			String sql = "SELECT COUNT(*) AS total FROM food WHERE rest_id = ?";
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, rest_id);
			ResultSet rs1 = stmt.executeQuery();
			
			if(rs1.next())
			{
				js.addProperty("total", rs1.getInt("total"));
			}
			
			sql = "SELECT * FROM food WHERE rest_id = ?";
			//show best 3 food in the restaurant
			if (min != null && max != null)
			{
				sql += " ORDER BY vote DESC LIMIT ?, ?";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, rest_id);
				stmt.setInt(2, Integer.parseInt(min));
				stmt.setInt(3, Integer.parseInt(max) - Integer.parseInt(min) + 1);
			}	
			else
			{
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, rest_id);
			}
			ResultSet rs = stmt.executeQuery();
				
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
