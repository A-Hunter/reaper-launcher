package com.reaper.launcher.cl;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static com.reaper.launcher.cl.PropertiesLoader.incrementalRepair;
import static com.reaper.launcher.cl.PropertiesLoader.reaperJobOwner;
import static com.reaper.launcher.lib.CassandraReaperRequests.*;


@Deprecated
@SpringBootApplication
public class ReaperLauncherCL implements CommandLineRunner {


    public static void main(String[] args) {
        new SpringApplicationBuilder(ReaperLauncherCL.class)
                .logStartupInfo(false)
                .run(args);
    }

    private static String constructRepair(String[] args) {
        String reaperIp;
        reaperIp = args[1];
        String cassandraCluster = args[2];
        String cassandraKeyspaceName = args[3];
        Integer repairThreadCount = Integer.valueOf(args[4]);
        if (repairThreadCount > 4) {
            System.out.println("The max number of concurrent threads to perform the repair is 4. Your provided value will be overridden and set to '4' instead of '{}'." + repairThreadCount);
        }
        repairThreadCount = 4;
        return createRepair(reaperIp, cassandraCluster, cassandraKeyspaceName, repairThreadCount, incrementalRepair, PropertiesLoader.reaperJobOwner);
    }

    private static void showUsage() {
        System.out.println("The Reaper Launcher usage :");
        System.out.println("");
        showConnectOperationUsage();
        System.out.println("");
        showListOperationUsage();
        System.out.println("");
        showCreateRepairOperationUsage();
        System.out.println("");
        showStartRepairOperationUsage();
        System.out.println("");
        showCreateAndStartRepairOperation();
        System.out.println("");
        showMonitorOperationUsage();
        System.out.println("");
        showCreateRunMonitorRepairOperationUsage();
        System.out.println("");
        showRunMonitorAndDeleteOperationUsage();
        System.out.println("");
        showStopOperationUsage();
        System.out.println("");
        showDeleteRepairOperationUsage();
    }

    private static void showDeleteRepairOperationUsage() {
        System.out.println("In order to delete a specific repair job :");
        System.out.println("  > ./run.sh  -delete-repair  cassandra-reaper-ip  repair-job-uuid");
    }

    private static void showStopOperationUsage() {
        System.out.println("In order to stop a specific repair job :");
        System.out.println("  > ./run.sh  -stop-repair  cassandra-reaper-ip  repair-job-uuid");
    }

    private static void showRunMonitorAndDeleteOperationUsage() {
        System.out.println("In order to create, run, monitor and delete a repair job :");
        System.out.println("  > ./run.sh  -run-monitor-and-delete  cassandra-reaper-ip  cluster-name  keyspace  thread-repair-number");
    }

    private static void showCreateRunMonitorRepairOperationUsage() {
        System.out.println("In order to create, run and monitor a repair job :");
        System.out.println("  > ./run.sh  -run-and-monitor  cassandra-reaper-ip  cluster-name  keyspace  thread-repair-number");
    }

    private static void showMonitorOperationUsage() {
        System.out.println("In order to monitor :");
        System.out.println("  > ./run.sh  -monitor  cassandra-reaper-ip");
    }

    private static void showCreateAndStartRepairOperation() {
        System.out.println("In order to create and start a repair job :");
        System.out.println("  > ./run.sh  -create-and-start  cassandra-reaper-ip  cluster-name  keyspace  thread-repair-number");
    }

    private static void showStartRepairOperationUsage() {
        System.out.println("In order to start a repair job :");
        System.out.println("  > ./run.sh  -start-repair  cassandra-reaper-ip  uuid");
    }

    private static void showCreateRepairOperationUsage() {
        System.out.println("In order to create a repair job :");
        System.out.println("  > ./run.sh  -create-repair  cassandra-reaper-ip  cluster-name  keyspace  thread-repair-number");
    }

    private static void showListOperationUsage() {
        System.out.println("In order to list all repair jobs :");
        System.out.println("  > ./run.sh  -list  cassandra-reaper-ip");
    }

    private static void showConnectOperationUsage() {
        System.out.println("In order to connect to the cluster :");
        System.out.println("  > ./run.sh  -connect  cassandra-reaper-ip  cassandra-entry-point");
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) {
            showUsage();
            System.exit(-1);
        }
        String operation = args[0];
        String reaperIp;
        try {

            switch (operation) {
                case "-connect":
                    if (args.length != 3) {
                        showConnectOperationUsage();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    String cassandraEntryPoint = args[2];
                    if (!isConnectedToCluster(reaperIp, cassandraEntryPoint)) {
                        System.out.println("The service is unreachable. You may verify the connectivity to the reaper ip : " + reaperIp + " and the cassandra ip :" + cassandraEntryPoint);
                        System.exit(-1);
                    } else {
                        System.out.println("You are successfully connected to the cluster through the entry point : {}" + cassandraEntryPoint);
                    }
                    break;
                case "-list":
                    if (args.length != 2) {
                        showListOperationUsage();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    listJobs(reaperIp);
                    break;
                case "-create-repair":
                    if (args.length != 5) {
                        showCreateRepairOperationUsage();
                        System.exit(-1);
                    }
                    constructRepair(args);
                    break;
                case "-start-repair":
                    if (args.length != 3) {
                        showStartRepairOperationUsage();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    String uuid = args[2];
                    startRepair(reaperIp, uuid);
                    break;
                case "-create-and-start":
                    if (args.length != 5) {
                        showCreateAndStartRepairOperation();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    startRepair(reaperIp, constructRepair(args));
                    break;
                case "-monitor":
                    if (args.length != 2) {
                        showMonitorOperationUsage();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    monitorTheCurrentRepair(reaperIp);
                    break;
                case "-run-and-monitor":
                    if (args.length != 5) {
                        showCreateRunMonitorRepairOperationUsage();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    startRepair(reaperIp, constructRepair(args));
                    monitorTheCurrentRepair(reaperIp);
                    break;
                case "-run-monitor-and-delete":
                    if (args.length != 5) {
                        showRunMonitorAndDeleteOperationUsage();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    startRepair(reaperIp, constructRepair(args));
                    monitorTheCurrentRepair(reaperIp);
                    deleteRepair(reaperIp, constructRepair(args), reaperJobOwner);
                    break;
                case "-stop-repair":
                    if (args.length != 3) {
                        showStopOperationUsage();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    String id = args[2];
                    stopRepair(reaperIp, id);
                    break;
                case "-delete-repair":
                    if (args.length != 3) {
                        showDeleteRepairOperationUsage();
                        System.exit(-1);
                    }
                    reaperIp = args[1];
                    String uid = args[2];
                    if (isRunning(reaperIp, uid)) {
                        stopRepair(reaperIp, uid);
                    }
                    Thread.sleep(1000);
                    deleteRepair(reaperIp, uid, reaperJobOwner);
                    break;
                case "-help":
                    showUsage();
                    break;
                case "-ws":
                    // This command will make us reach the REST Webservice
                    while (true) {
                    }
                default:
                    System.out.println("'{}' operation is not supported." + operation);
                    showUsage();
            }

            System.exit(0);
        } catch (Exception e) {
            System.out.println("An error occurred when trying to perform the operation " + args[0] + " caused by : {}" + e);
        }
    }
}
