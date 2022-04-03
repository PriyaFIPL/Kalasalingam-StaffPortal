package com.shasun.staffportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import webservice.WebService;

public class StaffBiometricLog extends AppCompatActivity {
    private TextView tvPageTitle, tvLastUpdated;
    private long lngEmployeeId=0;
    private static String strParameters[];
    private static String ResultString = "";
    private String strResultMessage="";
    private ArrayList<String> attendance_list = new ArrayList<String>(200);
    private int intFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staffbiometriclog);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Biometric Log");
        Button btnBack=(Button) findViewById(R.id.button_back);
        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdatedDate);
        intFlag=getIntent().getIntExtra("Flag",1);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            Intent intent;
            if (intFlag == 1){
                intent = new Intent(StaffBiometricLog.this, HomeScreenCategory.class);
                startActivity(intent);
            }
            if (intFlag == 2){
                intent = new Intent(StaffBiometricLog.this, HomePageGridViewLayout.class);
                startActivity(intent);
            }
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getBiometriclogJson";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(StaffBiometricLog.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(StaffBiometricLog.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(StaffBiometricLog.this);

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
            try{
                JSONObject jsonObject = new JSONObject(ResultString.toString());
                if (jsonObject.getString("Status").equals("Error"))
                    strResultMessage = jsonObject.getString("Message");
            }
            catch (Exception e){}
            try {
                JSONArray temp = new JSONArray(ResultString.toString());
                String strRow="";
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    strRow = object.getString("attendancedate") + "##" +
                            object.getString("firstin") + "##" +
                            object.getString("lastout") + "##" +
                            object.getString("totalhours") + "##" +
                            object.getString("late");

                    attendance_list.add(object.getString("attendancedate")
                            + "##" + object.getString("firstin")
                            + "##" + object.getString("lastout").trim()
                            + "##" + object.getString("totalhours")
                            + "##" + object.getString("late"));

                    if (!strRow.equals(""))
                        addData(strRow,i);
                    else
                        break;
                }
                if (strRow.equals("")){
                    Toast.makeText(StaffBiometricLog.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                Toast.makeText(StaffBiometricLog.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addData(String strRow, int bColor){
        TableLayout tl = findViewById(R.id.tblStaffAttendanceList);
        TableRow tr = new TableRow(StaffBiometricLog.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(10, 10, 10, 10);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        String[] strColumns = strRow.split("##");
        if (bColor % 2 != 0){
            tr.setBackgroundResource(R.color.cardColorg);
        }else{
            tr.setBackgroundResource(R.color.colorWhite);

        }
        //attendance date
        TextView tv = new TextView(StaffBiometricLog.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[0].trim());
//        tv.setWidth(300);
        tv.setPadding(15, 15, 15, 15);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setTextSize(getResources().getDimension(R.dimen.bio_data));
        tr.addView(tv);
        //First In
        tv = new TextView(StaffBiometricLog.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[1].trim()); // + "(" + strColumns[2].trim() + ")");
        tv.setPadding(15, 15, 15, 15);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setTextSize(getResources().getDimension(R.dimen.bio_data));
        tr.addView(tv);

        //Last Out
        tv = new TextView(StaffBiometricLog.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[2].trim());
        tv.setPadding(15, 15, 15, 15);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        tv.setTextSize(getResources().getDimension(R.dimen.bio_data));
        tr.addView(tv);

        //Total Hrs
        tv = new TextView(StaffBiometricLog.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[3].trim());
        tv.setPadding(15, 15, 15, 15);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        tv.setTextSize(getResources().getDimension(R.dimen.bio_data));
        tr.addView(tv);
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
}