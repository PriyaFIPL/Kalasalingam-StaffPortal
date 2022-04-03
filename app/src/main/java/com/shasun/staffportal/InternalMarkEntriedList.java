package com.shasun.staffportal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
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

public class InternalMarkEntriedList extends AppCompatActivity {
    private TextView tvPageTitle, tvLastUpdated,attendanceHeader1,attendanceHeader2;
    private long lngInternalBreakUpId=0, lngProgSecId=0;
    private static String strParameters[];
    private static String ResultString = "";
    private String strResultMessage="";
    private ArrayList<String> student_list = new ArrayList<String>(200);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internalmarkentrystudentlist);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Internal Mark View");
        attendanceHeader1 = (TextView) findViewById(R.id.attendanceHeader1);
        attendanceHeader2 = (TextView) findViewById(R.id.attendanceHeader2);
        attendanceHeader1.setText(getIntent().getExtras().getString(Properties.attendanceHeader1));
        attendanceHeader2.setText(getIntent().getExtras().getString(Properties.attendanceHeader2));

        Button btnBack=(Button) findViewById(R.id.button_back);
        ImageButton butSave = (ImageButton) findViewById(R.id.saveButton);
        butSave.setVisibility(View.INVISIBLE);
       // tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
        //tvLastUpdated.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            //Recycler View Menu
            //Intent intent = new Intent(InternalMarkEntriedList.this, HomeScreen.class);
            //Grid View Menu
            Intent intent = new Intent(InternalMarkEntriedList.this, HomeScreenCategory.class);
            startActivity(intent);
            }
        });
        if (getIntent().getExtras()!=null){
            lngInternalBreakUpId=getIntent().getExtras().getLong("internalbreakupid");
            lngProgSecId=getIntent().getExtras().getLong("progsecid");
            //intBreakUpId = getIntent().getExtras().getInt("breakupid");
        }
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        //lngEmployeeId = loginsession.getLong("userid", 1);

        strParameters = new String[]{"Long", "internalbreakupid", String.valueOf(lngInternalBreakUpId), "Long", "progsectionid", String.valueOf(lngProgSecId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getInternalMarkEntriedDetailsJson";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // new intent to call an activity that you choose
        //Intent intent = new Intent(this, HomeScreen.class);
        //Grid View Menu
        Intent intent = new Intent(this, HomeScreenCategory.class);
        startActivity(intent);
        // finish the activity picture
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(InternalMarkEntriedList.this);

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
                            object.getString("markobtained")+ "##"+
                            object.getString("conductingMaxMarks");

                    student_list.add(object.getString("uniqueid")
                            + "##" +object.getString("studentName")
                            + "##" +object.getString("registerNumber").trim()
                            + "##" +object.getString("markobtained")
                            + "##" +object.getString("conductingMaxMarks"));
                    if (!strRow.equals(""))
                        addData(strRow,i);
                    else
                        break;
                }
                if (strRow.equals("")){
                    Toast.makeText(InternalMarkEntriedList.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println(e.getMessage());
                Toast.makeText(InternalMarkEntriedList.this, "Response: "+strResultMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addData(String strRow,int bColor){
        TableLayout tl = findViewById(R.id.tblStudentList);
        TableRow tr = new TableRow(InternalMarkEntriedList.this);
        TableRow.LayoutParams params1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params1.setMargins(15, 25, 15, 25);
        params1.weight = 1;
        tr.setLayoutParams(params1);

        tr.setLayoutParams(params1);
        String[] strColumns = strRow.split("##");
        if (bColor % 2 != 0){
            tr.setBackgroundResource(R.color.cardColorg);
        }else{
            tr.setBackgroundResource(R.color.colorWhite);

        }

        //registerno
        TextView tv = new TextView(InternalMarkEntriedList.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[2].trim());
        tv.setPadding(20, 20, 20, 20);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tv);
        //Student Name
        tv = new TextView(InternalMarkEntriedList.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[1].trim()); // + "(" + strColumns[2].trim() + ")");
        tv.setPadding(20, 20, 20, 20);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        tv.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tv);

        //Mark Obtained
        tv = new TextView(InternalMarkEntriedList.this);
        tv.setLayoutParams(params1);
        tv.setText(strColumns[3].trim());
        tv.setPadding(20, 20, 20, 20);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setTextSize(getResources().getDimension(R.dimen.student_staff_list));
        tr.addView(tv);
        tl.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
    }
}
