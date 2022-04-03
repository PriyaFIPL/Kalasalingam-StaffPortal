package com.shasun.staffportal;
    import android.annotation.SuppressLint;
    import android.app.Activity;
    import android.app.AlertDialog;
    import android.app.DatePickerDialog;
    import android.app.ProgressDialog;
    import android.app.TimePickerDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.content.res.Configuration;
    import android.graphics.Bitmap;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Message;
    import android.text.InputType;
    import android.util.Log;
    import android.view.MotionEvent;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.inputmethod.InputMethodManager;
    import android.webkit.WebChromeClient;
    import android.webkit.WebSettings;
    import android.webkit.WebView;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.AutoCompleteTextView;
    import android.widget.Button;
    import android.widget.DatePicker;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.TimePicker;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.content.ContextCompat;

    import org.json.JSONArray;
    import org.json.JSONObject;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;
    import java.util.Collection;
    import java.util.Date;
    import java.util.LinkedHashMap;

    import im.delight.android.webview.AdvancedWebView;
    import webservice.SqlliteController;
    import webservice.WebService;

public class PermissionEntry extends AppCompatActivity implements View.OnClickListener{

    private AutoCompleteTextView txtEditLeavePeriod, txtEditLeaveType, txtEditFromSession, txtEditToSession, txtFromDate, edRemarks;

    private static String strParameters[];
    private static String ResultString1 = "";
    private long lngEmpId=0;
    private TextView  hdnFromDate,  txtLeavePeriodId, txtLeaveTypeId,  txtApprovalOfficer;
    //private TextView txtLeaveAvailabilityHyperLink;
    private int intLeaveTypeId, intLeavePeriodId;
    private String strMinCalendarDate,strMaxCalendarDate, strSetCalendarDate;
    private float flLeaveApplied;
    private static String ResultString = "";
    private TextView tvPageTitle;
    AlertDialog.Builder builder;
    private int intFlag = 0;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private LinkedHashMap<String, String> leaveperiod_data=new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> leavetype_data=new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> FromSession_data = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> ToSession_data = new LinkedHashMap<String, String>();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissionentry);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Permission Entry");
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));

        txtApprovalOfficer = (TextView) findViewById(R.id.txtApprovalOfficer);
        intFlag=getIntent().getIntExtra("Flag",1);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmpId = loginsession.getLong("userid", 1);

        Button butSave = (Button) findViewById(R.id.btn_SaveEntries);
        butSave.setOnClickListener(this);
        //Button butClear = (Button) findViewById(R.id.btn_Clear);
        //butClear.setOnClickListener(this);

        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Intent intent;
                if (intFlag == 1){
                    intent = new Intent(PermissionEntry.this, HomeScreenCategory.class);
                    startActivity(intent);
                }
                if (intFlag == 2){
                    intent = new Intent(PermissionEntry.this, HomePageGridViewLayout.class);
                    startActivity(intent);
                }
            }
        });
        FromSession_data.put("2","Full Day");
        FromSession_data.put("3","ForeNoon");
        FromSession_data.put("4","AfterNoon");

        ToSession_data.put("2","Full Day");
        ToSession_data.put("3","ForeNoon");
        ToSession_data.put("4","AfterNoon");

        txtLeavePeriodId = (TextView) findViewById(R.id.hdnLeavePeriodId);
        txtEditLeavePeriod = (AutoCompleteTextView) findViewById(R.id.txtLeavePeriod);
        txtEditLeavePeriod.setInputType(InputType.TYPE_NULL);
        txtEditLeavePeriod.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub
                txtEditLeavePeriod.showDropDown();
                txtEditLeavePeriod.requestFocus();                return false;
            }
        });

        txtEditLeavePeriod.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus){
                if (hasFocus){

                    txtEditLeavePeriod.setText("");
                    txtLeavePeriodId.setText("");
                    txtEditLeaveType.setText("");
                    txtLeaveTypeId.setText("");

                    txtFromDate.setText("");
                    hdnFromDate.setText("");

                    txtEditFromSession.setText("");
                    txtEditToSession.setText("");

                }
                if (!hasFocus){
//                    String val = txtEditLeavePeriod.getText().toString(); // + "##" + txtLeavePeriodId.getText();
//                    if (leaveperiod_data.containsValue(val)){
//                        //if (Arrays.asList(leaveperiod_list).contains(val)){
//                        txtEditLeavePeriod.setError("");
//
//                    } else {
//                       // txtEditLeavePeriod.setError("Invalid Input");
//                    }
                }
            }
        });

        /*
        txtEditLeavePeriod.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3){
                hideKeyboard(PermissionEntry.this);
                String id = (new ArrayList<String>(leaveperiod_data.keySet())).get(position).toString();
                String name = (new ArrayList<String>(leaveperiod_data.values())).get(position).toString();
                java.util.StringTokenizer st = new java.util.StringTokenizer(id,"##");
                txtEditLeavePeriod.setText(name);
                txtLeavePeriodId.setText(st.nextToken().trim());
                strSetCalendarDate = st.nextToken().trim();
                strMinCalendarDate = st.nextToken().trim();
                strMaxCalendarDate = st.nextToken().trim();
            }
        });
        */

        txtLeaveTypeId = (TextView) findViewById(R.id.hdnLeaveTypeId);
        txtEditLeaveType = (AutoCompleteTextView) findViewById(R.id.txtLeaveType);
        txtEditLeaveType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub
                txtEditLeaveType.showDropDown();
                txtEditLeaveType.requestFocus();
                return false;
            }
        });
        txtEditLeaveType.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3){
                String id = (new ArrayList<String>(leavetype_data.keySet())).get(position);
                String name = (new ArrayList<String>(leavetype_data.values())).get(position).toString();
                java.util.StringTokenizer st = new java.util.StringTokenizer(name,"[");
                txtEditLeaveType.setText(st.nextToken());

                txtLeaveTypeId.setText(id);
            }
        });
/*
        txtEditLeaveType.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    txtEditLeaveType.setText("");
                    txtLeaveTypeId.setText("");
                    txtLeaveAvailability.setText("");
                    txtFromDate.setText("");
                    hdnFromDate.setText("");
                    txtToDate.setText("");
                    hdnToDate.setText("");

                    txtEditFromSession.setText("");
                    txtFromSessionId.setText("");
                    txtEditToSession.setText("");
                    txtToSessionId.setText("");
                    if(txtLeavePeriodId.getText().toString().equalsIgnoreCase("")){
                        Toast.makeText(PermissionEntry.this, "Please Select Leave Period, Before selecting Leave Type", Toast.LENGTH_LONG).show();

                    }else {
                        intLeavePeriodId = Integer.parseInt(txtLeavePeriodId.getText().toString());
                        strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmpId), "int", "leaveperiodid", String.valueOf(intLeavePeriodId)};
                        WebService.strParameters = strParameters;
                        WebService.METHOD_NAME = "listLeaveTypeforPermissionJson";
                        AsyncCallWS task2 = new AsyncCallWS();
                        task2.execute();
                    }
                }
                if (!hasFocus){
                    String val = txtEditLeaveType.getText().toString();  // + "##" + txtLeaveTypeId.getText()
                    if (leavetype_data.containsValue(val)){
                        //                if (Arrays.asList(leavetype_list).contains(val)){
                        //
                    } else {
                        //txtEditLeaveType.setError("Invalid Input");
                    }
                }
            }
        });

 */
        txtFromDate = (AutoCompleteTextView) findViewById(R.id.FromDate);
        hdnFromDate = findViewById(R.id.hdnFromDate);
        final Calendar mMinDate = Calendar.getInstance();
        final Calendar mMaxDate = Calendar.getInstance();
        final Calendar mCurrentDate = Calendar.getInstance();
        txtFromDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus){
                if (hasFocus) {
                    hideKeyboard(PermissionEntry.this);
                }
            }
        });

        txtFromDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int mYear=0,  mMonth=0, mDay=0;
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                java.util.StringTokenizer st = new java.util.StringTokenizer(strMinCalendarDate, "-");
                mYear = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.YEAR);
                mMonth = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.MONTH);
                mDay = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.DAY_OF_MONTH);
                mMinDate.set(Calendar.YEAR, mYear);
                mMinDate.set(Calendar.MONTH, mMonth-1);
                mMinDate.set(Calendar.DAY_OF_MONTH, mDay);

                st = new java.util.StringTokenizer(strMaxCalendarDate, "-");
                mYear = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.YEAR);
                mMonth = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.MONTH);
                mDay = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.DAY_OF_MONTH);

                mMaxDate.set(Calendar.YEAR, mYear);
                mMaxDate.set(Calendar.MONTH, mMonth-1);
                mMaxDate.set(Calendar.DAY_OF_MONTH, mDay);

                st = new java.util.StringTokenizer(strSetCalendarDate, "-");
                mYear = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.YEAR);
                mMonth = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.MONTH);
                mDay = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.DAY_OF_MONTH);
                mCurrentDate.set(Calendar.YEAR, mYear);
                mCurrentDate.set(Calendar.MONTH, mMonth-1);
                mCurrentDate.set(Calendar.DAY_OF_MONTH, mDay);



                DatePickerDialog mFromDatePicker=new DatePickerDialog(PermissionEntry.this, new DatePickerDialog.OnDateSetListener(){
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay){
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time    */
                        String year1 = String.valueOf(selectedYear);
                        String month1 = String.valueOf(selectedMonth + 1);
                        String day1 = String.valueOf(selectedDay);
                        txtFromDate.setText(day1 + "/" + month1 + "/" + year1);
                        hdnFromDate.setText(year1 + "-" + month1 + "-" + day1);
                    }
                } , mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH), mCurrentDate.get(Calendar.DAY_OF_MONTH));
                mFromDatePicker.setTitle("Select From date");
                mFromDatePicker.getDatePicker().setMinDate(mMinDate.getTimeInMillis());
                mFromDatePicker.getDatePicker().setMaxDate(mMaxDate.getTimeInMillis());
                mFromDatePicker.show();
            }
        });
        txtEditFromSession = (AutoCompleteTextView) findViewById(R.id.txtFromSession);
        txtEditLeaveType = (AutoCompleteTextView) findViewById(R.id.txtLeaveType);
        txtEditToSession = (AutoCompleteTextView) findViewById(R.id.txtToSession);

        txtEditFromSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(PermissionEntry.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtEditFromSession.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();

            }
        });

        txtEditToSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(PermissionEntry.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtEditToSession.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();

            }
        });

        strParameters = new String[] { "Long","employeeid", String.valueOf(lngEmpId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "listLeavePeriodandTypeforPermissionJson";
        PermissionEntry.AsyncCallWS task2 = new PermissionEntry.AsyncCallWS();
        task2.execute();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(PermissionEntry.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(PermissionEntry.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private void displayLeavePeriod(){
        /*
        if (leaveperiod_data.size() == 0) {
            Toast.makeText(PermissionEntry.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
        } else {
            txtEditLeavePeriod = (AutoCompleteTextView) findViewById(R.id.txtLeavePeriod);
            Collection<String> LeavePeriodcollection = leaveperiod_data.values();
            String[] arrayLeavePeriod = LeavePeriodcollection.toArray(new String[LeavePeriodcollection.size()]);
            ArrayAdapter<String> LPA = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayLeavePeriod);
            txtEditLeavePeriod.setAdapter(LPA);
            txtEditLeavePeriod.showDropDown();
            txtEditLeavePeriod.requestFocus();
        }

         */
    }

    public void perform_action(View v){

    }

    private void displayLeaveType() {
        if (leavetype_data.size() == 0){
            Toast.makeText(PermissionEntry.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
        } else {
            txtEditLeaveType = (AutoCompleteTextView) findViewById(R.id.txtLeaveType);
            Collection<String> LeaveTypecollection=leavetype_data.values();
            String[] arrayLeaveType = LeaveTypecollection.toArray(new String[LeaveTypecollection.size()]);
            System.out.println(arrayLeaveType);
            ArrayAdapter<String> LTA = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayLeaveType);
            txtEditLeaveType.setAdapter(LTA);
            txtEditLeaveType.setThreshold(2000);
            txtEditLeaveType.showDropDown();

            //txtEditLeaveType.requestFocus();
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

    public void onClick(View v){
        hideKeyboard(PermissionEntry.this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        txtLeavePeriodId = (TextView) findViewById(R.id.hdnLeavePeriodId);
        txtLeaveTypeId = (TextView) findViewById(R.id.hdnLeaveTypeId);
        edRemarks = (AutoCompleteTextView) findViewById(R.id.edRemarks);
        edRemarks.getText().toString().trim();

        switch (v.getId()){
            case R.id.btn_SaveEntries:
                if (!CheckNetwork.isInternetAvailable(PermissionEntry.this)) {
                    Toast.makeText(PermissionEntry.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }else {

                    if (!Utility.isNotNull(txtLeavePeriodId.getText().toString().trim())) {
                        txtEditLeavePeriod.setError("Leave Period required");
                        txtEditLeavePeriod.requestFocus();
                        return;
                    } else {
                        txtEditLeavePeriod.setError(null);
                    }
                    if (!Utility.isNotNull(txtLeaveTypeId.getText().toString().trim())) {
                        txtEditLeaveType.setError("Leave Type required");
                        txtEditLeaveType.requestFocus();
                        return;
                    }
                    if (!Utility.isNotNull(txtFromDate.getText().toString().trim())) {
                        txtFromDate.setError("From Date required");
                        return;
                    }
                    if (!Utility.isNotNull(txtEditFromSession.getText().toString().trim())) {
                        txtEditFromSession.setError("From Time required");
                        txtEditFromSession.requestFocus();
                        return;
                    }
                    if (!Utility.isNotNull(txtEditToSession.getText().toString().trim())) {
                        txtEditToSession.setError("To Time required");
                        txtEditToSession.requestFocus();
                        return;
                    }
                    if (!Utility.isNotNull(edRemarks.getText().toString().trim())) {
                        edRemarks.setError("Reason is required!");
                        return;
                    }
                    intLeavePeriodId = Integer.parseInt(txtLeavePeriodId.getText().toString());
                    intLeaveTypeId = Integer.parseInt(txtLeaveTypeId.getText().toString());

                    String d1 = hdnFromDate.getText().toString() + " " + txtEditFromSession.getText().toString();
                    String d2 = hdnFromDate.getText().toString() + " " + txtEditToSession.getText().toString();
                    try {
                        Date dtFromdate = sdf.parse(d1);
                        Date dtTodate = sdf.parse(d2);
                        if (dtFromdate.compareTo(dtTodate) > 0) {
                            //System.out.println("Please ensure that the To Date is greater than or equal to the From Date.");
                            txtEditToSession.setError("'To Date Time' should be greater than 'From Date Time'.");
                            return;
                        }

//                    if(!isDateAfter(d1,d2)){
//                        txtToDate.setError("Please ensure that the End Date is greater than or equal to the Start Date.");
//                        break;
//                    }
                    } catch (Exception e) {
                    }
                    strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmpId),
                            "int", "leaveperiodid", String.valueOf(intLeavePeriodId),
                            "int", "leavetypeid", String.valueOf(intLeaveTypeId),
                            "String", "fromdate", String.valueOf(d1),
                            "String", "todate", String.valueOf(d2),
                            "String", "reason", edRemarks.getText().toString().trim(),
                            "float", "leaveapplieddays", "0"};

                    WebService.strParameters = strParameters;
                    WebService.METHOD_NAME = "saveEmployeePermissionDetailsJson";
                    PermissionEntry.AsyncCallSaveWS task = new PermissionEntry.AsyncCallSaveWS();
                    task.execute();
                }
                break;
//            case R.id.btn_Clear:
//                clearForm((ViewGroup) findViewById(R.id.PermissionEntrylayout));
//                break;
        }
    }

    public static boolean isDateAfter(String startDate,String endDate){
        try{
            String myFormatString = "yyyy-M-dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);
            if (date1.after(startingDate))
                return true;
            else
                return false;
        }
        catch (Exception e){
            return false;
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(PermissionEntry.this);

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
            ResultString1 = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            //Log.i(TAG, "onPostExecute");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            if (ResultString1.toString().equals("")){
                Toast.makeText(PermissionEntry.this, "No Data Found", Toast.LENGTH_LONG).show();

            }else  {
                try {
                    JSONObject object1 = new JSONObject(ResultString1.toString());
                    if(object1.getString("Status").equalsIgnoreCase("Success")){
                        int intPendngLeave=0;
                        if (intPendngLeave == 0) {
                            JSONArray temp = new JSONArray(object1.getString("Data"));
                            if(temp.length() >0){
                                final JSONObject object = new JSONObject(temp.getJSONObject(0).toString());
                                intPendngLeave = Integer.parseInt(object.getString("pendingleave"));
                                intLeavePeriodId = Integer.parseInt(object.getString("leaveperiodid"));
                                if (intPendngLeave > 0){
                                    builder = new AlertDialog.Builder(PermissionEntry.this);
                                    //Setting message manually and performing action on button click
                                    builder.setMessage(R.string.leavealertdialogmsg).setTitle(R.string.leavealertdialogtitle)
                                            //builder.setMessage("Do you want to close this application ?")
                                            .setCancelable(false)
                                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    finish();
                                                }
                                            });
                                    //Creating dialog box
                                    AlertDialog alert = builder.create();
                                    //Setting the title manually
                                    alert.setTitle(R.string.leavealertdialogtitle);
                                    alert.show();
                                    //leaveperiod_list.add(object.getString("leaveperioddesc") + "##" + object.getString("leaveperiodid"));  //+ "  " + object.getString("fromdate") + "  " + object.getString("todate")
                                } else {
                                    String strLeavePeriodID=object.getString("leaveperiodid") + "##" + object.getString("setcalenderdate")
                                            + "##" + object.getString("mincalenderdate") + "##" + object.getString("maxcalenderdate");
                                    leaveperiod_data.put( strLeavePeriodID,object.getString("leaveperioddesc"));
                                    String id = (new ArrayList<String>(leaveperiod_data.keySet())).get(0).toString();
                                    String name = (new ArrayList<String>(leaveperiod_data.values())).get(0).toString();
                                   // Log.i("Name : " , name + " : "+ id);
                                    java.util.StringTokenizer st = new java.util.StringTokenizer(id,"##");
                                    txtEditLeavePeriod.setText(name);
                                    txtLeavePeriodId.setText(st.nextToken().trim());
                                    strSetCalendarDate = st.nextToken().trim();
                                    strMinCalendarDate = st.nextToken().trim();
                                    strMaxCalendarDate = st.nextToken().trim();


                                    txtApprovalOfficer.setText("To approve by: "+object.getString("reportingofficer"));
                                    String strReportingOfficer=object.getString("reportingofficer");
                                    if (strReportingOfficer.trim().length() == 0){
                                        builder = new AlertDialog.Builder(PermissionEntry.this);
                                        builder.setMessage(R.string.approvalofficerdialogmsg).setTitle(R.string.leavealertdialogtitle)
                                                .setCancelable(false)
                                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        finish();
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.setTitle(R.string.leavealertdialogtitle);
                                        alert.show();
                                    }else{
                                        displayLeavePeriod();
                                    }
                                }
                            }

                        }

                        if (intLeavePeriodId > 0) {
                            JSONArray temp = new JSONArray(object1.getString("Data"));
                            leavetype_data.clear();
                            for (int i = 1; i <= temp.length() - 1; i++){

                                JSONObject object = new JSONObject(temp.getJSONObject(i).toString());

                                String strLeaveType=object.getString("leavetype") ;
                                leavetype_data.put(object.getString("leavetypeid"), strLeaveType);
                                //leavetype_list.add(object.getString("leavetype") + "##" + object.getString("leavetypeid"));  //+ "  " + object.getString("fromdate") + "  " + object.getString("todate")
                            }
                            displayLeaveType();
                        }
                    }else{
                        Toast.makeText(PermissionEntry.this, object1.getString("Data"), Toast.LENGTH_LONG).show();

                    }

                    // clearForm((ViewGroup) findViewById(R.id.leaveentrylayout));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }



        }
    }

    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText){
                ((EditText)view).setText("");
            }
            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }

    private class AsyncCallSaveWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(PermissionEntry.this);
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
                Toast.makeText(PermissionEntry.this, ResultString.toString(), Toast.LENGTH_LONG).show();
                clearForm((ViewGroup) findViewById(R.id.leaveentrylayout));
                Intent intent = new Intent(PermissionEntry.this, LeaveStatus.class);
                startActivity(intent);
                finish();


            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
