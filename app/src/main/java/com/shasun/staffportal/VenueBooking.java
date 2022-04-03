package com.shasun.staffportal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import im.delight.android.webview.AdvancedWebView;

public class VenueBooking extends AppCompatActivity {
    long lngEmployeeId = 0;
    String strEmployeeName = "";
    String strHTML = "";
    private TextView tvPageTitle;
    private AdvancedWebView wvVenueBooking;
    private int intFlag = 0;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venuebooking);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Venue Booking");
        intFlag=getIntent().getIntExtra("Flag",1);
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
            Intent intent;
            if (intFlag == 1){
                intent = new Intent(VenueBooking.this, HomeScreenCategory.class);
                startActivity(intent);
            }
            if (intFlag == 2){
                intent = new Intent(VenueBooking.this, HomePageGridViewLayout.class);
                startActivity(intent);
            }
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        strEmployeeName = loginsession.getString("employeename","");
        wvVenueBooking = (AdvancedWebView) findViewById(R.id.wvVenueBooking);
        wvVenueBooking.setWebViewClient(new WebViewClient());

        WebSettings webSettings = wvVenueBooking.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvVenueBooking.getSettings().setBuiltInZoomControls(true);
        wvVenueBooking.getSettings().setSupportMultipleWindows(true);
        wvVenueBooking.getSettings().setPluginState(WebSettings.PluginState.ON);
        wvVenueBooking.getSettings().setAllowFileAccess(true);
        wvVenueBooking.getSettings().setSupportZoom(true);
        wvVenueBooking.getSettings().setUseWideViewPort(true);
        wvVenueBooking.getSettings().setLoadWithOverviewMode(true);
//        strHTML = " <html><body><iframe width=\"100%\" height=\"100%\" src=\"http://erp.shasuncollege.edu.in/evarsityshasun/general/transaction/VenueBookingforMobile.jsp?EmployeeName=\"+strEmployeeName+\"&EmployeeId=\"+lngEmployeeId\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe></body></html>";
//        wvVenueBooking.loadDataWithBaseURL("http://erp.shasuncollege.edu.in/evarsityshasun/usermanager/loginManager/youLogin.jsp",strHTML,"text/html", "utf-8",null);
        wvVenueBooking.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/general/transaction/VenueBookingforMobile.jsp?EmployeeName="+strEmployeeName+"&EmployeeId="+lngEmployeeId);
        wvVenueBooking.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            AdvancedWebView newWebView = new AdvancedWebView(VenueBooking.this);
            // myParentLayout.addView(newWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        wvVenueBooking.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        wvVenueBooking.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        wvVenueBooking.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        wvVenueBooking.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(VenueBooking.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(VenueBooking.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        ProgressDialog dialog = new ProgressDialog(VenueBooking.this);
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            dialog.setMessage(getResources().getString(R.string.loading));
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

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
}