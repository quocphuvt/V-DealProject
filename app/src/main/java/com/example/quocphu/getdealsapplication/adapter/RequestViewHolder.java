package com.example.quocphu.getdealsapplication.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quocphu.getdealsapplication.R;

public class RequestViewHolder extends RecyclerView.ViewHolder {
    public TextView tv_username;
    public Button btn_accept_request,btn_delete_request;
    public ImageView iv_icon;
    Context context;
    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);
        tv_username = itemView.findViewById(R.id.tv_username_request);
        btn_accept_request = itemView.findViewById(R.id.btn_accept_request);
        btn_delete_request = itemView.findViewById(R.id.btn_delete_request);
        iv_icon = itemView.findViewById(R.id.iv_icon_store);

    }
}
