package com.techtrainingcamp_client_25;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ArticleActivity extends AppCompatActivity {
    int articleID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Intent intent = getIntent();
        articleID = intent.getIntExtra("first", 0);
        String userName = intent.getStringExtra("second");
        if(userName != null) {
            Toast.makeText(this, "Welcome "+userName+"!", Toast.LENGTH_SHORT).show();
        }
        initView();
    }

    public void initView() {
        if(Controller.model.getArticle(articleID).getContent().isEmpty()) {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        WebView webView = findViewById(R.id.article);
        webView.loadDataWithBaseURL(null, Controller.model.getArticle(articleID).getContent(), "text/html", "utf-8", null);

        // Toast.makeText(this, Model.getArticle(articleID).getId(),Toast.LENGTH_SHORT).show();
    }
}
