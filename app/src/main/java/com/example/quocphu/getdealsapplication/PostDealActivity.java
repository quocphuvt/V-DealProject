package com.example.quocphu.getdealsapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.Post;
import com.example.quocphu.getdealsapplication.model.Store;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PostDealActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private EditText et_title,et_content,et_datestart,et_dateend,et_quantity,et_code,et_percent_sale;
    private Button btn_date_start,btn_date_end,btn_post,btn_cancle_post;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private DatabaseReference node_post;
    private String key_user,key_store,key_post;
    private ArrayList<String> list_type = new ArrayList<>();
    private Spinner sp_type_post;
    private Uri uriImage;
    private ImageView iv_photo;
    private FirebaseStorage mStorage;
    private String timeResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_deal);
        toolbar = findViewById(R.id.toolbar_newpost);
        et_title = findViewById(R.id.et_title_post);
        et_content = findViewById(R.id.et_content_post);
        et_datestart = findViewById(R.id.et_datestart_post);
        et_dateend = findViewById(R.id.et_dateend_post);
        et_quantity = findViewById(R.id.et_quantity_post);
        et_code = findViewById(R.id.et_codedeal);
        btn_date_start = findViewById(R.id.btn_pickday_start);
        btn_date_end = findViewById(R.id.btn_pickday_end);
        btn_post = findViewById(R.id.btn_post);
        btn_cancle_post = findViewById(R.id.btn_cancle_post);
        sp_type_post = findViewById(R.id.sp_type_post);
        iv_photo = findViewById(R.id.iv_photo_post);
        et_percent_sale = findViewById(R.id.et_percent);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        list_type.add("Electronic Devices");
        list_type.add("Electronic Accesssories");
        list_type.add("TV & Home Appliances");
        list_type.add("Health & Beauty");
        list_type.add("Babies & Toys");
        list_type.add("Home & Lifestyle");
        list_type.add("Women's Fashion");
        list_type.add("Men's Fashion");
        list_type.add("Fashion Accessories");
        list_type.add("Sport & Travel");
        list_type.add("Automotive & Motocycles");
        ArrayAdapter<String> adapter_type = new ArrayAdapter<>(this,R.layout.spinner_item,list_type);
        sp_type_post.setAdapter(adapter_type);
        btn_date_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(PostDealActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (view.isShown()) { //Kiểm tra view có được tạo chưa để tránh DatePickerDialog hiện lần 2
                            try {
                                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                                SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                                Date d = simpleDate.parse(date);
                                timeResult = simpleDate.format(d);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(PostDealActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String timeFinal = timeResult + " " + hourOfDay + ":" + minute; //Đưa về dạng dd/MM/yyyy hh:mm
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        try {
                                            Date dateFinal = simpleDateFormat.parse(timeFinal);
                                            et_datestart.setText(simpleDateFormat.format(dateFinal));
                                        }catch (Exception e){

                                        }
                                    }
                                }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
                                timePickerDialog.show();
                            } catch (Exception e) {

                            }

                        }
                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }

        });
        btn_date_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(PostDealActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (view.isShown()) {
                            try {
                                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                                SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                                Date d = simpleDate.parse(date);
                                timeResult = simpleDate.format(d);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(PostDealActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String timeFinal = timeResult + " " + hourOfDay + ":" + minute; //Đưa về dạng dd/MM/yyyy hh:mm
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        try {
                                            Date dateFinal = simpleDateFormat.parse(timeFinal);
                                            et_dateend.setText(simpleDateFormat.format(dateFinal));
                                        }catch (Exception e){

                                        }
                                    }
                                }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);
                                timePickerDialog.show();

                            } catch (Exception e) {

                            }

                        }
                    }
                },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();

                }
        });
        final DatabaseReference node_allPost = database.getReference("allpost"); //Tạo node allpost để hiển thị tất cả post bên khách hàng
        iv_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooserPhoto();
            }
        });
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = et_title.getText().toString();
                final String content = et_content.getText().toString();
                final String dateStart = et_datestart.getText().toString();
                final String dateEnd = et_dateend.getText().toString();
                final int quantity = Integer.parseInt(et_quantity.getText().toString());
                final String codeDeal = et_code.getText().toString();
                final String typePost = sp_type_post.getSelectedItem().toString();
                final Double percentSale = Double.parseDouble(et_percent_sale.getText().toString());
                final String statePost = "AVAILABLE";
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm"); //vd: Wed, 4 Jul 2001, 12:08
                String currentTime = df.format(Calendar.getInstance().getTime());
                final String datePost = currentTime; //Time post bài là hiện tại
                try {
                    Date timeEnd = df.parse(dateEnd);
                    Date current = df.parse(currentTime);
                    if(title.isEmpty() || content.isEmpty() || dateStart.isEmpty() || dateEnd.isEmpty() || quantity==0 || percentSale==0 || codeDeal.isEmpty()){
                        Toast.makeText(PostDealActivity.this, "Please input all", Toast.LENGTH_SHORT).show();
                    }
                    else if(current.compareTo(timeEnd)>=0){
                        et_dateend.setError("Date end must than date strart");
                    }
                    else {
                        Query node_user = database.getReference("user").orderByChild("id_user").equalTo(currentUser.getUid());
                        node_user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot itemUser:dataSnapshot.getChildren()){
                                    key_user = itemUser.getKey();
                                }
                                final DatabaseReference node_store = database.getReference("stores").child(key_user);
                                node_store.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot itemStore: dataSnapshot.getChildren()){
                                            key_store = dataSnapshot.getKey();
                                        }
                                        DatabaseReference node_post = database.getReference("posts").child(key_store);
                                        key_post = node_post.push().getKey();
                                        Post post = new Post(key_post,title,content,typePost,datePost,dateStart,dateEnd,quantity,codeDeal,"Available",percentSale);
                                        uploadFile(key_store,key_post);
                                        node_post.child("posts").child(key_post).setValue(post); //Thêm dữ liệu vào node post
                                        node_post.child("allpost").child(key_post).setValue(post); //Thêm dữ liệu posts vào store
                                        node_allPost.child(key_post).setValue(post);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }catch (Exception e){

                }



             }

        });
        btn_cancle_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    //Hàm lấy tên đuổi mở rộng của file
    private String getFileExtension(Uri uri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }
    private void chooserPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 888);
    }
    private void uploadFile(final String key_store, final String key_post){
        StorageReference mPostPhoto = mStorage.getReference("post");
            final StorageReference postPhoto = mPostPhoto.child(System.currentTimeMillis() + "." + getFileExtension(uriImage));
            if (uriImage != null) {
                UploadTask uploadTask = postPhoto.putFile(uriImage);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // Continue with the task to get the download URL
                        return postPhoto.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Toast.makeText(PostDealActivity.this, "Photo uploaded", Toast.LENGTH_SHORT).show();
                            String urlPostPhoto = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            Map<String,Object> updatePost = new HashMap<>();
                            updatePost.put("photoPost",urlPostPhoto);
                            DatabaseReference post = database.getReference("posts").child(key_store).child("posts").child(key_post);
                            post.updateChildren(updatePost); //Thêm photo url vào posts/key_store/key_post -> photoPost
                            DatabaseReference allpost_store = database.getReference("posts").child(key_store).child("allpost").child(key_post);
                            allpost_store.updateChildren(updatePost);
                            DatabaseReference allPost =database.getReference("allpost").child(key_post);
                            allPost.updateChildren(updatePost);

                        }

                    }
                });
    }}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 888 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            uriImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                iv_photo.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
