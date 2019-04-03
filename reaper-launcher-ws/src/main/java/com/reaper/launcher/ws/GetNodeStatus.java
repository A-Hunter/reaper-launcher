package com.reaper.launcher.ws;


import com.reaper.launcher.lib.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.reaper.launcher.lib.CassandraReaperRequests.*;
import static com.reaper.launcher.ws.PropertiesLoader.*;


@Deprecated
@RestController
public class GetNodeStatus {

    private static Logger logger = LoggerFactory.getLogger(GetNodeStatus.class);

    private boolean repairAlreadyCreated = false;

    @RequestMapping("/cassandra")
    public void getStatus(@RequestParam(value = "ip") String ip) {
        logger.info("The node '{}' has started.", ip);

        if (!isConnectedToCluster(cassandraReaper, ip)) {
            logger.error("The service is unreachable. You may verify the connectivity to the reaper ip : '{}' and the cassandra ip :'{}'.", cassandraReaper, ip);
            System.exit(-1);
        }

        try {
            if (!repairAlreadyCreated) {
                repairAlreadyCreated = true;
                String repairUuid = createRepair(cassandraReaper, cassandraCluster, cassandraKeyspaceName, repairThreadCount, incrementalRepair, reaperJobOwner);
                Thread.sleep(DateUtils.remainingMilliSecondsToTheNext15Minutes());
                startRepair(cassandraReaper, repairUuid);
                monitorTheCurrentRepair(cassandraReaper);
                deleteRepair(cassandraReaper, repairUuid, reaperJobOwner);
                repairAlreadyCreated = false;
            }
        } catch (Exception e) {
            logger.error("Error occurred when trying to launch repair : {}", e);
        }
    }
}
