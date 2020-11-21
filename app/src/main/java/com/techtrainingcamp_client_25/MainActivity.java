package com.techtrainingcamp_client_25;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.techtrainingcamp_client_25.model.Article;
import com.techtrainingcamp_client_25.model.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void initView() {
        findViewById(R.id.button_login).setOnClickListener(this);

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
    }


    public String jsonText;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                // loginEvent2();
                loginEvent();
                break;
            default:
                Log.i(TAG, "Default");
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void loginEvent() {
        Model.clearArticle();

        String userName = ((EditText) findViewById(R.id.editTextTextPersonName)).getText().toString();
        String passWd = ((EditText) findViewById(R.id.editTextTextPassword)).getText().toString();

        Toast toast;
        if (userName.isEmpty() || passWd.isEmpty()) {
            toast = Toast.makeText(this, "Please validate your account!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Log.i("TAG", "LoginEvent");
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("username", userName)
                .add("password", passWd)
                .build();
        Request request = new Request.Builder()
                .url("https://vcapi.lvdaqian.cn/login")
                .addHeader("accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, "Post Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    Log.i(TAG, jsonObject.getString("message"));
                    Controller.token = jsonObject.getString("token");
                    Log.i(TAG, Controller.token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                call.cancel();
            }
        });

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
                        Model.addArticle(article);
                        Log.i(TAG, i + " " + article.toString());
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

        Intent intent = new Intent(this, BodyActivity.class);

        startActivity(intent);
        finish();
    }
}