package com.techtrainingcamp_client_25.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;

public class Download extends AsyncTask<String, Void, Object> {
    private final Session session;
    private InputStream is;
    private final String fileName;
    protected final String method;
    protected String resJson;

    public Download(Session session, String fileName, String method) {
        this.session = session;
        this.fileName = fileName;
        this.method = method.toLowerCase();
        is = null;
        resJson = null;
    }

    @Override
    public Object doInBackground(String... strings) {
        try {
            Channel channel = (Channel)session.openChannel("sftp");
            channel.connect(1000);
            ChannelSftp sftp = (ChannelSftp)channel;

            sftp.cd("bulletin");
            is = sftp.get(fileName);
            Log.i("TAG", is.available()==0?"true":"false");

            if(method.compareToIgnoreCase("json") == 0) {
                return convertToString();
            }
            else if(method.compareToIgnoreCase("blob") == 0) {
                return convertToBlob();
            }
        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String convertToString() {
        ArrayList<Byte> jsonText = new ArrayList<>();
        try {
            int tmp;
            while((tmp = is.read()) != -1) {
                jsonText.add((byte) tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = new byte[jsonText.size()];
        for(int i=0;i<jsonText.size();i++) {
            bytes[i] = jsonText.get(i);
        }

        try {
            resJson = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            resJson = null;
        }

        Log.i("TAG",resJson);
        return resJson;
    }

    public InputStream convertToBlob() {
        return is;
    }
}
