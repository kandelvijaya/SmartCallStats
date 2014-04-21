package com.doprog.smartcallstats;

import org.achartengine.chartdemo.demo.chart.AverageCubicTemperatureChart;
import org.achartengine.chartdemo.demo.chart.PieChartBuilder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.doprog.graphformissed.DynamicGraphActivity;

public class ViewReport extends Activity implements View.OnClickListener {
	// attributes
	private Button btnHistory, btnGraph, btnMultiGraph, btnChart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewreport);

		intialize();
	}

	private void intialize() {
		btnHistory = (Button) findViewById(R.id.btnViewHistory);
		btnGraph = (Button) findViewById(R.id.btnViewGraph);
		btnMultiGraph = (Button) findViewById(R.id.btnMultipleChart);
		btnChart = (Button) findViewById(R.id.btnPieChart);

		btnHistory.setOnClickListener(this);
		btnGraph.setOnClickListener(this);
		btnMultiGraph.setOnClickListener(this);
		btnChart.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnViewGraph:
			Intent i = new Intent(this, DynamicGraphActivity.class);
			this.startActivity(i);
			break;

		case R.id.btnViewHistory:
			// take to view history page
			Intent intentToViewHistory = new Intent(this, ViewHistory.class);
			startActivity(intentToViewHistory);
			break;

		case R.id.btnMultipleChart:
			AverageCubicTemperatureChart acc = new AverageCubicTemperatureChart();

			Intent intent = acc.execute(this);
			startActivity(intent);
			break;

		case R.id.btnPieChart:
			Intent intentToPie = new Intent(this, PieChartBuilder.class);
			startActivity(intentToPie);
			break;

		}

	}

}
