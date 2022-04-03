package com.shasun.staffportal;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import im.delight.android.webview.AdvancedWebView;

public class LMSIndex extends AppCompatActivity implements AdvancedWebView.Listener {
    long lngEmployeeId = 0;
    String strEmployeeName = "";
    TextView tvPageTitle;
    private AdvancedWebView wvLMS;
    private int intFlag = 0;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lms);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        intFlag=getIntent().getIntExtra("Flag",1);
        strEmployeeName = loginsession.getString("employeename","");
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Create Assignment");
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
            Intent intent;
            if (intFlag == 1){
                intent = new Intent(LMSIndex.this, HomeScreenCategory.class);
                startActivity(intent);
            }
            if (intFlag == 2){
                intent = new Intent(LMSIndex.this, HomePageGridViewLayout.class);
                startActivity(intent);
            }
            }
        });

        wvLMS = (AdvancedWebView) findViewById(R.id.wvLms);
        wvLMS.setWebViewClient(new WebViewClient());
        wvLMS.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = wvLMS.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvLMS.getSettings().setBuiltInZoomControls(true);
        wvLMS.getSettings().setSupportMultipleWindows(true);
        wvLMS.getSettings().setPluginState(WebSettings.PluginState.ON);
        wvLMS.getSettings().setAllowFileAccess(true);
        wvLMS.getSettings().setSupportZoom(true);
        wvLMS.getSettings().setUseWideViewPort(true);
        wvLMS.getSettings().setLoadWithOverviewMode(true);
        wvLMS.loadUrl("http://erp.shasuncollege.edu.in/evarsityshasun/lms/transaction/LMSIndexforMobile.jsp?EmployeeId="+lngEmployeeId);

        wvLMS.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWebView = new AdvancedWebView(LMSIndex.this);
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
        wvLMS.onResume();
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        wvLMS.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        wvLMS.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        wvLMS.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(LMSIndex.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(LMSIndex.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private String getMimeType(String url){
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return extension;
    }

    private boolean handleUri(WebView view,final Uri uri,String strMimeType) {
//        Log.i(TAG, "Uri =" + uri);
//        final String host = uri.getHost();
//        final String scheme = uri.getScheme();
        // Based on some condition you need to determine if you are going to load the url
        // in your web view itself or in a browser.
        // You can use `host` or `scheme` or any part of the `uri` to decide.
        final Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        if (strMimeType.contains("pdf")){
            intent.setDataAndType(uri, "application/pdf");
        }
        try {
            view.getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //user does not have a pdf viewer installed
        }
        return true;
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        ProgressDialog dialog = new ProgressDialog(LMSIndex.this);

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

        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String strMimeType = getMimeType(url);
            String strExtension = url.substring(url.lastIndexOf(".") + 1);
            String tempurl = url.substring(0,(url.length() - (strExtension.length()+1)));
            final Uri uri = Uri.parse(tempurl);
            return handleUri(view, uri,strMimeType);
        }
        @RequiresApi(Build.VERSION_CODES.N)
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final Uri uri = request.getUrl();
            String strMimeType = getMimeType(uri.toString());
            return handleUri(view, uri,strMimeType);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }
}
