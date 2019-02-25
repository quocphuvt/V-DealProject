package com.example.quocphu.getdealsapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DrawerLayout drawer;
    private NavigationView navigation;
    private android.support.v7.widget.Toolbar toolbar;
    public TabLayout tab_main;
    private ViewPager vPager;
    private FirebaseAuth firebaseAuth;
    private CoordinatorLayout htab_maincontent;
    private Animation hide_out;
    private LinearLayout view_home,view_profile,view_favour,view_signout;
    private ConstraintLayout view_deals;
    private FirebaseDatabase database;
    private TextView tv_count_deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent intentService = new Intent(this, CheckDatePostService.class);
//        startService(intentService);
        navigation = findViewById(R.id.nvView);
        toolbar = findViewById(R.id.toolbar);
        tab_main = findViewById(R.id.htab_tabs);
        vPager = findViewById(R.id.htab_viewpager);
        htab_maincontent = findViewById(R.id.htab_maincontent);
        drawer = findViewById(R.id.drawer_layout);
        view_home = findViewById(R.id.view_home_draw);
        view_deals = findViewById(R.id.view_mydeal_draw);
        view_favour = findViewById(R.id.view_favour_draw);
        view_profile = findViewById(R.id.view_myprofile_draw);
        view_signout = findViewById(R.id.view_sign_out_draw);
        tv_count_deal = findViewById(R.id.tv_count_deal_draw);
        //iv và tv chính
        final ImageView iv_user_main = findViewById(R.id.view_user_store_detail);
        final TextView tv_username_main = findViewById(R.id.tv_username_main);
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Picasso.with(MainActivity.this).load(user.getPhotoUrl()).into(iv_user_main);
            tv_username_main.setText(user.getEmail());

        }
        tab_main.addTab(tab_main.newTab().setText("MAP"));
        tab_main.addTab(tab_main.newTab().setText("STORES"));
        tab_main.addTab(tab_main.newTab().setText("DEALS"));
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
                hide_out = AnimationUtils.loadAnimation(MainActivity.this, R.anim.hide_main);
                if (positon == 0) {
                    classFragment = MapFragment.class;
                }
                if (positon == 1) {
                    classFragment = StoreFragment.class;
                }
                if (positon == 2) {
                    classFragment = DealFragment.class;
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
        view_home.setOnClickListener(this);
        view_signout.setOnClickListener(this);
        view_profile.setOnClickListener(this);
        view_favour.setOnClickListener(this);
        view_deals.setOnClickListener(this);
        Query queryUser = database.getReference("user").orderByChild("id_user").equalTo(user.getUid());

        queryUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    DatabaseReference node_user =database.getReference("user").child(item.getKey()).child("list_deal").child("valid");
                    node_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot!=null){
                                int count=0;
                                for(DataSnapshot item: dataSnapshot.getChildren()){
                                    count++;
                                    tv_count_deal.setText(count+"");
                                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            drawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        android.support.v4.app.Fragment fragment = null;
        Class classFragment = null;
        switch (v.getId()){
            case R.id.view_home_draw:
                Toast.makeText(this, "HOME", Toast.LENGTH_SHORT).show();
            break;
            case R.id.view_myprofile_draw: startActivity(new Intent(MainActivity.this,MyProfileActivity.class));
            break;
            case R.id.view_mydeal_draw:
                startActivity(new Intent(this,MyDealActivity.class ));
                break;
            case R.id.view_favour_draw:
//                startActivity(new Intent(this, ));
                break;
            case R.id.view_sign_out_draw:
                FirebaseAuth.getInstance().signOut();
                AccessToken.setCurrentAccessToken(null);
                startActivity(new Intent(this,ScreenSignActivity.class));
                finish();
                break;
                default: return;
        }
    }

    //Class Adapter Viewpager
    class MyFragmentAdapter extends FragmentStatePagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = new MapFragment();
                    break;
                case 1:
                    fragment = new StoreFragment();
                    break;
                case 2:
                    fragment = new DealFragment();
                    break;
                default:
                    return null;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3; //Trả về số fragment tương ứng với số Tab
        }
    }
}
