package com.doprog.smartcallstats;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;

public class ViewHistory extends ListActivity{

	Context ctx;
	String[] lista;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewhistory);
		
		//call the list creator method
		ctx=this.getApplicationContext();
		
		LocalDb ldb= new LocalDb(ctx);
		Cursor cur=ldb.getAllItemsForIncomingCalls();
		
		createListAndSetResult(cur);
		
	}
	
	private boolean createListAndSetResult(Cursor cur) {
		// intially remove views
		ListView lv = getListView();
		AdapterSetter doMyWork = new AdapterSetter(ctx);

		boolean result = doMyWork.handleListCreation(cur, lista,
				lv);
		
		
		
		return result;
	}

}
