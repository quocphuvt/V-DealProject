package com.example.quocphu.getdealsapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.print.PrinterId;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.quocphu.getdealsapplication.model.Post;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import bolts.Task;

public class EditPostActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar toolbar;
    private EditText et_title,et_content,et_date_start,et_date_end,et_quantity;
    private Button btn_edit,btn_pickdate_start,btn_pickdate_end,btn_cancle_edit;
    private TextView tv_code;
    private ImageView iv_photo_edit;
    private Spinner sp_type;
    private ArrayList<String> list_type = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference node_post;
    private Uri uriPhoto;
    private FirebaseStorage mStorage;
    private String key_store,key_post;
    private String timeResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        toolbar = findViewById(R.id.toolbar_neweditpost);
        et_title = findViewById(R.id.et_title_editpost);
        et_content = findViewById(R.id.et_content_editpost);
        et_date_start = findViewById(R.id.et_datestart_editpost);
        et_date_end = findViewById(R.id.et_dateend_editpost);
        et_quantity = findViewById(R.id.et_quantity_editpost);
        btn_edit = findViewById(R.id.btn_editpost);
        tv_code = findViewById(R.id.tv_codedeal);
        btn_pickdate_start = findViewById(R.id.btn_pickday_start_edit);
        btn_pickdate_end = findViewById(R.id.btn_pickday_end_edit);
        sp_type = findViewById(R.id.sp_type_editpost);
        iv_photo_edit = findViewById(R.id.iv_photo_post_edit);
        btn_cancle_edit = findViewById(R.id.btn_cancle_edit);
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        //Node post
        key_post = getIntent().getExtras().getString("key_post");
        key_store = getIntent().getExtras().getString("key_store");
        node_post = database.getReference("posts").child(key_store).child("posts").child(key_post);
        node_post.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                for(DataSnapshot itemPost:dataSnapshot.getChildren()){
                    et_title.setText(dataSnapshot.getValue(Post.class).getTittle());
                    et_content.setText(dataSnapshot.getValue(Post.class).getContentPost());
                    et_date_start.setText(dataSnapshot.getValue(Post.class).getTimeStart());
                    et_date_end.setText(dataSnapshot.getValue(Post.class).getTimeEnd());
                    et_quantity.setText(dataSnapshot.getValue(Post.class).getQuantity()+"");
                    tv_code.setText(dataSnapshot.getValue(Post.class).getCodeDeal());
                    Picasso.with(EditPostActivity.this).load(dataSnapshot.getValue(Post.class).getPhotoPost()).into(iv_photo_edit);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Thêm thể loại vào spinner
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
        sp_type.setAdapter(adapter_type);
        //Sự kiện nút chọn ngày
        btn_pickdate_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditPostActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (view.isShown()) { //Kiểm tra view có được tạo chưa để tránh DatePickerDialog hiện lần 2
                            try {
                                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                                SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                                Date d = simpleDate.parse(date);
                                timeResult = simpleDate.format(d);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPostActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String timeFinal = timeResult + " " + hourOfDay + ":" + minute; //Đưa về dạng dd/MM/yyyy HH:mm
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        try {
                                            Date dateFinal = simpleDateFormat.parse(timeFinal);
                                            et_date_start.setText(simpleDateFormat.format(dateFinal));
                                        }catch (Exception e){

                                        } //Đưa về dạng dd/MM/yyyy hh:mm
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
        btn_pickdate_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditPostActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        if (view.isShown()) {
                            try {
                                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                                SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
                                Date d = simpleDate.parse(date);
                                timeResult = simpleDate.format(d);
                                TimePickerDialog timePickerDialog = new TimePickerDialog(EditPostActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        String timeFinal = timeResult + " " + hourOfDay + ":" + minute; //Đưa về dạng dd/MM/yyyy hh:mm
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                        try {
                                            Date dateFinal = simpleDateFormat.parse(timeFinal);
                                            et_date_end.setText(simpleDateFormat.format(dateFinal));
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
        iv_photo_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();
                String dateStart = et_date_start.getText().toString();
                String dateEnd = et_date_end.getText().toString();
                int quantity = Integer.parseInt(et_quantity.getText().toString());
                String type = sp_type.getSelectedItem().toString();
                uploadPhoto();
                Map<String,Object> valuePost = new HashMap<>();
                valuePost.put("tittle",title);
                valuePost.put("contentPost",content);
                valuePost.put("timeStart",dateStart);
                valuePost.put("timeEnd",dateEnd);
                valuePost.put("quantity",quantity);
                node_post.updateChildren(valuePost);
                DatabaseReference node_allpost_store = database.getReference("posts").child(key_store).child("allpost").child(key_post);
                node_allpost_store.updateChildren(valuePost);
                finish();


            }
        });

    }
    private String getFileExtension(Uri uri){
//        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getMimeTypeFromExtension(uriPhoto.toString());

    }
    private void uploadPhoto() {
        StorageReference mPostPhoto = mStorage.getReference("post");
        final StorageReference itemPost = mPostPhoto.child(System.currentTimeMillis() + ".png");
        if (uriPhoto != null) {
            UploadTask uploadTask = itemPost.putFile(uriPhoto);
            com.google.android.gms.tasks.Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, com.google.android.gms.tasks.Task<Uri>>() {
                @Override
                public com.google.android.gms.tasks.Task<Uri> then(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return itemPost.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Toast.makeText(EditPostActivity.this, "Photo updated", Toast.LENGTH_SHORT).show();
                        String urlPostPhoto = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        Map<String, Object> updatePost = new HashMap<>();
                        updatePost.put("photoPost", urlPostPhoto);
                        DatabaseReference post = database.getReference("posts").child(key_store).child("posts").child(key_post);
                        post.updateChildren(updatePost); //Thêm photo url vào posts/key_store/key_post -> photoPost
                        DatabaseReference allpost_store = database.getReference("posts").child(key_store).child("allpost").child(key_post);
                        allpost_store.updateChildren(updatePost); //Thêm photo url vào stores/key_store ->posts/key_post
                        DatabaseReference allPost = database.getReference("allpost").child(key_post);
                        allPost.updateChildren(updatePost);

                    }

                }

            });

        }
    }
    private void openFileChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Image"),111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            uriPhoto = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriPhoto);
                iv_photo_edit.setImageBitmap(bitmap);
            }catch (Exception e){

            }
        }
    }
}
