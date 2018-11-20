package com.reaper.launcher;

/**
 * Created by Ghazi Naceur on 19/11/2018.
 */
public class Main {

    public static void main(String[] args) {

        GetRequest get = new GetRequest();

        PostRequest post = new PostRequest();
        boolean connectToCluster = post.isConnectToCluster();
        if (connectToCluster == false){
            // Can't connect to ip
            System.exit(-1);
        }

        get.isAllNodesUP();
    }
}
