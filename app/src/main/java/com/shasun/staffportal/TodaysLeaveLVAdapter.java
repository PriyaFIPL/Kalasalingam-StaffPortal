package com.shasun.staffportal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class TodaysLeaveLVAdapter extends RecyclerView.Adapter<TodaysLeaveLVAdapter.ViewHolder> {
    private static ArrayList<String> leavestatus_list=new ArrayList<String>();
    private int itemLayout;

    public TodaysLeaveLVAdapter(ArrayList<String> leavestatus_list, int itemLayout) {
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

            holder.textLeaveCount.setText(strColumns[0]);
            holder.textLateCount.setText(strColumns[1]);
            holder.textPermissionCount.setText(strColumns[2]);
            holder.textODCount.setText(strColumns[3]);
            holder.txtFromDate.setText(strColumns[4]);


    }

    @Override
    public int getItemCount() {
        return leavestatus_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextInputEditText  textLeaveCount, textLateCount, textPermissionCount,textODCount,txtFromDate;

        public ViewHolder(View itemView){
            super(itemView);
            textLeaveCount = (TextInputEditText) itemView.findViewById(R.id.txtProgram);
            textLateCount = (TextInputEditText) itemView.findViewById(R.id.txtdayorder);
            textPermissionCount = (TextInputEditText) itemView.findViewById(R.id.txtDayorderHour);
            textODCount = (TextInputEditText) itemView.findViewById(R.id.txtSN);
            txtFromDate = (TextInputEditText) itemView.findViewById(R.id.txtabRegN);

        }
    }
}
