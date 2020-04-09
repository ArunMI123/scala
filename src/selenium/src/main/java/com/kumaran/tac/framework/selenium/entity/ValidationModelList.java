package com.kumaran.tac.framework.selenium.entity;

import java.util.ArrayList;

public class ValidationModelList {
	private int stepId;
	private ArrayList attributes;
	private String transactionId;
	private ArrayList<ValidationModel> validations;

	public ArrayList getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList attributes) {
		this.attributes = attributes;
	}

	public int getStepId() {
		return stepId;
	}

	public void setStepId(int stepId) {
		this.stepId = stepId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public ArrayList<ValidationModel> getValidations() {
		return validations;
	}

	public void setValidations(ArrayList<ValidationModel> validations) {
		this.validations = validations;
	}
}
