package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*;
import com.terrafolio.gradle.plugins.glu.GluConfiguration
import com.terrafolio.gradle.plugins.glu.GluConfigurationConvention
import com.terrafolio.gradle.plugins.glu.GluGenerateFabricsTask
import com.terrafolio.gradle.plugins.glu.GluPlugin
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project;
import org.junit.Test;
import org.junit.Before;
import org.gradle.testfixtures.ProjectBuilder

class GluPluginTest {
	def private final Project project = ProjectBuilder.builder().withProjectDir(new File('build/tmp/test')).build()
	def private final GluPlugin plugin = new GluPlugin()
	
	@Before
	def void setupProject() {
		plugin.apply(project)
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
		assert project.tasks.findByName('generateFabrics') instanceof GluGenerateFabricsTask
	}
}
