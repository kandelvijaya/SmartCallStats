/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.chartdemo.demo.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.doprog.smartcallstats.LocalDb;
import com.doprog.smartcallstats.R;
import com.doprog.smartcallstats.ToastGreen;

public class PieChartBuilder extends Activity {
	/** Colors to be used for the pie slices. */
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,
			Color.MAGENTA, Color.CYAN };
	/** The main series that will include all the data. */
	private CategorySeries mSeries = new CategorySeries("");
	/** The main renderer for the main dataset. */
	private DefaultRenderer mRenderer = new DefaultRenderer();
	/** Button for adding entered data to the current series. */
	private Button btnUpdateChart;
	/** Edit text field for entering the slice value. */
	private EditText dayValue;
	/** The chart view that displays the data. */
	private GraphicalView mChartView;

	private int dayno = 7;

	private Context context;

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mSeries = (CategorySeries) savedState.getSerializable("current_series");
		mRenderer = (DefaultRenderer) savedState
				.getSerializable("current_renderer");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("current_series", mSeries);
		outState.putSerializable("current_renderer", mRenderer);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xy_chart);

		context = getApplicationContext();

		dayValue = (EditText) findViewById(R.id.dayValue);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(180);
		mRenderer.setDisplayValues(true);
		mRenderer.setLabelsTextSize(24);
		btnUpdateChart = (Button) findViewById(R.id.btnUpdateChart);

		btnUpdateChart.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dayValue != null) {

					// validate the day value to only hold integer from 1 and
					// above
					String value = dayValue.getText().toString();

					if (value.isEmpty()) {
						return;
					}

					int day = 7;
					try {
						day = Integer.parseInt(value);
						if (day >= 0) {
							System.out.println(day);
							dayno = day + 1; // this is the hack for not sending
												// 0 day before so that we are
												// still under
							// array index
						}
					} catch (Exception e) {
						ToastGreen.toastBad("Please enter a integer", context);
						return;
					}

				}

				drawTheChart();

			}
		});

		// for the first timer draw the chart too
		drawTheChart();
	}

	public void drawTheChart() {
		/*
		 * charts and graphs can only be drawn after the activity is loaded and
		 * ready to draw something so we do chart drawing in the thread but the
		 * problem is we can't update the view hirerachy directly via a thread
		 * 
		 * So we dont touch the ui elements in thread but call a drawable class
		 * from the achartengine library to draw itself using antoher system
		 * thread
		 */

		// first empty all the elemets from the chart and draw again
		mSeries.clear();

		Thread th = new Thread() {

			public void run() {

				try {
					sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {

					final LocalDb ldb = new LocalDb(getBaseContext());
					ldb.open();
					double value = 0;

					// FOR RECEIVED CALLS
					try {
						value = ldb
								.getTotalReceivedCallForThisDaysBefore(dayno);
					} catch (NumberFormatException e) {
						return;
					}
					mSeries.add("Total Received Calls", value);
					SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
					renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
							% COLORS.length]);
					mRenderer.addSeriesRenderer(renderer);
					mChartView.repaint();

					// FOR MISSED CALLS
					try {
						value = ldb.getTotalMissedCallForThisDaysBefore(dayno);
					} catch (NumberFormatException e) {
						return;
					}
					mSeries.add("Total missed Calls", value);
					renderer = new SimpleSeriesRenderer();
					renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
							% COLORS.length]);
					mRenderer.addSeriesRenderer(renderer);
					mChartView.repaint();

					// FOR TOTAL INCOMING CALLS
					try {
						value = ldb.getTotalCallForThisDaysBefore(dayno);
					} catch (NumberFormatException e) {
						return;
					}
					mSeries.add("Total Incoming calls", value);
					renderer = new SimpleSeriesRenderer();
					renderer.setColor(COLORS[(mSeries.getItemCount() - 1)
							% COLORS.length]);
					mRenderer.addSeriesRenderer(renderer);
					mChartView.repaint();

					ldb.close();
				}
			}
		};

		th.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			mRenderer.setClickEnabled(true);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView
							.getCurrentSeriesAndPoint();
					if (seriesSelection == null) {
						Toast.makeText(PieChartBuilder.this,
								"No chart element selected", Toast.LENGTH_SHORT)
								.show();
					} else {
						for (int i = 0; i < mSeries.getItemCount(); i++) {
							mRenderer.getSeriesRendererAt(i).setHighlighted(
									i == seriesSelection.getPointIndex());
						}
						mChartView.repaint();
						Toast.makeText(
								PieChartBuilder.this,
								"Chart data point index "
										+ seriesSelection.getPointIndex()
										+ " selected" + " point value="
										+ seriesSelection.getValue(),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}
}
