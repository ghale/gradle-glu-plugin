package com.terrafolio.gradle.plugins.glu

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GluAbstractTask extends DefaultTask {
	def Fabric fabric
	
	def GluService getService() {
		return new GluRESTServiceImpl(fabric.server.url + "/rest/v1/${fabric.name}/", fabric.server.username, fabric.server.password)
	}
	
	def fabric(Fabric fabric) {
		this.fabric = fabric
	}
	
	@TaskAction
	def executeTask() {
		initialize()
		doExecute()
	}
	
	def initialize() {
		
	}
	
	def abstract doExecute()
}
