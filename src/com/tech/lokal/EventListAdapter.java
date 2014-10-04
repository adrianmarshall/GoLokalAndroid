package com.tech.lokal;

import java.util.ArrayList;
import java.util.HashMap;

import com.squareup.picasso.Picasso;	// Picasso - for downloading the images

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventListAdapter extends BaseAdapter{

	private Context mContext;
	private final ArrayList<HashMap <String,String>> urls;		// Array list of HashMap Strings( The Events attributes)
	HashMap<String,String> result = new HashMap<String,String>();
	
	public static final String EVENT_IMAGE_URL = "http://lokalapp.co/"; 	// base url for site.. all image URI's will be appended to get the actual URL
	
	LayoutInflater inflater;
	
	//Class Constructor 
	public EventListAdapter(Context context, ArrayList<HashMap<String,String>> items){
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
		
		TextView event_title;
		ImageView event_image;
	//	TextView likes_count;		** NOT YET IMPLEMENTED ON SERVER SIDE
		TextView item_id;
		TextView date;
		
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.custom_list_event, parent,false);
		
		result = urls.get(position);
		
		event_title = (TextView) view.findViewById(R.id.event_title);
		event_image = (ImageView) view.findViewById(R.id.img_event);
		// likes_count = (TextView) view.findViewById(R.id.likes_count);		** NOT YET IMPLEMENTED ON SERVER SIDE
		item_id = 	(TextView) view.findViewById(R.id.event_id);
		date = (TextView) view.findViewById(R.id.event_date);
		
		event_title.setText(result.get(EventActivity.TAG_TITLE));
		// likes_count.setText(result.get(EventActivity.TAG_LIKES));	** NOT YET IMPLEMENTED ON SERVER SIDE
		item_id.setText(result.get(EventActivity.TAG_ID));
		
		//*********Converting DATE to a time format "h:mm " ****** 
		String TAG_DATE = result.get(EventActivity.TAG_DATE);
		TAG_DATE = DateFormater.convertDateToTime(TAG_DATE);	
		
		date.setText(TAG_DATE);
		// ********** DATE Converted *************
		//Picasso image loader library starts here
		
		Picasso.with(mContext).load(EVENT_IMAGE_URL + result.get(EventActivity.TAG_EVENT_IMAGE)) // Photo URL
		.placeholder(R.drawable.logo)		// This image will be displayed while photo URL is loading
		.error(R.drawable.ic_launcher)			// If error shows up during downloading
		.fit().centerCrop()	// settings , fit() Attempt to resize the image to fit exactly into the target ImageView's bounds.
							//centerCrop Crops an image inside of the bounds specified by resize(int, int) rather than distorting the aspect ratio.
		.into(event_image);		// We put it into our layout component (image view)
		return view;
	}

}
