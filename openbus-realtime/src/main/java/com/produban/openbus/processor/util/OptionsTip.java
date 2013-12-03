package com.produban.openbus.processor.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.produban.openbus.processor.properties.Conf;
import com.produban.openbus.processor.properties.Constant;

public class OptionsTip {
	private static final Logger LOG = LoggerFactory.getLogger(OptionsTip.class);
	
	public static Map<String, String> getOptions(String[] args) {
		Map<String, String> optionsStorm = new HashMap<String, String>();
		CommandLine cl = null;
		
        Options opt = new Options();        
        opt.addOption("local", false, "Mode local");
        opt.addOption("topic", true, "Topic broker");       
        opt.addOption("zookepperHost", true, "Host and port Zookepper. Ej: host:port");
        opt.addOption("broker", true, "Broker");       
        opt.addOption("opentsdb", false, "Use Opentsdb");
        opt.addOption("opentsdbHost", true, "Host Opentsdb");
        opt.addOption(Conf.PROP_HDFS_USE, false, "Use HDFS");
        opt.addOption("hbaseRoot", true, "HBase root directory");
        opt.addOption(Conf.STATIC_HOST, true, "Static hosts. Ej: host1,host2");
        BasicParser parser = new BasicParser();
        
		try {
			cl = parser.parse(opt, args);
		} catch (ParseException e) {
			LOG.warn("Options incorrect Storm ", e);
		}
				
		if (cl != null && cl.hasOption("local"))  
			optionsStorm.put("local", cl.getOptionValue("local"));            
		
		if (cl != null && cl.getOptionValue("topic") != null && !"".equals(cl.getOptionValue("topic")))  
			optionsStorm.put("topic", cl.getOptionValue("topic"));            
		else 
			optionsStorm.put("topic", Conf.KAFKA_TOPIC);
		
		if (cl != null && cl.getOptionValue("zookepperHost") != null && !"".equals(cl.getOptionValue("zookepperHost"))) 
			optionsStorm.put("zookepperHost", cl.getOptionValue("zookepperHost"));            
		else 
			optionsStorm.put("zookepperHost", Conf.ZOOKEEPER_HOST + ":" + Conf.ZOOKEEPER_PORT);
		
		if (cl != null && cl.getOptionValue("broker") != null && !"".equals(cl.getOptionValue("broker"))) 
			optionsStorm.put("broker", "/" + cl.getOptionValue("broker"));            
		else 
			optionsStorm.put("broker", Conf.ZOOKEEPER_BROKER);
		   						
		if (cl != null && cl.hasOption(Conf.PROP_OPENTSDB_USE))  
			optionsStorm.put(Conf.PROP_OPENTSDB_USE, Constant.YES);            
		else 
			optionsStorm.put(Conf.PROP_OPENTSDB_USE, Conf.OPENTSDB_USE); // Value default
		
		if (cl != null && cl.hasOption(Conf.PROP_HDFS_USE))  
			optionsStorm.put(Conf.PROP_HDFS_USE, Constant.YES);            
		else  
			optionsStorm.put(Conf.PROP_HDFS_USE, Conf.HDFS_USE);  // Value default
						
		if (cl != null && cl.getOptionValue(Conf.STATIC_HOST) != null && !"".equals(cl.getOptionValue(Conf.STATIC_HOST))) 
			optionsStorm.put(Conf.STATIC_HOST, cl.getOptionValue(Conf.STATIC_HOST));            
		else 
			optionsStorm.put(Conf.STATIC_HOST, ""); 
					
		LOG.info("Options: " + optionsStorm);
		
		return optionsStorm;
	}
}