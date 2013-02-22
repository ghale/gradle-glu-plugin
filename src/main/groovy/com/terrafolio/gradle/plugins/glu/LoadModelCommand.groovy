package com.terrafolio.gradle.plugins.glu

import org.apache.commons.chain.Command
import org.apache.commons.chain.Context;

class LoadModelCommand implements Command {
	def model
	
	public LoadModelCommand(String model) {
		this.model = model
	}

	@Override
	public boolean execute(Context context) throws Exception {
		def service = context.get(Constants.SERVICE)
		def fabricName = context.get(Constants.FABRIC)
		def logger = context.get(Constants.LOGGER)
		
		if (service.getFabric(fabricName) == null) {
			throw new MissingFabricException("Fabric ${fabricName} does not exist!")
		}
		
		logger.info("Deploying model to the ${fabricName} fabric on ${service.targetServer}")
		service.loadModel(fabricName, model)
		return Constants.SUCCESS
	}

}
