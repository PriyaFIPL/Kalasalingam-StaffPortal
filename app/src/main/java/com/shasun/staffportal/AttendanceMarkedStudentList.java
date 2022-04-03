package com.shasun.staffportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.shasun.staffportal.properties.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import webservice.WebService;

public class AttendanceMarkedStudentList extends AppCompatActivity implements View.OnClickListener{
    private TextView tvPageTitle, tvLastUpdated,attendanceHeader1,attendanceHeader2;
    private long lngAttenTransId=0;
    private static String strParameters[];
    private static String ResultString = "";
    private String strResultMessage="";
    private Button btAttenStatus;
    private ArrayList<Button> allBts = new ArrayList<Button>();
    private ArrayList<String> student_list = new ArrayList<String>(200);

    AlertDialog.Builder alertDialogBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentattendancestudentlist);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Attendance Marked View");
        attendanceHeader1 = (TextView) findViewById(R.id.attendanceHeader1);
        attendanceHeader2 = (TextView) findViewById(R.id.attendanceHeader2);
        attendanceHeader1.setText(getIntent().getExtras().getString(Properties.attendanceHeader1));
        attendanceHeader2.setText(getIntent().getExtras().getString(Properties.attendanceHeader2));

        Button btnBack=(Button) findViewById(R.id.button_back);
        ImageButton btnSave=(ImageButton) findViewById(R.id.AttendanceSave);
        btnSave.setBackgroundResource(R.drawable.ic_baseline_cancel_presentation_24);

        btnSave.setOnClickListener(this);
       // btnSave.setText("X");
        //tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
       // tvLastUpdated.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            //Recycler View Menu
            //Intent intent = new Intent(AttendanceMarkedStudentList.this, HomeScreen.class);
            Intent intent = new Intent(AttendanceMarkedStudentList.this, HomeScreenCategory.class);
            startActivity(intent);
            }
        });
        if (getIntent().getExtras()!=null){
            lngAttenTransId=getIntent().getExtras().getLong("attentransid");
        }
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        if (!CheckNetwork.isInternetAvailable(getApplicationContext())) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                return;
            }else {
            strParameters = new String[]{"Long", "transid", String.valueOf(lngAttenTransId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getAttendanceDetailsJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.AttendanceSave:
                // set title
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Attendance Entry CANCEL");
                // set dialog message
                alertDialogBuilder
                    .setMessage("Do you Want to Save?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            hideKeyboard(AttendanceMarkedStudentList.this);
                            if (lngAttenTransId > 0){
                                strParameters = new String[]{"Long", "attendancetransactionid", String.valueOf(lngAttenTransId)};
                                WebService.strParameters = strParameters;
                                WebService.METHOD_NAME = "cancelStudentAttendance";
                                AsyncCallSaveWS task = new AsyncCallSaveWS();
                                task.execute();
                            }
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
        }
    }

    private void cleanTable(TableLayout table){
        int childCount = table.getChildCount();
        // Remove all rows except the first one
        if (childCount > 0){
            table.removeViews(0, childCount);
        }
    }

    private class AsyncCallSaveWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(AttendanceMarkedStudentList.this);
        @Override
        protected void onPreExecute(){
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
                JSONObject jsonObject = new JSONObject(ResultString);
                alertDialogBuilder = new AlertDialog.Builder(AttendanceMarkedStudentList.this);
                alertDialogBuilder.setTitle("CANCEL Message");
                // set dialog message
                alertDialogBuilder
                        .setMessage(jsonObject.getString("Message"))
                        .setCancelable(false)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id){
                                cleanTable((TableLayout) findViewById(R.id.tblAttendanceStudentList));
                                Intent intent = new Intent(AttendanceMarkedStudentList.this, HomeScreenCategory.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();// show it
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }

    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // new intent to call an activity that you choose
        Intent intent = new Intent(this, HomeScreenCategory.class);
        //Grid View Menu
        //Intent intent = new Intent(this, HomePageGridViewLayout.class);
        startActivity(intent);
        // finish the activity picture
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(AttendanceMarkedStudentList.this);

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
                for (int i = 0; i <= temp.length() - 1; i++){
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    strRow = object.getString("uniqueid")+ "##"+
                            object.getString("studentName")+ "##"+
                            object.getString("registerNumber").trim()+ "##"+
                            object.getString("studentAttendance");

                    student_list.add(object.getString("uniqueid")
                            + "##" +object.getString("studentName")
                            + "##" +object.getString("registerNumber").trim()
                            + "##" +object.getString("studentAttendance"));
                    if (!strRow.equals(""))
                        addData(strRow,i);
                    else
                        break;
                }
                if (strRow.equals("")){
                    Toast.makeText(AttendanceMarkedStudentList.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                Toast.makeText(AttendanceMarkedStudentList.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

//    View.OnClickListener getOnClickDoSomething(final Button button, final TextView tv1, final TextView tv2){
//        return new View.OnClickListener() {
//            public void onClick(View v) {
//            if (button.getText().toString().trim().equals("\u2713 P")){
//                button.setText("\u2718 A");
//                button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
//                tv1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
//                tv2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
//            }
//            else{
//                button.setText("\u2713 P");
//                button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
//                tv1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
//                tv2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
//            }
//            }
//        };
//    }




    public void addData(String strRow,int bColor){
        TableLayout tl = findViewById(R.id.tblAttendanceStudentList);
        TableRow tr = new TableRow(AttendanceMarkedStudentList.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(15, 15, 15, 15);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        String[] strColumns = strRow.split("##");
        if (bColor % 2 != 0){
            tr.setBackgroundResource(R.color.cardColorg);
        }else{
            tr.setBackgroundResource(R.color.colorWhite);

        }

        btAttenStatus = new Button(this);
        TextView tvRegNO = new TextView(AttendanceMarkedStudentList.this);
        TextView tvStudentName = new TextView(AttendanceMarkedStudentList.this);
        TextView tvStatus = new TextView(AttendanceMarkedStudentList.this);
        //registerno
        //TextView tv = new TextView(AttendanceMarkedStudentList.this);
        tvRegNO.setLayoutParams(params1);
        tvRegNO.setText(strColumns[2].trim());
        tvRegNO.setPadding(15, 15, 10, 15);
        tvRegNO.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tvRegNO.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tvRegNO);
        //Student Name
        tvStudentName = new TextView(AttendanceMarkedStudentList.this);
        tvStudentName.setLayoutParams(params1);
        tvStudentName.setText(strColumns[1].trim()); // + "(" + strColumns[2].trim() + ")");
        tvStudentName.setPadding(0, 15, 0, 15);
        tvStudentName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tvStudentName.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tvStudentName);

//        String[] strInnerColumns = strColumns[0].split("_");
//        btAttenStatus.setTag("btStudentAttenStatus"+Integer.parseInt(strInnerColumns[1].toString()));
        //btAttenStatus.setOnClickListener(getOnClickDoSomething(btAttenStatus, tvRegNO, tvStudentName));
//        allBts.add(btAttenStatus);

        tvStatus.setLayoutParams(params1);
        tvStatus.setText(strColumns[3].trim());
        tvStatus.setPadding(15, 15, 15, 15);
        tvStatus.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (bColor % 2 != 0){
            tvStatus.setBackgroundResource(R.color.cardColorg);
        }else{
            tvStatus.setBackgroundResource(R.color.colorWhite);

        }

        if (strColumns[3].trim().equals("P")){
            tvStatus.setText("P");
            tvRegNO.setTextColor(getResources().getColor(R.color.colorGreen));
            tvStudentName.setTextColor(getResources().getColor(R.color.colorGreen));
            tvStatus.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        else{
            tvStatus.setText("A");
            tvRegNO.setTextColor(getResources().getColor(R.color.colorRed));
            tvStudentName.setTextColor(getResources().getColor(R.color.colorRed));
            tvStatus.setTextColor(getResources().getColor(R.color.colorRed));
        }
        tvStatus.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tvStatus);
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
}
