package com.terrafolio.gradle.plugins.glu

import org.apache.commons.chain.Chain
import org.apache.commons.chain.Command
import org.apache.commons.chain.impl.ChainBase
import org.apache.commons.chain.impl.ContextBase
import org.gradle.api.DefaultTask


class GluLoadModelTask extends GluAbstractTask {
	def Chain executionChain = ExecutionChainFactory.getExecutionChain()
	
	@Override
	def doExecute() {
		def context = new ContextBase()
		context.put(Constants.SERVICE, getService())
		context.put(Constants.FABRIC, fabric.name)
		context.put(Constants.LOGGER, project.logger)
		context.put(Constants.CONSOLE_URL, fabric.server.url)
		
		def Command command = new LoadModelCommand(fabric.model)
		executionChain.addCommand(command)
		executionChain.execute(context)
	}
}
