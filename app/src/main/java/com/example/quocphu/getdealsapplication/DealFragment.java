package com.example.quocphu.getdealsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quocphu.getdealsapplication.model.Post;
import com.example.quocphu.getdealsapplication.model.Store;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;

public class DealFragment extends Fragment {
    private RecyclerView rv_hotdeal,rv_alldeal;
    private FirebaseDatabase database;
    //LinkedHashMap là dạng Map
    private DatabaseReference node_post,node_store,node_post_store;
//    private LinkedHashMap<String,Post> posts = new LinkedHashMap<>();// có thể lấy được key và value;
    private String key_user;
    private FirebaseAuth mAuth;
    private Post post;
    private FirebaseRecyclerAdapter<Post,HotDealViewHolder> adapter_hotdeal;
    private FirebaseRecyclerAdapter<Post,AllDealViewHolder> adapter_alldeal;
    private LinkedHashMap<String,Post> list_allpost = new LinkedHashMap<>();
    private ImageView iv_filter;
    private FirebaseRecyclerOptions<Post> options_hotdeal;
    private FirebaseRecyclerOptions<Post> options_alldeal;
    public DealFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_deal, container, false);
        rv_hotdeal = view.findViewById(R.id.rv_deal_hotdeal);
        rv_alldeal =view.findViewById(R.id.rv_alldeal);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        node_post_store = database.getReference("posts");
        node_store = database.getReference("stores");
        Query queryUser = FirebaseDatabase.getInstance().getReference("user").orderByChild("id_user").equalTo(currentUser.getUid());
        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemUser : dataSnapshot.getChildren()) {
                    key_user = itemUser.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Query queryHotDeal = database.getReference("allpost").orderByChild("percent").startAt(60);
        options_hotdeal = new FirebaseRecyclerOptions.Builder<Post>().setQuery(queryHotDeal,Post.class).build();
        adapter_hotdeal = new FirebaseRecyclerAdapter<Post, HotDealViewHolder>(options_hotdeal) {
            @Override
            protected void onBindViewHolder(final HotDealViewHolder holder, int position, Post model) {
                final String key_post = getRef(position).getKey();
                holder.tv_title_deal.setText(model.getTittle());
                holder.tv_percent_deal.setText(model.getPercent()+"");
                holder.tv_quantity.setText(model.getQuantity()+"");
                holder.tv_datestart.setText(model.getTimeStart());
                Picasso.with(getContext()).load(model.getPhotoPost()).fit().centerCrop().into(holder.iv_deal);
                node_post_store.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemStore : dataSnapshot.getChildren()) {
                            final String key_store = itemStore.getKey();
                            node_post_store.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot itemStore : dataSnapshot.getChildren()) {
                                        final String key_store = itemStore.getKey();
                                        node_post_store.child(key_store).child("posts").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild(key_post)) {
                                                    holder.btn_seedeal.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent i = new Intent(getContext(), DealDetailActivity.class);
                                                            i.putExtra("key_post",key_post);
                                                            i.putExtra("key_user",key_user);
                                                            i.putExtra("key_store",key_store);
                                                            startActivity(i);
                                                        }
                                                    });
                                                    node_store.child(key_store).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot item_store : dataSnapshot.getChildren()) {
                                                                Picasso.with(getContext()).load(dataSnapshot.getValue(Store.class).getPhotoUrl()).into(holder.iv_store);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @NonNull
            @Override
            public HotDealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.one_item_hotdeal,viewGroup,false);
                return new HotDealViewHolder(view);
            }
        };
        rv_hotdeal.setHasFixedSize(true);
        rv_hotdeal.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        adapter_hotdeal.startListening();
        rv_hotdeal.setAdapter(adapter_hotdeal);

        DatabaseReference node_allpost = database.getReference("allpost");
        options_alldeal = new FirebaseRecyclerOptions.Builder<Post>().setQuery(node_allpost,Post.class).build();
        adapter_alldeal = new FirebaseRecyclerAdapter<Post, AllDealViewHolder>(options_alldeal) {
            @Override
            protected void onBindViewHolder(final AllDealViewHolder holder, int position, final Post model) {
                final String key_post = getRef(position).getKey();
                holder.tv_title_deal.setText(model.getTittle());
                holder.tv_percent_deal.setText(model.getPercent()+"");
                holder.tv_quantity.setText(model.getQuantity()+"");
                holder.tv_datestart.setText(model.getTimeStart());
                Picasso.with(getContext()).load(model.getPhotoPost()).fit().centerCrop().into(holder.iv_deal);

                node_post_store.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemStore : dataSnapshot.getChildren()) {
                            final String key_store = itemStore.getKey();
                            node_post_store.child(key_store).child("posts").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(key_post)) {
                                        holder.btn_seedeal.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent i = new Intent(getContext(), DealDetailActivity.class);
                                                i.putExtra("key_post",key_post);
                                                i.putExtra("key_user",key_user);
                                                i.putExtra("key_store",key_store);
                                                startActivity(i);
                                            }
                                        });
                                        node_store.child(key_store).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot item_store : dataSnapshot.getChildren()) {
                                                    Picasso.with(getContext()).load(dataSnapshot.getValue(Store.class).getPhotoUrl()).into(holder.iv_store);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public AllDealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view2 = LayoutInflater.from(getContext()).inflate(R.layout.one_item_all_deal,viewGroup,false);
                return new AllDealViewHolder(view2);
            }
        };
        rv_alldeal.setLayoutFrozen(true);
        rv_alldeal.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        rv_alldeal.setHasFixedSize(true);
        rv_alldeal.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL, false));
        adapter_alldeal.startListening();
        rv_alldeal.setAdapter(adapter_alldeal);


        return view;
    }


//    private class DealViewHolder extends RecyclerView.ViewHolder{
//        private ImageView iv_photo_deal,iv_state_deal;
//        private TextView tv_title_deal,tv_name_store,tv_state_deal;
//        private Button btn_getDeal;
//        public DealViewHolder(@NonNull View itemView) {
//            super(itemView);
//            iv_photo_deal = itemView.findViewById(R.id.iv_photo_deal);
//            tv_name_store = itemView.findViewById(R.id.tv_namestore_deal_post);
//            tv_title_deal = itemView.findViewById(R.id.tv_title_deal_post);
//            iv_state_deal = itemView.findViewById(R.id.iv_state_deal);
//            tv_state_deal = itemView.findViewById(R.id.tv_state_deal);
//            btn_getDeal = itemView.findViewById(R.id.btn_getdeal);
//
//        }
//    }
private class HotDealViewHolder extends RecyclerView.ViewHolder { //View của 1 item trong 1 section

    private TextView tv_title_deal,tv_percent_deal,tv_quantity,tv_namestore,tv_datestart;
    private ImageView iv_deal,iv_store;
    private Button btn_seedeal;


    public HotDealViewHolder(View view) {
        super(view);
        this.tv_title_deal = (TextView) view.findViewById(R.id.tv_title_deal);
        this.tv_percent_deal = view.findViewById(R.id.tv_percent_deal);
        this.tv_quantity = view.findViewById(R.id.tv_quantity);
        this.tv_namestore = view.findViewById(R.id.tv_namestore_deal);
        this.btn_seedeal = view.findViewById(R.id.btn_seedeal);
        this.iv_deal = view.findViewById(R.id.iv_product_deal);
        this.tv_datestart = view.findViewById(R.id.tv_date_start);
        this.iv_store = view.findViewById(R.id.iv_store_deal);

    }

}
    private class AllDealViewHolder extends RecyclerView.ViewHolder { //View của 1 item trong 1 section

        private TextView tv_title_deal,tv_percent_deal,tv_quantity,tv_datestart;
        private ImageView iv_deal,iv_store;
        private Button btn_seedeal;


        public AllDealViewHolder(View view2) {
            super(view2);
            this.tv_title_deal = (TextView) view2.findViewById(R.id.tv_title_deal_all);
            this.tv_percent_deal = view2.findViewById(R.id.tv_percent_deal_all);
            this.tv_quantity = view2.findViewById(R.id.tv_quantity_all);
            this.btn_seedeal = view2.findViewById(R.id.btn_seedeal_all);
            this.iv_deal = view2.findViewById(R.id.iv_product_all);
            this.tv_datestart = view2.findViewById(R.id.tv_date_start_all);
            this.iv_store = view2.findViewById(R.id.iv_store_all);

        }

    }


}
