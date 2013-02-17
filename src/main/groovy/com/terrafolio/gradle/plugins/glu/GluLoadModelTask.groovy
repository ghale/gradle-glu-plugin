package com.terrafolio.gradle.plugins.glu

import org.apache.commons.chain.Chain
import org.apache.commons.chain.Command
import org.apache.commons.chain.impl.ChainBase
import org.apache.commons.chain.impl.ContextBase
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GluLoadModelTask extends DefaultTask {
	def fabric
	def Chain executionChain = ExecutionChainFactory.getExecutionChain()
	
	@TaskAction
	def loadModel() {
		def context = new ContextBase()
		context.put(Constants.SERVICE, new GluRESTServiceImpl(fabric.server.url, fabric.server.username, fabric.server.password))
		context.put(Constants.FABRIC, fabric.name)
		context.put(Constants.LOGGER, project.logger)
		def Command command = new LoadModelCommand(fabric.model)
		executionChain.addCommand(command)
		executionChain.execute(context)
	}
	
	def fabric(Fabric fabric) {
		this.fabric = fabric
	}
}
