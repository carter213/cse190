package com.example.grubber;

import java.util.ArrayList;
import java.util.Arrays;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

	private ImageView userIV ;
	private TextView userNameTV;
	private TextView nameTV;
	private TextView emailTV;
	private ListView myVoteLV;
	
	static final String[] FRUITS = new String[] { "Apple", "Avocado", "Banana",
		"Blueberry", "Coconut", "Durian", "Guava", "Kiwifruit",
		"Jackfruit", "Mango", "Olive", "Pear", "Sugar-apple" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_profile);
		//hidden the keyboard!!!
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		userIV = (ImageView) findViewById(R.id.profile_userIV);
		userNameTV = (TextView)findViewById(R.id.profile_displayUserNameTV);
		nameTV = (TextView)findViewById(R.id.profile_displayNameTV);
		emailTV = (TextView)findViewById(R.id.profile_displayEmailTV);
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
			userNameTV.setText("Guest");
		}else{
			userNameTV.setText(user.getUserName());
			nameTV.setText(user.getFirstName()+ " " + user.getLastName());
			emailTV.setText(user.getEmail());
			//need to add the vote history!!
			
		}
		
		
		
		
	}
	
	
	
	
	
}
