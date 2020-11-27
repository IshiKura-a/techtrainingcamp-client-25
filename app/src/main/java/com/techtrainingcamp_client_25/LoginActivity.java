package com.techtrainingcamp_client_25;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        findViewById(R.id.button_login).setOnClickListener(this);
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
        // Model.clearArticle();

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

        boolean[] status = {true};
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                status[0] = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    Log.i(TAG, jsonObject.getString("message"));
                    Controller.token = jsonObject.getString("token");
                    Log.i(TAG, Controller.token);
                    onGetToken();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent loginIntent = getIntent();
                int articleID = loginIntent.getIntExtra("first", 0);

                Intent intent;
                intent = new Intent(LoginActivity.this, ArticleActivity.class);
                intent.putExtra("first", articleID);
                intent.putExtra("second", userName);
                startActivity(intent);

                finish();
            }
        });
    }

    public boolean onGetToken() {
        final boolean[] state = {true};
        Log.i(TAG, "OnGetToken");
        if(Controller.token == null || Controller.token.isEmpty()) return false;
        Log.i(TAG, "Parsing");
        for(String s: Controller.model.getAllArticleName()) {
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
                    Toast.makeText(LoginActivity.this, "get failed", Toast.LENGTH_SHORT).show();
                    state[0] = false;
                }

                @SuppressLint("StaticFieldLeak")
                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    final String res = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(res);
                        if(jsonObject.getString("message").compareTo("jwt malformed") == 0) {
                            Looper.prepareMainLooper();
                            state[0] = false;
                            Toast.makeText(LoginActivity.this, "Token is out of date. Please re-login!", Toast.LENGTH_SHORT).show();
                            Looper.prepareMainLooper();
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
                        for(String c: Controller.model.getArticle(s).getAllCoverName()) {
                            tmp = tmp.replaceAll("(src=\""+c+"\")","src=\"file:///android_asset/"+c+"\" width=\"100%\"");
                        }

                        Pattern pattern = Pattern.compile("(https?|ftp)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
                        Matcher matcher = pattern.matcher(tmp);

                        HashSet<String> urlSet = new HashSet<>();
                        while(matcher.find()) {
                            urlSet.add(matcher.group(0));
                        }
                        for(String url: urlSet) {
                            tmp = tmp.replaceAll("("+url+")", "<br><a href=\""+url+"\" style=\"word-break:break-all\">"+url+"</a>");
                        }
                        Controller.model.getArticle(s).setContent(tmp);

                        Log.i(TAG, Controller.model.getArticle(s).getContent());

                        if(s.compareTo(Controller.model.getArticle(Controller.model.getArticleCount()-1).getId()) == 0) {
                            // Save temp data
                            new AsyncTask<Void,Void,Void>(){
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    FileOutputStream fos;
                                    try{
                                        fos = openFileOutput("temp.dat", Context.MODE_PRIVATE);
                                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                                        oos.writeObject(Controller.model);
                                        oos.writeObject(Controller.token);
                                        oos.close();
                                        fos.close();
                                        Log.i(TAG, "Saving in: " + getFilesDir());
                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }
                            }.execute();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        state[0] = false;
                    }
                    call.cancel();
                }
            });
        }

        return state[0];
    }
}