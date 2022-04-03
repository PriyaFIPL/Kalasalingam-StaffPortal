package com.shasun.staffportal;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.shasun.staffportal.properties.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import webservice.WebService;

public class InternalBreakUp extends AppCompatActivity {
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private TextView pageHeader,tvPageTitle, txtExamDate, hdnExamDate;
    private static String strParameters[];
    private static String ResultString = "", strExamDate="", strResultMessage="";
    private long lngEmployeeId=0;
    private int intBreakUpId=0;

    private ArrayList<String> sub_list = new ArrayList<String>(200);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internalbreakup);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        pageHeader = (TextView) findViewById(R.id.pageHeader);
        tvPageTitle.setText("Internal Mark Entry");
        pageHeader.setText(getIntent().getExtras().getString(Properties.timeTableHeader));
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onBackPressed();
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);

        txtExamDate = findViewById(R.id.txtExamDate);
        hdnExamDate = findViewById(R.id.hdnExamDate);
        txtExamDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
        // TODO Auto-generated method stub
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear=mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker=new DatePickerDialog(InternalBreakUp.this, new DatePickerDialog.OnDateSetListener(){
                public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay){
                // TODO Auto-generated method stub
                /*      Your code   to get date and time    */

                String year1 = String.valueOf(selectedYear);
                String month1 = String.valueOf(selectedMonth + 1);
                String day1 = String.valueOf(selectedDay);
                txtExamDate.setText(day1 + "/" + month1 + "/" + year1);
                hdnExamDate.setText(year1 + "-" + month1 + "-" + day1);
                strExamDate= hdnExamDate.getText().toString();
                SharedPreferences.Editor ed = loginsession.edit();
                ed.putInt("breakupid", intBreakUpId);
                ed.putString("examdate",strExamDate);
                ed.commit();

                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId), "int", "breakupid", String.valueOf(intBreakUpId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getInternalBreakupsJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
                }
            },mYear, mMonth, mDay);
            mDatePicker.setTitle("Select date");
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();
            }
        });

        if (getIntent().getExtras()!=null){
            intBreakUpId = getIntent().getExtras().getInt("breakupid");
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
            }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(InternalBreakUp.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            //show dialog
            dialog.show();
            //Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Log.i(TAG, "doInBackground");
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
            InternalBreakUpLVAdapter TVA = new InternalBreakUpLVAdapter(sub_list, R.layout.internalbreakuplistitem);
            try{
                sub_list.clear();
                TVA.notifyDataSetChanged();
                JSONObject jsonObject = new JSONObject(ResultString.toString());
                if (jsonObject.getString("Status").equals("Error"))
                    strResultMessage = jsonObject.getString("Message");
            }
            catch (Exception e){}
            try {
                JSONArray temp = new JSONArray(ResultString.toString());
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sub_list.add(object.getString("InternalBreakUpId")
                            + "##" +object.getString("programsectionid")
                            + "##" +object.getString("subcode")
                            + "##" +object.getString("subdesc")
                            + "##" +object.getString("programsection")
                            + "##" +object.getString("conductingmaxmarks")
                            + "##" +object.getString("conversionmaxmark")
                            + "##" +object.getString("EnteredCnt"));
                }
                if (sub_list.size() == 0){
                    Toast.makeText(InternalBreakUp.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvBreakUp); // Assigning the RecyclerView Object to the xml View
                    mRecyclerView.setHasFixedSize(true);
                    // Letting the system know that the list objects are of fixed size
                    TVA = new InternalBreakUpLVAdapter(sub_list, R.layout.internalbreakuplistitem);
                    mRecyclerView.setAdapter(TVA);
                    mLayoutManager = new LinearLayoutManager(InternalBreakUp.this);                 // Creating a layout Manager
                    mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Toast.makeText(InternalBreakUp.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
