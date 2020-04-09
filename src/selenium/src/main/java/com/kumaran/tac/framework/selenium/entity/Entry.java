package com.kumaran.tac.framework.selenium.entity;

import java.util.ArrayList;

public class Entry {
	
	private  int seqNo;
	

	private String name;
	private String type;
	private int parentAttrId;
	private String Id;
	private ArrayList<FieldDetails> fieldDetails;
	private String windowOrFrame;
	
	public int getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<FieldDetails> getFieldDetails() {
		return fieldDetails;
	}
	public void setFieldDetails(ArrayList<FieldDetails> fieldDetails) {
		this.fieldDetails = fieldDetails;
	}
	public String getWindowOrFrame() {
		return windowOrFrame;
	}
	public void setWindowOrFrame(String windowOrFrame) {
		this.windowOrFrame = windowOrFrame;
	}
	public int getParentAttrId() {
		return parentAttrId;
	}

	public void setParentAttrId(int parentAttrId) {
		this.parentAttrId = parentAttrId;
	}
	
/*	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getName() + this.getSeqNo() + this.getType() + this.getWindowOrFrame() + this.getFieldDetails().toString();
	}*/
}
