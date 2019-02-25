package com.example.quocphu.getdealsapplication;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.Store;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {
    private RecyclerView rv_store;
    private FirebaseDatabase database;
    private FirebaseRecyclerOptions<Store> options;
    private FirebaseRecyclerAdapter<Store,StoreViewHolder> adapter;
    private int count=0;
    public StoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        rv_store = view.findViewById(R.id.rv_store);
        database = FirebaseDatabase.getInstance();
        final DatabaseReference node_store = database.getReference("stores");
        options = new FirebaseRecyclerOptions.Builder<Store>().setQuery(node_store,Store.class).build();
        adapter = new FirebaseRecyclerAdapter<Store, StoreViewHolder>(options) {
            @Override
            protected void onBindViewHolder(final StoreViewHolder holder, int position, final Store model) {
                final String key_store = getRef(position).getKey();
                holder.tv_namestore.setText(model.getNameStore());
                holder.tv_address.setText(model.getAddress());
                holder.tv_phone.setText(model.getPhoneStore());
                holder.tv_viewed.setText(model.getViewed()+" viewed");
                Picasso.with(getContext()).load(model.getPhotoUrl()).fit().centerCrop().into(holder.iv_store);
                Picasso.with(getContext()).load(model.getCover()).fit().centerCrop().into(holder.iv_background);
//                holder.tv_type_store.setText(model.getType);
                DatabaseReference node_store2 = database.getReference("posts").child(key_store).child("posts");
                node_store2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot item : dataSnapshot.getChildren()){
                            count++;
                            holder.tv_sumpost.setText("Posts: "+count);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.btn_visit_store.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), StoreUserActivity.class);
                        Map<String,Object> valueStore = new HashMap<>();
                        int viewed = model.getViewed();
                        valueStore.put("viewed",model.getViewed()+1);
                        node_store.child(key_store).updateChildren(valueStore);
                        i.putExtra("key_store",key_store);
                        startActivity(i);
                    }
                });
                holder.btn_seemap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TabLayout tabs = (TabLayout)((MainActivity)getActivity()).findViewById(R.id.htab_tabs); //Tham chiếu tới widget TabLayout Mainactivity
                        tabs.getTabAt(0).select(); //Chọn tab item tại vị trí 0 (MapFragment)
//                        Bundle b = new Bundle();
//                        b.putString("location",model.getLocation());
                        Double longtitude = Double.parseDouble(model.getLocation().substring((model.getLocation().indexOf(","))+1,model.getLocation().length()));
                        Double latitude = Double.parseDouble(model.getLocation().substring(0,model.getLocation().indexOf(","))); //Hàm substring lấy ký tự (int start,int end)
                        Toast.makeText(getContext(), ""+longtitude+"\n"+latitude, Toast.LENGTH_SHORT).show();
                        MapFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longtitude),15f));
//                        MapFragment mapFragment = new MapFragment(); //Có thể truyền dữ liệu qua lại giữ 2 fragment với nhau, hoặc fragment với activity
//                        mapFragment.setArguments(b);//Khai báo đối tượng Bundle rồi put Value
//                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                        fragmentManager.beginTransaction().replace(R.id.flContent, mapFragment).commit(); //R.id.flContent là view chứa fragment
                        String title = model.getNameStore().replace(" ","" ).toLowerCase(); //Lấy nameStore no spacebar + lowercase để làm key
                        Marker marker = MapFragment.markers.get(title); //Lấy marker có key là namestore
                        marker.showInfoWindow(); //Hiện Info marker
                    }
                });


            }

            @NonNull
            @Override
            public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view  = LayoutInflater.from(getContext()).inflate(R.layout.one_item_store,viewGroup,false);
                return new StoreViewHolder(view);
            }
        };
        adapter.startListening();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rv_store.setLayoutManager(manager);
        rv_store.setAdapter(adapter);
        return view;
    }
    private class StoreViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_store;
        private ImageView iv_background,iv_favour;
        private TextView tv_namestore,tv_address,tv_phone,tv_type_store,tv_viewed,tv_sumpost,tv_discription_store;
        private Button btn_seemap,btn_visit_store;
        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_store = itemView.findViewById(R.id.iv_store);
            iv_background = itemView.findViewById(R.id.iv_background);
            tv_namestore = itemView.findViewById(R.id.tv_store);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            btn_seemap = itemView.findViewById(R.id.btn_seemap);
            btn_visit_store = itemView.findViewById(R.id.btn_visit);
            iv_favour = itemView.findViewById(R.id.iv_favour_store);
            tv_viewed = itemView.findViewById(R.id.tv_viewed);
            tv_sumpost = itemView.findViewById(R.id.tv_sum_post);
        }
    }

}
