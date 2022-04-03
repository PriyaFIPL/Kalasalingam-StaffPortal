package com.shasun.staffportal;

import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class LeaveApprovalLVAdapter extends RecyclerView.Adapter<LeaveApprovalLVAdapter.ViewHolder>{
    private static ArrayList<String> leavepending_list=new ArrayList<String>(500);
    private int itemLayout;

    public LeaveApprovalLVAdapter(ArrayList<String> leavepending_list, int itemLayout){
        this.leavepending_list = leavepending_list;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new LeaveApprovalLVAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LeaveApprovalLVAdapter.ViewHolder holder, int position){
        String item = leavepending_list.get(position);
        String[] strColumns = item.split("##");

            holder.textEmployeeName.setText(strColumns[1]);
            holder.textDepartment.setText(strColumns[2]);
            holder.textDesignation.setText(strColumns[3]);
            holder.textLeaveType.setText(strColumns[4]);
            holder.textFromDate.setText(strColumns[5]);
            holder.textToDate.setText(strColumns[6]);
            holder.textSession.setText(strColumns[7]);
            holder.textReason.setText(strColumns[8]);
            holder.textNoofDays.setText(strColumns[9]);

    }

    @Override
    public int getItemCount() {
        return leavepending_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextInputEditText textEmployeeName;
        private TextInputEditText textDepartment;
        private TextInputEditText textDesignation;
        private TextInputEditText textLeaveType;
        private TextInputEditText textFromDate;
        private TextInputEditText textToDate;
        private TextInputEditText textSession;
        private TextInputEditText textReason;
        private TextInputEditText textNoofDays;
        private Button btnApprove;
        private Button btnReject;
        private long lngLeaveApplnId=0;

        public ViewHolder(View itemView){
            super(itemView);
            textEmployeeName = (TextInputEditText) itemView.findViewById(R.id.txtEmployeeName);
            textDepartment = (TextInputEditText) itemView.findViewById(R.id.txtDepartment);
            textDesignation = (TextInputEditText) itemView.findViewById(R.id.txtDesignation);
            textLeaveType = (TextInputEditText) itemView.findViewById(R.id.txtLeaveType);
            textFromDate = (TextInputEditText) itemView.findViewById(R.id.txtFromDate);
            textToDate = (TextInputEditText) itemView.findViewById(R.id.txtToDate);
            textSession = (TextInputEditText) itemView.findViewById(R.id.txtSession);
            textReason = (TextInputEditText) itemView.findViewById(R.id.txtReason);
            textNoofDays = (TextInputEditText) itemView.findViewById(R.id.txtNoofDays);
            btnApprove = (Button) itemView.findViewById(R.id.btnApprove);
            btnReject = (Button) itemView.findViewById(R.id.btnReject);

            //this.btnApprove.setOnClickListener();
            this.btnApprove.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                try {
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        String item = leavepending_list.get(getPosition());
                        String[] strColumns = item.split("##");
                        lngLeaveApplnId = Long.parseLong(strColumns[0]);
                        removeAt(getPosition());
                        ((LeaveApproval) v.getContext()).callLeaveApproveReject(lngLeaveApplnId, 1);
                    }
                } catch (Exception e) {
                    // ignore
                }
                }
            });
            this.btnReject.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                try {
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        String item = leavepending_list.get(getPosition());
                        String[] strColumns = item.split("##");
                        lngLeaveApplnId = Long.parseLong(strColumns[0]);
                        removeAt(getPosition());
                        ((LeaveApproval) v.getContext()).callLeaveApproveReject(lngLeaveApplnId, 9);
                    } } catch (Exception e) {
                    // ignore
                }
                }
            });
        }
    }

    public void removeAt(int position) {
        leavepending_list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, leavepending_list.size());
    }
}
