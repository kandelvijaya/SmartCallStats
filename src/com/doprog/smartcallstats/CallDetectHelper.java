package com.doprog.smartcallstats;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallDetectHelper {

	// general variables
	private Context ctx;
	TelephonyManager tm;
	IncomingCallListener incomingListener;

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
				doForMissedCalls();
				doForTalkTime();
				previousState = 0;
				break;
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
			}

			// if (startTalkTime != 0 && endTalkTime>0) {
			// // the call was received::: send the talk time
			// sendTimeToTrack(startTalkTime, endTalkTime);
			// startTalkTime=0;
			// }
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
					ctx.startActivity(intentSendTime);
					Log.d("checkpoint", "calling number is " + callingNumber);
				}
			}

			Log.d("checkpoint",
					"passed the sendTimeToTrack function::: state is "
							+ phoneState);

		}

	}// end of callstatelistener

}