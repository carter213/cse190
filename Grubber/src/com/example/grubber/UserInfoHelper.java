package com.example.grubber;

import android.app.Application;


public class UserInfoHelper  {
	private static UserInfoHelper instance;
	
	private String m_lastName;
	private String m_firstName;
	private String m_email;
	private String m_userName;
	private boolean is_signIn;
	private int m_userId;
	
	public static synchronized UserInfoHelper getInstance(){
		
		if(instance==null){
		       instance = new UserInfoHelper();
		}
		return instance;
	}
	public  UserInfoHelper (){
		this.m_userName = "";
		this.m_email = "";
		this.m_firstName = "";
		this.m_lastName = "";
		this.is_signIn = false;
	 }
	
	public void setAll (String username, String email,String firstname, String lastname){
		this.m_userName = username;
		this.m_email = email;
		this.m_firstName = firstname;
		this.m_lastName = lastname;
		this.is_signIn = true;
	 }
	
	 public void setUsername(String username){
		this.m_userName = username;
	}
	
	public void setEmail(String email){
		this.m_email = email;
	}
	public void setLastName(String lastName){
		this.m_lastName = lastName;
	}
	public void setFirstName(String firstName){
		this.m_firstName = firstName;
	}
	
	
	public String getUserName(){
		return this.m_userName;
	}
	public String getEmail(){
		return this.m_email;
	}
	public String getFirstName(){
		return this.m_firstName;
	}
	public String getLastName(){
		return this.m_lastName;
	}
	public Boolean getIsSignIn(){
		return this.is_signIn;
		
	}
	
	
	public void signOut(){
		this.m_userName = "";
		this.m_email = "";
		this.m_firstName = "";
		this.m_lastName = "";
		this.is_signIn = false;
	}
	
}
