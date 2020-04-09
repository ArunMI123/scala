package com.kumaran.tac.framework.selenium.entity;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attributes {

	// private int transactionId;
	private int id;
	private int seqNo;
	private int waitTime;
	private String name;
	private String Type;
	private String gridType;
	private int actionField;
	private String columnName;
	private ArrayList<FieldDetails> fieldDetails;
	private int parentAttrId;
	private int index;
	private int frameAttrId;
	private String windowOrFrame;
	private int ignore_ind;
	@JsonProperty("Fixedwait")
	private Integer Fixedwait;
	@JsonProperty("MaxWait")
	private Integer maxWait;
	@JsonProperty("AttributeWait")
	private String AttributeWait;
	@JsonProperty("PageLoadWait")
	private Boolean PageLoadWait;
	private Boolean endTransactionFlag;
	private Integer read_only;
	
	public Integer getRead_only() {
		return read_only;
	}

	public void setRead_only(Integer read_only) {
		this.read_only = read_only;
	}

	public Boolean getEndTransactionFlag() {
		return endTransactionFlag;
	}

	public void setEndTransactionFlag(Boolean endTransactionFlag) {
		this.endTransactionFlag = endTransactionFlag;
	}

	public Integer getFixedwait() {
		return Fixedwait;
	}

	public void setFixedwait(Integer fixedwait) {
		Fixedwait = fixedwait;
	}

	public Integer getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(Integer MaxWait) {
		maxWait = MaxWait;
	}

	public String getAttributeWait() {
		return AttributeWait;
	}

	public void setAttributeWait(String attributeWait) {
		AttributeWait = attributeWait;
	}

	public Boolean getPageLoadWait() {
		return PageLoadWait;
	}

	public void setPageLoadWait(Boolean pageLoadWait) {
		PageLoadWait = pageLoadWait;
	}

	public int getFrameAttrId() {
		return frameAttrId;
	}

	public void setFrameAttrId(int frameAttrId) {
		this.frameAttrId = frameAttrId;
	}

	public int getParentAttrId() {
		return parentAttrId;
	}

	public void setParentAttrId(int parentAttrId) {
		this.parentAttrId = parentAttrId;
	}

	// public int getTransactionId() {
	// return transactionId;
	// }

	// public void setTransactionId(int transactionId) {
	// this.transactionId = transactionId;
	// }
	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public int getActionField() {
		return actionField;
	}

	public void setActionField(int actionField) {
		this.actionField = actionField;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		this.Type = type;
	}

	public ArrayList<FieldDetails> getFieldDetails() {
		return fieldDetails;
	}

	public void setFieldDetails(ArrayList<FieldDetails> fieldDetails) {
		this.fieldDetails = fieldDetails;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}



	public String getWindowOrFrame() {
		return windowOrFrame;
	}

	public void setWindowOrFrame(String windowOrFrame) {
		this.windowOrFrame = windowOrFrame;
	}

	public String getGridType() {
		return gridType;
	}

	public void setGridType(String gridType) {
		this.gridType = gridType;
	}

	public int getIgnore_ind() {
		return ignore_ind;
	}

	public void setIgnore_ind(int ignore_ind) {
		this.ignore_ind = ignore_ind;
	}



	/*
	 * private String isTestDataRequired; private String versionAdded; private
	 * String versionDropped; private String defaultValue; private String
	 * defaultAction; private String parentTransactionAttributeId; private
	 * String isMandatory;
	 */

}
