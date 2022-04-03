package com.shasun.staffportal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import webservice.EncryptDecrypt;
import webservice.SqlliteController;
import webservice.WebService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button butLogin;
    ImageButton btnInfo;
    TextInputEditText etUsername, etPassword;
    TextInputLayout passwordInputLayout,usernameInputLayout;
    String editTextUsername, editTextPassword;
    ProgressDialog dialog;
    private static String strParameters[];
    private static String ResultString = "";
    SQLiteDatabase db;
    private String imeiNumber="";
    private String token="";
    TelephonyManager telephonyManager;
    SqlliteController controllerdb = new SqlliteController(this);
    EncryptDecrypt crypt = new EncryptDecrypt();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         dialog = new ProgressDialog(MainActivity.this);

        FirebaseMessaging.getInstance().subscribeToTopic("evarsity");

        FirebaseInstanceId.getInstance().getInstanceId()
        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    //Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                token = task.getResult().getToken();
              //  Log.e("Token Test : ", token);
               }
        });


        deviceId();

        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        db = controllerdb.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM stafflogindetails ", null);
            if (cursor.moveToFirst()) {
                SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
                SharedPreferences.Editor ed = loginsession.edit();
                do {
                    ed.putLong("userid", cursor.getLong(cursor.getColumnIndex("employeeid")));
                    ed.putString("employeename", cursor.getString(cursor.getColumnIndex("employeename")));
                    ed.putString("department", cursor.getString(cursor.getColumnIndex("department")));
                    ed.putString("designation", cursor.getString(cursor.getColumnIndex("designation")));
                    ed.commit();
                } while (cursor.moveToNext());
                Intent intent = new Intent(MainActivity.this, HomePageGridViewLayout.class);
                startActivity(intent);
                finish();
            }
            cursor.close();
        }catch (Exception e){

        }
        if(CheckNetwork.isInternetAvailable(MainActivity.this)) {
            butLogin = (Button) findViewById(R.id.loginButton);
            butLogin.setOnClickListener(this);
            btnInfo = (ImageButton) findViewById(R.id.btnInfo);
            btnInfo.setOnClickListener(this);
            hideKeyboard();
        }
        else {
            Toast.makeText(MainActivity.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v){
        etUsername = (TextInputEditText) findViewById(R.id.usernameInput);
        etPassword = (TextInputEditText) findViewById(R.id.passwordInput);
        passwordInputLayout =  (TextInputLayout) findViewById(R.id.passwordInputLayout);
        usernameInputLayout =  (TextInputLayout) findViewById(R.id.usernameInputLayout);
        editTextUsername = etUsername.getText().toString().trim();
        editTextPassword = etPassword.getText().toString().trim();
        strParameters = new String[] { "String","userid","", "String","password", ""};
        SharedPreferences myPrefs = v.getContext().getSharedPreferences("SessionLogin", MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.clear();
        editor.commit();
        SqlliteController sc = new SqlliteController(v.getContext());
        sc.deleteLoginStaffDetails();

        switch (v.getId()){
            case R.id.loginButton:
                if (!CheckNetwork.isInternetAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }else {

                    if (!Utility.isNotNull(editTextUsername)) {
                        usernameInputLayout.setError("username is required!");
                    }
                    if (!Utility.isNotNull(editTextPassword)) {
                        // etPassword.setError("password is required!");
                        passwordInputLayout.setError("password is required!");
                    }
                    deviceId();
                    if (Utility.isNotNull(editTextPassword) && Utility.isNotNull(editTextUsername)) {
                        strParameters = new String[]{"String", "userid", editTextUsername, "String", "password", editTextPassword,
                                "String", "deviceid", imeiNumber, "String", "acesstoken", token};
                        new Thread(new Runnable() {
                            public void run() {
                                WebService.strParameters = strParameters;
                                WebService.METHOD_NAME = "authenticateLoginUserJson";  //"authenticateLoginUserJsonEncrypted";
                                AsyncCallWS task = new AsyncCallWS();
                                task.execute();
                            }
                        }).start();
                        // dialog.setMessage("Loading......");
                        dialog.setMessage(getResources().getString(R.string.loading));

                        dialog.show();
                        hideKeyboard();
                        butLogin.setEnabled(false);
                    }
                }
                break;
            case R.id.btnInfo:
                Toast.makeText(MainActivity.this, getResources().getString(R.string.loginCredentials), Toast.LENGTH_LONG).show();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private void deviceId(){
        try {
            telephonyManager = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
             imeiNumber = telephonyManager.getDeviceId();
        }catch(SecurityException e){
            imeiNumber = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);
                        return;
                    }
                    imeiNumber = telephonyManager.getDeviceId();
//                    Log.d(TAG, imeiNumber);
//                    Toast.makeText(MainActivity.this,imeiNumber,Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,getResources().getString(R.string.loginPermission),Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void hideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            if(android.os.Debug.isDebuggerConnected())
                android.os.Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
            SharedPreferences.Editor ed = loginsession.edit();
            try{
               // String strResultString = crypt.getDecryptedData(ResultString.toString());
                JSONObject object = new JSONObject(ResultString.toString());
                if (!object.isNull("employeeid")){
                    ed.putLong("userid", object.getLong("employeeid"));
                    ed.putLong("officeid", object.getLong("officeid"));
//                    ed.putString("registerno", object.getString("registerno"));
                    ed.putString("employeename", object.getString("employeename"));
                    ed.putString("department", object.getString("department"));
                    ed.putString("designation", object.getString("designation"));
//                    ed.putString("school", object.getString("officename"));
//                    ed.putLong("courseid", object.getLong("courseid"));
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.loginSuccess), Toast.LENGTH_LONG).show();
                    ed.commit();
                    SqlliteController sc = new SqlliteController(MainActivity.this);
                    sc.deleteLoginStaffDetails();
                    /*
                    Log.e("Radha Test: " , object.getString("menuids"));
                    : 1##Personal Details,2##My Timetable,3##Leave Status,
                    4##Leave Entry,5##Leave Approval,6##Student Attendance,
                    7##Internal Mark Entry,8##Biometric Log,9##Payslip,
                            10##Send Notification to Students,
                    11##Send Notification to Staff,12##Notification to All,
                            13##Notification View,14##Canteen Menu Order,
                            15##Venue Booking Request,17##Management Dashboard,
                    18##Faculty Dashboard,19##Create Assignment,
                    20##Permission Entry,50##Logout

                     */
                    sc.insertLoginStaffDetails(object.getLong("employeeid"),object.getString("employeename"),
                            object.getString("department"),object.getString("designation"),object.getString("menuids"));
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Intent intent = new Intent(MainActivity.this, HomePageGridViewLayout.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    butLogin.setEnabled(true);
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.loginFailed), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                System.out.println("Error in Login Activity:"+e.getMessage());
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //this.finish();
        finishAffinity();
    }
}
