package com.example.quocphu.getdealsapplication.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
        private String id_user;
        private String fullName;
        private String emailUser;
        private String phoneUser;
        private String gender;
        private String birthday;
        private String typeAccount;
        private String request;
        private Map<String,Post> list_deal;

        public String getRequest() {
            return request;
        }

        public void setRequest(String request) {
            this.request = request;
        }

        public User(){

        }

        public User(String id_user, String fullName, String emailUser, String phoneUser, String gender, String birthday,String typeAccount) {
            this.id_user = id_user;
            this.fullName = fullName;
            this.emailUser = emailUser;
            this.phoneUser = phoneUser;
            this.gender = gender;
            this.birthday = birthday;
            this.typeAccount = typeAccount;
        }
        public User(String id_user, String fullName, String emailUser, String phoneUser, String gender, String birthday,String typeAccount,String request) {
            this.id_user = id_user;
            this.fullName = fullName;
            this.emailUser = emailUser;
            this.phoneUser = phoneUser;
            this.gender = gender;
            this.birthday = birthday;
            this.typeAccount = typeAccount;
            this.request = request;
        }

        public User(String id_user, String fullName, String emailUser, String phoneUser, String gender, String birthday) {
            this.id_user = id_user;
            this.fullName = fullName;
            this.emailUser = emailUser;
            this.phoneUser = phoneUser;
            this.gender = gender;
            this.birthday = birthday;
        }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String,Object> result  =new HashMap<>();
        result.put("id_user",getId_user());
        result.put("fullName",getFullName());
        result.put("emailUser",getEmailUser());
        result.put("phoneUser",getPhoneUser());
        result.put("gender",getGender());
        result.put("birthday",getBirthday());
        result.put("typeAccount",getTypeAccount());
        return result;
    }

    public Map<String, Post> getList_deal() {
        return list_deal;
    }

    public void setList_deal(Map<String, Post> list_deal) {
        this.list_deal = list_deal;
    }

    public String getTypeAccount() {
            return typeAccount;
        }

        public void setTypeAccount(String typeAccount) {
            this.typeAccount = typeAccount;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getId_user() {
            return id_user;
        }

        public void setId_user(String id_user) {
            this.id_user = id_user;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmailUser() {
            return emailUser;
        }

        public void setEmailUser(String emailUser) {
            this.emailUser = emailUser;
        }

        public String getPhoneUser() {
            return phoneUser;
        }

        public void setPhoneUser(String phoneUser) {
            this.phoneUser = phoneUser;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

    }
