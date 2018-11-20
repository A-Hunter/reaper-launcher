package com.reaper.launcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reaper.launcher.utils.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Ghazi Naceur on 19/11/2018.
 */
public class GetRequest {

    public static boolean isAllNodesUP(){
        try {
            String url = "http://127.0.0.1:8080/cluster/testcluster";

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);

            HttpResponse response = client.execute(request);

            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            boolean allNodesAreUP = true;

            if (result == null){
                System.out.println("result : "+result);
            }

            JSONObject object = new JSONObject(result.toString());
            System.out.println(object);

            if (object.toMap() != null){
                allNodesAreUP = JsonParser.isAllNodesAreUP(object.toMap());
            }
            System.out.println(allNodesAreUP);

            return allNodesAreUP;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
}
