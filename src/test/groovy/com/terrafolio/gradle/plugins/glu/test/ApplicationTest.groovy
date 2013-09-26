package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*;
import com.terrafolio.gradle.plugins.glu.Application
import com.terrafolio.gradle.plugins.glu.Fabric
import groovy.json.JsonBuilder
import org.junit.Test;

class ApplicationTest {
	@Test
	def void generate_generatesEntries() {
		def app = new Application('test')
		app.mountPoint = '/tst'
		app.script = 'somescript'
		app.tags = [ 'tag1', 'tag2' ]
		app.metadata = [ 'version': '1.2.1' ]
		
		assert app.generate(
								agents: [ 
											'atltstaxi01': [ 'step001' ],
											'atltstaxi02': [ 'step002' ]
									 	],
								tags: [ 'tst' ],
								initParameters: [ 'env': 'tst', 'package': 'mypackage' ]
							) == [
									entries: [
												[
													agent: 'atltstaxi01',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
													initParameters: [ 'env': 'tst', 'package': 'mypackage' ],
													metadata: [ 'version': '1.2.1' ]
												],
												[
													agent: 'atltstaxi02',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step002', 'tst', 'tag1', 'tag2' ],
													initParameters: [ 'env': 'tst', 'package': 'mypackage' ],
													metadata: [ 'version': '1.2.1' ]
												]
											 ]
									]
							
	}
	
	@Test
	def void generate_overridesMountpoint() {
		def app = new Application('test')
		app.mountPoint = '/tst'
		app.script = 'somescript'
		app.tags = [ 'tag1', 'tag2' ]
		
		assert app.generate(
								agents: [
											'atltstaxi01': [ 'step001' ]
										 ],
								tags: [ 'tst' ],
								mountPoint: '/tst2',
								initParameters: [ 'env': 'tst', 'package': 'mypackage' ]
							) == [
									entries: [
												[
													agent: 'atltstaxi01',
													mountPoint: '/tst2',
													script: 'somescript',
													tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
													initParameters: [ 'env': 'tst', 'package': 'mypackage' ],
													metadata: [:]
												]
											 ]
									]
							
	}
	
	@Test
	def void generate_addsToInitParameters() {
		def app = new Application('test')
		app.mountPoint = '/tst'
		app.script = 'somescript'
		app.tags = [ 'tag1', 'tag2' ]
		app.initParameters = [ 'option': 'default' ]
		
		assert app.generate(
								agents: [
											'atltstaxi01': [ 'step001' ]
										 ],
								tags: [ 'tst' ],
								initParameters: [ 'env': 'tst', 'package': 'mypackage' ]
							) == [
									entries: [
												[
													agent: 'atltstaxi01',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
													initParameters: [ 'option': 'default', 'env': 'tst', 'package': 'mypackage' ],
													metadata: [:]
												]
											 ]
									]
							
	}
	
	@Test
	def void generate_addsToMetadata() {
		def app = new Application('test')
		app.mountPoint = '/tst'
		app.script = 'somescript'
		app.tags = [ 'tag1', 'tag2' ]
		app.metadata = [ 'option': 'default', 'domain': 'dev' ]
		
		assert app.generate(
								agents: [
											'atltstaxi01': [ 'step001' ]
										 ],
								tags: [ 'tst' ],
								metadata: [ 'version': '1.2.1', 'domain': 'test' ]
							) == [
									entries: [
												[
													agent: 'atltstaxi01',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
													initParameters: [:],
													metadata: [ 'option': 'default', 'version': '1.2.1', 'domain': 'test' ]
												]
											 ]
									]
							
	}
	
	@Test
	def void generate_setsParent() {
		def app = new Application('test')
		app.mountPoint = '/tst'
		app.script = 'somescript'
		app.tags = [ 'tag1', 'tag2' ]
		app.parent = '/root'
		
		assert app.generate(
								agents: [
											'atltstaxi01': [ 'step001' ]
										 ],
								tags: [ 'tst' ]
							) == [
									entries: [
												[
													agent: 'atltstaxi01',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
													initParameters: [:],
													metadata: [:],
													parent: '/root'
												]
											 ]
									]
							
	}
	
	@Test
	def void generate_overridesParent() {
		def app = new Application('test')
		app.mountPoint = '/tst'
		app.script = 'somescript'
		app.tags = [ 'tag1', 'tag2' ]
		app.parent = '/root'
		
		assert app.generate(
								agents: [
											'atltstaxi01': [ 'step001' ]
										 ],
								tags: [ 'tst' ],
								parent: '/root2'
							) == [
									entries: [
												[
													agent: 'atltstaxi01',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
													initParameters: [:],
													metadata: [:],
													parent: '/root2'
												]
											 ]
									]
							
	}
	
	@Test
	def void generate_setEntryState() {
		def app = new Application('test')
		app.mountPoint = '/tst'
		app.script = 'somescript'
		app.tags = [ 'tag1', 'tag2' ]
		app.entryState = 'installed'
		
		assert app.generate(
								agents: [
											'atltstaxi01': [ 'step001' ]
										 ],
								tags: [ 'tst' ]
							) == [
									entries: [
												[
													agent: 'atltstaxi01',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
													initParameters: [:],
													metadata: [:],
													entryState: 'installed'
												]
											 ]
									]
							
	}
	
	@Test
	def void generate_overridesEntryState() {
		def app = new Application('test')
		app.mountPoint = '/tst'
		app.script = 'somescript'
		app.tags = [ 'tag1', 'tag2' ]
		app.entryState = 'installed'
		
		assert app.generate(
								agents: [
											'atltstaxi01': [ 'step001' ]
										 ],
								tags: [ 'tst' ],
								entryState: 'stopped'
							) == [
									entries: [
												[
													agent: 'atltstaxi01',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
													initParameters: [:],
													metadata: [:],
													entryState: 'stopped'
												]
											 ]
									]
							
	}

    @Test
    def void generate_appliesJsonClosure() {
        def app = new Application('test')
        app.mountPoint = '/tst'
        app.script = 'somescript'
        app.tags = ['tag1', 'tag2']

        assert app.generate {
                agents(atltstaxi01: 'step001')
                tags('tst')
        } == [
            entries: [
                [
                    agent: 'atltstaxi01',
                    mountPoint: '/tst',
                    script: 'somescript',
                    tags: [ 'step001', 'tst', 'tag1', 'tag2' ],
                    initParameters: [:],
                    metadata: [:],
                ]
            ]
        ]
    }
}
