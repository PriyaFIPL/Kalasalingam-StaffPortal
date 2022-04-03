package com.shasun.staffportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import webservice.WebService;

//http://erp.shasuncollege.edu.in/evarsityshasun/resources/Image/I2.jpg

public class CanteenSelection extends AppCompatActivity {
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private TextView tvPageTitle, tvLastUpdated;
    private static String strParameters[];
    private static String ResultString = "";
    private long lngEmployeeId=0;
    private String strCanteenIdSelected;
    private static String[] strCanteenName;
    private static String[] strCanteenId;
    private ArrayList<String> template_list = new ArrayList<String>(200);
    public int typeid=0;
    private int intFlag = 0;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen_selection);

        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Canteen Selection");
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        intFlag=getIntent().getIntExtra("Flag",1);
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            Intent intent;
            if (intFlag == 1){
                intent = new Intent(CanteenSelection.this, HomeScreenCategory.class);
                startActivity(intent);
            }
            if (intFlag == 2){
                intent = new Intent(CanteenSelection.this, HomePageGridViewLayout.class);
                startActivity(intent);
            }
            }
        });
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        typeid=1;
        strParameters = new String[]{"int", "itemid", String.valueOf(0)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getCanteenListJson";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();

        ListView listView1=(ListView) findViewById(R.id.lvCanteenName);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                TextView tv=(TextView) view.findViewById(R.id.tvCanteenId);
                strCanteenIdSelected=tv.getText().toString();
                typeid=2;
                strParameters = new String[]{"int", "canteenid", strCanteenIdSelected};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "getItemListJson";
                AsyncCallWS task = new AsyncCallWS();
                task.execute();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(CanteenSelection.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(CanteenSelection.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(CanteenSelection.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));
            dialog.show();
          //  //Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            ////Log.i(TAG, "doInBackground");
            if (android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            ////Log.i(TAG, "onPostExecute");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            try {
                if (ResultString.toString().equals("")){
                    builder = new AlertDialog.Builder(CanteenSelection.this);
                    if (typeid==1) {
                        builder.setMessage(R.string.canteennotfoundmsg).setTitle("Canteen Menu Order")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                    }
                    if(typeid==2) {
                        builder.setMessage(R.string.menuitemsfoundmsg).setTitle("Canteen Menu Order")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            });
                    }
                    AlertDialog alert = builder.create();
                    alert.setTitle("Canteen - Place Menu Order");
                    alert.show();
                }
                JSONArray temp = new JSONArray(ResultString.toString());
                template_list.clear();
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    if(typeid==1)
                        template_list.add(object.getString("canteenid") + "##" +object.getString("canteenname"));
                    if(typeid==2) {
                        template_list.add(object.getString("itemid") + "##" + object.getString("itemname") + "##" + object.getString("amountperunit") + "##" + object.getString("availablequantity"));
                    }
                }
                if (template_list.size() == 0){
                    Toast.makeText(CanteenSelection.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                }
                else {
                    if(typeid==1){
                        String[] strTempArrayCanteenName= template_list.toArray(new String[template_list.size()]);
                        strCanteenId=new String[template_list.size()];
                        strCanteenName=new String[template_list.size()];
                        for (int i=0;i< strTempArrayCanteenName.length;i++) {
                            String[] strTempCanteenName=strTempArrayCanteenName[i].split("##");
                            strCanteenId[i]=strTempCanteenName[0];
                            strCanteenName[i]=strTempCanteenName[1];
                        }
                        ListView listView=(ListView) findViewById(R.id.lvCanteenName);
                        CustomAdapter customAdapter=new CustomAdapter();
                        listView.setAdapter(customAdapter);
                    }
                    else if(typeid==2) {
                        String[] strTempArrayCanteenName1= template_list.toArray(new String[template_list.size()]);
                        ArrayList<String> stringList = new ArrayList<String>(strTempArrayCanteenName1.length);
                        Collection l = Arrays.asList(strTempArrayCanteenName1);
                        stringList.addAll(l);

                        Intent intent=new Intent(CanteenSelection.this, TestingActivity.class);
                        intent.putExtra("CanteenId",Integer.parseInt(strCanteenIdSelected));
                        intent.putExtra("ItemsCount",stringList.size());
                        intent.putStringArrayListExtra("CanteenItems",stringList);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(CanteenSelection.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return template_list.size();
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView=getLayoutInflater().inflate(R.layout.canteenname_layout,null);
            final TextView txtCanteenId=(TextView) convertView.findViewById(R.id.tvCanteenId);
            final TextView txtCanteenName=(TextView) convertView.findViewById(R.id.tvCanteenName);
            txtCanteenId.setText(strCanteenId[position]);
            txtCanteenName.setText(strCanteenName[position]);
            return convertView;
        }
    }
}