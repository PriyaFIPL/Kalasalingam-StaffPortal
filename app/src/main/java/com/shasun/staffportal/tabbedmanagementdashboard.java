package com.shasun.staffportal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.shasun.staffportal.properties.Properties;

public class tabbedmanagementdashboard extends AppCompatActivity {
    TextView tvPageTitle;
    private int intFlag = 0;
    private WebView wvMgmtDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbedmanagementdashboard);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
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

        int position=getIntent().getExtras().getInt("ClickedId");
        wvMgmtDashboard = (WebView) findViewById(R.id.wvMgmtDashboard);
        wvMgmtDashboard.setWebViewClient(new WebViewClient());
        WebSettings webSettings = wvMgmtDashboard.getSettings();
        webSettings.setJavaScriptEnabled(true);

        switch (position) {
            case 0:
                wvMgmtDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/AdmissionCurrentYear.jsp");
                break;
            case 1:
                wvMgmtDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/DueListforMobile.jsp");
                break;
            case 2:
                wvMgmtDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/BirthdayList.jsp");
                break;
            case 3:
                wvMgmtDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/StaffLeaveDetailsforMobile.jsp");
                break;
//            case 4:
//                wv.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/GrievanceCell.jsp");
//                break;
            case 5:
                wvMgmtDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/ProjectTrackerStatus.jsp");
                break;
            case 6:
                wvMgmtDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/dashboard/reports/EventRegistrationList.jsp");
                break;
            case 7:
                wvMgmtDashboard.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/file/report/FileOverAllReportforMobile.jsp");
                break;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        wvMgmtDashboard.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        wvMgmtDashboard.onPause();
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
        ProgressDialog dialog = new ProgressDialog(tabbedmanagementdashboard.this);

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog.setMessage(getResources().getString(R.string.loading));
            dialog.show();
            Runnable progressRunnable = new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
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