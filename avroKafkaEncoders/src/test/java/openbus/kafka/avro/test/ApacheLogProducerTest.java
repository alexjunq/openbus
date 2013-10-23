package openbus.kafka.avro.test;

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
import openbus.kafka.avro.ApacheLogProducerSample;

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
		ApacheLogProducerSample aps = new ApacheLogProducerSample("/kafka-test.properties");
		aps.apacheLogProducerHelper(1000,5,10,10);		
        assertTrue( true );
	}
	

   
}
