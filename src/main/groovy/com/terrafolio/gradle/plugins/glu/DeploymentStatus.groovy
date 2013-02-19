package com.terrafolio.gradle.plugins.glu

class DeploymentStatus {
	def status
	def startTime
	def endTime
	def completedSteps
	def totalSteps
	def username
	def description

	public DeploymentStatus() {	}
	
	public DeploymentStatus(Map map) {
		this.status = map.get('X-glu-status', 'UNKNOWN')
		this.startTime = map.get('X-glu-startTime', null)
		this.endTime = map.get('X-glu-endTime', null)
		this.completedSteps = map.get('X-glu-completedSteps', '0')
		this.totalSteps = map.get('X-glu-totalSteps', '???')
		this.username = map.get('X-glu-username', 'UNKNOWN')
		this.description = map.get('X-glu-description', 'No description')
	}
	

}
