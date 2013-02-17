package com.terrafolio.gradle.plugins.glu

import org.gradle.util.ConfigureUtil

class GluConfigurationConvention {
	def glu
	
	GluConfigurationConvention(GluConfiguration glu) {
		this.glu = glu
	}
	
	def glu(Closure closure) {
		ConfigureUtil.configure(closure, glu)
	}
}
