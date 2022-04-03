package com.shasun.staffportal;
    import android.content.Context;
    import android.content.Intent;
    import android.content.SharedPreferences;
    import androidx.recyclerview.widget.RecyclerView;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.RelativeLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.util.ArrayList;

public class ViewPaySlipPayPeriodLVAdapter extends RecyclerView.Adapter<ViewPaySlipPayPeriodLVAdapter.ViewHolder> {

    private static ArrayList<String> payperiod_list=new ArrayList<String>();
    private int itemLayout;

    public ViewPaySlipPayPeriodLVAdapter(ArrayList<String> payperiod_list, int itemLayout){
        this.payperiod_list = payperiod_list;
        this.itemLayout = itemLayout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        String item = payperiod_list.get(position);
        if (position % 2 != 0){
            holder.relativeLayoutpayperiod.setBackgroundResource(R.drawable.btnselector_revarse);
        }else{
            holder.relativeLayoutpayperiod.setBackgroundResource(R.drawable.btnselector_revarse_pink);
        }
        String[] strColumns = item.split("##");
        holder.textPayperiodDesc.setText(strColumns[4]);
        holder.textHiddenValue.setText(strColumns[0]+"##"+strColumns[1]+"##"+strColumns[2]+"##"+strColumns[3]);
        holder.textPdfName.setText(strColumns[5]);
        holder.textHiddenValue.setVisibility(View.INVISIBLE);
        holder.textPdfName.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount(){
        return payperiod_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textPayperiodDesc;
        private TextView textPdfName;
        private TextView textHiddenValue;
        private RelativeLayout relativeLayoutpayperiod;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayoutpayperiod = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutpayperiod);
            textPayperiodDesc = (TextView) itemView.findViewById(R.id.txtPayperiodDesc);
            textPdfName = (TextView) itemView.findViewById(R.id.txtPdfName);
            textHiddenValue = (TextView) itemView.findViewById(R.id.textHiddenValue);
            final SharedPreferences loginsession = itemView.getContext().getSharedPreferences("SessionLogin", 0);
            this.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (!CheckNetwork.isInternetAvailable(v.getContext())) {
                        Toast.makeText(v.getContext(),v.getContext().getResources().getString(R.string.loginNoInterNet), Toast.LENGTH_LONG).show();
                        return;
                    }else {

                        String item = payperiod_list.get(getPosition());
                        String[] strColumns = item.split("##");
                        String strPdfFileName = strColumns[5].toString().trim();
                        Context context = v.getContext();
                        Intent intent;
                        intent = new Intent(context, ViewPaySlip.class);
                        intent.putExtra("officeid", strColumns[0]);
                        intent.putExtra("employeeid", strColumns[1]);
                        intent.putExtra("paystructureid", strColumns[2]);
                        intent.putExtra("payperiodid", strColumns[3]);
                        intent.putExtra("pdffilename", strPdfFileName);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}