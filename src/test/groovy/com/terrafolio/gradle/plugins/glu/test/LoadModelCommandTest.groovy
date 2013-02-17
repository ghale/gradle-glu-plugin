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
			loadModel() { String _fabricName, Map _fabric ->
				assert _fabricName == fabricName
				assert _fabric == fabric
			}
		}
		
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			def command = new LoadModelCommand(fabric)
			command.execute(context)
		}
	}
}
