package com.kumaran.tac.framework.selenium.entity;

import java.util.ArrayList;
import java.util.List;
import com.kumaran.tac.framework.selenium.entity.Entry;



public class Transactions {

	private int transactionId;
	private int projectId;
	private String transactionName;
	private String variablePair;
	private int stepId; 
	private ArrayList<Attributes> attributes;
	
	private List<Attributes> entry = new ArrayList<Attributes>();
	private List<Attributes> reEntry = new ArrayList<Attributes>();

	// private String transactionDesc;
	// private String previousTransaction;
	// private String nextTransaction;
	public int getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
	public ArrayList<Attributes> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<Attributes> attributes) {
		this.attributes = attributes;
	}
	
	public List<Attributes> getEntry() {
		return entry;
	}

	public void setEntry(ArrayList<Attributes> entry) {
		this.entry = entry;
	}

	public List<Attributes> getReEntry() {
		return reEntry;
	}

	public void setReEntry(ArrayList<Attributes> reEntry) {
		this.reEntry = reEntry;
	}

	public String getVariablePair() {
		return variablePair;
	}

	public void setVariablePair(String variablePair) {
		this.variablePair = variablePair;
	}

	public int getStepId() {
		return stepId;
	}

	public void setStepId(int stepId) {
		this.stepId = stepId;
	}


	/*public void setTransactionDesc(String transactionDesc) {
		this.transactionDesc = transactionDesc;
	}

	public String getPreviousTransaction() {
		return previousTransaction;
	}

	public void setPreviousTransaction(String previousTransaction) {
		this.previousTransaction = previousTransaction;
	}

	public String getNextTransaction() {
		return nextTransaction;
	}

	public void setNextTransaction(String nextTransaction) {
		this.nextTransaction = nextTransaction;
	}*/

	
	
	
	

}
