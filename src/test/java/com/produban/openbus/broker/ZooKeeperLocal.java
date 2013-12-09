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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

public class ZooKeeperLocal {
	
	ZooKeeperServerMain zooKeeperServer;
	
	public ZooKeeperLocal() throws FileNotFoundException, IOException{
		Properties zookeeperProperties = new Properties();
		zookeeperProperties.load(getClass().getResourceAsStream("zklocal.properties"));
		
		QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
		try {
		    quorumConfiguration.parseProperties(zookeeperProperties);
		} catch(Exception e) {
		    throw new RuntimeException(e);
		}

		zooKeeperServer = new ZooKeeperServerMain();
		final ServerConfig configuration = new ServerConfig();
		configuration.readFrom(quorumConfiguration);
		
		
		new Thread() {
		    public void run() {
		        try {
		            zooKeeperServer.runFromConfig(configuration);
		        } catch (IOException e) {
		            System.out.println("ZooKeeper Failed");
		            e.printStackTrace(System.err);
		        }
		    }
		}.start();
	}
}


