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

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.doprog.smartcallstats.LocalDb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Average temperature demo chart.
 */
public class AverageCubicTemperatureChart extends AbstractDemoChart {
  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Call Stats";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "This is a multiple call stats which shows both the missed and received call amount in terms of day";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {
    String[] titles = new String[] { "Missed", "Received", "Total"};
   int numberOfDays=7;
    
    
    List<double[]> x = new ArrayList<double[]>();
    for (int i = 0; i < titles.length; i++) {
      x.add(new double[] { 1, 2, 3, 4, 5, 6, 7 });
    }
    List<double[]> values = new ArrayList<double[]>();
    
    //to fill the data get the data from the db and set it here
    //third row is the sum of the first two
    //first one is missed calls
    //second one is received calls
    LocalDb ldb=new LocalDb(context);
    ldb.open();
    
    values.add(ldb.getMissedCallCountArrayForThisDays(numberOfDays));
    
    values.add(ldb.getReceivedCallCountArrayForThisDays(numberOfDays));
    
    values.add(ldb.getAllCallCountArrayForThisDays(numberOfDays));
    
    ldb.close();
    
    int[] colors = new int[] {Color.RED, Color.MAGENTA, Color.BLUE };
    PointStyle[] styles = new PointStyle[] { PointStyle.DIAMOND,
        PointStyle.TRIANGLE, PointStyle.SQUARE };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    int length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
    }
    setChartSettings(renderer, "Call Stats", "Day", "Count", 0, numberOfDays, 0, 20,
        Color.LTGRAY, Color.LTGRAY);	
    //fot the value of 20 use a dynamic max value so that someparts wont be cut off
    
    renderer.setXLabels(numberOfDays);
    renderer.setYLabels(10);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.RIGHT);
    renderer.setYLabelsAlign(Align.RIGHT);
    renderer.setZoomButtonsVisible(true);
    renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
    renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
    Intent intent = ChartFactory.getCubicLineChartIntent(context, buildDataset(titles, x, values),
        renderer, 0.33f, "Multiple Call Stats");
    return intent;
  }

}
