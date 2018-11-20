package com.reaper.launcher;

import com.reaper.launcher.utils.ClusterHealthChecker;

/**
 * Created by Ghazi Naceur on 19/11/2018.
 */
public class Main {

    public static void main(String[] args) {

        boolean connectToCluster = PostRequest.isConnectToCluster();
        if (!connectToCluster) {
            // Can't connect to ip
            System.exit(-1);
        }

        GetRequest.isAllNodesUP();

        if (ClusterHealthChecker.isAllNodesAreUP(GetRequest.ping())) {
            // implement repair
        } else {
            // delete old repair
        }

        // TODO the previous check should be executed periodically
        // TODO need to add a flag that should indicate previous repair
    }
}
