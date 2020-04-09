package com.kumaran.tac.agent.model;

import org.springframework.stereotype.Component;

@Component
public class Agent {

	private String agentName;
	private String agentURL;
	private String serverURL;

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getAgentURL() {
		return agentURL;
	}

	public void setAgentURL(String agentURL) {
		this.agentURL = agentURL;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

}
