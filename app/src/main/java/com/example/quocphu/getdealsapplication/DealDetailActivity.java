package com.example.quocphu.getdealsapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.Post;
import com.example.quocphu.getdealsapplication.model.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DealDetailActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private TextView tv_address,tv_store,tv_phone,tv_viewed,tv_sumpost,tv_description,tv_datestart,tv_dateend,tv_quantity,
    tv_tittle_deal,tv_content_deal,tv_percent,tv_codedeal;
    private ImageView iv_background,iv_favour,iv_store;
    private Button btn_getdeal;
    private Post post;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_detail);
        initView(); //ánh xạ view
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final String key_user = getIntent().getExtras().getString("key_user");
        String key_store = getIntent().getExtras().getString("key_store");
        final String key_post = getIntent().getExtras().getString("key_post");
        DatabaseReference node_store = database.getReference("stores").child(key_store);
        node_store.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemStore : dataSnapshot.getChildren()){
                    Store store = dataSnapshot.getValue(Store.class);
                    Picasso.with(DealDetailActivity.this).load(store.getPhotoUrl()).fit().centerCrop().into(iv_store);
                    Picasso.with(DealDetailActivity.this).load(store.getCover()).fit().centerCrop().into(iv_background);
                    tv_store.setText(store.getNameStore());
                    tv_address.setText(store.getAddress());
                    tv_description.setText(store.getDescription());
                    tv_viewed.setText(store.getViewed()+" viewed");
                    tv_phone.setText(store.getPhoneStore());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference node_post = database.getReference("posts").child(key_store).child("posts").child(key_post);
        node_post.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemPost : dataSnapshot.getChildren()){
                    post = dataSnapshot.getValue(Post.class);
                    tv_datestart.setText(post.getTimeStart());
                    tv_dateend.setText(post.getTimeEnd());
                    tv_quantity.setText(post.getQuantity()+"");
                    tv_tittle_deal.setText(post.getTittle());
                    tv_codedeal.setText(post.getCodeDeal());
                    tv_content_deal.setText(post.getContentPost());
                    tv_percent.setText(post.getPercent()+"%");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference node_allpost_store = database.getReference("posts").child(key_store).child("allpost");
        node_allpost_store.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item_allpost : dataSnapshot.getChildren()){
                    count++;
                    tv_sumpost.setText("Posts: "+count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btn_getdeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference node_user = database.getReference("user").child(key_user).child("list_deal").child("valid");
                final Map<String, Object> valuePost = new HashMap<>();
                valuePost.put("quantity", Integer.parseInt(tv_quantity.getText().toString())- 1);
                node_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(key_post)) {
                            Toast.makeText(DealDetailActivity.this, "You can get once deal", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference node_post2 = database.getReference("allpost");
                            node_post2.child(key_post).updateChildren(valuePost); //update allpost/keypost
                            DatabaseReference node_userdeal = database.getReference("user").child(key_user).child("list_deal").child("valid");
                            node_userdeal.child(key_post).setValue(post);
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
                                                    node_post.updateChildren(valuePost); //update posts/key_store/posts/
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


    }
    private void initView(){
        tv_address = findViewById(R.id.tv_address_detail);
        tv_store = findViewById(R.id.tv_store_detail);
        tv_phone = findViewById(R.id.tv_phone_detail);
        tv_viewed = findViewById(R.id.tv_viewed_detail);
        tv_sumpost = findViewById(R.id.tv_sum_post_detail);
        tv_description = findViewById(R.id.tv_description_store_detail);
        tv_datestart = findViewById(R.id.tv_date_end_detail);
        tv_dateend = findViewById(R.id.tv_date_end_detail);
        tv_quantity = findViewById(R.id.tv_quantity_detail);
        tv_tittle_deal = findViewById(R.id.tv_title_deal_detail);
        tv_content_deal = findViewById(R.id.tv_content_detail);
        tv_percent = findViewById(R.id.tv_percent_detail);
        tv_codedeal = findViewById(R.id.tv_code_detail);
        iv_background = findViewById(R.id.iv_background);
        iv_favour = findViewById(R.id.iv_favour_store_detail);
        iv_store = findViewById(R.id.iv_store_detail);
        btn_getdeal = findViewById(R.id.btn_getdeal_detail);
    }
}
