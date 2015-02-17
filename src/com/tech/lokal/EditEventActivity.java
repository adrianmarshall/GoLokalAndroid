package com.tech.lokal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import com.squareup.picasso.Picasso;

public class EditEventActivity extends Activity implements OnClickListener{

	private Context mContext;
	//Progress Dialog
	private ProgressDialog pDialog;
	private JSONObject event = null;
	private final String SITE_URL = "http://192.168.1.3:8000/";		// TODO Change url when ready for production
	
	
	// Create TextViews to hold event information
    
    private TextView editTitle,editDate,editDescription,editLocationName,editAddressLine1,editAddressLine2,
    		 editCity,editState,editZipcode,editPrice,editUser= null;
    
    private String startTime,endTime,eventDay;		// Times for event, String SQL Format
    private boolean startTimeSet, endTimeSet, eventDaySet = false;
    private String base64Photo;
    private Button btnUpdateEvent, btnDeleteEvent,btnShowDate,btnStartDatePicker,btnEndDatePicker,btnPhoto,btnStartTime;
    private int mYear,mMonth,mDay;	// Year,Month, and Day for the event
	private static int RESULT_LOAD_IMAGE = 1; 		// This is to handle the result back when an image is selected from Image Gallery.

    private Date event_date,event_startTime,event_endTime;
    private Spinner spinStates,spinCategories;		// Spinner for the states & categories
    private String[] statesList,categoriesList;
   
    private ImageView event_image = null;		// Image view holder for the picture of the event
    private Bitmap eventPhoto = null; 			// Bitmap image that we will get from the user and send to the server
    
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
	        btnPhoto = (Button) findViewById(R.id.editEbtnPhoto);
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
	    	
	    	String URL_Event = "http://192.168.1.9:8000/api/event/?format=json";
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
				
				// Gets the event parameters and sends them to the server to be updated.
				new uploadEvent().execute(getEventParams());
				
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
			
			// When user clicks on the Photo button
			if(v == btnPhoto){
				
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
						);
				startActivityForResult(i,RESULT_LOAD_IMAGE);
				
			}
			
		}
		
		
// 2 Functions below to handle uploading the image
		// The function called when the user is done picking a photo from the gallery
		@Override
		protected void onActivityResult(int requestCode,int resultCode,Intent data){
			super.onActivityResult(requestCode, resultCode, data);
			
			// TODO get required width and height from photo 
			int reqWidth = 150;
			int reqHeight = 250;
			
			if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
				Uri selectedImage = data.getData();
				String[] filePathColumn = {MediaStore.Images.Media.DATA};
				
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				
				// TODO:  Add a Try-catch block to catch OutOfMemory Exception when Bitmap is too big. If it is, Then resize
				 //Creating BitmapFactory options to resize/downsize the image if it's too big
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;		//Setting the inJustDecodeBounds property to true while decoding avoids memory allocation error( OutOfMemory error)
				
				// Get image
				BitmapFactory.decodeFile(picturePath,options);
				
				// Caluclate inSampleSize
				options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
				
				// Decode bitmap with inSampleSize set 
				options.inJustDecodeBounds = false;
				eventPhoto = BitmapFactory.decodeFile(picturePath,options);
				
				if(eventPhoto != null){
					Toast.makeText(getApplicationContext(), "Photo has been picked", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(getApplicationContext(), "Couldn't get the photo. Please pick the photo again", Toast.LENGTH_SHORT).show();
				}
				
				
			}
			
		}
		// Gets the sample size of the photo the user picked so we can resize it
		public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;

	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }

	    return inSampleSize;
	}
		
		// Converts image to Base64 encoding.. The django server side of Go Lokal takes the image in base64 format and decodes it
		 public String convert_bitmap_to_string(Bitmap bitmap)
		    {
			 // Re-size image before uploading or will get an outOfMemory error.
			 // In testing..Photos that have been resized to Instagram upload with no problem.
		        ByteArrayOutputStream full_stream = new ByteArrayOutputStream();
		        bitmap.compress(Bitmap.CompressFormat.PNG, 100, full_stream);
		        byte[] full_bytes = full_stream.toByteArray();
		        String Str_image = Base64.encodeToString(full_bytes, Base64.DEFAULT);

		        return Str_image; 
		    }
		
		 // Loads all of the information for this event
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

	    	
	        
	    
	        editTitle = (TextView) findViewById(R.id.tfEditTitle);
	      //  likes_count = (TextView) findViewById(R.id.likes_count);
	        editDate = (TextView) findViewById(R.id.editEBtnShowDate);
	        
	        
	        editDescription = (TextView) findViewById(R.id.tfEditDescription);
	        editLocationName = (TextView) findViewById(R.id.tfEditLocationName);
	        editAddressLine1 = (TextView) findViewById(R.id.tfEditAddresssLine1);
	        editAddressLine2 = (TextView) findViewById(R.id.tfEditAddressLine2);
	        editCity = (TextView) findViewById(R.id.tfEditCity);
	        
	        editZipcode = (TextView) findViewById(R.id.tfEditZipcode);
	        editPrice = (TextView) findViewById(R.id.tfEditPrice);

	       
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
        	 String TAG_PRICE = event.getString("price");
        	 
        	 // Set startTime,endTime, and eventDay. Set these variables now so if the user
        	 // doesn't change the date then the same date will be sent back to the API
        	 // IN IT'S SQL FORMAT so we will not get an error converting the time over the server.
        	 eventDay = TAG_DATE;
        	 eventDaySet = true;
        	 
        	 startTime = TAG_START_TIME;
        	 startTimeSet = true;
        	 
        	 endTime = TAG_END_TIME;
        	 endTimeSet = true;
        	 
        	 
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
        	editTitle.setText(TAG_TITLE);
        //	likes_count.setText(TAG_LIKES);

        	editDescription.setText(TAG_DESCRIPTION);
        	editLocationName.setText(TAG_LOCATION_NAME);
        	editAddressLine1.setText(TAG_ADDRESSLINE1);
        	editAddressLine2.setText(TAG_ADDRESSLINE2);
        	editCity.setText(TAG_CITY);
        	editZipcode.setText(TAG_ZIPCODE);
        	editPrice.setText(TAG_PRICE);
        	
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
		
		public class uploadEvent extends AsyncTask<String,String,String>{
			private ProgressDialog pDialog;
			
			@Override 
			protected void onPreExecute(){
				super.onPreExecute();
				
				pDialog = new ProgressDialog(EditEventActivity.this);
				pDialog.setMessage(" Uploading Event...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(false);
				pDialog.show();
				
			}
			
			@Override
			protected String doInBackground(String... params) {
				
				int status = submitEvent(params);		// submits the event
				return status+"";
			}
			
			@Override
			protected void onPostExecute(String data){
				pDialog.dismiss();
				
				//int status = Integer.getInteger(data);
				if(data == "200"){
					Toast.makeText(getApplicationContext(), "Event Updated Successfully", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "Error updating event. Status code: "+data, Toast.LENGTH_LONG).show();
				}
			}
		
		}
		
		// The function that submits the event and updates it on the server side.
		private int submitEvent(String[] event){
			// get information out of event to be more readable in this section of code
			String title,locationName,addressline1,addressline2,startTime,endTime,city,state,zipcode,description,
			category,price,username,event_id;
			
			title = event[0];
			locationName = event[1];
			addressline1 = event[2];
			addressline2 = event[3];
			city = event[4];
			state = event[5];
			zipcode = event[6];
			startTime = event[8];
			endTime = event[9];
			description = event[10];
			category = event[11];
			price = "0"; // event[12]; 	TODO check on this to make sure we can get the price argument 
			event_id = event[14];	
			username = getUserNameFromPref();		
			
			Log.d("price: ", event[12]);
			
			
			
			String updateEventURL = "http://192.168.1.9:8000/api/Mobile_UpdateEvent/";		// API URL to update an event TODO : create API
			InputStream is = null;
			String result = "";		//
			int status = 0;
			
			Log.d("submitEvent", "in Submit Event");
			try{
				DefaultHttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(updateEventURL);
				
				
				List<NameValuePair> postParams = new ArrayList<NameValuePair>(); 		// Array list of NameValuePairs to hold user information

				postParams.add(new BasicNameValuePair("id",event_id));
				postParams.add(new BasicNameValuePair("title",title));
				postParams.add(new BasicNameValuePair("locationname",locationName));
				postParams.add(new BasicNameValuePair("startTime",startTime));
				postParams.add(new BasicNameValuePair("endTime",endTime));
				postParams.add(new BasicNameValuePair("addressline1",addressline1));
				postParams.add(new BasicNameValuePair("addressline2",addressline2));
				postParams.add(new BasicNameValuePair("city",city));
				postParams.add(new BasicNameValuePair("state",state));
				postParams.add(new BasicNameValuePair("zipcode",zipcode));
				postParams.add(new BasicNameValuePair("description",description));
				postParams.add(new BasicNameValuePair("price",price));
				postParams.add(new BasicNameValuePair("category",category));
				postParams.add(new BasicNameValuePair("photo",base64Photo));
				// String name = "adrian";
				postParams.add(new BasicNameValuePair("username",username));
				postParams.add(new BasicNameValuePair("event_date",eventDay));
				
				
				
				// Execute post request to the API to update the event
				
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);
				post.setEntity(entity);
				HttpResponse response = client.execute(post);
				HttpEntity responseEntity  = response.getEntity();
				is = responseEntity.getContent();
				
				 status = response.getStatusLine().getStatusCode();
				
				 Log.v("status: ","" + status);
				 
			}catch(Exception e){
				Log.e("RegisterActivity:","Error in Http connection" + e.toString());
			}
			
			// Convert response to String
			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				
				while((line = reader.readLine()) != null){
					sb.append(line + "\n");
				}
				
				is.close();
				result = sb.toString();
				
				Log.v("log", "Result: " + result);
				System.out.println(result);
				
				// save results to html file for viewing or any errors
				// result will be saved in file named "myandroiderror.html"
				try{
				
				File file = new File("out.txt");
				FileOutputStream fos = new FileOutputStream(file);
				PrintStream ps = new PrintStream(fos);
				System.setOut(ps);
				System.out.println(result);		// sends result to the file
				PrintStream console = System.out; 
				System.setOut(console);
				
				}catch(Exception e){
					e.printStackTrace();
				}
			} catch (Exception e ){
				Log.v("Log:"," Error converting result " + e.toString());
			}
			/*
			if(status == 500){
				Context context = getApplicationContext();
				String message = result;
				int duration = Toast.LENGTH_LONG;
				
				Toast toast = Toast.makeText(context, message, duration);
				toast.show();
			}
			*/
			
			return status;
			
			
		}
			

		private String getUserNameFromPref(){
			
			// Create shared preferences
			SharedPreferences sharedPreferences;
			
			final String MyPREFERENCES = "MyPrefs" ;
			final String USERNAME_KEY = "username";
			String userName = null;
			
			sharedPreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
			
			// gets the users user name from the shared Preferences
			if(sharedPreferences.contains(USERNAME_KEY)){
				userName = sharedPreferences.getString(USERNAME_KEY, "");
			}
			
			return userName;
		}
	    
	    // Delete the event
	    public class deleteEvent extends AsyncTask<String,String,String>{

		@Override
		protected String doInBackground(String... params) {
			String id= params[0];
			DeleteMyEvent(id);
			return null;
		}
	    	
	    }
	    
	    
	    private String DeleteMyEvent(String event_id){
	    	String returned_message = null;
	    	
	    	return returned_message;
	    }
	    // this function gets and returns a list of all of the parameters that have been filled in
	    // will be called right before the event is sent to the server to be updated.
		private String[] getEventParams() {
			
			String title = editTitle.getText().toString();
			String locationName = editLocationName.getText().toString();
			String addressline1 = editAddressLine1.getText().toString();
			String addressline2 = editAddressLine2.getText().toString();
			String city = editCity.getText().toString();
			String state = spinStates.getSelectedItem().toString();
			String zipcode = editZipcode.getText().toString();
			String description = editDescription.getText().toString();
			String category = spinCategories.getSelectedItem().toString();
			String price = "0"; // editPrice.getText().toString();
			
			
			String event_id = null;
			try {
				event_id = event.getString("id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
			// Already got 'eventDay' , 'startTime' , and 'EndTime' variables when the user set them
			// check to confirm if the program has already set the times for the event
			if(startTimeSet == false && endTimeSet == false && eventDaySet == false){
				
			
			startTime = eventDay+"T"+startTime;
			endTime = eventDay+"T"+endTime;
			}
			//eventDay = eventDay+"T"+startTime;
			
			// IF the user doesn't pick a new image, keep the current one
			if(eventPhoto == null){
				
			// Here we're taking the image that's within the image view, turning it into a bitmap so we can turn that into base64 format
			event_image.buildDrawingCache();
			eventPhoto = event_image.getDrawingCache();		// gets the Bitmap version of the image
			}
			
			
			// Converts the photo to a base64 encoding .. Sending to server in base64 format
			base64Photo = convert_bitmap_to_string(eventPhoto);
			
			
			if(base64Photo != null){
				Log.d("base64:", "Photo converted successfully");
			}else {
				Log.d("base64: ", "Photo failed to convert to base64");
			}
			
			String[] params = {title,locationName,addressline1,addressline2,city,state,zipcode,eventDay,startTime,endTime,description,category,price,base64Photo,event_id};
			
			
			return params;
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
	    	
	    	checkNull(editTitle);
	    	checkNull(editDate);
	    	checkNull(startTime);
	    	checkNull(endTime);
	    	checkNull(editDescription);
	    	checkNull(editLocationName);
	    	checkNull(editAddressLine1);
	    	checkNull(editAddressLine2);
	    	checkNull(editCity);
	    	checkNull(editState);
	    	checkNull(editZipcode);
	    	//checkNull(user);
	    }


	
}
