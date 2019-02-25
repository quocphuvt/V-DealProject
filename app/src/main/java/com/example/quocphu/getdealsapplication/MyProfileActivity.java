package com.example.quocphu.getdealsapplication;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {
    EditText et_fullname_prof,et_phonenumber_prof,et_birthday_pro;
    Button btn_accept_prof,btn_cancle_prof,btn_pickday_prof;
    CircleImageView iv_prof;
    TextView tv_username_prof,tv_type_prof;
    String key_user = "";
    Toolbar toolbar_myprofile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        et_fullname_prof = findViewById(R.id.et_fullname_prof);
        et_birthday_pro = findViewById(R.id.et_birhday_prof);
        et_phonenumber_prof = findViewById(R.id.et_number_prof);
        btn_accept_prof = findViewById(R.id.btn_accept_prof);
        btn_cancle_prof = findViewById(R.id.btn_cancle_prof);
        tv_username_prof= findViewById(R.id.tv_username_prof);
        tv_type_prof = findViewById(R.id.tv_type_prof);
        iv_prof = findViewById(R.id.iv_user_prof);
        btn_pickday_prof = findViewById(R.id.btn_pickday_prof);
        toolbar_myprofile = findViewById(R.id.toolbar_myprofile);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Picasso.with(this).load(user.getPhotoUrl()).into(iv_prof);
        tv_username_prof.setText(user.getDisplayName());
        setSupportActionBar(toolbar_myprofile);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Query query_user = FirebaseDatabase.getInstance().getReference("user").orderByChild("id_user").equalTo(user.getUid());
        query_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemUser: dataSnapshot.getChildren()){
                    key_user = itemUser.getKey();
                    User user1 = itemUser.getValue(User.class);
                    tv_type_prof.setText(user1.getTypeAccount());
                    et_fullname_prof.setText(user1.getFullName());
                    et_phonenumber_prof.setText(user1.getPhoneUser());
                    et_birthday_pro.setText(user1.getBirthday());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btn_pickday_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(MyProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        try {
                            String date = year+"-"+(month+1)+"-"+dayOfMonth;
                            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
                            Date d = simpleDate.parse(date);
                            et_birthday_pro.setText(simpleDate.format(d));
                        }catch (Exception e){

                        }

                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        btn_accept_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name_update = et_fullname_prof.getText().toString();
                String phone_update = et_phonenumber_prof.getText().toString();
                String birthday_update= et_birthday_pro.getText().toString();
                final DatabaseReference node_user = FirebaseDatabase.getInstance().getReference("user").child(key_user);
                final Map<String,Object> userValue = new HashMap<>();
                userValue.put("fullName",name_update);
                userValue.put("phoneUser",phone_update);
                userValue.put("birthday",birthday_update);
                Snackbar snackbar = Snackbar.make(v, "Do you want to update your profile?",5000);
                snackbar.setAction("Accept", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        node_user.updateChildren(userValue);
                        finish();
                        Toast.makeText(MyProfileActivity.this, "Update profile success", Toast.LENGTH_SHORT).show();
                    }
                }).show();

            }
        });
        btn_cancle_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
