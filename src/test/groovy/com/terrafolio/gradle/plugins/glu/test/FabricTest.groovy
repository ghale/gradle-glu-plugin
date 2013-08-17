package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*;
import com.terrafolio.gradle.plugins.glu.Fabric
import groovy.json.JsonBuilder
import org.junit.Test;

class FabricTest {
	@Test
	def void model_appliesJsonMap() {
		def _fabric = new Fabric('test')
		def map = ['fabric': _fabric.name]
		_fabric.model(map)
		assert _fabric.model == map
	}
	
	@Test 
	def void model_appliesJsonFile() {
		def _fabric = new Fabric('test')
		def jsonFile = File.createTempFile('test', 'json')
		def map = ['fabric': _fabric.name]
		
		def builder = new JsonBuilder()
		builder map
		jsonFile << builder.toString()
		
		_fabric.model(jsonFile)
		assert _fabric.model == map
	}
	
	@Test
	def void model_appliesJsonClosure() {
		def _fabric = new Fabric('test')
		def map = ['fabric': _fabric.name]
		
		_fabric.model {
			fabric _fabric.name
		}
		
		assert _fabric.model == map
	}
	
	@Test
	def void merge_mergesJsonMap() {
		def _fabric = new Fabric('test')
		def map = [ 'fabric': _fabric.name, agentTags: [ 'test': [ 'tag1' ] ] ]
		_fabric.model(map)
		
		def builder = new JsonBuilder()
		def newMap = builder {
			'fabric' 'newFabricName'
			agentTags (
				'test': [ 'tag2', 'tag3' ]
			)
			entries (
					[
						[
							agent: 'test',
							mountPoint: '/tst2'
						]
					]
				)
		}
		
		assert _fabric.merge(newMap) == ['fabric': 'newFabricName', agentTags: [ 'test': [ 'tag1', 'tag2', 'tag3' ] ], entries: [ [ agent: 'test', mountPoint: '/tst2' ] ] ]
	}
	
	@Test 
	def void merge_mergesJsonClosure() {
		def _fabric = new Fabric('test')
		def map = [ 'fabric': _fabric.name, agentTags: [ 'test': [ 'tag1' ] ] ]
		_fabric.model(map)
		
		def closure = {
			'fabric' 'newFabricName'
			agentTags (
				'test': [ 'tag2', 'tag3' ]
			)
			entries (
					[
						[
							agent: 'test',
							mountPoint: '/tst2'
						]
					]
				)
		}
		
		assert _fabric.merge(closure) == ['fabric': 'newFabricName', agentTags: [ 'test': [ 'tag1', 'tag2', 'tag3' ] ], entries: [ [ agent: 'test', mountPoint: '/tst2' ] ] ]
	}
	
	@Test
	def void generate_generatesJson() {
		def _fabric = new Fabric('test')
		_fabric.model {
			agentTags (
				'test': [ 'tag2', 'tag3' ]
			)
			entries (
					[
						[
							agent: 'test',
							mountPoint: '/tst2'
						]
					]
				)
		}
		
		assert _fabric.generate() == '{"agentTags":{"test":["tag2","tag3"]},"entries":[{"agent":"test","mountPoint":"/tst2"}],"fabric":"test"}'
	}
}
