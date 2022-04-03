package com.shasun.staffportal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shasun.staffportal.properties.Properties;

import java.util.ArrayList;

public class StudentAttendanceTemplateLVAdapter extends RecyclerView.Adapter<StudentAttendanceTemplateLVAdapter.ViewHolder> {

    private static ArrayList<String> template_list=new ArrayList<String>();
    private int itemLayout;

    public StudentAttendanceTemplateLVAdapter(ArrayList<String> template_list, int itemLayout){
        this.template_list = template_list;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String item = template_list.get(position);
        String[] strColumns = item.split("##");
        holder.textTemplateId.setText(strColumns[0]);
        holder.textTemplateDesc.setText(strColumns[2]);
        holder.textTemplateId.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount(){
        return template_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTemplateDesc;
        private TextView textTemplateId;

        public ViewHolder(View itemView) {
            super(itemView);
            textTemplateDesc = (TextView) itemView.findViewById(R.id.txtTemplateDesc);
            textTemplateId = (TextView) itemView.findViewById(R.id.txtTemplateId);
            final SharedPreferences loginsession = itemView.getContext().getSharedPreferences("SessionLogin", 0);
            final int intMenuFlag = loginsession.getInt("menuflag", 1);
            this.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        String item = template_list.get(getPosition());
                        String[] strColumns = item.split("##");
                        int intTemplateId = Integer.parseInt(strColumns[0]);
                        int intOfficeId = Integer.parseInt(strColumns[1]);
                        String pageTitle = strColumns[2];
                        Context context = v.getContext();
                        Intent intent;
                        if (intMenuFlag == 1) {
                            intent = new Intent(context, StaffTimeTable.class);
                        } else {
                            intent = new Intent(context, StudentAttendanceHourCourses.class);
                        }
                        intent.putExtra(Properties.timeTableHeader, pageTitle);
                        intent.putExtra("templateid", intTemplateId);
                        intent.putExtra("officeid", intOfficeId);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}

