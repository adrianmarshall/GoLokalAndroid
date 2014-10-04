package com.tech.lokal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends ListActivity{
	//Progress Dialog
	private ProgressDialog pDialog;
	
	// private ListView lv
	private BaseAdapter mAdapter;
	
	static final String TAG_ID = "id";
	//static final String TAG_LIKES = "likes";			** NOT YET IMPLEMENTED ON SERVER SIDE
	static final String TAG_TITLE = "title";
	static final String TAG_EVENT_IMAGE = "photo";
	static final String TAG_DATE = "date";
	
	public static final String URL_EVENT = "http://lokalapp.co/api/event/?format=json";
	
	// Events JSON Array
	JSONArray events = null;
	
	// Creating JSON Parser object 
	JSONParser jsonParser = new JSONParser();
	
	
	
	ArrayList<HashMap<String,String>> eventList;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		eventList = new ArrayList<HashMap<String,String>>();	// HashMap for listview
		
		Log.d("onCreate", "Yep");
		
		new LoadEvents().execute();	// Execute LoadEvents function
		
		//get listview 
		ListView lv = getListView();
		
		// come back and change this divider later to a more reasonable and appealing color/image
		lv.setDivider(this.getResources().getDrawable(R.drawable.repeat_bg));		// Setting a divider between the list elements
		lv.setDividerHeight(3);
		
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			@Override	// TODO add an intent to go to a Detail view
			public void onItemClick(AdapterView<?> parent, View view,			// parent - arg0 , id-arg3
					int position, long id) {
				
				Toast.makeText(EventActivity.this, "Item Selected: "+ position, Toast.LENGTH_LONG).show();
				
				/*	Uncomment **********	 GO TO DETAIL VIEW ********** <---- TO-Do: Implement
				 * */
				 Intent detail = new Intent(getApplicationContext(), EventDetailActivity.class);
				  
				 String event_id = ((TextView) view.findViewById(R.id.event_id)).getText().toString();
				 detail.putExtra("event_id",event_id);		//sends the event id to  EventDetailActivity 
				  
				 startActivity(detail);
				  
				 
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
			pDialog = new ProgressDialog(EventActivity.this);
			pDialog.setMessage("Listing Events...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			
		}
		
		@Override
		protected String doInBackground(String... args) {
			// Building Parameters 
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			//getting JSON string from URL
			String json  = jsonParser.makeHttpRequest(URL_EVENT,"GET",params);
			
			// check your log cat for JSON response
			Log.d("Events JSON:",">>"+ json);
			
			return json;
		}

		@Override 
		protected void onPostExecute(String json){
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
						String event_image = e.getString(TAG_EVENT_IMAGE);
						String date = e.getString(TAG_DATE);
						
						
						// creating new HashMap
						HashMap<String,String> map = new HashMap<String,String>();
						
						// adding each child node to HashMap key > value
						map.put(TAG_ID, id);
						map.put(TAG_TITLE, title);
						// map.put(TAG_LIKES, likes); 	** NOT YET IMPLEMENTED ON SERVER SIDE
						map.put(TAG_EVENT_IMAGE, event_image);
						map.put(TAG_DATE, date);
						
						//adding HashList to ArrayList
						
						eventList.add(map);
					}
					 mAdapter = new EventListAdapter(EventActivity.this,eventList);
					getListView().setAdapter(mAdapter);
					
					pDialog.dismiss();
					
				}
			}catch (JSONException e){
				e.printStackTrace();
			}
			
		}
	}
}
