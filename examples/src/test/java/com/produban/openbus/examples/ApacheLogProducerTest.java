package com.produban.openbus.examples;

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.produban.openbus.examples.ApacheLogProducerSample;

/**
 * Unit test for schema encoded avro message to kafka.
 */
public class ApacheLogProducerTest  extends TestCase  {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ApacheLogProducerTest( String testName )  {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()  {
        return new TestSuite( ApacheLogProducerTest.class );
    }

	
	/**
	 *  send 1000 messages, with 5 users, 10 sessions and 10 requests
	 * 
	 */
	public void testApacheLogProducer() {
		ApacheLogProducerSampleTemp aps = new ApacheLogProducerSampleTemp("/kafka-test.properties","testtopic1", 0);
		aps.apacheLogProducerHelper(100,5,10,10);		
        assertTrue( true );
	}
	
	/**
	 *  send 1 messages, with 1 users, 1 sessions and 1 requests
	 * 
	 */
	public void testApacheLogProducerMin() {
		ApacheLogProducerSampleTemp aps = new ApacheLogProducerSampleTemp("/kafka-test.properties","webserverlog",0);
		aps.apacheLogProducerHelper(1,1,1,1);		
        assertTrue( true );
	}
	
	/**
	 *  send 1 messages, with 1 users, 1 sessions and 1 requests
	 * 
	 */
	public void testApacheLogProducer2Users() {
		ApacheLogProducerSampleTemp aps = new ApacheLogProducerSampleTemp("/kafka-test.properties","webserverlog",0);
		aps.apacheLogProducerHelper(100,2,4,4);		
        assertTrue( true );
	}	
   
}
