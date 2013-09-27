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
        def findFabric = { fabricName ->
            project.glu.fabrics.find { f -> 
                f.name == fabricName || f.name.capitalize() == fabricName 
            }
        }

        [ 'start', 'stop', 'deploy', 'redeploy', 'undeploy', 'bounce' ].each { action ->
            project.tasks.addRule "Pattern: ${action}<Fabric>", { taskName ->
                def fabricName = taskName - action

                if (taskName.startsWith(action)) {
                    project.task("${action}${fabricName.capitalize()}", type: GluExecutionTask) {
                        fabric { findFabric(fabricName) }
                        "${action}"()
                    }
                }
            }
        }
            
        project.tasks.addRule "Pattern: loadModel<Fabric>", { taskName ->
            if (taskName.startsWith('loadModel')) {
                def fabricName = taskName - 'loadModel'

                project.task("loadModel${fabricName.capitalize()}", type: GluLoadModelTask) {
                    fabric { findFabric(fabricName) }
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
