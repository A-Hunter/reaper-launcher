package com.reaper.launcher;

/**
 * Created by Ghazi Naceur on 19/11/2018.
 */
public class Main {

    public static void main(String[] args) {

        boolean connectToCluster = PostRequest.isConnectToCluster();
        if (!connectToCluster){
            // Can't connect to ip
            System.exit(-1);
        }

        GetRequest.isAllNodesUP();
    }
}
