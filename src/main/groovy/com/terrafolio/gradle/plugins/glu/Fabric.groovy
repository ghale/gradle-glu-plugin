package com.terrafolio.gradle.plugins.glu

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class Fabric {
	def name
	def zookeeper
	def zookeeperTimeout = "30s"
	def color = "#005a87"
	def server
	def model
	
	Fabric(String name) {
		this.name = name
	}
	
	def server(GluServer server) {
		this.server = server
	}
	
	def zookeeper(String zookeeper) {
		this.zookeeper = zookeeper
	}
	
	def zookeeperTimeout(String zookeeperTimeout) {
		this.zookeeperTimeout = zookeeperTimeout
	}
	
	def color(String color) {
		this.color = color
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
		return generate(false)
	}
	
	def generate(boolean prettyPrint) {
		def builder = new JsonBuilder()
		model['fabric'] = name
		builder model
		return prettyPrint ? builder.toPrettyString() : builder.toString()
	}
}
