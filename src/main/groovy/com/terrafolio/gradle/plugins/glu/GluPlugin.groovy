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
		applyRules(project)
	}
	
	def void applyTasks(Project project) {
		project.task('generateModels', type: GluGenerateModelsTask.class)
	}
	
	def void applyRules(Project project) {
		project.glu.fabrics.addRule('AutoConfigure Tasks') { fabricName ->
			[ 'start', 'stop', 'deploy', 'redeploy', 'undeploy', 'bounce' ].each { action ->
				project.task("${action}${fabricName.capitalize()}", type: GluExecutionTask) {
					fabric { project.glu.fabrics.findByName(fabricName) }
					"${action}"()
				}
			}
			
			project.task("loadModel${fabricName.capitalize()}", type: GluLoadModelTask) {
				fabric { project.glu.fabrics.findByName(fabricName) }
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
