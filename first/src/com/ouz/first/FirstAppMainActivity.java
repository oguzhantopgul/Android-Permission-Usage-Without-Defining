package com.ouz.first;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;
import android.provider.ContactsContract;

public class FirstAppMainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_app_main);
        
        startService(new Intent(this, BGService.class));
        
       /** Button startServiceButton = (Button) findViewById(R.id.service_start_button);
        startServiceButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
			}
		});
		**/    
    }
    
    public void ButtonClicked(View view) {
        startService(new Intent(this, BGService.class));
    }
}
