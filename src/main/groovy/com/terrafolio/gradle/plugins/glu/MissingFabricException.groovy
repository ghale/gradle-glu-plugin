package com.terrafolio.gradle.plugins.glu

class MissingFabricException extends Exception {

	public MissingFabricException() {
		super();
	}

	public MissingFabricException(String message, Throwable t) {
		super(message, t);
	}

	public MissingFabricException(String message) {
		super(message);
	}

	public MissingFabricException(Throwable t) {
		super(t);
	}

}
