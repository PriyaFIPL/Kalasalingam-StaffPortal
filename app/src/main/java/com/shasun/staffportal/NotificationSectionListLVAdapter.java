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

    public class NotificationSectionListLVAdapter extends RecyclerView.Adapter<NotificationSectionListLVAdapter.ViewHolder>{

        private static ArrayList<String> section_list=new ArrayList<String>();
        private int itemLayout;

        public NotificationSectionListLVAdapter(ArrayList<String> section_list, int itemLayout){
            this.section_list = section_list;
            this.itemLayout = itemLayout;
        }

        @Override
        public NotificationSectionListLVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
            return new NotificationSectionListLVAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(NotificationSectionListLVAdapter.ViewHolder holder, int position){
            if (position % 2 != 0){
                holder.llist.setBackgroundResource(R.drawable.btnselector_revarse);
            }else{
                holder.llist.setBackgroundResource(R.drawable.btnselector_revarse_pink);
            }

            String item = section_list.get(position);
            String[] strColumns = item.split("##");
            holder.txtId.setText(strColumns[1]);
            holder.txtSection.setText(strColumns[0]);
            holder.txtId.setVisibility(View.INVISIBLE);
        }

        @Override
        public int getItemCount(){
            return section_list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder{
            private TextView txtSection;
            private TextView txtId;
            private LinearLayout llist;

            public ViewHolder(View itemView){
                super(itemView);
                txtSection = (TextView) itemView.findViewById(R.id.txtProgramSection);
                txtId = (TextView) itemView.findViewById(R.id.txtSectionId);
                llist = (LinearLayout) itemView.findViewById(R.id.llist);
                this.itemView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                    Context context = v.getContext();
                    String item = section_list.get(getPosition());
                    String[] strColumns = item.split("##");
                    long lngSecId=Long.parseLong(strColumns[1]);
                    long lngSubId=Long.parseLong(strColumns[2]);
                    Intent intent = new Intent(context, NotificationStudentList.class);
                    intent.putExtra("sectionid", lngSecId);
                    intent.putExtra("subid", lngSubId);
                    context.startActivity(intent);
                    }
                });
            }
        }
    }
