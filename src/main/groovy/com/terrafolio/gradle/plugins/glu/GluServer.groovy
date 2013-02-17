package com.terrafolio.gradle.plugins.glu

class GluServer {
	def name
	def url
	def username
	def password
	
	GluServer(String name) {
		this.name = name
	}
	
	def url(String url) {
		this.url = url
	}
	
	def username(String username) {
		this.username = username
	}
	
	def password(String password) {
		this.password = password
	}
}
