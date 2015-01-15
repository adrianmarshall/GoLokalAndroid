package com.tech.lokal;

import java.util.ArrayList;
import java.util.HashMap;

import com.squareup.picasso.Picasso;	// Picasso - for downloading the images

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyEventsListAdapter extends BaseAdapter{

	private Context mContext;
	private final ArrayList<HashMap <String,String>> urls;		// Array list of HashMap Strings( The Events attributes)
	HashMap<String,String> result = new HashMap<String,String>();
	
	public static final String EVENT_IMAGE_URL = "http://lokalapp.co/"; 	// base url for site.. all image URI's will be appended to get the actual URL
	
	LayoutInflater inflater;
	
	//Class Constructor 
	public MyEventsListAdapter(Context context, ArrayList<HashMap<String,String>> items){
		mContext = context;
		urls = items;
	}
	// How many events are in the data set represented by this Adapter
	@Override
	public int getCount() {
	
		return urls.size();
	}

	// Get the data item associated with the specified position in the data set
	@Override	
	public Object getItem(int position) {
		
		return position;
	}

	//Get the row id associated with the specified posistion in the list 
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return getItem(position).hashCode();
	}
	
	// Displaying the listview with the mapped items
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.d("getView: "," in getView() ");
		
		TextView event_title;
		ImageView event_image;
	//	TextView likes_count;		** NOT YET IMPLEMENTED ON SERVER SIDE
		TextView item_id;
		TextView event_time;
		TextView event_day; 		
		TextView event_description;
		
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.custom_list_myevent, parent,false);
		
		result = urls.get(position);
		
		// connect variables to their corresponding UI counterparts 
		
		event_title = (TextView) view.findViewById(R.id.myevent_title);
		//event_image = (ImageView) view.findViewById(R.id.img_event);
		// likes_count = (TextView) view.findViewById(R.id.likes_count);		** NOT YET IMPLEMENTED ON SERVER SIDE
		item_id = 	(TextView) view.findViewById(R.id.myevent_id);		// The id is needed to identify the which object is which but not shown in the UI
		//event_time = (TextView) view.findViewById(R.id.event_time);
		event_day = (TextView) view.findViewById(R.id.myevent_date);
		//event_description = (TextView) view.findViewById(R.id.event_description);
		
		
		event_title.setText(result.get(EventActivity.TAG_TITLE));
		// likes_count.setText(result.get(EventActivity.TAG_LIKES));	** NOT YET IMPLEMENTED ON SERVER SIDE
		item_id.setText(result.get(EventActivity.TAG_ID));
		//event_description.setText(result.get(EventActivity.TAG_DESCRIPTION));
		
		//Log.d("description: ", " " +event_description.getText().toString());
		//*********Converting DATE to a time format "DayofWeek, Month DayofMonth, YEAR" ****** 
		String TAG_DATE = result.get(EventActivity.TAG_START_TIME);
		Log.d("tag_date: ",TAG_DATE);
		TAG_DATE = DateFormater.convertDateToDay(TAG_DATE);
		Log.d("tag_date: ","After conversion:"+ TAG_DATE);
		event_day.setText(TAG_DATE);		// Set the day of the event
		// ********** DATE Converted *************
	
		return view;
	}

}
