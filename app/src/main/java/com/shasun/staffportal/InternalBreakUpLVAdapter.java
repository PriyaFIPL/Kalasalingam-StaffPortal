package com.shasun.staffportal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shasun.staffportal.properties.Properties;

import java.util.ArrayList;

public class InternalBreakUpLVAdapter extends RecyclerView.Adapter<InternalBreakUpLVAdapter.ViewHolder> {
    private static ArrayList<String> sub_list=new ArrayList<String>();
    private int itemLayout;

    public InternalBreakUpLVAdapter(ArrayList<String> sub_list, int itemLayout){
        this.sub_list = sub_list;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String item = sub_list.get(position);
        String[] strColumns = item.split("##");
        holder.textInternalBreakupId.setText(strColumns[0]);
        holder.textProgSectionId.setText(strColumns[1]);
        holder.txtSubject.setText(strColumns[2] +"-"+ strColumns[3]);
        holder.txtProgSection.setText(strColumns[4]);
        holder.txtConductConversionMaxMark.setText("Conduct Max Mark:" + strColumns[5] + "   Conversion Max Mark: "+ strColumns[6]);
        holder.textInternalBreakupId.setVisibility(View.GONE);
        holder.textProgSectionId.setVisibility(View.GONE);
        if (Integer.parseInt(strColumns[7].trim()) > 0){
            holder.txtSubject.setBackgroundColor(Color.parseColor("#567845"));
            holder.txtProgSection.setBackgroundColor(Color.parseColor("#567845"));
            holder.txtConductConversionMaxMark.setBackgroundColor(Color.parseColor("#567845"));
            holder.txtSubject.setTextColor(Color.parseColor("#ffffff"));
            holder.txtProgSection.setTextColor(Color.parseColor("#ffffff"));
            holder.txtConductConversionMaxMark.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return sub_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtSubject;
        private TextView txtProgSection;
        private TextView txtConductConversionMaxMark;
        private TextView textInternalBreakupId;
        private TextView textProgSectionId;

        public ViewHolder(View itemView){
            super(itemView);
            txtSubject = (TextView) itemView.findViewById(R.id.txtSubject);
            txtProgSection = (TextView) itemView.findViewById(R.id.txtProgSection);
            txtConductConversionMaxMark = (TextView) itemView.findViewById(R.id.txtConductConvertMaxMarks);
            textProgSectionId = (TextView) itemView.findViewById(R.id.txtProgSecId);
            textInternalBreakupId = (TextView) itemView.findViewById(R.id.txtInternalBreakupId);

            this.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        Context context = v.getContext();
                        String item = sub_list.get(getPosition());
                        String[] strColumns = item.split("##");
                        long lngInternalBreakUpId = Long.parseLong(strColumns[0]);
                        long lngProgSecId = Long.parseLong(strColumns[1]);
                        final SharedPreferences loginsession = context.getSharedPreferences("SessionLogin", 0);
                        int intBreakUpId = loginsession.getInt("breakupid", 1);
                        if (Integer.parseInt(strColumns[7]) == 0) {
                            Intent intent = new Intent(context, InternalMarkEntryStudentList.class);
                            intent.putExtra("internalbreakupid", lngInternalBreakUpId);
                            intent.putExtra("progsecid", lngProgSecId);
                            intent.putExtra("breakupid", intBreakUpId);
                            intent.putExtra(Properties.attendanceHeader1, txtSubject.getText().toString());
                            intent.putExtra(Properties.attendanceHeader2, txtProgSection.getText().toString());
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Response: Already Mark entered", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, InternalMarkEntriedList.class);
                            intent.putExtra("internalbreakupid", lngInternalBreakUpId);
                            intent.putExtra("progsecid", lngProgSecId);
                            intent.putExtra(Properties.attendanceHeader1, txtSubject.getText().toString());
                            intent.putExtra(Properties.attendanceHeader2, txtProgSection.getText().toString());
                            context.startActivity(intent);
                        }
                    }
                }
            });
        }
    }
}