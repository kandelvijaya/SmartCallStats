package com.doprog.graphformissed;

import java.util.Random;

import android.content.Context;

import com.doprog.smartcallstats.LocalDb;

//total missed call count for x day before today should be returned each time
//this is called n times where n is the number of days we want the graph to be plotted

public class MockData {

	
	private static Context context;
	// x is the day number, 0, 1, 2, 3
	public static Point getDataFromReceiver(int x, Context ctx)
	{
		context=ctx;
		return new Point(x, getEachDayMissedcallCountForLastSevenDays(x));
	}
	
	private static int getEachDayMissedcallCountForLastSevenDays(int xDayBack) {
		
		LocalDb ldb = new LocalDb(context);
		short missedCallCount=ldb.getMissedCallCountForThisDayBeforeToday(xDayBack);
		ldb.close();
		//
		return missedCallCount;
	}

	private static int generateRandomData()
	{
		Random random = new Random();
		return random.nextInt(7);
	}

	public static Point getReceivedCallDataFromReceiver(int i, Context context2) {
		return new Point(i,generateRandomData());
	}
}
