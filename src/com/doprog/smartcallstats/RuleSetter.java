package com.doprog.smartcallstats;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RuleSetter extends Activity implements View.OnClickListener {

	// attribute
	private int id;
	private Context context;
	private TextView tvNumber, tvToggleBlock;

	private EditText etPriorityLevel, etRemindInterval;
	private Button btnUpdate;
	private boolean isBlocked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.rulesetter);

		id = Integer.parseInt(getIntent().getExtras().getString("id"));
		context = this;

		Log.d("the obtained id is", "" + id);

		getViews();
		setTheValuesInTheFields();
	}

	private void getViews() {
		tvNumber = (TextView) findViewById(R.id.tvNumber);
		tvToggleBlock = (TextView) findViewById(R.id.tvBlockIt);
		etPriorityLevel = (EditText) findViewById(R.id.etPriority);
		etRemindInterval = (EditText) findViewById(R.id.etInterval);
		btnUpdate = (Button) findViewById(R.id.btnUpadateRule);

		btnUpdate.setOnClickListener(this);
		tvToggleBlock.setOnClickListener(this);
	}

	private void setTheValuesInTheFields() {
		LocalDb ldb = new LocalDb(context);
		ldb.open();

		String[] detail = ldb.getArrayDetailForThisCallId(id);

		ldb.close();

		Log.d("checkpoint", detail[0] + detail[1]);

		tvNumber.setText(detail[0]);
		etPriorityLevel.setText(detail[1]);
		etRemindInterval.setText(detail[2]);

		short hasBeenBlocked=(short) Integer.parseInt(detail[3]);		//1 for blocked 0 for not blocked
		
		
		if(hasBeenBlocked==1){
			isBlocked=true;
			Log.d("checkpoint", "blocked"+detail[3]);
		}else{
			isBlocked=false;
			Log.d("checkpoint", "not blocked"+ detail[3]);
		}

		changeBlockStatus();

	}

	private void changeBlockStatus() {
		if (isBlocked) {
			tvToggleBlock.setText("Remove Block");

		} else {
			tvToggleBlock.setText("Add Block");

		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnUpadateRule:
			int priorityLevel;
			boolean isValueOkay = true;
			boolean blockesStatus = false;
			int remindInterval;
			try {
				priorityLevel = Integer.parseInt(etPriorityLevel.getText()
						.toString());

				if (!(priorityLevel <= 3 && priorityLevel >= 0)) {
					ToastGreen.toastBad(
							"Plz input priority level between 0 and 4 only",
							context);
					isValueOkay = false;
				}

			} catch (Exception e) {
				priorityLevel = 0;
			}

			try {
				remindInterval = Integer.parseInt(etRemindInterval.getText()
						.toString());
				if (!(remindInterval <= 60 && priorityLevel >= 0)) {
					ToastGreen.toastBad(
							"Plz input interval level between 0 and 60 only",
							context);
					isValueOkay = false;
				}
			} catch (Exception e) {
				remindInterval = 0;
			}
			
			blockesStatus=isBlocked;
			
			short blockCode=0;
			
			if(blockesStatus){
				Log.d("checkpoint", "blocked");
				blockCode=1;
			}

			if (isValueOkay) {
				LocalDb ldb = new LocalDb(context);
				ldb.open();
				ldb.updateRulesForThisNumber(priorityLevel, remindInterval,
						blockCode, tvNumber.getText().toString());
				ldb.close();

				ToastGreen.toastGood("Successfully Updated: REFRESH to see!! ",
						context);
			} else {
				ToastGreen.toastBad("Correct the format", context);
			}
			break;

		case R.id.tvBlockIt:

			if (isBlocked) {
				isBlocked = false;
			} else {
				isBlocked = true;
			}

			changeBlockStatus();
			break;
		}
	}
}
