package com.terrafolio.gradle.plugins.glu.test;

import static org.junit.Assert.*

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context
import org.gradle.api.Project
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import com.terrafolio.gradle.plugins.glu.GluPlugin;
import com.terrafolio.gradle.plugins.glu.ExecutionCommand
import com.terrafolio.gradle.plugins.glu.GluExecutionTask
import com.terrafolio.gradle.plugins.glu.Constants
import com.terrafolio.gradle.plugins.glu.ExecutionChainFactory

import groovy.mock.interceptor.MockFor

class GluExecutionTaskTest {
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
	def void deploy_callsExecutionCommandWithCorrectArgs() {
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
						assert context.get(Constants.FABRIC) == 'test'
						assert context.get(Constants.LOGGER) == project.logger
						assert commands.size == 1
						assert commands[0].action == [ planAction: 'deploy' ]
						assert commands[0].tags == [ 'step001' ]
						assert commands[0].order == 'parallel'
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('deployTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				deploy(tags: [ 'step001' ])
			}
			project.tasks.deployTest.execute()
		}
	}
	
	@Test
	def void redeploy_callsExecutionCommandWithCorrectArgs() {
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
						assert context.get(Constants.FABRIC) == 'test'
						assert context.get(Constants.LOGGER) == project.logger
						assert commands.size == 1
						assert commands[0].action == [ planAction: 'redeploy' ]
						assert commands[0].tags == [ 'step001' ]
						assert commands[0].order == 'parallel'
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('redeployTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				redeploy(tags: [ 'step001' ])
			}
			project.tasks.redeployTest.execute()
		}
	}
	
	@Test
	def void undeploy_callsExecutionCommandWithCorrectArgs() {
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
						assert context.get(Constants.FABRIC) == 'test'
						assert context.get(Constants.LOGGER) == project.logger
						assert commands.size == 1
						assert commands[0].action == [ planAction: 'undeploy' ]
						assert commands[0].tags == [ 'step001' ]
						assert commands[0].order == 'parallel'
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('undeployTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				undeploy(tags: [ 'step001' ])
			}
			project.tasks.undeployTest.execute()
		}
	}
	
	@Test
	def void start_callsExecutionCommandWithCorrectArgs() {
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
						assert context.get(Constants.FABRIC) == 'test'
						assert context.get(Constants.LOGGER) == project.logger
						assert commands.size == 1
						assert commands[0].action == [ planAction: 'start' ]
						assert commands[0].tags == [ 'step001' ]
						assert commands[0].order == 'parallel'
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('startTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				start(tags: [ 'step001' ])
			}
			project.tasks.startTest.execute()
		}
	}
	
	@Test
	def void stop_callsExecutionCommandWithCorrectArgs() {
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
						assert context.get(Constants.FABRIC) == 'test'
						assert context.get(Constants.LOGGER) == project.logger
						assert commands.size == 1
						assert commands[0].action == [ planAction: 'stop' ]
						assert commands[0].tags == [ 'step001' ]
						assert commands[0].order == 'parallel'
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('stopTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				stop(tags: [ 'step001' ])
			}
			project.tasks.stopTest.execute()
		}
	}
	
	@Test
	def void bounce_callsExecutionCommandWithCorrectArgs() {
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
						assert context.get(Constants.FABRIC) == 'test'
						assert context.get(Constants.LOGGER) == project.logger
						assert commands.size == 1
						assert commands[0].action == [ planAction: 'bounce' ]
						assert commands[0].tags == [ 'step001' ]
						assert commands[0].order == 'parallel'
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('bounceTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				bounce(tags: [ 'step001' ])
			}
			project.tasks.bounceTest.execute()
		}
	}
	
	@Test
	def void bounce_callsExecutionWithTaskOverriddenOrder() {
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
						assert commands[0].order == 'sequential'
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('bounceTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				order 'sequential'
				
				bounce(tags: [ 'step001' ])
			}
			project.tasks.bounceTest.execute()
		}
	}
	
	@Test
	def void bounce_callsExecutionWithCommandOverriddenOrder() {
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
						assert commands[0].order == 'sequential'
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('bounceTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				bounce(tags: [ 'step001' ], order: 'sequential')
			}
			project.tasks.bounceTest.execute()
		}
	}
	
	@Test
	def void execute_callsExecutionWithMultipleCommands() {
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
						assert context.get(Constants.FABRIC) == 'test'
						
						assert commands.size == 2
						
						assert commands[0].action == [ planAction: 'stop' ]
						assert commands[0].tags == [ 'step001' ]
						assert commands[0].order == 'sequential'
						
						assert commands[1].action == [ planAction: 'deploy' ]
						assert commands[1].tags == [ 'step002' ]
						assert commands[1].order == 'parallel'
						
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('multipleTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				stop(tags: [ 'step001' ], order: 'sequential')
				deploy(tags: [ 'step002' ], order: 'parallel')
			}
			project.tasks.multipleTest.execute()
		}
	}
	
	@Test (expected = TaskExecutionException.class)
	def void execute_throwsExceptionOnFailure() {
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
						return true
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('stopTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				stop(tags: [ 'step001' ])
			}
			project.tasks.stopTest.execute()
		}
	}
	
	@Test
	def void execute_callsExecutionWithExecutionTime() {
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
						return false
					}
				}
			}
		}
		
		mockChainFactory.use {
			def testExecutionTime = 11111
			project.task('bounceTest', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				executionTime = testExecutionTime
				
				bounce(tags: [ 'step001' ], order: 'sequential')
			}
			
			project.ext.called = false
			project.tasks.bounceTest.sleepUntil = { executionTime -> 
				assert executionTime == testExecutionTime
				project.ext.called = true
			} 
			
			project.tasks.bounceTest.execute()
			assert project.ext.called
		}
	}
}
