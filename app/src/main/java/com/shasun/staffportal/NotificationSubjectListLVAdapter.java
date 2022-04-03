package com.shasun.staffportal;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationSubjectListLVAdapter extends RecyclerView.Adapter<NotificationSubjectListLVAdapter.ViewHolder> {

    private static ArrayList<String> sub_list=new ArrayList<String>();
    private int itemLayout;

    public NotificationSubjectListLVAdapter(ArrayList<String> sub_list, int itemLayout){
        this.sub_list = sub_list;
        this.itemLayout = itemLayout;
    }

    @Override
    public NotificationSubjectListLVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new NotificationSubjectListLVAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationSubjectListLVAdapter.ViewHolder holder, int position){
        if (position % 2 != 0){
            holder.llist.setBackgroundResource(R.drawable.btnselector_revarse);
        }else{
            holder.llist.setBackgroundResource(R.drawable.btnselector_revarse_pink);
        }  String item = sub_list.get(position);
        String[] strColumns = item.split("##");
        holder.txtIds.setText(strColumns[0]);
        holder.txtSubject.setText(strColumns[1] +"-"+ strColumns[2]);
        //holder.txtProgSection.setText(strColumns[5]);
        //holder.txtDayorderHour.setText("Day Order:" + strColumns[3] + "   Hour: "+ strColumns[4]);
        holder.txtIds.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return sub_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtSubject;
        private TextView txtIds;
        private LinearLayout llist;

        public ViewHolder(View itemView){
            super(itemView);
            txtSubject = (TextView) itemView.findViewById(R.id.txtSubject);
            txtIds = (TextView) itemView.findViewById(R.id.txtIds);
            llist = (LinearLayout) itemView.findViewById(R.id.llist);
            this.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                Context context = v.getContext();
                String item = sub_list.get(getPosition());
                String[] strColumns = item.split("##");
                long lngSubId=Long.parseLong(strColumns[0]);
                Intent intent = new Intent(context, NotificationForStudentsSectionList.class);
                intent.putExtra("subjectid", lngSubId);
                context.startActivity(intent);
                }
            });
        }
    }
}
