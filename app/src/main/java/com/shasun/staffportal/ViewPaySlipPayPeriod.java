package com.shasun.staffportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import webservice.SqlliteController;
import webservice.WebService;

public class ViewPaySlipPayPeriod extends AppCompatActivity {
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private TextView tvPageTitle, tvLastUpdated;
    private static String strParameters[];
    private String strResultMessage="";
    private static String ResultString = "";
    private long lngEmployeeId=0;
    private ArrayList<String> payperiod_list = new ArrayList<String>(200);
    SQLiteDatabase db;
    private int intFlag = 0;
    SqlliteController controllerdb = new SqlliteController(this);

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpayslippayperiod);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("PaySlip");
        Button btnBack=(Button) findViewById(R.id.button_back);
        Button btnRefresh=(Button) findViewById(R.id.button_refresh);
        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdatedDate);
        intFlag=getIntent().getIntExtra("Flag",1);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            onBackPressed();
            }
        });
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getPayperiodJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
            }
        });
        displayPayPeriod();
    }

    public void displayPayPeriod(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM payslippayperiod WHERE employeeid=" + lngEmployeeId, null);
            if (cursor.moveToFirst()){
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    payperiod_list.add(cursor.getString(cursor.getColumnIndex("officeid")) + "##" +
                            cursor.getString(cursor.getColumnIndex("employeeid")) + "##" +
                            cursor.getString(cursor.getColumnIndex("paystructureid")) + "##" +
                            cursor.getString(cursor.getColumnIndex("payperiodid")) + "##" +
                            cursor.getString(cursor.getColumnIndex("payperioddesc")) + "##" +
                            cursor.getString(cursor.getColumnIndex("payslipfilename")));
                } while (cursor.moveToNext());
                if (payperiod_list.size() == 0) {
                    Toast.makeText(ViewPaySlipPayPeriod.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvPayPeriod); // Assigning the RecyclerView Object to the xml View
                    mRecyclerView.setHasFixedSize(true);
                    // Letting the system know that the list objects are of fixed size
                    ViewPaySlipPayPeriodLVAdapter TVA = new ViewPaySlipPayPeriodLVAdapter(payperiod_list, R.layout.viewpayslippayperiodlistitem);
                    mRecyclerView.setAdapter(TVA);
                    mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
                    mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
                }
            } else {
                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getPayperiodJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getPayperiodJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(ViewPaySlipPayPeriod.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(ViewPaySlipPayPeriod.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(ViewPaySlipPayPeriod.this);

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
                payperiod_list.clear();
                JSONObject jsonObject = new JSONObject(ResultString.toString());
                if (jsonObject.getString("Status").equals("Error"))
                    strResultMessage = jsonObject.getString("Message");
            }
            catch (Exception e){}
            try {
                JSONArray temp = new JSONArray(ResultString.toString());
                SqlliteController sc = new SqlliteController(ViewPaySlipPayPeriod.this);
                sc.deletePaySlipPayPeriod(lngEmployeeId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    sc.insertPaySlipPayPeriod(lngEmployeeId, object.getInt("officeid"),
                            object.getInt("paystructureid"), object.getInt("payperiodid"),
                            object.getString("payperioddesc"), object.getString("payslipfilename"));
                    //payperiod_list.add(object.getString("payperioddesc") + "##" +object.getString("payslipfilename"));
                }
                displayPayPeriod();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                Toast.makeText(ViewPaySlipPayPeriod.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }
}
