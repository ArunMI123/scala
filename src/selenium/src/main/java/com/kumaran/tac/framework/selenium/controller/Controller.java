package com.kumaran.tac.framework.selenium.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.kumaran.tac.framework.selenium.frameworklayer.BrowserControls;
import com.kumaran.tac.framework.selenium.frameworklayer.PageObjectHandler;
import com.kumaran.tac.framework.selenium.frameworklayer.Utility;
import com.kumaran.tac.framework.selenium.frameworklayer.Validations;
import com.kumaran.tac.framework.selenium.entity.Attributes;
import com.kumaran.tac.framework.selenium.entity.Entry;
import com.kumaran.tac.framework.selenium.entity.ExecuteObj;
import com.kumaran.tac.framework.selenium.entity.FieldDetails;
import com.kumaran.tac.framework.selenium.entity.Transactions;
import com.kumaran.tac.framework.selenium.entity.ValidationModel;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Controller {

	public static HashMap<Integer, ArrayList<FieldDetails>> frameMap;
	public static HashMap<String, String> AttributesMap = new HashMap<String, String>();
	public static LinkedHashMap<String, String> Variablesvalue = new LinkedHashMap<String, String>();
	public static LinkedHashMap<String, String> Projectvalidations = new LinkedHashMap<String, String>();
	public static int frame_attr_id = 1;
	public static JSONArray valArr = null;
	public static JSONObject validations = null;
	public static ValidationModel[] entryValidations = null;
	public static ValidationModel[] eoaValidations = null;
	public static ValidationModel[] beforeValidations = null;
	public static ValidationModel[] afterValidations = null;
	public static ValidationModel[] afterReEntryValidations = null;
	public static ArrayList<Integer> beforePositionIds = null;
	public static ArrayList<Integer> afterPositionIds = null;
	public static int multiElement = 0;
	public static ExecuteObj oRObject = null;
	public static HashMap<String,Object> exePlan = null;
	public static Attributes AttributesValue = null;
	public static LinkedHashMap<String, Integer> orPositions = new LinkedHashMap<>();
	public static String transactionId = "";
	public static String transactionStepId = "";
	public static String transactionName = "";
	public static String transactionColumnName = "";
	public static String agentUrl = "";
	public static int mrbpid = 0;
	public static int custom_ind = 0;
	public static String starttime;
	public static String completedTime;
	public static String mrbgridtype;
	public static String ValidationPosition = null;
	public static HashMap<String, Object> testRunDetails;
	public static int ValidationStatusInd;
	public static int positionAttributeId;
	public static boolean escapeWaitTime;
	public static Integer attributeMaxWait=0;
	public static String browserControl = null;
//	public static Boolean ignoreInd;
	public static Logger mainLogger = Logger.getLogger(Controller.class);
	public static int timeCount = 0;

	public static void main(String args[]) throws Exception {

//		 String testRunId = args[0];

		/* testRunId = "1334"; */
		String testRunId = null;

		try {
			Class<?> clas2 = Class.forName("Custom_Validation");
			if (clas2 != null) {
				mainLogger.info("CLASS ADDED Custom_Validation");

			}
		} catch (Exception e) {
			mainLogger.info("Call utility");

		}catch (NoClassDefFoundError e){
			mainLogger.info("Cls def err");
		}

		try {
			Class<?> clas = Class.forName("Custom_Attributes");
			if (clas != null) {
				mainLogger.info("CLASS ADDED ATTRIBUTES");

			}
		}catch(ClassNotFoundException clsn){
			mainLogger.info("Call utility clsn");
		}
		catch (Exception e) {

			mainLogger.info("Call utility");
		} 
		 
//		 *******************Selenium Debugging code to use - below block of code************
/*		Properties p = new Properties();
		InputStream fin = new FileInputStream(new File("D:\\Selenium\\config.properties"));
		p.load(fin);
		agentUrl = p.getProperty("agentUrl");
		mainLogger.info("agentUrl : " + agentUrl);*/
// ********************Selenium Debugging code to use - End of block**************

// ********************Selenium Runnable jar code to use - below block of code************
		  Properties p = new Properties(); String externalFileName =
		  System.getProperty("config.location"); InputStream fin = new
		  FileInputStream( new File(externalFileName)); p.load(fin); agentUrl =
		  p.getProperty("agentUrl");
		  mainLogger.info(agentUrl);
// ********************Selenium Runnable jar code to use - End of block code************

		Utility.AplliactionType = "web";
		
		HashMap<String, Object> savedAgentDetail = null;
		ObjectMapper mapper = new ObjectMapper();
		while (true) {
			try {
			String nextCommand = getNextCommandFromAgent();
			if (nextCommand != null) {
				if (!nextCommand.contains("MSG:")) {
			savedAgentDetail = mapper.readValue(nextCommand,new TypeReference<HashMap<String, Object>>() {});
			if (savedAgentDetail.containsKey("exit")) {
				break;
			}
			if (savedAgentDetail.containsKey("abort")) {
				sendAbortConfirmation(testRunId);
				if(BrowserControls.driver != null){					
					BrowserControls.closeBrowser(oRObject.getBrowser());
				}
				break;
			}
			if(savedAgentDetail.containsKey("wait")){
				continue;
			}
			exePlan = mapper.readValue( mapper.writeValueAsString(savedAgentDetail.get("executionPlan")),new TypeReference<HashMap<String, Object>>() {});
			testRunId=exePlan.get("testRunExecutionId").toString();
			browserControl = exePlan.get("browserControl") != null ? exePlan.get("browserControl").toString() : null ;
			oRObject = mapper.convertValue(savedAgentDetail.get("ORjson"), ExecuteObj.class);
			escapeWaitTime=oRObject.getescapeWaittime();
			Utility.AplliactionUrl = oRObject.getUrl();
			if(BrowserControls.driver == null){				
				BrowserControls.OpenBrowser(oRObject.getBrowser());
			}
			oRObject.getTransactions().forEach(transaction -> {
				List<Attributes> entryList = transaction.getEntry();
				List<Attributes> reEntryList = transaction.getReEntry();

				entryList.forEach(entry -> {
					mainLogger.info("Entry Data - Navigation");
					mainLogger.info(entry.toString());
				});
				reEntryList.forEach(reEntry -> {
					mainLogger.info("ReEntry Data - Navigation");
					mainLogger.info(reEntry.toString());
				});
				mainLogger.info(reEntryList.toString());
				mainLogger.info(entryList.toString());
			});
			JSONParser parser = new JSONParser();
			JSONObject nextCommandJSON = (JSONObject) parser.parse(mapper.writeValueAsString(savedAgentDetail.get("teststepData")));
			mainLogger.info("---nextCommand---" + nextCommand);
			if(nextCommandJSON==null) {
				break;
			}else {
				executeTest(nextCommandJSON);
				Verification.DvMapPassStatus = new HashMap<String, Object>();
			}
			}else if(nextCommand.contains("MSG:") && nextCommand.contains("testRunCompleted")){
				if(BrowserControls.driver != null){					
					BrowserControls.closeBrowser(oRObject.getBrowser());
				}
				break;
			}else if(nextCommand.contains("MSG:") && nextCommand.contains("testcaseEnd")){
				if(browserControl != null && browserControl.equals("tc")){
					BrowserControls.closeBrowser(oRObject.getBrowser());
					BrowserControls.driver = null;
				}
			}
			}
			} catch (Exception exception) {
				exception.printStackTrace();
				break;
			}
		}
		endRun();
	}

private static void sendAbortConfirmation(String testRunId) {
	try{			
		String url = agentUrl + "abortConfirmation/" + testRunId;
		System.out.println(url);
		String restResponse = RestUtil.RestClientGet(url);
		
	}catch(Exception e){
		e.getStackTrace();
	}
	}

/*	private static String getObjectRepositoryJson(String testRunId) throws Exception {

		String url = agentUrl + "getTestRunDetails/" + testRunId;
		System.out.println(url);
		String restResponse = RestUtil.RestClientGet(url);
		return restResponse;
	}*/

	private static String getNextCommandFromAgent() throws Exception {
		try{			
			String url = agentUrl + "getNextStep/" + "selenium";
			String restResponse = RestUtil.RestClientGet(url);
			return restResponse;
		}catch(Exception e){
			e.getStackTrace();
			return null;
		}
	}
	private static String endRun() throws Exception {
		try{			
			String url = agentUrl + "getNextStep/" + "selenium" +"-testRunComplete";
			String restResponse = RestUtil.RestClientGet(url);
			return restResponse;
		}catch(Exception e){
			e.getStackTrace();
			return null;
		}
	}

	public static String sendTestRunStatus(Map<String, Object> testDataJson) throws Exception {

		String url = agentUrl + "saveTestStepResult";
		String testStepResultJson = new ObjectMapper().writeValueAsString(testDataJson);
		mainLogger.info("testStepResultJson:" + testStepResultJson);
		String restResponse = RestUtil.RestClientPost(url, testStepResultJson);
		return restResponse;
	}

	/*******************************
	 * Agent Rest Call for Actual value and validation
	 **********************/

	public static String validationActualStatus(Map<String, Object> testDataJson) throws Exception {

		String url = agentUrl + "agentValidation";
		String agentValidationstatus = new ObjectMapper().writeValueAsString(testDataJson);
		mainLogger.info("agentValidationstatus: " + agentValidationstatus);
		String restResponse = RestUtil.RestClientPost(url, agentValidationstatus);
		System.out.println(restResponse);
		return restResponse;
	}

	public static void executeTest(JSONObject testDataJson) throws Exception {

		// Integer g = 9;
		testRunDetails = new HashMap<String, Object>();
		testRunDetails.put("testRunStepId", testDataJson.get("testRunStepId").toString());
		// ***********************Custom method call *******************
		try {
			Class<?> ca = Class.forName("Custom_Attributes");
			if (ca != null) {
				mainLogger.info("class added");
				Object objca = ca.newInstance();
				Method cam = ca.getMethod("startTime");
				if (cam != null) {
					starttime = (String) cam.invoke(objca);
				}
			}
		} catch (ClassNotFoundException e) {
			mainLogger.info("Call utility.");
			starttime = Utility.startTime();
		}catch (NoClassDefFoundError e){
			mainLogger.info("Cls def err");
		}

		testRunDetails.put("startTime", starttime);
		mainLogger.info("startTime" + starttime);
		try {
			readTestDataFromMaptoAppliaction(testDataJson);
			testRunDetails.put("status", "PASS");
			
			try {
				Class<?> ca = Class.forName("Custom_Attributes");
				if (ca != null) {
					mainLogger.info("class added");
					Object objca = ca.newInstance();
					Method cam = ca.getMethod("completedTime");
					if (cam != null) {
						completedTime = (String) cam.invoke(objca);
					}
				}
			} catch (Exception e1) {
				mainLogger.info("Call utility.");
				completedTime = Utility.completedTime();
			}catch (NoClassDefFoundError e){
				mainLogger.info("Cls def err");
			}

			testRunDetails.put("completedTime", completedTime);
			testRunDetails.put("testRunExecutionId", exePlan.get("testRunExecutionId"));
			mainLogger.info("DataVerification Value Now : " + Verification.DataVerification);
			if (Verification.DataVerification != null) {
				if (Verification.DvMapPassStatus.size() > 0) {
					testRunDetails.put("VerificationStatus",
							Verification.DvMapPassStatus.get("VerificationStatus").toString());
				} else {
					testRunDetails.put("VerificationStatus", null);
				}
			}
			sendTestRunStatus(testRunDetails);
		}
		catch (Exception e) {
			if(e.getMessage().equalsIgnoreCase("Test Fail")){
				System.out.println("Test failed");
			}else{				
				mainLogger.fatal("Exception :", e);
				e.printStackTrace();
			}
			try {
				Class<?> ca = Class.forName("Custom_Attributes");
				if (ca != null) {
					mainLogger.info("class added");
					Object objca = ca.newInstance();
					Method cam = ca.getMethod("completedTime");
					if (cam != null) {
						completedTime = (String) cam.invoke(objca);
					}
				}
			} catch (Exception e2) {
				mainLogger.info("Call utility.");
				completedTime = Utility.completedTime();
			}catch (NoClassDefFoundError e1){
				mainLogger.info("Cls def err");
			}
			mainLogger.info("DataVerification Value Now : " + Verification.DataVerification);
			if (Verification.DataVerification != null) {
				if (Verification.DvMapPassStatus.size() > 0) {
					testRunDetails.put("VerificationStatus",
							Verification.DvMapPassStatus.get("VerificationStatus").toString());
				} else {
					testRunDetails.put("VerificationStatus", null);
				}
			}
			testRunDetails.put("status", "FAIL");
			testRunDetails.put("completedTime", completedTime);
			testRunDetails.put("testRunExecutionId", exePlan.get("testRunExecutionId"));
			testRunDetails.put("failureReason", PageObjectHandler.failureReason);
			sendTestRunStatus(testRunDetails);
		}

	}

	public static void readTestDataFromMaptoAppliaction(JSONObject testDataJson) throws Exception {

		
		ValidationStatusInd = 1;

		frameMap = new HashMap<Integer, ArrayList<FieldDetails>>();
		ObjectMapper mapper = new ObjectMapper();
		Set testDatakeys = testDataJson.keySet();
		transactionStepId = testDataJson.get("stepId").toString();
		transactionId = testDataJson.get("transactionId").toString();
		transactionColumnName = "";
		@SuppressWarnings("unchecked")

		Iterator<String> keysItr = testDatakeys.iterator();
		Utility.variableDictionary(testDataJson);
		try {
			Class<?> cv = Class.forName("Custom_Validation");
			if (cv != null) {
				
				Object objcv = cv.newInstance();
				Method cvm = cv.getMethod("CurrentPageValidation");
				if (cvm != null) {
					cvm.invoke(objcv);
				}
			}
		} catch (ClassNotFoundException e) {
			Validations.CurrentPageValidation();
		}

		ArrayList readMrbFromMap = new ArrayList<>();
		ArrayList<Transactions> transactionList = oRObject.getTransactions();

		Validations.CurrentPageValidation();

		JSONArray arrtibuteJson = (JSONArray) testDataJson.get("attributes");
		int testDataSize = arrtibuteJson.size();

		Transactionloop: for (int i = 0; i < transactionList.size(); i++) {

			Transactions transaction = transactionList.get(i);
			int orTransctionId = transaction.getTransactionId();

			/************** Entry code for the Each transaction ***************/

			if (orTransctionId == Integer.valueOf(transactionId)) {

				List<Attributes> entry = transaction.getEntry();
				mainLogger.info("entry.size()" + entry.size());
				if (entry.size() > 0) {
					mainLogger.info("entry.size()" + entry.size() + "" + entry.get(0).getId());
					for (int j = 0; j < entry.size(); j++) {
						mainLogger.info("Post Validation  Entry");
						Attributes entryArt = entry.get(j);

						/* navigationentry(testDataJson,i); */

						PageObjectHandler.datafeed("", entryArt.getFieldDetails(), entryArt.getType(),
								entryArt.getWindowOrFrame());
					}
				}

				for (int a = 0; a < testDataSize; a++) {
					JSONObject singleTestDataJsonObject = (JSONObject) arrtibuteJson.get(a);

					try {
						validations = (JSONObject) singleTestDataJsonObject.get("validations");
					} catch (NullPointerException e) {
						validations = null;
					} 

					transactionName = transaction.getTransactionName();
					boolean isParent = false;
					if(validations!=null){
						try{
							entryValidations = mapper.readValue(validations.get("entry").toString(),ValidationModel[].class);
							ValidationPosition = "entry";
							Validations.ValidationStart(entryValidations, oRObject, i);
							ValidationPosition = null;
							if(ValidationStatusInd == 0){								
								throw new Exception("Test Fail");
							}
						}catch(NullPointerException e){							
							entryValidations = null;	
						}
					}
					
					if(validations!=null){
						try{
							beforeValidations = mapper.readValue(validations.get("before").toString(),ValidationModel[].class);
							beforePositionIds = new ArrayList<Integer>();
							for(ValidationModel element:beforeValidations){
								beforePositionIds.add(element.getPositionAttributeId());
							}
							
						}catch (Exception e){
							beforeValidations = null;
						}
						try{
							afterValidations = mapper.readValue(validations.get("after").toString(),ValidationModel[].class);
							afterPositionIds = new ArrayList<Integer>();
							for(ValidationModel element:afterValidations){
								afterPositionIds.add(element.getPositionAttributeId());
							}
							
						}catch (Exception e){
							afterValidations = null;
						}
					}
					ArrayList<Attributes> attributesList = transaction.getAttributes();
					int size = attributesList.size();
					mainLogger.info(size);
					if (ValidationStatusInd == 1) {
						for (int j = 0; j < attributesList.size(); j++) {
							Attributes attributes = attributesList.get(j);
//							Controller.ignoreInd=arrtibutes.getIgnore_ind();
							if(attributes.getRead_only()!=null){
								if(attributes.getRead_only() == 1)
								continue;
							}
							int attributeId = attributes.getId();
							int parentAttributeId = attributes.getParentAttrId();
							try{
								WebElement element = PageObjectHandler.findobject(attributes.getFieldDetails());
								if(attributes.getAttributeWait()!=null){									
									switch(attributes.getAttributeWait()){
									case "visible":
										timeCount = 0;
										do{											
											if(element.isDisplayed()){												
												System.out.println("Visible");
												break;
											}else{
												Thread.sleep(1000);
												timeCount++;
											}
										}while(timeCount< attributeMaxWait);
										break;
									case "editable":
										timeCount = 0;
										do{											
											if(element.isDisplayed()){												
												System.out.println("Editable");
												break;
											}else{
												Thread.sleep(1000);
												timeCount++;
											}
										}while(timeCount< attributeMaxWait);
										break;
									default:
										break;
									}
								}
							}catch(NoSuchElementException e){
								if(attributes.getIgnore_ind() == 0){
									continue;
								}else{
									throw new Exception("Test Fail");
								}
							}
							if(beforeValidations!=null){								
								if(beforePositionIds.contains(attributeId)){
									positionAttributeId= attributeId;
									ValidationPosition = "before";
									Validations.ValidationStart(beforeValidations, oRObject, i);
									ValidationPosition = null;			
									positionAttributeId = 0;
									if(ValidationStatusInd == 0){								
										throw new Exception("Test Fail");
									}
								}
							}
							String mrbgrid = attributes.getType();

							if (mrbgrid.equalsIgnoreCase("frame")) {

								frameMap.put(Integer.valueOf(attributeId), attributes.getFieldDetails());

							}
							if (parentAttributeId == 0 && !mrbgrid.equalsIgnoreCase("grid") && !isParent
									&& !mrbgrid.equalsIgnoreCase("frame") && !mrbgrid.equalsIgnoreCase("window")) {
								Utility.TransactionDataHandel(attributes, singleTestDataJsonObject);

							}
							if (parentAttributeId == 0 && attributes.getType().equalsIgnoreCase("grid")
									|| attributes.getType().equalsIgnoreCase("window")
									|| attributes.getType().equalsIgnoreCase("frame") && !isParent) {
								attributeId =attributes.getId();
								readMrbFromMap = (ArrayList) singleTestDataJsonObject.get(String.valueOf(attributeId));
								mainLogger.info(readMrbFromMap.toString());
								isParent = true;
								ArrayList<FieldDetails> mrbFieldDeatils = attributes.getFieldDetails();

								for (int mrbcount = 0; mrbcount < readMrbFromMap.size(); mrbcount++) {
									JSONObject readListOfMrbs = (JSONObject) readMrbFromMap.get(mrbcount);
									Utility.tableLookupRowData = "";
									Utility.columnNames = new ArrayList<>();

									for (int l = j; l < attributesList.size(); l++) {

										Attributes mrbAttributes = attributesList.get(l);
										int id = mrbAttributes.getId();
										String smrbgrid = mrbAttributes.getType();
										mrbpid = mrbAttributes.getParentAttrId();
										String SubMrebssnew = mrbAttributes.getType();
										mrbgridtype = attributes.getGridType();
										if (mrbpid == attributeId && mrbgrid.equalsIgnoreCase("window")) {
											Utility.mrbTransactionDataHandel(mrbAttributes, readListOfMrbs,
													mrbFieldDeatils);
										}
										if (mrbpid == attributeId && mrbgrid.equalsIgnoreCase("frame")) {

											Utility.mrbTransactionDataHandel(mrbAttributes, readListOfMrbs,
													mrbFieldDeatils);
										}
										if (mrbpid == attributeId && mrbgrid.equalsIgnoreCase("grid")
												&& !SubMrebssnew.equalsIgnoreCase("grid")) {

											Utility.mrbTransactionDataHandel(mrbAttributes, readListOfMrbs,
													mrbFieldDeatils);

										} else if (mrbpid == attributeId && SubMrebssnew.equalsIgnoreCase("grid")) {
											int subsubreb = mrbAttributes.getId();

											ArrayList<FieldDetails> subMrbFieldDeatils = mrbAttributes
													.getFieldDetails();

											ArrayList readSubMrb = (ArrayList) readListOfMrbs
													.get(String.valueOf(subsubreb));
											mainLogger.info("sub mrabs data size" + readSubMrb);
											for (int subMrbCount = 0; subMrbCount < readSubMrb.size(); subMrbCount++) {
												JSONObject getSubMrbData = (JSONObject) readSubMrb.get(subMrbCount);
												Utility.tableLookupRowData = "";
												Utility.columnNames = new ArrayList<>();
												for (int m = l; m < attributesList.size(); m++) {
													Transactions subMebTransactions = oRObject.getTransactions().get(i);
													Attributes subMebAttributes = subMebTransactions.getAttributes()
															.get(m);
													int subPid = subMebAttributes.getParentAttrId();
													String SubMrebssnew2 = subMebAttributes.getType();
													if (subPid == subsubreb
															&& !SubMrebssnew2.equalsIgnoreCase("grid")) {
														Utility.mrbTransactionDataHandel(subMebAttributes,
																getSubMrbData, subMrbFieldDeatils);
													}
												}
											}
										}
									}
								}

							}

							if (isParent) {

								if (!oRObject.getTransactions().get(i).getAttributes().get(j).getType()
										.equalsIgnoreCase("grid")) {
									attributeId = 0;
									isParent = false;
									continue;
								}
							}
							if(afterValidations!=null){								
								if(afterPositionIds.contains(attributeId)){
									positionAttributeId= attributeId;
									ValidationPosition = "after";
									Validations.ValidationStart(afterValidations, oRObject, i);
									ValidationPosition = null;			
									positionAttributeId = 0;
									if(ValidationStatusInd == 0){								
										throw new Exception("Test Fail");
									}
								}
							}
						}
						if (validations != null) {
							try{
								eoaValidations = mapper.readValue(validations.get("endofattribute").toString(),ValidationModel[].class);
								ValidationPosition = "endofattribute";
								Validations.ValidationStart(eoaValidations, oRObject, i);
								ValidationPosition = null;
								if(ValidationStatusInd == 0){								
									throw new Exception("Test Fail");
								}
							}catch(NullPointerException e){							
								eoaValidations = null;	
							}
							
						}
						try {

							mainLogger.info("Post Validation Re Entry");
							List<Attributes> reentry = transaction.getReEntry();
							mainLogger.info("reentry.size()" + reentry.size());
							if (reentry.size() > 0) {

								

								for (int j = 0; j < reentry.size(); j++) {
									mainLogger.info("Post Validation Re Entry");
									Attributes ReentryArt = reentry.get(j);

									mainLogger.info("Post Validation Re Entry");

									PageObjectHandler.datafeed("", ReentryArt.getFieldDetails(), ReentryArt.getType(),
											ReentryArt.getWindowOrFrame());
								}
								Verification.DataVerificationFromMaptoAppliaction(testDataJson, i);
								Verification.DataVerification = "";
							}

						} catch (Exception e) {
							List<Entry> reentry = null;

						}

						if (validations != null) {
							try{
								afterReEntryValidations = mapper.readValue(validations.get("after reentry").toString(),ValidationModel[].class);
								ValidationPosition = "after reentry";
								Validations.ValidationStart(afterReEntryValidations, oRObject, i);
								ValidationPosition = null;
								if(ValidationStatusInd == 0){								
									throw new Exception("Test Fail");
								}
							}catch (NullPointerException e){
									afterReEntryValidations = null;
							}
							
						}

						/********************
						 * Below Entry code copied to make the Entry from next
						 * transaction
						 ***********************/
						

						if (a == testDataSize - 1) {
							break Transactionloop;
						}
					}

				}
			}
		}

	}

	public static void getAllOrPosition(int transctionId) {
		orPositions = new LinkedHashMap<>();
		for (int i = 0; i < oRObject.getTransactions().size(); i++) {

			if (oRObject.getTransactions().get(i).getTransactionId() == transctionId) {
				for (int j = 0; j < oRObject.getTransactions().get(i).getAttributes().size(); j++) {

					int id = oRObject.getTransactions().get(i).getAttributes().get(j).getId();
					orPositions.put(String.valueOf(id), j);

				}
			}
		}
	}

	
}
