package com.terrafolio.gradle.plugins.glu

import org.apache.commons.chain.Command
import org.apache.commons.chain.Context;

class LoadModelCommand implements Command {
	def fabric
	
	public LoadModelCommand(Map fabric) {
		this.fabric = fabric
	}

	@Override
	public boolean execute(Context context) throws Exception {
		def service = context.get(Constants.SERVICE)
		def fabricName = context.get(Constants.FABRIC)
		service.loadModel(fabricName, fabric)
		return false
	}

}
