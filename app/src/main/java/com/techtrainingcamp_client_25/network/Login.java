package com.techtrainingcamp_client_25.network;

import android.os.AsyncTask;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

public class Login extends AsyncTask<String, Void, Integer> {
    private String ipAddress;
    private Session session;

    public Login(String ipAddress, Session session) {
        this.ipAddress = ipAddress;
        this.session = session;
    }

    @Override
    public Integer doInBackground(String... strings) {
        String userName = strings[0];
        String passWd = strings[1];
        boolean flag = true;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(userName, ipAddress, 22);
            session.setPassword(passWd);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(10000);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            flag = false;
        }
        return flag?0:-1;
    }

    public Session getSession() {
        return session;
    }
}
