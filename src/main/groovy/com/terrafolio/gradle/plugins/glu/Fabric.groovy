package com.terrafolio.gradle.plugins.glu

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class Fabric {
	def name
	def server
	def model
	
	Fabric(String name) {
		this.name = name
	}
	
	def server(GluServer server) {
		this.server = server
	}
	
	def model(Map jsonMap) {
		model = jsonMap
	}
	
	def model(File jsonFile) {
		def slurper = new JsonSlurper()
		def map = slurper.parseText(jsonFile.getText())
		model(map)
	}
	
	def model(Closure jsonClosure) {
		def builder = new JsonBuilder()
		def map = builder jsonClosure
		model(map)
	}
	
	def merge(Map jsonMap) {
		return mergeMaps(model, jsonMap)
	}
	
	def mergeMaps(Map map1, Map map2) {
		def newMap = [:]
		[ map1, map2 ].each { map ->
			map.each { key, value ->
				if (! newMap.containsKey(key)) {
					newMap[key] = value
				} else { 
					if (value instanceof List) {
							newMap[key] += value
					} else if (value instanceof Map) {
						newMap[key] = mergeMaps(newMap[key], value)
					} else {
						newMap[key] = value
					}
				}
			}
		}
		return newMap
	}
	
	def generate() {
		def builder = new JsonBuilder()
		builder merge([ 'fabric': name ])
		return builder.toString()
	}
}
