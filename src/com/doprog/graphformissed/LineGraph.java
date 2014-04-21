package com.doprog.graphformissed;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class LineGraph {

	private GraphicalView view;
	
	private TimeSeries dataset = new TimeSeries("Missed Call Count"); 
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	
	private XYSeriesRenderer renderer = new XYSeriesRenderer(); // This will be used to customize line 1
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); // Holds a collection of XYSeriesRenderer and customizes the graph
	
	@SuppressLint("ResourceAsColor")
	@SuppressWarnings("deprecation")
	public LineGraph()
	{
		// Add single dataset to multiple dataset
		mDataset.addSeries(dataset);
		
		// Customization time for line 1!
		renderer.setColor(Color.RED);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		
		// Enable Zoom
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setXTitle("Day ->");
		mRenderer.setYTitle("Number Of Missed Calls");
		mRenderer.setAxisTitleTextSize(24);
		
		mRenderer.setChartTitleTextSize(30);
		mRenderer.setChartValuesTextSize(20);
		mRenderer.setShowGrid(true);
		mRenderer.setLegendTextSize(24);
		mRenderer.setZoomButtonsVisible(false);
		mRenderer.setZoomEnabled(false);
		
		mRenderer.setLabelsTextSize(24);
		mRenderer.setYLabelsAlign(Align.LEFT);
		mRenderer.setYLabelsColor(0, Color.BLUE);
		
		
		// Add single renderer to multiple renderer
		mRenderer.addSeriesRenderer(renderer);	
	}
	
	
	public GraphicalView getView(Context context) 
	{
		view =  ChartFactory.getLineChartView(context, mDataset, mRenderer);
		return view;
	}
	
	public void addNewPoints(Point p)
	{
		dataset.add(p.getX(), p.getY());
	}
	
}
