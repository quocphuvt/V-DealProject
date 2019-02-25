package com.example.quocphu.getdealsapplication;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quocphu.getdealsapplication.model.Store;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    public static GoogleMap mMap;
    public static Map<String,Marker> markers = new HashMap<>();
    private ArrayList<Marker> list_marker = new ArrayList<>();
    private Dialog dialog;
    private Marker marker_nguyenkim,marker_tanhung;
    private MapView mapView;
    private android.support.v7.widget.SearchView sv_location;
    public static BottomSheetBehavior mBottomSheetBehavior;
    private FirebaseDatabase database;
    private View bottomSheet;
    private String key_store;
    private String key_store2;
    public static MapFragment newInstance() {
        return new MapFragment();
    }
    public MapFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        sv_location =view.findViewById(R.id.sv_location);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Button btn_collapse = bottomSheet.findViewById(R.id.btn_collapse);
        btn_collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        mapView = (MapView)view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState); //Khởi tạo mapView
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                UiSettings uisetting = mMap.getUiSettings(); //Lấy giao diện Map
                uisetting.setCompassEnabled(true); //La bàn
                uisetting.setZoomControlsEnabled(true);
                uisetting.setScrollGesturesEnabled(true);
                uisetting.setTiltGesturesEnabled(true);
                uisetting.setMyLocationButtonEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//bang M
                    if (getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        xuLyQuyen();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                    }
                } else {
                    xuLyQuyen();
                }

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //đặt kiểu map
            }
        });
        return view;
    }
    //Hàm chạy khi vào fragment Map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public void xuLyQuyen() {
        mMap.setMyLocationEnabled(true);
        LocationManager service = (LocationManager)getContext().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, true);
        Location location = service.getLastKnownLocation(provider); //lấy vị trí hiện tại
        LatLng vitri = new LatLng(location.getLatitude(),location.getLongitude()); //Lấy tọa độ Latitude: kinh độ + Longitude: Vĩ độ
        //Tạo marker tại vị trí hiện tại

        database = FirebaseDatabase.getInstance();
        DatabaseReference node_store = database.getReference("stores");
        node_store.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                    for (DataSnapshot itemStore : dataSnapshot.getChildren()) {
                        Store store = itemStore.getValue(Store.class);
                        String location = store.getLocation();
                        String longtitude = location.substring((location.indexOf(",")) + 1, location.length());
                        String latitude = location.substring(0, location.indexOf(","));
                        String title = store.getNameStore().toLowerCase(); //Ép về chuỗi thường
                        String key_tittle = title.replace(" ", ""); //Đưa chuỗi về dạng nospacebar
                            Marker marker_nguyenkim = mMap.addMarker( //Tạo marker
                                    new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longtitude)))
                                            .title(store.getNameStore())
                                            .snippet("test")
                                            .icon(bitmapDescriptorFromVector(getContext(), R.drawable.icon_store)));
                            markers.put(key_tittle, marker_nguyenkim); //Thêm marker vào Map để search = key_tittle}

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(vitri, 15));;//zoom tới vị trí marker với độ zoom 15
//        Bundle bundle = this.getArguments();
//        if(bundle !=null){
//            String location_store = bundle.getString("location");
//            Double longtitude = Double.parseDouble(location_store.substring((location_store.indexOf(","))+1,location_store.length()));
//            Double latitude = Double.parseDouble(location_store.substring(0,location_store.indexOf(",")));
//            Toast.makeText(getContext(), ""+longtitude+"\n"+latitude, Toast.LENGTH_SHORT).show();
//            MapFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longtitude), 15));
//        }

        //Hàm nhấn vào bảng đồ để tạo marker
//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng arg0) {
//                // TODO Auto-generated method stub
//                Marker mym2 = mMap.addMarker(
//                        new MarkerOptions()
//                                .position(arg0)
//                                .title("dia diem")
//                                .snippet(arg0.latitude +","+arg0.longitude)
//                                .icon(BitmapDescriptorFactory.defaultMarker(
//                                        BitmapDescriptorFactory.HUE_ROSE)));
//                markers.add(mym2);
//            }
//        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                // TODO Auto-generated method stub


                return false;
            }

        });
        //Nhấn vào Markert hiện info
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
                // TODO Auto-generated method stub
            }
            @Override
            public View getInfoContents(Marker arg0) {
                // TODO Auto-generated method stub
                View view = getActivity().getLayoutInflater()
                        .inflate(R.layout.markerinfo, null);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                final TextView tv_nameStore = bottomSheet.findViewById(R.id.tv_namestore_sheet);
                final TextView tv_address = bottomSheet.findViewById(R.id.tv_address_sheet);
                final TextView tv_location = bottomSheet.findViewById(R.id.tv_location_sheet);
                TextView btn_goto = bottomSheet.findViewById(R.id.btn_goto_sheet);
                final ImageView iv_store = bottomSheet.findViewById(R.id.iv_store_sheet);
                final TextView tv_phone = bottomSheet.findViewById(R.id.tv_phone_sheet);
                final TextView tv_description = bottomSheet.findViewById(R.id.tv_discription_sheet);
                //
                double latitude = arg0.getPosition().latitude;
                double longtitude = arg0.getPosition().longitude;
                Query node_store = database.getReference("stores").orderByChild("location").equalTo(latitude+","+longtitude);
                node_store.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemStore : dataSnapshot.getChildren()){
                            Store store = itemStore.getValue(Store.class);
                            tv_nameStore.setText("Name's store: "+store.getNameStore());
                            tv_address.setText("Address: "+store.getAddress());
                            tv_location.setText("Location: "+store.getLocation());
                            tv_phone.setText("Phone's store: "+store.getPhoneStore());
                            tv_description.setText("Description: "+store.getDescription());
                            Picasso.with(getContext()).load(store.getPhotoUrl()).fit().centerCrop().into(iv_store);
                            key_store = itemStore.getKey();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                btn_goto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), StoreUserActivity.class);
                        i.putExtra("key_store",key_store);
                        startActivity(i);
                    }
                });
                TextView tv_title = ((TextView)view.findViewById(R.id.tv_marker_title));
                tv_title.setText(arg0.getTitle());
                TextView tv_snipset = ((TextView)view.findViewById(R.id.tv_marker_snipset));
                tv_snipset.setText(arg0.getSnippet());
                return view;
            }
        });
        //Nhấn vào info hiện detail info
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker arg0) {
                // TODO Auto-generated method stub
                dialog = new Dialog(getContext());
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
// layout to display
                dialog.setContentView(R.layout.markerdetail);
                Button b=(Button)dialog.findViewById(R.id.button1);

                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.cancel();

                        Toast.makeText(getActivity(), "nhan nut",Toast.LENGTH_SHORT).show();
                    }
                });
// set color transpartent
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();


            }
        });

        sv_location.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() { //Tìm marker theo key Map
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                try {
                    Marker marker = markers.get(s);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15f));
                }catch (Exception e){

                }
                return false;

            }
        });





    }
    //Hàm xử lý quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(getContext(), "xx", Toast.LENGTH_SHORT).show();
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getContext(), "xin duoc quyen roi", Toast.LENGTH_SHORT).show();
            xuLyQuyen();
        }
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) { //Đặt custom marker
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}
