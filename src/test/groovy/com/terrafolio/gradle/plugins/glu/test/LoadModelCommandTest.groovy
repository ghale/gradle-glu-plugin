package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*
import groovy.json.JsonBuilder
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
		def modelMap = [fabric: 'test']
		def builder = new JsonBuilder()
		builder modelMap
		def model = builder.toString()
		MockRESTServiceImpl.demand.with {
			getFabric() { String _fabricName ->
				return model
			}
			
			loadModel() { String _fabricName, String _model ->
				assert _fabricName == fabricName
				assert _model == model
			}
		}
		
		MockRESTServiceImpl.ignore('getTargetServer')
		
		
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new LoadModelCommand(model)
			command.execute(context)
		}
	}
	
	@Test (expected = MissingFabricException.class)
	def void executeLoadModel_FailsOnMissingFabric() {
		def fabricName = 'test'
		def modelMap = [fabric: 'test']
		def builder = new JsonBuilder()
		builder modelMap
		def model = builder.toString()
		
		MockRESTServiceImpl.demand.with {
			getFabric() { String _fabricName ->
				return null
			}
			
			loadModel() { String _fabricName, Map _model ->
				assert _fabricName == fabricName
				assert _model == model
			}
		}
		
		MockRESTServiceImpl.ignore('getTargetServer')
		
		
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new LoadModelCommand(model)
			command.execute(context)
		}
	}
}
