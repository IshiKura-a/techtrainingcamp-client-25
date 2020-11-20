package com.techtrainingcamp_client_25.model;

import java.util.ArrayList;

public class Model {
    private static final ArrayList<Article> articleArrayList = new ArrayList<>();

    private Model() {
    }

    public static void addArticle(Article article) {
        articleArrayList.add(article);
    }

    public static void addArticle(int index, Article article) {
        articleArrayList.set(index, article);
    }

    public static Article getArticle(int index) {
        return articleArrayList.get(index);
    }

    public static Article getArticle(String id) {
        for(Article a: articleArrayList) {
            if(a.getId().compareTo(id) == 0)
                return a;
        }
        return null;
    }

    public static ArrayList<Article> getAllArticle() {
        return articleArrayList;
    }


    public static ArrayList<String> getAllArticleName() {
        ArrayList<String> res = new ArrayList<>();
        for(Article a: articleArrayList) {
            res.add(a.getId());
        }
        return res;
    }
}
