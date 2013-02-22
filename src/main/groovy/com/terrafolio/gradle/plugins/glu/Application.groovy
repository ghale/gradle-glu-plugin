package com.terrafolio.gradle.plugins.glu

class Application {
	def name
	def mountPoint
	def script
	def tags
	
	Application(String name) {
		this.name = name
	}
	
	def mountPoint(String mountPoint) {
		this.mountPoint = mountPoint
	}
	
	def script(String script) {
		this.script = script
	}
	
	def tags(List tags) {
		this.tags = tags
	}
	
	def generate(Map options) {
		return [
					entries: options.agents.collect { agent, genTags ->
						[
							'agent': agent,
							'mountPoint': mountPoint,
							'script': script,
							'tags': mergeTags([ genTags, options.tags, tags ]),
							'initParameters': options.initParameters
							]
					}	
				]
				
	}
	
	def mergeTags(List tagsList) {
		def allTags = []
		tagsList.each { tagList ->
			if (tagList != null) {
				allTags += tagList
			}
		}
		return allTags
	}
}
