package com.example.quocphu.getdealsapplication.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Store {
    private String id_store;
    private String nameStore;
    private String address;
    private String phoneStore;
    private String location;
    private String photoUrl;
    private String cover;
    private String description;
    private int viewed;

    public Store(){

    }

    public Store(String id_store, String nameStore, String description, String address,String location, String phoneStore,String photoUrl,String cover) {
        this.id_store = id_store;
        this.nameStore = nameStore;
        this.address = address;
        this.phoneStore = phoneStore;
        this.location = location;
        this.photoUrl = photoUrl;
        this.cover = cover;
        this.description = description;
    }

    public Store(String id_store, String nameStore,String description, String address, String location, String phoneStore) {
        this.id_store = id_store;
        this.nameStore = nameStore;
        this.address = address;
        this.phoneStore = phoneStore;
        this.location = location;
        this.description = description;
    }

    public int getViewed() {
        return viewed;
    }

    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId_store() {
        return id_store;
    }

    public void setId_store(String id_store) {
        this.id_store = id_store;
    }

    public String getNameStore() {
        return nameStore;
    }

    public void setNameStore(String nameStore) {
        this.nameStore = nameStore;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneStore() {
        return phoneStore;
    }

    public void setPhoneStore(String phoneStore) {
        this.phoneStore = phoneStore;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id_store='" + id_store + '\'' +
                ", nameStore='" + nameStore + '\'' +
                ", address='" + address + '\'' +
                ", phoneStore='" + phoneStore + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result  =new HashMap<>();
        result.put("id_store",getId_store());
        result.put("nameStore",getNameStore());
        result.put("address",getAddress());
        result.put("location",getLocation());
        result.put("phoneStrore",getPhoneStore());
        result.put("photoUrl",getPhotoUrl());
        return result;
    }
}
