package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*;
import com.terrafolio.gradle.plugins.glu.CreateFabricCommand
import com.terrafolio.gradle.plugins.glu.Fabric
import com.terrafolio.gradle.plugins.glu.GluRESTServiceImpl
import com.terrafolio.gradle.plugins.glu.Constants
import groovy.mock.interceptor.MockFor
import org.gradle.api.logging.Logging

import org.apache.commons.chain.impl.ContextBase
import org.junit.Before
import org.junit.Test

class CreateFabricCommandTest {
	def MockRESTServiceImpl
	
	@Before
	def void setupProject() {
		MockRESTServiceImpl = new MockFor(GluRESTServiceImpl.class)
	}
	
	@Test
	def void createFabric_callsServiceWithCorrectArgs() {
		def fabricName = 'test'
		def zookeeper = "localhost:2181"
		def zookeeperTimeout = "30s"
		def color = "#005a87"
		
		MockRESTServiceImpl.demand.with {
			getFabric() { String _fabricName ->
				return null
			}
			
			createFabric() { String _fabricName, String _zookeeper, String _zookeeperTimeout, String _color ->
				assert _fabricName == fabricName
				assert _zookeeper == zookeeper
				assert _zookeeperTimeout == zookeeperTimeout
				assert _color == color
			}
		}
		
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new CreateFabricCommand(fabricName, zookeeper, zookeeperTimeout, color)
			command.execute(context)
		}
	}
}
