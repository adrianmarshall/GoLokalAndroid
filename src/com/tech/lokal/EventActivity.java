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

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
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

public class EventActivity extends ListActivity{
	//Progress Dialog
	private ProgressDialog pDialog;
	
	// private ListView lv
	private BaseAdapter mAdapter;
	private Spinner spinDate;
	
	static final String TAG_ID = "id";
	//static final String TAG_LIKES = "likes";			** NOT YET IMPLEMENTED ON SERVER SIDE
	static final String TAG_TITLE = "title";
	static final String TAG_EVENT_IMAGE = "photo";
	static final String TAG_DATE = "event_date";
	static final String TAG_START_TIME = "startTime";
	static final String TAG_DESCRIPTION = "description";
	
	public static final String URL_EVENT = "http://192.168.1.9:8000/api/event/?format=json";
	
	//Parameter for the date to filter by 
	String dateParam = "";			// Concatenate the date if user selects to use a certain date
	
	// Events JSON Array
	JSONArray events = null;
	
	// Creating JSON Parser object 
	JSONParser jsonParser = new JSONParser();
	
	
	String eventCity,eventState,eventCategory;
	ArrayList<HashMap<String,String>> eventList;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		eventList = new ArrayList<HashMap<String,String>>();	// HashMap for listview
		
		// Get City and state infromation from Category Activity 
		  
        Bundle extras = null;
        eventCity= null;
        eventState = null;	
        eventCategory = null;
        
        int spinPosition = 0;
        
        
        
        // Getting The event Id from when it was clicked on in the EventActivity activity 
        if(savedInstanceState == null){
        	extras = getIntent().getExtras();
        	if(extras == null){
        		eventCity = null;
        		eventState = null;
        	}
        	else{
        		eventCity = extras.getString("city");
        		eventState = extras.getString("state");
        		if(extras.containsKey("category")){
        			eventCategory = extras.getString("category");
        		}
        		if(extras.containsKey("dateParam")){
        				dateParam = extras.getString("dateParam");
        	}
        		if(extras.containsKey("spinPosition")){			// Gets the previous position of the item that was last selected
        			spinPosition = extras.getInt("spinPosition");
        		}
        }
        } else{
        	eventCity = (String) savedInstanceState.getSerializable("city");
        	eventState = (String) savedInstanceState.getSerializable("state");
        	if(extras.containsKey("category")){
        		eventCategory = (String) savedInstanceState.getSerializable("category");
        	}
        	if(extras.containsKey("dateParam")){
        		dateParam = (String) savedInstanceState.getSerializable("dateParam");
        	}
        	if(extras.containsKey("spinPosition")){			// Gets the previous position of the item that was last selected
    			spinPosition =  Integer.parseInt(((String) savedInstanceState.getSerializable("spinPosition")));
    		}
        	
        }
       
        final String previousDateParam = dateParam;
        
        Log.d("Event City,State ", eventCity + ","+eventState);
        
        spinDate = (Spinner) findViewById(R.id.spinDateChoice);
        
      //Create an Array adapter using the string array 'datechoices' for the date to filter by and the default array adapter
	
        ArrayAdapter<CharSequence> adapterDate = ArrayAdapter.createFromResource(this, R.array.datechoices, android.R.layout.simple_spinner_item);		//  The simple_spinner_item layout is provided by the platform and is the default layout you should use unless you'd like to define your own layout for the spinner's appearance.
        
     // Specify the layout to use when the spinner shows the list of choices
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        spinDate.setAdapter(adapterDate);
        spinDate.setSelection(spinPosition);	// Sets the selection to the first value which is "All Days"
        
        
        spinDate.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String datechoice = parent.getItemAtPosition(position).toString();
				// datechoice will either be "All Days" or "Today"
				
				
				if(datechoice.equals("Today")){
					// Get todays date 
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					   //get current date time with Date()
					   Date date = new Date();
					   String todaysDate = dateFormat.format(date);
					   
					   Log.d("TodaysDate: ", todaysDate);
					   dateParam = "&event_date="+todaysDate;			// adds todays date to the dateParam
					   Log.d("compare:",dateParam +" -prev->"+ previousDateParam);
					   Log.d("Equal?? :", dateParam.equals(previousDateParam) +"");
				}else if(datechoice.equals("All Days")){
					dateParam="";
				}else if(datechoice.equals("Upcoming")){
					// Get todays date 
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					   //get current date time with Date()
					   Date date = new Date();
					   String todaysDate = dateFormat.format(date);
					   
					   Log.d("TodaysDate: ", todaysDate);
					   dateParam = "&event_date__gte="+todaysDate;			// adds todays date to the dateParam
					   Log.d("compare:",dateParam +" -prev->"+ previousDateParam);
					   Log.d("Equal?? :", dateParam.equals(previousDateParam) +"");
					
				}
				if(dateParam != previousDateParam)
					Log.d("output: ", "It's saying they're not equal ");
				
				if(! (dateParam.equals(previousDateParam)) ){
					
				
				Intent i = new Intent(getApplicationContext(),EventActivity.class);
				i.putExtra("dateParam", dateParam);		// passing date parameter to update the view based on the date
				
				// must pass in parameters that we're passed in from previous "CategoryActivity"
				i.putExtra("city", eventCity);
				i.putExtra("state", eventState);
				i.putExtra("spinPosition", position);
				startActivity(i);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
        });
		
		
		new LoadEvents().execute();	// Execute LoadEvents function
		
		//get listview 
		ListView lv = getListView();
		
		// TODO come back and change this divider later to a more reasonable and appealing color/image
		lv.setDivider(this.getResources().getDrawable(R.drawable.repeat_bg));		// Setting a divider between the list elements
		lv.setDividerHeight(15);
		
		//lv.setDividerHeight(10);
		//lv.setDivider(getResources().getDrawable(android.R.color.holo_orange_dark));
		
		lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {

			@Override	
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
			params.add(new BasicNameValuePair("city","&city="+eventCity));
			params.add(new BasicNameValuePair("state","&state="+eventState));
			if(eventCategory == null){	
				params.add(new BasicNameValuePair("category",""));
			
			
			}else{
				params.add(new BasicNameValuePair("category","&category="+eventCategory));
			}
			
			// If the user selected "Today" for the date we'll send over the date to the server
			if(dateParam == null || dateParam == ""){	
				params.add(new BasicNameValuePair("event_date",""));
			
			
			}else{
				params.add(new BasicNameValuePair("event_date",dateParam));
			}
			Log.d("category param: ",params.get(2).getValue());
			Log.d("date param: ",params.get(3).getValue());
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
						String date = e.getString(TAG_START_TIME);
						String description = e.getString(TAG_DESCRIPTION);
						
						
						// creating new HashMap
						HashMap<String,String> map = new HashMap<String,String>();
						
						// adding each child node to HashMap key > value
						map.put(TAG_ID, id);
						map.put(TAG_TITLE, title);
						// map.put(TAG_LIKES, likes); 	** NOT YET IMPLEMENTED ON SERVER SIDE
						map.put(TAG_EVENT_IMAGE, event_image);
						map.put(TAG_START_TIME, date);
						map.put(TAG_DESCRIPTION, description);
						
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
