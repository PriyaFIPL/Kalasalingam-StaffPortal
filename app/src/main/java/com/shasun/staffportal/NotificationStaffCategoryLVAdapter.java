
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

public class NotificationStaffCategoryLVAdapter extends RecyclerView.Adapter<NotificationStaffCategoryLVAdapter.ViewHolder> {

    private static ArrayList<String> category_list=new ArrayList<String>();
    private int itemLayout;

    public NotificationStaffCategoryLVAdapter(ArrayList<String> category_list, int itemLayout){
        this.category_list = category_list;
        this.itemLayout = itemLayout;
}

    @Override
    public NotificationStaffCategoryLVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new NotificationStaffCategoryLVAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationStaffCategoryLVAdapter.ViewHolder holder, int position){
        if (position % 2 != 0){
            holder.llist.setBackgroundResource(R.drawable.btnselector_revarse);
        }else{
            holder.llist.setBackgroundResource(R.drawable.btnselector_revarse_pink);
        }

        String item = category_list.get(position);
        String[] strColumns = item.split("##");
        holder.txtIds.setText(strColumns[0]);
        holder.txtStaffCategory.setText(strColumns[1]);
        holder.txtIds.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return category_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtStaffCategory;
        private TextView txtIds;
        private LinearLayout llist;


        public ViewHolder(View itemView){
            super(itemView);
            llist = (LinearLayout) itemView.findViewById(R.id.llist);
            txtStaffCategory = (TextView) itemView.findViewById(R.id.txtStaffCategory);
            txtIds = (TextView) itemView.findViewById(R.id.txtIds);
            this.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    String item = category_list.get(getPosition());
                    String[] strColumns = item.split("##");
                    long lngCatgeoryId=Long.parseLong(strColumns[0]);
                    Intent intent = new Intent(context, NotificationForStaffList.class);
                    intent.putExtra("employeecategoryid", lngCatgeoryId);
                    context.startActivity(intent);
                }
            });
        }
    }
}
