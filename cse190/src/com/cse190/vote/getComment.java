package com.cse190.vote;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class getComment
 * This class is for getting details on one specific food
 */
public class getComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "cse190";
	static String PASS = "yelp190";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getComment() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		
		String food_id = request.getParameter("food_id");
		String min = request.getParameter("min");
		String max = request.getParameter("max");
		
		JsonObject js = new JsonObject();

		if (food_id == null || min == null || max == null) {  
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
			
			// Allow either email or username to be submitted
			String sql;
			sql = "SELECT COUNT(*) AS total FROM vote WHERE food_id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, food_id);
			
			ResultSet rs1 = stmt.executeQuery();
			
			if(rs1.next())
			{
				js.addProperty("total", rs1.getString("total"));
			}

			int maxr = Integer.parseInt(max);
			int minr = Integer.parseInt(min);
			
			sql = "SELECT v.comment, u.username, v.time FROM vote v, user u WHERE v.food_id = ? AND v.user_id=u.user_id ORDER BY v.time DESC LIMIT ?, ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, food_id);
			stmt.setInt(2, minr);
			stmt.setInt(3, maxr - minr + 1);
			
			ResultSet rs = stmt.executeQuery();	
			JsonArray jsarr = new JsonArray();
			while (rs.next())
			{	
				JsonObject obj = new JsonObject();
				obj.addProperty("username", rs.getString("username"));
				obj.addProperty("comment", rs.getString("comment"));
				obj.addProperty("time", rs.getString("time"));
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
