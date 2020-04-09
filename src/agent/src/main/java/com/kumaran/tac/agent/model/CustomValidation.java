package com.kumaran.tac.agent.model;

import java.util.ArrayList;
import java.util.List;

public class CustomValidation {
	private List<String> expectedValues = new ArrayList<String>();
	private String expectedValue;
	private String boundaryStart;
	private String boundaryEnd;
	private String rowNumber;
	private String contentInRow;
	private String columnHeading;
	private String uniqueRowData;
	private String variableId;
	private String type;
	
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
	public String getBoundaryStart() {
		return boundaryStart;
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
	public String getRowNumber() {
		return rowNumber;
	}
	public void setRowNumber(String rowNumber) {
		this.rowNumber = rowNumber;
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
	public String getUniqueRowData() {
		return uniqueRowData;
	}
	public void setUniqueRowData(String uniqueRowData) {
		this.uniqueRowData = uniqueRowData;
	}
	public String getVariableId() {
		return variableId;
	}
	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
