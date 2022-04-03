package com.shasun.staffportal;
import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.PorterDuff;
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

public class LeaveStatusLVAdapter extends RecyclerView.Adapter<LeaveStatusLVAdapter.ViewHolder> {
    private static ArrayList<String> leavestatus_list=new ArrayList<String>();
    private int itemLayout;

    public LeaveStatusLVAdapter(ArrayList<String> leavestatus_list, int itemLayout) {
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

            holder.textApplnDate.setText(strColumns[0]);
            holder.textLeaveType.setText(strColumns[1]);
            holder.textFromDate.setText(strColumns[2]);
            holder.textToDate.setText(strColumns[3]);
            holder.textSession.setText(strColumns[4]);
            holder.textReason.setText(strColumns[5]);
            holder.textNoofDays.setText(strColumns[6]);
            holder.textLeaveStatus.setText(strColumns[7]);

        if (!strColumns[7].trim().equals("Applied")){
//            holder.btnCancel.setVisibility(View.INVISIBLE);
           // holder.btnCancel.getBackground().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
//            holder.btnCancel.setBackgroundColor(Color.parseColor("#808080"));
            holder.btnCancel.setVisibility(View.GONE);
        }
        else{
         //   holder.btnCancel.getBackground().setColorFilter(null);
            holder.btnCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return leavestatus_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextInputEditText textApplnDate, textLeaveType, textFromDate,textLeaveStatus, textToDate,textSession,textReason,textNoofDays;

        private Button btnCancel;
        private long lngLeaveApplnId=0;

        public ViewHolder(View itemView){
            super(itemView);
            textApplnDate = (TextInputEditText) itemView.findViewById(R.id.txtApplnDate);
            textLeaveType = (TextInputEditText) itemView.findViewById(R.id.txtLeaveType);
            textFromDate = (TextInputEditText) itemView.findViewById(R.id.txtFromDate);
            textToDate = (TextInputEditText) itemView.findViewById(R.id.txtToDate);
            textSession = (TextInputEditText) itemView.findViewById(R.id.txtSession);
            textReason = (TextInputEditText) itemView.findViewById(R.id.txtReason);
            textNoofDays = (TextInputEditText) itemView.findViewById(R.id.txtNoofDays);
            textLeaveStatus = (TextInputEditText) itemView.findViewById(R.id.txtLeaveStatus);
            btnCancel = (Button) itemView.findViewById(R.id.btnCancel);

            this.btnCancel.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                try {
                    //network check
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        String item = leavestatus_list.get(getPosition());
                        String[] strColumns = item.split("##");
                        lngLeaveApplnId = Long.parseLong(strColumns[8]);
                        removeAt(getPosition());
                        ((LeaveStatus) v.getContext()).callLeaveCancel(lngLeaveApplnId);
                    }
                } catch (Exception e){
                    // ignore
                }
                }
            });
        }
    }

    public void removeAt(int position){
        leavestatus_list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, leavestatus_list.size());
    }
}
