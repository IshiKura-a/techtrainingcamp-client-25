package com.techtrainingcamp_client_25.model;

import java.util.ArrayList;

public class Model {
    private static final ArrayList<Article> articleArrayList = new ArrayList<>();;

    private Model() {
    }

    public static void add(Article article) {
        articleArrayList.add(article);
    }

    public static void add(int index, Article article) {
        articleArrayList.set(index, article);
    }

    public static Article get(int index) {
        return articleArrayList.get(index);
    }

    public static ArrayList<Article> getAllData() {
        return articleArrayList;
    }
}
