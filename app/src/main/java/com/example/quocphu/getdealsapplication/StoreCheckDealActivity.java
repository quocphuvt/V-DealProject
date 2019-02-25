package com.example.quocphu.getdealsapplication;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.quocphu.getdealsapplication.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StoreCheckDealActivity extends AppCompatActivity {
    private SearchView sv_search;
    private RecyclerView rv_listuser;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private String key_store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_check_deal);
        sv_search = findViewById(R.id.sv_search_user_check);
        rv_listuser = findViewById(R.id.rv_list_user_check);
        database = FirebaseDatabase.getInstance();
        key_store = getIntent().getExtras().getString("key_store");
        sv_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query queryUser = database.getReference("user").orderByChild("emailUser").startAt(newText).endAt(newText+"\uf8ff");
                FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(queryUser,User.class).build();
                FirebaseRecyclerAdapter<User,StoreCheckDealViewHolder>adapter = new FirebaseRecyclerAdapter<User, StoreCheckDealViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(StoreCheckDealViewHolder holder, int position, User model) {
                        final String key_user = getRef(position).getKey();
                        holder.tv_username.setText(model.getEmailUser());
                        holder.btn_check.setText("Choose Deal");
                        holder.btn_check.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Query queryuser = database.getReference("user").child(key_user).child("list_deal").child("valid")
                                        .orderByChild("keystore").equalTo(key_store);

                                AlertDialog.Builder builder = new AlertDialog.Builder(StoreCheckDealActivity.this);
                                LayoutInflater inflater = getLayoutInflater();
                                View view = inflater.inflate(R.layout.dialog_checkdeal,null,false);
                                builder.setView(view);
                                builder.show();
                                builder.setPositiveButton("Choose deal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Dialog dialogOj = (Dialog.class).cast(dialog);
                                        Spinner sp_deal = dialogOj.findViewById(R.id.sp_listdeal);
                                        final TextView tv_username = dialogOj.findViewById(R.id.tv_username_check);
                                        TextView tv_email = dialogOj.findViewById(R.id.tv_email_check);
                                        TextView tv_birhday = dialogOj.findViewById(R.id.tv_birthday_check);
                                        TextView tv_gender = dialogOj.findViewById(R.id.tv_gender_check);
                                        tv_username.setText("LEQUOCPHU");
                                        queryuser.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public StoreCheckDealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(StoreCheckDealActivity.this).inflate(R.layout.one_item_request_store,viewGroup,false);
                        return new StoreCheckDealViewHolder(view);
                    }
                };
                LinearLayoutManager manager = new LinearLayoutManager(StoreCheckDealActivity.this,LinearLayoutManager.VERTICAL,false);
                rv_listuser.setLayoutManager(manager);
                rv_listuser.setHasFixedSize(true);
                adapter.startListening();
                rv_listuser.setAdapter(adapter);

                return false;
            }
        });



    }
    private class StoreCheckDealViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_username;
        private Button btn_check;
        private ImageView iv_icon;
        Context context;
        public StoreCheckDealViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = itemView.findViewById(R.id.tv_username_request);
            btn_check = itemView.findViewById(R.id.btn_accept_request);
            iv_icon = itemView.findViewById(R.id.iv_icon_store);

        }
    }
}
