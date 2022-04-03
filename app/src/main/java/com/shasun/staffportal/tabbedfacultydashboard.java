package com.shasun.staffportal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.shasun.staffportal.properties.Properties;

public class tabbedfacultydashboard extends AppCompatActivity {
    TextView tvPageTitle;
    private int intFlag = 0;
    private long lngEmployeeId = 0;
    private WebView wvFacultyDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbedfacultydashboard);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);

//        progressBar = findViewById(R.id.progressBar);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText(getIntent().getExtras().getString(Properties.dashboardName));
        intFlag=getIntent().getIntExtra("Flag",1);
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
            onBackPressed();
            }
        });

        wvFacultyDashboard = (WebView) findViewById(R.id.webview);
        wvFacultyDashboard.setWebViewClient(new WebViewClient());
        WebSettings webSettings = wvFacultyDashboard.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //       "Staff Leave","Library Transaction","Absentee Count","Todays Absentee","Project Tracker","Staff Birthday","Upcoming Events"
        int position=getIntent().getExtras().getInt("ClickedId");
        switch (position) {
            case 0:
                wvFacultyDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/StaffLeaveDetailsforMobile.jsp?EmployeeId="+lngEmployeeId);
                break;
            case 1:
                wvFacultyDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/LibraryforAndroid.jsp?EmployeeId="+lngEmployeeId);
                break;
            case 2:
                wvFacultyDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/StudentAbsenteeCountforAndroid.jsp?EmployeeId="+lngEmployeeId);
                break;
            case 3:
                wvFacultyDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/TodayAbsenteesforAndroid.jsp?EmployeeId="+lngEmployeeId);
                break;
            case 4:
                wvFacultyDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/ProjectTrackerStatus.jsp?EmployeeId="+lngEmployeeId);
                break;
            case 5:
                wvFacultyDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/BirthdayList.jsp");
                break;
            case 6:
                wvFacultyDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/EventRegistrationList.jsp?EmployeeId="+lngEmployeeId);
                break;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        wvFacultyDashboard.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        wvFacultyDashboard.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    public class WebViewClient extends android.webkit.WebViewClient {
        ProgressDialog dialog = new ProgressDialog(tabbedfacultydashboard.this);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog.setMessage(getResources().getString(R.string.loading));
            //show dialog
            dialog.show();
            Runnable progressRunnable = new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
//                    Toast.makeText(ENoticeView.this, "Slow Internet Connection", Toast.LENGTH_LONG).show();
                }
            };
            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 500);
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
//            super.onPageFinished(view, url);
        }
    }
}