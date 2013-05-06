package com.cse190.food;

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


/**
 * Servlet implementation class createFood
 */
public class createFood extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "cse190";
	static String PASS = "yelp190";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public createFood() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		int rest_id;
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		String rest_id_result = request.getParameter("rest_id");
		
		if( name == null || rest_id_result == null)
		{
			out.println("Error, parameter missing");
			return;
		}
		else {
			rest_id = Integer.parseInt(rest_id_result);
		}
		
		if (description == null)
			description = "";
	
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			
			String sql = "SELECT food_id FROM food WHERE name = ? AND rest_id = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, name);
			stmt.setInt(2, rest_id);
			
			ResultSet rs = stmt.executeQuery();
			rs.next();
			if(rs.getRow() == 0)
			{
				sql = "INSERT INTO food (rest_id, name, description) VALUES (?, ?, ?)";
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, rest_id);
				stmt.setString(2, name);
				stmt.setString(3, description);
				
				stmt.executeUpdate();
				out.println("Success");
			}
			else
			{
				out.println("Error, duplicate found");
			}
			

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
