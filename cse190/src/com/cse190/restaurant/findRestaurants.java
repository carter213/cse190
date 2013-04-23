package com.cse190.restaurant;

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
 * Servlet implementation class findRestaurants
 */
public class findRestaurants extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "cse190";
	static String PASS = "yelp190";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public findRestaurants() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		
		String key = request.getParameter("key");
		String inlatitude = request.getParameter("latitude");
		String inlongitude = request.getParameter("longitude");
		String inzip = request.getParameter("zip");
		String incity = request.getParameter("city");
		

		String sql;
		sql = "SELECT * FROM restaurant WHERE";
		//adding zip on the query
		if (inzip != null)
		{
			sql = sql + " zip = '" + inzip + "' AND";
		}
		
		if (incity != null)
		{
			sql = sql + " city = '" + incity + "' AND";
		}
		
		/*if (inlatitude != null)
		{
			sql = sql + " latitude >= '" + inlatitude + "' AND latitude <= '" + inlatitude + "' AND";
		}*/
		
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			   
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
			
			sql = sql + " name LIKE '%" + key + "%'";
			ResultSet rs = stmt.executeQuery(sql);
				
			
			//Gson gson = new Gson();
			
			int i = 0;
			while (rs.next()) {
				int id = rs.getInt("rest_id");
	            String name = rs.getString("name");
	            String address = rs.getString("address");
	            String phone = rs.getString("phone");
	            double longitude = rs.getDouble("longitude");
	            double latitude = rs.getDouble("latitude");
	            String website = rs.getString("website");
	            String city = rs.getString("city");
	            String state = rs.getString("state");
	            i++;
	            out.println(id + " " + name + " " + address + " " + phone + " " + city + " " + latitude  + " " + longitude + " " + state +  " " + website);
	        }
			out.println(i);

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
	}

}
