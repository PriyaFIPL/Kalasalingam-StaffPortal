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

import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import webservice.SqlliteController;
import webservice.WebService;

import static webservice.WebService.strParameters;

public class NotificationView extends AppCompatActivity {
    private static String ResultString = "";
    private long lngEmployeeId=0;
    private int intFlag=0;
    TextView tvPageTitle, tvLastUpdated;
    SQLiteDatabase db;
    private ArrayList<String> notification_list = new ArrayList<String>(200);
    private HashMap<String, List<String>> child_notificationlist = new HashMap<String,List<String>>();
    SqlliteController controllerdb = new SqlliteController(this);
    private String strResultMessage="";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notificationview);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Notification List");
//        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        intFlag = Integer.parseInt(getIntent().getStringExtra("sentreceivedflag"));
        Button btnBack=(Button) findViewById(R.id.button_back);
//        Button btnRefresh=(Button) findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NotificationView.this, NotificationList.class);
                startActivity(intent);
            }
        });
//        btnRefresh.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId), "Int","flag",String.valueOf(intFlag)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getViewSentNotificationListJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
//            }
//        });
//        displayNotificationDetails();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        // new intent to call an activity that you choose
        Intent intent = new Intent(this, NotificationList.class);
        startActivity(intent);
        // finish the activity picture
        this.finish();
    }

    private void displayNotificationDetails(){
        db = controllerdb.getReadableDatabase();
        try {
            String strNotificationDetails="";
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated," +
                    " strftime('%d-%m-%Y', notificationdate) as groupcolumn," +
                    " group_concat(notificationtitle||'##'||notificationtime||'##'||notificationmessage) as notificationdetails " +
                    " FROM notificationdetails WHERE employeeid=" + lngEmployeeId + " GROUP BY notificationdate ORDER BY notificationdate DESC;", null); // ORDER BY strftime('%s', notificationdate)
            if (cursor.moveToFirst()){
                do {
//                    tvLastUpdated.setText("Last Updated: "+cursor.getString(cursor.getColumnIndex("lastupdated")));
                    notification_list.add(cursor.getString(cursor.getColumnIndex("groupcolumn")));
                    StringTokenizer st = new StringTokenizer(cursor.getString(cursor.getColumnIndex("notificationdetails")), ",");
                    List<String> child_listInner = new ArrayList<String>();
                    while (st.hasMoreTokens()){
                        strNotificationDetails = st.nextToken().toString().trim();
                        child_listInner.add(strNotificationDetails);
                        child_notificationlist.put(cursor.getString(cursor.getColumnIndex("groupcolumn")), child_listInner);
                    }
                } while (cursor.moveToNext());
                if (notification_list.size() == 0){
                    Toast.makeText(NotificationView.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    ExpandableListView lstCT = (ExpandableListView) findViewById(R.id.lvSentNotification);
                    NotificationViewLVAdapter NVLVA = new NotificationViewLVAdapter (NotificationView.this, notification_list, child_notificationlist);
                    lstCT.setAdapter(NVLVA);
//                    int count = NVLVA.getGroupCount();
//                    for ( int i = 0; i < count; i++ )
//                        lstCT.expandGroup(i);
                }
            } else {
                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getViewSentNotificationListJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
            cursor.close();
        }catch (Exception e){
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
            WebService.strParameters = strParameters;
            WebService.METHOD_NAME = "getViewSentNotificationListJson";
            AsyncCallWS task = new AsyncCallWS();
            task.execute();
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(NotificationView.this);

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
                SqlliteController sc = new SqlliteController(NotificationView.this);
                sc.deleteNotificationDetails(lngEmployeeId);
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    String[] strNotDate =  object.getString("notificationdate").split("-");
                    String strNotificationDate = strNotDate[2] +"-" + strNotDate[1] + "-" + strNotDate[0];
                    sc.insertNotificationDetails(lngEmployeeId,strNotificationDate ,
                            object.getString("notificationtime"),object.getString("notificationtitle"),
                            object.getString("message"));
                }
                final ExpandableListView lstCT = (ExpandableListView)findViewById(R.id.lvSentNotification);
                notification_list.clear();
                child_notificationlist.clear();
                NotificationViewLVAdapter NVLVA = new NotificationViewLVAdapter(NotificationView.this,notification_list,child_notificationlist);
                NVLVA.notifyDataSetChanged();
                displayNotificationDetails();
            } catch (Exception e) {
                Toast.makeText(NotificationView.this, "Response: " + strResultMessage, Toast.LENGTH_LONG).show();
                System.out.println(e.getMessage());
            }
        }
    }
}
