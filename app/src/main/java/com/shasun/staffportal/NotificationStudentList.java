package com.shasun.staffportal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import webservice.WebService;

public class NotificationStudentList extends AppCompatActivity implements View.OnClickListener {
    private TextView tvPageTitle;
    private static String strParameters[];
    private static String ResultString = "";
    private long lngProgSecId=0, lngEmployeeId=0, lngSubId=0;
    private int intOfficeId=0;
    private String strAttendanceDate="", strIds="";
    private CheckBox chkIsSend;
    private String strResultMessage="";
    ImageButton butSend;
    private ArrayList<String> student_list = new ArrayList<String>(200);
    private ArrayList<CheckBox> allChks = new ArrayList<CheckBox>();
    AlertDialog.Builder alertDialogBuilder;
    private CheckBox checkAll;
    final int textSize = 14;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationsectionstudentlist);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Notification to Student");
        Button btnBack = (Button) findViewById(R.id.button_back);
//        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
//        tvLastUpdated.setVisibility(View.INVISIBLE);
        butSend = (ImageButton) findViewById(R.id.btnSend);
        butSend.setOnClickListener(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Recycler View Menu
                Intent intent = new Intent(NotificationStudentList.this, NotificationForStudentsSubjectList.class);
                startActivity(intent);
            }
        });
        if (getIntent().getExtras() != null){
            lngProgSecId = getIntent().getExtras().getLong("sectionid");
            lngSubId = getIntent().getExtras().getLong("subid");
            //strIds = getIntent().getExtras().getString("ids");
        }
        checkAll=(CheckBox)findViewById(R.id.chkAll);
        //CheckBox chk = (CheckBox) findViewById(R.id.chk1);
        checkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((CheckBox) v).isChecked();
                hideKeyboard(NotificationStudentList.this);
                // Check which checkbox was clicked
                if (checked){
                    // Do your coding
                    for (int i=0; i < allChks.size(); i++){
                        allChks.get(i).setChecked(true);
                    }
                }
                else{
                    // Do your coding
                    for (int i=0; i < allChks.size(); i++){
                        allChks.get(i).setChecked(false);
                    }
                }
            }
        });
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        //intOfficeId = loginsession.getInt("officeid", 0);
        //strAttendanceDate = loginsession.getString("attendancedate", "");
        lngEmployeeId = loginsession.getLong("userid", 1);
        strParameters = new String[]{"Long", "subjectid", String.valueOf(lngSubId),
                "Long", "programsectionid", String.valueOf(lngProgSecId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getStudentListJson";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // new intent to call an activity that you choose
        //Recycler View Menu
        Intent intent = new Intent(this, HomeScreenCategory.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnSend:
                if (student_list.size() == 0) return;
                EditText input = (EditText) findViewById(R.id.etNotificationMessage);
                final String strNotificationMessage = input.getText().toString();
                if (! Utility.isNotNull(strNotificationMessage)) return;
                // set title
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Send Notification");
                //final EditText input = new EditText(this);
                //alertDialogBuilder.setView(input);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Do something with value!
                        hideKeyboard(NotificationStudentList.this);
                        JSONObject obj = new JSONObject();
                        JSONArray ArrayObj = new JSONArray();
                        String jsonString="";
                        try {
                            for (int i=0; i < student_list.size(); i++){
                                String item = student_list.get(i);
                                String[] strColumns = item.split("##");
                                if (allChks.get(i).isChecked()) {
                                    obj = new JSONObject();
                                    obj.put("uniqueid", strColumns[0]);  //strColumns[0] = studentid
                                    ArrayObj.put(obj);
                                }
                            }
                            if (ArrayObj.length() > 0){
                                jsonString = ArrayObj.toString();
                            }
                        }catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        if (Utility.isNotNull(jsonString)){
                            strParameters = new String[]{"String", "returndata", jsonString,
                                    "Long", "subjectid", String.valueOf(lngSubId),
                                    "Long","employeeid",String.valueOf(lngEmployeeId),
                                    "String","notificationmessage",strNotificationMessage};
                            WebService.strParameters = strParameters;
                            WebService.METHOD_NAME = "sendNotification";
                            AsyncCallSaveWS task = new AsyncCallSaveWS();
                            task.execute();
                        }
                        else{

                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });
                alertDialogBuilder.show();
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
        ProgressDialog dialog = new ProgressDialog(NotificationStudentList.this);
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
                alertDialogBuilder = new AlertDialog.Builder(NotificationStudentList.this);
                alertDialogBuilder.setTitle("Notification Sent Message");
                // set dialog message
                alertDialogBuilder
                        .setMessage(jsonObject.getString("Message"))
                        .setCancelable(false)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id){
                                student_list.clear();
                                cleanTable((TableLayout) findViewById(R.id.tblStudentList));
                                Intent intent = new Intent(NotificationStudentList.this, NotificationForStudentsSubjectList.class);
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


    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(NotificationStudentList.this);

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
                    Toast.makeText(NotificationStudentList.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                Toast.makeText(NotificationStudentList.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

//    View.OnClickListener getOnClickDoSomething(final CheckBox button,final EditText editText){
//        return new View.OnClickListener() {
//            public void onClick(View v) {
//                if (button.isChecked()){
//                    editText.setText("");
//                    editText.setEnabled(false);
//                }
//                else{
//                    editText.setText("");
//                    editText.setEnabled(true);
//                }
//            }
//        };
//    }

    View.OnClickListener getOnClickDoSomething(){
        return new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(NotificationStudentList.this);
            }
        };
    }

    public void addData(String strRow,int pos){
        TableLayout tl = findViewById(R.id.tblStudentList);
        TableRow tr = new TableRow(NotificationStudentList.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, 10, 10, 10);
//        params1.weight = 1;
        tr.setLayoutParams(params1);

        String[] strColumns = strRow.split("##");
        //String[] strInnerColumns = strColumns[0].split("_");
        tr.setBackgroundResource(R.color.colorWhite);
        //btAttenStatus = new Button(this);

        TextView tvRegNO = new TextView(NotificationStudentList.this);
        TextView tvStudentName = new TextView(NotificationStudentList.this);

        chkIsSend = new CheckBox(this);
        TableRow.LayoutParams params2 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params2.setMargins(30, 10, 10, 10);
        chkIsSend.setLayoutParams(params2);
        chkIsSend.setId(Integer.parseInt(strColumns[0].toString()));
        chkIsSend.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        chkIsSend.setOnClickListener(getOnClickDoSomething());

//        chkIsSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(buttonView.isChecked())
//                {
//                    buttonView.setBackgroundColor(Color.WHITE);  //Color.rgb(64, 131, 207)
//                }
//                if(!buttonView.isChecked())
//                {
//                    buttonView.setBackgroundColor(Color.WHITE);
//                }
//
//            }
//        });
        allChks.add(chkIsSend);
        tr.addView(chkIsSend);
        //registerno
        tvRegNO.setLayoutParams(params1);
//        tvRegNO.setWidth(100);
        tvRegNO.setText(strColumns[2].trim());
        tvRegNO.setTextColor(getResources().getColor(R.color.colorBlack));
        //tvRegNO.setOnClickListener(getOnClickDoSomething(btAttenStatus, tvRegNO, tvStudentName));
        tvRegNO.setPadding(0, 20, 15, 20);
        tvRegNO.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tvRegNO.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tvRegNO);

        //Student Name
        tvStudentName.setLayoutParams(params1);
        tvStudentName.setText(strColumns[1].trim()); // + "(" + strColumns[2].trim() + ")");
        tvStudentName.setTextColor(getResources().getColor(R.color.colorBlack));
        //tvStudentName.setOnClickListener(getOnClickDoSomething(btAttenStatus, tvRegNO, tvStudentName));
        tvStudentName.setPadding(15, 20, 15, 20);
        tvStudentName.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tvStudentName.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tvStudentName);
        Resources resource = this.getResources();

        if(pos%2 == 0) {
            tr.setBackgroundColor(resource.getColor(R.color.colorWhite));
        }else{
            tr.setBackgroundColor(resource.getColor(R.color.colorGrey));
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
}
