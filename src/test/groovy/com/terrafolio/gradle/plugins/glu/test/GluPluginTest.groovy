package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*;
import com.terrafolio.gradle.plugins.glu.GluConfigurationConvention
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
}
