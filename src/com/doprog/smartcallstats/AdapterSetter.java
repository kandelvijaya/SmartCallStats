package com.doprog.smartcallstats;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.doprog.smartcallstats.BigAdapter.ViewHolder;

public class AdapterSetter {
	// vars
	private Context context;
	private static final int START_ID_COUNT = 320;
	protected final int SEARCH_ON_HOSPITAL = 1;
	protected final int SEARCH_ON_DOCTOR = 2;
	protected int whichSearch; // 1 for hospital 2 for doctor by
								// default it is 1

	public AdapterSetter(Context ctx) {
		context = ctx;
	}

	/**
	 * Takes a cursor as a parameter if null return false and empties the list
	 * 
	 * if something then inflate the list and display the db result
	 * */

	public boolean handleListCreation(Cursor cur, String[] lista, ListView lv) {
		// intially remove views
		int count = cur.getCount();

		// lista is a array of strings which will contain one item of list view
		// so we need to have as many index as the cursor has in its count
		lista = new String[count];

		// **************************************************
		/*
		 * Check if the search has to be performed on hospital or on doctor Then
		 * do accordingly
		 */
		ViewHistoryHelper dha = new ViewHistoryHelper(context);

		cur.moveToFirst();
		for (int idvalue = START_ID_COUNT; idvalue < (START_ID_COUNT + count); idvalue++) {
			lista[idvalue - START_ID_COUNT] = dha
					.prepareListElementsSpecificToAllDatabase(cur);
			// increase the cursor index
			cur.moveToNext();
		}

		lv.setAdapter(new BigAdapter(context,
				android.R.layout.simple_list_item_1, 0, lista));
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				BigAdapter.ViewHolder viewHolder = (ViewHolder) view.getTag();
				String identifier = viewHolder.getId();
				
				Intent in = new Intent(context, RuleSetter.class);


				if (in != null) {
					in.putExtra("id", identifier);
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(in);
				}
			}
		});

		
		return true;



}}