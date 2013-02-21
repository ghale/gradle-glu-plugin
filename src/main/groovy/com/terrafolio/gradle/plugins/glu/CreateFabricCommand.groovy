package com.terrafolio.gradle.plugins.glu

import org.apache.commons.chain.Command
import org.apache.commons.chain.Context;

class CreateFabricCommand implements Command {
	def fabricName
	def zookeeper
	def zookeeperTimeout
	def color

	public CreateFabricCommand(String fabricName, String zookeeper, String zookeeperTimeout, String color) {
		this.fabricName = fabricName
		this.zookeeper = zookeeper
		this.zookeeperTimeout = zookeeperTimeout
		this.color = color
	}

	@Override
	public boolean execute(Context context) throws Exception {
		def service = context.get(Constants.SERVICE)
		def fabricName = context.get(Constants.FABRIC)
		def logger = context.get(Constants.LOGGER)
		
		if (service.getFabric(fabricName) == null) {
			logger.warn("Creating new fabric with name: ${fabricName}")
			service.createFabric(fabricName, zookeeper, zookeeperTimeout, color)
		}
		
		return Constants.SUCCESS
	}

}
