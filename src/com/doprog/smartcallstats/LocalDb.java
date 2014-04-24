package com.doprog.smartcallstats;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalDb extends SQLiteAssetOpenHelper {

	private static final String DATABASE_NAME = "smartcallstat";
	private static final int DATABASE_VERSION = 1;

	// ****************increase the number of version to force the user to
	// delete their previous database and copy new ones from the apk for the
	// first time
	// ****************force upgrade
	//

	private SQLiteDatabase db;
	private Cursor cursor = null;
	private String sql;
	private Context context;

	public LocalDb(Context context) {
		// super(context, DATABASE_NAME, null, DATABASE_VERSION);
		super(context, DATABASE_NAME, context.getExternalFilesDir(null)
				.getAbsolutePath(), null, DATABASE_VERSION);

		// you can use an alternate constructor to specify a database location
		// (such as a folder on the sd card)
		// you must ensure that this folder is available and you have permission
		// to write to it
		// super(context, DATABASE_NAME,
		// context.getExternalFilesDir(null).getAbsolutePath(), null,
		// DATABASE_VERSION);
		// setForcedUpgradeVersion(2);
		this.context = context;
		open();
	}

	public SQLiteDatabase open() {
		setDb(getWritableDatabase());
		return getDb();
	}

	private void testdb() {
		cursor = null;
	}

	public SQLiteDatabase getDb() {
		return db;
	}

	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}

	public void insertCallDetail(String callingNumber, String calltime,
			String calldate, String calltype, double callduration) {
		cursor = null;
		ContentValues cv = new ContentValues();
		cv.put("callernumber", callingNumber);
		cv.put("calltime", calltime);
		cv.put("calldate", calldate);
		cv.put("callduration", callduration);
		cv.put("calltype", calltype);
		int result = (int) db.insert("call", null, cv);

		Log.d("checkpoint", " " + result);
	}

	public Cursor getAllItemsForIncomingCalls() {
		cursor = null;

		String sql = "select * from call order by callid desc";
		cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public short getMissedCallCountForThisDayBeforeToday(int xDayBack) {
		String calldate = getDateAfterSubtractingThisNumberOfDays(xDayBack);

		Cursor cur = db.rawQuery(
				"select count(*) from Call where calldate like '" + calldate
						+ "' and calltype='Missed' ", null);

		short missedCallCountForThisDay = 0;

		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {

			missedCallCountForThisDay = cur.getShort(0);

		}

		Log.d("checkpoint", "total missed call for this day were"
				+ missedCallCountForThisDay);

		return missedCallCountForThisDay;

	}

	private String getDateAfterSubtractingThisNumberOfDays(int xDayBack) {
		String theDate = "";

		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(System.currentTimeMillis());
		Date todayDate = today.getTime();

		// String calldate=todayDate.getYear()+1900 + " "+((int)
		// todayDate.getMonth()+1) + " "+(todayDate.getDate()-xDayBack);

		int year = todayDate.getYear() + 1900;
		int month = todayDate.getMonth() + 1;
		int day = todayDate.getDate() - xDayBack;

		if (day <= 0) {
			// we need to get to previous month
			month -= 1;

			Calendar cal = Calendar.getInstance();

			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);

			int MaxDaysInThisMonth = cal
					.getActualMaximum(Calendar.DAY_OF_MONTH);
			Log.d("checkpoint", "Max days in this month is "
					+ MaxDaysInThisMonth);
			day = MaxDaysInThisMonth + day;
		}
		String calldate = year + " " + month + " " + day;
		theDate = calldate;

		Log.d("checkpoint", "the date to look for is (" + calldate + ")");

		return theDate;
	}

	public short getReceivedCallCountForThisDayBeforeToday(int xDayBack) {

		String calldate = getDateAfterSubtractingThisNumberOfDays(xDayBack);

		Cursor cur = db.rawQuery(
				"select count(*) from Call where calldate like '" + calldate
						+ "' and calltype='Received' ", null);

		short missedCallCountForThisDay = 0;

		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {

			missedCallCountForThisDay = cur.getShort(0);

		}

		Log.d("checkpoint", "total received call for this day were"
				+ missedCallCountForThisDay);

		return missedCallCountForThisDay;

	}

	public short getAllCallCountForThisDayBeforeToday(int xDayBack) {
		String calldate = getDateAfterSubtractingThisNumberOfDays(xDayBack);

		Cursor cur = db.rawQuery(
				"select count(*) from Call where calldate like '" + calldate
						+ "' ", null);

		short missedCallCountForThisDay = 0;

		for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {

			missedCallCountForThisDay = cur.getShort(0);

		}

		Log.d("checkpoint", "total calls for this day were"
				+ missedCallCountForThisDay);

		return missedCallCountForThisDay;

	}

	public double[] getMissedCallCountArrayForThisDays(int dayno) {
		double[] mycountarray = new double[dayno];
		;

		int i;
		for (i = dayno; i > 0; i--) {
			mycountarray[i - 1] = getMissedCallCountForThisDayBeforeToday(i - 1);
		}

		return mycountarray;
	}

	public double[] getReceivedCallCountArrayForThisDays(int dayno) {
		double[] mycountarray = new double[dayno];
		;

		int i;
		for (i = dayno; i > 0; i--) {
			mycountarray[i - 1] = getReceivedCallCountForThisDayBeforeToday(i - 1);
		}

		return mycountarray;
	}

	public double[] getAllCallCountArrayForThisDays(int dayno) {
		double[] mycountarray = new double[dayno];
		;

		int i;
		for (i = dayno; i > 0; i--) {
			mycountarray[i - 1] = getReceivedCallCountForThisDayBeforeToday(i - 1)
					+ getMissedCallCountForThisDayBeforeToday(i - 1);
		}

		return mycountarray;
	}

	public double getTotalMissedCallForThisDaysBefore(int dayno) {
		double[] individualDayCount = this
				.getMissedCallCountArrayForThisDays(dayno);

		int i;
		double count = 0;
		;
		for (i = 0; i < individualDayCount.length; i++) {
			count += individualDayCount[i];
		}

		return count;
	}

	public double getTotalReceivedCallForThisDaysBefore(int dayno) {
		double[] individualDayCount = this
				.getReceivedCallCountArrayForThisDays(dayno);

		int i;
		double count = 0;
		;
		for (i = 0; i < individualDayCount.length; i++) {
			count += individualDayCount[i];
		}

		return count;
	}

	public double getTotalCallForThisDaysBefore(int dayno) {
		double[] individualDayCount = this
				.getAllCallCountArrayForThisDays(dayno);

		int i;
		double count = 0;
		;
		for (i = 0; i < individualDayCount.length; i++) {
			count += individualDayCount[i];
		}

		return count;
	}

	public String[] getArrayDetailForThisCallId(int id) {
		String[] arrayDetail = new String[4];

		String number = "";
		String priorityLevel = "";
		String blockedSatus = "";
		String remindinterval = "";

		// get the number for the id
		Cursor cur = db.rawQuery(
				"select callernumber from call where callid ='" + id + "'",
				null);

		if (cur != null) {
			cur.moveToFirst();
			number = cur.getString(0);
		}

		cur = null; // empty it

		// get the priorityLevel, remindInterval and blockedStatus for the
		// number
		String sql = "select r.prioritylevel, r.remindinterval, r.isblocked from rule r join caller c "
				+ "on r.callerid=c.callerid and c.callernumber='"
				+ number
				+ "'";

		cur = db.rawQuery(sql, null);

		if (cur != null) {

			if (cur.getCount() > 0) {
				cur.moveToFirst();
				priorityLevel = cur.getString(0);
				remindinterval = cur.getString(1);
				blockedSatus = cur.getString(2);
			}
		}

		cur = null; // empty it

		// fit into the array
		arrayDetail[0] = number;
		arrayDetail[1] = priorityLevel;
		arrayDetail[2] = remindinterval;
		arrayDetail[3] = blockedSatus;

		return arrayDetail;
	}

	public void updateRulesForThisNumber(int priorityLevel, int remindInterval,
			short blockCode, String number) {
		int callerid = 0;
		Cursor cur = null;

		// get the callerid of this number
		callerid = getCallerId(number);
		Log.d("Checkpoint", "caller id is " + callerid);

		// check wether rules exist for this callernumer
		cur = db.rawQuery("select remindinterval from rule where callerid='"
				+ callerid + "'", null);

		if (cur != null) {
			if (cur.getCount() > 0) {
				// record exist update it
				cur.moveToFirst();
				t("remindinterval is" + cur.getString(0));
				updateRule(callerid, priorityLevel, remindInterval,
						blockCode);
				t("insisde update");
			} else {
				// record doesnot exist insert it
				insertRule(callerid, priorityLevel, remindInterval,
						blockCode);
				t("insisde entry");
			}
		}
		cur = null; // empty it

	}

	/**
	 * This method is a recursive one. Takes callernumber, inserts into caller
	 * and returns its row if its not there before. If already present it
	 * retrives the callerid.
	 * 
	 * @param callernumber
	 * @return callerid
	 */
	private int getCallerId(String number) {
		int callerid = 0;
		Cursor cur = db.rawQuery(
				"select callerid from caller where callernumber = '" + number
						+ "'", null);
		if (cur != null) {
			if (cur.getCount() > 0) {
				// yes entry
				cur.moveToFirst();
				callerid = cur.getShort(0);
				t("here retriving and the value is " + callerid);
				return callerid;
			} else {
				// no entry
				ContentValues cv = new ContentValues();
				cv.put("callernumber", number);
				db.insert("caller", null, cv);
				t("just inserted the number, now retriving");
				callerid = getCallerId(number);
				return callerid;
			}
		}

		t("Ended");
		return 0;
	}

	private void insertRule(int callerid, int priorityLevel,
			int remindInterval, short blockCode) {
		ContentValues cv = new ContentValues();
		cv.put("callerid", callerid);
		cv.put("prioritylevel", priorityLevel);
		cv.put("remindinterval", remindInterval);
		cv.put("isblocked", blockCode);
		db.insert("rule", null, cv);
	}

	private void updateRule(int callerid, int priorityLevel,
			int remindInterval, short blockCode) {
		ContentValues cv = new ContentValues();

		cv.put("prioritylevel", priorityLevel);
		cv.put("remindinterval", remindInterval);
		cv.put("isblocked", blockCode);
		db.update("rule", cv, "callerid='" + callerid + "'", null);

	}

	private void t(String msg) {
		Log.d("checkpoint", msg);
	}

	public int getRemindInterval(String callingNumber) {
		Cursor cur = null;
		String sql = "select r.remindinterval from rule r join caller c on r.callerid=c.callerid and c.callernumber='"
				+ callingNumber + "'";
		cur=db.rawQuery(sql, null);
		
		int remindinterval=0;
		
		if(cur!=null){
			if(cur.getCount()>0){
				cur.moveToFirst();
				remindinterval=cur.getShort(0);
				return remindinterval;
			}
		}
		
		return remindinterval;
	}

	public short getBlockCodeFor(String incomingNumber) {
		
		String sql="select r.isblocked from rule r join caller c on r.callerid=c.callerid and c.callernumber='"+incomingNumber+"'";
		Cursor cur= db.rawQuery(sql, null);
		
		if(cur!=null){
			if(cur.getCount()>0){
				cur.moveToFirst();
				return cur.getShort(0);
			}
		}
		
		return 0;
	}

}
