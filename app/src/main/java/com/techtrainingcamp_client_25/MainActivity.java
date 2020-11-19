package com.techtrainingcamp_client_25;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.techtrainingcamp_client_25.network.Login;

import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
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

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_login:
                String userName = ((EditText)findViewById(R.id.editTextTextPersonName)).getText().toString();
                String passWd = ((EditText)findViewById(R.id.editTextTextPassword)).getText().toString();
                Login txn = new Login("121.196.99.154");
                Log.i(TAG,"Log in: " + userName + passWd);
                Log.i(TAG, txn.doInBackground(userName, passWd)==-1?"Failed":"Succeeded");
                break;
            default:
                Log.i(TAG,"Default");
                break;
        }
    }
}