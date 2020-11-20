package com.techtrainingcamp_client_25.network;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;

public class Download extends AsyncTask<String, Void, Object> {
    private final Session session;
    private InputStream is;
    private final String fileName;
    protected final String method;
    private final String path;
    protected String stringGet;

    public Download(Session session, String fileName, String method) {
        this.session = session;
        this.fileName = fileName;
        this.method = method.toLowerCase();
        path = "bulletin";
        is = null;
        stringGet = null;
    }

    public Download(Session session, String path, String fileName, String method) {
        this.session = session;
        this.fileName = fileName;
        this.method = method.toLowerCase();
        this.path = path;
        is = null;
        stringGet = null;
    }

    @Override
    public Object doInBackground(String... strings) {
        try {
            Channel channel = (Channel)session.openChannel("sftp");
            channel.connect(1000);
            ChannelSftp sftp = (ChannelSftp)channel;

            sftp.cd(path);
            is = sftp.get(fileName);
            Log.i("TAG", is.available()==0?"true":"false");

            if(method.compareToIgnoreCase("json") == 0) {
                return convertToString();
            }
            else if(method.compareToIgnoreCase("blob") == 0) {
                return convertToBlob();
            }
            else if(method.compareToIgnoreCase("md") == 0) {
                convertToString();
                return convertToHTML5();
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
            stringGet = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            stringGet = null;
        }

        Log.i("TAG", stringGet);
        return stringGet;
    }

    public InputStream convertToBlob() {
        return is;
    }

    public String convertToHTML5() {
        MutableDataSet options = new MutableDataSet();

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // You can re-use parser and renderer instances
        Node document = parser.parse(stringGet);
        stringGet = renderer.render(document);

        Log.i("TAG", stringGet);
        return stringGet;
    }
}
