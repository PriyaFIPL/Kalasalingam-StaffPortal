package com.shasun.staffportal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class BirthdayLVAdapter extends RecyclerView.Adapter<BirthdayLVAdapter.ViewHolder> {
    private static ArrayList<String> leavestatus_list=new ArrayList<String>();
    private int itemLayout;

    public BirthdayLVAdapter(ArrayList<String> leavestatus_list, int itemLayout) {
        this.leavestatus_list = leavestatus_list;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = leavestatus_list.get(position);
        String[] strColumns = item.split("##");

            holder.textLeaveType.setText(strColumns[0]);
            holder.textEligibility.setText(strColumns[1]);

    }

    @Override
    public int getItemCount() {
        return leavestatus_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textLeaveType, textEligibility;

        public ViewHolder(View itemView){
            super(itemView);
            textLeaveType = (TextView) itemView.findViewById(R.id.txtEmployeeName);
            textEligibility = (TextView) itemView.findViewById(R.id.txtDesignation);

        }
    }
}
