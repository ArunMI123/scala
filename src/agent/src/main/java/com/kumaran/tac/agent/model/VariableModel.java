package com.kumaran.tac.agent.model;

public class VariableModel {

	private int variableId;
	private int testcaseId;
	private String variableName;
	private String variableValue;
	
	public int getVariableId() {
		return variableId;
	}
	public void setVariableId(int variableId) {
		this.variableId = variableId;
	}
	public int getTestcaseId() {
		return testcaseId;
	}
	public void setTestcaseId(int testcaseId) {
		this.testcaseId = testcaseId;
	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public String getVariableValue() {
		return variableValue;
	}
	public void setVariableValue(String variableValue) {
		this.variableValue = variableValue;
	}
	
}
