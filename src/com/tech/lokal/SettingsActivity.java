package com.tech.lokal;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener{
	
	private TextView tvUsername, tvEmail,tvCurrentPasswordLabel,tvNewPasswordLabel;
	private EditText editCurrentPassword,editNewPassword;
	private Button btnChangePassword,btnSubmitChanges;
	private ProgressDialog pDialog;
	private String old_password,new_password;
	

	SharedPreferences sharedPreferences;		// shared preferences will be used to store username & password information
	
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String passwordKey = "password";
	public static final String URL_ChangePassword = "http://lokalapp.co/api/mobile_changePassword/";
	
	
	
	@Override 
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		
		
		tvUsername = (TextView) findViewById(R.id.tvUsername);
		tvEmail = (TextView) findViewById(R.id.tvEmail);
		tvCurrentPasswordLabel = (TextView) findViewById(R.id.tvCurrentPasswordLabel);
		tvNewPasswordLabel = (TextView) findViewById(R.id.tvNewPasswordLabel);
		
		editCurrentPassword = (EditText) findViewById(R.id.editCurrentPassword);
		editNewPassword = (EditText) findViewById(R.id.editNewPassword);
		
		btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
		btnSubmitChanges = (Button) findViewById(R.id.btnSubmitChanges);
		
		// set password text and edit fields to invisible until the user is ready to change them
		tvCurrentPasswordLabel.setVisibility(View.INVISIBLE);
		tvNewPasswordLabel.setVisibility(View.INVISIBLE);
		editCurrentPassword.setVisibility(View.INVISIBLE);
		editNewPassword.setVisibility(View.INVISIBLE);
		btnSubmitChanges.setVisibility(View.INVISIBLE);
		
		
		// onClick listeners for objects 
		btnChangePassword.setOnClickListener(this);
		btnSubmitChanges.setOnClickListener(this);
		
		// Get users username and e-mail 
		sharedPreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
		
		if(sharedPreferences.contains("username")){
			tvUsername.setText(sharedPreferences.getString("username", ""));
		}
		
		
	}

	// Adding click functionality 
	@Override
	public void onClick(View v) {
	
		
		if(v == btnChangePassword){
			
			// Setting password labels visible to allow user to input current and new password
			
			tvCurrentPasswordLabel.setVisibility(View.VISIBLE);
			tvNewPasswordLabel.setVisibility(View.VISIBLE);
			editCurrentPassword.setVisibility(View.VISIBLE);
			editNewPassword.setVisibility(View.VISIBLE);
			btnSubmitChanges.setVisibility(View.VISIBLE);
			
		}
		
		if(v == btnSubmitChanges){
			//TODO add functionally to make RESTful API call to submit password changes
			
			//Toast toast = Toast.makeText(SettingsActivity.this, "Test Changing password", Toast.LENGTH_SHORT);
			//toast.show();
			new changePassword().execute();
		}
	}
	
	
	public class changePassword extends AsyncTask<String,String,String>{

		@Override
		protected void onPreExecute(){
			// Display loading Dialog
			super.onPreExecute();	// Runs the UI thread before doInBackground
			pDialog = new ProgressDialog(SettingsActivity.this);
			pDialog.setMessage("Changing Password...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			int statusCode = passwordChange();
			return String.valueOf(statusCode);
		}
		
		protected void onPostExecute(String params){
			pDialog.dismiss();		// Close progress Dialog
			
			// Get status code
			int statusCode = Integer.parseInt(params);
			Log.v("Status Code:", " "+ statusCode);
			
			// Show a successful password changed message if status code comes back 200 or "OK"
			if(statusCode == 200){
				String message = "Code: "+ statusCode+ ". Password successfully changed";
				Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
				toast.show();
				
				// Store the users new password
				Editor editor = sharedPreferences.edit();
				editor.putString(passwordKey, new_password);
				editor.commit();		// Saves the new data (password) we just added/changed in the preferences
				
				// Go back to category view
				Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
				startActivity(i);
				
			}else if(statusCode == 500){
				String message = "Code: " + statusCode + ". Check to make sure your current password is correct";
				Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
				toast.show();
			}
		}
		
	}
	
	public int passwordChange(){
		
		int statusCode = 0;
		
		InputStream is = null;
		String result = "";	
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();		// Array list of NameValuePairs to hold user information
		
		Log.d("settings", "in settings");
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(URL_ChangePassword);
			
			String json = "";
			
			// Get data 
			String username = tvUsername.getText().toString();
			old_password = editCurrentPassword.getText().toString();
			new_password = editNewPassword.getText().toString();
			
			postParams.add(new BasicNameValuePair("username",username));
			postParams.add(new BasicNameValuePair("old_password",old_password));
			postParams.add(new BasicNameValuePair("new_password",new_password));

			// Execute post request to the URL to register new USERS
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);
			post.setEntity(entity);
			HttpResponse response = client.execute(post);
			HttpEntity responseEntity  = response.getEntity();
			is = responseEntity.getContent();
			
			 statusCode = response.getStatusLine().getStatusCode();
			
			 Log.v("status: ","" + statusCode);
			
			// Set some headers to inform the server about the type of Content we're sending over
			
			 statusCode = response.getStatusLine().getStatusCode();
			
			 Log.v("status: ","" + statusCode);
			 
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
		
		return statusCode;
	}

}
