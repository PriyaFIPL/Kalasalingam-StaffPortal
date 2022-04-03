package com.shasun.staffportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.util.StringTokenizer;

import webservice.WebService;

public class StudentAttendanceStudentList  extends AppCompatActivity implements View.OnClickListener{
    private TextView tvPageTitle,attendanceHeader1,attendanceHeader2;
    private static String strParameters[];
    private static String ResultString = "";
    private long lngProgSecId=0, lngEmployeeId=0, lngSubId=0;
    private int intOfficeId=0;
    private String strAttendanceDate="", strIds="";
    private Button btAttenStatus;
    private String strResultMessage="";
    ImageButton butSave;
    private ArrayList<Button> allBts = new ArrayList<Button>();
    private ArrayList<String> student_list = new ArrayList<String>(200);
    AlertDialog.Builder alertDialogBuilder;
    final float textSize = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.studentattendancestudentlist);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        attendanceHeader1 = (TextView) findViewById(R.id.attendanceHeader1);
        attendanceHeader2 = (TextView) findViewById(R.id.attendanceHeader2);
        tvPageTitle.setText("Student List");
        attendanceHeader1.setText(getIntent().getExtras().getString(Properties.attendanceHeader1));
        attendanceHeader2.setText(getIntent().getExtras().getString(Properties.attendanceHeader2));
        Button btnBack=(Button) findViewById(R.id.button_back);
//        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
//        tvLastUpdated.setVisibility(View.INVISIBLE);
        butSave = (ImageButton) findViewById(R.id.AttendanceSave);
        butSave.setBackgroundResource(R.drawable.ic_baseline_save_alt_24);
        butSave.setOnClickListener(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            //Recycler View Menu
            //Intent intent = new Intent(StudentAttendanceStudentList.this, HomeScreen.class);
            //Grid View Menu
            Intent intent = new Intent(StudentAttendanceStudentList.this, HomeScreenCategory.class);
            startActivity(intent);
                StudentAttendanceStudentList.this.finish();
            }
        });
        if (getIntent().getExtras()!=null){
            lngProgSecId=getIntent().getExtras().getLong("progsecid");
            lngSubId = getIntent().getExtras().getLong("subid");
            strIds = getIntent().getExtras().getString("ids");
        }
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        intOfficeId = loginsession.getInt("officeid", 0);
        strAttendanceDate = loginsession.getString("attendancedate", "");
        lngEmployeeId = loginsession.getLong("userid", 1);
        strParameters = new String[]{"Long", "subjectid", String.valueOf(lngSubId),
            "Long","officeid", String.valueOf(intOfficeId),
            "Long","programsectionid", String.valueOf(lngProgSecId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getStudentAttendanceListJson";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // new intent to call an activity that you choose
        //Recycler View Menu
        //Intent intent = new Intent(this, HomeScreen.class);
        //Grid View Menu
        Intent intent = new Intent(this, HomeScreenCategory.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.AttendanceSave:
                if (!CheckNetwork.isInternetAvailable(StudentAttendanceStudentList.this)) {
                    Toast.makeText(StudentAttendanceStudentList.this, getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }else {
                    // set title
                    alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Attendance Entry SAVE");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Do you Want to Save?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    hideKeyboard(StudentAttendanceStudentList.this);
                                    JSONObject obj = new JSONObject();
                                    JSONArray ArrayObj = new JSONArray();
                                    String jsonString = "";
                                    String strStudentAttendance = "";

                                    if (student_list.size() == 0) return;
                                    try {
                                        for (int i = 0; i < student_list.size(); i++) {
                                            String item = student_list.get(i);
                                            String[] strColumns = item.split("##");
                                            if (allBts.get(i).getText().toString().equals("\u2713 P")) {
                                                strStudentAttendance = "true";  //present - attendance status
                                            } else {
                                                strStudentAttendance = "false"; // absent - attendance status
                                            }
                                            obj = new JSONObject();
                                            obj.put("uniqueid", strColumns[0]);  //strColumns[0] = programwisesectionid_studentid
                                            obj.put("studentAttendance", strStudentAttendance);
                                            ArrayObj.put(obj);
                                        }
                                        if (ArrayObj.length() > 0) {
                                            jsonString = ArrayObj.toString();
                                        }
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                    if (Utility.isNotNull(jsonString)) {
                                        // strIds = subid "$$" progsectionid "$$" delegateempid "$$" attendancetransid "$$" dayorderid "$$" hourid
                                        StringTokenizer strIdsItems = new StringTokenizer(strIds, "$$");
                                        strIdsItems.nextToken();
                                        strIdsItems.nextToken();
                                        long lngDelegationId = Long.parseLong(strIdsItems.nextToken().trim());
                                        strIdsItems.nextToken();
                                        int intDayOrderId = Integer.parseInt(strIdsItems.nextToken().trim());
                                        int intHourId = Integer.parseInt(strIdsItems.nextToken().trim());

                                        strParameters = new String[]{"String", "returndata", jsonString,
                                                "Long", "subjectid", String.valueOf(lngSubId),
                                                "Long", "employeeid", String.valueOf(lngEmployeeId),
                                                "Long", "delegationid", String.valueOf(lngDelegationId),
                                                "String", "attendancedate", String.valueOf(strAttendanceDate),
                                                "int", "dayorderid", String.valueOf(intDayOrderId),
                                                "int", "hourid", String.valueOf(intHourId)};
                                        WebService.strParameters = strParameters;
                                        WebService.METHOD_NAME = "saveStudentAttendance";
                                        AsyncCallSaveWS task = new AsyncCallSaveWS();
                                        task.execute();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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
                break;
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
        ProgressDialog dialog = new ProgressDialog(StudentAttendanceStudentList.this);
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
                alertDialogBuilder = new AlertDialog.Builder(StudentAttendanceStudentList.this);
                alertDialogBuilder.setTitle("SAVE Message");
                // set dialog message
                alertDialogBuilder
                .setMessage(jsonObject.getString("Message"))
                .setCancelable(false)
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id){
                        student_list.clear();
                        cleanTable((TableLayout) findViewById(R.id.tblAttendanceStudentList));

                        Intent intent = new Intent(StudentAttendanceStudentList.this, HomeScreenCategory.class);
                        startActivity(intent);
                        StudentAttendanceStudentList.this.finish();
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

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(StudentAttendanceStudentList.this);

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
            if (dialog != null && dialog.isShowing()){
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
                            object.getString("registerNumber").trim();

                    student_list.add(object.getString("uniqueid")
                            + "##" +object.getString("studentName")
                            + "##" +object.getString("registerNumber").trim());
                    if (!strRow.equals(""))

                        addData(strRow,i);
                    else
                        break;
                }
                if (strRow.equals("")){
                    Toast.makeText(StudentAttendanceStudentList.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                Toast.makeText(StudentAttendanceStudentList.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    View.OnClickListener getOnClickDoSomething(final Button button, final TextView tv1, final TextView tv2){
        return new View.OnClickListener() {
            public void onClick(View v) {
            if (button.getText().toString().trim().equals("\u2713 P")){
                button.setText("\u2718 A");
                button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                tv1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
                tv2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed));
            }
            else{
                button.setText("\u2713 P");
                button.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGreen));
                tv1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                tv2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
            }
            }
        };
    }

    public void addData(String strRow, int bColor){
        TableLayout tl = findViewById(R.id.tblAttendanceStudentList);
        TableRow tr = new TableRow(StudentAttendanceStudentList.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(20, 0, 10, 0);
        params1.weight = 1;


        tr.setLayoutParams(params1);
        String[] strColumns = strRow.split("##");
       // tr.setBackgroundResource(R.color.colorWhite);

        if (bColor % 2 != 0){
            tr.setBackgroundResource(R.color.cardColorg);
        }else{
            tr.setBackgroundResource(R.color.colorWhite);

        }
        btAttenStatus = new Button(this);
        TextView tvRegNO = new TextView(StudentAttendanceStudentList.this);
        TextView tvStudentName = new TextView(StudentAttendanceStudentList.this);
        //registerno
        tvRegNO.setLayoutParams(params1);
        //tvRegNO.setWidth(400);
        tvRegNO.setText(strColumns[2].trim());
        tvRegNO.setTextColor(getResources().getColor(R.color.colorBlack));
        tvRegNO.setOnClickListener(getOnClickDoSomething(btAttenStatus, tvRegNO, tvStudentName));
        tvRegNO.setPadding(5, 15, 0, 15);
        tvRegNO.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        tvRegNO.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tvRegNO);
        //Student Name
        tvStudentName.setLayoutParams(params1);
        tvStudentName.setText(strColumns[1].trim()); // + "(" + strColumns[2].trim() + ")");
        tvStudentName.setTextColor(getResources().getColor(R.color.colorBlack));
        tvStudentName.setOnClickListener(getOnClickDoSomething(btAttenStatus, tvRegNO, tvStudentName));
        tvStudentName.setPadding(0, 15, 0, 15);
        tvStudentName.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        tvStudentName.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tvStudentName);

        btAttenStatus.setPadding(15, 15, 15, 15);
        btAttenStatus.setBackgroundColor(Color.parseColor("#ffffff"));
        btAttenStatus.setText("\u2713 P");
        btAttenStatus.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        btAttenStatus.setTextColor(getResources().getColor(R.color.colorGreen));
        if (bColor % 2 != 0){
            btAttenStatus.setBackgroundResource(R.color.cardColorg);
        }else{
            btAttenStatus.setBackgroundResource(R.color.colorWhite);

        }

        String[] strInnerColumns = strColumns[0].split("_");
        btAttenStatus.setTag("btStudentAttenStatus"+Integer.parseInt(strInnerColumns[1].toString()));
        btAttenStatus.setOnClickListener(getOnClickDoSomething(btAttenStatus, tvRegNO, tvStudentName));
        allBts.add(btAttenStatus);
        tr.addView(btAttenStatus);
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
}
