/*
* Copyright 2013 Produban
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.produban.openbus.processor.topology;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.state.StateFactory;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.contrib.hbase.trident.HBaseAggregateState;
import backtype.storm.contrib.hbase.utils.TridentConfig;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;

import com.produban.openbus.processor.aggregator.WebServerLog2TSDB;
import com.produban.openbus.processor.filter.WebServerLogFilter;
import com.produban.openbus.processor.function.AvroLogDecoder;
import com.produban.openbus.processor.function.DatePartition;
import com.produban.openbus.processor.function.HDFSPersistence;
import com.produban.openbus.processor.properties.Conf;
import com.produban.openbus.processor.properties.Constant;
import com.produban.openbus.processor.spout.BrokerSpout;
import com.produban.openbus.processor.util.LogFilter;
import com.produban.openbus.processor.util.OptionsTip;

/**
 * Topology openbus-realtime
 * 
 */
public class OpenbusProcessorTopology {	
	private static final Logger LOG = LoggerFactory.getLogger(OpenbusProcessorTopology.class);
	    	
	public static StormTopology buildTopology(Config conf) {			
		TridentTopology topology = new TridentTopology();
		Stream stream = null;
		
		List<String> fieldsWebLog = new ArrayList<String>();
		fieldsWebLog.add("host");
		fieldsWebLog.add("log");
		fieldsWebLog.add("user");
		fieldsWebLog.add("datetime");
		fieldsWebLog.add("request");
		fieldsWebLog.add("status");
		fieldsWebLog.add("size");
		fieldsWebLog.add("referer");
		fieldsWebLog.add("userAgent");
		fieldsWebLog.add("session");
		fieldsWebLog.add("responseTime");
		fieldsWebLog.add("timestamp");
		fieldsWebLog.add("json");
				
	    @SuppressWarnings("rawtypes")
		TridentConfig configRequest = new TridentConfig(
	    		(String)conf.get(Conf.PROP_HBASE_TABLE_REQUEST), 
	    		(String)conf.get(Conf.PROP_HBASE_ROWID_REQUEST));
	    	  
		@SuppressWarnings("unchecked")
		StateFactory stateRequest = HBaseAggregateState.transactional(configRequest);
	    
	    @SuppressWarnings("rawtypes")
	    TridentConfig configUser = new TridentConfig(
	    		(String)conf.get(Conf.PROP_HBASE_TABLE_USER), 
	    		(String)conf.get(Conf.PROP_HBASE_ROWID_REQUEST));
	    
	    @SuppressWarnings("unchecked")
	    StateFactory stateUser = HBaseAggregateState.transactional(configUser);
		
	    BrokerSpout openbusBrokerSpout = null;
	    
	    if(conf.get(Conf.STATIC_HOST) == null || "".equals(conf.get(Conf.STATIC_HOST))) {
			openbusBrokerSpout = new BrokerSpout(
					(String)conf.get(Conf.PROP_BROKER_TOPIC), 
					(String)conf.get(Conf.PROP_ZOOKEEPER_HOST), 
					(String)conf.get(Conf.PROP_ZOOKEEPER_BROKER),
					(String)conf.get(Conf.PROP_KAFKA_IDCLIENT));	    
	    } else {
	    	openbusBrokerSpout = new BrokerSpout(
	    			(String)conf.get(Conf.PROP_BROKER_TOPIC), 
	    			(String)conf.get(Conf.STATIC_HOST), 
	    			new Integer(Conf.KAFKA_BROKER_PORT).intValue(),
	    			(String)conf.get(Conf.PROP_KAFKA_IDCLIENT));	    
	    }
	    
		stream = topology.newStream("spout", openbusBrokerSpout.getPartitionedTridentSpout());			
		stream = stream.each(new Fields("bytes"), new AvroLogDecoder(), new Fields(fieldsWebLog));	    	   	    	    
		stream = stream.each(new Fields(fieldsWebLog), new WebServerLogFilter());
		
		stream.each(new Fields("request", "datetime"), new DatePartition(), new Fields("cq", "cf"))
				.groupBy(new Fields("request", "cq", "cf"))
				.persistentAggregate(stateRequest, new Count(), new Fields("count"))
				//.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"))	// Test				
				.newValuesStream()
				.each(new Fields("request", "cq", "cf", "count"), new LogFilter());
		
		stream.each(new Fields("user", "datetime"), new DatePartition(), new Fields("cq", "cf"))
				.groupBy(new Fields("user", "cq", "cf"))
				.persistentAggregate(stateUser, new Count(), new Fields("count"))
				//.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"))	// Test				
				.newValuesStream()
				.each(new Fields("user", "cq", "cf", "count"), new LogFilter());
  
		if (Constant.YES.equals(conf.get(Conf.PROP_OPENTSDB_USE))) {
			LOG.info("OpenTSDB: " + conf.get(Conf.PROP_OPENTSDB_USE));
			stream.groupBy(new Fields(fieldsWebLog)).aggregate(new Fields(fieldsWebLog), new WebServerLog2TSDB(), new Fields("count"));
		}
		
		if (Constant.YES.equals(conf.get(Conf.PROP_HDFS_USE))) {
			LOG.info("HDFS: " + conf.get(Conf.PROP_HDFS_USE));
			stream.each(new Fields(fieldsWebLog), new HDFSPersistence(), new Fields("result"));
		}
		
		return topology.build();				
	}

	public static void main(String[] args) throws Exception {	
		Map<String, String> options = OptionsTip.getOptions(args);
		
		Config conf = new Config();		
		conf.put(Conf.PROP_BROKER_TOPIC, (String)options.get("topic"));
		conf.put(Conf.PROP_KAFKA_IDCLIENT, Conf.KAFKA_IDCLIENT);
		conf.put(Conf.PROP_ZOOKEEPER_HOST, (String)options.get("zookepperHost"));
		conf.put(Conf.PROP_ZOOKEEPER_BROKER, (String)options.get("broker"));
		conf.put(Conf.PROP_HBASE_TABLE_REQUEST, Conf.HBASE_TABLE_REQUEST);
		conf.put(Conf.PROP_HBASE_ROWID_USER, Conf.HBASE_ROWID_USER);
		conf.put(Conf.PROP_HBASE_TABLE_USER, Conf.HBASE_TABLE_USER);
		conf.put(Conf.PROP_HBASE_ROWID_REQUEST, Conf.HBASE_ROWID_REQUEST);
		conf.put(Conf.PROP_OPENTSDB_USE, (String)options.get(Conf.PROP_OPENTSDB_USE));						
		conf.put(Conf.PROP_HDFS_USE, (String)options.get(Conf.PROP_HDFS_USE));
		conf.put(Conf.STATIC_HOST, (String)options.get(Conf.STATIC_HOST));		
		
		if (args.length == 0 || "local".equals((String)options.get("local"))) {		
			LOG.info("Storm mode local");
			LocalCluster cluster = new LocalCluster();
		    cluster.submitTopology("openbus", conf, buildTopology(conf));			
			Thread.sleep(2000);		    		    		   
		     //cluster.shutdown();
		} else {
			LOG.info("Storm mode cluster");			
			StormSubmitter.submitTopology("openbus", conf, buildTopology(conf));
			Thread.sleep(2000);			
		}
	}			
}