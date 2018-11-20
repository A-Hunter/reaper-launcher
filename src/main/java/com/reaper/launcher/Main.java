package com.reaper.launcher;

import com.reaper.launcher.utils.ClusterHealthChecker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

        while (ClusterHealthChecker.isAllNodesAreUP(GetRequest.ping())){

        }
    }
}
