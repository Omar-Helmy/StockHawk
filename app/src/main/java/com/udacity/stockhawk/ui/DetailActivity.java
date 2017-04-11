package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String symbol = getIntent().getStringExtra("symbol");
        //String[] projection = {Contract.Quote.COLUMN_HISTORY };
        Cursor cursor = getContentResolver().query(Contract.Quote.makeUriForStock(symbol),null ,null,null,null);
        cursor.moveToFirst();
        String[] history = cursor.getString(Contract.Quote.POSITION_HISTORY).split("\n");

        GraphView graph = (GraphView) findViewById(R.id.graph);

        DataPoint[] dataPoints = new DataPoint[history.length];
        for(int i=0; i<history.length; i++) {
            String date = history[i].split(", ")[0];
            Date d = new Date(Long.parseLong(date));
            String value = history[i].split(", ")[1];
            dataPoints[i] = new DataPoint(i, ((int) (Double.parseDouble(value)*100))/100.0);
            //dataPoints[i] = new DataPoint(i,i);
        }

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

        graph.getViewport().setScrollable(true);
        graph.getViewport().setXAxisBoundsManual(true);
        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(dataPoints.length/10); // only 4 because of the space
        graph.getViewport().setMinX(dataPoints[0].getX());
        graph.getViewport().setMaxX(dataPoints[dataPoints.length/10].getX());
        // styling series
        series.setTitle("Stock:");
        series.setColor(Color.GREEN);
        series.setDrawDataPoints(true);
        series.setDataPointsRadius(10);
        series.setThickness(8);
        graph.addSeries(series);


    }
}
