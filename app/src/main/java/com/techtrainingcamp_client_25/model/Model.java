package com.techtrainingcamp_client_25.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Model implements Serializable {
    private static final long serialVersionUID = -8585415044783398901L;
    private final ArrayList<Article> articleArrayList = new ArrayList<>();

    public void addArticle(Article article) {
        articleArrayList.add(article);
    }

    public void addArticle(int index, Article article) {
        articleArrayList.set(index, article);
    }

    public Article getArticle(int index) {
        return articleArrayList.get(index);
    }

    public Article getArticle(String id) {
        for(Article a: articleArrayList) {
            if(a.getId().compareTo(id) == 0)
                return a;
        }
        return null;
    }

    public ArrayList<Article> getAllArticle() {
        return articleArrayList;
    }

    public ArrayList<String> getAllArticleName() {
        ArrayList<String> res = new ArrayList<>();
        for(Article a: articleArrayList) {
            res.add(a.getId());
        }
        return res;
    }

    public void clearArticle() {
        articleArrayList.clear();
    }

    public int getArticleCount() {
        return articleArrayList.size();
    }
}
