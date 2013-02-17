package com.terrafolio.gradle.plugins.glu;

import java.util.List;
import java.util.Map;

public interface GluService {
	public void loadModel(String fabricName, Map fabric) throws GluServiceException;
	
	public String createPlan(String fabricName, List tags, Map action, String order) throws GluServiceException;
	
	public String executePlan(String fabricName, String planId) throws GluServiceException;
	
	public DeploymentStatus getDeploymentStatus(String fabricName, String executionId) throws GluServiceException;
	
	public Object getExecutionStatus(String fabricName, String planId, String executionId) throws GluServiceException;
}
