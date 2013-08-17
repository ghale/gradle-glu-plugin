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
	
	def merge(Closure jsonClosure) {
		def builder = new JsonBuilder()
		def map = builder jsonClosure
		merge(map)
	}
	
	def merge(Map jsonMap) {
		return MapUtil.mergeMaps(model, jsonMap)
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
