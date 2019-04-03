package com.reaper.launcher.utils;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;


public class Client {

    private Client() {
        super();
    }

    @Deprecated
    public static DefaultHttpClient getThreadSafeClient() {

        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 60000);
        HttpConnectionParams.setSoTimeout(params, 60000);
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(mgr.getSchemeRegistry()));
        client.setParams(params);
        return client;
    }
}
