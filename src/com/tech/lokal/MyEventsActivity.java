package com.tech.lokal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tech.lokal.EventActivity.LoadEvents;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;



public class MyEventsActivity extends ListActivity {
	
	//Progress Dialog
		private ProgressDialog pDialog;
		
		// private ListView lv
		private BaseAdapter mAdapter;
		private String username;
		
		// ID's for the names of the data we're retrieving from the API
		
		static final String TAG_ID = "id";
		//static final String TAG_LIKES = "likes";			** NOT YET IMPLEMENTED ON SERVER SIDE
		static final String TAG_TITLE = "title";
		//static final String TAG_EVENT_IMAGE = "photo";	** Not needed for this activity. Remove later
		static final String TAG_DATE = "event_date";
		static final String TAG_START_TIME = "startTime";
		
		
		// TODO - Get users username and append to URL_event to get all the users events
		
		
		SharedPreferences sharedPreferences;		// shared preferences will be used to store username & password information
		
		public static final String MyPREFERENCES = "MyPrefs" ;
		String usernameKey = "username";		// the key to look for to retrieve the users username
		
		
		
		
		// append -->   &user__username= <user's username>
		
		private static String URL_EVENT = "http://192.168.1.3:8000/api/event/?format=json";
		
		//Parameter for the date to filter by 
		String dateParam = "";			// Add the name of the user to get all events from this user
		
		// Events JSON Array
		JSONArray events = null;
		
		// Creating JSON Parser object 
		JSONParser jsonParser = new JSONParser();
		
		
		ArrayList<HashMap<String,String>> eventList;
		
		@Override
		public void onCreate(Bundle savedInstanceState){
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_myevents);
			eventList = new ArrayList<HashMap<String,String>>();	// HashMap for listview
			
	        
	        sharedPreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
	       
	        // Retrieve the users' username
	        if(sharedPreferences.contains(usernameKey)){
	        	try{
	        		username = sharedPreferences.getString(usernameKey, "");
	        		
	        		// append username to URL_EVENT
	        		URL_EVENT += "&user__username="+username;
	        	}catch(Exception e){
	        		e.printStackTrace();
	        	}
	        }
	        
	    
			new LoadEvents().execute();	// Execute LoadEvents function
			
			//get listview 
			ListView lv = getListView();
			
			// TODO come back and change this divider later to a more reasonable and appealing color/image
			lv.setDivider(this.getResources().getDrawable(R.drawable.repeat_bg));		// Setting a divider between the list elements
			lv.setDividerHeight(10);
			
			//lv.setDividerHeight(10);
			//lv.setDivider(getResources().getDrawable(android.R.color.holo_orange_dark));
			
			lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

				@Override	
				public void onItemClick(AdapterView<?> parent, View view,			// parent - arg0 , id-arg3
						int position, long id) {
					
					Toast.makeText(MyEventsActivity.this, "Item Selected: "+ position, Toast.LENGTH_LONG).show();
					
					// **********	 GO TO EDIT EVENT VIEW ********** <---- TO-Do: Implement
					
					 Intent editEvent = new Intent(getApplicationContext(), EditEventActivity.class);
					  
					 String event_id = ((TextView) view.findViewById(R.id.myevent_id)).getText().toString(); 		// Gets the id for the event clicked
					 editEvent.putExtra("event_id",event_id);		//sends the event id to  EventDetailActivity 
					  
					 startActivity(editEvent);
					  
				
				}
				
			});
			
		}
		
					// TODO  Implement this function to reload the data once the screen is visible again(activity resumed)
		@Override
	    protected void onResume() {
	        super.onResume();
	        // The activity has become visible (it is now "resumed").
	    }
		
		
		
		public class LoadEvents extends AsyncTask<String,String,String>{
		
			
			@Override
			protected void onPreExecute(){
				// Display loading Dialog
				super.onPreExecute();	// Runs the UI thread before doInBackground
				pDialog = new ProgressDialog(MyEventsActivity.this);
				pDialog.setMessage("Listing Your Events...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
				
				Log.d("PreExecute: ", " In preExecute");
			}
			
			@Override
			protected String doInBackground(String... args) {
				// Building Parameters 
				
				//List<NameValuePair> params = new ArrayList<NameValuePair>();
				//params.add(new BasicNameValuePair("city","&city="+eventCity));
				
				
				//getting JSON string from URL
				String json  = jsonParser.makeHttpRequest(URL_EVENT,"GET");
				
				// check your log cat for JSON response
				Log.d("Events JSON:",">>"+ json);
				
				return json;
			}

			@Override 
			protected void onPostExecute(String json){
				Log.d("PostExecute: ", " In postExecute");
				try{
					JSONObject alldata = new JSONObject(json);	// Gets the whole JSON Object from the request,turns into a JSON Object
					JSONArray events = alldata.getJSONArray("objects");		// Gets the JSON Array of events
					
					if(events != null){
						//Looping through all events
						for(int i =0; i < events.length(); i++){
							JSONObject e = events.getJSONObject(i);
							
							// Storing each json item values in variable
							String id = e.getString(TAG_ID);
							String title = e.getString(TAG_TITLE);
							//String likes = e.getString(TAG_LIKES);	   ** Likes NOT YET implemented on server 
							
							String date = e.getString(TAG_START_TIME);
							
							
							// creating new HashMap
							HashMap<String,String> map = new HashMap<String,String>();
							
							// adding each child node to HashMap key > value
							map.put(TAG_ID, id);
							map.put(TAG_TITLE, title);
							// map.put(TAG_LIKES, likes); 	** NOT YET IMPLEMENTED ON SERVER SIDE
							map.put(TAG_START_TIME, date);
							
							//adding HashList to ArrayList
							
							eventList.add(map);
						}
						 mAdapter = new MyEventsListAdapter(MyEventsActivity.this,eventList);
						getListView().setAdapter(mAdapter);
						
						pDialog.dismiss();
						
					}
				}catch (JSONException e){
					e.printStackTrace();
				}
				
			}
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
