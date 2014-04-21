package com.doprog.smartcallstats;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ToastGreen{
	
	public static void toastGood(String message, Context context){
		Toast toast=Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, toast.getXOffset(), toast.getYOffset());
		
		LinearLayout ll=new LinearLayout(context);
		ll.setGravity(0x10);
		ll.setBackgroundColor(context.getResources().getColor(R.color.forestgreen));
		ll.setOrientation(0);

		ImageView img=new ImageView(context);
		img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_good));
		img.setPadding(5, 5, 5, 5);
		
		TextView tv=new TextView(context);
		tv.setTextColor(context.getResources().getColor(R.color.floralwhite));
		tv.setTextSize(context.getResources().getDimension(R.dimen.textsizelarge));
		tv.setPadding(5, 5, 5, 5);
		
		tv.setText(message);
		
		ll.addView(img);
		ll.addView(tv);
		
		toast.setView(ll);
		toast.show();
	}
	public static void toastBad(String message, Context context){
		Toast toast=Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, toast.getXOffset(), toast.getYOffset());
		
		LinearLayout ll=new LinearLayout(context);
		ll.setGravity(0x10);
		ll.setBackgroundColor(context.getResources().getColor(R.color.forestgreen));
		ll.setOrientation(0);
		
		
		ImageView img=new ImageView(context);
		img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bad));
		img.setPadding(5, 5, 5, 5);
		
		TextView tv=new TextView(context);
		tv.setTextColor(context.getResources().getColor(R.color.floralwhite));
		tv.setTextSize(context.getResources().getDimension(R.dimen.textsizelarge));
		tv.setPadding(5, 5, 5, 5);
		
		tv.setText(message);
		
		ll.addView(img);
		ll.addView(tv);
		
		toast.setView(ll);
		toast.show();
	}
	
	public static void justToast(String message, Context context){
		Toast toast=Toast.makeText(context, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, toast.getXOffset(), toast.getYOffset());
				
		TextView tv=new TextView(context);
		tv.setTextColor(context.getResources().getColor(R.color.floralwhite));
		tv.setTextSize(context.getResources().getDimension(R.dimen.textsizelarge));
		tv.setPadding(5, 5, 5, 5);
		
		tv.setText(message);
		
		toast.setView(tv);
		toast.show();
	}

}
