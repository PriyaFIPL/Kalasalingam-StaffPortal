package com.shasun.staffportal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class AbsenteeCountLVAdapter extends RecyclerView.Adapter<AbsenteeCountLVAdapter.ViewHolder> {
    private static ArrayList<String> leavestatus_list=new ArrayList<String>();
    private int itemLayout;

    public AbsenteeCountLVAdapter(ArrayList<String> leavestatus_list, int itemLayout) {
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
            holder.txtStudentCount.setText(strColumns[2]);
            holder.txtDayorderHour.setText(strColumns[3]);

    }

    @Override
    public int getItemCount() {
        return leavestatus_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextInputEditText  textLeaveType, textEligibility,txtStudentCount,txtDayorderHour;

        public ViewHolder(View itemView){
            super(itemView);
            textLeaveType = (TextInputEditText) itemView.findViewById(R.id.txtEmployeeName);
            textEligibility = (TextInputEditText) itemView.findViewById(R.id.txtDesignation);
            txtStudentCount = (TextInputEditText) itemView.findViewById(R.id.txtStudentCount);
            txtDayorderHour = (TextInputEditText) itemView.findViewById(R.id.txtDayorderHour);

        }
    }
}
