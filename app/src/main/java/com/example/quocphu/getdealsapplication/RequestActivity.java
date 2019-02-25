package com.example.quocphu.getdealsapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.adapter.RequestViewHolder;
import com.example.quocphu.getdealsapplication.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestActivity extends AppCompatActivity {
    public RecyclerView rv_request;
    FirebaseRecyclerOptions<User> options;
    FirebaseRecyclerAdapter<User,RequestViewHolder> adapter;
    FirebaseDatabase database;
    private Query queryRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        rv_request = findViewById(R.id.rv_request);
        final SearchView search_request = findViewById(R.id.search_request);
        database = FirebaseDatabase.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar_request);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + "Request" + "</font>")); //Đặt màu và title cho toolbar
        queryRequest = FirebaseDatabase.getInstance().getReference("user").orderByChild("request").equalTo("1");
        queryRequest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final  DataSnapshot dataSnapshot2) {
                for (DataSnapshot itemEmail : dataSnapshot2.getChildren()) {
                search_request.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Query queryEmail = dataSnapshot2.getRef().orderByChild("emailUser").startAt(newText).endAt(newText+"\uf8ff");
                        FirebaseRecyclerOptions<User> options2 = new FirebaseRecyclerOptions.Builder<User>().setQuery(queryEmail, User.class).build();
                        adapter = new FirebaseRecyclerAdapter<User, RequestViewHolder>(options2) {
                            @Override
                            protected void onBindViewHolder(final RequestViewHolder holder, final int position, User model) {
                                DatabaseReference node_user = getRef(position);
                                final String key_user = node_user.getKey();
                                if (model.getTypeAccount().equals("client")) {
                                    holder.iv_icon.setImageResource(R.drawable.client_icon);
                                }
                                holder.tv_username.setText(model.getEmailUser());
                                holder.btn_accept_request.setText("Add Store");
                                holder.btn_delete_request.setText("DENY");
                                holder.btn_accept_request.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(RequestActivity.this,StoreUserActivity.class);
                                        i.putExtra("key_user",key_user);
                                        startActivity(i);

                                    }
                                });
                                holder.btn_delete_request.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DatabaseReference node_user = database.getReference("user").child(key_user);
                                        Map<String, Object> requestValue = new HashMap<>();
                                        requestValue.put("request", "0");
                                        requestValue.put("typeAccount", "client");
                                        node_user.updateChildren(requestValue);
                                    }
                                });
                            }

                            @NonNull
                            @Override
                            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                                View view = LayoutInflater.from(RequestActivity.this).inflate(R.layout.one_item_request_store, null, false);
                                return new RequestViewHolder(view);
                            }
                        };
                        LinearLayoutManager manager = new LinearLayoutManager(RequestActivity.this, LinearLayoutManager.VERTICAL, false);
                        adapter.startListening();
                        rv_request.setLayoutManager(manager);
                        rv_request.setAdapter(adapter);
                        if (newText.isEmpty()){
                            presentAdapter(queryRequest);
                        }
                        return false;
                    }
                });
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        presentAdapter(queryRequest);




    }
    private void presentAdapter(Query queryRequest){
        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(queryRequest, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final RequestViewHolder holder, final int position, User model) {
                DatabaseReference node_user = getRef(position);
                final String key_user = node_user.getKey();
                if(model.getTypeAccount().equals("client")){
                    holder.iv_icon.setImageResource(R.drawable.client_icon);
                }
                holder.tv_username.setText(model.getEmailUser());
                holder.btn_accept_request.setText("ADD STORE");
                holder.btn_delete_request.setText("DENY");
                holder.btn_accept_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(RequestActivity.this,StoreUserActivity.class);
                        i.putExtra("key_user",key_user);
                        finish();
                        startActivity(i);
                    }
                });
                holder.btn_delete_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference node_user = database.getReference("user").child(key_user);
                        Map<String, Object> requestValue = new HashMap<>();
                        requestValue.put("request", "0");
                        requestValue.put("typeAccount", "client");
                        node_user.updateChildren(requestValue);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                });
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(RequestActivity.this).inflate(R.layout.one_item_request_store, null, false);
                return new RequestViewHolder(view);
            }
        };
        LinearLayoutManager manager = new LinearLayoutManager(RequestActivity.this, LinearLayoutManager.VERTICAL, false);
        adapter.startListening();
        rv_request.setLayoutManager(manager);
        rv_request.setAdapter(adapter);
    }

}
