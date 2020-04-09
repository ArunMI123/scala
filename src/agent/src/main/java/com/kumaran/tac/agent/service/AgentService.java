package com.kumaran.tac.agent.service;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kumaran.tac.agent.model.Agent;


public interface AgentService {
	public byte[] getScreenshot();
	
	public String getTestScriptData(String machinestype);

	public String addScriptvale();

	public String updateScriptStatus();
	
	public int frameworkUpload(MultipartFile file);

	public void startCodedUi();

	public void startQtp(HashMap<String,byte[]> customFiles);
	
//	public void startSelenium();
	
	public void invokeSelenium(String testRunId,HashMap<String,byte[]> customFiles,String uniqueProjectId);
	
	public ResponseEntity<String> getTestRunOR(String testRunId);
	
	public ResponseEntity<String> getTestStepData(String testRunId) throws Exception;
	
	public void saveTestStepResult(HashMap<String,String> testStepResult) throws Exception;

	Object saveFailScreenshot(String testStepResultId, byte[] inputStream) throws Exception;
	
	public ResponseEntity<String> getTestRunId() throws Exception;

	public void startAPI(String testRunId);

	public HashMap<String, String> getValidationStatus(HashMap<String, String> validationDetials);

	public void saveResponseData(HashMap<String, Object> responseDataJson);

	public HashMap<String,String> saveAgentDetail(Agent agentInfo) throws Exception;

	public String getTeststepData(int agentId) throws Exception;
	
	
	public void invokeFramework(String tool);

	public Object getNextStepDataFromQueue(String frameworkName) throws InterruptedException;
	
	public void formValidationJson(Object nextTestStepJson);
	
	public String abortConfirmation(int testRunId);
}
