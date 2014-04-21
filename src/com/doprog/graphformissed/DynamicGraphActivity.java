package com.doprog.graphformissed;

import org.achartengine.GraphicalView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.doprog.smartcallstats.R;

public class DynamicGraphActivity extends Activity {

	private static GraphicalView view;
	private LineGraph line = new LineGraph();
	private LineGraph lineReceived= new LineGraph();
	private static Thread thread;
	private Context context;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphmain);

		context=getApplicationContext();
		
		thread = new Thread() {
			public void run()
			{
				int maxDay=6;			//this is the number of days before we want to look for in the graph
				
				for (int i = maxDay; i >= 0; i--) 	//only showing data for the last 7 days
				{
					try {
						Thread.sleep(100);			//slepping for animation effect to take place
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//Think we need to get the missed calls count per day
					//put into an array and loop through it and add new points
					//the data is collected from database by grouping them according to date
					//we don't bring data for more than 10 days at most
					
					
					Point p = MockData.getDataFromReceiver(i,context); // We got new data!
					Point pReceived= MockData.getReceivedCallDataFromReceiver(i,context);
					
					line.addNewPoints(p); // Add it to our graph
					lineReceived.addNewPoints(pReceived);
					
					view.repaint();
				}
			}
		};
		thread.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
		view = line.getView(this);
		view.setBackgroundColor(context.getResources().getColor(R.color.beige));
		
		setContentView(view);
	}

}