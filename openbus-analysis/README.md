#openbus-batch

This subproject contains the code related with batch processes for:

  - Data ingestion from kafka broker to Hadoop
  - Batch reports with Pig, Hive or Mapreduce.
  - Custom UDFs

##Running tests

To test the Java UDFs:

  - go to [java](./java) folder
  - run:

  		mvn test

##Running Pig scripts

Before running a Pig script you will need to create the openbus-batch jar file.

To do that, simply go to [java](./java) folder and run:

	mvn package

Note that the generated jar file name has to be the same as the jar file imported in Pig scripts

###top_users.pig

This script takes as input a raw log of proxy activity and calculates the users that appear most on it.

It expects two parameters:
  
  - `input_log`: the path for the input log
  - `N`: the maximum number of users to be displayed on final input

Example (from hadoop client node):

	pig -param input_log=/user/gpadmin/proxy.log -param N=10 top_users.pig