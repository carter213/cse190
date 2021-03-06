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
 * Servlet implementation class trendingFood
 * Returns the 5 most recently voted on foods from the database
 */
public class trendingFood extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public trendingFood() {
        super();
    }

	/**
	 * Queries DB for 5 most recently voted on foods (unique) and writes the JSON output
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		JsonObject js = new JsonObject();
		
		String sql;
		sql = "SELECT v2.food_id, f.description AS description, f.name AS food_name, f.vote, r.name AS rest_name, r.rest_id AS rest_id, (SELECT COUNT(*) from cse190.vote v WHERE v.comment!=\"\" AND v.food_id=f.food_id) as comments FROM cse190.food f, cse190.restaurant r, (SELECT DISTINCT(v.food_id) FROM (SELECT * FROM vote ORDER BY time DESC) v) v2 WHERE f.rest_id=r.rest_id AND f.food_id=v2.food_id LIMIT 5;";
		
		Statement stmt = null;
		Connection conn = null;
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
				
			JsonArray jsarr = new JsonArray();
			
			while (rs.next()) {
				String food_name = rs.getString("food_name");
	            int vote = rs.getInt("vote");
	            String rest_name = rs.getString("rest_name");
	            int comments = rs.getInt("comments");
	            int food_id = rs.getInt("food_id");
	            int rest_id = rs.getInt("rest_id");
	            String food_description = rs.getString("description");

	            JsonObject obj = new JsonObject();
	            obj.addProperty("food_name", food_name);
	            obj.addProperty("food_id", food_id);
	            obj.addProperty("food_description", food_description);
	            obj.addProperty("rest_name", rest_name);
	            obj.addProperty("rest_id", rest_id);
	            obj.addProperty("vote", vote);
	            obj.addProperty("comments", comments);
	            	
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