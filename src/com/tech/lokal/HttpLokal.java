package com.tech.lokal;

// HTTP class where I'll be receiving information

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.tech.lokal.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HttpLokal extends Activity{
	
	TextView httpStuff,tvDate;
	HttpClient client;		// Our general Http client to handle http request
	JSONObject json;		// Need to create a json object that we'll create in the lasttweet method
	
	// the url/api we'll be using to get data from
	final static String URL = "http://lokalapp.co/api/event/?format=json";	
	
	// added the below 2 lines to be able to implement StrictMode to allow for networking access
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.httpex);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		httpStuff = (TextView) findViewById(R.id.tvHttp);
		tvDate = (TextView) findViewById(R.id.tvDate);
		
		client = new DefaultHttpClient();
		System.out.println("before execution");
		// How we're going to execute the read method
		new Read().execute("limit");		// put 'time' as the argument to get the object with that 'time' value
		// this is actually the key that we're looking for within the JSONObject
			
		
		
		/*
		GetMethodLokal test = new GetMethodLokal();
		String returned;
		try {
			returned = test.getInternetData(); // put the returned string of internet data into our textView String to be displayed
			httpStuff.setText(returned);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	public JSONObject lastTweet(String username) throws ClientProtocolException,IOException, JSONException{
		System.out.println("in last tweet");
		StringBuilder url = new StringBuilder(URL); 	//Creating a String builder from our original URL string
		//url.append(username);	// appends the username to the url, so we can query for the users tweets(data)
		HttpGet get = new HttpGet(url.toString());
		
		HttpResponse response = client.execute(get);
		int status = response.getStatusLine().getStatusCode();	// Check if the client execute() function cleared
		
		/*
		 * Status Code:
		 * 1** or 100 - Information
		 * 2** or 200 - Success
		 * 3** or 300 - Redirection
		 * 4** or 400 - Client Error
		 * 5** or 500 - Server error
		 * 			We want the status code 200 
		 */
		if(status == 200){
			System.out.println("Got Back information from server");
			HttpEntity entity = response.getEntity();
			String data = EntityUtils.toString(entity);		// Returns the string of our entity
			
			// Setup JSON Array and Object to return
			JSONObject events = new JSONObject(data);		// Use JSONArray normally, right now I queried a api with one JSON object that's why I used JSONObject
			//JSONObject last = events.getJSONObject(0);		// zero index gets the most recent object( would get the most recent tweet if using twitter api)
			System.out.println("Got JSON object: succes");
			System.out.println(events.toString());
			return events;
		}else{
			Toast.makeText(HttpLokal.this, "Error", Toast.LENGTH_SHORT);
			System.out.println("Status code Error");
			return null;
			
		}
		
	}
	
	// Setup a Async Task to read the most recent JSON Object
	public class Read extends AsyncTask<String,Integer,String>{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				json = lastTweet("-"); // Normally with the new boston tut i'd pass username to get the username value, here I'm not because I"m not using twitter api
				return json.getString(params[0]); 	// params at position 0 is the first parameter we called in the onCreate methode using "new Read().execute(params[0])"
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		// After doInBackground() returns it's going to call the onPostExecute() method
		@Override
		protected void onPostExecute(String result) {
			// Here we use the returned information in the string and set our variables
			// in this case just setting our text view
			httpStuff.setText(result);
			/*try {
				tvDate.setText(json.getString("category"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  */
		}

		
		
	}
	

}
