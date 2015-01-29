package com.tech.lokal;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditEventActivity extends Activity implements OnClickListener{

	private Context mContext;
	//Progress Dialog
	private ProgressDialog pDialog;
	private JSONObject event = null;
	private final String SITE_URL = "http://192.168.1.3:8000/";		// TODO Change url when ready for production
	
	
	// Create TextViews to hold event information
    
    private TextView title,likes_count,date,description,locationName,addressLine1,addressLine2,
    		 city,state,zipcode,user= null;
    
    private String startTime,endTime,eventDay;		// Times for event
    private Button btnUpdateEvent, btnDeleteEvent,btnShowDate,btnStartDatePicker,btnEndDatePicker,btnPhotoPicker,btnStartTime;
    private int mYear,mMonth,mDay;	// Year,Month, and Day for the event
    private Date event_date,event_startTime,event_endTime;
    private Spinner spinStates,spinCategories;		// Spinner for the states & categories
    private String[] statesList,categoriesList;
   
    private ImageView event_image = null;		// Image view holder for the picture of the event
    
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        // The activity is being created.
	        setContentView(R.layout.activity_edit_event);
	        
	        
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
	        
	        // assigning buttons from the xml/android GUI to their progmatic counterparts
	        btnUpdateEvent = (Button) findViewById(R.id.editEBtnUpdateEvent);
	        btnDeleteEvent = (Button) findViewById(R.id.editEBtnDeleteEvent);
	        btnShowDate = (Button) findViewById(R.id.editEBtnShowDate);
	        btnStartDatePicker = (Button) findViewById(R.id.editEbtnStartTimePicker);
	        btnEndDatePicker = (Button) findViewById(R.id.editEbtnEndTime);
	        btnPhotoPicker = (Button) findViewById(R.id.editEbtnPhoto);
	        event_image = (ImageView) findViewById(R.id.editEimg_event);		// image holder for event image
	        
	        spinStates = (Spinner) findViewById(R.id.editEspinnerState);
	        spinCategories = (Spinner) findViewById(R.id.editEspinnerCategories);
	        
	     // ************* SETUP SPINNER FOR STATES *********************
			int spinPosition = 0; 		// Spinner Selection position 
			//Create an Array adapter using the string array for states and the default array adapter
			ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);		//  The simple_spinner_item layout is provided by the platform and is the default layout you should use unless you'd like to define your own layout for the spinner's appearance.

			ArrayAdapter<CharSequence> adapterState = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
			
			
			// Specify the layout to use when the spinner shows the list of choices
			adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			adapterState.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			// Apply the adapter to the spinner
			spinCategories.setAdapter(adapterCategory);
	        // Apply the adapter to the Spinners
			
			spinStates.setAdapter(adapterState);
			
			statesList = getResources().getStringArray(R.array.states);		// Gets all of the states from the array located in strings.xml file
			categoriesList = getResources().getStringArray(R.array.categories);		// Gets all of the categories from the array in the strings.xml file
			
			
			
	        // setting onClickListener so that each button listens and reacts to clicks based on the overridden "onClick" method
	        
	        btnUpdateEvent.setOnClickListener(this);
	        btnDeleteEvent.setOnClickListener(this);
	        btnShowDate.setOnClickListener(this);
	        btnStartDatePicker.setOnClickListener(this);
	        btnEndDatePicker.setOnClickListener(this);
	        
	    }

	    // This will return our JSON object
	    public JSONObject getSingleEvent(String event_id){			
	    	
	    	String URL_Event = "http://192.168.1.3:8000/api/event/?format=json";
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
	    
		@Override
		public void onClick(View v) {
			
			// Update button is clicked
			if(v == btnUpdateEvent){
				
			}
			
			// Delete button is clicked 
			if(v == btnDeleteEvent){
				
			}
			
			// Show Date button is clicked 
			if(v == btnShowDate){
				
				// Process to get Current DAte
				final Calendar c = Calendar.getInstance();
				c.setTime(event_date);	// Sets the Date from the event. See setEventData() to see where we construct theDate
				
				// set Year, Month and Day to the current date that's in the database.(we get from our api)
				int mYear = c.get(Calendar.YEAR);
				int mMonth = c.get(Calendar.MONTH);
				int mDay = c.get(Calendar.DAY_OF_MONTH);
				
				// Launch Date Picker Dialog
				DatePickerDialog dpd = new DatePickerDialog(this,
						new DatePickerDialog.OnDateSetListener() {
							
							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear,
									int dayOfMonth) {
								
								eventDay = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;		// Creating a Date in MYSQL format YYYY:MM:DD
								
								// Display selected date
								Toast.makeText(getApplicationContext(), eventDay, Toast.LENGTH_SHORT).show();
								
							}
						},mYear,mMonth,mDay);
				dpd.show();
			}
			
			//Start Date Picker
			if(v == btnStartDatePicker){
				// Process to set the time to the current time the event is already set for
				final Calendar c = Calendar.getInstance();
				c.setTime(event_startTime);	// sets the time
				int mHour = c.get(Calendar.HOUR_OF_DAY);	// Would use Calendar.HOUR to get the 12 hour clock but we're sending time to the server which processes time in 24 hour clock
				int mMinute = c.get(Calendar.MINUTE);
				
				// Launch Time Picker Dialog
				TimePickerDialog tpd = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// Display selected time
						
						startTime = hourOfDay+":"+minute+":00";		// creating Time in MYSQL format
						Log.d("Start Time",startTime);
						Toast toast = Toast.makeText(getApplicationContext(), hourOfDay+":"+minute, Toast.LENGTH_SHORT);
						toast.show();
						
					}
				},mHour,mMinute,false);
				tpd.show();
				
			}
			
			// End Date picker
			if(v == btnEndDatePicker){
				

				// Process to get end time of event 
				final Calendar c = Calendar.getInstance();
				c.setTime(event_endTime);	// Sets the time of Calendar c to the time from the Event
				int mHour = c.get(Calendar.HOUR_OF_DAY);	// Would use Calendar.HOUR to get the 12 hour clock but we're sending time to the server which processes time in 24 hour clock
				int mMinute = c.get(Calendar.MINUTE);
				
				// Launch Time Picker Dialog
				TimePickerDialog tpd = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						// Display selected time
						
						endTime = hourOfDay+":"+minute+":00";
						Log.d("endTime", endTime);
						Toast toast = Toast.makeText(getApplicationContext(), endTime, Toast.LENGTH_SHORT);
						toast.show();
						
					}
				},mHour,mMinute,false);
				
				tpd.show();
			}
			
		}
	

	    public class loadEvent extends AsyncTask<String,String,String>{

	    	
	    	@Override
			protected void onPreExecute(){
				// Display loading Dialog
				super.onPreExecute();	// Runs the UI thread before doInBackground
				pDialog = new ProgressDialog(EditEventActivity.this);
				pDialog.setMessage("Getting Event information...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
				
			}
			
	    	
			@Override
			protected String doInBackground(String... params) {
				String event_id = params[0]; 		// Gets the event ID 
				
				event = getSingleEvent(event_id);
				
				
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
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Sets the Text fields in the UI to the corresponding event data
				//checkNullAll();			// Checks all the TextViews, if null set value to blank " "
				Log.d("onPostExecute", "end of onPost Execute");
				
			}
	    	
	    }
	    
	    public void setEventData() throws ClientProtocolException, IOException, ParseException{

	    	
	        
	    
	        title = (TextView) findViewById(R.id.tfEditTitle);
	      //  likes_count = (TextView) findViewById(R.id.likes_count);
	        date = (TextView) findViewById(R.id.editEBtnShowDate);
	        
	        
	        description = (TextView) findViewById(R.id.tfEditDescription);
	        locationName = (TextView) findViewById(R.id.tfEditLocationName);
	        addressLine1 = (TextView) findViewById(R.id.tfEditAddresssLine1);
	        addressLine2 = (TextView) findViewById(R.id.tfEditAddressLine2);
	        city = (TextView) findViewById(R.id.tfEditCity);
	        
	        zipcode = (TextView) findViewById(R.id.tfEditZipcode);

	       
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
        	 
        	 // Get the Day, Month, and Year from the event and set it when the "Pick Day" button is clicked
        	 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
			 event_date = formatter.parse(TAG_DATE);
		
        	 
        	 // Format the date
        	 
        	 TAG_DATE = DateFormater.convertDateToDay(TAG_START_TIME);
        	 Log.d("DATE Format in Detail", TAG_DATE);
        	 
        	 TAG_START_TIME = DateFormater.convertDateToTime(TAG_START_TIME);		// time format "h:mm a"
        	 formatter.applyPattern("h:mm");	// change formatter pattern
        	 event_startTime = formatter.parse(TAG_START_TIME);		// Stores start time so we can set it 
        	
        	 if(TAG_END_TIME != ""){
        		 // Format the endTime if it wasn't left blank
        	 
        		 TAG_END_TIME = DateFormater.convertDateToTime(TAG_END_TIME);
        		 event_endTime = formatter.parse(TAG_END_TIME);		// Stores end time so we can set it 
        	 } 
        	 // theUserName = getUserName(TAG_USER);		// HTTP request to get the Users username
        	 
        	 // Here we set the text Values on all of our TextViews (labels so to speak) in our User Interface layout
        	title.setText(TAG_TITLE);
        //	likes_count.setText(TAG_LIKES);

        	description.setText(TAG_DESCRIPTION);
        	locationName.setText(TAG_LOCATION_NAME);
        	addressLine1.setText(TAG_ADDRESSLINE1);
        	addressLine2.setText(TAG_ADDRESSLINE2);
        	city.setText(TAG_CITY);
        	zipcode.setText(TAG_ZIPCODE);
        	
        	// Set the state. Loops through the state list and finds the State associated with this selected event
        	for(int i =0; i < statesList.length;i++){
        		if(TAG_STATE == statesList[i]){
        			spinStates.setSelection(i);
        			Log.d("event State: ",statesList[i].toString());	// output the state for debugging pursposes
        			break;
        		}
        	}
        	
        	// Set the category. Loops through the state list and finds the category associated with this selected event
        	for(int i =0; i < categoriesList.length;i++){
        		if(TAG_STATE == categoriesList[i]){
        			spinCategories.setSelection(i);
        			Log.d("event Category: ",categoriesList[i].toString());	// output the category for debugging pursposes
        			break;
        		}
        	}
        	
        } catch(JSONException e){
        	Log.e("Mapping data to TextView"," Could not map the data to the Text Views. Possible mapping value not found");
        }
        
      //Picasso image loader library starts here

		try{
			
		 mContext = getApplicationContext();
       	 String TAG_PHOTO = event.getString("photo");		 // url path to the photo for the event

		
		Picasso.with(mContext).load(SITE_URL + TAG_PHOTO) // Photo URL
		.placeholder(R.drawable.logo)		// This image will be displayed while photo URL is loading
		.error(R.drawable.ic_launcher)			// If error shows up during downloading
		.fit()	// settings , fit() Attempt to resize the image to fit exactly into the target ImageView's bounds.
							//centerCrop() Crops an image inside of the bounds specified by resize(int, int) rather than distorting the aspect ratio.
		.into(event_image);		// We put it into our layout component (image view)
		} catch (Exception e){
			e.printStackTrace();
			Log.e("Picasso", "Error Loading image with picasso");
		}
        
        
	    } // End of setEventData
	    
	   // update the event
	    public class updateEvent extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}
	    	
	    }
	    
	    // Delete the event
	    public class deleteEvent extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}
	    	
	    }
	    
	    @SuppressWarnings("null")
		public void checkNull(TextView view){
	    	
	    	// Checks to see if a text view is null, if it is we set it to blank " " so that 
	    	// it won't show up as "null" in the application
	    	if(view.getText() == "null" || view.getText().length() == 0)
	    		view.setText(" ");
	    }
	    
public void checkNull(String text){
	    	
	    	// Checks to see if a String is null, if it is we set it to blank " " so that 
	    	// it won't show up as "null" in the application
	    	if(text == "null" || text.length() == 0)
	    		text = "";
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


	
}
