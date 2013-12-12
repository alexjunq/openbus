/*
 * Copyright 2013 Produban
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.produban.openbus.broker;

import static org.junit.Assert.*;

import java.util.Properties;

import kafka.message.MessageAndMetadata;

import org.junit.BeforeClass;
import org.junit.Test;

import com.produban.openbus.broker.BasicConsumer;

public class BasicProducerTest {

	static BasicConsumer consumer;
	static KafkaLocal kafka;

	@BeforeClass
	public static void startKafka(){
		Properties kafkaProperties = new Properties();
		Properties zkProperties = new Properties();
		
		try {
			//load properties
			kafkaProperties.load(Class.class.getResourceAsStream("/kafkalocal.properties"));
			zkProperties.load(Class.class.getResourceAsStream("/zklocal.properties"));
			
			//start kafka
			kafka = new KafkaLocal(kafkaProperties, zkProperties);
			Thread.sleep(5000);
		} catch (Exception e){
			e.printStackTrace(System.out);
			fail("Error running local Kafka broker");
			e.printStackTrace(System.out);
		}
		consumer = new BasicConsumer("test", kafkaProperties.getProperty("zookeeper.connect"), "test_consumer_group");
	}

	@Test
	public void sendMessageToNewTopic() {
		String topic = "test";
		BasicProducer producer = new BasicProducer("localhost:9092", true);
		//produce some messages
		producer.sendMessage(topic, "1", "first message");
		producer.sendMessage(topic, "2", "second message");

		MessageAndMetadata<byte[], byte[]> firstMessage = consumer.consumeOne();
		assertEquals("first message", new String(firstMessage.message()));
		
		MessageAndMetadata<byte[], byte[]> secondMessage = consumer.consumeOne();
		assertEquals("second message", new String(secondMessage.message()));

	}

}
