package com.techtrainingcamp_client_25;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techtrainingcamp_client_25.model.Article;
import com.techtrainingcamp_client_25.model.Model;
import com.techtrainingcamp_client_25.recycler.LinearItemDecoration;
import com.techtrainingcamp_client_25.recycler.MyAdapter;

public class BodyActivity extends AppCompatActivity implements MyAdapter.IOnItemClickListener{
    private static final String TAG = "TAG";

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;

    private long exitTime = 0;
    private Handler handler;
    private Runnable finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body);
        handler = new Handler();
        finish = new Runnable() {
            @Override
            public void run() {
                finish();
            }
        };
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
        mAdapter = new MyAdapter(Model.getAllData());

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
        toast = Toast.makeText(this, "点击了第" + position + "条", Toast.LENGTH_SHORT);
        toast.show();
        mAdapter.addData(position + 1, new Article("new id", "Title", "author", "2020.11.19",0));
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 700);
    }

    @Override
    public void onItemLongCLick(int position, Article data) {
        Toast toast;
        toast = Toast.makeText(this, "长按了第" + position + "条", Toast.LENGTH_SHORT);
        toast.show();
        mAdapter.removeData(position);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 700);
    }
}
