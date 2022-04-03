
package com.shasun.staffportal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.shasun.staffportal.properties.DemoBase;
import com.shasun.staffportal.properties.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import webservice.WebService;

public class PieChartActivity extends DemoBase implements
        OnChartValueSelectedListener {

    private PieChart chart;
    private static String ResultString = "";
    private int intOfficeId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pie_chart);
        TextView tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        if(getIntent().getExtras() != null) {
            tvPageTitle.setText(getIntent().getExtras().getString(Properties.dashboardName, ""));
        } Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        chart = findViewById(R.id.chart1);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(38f);
        chart.setTransparentCircleRadius(51f);

        chart.setDrawCenterText(true);
        chart.setDrawEntryLabels(false);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.setCenterText("Admission Count");



        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);
        String offID = "1";
        if(getIntent() != null && getIntent().getExtras() != null) {
            offID = getIntent().getExtras().getString("OfficeId", 1 + "");
            intOfficeId = Integer.parseInt(offID);
            WebService.strParameters = new String[]{"int", "officeid", String.valueOf(intOfficeId)};
            WebService.METHOD_NAME = "getDeptWiseAdmission";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }


        chart.animateY(400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);
    }

    private void setData() {

        PieDataSet dataSet = new PieDataSet(entries, "Admission Count");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }


    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "PieChartActivity");
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(PieChartActivity.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            //show dialog
            dialog.show();
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
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                JSONObject JSobject = new JSONObject(ResultString.toString());
                if(JSobject.has("Status") && JSobject.getString("Status").equalsIgnoreCase("Success")) {
                    JSONArray temp = new JSONArray(JSobject.getString("Data"));


                    for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    entries.add(new PieEntry(Float.parseFloat(object.getString("admissioncnt")),object.getString("program") ));
                }
                setData();
                //displayBarChart();
            }else{
                Toast.makeText(PieChartActivity.this,JSobject.getString("Message"),Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            Toast.makeText(PieChartActivity.this,ResultString,Toast.LENGTH_LONG).show();
        }
        }
    }
    ArrayList<PieEntry> entries = new ArrayList<>();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
