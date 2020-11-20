package com.techtrainingcamp_client_25;

import androidx.appcompat.app.AppCompatActivity;

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

import com.techtrainingcamp_client_25.model.Article;
import com.techtrainingcamp_client_25.model.Model;
import com.techtrainingcamp_client_25.network.Download;
import com.techtrainingcamp_client_25.network.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Controller.session = null;
        initView();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void initView() {
        findViewById(R.id.button_login).setOnClickListener(this);
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Model.addImageID("event_02.png", R.drawable.event_02);
                Model.addImageID("tancheng.jpg", R.drawable.tancheng);
                Model.addImageID("tb09_1.jpeg", R.drawable.tb09_1);
                Model.addImageID("tb09_2.jpeg", R.drawable.tb09_2);
                Model.addImageID("tb09_3.jpeg", R.drawable.tb09_3);
                Model.addImageID("tb09_4.jpeg", R.drawable.tb09_4);
                Model.addImageID("teambuilding_04.png", R.drawable.teambuilding_04);
                return null;
            }
        }.execute();

        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                while(Controller.width == 0) {
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
        switch(view.getId()) {
            case R.id.button_login:
                loginEvent();
                break;
            default:
                Log.i(TAG,"Default");
                break;
        }
    }

    private void loginEvent() {
        String userName = ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString();
        String passWd = ((EditText)findViewById(R.id.editTextTextPassword)).getText().toString();

        Toast toast;
        if(userName.isEmpty() || passWd.isEmpty()) {
            toast = Toast.makeText(this, "Please validate your account!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        Controller.session = null;
        Login txn = new Login("121.196.99.154",Controller.session);
        Log.i(TAG,"Log in: " + userName + passWd);

        int result = txn.doInBackground(userName, passWd);
        toast = Toast.makeText(this, result==0?("Welcome "+userName+"!"):"Wrong Password!", Toast.LENGTH_SHORT);
        toast.show();

        if(result == 0) {
            Controller.session = txn.getSession();

            @SuppressLint("StaticFieldLeak")
            Download downloadJson = new Download(Controller.session,"metadata.json", "json") {
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
                                    article.addCoverName(jsonObject.getString("cover"));
                                }
                                else {
                                    JSONArray coverList = new JSONArray(jsonObject.getString("covers"));
                                    for(int j=0; j<coverList.length(); j++) {
                                        article.addCoverName((String)coverList.get(j));
                                    }
                                }
                            }
                            Model.addArticle(article);
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
                            jsonText = stringGet;
                            // Toast.makeText(getApplicationContext(), "Succeed in getting json", Toast.LENGTH_SHORT).show();
                        }
                    }
                    Log.i("TAG","Json Done");
                    Controller.txnDone = true;
                }
            };
            downloadJson.execute();
            Intent intent = new Intent(this, BodyActivity.class);

            startActivity(intent);
            finish();
            if(Controller.session != null && Controller.txnDone) Controller.session.disconnect();
        }
    }
}