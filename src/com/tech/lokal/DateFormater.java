package com.tech.lokal;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.util.Log;

public class DateFormater {
	
	static SimpleDateFormat fromServer = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm");		// This is the Date format that the server uses (how it's formated in the MySQL Database)
	static SimpleDateFormat myTimeFormat = new SimpleDateFormat("h:mm a");				// formats to a pattern like "12:08 PM"
	static SimpleDateFormat myDateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");	// Formats to a pattern like "Wed, Oct 5, 2014"
	static String reformatedDate = null;
	
	// formats a given date to just it's time "12:08 PM"
	public static String convertDateToTime(String inputDate){
	
	try {
		 reformatedDate = myTimeFormat.format(fromServer.parse(inputDate));
	} catch (ParseException e) {
		//print the stack trace and return the date in the same format that it was given.
		e.printStackTrace();
		Log.d("Date Formater", "Could not format date. See Dateformater.java");
		return inputDate;
	}
	
	return reformatedDate;
	}
	
	// Formats a given date "inputDate" to a pattern like "Wed, Oct 5, 2014"
	public static String convertDateTime(String inputDate){
			
			try {
				 reformatedDate = myDateFormat.format(fromServer.parse(inputDate));
			} catch (ParseException e) {
				//print the stack trace and return the date in the same format that it was given.
				e.printStackTrace();
				Log.d("Date Formater", "Could not format date. See Dateformater.java");
				return inputDate;
			}
			
			return reformatedDate;
	}
}
