package com.shasun.staffportal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.shasun.staffportal.properties.Properties;

import webservice.WebService;

public class FacultyDashboard extends AppCompatActivity implements View.OnClickListener {
    long lngEmployeeId = 0;
    TextView tvPageTitle;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facultydashboard);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText(getResources().getString(R.string.mFacultyDashBoard));

        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
            onBackPressed();
            }
        });

        CardView cv_staffleave = (CardView) findViewById(R.id.fdbstaffleave);
        CardView cv_librarytransaction = (CardView) findViewById(R.id.fdblibrarytransactions);
        CardView cv_absenteecount = (CardView) findViewById(R.id.fdbabsenteecount);
        CardView cv_todaysabsentee = (CardView) findViewById(R.id.fdbtodaysabsentee);
        CardView cv_projecttracker = (CardView) findViewById(R.id.fdbprojecttracker);
        CardView cv_staffbirthday = (CardView) findViewById(R.id.fdbstaffbirthday);
        CardView cv_UpcomingEvents = (CardView) findViewById(R.id.fdbUpcomingEvents);

        cv_staffleave.setOnClickListener(this);
        cv_librarytransaction.setOnClickListener(this);
        cv_absenteecount.setOnClickListener(this);
        cv_todaysabsentee.setOnClickListener(this);
        cv_projecttracker.setOnClickListener(this);
        cv_staffbirthday.setOnClickListener(this);
        cv_UpcomingEvents.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(FacultyDashboard.this, tabbedfacultydashboard.class);
        switch(v.getId()){
            case R.id.fdbstaffleave:
                intent = new Intent(FacultyDashboard.this, MISActivity.class);
                WebService.strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                WebService.METHOD_NAME = getResources().getString(R.string.wsLeaveDetails);

                intent.putExtra("ClickedId",0);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdStaffLeave));


                break;
            case R.id.fdblibrarytransactions:
                intent.putExtra("ClickedId",1);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdLibraryTransaction));

                break;
            case R.id.fdbabsenteecount:
                intent = new Intent(FacultyDashboard.this, MISActivity.class);
                WebService.strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                WebService.METHOD_NAME = getResources().getString(R.string.wsAbsenteeCount);

                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdAbsenteeCount));
                intent.putExtra("ClickedId",2);


                break;
            case R.id.fdbtodaysabsentee:
                intent = new Intent(FacultyDashboard.this, MISActivity.class);
                WebService.strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                WebService.METHOD_NAME = getResources().getString(R.string.wsTodayAbsentee);
                intent.putExtra("ClickedId",3);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdTodaysAbsentee));

                break;
            case R.id.fdbprojecttracker:
                intent.putExtra("ClickedId",4);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdProjectTracker));

                break;
            case R.id.fdbstaffbirthday:
                intent = new Intent(FacultyDashboard.this, MISActivity.class);
                WebService.METHOD_NAME = getResources().getString(R.string.wsBitrhdayList);
                intent.putExtra("ClickedId",5);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdStaffBirthday));
                break;
            case R.id.fdbUpcomingEvents:
                intent.putExtra("ClickedId",6);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdUpcomingEvents));
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }
}
