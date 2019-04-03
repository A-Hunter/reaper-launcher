package com.reaper.launcher;

import com.reaper.launcher.exception.MissingParameterException;
import com.reaper.launcher.utils.Client;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.reaper.launcher.utils.PropertiesLoader.reaperJobOwner;

public class CassandraReaperRequests {

    private static final Logger logger = LoggerFactory.getLogger(CassandraReaperRequests.class);
    private static final String HTTP = "http://";
    private static final String RESPONSE_CODE = "Response Code : ";
    private static final String REPAIR_RUN_URL = ":8080/repair_run";
    private static final String REPAIR_JOBS_NODE = "{\"jobs\":";
    private static final String STATE_NODE = "state";
    private static final String SEGMENTS_REPAIRED_NODE = "segments_repaired";
    private static final String TOTAL_SEGMENTS_NODE = "total_segments";

    private CassandraReaperRequests() {
        super();
    }

    @Deprecated
    public static boolean isConnectedToCluster(String nodeIp, String seedHost) {

        StringBuilder url = new StringBuilder(HTTP)
                .append(nodeIp)
                .append(":8080/cluster?seedHost=")
                .append(seedHost);

        try {
            HttpClient client = Client.getThreadSafeClient();
            HttpPost request = new HttpPost(url.toString());
            HttpResponse response = client.execute(request);

            if (logger.isDebugEnabled()) {
                logger.debug("Sending 'POST' request to URL  : {}", url);
                logger.debug(RESPONSE_CODE, response.getStatusLine().getStatusCode());
            }
            request.releaseConnection();
            ((DefaultHttpClient) client).close();
            return response.getStatusLine().getStatusCode() != 400;
        } catch (Exception e) {
            logger.error("An error occurred when trying to connect to cluster : {}", e);
            return false;
        }
    }

    @Deprecated
    public static String createRepair(String nodeIp, String clusterName, String keyspace, Integer repairThreadCount, Boolean incrementalRepair) {

        String uuid = "";
        StringBuilder url = new StringBuilder(HTTP)
                .append(nodeIp)
                .append(":8080/repair_run?clusterName=")
                .append(clusterName)
                .append("&keyspace=")
                .append(keyspace)
                .append("&owner=")
                .append(reaperJobOwner)
                .append("&repairThreadCount=")
                .append(repairThreadCount)
                .append("&incrementalRepair=")
                .append(incrementalRepair.toString());

        try {
            HttpClient client = Client.getThreadSafeClient();
            HttpPost request = new HttpPost(url.toString());
            HttpResponse response = client.execute(request);

            if (logger.isDebugEnabled()) {
                logger.debug("Sending 'POST' request to URL : {}", url);
                logger.debug(RESPONSE_CODE, response.getStatusLine().getStatusCode());
            }
            if (response.getHeaders("Location").length != 0) {
                String[] locations = response.getHeaders("Location")[0].getValue().split("/");
                uuid = locations[locations.length - 1];
                logger.info("A repair was created with the id : {}", uuid);
            }
            request.releaseConnection();
            ((DefaultHttpClient) client).close();
            if (!uuid.isEmpty()) {
                return uuid;
            } else {
                throw new MissingParameterException("The uuid is empty. You need to reconnect to the cluster through the command '> ./run.sh  -connect  cassandra-reaper-ip  cassandra-entry-point'.");
            }
        } catch (Exception e) {
            logger.error("An error occurred when trying to create a repair : {}", e);
            return null;
        }
    }

    @Deprecated
    public static void startRepair(String nodeIp, String repairUuid) {

        StringBuilder url = new StringBuilder(HTTP)
                .append(nodeIp)
                .append(REPAIR_RUN_URL)
                .append("/")
                .append(repairUuid)
                .append("/state/RUNNING");

        try {
            HttpClient client = Client.getThreadSafeClient();
            HttpPut request = new HttpPut(url.toString());
            HttpResponse response = client.execute(request);

            if (logger.isDebugEnabled()) {
                logger.debug("Sending 'PUT' request to URL : {}", url);
                logger.debug(RESPONSE_CODE, response.getStatusLine().getStatusCode());
            }

            if (response.getStatusLine().getStatusCode() == 200) {
                logger.info("A repair with the id '{}' was activated.", repairUuid);
            }
            request.releaseConnection();
            ((DefaultHttpClient) client).close();
        } catch (Exception e) {
            logger.error("An error occurred when trying to start a repair : {}", e);
        }
    }

    @Deprecated
    public static void stopRepair(String nodeIp, String repairUuid) {

        StringBuilder url = new StringBuilder(HTTP)
                .append(nodeIp)
                .append(REPAIR_RUN_URL)
                .append("/")
                .append(repairUuid)
                .append("/state/PAUSED");

        try {
            HttpClient client = Client.getThreadSafeClient();
            HttpPut request = new HttpPut(url.toString());
            HttpResponse response = client.execute(request);

            if (logger.isDebugEnabled()) {
                logger.debug("Sending 'PUT' request to URL : {}", url);
                logger.debug(RESPONSE_CODE, response.getStatusLine().getStatusCode());
            }

            if (response.getStatusLine().getStatusCode() == 200) {
                logger.info("A repair with the id '{}' was stopped.", repairUuid);
            }
            request.releaseConnection();
            ((DefaultHttpClient) client).close();
        } catch (Exception e) {
            logger.error("An error occurred when trying to stop a repair : {}", e);
        }
    }

    @Deprecated
    public static boolean isRunning(String nodeIp, String uuid) {

        StringBuilder url = new StringBuilder(HTTP)
                .append(nodeIp)
                .append(REPAIR_RUN_URL);
        try {
            HttpClient client = Client.getThreadSafeClient();
            HttpGet request = new HttpGet(url.toString());
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            if (!result.toString().substring(1, result.length() - 1).isEmpty()) {
                JSONObject object = new JSONObject(REPAIR_JOBS_NODE + result.toString() + "}");

                JSONArray values = object.getJSONArray("jobs");

                for (int i = 0; i < values.length(); i++) {

                    JSONObject job = values.getJSONObject(i);
                    String state = (String) job.get(STATE_NODE);
                    String id = (String) job.get("id");

                    if (state.equals("RUNNING") && id.equals(uuid)) {
                        return true;
                    }
                }
                return false;
            }
            request.releaseConnection();
            ((DefaultHttpClient) client).close();
        } catch (Exception e) {
            logger.error("An error occurred when trying to verify the completion of the current repair : {}", e);
        }
        return false;
    }

    @Deprecated
    public static void deleteRepair(String nodeIp, String uuid) {

        StringBuilder url = new StringBuilder(HTTP)
                .append(nodeIp)
                .append(REPAIR_RUN_URL)
                .append("/")
                .append(uuid)
                .append("?owner=")
                .append(reaperJobOwner);

        try {
            HttpClient client = Client.getThreadSafeClient();
            HttpDelete delete = new HttpDelete(url.toString());
            HttpResponse response = client.execute(delete);

            if (logger.isDebugEnabled()) {
                logger.debug("Sending 'DELETE' request to URL : {}", url);
                logger.debug(RESPONSE_CODE, response.getStatusLine().getStatusCode());
            }

            if (response.getStatusLine().getStatusCode() == 202) {
                logger.info("The repair '{}' was deleted.", uuid);
            }
            delete.releaseConnection();
            ((DefaultHttpClient) client).close();
        } catch (Exception e) {
            logger.error("An error occurred when trying to delete the previous repair : ", e);
        }
    }

    @Deprecated
    public static void monitorTheCurrentRepair(String nodeIp) {

        StringBuilder url = new StringBuilder(HTTP)
                .append(nodeIp)
                .append(REPAIR_RUN_URL);
        boolean isNearlyCompleted = false;
        Integer previousState = -1;
        String id = "";
        JSONObject job;
        try {
            HttpClient client = Client.getThreadSafeClient();
            HttpGet request = new HttpGet(url.toString());
            while (!isNearlyCompleted) {

                HttpResponse response = client.execute(request);

                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                if (!result.toString().substring(1, result.length() - 1).isEmpty()) {

                    JSONObject object = new JSONObject(REPAIR_JOBS_NODE + result.toString() + "}");
                    JSONArray values = object.getJSONArray("jobs");
                    for (int i = 0; i < values.length(); i++) {
                        job = values.getJSONObject(i);
                        if (job.get(STATE_NODE).equals("RUNNING")) {
                            id = (String) job.get("id");
                            if (previousState != job.get(SEGMENTS_REPAIRED_NODE)) {
                                logger.info("Repaired segments : {}/{}" , job.get(SEGMENTS_REPAIRED_NODE), job.get(TOTAL_SEGMENTS_NODE));
                            }
                            if (job.get(SEGMENTS_REPAIRED_NODE) == job.get(TOTAL_SEGMENTS_NODE)) {
                                isNearlyCompleted = true;
                            }
                            previousState = (Integer) job.get(SEGMENTS_REPAIRED_NODE);
                        }
                    }
                }
            }

            StringBuilder b = new StringBuilder(HTTP)
                    .append(nodeIp)
                    .append(REPAIR_RUN_URL)
                    .append("/")
                    .append(id);
            HttpGet rqt = new HttpGet(b.toString());
            boolean isDone = false;
            while (!isDone) {
                HttpResponse response = client.execute(rqt);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                JSONObject object = new JSONObject(result.toString());
                if (object.get(STATE_NODE) != null && object.get(STATE_NODE).equals("DONE")) {
                    isDone = true;
                }
            }
            request.releaseConnection();
            rqt.releaseConnection();
            ((DefaultHttpClient) client).close();
        } catch (Exception e) {
            logger.error("An error occurred when trying to verify the completion of the current repair : {}", e);
        }
    }

    @Deprecated
    public static void listJobs(String nodeIp) {

        StringBuilder url = new StringBuilder(HTTP)
                .append(nodeIp)
                .append(REPAIR_RUN_URL);
        try {
            HttpClient client = Client.getThreadSafeClient();
            HttpGet request = new HttpGet(url.toString());
            HttpResponse response = client.execute(request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            if (!result.toString().substring(1, result.length() - 1).isEmpty()) {

                JSONObject object = new JSONObject(REPAIR_JOBS_NODE + result.toString() + "}");
                JSONArray values = object.getJSONArray("jobs");
                for (int i = 0; i < values.length(); i++) {
                    int j = i + 1;
                    JSONObject job = values.getJSONObject(i);
                    logger.info("*****  Repair Job {}  *****", j);
                    logger.info("Repair id : {}", job.get("id"));
                    logger.info("Repair state : {}", job.get(STATE_NODE));
                    logger.info("Repaired segments : {}/{}", job.get(SEGMENTS_REPAIRED_NODE), job.get(TOTAL_SEGMENTS_NODE));
                    logger.info("Repair estimated time : {}", job.get("estimated_time_of_arrival"));
                    logger.info("Repair start time : {}", job.get("start_time"));
                    logger.info("Repair end time : {}", job.get("end_time"));
                    logger.info("Repair pause time : {}", job.get("pause_time"));
                    logger.info("Repair duration : {}", job.get("duration"));
                    logger.info("");
                }
            } else {
                logger.info("These are no repair jobs.");
            }
            request.releaseConnection();
            ((DefaultHttpClient) client).close();
        } catch (Exception e) {
            logger.error("An error occurred when trying to list the repair jobs : {}", e);
        }
    }
}
