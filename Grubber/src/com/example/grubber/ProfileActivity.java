package com.example.grubber;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	//private ImageView userIV ;
	//private TextView userIDTV;
	private EditText firstNameET;
	private EditText lastNameET;
	private EditText emailET;
	private EditText userNameET;
	private EditText pwdET;
	private ListView myVoteLV;
	
	
	static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
		"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
		"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };
	public static final String GUESTMSG = "N/a";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_profile);
		//hidden the keyboard!!!
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		//userIV = (ImageView) findViewById(R.id.profile_userIV);
		//userIDTV = (TextView)findViewById(R.id.profile_userIDTV);
		userNameET= (EditText)findViewById(R.id.profile_userNameET);
		firstNameET = (EditText)findViewById(R.id.profile_firstNameET);
		lastNameET = (EditText)findViewById(R.id.profile_lastNameET);
		emailET = (EditText)findViewById(R.id.profile_emailET);
		pwdET = (EditText) findViewById(R.id.profile_newPwdET);
		myVoteLV = (ListView)findViewById(R.id.profile_myVoteLV);
		
		
		UserInfoHelper user = UserInfoHelper.getInstance();
		//Toast.makeText(this, user.getUserName(), Toast.LENGTH_LONG).show();
		
		ArrayList<String> userVote_info = new ArrayList<String>();
		for(int i = 0; i < FRUITS.length -1 ;i ++ ){
			userVote_info.add(FRUITS[i]);
		}
		
		ArrayAdapter<String> arrayAdapter;
		arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, userVote_info);
		myVoteLV.setAdapter(arrayAdapter); 
		
			
		if(!user.getIsSignIn()){
			//userIDTV.setText(GUESTMSG);
			userNameET.setText(GUESTMSG);
			lastNameET.setText(GUESTMSG);
			firstNameET.setText(GUESTMSG);
			emailET.setText(GUESTMSG);
			
			userNameET.setEnabled(false);
			lastNameET.setEnabled(false);
			emailET.setEnabled(false);
			firstNameET.setEnabled(false);
			pwdET.setEnabled(false);
			
		}else{
			userNameET.setText(user.getUserName());
			firstNameET.setText(user.getFirstName());
			lastNameET.setText(user.getLastName());
			emailET.setText(user.getEmail());
			
			//need to add the vote history!!
			
		}
	
		
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
}
