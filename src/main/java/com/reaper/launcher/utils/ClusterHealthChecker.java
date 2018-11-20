package com.reaper.launcher.utils;

import java.util.*;

/**
 * Created by Ghazi Naceur on 20/11/2018.
 */
public class ClusterHealthChecker implements Runnable {

    Map<String, Object> map;

    public static boolean isAllNodesAreUP(Map<String, Object> map) {
        System.out.println(map.get("seed_hosts"));
        List<String> seed_hosts = (List<String>) map.get("seed_hosts");

        Map<String, Object> nodesStatus = (HashMap<String, Object>) map.get("nodes_status");

        if (nodesStatus == null || nodesStatus.get("endpointStates") == null) {
//            return false;
            return true;
        }

        List<Object> objectMap = (ArrayList<Object>) nodesStatus.get("endpointStates");
        final Map<String, Object> obj = (Map<String, Object>) objectMap.get(0);
        System.out.println(obj.get("endpointNames"));
        List<String> names = (List<String>) obj.get("endpointNames");

        return seed_hosts != null && names != null && names.equals(seed_hosts);

    }

    public ClusterHealthChecker() {
        super();
    }

    public ClusterHealthChecker(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public void run() {
        System.out.println(map.get("seed_hosts"));
        List<String> seed_hosts = (List<String>) map.get("seed_hosts");

        Map<String, Object> nodesStatus = (HashMap<String, Object>) map.get("nodes_status");

        if (nodesStatus == null || nodesStatus.get("endpointStates") == null) {
//            return false;
            System.out.println("Not all nodes are UP !!!");
        }

        List<Object> objectMap = (ArrayList<Object>) nodesStatus.get("endpointStates");
        final Map<String, Object> obj = (Map<String, Object>) objectMap.get(0);
        System.out.println(obj.get("endpointNames"));
        List<String> names = (List<String>) obj.get("endpointNames");

//        return seed_hosts != null && names != null && names.equals(seed_hosts);
        if (seed_hosts != null && names != null && names.equals(seed_hosts)){
            System.out.println("All nodes are UP !!!");
        }
        System.out.println(new Date());
    }
}
