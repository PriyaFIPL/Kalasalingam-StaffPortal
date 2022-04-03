package com.shasun.staffportal;

import static webservice.WebService.strParameters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import webservice.SqlliteController;
import webservice.WebService;

public class ProfileEdit extends AppCompatActivity implements View.OnClickListener{
    private static String ResultString = "";
    private long lngEmployeeId=0;
    TextView  tvPageTitle, tvLastUpdated,tvAddress;
    TextInputEditText tvEmployee, tvDob, tvDivision, tvDesignation, tvQualification,tvDoj, tvMobile, tvEmail;
    SQLiteDatabase db;
    SqlliteController controllerdb = new SqlliteController(this);

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileedit);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Profile Edit");
        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
        Button btnBack=(Button) findViewById(R.id.button_back);
        Button btnRefresh=(Button) findViewById(R.id.button_refresh);
        btnRefresh.setVisibility(View.GONE);
        Button butSave = (Button) findViewById(R.id.btnSave);
        butSave.setOnClickListener(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //Recycler View Menu
                //Intent intent = new Intent(PersonalDetails.this, HomeScreen.class);
                //Grid View Menu
                Intent intent = new Intent(ProfileEdit.this, PersonalDetails.class);
                startActivity(intent);
                finish();
            }
        });
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
//        strSchool = loginsession.getString("school", "");
        displayProfile();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomePageGridViewLayout.class);
        startActivity(intent);
        this.finish();
    }
    private void displayProfile(){
        db = controllerdb.getReadableDatabase();
        try{
            Cursor cursor = db.rawQuery("SELECT strftime('%d-%m-%Y %H:%M:%S', lastupdatedate) as lastupdated,* FROM profiledetails pf WHERE employeeid=" + lngEmployeeId, null);
            if (cursor.moveToFirst()){
                do{
                    tvLastUpdated.setText("Last Updated: " + cursor.getString(cursor.getColumnIndex("lastupdated")));
                    tvEmployee = (TextInputEditText) findViewById(R.id.txtEmployeeName);
                    tvEmployee.setText(cursor.getString(cursor.getColumnIndex("employeename")));

                    tvQualification = (TextInputEditText) findViewById(R.id.txtQualification);
                    tvQualification.setText(cursor.getString(cursor.getColumnIndex("qualification")));

                    tvDivision = (TextInputEditText) findViewById(R.id.txtDivision);
                    tvDivision.setText(cursor.getString(cursor.getColumnIndex("division")));

                    tvDesignation = (TextInputEditText) findViewById(R.id.txtDesignation);
                    tvDesignation.setText(cursor.getString(cursor.getColumnIndex("designation")));

                    tvDob = (TextInputEditText) findViewById(R.id.txtDob);
                    tvDob.setText(cursor.getString(cursor.getColumnIndex("dob")));

                    tvDoj = (TextInputEditText) findViewById(R.id.txtDoj);
                    tvDoj.setText(cursor.getString(cursor.getColumnIndex("doj")));

                    tvMobile = (TextInputEditText) findViewById(R.id.txtMobile);
                    tvMobile.setText(cursor.getString(cursor.getColumnIndex("mobile")));

                    tvEmail = (TextInputEditText) findViewById(R.id.txtEmmail);
                    tvEmail.setText(cursor.getString(cursor.getColumnIndex("email")));

                    tvAddress = (TextView) findViewById(R.id.txtAddress);
                    tvAddress.setText(cursor.getString(cursor.getColumnIndex("address")));

                }while (cursor.moveToNext());

            } else {
                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                strParameters = strParameters;
                WebService.METHOD_NAME = "employeePersonalDetailsJson";
                ProfileEdit.AsyncCallWS task = new ProfileEdit.AsyncCallWS();
                task.execute();
            }
            cursor.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
            strParameters = strParameters;
            WebService.METHOD_NAME = "employeePersonalDetailsJson";
            ProfileEdit.AsyncCallWS task = new ProfileEdit.AsyncCallWS();
            task.execute();
        }
    }

//    public static byte[] getBytes(Bitmap bitmap) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
//        return stream.toByteArray();
//    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(ProfileEdit.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getResources().getString(R.string.loading));

            //show dialog
            dialog.show();
//            //Log.i(TAG, "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
//            //Log.i(TAG, "doInBackground");
            if (Debug.isDebuggerConnected())
                Debug.waitForDebugger();
            ResultString = WebService.invokeWS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
//            //Log.i(TAG, "onPostExecute");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
            SharedPreferences.Editor ed = loginsession.edit();
            try {
                SqlliteController sc = new SqlliteController(ProfileEdit.this);
                JSONObject object = new JSONObject(ResultString.toString());
                if (!object.isNull("Name")) {
                    sc.insertProfileDetails(lngEmployeeId, object.getString("Name"),
                            object.getString("Division"), object.getString("Designation"),
                            object.getString("DOB"), object.getString("DOJ"),
                            object.getString("Mobile"), object.getString("Email"),
                            object.getString("Qualification"), object.getString("Address"));

                    displayProfile();
                } else {
                    Toast.makeText(ProfileEdit.this, "No Personal Details Found", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                //Update List
                strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
                WebService.strParameters = strParameters;
                WebService.METHOD_NAME = "employeePersonalDetailsJson";
                ProfileEdit.AsyncCallWS task = new ProfileEdit.AsyncCallWS();
                task.execute();
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

    public void onClick(View v){
        hideKeyboard(ProfileEdit.this);
       // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        switch (v.getId()){

                case R.id.btnSave:
                    if (!CheckNetwork.isInternetAvailable(ProfileEdit.this)) {
                        Toast.makeText(ProfileEdit.this,getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        if (!Utility.isNotNull(tvQualification.getText().toString().trim())) {
                            tvQualification.setError("Qualification required");
                            tvQualification.requestFocus();
                            return;
                        } else {
                            tvQualification.setError(null);
                        }
                        if (!Utility.isNotNull(tvMobile.getText().toString().trim())) {
                            tvMobile.setError("Mobile Number required");
                            tvMobile.requestFocus();
                            return;
                        }
                        if (!Utility.isNotNull(tvEmail.getText().toString().trim())) {
                            tvEmail.setError("Email Address required");
                            return;
                        }
                        if (!Utility.isNotNull(tvAddress.getText().toString().trim())) {
                            tvAddress.setError("Address required");
                            return;
                        }
                        WebService.strParameters = new String[]{"Long", "employeeid", String.valueOf(String.valueOf(lngEmployeeId)),
                                "String", "mobileno", tvMobile.getText().toString().trim(),
                                "String", "address", tvAddress.getText().toString().trim(),
                                "String", "email", tvEmail.getText().toString().trim(),
                                "String", "qualification", tvQualification.getText().toString().trim(),
                        };

                        //WebService.strParameters = strParameters;
                        WebService.METHOD_NAME = "updateEmployeeProfile";
                        AsyncCallSaveWS task = new AsyncCallSaveWS();
                        task.execute();
                    }
                break;
        }
    }

    private class AsyncCallSaveWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(ProfileEdit.this);
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

            if (ResultString.toString().equals("")){
                Toast.makeText(ProfileEdit.this, "Data Not Saved", Toast.LENGTH_LONG).show();

            }else  {

                try {
                    JSONObject object = new JSONObject(ResultString.toString());
                    if(object.getString("Status").equalsIgnoreCase("Success")){
                        Toast.makeText(ProfileEdit.this, object.getString("Data"), Toast.LENGTH_LONG).show();
                        SqlliteController sc = new SqlliteController(ProfileEdit.this);
                        sc.insertProfileDetails(lngEmployeeId,
                                tvEmployee.getText().toString().trim(),
                                tvDivision.getText().toString().trim(), tvDesignation.getText().toString().trim(),
                                tvDob.getText().toString().trim(), tvDoj.getText().toString().trim(),
                                tvMobile.getText().toString().trim(), tvEmail.getText().toString().trim(),
                                tvQualification.getText().toString().trim(), tvAddress.getText().toString().trim());
                        Intent intent = new Intent(ProfileEdit.this, PersonalDetails.class);
                        startActivityForResult(intent, 2);
                        finish();
                    }else{
                        Toast.makeText(ProfileEdit.this, object.getString("Data"), Toast.LENGTH_LONG).show();

                    }

                    // clearForm((ViewGroup) findViewById(R.id.leaveentrylayout));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }
        }

    }

}