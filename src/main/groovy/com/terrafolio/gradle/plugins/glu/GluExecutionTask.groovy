package com.terrafolio.gradle.plugins.glu

import org.apache.commons.chain.Chain
import org.apache.commons.chain.Command
import org.apache.commons.chain.Context
import org.apache.commons.chain.impl.ChainBase
import org.apache.commons.chain.impl.ContextBase
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GluExecutionTask extends DefaultTask {
	def fabric
	def order = 'parallel'
	def executionTime = 0
	def Chain executionChain = ExecutionChainFactory.getExecutionChain()
	
	@TaskAction
	def void executeDeployment() {
		def Context context = new ContextBase()
		context.put(Constants.SERVICE, new GluRESTServiceImpl(fabric.server.url, fabric.server.username, fabric.server.password))
		context.put(Constants.FABRIC, fabric.name)
		context.put(Constants.LOGGER, project.logger)
		
		if (executionTime > 0) {
			sleepUntil(executionTime)
		}
		
		if (executionChain.execute(context)) {
			throw new Exception("Execution Chain Failed!")
		}
	}
	
	def sleepUntil = { executionTime ->
		def long now = System.currentTimeMillis()
		if (executionTime <= now) {
			return
		} else {
			def sleepTime = executionTime - now
			this.sleep(sleepTime)
		}
	}
	
	def String getOrder(Map map) {
		return map.containsKey('order') ? map.order : order
	}
	
	def void start(Map map) {
		def Command command = new ExecutionCommand([ planAction: 'start' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void deploy(Map map) {
		def Command command = new ExecutionCommand([ planAction: 'deploy' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void stop(Map map) {
		def Command command = new ExecutionCommand([ planAction: 'stop' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void redeploy(Map map) {
		def Command command = new ExecutionCommand([ planAction: 'redeploy' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void bounce(Map map) {
		def Command command = new ExecutionCommand([ planAction: 'bounce' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void undeploy(Map map) {
		def Command command = new ExecutionCommand([ planAction: 'undeploy' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
}
