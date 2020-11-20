package com.techtrainingcamp_client_25;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techtrainingcamp_client_25.model.Article;
import com.techtrainingcamp_client_25.model.Model;
import com.techtrainingcamp_client_25.custom_layout.LinearItemDecoration;
import com.techtrainingcamp_client_25.custom_layout.RecyclerAdapter;
import com.techtrainingcamp_client_25.network.Download;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BodyActivity extends AppCompatActivity implements RecyclerAdapter.IOnItemClickListener{
    private static final String TAG = "TAG";

    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;

    private long exitTime = 0;
    private Handler handler;
    private Runnable finish;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_body);
        handler = new Handler();
        finish = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };

        for(String s: Model.getAllArticleName()) {
            Download downloadJson = (Download) new Download(Controller.session,"bulletin/"+s,s+".md", "md") {
                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if(method.compareTo("md") == 0) {
                        if(o == null) {
                            Toast.makeText(getApplicationContext(), "Fail to download article", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Model.getArticle(s).setContent(stringGet);
                            // Toast.makeText(getApplicationContext(), "Succeed in getting json", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }.execute();
        }

        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((System.currentTimeMillis()-exitTime) > 2000){
            exitTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            handler.postDelayed(finish, 0);
        }
        //return super.onKeyDown(keyCode, event);
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    void initView() {
        recyclerView = findViewById(R.id.recycler);
        mAdapter = new RecyclerAdapter(Model.getAllArticle());

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter.setOnItemClickListener(this);
        LinearItemDecoration itemDecoration = new LinearItemDecoration(Color.rgb(168,165,181));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(1500);
        recyclerView.setItemAnimator(animator);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemCLick(int position, Article data) {
        Toast toast;
        toast = Toast.makeText(this, "点击了第" + (position+1) + "条", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent = new Intent(this, ArticleActivity.class);
        intent.putExtra("first", position);
        startActivity(intent);


    }

    @Override
    public void onItemLongCLick(int position, Article data) {
        // ignore
    }
}
