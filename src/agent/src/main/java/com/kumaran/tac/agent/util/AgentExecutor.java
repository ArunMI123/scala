package com.kumaran.tac.agent.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumaran.tac.agent.service.AgentService;

@Component
@Scope(value = "prototype")
public class AgentExecutor implements Runnable {

	public static BlockingQueue<String> agentTriggerQueue = new LinkedBlockingQueue<>();

	@Autowired
	AgentService agentService;

	ObjectMapper mapper = new ObjectMapper();

	@Autowired
	FrameworkQueueDetails frameworkQueueDetail;
	
//	HashMap<String,Boolean> frameworkInitializationInfo = new HashMap<>();
	
	private static final Logger logger = LogManager.getLogger(AgentExecutor.class);
	
	@Autowired
	ApplicationContext applicationContext;

	@Override
	public void run() {
		try {
			frameworkQueueDetail.frameworkQueueMap.clear();
			agentTriggerQueue.clear();
			boolean flag = true;
			String serverURL = "";
			boolean serverUrlExists = false;
			Integer agentId = null;
//			Set<String> frameworkInfo = new HashSet<>();
			while (flag) {
				String dataFromAgentTriggerQueue = agentTriggerQueue.poll(2, TimeUnit.SECONDS);
				if (!serverUrlExists) {
					serverURL = LoadConfigFile.getInstance().getPropertyAsString("agentUrl");
					if (serverURL != null) {
						agentId = LoadConfigFile.getInstance().getPropertyAsIntger("agentId");
						serverUrlExists = true;
					}
				}
				if(dataFromAgentTriggerQueue !=null){					
					if (dataFromAgentTriggerQueue.contains("testRunAborted")) {
						frameworkQueueDetail.frameworkQueueMap.clear();
						dataFromAgentTriggerQueue=null;
						continue;
					} 
				}
				
				if (serverURL != null && agentId!=null) {
					logger.info("Communicate serverWeb and get step data.");
					String teststepData = agentService.getTeststepData(agentId);
					logger.info("teststepData:"+teststepData);
//					if (  teststepData != null && !teststepData.contains("testcaseStarted") && !teststepData.contains("testcaseEnd")
//							&& !teststepData.contains("testcaseEnd")) {
					if (teststepData != null) {
						if (!teststepData.contains("MSG:")) {
							HashMap<String, Object> nextStepData = mapper.readValue(teststepData,
									new TypeReference<HashMap<String, Object>>() {
									});
							String frameworkName = findFrameworkName(nextStepData);
							logger.info("Send Data to Framework queue:" + frameworkName);
							frameworkQueueDetail.sendToFrameworkQueue(frameworkName, nextStepData);
//							frameworkInfo.add(frameworkName);
//						if (!frameworkInitializationInfo.get(frameworkName)) {
//							agentService.invokeFramework(frameworkName);
//							frameworkInitializationInfo.put(frameworkName, true);
//						}
						}else if (teststepData.contains("MSG:") && teststepData.contains("testRunAborted")) {
							frameworkQueueDetail.frameworkQueueMap.forEach((key,value) -> {
								try {
									value.put("testRunAborted");
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								frameworkQueueDetail.frameworkQueueMap.put(key, value);
							});
						}
						else if (teststepData.contains("MSG:") && teststepData.contains("testRun")){
							if(teststepData.contains("testRunStarted")) {
								frameworkQueueDetail.frameworkQueueMap.clear();
							}
							teststepData = teststepData.replace("\"", "");
							String frameworkName = getFrameworkName(teststepData);
							frameworkQueueDetail.sendToFrameworkQueue(frameworkName, teststepData);
						} else if (teststepData.contains("MSG:") && teststepData.contains("testcase")){
							teststepData = teststepData.replace("\"", "");
							String frameworkName = getFrameworkName(teststepData);
							frameworkQueueDetail.sendToFrameworkQueue(frameworkName, teststepData);
						}
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private String getFrameworkName(String frameworkMsg) {
		String arr[] = frameworkMsg.split(",");
		return arr[1];
	}

	private String findFrameworkName(HashMap<String, Object> nextStepData) {
		HashMap<String, Object> testRunDetail = (HashMap<String, Object>) nextStepData.get("executionPlan");
		String tool = testRunDetail.get("tool").toString();
		return tool == null ? "" : tool;
	}

}
