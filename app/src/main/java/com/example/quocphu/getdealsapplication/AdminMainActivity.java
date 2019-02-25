package com.example.quocphu.getdealsapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.zip.Inflater;

public class AdminMainActivity extends AppCompatActivity {
    ImageView iv_request,iv_account_manager,iv_store_manager;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        iv_request = findViewById(R.id.iv_request_manager);
        iv_account_manager = findViewById(R.id.iv_account_manager);
        iv_store_manager = findViewById(R.id.iv_store_manager);
        toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#ffffff\">" + getString(R.string.app_name) + "</font>"));
        iv_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, RequestActivity.class));
                Toast.makeText(AdminMainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        });
        iv_account_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this,AccountManagerActivity.class));
            }
        });
        iv_store_manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this,StoreManagerActivity.class));
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logout_admin){
            AccessToken.setCurrentAccessToken(null);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_admin,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
