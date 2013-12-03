#Openbus


A framework for centralized logging that supports batch and real time ingestion and analysis of events.

Openbus is currently splitted in the following subprojects:

 -  [openbus-broker](./openbus-broker): Messaging layer: based in [Apache Kafka](https://kafka.apache.org/).
 -  [openbus-batch](./openbus-batch): Batch processes for ingestion and analysis. Based on [Hadoop](http://hadoop.apache.org/) and related technologies.
 -  [openbus-realtime](./openbus-realtime): Real time consumptiom and analysis of events. Based on [Storm] (http://storm-project.net/).

##Use Cases

Some use cases where openbus could be applied are:

	- Web analytics
	- Social Network Analysis
	- Security Information and Event Management

##Installation

To deploy openbus on your environment, you need to first install all dependencies and then the openbus code itself.

### Installing dependencies

  - Install Hadoop
  - [Install Kafka] (https://github.com/Produban/openbus/wiki/Deploying-Kafka-in-RHEL-6.4)
  - [Install Storm] (https://github.com/Produban/openbus/wiki/Install-Storm-cluster)
  - Install Camus


### Installing openbus

##Executing openbus

### Submitting events to the Kafka broker

### Running batch ETL processes from Kafka to Hadoop

### Running real time analysis with Storm topologies

### Visualizing data
