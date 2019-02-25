package com.example.quocphu.getdealsapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.Store;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreUserActivity extends AppCompatActivity {
    private TextView tv_nameStore,tv_phone,tv_viewed,tv_address,tv_sumpost,tv_description;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FloatingActionButton fab_addStore;
    private Uri imageUri;
    private ImageView iv_cover;
    private Button btn_seepost,btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_user);
        tv_nameStore = findViewById(R.id.tv_name_store);
        tv_description = findViewById(R.id.tv_description_store);
        tv_phone = findViewById(R.id.tv_phone_store);
        tv_viewed = findViewById(R.id.tv_viewed_store);
        tv_address = findViewById(R.id.tv_address_store);
        tv_sumpost = findViewById(R.id.tv_sum_post_store);
        fab_addStore = findViewById(R.id.fab_addStore);
        iv_cover = findViewById(R.id.iv_cover_store_user);
        btn_seepost = findViewById(R.id.btn_seepost_store);
        btn_back = findViewById(R.id.btn_back);
        Toolbar toolbar = findViewById(R.id.toolbar_store_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        final CircleImageView view_user = (CircleImageView)findViewById(R.id.view_user_store_detail);
        final TextView tv_user_store = findViewById(R.id.tv_user_store);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final String key_user = getIntent().getExtras().getString("key_user");
        final String key_store = getIntent().getExtras().getString("key_store");
        if(key_user != null) {
            final DatabaseReference node_store = database.getReference("stores");
            node_store.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(key_user)) {
                        Toast.makeText(StoreUserActivity.this, "Null store", Toast.LENGTH_SHORT).show();
                        tv_sumpost.setText("null");
                        tv_viewed.setText("null");
                        tv_description.setText("null");
                        tv_address.setText("null");
                        tv_phone.setText("null");
                        fab_addStore.show();
                        view_user.setImageResource(R.drawable.icon_market);
                    } else {
                        node_store.child(key_user).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot itemStore : dataSnapshot.getChildren()) {
                                    Store store = dataSnapshot.getValue(Store.class);
                                    tv_nameStore.setText(store.getNameStore());
                                    tv_phone.setText(store.getPhoneStore());
                                    tv_address.setText(store.getAddress());
                                    tv_description.setText(store.getDescription());
                                    tv_viewed.setText(store.getViewed()+"");
                                    Picasso.with(StoreUserActivity.this).load(store.getPhotoUrl()).into(view_user);
                                    Picasso.with(StoreUserActivity.this).load(store.getCover()).into(iv_cover);
                                    DatabaseReference node_posts = database.getReference("posts").child(key_user).child("allpost");
                                    node_posts.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int count=0;
                                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                                count++;
                                                tv_sumpost.setText(count+"");
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

                        fab_addStore.hide();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            btn_seepost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(StoreUserActivity.this, StoreAllPostActivity.class);
                    i.putExtra("key_user_store", key_user);
                    startActivity(i);
                }
            });
            fab_addStore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(StoreUserActivity.this, AddStoreActivity.class);
                    i.putExtra("key_user2", key_user);
                    startActivity(i);

                }
            });
        }
        //Key_store tồn tại khi Mapfragment startIntent
        if(key_store!=null){
            fab_addStore.hide();
            DatabaseReference node_store = database.getReference("stores").child(key_store);
            node_store.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot itemStore: dataSnapshot.getChildren()){
                        Store store = dataSnapshot.getValue(Store.class);
                        Picasso.with(StoreUserActivity.this).load(store.getCover()).into(iv_cover);
                        Picasso.with(StoreUserActivity.this).load(store.getPhotoUrl()).into(view_user);
                        tv_user_store.setText(store.getAddress());
                        tv_phone.setText(store.getPhoneStore());
                        tv_nameStore.setText(store.getNameStore());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            startActivity(new Intent(StoreUserActivity.this,RequestActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

