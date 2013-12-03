#openbus-realtime

---

Kafka are consumed for processing messages with Trident Storm 

Is necesary install the proyect storm-kafka-0.8-plus (https://github.com/wurstmeister/storm-kafka-0.8-plus) in the repository of maven.
Is necesary install the proyect storm-hbase (https://github.com/jrkinley/storm-hbase) in the repository of maven.

#### Publish storm-kafka-0.8-plus to local maven repo

    $ mvn install:install-file -Dfile=libs/storm-kafka-0.8-plus-0.1.0-SNAPSHOT.jar -DgroupId=storm -DartifactId=storm-kafka-0.8-plus -Dversion=0.1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true 

#### Publish storm-hbase to local maven repo

    $ mvn install:install-file -Dfile=libs/storm-hbase-0.0.1-SNAPSHOT.jar -DgroupId=storm.contrib -DartifactId=storm-hbase -Dversion=0.0.1-SNAPSHOT -Dpackaging=jar -DgeneratePom=true 


#### Compile

    $ mvn clean compile


#### Execute example TridentWordCount in local

    $ mvn exec:java -Dexec.mainClass="com.produban.openbus.processor.topology.OpenbusProcessorTopology"


#### Example for verification cluster Storm with Trident

    $ mvn package
    $ storm jar target/openbus-realtime-0.0.1-shaded.jarr com.produban.openbus.processor.topology.OpenbusProcessorTopology openbus


#### Installing Apache Storm in Cluster
* <a href="https://github.com/Produban/openbus/wiki/Install-Storm-cluster">Installing Apache Storm in Cluster</a>

	$ storm jar target/openbus-realtime-0.0.1-shaded.jar com.produban.openbus.processor.topology.OpenbusProcessorTopology openbus [-zookepperHost hostZookepper:port] [-topic webserverlog] [-staticHost hostKafka] [-broker brokers]

##### Example
	$ storm jar target/openbus-realtime-0.0.1-shaded.jar com.produban.openbus.processor.topology.OpenbusProcessorTopology openbus -zookepperHost vmlbcnimbusl01:2181 -topic webserverlog -staticHost vmlbcbrokerl02
	

#### HBase

	Settings in file: \src\main\resources\hbase-site.xml
	
##### Tables in HBase
	
	$ create 'wslog_request', {NAME => 'data', 31536000 => 1},{NAME => 'hourly', VERSION => 1, TTL => 31536000}, {NAME => 'daily', VERSION => 1, TTL => 31536000}, {NAME => 'weekly', VERSION => 1, TTL => 31536000}, {NAME => 'monthly', VERSION => 1, TTL => 31536000}
	$ create 'wslog_user', {NAME => 'data', VERSIONS => 1}, {NAME => 'hourly', VERSION => 1, TTL => 31536000}, {NAME => 'daily', VERSION => 1, TTL => 31536000}, {NAME => 'weekly', VERSION => 1, TTL => 31536000}, {NAME => 'monthly', VERSION => 1, TTL => 31536000}
	$ create 'wslog_session', {NAME => 'data', VERSIONS => 1}, {NAME => 'hourly', VERSION => 1, TTL => 31536000}, {NAME => 'daily', VERSION => 1, TTL => 31536000}, {NAME => 'weekly', VERSION => 1, TTL => 31536000}, {NAME => 'monthly', VERSION => 1, TTL => 31536000}

#### Install Storm cluster

