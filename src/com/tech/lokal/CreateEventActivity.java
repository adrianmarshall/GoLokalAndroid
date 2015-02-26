package com.tech.lokal;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateEventActivity extends Activity implements OnClickListener{

	// Declare variables
	
	public static int RESULT_LOAD_IMAGE = 1; 		// This is to handle the result back when an image is selected from Image Gallery.
	
	
	// Date picker
	// Buttons
	Button btnPickDate,btnSubmit,btnStartTimePicker,btnEndTimePicker,btnPhoto;
	
	String startTime,endTime,eventDay,dateTime,base64Photo;		// Strings to hold Date time information
	
	
	// EditText Fields 
	EditText editTitle,editLocationName,editAddressline1,editAddressline2,editCity,editZipcode,editDescription,editPrice;
	
	// Spinners
	Spinner spinCategories;
	Spinner spinState;
	
	// Event Photo
	Bitmap eventPhoto;
	
	// Variables or storing current date and time
	private int mYear,mMonth,mDay,mHour, mMinute;
	
	// Get Preferences to get the users username
	SharedPreferences sharedPreferences;
	
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String usernameKey = "username";
	
	private String username;
	
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
		
		
		 
		 btnPickDate = (Button) findViewById(R.id.editEBtnShowDate);
		 
		 
		 // buttons 
		  btnSubmit = (Button) findViewById(R.id.editEBtnUpdateEvent);
		  btnStartTimePicker = (Button) findViewById(R.id.editEbtnStartTimePicker);
		  btnEndTimePicker = (Button) findViewById(R.id.editEbtnEndTime);
		  btnPhoto = (Button) findViewById(R.id.editEbtnPhoto);
		  
		 
		 
		
		 spinCategories = (Spinner) findViewById(R.id.editEspinnerCategories);
		 spinState = (Spinner) findViewById(R.id.editEspinnerState);
		
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
		spinCategories.setSelection(spinPosition);
		
		spinState.setAdapter(adapterState);
		spinState.setSelection(spinPosition);
		
		 editTitle = (EditText) findViewById(R.id.tfEditTitle);
		 editLocationName = (EditText) findViewById(R.id.tfEditLocationName);
		 editAddressline1 = (EditText) findViewById(R.id.tfEditAddresssLine1);
		 editAddressline2 = (EditText) findViewById(R.id.tfEditAddressLine2);
		 editCity = (EditText) findViewById(R.id.tfEditCity);
		 editZipcode = (EditText) findViewById(R.id.tfEditZipcode);
		 editDescription = (EditText) findViewById(R.id.tfEditDescription);
		 editPrice = (EditText) findViewById(R.id.tfEditPrice);
		 
		 // Get the users username
		sharedPreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
		if(sharedPreferences.contains(usernameKey)){
			username = sharedPreferences.getString(usernameKey, "");
		}
		 
		
		btnPickDate.setOnClickListener(this);
		
		btnSubmit.setOnClickListener(this);
		btnStartTimePicker.setOnClickListener(this);
		btnEndTimePicker.setOnClickListener(this);
		btnPhoto.setOnClickListener(this);
		
	}
	
	@Override		// Added functionality to buttons Clicked on
	public void onClick(View v) {
		
		// When users click button to pick the date
		if(v == btnPickDate){
			
			// Process to get Current DAte
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
			
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
		
		// When the user clicks the button to pick the time
		if(v == btnStartTimePicker){
			
			
			// Process to get Current Time
			final Calendar c = Calendar.getInstance();
			mHour = c.get(Calendar.HOUR_OF_DAY);	// Would use Calendar.HOUR to get the 12 hour clock but we're sending time to the server which processes time in 24 hour clock
			mMinute = c.get(Calendar.MINUTE);
			
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
		
	if(v == btnEndTimePicker){
			
			
			// Process to get Current Time
			final Calendar c = Calendar.getInstance();
			mHour = c.get(Calendar.HOUR_OF_DAY);	// Would use Calendar.HOUR to get the 12 hour clock but we're sending time to the server which processes time in 24 hour clock
			mMinute = c.get(Calendar.MINUTE);
			
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
	
	if(v == btnSubmit){
		
		if(eventDay == null)
			Toast.makeText(getApplicationContext(), "Please pick a Day for the Event", Toast.LENGTH_LONG).show();
		
		if(startTime == null){
			Toast.makeText(getApplicationContext(), "Please select a Start Time", Toast.LENGTH_LONG).show();
			
		}
		if(endTime == null)
			Toast.makeText(getApplicationContext(), "Please select an End Time", Toast.LENGTH_LONG).show();
		
		if(eventPhoto == null)
			Toast.makeText(getApplicationContext(), "Please select a Photo", Toast.LENGTH_LONG).show();
		
		if(editTitle.getText().toString() == "")
			Toast.makeText(getApplicationContext(), "Please enter a Title", Toast.LENGTH_LONG).show();
		
		if(editLocationName.getText().toString() == "")
			Toast.makeText(getApplicationContext(), "Please enter a location name", Toast.LENGTH_LONG).show();
		
		if(editAddressline1.getText().toString() == "")
			Toast.makeText(getApplicationContext(), "Please enter an address for address line 1", Toast.LENGTH_LONG).show();
		
		if(editCity.getText().toString() == "")
			Toast.makeText(getApplicationContext(), "Please enter a City", Toast.LENGTH_LONG).show();
		
		if(editZipcode.getText().toString() == "")
			Toast.makeText(getApplicationContext(), "Please enter a zipcode ", Toast.LENGTH_LONG).show();
		
		
		if(startTime != null && endTime != null && eventDay != null && eventPhoto != null){
			
		
		// Already got 'eventDay' , 'startTime' , and 'EndTime' variables when the user set them
		
		// Gets all of the parameters from the Create event view then passes it uploadEvent which uploads the event
		String[] params = getEventParams();
		
		new uploadEvent().execute(params);
		}
	}

		
	}
	
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
	
	
	public class uploadEvent extends AsyncTask<String,String,String>{
		private ProgressDialog pDialog;
		
		@Override 
		protected void onPreExecute(){
			super.onPreExecute();
			
			pDialog = new ProgressDialog(CreateEventActivity.this);
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
				Toast.makeText(getApplicationContext(), "Event Created Successfully", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Error creating event. Status code: "+data, Toast.LENGTH_LONG).show();
			}
		}
		//TODO Go to category acitvity
	}
	
	private String[] getEventParams(){
		
		String title = editTitle.getText().toString();
		String locationName = editLocationName.getText().toString();
		String addressline1 = editAddressline1.getText().toString();
		String addressline2 = editAddressline2.getText().toString();
		String city = editCity.getText().toString();
		String state = spinState.getSelectedItem().toString();
		String zipcode = editZipcode.getText().toString();
		String description = editDescription.getText().toString();
		String category = spinCategories.getSelectedItem().toString();
		String price = editPrice.getText().toString();
		
		// Already got 'eventDay' , 'startTime' , and 'EndTime' variables when the user set them
		startTime = eventDay+"T"+startTime;
		endTime = eventDay+"T"+endTime;
		//eventDay = eventDay+"T"+startTime;
		// Converts the photo to a base64 encoding .. Sending to server in base64 format
		base64Photo = convert_bitmap_to_string(eventPhoto);
		
		if(base64Photo != null){
			Log.d("base64:", "Photo converted successfully");
		}else {
			Log.d("base64: ", "Photo failed to convert to base64");
		}
		
		String[] params = {title,locationName,addressline1,addressline2,city,state,zipcode,eventDay,startTime,endTime,description,category,price,base64Photo};
		
		
		return params;
	}
	
	// Converts image to Base64 encoding.. The django server side of Go Lokal takes the image in base64 format and decodes it
	 public String convert_bitmap_to_string(Bitmap bitmap)
	    {
		 //TODO Re-size image before uploading or will get an outOfMemory error.
		 // In testing..Photos that have been resized to Instagram upload with no problem.
	        ByteArrayOutputStream full_stream = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.PNG, 100, full_stream);
	        byte[] full_bytes = full_stream.toByteArray();
	        String Str_image = Base64.encodeToString(full_bytes, Base64.DEFAULT);

	        return Str_image; 
	    }
	
	
	private int submitEvent(String[] event){
		// get information out of event to be more readable in this section of code
		String title,locationName,addressline1,addressline2,startTime,endTime,city,state,zipcode,description,
		category,price;
		
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
		price = "0"; // event[12];
		
		
		Log.d("locationName: ", locationName);
		// 'base64Photo' global variable in CreateEventActivity
		
		
		
		String createEventURL = "http://lokalapp.co/api/mobile_create_event/";		// API URL to create an event
		InputStream is = null;
		String result = "";		//
		int status = 0;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();		// Array list of NameValuePairs to hold user information
		
		Log.d("submitEvent", "in Submit Event");
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(createEventURL);
			
			
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			
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
			
			
			
			// Execute post request to the URL to register new USERS
			
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
		


	
}// End of CreateEventActivity
