package com.kumaran.tac.framework.selenium.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import com.kumaran.tac.framework.selenium.entity.Attributes;
import com.kumaran.tac.framework.selenium.entity.FieldDetails;
import com.kumaran.tac.framework.selenium.entity.Transactions;
import com.kumaran.tac.framework.selenium.frameworklayer.BrowserControls;
import com.kumaran.tac.framework.selenium.frameworklayer.PageObjectHandler;
import com.kumaran.tac.framework.selenium.frameworklayer.Utility;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Verification extends Controller {

	
   public static String DataVerification ="";
private static int a;
public static HashMap<Integer, String> DvStatus;
public static HashMap<String, Object> DvMapFailStatus;
public static  HashMap<String, Object> DvMapPassStatus = new HashMap<String, Object>();
public static Logger mainLogger = Logger.getLogger(Verification.class);


	public static void DataVerificationFromMaptoAppliaction(JSONObject testDataJson,int i) throws Exception {
		
		DataVerification = "Verification";	
		
		frameMap = new HashMap<Integer, ArrayList<FieldDetails>>();
		ObjectMapper mapper = new ObjectMapper();
		Set testDatakeys = testDataJson.keySet();
		transactionStepId = testDataJson.get("stepId").toString();
		mainLogger.info("transactionStepId :" + transactionStepId);
		transactionId = testDataJson.get("transactionId").toString();
		mainLogger.info("transactionId :" + transactionId);
		transactionColumnName = "";
		@SuppressWarnings("unchecked")
		Iterator<String> keysItr = testDatakeys.iterator();
		Utility.variableDictionary(testDataJson);
		
		ArrayList readMrbFromMap = new ArrayList<>();
		ArrayList<Transactions> transactionList = oRObject.getTransactions();

		JSONArray arrtibuteJson = (JSONArray) testDataJson.get("attributes");
		int testDataSize = arrtibuteJson.size();
		

			Transactions transaction = transactionList.get(i);
			int orTransctionId = transaction.getTransactionId();
mainLogger.info("orTransctionId : "+orTransctionId);
			
			if (orTransctionId == Integer.valueOf(transactionId)) {
				
				
				
				
			for(int a=0; a < testDataSize; a++) {
				JSONObject singleTestDataJsonObject = (JSONObject) arrtibuteJson.get(a);
		
				transactionName = transaction.getTransactionName();
				boolean isParent = false;
				ArrayList<Attributes> attributesList = transaction.getAttributes();
				int size = attributesList.size();
				mainLogger.info(size);
				
				for (int j = 0; j < attributesList.size(); j++) {
					Attributes arrtibutes = attributesList.get(j);
					int attributeId = arrtibutes.getId();
					int parentAttributeId = arrtibutes.getParentAttrId();
					// String mrbgrid = arrtibutes.getType();
					String mrbgrid = arrtibutes.getType();
					
					if (mrbgrid.equalsIgnoreCase("frame")) {

						frameMap.put(Integer.valueOf(attributeId), arrtibutes.getFieldDetails());

					}
					if (parentAttributeId == 0 && !mrbgrid.equalsIgnoreCase("grid") && !isParent
							&& !mrbgrid.equalsIgnoreCase("frame") && !mrbgrid.equalsIgnoreCase("window")) {
						
						//TestData as array
							Utility.TransactionDataHandel(arrtibutes, singleTestDataJsonObject);


					}
					if (parentAttributeId == 0 && arrtibutes.getType().equalsIgnoreCase("grid")
							|| arrtibutes.getType().equalsIgnoreCase("window")
							|| arrtibutes.getType().equalsIgnoreCase("frame") && !isParent) {
						attributeId = arrtibutes.getId();
						readMrbFromMap = (ArrayList) singleTestDataJsonObject.get(String.valueOf(attributeId));
						mainLogger.info(readMrbFromMap.toString());
						isParent = true;
						ArrayList<FieldDetails> mrbFieldDeatils = arrtibutes.getFieldDetails();

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
								if (mrbpid == attributeId && mrbgrid.equalsIgnoreCase("window")) {

									Utility.mrbTransactionDataHandel(mrbAttributes, readListOfMrbs, mrbFieldDeatils);
								}

								if (mrbpid == attributeId && mrbgrid.equalsIgnoreCase("frame")) {

									Utility.mrbTransactionDataHandel(mrbAttributes, readListOfMrbs, mrbFieldDeatils);
								}

								if (mrbpid == attributeId && mrbgrid.equalsIgnoreCase("grid")
										&& !SubMrebssnew.equalsIgnoreCase("grid")) {

									Utility.mrbTransactionDataHandel(mrbAttributes, readListOfMrbs, mrbFieldDeatils);

								} else if (mrbpid == attributeId && SubMrebssnew.equalsIgnoreCase("grid")) {
									int subsubreb = mrbAttributes.getId();

									ArrayList<FieldDetails> subMrbFieldDeatils = mrbAttributes.getFieldDetails();

									ArrayList readSubMrb = (ArrayList) readListOfMrbs.get(String.valueOf(subsubreb));
									mainLogger.info("sub mrabs data size" + readSubMrb);
									for (int subMrbCount = 0; subMrbCount < readSubMrb.size(); subMrbCount++) {
										JSONObject getSubMrbData = (JSONObject) readSubMrb.get(subMrbCount);
										Utility.tableLookupRowData = "";
										Utility.columnNames = new ArrayList<>();
										for (int m = l; m < attributesList.size(); m++) {
											Transactions subMebTransactions = oRObject.getTransactions().get(1);
											Attributes subMebAttributes = subMebTransactions.getAttributes().get(m);
											int subPid = subMebAttributes.getParentAttrId();
											String SubMrebssnew2 = subMebAttributes.getType();
											if (subPid == subsubreb && !SubMrebssnew2.equalsIgnoreCase("grid")) {
												Utility.mrbTransactionDataHandel(subMebAttributes, getSubMrbData,
														subMrbFieldDeatils);
											}
										}
									}
								}
							}
						}

					}

					if (isParent) {

						if (!oRObject.getTransactions().get(1).getAttributes().get(j).getType()
								.equalsIgnoreCase("grid")) {
							attributeId = 0;
							isParent = false;
							continue;
						}
					}

				}
				
				
				}
				}
			
				// close loop here
			}

	



	public static void dataVerificationfeed(String testdata, ArrayList<FieldDetails> fieldIdentifiers, String action,
			String windowOrFrame) throws Exception {

		String windowshandel = null;
		boolean frameFalg = false;

		if (windowOrFrame != null) {

			if (windowOrFrame.startsWith("window")) {

				windowshandel = BrowserControls.WindowsHandeling();
			} else if (windowOrFrame.contains("frame")) {
				frameFalg = true;
				ArrayList<FieldDetails> fieldDetail = Controller.frameMap.get(Controller.frame_attr_id);

				// -------------------------Need to Rewart code
				PageObjectHandler.frameHandle(fieldDetail);

			}
		}
		if (!action.equalsIgnoreCase("alert")) {

			try {
				WebElement ele = PageObjectHandler.findobject(fieldIdentifiers);
				fieldActionVerification(action, testdata);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {

			if (testdata.contains("|")) {

				String[] alertSplit = testdata.split("\\|");
				PageObjectHandler.applicationPopup(alertSplit[0], alertSplit[1]);
			} else {
				PageObjectHandler.applicationPopup(testdata, "");
			}

		}
		if (windowshandel != null) {
			BrowserControls.driver.switchTo().window(windowshandel);
		}
		if (frameFalg) {
			BrowserControls.driver.switchTo().defaultContent();
		}

	}

	public static void fieldActionVerification(String action, String testData) throws Exception {
		
		DvStatus = new HashMap<Integer, String>();
		DvMapFailStatus = new HashMap<String, Object>();
	
		String VStatus="";
		BrowserControls.waitPageloadComplete();
		mainLogger.info("Test Data Verification");
		switch (action.toUpperCase()) {
		case "TEXTBOX":
			BrowserControls.highlightElements(PageObjectHandler.TrasnactionElement, "Verification");
			PageObjectHandler.TrasnactionElement.getAttribute("Value");
			mainLogger.info("Textbox data");
			mainLogger.info(PageObjectHandler.TrasnactionElement.getAttribute("Value") +"   Textbox   "+testData);
		
			String ActualTextValue = PageObjectHandler.TrasnactionElement.getAttribute("Value");
			if(ActualTextValue.equals(testData)){
				mainLogger.info("PASS");
				DvStatus.put(Utility.attributeId, "PASS");
				}
			else{
				mainLogger.info("FAIL");

				DvStatus.put(Utility.attributeId, "FAIL");
			}
			break;
		case "RADIO":
			BrowserControls.highlightElements(PageObjectHandler.TrasnactionElement, "Verification");
			PageObjectHandler.TrasnactionElement.isSelected();
			mainLogger.info("Radio data");
			mainLogger.info(PageObjectHandler.TrasnactionElement.isSelected() +"   Radio   "+testData);
			boolean ActualRadioValue = PageObjectHandler.TrasnactionElement.isSelected();
			if(ActualRadioValue=true){
				mainLogger.info("PASS");
				DvStatus.put(Utility.attributeId, "PASS");
				}
			else{
				mainLogger.info("FAIL");
				DvStatus.put(Utility.attributeId, "FAIL");
							}
		case "CHECKBOX":
			BrowserControls.highlightElements(PageObjectHandler.TrasnactionElement, "Verification");
			PageObjectHandler.TrasnactionElement.isSelected();
			mainLogger.info("CHECKBOX data");
			mainLogger.info(PageObjectHandler.TrasnactionElement.isSelected() +"   CheckBox   "+testData);
			boolean ActualCheckBoxValue = PageObjectHandler.TrasnactionElement.isSelected();
			if(ActualCheckBoxValue=true){
				mainLogger.info("PASS");
				DvStatus.put(Utility.attributeId, "PASS");
				}
			else{
				mainLogger.info("FAIL");
				DvStatus.put(Utility.attributeId, "FAIL");
				}
			break;
		case "DROPDOWN":
			BrowserControls.highlightElements(PageObjectHandler.TrasnactionElement, "Verification");
			mainLogger.info("Test Data Verification - DropDown");
				Select dropdown = new Select(PageObjectHandler.TrasnactionElement);
				mainLogger.info("TrasnactionElement" + PageObjectHandler.TrasnactionElement);
				mainLogger.info("Get Drop Down Option");
				dropdown.getFirstSelectedOption();
				mainLogger.info(dropdown.getFirstSelectedOption().getText()+"   fasssssssss   "+testData);
				String ActualDropDownValue = dropdown.getFirstSelectedOption().getText();
				if(ActualDropDownValue.equals(testData)){
					mainLogger.info("PASS");
					DvStatus.put(Utility.attributeId, "PASS");
					}
				else{
					mainLogger.info("FAIL");
					DvStatus.put(Utility.attributeId, "FAIL");
				}
				
			break;

		default:
			break;
		}
		 for(Entry<Integer, String> m:DvStatus.entrySet()){ 
		
	           mainLogger.info(m.getKey()+" "+m.getValue());
	      	 if(m.getValue().equalsIgnoreCase("FAIL")){
	      		
	      		mainLogger.info("FAIL");
	      		DvMapPassStatus.put("VerificationStatus", "FAIL");
	      		break;
	      	 }   else if(m.getValue().equalsIgnoreCase("PASS")){
	      		mainLogger.info("PASS");
	      		DvMapPassStatus.put("VerificationStatus", "PASS");
	      	 }
	          }  
}
}

