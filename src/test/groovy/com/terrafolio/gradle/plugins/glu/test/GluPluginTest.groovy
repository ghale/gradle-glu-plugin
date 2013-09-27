package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*;
import com.terrafolio.gradle.plugins.glu.GluConfiguration
import com.terrafolio.gradle.plugins.glu.GluConfigurationConvention
import com.terrafolio.gradle.plugins.glu.GluGenerateModelsTask
import com.terrafolio.gradle.plugins.glu.GluPlugin
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project;
import org.junit.Test;
import org.junit.Before;
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.plugins.BasePlugin

class GluPluginTest {
	def private final Project project = ProjectBuilder.builder().withProjectDir(new File('build/tmp/test')).build()
	def private final GluPlugin plugin = new GluPlugin()
	
	@Before
	def void setupProject() {
		plugin.apply(project)
	}
	
	@Test
	def void apply_appliesBasePlugin() {
		assert project.plugins.findPlugin(BasePlugin.class) != null
	}
	
	@Test
	def void apply_appliesGluConfigurationConvention() {
		assert project.convention.plugins.glu instanceof GluConfigurationConvention
	}
	
	@Test void apply_appliesGluConfiguration() {
		assert project.hasProperty('glu')
		assert project.glu instanceof GluConfiguration
		assert project.glu.fabrics instanceof NamedDomainObjectCollection<Fabric>
		assert project.glu.servers instanceof NamedDomainObjectCollection<Server>
		assert project.glu.applications instanceof NamedDomainObjectCollection<Application>
	}
	
	@Test
	def void apply_appliesTasks() {
		assert project.tasks.generateModels instanceof GluGenerateModelsTask
	}
	
	@Test
	def void apply_autoconfiguresTasks() {
		project.glu {
			fabrics {
				test1 { }
				test2 { }
			}
		}
		
		[ 'start', 'stop', 'deploy', 'redeploy', 'undeploy', 'bounce', 'loadModel' ].each { action ->
			[ 'test1', 'test2'].each { fabric ->
				assert project.tasks.findByName("${action}${fabric.capitalize()}")
			}
		}
	}

    @Test
    def void apply_doesntCreateExtraTasks() {
		project.glu {
            servers {
                test {}
            }

			fabrics {
				test1 { 
                    server = servers.test
                }
			}
		}
		
		[ 'start', 'stop', 'deploy', 'redeploy', 'undeploy', 'bounce', 'loadModel' ].each { action ->
            def extraTasks = project.tasks.find { task ->
                task.name.startsWith(action) && !task.name.endsWith('Test1')
            }
            assert !extraTasks
		}
	}
}
