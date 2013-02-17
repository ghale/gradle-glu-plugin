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
		this.status = map['X-glu-status']
		this.startTime = map['X-glu-startTime']
		this.endTime = map['X-glu-endTime']
		this.completedSteps = map['X-glu-completedSteps']
		this.totalSteps = map['X-glu-totalSteps']
		this.username = map['X-glu-username']
		this.description = map['X-glu-description']
	}
}
