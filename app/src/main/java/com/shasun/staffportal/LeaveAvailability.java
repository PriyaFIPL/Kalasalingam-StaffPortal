package com.shasun.staffportal;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import webservice.SqlliteController;
import webservice.WebService;

public class LeaveAvailability extends AppCompatActivity {
    long lngEmployeeId = 0;
    String strEmployeeName = "";
    TextView tvPageTitle;
    String strHTML = "";
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private static String ResultString = "";
    private String strResultMessage = "";
    private ArrayList<String> leavestatus_list = new ArrayList<String>(200);


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaveavailability);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Leave Availability");
        Button btnBack = (Button) findViewById(R.id.button_back);
        TableLayout tbLeaveAvailability = (TableLayout) findViewById(R.id.tbLeaveAvailability);
        tbLeaveAvailability.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeaveAvailability.this, LeaveEntry.class);
                startActivity(intent);
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        strEmployeeName = loginsession.getString("employeename", "");
        WebService.strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
        WebService.METHOD_NAME = "listLeaveAvailability";
        if (!CheckNetwork.isInternetAvailable(LeaveAvailability.this)) {
            Toast.makeText(LeaveAvailability.this, getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
            return;
        } else {

            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();

        // ...
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {

        // ...
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // new intent to call an activity that you choose
        Intent intent = new Intent(this, LeaveEntry.class);
        setResult(RESULT_OK, intent);
        startActivity(intent);
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(LeaveAvailability.this);

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
        protected void onPostExecute(Void result) {
            //Log.i(TAG, "onPostExecute");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            //Log.e("TEST:", ResultString);
            leavestatus_list.clear();
            try {
                JSONArray temp = new JSONArray(ResultString.toString());
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    leavestatus_list.add(object.getString("leavetype") + "##" + object.getString("leaveavailability") + "##" + object.getString("eligibility"));
                }
                mRecyclerView = (RecyclerView) findViewById(R.id.rvLeaveStatus); // Assigning the RecyclerView Object to the xml View
                LeaveAvailabilityLVAdapter TVA = new LeaveAvailabilityLVAdapter(leavestatus_list, R.layout.leaveavailabilitylistitem);
                TVA.notifyDataSetChanged();
                mRecyclerView.setAdapter(TVA);
                mLayoutManager = new LinearLayoutManager(getApplicationContext());                 // Creating a layout Manager
                mRecyclerView.setLayoutManager(mLayoutManager);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }
    }

}


