package com.shasun.staffportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.shasun.staffportal.properties.DemoBase;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import webservice.WebService;

public class DeptWiseAdmissionBarChart extends DemoBase implements OnChartValueSelectedListener {
    private static String strParameters[];
    private static String ResultString = "";
    private int intOfficeId = 0;
    private ArrayList<String> BarEntryLabels; // = new ArrayList<String>(200);
    ArrayList<BarEntry> BARENTRY;
    BarChart chart ;
    //    ArrayList<BarEntry> BARENTRY ;
//    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admissioncurrentyearbarchart);
        chart = (BarChart) findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        intOfficeId = getIntent().getIntExtra("OfficeId",1);
        strParameters = new String[]{"int", "officeid", String.valueOf(intOfficeId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getDeptWiseAdmission";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    @Override
    protected void saveToGallery() {

    }

    public void displayBarChart(){
        if (BARENTRY.size() == 0) {
            Toast.makeText(DeptWiseAdmissionBarChart.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
        } else {
            Bardataset = new BarDataSet(BARENTRY, "Admission Count");
           // BARDATA = new BarData(BarEntryLabels, Bardataset);
            Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            chart.setData(BARDATA);
            chart.animateY(3000);
        }
    }

    @Override
    public void onValueSelected(Entry e,  Highlight h) {
//        Intent intent = new Intent(DeptWiseAdmissionBarChart.this, AdmissionCurrentYearBarChart.class);
//        intent.putExtra("OfficeId",e.getData().toString());
//        startActivity(intent);

        Toast.makeText(DeptWiseAdmissionBarChart.this, "Value: " + e.getData()
                + ", DataSet index: " + h.getDataSetIndex() + " Data: " + e.getData(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected() {

    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(DeptWiseAdmissionBarChart.this);

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
                JSONArray temp = new JSONArray(ResultString.toString());
                BARENTRY = new ArrayList<>();
                BarEntryLabels = new ArrayList<String>();

                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    BarEntryLabels.add(object.getString("program"));
                    BARENTRY.add(new BarEntry(Float.parseFloat(object.getString("admissioncnt")), i, object.getInt("courseid")));
                }
                displayBarChart();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}