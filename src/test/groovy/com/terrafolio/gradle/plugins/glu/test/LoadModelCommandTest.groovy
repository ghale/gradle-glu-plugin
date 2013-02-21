package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*
import groovy.mock.interceptor.MockFor

import org.apache.commons.chain.Context
import org.apache.commons.chain.impl.ContextBase
import org.junit.Before
import org.junit.Test

import com.terrafolio.gradle.plugins.glu.ExecutionCommand
import com.terrafolio.gradle.plugins.glu.GluRESTServiceImpl
import com.terrafolio.gradle.plugins.glu.LoadModelCommand
import com.terrafolio.gradle.plugins.glu.Constants
import com.terrafolio.gradle.plugins.glu.MissingFabricException
import org.gradle.api.logging.Logging

class LoadModelCommandTest {
	def MockRESTServiceImpl
	
	@Before
	def void setupProject() {
		MockRESTServiceImpl = new MockFor(GluRESTServiceImpl.class)
	}
	
	@Test
	def void executeLoadModel_callsServiceWithCorrectArgs() {
		def fabricName = 'test'
		def fabric = [ 'fabric': 'test' ]
		MockRESTServiceImpl.demand.with {
			getFabric() { String _fabricName ->
				return fabric
			}
			
			loadModel() { String _fabricName, Map _fabric ->
				assert _fabricName == fabricName
				assert _fabric == fabric
			}
		}
		
		MockRESTServiceImpl.ignore('getTargetServer')
		
		
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new LoadModelCommand(fabric)
			command.execute(context)
		}
	}
	
	@Test (expected = MissingFabricException.class)
	def void executeLoadModel_FailsOnMissingFabric() {
		def fabricName = 'test'
		def fabric = [ 'fabric': 'test' ]
		MockRESTServiceImpl.demand.with {
			getFabric() { String _fabricName ->
				return null
			}
			
			loadModel() { String _fabricName, Map _fabric ->
				assert _fabricName == fabricName
				assert _fabric == fabric
			}
		}
		
		MockRESTServiceImpl.ignore('getTargetServer')
		
		
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new LoadModelCommand(fabric)
			command.execute(context)
		}
	}
}
