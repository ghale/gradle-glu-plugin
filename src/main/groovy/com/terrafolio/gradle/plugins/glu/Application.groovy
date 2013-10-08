package com.terrafolio.gradle.plugins.glu

import groovy.json.JsonBuilder

class Application {
	def name
	def mountPoint
	def script
	def tags
	def initParameters
	def metadata
	def entryState
	def parent
	
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
	
	def metadata(Map metadata) {
		this.metadata = metadata
	}
	
	def entryState(String entryState) {
		this.entryState = entryState
	}
	
	def parent(String parent) {
		this.parent = parent
	}
	
	def initParameters(Map initParameters) {
		this.initParameters = initParameters
	}
	
	def generate(Map options) {
		return [
					entries: options.agents.collect { agent, genTags ->
						def entryMap = 
							[
								'agent': agent,
							]
							
                        entryMap.script = options.containsKey('script') ? options.script : script
						entryMap.mountPoint = options.containsKey("mountPoint") ? options.mountPoint : mountPoint
						entryMap.tags = mergeTags([ genTags, options.tags, tags ])
						entryMap.initParameters = MapUtil.mergeMaps(initParameters, options.initParameters)
						entryMap.metadata = MapUtil.mergeMaps(metadata, options.metadata)
						
						def String _parent = options.containsKey("parent") ? options.parent : parent
						if (_parent != null) { entryMap.parent = _parent }
						
						def String _entryState = options.containsKey("entryState") ? options.entryState : entryState
						if (_entryState != null) { entryMap.entryState = _entryState }
						
						return entryMap	
					}	
				]
	}

    def generate(Closure jsonClosure) {
        def builder = new JsonBuilder()
        def map = builder jsonClosure
        return generate(map)
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
