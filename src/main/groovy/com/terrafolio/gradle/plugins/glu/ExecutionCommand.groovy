package com.terrafolio.gradle.plugins.glu

import java.text.DateFormat
import java.text.SimpleDateFormat
import org.apache.commons.chain.Command
import org.apache.commons.chain.Context

class ExecutionCommand implements Command {
	def action
	def tags
	def order
	def pollInterval = 15000

	public ExecutionCommand(Map action, List tags, order) {
		this.action = action
		this.tags = tags
		this.order = order
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		def service = context.get(Constants.SERVICE)
		def fabricName = context.get(Constants.FABRIC)
		def logger = context.get(Constants.LOGGER)
		
		def planId = service.createPlan(fabricName, tags, action, order)
		def executionId = service.executePlan(fabricName, planId)
		def DeploymentStatus deploymentStatus = null
		def lastCompleted = 0
		while (true) {
			deploymentStatus = service.getDeploymentStatus(fabricName, executionId)
			if (deploymentStatus.completedSteps != lastCompleted) {
				logger.warn("${deploymentStatus.completedSteps} of ${deploymentStatus.totalSteps} steps completed...")
				lastCompleted = deploymentStatus.completedSteps
			}
			
			if (deploymentStatus.status == Constants.RUNNING) {
				this.sleep(pollInterval)
			} else {
				break
			}
		}
		def executionDocument = service.getExecutionStatus(fabricName, planId, executionId)
		context.put(Constants.STATUS, executionDocument)
		
		logStatus(logger, executionDocument)
		return deploymentStatus.status != Constants.COMPLETED
 	}
	
	def logStatus(logger, executionDocument) {
		String msg = ""
		executionDocument.children().each { execution ->
			msg += nodeToString('', execution)
			execution.sequential.each { mountpoint ->
				msg += nodeToString('', mountpoint)
				mountpoint.leaf.each { leaf ->
					msg += nodeToString('    ', leaf)
				}
			}
		}
		logger.warn(msg)
	}
	
	def nodeToString(prefix, node) {
		def name = node.attributes().containsKey('name') ? node.@name.text() : node.@mountPoint.text()
		def status = node.@status.text()
		def startTime = node.@startTime.text()
		def endTime = node.@endTime.text()
		
		def DateFormat formatter = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
		def Date startDate = formatter.parse(startTime)
		def Date endDate = formatter.parse(endTime)
		def totalTime = (endDate.getTime() - startDate.getTime())/1000
		
		"${prefix}${name}: ${status} in ${totalTime}s\n"
	}

}
