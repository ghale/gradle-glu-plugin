package com.terrafolio.gradle.plugins.glu

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GluGenerateModelsTask extends DefaultTask {
	def prettyPrint = true

	@TaskAction
	def void doExecute() {
		project.glu.fabrics.each { fabric ->
			project.logger.warn("Generating model for ${fabric.name} into ${project.buildDir.name}/${fabric.name}_model.json")
			if (! project.buildDir.exists()) {
				project.buildDir.mkdir()
			}
			def File file = new File(project.buildDir, "${fabric.name}_fabric.json")
			file.write(fabric.generate(prettyPrint))
		}
	}
}
