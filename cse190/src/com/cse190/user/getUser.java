package com.cse190.user;

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
 * Servlet implementation class getUser
 */
public class getUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "cse190";
	static String PASS = "yelp190";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String user_id = request.getParameter("user_id");

		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			JsonObject js = new JsonObject();
			// Allow either email or username to be submitted
			String sql;
			if (user_id ==null) {  
				js.addProperty("result", false);
				js.addProperty("message", "Parameter required");
				out.println(js.toString());
				return;
			}
			
			sql = "SELECT username, email, first_name, last_name FROM user WHERE user_id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, user_id);
			
			ResultSet rs = stmt.executeQuery();
				
			//
			if(rs.next())
			{	
				js.addProperty("result", true);
				js.addProperty("message", "User found");
				js.addProperty("username", rs.getString("username"));
				js.addProperty("email", rs.getString("email"));
				js.addProperty("first_name", rs.getString("first_name"));
				js.addProperty("last_name", rs.getString("last_name"));

			}
			else
			{
				js.addProperty("result", false);
				js.addProperty("message", "User not found");
			}
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
