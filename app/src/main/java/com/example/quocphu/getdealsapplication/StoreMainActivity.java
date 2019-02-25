package com.example.quocphu.getdealsapplication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.service.CheckDatePostService;
import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class StoreMainActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private TabLayout tab_main;
    private ViewPager vPager;
    private FirebaseAuth firebaseAuth;
    private CoordinatorLayout htab_maincontent;
    private ConstraintLayout htab_header;
    private Animation hide_out;
    private FloatingActionButton fab_newpost,fab_storedetail,fab_checkdeal,fab_main;
    private boolean actionFab = false;
    private ImageView iv_background;
    private String key_store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_main);
        toolbar = findViewById(R.id.toolbar);
        tab_main = findViewById(R.id.htab_tabs);
        vPager = findViewById(R.id.htab_viewpager);
        htab_maincontent = findViewById(R.id.htab_maincontent);
        htab_header = findViewById(R.id.htab_header);
        fab_main = findViewById(R.id.fab_main);
        fab_newpost = findViewById(R.id.fab_newpost);
        iv_background = findViewById(R.id.iv_background_store);
        fab_storedetail = findViewById(R.id.fab_storedetail);
        fab_checkdeal = findViewById(R.id.fab_checkdeal);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        //Node user lấy key_store = key_user;
        Query queryUser = FirebaseDatabase.getInstance().getReference("user").orderByChild("id_user").equalTo(user.getUid());
        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    key_store = item.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionFab==false){
                    showFab();
                    actionFab = true;
                }else {
                    hideFab();
                    actionFab=false;
                }
            }
        });
        fab_newpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StoreMainActivity.this, PostDealActivity.class);
                i.putExtra("key_store",key_store);
                startActivity(i);
            }
        });
        fab_checkdeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StoreMainActivity.this, StoreCheckDealActivity.class);
                i.putExtra("key_store",key_store);
                startActivity(i);
            }
        });
        final ImageView iv_user_main = findViewById(R.id.view_user_store_detail);
        final TextView tv_username_main = findViewById(R.id.tv_username_main);
        Picasso.with(StoreMainActivity.this).load(user.getPhotoUrl()).fit().centerCrop().into(iv_user_main);
        tv_username_main.setText(user.getEmail());
        tab_main.addTab(tab_main.newTab().setText("NEW POST"));
        tab_main.addTab(tab_main.newTab().setText("RECENT"));
        MyFragmentAdapter fragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        vPager.setAdapter(fragmentAdapter);
        vPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_main));
        tab_main.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vPager.setCurrentItem(tab.getPosition()); //set Frament tại vị trí tab thứ ....
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tab_main.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int positon = tab.getPosition();
                android.support.v4.app.Fragment fragment = null;
                Class classFragment = null;
                hide_out = AnimationUtils.loadAnimation(StoreMainActivity.this,R.anim.hide_main);
                if(positon==0){
                    classFragment = MapFragment.class;

                }
                if (positon==1){

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(StoreMainActivity.this,ScreenSignActivity.class));
            firebaseAuth.signOut();
            AccessToken.setCurrentAccessToken(null);
        }
        return super.onOptionsItemSelected(item);
    }
    //Class Adapter Viewpager
    class MyFragmentAdapter extends FragmentStatePagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position){
                case 0: fragment = new NewPostFragment();
                    break;
                case 1: fragment = new NewPostFragment();
                    break;
                default: return null;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2; //Trả về số fragment tương ứng với số Tab
        }
    }
    private void showFab(){
        fab_newpost.show();
        fab_storedetail.show();
        fab_checkdeal.show();
    }
    private void hideFab(){
        fab_checkdeal.hide();
        fab_newpost.hide();
        fab_storedetail.hide();
    }
}
