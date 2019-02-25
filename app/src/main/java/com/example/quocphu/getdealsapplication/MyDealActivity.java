package com.example.quocphu.getdealsapplication;

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
import android.widget.LinearLayout;
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

public class MyDealActivity extends AppCompatActivity {
    private RecyclerView rv_mydeal;
    private String key_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_deal);
        rv_mydeal = findViewById(R.id.rv_mydeal);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query queryUser = FirebaseDatabase.getInstance().getReference("user").orderByChild("id_user").equalTo(currentUser.getUid());
        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemUser : dataSnapshot.getChildren()) {
                    key_user = itemUser.getKey();
                }
                DatabaseReference node_user = FirebaseDatabase.getInstance().getReference("user").child(key_user).child("list_deal").child("valid");
                FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(node_user,Post.class).build();
                FirebaseRecyclerAdapter<Post,PostViewHolder> adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(final PostViewHolder holder, int position, Post model) {
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
                        holder.btn_edit.setVisibility(View.GONE);
                        holder.btn_delete.setVisibility(View.GONE);
                    }

                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(MyDealActivity.this).inflate(R.layout.one_item_post,viewGroup,false);
                        return new PostViewHolder(view);
                    }
                };
                adapter.startListening();
                rv_mydeal.setLayoutManager(new LinearLayoutManager(MyDealActivity.this));
                rv_mydeal.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
