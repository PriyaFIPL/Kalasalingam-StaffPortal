package com.shasun.staffportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.shasun.staffportal.properties.Properties;

import org.json.JSONArray;
import org.json.JSONObject;
import webservice.SqlliteController;
import webservice.WebService;

public class StaffTimeTable extends AppCompatActivity {
    private int intTemplateId=0;
    private ProgressDialog progDailog;
    private static String strParameters[];
    private static String ResultString = "";
    private long lngEmployeeId=0;
    private TextView pageHeader;
//    private int intOfficeId=0;
    TextView tvPageTitle,tvLastUpdated;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stafftimetable);
        //Status Bar Color
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        pageHeader = (TextView) findViewById(R.id.pageHeader);
        tvPageTitle.setText("My TimeTable");
        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
        Button btnBack=(Button) findViewById(R.id.button_back) ;
        Button btnRefresh=(Button) findViewById(R.id.button_refresh) ;
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
           onBackPressed();
            }
        });
        Intent i = getIntent();
        intTemplateId = getIntent().getExtras().getInt("templateid");
        pageHeader.setText(getIntent().getExtras().getString(Properties.timeTableHeader));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
//        intOfficeId = loginsession.getInt("officeid", 1);
        lngEmployeeId = loginsession.getLong("userid", 1);
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!CheckNetwork.isInternetAvailable(StaffTimeTable.this)) {
                    Toast.makeText(StaffTimeTable.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }else {

                    strParameters = new String[]{"int", "templateid", String.valueOf(intTemplateId),
                            "Long", "employeeid", String.valueOf(lngEmployeeId)};
//                    "int","officeid", String.valueOf(intOfficeId)};
                    WebService.strParameters = strParameters;
                    WebService.METHOD_NAME = "getEmployeeTimeTableJson";
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();
                }
            }
        });
        displayTimeTable();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void displayTimeTable(){
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,strftime('%H:%M', fromtime)||' - '||strftime('%H:%M', totime) as hourdesc FROM stafftimetable " +
                    " WHERE employeeid=" + lngEmployeeId + " AND dayordertemplateid=" + intTemplateId +
                    " GROUP BY fromtime,totime", null);
            if (cursor.moveToFirst()) {
                String strRow = "D/H";
                do {
                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    strRow = strRow + "##" + cursor.getString(cursor.getColumnIndex("hourdesc"));
                }while (cursor.moveToNext());
                String[] strColumns = strRow.split("##");
                addHeaderData(strColumns);
                cursor.close();

                cursor = db.rawQuery("SELECT dayorderdesc,GROUP_CONCAT(subjectcode,'##') AS subject FROM stafftimetable WHERE employeeid = " +
                        lngEmployeeId + " AND dayordertemplateid = " + intTemplateId + " GROUP BY dayorderdesc ORDER BY dayorderid", null);
                if (cursor.moveToFirst()){
                    String strDayOrder="", strOldDayOrder="";
                    strRow="";
                    do {
                        strDayOrder = cursor.getString(cursor.getColumnIndex("dayorderdesc")).toString();
                        if (!strDayOrder.equals(strOldDayOrder)){
                            if (!strOldDayOrder.equals("")){
                                strColumns = strRow.split("##");
                                addData(strColumns);
                            }
                            strRow = strDayOrder;
                            strOldDayOrder = strDayOrder;
                        }
                        strRow = strRow + "##" + cursor.getString(cursor.getColumnIndex("subject"));
                    }while (cursor.moveToNext());
                    strColumns = strRow.split("##");
                    addData(strColumns);
                }
                cursor.close();

                cursor = db.rawQuery("SELECT GROUP_CONCAT(distinct subjectdesc) AS subject FROM stafftimetable WHERE subjectcode not in ('-') AND employeeid = " +
                        lngEmployeeId + " AND dayordertemplateid = " + intTemplateId + " GROUP BY subjectdesc", null);
                if (cursor.moveToFirst()){
                    strRow="";
                    do {
                        strRow = cursor.getString(cursor.getColumnIndex("subject"));
                        strColumns = strRow.split(",");
                        addAbbrivationData(strColumns);
                    } while (cursor.moveToNext());
                }else{
                    TableLayout tl = findViewById(R.id.abbrevation);
                    tl.setVisibility(View.GONE);

                }
                cursor.close();
            }
            else{
                if (!CheckNetwork.isInternetAvailable(StaffTimeTable.this)) {
                    Toast.makeText(StaffTimeTable.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }else {

                    strParameters = new String[]{"int", "templateid", String.valueOf(intTemplateId),
                            "Long", "employeeid", String.valueOf(lngEmployeeId)};
//                        "int","officeid", String.valueOf(intOfficeId)};
                    WebService.strParameters = strParameters;
                    WebService.METHOD_NAME = "getEmployeeTimeTableJson";
                    AsyncCallWS task = new AsyncCallWS();
                    task.execute();
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            strParameters = new String[] {"int","templateid",String.valueOf(intTemplateId),
                    "Long","employeeid", String.valueOf(lngEmployeeId)};
                    //"int","officeid", String.valueOf(intOfficeId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getEmployeeTimeTableJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(StaffTimeTable.this);
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
            if(android.os.Debug.isDebuggerConnected())
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
                int intCurrDayOrder=0, intCurrHour=0;
                JSONArray temp = new JSONArray(ResultString.toString());
                SqlliteController sc = new SqlliteController(StaffTimeTable.this);
                sc.deleteStaffTimeTable(lngEmployeeId);
                for (int i = 0; i <= temp.length() - 1; i++){
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    intCurrDayOrder = Integer.parseInt(object.getString("dayorderid"));
                    intCurrHour = Integer.parseInt(object.getString("hourid"));
                    sc.insertStaffTimeTable(lngEmployeeId, intTemplateId, intCurrDayOrder, intCurrHour,object.getString("dayorderdesc"),
                            object.getString("fromtime"),object.getString("totime"),object.getString("subjectcode"),
                            object.getString("subjectdesc"));
                }
                TableLayout tl = findViewById(R.id.tblViewTimeTable);
                int count = tl.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = tl.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                TableLayout t2 = findViewById(R.id.abbrevation);
                count = t2.getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = t2.getChildAt(i);
                    if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
                }
                displayTimeTable();
            }
            catch (Exception e){}
        }
    }

    public void addHeaderData(String[] str){
//        String str[];
//        if (intStructureId==168){
//            str = new String [] {"Subject", "PT", "NBM", "SEA", "TEX", "OT", "Grade"};
//        }
//        else{
//            str = new String [] {"Subject", "Marks", "Grade"};
//        }
        TableLayout tl = findViewById(R.id.tblViewTimeTable);
        TableRow tr = new TableRow(this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
//        params1.setMargins(2, 0, 0, 2);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        tr.setBackgroundResource(R.color.cardColoro);
        for (int i = 0; i < str.length ; i++){
            TextView tv = new TextView(this);
            tv.setLayoutParams(params1);
            tv.setTextColor(0xFFFFFFFF);
            tv.setPadding(5,10,5,10);
            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            tv.setTextSize(getResources().getDimension(R.dimen.timetableheader));
            tv.setGravity(Gravity.CENTER);
            String strtemp=str[i];
            if(strtemp.contains("-")) {
                strtemp = str[i].replace("-", "<br>");
                tv.setText(HtmlCompat.fromHtml(strtemp, 0));
            }
            else
                tv.setText(strtemp);
            tr.addView(tv);
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }

    public void addData(String[] str){
        TableLayout tl = findViewById(R.id.tblViewTimeTable);
        TableRow tr = new TableRow(this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(2, 0, 0, 2);
        params1.weight = 1;
        tr.setLayoutParams(params1);
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 8, 8, 8, 8, 0, 0, 0, 0 });
        shape.setColor(Color.WHITE);
        shape.setStroke(3, Color.DKGRAY);
        tr.setBackground(shape);
        for (int i = 0; i < str.length ; i++){
            TextView tv = new TextView(this);
            tv.setLayoutParams(params1);
            String strTemp=str[i];
            if(strTemp.contains("Day")) {
                strTemp=strTemp.replace("Day","");
            }
            tv.setText(strTemp);
            if (i==0){
                tv.setBackgroundResource(R.color.cardColoro);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                tv.setTextSize(getResources().getDimension(R.dimen.timetableheader));

            }else{
                tv.setBackgroundResource(R.color.colorWhite);
                tv.setTextSize(getResources().getDimension(R.dimen.timetableheader));

            }
//            tv.setPadding(20,10,20,10);
            tv.setPadding(5,20,5,20);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tr.addView(tv);
        }
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//        // Add vertical separator
//        View v = new View(this);
//        v.setLayoutParams(new TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT));
//        v.setBackgroundColor(Color.rgb(50, 50, 50));
//        tl.addView(v);
    }

    public void addAbbrivationData(String[] str){

            //String[] str={"PT  - Periodic Test","NBM - Note Book Maintenace","SEA - Subject Enri. Activity","TEX -Terminal Exam","OT  - Overall Total"};
            TableLayout tl = findViewById(R.id.abbrevation);
            for (int i = 0; i < str.length; i++) {
                TableRow tr = new TableRow(this);
                TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                TextView tv = new TextView(this);
                tv.setLayoutParams(params1);
                tv.setText(str[i]);
                tv.setPadding(20, 10, 20, 10);
                tv.setTextSize(getResources().getDimension(R.dimen.timetableAbb));
                tr.addView(tv);
                tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }

    }
}
