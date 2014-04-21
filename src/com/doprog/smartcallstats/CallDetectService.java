package com.doprog.smartcallstats;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CallDetectService extends Service {

	// general variables defination
	CallDetectHelper callhelper;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		/*
		 * This is fired up when the caller hits startService(intent);
		 * */
		
		super.onStartCommand(intent, flags, startId);
		callhelper = new CallDetectHelper(getApplicationContext());
		callhelper.startDetecting();
		/*
		 * LOGIC OF RETURN: this return will impact on what and how things
		 * happen if this service is to be stopped by the system on resource
		 * demand
		 */
		return START_STICKY;
	}
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		/*
		 * this is fired up when caller hits stopService(intent)
		 * */
		super.onDestroy();
		callhelper.stopDetection();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
