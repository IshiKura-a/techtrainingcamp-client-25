package com.techtrainingcamp_client_25;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.techtrainingcamp_client_25.custom_layout.LinearItemDecoration;
import com.techtrainingcamp_client_25.custom_layout.RecyclerAdapter;
import com.techtrainingcamp_client_25.model.Article;
import com.techtrainingcamp_client_25.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
        Log.i(TAG, "onCreate BodyActivity");
        super.onCreate(savedInstanceState);
        parseMetaData();
        setContentView(R.layout.activity_body);
        handler = new Handler();
        finish = new Runnable() {
            @Override
            public void run() {
                if(deleteFile("temp.dat")) {
                    Log.i(TAG, "delete file "+ "temp.dat sucessfully");
                } else {
                    Log.i(TAG, "failed to delete file temp.dat");
                }
                Controller.token = null;
                Controller.model.clearArticle();
                finish();
            }
        };

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        reloadCacheData();
    }

    @SuppressLint("StaticFieldLeak")
    public void parseMetaData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    String stringGet;
                    InputStream is = getAssets().open("metadata.json");

                    ArrayList<Byte> jsonText = new ArrayList<>();
                    try {
                        int tmp;
                        while ((tmp = is.read()) != -1) {
                            jsonText.add((byte) tmp);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[] bytes = new byte[jsonText.size()];
                    for (int i = 0; i < jsonText.size(); i++) {
                        bytes[i] = jsonText.get(i);
                    }

                    try {
                        stringGet = new String(bytes, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        stringGet = null;
                    }

                    Log.i(TAG, "NEW:\n" + stringGet);
                    JSONArray jsonArray = new JSONArray(stringGet);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Article article = new Article(jsonObject.getString("id"),
                                jsonObject.getString("title"), jsonObject.getString("author"),
                                jsonObject.getString("publishTime"), jsonObject.getInt("type"));
                        if (article.getType() != 0) {
                            if (article.getType() != 4) {
                                article.addCoverName(jsonObject.getString("cover"));
                            } else {
                                JSONArray coverList = new JSONArray(jsonObject.getString("covers"));
                                for (int j = 0; j < coverList.length(); j++) {
                                    article.addCoverName((String) coverList.get(j));
                                }
                            }
                        }
                        Controller.model.addArticle(article);
                        Log.i(TAG, i + " " + article.toString());
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
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

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (Controller.width == 0) {
                    Resources resources = getResources();
                    DisplayMetrics dm = resources.getDisplayMetrics();
                    Controller.width = dm.widthPixels;
                    Controller.height = dm.heightPixels;
                    Controller.density = dm.density;
                }
                return null;
            }
        }.execute();

        recyclerView = findViewById(R.id.recycler);
        mAdapter = new RecyclerAdapter(Controller.model.getAllArticle());

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter.setOnItemClickListener(this);
        LinearItemDecoration itemDecoration = new LinearItemDecoration(Color.rgb(168,165,181));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        recyclerView.swapAdapter(mAdapter, true);

        Log.i(TAG, "ArticleCount: " + Controller.model.getArticleCount());
        while(recyclerView.getAdapter().getItemCount() > Controller.model.getArticleCount()) {
            Log.i(TAG, "RecView: " + recyclerView.getAdapter().getItemCount());
            ((RecyclerAdapter)recyclerView.getAdapter()).removeData(Controller.model.getArticleCount());
        }
    }

    @Override
    public void onItemCLick(int position, Article data) {
        Log.i(TAG, "View: Child Count" + recyclerView.getChildCount());
        Toast toast;
        toast = Toast.makeText(this, "点击了第" + (position+1) + "条", Toast.LENGTH_SHORT);
        toast.show();


        if(Controller.token == null || Controller.token.isEmpty()) {
            Log.i(TAG, "Starting login...");
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("first", position);
            startActivity(loginIntent);
        }
        else {
            Intent intent;
            intent = new Intent(this, ArticleActivity.class);
            intent.putExtra("first", position);
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongCLick(int position, Article data) {
        // ignore
    }

    @SuppressLint("StaticFieldLeak")
    public void reloadCacheData() {
        Log.i(TAG, "Begin reloadCacheData");
        if(Controller.token == null) {
            new AsyncTask<Void,Void,Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    FileInputStream fis;
                    try {
                        Log.i(TAG, "Loading temp data");
                        fis = openFileInput("temp.dat");
                        ObjectInputStream ois = new ObjectInputStream(fis);
                        Controller.model = (Model)ois.readObject();
                        Controller.token = (String)ois.readObject();
                        Log.i(TAG, Controller.token==null?"No token":Controller.token);
                        ois.close();
                        fis.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();
        }
    }
}
