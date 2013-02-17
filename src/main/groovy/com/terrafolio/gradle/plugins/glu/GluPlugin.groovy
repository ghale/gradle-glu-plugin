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
		project.task('generateFabrics', type: GluGenerateFabricsTask)
	}
	
	def void applyConventions(Project project) {
		def fabrics = project.container(Fabric) { name ->
			new Fabric(name)
		}
		
		def servers = project.container(GluServer) { name ->
			new GluServer(name)
		}
		
		def applications = project.container(Application) { name ->
			new Application(name)
		}
		
		def glu = new GluConfiguration(fabrics, servers, applications)
		
		project.convention.plugins.glu = new GluConfigurationConvention(glu)
	}

}
