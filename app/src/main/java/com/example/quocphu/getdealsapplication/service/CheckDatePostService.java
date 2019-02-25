package com.example.quocphu.getdealsapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckDatePostService extends Service {
    private FirebaseDatabase database;
    public Handler handler=new Handler(); //Tạo luồng hander
    boolean isRunning=false;
    Runnable chay;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service khoi tao", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(chay);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning=true;
        tangthoigian();
        return START_NOT_STICKY;
    }
    public void tangthoigian()
    {
        if(isRunning==true)
        {
            chay=new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    runMethod();checkQuantityPost();
                    tangthoigian();
                };
            };
            handler.postDelayed(chay,1000);
        }
    }

    private void runMethod(){
            // TODO Auto-generated method stub
            database = FirebaseDatabase.getInstance();
            final DatabaseReference node_post = database.getReference("allpost");
            final Calendar calendar = Calendar.getInstance();
            final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm");
            try {
                final Date current = sdf.parse(sdf.format(calendar.getTime()));
                node_post.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            for (DataSnapshot itemAllPost : dataSnapshot.getChildren()) {
                                final String key_post = itemAllPost.getKey();
                                Post post = itemAllPost.getValue(Post.class);
                                try {
                                    Date timeEnd = sdf.parse(post.getTimeEnd());
                                    if (timeEnd.after(current) == false) { //Kiểm tra dateEnd có sau thời gian hiện tại hay ko. Nếu không thỳ false
                                        node_post.child(key_post).removeValue();
                                        final DatabaseReference node_post2 = database.getReference("posts"); //Truy vào posts kiểm tra có key quá hạn và xóa đi
                                        node_post2.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot != null) {
                                                    for (DataSnapshot itemPost : dataSnapshot.getChildren()) {
                                                        String key_store = itemPost.getKey();
                                                        final DatabaseReference node_item_post = node_post2.child(key_store).child("posts");
                                                        node_item_post.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot != null) {
                                                                    if (dataSnapshot.hasChild(key_post)) {
                                                                        node_item_post.child(key_post).removeValue();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        DatabaseReference node_store = database.getReference("posts").child(key_store).child("allpost").child(key_post);
                                                        final Map<String, Object> mapStore = new HashMap<>();
                                                        mapStore.put("statePost", "EXPIRED");
                                                        node_store.updateChildren(mapStore);
                                                        final DatabaseReference node_user = database.getReference("user");
                                                        node_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot != null) {
                                                                    for (DataSnapshot itemUser : dataSnapshot.getChildren()) {
                                                                        final DatabaseReference queryUser = node_user.child(itemUser.getKey()).child("list_deal");
                                                                        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                            @Override
                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                if (dataSnapshot != null) {
                                                                                    if (dataSnapshot.hasChild(key_post)) {
                                                                                        queryUser.child(key_post).updateChildren(mapStore);
                                                                                    }
                                                                                }
                                                                            }

                                                                            @Override
                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });

                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
//                                        else if(current.getTime()-timeEnd.getTime()<900000 && current.getTime()-timeEnd.getTime()>0){ //Thời gian hiện tại - thời gian dateEnd = millisecond. Nếu nó bé hơn 900000 (dateEnd còn 5 phút là hết deal)
//                                            Toast.makeText(CheckDatePostService.this, "sap het han"+key_post, Toast.LENGTH_SHORT).show();//Tương đương 5 phút = 1000ml/s * 60s * 5p
//
//                                        }
                                    else {

                                    }
                                    //  0 comes when two date are same,
                                    //  1 comes when date1 is higher then date2
                                    // -1 comes when date1 is lower then date2
                                } catch (Exception e) {

                                }

                            }
                        }
                    }

                        @Override
                        public void onCancelled (@NonNull DatabaseError databaseError){

                        }
                });
            }catch (Exception e){
                Log.d("erro",e+"");
            }
        }
        private void checkQuantityPost(){
        final Map<String,Object> valuePost = new HashMap<>();
        valuePost.put("statePost","SOLD OUT");
        final DatabaseReference node_posts = database.getReference("posts");
        node_posts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item_post : dataSnapshot.getChildren()){
                    final String key_store = item_post.getKey();
                    Query node_post_store = node_posts.child(key_store).child("posts").orderByChild("quantity").equalTo(0);
                    node_post_store.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot item : dataSnapshot.getChildren()){
                                node_posts.child(key_store).child("posts").child(item.getKey()).removeValue(); //Xóa post trong posts/key_store/posts/key_post
                                DatabaseReference node_allpost = database.getReference("allpost").child(item.getKey());
                                node_allpost.removeValue(); //Xóa post trong allpost
                                DatabaseReference node_allpost_store= database.getReference("posts").child(key_store).child("allpost").child(item.getKey());
                                node_allpost_store.updateChildren(valuePost);
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


}
