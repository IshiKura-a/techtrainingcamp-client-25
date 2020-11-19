package com.techtrainingcamp_client_25.model;

import android.graphics.Bitmap;
import android.graphics.Picture;

public class Article {
    private String id;
    private String title;
    private String author;
    private String publishTime;
    private int type;
    private int cntCover;
    private String[] coverName;
    private Bitmap[] cover;

    public Article(String id, String title, String author, String publishTime, int type) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishTime = publishTime;
        this.type = type;
        if(type == 0) cntCover = 0;
        else if(type == 4) cntCover = 4;
        else cntCover = 1;

        this.coverName = new String[cntCover];
        this.cover = new Bitmap[cntCover];
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCover(Bitmap cover, int index) {
        this.cover[index] = cover;
    }

    public void setCoverName(String coverName, int index) {
        this.coverName[index] = coverName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getCover(int index) {
        return cover[index];
    }

    public int getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public String getCoverName(int index) {
        return coverName[index];
    }

    public String getId() {
        return id;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getTitle() {
        return title;
    }

    public int getCntCover() {
        return cntCover;
    }

    public String toString() {
        StringBuilder tmp = new StringBuilder();
        for(String s:coverName) {
            tmp.append(",");
            tmp.append(s);
        }
        return id+","+title+","+author+tmp.toString();
    }
}
