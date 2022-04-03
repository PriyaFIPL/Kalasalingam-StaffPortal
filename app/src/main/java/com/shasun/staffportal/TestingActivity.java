package com.shasun.staffportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TestingActivity extends AppCompatActivity {
    int[] MenuId;
    String[] Menu;
    int[] MenuImagesIds;
    String[] MenuImages;
    float[] Price;
    int[] Quantity;

    ListView lstMenu;
    float Total = 0;
    TextView tvTotal;
    TextView tvPageTitle;

    int[] MenuIdTemp;
    String[] MenuTemp;
    int[] PositionTemp;
    float[] PriceTemp;
    int[] QuantityTemp;
    int[] MenuImagesIdsTemp;
    String[] MenuImagesTemp;


    int QuantityCount=0;
    ArrayList<String> alMenuItems = new ArrayList<String>();
    int intCanteenId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        tvPageTitle = (TextView) findViewById(R.id.pageTitle);
        tvPageTitle.setText("Menu List");
        Button btnBack=(Button) findViewById(R.id.button_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(TestingActivity.this, HomeScreenCategory.class);
                startActivity(intent);
            }
        });
        StatusColor.SetStatusColor(getWindow(), ContextCompat.getColor(this, R.color.colorblue));
        tvTotal = (TextView) findViewById(R.id.lTotal);
        intCanteenId= getIntent().getIntExtra("CanteenId",0);
        alMenuItems = getIntent().getExtras().getStringArrayList("CanteenItems");
        String listString = "";
        if (alMenuItems.size() > 0) {
            MenuId = new int[alMenuItems.size()];
            Menu = new String[alMenuItems.size()];
            //MenuImagesIds = new int[alMenuItems.size()];
            Price = new float[alMenuItems.size()];
            Quantity = new int[alMenuItems.size()];
            MenuImages = new String[alMenuItems.size()];

            try {
                int i = 0;
                for (String tempMenulist : alMenuItems) {
                    String[] tempMenuItems = tempMenulist.split("##");
                    MenuId[i] = Integer.parseInt(tempMenuItems[0]);
                    Menu[i] = tempMenuItems[1].toString();
                    //MenuImagesIds[i] = R.drawable.noimage;
                    Price[i] = Float.parseFloat(tempMenuItems[2]);
                    Quantity[i] = 0;
                    MenuImages[i]="http://erp.shasuncollege.edu.in/evarsityshasun/resources/Image/I"+tempMenuItems[0]+".jpg";
                    //Quantity[i]=Integer.parseInt(tempMenuItems[3]);
                    i++;
                }
                //Toast.makeText(TestingActivity.this, listString, Toast.LENGTH_LONG).show();
                //Log.i("Received String", listString);
                Log.println(Log.ERROR, "Received String", listString);
            } catch (Exception e) {
            }

            //Intent from Second Activity Process
            try{
                Intent intentFromSecondActivity=getIntent();
                if(intentFromSecondActivity.getIntExtra("ItemListCount",0) > 0 ){
                    Total=intentFromSecondActivity.getIntExtra("Total",0);
                    QuantityCount=intentFromSecondActivity.getIntExtra("ItemListCount",0);
                    int[] lItemPosition=getIntent().getIntArrayExtra("ItemPosition");
                    int[] lQuantity=getIntent().getIntArrayExtra("Quantity");
                    //int[] lItemTotal=getIntent().getIntArrayExtra("ItemTotal");
                    for(int i=0;i<QuantityCount;i++){
                        int tempPosition=lItemPosition[i];
                        Quantity[tempPosition]=lQuantity[i];
                        //ItemTotal[tempPosition]=lItemPosition[i];
                    }
                    getTotal();
                    //tvTotal.setText("\u20B9" + " " + String.format("%.02f", Total));
                }
            }catch (Exception e){}
            //List View loading Process


            lstMenu = (ListView) findViewById(R.id.lstmenu);
            CustomAdapter customAdapter = new CustomAdapter();
            lstMenu.setAdapter(customAdapter);

            //Floating Action Button Process
            FloatingActionButton btnMycart=(FloatingActionButton) findViewById(R.id.btnmycart);
            btnMycart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    } else {

                        if (Total > 0) {

                            assignSelectedItemsForIntentProcess();
                            Intent intent = new Intent(TestingActivity.this, MenuOrderPlacing.class);
                            intent.putExtra("CanteenId", intCanteenId);
                            intent.putExtra("MenuId", MenuIdTemp);
                            intent.putExtra("Menu", MenuTemp);
                            intent.putExtra("Price", PriceTemp);
                            intent.putExtra("Quantity", QuantityTemp);
                            //intent.putExtra("MenuImages",MenuImagesIdsTemp);
                            intent.putExtra("MenuImage", MenuImagesTemp);
                            intent.putExtra("Total", Total);
                            intent.putExtra("ItemPosition", PositionTemp);
                            intent.putStringArrayListExtra("CanteenItems", alMenuItems);
                            startActivity(intent);
                        }
                    else
                        Toast.makeText(getApplicationContext(), "Menu not selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void getItemSelectedCount(){
        QuantityCount=0;
        for(int i=0; i<Menu.length;i++){
            if(Quantity[i]>0)
                QuantityCount++;
        }
    }

    public void getTotal() {
        Total = 0;
        String strtemp = "";
        for (int i = 0; i < Menu.length; i++) {
            float tempTotal = Quantity[i] * Price[i];
            Total = Total + tempTotal;
        }
        tvTotal.setText("\u20B9" + " " + String.format("%.02f", Total));
    }

    public void assignSelectedItemsForIntentProcess()
    {
        getItemSelectedCount();
        PositionTemp=new int[QuantityCount];
        MenuIdTemp=new int[QuantityCount];
        MenuTemp=new String[QuantityCount];
        PriceTemp=new float[QuantityCount];
        QuantityTemp=new int[QuantityCount];
        MenuImagesTemp=new String[QuantityCount];

        for(int i=0,j=0;i<Menu.length;i++){
            if(Quantity[i]>0){
                PositionTemp[j]=i;
                MenuIdTemp[j]=MenuId[i];
                MenuTemp[j]=Menu[i];
                PriceTemp[j]=Price[i];
                QuantityTemp[j]=Quantity[i];
                //MenuImagesIdsTemp[j]=MenuImagesIds[i];
                MenuImagesTemp[j]=MenuImages[i];
                j++;
            }
        }
    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return Menu.length;
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
            convertView = getLayoutInflater().inflate(R.layout.menu_layout, null);
            final TextView txtQuantity = (TextView) convertView.findViewById(R.id.txtquantity);
            final TextView txtMenu = (TextView) convertView.findViewById(R.id.txtmenu);
            final TextView txtPrice = (TextView) convertView.findViewById(R.id.txtprice);
            final Button btnPlus = (Button) convertView.findViewById(R.id.btnplus);
            final Button btnMinus = (Button) convertView.findViewById(R.id.btnminus);
            final ImageView imgMenuImage = (ImageView) convertView.findViewById(R.id.imgMenuImage);

            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strQuantity = txtQuantity.getText().toString();
                    int tempQuantity = Integer.parseInt(strQuantity);
                    float tempPrice = Price[position];
                    ++tempQuantity;
                    txtQuantity.setText(String.valueOf(tempQuantity));
                    Quantity[position] = tempQuantity;
                    //ItemTotal[position]=tempQuantity*tempPrice;
                    getTotal();
                    if (tempQuantity == 0)
                        btnMinus.setEnabled(false);
                    else
                        btnMinus.setEnabled(true);
                }
            });
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strQuantity1 = txtQuantity.getText().toString();
                    int tempQuantity1 = Integer.parseInt(strQuantity1);
                    float tempPrice1 = Price[position];
                    if (tempQuantity1 > 0) {
                        tempQuantity1--;
                        txtQuantity.setText(String.valueOf(tempQuantity1));
                        Quantity[position] = tempQuantity1;
                        //ItemTotal[position]=tempQuantity1*tempPrice1;
                        getTotal();
                        if (tempQuantity1 == 0)
                            btnMinus.setEnabled(false);
                        else
                            btnMinus.setEnabled(true);
                    }
                }
            });
            txtMenu.setText(Menu[position]);
            txtQuantity.setText(Integer.toString(Quantity[position]));
//            txtPrice.setText(Float.toString(Price[position]));
            txtPrice.setText(String.format("%.02f", Price[position]));
            //imgMenuImage.setImageResource(MenuImagesIds[position]);
            Picasso.with(TestingActivity.this).load(MenuImages[position]).into(imgMenuImage);
           // Picasso.get().load(MenuImages[position]).into(imgMenuImage);

            return convertView;
        }
//        public void getTotal() {
////            Total = 0;
////            String strtemp = "";
////            for (int i = 0; i < Menu.length; i++) {
////                float tempTotal = Quantity[i] * Price[i];
////                Total = Total + tempTotal;
////                //strtemp=strtemp+","+Integer.toString(ItemTotal[i]);
////            }
////            //Log.i("info",strtemp);
////            tvTotal.setText("\u20B9" + " " + String.format("%.02f", Total));
////        }
    }
}
