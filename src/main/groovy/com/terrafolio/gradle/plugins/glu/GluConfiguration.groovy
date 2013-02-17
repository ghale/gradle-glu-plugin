package com.terrafolio.gradle.plugins.glu

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.util.ConfigureUtil

class GluConfiguration {
	def fabrics
	def defaultServer
	def servers
	def applications
	
	GluConfiguration(NamedDomainObjectCollection<Fabric> fabrics, NamedDomainObjectCollection<GluServer> servers, NamedDomainObjectCollection<Application> applications) {
		this.fabrics = fabrics
		this.servers = servers
		this.applications = applications
	}
	
	def fabrics(Closure closure) {
		ConfigureUtil.configure(closure, fabrics)
	}
	
	def servers(Closure closure) {
		ConfigureUtil.configure(closure, servers)
	}
	
	def applications(Closure closure) {
		ConfigureUtil.configure(closure, applications)
	}
}
