package com.techtrainingcamp_client_25;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.techtrainingcamp_client_25.model.Model;

public class ArticleActivity extends AppCompatActivity {
    int articleID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Intent intent = getIntent();
        articleID = intent.getIntExtra("first", 0);

        initView();
    }

    public void initView() {
        WebView webView = findViewById(R.id.article);
        webView.loadDataWithBaseURL(null, Model.getArticle(articleID).getContent(), "text/html", "utf-8", null);

        // Toast.makeText(this, Model.getArticle(articleID).getId(),Toast.LENGTH_SHORT).show();
    }
}
