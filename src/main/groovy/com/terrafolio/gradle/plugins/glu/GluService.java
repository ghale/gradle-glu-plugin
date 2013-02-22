package com.terrafolio.gradle.plugins.glu;

import java.util.List;
import java.util.Map;

public interface GluService {
	public String getTargetServer();
	
	public Object getFabric(String fabricName) throws GluServiceException;
	
	public void createFabric(String fabricName, String zookeeper, String zookeeperTimeout, String color) throws GluServiceException;
	
	public void loadModel(String fabricName, String model) throws GluServiceException;
	
	public String createPlan(String fabricName, List<String> tags, Map<String,String> action, String order) throws GluServiceException;
	
	public String executePlan(String fabricName, String planId) throws GluServiceException;
	
	public DeploymentStatus getDeploymentStatus(String fabricName, String executionId) throws GluServiceException;
	
	public Object getExecutionStatus(String fabricName, String planId, String executionId) throws GluServiceException;
}
