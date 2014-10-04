package com.tech.lokal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";
	
	// Constructor 
	
	public JSONParser(){
		
	}
	
	/*
	 * Making Service call 
	 * 
	 * @url - url to make request
	 * @method - http request method
	 * 
	 */
	
	public String makeHttpRequest(String url, String method){
		return this.makeHttpRequest(url, method,null);
	}
	
	public JSONObject getJSONFromUrl(String url,List<NameValuePair> params){
		//Making Http Request
		
		try{
			//defaultHttpclient
			DefaultHttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse response = client.execute(httpPost);
			HttpEntity httpEntity = response.getEntity();
			is = httpEntity.getContent();
		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		}catch(ClientProtocolException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	
	
	try{
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(is,"iso-8859-1"),8);
		StringBuilder sb = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null){
			sb.append(line + "\n");
		}
		is.close();
		json = sb.toString();		// puts the String from the String Builder into the String json variable
		Log.e("JSON", json);
		
	} catch(Exception e){
		Log.e("Buffer Error", "Error Converting result" + e.toString());
	}

	// try to parse the string to JSON object
	
	try{
		jObj = new JSONObject(json);
	} catch (JSONException e){
		Log.e("JSON Parser", "Error parsing data" + e.toString());
	}
	
	// return JSON object 
	return jObj;
	
 }	// End of getStringFromUrl
	
	// fucntion to get json from url
	// by making HTTP POST or GET
	
	public String makeHttpRequest(String url, String method,List<NameValuePair> params){
		
		// Making Http request
		
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			// check for request method
			if(method == "POST"){
				HttpPost httpPost = new HttpPost(url);
				if(params != null){
					httpPost.setEntity(new UrlEncodedFormEntity(params));
				}
				HttpResponse response = client.execute(httpPost);
				HttpEntity httpEntity = response.getEntity();
				is = httpEntity.getContent();
			} else if ( method == "GET"){
				if(params != null){
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString; 	// Alter this line if request not coming out correctly, this line may be USELESS..come back and check
				}
				HttpGet httpGet = new HttpGet(url);
				
				HttpResponse response = client.execute(httpGet);
				HttpEntity httpEntity = response.getEntity();
				is = httpEntity.getContent();
			}
		} catch(UnsupportedEncodingException e){
			e.printStackTrace();
		} catch(ClientProtocolException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			BufferedReader reader = new BufferedReader(new 
					InputStreamReader(is,"iso-8859-1"),8);
			
			StringBuilder sb = new StringBuilder();
			String line = null;
			while( (line = reader.readLine()) != null ){
				sb.append(line + "\n");		// adds the read line to the String builder then calls '\n' to go to the next line
			}
			is.close(); 	// Done working with input stream so close it
			json = sb.toString();	// puts the String representation we built int the json string 
		} catch (Exception e){
			Log.e("Buffer Error", "Error Converting result" + e.toString());
		}
		
		// return the JSON String 
		return json;
	}
}
