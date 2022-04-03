package com.shasun.staffportal;

    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import androidx.core.content.ContextCompat;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;
    import org.json.JSONArray;
    import org.json.JSONObject;
    import java.util.ArrayList;
    import webservice.WebService;

public class InternalMarkEntryMain extends AppCompatActivity {
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    private TextView tvPageTitle;
    private static String strParameters[];
    private static String ResultString = "";
    private long lngEmployeeId=0;
    private ArrayList<String> breakup_list = new ArrayList<String>(200);
    private int intFlag = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internalmarkentrymain);
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Internal Mark Entry");
        intFlag=getIntent().getIntExtra("Flag",1);
        Button btnBack=(Button) findViewById(R.id.button_back);
//        tvLastUpdated = (TextView) findViewById(R.id.txtLastUpdated);
//        tvLastUpdated.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            Intent intent;
            if (intFlag == 1){
                intent = new Intent(InternalMarkEntryMain.this, HomeScreenCategory.class);
                startActivity(intent);
            }
            if (intFlag == 2){
                intent = new Intent(InternalMarkEntryMain.this, HomePageGridViewLayout.class);
                startActivity(intent);
            }
            }
        });
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        final SharedPreferences loginsession = getApplicationContext().getSharedPreferences("SessionLogin", 0);
        lngEmployeeId = loginsession.getLong("userid", 1);
        strParameters = new String[]{"Long", "employeeid", String.valueOf(lngEmployeeId)};
        WebService.strParameters = strParameters;
        WebService.METHOD_NAME = "getTestcomponentJson";
        AsyncCallWS task = new AsyncCallWS();
        task.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(InternalMarkEntryMain.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(InternalMarkEntryMain.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog = new ProgressDialog(InternalMarkEntryMain.this);

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
            try {
                JSONArray temp = new JSONArray(ResultString.toString());
                for (int i = 0; i <= temp.length() - 1; i++) {
                    JSONObject object = new JSONObject(temp.getJSONObject(i).toString());
                    breakup_list.add(object.getString("breakupid") + "##" +object.getString("breakupdesc"));
                }
                if (breakup_list.size() == 0){
                    Toast.makeText(InternalMarkEntryMain.this, "Response: No Data Found", Toast.LENGTH_LONG).show();
                } else {
                    mRecyclerView = (RecyclerView) findViewById(R.id.rvTestComponent); // Assigning the RecyclerView Object to the xml View
                    mRecyclerView.setHasFixedSize(true);
                    // Letting the system know that the list objects are of fixed size
                    InternalMarkEntryLVAdapter TVA = new InternalMarkEntryLVAdapter(breakup_list, R.layout.internalmarkentrymainlistitem);
                    mRecyclerView.setAdapter(TVA);
                    mLayoutManager = new LinearLayoutManager(InternalMarkEntryMain.this);                 // Creating a layout Manager
                    mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
