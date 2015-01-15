package com.tech.lokal;
/*
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
*/
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class CategoryActivity extends Activity{
	// TO:DO implement GridView
	//GridView gridView;
	//ArrayList<Item> gridArray = new ArrayList<Item>();
	//CustomGridViewAdapter customGridAdapter;
	
	EditText editCity;
	Spinner spinState;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category);	// Sets the view to our XML Category view
		
		// Setup spinners
		spinState = (Spinner) findViewById(R.id.spinState);		// Spinner for States list. (Manually inputed for now. Add dynamic functionality later to retrieve from server or not if it saves more data
		editCity = (EditText) findViewById(R.id.categoryCity);		// Spinner for City list
		
		// ************* SETUP SPINNER FOR STATES *********************
		int spinStatePosition = 0; 		// Spinner Selection position 
		//Create an Array adapter using the string array for states and the default array adapter
		ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);		//  The simple_spinner_item layout is provided by the platform and is the default layout you should use unless you'd like to define your own layout for the spinner's appearance.

		// Specify the layout to use when the spinner shows the list of choices
		adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinState.setAdapter(adapterState);
		spinState.setSelection(spinStatePosition);
		
	
		
		//******* Working with Grid View ******************
		  
			//set grid view item
		 // Bitmap homeIcon = BitmapFactory.decodeResource(this.getResources(), );
		 // Bitmap userIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.personal);
		
		// Setup Image Buttons
		ImageButton imgBtnFamily = (ImageButton) findViewById(R.id.ibtnFamily);
		ImageButton imgBtnBars = (ImageButton) findViewById(R.id.ibtnBars);
		
		
		
		
		//Create Event button
		Button createEvent = (Button) findViewById(R.id.btnCreateEvent);
		
		
		createEvent.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// Going to the CreateEventActivity
				Intent i = new Intent(getApplicationContext(),CreateEventActivity.class);
				startActivity(i);
			}
			
		});
		
		imgBtnFamily.setOnClickListener(new OnClickListener(){
			
			public void onClick(View arg0) {
 
				String city = editCity.getText().toString();
				String state = spinState.getSelectedItem().toString();
				
				Toast.makeText(CategoryActivity.this, "Showing All events!", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(),EventActivity.class);
				
				i.putExtra("city", city);
				i.putExtra("state", state);
				//i.putExtra("category", "");
				startActivity(i);
			}
 
		});
		
		imgBtnBars.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				String city = editCity.getText().toString();
				String state = spinState.getSelectedItem().toString();
				
				Toast.makeText(CategoryActivity.this, "Heading to the Bars!", Toast.LENGTH_SHORT).show();
				Intent i = new Intent(getApplicationContext(),EventActivity.class);
				
				i.putExtra("city", city);
				i.putExtra("state", state);
				i.putExtra("category", "Nightlife");
				startActivity(i);
			}
			
		});
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			
			Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
			startActivity(i);
			//return true;
		}
		
		if(id == R.id.action_myevents){
			Intent i = new Intent(getApplicationContext(),MyEventsActivity.class);
			startActivity(i);
		}
		
		if(id == R.id.action_logout){
			// Clears all of the users data first. Clear username/password
			SharedPreferences preferences = getSharedPreferences(LoginActivity.MyPREFERENCES, 0);
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear(); 
			editor.commit();
			Intent i = new Intent(getApplicationContext(),LoginActivity.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

}
