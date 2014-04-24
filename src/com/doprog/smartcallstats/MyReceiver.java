package com.doprog.smartcallstats;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// context is coming so we do things from here for the time being
		// later we will move on to services
		NotificationManager nm = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		Notification notify = new Notification(R.drawable.call,
				"Missed Call Reminder", System.currentTimeMillis());
		// notify.sound=Uri.parse(get);
		// notify.sound = Uri.parse("android.resource://"
		// + context.getPackageName() + "/" + R.raw.siren);
		notify.defaults |= Notification.PRIORITY_HIGH;
		long[] vib = new long[] { 1000, 2000, 20000, 3000, 20000, 1000 };
		notify.vibrate = vib;
		Intent in = new Intent(context, Launcher.class);
		PendingIntent pin = PendingIntent.getActivity(context, 0, in, 0);
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notify.setLatestEventInfo(context, "Important Call Missed",
				"Check the log to call ", pin);
		nm.notify(101, notify);
	}

}
