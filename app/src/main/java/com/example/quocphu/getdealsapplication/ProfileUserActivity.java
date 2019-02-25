package com.example.quocphu.getdealsapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.User;
import com.facebook.AccessToken;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileUserActivity extends AppCompatActivity {
    private CircleImageView iv_userpicture;
    private TextView tv_username;
    private EditText et_fullname,et_email,et_phone,et_birthDay;
    private RadioGroup rdo_gender;
    private AppCompatRadioButton rdo_male,rdo_female;
    private Spinner sp_type_account;
    private Button btn_accept,btn_cancle,btn_pickdate;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        iv_userpicture = findViewById(R.id.iv_user_reg);
        tv_username = findViewById(R.id.tv_username_reg);
        et_fullname = findViewById(R.id.et_fullname_reg);
        et_email = findViewById(R.id.et_email_reg);
        et_phone = findViewById(R.id.et_phonenumber_reg);
        et_birthDay = findViewById(R.id.et_birhday_reg);
        rdo_gender = findViewById(R.id.rdo_gender);
        rdo_male = findViewById(R.id.rdo_male);
        rdo_female = findViewById(R.id.rdo_female);
        sp_type_account = findViewById(R.id.sp_type_reg);
        btn_accept = findViewById(R.id.btn_accept_reg);
        btn_cancle = findViewById(R.id.btn_cancle_reg);
        btn_pickdate = findViewById(R.id.btn_pickday_reg);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final List<String> type_account = new ArrayList<>();
        type_account.add("Client");
        type_account.add("Store Manager");
        ArrayAdapter<String> adapter_type = new ArrayAdapter<>(ProfileUserActivity.this,R.layout.spinner_item,type_account);
        sp_type_account.setAdapter(adapter_type);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        tv_username.setText(user.getDisplayName());
        Picasso.with(this).load(user.getPhotoUrl()).into(iv_userpicture);
        et_email.setText(user.getEmail());
        et_phone.setText(user.getPhoneNumber());
        btn_pickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileUserActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        try {
                            String date = dayOfMonth+"/"+(month+1)+"/"+year;
                            SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                            Date d = simpleDate.parse(date);
                            et_birthDay.setText(simpleDate.format(d));
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
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference node_user = database.getReference("user");
                String id_user = user.getUid();
                String fullname = et_fullname.getText().toString();
                String email = et_email.getText().toString();
                String phone = et_phone.getText().toString();
                int selectid = rdo_gender.getCheckedRadioButtonId();
                AppCompatRadioButton rdoGender = findViewById(selectid);
                String gender = rdoGender.getText().toString();
                String birthday = et_birthDay.getText().toString();
                String type_account = "admin";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy"); //Taọ pattern format
                String getCurrentTime = sdf.format(c.getTime()); //Ép time hiện tại theo pattern
                if(getCurrentTime.compareTo(birthday)==0){ //Nếu birthday trước CurrentTime trả về <0
                    Toast.makeText(ProfileUserActivity.this, "Date Error", Toast.LENGTH_SHORT).show(); //Nếu birthday bằng CurrentTime trả về =0
                }else if(fullname.isEmpty()||email.isEmpty()||phone.isEmpty()||birthday.isEmpty()||gender.isEmpty()){
                    Toast.makeText(ProfileUserActivity.this, "Please input all...", Toast.LENGTH_SHORT).show();
                } else {                                                                                         //Nếu birthday sau CurrentTime trả về >0
                    if (sp_type_account.getSelectedItemPosition() == 0) {
                        type_account = "client";
                        User user1 = new User(id_user, fullname, email, phone, gender, birthday, type_account);
                        node_user.push().setValue(user1);
                        startActivity(new Intent(ProfileUserActivity.this, MainActivity.class));
                        finish();
                    } else if (sp_type_account.getSelectedItemPosition() == 1) {
                        type_account = "null";
                        String request = "1";
                        User user1 = new User(id_user, fullname, email, phone, gender, birthday, type_account, request);
                        node_user.push().setValue(user1);
                        Toast.makeText(ProfileUserActivity.this, "Store need accept in 24h by Admin", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        AccessToken.setCurrentAccessToken(null);
                        finish();
                    }
                }




            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}
