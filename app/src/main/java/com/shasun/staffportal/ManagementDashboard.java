 package com.shasun.staffportal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
//import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.shasun.staffportal.properties.Properties;

import webservice.WebService;

 public class ManagementDashboard extends AppCompatActivity implements View.OnClickListener {
    private TextView tvPageTitle;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managementdashboard);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText(getResources().getString(R.string.mManagementDashboard));
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                onBackPressed();
            }
        });

        CardView cv_currentadmission = (CardView) findViewById(R.id.mdbcurrentadmission);
        CardView cv_duelist = (CardView) findViewById(R.id.mdbduelist);
        CardView cv_staffbirthday = (CardView) findViewById(R.id.mdbstaffbirthday);
        CardView cv_staffattendance = (CardView) findViewById(R.id.mdbstaffattendance);

        //CardView cv_grievancecell = (CardView) findViewById(R.id.mdbgrievancecell);
        CardView cv_projecttracker = (CardView) findViewById(R.id.mdbprojecttracker);
        CardView cv_UpcomingEvents = (CardView) findViewById(R.id.mdbUpcomingEvents);
        CardView cv_helpdesk = (CardView) findViewById(R.id.mdbhelpdesk);

        cv_currentadmission.setOnClickListener(this);
        cv_duelist.setOnClickListener(this);
        cv_staffbirthday.setOnClickListener(this);
        cv_staffattendance.setOnClickListener(this);
        //cv_grievancecell.setOnClickListener(this);
        cv_projecttracker.setOnClickListener(this);
        cv_UpcomingEvents.setOnClickListener(this);
        cv_helpdesk.setOnClickListener(this);
    }




     @Override
    public void onClick(View v) {
        Intent intent = new Intent(ManagementDashboard.this, tabbedmanagementdashboard.class);
        switch(v.getId()){
            case R.id.mdbcurrentadmission:
                intent = new Intent(ManagementDashboard.this, AdmissionCurrentYearBarChart.class);
                //intent = new Intent(ManagementDashboard.this, AdmissionCurrentYearBarChart.class);
                intent.putExtra("ClickedId",0);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdCurrentAdmission));
                break;
            case R.id.mdbduelist:
                intent.putExtra("ClickedId",1);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdDueList));

                break;
            case R.id.mdbstaffbirthday:
                intent = new Intent(ManagementDashboard.this, MISActivity.class);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdStaffBirthday));
                intent.putExtra("ClickedId",2);
                WebService.METHOD_NAME = getResources().getString(R.string.wsBitrhdayList);


                break;
            case R.id.mdbstaffattendance:
                intent = new Intent(ManagementDashboard.this, MISActivity.class);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdStaffAttendance));
                intent.putExtra("ClickedId",3);
                WebService.strParameters = new String[]{"Long", "employeeid", "0"};
                WebService.METHOD_NAME = getResources().getString(R.string.wsLeaveDetails);


                break;
//            case R.id.mdbgrievancecell:
//                intent.putExtra("ClickedId",4);
//                break;
            case R.id.mdbprojecttracker:
                intent.putExtra("ClickedId",5);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdProjectTracker));

                break;
            case R.id.mdbUpcomingEvents:
                intent.putExtra("ClickedId",6);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdUpcomingEvents));

                break;
            case R.id.mdbhelpdesk:
                intent.putExtra("ClickedId",7);
                intent.putExtra(Properties.dashboardName,getResources().getString(R.string.mdHelpDesk));

                break;
        }
        startActivity(intent);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }
}
