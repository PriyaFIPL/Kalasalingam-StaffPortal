package com.shasun.staffportal;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shasun.staffportal.properties.Properties;

import java.util.ArrayList;

public class StudentAttendanceHourCoursesLVAdapter extends RecyclerView.Adapter<StudentAttendanceHourCoursesLVAdapter.ViewHolder> {
    private static ArrayList<String> sub_list=new ArrayList<String>();
    private int itemLayout;

    public StudentAttendanceHourCoursesLVAdapter(ArrayList<String> sub_list, int itemLayout){
        this.sub_list = sub_list;
        this.itemLayout = itemLayout;
    }

    @Override
    public StudentAttendanceHourCoursesLVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new StudentAttendanceHourCoursesLVAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(StudentAttendanceHourCoursesLVAdapter.ViewHolder holder, int position){
        String item = sub_list.get(position);
        String[] strColumns = item.split("##");
        holder.txtIds.setText(strColumns[0]+"$$"+strColumns[6]+"$$"+strColumns[7]+"$$"+strColumns[8]+"$$"+strColumns[9]+"$$"+strColumns[4]);
        //subid "$$" progsectionid "$$" delegateempid "$$" attendancetransid "$$" dayorderid "$$" hourid

        holder.txtSubject.setText(strColumns[1] +"-"+ strColumns[2]);
        holder.txtProgSection.setText(strColumns[5]);
        holder.txtDayorderHour.setText("Day Order:" + strColumns[3] + "   Hour: "+ strColumns[4]);
        holder.txtIds.setVisibility(View.INVISIBLE);

        if (Integer.parseInt(strColumns[8].trim()) > 0){
            holder.llList.setBackgroundResource(R.drawable.btnselector_revarse_green);
           // holder.txtSubject.setBackgroundColor(Color.parseColor("#567845"));
            //holder.txtProgSection.setBackgroundColor(Color.parseColor("#567845"));
            //holder.txtDayorderHour.setBackgroundColor(Color.parseColor("#567845"));

            holder.txtSubject.setTextColor(Color.parseColor("#ffffff"));
            holder.txtProgSection.setTextColor(Color.parseColor("#ffffff"));
            holder.txtDayorderHour.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return sub_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtSubject;
        private TextView txtProgSection;
        private TextView txtDayorderHour;
        private TextView txtIds;
        private LinearLayout llList;


        public ViewHolder(View itemView){
            super(itemView);
            llList = (LinearLayout) itemView.findViewById(R.id.llList);
            txtSubject = (TextView) itemView.findViewById(R.id.txtSubject);
            txtProgSection = (TextView) itemView.findViewById(R.id.txtProgSection);
            txtDayorderHour = (TextView) itemView.findViewById(R.id.txtDayorderHour);
            txtIds = (TextView) itemView.findViewById(R.id.txtIds);
            this.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(),"You dont have Internet connection", Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        Context context = v.getContext();
                        String item = sub_list.get(getPosition());
                        String[] strColumns = item.split("##");
                        long lngSubId = Long.parseLong(strColumns[0]);
                        long lngProgSecId = Long.parseLong(strColumns[6]);
                        long lngAttenTransId = Long.parseLong(strColumns[8]);
                        if (lngAttenTransId == 0) { //strColumns[8] contains attendance transaction id
                            Intent intent = new Intent(context, StudentAttendanceStudentList.class);
                            intent.putExtra("subid", lngSubId);
                            intent.putExtra("progsecid", lngProgSecId);
                            intent.putExtra("ids", txtIds.getText());
                            intent.putExtra(Properties.attendanceHeader1, strColumns[5]);
                            intent.putExtra(Properties.attendanceHeader2, txtDayorderHour.getText().toString());
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Response: Already Mark entered", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, AttendanceMarkedStudentList.class);
                            intent.putExtra("attentransid", lngAttenTransId);
                            intent.putExtra(Properties.attendanceHeader1, strColumns[5]);
                            intent.putExtra(Properties.attendanceHeader2, txtDayorderHour.getText().toString());
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}
