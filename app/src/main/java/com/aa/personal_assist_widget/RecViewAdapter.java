package com.aa.personal_assist_widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by kummukes on 4/4/2018.
 */

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.ViewHolder> {

    Logger logger = Logger.getLogger(RecViewAdapter.class.getName());

    private List<ListModel> listDetails;
    private Context context;

    public RecViewAdapter(List<ListModel> listDetails, Context context) {
        this.listDetails = listDetails;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vw = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items,parent,false);
        return new ViewHolder(vw);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ListModel lst1 = listDetails.get(position);
        holder.txtViewName.setText(lst1.getName());
        holder.txtViewInfo.setText(lst1.getInfo());
        holder.txtViewWaitTime.setText(lst1.getWaitTime() + " mins");
        if(lst1.getStatus().endsWith("Done")) {
            int col = context.getResources().getColor(R.color.american_dark_green);
            holder.txtViewStatus.setBackgroundColor(col);
            holder.txtViewName.setTextColor(col);
        } else {
            int col = context.getResources().getColor(R.color.american_orange);
            holder.txtViewStatus.setBackgroundColor(col);
            holder.txtViewName.setTextColor(col);
        }
        //holder.txtViewDetail.setText(lst1.getDetails());
        if(position == 1) {
            holder.imgView.setImageResource(R.drawable.icon_security_2);
        }

    }

    @Override
    public int getItemCount() {
        return listDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtViewName;
        public TextView txtViewInfo;
        public TextView txtViewWaitTime;
        public ImageView imgView;
        public TextView txtViewDetail;
        public TextView txtViewStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            txtViewName = (TextView) itemView.findViewById(R.id.txtViewName);
            txtViewInfo = (TextView) itemView.findViewById(R.id.txtViewInfo);
            txtViewStatus = (TextView) itemView.findViewById(R.id.status_border);
            txtViewWaitTime = (TextView) itemView.findViewById(R.id.txtViewWaitTime);
            //txtViewInfo = itemView.findViewById(R.id.main_content);
            imgView = (ImageView)itemView.findViewById(R.id.row_img);
        }
    }
}
