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
													initParameters: [ 'env': 'tst', 'package': 'mypackage' ]
												],
												[
													agent: 'atltstaxi02',
													mountPoint: '/tst',
													script: 'somescript',
													tags: [ 'step002', 'tst', 'tag1', 'tag2' ],
													initParameters: [ 'env': 'tst', 'package': 'mypackage' ]
												]
											 ]
									]
	}
}
