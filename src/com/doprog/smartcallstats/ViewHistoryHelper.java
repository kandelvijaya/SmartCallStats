package com.doprog.smartcallstats;

import android.content.Context;
import android.database.Cursor;
import android.view.inputmethod.InputMethodManager;

public class ViewHistoryHelper {
	/**
	 * Takes a cursor as a parameter if null return false and empties the list
	 * 
	 * if something then inflate the list and display the db result
	 * */
	private Context context;
	private InputMethodManager imm;

	public ViewHistoryHelper(Context ctx) {
		context = ctx;
	}


	/**
	 * This method takes a cursor which contains one row returned from searching
	 * on hospital table by iterating over the list Returns all the required
	 * information to be presented on one list item bundled on a long string
	 * delimitted by @@ *
	 */
	public String prepareListElementsSpecificToAllDatabase(Cursor cur) {
		String callingnumber = cur.getString(cur.getColumnIndex("callernumber"));
		int rowid = cur.getShort(cur.getColumnIndex("callid"));
		String calltype = cur.getString(cur.getColumnIndex("calltype"));
		String date = cur.getString(cur.getColumnIndex("calldate"));
		String callduration = cur.getString(cur.getColumnIndex("callduration"));

		String listElement = callingnumber + "@@" + calltype + "@@" + date
				+ "@@" + callduration + "@@" +rowid;

		return listElement;
	}

	

}
