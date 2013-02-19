package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*
import groovy.mock.interceptor.MockFor
import groovy.xml.StreamingMarkupBuilder

import org.apache.commons.chain.impl.ContextBase
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit
import org.gradle.api.logging.Logging
import org.gradle.api.logging.Logging.LoggerImpl
import org.junit.Before
import org.junit.Test

import com.terrafolio.gradle.plugins.glu.Constants
import com.terrafolio.gradle.plugins.glu.DeploymentStatus
import com.terrafolio.gradle.plugins.glu.ExecutionCommand
import com.terrafolio.gradle.plugins.glu.GluRESTServiceImpl

class ExecutionCommandTest {
	def MockRESTServiceImpl
	
	@Before
	def void setupProject() {
		MockRESTServiceImpl = new MockFor(GluRESTServiceImpl.class)
	}
	
	@Test
	def void execute_callsServiceWithCorrectArgs() {
		def fabricName = 'test'
		def tags = [ 'tag1', 'tag2' ]
		def order = 'parallel'
		def planId = '8283e25e-f68d-4bbd-8a71-5149f23466ec'
		def executionId = '9'
		def executionDocument = """\
<?xml version="1.0"?>
<plan fabric="glu-dev-1" systemId="afb580a022b3f0e79e54bb6f888bf151ea3b16fc" id="10c8ce36-5e97-4a56-9785-d560c0eb5732" name="Deploy - Fabric [glu-dev-1] - PARALLEL">
  <parallel startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED">
    <sequential agent="agent-1" mountPoint="/sample/i001" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i001" name="Run [start] phase for [/sample/i001] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED" />
    </sequential>
    <sequential agent="agent-1" mountPoint="/sample/i002" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i002" name="Run [start] phase for [/sample/i002] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED" />
    </sequential>
    <sequential agent="agent-1" mountPoint="/sample/i003" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i003" name="Run [start] phase for [/sample/i003] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED" />
    </sequential>
  </parallel>
</plan>
"""
		
		MockRESTServiceImpl.demand.with {
			createPlan() { String _fabricName, List _tags, Map _action, String _order ->
				assert _fabricName == fabricName
				assert _tags == tags
				assert _action == [ planAction: 'deploy' ]
				assert _order == order
				return planId
			}
			
			executePlan() { String _fabricName, String _planId ->
				assert _fabricName == fabricName
				assert _planId == planId
				return executionId
			}
			
			getDeploymentStatus() { String _fabricName, String _executionId ->
				assert _fabricName == fabricName
				assert _executionId == executionId
				
				return new DeploymentStatus([
												'X-glu-status': 'RUNNING',
												'X-glu-startTime': '1312038160946',
												'X-glu-completedSteps': '5',
												'X-glu-totalSteps': '10',
												'X-glu-username': 'testuser',
												'X-glu-description': 'Deploy - Fabric [glu-dev-1] - PARALLEL'
											])
			}
			
			getDeploymentStatus() { String _fabricName, String _executionId ->
				assert _fabricName == fabricName
				assert _executionId == executionId
				
				return new DeploymentStatus([
												'X-glu-status': 'COMPLETED',
												'X-glu-startTime': '1312038160946',
												'X-glu-endTime': '1312038165459',
												'X-glu-completedSteps': '10',
												'X-glu-totalSteps': '10',
												'X-glu-username': 'testuser',
												'X-glu-description': 'Deploy - Fabric [glu-dev-1] - PARALLEL'
											])
			}
			
			getExecutionStatus() { String _fabricName, String _planId, String _executionId ->
				assert _fabricName == fabricName
				assert _planId == planId
				assert _executionId == executionId
				
				return new XmlSlurper().parseText(executionDocument)
			}
		}
		
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new ExecutionCommand([ planAction: 'deploy' ], tags, order)
			command.pollInterval = 10
			assert ! command.execute(context)
			
			def outputBuilder = new StreamingMarkupBuilder()
			def result = outputBuilder.bind { mkp.yield context.get(Constants.STATUS) }		
			XMLUnit.setIgnoreWhitespace(true)
			def xmlDiff = new Diff(executionDocument, result.toString())
			assert xmlDiff.similar()
		}
	}
	
	@Test
	def void execute_logsMessages() {
		def fabricName = 'test'
		def tags = [ 'tag1', 'tag2' ]
		def order = 'parallel'
		def planId = '8283e25e-f68d-4bbd-8a71-5149f23466ec'
		def executionId = '9'
		def executionDocument = """\
<?xml version="1.0"?>
<plan fabric="glu-dev-1" systemId="afb580a022b3f0e79e54bb6f888bf151ea3b16fc" id="10c8ce36-5e97-4a56-9785-d560c0eb5732" name="Deploy - Fabric [glu-dev-1] - PARALLEL">
  <parallel startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED">
    <sequential agent="agent-1" mountPoint="/sample/i001" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i001" name="Run [start] phase for [/sample/i001] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED" />
    </sequential>
    <sequential agent="agent-1" mountPoint="/sample/i002" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i002" name="Run [start] phase for [/sample/i002] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED" />
    </sequential>
    <sequential agent="agent-1" mountPoint="/sample/i003" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i003" name="Run [start] phase for [/sample/i003] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED" />
    </sequential>
  </parallel>
</plan>
"""
		
		MockRESTServiceImpl.demand.with {
			createPlan() { String _fabricName, List _tags, Map _action, String _order ->
				assert _fabricName == fabricName
				assert _tags == tags
				assert _action == [ planAction: 'deploy' ]
				assert _order == order
				return planId
			}
			
			executePlan() { String _fabricName, String _planId ->
				assert _fabricName == fabricName
				assert _planId == planId
				return executionId
			}
			
			getDeploymentStatus() { String _fabricName, String _executionId ->
				assert _fabricName == fabricName
				assert _executionId == executionId
				
				return new DeploymentStatus([
												'X-glu-status': 'RUNNING',
												'X-glu-startTime': '1312038160946',
												'X-glu-completedSteps': '5',
												'X-glu-totalSteps': '10',
												'X-glu-username': 'testuser',
												'X-glu-description': 'Deploy - Fabric [glu-dev-1] - PARALLEL'
											])
			}
			
			getDeploymentStatus() { String _fabricName, String _executionId ->
				assert _fabricName == fabricName
				assert _executionId == executionId
				
				return new DeploymentStatus([
												'X-glu-status': 'COMPLETED',
												'X-glu-startTime': '1312038160946',
												'X-glu-endTime': '1312038165459',
												'X-glu-completedSteps': '10',
												'X-glu-totalSteps': '10',
												'X-glu-username': 'testuser',
												'X-glu-description': 'Deploy - Fabric [glu-dev-1] - PARALLEL'
											])
			}
			
			getExecutionStatus() { String _fabricName, String _planId, String _executionId ->
				assert _fabricName == fabricName
				assert _planId == planId
				assert _executionId == executionId
				
				return new XmlSlurper().parseText(executionDocument)
			}
		}
		
		def mockLogger = new MockFor(LoggerImpl.class)
		mockLogger.demand.with {
			warn() { String message ->
				assert message == 'Creating plan for test with tags=[tag1, tag2], action=[planAction:deploy], and order=parallel'
			}
			
			warn() { String message ->
				assert message == 'Executing plan 8283e25e-f68d-4bbd-8a71-5149f23466ec in test'
			}
			
			warn() { String message ->
				assert message == '5 of 10 steps completed...'
			}
			
			warn() { String message ->
				assert message == '10 of 10 steps completed...'
			}
			
			warn() { String message ->
				assert message == """\
Deploy - Fabric [glu-dev-1] - PARALLEL: COMPLETED in 19s
/sample/i001: COMPLETED in 19s
    Run [start] phase for [/sample/i001] on [agent-1]: COMPLETED in 19s
/sample/i002: COMPLETED in 15s
    Run [start] phase for [/sample/i002] on [agent-1]: COMPLETED in 15s
/sample/i003: COMPLETED in 15s
    Run [start] phase for [/sample/i003] on [agent-1]: COMPLETED in 15s
"""
			}
		}
		
		mockLogger.ignore('info')
		mockLogger.ignore('debug')
		
		mockLogger.use {
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new ExecutionCommand([ planAction: 'deploy' ], tags, order)
			command.pollInterval = 10
			assert ! command.execute(context)
			
			def outputBuilder = new StreamingMarkupBuilder()
			def result = outputBuilder.bind { mkp.yield context.get(Constants.STATUS) }
			XMLUnit.setIgnoreWhitespace(true)
			def xmlDiff = new Diff(executionDocument, result.toString())
			assert xmlDiff.similar()
		}
		}
	}
	
	@Test
	def void executeDeploy_handlesFailedDeployment() {
		def fabricName = 'test'
		def tags = [ 'tag1', 'tag2' ]
		def order = 'parallel'
		def planId = '8283e25e-f68d-4bbd-8a71-5149f23466ec'
		def executionId = '9'
		def executionDocument = """\
<?xml version="1.0"?>
<plan fabric="glu-dev-1" systemId="afb580a022b3f0e79e54bb6f888bf151ea3b16fc" id="10c8ce36-5e97-4a56-9785-d560c0eb5732" name="Deploy - Fabric [glu-dev-1] - PARALLEL">
  <parallel startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="FAILED">
    <sequential agent="agent-1" mountPoint="/sample/i001" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i001" name="Run [start] phase for [/sample/i001] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED" />
    </sequential>
    <sequential agent="agent-1" mountPoint="/sample/i002" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="FAILED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i002" name="Run [start] phase for [/sample/i002] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="FAILED" />
    </sequential>
    <sequential agent="agent-1" mountPoint="/sample/i003" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED">
      <leaf agent="agent-1" fabric="glu-dev-1" mountPoint="/sample/i003" name="Run [start] phase for [/sample/i003] on [agent-1]" scriptAction="start" toState="running" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:00 -0500" status="COMPLETED" />
    </sequential>
  </parallel>
</plan>
"""
		
		MockRESTServiceImpl.demand.with {
			createPlan() { String _fabricName, List _tags, Map _action, String _order ->
				assert _fabricName == fabricName
				assert _tags == tags
				assert _action == [ planAction: 'deploy' ]
				assert _order == order
				return planId
			}
			
			executePlan() { String _fabricName, String _planId ->
				assert _fabricName == fabricName
				assert _planId == planId
				return executionId
			}
			
			getDeploymentStatus() { String _fabricName, String _executionId ->
				assert _fabricName == fabricName
				assert _executionId == executionId
				
				return new DeploymentStatus([
												'X-glu-status': 'RUNNING',
												'X-glu-startTime': '1312038160946',
												'X-glu-completedSteps': '5',
												'X-glu-totalSteps': '10',
												'X-glu-username': 'testuser',
												'X-glu-description': 'Deploy - Fabric [glu-dev-1] - PARALLEL'
											])
			}
			
			getDeploymentStatus() { String _fabricName, String _executionId ->
				assert _fabricName == fabricName
				assert _executionId == executionId
				
				return new DeploymentStatus([
												'X-glu-status': 'FAILED',
												'X-glu-startTime': '1312038160946',
												'X-glu-endTime': '1312038165459',
												'X-glu-completedSteps': '10',
												'X-glu-totalSteps': '10',
												'X-glu-username': 'testuser',
												'X-glu-description': 'Deploy - Fabric [glu-dev-1] - PARALLEL'
											])
			}
			
			getExecutionStatus() { String _fabricName, String _planId, String _executionId ->
				assert _fabricName == fabricName
				assert _planId == planId
				assert _executionId == executionId
				
				return new XmlSlurper().parseText(executionDocument)
			}
		}
		
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new ExecutionCommand([ planAction: 'deploy' ], tags, order)
			command.pollInterval = 10
			assert command.execute(context)
			
			def outputBuilder = new StreamingMarkupBuilder()
			def result = outputBuilder.bind { mkp.yield context.get(Constants.STATUS) }
			XMLUnit.setIgnoreWhitespace(true)
			def xmlDiff = new Diff(executionDocument, result.toString())
			assert xmlDiff.similar()
		}
	}
	
	@Test
	def void deploy_handlesDeploymentWithNoChanges() {
		def fabricName = 'test'
		def tags = [ 'tag1', 'tag2' ]
		def order = 'parallel'
		
		
		MockRESTServiceImpl.demand.with {
			createPlan() { String _fabricName, List _tags, Map _action, String _order ->
				assert _fabricName == fabricName
				assert _tags == tags
				assert _action == [ planAction: 'deploy' ]
				assert _order == order
				return null
			}
		}
		
		def mockLogger = new MockFor(LoggerImpl.class)
		mockLogger.demand.with {
			warn() { String message ->
				assert message == 'Creating plan for test with tags=[tag1, tag2], action=[planAction:deploy], and order=parallel'
			}
			
			warn() { String message ->
				assert message == 'Either there are no changes to deploy or the filters did not match any servers'
			}
		}
		
		mockLogger.ignore('info')
		mockLogger.ignore('debug')
		
		mockLogger.use {
		MockRESTServiceImpl.use {
			def context = new ContextBase()
			context.put(Constants.SERVICE, new GluRESTServiceImpl('http://glu', 'testuser', 'testpass'))
			context.put(Constants.FABRIC, fabricName)
			context.put(Constants.LOGGER, Logging.getLogger(this.class))
			def command = new ExecutionCommand([ planAction: 'deploy' ], tags, order)
			command.pollInterval = 10
			assert ! command.execute(context)
		}
		}
	}
}
