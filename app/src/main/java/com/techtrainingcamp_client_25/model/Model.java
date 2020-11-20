package com.techtrainingcamp_client_25.model;

import android.graphics.Bitmap;

import com.techtrainingcamp_client_25.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Model {
    private static final ArrayList<Article> articleArrayList = new ArrayList<>();
    private static final HashMap<String, Integer> imageIDMap = new HashMap<>();

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

    public static void addImageID(String imgName, int id) {
        imageIDMap.put(imgName.toLowerCase(), id);
    }

    public static int getImageID(String imgName) {
        try {
            return imageIDMap.get(imgName.toLowerCase());
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return R.drawable.ic_launcher_foreground;
        }
    }

    public static ArrayList<Integer> getAllImageID(ArrayList<String> coverName) {
        ArrayList<Integer> res = new ArrayList<>();
        for(String s: coverName) {
            try {
                res.add(imageIDMap.get(s.toLowerCase()));
            }
            catch(NullPointerException e) {
                e.printStackTrace();
            }

        }
        return res;
    }

    public static ArrayList<String> getAllArticleName() {
        ArrayList<String> res = new ArrayList<>();
        for(Article a: articleArrayList) {
            res.add(a.getId());
        }
        return res;
    }
}
