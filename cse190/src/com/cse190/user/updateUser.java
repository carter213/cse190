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
 * Servlet implementation class updateUser
 * This class is used to update a user's profile using the new posted information
 */
public class updateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public updateUser() {
        super();
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

		JsonObject obj = new JsonObject();
		if (username == null || password == null || email == null)
		{
			obj.addProperty("result", "false");
			obj.addProperty("message", "Parameter required");
			out.println(obj.toString());
			return;
		}
		
		if(first_name == null)
		{
			first_name = "";
		}
		
		if(last_name == null)
		{
			last_name = "";
		}
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			String sql = "SELECT user_id FROM user WHERE email = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, email);
			ResultSet rs = stmt.executeQuery();
				
			rs.next();
			if(rs.getRow() == 0)
			{
				sql = "UPDATE user SET password = ?, email = ?, first_name = ?, last_name = ? WHERE user_id = ?";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, password);
				stmt.setString(2,  email);
				stmt.setString(3, first_name);
				stmt.setString(4, last_name);
				stmt.setInt(5, rs.getInt("user_id"));
				stmt.executeUpdate();
				obj.addProperty("result", true);
				obj.addProperty("message", "Update success");
			}
			else
			{
				obj.addProperty("result", false);
				obj.addProperty("message", "User not found");
			}

			out.print(obj.toString());
			
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
