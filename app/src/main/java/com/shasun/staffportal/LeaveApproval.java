package com.shasun.staffportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import webservice.SqlliteController;
import webservice.WebService;

public class LeaveApproval extends AppCompatActivity{
    private TextView tvPageTitle, tvLastUpdated;
    private long lngEmployeeId=0;
    private static String strParameters[];
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private static String ResultString = "";
    private ArrayList<String> leavepending_list = new ArrayList<String>(200);
    SQLiteDatabase db;
    private String strResultMessage="";
    private int intFlag = 0;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaveapproval);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Leave Approval");
        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
        tvLastUpdated.setVisibility(View.GONE);
        Button btnBack=(Button) findViewById(R.id.button_back);
        Button btnRefresh=(Button) findViewById(R.id.button_refresh);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        intFlag=getIntent().getIntExtra("Flag",1);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            Intent intent;
            if (intFlag == 1){
                intent = new Intent(LeaveApproval.this, HomeScreenCategory.class);
                startActivity(intent);
            }
            if (intFlag == 2){
                intent = new Intent(LeaveApproval.this, HomePageGridViewLayout.class);
                startActivity(intent);
            }
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "listPendingLeaveApplicationsJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
            }
        });
        strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "listPendingLeaveApplicationsJson";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    public void callLeaveApproveReject(long lngLeaveApplicationId,int intLeaveStatus){
        strParameters = new String[]{"Long", "leaveapplicationid", String.valueOf(lngLeaveApplicationId),
                                     "int", "leavestatus", String.valueOf(intLeaveStatus),
                                     "Long", "approvalemployeeid", String.valueOf(lngEmployeeId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "saveLeaveApprovalJson";
        AsyncCallApproveLeaveWS task = new AsyncCallApproveLeaveWS();
        task.execute();
    }

    private class AsyncCallApproveLeaveWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(LeaveApproval.this);
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
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            try {
                Toast.makeText(LeaveApproval.this, ResultString.toString(), Toast.LENGTH_LONG).show();
                //clearForm((ViewGroup) findViewById(R.id.leaveentrylayout));
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(LeaveApproval.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(LeaveApproval.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(LeaveApproval.this);

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
                for (int i = 0; i <= temp.length() - 1; i++){
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    leavepending_list.add(object.getString("leaveapplicationid") + "##" +object.getString("employeename") + "##" + object.getString("department")+ "##" + object.getString("designation") + "##" + object.getString("leavetype") + "##" + object.getString("fromdate") + "##" + object.getString("todate") + "##" + object.getString("session") + "##" + object.getString("reason") + "##" + object.getString("noofdays"));
                }
                if (leavepending_list.size() == 0){
                    Toast.makeText(LeaveApproval.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvLeaveStatus); // Assigning the RecyclerView Object to the xml View
                    mRecyclerView.setHasFixedSize(true);
                    // Letting the system know that the list objects are of fixed size
                    LeaveApprovalLVAdapter TVA = new LeaveApprovalLVAdapter(leavepending_list, R.layout.leaveapprovallistitems);
                    mRecyclerView.setAdapter(TVA);
                    mLayoutManager = new LinearLayoutManager(LeaveApproval.this);                 // Creating a layout Manager
                    mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                Toast.makeText(LeaveApproval.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
