package com.shasun.staffportal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewPaySlip extends AppCompatActivity {
    private String strPdfFileName="";
    private long lngEmployeeId=0;
    private int intOfficeId=0;
    private int intPaystructureId=0;
    private int intPayperiodId=0;
    private String strPdfUrl="";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras()!=null){
            strPdfFileName=getIntent().getExtras().getString("employeeid");
            lngEmployeeId=Long.parseLong(getIntent().getExtras().getString("employeeid"));
            intOfficeId=Integer.parseInt(getIntent().getExtras().getString("officeid"));
            intPaystructureId=Integer.parseInt(getIntent().getExtras().getString("paystructureid"));
            intPayperiodId=Integer.parseInt(getIntent().getExtras().getString("payperiodid"));
        }
        strPdfUrl = "http://erp.shasuncollege.edu.in/evarsityshasun/workforce/report/PaySlip.jsp?iden=8&EmployeeCategoryId=-1&PayStructureId="+intPaystructureId+"&PayPeriodId="+intPayperiodId+"&perPage=1&OfficeId="+intOfficeId+"&DivisionId=0&EmployeeId="+lngEmployeeId+"&intFlag=1";
        //sstrPdfUrl = "http://183.82.33.46/evarsityshasun/workforce/jrxml/" + strPdfFileName;
        //String strUrl = "http://1.23.166.206/schoolstudentportal/attachment/Content_2082_2019_1_14_19_31_994";
        if(CheckNetwork.isInternetAvailable(ViewPaySlip.this)){
            MyPaySlipTask task = new MyPaySlipTask();
            task.execute(strPdfUrl);
        }
        else {
            Toast.makeText(ViewPaySlip.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Intent intent = new Intent(this, HomeScreen.class);
        //Grid View Menu
        Intent intent = new Intent(this, HomeScreenCategory.class);
        startActivity(intent);
//        // finish the activity picture
        this.finish();
    }

    private class MyPaySlipTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =  (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            if (bResponse==true){
                try {
                    File file = new File(strPdfUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri;
                    if (Build.VERSION.SDK_INT < 24) {
                        uri = Uri.fromFile(file);
                    } else {
                        uri = Uri.parse(file.getPath());
                    }
                    intent.setDataAndType(uri,"application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    finish();
                } catch (ActivityNotFoundException anfe) {
                    Toast.makeText(ViewPaySlip.this, "No activity found to open this attachment.", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(ViewPaySlip.this, "File does not exist!: Payroll not approved / PDF File not generated in ERP", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
