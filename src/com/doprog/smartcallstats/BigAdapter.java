package com.doprog.smartcallstats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BigAdapter extends ArrayAdapter<String> {
	// general vars
	private Context context;
	private String[] lista;
	private final int TOTALTIME = 30;
	private final int TIME_ONEBEEP = 2;

	public BigAdapter(Context ctx, int resource, int textViewResourceId,
			String[] strings) {
		super(ctx, resource, textViewResourceId, strings);

		this.lista = strings;
		this.context = ctx;
		// textcolor = context.getResources().getColor(R.color.bodyTxt);
		// text_size_large = (int) context.getResources().getDimension(
		// R.dimen.textsizelarge);
	}

	/**
	 * Data holder class for the purpose of optimization It is better to have a
	 * object allocate memory once and use that several times rather then each
	 * object having to be created from scratch
	 * */
	public class ViewHolder {
		TextView tvName;
		TextView tvLocation;
		TextView tvStatus, tvGeneralName;
		String identifier;

		public String getId() {
			return identifier;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		/**
		 * The logic here is to optimize the process
		 * 
		 * a list view can have more elements than visible on the screen but the
		 * user only sees whats visible at that point
		 * 
		 * So can we not try to use the concept that a row that is invisbile can
		 * be assigned to the row that is just visible (via convertView)
		 * 
		 * If all is require convertView will be set to Null
		 * 
		 * Similarly, we donot want to create objects fo textview and other
		 * layout elements on each row creation so we have inner calss
		 * ViewHolder that only allocates the object memory at one time
		 * */
		View row = convertView;
		if (convertView == null) {
			LayoutInflater inflator = (LayoutInflater) context
					.getSystemService(context.LAYOUT_INFLATER_SERVICE);
			row = inflator.inflate(R.layout.list_item, parent, false);
			// row.setBackgroundColor(Color.DKGRAY);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.tvName = (TextView) row
					.findViewById(R.id.tvListCallingNumber);
			viewHolder.tvLocation = (TextView) row
					.findViewById(R.id.tvListMissedOrTaken);
			viewHolder.tvStatus = (TextView) row
					.findViewById(R.id.tvListTiming);
			// for use on later caase
			viewHolder.tvGeneralName = (TextView) row
					.findViewById(R.id.tvListCommonName);

			row.setTag(viewHolder);
		}

		ViewHolder viewHolder = (ViewHolder) row.getTag();

		// decomposing strings with delimeters of @@ 1 name 2 location 3
		// status

		Character oldChar = '\\';
		Character newChar = '\'';
		String goodList = lista[position].replace(oldChar, newChar);

		String callingnumber = goodList.substring(0,
				lista[position].indexOf("@@"));
		String remaining = lista[position].substring(lista[position]
				.indexOf("@@") + 2);
		String calltype = remaining.substring(0, remaining.indexOf("@@"));
		remaining = remaining.substring(remaining.indexOf("@@") + 2);
		String calldate = remaining.substring(0, remaining.indexOf("@@"));
		remaining = remaining.substring(remaining.indexOf("@@") + 2);
		String callduration = remaining.substring(0, remaining.indexOf("@@"));
		String rowid = remaining.substring(remaining.indexOf("@@") + 2);

		viewHolder.tvName.setText(callingnumber);
		// viewHolder.tvName.setTextSize(text_size_large);

		viewHolder.tvLocation.setText(calltype);

		// if call type is missed make the list item background red
		if (calltype.equalsIgnoreCase("missed")) {
			viewHolder.tvName.setTextColor(context.getResources().getColor(
					R.color.orange_red));

			int beeps = Integer.parseInt(callduration) / this.TIME_ONEBEEP;
			viewHolder.tvStatus.setText("Beeps:" + beeps + "/ "
					+ this.TOTALTIME / this.TIME_ONEBEEP);
		} else {
			viewHolder.tvName.setTextColor(context.getResources().getColor(
					R.color.black));
			viewHolder.tvStatus.setText("Time:" + callduration + " Sec");
		}

		// viewHolder.tvLocation.setTextSize(context.getResources()
		// .getDimension(R.dimen.textsizesmall));

		// for accessing details from database
		viewHolder.tvGeneralName.setText(calldate);
		viewHolder.identifier = rowid;

		return row; // if it reaches here something was wrong

	}

}
