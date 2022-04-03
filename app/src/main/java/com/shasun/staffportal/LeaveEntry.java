package com.shasun.staffportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;

import webservice.WebService;

public class LeaveEntry extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView txtEditLeavePeriod, txtEditLeaveType, txtEditFromSession, txtEditToSession, txtFromDate, txtToDate, edRemarks;
    private static String strParameters[];
    private static String ResultString1 = "";
    private long lngEmpId = 0;
    private float availableDays = 0;
    private TextView hdnFromDate, hdnToDate, txtLeavePeriodId, txtLeaveTypeId, txtFromSessionId, txtToSessionId, txtApprovalOfficer, txtLeaveAvailability;
    private TextView txtLeaveAvailabilityHyperLink;
    private int intLeaveTypeId, intLeavePeriodId, intFromSessId, intToSessId;
    private String strMinCalendarDate, strMaxCalendarDate, strSetCalendarDate;
    private float flLeaveApplied;
    private static String ResultString = "";
    private TextView tvPageTitle;
    AlertDialog.Builder builder;
    private int intFlag = 0;
    private LinkedHashMap<String, String> leaveperiod_data = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> leavetype_data = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> FromSession_data = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> ToSession_data = new LinkedHashMap<String, String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaveentry);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Leave Entry");
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));

        txtApprovalOfficer = (TextView) findViewById(R.id.txtApprovalOfficer);
        txtLeaveAvailability = (TextView) findViewById(R.id.txtLeaveAvailability);
        txtLeaveTypeId = (TextView) findViewById(R.id.hdnLeaveTypeId);
        txtEditLeaveType = (AutoCompleteTextView) findViewById(R.id.txtLeaveType);

        intFlag = getIntent().getIntExtra("Flag", 1);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmpId = loginsession.getLong("userid", 1);
        Button butLeaveAvailability = (Button) findViewById(R.id.btn_LeaveAvailability);
        butLeaveAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(LeaveEntry.this, LeaveAvailability.class);
                startActivity(intent);
            }
        });

        Button butSave = (Button) findViewById(R.id.btn_SaveEntries);
        butSave.setOnClickListener(this);
        //Button butClear = (Button) findViewById(R.id.btn_Clear);
        //butClear.setOnClickListener(this);

        Button btnBack = (Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (intFlag == 1) {
                    intent = new Intent(LeaveEntry.this, HomeScreenCategory.class);
                    startActivity(intent);
                }
                if (intFlag == 2) {
                    intent = new Intent(LeaveEntry.this, HomePageGridViewLayout.class);
                    startActivity(intent);
                }
            }
        });
        FromSession_data.put("2", "Full Day");
        FromSession_data.put("3", "ForeNoon");
        FromSession_data.put("4", "AfterNoon");

        ToSession_data.put("2", "Full Day");
        ToSession_data.put("3", "ForeNoon");
        ToSession_data.put("4", "AfterNoon");

        txtLeavePeriodId = (TextView) findViewById(R.id.hdnLeavePeriodId);
        txtEditLeavePeriod = (AutoCompleteTextView) findViewById(R.id.txtLeavePeriod);
        txtEditLeavePeriod.setInputType(InputType.TYPE_NULL);
        txtEditLeavePeriod.setThreshold(5000);
        txtEditLeavePeriod.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub
                txtEditLeavePeriod.showDropDown();
                txtEditLeavePeriod.requestFocus();
                return false;
            }
        });

        txtEditLeavePeriod.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {

                    txtEditLeavePeriod.setText("");
                    txtLeavePeriodId.setText("");
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
                }
                if (!hasFocus) {
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

        txtEditLeavePeriod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                hideKeyboard(LeaveEntry.this);
                String id = (new ArrayList<String>(leaveperiod_data.keySet())).get(position).toString();
                String name = (new ArrayList<String>(leaveperiod_data.values())).get(position).toString();
                java.util.StringTokenizer st = new java.util.StringTokenizer(id, "##");
                txtEditLeavePeriod.setText(name);
                txtLeavePeriodId.setText(st.nextToken().trim());
                strSetCalendarDate = st.nextToken().trim();
                strMinCalendarDate = st.nextToken().trim();
                strMaxCalendarDate = st.nextToken().trim();
                //test by radha
                intLeavePeriodId = Integer.parseInt(txtLeavePeriodId.getText().toString());
                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmpId), "int", "leaveperiodid", String.valueOf(intLeavePeriodId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "listLeaveTypeJson";
                AsyncCallWS task2 = new AsyncCallWS();
                task2.execute();
            }
        });

        txtEditLeaveType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub

                txtEditLeaveType.showDropDown();
                txtEditLeaveType.requestFocus();
                return false;
            }
        });
        txtEditLeaveType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                String id = (new ArrayList<String>(leavetype_data.keySet())).get(position);
                String name = (new ArrayList<String>(leavetype_data.values())).get(position).toString();
                java.util.StringTokenizer st = new java.util.StringTokenizer(name, "[");
                txtEditLeaveType.setText(st.nextToken());
                txtLeaveAvailability.setText("[" + st.nextToken());
                java.util.StringTokenizer st1 = new java.util.StringTokenizer(name, "[");

                String lavail1 = st1.nextToken();
                String lavail = "["+st1.nextToken();
                String[] strColumns = lavail.split(" ");
                availableDays = Float.parseFloat(strColumns[3]);
                Log.i("TEST FLOAT : ", strColumns[3]);
                txtLeaveTypeId.setText(id);
            }
        });

        txtEditLeaveType.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if (hasFocus) {

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
                    if (txtLeavePeriodId.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(LeaveEntry.this, "Please Select Leave Period, Before selecting Leave Type", Toast.LENGTH_LONG).show();

                    } else {
                        intLeavePeriodId = Integer.parseInt(txtLeavePeriodId.getText().toString());
                        strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmpId), "int", "leaveperiodid", String.valueOf(intLeavePeriodId)};
                        WebService.strParameters = strParameters;
                        WebService.METHOD_NAME = "listLeaveTypeJson";
                        AsyncCallWS task2 = new AsyncCallWS();
                        task2.execute();
                    }
                }
                if (!hasFocus) {
                    String val = txtEditLeaveType.getText().toString();  // + "##" + txtLeaveTypeId.getText()
                    if (leavetype_data.containsValue(val)) {
                        //                if (Arrays.asList(leavetype_list).contains(val)){
                        //
                    } else {
                        //txtEditLeaveType.setError("Invalid Input");
                    }
                }
            }
        });
        txtFromDate = (AutoCompleteTextView) findViewById(R.id.FromDate);
        hdnFromDate = findViewById(R.id.hdnFromDate);
        final Calendar mMinDate = Calendar.getInstance();
        final Calendar mMaxDate = Calendar.getInstance();
        final Calendar mCurrentDate = Calendar.getInstance();
        txtFromDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(LeaveEntry.this);
                }
            }
        });

        txtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mYear = 0, mMonth = 0, mDay = 0;
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                java.util.StringTokenizer st = new java.util.StringTokenizer(strMinCalendarDate, "-");
                mYear = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.YEAR);
                mMonth = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.MONTH);
                mDay = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.DAY_OF_MONTH);
                mMinDate.set(Calendar.YEAR, mYear);
                mMinDate.set(Calendar.MONTH, mMonth - 1);
                mMinDate.set(Calendar.DAY_OF_MONTH, mDay);

                st = new java.util.StringTokenizer(strMaxCalendarDate, "-");
                mYear = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.YEAR);
                mMonth = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.MONTH);
                mDay = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.DAY_OF_MONTH);

                mMaxDate.set(Calendar.YEAR, mYear);
                mMaxDate.set(Calendar.MONTH, mMonth - 1);
                mMaxDate.set(Calendar.DAY_OF_MONTH, mDay);

                st = new java.util.StringTokenizer(strSetCalendarDate, "-");
                mYear = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.YEAR);
                mMonth = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.MONTH);
                mDay = Integer.parseInt(st.nextToken()); //mcurrentDate.get(Calendar.DAY_OF_MONTH);
                mCurrentDate.set(Calendar.YEAR, mYear);
                mCurrentDate.set(Calendar.MONTH, mMonth - 1);
                mCurrentDate.set(Calendar.DAY_OF_MONTH, mDay);

                DatePickerDialog mFromDatePicker = new DatePickerDialog(LeaveEntry.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time    */
                        String year1 = String.valueOf(selectedYear);
                        String month1 = String.valueOf(selectedMonth + 1);
                        String day1 = String.valueOf(selectedDay);
                        txtFromDate.setText(day1 + "/" + month1 + "/" + year1);
                        hdnFromDate.setText(year1 + "-" + month1 + "-" + day1);
                    }
                }, mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH), mCurrentDate.get(Calendar.DAY_OF_MONTH));
                mFromDatePicker.setTitle("Select From date");
                mFromDatePicker.getDatePicker().setMinDate(mMinDate.getTimeInMillis());
                mFromDatePicker.getDatePicker().setMaxDate(mMaxDate.getTimeInMillis());
                mFromDatePicker.show();
            }
        });
        txtToDate = (AutoCompleteTextView) findViewById(R.id.ToDate);
        hdnToDate = findViewById(R.id.hdnToDate);
        txtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            // TODO Auto-generated method stub
                DatePickerDialog mToDatePicker = new DatePickerDialog(LeaveEntry.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        // TODO Auto-generated method stub
                        /*      Your code   to get date and time    */
                        String year1 = String.valueOf(selectedYear);
                        String month1 = String.valueOf(selectedMonth + 1);
                        String day1 = String.valueOf(selectedDay);
                        txtToDate.setText(day1 + "/" + month1 + "/" + year1);
                        hdnToDate.setText(year1 + "-" + month1 + "-" + day1);
                    }
                }, mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH), mCurrentDate.get(Calendar.DAY_OF_MONTH));
                mToDatePicker.setTitle("Select From date");
                mToDatePicker.getDatePicker().setMinDate(mMinDate.getTimeInMillis());
                mToDatePicker.getDatePicker().setMaxDate(mMaxDate.getTimeInMillis());
                mToDatePicker.show();
            }
        });
        txtEditFromSession = (AutoCompleteTextView) findViewById(R.id.txtFromSession);
        txtFromSessionId = (TextView) findViewById(R.id.hdnFromSessionId);
        txtEditLeaveType = (AutoCompleteTextView) findViewById(R.id.txtLeaveType);

        Collection<String> FromSessioncollection = FromSession_data.values();
        String[] FromSessionArray = FromSessioncollection.toArray(new String[FromSessioncollection.size()]);
        ArrayAdapter<String> FSA = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, FromSessionArray);
        txtEditFromSession.setThreshold(1000);
        txtEditFromSession.setAdapter(FSA);

        txtEditToSession = (AutoCompleteTextView) findViewById(R.id.txtToSession);
        txtToSessionId = (TextView) findViewById(R.id.hdnToSessionId);
        Collection<String> ToSessioncollection = ToSession_data.values();
        String[] ToSessionArray = ToSessioncollection.toArray(new String[ToSessioncollection.size()]);
        ArrayAdapter<String> TSA = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, ToSessionArray);
        txtEditToSession.setThreshold(1000);
        txtEditToSession.setAdapter(TSA);

//        ArrayAdapter<String> FSL = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, FromSessionList);
//        ArrayAdapter<String> TSL = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, FromSessionList);
//        txtEditFromSession.setThreshold(1);
//        txtEditFromSession.setAdapter(FSL);
//        txtEditToSession.setThreshold(1);
//        txtEditToSession.setAdapter(TSL);

        txtEditFromSession.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub
                txtEditFromSession.showDropDown();
                txtEditFromSession.requestFocus();
                return false;
            }
        });
        txtEditFromSession.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String val = txtEditFromSession.getText().toString();  // + "##" + txtFromSessionId.getText();
                    if (FromSession_data.containsValue(val)) {
                        //if (Arrays.asList(FromSessionList).contains(val)){

                    } else {
                        //txtEditFromSession.setError("Invalid Input");
                    }
                }
            }
        });
        txtEditFromSession.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                String id = (new ArrayList<String>(FromSession_data.keySet())).get(position);
                String name = (new ArrayList<String>(FromSession_data.values())).get(position).toString();
                txtEditFromSession.setText(name);
                txtFromSessionId.setText(id);
                //           Toast.makeText(LeaveEntry.this, "Selected Item:" + name + "," + id, Toast.LENGTH_LONG).show();
            }
        });

        txtEditToSession.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
                // TODO Auto-generated method stub
                txtEditToSession.showDropDown();
                txtEditToSession.requestFocus();
                return false;
            }
        });
        txtEditToSession.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String val = txtEditToSession.getText().toString();  // + "##" + txtToSessionId.getText();
                    if (ToSession_data.containsValue(val)) {
                        //if (Arrays.asList(ToSessionList).contains(val)){

                    } else {
                        //txtEditToSession.setError("Invalid Input");
                    }
                }
            }
        });
        txtEditToSession.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                String id = (new ArrayList<String>(ToSession_data.keySet())).get(position);
                String name = (new ArrayList<String>(ToSession_data.values())).get(position).toString();
                txtEditToSession.setText(name);
                txtToSessionId.setText(id);
                //           Toast.makeText(LeaveEntry.this, "Selected Item:" +  name + "," + id, Toast.LENGTH_LONG).show();
            }
        });
        strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmpId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "listLeavePeriodJson";
        AsyncCallWS task2 = new AsyncCallWS();
        task2.execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1) {
            intent = new Intent(LeaveEntry.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2) {
            intent = new Intent(LeaveEntry.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private void displayLeavePeriod() {
        if (leaveperiod_data.size() == 0) {
            Toast.makeText(LeaveEntry.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
        } else {
            txtEditLeavePeriod = (AutoCompleteTextView) findViewById(R.id.txtLeavePeriod);
            Collection<String> LeavePeriodcollection = leaveperiod_data.values();
            String[] arrayLeavePeriod = LeavePeriodcollection.toArray(new String[LeavePeriodcollection.size()]);
            ArrayAdapter<String> LPA = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayLeavePeriod);
            txtEditLeavePeriod.setAdapter(LPA);
            txtEditLeavePeriod.showDropDown();
            txtEditLeavePeriod.requestFocus();
        }
    }

    public void perform_action(View v) {

    }

    private void displayLeaveType() {
        if (leavetype_data.size() == 0) {
            Toast.makeText(LeaveEntry.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
        } else {
            txtEditLeaveType = (AutoCompleteTextView) findViewById(R.id.txtLeaveType);
            Collection<String> LeaveTypecollection = leavetype_data.values();
            String[] arrayLeaveType = LeaveTypecollection.toArray(new String[LeaveTypecollection.size()]);
            //System.out.println(arrayLeaveType);
            ArrayAdapter<String> LTA = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayLeaveType);
            txtEditLeaveType.setAdapter(LTA);
            txtEditLeaveType.setThreshold(2000);
            txtEditLeaveType.showDropDown();
            txtEditLeaveType.requestFocus();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.

        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onClick(View v) {
        hideKeyboard(LeaveEntry.this);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        txtLeavePeriodId = (TextView) findViewById(R.id.hdnLeavePeriodId);
        txtLeaveTypeId = (TextView) findViewById(R.id.hdnLeaveTypeId);
        txtFromSessionId = (TextView) findViewById(R.id.hdnFromSessionId);
        txtToSessionId = (TextView) findViewById(R.id.hdnToSessionId);
        edRemarks = (AutoCompleteTextView) findViewById(R.id.edRemarks);
        edRemarks.getText().toString().trim();

        switch (v.getId()) {
            case R.id.btn_SaveEntries:
                if (!CheckNetwork.isInternetAvailable(LeaveEntry.this)) {
                    Toast.makeText(LeaveEntry.this,v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }else {

                    if (!Utility.isNotNull(txtLeavePeriodId.getText().toString().trim())) {
                        txtEditLeavePeriod.setError("Leave Period required");
                        txtEditLeavePeriod.requestFocus();
                        return;
                    }else if (!Utility.isNotNull(txtLeaveTypeId.getText().toString().trim())) {
                        txtEditLeaveType.setError("Leave Type required");
                        txtEditLeaveType.requestFocus();
                        return;
                    }else if (!Utility.isNotNull(txtFromDate.getText().toString().trim())) {
                        txtFromDate.setError("From Date required");
                        return;
                    }else if (!Utility.isNotNull(txtFromSessionId.getText().toString().trim())) {
                        txtEditFromSession.setError("From Session required");
                        txtEditFromSession.requestFocus();
                        return;
                    }else if (!Utility.isNotNull(txtToDate.getText().toString().trim())) {
                        txtToDate.setError("To Date required");
                        return;
                    }else if (!Utility.isNotNull(txtToSessionId.getText().toString().trim())) {
                        txtEditToSession.setError("To Session required");
                        txtEditToSession.requestFocus();
                        return;
                    }else if (!Utility.isNotNull(edRemarks.getText().toString().trim())) {
                        edRemarks.setError("Reason is required!");
                        edRemarks.requestFocus();
                        return;
                    }
                    intLeavePeriodId = Integer.parseInt(txtLeavePeriodId.getText().toString());
                    intLeaveTypeId = Integer.parseInt(txtLeaveTypeId.getText().toString());
                    intFromSessId = Integer.parseInt(txtFromSessionId.getText().toString());
                    intToSessId = Integer.parseInt(txtToSessionId.getText().toString());

                    String d1 = hdnFromDate.getText().toString();
                    String d2 = hdnToDate.getText().toString();
                    try {
                        Date dtFromdate = sdf.parse(d1);
                        Date dtTodate = sdf.parse(d2);
                        long diff =  dtTodate.getTime() - dtFromdate.getTime();
                        float numOfDays = (float) (diff / (1000 * 60 * 60 * 24));
                        if(numOfDays > availableDays){
                            showDialog("Number of Available days for this Leave type is "+availableDays+", Please select days with in the limit ");
                            txtToDate.setError("Number of Available days for this Leave type is "+availableDays+" Please select days with in the limit ");
                            return;
                        }else  if (dtFromdate.compareTo(dtTodate) > 0) {
                            //System.out.println("Please ensure that the To Date is greater than or equal to the From Date.");
                            showDialog("'To Date' should be greater than 'From Date'.");
                            txtToDate.setError("'To Date' should be greater than 'From Date'.");
                            return;
                        } else if (dtFromdate.compareTo(dtTodate) < 0) { // Multiple days leave
                            if (intFromSessId == 3) { // sessionId= "2","Full Day";       "3","ForeNoon";      "4","AfterNoon"
                                //Morning session not allowed (instead use fullday)
                                showDialog("Instead of ForeNoon, use Full Day");
                                txtEditFromSession.setError("Instead of ForeNoon, use Full Day");
                                return;
                            }
                            if (intToSessId == 4) {
                                //Evening session not allowed (instead use fullday)
                                showDialog("Instead of AfterNoon, use Full Day");

                                txtEditFromSession.setError("Instead of AfterNoon, use Full Day");
                                return;
                            }
                            // System.out.println("Date1 is before Date2");
                        } else if (dtFromdate.compareTo(dtTodate) == 0) { // single day leave
                            if (intFromSessId != intToSessId) {
                                showDialog("Same session expected for one day leave");
                                txtEditToSession.setError("Same session expected for one day leave");
                                return;
                            }
                            //System.out.println("Date1 is equal to Date2");
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
                            "int", "fromsession", String.valueOf(intFromSessId),
                            "int", "tosession", String.valueOf(intToSessId),
                            "String", "reason", edRemarks.getText().toString().trim(),
                            "float", "leaveapplieddays", "0"};

                    WebService.strParameters = strParameters;
                    WebService.METHOD_NAME = "saveEmployeeLeaveDetailsJson";
                    AsyncCallSaveWS task = new AsyncCallSaveWS();
                    task.execute();
                }
                break;
//            case R.id.btn_Clear:
//                clearForm((ViewGroup) findViewById(R.id.leaveentrylayout));
//                break;
        }
    }

    public static boolean isDateAfter(String startDate, String endDate) {
        try {
            String myFormatString = "yyyy-M-dd"; // for example
            SimpleDateFormat df = new SimpleDateFormat(myFormatString);
            Date date1 = df.parse(endDate);
            Date startingDate = df.parse(startDate);
            if (date1.after(startingDate))
                return true;
            else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(LeaveEntry.this);

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

//            Integer result = null;
//            try{
//                ExecutorService executor = Executors.newFixedThreadPool(2);
//                Runnable thread1 = new Runnable() {
//                    @Override
//                    public void run() {
//                    try {
//                        strParameters = new String[] { "Long","employeeid", String.valueOf(lngEmpId)};
//                        WebService.strParameters = strParameters;
//                        WebService.METHOD_NAME = "listLeavePeriodJson";
//                        ResultString1 = WebService.invokeWS();
//                    } catch (Exception ex) {
//                        throw new RuntimeException(ex);
//                    }
//                    }
//                };
//                executor.submit(thread1);
//                Runnable thread2 = new Runnable() {
//                    @Override
//                    public void run() {
//                    try {
//                        strParameters = new String[] { "Long","employeeid", String.valueOf(lngEmpId)};
//                        WebService.strParameters = strParameters;
//                        WebService.METHOD_NAME = "listLeaveTypeJson";
//                        ResultString2 = WebService.invokeWS();
//                        //result = method2(_phoneno,_ticket);
//                    } catch (Exception ex) {
//                        throw new RuntimeException(ex);
//                    }
//                    }
//                };
//                executor.submit(thread2);
//                executor.shutdown();
            //Timeout for both tasks = 120 seconds
//                if (!executor.awaitTermination(120, TimeUnit.SECONDS)){
//                    throw new RuntimeException("Could not complete requests in the given time!");
//                }
//            }catch(Exception ex){}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Log.i(TAG, "onPostExecute");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            int intPendngLeave = 0;
            try {
                if (intPendngLeave == 0) {
                    Log.e("Test Leave Entry:", ResultString1.toString());
                    JSONArray temp = new JSONArray(ResultString1.toString());
                    for (int i = 0; i <= temp.length() - 1; i++) {
                        final JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                        intPendngLeave = Integer.parseInt(object.getString("pendingleave"));
                        if (intPendngLeave > 0) {
                            builder = new AlertDialog.Builder(LeaveEntry.this);
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
                            String strLeavePeriodID = object.getString("leaveperiodid") + "##" + object.getString("setcalenderdate")
                                    + "##" + object.getString("mincalenderdate") + "##" + object.getString("maxcalenderdate");
                            leaveperiod_data.put(strLeavePeriodID, object.getString("leaveperioddesc"));
                            txtApprovalOfficer.setText("To approve by: " + object.getString("reportingofficer"));
                            String strReportingOfficer = object.getString("reportingofficer");
                            if (strReportingOfficer.trim().length() == 0) {
                                builder = new AlertDialog.Builder(LeaveEntry.this);
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
                            } else {
                                displayLeavePeriod();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            try {
                intLeavePeriodId = Integer.parseInt(txtLeavePeriodId.getText().toString());
                if (intLeavePeriodId > 0) {
                    JSONArray temp = new JSONArray(ResultString1.toString());
                    leavetype_data.clear();
                    for (int i = 0; i <= temp.length() - 1; i++) {
                        JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                        String strLeaveType = object.getString("leavetype") + " [" + object.getString("leaveavailability") + " ]";
                        leavetype_data.put(object.getString("leavetypeid"), strLeaveType);
                        //leavetype_list.add(object.getString("leavetype") + "##" + object.getString("leavetypeid"));  //+ "  " + object.getString("fromdate") + "  " + object.getString("todate")
                    }
                    displayLeaveType();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void clearForm(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText) view).setText("");
            }
            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                clearForm((ViewGroup) view);
        }
    }

    private class AsyncCallSaveWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(LeaveEntry.this);

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
            try {
                Toast.makeText(LeaveEntry.this, ResultString.toString(), Toast.LENGTH_LONG).show();
                clearForm((ViewGroup) findViewById(R.id.leaveentrylayout));
                Intent intent = new Intent(LeaveEntry.this, LeaveStatus.class);
                startActivity(intent);
                finish();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    AlertDialog alert;
    public void showDialog(String msg){
        builder = new AlertDialog.Builder(LeaveEntry.this);
        //Setting message manually and performing action on button click
        builder.setMessage(msg).setTitle(R.string.leavealertdialogtitle)
                //builder.setMessage("Do you want to close this application ?")
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                    }
                });
        //Creating dialog box
        //Setting the title manually
         alert = builder.create();

        alert.setTitle(R.string.leavealertdialogtitle);
        alert.show();

    }
}
