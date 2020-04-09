package com.kumaran.tac.agent.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.kumaran.tac.agent.controller.AgentController;

public class LoadConfigFile {

	private final Properties configProp = new Properties();

	LoadConfigFile() {
		try {
			InputStream agentStream = new FileInputStream(AgentController.home + "/agent/agent.properties");
			if(agentStream.available()>0) {
				configProp.load(agentStream);	
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static LoadConfigFile getInstance() {
		return new LoadConfigFile();
	}

	public String getPropertyAsString(String key) {
		return configProp.getProperty(key);
	}
	
	public Integer getPropertyAsIntger(String key) {
		return Integer.parseInt(configProp.getProperty(key));
	}

}
