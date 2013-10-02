package com.terrafolio.gradle.plugins.glu

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class GluAbstractTask extends DefaultTask {
	def fabric
	
	def GluService getService() {
        def fabric = getFabric()
		return new GluRESTServiceImpl(fabric.server.url + "/rest/v1/${fabric.name}/", fabric.server.username, fabric.server.password)
	}
	
	def fabric(fabric) {
		this.fabric = fabric
	}
	
	def Fabric getFabric() {
		if (fabric instanceof Closure) {
			return fabric.call()
		} else { 
			return fabric
		}
	}
	
	@TaskAction
	def void executeTask() {
		initialize()
		doExecute()
	}
	
	def initialize() {
		
	}
	
	def abstract void doExecute()
}
