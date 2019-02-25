package com.example.quocphu.getdealsapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

import at.blogc.android.views.ExpandableTextView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StoreAllPostActivity extends AppCompatActivity {
    private TextView nameStore,addressStore,locationStore,phoneStore;
    private RecyclerView rv_post_deal;
    private FirebaseDatabase database;
    private CircleImageView view_store;
    private String key_post,key_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_allpost);
        nameStore = findViewById(R.id.tv_name_store_detail);
        addressStore = findViewById(R.id.tv_address_detail);
        phoneStore = findViewById(R.id.tv_phone_store_detail);
        rv_post_deal = findViewById(R.id.rv_post_deal);
        view_store = findViewById(R.id.view_user_store_detail);
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
        final String key_user_store = getIntent().getExtras().getString("key_user_store");
        DatabaseReference node_store = database.getReference("stores").child(key_user_store);
        node_store.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                for(DataSnapshot itemStore : dataSnapshot.getChildren()){
                    Store store = dataSnapshot.getValue(Store.class);
                    Picasso.with(StoreAllPostActivity.this).load(store.getPhotoUrl()).fit().centerCrop().into(view_store);
                    nameStore.setText(store.getNameStore());
                    addressStore.setText(store.getAddress());
                    phoneStore.setText(store.getPhoneStore());
                }}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference node_store2 = database.getReference("posts").child(key_user_store).child("posts");
        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(node_store2,Post.class).build();
        FirebaseRecyclerAdapter<Post,PostViewHolder> adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final PostViewHolder holder, int position, final Post model) {
                key_post = getRef(position).getKey();
                holder.tv_tite.setText(model.getTittle());
                holder.tv_start.setText("Start: " + model.getTimeStart());
                holder.tv_end.setText("End: " + model.getTimeEnd());
                holder.tv_quantity.setText(model.getQuantity() + "");
                holder.tv_timePost.setText("Post time: " + model.getTimePost());
                holder.tv_typePost.setText(model.getTypePost());
                holder.tv_content.setText(model.getContentPost());
                holder.tv_content.setInterpolator(new OvershootInterpolator()); //// set interpolators for both expanding and collapsing animations
                holder.iv_expand.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.tv_content.toggle();
                    }
                });
                holder.iv_expand.setOnClickListener(new View.OnClickListener() //Sự kiện chọn nút iv_expand
                {
                    @Override
                    public void onClick(final View v) {
                        if (holder.tv_content.isExpanded()) {
                            holder.tv_content.collapse();
                        } else {
                            holder.tv_content.expand();
                        }
                    }
                });
                holder.btn_edit.setText("Get Deals");
                holder.btn_delete.setVisibility(View.GONE);
                //Sự kiện chọn 1 item trên adapter
                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DatabaseReference node_user = database.getReference("user").child(key_user).child("list_deal").child("valid");
                        final Map<String, Object> valuePost = new HashMap<>();
                        valuePost.put("quantity",model.getQuantity()- 1);
                        node_user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(key_post)) {
                                    Toast.makeText(StoreAllPostActivity.this, "You can get once deal", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseReference node_post2 = database.getReference("allpost");
                                    node_post2.child(key_post).updateChildren(valuePost); //update allpost/keypost
                                    DatabaseReference node_userdeal = database.getReference("user").child(key_user).child("list_deal").child("valid");
                                    node_userdeal.child(key_post).setValue(model);
                                    final DatabaseReference node_post_store2 = database.getReference("posts");
                                    node_post_store2.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot itemStore : dataSnapshot.getChildren()) {
                                                final String key_store2 = itemStore.getKey();
                                                node_post_store2.child(key_store2).child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.hasChild(key_post)) {
                                                            DatabaseReference node_post2 = database.getReference("posts").child(key_store2).child("posts");
                                                            node_post2.updateChildren(valuePost); //update posts/key_store/posts/
                                                            DatabaseReference node_allpost_store= database.getReference("posts").child(key_store2).child("allpost");
                                                            node_allpost_store.child(key_post).updateChildren(valuePost); //update posts/key_store/allpost/
                                                            DatabaseReference node_userdeal = database.getReference("user").child(key_user).child("list_deal").child("valid");
                                                            Map<String,Object> valueUser = new HashMap<>();
                                                            valueUser.put("keystore",key_store2);
                                                            node_userdeal.child(key_post).updateChildren(valueUser);//Update key_store vào user/key_user/list_deal/valid/key_post/keystore
                                                            // Để check deal của cửa hàng nào


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
                });
//                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        node_post.child(key_post).removeValue();
//                        DatabaseReference node_all = database.getReference("allpost");
//                        node_all.child(key_post).removeValue();
//                    }
//                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(StoreAllPostActivity.this).inflate(R.layout.one_item_post,viewGroup,false);
                return new PostViewHolder(view);
            }
        };
        rv_post_deal.setLayoutManager(new LinearLayoutManager(this));
        adapter.startListening();
        rv_post_deal.setAdapter(adapter);
    }
    private class PostViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_tite, tv_quantity,tv_typePost,tv_start,tv_end,tv_timePost;
        private ExpandableTextView tv_content;
        private ImageView iv_expand;
        private Button btn_delete,btn_edit;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tite = itemView.findViewById(R.id.tv_title_deal);
            tv_start = itemView.findViewById(R.id.tv_start_deal);
            tv_end = itemView.findViewById(R.id.tv_end_deal);
            tv_quantity = itemView.findViewById(R.id.tv_quantity_deal);
            tv_timePost = itemView.findViewById(R.id.tv_timepost_deal);
            tv_typePost = itemView.findViewById(R.id.tv_type_deal);
            tv_content = itemView.findViewById(R.id.tv_content_deal);
            iv_expand = itemView.findViewById(R.id.iv_expand);
            btn_delete = itemView.findViewById(R.id.btn_delete_post);
            btn_edit = itemView.findViewById(R.id.btn_edit_post);
        }
    }

}
