package com.terrafolio.gradle.plugins.glu

import java.util.List;
import java.util.Map;
import net.sf.json.xml.XMLSerializer

import groovyx.net.http.RESTClient
import groovyx.net.http.ContentType
import groovyx.net.http.Method

class GluRESTServiceImpl implements GluService {
	def RESTClient client
	def url
	def username
	def password
	
	def GluRESTServiceImpl(String url, String username, String password) {
		this.url = url
		this.username = username
		this.password = password
	}
	
	def getRestClient() {
		if (client == null) {
			client = new RESTClient(url)
			if (username != null) {
				client.client.addRequestInterceptor(new PreemptiveAuthInterceptor(username, password))
			}
		}
		
		return client
	}
	
	@Override
	public String getTargetServer() {
		return url
	}
	
	@Override 
	public Object getFabric(String fabricName) throws GluServiceException {
		try {
			def client = getRestClient()
			client.parser.'text/json' = client.parser.'application/json'
			return client.request(Method.GET) { request ->
				uri.path = ""
				
				response.'404' = { resp ->
					null
				}
			}
		} catch (Exception e) {
			throw new GluServiceException("Glu Service Call Failed", e)
		}
	}
	
	@Override
	public void createFabric(String fabricName, String zookeeper, String zookeeperTimeout, String color) throws GluServiceException {
		try {
			def client = getRestClient()
			client.parser.'text/html' = client.parser.'text/plain'
			client.request(Method.PUT) { request ->
				uri.path = ""
				uri.query = [ 'zkConnectString': zookeeper, 'zkSessionTimeout': zookeeperTimeout, 'color': color ]
			}
		} catch (Exception e) {
			throw new GluServiceException("Glu Service Call Failed", e)
		}
	}

	@Override
	public void loadModel(String fabricName, String model) throws GluServiceException {
		try {
			def client = getRestClient()
			client.parser.'text/html' = client.parser.'text/plain'
			client.request(Method.POST, ContentType.TEXT) { request ->
				uri.path = 'model/static'
				requestContentType = ContentType.JSON
				body = model
			}
		} catch (Exception e) {
			throw new GluServiceException("Glu Service Call Failed", e)
		}
	}

	@Override
	public String createPlan(String fabricName, List tags, Map action, String order)
			throws GluServiceException {
				
		def planId = null
		try {
			getRestClient().request(Method.POST) { request ->
				uri.path = 'plans'
				requestContentType = ContentType.URLENC
				
				body = [ systemFilter: 'tags.hasAll(\'' + tags.join(';') + '\')', order: order ] + action
				
				response.success = { resp, body ->
					planId = body
				}
				
				response.'204' = { resp, body ->
					planId = null
				}
			}
		} catch (Exception e) {
			throw new GluServiceException("Glu Service Call Failed", e)
		}
		return planId
	}

	@Override
	public String executePlan(String fabricName, String planId)
			throws GluServiceException {
		def executionId = null
		try {
			getRestClient().request(Method.POST) { request ->
				uri.path = "plan/${planId}/execution"
				response.success = { resp, body ->
					executionId = body
				}
			}
		} catch (Exception e) {
			throw new GluServiceException("Glu Service Call Failed", e)
		}
		return executionId;
	}

	@Override
	public DeploymentStatus getDeploymentStatus(String fabricName, String executionId) throws GluServiceException {
		def status = null
		try {
			getRestClient().request(Method.HEAD) { request ->
				uri.path = "deployment/current/${executionId}"
				response.success = { resp, body ->
					status = new DeploymentStatus(resp.headers.inject([:]) {map, header -> map << [ (header.name): header.value ] })
				}
			}
		} catch (Exception e) {
			throw new GluServiceException("Glu Service Call Failed", e)
		}
		return status
	}

	@Override
	public Object getExecutionStatus(String fabricName, String planId,
			String executionId) throws GluServiceException {
		def status = null
		try {
			getRestClient().request(Method.GET) { request ->
				uri.path = "plan/${planId}/execution/${executionId}"
				response.success = { resp, body ->
					status = body
				}
			}
		} catch (Exception e) {
			throw new GluServiceException("Glu Service Call Failed", e)
		}
		return status
	}
	
	
	
	
}
