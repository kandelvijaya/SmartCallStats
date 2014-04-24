package com.doprog.smartcallstats;

<<<<<<< HEAD
=======
import java.lang.reflect.Method;

>>>>>>> Detection
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

public class CallDetectHelper {

	// general variables
	private Context ctx;
	TelephonyManager tm;
	IncomingCallListener incomingListener;
	private short thisCallWasBlocked = 0; // 1 for blocked

	/*
	 * When a call is blocked it should be stored as type blocked
	 */

	public CallDetectHelper(Context c) {
		this.ctx = c;
		incomingListener = new IncomingCallListener();
	}

	public void startDetecting() {
		tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(incomingListener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	public void stopDetection() {
		tm.listen(incomingListener, PhoneStateListener.LISTEN_NONE);
	}

	// INNER CLASSES ARE DEFINED BELOW THIS PART

	private class IncomingCallListener extends PhoneStateListener {

		// general variables
		private long startTime, endTime, startTalkTime, endTalkTime;
		String callingNumber, phoneState;

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);

			if (!incomingNumber.isEmpty()) {
				callingNumber = incomingNumber;
			}
			Log.d("checkpoint", "state changed with number of " + callingNumber);
			/*
			 * LOGIC 3 states of call while incoming 1. ringing 2. offhook
			 * 3.idle * when not use phone state is idle * This switch will
			 * determine what state a phone is going throughTHE MAIN AIM IS TO
			 * GET THE TIME DURATION BETWEEN A INCOMINGCALL START AND END OR
			 * RECEIVED=====startTime. endTime are system time at that
			 * consequent states
			 */

			// CHECK THE RULE FOR THIS CALLER AND BLOCK IF NECESSARY
			blockTheCallerIfNeeded(incomingNumber);

			short previousState = 0; // 0 for idle, 1 for ringing and 2 for
										// offhook
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				// **********phone starts coming
				startTime = System.currentTimeMillis();
				Log.d("checkpoint", "" + startTime + ":::ringing state");
				previousState = 1;
				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				// *********phone is received:: most probably happens when
				endTime = System.currentTimeMillis();
				startTalkTime = startTime;
				Log.d("checkpoint", "" + endTime + ":::offhook state");
				// outgoing (ONRESEARCH)
				doForReceivedCalls();
				previousState = 2;
				break;

			case TelephonyManager.CALL_STATE_IDLE:
				// *********caller stops calling, back to normal or call taken
				endTime = System.currentTimeMillis();
				endTalkTime = endTime;
				Log.d("checkpoint", "" + endTime + ":::idle state");
				// (ONRESEARCH)
				if (thisCallWasBlocked == 1) {
					//store as blocked call type
					doForBlockedCalls();
					
				} else {
					doForMissedCalls();
					doForTalkTime();
				}
				previousState = 0;
				break;
			}
		}

		private void blockTheCallerIfNeeded(String incomingNumber) {
			LocalDb ldb = new LocalDb(ctx);
			ldb.open();
			short blockCode = ldb.getBlockCodeFor(incomingNumber);
			ldb.close();

			Log.d("checkpoint", "detector blockCode for this number "
					+ blockCode);

			if (blockCode == 1) {
				// END THIS CALL RIGHT NOW EQUIVALENT TO BLOCKING
				try {
					TelephonyManager manager = (TelephonyManager) ctx
							.getSystemService(Context.TELEPHONY_SERVICE);
					Class c = Class.forName(manager.getClass().getName());
					Method m = c.getDeclaredMethod("getITelephony");
					m.setAccessible(true);
					ITelephony telephony = (ITelephony) m.invoke(manager);
					telephony.endCall();
				} catch (Exception e) {
					Log.d("", e.getMessage());
				}
				
				//set the wasblocked byte
				thisCallWasBlocked=1;
			}else{
				thisCallWasBlocked=0;
			}

		}

		private void doForReceivedCalls() {
			if (startTime != 0 && endTime != 0) {
				// call started at some point and is taken
				phoneState = "Received";
				sendTimeToTrack(startTime, endTime);
				startTime = endTime = 0;
			}

		}

		private void doForTalkTime() {
			if (startTalkTime != 0 && endTalkTime != 0) {
				// phone recieved at one point and halted at some next point
				if (endTalkTime > startTalkTime) {
					// this is what happens naturally
					phoneState = "Talktime";
					sendTimeToTrack(startTalkTime, endTalkTime);
					startTalkTime = endTalkTime = 0;
				}
			}
		}

		private void doForMissedCalls() {
			// ---------track the time duration to the caller
			// in the future put it into corresponding databse
			// BE SURE TO CHECK IF START TIME IS NOT 0,sometimes genreal states
			// might be this without initiator
			if (startTime != 0) {
				// either a call is accepted or missed
				// duration between ringing to offhook or idle
				phoneState = "Missed";
				sendTimeToTrack(startTime, endTime);
				startTime = endTime = 0;

				// get the remind interval if this caller has some rules
				// attached to it
				LocalDb ldb = new LocalDb(ctx);
				ldb.open();

				int remindIntervalInMinutes = ldb
						.getRemindInterval(callingNumber);
				Log.d("checkpoint", "" + remindIntervalInMinutes);

				ldb.close();

				// now take the time check if its not 0; 0 is default so exclude
				// it then set the alarm
				remindTheUser(remindIntervalInMinutes);

			}

			// if (startTalkTime != 0 && endTalkTime>0) {
			// // the call was received::: send the talk time
			// sendTimeToTrack(startTalkTime, endTalkTime);
			// startTalkTime=0;
			// }
		}
		
		private void doForBlockedCalls() {
			// ---------track the time duration to the caller
			// in the future put it into corresponding databse
			// BE SURE TO CHECK IF START TIME IS NOT 0,sometimes genreal states
			// might be this without initiator
			if (startTime != 0) {
				// either a call is accepted or missed
				// duration between ringing to offhook or idle
				phoneState = "Blocked";
				sendTimeToTrack(startTime, endTime);
				startTime = endTime = 0;
//
//				// get the remind interval if this caller has some rules
//				// attached to it
//				LocalDb ldb = new LocalDb(ctx);
//				ldb.open();
//
//				int remindIntervalInMinutes = ldb
//						.getRemindInterval(callingNumber);
//				Log.d("checkpoint", "" + remindIntervalInMinutes);
//
//				ldb.close();
//
//				// now take the time check if its not 0; 0 is default so exclude
//				// it then set the alarm
//				remindTheUser(remindIntervalInMinutes);

			}

			// if (startTalkTime != 0 && endTalkTime>0) {
			// // the call was received::: send the talk time
			// sendTimeToTrack(startTalkTime, endTalkTime);
			// startTalkTime=0;
			// }
		}


		private void remindTheUser(int remindIntervalInMinutes) {
			if (remindIntervalInMinutes != 0) {

				long nextRinger = System.currentTimeMillis()
						+ remindIntervalInMinutes * 1000 * 60;

				Log.d("Current Time ", "" + System.currentTimeMillis());
				Log.d("Rinder Time", "" + nextRinger);

				AlarmManager alm = (AlarmManager) ctx
						.getSystemService(ctx.ALARM_SERVICE);
				Intent in = new Intent(ctx, MyReceiver.class);

				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pin = PendingIntent.getBroadcast(ctx, 0, in,
						PendingIntent.FLAG_UPDATE_CURRENT);
				alm.set(AlarmManager.RTC, nextRinger, pin);
			}

		}

		private void remindTheUser(int remindIntervalInMinutes) {
			if (remindIntervalInMinutes != 0) {

				long nextRinger= System.currentTimeMillis()+ remindIntervalInMinutes*1000*60; 
				
				Log.d("Current Time ", ""+System.currentTimeMillis());
				Log.d("Rinder Time", ""+nextRinger);
				
				AlarmManager alm = (AlarmManager) ctx
						.getSystemService(ctx.ALARM_SERVICE);
				Intent in = new Intent(ctx, MyReceiver.class);

				in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pin = PendingIntent.getBroadcast(ctx, 0, in,
						PendingIntent.FLAG_UPDATE_CURRENT);
				alm.set(AlarmManager.RTC, nextRinger, pin);
			}

		}

		private void sendTimeToTrack(long start, long end) {
			// send the details obtained in intent to another activity or to
			// database via service

			// *********for testing we send it to another new activity to track
			// the time using intent
			Intent intentSendTime = new Intent(ctx, TimeTracker.class);
			intentSendTime.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intentSendTime.putExtra("startTime", start);
			intentSendTime.putExtra("endTime", end);
			intentSendTime.putExtra("status", phoneState);
			intentSendTime.putExtra("callingNumber", callingNumber);
			if (callingNumber != null) {
				if (!callingNumber.isEmpty()) {
					if (!phoneState.equals("Received")) { // dont pop up for
															// received call
															// this will
															// distract
						ctx.startActivity(intentSendTime);
						Log.d("checkpoint", "calling number is "
								+ callingNumber);
					}
				}
			}

			Log.d("checkpoint",
					"passed the sendTimeToTrack function::: state is "
							+ phoneState);

		}

	}// end of callstatelistener

}