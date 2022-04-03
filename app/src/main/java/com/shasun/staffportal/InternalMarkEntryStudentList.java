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
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
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

import com.shasun.staffportal.properties.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import webservice.WebService;

public class InternalMarkEntryStudentList extends AppCompatActivity implements View.OnClickListener {
    private TextView tvPageTitle,attendanceHeader1,attendanceHeader2;
    private static String strParameters[];
    private static String ResultString = "";
    private long lngInternalBreakUpId=0, lngProgSecId=0, lngEmployeeId=0;
    private int intBreakUpId=0;
    private String strExamDate="";
    private EditText edMarkInp;
    private CheckBox chkIsAbsent;
    private String strResultMessage="";
    ImageButton butSave;
    private String strMarkObtained="";
    private ArrayList<EditText> allEds = new ArrayList<EditText>();
    private ArrayList<CheckBox> allChks = new ArrayList<CheckBox>();

    private ArrayList<String> student_list = new ArrayList<String>(200);
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internalmarkentrystudentlist);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Student List");
        attendanceHeader1 = (TextView) findViewById(R.id.attendanceHeader1);
        attendanceHeader2 = (TextView) findViewById(R.id.attendanceHeader2);
        attendanceHeader1.setText(getIntent().getExtras().getString(Properties.attendanceHeader1));
        attendanceHeader2.setText(getIntent().getExtras().getString(Properties.attendanceHeader2));

        Button btnBack=(Button) findViewById(R.id.button_back);
//        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
//        tvLastUpdated.setVisibility(View.INVISIBLE);
        butSave = (ImageButton) findViewById(R.id.saveButton);
        butSave.setOnClickListener(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            //Recycler View Menu
            //Intent intent = new Intent(InternalMarkEntryStudentList.this, HomeScreen.class);
            //Grid View Menu
            Intent intent = new Intent(InternalMarkEntryStudentList.this, HomePageGridViewLayout.class);
            startActivity(intent);
            }
        });
        if (getIntent().getExtras()!=null){
            lngInternalBreakUpId=getIntent().getExtras().getLong("internalbreakupid");
            lngProgSecId=getIntent().getExtras().getLong("progsecid");
            intBreakUpId = getIntent().getExtras().getInt("breakupid");
        }
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        strExamDate = loginsession.getString("examdate", "");
        strParameters = new String[]{"Long", "internalbreakupid", String.valueOf(lngInternalBreakUpId), "Long", "progsectionid", String.valueOf(lngProgSecId),
                                    "int","breakupid", String.valueOf(intBreakUpId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getStudentsListJson";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.saveButton:
                if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                    Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }else {
// set title
                    alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Mark Entry SAVE");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Do you Want to Save?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    hideKeyboard(InternalMarkEntryStudentList.this);
                                    JSONObject obj = new JSONObject();
                                    JSONArray ArrayObj = new JSONArray();
                                    String jsonString = "";
                                    String strIsAbsent = "";
                                    int x, y;
                                    if (student_list.size() == 0) return;
                                    try {
                                        for (int i = 0; i < student_list.size(); i++) {
                                            String item = student_list.get(i);
                                            String[] strColumns = item.split("##");
                                            strMarkObtained = allEds.get(i).getText().toString();
                                            if (allChks.get(i).isChecked())
                                                strIsAbsent = "true";
                                            else
                                                strIsAbsent = "false";
                                            if (!Utility.isNotNull(strMarkObtained) && strIsAbsent.equals("false")) {
                                                allEds.get(i).setError("Value required!");
                                                allEds.get(i).requestFocus();
                                                return;
                                            }
                                            if (strIsAbsent.equals("false")) {
                                                x = Integer.parseInt(allEds.get(i).getText().toString());  //Input mark obtained value
                                                y = Integer.parseInt(strColumns[4]);  //conducting max mark value
                                                if (x > y) {
                                                    allEds.get(i).setError("Mark should not be greater than Max mark: " + y);
                                                    allEds.get(i).requestFocus();
                                                    return;
                                                }
                                            }
                                            obj = new JSONObject();
                                            obj.put("studentid", strColumns[0]);
                                            obj.put("markobtained", strMarkObtained);
                                            obj.put("IsAbsent", strIsAbsent);
                                            ArrayObj.put(obj);
                                        }
                                        if (ArrayObj.length() > 0) {
                                            jsonString = ArrayObj.toString();
                                        }
                                    } catch (Exception e) {
                                        System.out.println(e.getMessage());
                                    }
                                    if (Utility.isNotNull(jsonString)) {
                                        strParameters = new String[]{"String", "returndata", jsonString,
                                                "Long", "internalbreakupid", String.valueOf(lngInternalBreakUpId),
                                                "Long", "programsectionid", String.valueOf(lngProgSecId),
                                                "Long", "employeeid", String.valueOf(lngEmployeeId),
                                                "String", "examdate", String.valueOf(strExamDate)};
                                        WebService.strParameters = strParameters;
                                        WebService.METHOD_NAME = "saveInternalMarkEntry";
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
        ProgressDialog dialog = new ProgressDialog(InternalMarkEntryStudentList.this);
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
                alertDialogBuilder = new AlertDialog.Builder(InternalMarkEntryStudentList.this);
                alertDialogBuilder.setTitle("SAVE Message");
                // set dialog message
                alertDialogBuilder
                    .setMessage(jsonObject.getString("Message"))
                    .setCancelable(false)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id){
                        student_list.clear();
                        cleanTable((TableLayout) findViewById(R.id.tblStudentList));
                        Intent intent = new Intent(InternalMarkEntryStudentList.this, HomeScreen.class);
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

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(InternalMarkEntryStudentList.this);

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
                            object.getString("dob")+ "##"+
                            object.getString("registerNumber").trim()+ "##"+
                            object.getString("conductingMaxMarks");

                    student_list.add(object.getString("uniqueid")
                            + "##" +object.getString("studentName")
                            + "##" +object.getString("dob")
                            + "##" +object.getString("registerNumber").trim()
                            + "##" +object.getString("conductingMaxMarks"));
                    if (!strRow.equals(""))
                        addData(strRow,i);
                    else
                        break;
                }
                if (strRow.equals("")){
                    Toast.makeText(InternalMarkEntryStudentList.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                Toast.makeText(InternalMarkEntryStudentList.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    View.OnClickListener getOnClickDoSomething(final CheckBox button,final EditText editText){
        return new View.OnClickListener() {
            public void onClick(View v) {
            if (button.isChecked()){
                editText.setText("");
                editText.setEnabled(false);
            }
            else{
                editText.setText("");
                editText.setEnabled(true);
            }
            }
        };
    }

    public void addData(String strRow, int bColor){
        TableLayout tl = findViewById(R.id.tblStudentList);
        TableRow tr = new TableRow(InternalMarkEntryStudentList.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(15, 25, 15, 25);

        params1.weight = 1;
        tr.setLayoutParams(params1);
        String[] strColumns = strRow.split("##");
        if (bColor % 2 != 0){
            tr.setBackgroundResource(R.color.cardColorg);
        }else{
            tr.setBackgroundResource(R.color.colorWhite);

        }
        //registerno
        TextView tv = new TextView(InternalMarkEntryStudentList.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[3].trim());
        tv.setPadding(20, 20, 20, 20);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tv);
        //Student Name
        tv = new TextView(InternalMarkEntryStudentList.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[1].trim()); // + "(" + strColumns[2].trim() + ")");
        tv.setPadding(20, 20, 20, 20);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tv);

        edMarkInp=new EditText(this);
        edMarkInp.setWidth(100);
        edMarkInp.setLayoutParams(params1);

        //edMarkInp.setBackgroundColor(getResources().getColor(R.color.colorRed));
        edMarkInp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        String[] strInnerColumns = strColumns[0].split("_");
        edMarkInp.setTag("edStudentMark"+Integer.parseInt(strInnerColumns[0].toString()));
        edMarkInp.setId(Integer.parseInt(strInnerColumns[0].toString()));
        edMarkInp.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        edMarkInp.setTextColor(Color.BLACK);
        edMarkInp.setFilters(new InputFilter[]{ new InputFilterMinMax("1", strColumns[4].trim())});
        edMarkInp.setInputType(InputType.TYPE_CLASS_NUMBER);
        edMarkInp.setKeyListener(new DigitsKeyListener());
        edMarkInp.setPadding(20, 20, 20, 20);
        allEds.add(edMarkInp);
        tr.addView(edMarkInp);

        chkIsAbsent = new CheckBox(this);
        //chkIsAbsent.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        chkIsAbsent.setLayoutParams(params1);
        chkIsAbsent.setId(Integer.parseInt(strInnerColumns[0].toString()));
        chkIsAbsent.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        chkIsAbsent.setGravity(Gravity.CENTER_VERTICAL);
        chkIsAbsent.setOnClickListener(getOnClickDoSomething(chkIsAbsent,edMarkInp));
        allChks.add(chkIsAbsent);
        tr.addView(chkIsAbsent);
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
}