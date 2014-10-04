package com.tech.lokal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class GetMethodLokal {

	
	public String getInternetData() throws Exception{
		BufferedReader in = null;		// used to make information we get from webserver readable
		String data = null;			// our returned data
		
		// Try to connect to the HTTP client
		
		try {
			HttpClient client = new DefaultHttpClient();	// Default Http Client
			
			// set URI - Site we'll be referring to to get the data 
			URI website = new URI("");		// Use this to get events in json: http://localhost:8000/api/event/?format=json
			HttpGet request = new HttpGet();	// since we're just trying to "Get" data we're using HttpGet(), if we were trying to post data we'd use HttpPost() I assume
			request.setURI(website);
			
			// use the get method with the set URI
			HttpResponse response = client.execute(request);	// most important method
			// response gets the information but it's unreadable. We use our Buffered Reader to make the information readable
			
			// Buffered reader setup to get data. Creates a Buffered Reader which takes a InputStreamReader argument
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent() ) );
			
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String nl = System.getProperty("line.separator"); 	// line separator to separate our data so it's not all jumbled together
			
			while((line = in.readLine()) != null){		// As long as there is a line available to read, Read that line 
				sb.append(line + nl);		// read line, append the next line and append a new line separator
			}
			in.close();
			data = sb.toString();		// puts our string buffer data into a string
			return data;
			
			
		} finally{
			if(in != null){
				try{
					in.close();
					return data;
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
