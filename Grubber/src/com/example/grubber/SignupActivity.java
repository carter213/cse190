package com.example.grubber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.example.grubber.R;
import com.example.grubber.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

public class SignupActivity extends Activity implements View.OnClickListener  {
	
	public final int pwdToShort = 5;
	
	public final String pwdToShortError = "Password needs to be at least 5 characters.";
	public final String noValid = "Not Valid.";
	public final String pwdDiffError = "The passwords do not match.";
	public final String firstnameError = "First name is required.";
	public final String lastnameError = "Last name is required.";
	public final String usernameError = "User name is required.";
	//UI Id
	private EditText usernameET ;
	private EditText pwdET;
	private EditText reenterpwdET;
	private EditText lastnameET;
	private EditText firstnameET;
	private EditText emailET;
	
	
	private Button confirmBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_signup);
		//hidden the keyboard by deafult
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		usernameET =  (EditText) findViewById(R.id.signup_username_ET);
		pwdET = (EditText) findViewById(R.id.signup_pwd_ET);
		reenterpwdET = (EditText) findViewById(R.id.signup_pwd_reenter_ET);
		lastnameET = (EditText) findViewById(R.id.signup_lastname_ET);
		firstnameET = (EditText) findViewById(R.id.signup_firstname_ET);
		emailET = (EditText) findViewById(R.id.signup_email_ET);
		
		confirmBtn = (Button) findViewById(R.id.signup_confirm_btn);
		
		
		confirmBtn.setOnClickListener(this);
		
		
		
	}

	@Override
	public void onClick(View v) {
	
		if(v.getId() == R.id.signup_confirm_btn){
			
			boolean errorCheck = false;
			if(!isValidEmail( emailET.getText().toString())){
				emailET.setError(noValid);
				errorCheck = true;
			}
			 if(firstnameET.length() == 0 ){
				firstnameET.setError(firstnameError);
				errorCheck = true;
			}
			if(lastnameET.length() == 0 ){
				lastnameET.setError(lastnameError);
				errorCheck = true;
			}
			if(usernameET.length() == 0){
				usernameET.setError(usernameError);
				errorCheck = true;
			}
			 if(pwdET.length() < pwdToShort ){
				pwdET.setError(pwdToShortError);
				errorCheck = true;
			}
		   if(!pwdET.getText().toString().equals(reenterpwdET.getText().toString())){
				reenterpwdET.setError(pwdDiffError);
				errorCheck = true;
			}
		   
		   //error the input text
			if(!errorCheck){
				
				
				//get the error for the servlet
				boolean userFailFalg = checkLogin(emailET.getText().toString(),pwdET.getText().toString(),usernameET.getText().toString(),firstnameET.getText().toString(), 
								lastnameET.getText().toString());
						
				if(userFailFalg){
					//set global user info
					Toast.makeText(this, usernameET.getText().toString() + "  is connected.", Toast.LENGTH_LONG).show();
					UserInfoHelper user = UserInfoHelper.getInstance();
					user.setAll(usernameET.getText().toString(),emailET.getText().toString(),firstnameET.getText().toString(),
						     lastnameET.getText().toString());
				}else{
					
				}
			}
		
	 }
		
		
		
		
	}
	
	public final static boolean isValidEmail(CharSequence target) {
	    if (target == null) {
	   
	        return false;
	    } else {
	    	 return  android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	    
	}
	
	
	protected boolean checkLogin(String email, String password, String username, String firstname, String lastname){
		URL url;
		URLConnection uc;
		BufferedReader data;
		try {
			url = new URL("http://cse190.myftp.org:8080/cse190/createUser?email=" + email + "&password=" + password + "&username=" + username
						  + "&first_name=" + firstname + "&last_name=" + lastname);
			uc = url.openConnection();
			data = new BufferedReader(new InputStreamReader(uc.getInputStream()));
			String inputLine = data.readLine();
			String tr = "NULL";
			if(!inputLine.equals(tr))
			{
				
				return true;
			}
			else
			{
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//No Connection
			return false;
		}
	}
	


}
