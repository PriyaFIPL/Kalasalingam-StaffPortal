//package com.shasun.staffportalshasun;
//
//
//    import androidx.appcompat.app.AppCompatActivity;
//
//    import android.content.ClipData;
//    import android.content.Intent;
//    import android.os.Bundle;
//    import android.util.Log;
//    import android.view.View;
//    import android.view.ViewGroup;
//    import android.widget.BaseAdapter;
//    import android.widget.Button;
//    import android.widget.EditText;
//    import android.widget.ImageView;
//    import android.widget.ListView;
//    import android.widget.TextView;
//    import android.widget.Toast;
//
//    import com.google.android.material.floatingactionbutton.FloatingActionButton;
//
//public class CanteenMenuOrder extends AppCompatActivity {
//    ListView lstMenu;
//    TextView tvTotal;
//    String[] Menu={"Adai","Dosa","Idly","Meduvada","Upma"};
//    int[] MenuImagesIds={R.drawable.adai_final,R.drawable.dosa_final,R.drawable.idly_final,R.drawable.meduvada_final,R.drawable.upma_final};
//    int[] Price={60,50,10,10,30};
//    int[] Quantity={0,0,0,0,0};
//    //int[] ItemTotal={0,0,0,0,0};
//    int Total=0;
//    int QuantityCount=0;
//
//    String[] MenuTemp;
//    int[] PositionTemp;
//    int[] PriceTemp;
//    int[] QuantityTemp;
//    int[] MenuImagesIdsTemp;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        tvTotal=(TextView) findViewById(R.id.lTotal);
//        //Intent from Second Activity Process
//        try{
//            Intent intentFromSecondActivity=getIntent();
//            if(intentFromSecondActivity.getIntExtra("ItemListCount",0) > 0 ){
//                Total=intentFromSecondActivity.getIntExtra("Total",0);
//                QuantityCount=intentFromSecondActivity.getIntExtra("ItemListCount",0);
//                int[] lItemPosition=getIntent().getIntArrayExtra("ItemPosition");
//                int[] lQuantity=getIntent().getIntArrayExtra("Quantity");
//                //int[] lItemTotal=getIntent().getIntArrayExtra("ItemTotal");
//                for(int i=0;i<QuantityCount;i++){
//                    int tempPosition=lItemPosition[i];
//                    Quantity[tempPosition]=lQuantity[i];
//                    //ItemTotal[tempPosition]=lItemPosition[i];
//                }
//
//                tvTotal.setText("\u20B9"+" "+ Integer.toString(Total));
//            }
//        }catch (Exception e){}
//
//        //List View loading Process
//        lstMenu=(ListView) findViewById(R.id.lstmenu);
//        CustomAdapter customAdapter=new CustomAdapter();
//        lstMenu.setAdapter(customAdapter);
//        //Floating Action Button Process
//        FloatingActionButton btnMycart=(FloatingActionButton) findViewById(R.id.btnmycart);
//        btnMycart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Total>0){
//                    assignSelectedItemsForIntentProcess();
//                    Intent intent=new Intent(MainActivity.this,SecondActivity.class);
//                    intent.putExtra("Menu", MenuTemp);
//                    intent.putExtra("Price",PriceTemp);
//                    intent.putExtra("Quantity",QuantityTemp);
//                    intent.putExtra("MenuImages",MenuImagesIdsTemp);
//                    intent.putExtra("Total",Total);
//                    intent.putExtra("ItemPosition",PositionTemp);
//                    startActivity(intent);
//                }
//                else
//                    Toast.makeText(getApplicationContext(), "Menu not selected", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    public void getItemSelectedCount(){
//        QuantityCount=0;
//        for(int i=0; i<Menu.length;i++){
//            if(Quantity[i]>0)
//                QuantityCount++;
//        }
//    }
//
//    public void assignSelectedItemsForIntentProcess()
//    {
//        getItemSelectedCount();
//        PositionTemp=new int[QuantityCount];
//        MenuTemp=new String[QuantityCount];
//        PriceTemp=new int[QuantityCount];
//        QuantityTemp=new int[QuantityCount];
//        MenuImagesIdsTemp=new int[QuantityCount];
//
//        for(int i=0,j=0;i<Menu.length;i++){
//            if(Quantity[i]>0){
//                PositionTemp[j]=i;
//                MenuTemp[j]=Menu[i];
//                PriceTemp[j]=Price[i];
//                QuantityTemp[j]=Quantity[i];
//                MenuImagesIdsTemp[j]=MenuImagesIds[i];
//                j++;
//            }
//        }
//
//    }
//
//    class CustomAdapter extends BaseAdapter {
//        @Override
//        public int getCount() {
//            return Menu.length;
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
//            final TextView txtprice=(TextView) convertView.findViewById(R.id.txtprice);
//            final Button btnPlus=(Button) convertView.findViewById(R.id.btnplus);
//            final Button btnMinus=(Button) convertView.findViewById(R.id.btnminus);
//            final ImageView imgMenuImage=(ImageView) convertView.findViewById(R.id.imgMenuImage);
//
//            btnPlus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String strQuantity=txtQuantity.getText().toString();
//                    int tempQuantity=Integer.parseInt(strQuantity);
//                    int tempPrice=Price[position];
//                    ++tempQuantity;
//                    txtQuantity.setText(String.valueOf(tempQuantity));
//                    Quantity[position]=tempQuantity;
//                    //ItemTotal[position]=tempQuantity*tempPrice;
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
//                    int tempPrice1=Price[position];
//                    if(tempQuantity1 >0){
//                        tempQuantity1--;
//                        txtQuantity.setText(String.valueOf(tempQuantity1));
//                        Quantity[position]=tempQuantity1;
//                        //ItemTotal[position]=tempQuantity1*tempPrice1;
//                        getTotal();
//                        if(tempQuantity1==0)
//                            btnMinus.setEnabled(false);
//                        else
//                            btnMinus.setEnabled(true);
//                    }
//                }
//            });
//
//            txtMenu.setText(Menu[position]);
//            txtQuantity.setText(Integer.toString(Quantity[position]));
//            txtprice.setText(Integer.toString(Price[position]));
//            imgMenuImage.setImageResource(MenuImagesIds[position]);
//            return convertView;
//        }
//
//        public void getTotal(){
//            Total=0;
//            String strtemp="";
//            for(int i=0; i<Menu.length; i++) {
//                int tempTotal=Quantity[i]*Price[i];
//                Total = Total + tempTotal;
//                //strtemp=strtemp+","+Integer.toString(ItemTotal[i]);
//            }
//            //Log.i("info",strtemp);
//            tvTotal.setText("\u20B9"+" "+ Integer.toString(Total));
//        }
//    }
//}
