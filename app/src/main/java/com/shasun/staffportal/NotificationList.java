package com.shasun.staffportal;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NotificationList extends AppCompatActivity implements View.OnClickListener {
    Button btnSent, btnReceived;
    TextView tvPageTitle;
    private int intFlag = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notificationviewlist);
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Notification List");
        intFlag=getIntent().getIntExtra("Flag",1);

        Button btnBack=(Button) findViewById(R.id.button_back);
        Button btnRefresh=(Button) findViewById(R.id.button_refresh);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            Intent intent;
            if (intFlag == 1){
                intent = new Intent(NotificationList.this, HomeScreenCategory.class);
                startActivity(intent);
            }
            if (intFlag == 2){
                intent = new Intent(NotificationList.this, HomePageGridViewLayout.class);
                startActivity(intent);
            }
            }
        });

        btnSent = (Button) findViewById(R.id.btnNotificationSent);
        btnReceived = (Button) findViewById(R.id.btnNotificationRece);
        btnSent.setOnClickListener(this);
        btnReceived.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnNotificationSent:
                Intent intent = new Intent(NotificationList.this, NotificationView.class);
                intent.putExtra("sentreceivedflag","1");
                startActivity(intent);
                break;
            case R.id.btnNotificationRece:
                intent = new Intent(NotificationList.this, NotificationView.class);
                intent.putExtra("sentreceivedflag","2");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent;
        if (intFlag == 1){
            intent = new Intent(NotificationList.this, HomeScreenCategory.class);
            startActivity(intent);
        }
        if (intFlag == 2){
            intent = new Intent(NotificationList.this, HomePageGridViewLayout.class);
            startActivity(intent);
        }
        this.finish();
    }
}
