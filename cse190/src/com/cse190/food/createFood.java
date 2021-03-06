package com.cse190.food;

import java.io.File;
import java.io.FileOutputStream;
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

import com.google.gson.JsonObject;
import org.apache.commons.codec.binary.Base64;

/**
 * Servlet implementation class createFood
 * This class is used to add foods from users to the database.
 */
public class createFood extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String DB_URL = "jdbc:mysql://ec2-54-244-83-228.us-west-2.compute.amazonaws.com:3306/cse190";
	//  Database credentials
	static String USER = "";
	static String PASS = "";
       
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
	
	}

	/**
	 * Handle post of new food and add it to the database.
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		//Restaurant ID
		int rest_id;
		
		String name = request.getParameter("name"); 				//Food name
		String description = request.getParameter("description");	//Restaurant description
		String rest_id_result = request.getParameter("rest_id");	//Restaurant ID
		byte [] image;												
		String imagein = request.getParameter("image");				//Food Image
		JsonObject js = new JsonObject();
		
		if( name == null || rest_id_result == null || imagein == null)
		{
			js.addProperty("result", false);
			js.addProperty("message", "Missing parameter");
			out.println(js.toString());
			return;
		}
		else {
			rest_id = Integer.parseInt(rest_id_result);
			//decode Base64
			image = Base64.decodeBase64(imagein);

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
				//int id = 0;
				sql = "INSERT INTO food (rest_id, name, description, vote) VALUES (?, ?, ?, 0)";
				stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(1, rest_id);
				stmt.setString(2, name);
				stmt.setString(3, description);
				stmt.executeUpdate();

				ResultSet rs1 = stmt.getGeneratedKeys();
				rs1.next();
				if (rs1.getRow() != 0)
				{
					//write the pics to server
					try{
					File file = new File("/var/www/lighttpd/picture/" + rs1.getInt(1) + ".jpg");
					FileOutputStream fw = new FileOutputStream(file);
					fw.write(image, 0, image.length);
					fw.close();
					}
					catch (Exception e)
					{
						out.println("Failed");
						return;
					}
					//json
					js.addProperty("result", true);
					js.addProperty("message", "Food has been added");
				}
				else
				{
					js.addProperty("result", false);
					js.addProperty("message", "Not found");
				}
			}
			else
			{
				js.addProperty("result", false);
				js.addProperty("message", "Duplicate found");
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

}
