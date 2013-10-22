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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('deployTestStep1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				deploy(tags: [ 'step001' ])
			}
			project.tasks.deployTestStep1.execute()
		}
	}
	
	@Test
	def void deploy_callsExecutionCommandWithEmptyMap() {
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
						assert commands[0].tags == []
						assert commands[0].order == 'parallel'
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('deployTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				deploy()
			}
			project.tasks.deployTest1.execute()
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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('redeployTestStep1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				redeploy(tags: [ 'step001' ])
			}
			project.tasks.redeployTestStep1.execute()
		}
	}
	
	@Test
	def void redeploy_callsExecutionCommandWithEmptyMap() {
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
						assert commands[0].tags == []
						assert commands[0].order == 'parallel'
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('redeployTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				redeploy()
			}
			project.tasks.redeployTest1.execute()
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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('undeployTestStep1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				undeploy(tags: [ 'step001' ])
			}
			project.tasks.undeployTestStep1.execute()
		}
	}
	
	@Test
	def void undeploy_callsExecutionCommandWithEmptyMap() {
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
						assert commands[0].tags == []
						assert commands[0].order == 'parallel'
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('undeployTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				undeploy()
			}
			project.tasks.undeployTest1.execute()
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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('startTestStep1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				start(tags: [ 'step001' ])
			}
			project.tasks.startTestStep1.execute()
		}
	}
	
	@Test
	def void start_callsExecutionCommandWithEmptyMap() {
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
						assert commands[0].tags == []
						assert commands[0].order == 'parallel'
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('startTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				start()
			}
			project.tasks.startTest1.execute()
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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('stopTestStep1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				stop(tags: [ 'step001' ])
			}
			project.tasks.stopTestStep1.execute()
		}
	}
	
	@Test
	def void stop_callsExecutionCommandWithEmptyMap() {
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
						assert commands[0].tags == []
						assert commands[0].order == 'parallel'
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('stopTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				stop()
			}
			project.tasks.stopTest1.execute()
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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('bounceTestStep1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				bounce(tags: [ 'step001' ])
			}
			project.tasks.bounceTestStep1.execute()
		}
	}
	
	@Test
	def void bounce_callsExecutionCommandWithEmptyMap() {
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
						assert commands[0].tags == []
						assert commands[0].order == 'parallel'
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('bounceTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				bounce()
			}
			project.tasks.bounceTest1.execute()
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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('bounceTestStep1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				order 'sequential'
				
				bounce(tags: [ 'step001' ])
			}
			project.tasks.bounceTestStep1.execute()
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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('bounceTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				bounce(tags: [ 'step001' ], order: 'sequential')
			}
			project.tasks.bounceTest1.execute()
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
						
						return Constants.SUCCESS
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
						return Constants.FAILURE
					}
				}
			}
		}
		
		mockChainFactory.use {
			project.task('stopTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				stop(tags: [ 'step001' ])
			}
			project.tasks.stopTest1.execute()
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
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			def testExecutionTime = 11111
			project.task('bounceTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				executionTime = testExecutionTime
				
				bounce(tags: [ 'step001' ], order: 'sequential')
			}
			
			project.ext.called = false
			project.tasks.bounceTest1.sleepUntil = { executionTime -> 
				assert executionTime == testExecutionTime
				project.ext.called = true
			} 
			
			project.tasks.bounceTest1.execute()
			assert project.ext.called
		}
	}
	
	@Test
	def void execute_callsExecutionWithDeploymentPollingAction() {
		def customPollingAction = { }
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
						assert context.get(Constants.POLLING_ACTION) == customPollingAction
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			def testExecutionTime = 11111
			project.task('bounceTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				bounce(tags: [ 'step001' ], order: 'sequential')
				
				withDeploymentPollingAction customPollingAction
			}
			
			project.tasks.bounceTest1.execute()
		}
	}
	
	@Test
	def void execute_callsExecutionWithDeploymentCompleteAction() {
		def customCompleteAction = { }
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
						assert context.get(Constants.COMPLETE_ACTION) == customCompleteAction
						return Constants.SUCCESS
					}
				}
			}
		}
		
		mockChainFactory.use {
			def testExecutionTime = 11111
			project.task('bounceTest1', type: GluExecutionTask) {
				fabric project.glu.fabrics.test
				
				bounce(tags: [ 'step001' ], order: 'sequential')
				
				withDeploymentCompleteAction customCompleteAction
			}
			
			project.tasks.bounceTest1.execute()
		}
	}
}
