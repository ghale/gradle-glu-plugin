package com.terrafolio.gradle.plugins.glu

import org.apache.commons.chain.Chain
import org.apache.commons.chain.impl.ChainBase

class ExecutionChainFactory {
		def static Chain getExecutionChain() {
			return new ChainBase()
		}
}
