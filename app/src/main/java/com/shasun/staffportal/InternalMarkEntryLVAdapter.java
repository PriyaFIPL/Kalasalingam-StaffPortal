package com.shasun.staffportal;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shasun.staffportal.properties.Properties;

public class InternalMarkEntryLVAdapter extends RecyclerView.Adapter<InternalMarkEntryLVAdapter.ViewHolder> {
    private static ArrayList<String> breakup_list=new ArrayList<String>();
    private int itemLayout;

    public InternalMarkEntryLVAdapter(ArrayList<String> breakup_list, int itemLayout){
        this.breakup_list = breakup_list;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String item = breakup_list.get(position);
        String[] strColumns = item.split("##");
        holder.textBreakupId.setText(strColumns[0]);
        holder.textBreakupDesc.setText(strColumns[1]);
        holder.textBreakupId.setVisibility(View.INVISIBLE);
        if (position % 2 != 0){
            holder.relativeLayoutInternal.setBackgroundResource(R.drawable.btnselector_revarse);
        }else{
            holder.relativeLayoutInternal.setBackgroundResource(R.drawable.btnselector_revarse_pink);
        }
    }

    @Override
    public int getItemCount() {
        return breakup_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textBreakupDesc;
        private TextView textBreakupId;
        RelativeLayout relativeLayoutInternal;

        public ViewHolder(View itemView){
            super(itemView);
            relativeLayoutInternal = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutInternal);
            textBreakupDesc = (TextView) itemView.findViewById(R.id.txtBreakupDesc);
            textBreakupId = (TextView) itemView.findViewById(R.id.txtBreakupId);

            this.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        String item = breakup_list.get(getPosition());
                        String[] strColumns = item.split("##");
                        int intBreakUpId = Integer.parseInt(strColumns[0]);
                        Context context = v.getContext();
                        Intent intent = new Intent(context, InternalBreakUp.class);
                        intent.putExtra("breakupid", intBreakUpId);
                        intent.putExtra(Properties.timeTableHeader, strColumns[1]);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}