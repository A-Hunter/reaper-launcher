# Reaper Launcher

The aim of this project is to launch a Cassandra repair through the Cassandra Reaper project : https://github.com/thelastpickle/cassandra-reaper . <br/>
It provides 2 entry points : <br/>
    - Command lines <br/>
    - REST webservice

### Prerequisites

 JDK 8 <br/>
 Cassandra 3 <br/>

### Getting started :
Once the repair job is created, it will only start at one of these specific minutes : HH:00 , HH:15 , HH:30 or HH:45. <br/>
So taking for example, you created a repair job at 15:07. This job will start at 15:15.
Taking another example, you have a second repair at 14:35. It will start at 14:45.

##### REST webservice :
You start the reaper-launcher-ws project. It helps to start a Cassandra repair by invoking the following url : `http://localhost:8087/cassandra/cassandra_host_ip` <br/>
The `cassandra_host_ip` is the ip of the cassandra node which came back to life again.<br/>
This project will : <br/>
    - Create a repair job <br/>
    - Wait until HH:00 , HH:15 , HH:30 or HH:45 <br/>
    - Start the repair job <br/>
    - Monitor the repair job : Showing the achievement percentage <br/>
    - Delete the repair job <br/>

##### Command lines :
You start the reaper-launcher-cl project. It provides many commands lines : <br/>
    `-connect cassandra_reaper_ip cassandra_node_ip` <br/>
    `-list cassandra_reaper_ip` <br/>
    `-create-repair cassandra_reaper_ip test_cluster my_keyspace repair_thread_count` <br/>
    `-start-repair cassandra_reaper_ip repair_id` <br/>
    `-create-and-start cassandra_reaper_ip test_cluster my_keyspace repair_thread_count` <br/>
    `-monitor cassandra_reaper_ip` <br/>
    `-run-and-monitor cassandra_reaper_ip test_cluster my_keyspace repair_thread_count` <br/>
    `-run-monitor-and-delete cassandra_reaper_ip test_cluster my_keyspace repair_thread_count` <br/>
    `-stop-repair cassandra_reaper_ip repair_id` <br/>
    `-delete-repair cassandra_reaper_ip repair_id` <br/>
    `-help` <br/>

###### Note :
The Cassandra Reaper provides a GUI to manage Cassandra repairs.
It provides also a command line interface called `spreaper`. <br/>
Check the project : https://github.com/thelastpickle/cassandra-reaper