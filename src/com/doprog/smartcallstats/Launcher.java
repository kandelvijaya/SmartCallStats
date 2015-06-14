package com.doprog.smartcallstats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Author: Vijaya Prakash Kandel Version: 1.1 LastModified: Dec 15 2013 Modified
 * Elements: -DatabaseHelper.java created -DatabaseService.java created==> will
 * insert the call logs into database(SQLite in background) #incomplete
 * */

public class Launcher extends Activity implements View.OnClickListener {
	// variable define
	private boolean detectEnabled;
	Button btnToggleOn, btnExit, btnAboutApp, btnViewReport, btnShare;
	TextView tvStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launcher_layout);

		// initialize variables before moving ahead:: we need them later
		intializeVariables();
		
		ToggleDetectionService(isMyServiceRunning());		//for toggling to turn on or off the service
		//This is useful when launching the new instance of the app after killing it once
	}

	private void intializeVariables() {
		// TODO Auto-generated method stub
		btnToggleOn = (Button) findViewById(R.id.btnTurnToggle);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnAboutApp = (Button) findViewById(R.id.btnAbout);
		btnViewReport = (Button) findViewById(R.id.btnViewReport);
		btnShare = (Button) findViewById(R.id.btnShare);

		tvStatus = (TextView) findViewById(R.id.tvStatus);

		// set click listeners to btns
		btnToggleOn.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		btnAboutApp.setOnClickListener(this);
		btnViewReport.setOnClickListener(this);
		btnShare.setOnClickListener(this);

	}
	
	
	/**
	 * Return wether this service is already up and running or not 
	 * True if it is running, false if its not
	 * */
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (CallDetectService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	    
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnExit:
			// terminate this activity life cycle
			this.finish();
			break;
		case R.id.btnTurnToggle:
			// send the opposite of present state of detectEnabled so that it
			// can be directly set to
			// the same variable again from that method and worked on to display
			// appropriate
			// button text
			ToggleDetectionService(!detectEnabled);
			break;

		case R.id.btnAbout:
			// about btn handle
			startActivity(new Intent(this, About.class));
			break;

		case R.id.btnViewReport:
			startActivity(new Intent(this, ViewReport.class));

			break;

		case R.id.btnShare:
			//

			// image naming and path to include sd card appending name you
			// choose for file
			String mPath = Environment.getExternalStorageDirectory().toString()
					+ "/" + "screenshoted.jpg";

			// create bitmap screen capture
			Bitmap bitmap;
			View v1 = getWindow().getDecorView().getRootView();
			v1.setDrawingCacheEnabled(true);
			bitmap = Bitmap.createBitmap(v1.getDrawingCache());
			v1.setDrawingCacheEnabled(false);

			OutputStream fout = null;
			File imageFile = new File(mPath);

			try {
				fout = new FileOutputStream(imageFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
				fout.flush();
				fout.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
			// share the image
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.setType("image/jpeg");

			shareIntent
			.putExtra(Intent.EXTRA_TEXT,
					"Hospitals Nepal is a wonderful application. Share this to help people.TESTING");
			shareIntent.putExtra(Intent.EXTRA_STREAM,
					Uri.fromFile(new File(mPath)));
			
			startActivity(Intent.createChooser(shareIntent, "Share this app!"));

			break;

		}
	}

	private void ToggleDetectionService(boolean b) {
		// TODO Auto-generated method stub
		detectEnabled = b;

		// INITAILIZE INTENT FOR SERVICE to start or stop
		Intent serviceIntent = new Intent(this, CallDetectService.class);

		if (b) { // detection started
			/*
			 * 1. set btnToggle text to turn off detection 2. set status bar to
			 * detecting 3.start service to detect calls
			 */
			btnToggleOn.setText(R.string.textWhenToggleOn);
			tvStatus.setText(R.string.tvServiceStarterON);
			startService(serviceIntent);
			Log.d("launcher", "intent passed this level");

			//
		} else { // detection stopped
			/*
			 * 1. set btnToggle text to detect On 2. set status text to not
			 * detecting 3. stop the servie started early
			 */
			btnToggleOn.setText(R.string.textWhenToggleOff);
			tvStatus.setText(R.string.tvServiceStarterOff);
			stopService(serviceIntent);

		}
	}

}
