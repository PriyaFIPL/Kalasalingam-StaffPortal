package com.shasun.staffportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import webservice.SqlliteController;
import webservice.WebService;

public class NotificationForStudentsSubjectList extends AppCompatActivity {
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager

    private TextView tvPageTitle, tvLastUpdated;
    private long lngEmployeeId=0;
    private static String ResultString = "";
    private static String strParameters[];
    private ArrayList<String> sub_list = new ArrayList<String>(200);
    private String strResultMessage="";
    private int intFlag = 0;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationforstudentssubjectlist);
        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Notification to Students");
        Button btnRefresh=(Button) findViewById(R.id.button_refresh);
        Button btnBack=(Button) findViewById(R.id.button_back);
        intFlag=getIntent().getIntExtra("Flag",1);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            Intent intent;
            if (intFlag == 1){
                intent = new Intent(NotificationForStudentsSubjectList.this, HomeScreenCategory.class);
                startActivity(intent);
            }
            if (intFlag == 2){
                intent = new Intent(NotificationForStudentsSubjectList.this, HomePageGridViewLayout.class);
                startActivity(intent);
            }
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getStaffSubjectsJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
        });
        displaySubject();
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
    }

    private void displaySubject(){
        db = controllerdb.getReadableDatabase();
        try { //strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,
            Cursor cursor = db.rawQuery("SELECT subjectcode,subjectdesc,subjectid " +
                    " FROM staffsubjetcs WHERE employeeid=" + lngEmployeeId +
                    " GROUP BY subjectcode,subjectdesc,subjectid", null);
            if (cursor.moveToFirst()){
                do {
                    //tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    sub_list.add(cursor.getString(cursor.getColumnIndex("subjectid")) + "##" + cursor.getString(cursor.getColumnIndex("subjectcode")) + "##" + cursor.getString(cursor.getColumnIndex("subjectdesc")));
                } while (cursor.moveToNext());
                if (sub_list.size() == 0) {
                    Toast.makeText(NotificationForStudentsSubjectList.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvCourses); // Assigning the RecyclerView Object to the xml View
                    mRecyclerView.setHasFixedSize(true);
                    // Letting the system know that the list objects are of fixed size
                    NotificationSubjectListLVAdapter NSA = new NotificationSubjectListLVAdapter(sub_list, R.layout.notificationsubjetlistitem);
                    mRecyclerView.setAdapter(NSA);
                    mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
                    mRecyclerView.setLayoutManager(mLayoutManager);                         // Setting the layout Manager
                }
            } else {
                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getStaffSubjectsJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getStaffSubjectsJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(NotificationForStudentsSubjectList.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(NotificationForStudentsSubjectList.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(NotificationForStudentsSubjectList.this);
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
            NotificationSubjectListLVAdapter TVA = new NotificationSubjectListLVAdapter(sub_list, R.layout.studentattendancehourwisecourseslistitem);
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
                SqlliteController sc = new SqlliteController(NotificationForStudentsSubjectList.this);
                sc.deleteStaffSubjects(lngEmployeeId);

                for (int i = 0; i <= temp.length() - 1; i++){
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertStaffSubjects(lngEmployeeId, object.getString("programsection"),
                            object.getString("subjectcode"), object.getString("subjectdesc"),
                            Long.parseLong(object.getString("programsectionid")),
                            Long.parseLong(object.getString("subjectid")));
//                    sub_list.add(object.getString("subjectid")
//                            + "##" +object.getString("subjectcode")
//                            + "##" +object.getString("subjectdesc"));
                }
                displaySubject();
//                if (sub_list.size() == 0){
//                    Toast.makeText(NotificationForStudentsSubjectList.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
//                } else {
//                    mRecyclerView = (RecyclerView) findViewById(R.id.rvCourses); // Assigning the RecyclerView Object to the xml View
//                    mRecyclerView.setHasFixedSize(true);
//                    // Letting the system know that the list objects are of fixed size
//                    TVA = new NotificationSubjectListLVAdapter(sub_list, R.layout.notificationsubjetlistitem);
//                    mRecyclerView.setAdapter(TVA);
//                    mLayoutManager = new LinearLayoutManager(NotificationForStudentsSubjectList.this);                 // Creating a layout Manager
//                    mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
//                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Toast.makeText(NotificationForStudentsSubjectList.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
