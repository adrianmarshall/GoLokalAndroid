package com.tech.lokal;
/*
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
*/
import com.tech.lokal.R;

import android.app.Activity;
import android.os.Bundle;

import android.content.ClipData.Item;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import android.os.Build;

public class CategoryActivity extends Activity{
	// TO:DO implement GridView
	//GridView gridView;
	//ArrayList<Item> gridArray = new ArrayList<Item>();
	//CustomGridViewAdapter customGridAdapter;
	
	private Integer[] mThumbIds = {
            
		
            
            };
 
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category);	// Sets the view to our XML Category view
		
		// Setup spinners
		Spinner spinState = (Spinner) findViewById(R.id.spinState);		// Spinner for States list. (Manually inputed for now. Add dynamic functionality later to retrieve from server or not if it saves more data
		Spinner spinCity = (Spinner) findViewById(R.id.spinCity);		// Spinner for City list
		
		// ************* SETUP SPINNER FOR STATES *********************
		int spinStatePosition = 0; 		// Spinner Selection position 
		//Create an Array adapter using the string array for states and the default array adapter
		ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);		//  The simple_spinner_item layout is provided by the platform and is the default layout you should use unless you'd like to define your own layout for the spinner's appearance.

		// Specify the layout to use when the spinner shows the list of choices
		adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinState.setAdapter(adapterState);
		spinState.setSelection(spinStatePosition);
		
		// ********* SETUP SPINNER FOR CITIES ***************
		int spinCityPosition = 0; 		// Spinner Selection position 
		//Create an Array adapter using the string array for states and the default array adapter
		ArrayAdapter<CharSequence> adapterCity = ArrayAdapter.createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);		//  The simple_spinner_item layout is provided by the platform and is the default layout you should use unless you'd like to define your own layout for the spinner's appearance.

		// Specify the layout to use when the spinner shows the list of choices
		adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinCity.setAdapter(adapterCity);
		spinCity.setSelection(spinCityPosition);
		
		//******* Working with Grid View ******************
		  
			//set grid view item
		 // Bitmap homeIcon = BitmapFactory.decodeResource(this.getResources(), );
		 // Bitmap userIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.personal);
		
		// Setup Image Buttons
		ImageButton imgBtnFamily = (ImageButton) findViewById(R.id.ibtnFamily);
		ImageButton imgBtnBars = (ImageButton) findViewById(R.id.ibtnBars);
		
		imgBtnFamily.setOnClickListener(new OnClickListener(){
			
			public void onClick(View arg0) {
 
			   Toast.makeText(CategoryActivity.this,
				"Family Button is clicked!", Toast.LENGTH_SHORT).show();
 
			}
 
		});
		
		imgBtnBars.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				Toast.makeText(CategoryActivity.this, "Heading to the Bars!", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(),EventActivity.class);
				startActivity(i);
			}
			
		});
	}
}
