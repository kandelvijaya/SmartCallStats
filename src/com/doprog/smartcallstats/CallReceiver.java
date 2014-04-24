package com.doprog.smartcallstats;

import java.lang.reflect.Method;import com.android.internal.telephony.ITelephony;import android.content.BroadcastReceiver;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver{

      private String incomingnumber;

      @Override      
      public void onReceive(Context context, Intent intent) {            
    	  String s[]={"9000000000","800000000"};            
    	  Bundle b = intent.getExtras();            
    	  incomingnumber = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);            
    	  try {		
      

                  TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                  Class c = Class.forName(tm.getClass().getName());                  
                  Method m = c.getDeclaredMethod("getITelephony");                  
                  m.setAccessible(true);                  
                  com.android.internal.telephony.ITelephony telephonyService = (ITelephony) m.invoke(tm);  

                  for (int i = 0; i < s.length; i++) {                        
                	  if(s[i].equals(incomingnumber)){                              
                		  telephonyService.endCall();                        
                		  
                	  }
                  }
    	  }catch(Exception e){
    		  
    	  }
      }
}