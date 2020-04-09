package com.kumaran.tac.framework.selenium.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class ExecuteObj {
	
	private String browser;
	private String url;
	private ArrayList<Transactions> transactions;
	//private  JSONObject projectValidation;
	private HashMap projectValidation;
	private boolean escapeWaittime;
	
	public ArrayList<Transactions> getTransactions() {
		return transactions;
	}
	public void setTransactions(ArrayList<Transactions> transactions) {
		this.transactions = transactions;
	}
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	public String getUrl() {
		
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public HashMap getProjectValidation() {
		return projectValidation;
	}
	public void setProjectValidation(HashMap projectValidation) {
		this.projectValidation = projectValidation;
	}
	public boolean getescapeWaittime() {
		return escapeWaittime;
	}
	
	
	

	
	
	

}
