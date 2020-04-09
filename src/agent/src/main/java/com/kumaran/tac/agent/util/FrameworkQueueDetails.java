package com.kumaran.tac.agent.util;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.kumaran.tac.agent.service.AgentService;

import ch.qos.logback.core.db.dialect.SybaseSqlAnywhereDialect;

@Component
@Scope("singleton")
public class FrameworkQueueDetails {
	
	@Autowired
	private AgentService agentService;
	
	private static final Logger logger = LogManager.getLogger(FrameworkQueueDetails.class);
	
	// Map to hold agent with queue information
	public Map<String, BlockingQueue<Object>> frameworkQueueMap = new ConcurrentHashMap<String, BlockingQueue<Object>>();

	private  BlockingQueue<Object> getFrameworkQueue( String frameworkName) throws InterruptedException {
//		logger.info("Check Framework  Queue is  exist :"+frameworkName);
		if( !frameworkQueueMap.containsKey( frameworkName)) {
			frameworkQueueMap.put( frameworkName, new LinkedBlockingQueue<Object>());
			logger.info("If not exist Create new framework Queue  :"+frameworkName);
//			frameworkQueueMap.get( frameworkName).put(data);
			// Start FW
//			logger.info("Start Framework:"+frameworkName);
			agentService.invokeFramework(frameworkName);
		}
//		logger.info("get Data From FrameworkQueue:"+frameworkQueueMap.get( frameworkName));
		return frameworkQueueMap.get( frameworkName);
	}
	
	public synchronized void sendToFrameworkQueue(String frameworkName, Object data) throws InterruptedException {
		getFrameworkQueue(frameworkName).put(data);
	}
	
	public Object getPendingDataToFramework(String frameworkName) throws InterruptedException {
		return getFrameworkQueue(frameworkName).poll(5, TimeUnit.SECONDS);
	}

	public Map<String, BlockingQueue<Object>> getFrameworkQueueMap() {
		return frameworkQueueMap;
	}

	public void setFrameworkQueueMap(Map<String, BlockingQueue<Object>> frameworkQueueMap) {
		this.frameworkQueueMap = frameworkQueueMap;
	}
	
	

}
