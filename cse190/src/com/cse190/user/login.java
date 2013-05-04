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
 * Servlet implementation class login
 */
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "cse190";
	static String PASS = "yelp190";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
				
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			JsonObject js = new JsonObject();
			// Allow either email or username to be submitted
			String sql;
			if (email!=null) {  
				sql = "SELECT user_id, password FROM user WHERE email = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, email);
			}
			else if (username!=null) {
				sql = "SELECT user_id, password FROM user WHERE username = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, username);
			}
			else {
				//out.println("Error, parameter required");
				js.addProperty("result", false);
				js.addProperty("message", "Parameter required");
				out.println(js.toString());
				return;
			}
			
			ResultSet rs = stmt.executeQuery();
				
			// Compare the password stored in the db to their entered password
			if(rs.next())
			{
				String stored = rs.getString("password");
				if (Password.check(password, stored))
				{
					js.addProperty("result", true);
					js.addProperty("message", "Login success");
					js.addProperty("user_id", rs.getInt("user_id"));
				}
				else
				{
					js.addProperty("result", false);
					js.addProperty("message", "Wrong username or password");
				}
			}
			else
			{
				js.addProperty("result", false);
				js.addProperty("message", "Wrong username or password");
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
