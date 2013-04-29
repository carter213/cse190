package com.example.grubber;

import com.example.grubberlayout.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onSearchRequested();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void doSearchNearby(View view) {
    	Intent intent = new Intent(this, Results.class);
    	startActivity(intent);   
    }
    
    public void doLogin(View view) {
    	Intent intent = new Intent(this, LoginActivity.class);
    	startActivity(intent);
    	
    }
    
    public void doSearch(View view) {
    	Intent intent = new Intent(this, Results.class);
    	startActivity(intent);     	
    }
    
    public void openSearchDialog(View view) {
    	onSearchRequested();
    }
}
