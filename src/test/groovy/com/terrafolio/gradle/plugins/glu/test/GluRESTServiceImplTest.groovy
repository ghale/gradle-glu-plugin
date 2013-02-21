package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*

import com.terrafolio.gradle.plugins.glu.GluRESTServiceImpl
import com.terrafolio.gradle.plugins.glu.GluServiceException
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.mock.interceptor.MockFor
import groovy.xml.StreamingMarkupBuilder
import net.sf.json.JSON
import net.sf.json.JSONSerializer
import net.sf.json.xml.XMLSerializer
import org.apache.http.HttpResponse
import org.apache.http.message.BasicHttpResponse
import org.apache.http.message.BasicStatusLine
import org.junit.Before;
import org.junit.Test

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import groovyx.net.http.ContentType
import groovyx.net.http.Method
import org.apache.http.HttpVersion
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.XMLUnit

class GluRESTServiceImplTest {
	def mockRESTClient
	def class DynamicObject {
		def storage = [:]
		
		def propertyMissing(String name) {
			println "Missing!"
			if (! storage.containsKey(name))  {
				storage[name] = new DynamicObject()
			}
			return storage[name]
		}
		
		def void propertyMissing(String name, value) { storage[name] = value }
	}
	
	@Before
	def void setupProject() {
		mockRESTClient = new MockFor(RESTClient.class)
	}
	
	@Test
	def void loadModel_callsServiceWithCorrectArgs() {
		def fabric = [fabric: 'test']
		
		mockRESTClient.demand.with {
			getParser(2) {
				return new Expando()
			}
			
			request() { method, type, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				configDelegate.headers = new Expando()
				 
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 204, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				
				assert configDelegate.body == fabric
				assert configDelegate.uri.path == 'model/static'
				assert configDelegate.requestContentType == ContentType.JSON
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			service.loadModel('test', fabric)
		}
	}
	
	@Test (expected = GluServiceException.class)
	def void loadModel_throwsExceptionOnFailure() {
		def fabric = [fabric: 'test']
		
		mockRESTClient.demand.with {
			getParser(2) {
				return new Expando()
			}
			
			request() { method, type, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				configDelegate.headers = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, "ERROR"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				
				throw new HttpResponseException(response) 
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			service.loadModel('test', fabric)
		}
	}
	
	@Test
	def void createPlan_callsServiceWithCorrectArgs() {
		def tags = [ 'tag1', 'tag2' ]
		def action = [ planAction: 'deploy' ]
		def order = 'parallel'
		def planId = "8283e25e-f68d-4bbd-8a71-5149f23466ec"
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 201, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, planId)
				response.setHeader("Location", "http://localhost:8080/console/rest/v1/glu-dev-1/plan/" + planId)
				
				assert configDelegate.uri.path == 'plans'
				assert configDelegate.requestContentType == ContentType.URLENC
				assert configDelegate.body == [ systemFilter: 'tags.hasAll(\'' + tags.join(';') + '\')', planAction: 'deploy', order: 'parallel' ]
				configDelegate.response.success.call(response, planId)
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			assert service.createPlan('test', tags, action, order) == planId
			
		}
	}
	
	@Test
	def void createPlan_getsNullOnNoPlan() {
		def tags = [ 'tag1', 'tag2' ]
		def action = [ planAction: 'deploy' ]
		def order = 'parallel'
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 204, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				
				assert configDelegate.uri.path == 'plans'
				assert configDelegate.requestContentType == ContentType.URLENC
				assert configDelegate.body == [ systemFilter: 'tags.hasAll(\'' + tags.join(';') + '\')', planAction: 'deploy', order: 'parallel' ]
				configDelegate.response.'204'.call(response, null)
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			assert service.createPlan('test', tags, action, order) == null
		}
	}
	
	@Test (expected = GluServiceException.class)
	def void createPlan_throwsExceptionOnFailure() {
		def tags = [ 'tag1', 'tag2' ]
		def action = [ planAction: 'deploy' ]
		def order = 'parallel'
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, "ERROR"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				
				throw new HttpResponseException(response)
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			service.createPlan('test', tags, action, order)
		}
	}
	
	@Test 
	def void executePlan_callsServiceWithCorrectArgs() {
		def planId = "8283e25e-f68d-4bbd-8a71-5149f23466ec"
		def executionId = "9"
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 201, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, "9")
				response.setHeader("Location", "http://localhost:8080/console/rest/v1/glu-dev-1/plan/${planId}/execution/${executionId}")
				
				assert configDelegate.uri.path == "plan/${planId}/execution"
				configDelegate.response.success.call(response, executionId)
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			assert service.executePlan('test', planId) == executionId
		}
	}
	
	@Test (expected = GluServiceException.class)
	def void executePlan_throwsExceptionOnFailure() {
		def planId = "8283e25e-f68d-4bbd-8a71-5149f23466ec"
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, "ERROR"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				
				throw new HttpResponseException(response)
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			service.executePlan('test', planId)
		}
	}
	
	@Test
	def void getDeploymentStatus_callsServiceWithCorrectArgs() {
		def executionId = "9"
		def status = "COMPLETE"
		def startTime = '1312038160946'
		def endTime = '1312038165459'
		def username = 'testuser'
		def description = 'Deploy - Fabric [glu-dev-1] - PARALLEL'
		def completedSteps = '5'
		def totalSteps = '10'
		
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 201, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				response.setHeader("X-glu-status", status)
				response.setHeader("X-glu-endTime", endTime)
				response.setHeader("X-glu-startTime", startTime)
				response.setHeader("X-glu-username", username)
				response.setHeader("X-glu-description", description)
				response.setHeader("X-glu-completedSteps", completedSteps)
				response.setHeader("X-glu-totalSteps", totalSteps)
				
				assert method == Method.HEAD
				assert configDelegate.uri.path == "deployment/current/${executionId}"
				configDelegate.response.success.call(response, null)
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			def deploymentStatus = service.getDeploymentStatus('test', executionId) 
			assert deploymentStatus.status == status
			assert deploymentStatus.endTime == endTime
			assert deploymentStatus.startTime == startTime
			assert deploymentStatus.username == username
			assert deploymentStatus.description == description
			assert deploymentStatus.completedSteps == completedSteps
			assert deploymentStatus.totalSteps == totalSteps
		}
	}
	
	@Test (expected = GluServiceException.class)
	def void getDeploymentStatus_throwsExceptionOnFailure() {
		def executionId = "9"
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 201, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				
				throw new HttpResponseException(response)
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			service.getDeploymentStatus('test', executionId) 
		}
	}
	
	@Test 
	def void getExecutionStatus_callsServiceWithCorrectArgs() {
		def planId = "8283e25e-f68d-4bbd-8a71-5149f23466ec"
		def executionId = "9"
		def executionDocument = """\
<?xml version="1.0"?>
<plan fabric="glu-dev-1" systemId="afb580a022b3f0e79e54bb6f888bf151ea3b16fc" id="10c8ce36-5e97-4a56-9785-d560c0eb5732" name="Deploy - Fabric [glu-dev-1] - PARALLEL">
  <parallel name="Deploy - Fabric [glu-dev-1] - PARALLEL" startTime="2013-02-12 16:51:45 -0500" endTime="2013-02-12 16:52:04 -0500" status="COMPLETED">
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
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 201, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, executionDocument)
				
				assert method == Method.GET
				assert configDelegate.uri.path == "plan/${planId}/execution/${executionId}"
				configDelegate.response.success.call(response, new XmlSlurper().parseText(executionDocument))
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			
			def outputBuilder = new StreamingMarkupBuilder()
			def result = outputBuilder.bind { mkp.yield service.getExecutionStatus('test', planId, executionId) }
			
			XMLUnit.setIgnoreWhitespace(true)
			def xmlDiff = new Diff(executionDocument, result.toString())
			assert xmlDiff.similar()
		}
	}
	
	
	@Test (expected = GluServiceException)
	def void getExecutionStatus_throwsExceptionOnFailure() {
		def planId = "8283e25e-f68d-4bbd-8a71-5149f23466ec"
		def executionId = "9"
		
		
		mockRESTClient.demand.with {
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 500, "ERROR"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, executionDocument)
				
				throw new HttpResponseException(response)			
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			
			service.getExecutionStatus('test', planId, executionId)
		}
	}
	
	@Test
	def void getFabric_callsServiceWithCorrectArgs() {
		def fabricName = "test"
		def fabric = '''\
{
  "color": "#005a87",
  "name": "test",
  "zkConnectString": "localhost:2181",
  "zkSessionTimeout": "30s"
}'''
		mockRESTClient.demand.with {
			getParser(2) {
				return new Expando()
			}
			
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, fabric)
				
				assert method == Method.GET
				assert configDelegate.uri.path == ""
				return fabric
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			assert service.getFabric(fabricName) != null
		}
		
	}
	
	@Test
	def void getFabric_returnsNullOnMissingFabric() {
		def fabricName = "test"
		
		mockRESTClient.demand.with {
			getParser(2) {
				return new Expando()
			}
			
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 404, "NOT Found"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				
				assert method == Method.GET
				assert configDelegate.uri.path == ""
				return configDelegate.response.'404'.call(response)
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			assert service.getFabric(fabricName) == null
		}
		
	}
	
	@Test 
	def void createFabric_callsServiceWithCorrectArgs() {
		def fabricName = "test"
		def zookeeper = "localhost:2181"
		def zookeeperTimeout = "30s"
		def color = "#005a87"
		
		
		mockRESTClient.demand.with {
			getParser(2) {
				return new Expando()
			}
			
			request() { method, closure ->
				def reqMethod = method.getRequestType().newInstance();
				def configDelegate = new Expando()
				configDelegate.uri = new Expando()
				configDelegate.response = new Expando()
				configDelegate.headers = new Expando()
				
				closure.setDelegate(configDelegate)
				closure.setResolveStrategy(Closure.DELEGATE_FIRST)
				closure.call(reqMethod)
				
				HttpResponse baseResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK"))
				HttpResponseDecorator response = new HttpResponseDecorator(baseResponse, null)
				
				assert method == Method.PUT
				assert configDelegate.uri.path == ""
				assert configDelegate.uri.query == [ zkConnectString: zookeeper, zkSessionTimeout: zookeeperTimeout, 'color': color ]
			}
		}
		
		mockRESTClient.ignore('getClient')
		mockRESTClient.use {
			def service = new GluRESTServiceImpl("http://test", "testuser", "testpass")
			service.createFabric(fabricName, zookeeper, zookeeperTimeout, color)
		}
	}
	
}
