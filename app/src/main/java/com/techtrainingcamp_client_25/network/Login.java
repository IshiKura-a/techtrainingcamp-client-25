package com.techtrainingcamp_client_25.network;

import android.os.AsyncTask;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

public class Login extends AsyncTask<String, Void, Integer> {
    String ipAddress;
    public Login(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public Integer doInBackground(String... strings) {
        String userName = strings[0];
        String passWd = strings[1];
        boolean flag = true;
        Session session = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(userName, ipAddress, 22);
            session.setPassword(passWd);
            // session.setConfig("StrictHostKeyChecking", "no");
            session.connect(5 * 1000);
        } catch (Exception ex) {
            System.out.println(ex.getCause());
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            // System.out.println(ex.getLocalizedMessage());
            flag = false;
        }

        if(session != null && session.isConnected()) session.disconnect();
        return flag?-1:0;
    }
}
