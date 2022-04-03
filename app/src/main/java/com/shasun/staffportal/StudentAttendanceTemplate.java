package com.shasun.staffportal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import webservice.WebService;

public class StudentAttendanceTemplate extends AppCompatActivity {
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private TextView tvPageTitle, tvLastUpdated;
    private static String strParameters[];
    private static String ResultString = "";
    private long lngEmployeeId=0;
    private ArrayList<String> template_list = new ArrayList<String>(200);
    private int intFlag = 0;
    AlertDialog.Builder builder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentattendancetemplate);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        final int intMenuFlag = loginsession.getInt("menuflag", 1);
        if (intMenuFlag==1) {
            tvPageTitle.setText("My Timetable");
        }else{
            tvPageTitle.setText("Student Attendance");
        }
        intFlag=getIntent().getIntExtra("Flag",1);
        Button btnBack=(Button) findViewById(R.id.button_back);
//        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
//        tvLastUpdated.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            onBackPressed();
            }
        });
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        if (!CheckNetwork.isInternetAvailable(StudentAttendanceTemplate.this)) {
            Toast.makeText(StudentAttendanceTemplate.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
            return;
        }else {

            lngEmployeeId = loginsession.getLong("userid", 1);
            strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getStudentAttendanceTemplateJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(StudentAttendanceTemplate.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(StudentAttendanceTemplate.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(StudentAttendanceTemplate.this);

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
            try {
                if (ResultString.toString().equals("")){
                    builder = new AlertDialog.Builder(StudentAttendanceTemplate.this);
                    builder.setMessage(R.string.timetablenotfoundmsg).setTitle("Timetable Alert")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Timetable Alert");
                    alert.show();
                }
                JSONArray temp = new JSONArray(ResultString.toString());
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    String strTemplate= object.getString("dayordertemplatedesc");
                    strTemplate=strTemplate.replace("Template", "Timetable");
                    template_list.add(object.getString("officeid") + "##" +object.getString("dayordertemplateid") + "##" +object.getString("dayordertemplatedesc"));
                }
                if (template_list.size() == 0){
                    Toast.makeText(StudentAttendanceTemplate.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvTTTemplate); // Assigning the RecyclerView Object to the xml View
                    mRecyclerView.setHasFixedSize(true);
                    // Letting the system know that the list objects are of fixed size
                    StudentAttendanceTemplateLVAdapter TVA = new StudentAttendanceTemplateLVAdapter(template_list, R.layout.studentattendancetemplatelistitem);
                    mRecyclerView.setAdapter(TVA);
                    mLayoutManager = new LinearLayoutManager(StudentAttendanceTemplate.this);                 // Creating a layout Manager
                    mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Toast.makeText(StudentAttendanceTemplate.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
            }
        }
    }
}
