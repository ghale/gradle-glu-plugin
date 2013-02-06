package com.terrafolio.gradle.plugins.glu

import org.gradle.api.Plugin
import org.gradle.api.Project

class GluPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		applyConventions(project)
		applyTasks(project)
	}
	
	def void applyTasks(Project project) {
		
	}
	
	def void applyConventions(Project project) {
		project.convention.plugins.glu = new GluConfigurationConvention()
	}

}
