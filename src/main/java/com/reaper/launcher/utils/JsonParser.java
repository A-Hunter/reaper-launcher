package com.reaper.launcher.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Ghazi Naceur on 19/11/2018.
 */
public class JsonParser {

    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        try {

            // read JSON from a file
            Map<String, Object> map = mapper.readValue(
                    new File("E:\\GitHubRepositories\\reaper-launcher\\src\\main\\resources\\get_request.json"),
                    new TypeReference<Map<String, Object>>() {
                    });

            boolean state = isAllNodesAreUP(map);

//            for (String node : seed_hosts) {
//                if (!names.contains(node)) {
//                    state = false;
//                } else {
//                    state = true;
//                }
//            }

            System.out.println(state);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isAllNodesAreUP(Map<String, Object> map) {
        System.out.println(map.get("seed_hosts"));
        List<String> seed_hosts = (List<String>) map.get("seed_hosts");

        Map<String, Object> nodesStatus = (HashMap<String, Object>) map.get("nodes_status");

        if (nodesStatus == null || nodesStatus.get("endpointStates") == null){
            return false;
        }

        List<Object> objectMap = (ArrayList<Object>) nodesStatus.get("endpointStates");
        final Map<String, Object> obj = (Map<String, Object>) objectMap.get(0);
        System.out.println(obj.get("endpointNames"));
        boolean state = true;
        List<String> names = (List<String>)  obj.get("endpointNames");

        if (seed_hosts == null || names == null){
            return false;
        }

        if (names.equals(seed_hosts)) {
            state = true;
        } else {
            state = false;
        }
        return state;
    }
}
