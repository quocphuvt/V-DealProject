package com.example.quocphu.getdealsapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quocphu.getdealsapplication.R;
import com.example.quocphu.getdealsapplication.model.Post;
import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;

public class SectionPostAdapter extends RecyclerView.Adapter<SectionPostAdapter.SingleItemRowHolder> {

    private LinkedHashMap<String,Post> item_post;
    private Context context;
    private Object[]keys;

    public SectionPostAdapter(Context context, LinkedHashMap<String,Post> item_post) {
        this.item_post = item_post;
        this.context = context;
        keys  = item_post.keySet().toArray(); //Lấy key dựa vào vị trí
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_item_hotdeal, null);
        return new SingleItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {
        holder.tv_quantity.setText(item_post.get(keys[i]).getQuantity()+"");
        holder.tv_percent_deal.setText(item_post.get(keys[i]).getPercent()+"");
        holder.tv_title_deal.setText(item_post.get(keys[i]).getTittle());
        Picasso.with(context).load(item_post.get(keys[i]).getPhotoPost()).fit().centerCrop().into(holder.iv_deal);
    }

    @Override
    public int getItemCount() {
        return (null != item_post ? item_post.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder { //View của 1 item trong 1 section

        protected TextView tv_title_deal,tv_percent_deal,tv_quantity,tv_namestore;
        protected ImageView iv_deal;
        protected Button btn_seedeal;


        public SingleItemRowHolder(View view) {
            super(view);
            this.tv_title_deal = (TextView) view.findViewById(R.id.tv_title_deal);
            this.tv_percent_deal = view.findViewById(R.id.tv_percent_deal);
            this.tv_quantity = view.findViewById(R.id.tv_quantity);
            this.tv_namestore = view.findViewById(R.id.tv_namestore_deal);
            this.btn_seedeal = view.findViewById(R.id.btn_seedeal);
            this.iv_deal = view.findViewById(R.id.iv_product_deal);

        }

    }

}
