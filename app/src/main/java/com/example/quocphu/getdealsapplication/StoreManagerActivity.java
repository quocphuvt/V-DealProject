package com.example.quocphu.getdealsapplication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.quocphu.getdealsapplication.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class StoreManagerActivity extends AppCompatActivity {
    RecyclerView rv_account;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manager);
        rv_account = findViewById(R.id.rv_account);
        database = FirebaseDatabase.getInstance();
        Query node_user = database.getReference("user").orderByChild("typeAccount").equalTo("store");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(node_user,User.class).build();
        FirebaseRecyclerAdapter<User,StoreViewHolder> adapter = new FirebaseRecyclerAdapter<User,StoreViewHolder>(options) {
            @Override
            protected void onBindViewHolder(StoreViewHolder holder, int position, User model) {
                holder.tv_username_store.setText(model.getEmailUser());
                
            }

            @NonNull
            @Override
            public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(StoreManagerActivity.this).inflate(R.layout.one_item_request_store,viewGroup,false);
                return new StoreViewHolder(itemView);
            }
        };
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter.startListening();
        rv_account.setLayoutManager(manager);
        rv_account.setAdapter(adapter);

    }
    private class StoreViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_username_store;
        private Button btn_accept,btn_delete;
        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username_store = itemView.findViewById(R.id.tv_username_request);
            btn_accept = itemView.findViewById(R.id.btn_accept_request);
            btn_delete = itemView.findViewById(R.id.btn_delete_request);
        }
    }
}
