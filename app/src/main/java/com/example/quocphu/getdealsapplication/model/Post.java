package com.example.quocphu.getdealsapplication.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

public class Post {
    private String id_post;
    private String tittle;
    private String contentPost;
    private String typePost;
    private String timePost;
    private String timeStart;
    private String timeEnd;
    private int quantity;
    private String codeDeal;
    private String photoPost;
    private String statePost;
    private double percent;


    public Post() {

    }

    public Post(String id_post, String tittle, String contentPost,String typePost, String timePost, String timeStart, String timeEnd, int quantity, String codeDeal,String statePost,double percent) {
        this.id_post = id_post;
        this.tittle = tittle;
        this.contentPost = contentPost;
        this.timePost = timePost;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.quantity = quantity;
        this.codeDeal = codeDeal;
        this.typePost = typePost;
        this.statePost = statePost;
        this.percent = percent;
    }
    public Post(String id_post, String tittle, String contentPost,String typePost, String timePost, String timeStart, String timeEnd, int quantity,String photoPost, String codeDeal,String statePost) {
        this.id_post = id_post;
        this.tittle = tittle;
        this.contentPost = contentPost;
        this.timePost = timePost;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.quantity = quantity;
        this.codeDeal = codeDeal;
        this.typePost = typePost;
        this.photoPost = photoPost;
        this.statePost = statePost;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public String getStatePost() {
        return statePost;
    }

    public void setStatePost(String statePost) {
        this.statePost = statePost;
    }

    public String getPhotoPost() {
        return photoPost;
    }

    public void setPhotoPost(String photoPost) {
        this.photoPost = photoPost;
    }

    public String getTypePost() {
        return typePost;
    }

    public void setTypePost(String typePost) {
        this.typePost = typePost;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getContentPost() {
        return contentPost;
    }

    public void setContentPost(String contentPost) {
        this.contentPost = contentPost;
    }

    public String getTimePost() {
        return timePost;
    }

    public void setTimePost(String timePost) {
        this.timePost = timePost;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getCodeDeal() {
        return codeDeal;
    }

    public void setCodeDeal(String codeDeal) {
        this.codeDeal = codeDeal;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id_post='" + id_post + '\'' +
                ", tittle='" + tittle + '\'' +
                ", contentPost='" + contentPost + '\'' +
                ", timePost='" + timePost + '\'' +
                ", timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                ", codeDeal='" + codeDeal + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
    public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("id_post",getId_post());
        map.put("tittle",getTittle());
        map.put("contentPost",getContentPost());
        map.put("timePost",getTimePost());
        map.put("timeStart",getTimeStart());
        map.put("timeEnd",getTimeEnd());
        map.put("codeDeal",getCodeDeal());
        map.put("quantity",getQuantity());
        map.put("statePost",getStatePost());
        map.put("photoPost",getPhotoPost());
        return map;
    }
}
