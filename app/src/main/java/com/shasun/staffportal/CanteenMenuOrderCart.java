//package com.shasun.staffportalshasun;
//    import androidx.appcompat.app.AppCompatActivity;
//    import android.content.Intent;
//    import android.os.Bundle;
//    import android.view.View;
//    import android.view.ViewGroup;
//    import android.widget.BaseAdapter;
//    import android.widget.Button;
//    import android.widget.ImageView;
//    import android.widget.ListView;
//    import android.widget.TextView;
//    import android.widget.Toast;
//
//public class CanteenMenuOrderCart extends AppCompatActivity {
//    ListView lstMenu;
//    TextView tvTotal;
//
//    String[] lMenu;
//    int[] lPrice;
//    int[] lQuantity;
//    int[] lMenuImagesIds;
//    int[] lPosition;
//    int lTotal=0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_second);
//
//        lMenu=getIntent().getStringArrayExtra("Menu");
//        lPrice=getIntent().getIntArrayExtra("Price");
//        lQuantity=getIntent().getIntArrayExtra("Quantity");
//        lMenuImagesIds=getIntent().getIntArrayExtra("MenuImages");
//        lPosition=getIntent().getIntArrayExtra("ItemPosition");
//        lTotal=getIntent().getIntExtra("Total",0);
//
//        lstMenu=(ListView) findViewById(R.id.lstmenu1);
//        CustomAdapter1 customAdapter=new CustomAdapter1();
//        lstMenu.setAdapter(customAdapter);
//
//        tvTotal=(TextView) findViewById(R.id.secondTotal);
//        tvTotal.setText("\u20B9"+" "+ Integer.toString(lTotal));
//
//        Button btnBack=(Button) findViewById(R.id.btnBack);
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onCustomBackPressed();
//            }});
//
//        Button btnPlaceOrder=(Button) findViewById(R.id.btnPlaceOrder);
//        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Your Order Placed !!!", Toast.LENGTH_SHORT).show();
//            }});
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        onCustomBackPressed();
//    }
//
//    public void onCustomBackPressed(){
//        Intent intent=new Intent(SecondActivity.this,MainActivity.class);
//        intent.putExtra("ItemPosition", lPosition);
//        intent.putExtra("Quantity",lQuantity);
//        //intent.putExtra("ItemTotal",lItemTotal);
//        intent.putExtra("Total",lTotal);
//        intent.putExtra("ItemListCount",lQuantity.length);
//        startActivity(intent);
//    }
//
//    class CustomAdapter1 extends BaseAdapter {
//        @Override
//        public int getCount() {
//            return lMenu.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            convertView=getLayoutInflater().inflate(R.layout.layout,null);
//            final TextView txtQuantity=(TextView) convertView.findViewById(R.id.txtquantity);
//            final TextView txtMenu=(TextView) convertView.findViewById(R.id.txtmenu);
//            final TextView txtPrice=(TextView) convertView.findViewById(R.id.txtprice);
//            final Button btnPlus=(Button) convertView.findViewById(R.id.btnplus);
//            final Button btnMinus=(Button) convertView.findViewById(R.id.btnminus);
//            final ImageView imgMenuImage=(ImageView) convertView.findViewById(R.id.imgMenuImage);
//
//            btnPlus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String strQuantity=txtQuantity.getText().toString();
//                    int tempQuantity=Integer.parseInt(strQuantity);
//                    int tempPrice=lPrice[position];
//                    ++tempQuantity;
//                    //Toast.makeText(getApplicationContext(), strQuantity, Toast.LENGTH_SHORT).show();
//                    txtQuantity.setText(String.valueOf(tempQuantity));
//                    lQuantity[position]=tempQuantity;
//                    //lItemTotal[position]=tempQuantity*tempPrice;
//                    getTotal();
//                    if(tempQuantity==0)
//                        btnMinus.setEnabled(false);
//                    else
//                        btnMinus.setEnabled(true);
//                }
//            });
//
//            btnMinus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String strQuantity1=txtQuantity.getText().toString();
//                    int tempQuantity1=Integer.parseInt(strQuantity1);
//                    int tempPrice1=lPrice[position];
//                    if(tempQuantity1 >0){
//                        tempQuantity1--;
//                        Toast.makeText(getApplicationContext(), strQuantity1, Toast.LENGTH_SHORT).show();
//                        txtQuantity.setText(String.valueOf(tempQuantity1));
//                        lQuantity[position]=tempQuantity1;
//                        //lItemTotal[position]=tempQuantity1*tempPrice1;
//                        getTotal();
//                        if(tempQuantity1==0)
//                            btnMinus.setEnabled(false);
//                        else
//                            btnMinus.setEnabled(true);
//                    }
//                }
//            });
//
//            txtMenu.setText(lMenu[position]);
//            txtPrice.setText(Integer.toString(lPrice[position]));
//            txtQuantity.setText(Integer.toString(lQuantity[position]));
//            imgMenuImage.setImageResource(lMenuImagesIds[position]);
//            return convertView;
//        }
//
//
//        public void getTotal(){
//            lTotal=0;
//            for(int i=0; i<lMenu.length; i++) {
//                int tempTotal=lQuantity[i]*lPrice[i];
//                lTotal = lTotal + tempTotal;
//            }
//            tvTotal.setText("Total Amount : "+"\u20B9"+" "+ Integer.toString(lTotal));
//        }
//    }
//}
