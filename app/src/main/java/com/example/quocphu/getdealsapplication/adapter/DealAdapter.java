//package com.example.quocphu.getdealsapplication.adapter;
//
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.quocphu.getdealsapplication.R;
//import com.example.quocphu.getdealsapplication.model.Post;
//import com.example.quocphu.getdealsapplication.model.Store;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.squareup.picasso.Picasso;
//
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
//    private Context context;
//    private LinkedHashMap<String,Post> posts;
//    private Object[] keys;
//    private String key_user;
//
//
//    public DealAdapter(Context context,LinkedHashMap<String,Post> posts,String key_user) {
//        this.context = context;
//        this.posts = posts;
//        keys  = posts.keySet().toArray(); //Lấy key dựa vào vị trí
//        this.key_user = key_user;
//    }
//
//    @NonNull
//    @Override
//    public DealAdapter.DealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(context).inflate(R.layout.one_item_deal,viewGroup,false);
//        return new DealViewHolder(view);
//    }
//    class DealViewHolder extends RecyclerView.ViewHolder{
//        private ImageView iv_photo_deal,iv_state_deal;
//        private TextView tv_title_deal,tv_name_store,tv_state_deal;
//        private Button btn_getDeal;
//        public DealViewHolder(@NonNull View itemView) {
//            super(itemView);
//            iv_photo_deal = itemView.findViewById(R.id.iv_photo_deal);
//            tv_name_store = itemView.findViewById(R.id.tv_namestore_deal_post);
//            tv_title_deal = itemView.findViewById(R.id.tv_title_deal_post);
//            iv_state_deal = itemView.findViewById(R.id.iv_state_deal);
//            tv_state_deal = itemView.findViewById(R.id.tv_state_deal);
//            btn_getDeal = itemView.findViewById(R.id.btn_getdeal);
//
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final DealViewHolder holder, final int i) {
////        for(String key: posts.keySet()){
////            Post post =  posts.get(key);
////            holder.tv_title_deal.setText(post.getTittle());
////            //some method to get that 25 value
////        }
////        Random r = new Random();
////        final int i1 = r.nextInt(posts.size()); //nextInt(int n) method return a random number between 0 and provided integer value.
//        final String keyPost = keys[i].toString(); //Lấy key post dựa vào vị trí adapter
//        final DatabaseReference node_post = FirebaseDatabase.getInstance().getReference("posts");
//        final DatabaseReference node_store = FirebaseDatabase.getInstance().getReference("stores");
//        final DatabaseReference node_user = FirebaseDatabase.getInstance().getReference("user").child(key_user);
//        holder.tv_title_deal.setText(posts.get(keys[i]).getTittle().toString());//Map post lấy tittle dựa vào key
//        Picasso.with(context).load(posts.get(keys[i]).getPhotoPost()).into(holder.iv_photo_deal);
//        final Post post = posts.get(keys[i]);
//        //Lấy thông tin store và thông tin post thuộc store đó
//        node_post.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot != null) {
//                    for (DataSnapshot item : dataSnapshot.getChildren()) {
//                        final String key_store = item.getKey();
//                       //Đặt final tránh key_store bị thay đổi khi chạy vòng lập For
//                        DatabaseReference node_post2 = node_post.child(key_store).child("posts");
//                        node_post2.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if (dataSnapshot != null) {
//                                    if (dataSnapshot.hasChild(keyPost)) { //Kiểm tra key tồn tại child(key) (key post) lấy được key store
//                                        DatabaseReference node_store = FirebaseDatabase.getInstance().getReference("stores").child(key_store);
//                                        node_store.addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                if (dataSnapshot != null) {
//                                                    for (DataSnapshot itemStore : dataSnapshot.getChildren()) {
//                                                        Store store = dataSnapshot.getValue(Store.class);
//                                                        holder.tv_name_store.setText(store.getNameStore());
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        holder.btn_getDeal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String keyPost2 = keys[i].toString();
//                Toast.makeText(context, "OK deal =))", Toast.LENGTH_SHORT).show();
//                node_user.child("userdeal").addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot != null) {
//                            if (dataSnapshot.hasChild(keyPost2)) { //Kiểm tra xem user có keypost tồn tại chưa vì 1 user get 1 deal;
//                                Toast.makeText(context, "You only get once a deal", Toast.LENGTH_SHORT).show();
//                            } else {
//                                final DatabaseReference all_post = FirebaseDatabase.getInstance().getReference("allpost").child(keyPost2);
//                                final Map<String, Object> mapPost = new HashMap<>();
//                                mapPost.put("quantity", (posts.get(keys[i]).getQuantity()) - 1);
//                                node_user.child("userdeal").child(keyPost2).setValue(post); //Update User/userdeal
//                                node_post.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        for(DataSnapshot item_post : dataSnapshot.getChildren()){
//                                            final String key_store = item_post.getKey();
//                                            //Đặt final tránh key_store bị thay đổi khi chạy vòng lập For
//                                            DatabaseReference node_post2 = node_post.child(key_store).child("posts");
//                                            node_post2.addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    if (dataSnapshot != null) {
//                                                        if (dataSnapshot.hasChild(keyPost2)) { //Kiểm tra key tồn tại child(key) (key post) lấy được key store
//                                                            all_post.updateChildren(mapPost); //Update allpost
//                                                            node_post.child(key_store).child("posts").child(keyPost2).updateChildren(mapPost); //Update post/keystore/post/keypost
//                                                            node_post.child(key_store).child("allpost").child(keyPost2).updateChildren(mapPost); //Update post/keystore/allpost/keypost
//                                                            notifyItemChanged(i);
//                                                        }
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                }
//                                            });
//
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        });
//
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return posts.size();
//    }
//
//}
