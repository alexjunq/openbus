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

package com.produban.openbus.broker.producer;

import static org.junit.Assert.*;
import kafka.message.MessageAndMetadata;

import org.junit.BeforeClass;
import org.junit.Test;

import com.produban.openbus.broker.consumer.BasicConsumer;

public class BasicProducerTest {

	static BasicConsumer consumer;
	static KafkaLocal kafka;

	@BeforeClass
	public static void startKafka(){		
		try {
			kafka = new KafkaLocal();
			Thread.sleep(5000);
		} catch (Exception e){
			fail("Error instantiating local Kafka broker");
			System.out.println(e.getMessage());
		}
		consumer = new BasicConsumer("test", kafka.getKafkaProperties().getProperty("zookeeper.connect"), "test_consumer_group");
	}

	@Test
	public void sendMessageToNewTopic() {
		String topic = "test";
		BasicProducer producer = new BasicProducer("localhost:9092", true);
		//produce some messages
		producer.sendMessage(topic, "1", "first message");
		producer.sendMessage(topic, "2", "second message");

		MessageAndMetadata<byte[], byte[]> firstMessage = consumer.consumeOne();
		assertEquals("first message", firstMessage.message());
		
		MessageAndMetadata<byte[], byte[]> secondMessage = consumer.consumeOne();
		assertEquals("second message", secondMessage.message());

	}

}
