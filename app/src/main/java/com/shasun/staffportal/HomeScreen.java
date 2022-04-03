package com.shasun.staffportal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import webservice.SqlliteController;

public class HomeScreen extends AppCompatActivity {
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private TextView tvPageTitle;
    private long lngEmployeeId=0;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);
    String[] MENULIST= new String[1];
    int[] ICONS =new int[1];


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("DashBoard");
        Button btnBack = (Button) findViewById(R.id.button_back);
        btnBack.setVisibility(View.INVISIBLE);
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        getMenuValues();
        Button btnRefresh=(Button) findViewById(R.id.button_refresh);
//        btnRefresh.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.icon_logout));
//        btnRefresh.setWidth(100);
//        btnRefresh.setHeight(100);
        btnRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SharedPreferences myPrefs = view.getContext().getSharedPreferences("SessionLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.clear();
                editor.commit();
                SqlliteController sc = new SqlliteController(view.getContext());
                sc.deleteLoginStaffDetails();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(intent);
            }
        });
//        if (MENULIST.length == 0) {
//            Toast.makeText(HomeScreen.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
//        } else {
//            mRecyclerView = (RecyclerView) findViewById(R.id.rvMenuContent); // Assigning the RecyclerView Object to the xml View
//            mRecyclerView.setHasFixedSize(true);
//            // Letting the system know that the list objects are of fixed size
//            HomeScreenAdapter TVA = new HomeScreenAdapter(MENULIST, ICONS);
//            mRecyclerView.setAdapter(TVA);
//            mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
//            mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
//        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
//        // new intent to call an activity that you choose
//        Intent intent = new Intent(this, HomeScreen.class);
//        //Grid View Menu
//        //Intent intent = new Intent(this, HomePageGridViewLayout.class);
//        startActivity(intent);
//        // finish the activity picture
        this.finish();
    }

    private void getMenuValues() {
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM userwisemenuaccessrights WHERE employeeid=" + lngEmployeeId + " ORDER BY  menusortnumber", null );
            MENULIST= new String[cursor.getCount()];
            ICONS= new int[cursor.getCount()];
            cursor.moveToFirst();
            if (cursor.moveToFirst()){
                int i = 0;
                do {
                    long lngMenuId=cursor.getLong(cursor.getColumnIndex("menuid"));
                    String strMenuname = cursor.getString(cursor.getColumnIndex("menuname"))+ "##" +lngMenuId;
                    MENULIST[i] = strMenuname;

                    if (lngMenuId == 1){
                        ICONS[i] = R.drawable.icon_profile;
                    }else if (lngMenuId == 2){
                        ICONS[i] = R.drawable.icon_timetable;
                    } else if (lngMenuId == 3){
                        ICONS[i] = R.drawable.icon_leavestatus;
                    }else if (lngMenuId == 4){
                        ICONS[i] = R.drawable.icon_leaveentry;
                    }else if (lngMenuId == 5){
                        ICONS[i] = R.drawable.icon_leaveapproval;
                    }else if (lngMenuId == 6){
                        ICONS[i] = R.drawable.icon_studentattendance;
                    }else if (lngMenuId == 7){
                        ICONS[i] = R.drawable.icon_markentry;
                    }else if (lngMenuId == 8){
                        ICONS[i] = R.drawable.icon_biometriclog;
                    }else if (lngMenuId == 9){
                        ICONS[i] = R.drawable.icon_payslip;
                    }else if (lngMenuId == 10){
                        ICONS[i] = R.drawable.icon_notification;
                    }else if (lngMenuId == 11){
                        ICONS[i] = R.drawable.icon_notificationtoemployee;
                    }else if (lngMenuId == 12){
                        ICONS[i] = R.drawable.icon_notificationtoall;
                    }else if (lngMenuId == 13) {
                        ICONS[i] = R.drawable.icon_notificationview;
                    }else if (lngMenuId == 14){
                        ICONS[i] = R.drawable.icon_logout; //canteen
                    }else if (lngMenuId == 15){
                        ICONS[i] = R.drawable.icon_logout; //Venue Booking
                    }else if (lngMenuId == 16){
                        ICONS[i] = R.drawable.icon_logout; //ENotice View
                    }else if (lngMenuId == 17){
                        ICONS[i] = R.drawable.icon_logout; //Management Dashboard
                    }else if (lngMenuId == 18){
                        ICONS[i] = R.drawable.icon_logout; //Faculty Dashboard
                    }else if (lngMenuId == 19){
                        ICONS[i] = R.drawable.icon_logout; //LMS Index
                    }else if (lngMenuId == 50){
                        ICONS[i] = R.drawable.icon_logout;
                    }
                    i++;
                }while (cursor.moveToNext());
            }
            cursor.close();

            if (MENULIST.length == 0) {
                Toast.makeText(HomeScreen.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
            } else {
                mRecyclerView = (RecyclerView) findViewById(R.id.rvMenuContent); // Assigning the RecyclerView Object to the xml View
                mRecyclerView.setHasFixedSize(true);
                // Letting the system know that the list objects are of fixed size
                HomeScreenAdapter TVA = new HomeScreenAdapter(MENULIST, ICONS);
                mRecyclerView.setAdapter(TVA);
                mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
                mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}