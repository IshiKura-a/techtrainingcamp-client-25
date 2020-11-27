package com.techtrainingcamp_client_25.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Article implements Serializable {
    private static final long serialVersionUID = 2441705022299166624L;
    private String id;
    private String title;
    private String author;
    private String publishTime;
    private int type;
    private int cntCover;
    private ArrayList<String> coverName;
    private String content;

    public Article(String id, String title, String author, String publishTime, int type) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publishTime = publishTime;
        this.type = type;
        coverName = new ArrayList<>();
        cntCover = 0;
        content = "";
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCoverName(String coverName, int index) {
        this.coverName.set(index, coverName);
    }

    public void addCoverName(String coverName) {
        cntCover++;
        this.coverName.add(coverName);
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

    public int getType() {
        return type;
    }

    public String getAuthor() {
        return author;
    }

    public String getCoverName(int index) {
        return coverName.get(index);
    }

    public ArrayList<String> getAllCoverName() {
        return coverName;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
