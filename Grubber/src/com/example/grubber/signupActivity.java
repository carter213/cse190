package com.example.grubber;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.example.grubber.R;
import com.example.grubber.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;

public class signupActivity extends Activity implements View.OnClickListener  {
	
	public final int pwdToShort = 5;
	
	public final String pwdToShortError = "Password needs to be at least 5 characters.";
	public final String noValid = "Not Valid.";
	public final String pwdDiffError = "The passwords do not match.";
	//UI Id
	private EditText usernameET ;
	private EditText pwdET;
	private EditText reenterpwdET;
	private Button confirmBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_signup);
		
		usernameET =  (EditText) findViewById(R.id.signup_username_ET);
		pwdET = (EditText) findViewById(R.id.signup_pwd_ET);
		reenterpwdET = (EditText) findViewById(R.id.signup_pwd_reenter_ET);
		confirmBtn = (Button) findViewById(R.id.signup_confirm_btn);
		
		confirmBtn.setOnClickListener(this);
		
		
		
	}

	@Override
	public void onClick(View v) {
	
		if(v.getId() == R.id.signup_confirm_btn){
			if(!isValidEmail( usernameET.getText().toString())){
				usernameET.setError(noValid);
			}
			else if(pwdET.length() < pwdToShort){
				pwdET.setError(pwdToShortError);
			}
			else if(!pwdET.getText().toString().equals(reenterpwdET.getText().toString())){
				reenterpwdET.setError(pwdDiffError);
			}
			else{
				//compare the database user name!
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

}
