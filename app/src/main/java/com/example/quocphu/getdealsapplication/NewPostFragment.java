package com.example.quocphu.getdealsapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quocphu.getdealsapplication.model.Post;
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

import at.blogc.android.views.ExpandableTextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewPostFragment extends Fragment {
    private RecyclerView rv_newPost;
    private FirebaseRecyclerOptions<Post> options;
    private FirebaseRecyclerAdapter<Post,PostViewHolder> adapter;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private String key_user,key_post;
    public NewPostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);
        rv_newPost = view.findViewById(R.id.rv_newPost);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Query node_user = database.getReference("user").orderByChild("id_user").equalTo(currentUser.getUid());
        node_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot itemUser : dataSnapshot.getChildren()) {
                        key_user = itemUser.getKey();
                    }
                    final DatabaseReference node_post = database.getReference("posts").child(key_user).child("posts");
                    options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(node_post, Post.class).build();
                    if (options != null) {
                        adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
                            @Override
                            protected void onBindViewHolder(final PostViewHolder holder, int position, Post model) {
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
                                //Sự kiện chọn 1 item trên adapter
                                holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(getContext(), EditPostActivity.class);
                                        i.putExtra("key_post", key_post);
                                        i.putExtra("key_store", key_user);
                                        startActivity(i);
                                    }
                                });
                                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        node_post.child(key_post).removeValue();
                                        DatabaseReference node_all = database.getReference("allpost");
                                        node_all.child(key_post).removeValue();
                                    }
                                });
                            }

                            @NonNull
                            @Override
                            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                View view = LayoutInflater.from(getContext()).inflate(R.layout.one_item_post, viewGroup, false);
                                return new PostViewHolder(view);
                            }
                        };
                        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        adapter.startListening();
                        rv_newPost.setLayoutManager(manager);
                        rv_newPost.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
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
