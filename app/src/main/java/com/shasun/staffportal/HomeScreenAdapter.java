package com.shasun.staffportal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import webservice.SqlliteController;

import static android.content.Context.MODE_PRIVATE;

public class HomeScreenAdapter extends RecyclerView.Adapter<HomeScreenAdapter.ViewHolder> {
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private static String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
    private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;
        TextView tvMenuContent;
        ImageView imageView;

        public ViewHolder(View itemView, int ViewType){                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            tvMenuContent = (TextView) itemView.findViewById(R.id.txtMenuContent); // Creating TextView object with the id of textView from item_row.xml
            imageView = (ImageView) itemView.findViewById(R.id.imgMenuImage);// Creating ImageView object with the id of ImageView from item_row.xml
            Holderid = 1;
            this.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                String item = mNavTitles[getPosition()]; //leavepending_list.get(getPosition());
                String[] strColumns = item.split("##");
                int intMenuId=Integer.parseInt(strColumns[1]);
                  //  Log.e("TEst Notification : ",""+intMenuId);
                if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                    Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                    return;
                }
                switch (intMenuId){
                    case 1:
                        Intent intent = new Intent(v.getContext(), PersonalDetails.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 2:
                        SharedPreferences loginsession = v.getContext().getSharedPreferences("SessionLogin", 0);
                        SharedPreferences.Editor ed = loginsession.edit();
                        ed.putInt("menuflag", 1);
                        ed.commit();
                        intent = new Intent(v.getContext(), StudentAttendanceTemplate.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(v.getContext(), LeaveStatus.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(v.getContext(), LeaveEntry.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(v.getContext(), LeaveApproval.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 6:
                        loginsession = v.getContext().getSharedPreferences("SessionLogin", 0);
                        ed = loginsession.edit();
                        ed.putInt("menuflag", 2);
                        ed.commit();
                        intent = new Intent(v.getContext(), StudentAttendanceTemplate.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 7:
                        intent = new Intent(v.getContext(), InternalMarkEntryMain.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 8:
                        intent = new Intent(v.getContext(), StaffBiometricLog.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 9:
                        intent = new Intent(v.getContext(), ViewPaySlipPayPeriod.class);
                        v.getContext().startActivity(intent);
                        break;
                    case 10:
                        intent = new Intent(v.getContext(), NotificationForStudentsSubjectList.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 11:
                        intent = new Intent(v.getContext(), NotificationStaffCategory.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
//                    case 12:
//                        intent = new Intent(v.getContext(), NotificationForStudentsSubjectList.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        v.getContext().startActivity(intent);
//                        break;
                    case 13:
                        intent = new Intent(v.getContext(), NotificationList.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 14:
                        intent = new Intent(v.getContext(), CanteenSelection.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 15:
                        intent = new Intent(v.getContext(), VenueBooking.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 16:
                        intent = new Intent(v.getContext(), ENoticeView.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 17:
                        intent = new Intent(v.getContext(), ManagementDashboard.class); //ManagementDashboard
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 18:
                        intent = new Intent(v.getContext(), FacultyDashboard.class);  //FacultyDashboard
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 19:
                        intent = new Intent(v.getContext(), LMSIndex.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 20:
                        intent = new Intent(v.getContext(), PermissionEntry.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    case 50:
                        SharedPreferences myPrefs = v.getContext().getSharedPreferences("SessionLogin", MODE_PRIVATE);
                        SharedPreferences.Editor editor = myPrefs.edit();
                        editor.clear();
                        editor.commit();
                        SqlliteController sc = new SqlliteController(v.getContext());
                        sc.deleteLoginStaffDetails();
                        intent = new Intent(v.getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        v.getContext().startActivity(intent);
                        break;
                    default:
                        break;
                    }
                };
            });
        }
    }

    HomeScreenAdapter(String Titles[],int Icons[]){ // HomeScreenAdapter Constructor with titles and icons parameter
        // titles, icons are passed from the main activity as we
        mNavTitles = Titles;
        mIcons = Icons;
    }

    @Override
    public HomeScreenAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.homescreenlistitems,parent,false); //Inflating the layout
        ViewHolder vhItem = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view
        return vhItem; // Returning the created object
    }

    @Override
    public void onBindViewHolder(HomeScreenAdapter.ViewHolder holder, int position) {
        try {
            if (holder.Holderid == 1){                              // as the list view is going to be called after the header view so we decrement the
                // position by 1 and pass it to the holder while setting the text and image
                String item = mNavTitles[position]; //leavepending_list.get(getPosition());
                String[] strColumns = item.split("##");
                holder.tvMenuContent.setText(strColumns[0].trim()); // Setting the Text with the array of our Titles
                holder.imageView.setImageResource(mIcons[position]);// Setting the image with array of our icons
                holder.tvMenuContent.setTextColor(Color.BLACK);
            }
        }catch(Exception e){}
    }

    @Override
    public int getItemCount() {

        return mNavTitles.length; // the number of items in the list will be +1 the titles including the header view.
    }

    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
}
