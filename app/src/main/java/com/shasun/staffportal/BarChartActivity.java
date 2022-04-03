
package com.shasun.staffportal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.shasun.staffportal.properties.DemoBase;
import com.shasun.staffportal.properties.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import webservice.WebService;

public class BarChartActivity extends DemoBase implements
        OnChartValueSelectedListener {

    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.admissioncurrentyearbarchart);

        TextView tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        if(getIntent().getExtras() != null) {
            tvPageTitle.setText(getIntent().getExtras().getString(Properties.dashboardName, ""));
        }
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        //xAxis.setValueFormatter(xAxisFormatter);

       // IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        //leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        //rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        WebService.strParameters = new String[]{"int", "flag", "0"};
        WebService.METHOD_NAME = "getCurrentYearAdmissionShift";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();

        //XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        //mv.setChartView(chart); // For bounds control
        //chart.setMarker(mv); // Set the marker to the chart

        // setting data
        //seekBarY.setProgress(50);
        //seekBarX.setProgress(12);

        // chart.setDrawLegend(false);
    }

    private void setData() {


        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "Current Year Admission");

            set1.setDrawIcons(false);
            set1.setColors(ColorTemplate.COLORFUL_COLORS);


            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.bar, menu);
        return true;
    }
 @Override
    protected void saveToGallery() {
        saveToGallery(chart, "BarChartActivity");
    }


    private final RectF onValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Intent intent = new Intent(BarChartActivity.this, PieChartActivity.class);
        try {
            JSONObject JSobject = new JSONObject(ResultString.toString());
            if(JSobject.has("Status") && JSobject.getString("Status").equalsIgnoreCase("Success")) {
                JSONArray temp = new JSONArray(JSobject.getString("Data"));
                int i = Math.round(e.getX());
                //[{"officename":"Shift 1","officeid":"1","updateddate":"23-06-2021 22:24","admissioncnt":"176"},{"officename":"Shift 2","officeid":"2","updateddate":"23-06-2021 22:24","admissioncnt":"66"}]

                JSONObject object = new JSONObject(temp.getJSONObject(i).toString());

                intent.putExtra("OfficeId", object.getString("officeid"));
                intent.putExtra(Properties.dashboardName, "Admission: " + object.getString("officename"));
                //Log.e("TEST",object.getString("officeid"));
                startActivity(intent);

            }

//            Toast.makeText(BarChartActivity.this," DataSet index: " + h.getDataSetIndex() + " Data: " + object.getString("OfficeId"), Toast.LENGTH_LONG).show();

        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        Log.e("TEST",e.getX()+"" );
//

    }

    @Override
    public void onNothingSelected() { }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(BarChartActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            //show dialog
            dialog.show();
            //Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //Log.i(TAG, "onPostExecute");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                JSONObject JSobject = new JSONObject(ResultString.toString());
                if(JSobject.has("Status") && JSobject.getString("Status").equalsIgnoreCase("Success")) {
                    JSONArray temp = new JSONArray(JSobject.getString("Data"));


                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    values.add(new BarEntry(i,Float.parseFloat(object.getString("admissioncnt"))));
                }
               setData();
                }else{
                    Toast.makeText(BarChartActivity.this,JSobject.getString("Message"),Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Toast.makeText(BarChartActivity.this,ResultString,Toast.LENGTH_LONG).show();
            }
        }
    }
    ArrayList<BarEntry> values = new ArrayList<>();
    private static String ResultString = "";

    public class DayAxisValueFormatter extends ValueFormatter {
        private final BarLineChartBase<?> chart;
        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }
        @Override
        public String getFormattedValue(float value) {
            int val = Math.round(value)+1;

            return "Shift-" + val;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
