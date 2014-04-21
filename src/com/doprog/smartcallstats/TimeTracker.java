package com.doprog.smartcallstats;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TimeTracker extends Activity{

	//general variables declaration
	TextView tvTimeShow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.time_tracker);
		
		//initializer for general variables
		initializeVariables();
		
		//on getting intent data for time use it to display on text view
		getIntentDataAndSetTextView();
		
	}

	private void getIntentDataAndSetTextView() {
		// TODO Auto-generated method stub
		Bundle bagObtained= this.getIntent().getExtras();
		long startTime= bagObtained.getLong("startTime");
		long endTime= bagObtained.getLong("endTime");
		
		
		String callingNumber=bagObtained.getString("callingNumber");
		
		
		String calltype=bagObtained.getString("status");
		
		Calendar mycal= Calendar.getInstance();
		mycal.setTimeInMillis(startTime);
		Date callTiming=mycal.getTime();
		
		
		double callduration= (endTime-startTime)/1000;		//this is in milliseconds so conversion needed
		@SuppressWarnings("deprecation")
		String calltime=callTiming.getHours() + " "+callTiming.getMinutes() ;
		@SuppressWarnings("deprecation")
//LOGIC
		//getyear() brings the count starting from 1900, getMonth() brings 0 indexed months
		
		String calldate=callTiming.getYear()+1900 + " "+((int) callTiming.getMonth()+1) + " "+callTiming.getDate();
		
		Log.d("checkpoint",calldate);
		Log.d("checkpoint",calltime);
		
		tvTimeShow.setText(callingNumber+"("+calltype+") called you at "+mycal.getTime()+"\nTotal call time is: "+callduration+"seconds \n StartTime was:::"+startTime+"\n end time was::::"+endTime);
		
		/*
		 * What if we insert this information in the database and leave
		 * This information is a one row and will take split second and hence we wont do much on the background too
		 * 
		 * 
		 * We also need to check the logic for alerting the user at this point
		 * if the alert has to be made then we need to set the pending intent and the notification at this point too
		 */
		
		LocalDb ldb= new LocalDb(this.getApplicationContext());
		ldb.insertCallDetail(callingNumber, calltime, calldate, calltype, callduration);
		ldb.close();
		
	}

	private void initializeVariables() {
		// TODO Auto-generated method stub
		tvTimeShow= (TextView) findViewById(R.id.tvTimeTracker);
	}
	

}
