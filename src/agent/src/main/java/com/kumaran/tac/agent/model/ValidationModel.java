package com.kumaran.tac.agent.model;

import java.util.ArrayList;
import java.util.List;

public class ValidationModel {
	private int attributeId;
	private String controlName;
	private String conditionExpression;
	private List<String> expectedValues = new ArrayList<String>();
	private String expectedValue;
	private String boundaryStart;
	private String boundaryEnd;
	private String rowNumber;
	private String contentInRow;
	private String columnHeading;
	private String validationType;
	private String uniqueRowData;
	private Integer validationId;
	private Integer variableId;
	private String variableName;
	private String inputValue;
	private String outputValue;
	private String status;
	private String path;
	private String position;
	private int validationLinkId;
	private int positionAttributeId;
	private String positionAttribute;
	private String responseHeaderType;
	
	public String getPositionAttribute() {
		return positionAttribute;
	}

	public void setPositionAttribute(String positionAttribute) {
		this.positionAttribute = positionAttribute;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInputValue() {
		return inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}

	public String getOutputValue() {
		return outputValue;
	}

	public void setOutputValue(String outputValue) {
		this.outputValue = outputValue;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(Integer variableId) {
		this.variableId = variableId;
	}

	public Integer getValidationId() {
		return validationId;
	}

	public void setValidationId(Integer validationId) {
		this.validationId = validationId;
	}

	public int getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

	public String getUniqueRowData() {
		return uniqueRowData;
	}

	public void setUniqueRowData(String uniqueRowData) {
		this.uniqueRowData = uniqueRowData;
	}

	public String getControlName() {
		return controlName;
	}

	public void setControlName(String controlName) {
		this.controlName = controlName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public int getValidationLinkId() {
		return validationLinkId;
	}

	public void setValidationLinkId(int validationLinkId) {
		this.validationLinkId = validationLinkId;
	}

	public int getPositionAttributeId() {
		return positionAttributeId;
	}

	public void setPositionAttributeId(int positionAttributeId) {
		this.positionAttributeId = positionAttributeId;
	}

	public String getConditionExpression() {
		return conditionExpression;
	}

	public void setConditionExpression(String conditionExpression) {
		this.conditionExpression = conditionExpression;
	}

	public String getBoundaryStart() {
		return boundaryStart;
	}

	public List<String> getExpectedValues() {
		return expectedValues;
	}

	public void setExpectedValues(List<String> expectedValues) {
		this.expectedValues = expectedValues;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	public void setBoundaryStart(String boundaryStart) {
		this.boundaryStart = boundaryStart;
	}

	public String getBoundaryEnd() {
		return boundaryEnd;
	}

	public void setBoundaryEnd(String boundaryEnd) {
		this.boundaryEnd = boundaryEnd;
	}

	public String getContentInRow() {
		return contentInRow;
	}

	public void setContentInRow(String contentInRow) {
		this.contentInRow = contentInRow;
	}

	public String getColumnHeading() {
		return columnHeading;
	}

	public void setColumnHeading(String columnHeading) {
		this.columnHeading = columnHeading;
	}

	public String getValidationType() {
		return validationType;
	}

	public void setValidationType(String validationType) {
		this.validationType = validationType;
	}

	public String getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getResponseHeaderType() {
		return responseHeaderType;
	}

	public void setResponseHeaderType(String responseHeaderType) {
		this.responseHeaderType = responseHeaderType;
	}
	
}
