package com.shasun.staffportal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import webservice.SqlliteController;

public class NotificationForStudentsSectionList extends AppCompatActivity {
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private ArrayList<String> section_list = new ArrayList<String>(200);
    private TextView tvPageTitle;
    private long lngEmployeeId=0, lngSubjectId=0;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationsectionlist);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Notification to Students");
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            //Recycler View Menu
            Intent intent = new Intent(NotificationForStudentsSectionList.this, NotificationForStudentsSubjectList.class);
            startActivity(intent);
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        if (getIntent().getExtras()!=null){
            lngSubjectId = getIntent().getExtras().getLong("subjectid");
        }
        displaySections();
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // new intent to call an activity that you choose
        //Recycler View menu
        Intent intent = new Intent(this, HomeScreenCategory.class);
        startActivity(intent);
        // finish the activity picture
        this.finish();
    }

    private void displaySections(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM staffsubjetcs" +
                    " WHERE employeeid=" + lngEmployeeId + " AND subjectid = " + lngSubjectId, null);
            if (cursor.moveToFirst()){
                do {
                    section_list.add(cursor.getString(cursor.getColumnIndex("programsection")) + "##" + cursor.getString(cursor.getColumnIndex("programsectionid"))+ "##" + lngSubjectId);
                } while (cursor.moveToNext());
                if (section_list.size() == 0){
                    Toast.makeText(NotificationForStudentsSectionList.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvSections); // Assigning the RecyclerView Object to the xml View
                    mRecyclerView.setHasFixedSize(true);
                    // Letting the system know that the list objects are of fixed size
                    NotificationSectionListLVAdapter NSA = new NotificationSectionListLVAdapter(section_list, R.layout.notificationsectionlistitem);
                    mRecyclerView.setAdapter(NSA);
                    mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
                    mRecyclerView.setLayoutManager(mLayoutManager);                         // Setting the layout Manager
                }
            } else{
//                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
//                WebService.strParameters = strParameters;
//                WebService.METHOD_NAME = "leaveStatusjson";
//                LeaveStatus.AsyncCallWS task = new LeaveStatus.AsyncCallWS();
//                task.execute();
            }
            cursor.close();
        }catch (Exception e){
            System.out.println(e.getMessage());

        }
    }
}
