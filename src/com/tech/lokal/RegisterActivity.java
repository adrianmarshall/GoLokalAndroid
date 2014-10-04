package com.tech.lokal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// Set View to register.xml
		setContentView(R.layout.register);
		
		TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
		Button btnRegister = (Button) findViewById(R.id.btnRegister);
		
		final EditText email = (EditText) findViewById(R.id.reg_email);
		final EditText username = (EditText) findViewById(R.id.reg_username);
		final EditText password = (EditText) findViewById(R.id.reg_password);
		final EditText repassword = (EditText) findViewById(R.id.reg_repassword);
		
		// Setting Strict Mode to avoid Network errors
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
				StrictMode.setThreadPolicy(policy);
		
		// Listening to Login Screen link
		loginScreen.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Closing registration screen
			// Switching to Login Screen/closing Screen
				finish();
				
			}
		});
		
		
		// Listening to Register Button
		btnRegister.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String textEmail = email.getText().toString();
				String textUsername = username.getText().toString();
				String textPassword = password.getText().toString();
				String textRePassword = repassword.getText().toString();
				
				// Check to make sure the user has entered the same password for both password fields
				
				if(!(textPassword.equals(textRePassword))){
					
					Context context = getApplicationContext();
					String message = "Your passwords do not match. Please enter matching passwords";
					int duration = Toast.LENGTH_LONG;
					
					Toast toast = Toast.makeText(context, message, duration);
					toast.show();
					
					Log.v("paswords(1st,re-entered)",textPassword.equals(textRePassword) +"-" +textPassword +"-" + textRePassword);
					
					return;
					
				}
				
				// Execute Async Task to register the new user 
				new registerUser().execute(textEmail,textUsername,textPassword);
				
			}
		});
		
		
	}
	
	public class registerUser extends AsyncTask<String,String,String>{

		private ProgressDialog pDialog;
		private String username,password,email;
		
		@Override
		protected void onPreExecute(){
			// loading dialog box
			super.onPreExecute();
			
			pDialog = new ProgressDialog(RegisterActivity.this);
			pDialog.setMessage("Creating new user...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			 email = params[0];
			 username = params[1];
			 password = params[2];
			 
			 
			 Log.d("doInBackground:", "Before register");
			 
			int statusCode = register(email,username,password);
			
			Log.d("doInBackground:", "Finished. Going to post execute");
			
			return String.valueOf(statusCode) ;
		}
		
		protected void onPostExecute(String params){
			pDialog.dismiss();	// Dismiss Dialog
			
			int statusCode = Integer.parseInt(params);
			Log.v("Status Code:", " "+ statusCode);
			
			if(statusCode == 200 || statusCode == 201){
				
				
				
				Log.v("Status Code:", " "+ statusCode);
				
				Context context = getApplicationContext();
				String message = " User Successfully registered! ";
				int duration = Toast.LENGTH_SHORT;
				
				Toast toast = Toast.makeText(context, message, duration);
				toast.show();
				
				// Goes to the LoginActivity and attempts to log the user in with the new credentials
				Intent login = new Intent(getApplicationContext(),LoginActivity.class);
				startActivity(login);
			}
			else{
				
				String message = "Error: " + statusCode + "! User already exist ";
				int duration = Toast.LENGTH_LONG;
				
				Context context = getApplicationContext();
				Toast toast = Toast.makeText(context, message, duration);
				toast.show();
				
			}
			
			
		}
		
	}
	
	public int register(String email,String username, String password){
		
		String registerURL = "http://lokalapp.co/api/register/?format=json";		// API URL to register a new User with Go Lokal
		InputStream is = null;
		String result = "";		//
		int status = 0;
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();		// Array list of NameValuePairs to hold user information
		
		Log.d("register", "in register");
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(registerURL);
			
			String json = "";
			
			// Build jsonObject
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.accumulate("email",email);
			jsonObject.accumulate("username",username);
			jsonObject.accumulate("password",password);
			
			// convert JSONObject to JSON string
			json = jsonObject.toString();
			
			//Set json to String Entitiy 
			StringEntity se = new StringEntity(json);
			
			// Set post Entity
			post.setEntity(se);
			
			// Set some headers to inform the server about the type of Content we're sending over
			post.setHeader("Accept","application/json");
			post.setHeader("Content-type","application/json");
			
			// Execute post request to the URL to register new USERS
			
			HttpResponse response = client.execute(post);
			HttpEntity entity  = response.getEntity();
			is = entity.getContent();
			
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
}
