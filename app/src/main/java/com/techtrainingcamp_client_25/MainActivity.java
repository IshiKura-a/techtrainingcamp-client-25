package com.techtrainingcamp_client_25;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jcraft.jsch.Session;
import com.techtrainingcamp_client_25.model.Article;
import com.techtrainingcamp_client_25.model.Model;
import com.techtrainingcamp_client_25.network.Download;
import com.techtrainingcamp_client_25.network.Login;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = null;
        initView();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initView() {
        findViewById(R.id.button_login).setOnClickListener(this);
    }


    public String jsonText;
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_login:
                String userName = ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString();
                String passWd = ((EditText)findViewById(R.id.editTextTextPassword)).getText().toString();

                Toast toast;
                if(userName.isEmpty() || passWd.isEmpty()) {
                    toast = Toast.makeText(this, "Please validate your account!", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                session = null;
                Login txn = new Login("121.196.99.154",session);
                Log.i(TAG,"Log in: " + userName + passWd);

                int result = txn.doInBackground(userName, passWd);
                toast = Toast.makeText(this, result==0?("Welcome "+userName+"!"):"Wrong Password!", Toast.LENGTH_SHORT);
                toast.show();

                if(result == 0) {
                    session = txn.getSession();

                    @SuppressLint("StaticFieldLeak")
                    Download downloadJson = new Download(session,"metadata.json", "json") {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            while(!Controller.txnDone);
                            Controller.txnDone = false;
                        }

                        @Override
                        public String convertToString() {
                            String res = super.convertToString();
                            try {
                                JSONArray jsonArray = new JSONArray(res);
                                for(int i=0; i<jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Article article = new Article(jsonObject.getString("id"),
                                            jsonObject.getString("title"),jsonObject.getString("author"),
                                            jsonObject.getString("publishTime"),jsonObject.getInt("type"));
                                    if(article.getType() != 0) {
                                        if(article.getType() != 4){
                                            article.setCoverName(jsonObject.getString("cover"), 0);
                                        }
                                        else {
                                            JSONArray coverList = new JSONArray(jsonObject.getString("covers"));
                                            for(int j=0; j<article.getCntCover(); j++) {
                                                article.setCoverName((String)coverList.get(j),j);
                                            }
                                        }
                                    }
                                    Model.add(article);
                                    Log.i(TAG, i + " " + article.toString());
                                }
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return res;
                        }

                        @Override
                        protected void onPostExecute(Object o) {
                            super.onPostExecute(o);
                            if(method.compareTo("json") == 0) {
                                if(o == null) {
                                    Toast.makeText(getApplicationContext(), "Fail to download metadata.json", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    jsonText = resJson;
                                    // Toast.makeText(getApplicationContext(), "Succeed in getting json", Toast.LENGTH_SHORT).show();
                                }
                            }
                            Log.i("TAG","Json Done");
                            Controller.txnDone = true;
                        }
                    };
                    downloadJson.execute();

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(this, BodyActivity.class);

                    startActivity(intent);
                    finish();
                    if(session != null && Controller.txnDone) session.disconnect();
                }

                break;
            default:
                Log.i(TAG,"Default");
                break;
        }
    }

}