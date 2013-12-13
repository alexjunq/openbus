package openbus.kafka.avro;

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

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.message.Message;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.log4j.Logger;
 

/**
 * 
 * A simple Avro Kafka Producer.
 * Writes specified fields from avro schema to selected topic
 * Uses AvroSchemaSerializer for embedding schema with message
 */

public class AvroProducer {
	static final Logger logger = Logger.getLogger(AvroProducer.class);
	
	private Producer<String, byte[]> producer;
	private AvroSchemaSerializer as;	
	private String topic;
	
	/**
	 * Constructor
	 * @param brokerList kafka broker list 
	 * @param topic target topic
	 * @param avroResource file with the schema
	 * @param fields list of avro fields 
	 */
    public AvroProducer(String brokerList, String topic, String avroResource, String[] fields) {

    	
        Properties props = new Properties();
        props.put("metadata.broker.list", brokerList);
        
        this.topic=topic;

        as=new AvroSchemaSerializer(ClassLoader.class.getResourceAsStream(avroResource), fields );

        producer = new kafka.javaapi.producer.Producer<String, byte[]>(new ProducerConfig(props));
        	   
    }
    
    /**
     * Send a message 
     * @param payload by default an space-separated avro field list 	
     */
    public void send(String payload) {
    	Message message = new Message(as.toBytes(payload));
		producer.send(new KeyedMessage<String, byte[]>(topic, message.buffer().array()));	
    }

	/**
	 * closes producer
	 */
	public void close() {
		producer.close();
	}
              
    
}