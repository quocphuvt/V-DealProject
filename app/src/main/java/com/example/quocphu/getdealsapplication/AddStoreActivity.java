package com.example.quocphu.getdealsapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.Store;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStoreActivity extends AppCompatActivity {
    private CircleImageView iv_store;
    private ImageView iv_choofile, iv_cover;
    private EditText et_name_store, et_address_store, et_location_store, et_phone_store,et_description;
    private Uri uriImage, uriCover;
    private Button btn_add, btn_cancle, btn_choose_cover;
    private StorageReference mStorageImage;
    private FirebaseStorage mStore;
    private String key_store, key_user;
    private DatabaseReference node_store;
    public static final int KITKAT_VALUE = 1002;
    Intent intent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_store);
        iv_store = findViewById(R.id.view_store_add);
        iv_choofile = findViewById(R.id.iv_choosefile);
        iv_cover = findViewById(R.id.iv_cover_store);
        et_name_store = findViewById(R.id.et_namestore_add);
        et_address_store = findViewById(R.id.et_address_add);
        et_location_store = findViewById(R.id.et_location_add);
        et_phone_store = findViewById(R.id.et_phone_add);
        et_description = findViewById(R.id.et_description_add);
        btn_add = findViewById(R.id.btn_store_add);
        btn_cancle = findViewById(R.id.btn_cancle_add);
        btn_choose_cover = findViewById(R.id.btn_choose_cover);
        toolbar = findViewById(R.id.toolbar_addstore);
        setSupportActionBar(toolbar); //Tạo toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        key_user = getIntent().getExtras().getString("key_user2");
        mStore = FirebaseStorage.getInstance(); //Khai báo firebase Storge
        mStorageImage = mStore.getReference("stores");
        node_store = FirebaseDatabase.getInstance().getReference("stores").child(key_user); //Node store database
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nameStrore = et_name_store.getText().toString();
                final String address = et_address_store.getText().toString();
                final String location = (et_location_store.getText().toString()).replace(" ", "");
                final String phoneStore = et_phone_store.getText().toString();
                final String description = et_description.getText().toString();
                if (nameStrore.isEmpty() || address.isEmpty() || location.isEmpty() || phoneStore.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AddStoreActivity.this, "Please input all...", Toast.LENGTH_SHORT).show();
                } else {
                    key_store = node_store.push().getKey();
                    uploadFile();
                    Store store = new Store(key_store, nameStrore, description, address, location, phoneStore);
                    node_store.setValue(store);
                    DatabaseReference node_user = FirebaseDatabase.getInstance().getReference("user").child(key_user);
                    Map<String, Object> updateUser = new HashMap<>();
                    updateUser.put("request", "0");
                    updateUser.put("typeAccount", "store");
                    node_user.updateChildren(updateUser);
                    Map<String, Object> valueViewed = new HashMap<>();
                    valueViewed.put("viewed", 1);
                    node_store.updateChildren(valueViewed);
                    Toast.makeText(AddStoreActivity.this, "Add Store Successfully", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }
        });
        iv_choofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        btn_choose_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooserCover();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 888);
    }

    private void openFileChooserCover() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 999);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 888 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            uriImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriImage);
                iv_store.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if(requestCode == 999 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            uriCover = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriCover);
                iv_cover.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    private String getFileExtension(Uri uri){
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }
    private void uploadFile() {
         final StorageReference coverRef = mStorageImage.child(System.currentTimeMillis() + "." + getFileExtension(uriCover));
         final StorageReference avatarRef = mStorageImage.child(System.currentTimeMillis() + "." + getFileExtension(uriImage));
        if (uriImage != null) {
            UploadTask uploadTask = avatarRef.putFile(uriImage);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return avatarRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String urlAvatar = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        Map<String,Object> updateUser = new HashMap<>();
                        updateUser.put("photoUrl",urlAvatar);
                        node_store.updateChildren(updateUser);
                        }

                }
            });
            if (uriCover != null) {
                UploadTask task = coverRef.putFile(uriCover);
                Task<Uri> uriTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return coverRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Toast.makeText(AddStoreActivity.this, "Cover uploaded", Toast.LENGTH_SHORT).show();
                            String urlCover = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            Map<String,Object> updateUser2 = new HashMap<>();
                            updateUser2.put("cover",urlCover);
                            node_store.updateChildren(updateUser2);

                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
