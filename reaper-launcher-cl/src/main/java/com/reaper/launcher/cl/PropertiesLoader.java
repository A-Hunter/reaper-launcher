package com.reaper.launcher.cl;

import com.reaper.launcher.lib.ReaperLauncherException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

    private static final String APPLICATION_CONFIGURATION_FILE = "application.properties";
    private static Properties appProperties = new Properties();
    private static final String WRONG_PARAMETER = "A required parameter is missing from the configuration file : ";

    private static String cassandraReaperIP = "cassandra.reaper.ip";
    private static String cassandraClusterName = "cassandra.cluster.name";
    private static String cassandraKeyspace = "cassandra.keyspace";
    private static String cassandraRepairThreadCount = "cassandra.repair.thread.count";

    public static String cassandraReaper = "";
    public static String cassandraCluster = "";
    public static String cassandraKeyspaceName = "";
    public static String reaperJobOwner = "iot_team";
    public static Integer repairThreadCount;
    public static boolean incrementalRepair = false;

    static {
        loadApplicationProperties();
        parseProperties();
    }

    private PropertiesLoader() {
        super();
    }

    private static Properties loadApplicationProperties() {

        try (InputStream stream = Configuration.class.getClassLoader().getResourceAsStream(APPLICATION_CONFIGURATION_FILE)) {
            appProperties.load(stream);
        } catch (Exception e) {
            logger.error("An error occurred when trying to load properties from the '" + APPLICATION_CONFIGURATION_FILE + "' file : {}", e);
        }
        return appProperties;
    }

    private static void parseProperties() {
        checkForParameterValidity(cassandraReaperIP);
        cassandraReaper = appProperties.getProperty(cassandraReaperIP);

        checkForParameterValidity(cassandraClusterName);
        cassandraCluster = appProperties.getProperty(cassandraClusterName);

        checkForParameterValidity(cassandraKeyspace);
        cassandraKeyspaceName = appProperties.getProperty(cassandraKeyspace);

        checkForParameterValidity(cassandraRepairThreadCount);
        repairThreadCount = Integer.valueOf(appProperties.getProperty(cassandraRepairThreadCount));
    }

    private static void checkForParameterValidity(final String name) {
        if (appProperties.getProperty(name) == null) {
            logger.error(WRONG_PARAMETER, name);
            throw new ReaperLauncherException(WRONG_PARAMETER + name);
        }
    }
}
