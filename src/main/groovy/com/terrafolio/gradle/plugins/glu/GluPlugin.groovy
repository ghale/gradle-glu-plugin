package com.terrafolio.gradle.plugins.glu

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin

class GluPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		project.plugins.apply(BasePlugin.class)
		
		applyConventions(project)
		applyTasks(project)
		project.afterEvaluate { autoconfigureTasks(project) }
	}
	
	def void applyTasks(Project project) {
		project.task('generateModels', type: GluGenerateModelsTask.class)
	}
	
	def void autoconfigureTasks(Project project) {
		project.glu.fabrics.each { _fabric ->
			[ 'start', 'stop', 'deploy', 'redeploy', 'undeploy', 'bounce' ].each { action ->
				project.task("${action}${_fabric.name.capitalize()}", type: GluExecutionTask) {
					fabric _fabric
					"${action}"()
				}
			}
		}
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
