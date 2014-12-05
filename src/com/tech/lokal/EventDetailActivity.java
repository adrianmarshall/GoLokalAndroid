package com.tech.lokal;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
// Picasso - for downloading the images


public class EventDetailActivity extends Activity {
	
	private Context mContext;
	//Progress Dialog
	private ProgressDialog pDialog;
	JSONObject event = null;
	final String SITE_URL = "http://lokalapp.co/";
	
	
	// Create TextViews to hold event information
    
    TextView title,likes_count,date,startTime,endTime,description,locationName,addressLine1,addressLine2,
    		 city,state,zipcode,user= null;
    String theUserName = "";
    ImageView event_image = null;
    
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        // The activity is being created.
	        setContentView(R.layout.activity_detail_event);
	        
	        Bundle extras = null;
	        String event_id = null;		// The id of the event given from the "EventActivity" 
	        
	        // Getting The event Id from when it was clicked on in the EventActivity activity 
	        if(savedInstanceState == null){
	        	extras = getIntent().getExtras();
	        	if(extras == null)
	        		event_id = null;
	        	else
	        		event_id = extras.getString("event_id");
	        	
	        } else{
	        	event_id = (String) savedInstanceState.getSerializable("event_id");
	        }
	       
	        Log.d("Event ID: ", event_id);
	        
	        new loadEvent().execute(event_id);
	        
	        
	    }

	    // This will return our JSON object
	    public JSONObject getSingleEvent(String event_id){			
	    	
	    	String URL_Event = "http://lokalapp.co/api/event/?format=json";
	    	String params = "&id=";
	    	params += event_id;		// appends the event objects ID number to the 'params' variable
	    	
	    	JSONObject event = null;
	    	
	    	JSONParser jParser = new JSONParser();
	    	
	    	String jsonEvent = jParser.makeHttpRequest(URL_Event + params, "GET",null);
	    	
	    	try{
	    		JSONObject alldata = new JSONObject(jsonEvent);
	    		JSONArray eventlist = alldata.getJSONArray("objects");
	    		event = eventlist.getJSONObject(0);
	    		Log.d("Single Event JSON", event.toString());
	    	}catch( JSONException e ){
	    		e.printStackTrace();
	    		Log.e("EventDetail->getSingleEvent", "Failed to create new JSONObject from data");
	    	}
	    	
	    	return event;
	    	
	    }

	    public class loadEvent extends AsyncTask<String,String,String>{

	    	
	    	@Override
			protected void onPreExecute(){
				// Display loading Dialog
				super.onPreExecute();	// Runs the UI thread before doInBackground
				pDialog = new ProgressDialog(EventDetailActivity.this);
				pDialog.setMessage("Getting Event information...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
				
			}
			
	    	
			@Override
			protected String doInBackground(String... params) {
				String event_id = params[0]; 		// Gets the event ID 
				
				event = getSingleEvent(event_id);
				
		
				String TAG_USER = "";
				try {
					TAG_USER = event.getString("user");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	  try {
					theUserName = getUserName(TAG_USER);		// HTTP request to get the users username. Can be modified to get all user information & put into a JSON object
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		// HTTP request to get the Users username

				
				
				return event.toString();
			}
			
			protected void onPostExecute(String eventData){
				
				pDialog.dismiss();		// Dismisses the Dialog
				
				try {
					event = new JSONObject(eventData);
					Log.d("Event Data", eventData);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d("onPostExecute", "Can't convert eventData string to JSON object");
				}
				try {
					setEventData();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			// Sets the Text fields in the UI to the corresponding event data
				checkNullAll();			// Checks all the TextViews, if null set value to blank " "
				Log.d("onPostExecute", "end of onPost Execute");
				
			}
	    	
	    }
	    
	    public void setEventData() throws ClientProtocolException, IOException{

	    	
	    	
	        String photoPath = null; // url path to the photo for the event
	        
	        // ********* TODO -> Format dates & times
	        title = (TextView) findViewById(R.id.event_title);
	      //  likes_count = (TextView) findViewById(R.id.likes_count);
	        date = (TextView) findViewById(R.id.date);
	        startTime = (TextView) findViewById(R.id.startTime_text);
	        endTime = (TextView) findViewById(R.id.endTime_text);
	        description = (TextView) findViewById(R.id.description_text);
	        locationName = (TextView) findViewById(R.id.locationName);
	        addressLine1 = (TextView) findViewById(R.id.addressLine1);
	        addressLine2 = (TextView) findViewById(R.id.addressLine2);
	        city = (TextView) findViewById(R.id.city);
	        state = (TextView) findViewById(R.id.state);
	        zipcode = (TextView) findViewById(R.id.zipcode);
	        user = (TextView) findViewById(R.id.eventUser);
	        
	        event_image = (ImageView) findViewById(R.id.img_event);

			try {
				photoPath = (String) event.get("photo");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	
       Log.d("AFTER EVENT", "after getting json.before getting strings");
        	// Map all of the data from the event post JSON to the Text Views
        try{
        	 String TAG_TITLE = event.getString("title");
        	// String TAG_LIKES = event.getString("likes");
        	 String TAG_DATE = event.getString("event_date");
        	 String TAG_START_TIME = event.getString("startTime");
        	 String TAG_END_TIME = event.getString("endTime");
        	 String TAG_DESCRIPTION = event.getString("description");
        	 String TAG_LOCATION_NAME = event.getString("locationname");
        	 String TAG_ADDRESSLINE1 = event.getString("addressline1");
        	 String TAG_ADDRESSLINE2 = event.getString("addressline2");
        	 String TAG_CITY = event.getString("city");
        	 String TAG_STATE = event.getString("state");
        	 String TAG_ZIPCODE = event.getString("zipcode");
        	 String TAG_USER =	event.getString("user");
        	 
        	 
        	 // Format the date
        	 
        	 TAG_DATE = DateFormater.convertDateToDay(TAG_START_TIME);
        	 Log.d("DATE Format in Detail", TAG_DATE);
        	 
        	 TAG_START_TIME = DateFormater.convertDateToTime(TAG_START_TIME);
        	 
        	 if(TAG_END_TIME != "")			// Format the endTime if it wasn't left blank
        		 TAG_END_TIME = DateFormater.convertDateToTime(TAG_END_TIME);
        	 
        	 // theUserName = getUserName(TAG_USER);		// HTTP request to get the Users username
        	 
        	 // Here we set the text Values on all of our TextViews (labels so to speak) in our User Interface layout
        	title.setText(TAG_TITLE);
        //	likes_count.setText(TAG_LIKES);
        	date.setText(TAG_DATE);
        	startTime.setText(TAG_START_TIME);
        	endTime.setText(TAG_END_TIME);
        	description.setText(TAG_DESCRIPTION);
        	locationName.setText(TAG_LOCATION_NAME);
        	addressLine1.setText(TAG_ADDRESSLINE1);
        	addressLine2.setText(TAG_ADDRESSLINE2);
        	city.setText(TAG_CITY);
        	state.setText(TAG_STATE);
        	zipcode.setText(TAG_ZIPCODE);
        	user.setText(theUserName);
        	
        } catch(JSONException e){
        	Log.e("Mapping data to TextView"," Could not map the data to the Text Views. Possible mapping value not found");
        }
        
      //Picasso image loader library starts here
		try{
			
		
		Picasso.with(mContext).load(SITE_URL + photoPath) // Photo URL
		.placeholder(R.drawable.logo)		// This image will be displayed while photo URL is loading
		.error(R.drawable.ic_launcher)			// If error shows up during downloading
		.fit().centerCrop()	// settings , fit() Attempt to resize the image to fit exactly into the target ImageView's bounds.
							//centerCrop Crops an image inside of the bounds specified by resize(int, int) rather than distorting the aspect ratio.
		.into(event_image);		// We put it into our layout component (image view)
		} catch (Exception e){
			e.printStackTrace();
			Log.e("Picasso", "Error Loading image with picasso");
		}
        
	    } // End of setEventData
	    
	    // Gets all the information from the 'User' object. Returns the users username
	    public String getUserName(String user_uri) throws JSONException, ClientProtocolException, IOException{
	    	
	    	// This function could be updated to get all of the users information by returning the whole JSON Object instead of just the username
	    	String BASE_URL = "http://lokalapp.co";
	    	String username = "";
	    	
	    	// Get the logged in users username and password
	    	SharedPreferences sharedPreferences;
	    	final String MyPREFERENCES = "MyPrefs" ;
			sharedPreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);

	    	String myUsername= sharedPreferences.getString("username", ""); 
	    	String myPassword = sharedPreferences.getString("password", "");
	    	
	    	DefaultHttpClient client = new DefaultHttpClient();
			final String URL = BASE_URL + user_uri;
			HttpGet get = new HttpGet(URL);
			Credentials cred = new UsernamePasswordCredentials(myUsername,myPassword);		// creating our user credentials to pass to the client
			client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST,AuthScope.ANY_PORT), cred);
			
		
			HttpResponse response = client.execute(get);	// Executes our post request
			int status = response.getStatusLine().getStatusCode();
			
			if(status == 200){
				Log.d("EventDetail", "Status code: "+status);		// Log to show in debugger

				HttpEntity entity = response.getEntity();	// Gets Entity from response, returns null if no entity
				String data = EntityUtils.toString(entity);	// Turns entity data into a string
			
	    	JSONObject userObject = new JSONObject(data);
	    	
	    	username = userObject.getString("username");
	    	
			}
	    	
	    	return username;
	    }
	    
	    
	    @SuppressWarnings("null")
		public void checkNull(TextView view){
	    	
	    	// Checks to see if a text view is null, if it is we set it to blank " " so that 
	    	// it won't show up as "null" in the application
	    	if(view.getText() == "null" || view.getText().length() == 0)
	    		view.setText(" ");
	    }


	    public void checkNullAll(){
	    	/* Performs the checkNull function on all TextViews.. set to blank if value is null
	    	
	    	Our TextViews: title,likes_count,date,startTime,endTime,description,addressLine1,addressLine2,
   		 		city,state,zipcode,user= null;
   		 	*/
	    	
	    	checkNull(title);
	    	checkNull(date);
	    	checkNull(startTime);
	    	checkNull(endTime);
	    	checkNull(description);
	    	checkNull(locationName);
	    	checkNull(addressLine1);
	    	checkNull(addressLine2);
	    	checkNull(city);
	    	checkNull(state);
	    	checkNull(zipcode);
	    	//checkNull(user);
	    }
}// End of EventDetail Class
