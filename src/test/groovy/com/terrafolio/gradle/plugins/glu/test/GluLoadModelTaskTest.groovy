package com.terrafolio.gradle.plugins.glu.test

import static org.junit.Assert.*

import com.terrafolio.gradle.plugins.glu.GluPlugin
import com.terrafolio.gradle.plugins.glu.GluServiceException
import com.terrafolio.gradle.plugins.glu.LoadModelCommand
import com.terrafolio.gradle.plugins.glu.GluLoadModelTask
import com.terrafolio.gradle.plugins.glu.Constants
import com.terrafolio.gradle.plugins.glu.ExecutionChainFactory
import groovy.mock.interceptor.MockFor

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context
import org.apache.commons.chain.Chain
import org.gradle.api.Project
import org.gradle.api.tasks.TaskExecutionException
import org.junit.Test
import org.junit.Before
import org.gradle.testfixtures.ProjectBuilder

class GluLoadModelTaskTest {
	def private final Project project = ProjectBuilder.builder().withProjectDir(new File('build/tmp/test')).build()
	def private final GluPlugin plugin = new GluPlugin()
	def mockChainFactory
	
	@Before
	def void setupProject() {
		plugin.apply(project)
		
		project.glu {
			servers {
				test {
					url 'http://test'
					username 'testuser'
					password 'testpass'
				}
			}
				
			applications {
				myapp {
					mountPoint '/myapp'
					script 'http://somescript'
					tags = [ 'myapp' ]
				}
			}
				
			fabrics {
				test {
					server servers.test
					model merge(applications.myapp.generate(
								agents: [ 
											'agent1': [ 'step001' ],
											'agent2': [ 'step002' ]
									 	],
								tags: [ 'tst' ],
								initParameters: [ 'env': 'tst', 'package': 'mypackage' ]
							))
				}
			}
		}

		mockChainFactory = new MockFor(ExecutionChainFactory.class)
	}
	
	@Test
	def void load_loadsModel() {
		mockChainFactory.demand.with {
			getExecutionChain() {
				return new Chain() {
					def commands = []
					
					@Override
					public void addCommand(Command command) {
						assert command.fabric == project.glu.fabrics.test.model
						assert command instanceof LoadModelCommand
						commands += command
					}
			
					@Override
					public boolean execute(Context context) throws Exception {
						assert context.get(Constants.FABRIC) == 'test'
						assert context.get(Constants.LOGGER) == project.logger
						assert commands.size == 1
						return Constants.SUCCESS
					}
				}	
			}
		}
		
		mockChainFactory.use {
			project.task('loadTestModel', type: GluLoadModelTask) {
				fabric project.glu.fabrics.test
			}
			
			project.tasks.loadTestModel.execute()
		}
	}
	
	@Test (expected = TaskExecutionException.class)
	def void load_throwsExceptionOnFailure() {
		mockChainFactory.demand.with {
			getExecutionChain() {
				return new Chain() {
					def commands = []
					
					@Override
					public void addCommand(Command command) {
						commands += command
					}
			
					@Override
					public boolean execute(Context context) throws Exception {
						throw new GluServiceException("Mock Service Exception")
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('loadTestModel', type: GluLoadModelTask) {
				fabric project.glu.fabrics.test
			}
			
			project.tasks.loadTestModel.execute()
		}
	}
}
