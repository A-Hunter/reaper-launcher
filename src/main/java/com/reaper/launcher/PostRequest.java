package com.reaper.launcher;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by Ghazi Naceur on 19/11/2018.
 */
public class PostRequest {

    public static boolean isConnectToCluster() {

        String url = "http://127.0.0.1:8080/cluster?seedHost=localhost";

        //TODO   if url returns 201 or 204 => OK continue
        //TODO   if url returns 400 => Bad Request
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(url);

            HttpResponse response = client.execute(request);

            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

            return response.getStatusLine().getStatusCode() != 400;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
