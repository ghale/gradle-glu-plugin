package com.terrafolio.gradle.plugins.glu

import org.apache.commons.chain.Chain
import org.apache.commons.chain.Command
import org.apache.commons.chain.Context
import org.apache.commons.chain.impl.ChainBase
import org.apache.commons.chain.impl.ContextBase
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GluExecutionTask extends GluAbstractTask {
	def order = 'parallel'
	def executionTime = 0
	def Chain executionChain = ExecutionChainFactory.getExecutionChain()
	def customDeploymentPollingAction = null
	def customDeploymentCompleteAction = null
	
	@Override
	def void doExecute() {
		def Context context = new ContextBase()
		context.put(Constants.SERVICE, getService())
		context.put(Constants.FABRIC, fabric.name)
		context.put(Constants.LOGGER, project.logger)
		context.put(Constants.CONSOLE_URL, fabric.server.url)
		
		if (customDeploymentPollingAction) {
			context.put(Constants.POLLING_ACTION, customDeploymentPollingAction)
		}
		
		if (customDeploymentCompleteAction) {
			context.put(Constants.COMPLETE_ACTION, customDeploymentCompleteAction)
		}
		
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
	
	def void start(Map map = [tags: []]) {
		def Command command = new ExecutionCommand([ planAction: 'start' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void deploy(Map map = [tags: []]) {
		def Command command = new ExecutionCommand([ planAction: 'deploy' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void stop(Map map = [tags: []]) {
		def Command command = new ExecutionCommand([ planAction: 'stop' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void redeploy(Map map = [tags: []]) {
		def Command command = new ExecutionCommand([ planAction: 'redeploy' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void bounce(Map map = [tags: []]) {
		def Command command = new ExecutionCommand([ planAction: 'bounce' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void undeploy(Map map = [tags: []]) {
		def Command command = new ExecutionCommand([ planAction: 'undeploy' ], map.tags, getOrder(map))
		executionChain.addCommand(command)
	}
	
	def void withDeploymentPollingAction(Closure closure) {
		customDeploymentPollingAction = closure
	}
	
	def void withDeploymentCompleteAction(Closure closure) {
		customDeploymentCompleteAction = closure
	}
}
