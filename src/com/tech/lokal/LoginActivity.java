package com.tech.lokal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.tech.lokal.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.TargetApi;
import android.app.AliasActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class LoginActivity extends ActionBarActivity {

	// Create HTTP client  . .Will be used in Login() 
		//HttpClient client;  // Our Default client that will handle Http request
		JSONObject json; 	// The JSON object the will contain our returned information 
		String username,password; 	// user's username and password, will be set later
		
		// URL/API that will be used for User Authentication
		
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		// Setting Strict Mode to avoid Network errors
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		// Sets the Username and Password EditText objects so we can use the getText() method later
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		
		
		
		//client = new DefaultHttpClient(); 
		
		// Listening to Register new Account link 
		registerScreen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Switching to Register Screen
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
			}
		});
		
		//Listening to Login Button
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v){
				//Go to Categories view 
				
				
				//implements Login() function
				try {
					Login(username.getText().toString(),password.getText().toString());
					
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
			}
		});
		/*
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		*/
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
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);
			return rootView;
		}
	}
	
	// Function used to Login 
	public void Login(String username,String password) throws ClientProtocolException, IOException,JSONException{
		
		DefaultHttpClient client = new DefaultHttpClient();
		final String URL = "http://lokalapp.co/api/auth/user/?format=json&username="+username;
		HttpGet get = new HttpGet(URL);
		Credentials cred = new UsernamePasswordCredentials(username,password);		// creating our user credentials to pass to the client
		client.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST,AuthScope.ANY_PORT), cred);
		
	
		HttpResponse response = client.execute(get);	// Executes our post request
		int status = response.getStatusLine().getStatusCode();
		
		if(status == 200){
			Log.d("LoginActivity", "Status code: "+status);		// Log to show in debugger

			HttpEntity entity = response.getEntity();	// Gets Entity from response, returns null if no entity
			String data = EntityUtils.toString(entity);	// Turns entity data into a string
			
			JSONObject alldata = new JSONObject(data);	// Gets the whole JSON Object from the request,turns into a JSON Object
			JSONArray users = alldata.getJSONArray("objects");		// Gets the JSON Array of Users
			
			/* Gets the first JSONObject in the Array and gets the username value from it.
			 *   **NOTE** Since we query with one user with the "&username=" paramater in the
			 *   URL variable we should only be getting back one user or JSONObject 
			 *   so it will be Object at index zero.  
			 *    ---- If we want to loop through objects we can do so with a for loop and use variable 'i' in place of the index
			 * 
			 */
			String theUser = users.getJSONObject(0).getString("username");	
			
			// Create a Toast message saying user logged in
			Context context = getApplicationContext();	// application context, Can use 'LoginActivity.this' in place of the context argument
			String text = "Logged in as " + theUser;
			int duration = Toast.LENGTH_SHORT;
			
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			Intent i = new Intent(getApplicationContext(),CategoryActivity.class);		
			startActivity(i);		// Go to Category Activity /view
			
		}
		else{
			Log.d("LoginActivity"," Whole Status line" + response.getStatusLine().toString());
			Log.d("LoginActivity", "Status code: "+status);		// Log to show in debugger
			// Create a Toast message saying user logged in
			Context context = getApplicationContext();	// application context, Can use 'LoginActivity.this' in place of the context argument
			String text = "Error: Error code " + status;
			int duration = Toast.LENGTH_SHORT;
			
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		
		}
		
	}

}
