package com.kumaran.tac.framework.selenium.entity;

import java.util.ArrayList;

public class Transaction {

	private int TransactionId;
	private int projectId;
	private String transactionName;
	private String transactionDesc;
	private String previousTransaction;
	private String nextTransaction;
	private ArrayList<Attributes> Attributes;
	public int getTransactionId() {
		return TransactionId;
	}

	public void setTransactionId(int transactionId) {
		TransactionId = transactionId;
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

	public String getTransactionDesc() {
		return transactionDesc;
	}

	public void setTransactionDesc(String transactionDesc) {
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
	}

	public ArrayList<Attributes> getAttributes() {
		return Attributes;
	}

	public void setAttributes(ArrayList<Attributes> attributes) {
		Attributes = attributes;
	}

}
