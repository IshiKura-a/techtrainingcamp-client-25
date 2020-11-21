package com.techtrainingcamp_client_25;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techtrainingcamp_client_25.custom_layout.LinearItemDecoration;
import com.techtrainingcamp_client_25.custom_layout.RecyclerAdapter;
import com.techtrainingcamp_client_25.model.Article;
import com.techtrainingcamp_client_25.model.Model;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BodyActivity extends AppCompatActivity implements RecyclerAdapter.IOnItemClickListener{
    private static final String TAG = "TAG";

    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;

    private long exitTime = 0;
    private Handler handler;
    private Runnable finish;

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
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .get()
                    .url("https://vcapi.lvdaqian.cn/article/"+s+"?markdown=true")
                    .addHeader("accept","application/json")
                    .addHeader("Authorization", "Bearer "+Controller.token)
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(BodyActivity.this, "get failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String res = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if(jsonObject.getString("message").compareTo("jwt malformed") == 0) {
                            Looper.prepare();
                            Toast.makeText(BodyActivity.this, "Token is out of date. Please re-login!", Toast.LENGTH_SHORT).show();
                            Looper.prepare();
                            return;
                        }
                        Log.i(TAG, "Get "+s+" :"+jsonObject.getString("message"));
                        MutableDataSet options = new MutableDataSet();

                        Parser parser = Parser.builder(options).build();
                        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

                        String tmp = jsonObject.getString("data").replaceAll("\\n[ ]{3,}","\n");
                        Log.i(TAG, tmp);
                        Node document = parser.parse(tmp);
                        tmp = renderer.render(document);
                        for(String c: Model.getArticle(s).getAllCoverName()) {
                            tmp = tmp.replaceAll("(src=\""+c+"\")","src=\"file:///android_asset/"+c+"\" width=\"100%\"");
                        }
                        Model.getArticle(s).setContent(tmp);

                        Log.i(TAG, Model.getArticle(s).getContent());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    call.cancel();
                }
            });
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

        recyclerView.swapAdapter(mAdapter, true);

        Log.i(TAG, "ArticleCount: " + Model.getArticleCount());
        while(recyclerView.getAdapter().getItemCount() > Model.getArticleCount()) {
            Log.i(TAG, "RecView: " + recyclerView.getAdapter().getItemCount());
            ((RecyclerAdapter)recyclerView.getAdapter()).removeData(Model.getArticleCount());
        }
    }

    @Override
    public void onItemCLick(int position, Article data) {
        Log.i(TAG, "View: Child Count" + recyclerView.getChildCount());
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
