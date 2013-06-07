package com.cse190.user;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class createUser
 * Registers a new user in the database.
 */
@WebServlet("/createUser")
public class createUser extends HttpServlet {
	private static final long serialVersionUID = 1L;

	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
	   
    /**
     * Default constructor. 
     */
    public createUser() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
				
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");
		String first_name = request.getParameter("first_name");
		String last_name = request.getParameter("last_name");
		
		JsonObject js = new JsonObject();

		//***REQUIRED PARAMETER
		if (username == null || password == null || email == null)
		{
			js.addProperty("result", false);
			js.addProperty("message", "Missing parameter");
			out.println(js.toString());
			return;
		}
		//*******
		
		// Get the hash of their password and store this instead
		String hash = "";
		try {
			hash = Password.getSaltedHash(password);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if (first_name == null)
		{
			first_name ="";
		}
		
		if (last_name == null)
		{
			last_name = "";
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			String sql = "SELECT username, email FROM user WHERE username = ? OR email = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, email);
			
			ResultSet rs = stmt.executeQuery();
				
			rs.next();
			if(rs.getRow() == 0)
			{
				sql = "INSERT INTO user(username, password, email, first_name, last_name) VALUES(?, ?, ?, ?, ?)";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, username);
				stmt.setString(2, hash);
				stmt.setString(3, email);
				stmt.setString(4, first_name);
				stmt.setString(5, last_name);
				stmt.executeUpdate();
				js.addProperty("result", true);
				js.addProperty("message", "User register success");
			}
			else
			{
				js.addProperty("result", false);
				if(email.equals(rs.getString("email")))
				{
					js.addProperty("message", "Email already exists");
				}
				else if(username.equals(rs.getString("username")))
				{
					js.addProperty("message", "Username already exists");
				}
				else
				{
					js.addProperty("message", "Unknown error");
				}
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
