package com.kumaran.tac.framework.selenium.entity;

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
	private String validationId;
	private String validationStatusId;
	private String variableId;
	private String variableName;
	private String positionAttribute;
	private String position;
	private int validationLinkId;
	private int positionAttributeId;
	private String type;
	
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

	public String getValidationId() {
		return validationId;
	}

	public void setValidationId(String validationId) {
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

	public String getVariableId() {
		return variableId;
	}

	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public String getValidationStatusId() {
		return validationStatusId;
	}

	public void setValidationStatusId(String validationStatusId) {
		this.validationStatusId = validationStatusId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	public String getPositionAttribute() {
		return positionAttribute;
	}

	public void setPositionAttribute(String positionAttribute) {
		this.positionAttribute = positionAttribute;
	}
}
