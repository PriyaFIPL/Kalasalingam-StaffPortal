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

public class StudentAttendanceHourCourses extends AppCompatActivity {
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private TextView tvPageTitle,pageHeader, tvLastUpdated, txtAttendanceDate, hdnAttendanceDate;
    private static String strParameters[];
    private static String ResultString = "", strAttendanceDate="";
    private long lngEmployeeId=0;
    private int intTemplateId=0, intOfficeId=0;
    private String strResultMessage="";

    private ArrayList<String> sub_list = new ArrayList<String>(200);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentattendancehourwisecourses);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        pageHeader = (TextView) findViewById(R.id.pageHeader);
        tvPageTitle.setText("Student Attendance");
        Button btnBack=(Button) findViewById(R.id.button_back);
//        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
//        tvLastUpdated.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            //Recycler View Menu
            //Intent intent = new Intent(StudentAttendanceHourCourses.this, HomeScreen.class);
            //Grid View Menu
            Intent intent = new Intent(StudentAttendanceHourCourses.this, HomeScreenCategory.class);
            startActivity(intent);
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        pageHeader.setText(getIntent().getExtras().getString(Properties.timeTableHeader));
        txtAttendanceDate = findViewById(R.id.txtAttendanceDate);
        hdnAttendanceDate = findViewById(R.id.hdnAttendanceDate);
        txtAttendanceDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
            // TODO Auto-generated method stub
            Calendar mcurrentDate=Calendar.getInstance();
            int mYear=mcurrentDate.get(Calendar.YEAR);
            int mMonth=mcurrentDate.get(Calendar.MONTH);
            int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker=new DatePickerDialog(StudentAttendanceHourCourses.this, new DatePickerDialog.OnDateSetListener(){
                public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay){
                // TODO Auto-generated method stub
                /*      Your code   to get date and time    */

                String year1 = String.valueOf(selectedYear);
                String month1 = String.valueOf(selectedMonth + 1);
                String day1 = String.valueOf(selectedDay);
                txtAttendanceDate.setText(day1 + "/" + month1 + "/" + year1);
                hdnAttendanceDate.setText(year1 + "-" + month1 + "-" + day1);
                strAttendanceDate= hdnAttendanceDate.getText().toString();
                SharedPreferences.Editor ed = loginsession.edit();
                ed.putInt("officeid", intOfficeId);
                ed.putString("attendancedate",strAttendanceDate);
                ed.commit();

                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId), "int", "attendancetemplateid", String.valueOf(intTemplateId),
                                             "String","attendancedate",String.valueOf(strAttendanceDate)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getStudentAttendanceHourWiseCoursesJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
                }
            },mYear, mMonth, mDay);
            mDatePicker.setTitle("Select Attendance Date");
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();
            }
        });

        if (getIntent().getExtras()!=null){
            intOfficeId = getIntent().getExtras().getInt("officeid");
            intTemplateId = getIntent().getExtras().getInt("templateid");
        }
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // new intent to call an activity that you choose
        //Recycler View menu
        //Intent intent = new Intent(this, HomeScreen.class);
        //Grid View menu
        Intent intent = new Intent(this, HomeScreenCategory.class);
        startActivity(intent);
        // finish the activity picture
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(StudentAttendanceHourCourses.this);

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
            StudentAttendanceHourCoursesLVAdapter TVA = new StudentAttendanceHourCoursesLVAdapter(sub_list, R.layout.studentattendancehourwisecourseslistitem);
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
                for (int i = 0; i <= temp.length() - 1; i++){
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sub_list.add(object.getString("subid")
                            + "##" +object.getString("subcode")
                            + "##" +object.getString("subdesc")
                            + "##" +object.getString("dayorderdesc")
                            + "##" +object.getString("hourdesc")
                            + "##" +object.getString("programsection")
                            + "##" +object.getString("programsectionid")
                            + "##" +object.getString("Delegateempid")
                            + "##" +object.getString("attenTransId")
                            + "##" +object.getString("dayOrderId"));
                }
                if (sub_list.size() == 0){
                    Toast.makeText(StudentAttendanceHourCourses.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvHourCourses); // Assigning the RecyclerView Object to the xml View
                    mRecyclerView.setHasFixedSize(true);
                    // Letting the system know that the list objects are of fixed size
                    TVA = new StudentAttendanceHourCoursesLVAdapter(sub_list, R.layout.studentattendancehourwisecourseslistitem);
                    mRecyclerView.setAdapter(TVA);
                    mLayoutManager = new LinearLayoutManager(StudentAttendanceHourCourses.this);                 // Creating a layout Manager
                    mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Toast.makeText(StudentAttendanceHourCourses.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
