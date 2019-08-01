/*

Author: Frank Ableson  fableson@navitend.com
Purpose: Sample application for IBM Developerworks
License: use as you like where you like. Be careful, as usual. Use at own risk.

 */
package com.navitend.sensorfun;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.highsoft.highcharts.Common.HIChartsClasses.HIChart;
import com.highsoft.highcharts.Common.HIChartsClasses.HICredits;
import com.highsoft.highcharts.Common.HIChartsClasses.HIOptions;
import com.highsoft.highcharts.Common.HIChartsClasses.HISpline;
import com.highsoft.highcharts.Common.HIChartsClasses.HITitle;
import com.highsoft.highcharts.Common.HIChartsClasses.HIXAxis;
import com.highsoft.highcharts.Common.HIChartsClasses.HIYAxis;
import com.highsoft.highcharts.Core.HIChartView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    final String tag = "SensorFun";
    SensorManager sm = null;                            // access Sensor subsystem
    Sensor light;                                       // connection to light sensor
    HIChartView chartView = null;                       // connect to UII
    HIChart chart = new HIChart();                      // the chart
    HITitle title = new HITitle();                      // title of the chart
    HIOptions options = new HIOptions();                // options for how the chart will look
    HISpline lightSensor = new HISpline();              // representation of our sensor data
    ArrayList lightData = new ArrayList<>();            // hold sensor light data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // setup sensor
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);

        // setup a representation of the light data for the chart
        lightSensor.setName("Light Sensor");
        lightSensor.setData(lightData);

        // setup chart
        chartView = (HIChartView) findViewById(R.id.hc);
        HICredits credits = new HICredits();
        credits.setEnabled(false);
        options.setCredits(credits);


        title.setText("Light Level");
        options.setTitle(title);
        chart.setType("spline");
        options.setChart(chart);

        // Y Axis
        final HIYAxis hiyAxis = new HIYAxis();
        hiyAxis.setMin(0);
        hiyAxis.setTitle(new HITitle());
        hiyAxis.getTitle().setText("Level");
        options.setYAxis(new ArrayList(){{add(hiyAxis);}});

        // X Axis
        final HIXAxis hixAxis = new HIXAxis();
        hixAxis.setType("datetime");
        hixAxis.setTickPixelInterval(150);
        options.setXAxis(new ArrayList(){{add(hixAxis);}});

        // setup data
        ArrayList series = new ArrayList<>();
        series.add(lightSensor);
        options.setSeries(series);

        // setup chart!
        chartView.setOptions(options);

    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignoring this
    }

    public void onSensorChanged(SensorEvent event) {
        synchronized ( this ) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_LIGHT:
                    HashMap<String, Object> newpoint = new HashMap<>();
                    newpoint.put("x", System.currentTimeMillis());
                    newpoint.put("y", event.values[0] );
                    if (lightData.size() > 500) {
                        lightData.remove(0);
                    }
                    lightData.add(newpoint);
                    chart.setPanning(true);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this,light,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        sm.unregisterListener(this);
        super.onStop();
    }
}
