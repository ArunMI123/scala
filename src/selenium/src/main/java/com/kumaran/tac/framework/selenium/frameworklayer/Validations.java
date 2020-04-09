package com.kumaran.tac.framework.selenium.frameworklayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.kumaran.tac.framework.selenium.controller.Controller;
import com.kumaran.tac.framework.selenium.entity.Attributes;
import com.kumaran.tac.framework.selenium.entity.ExecuteObj;
import com.kumaran.tac.framework.selenium.entity.FieldDetails;
import com.kumaran.tac.framework.selenium.entity.Transactions;
import com.kumaran.tac.framework.selenium.entity.ValidationModel;


public class Validations  {
	public static String control_name = "";
	public static String pre_post = "";
	public static String type="";
	public static String expectedValue = "";
	public static String boundary_start = "";
	public static String boundary_end = "";
	public static String row_number = "";
	public static String content_in_row = "";
	public static String column_heading = "";
	public static String unique_rowdata = "";
	public static String variableKey = null;
	public static String StatusofValidations;
	public static int fieldIgnoreInd;
	static Document doc1 = null;
	static String Htmltext = null;
	public static HashMap<String, Object> validatiionDetails = new HashMap<String, Object>();
	public static int validationId = 0;
	public static ArrayList<HashMap<Object, Object>> returnValidationData = new ArrayList<HashMap<Object, Object>>();
	public static ArrayList<HashMap<Object, Object>> ValidationData;
	public static HashMap<Object, Object> ValidationStatus;
	public static  HashMap<String, Object> ValidationStatusMap = new HashMap<String, Object>();
	public static Logger mainLogger = Logger.getLogger(Validations.class);
	public static String actualValue;
	public static String comparisonType;
	public static String objectVisiblevalue;
	public static String objecteditvalue;
	public static String validationStatusId;
	public static String StatusValidation; 
	public static String comparison_method;
	public static HashMap<String, Object> titleValidation = new HashMap<String, Object>();
	public static HashMap<String, Object> alertText = new HashMap<String, Object>();
	public static HashMap<String, Object> objectVisible = new HashMap<String, Object>();
	public static HashMap<String, Object> objectEnable = new HashMap<String, Object>();
	public static HashMap<String, Object> toolTipMessage = new HashMap<String, Object>();
	public static HashMap<String, Object> CustomValidationMessage = new HashMap<String, Object>();
	public static HashMap<String, Object> tableValidation = new HashMap<String, Object>();
	public static HashMap<String, Object> ControlEditability = new HashMap<String, Object>();
	public static HashMap<String, Object> messageValidation = new HashMap<String, Object>();
	public static HashMap<String, Object> cssValidation = new HashMap<String, Object>();
	public static HashMap<String, Object> controlContentMap = new HashMap<String, Object>();
	public static JSONParser parser1 = new JSONParser();
	public static JSONObject StatusofValidation01;
	
	public static void ValidationStart(ValidationModel[] validationModelData, ExecuteObj oRObject, int i) throws Exception {

		for (int val = 0; val < validationModelData.length; val++) {

			ValidationModel validationData = validationModelData[val];
			int valKey = validationData.getAttributeId();
			comparison_method = validationData.getConditionExpression();
			String validationType = validationData.getValidationType();
			control_name = validationData.getControlName();	
			expectedValue = validationData.getExpectedValue();
			boundary_start = validationData.getBoundaryStart();
			boundary_end = validationData.getBoundaryEnd();
			row_number = validationData.getRowNumber();
			content_in_row = validationData.getContentInRow();
			column_heading = validationData.getColumnHeading();
			unique_rowdata = validationData.getUniqueRowData();
			variableKey = validationData.getVariableName();
			type=validationData.getType();
			Transactions transctions = oRObject.getTransactions().get(i);
			ArrayList<Attributes> attributesList = transctions.getAttributes();
			if(Controller.ValidationPosition.equals("before") || Controller.ValidationPosition.equals("after")){
				if(Controller.positionAttributeId !=  validationData.getPositionAttributeId()){
					continue;
				}
			}

			for (int j = 0; j < attributesList.size(); j++) {
				Attributes attribute = attributesList.get(j);
				validationId = attribute.getId();
				validationStatusId = validationData.getValidationId();
				fieldIgnoreInd=attribute.getIgnore_ind();
				String mrbgrid = attribute.getType();
				ArrayList<FieldDetails> FieldIdentifiers = attribute.getFieldDetails();
				String windowOrFrame = attribute.getWindowOrFrame();
				int index = attribute.getIndex();
				Controller.frame_attr_id = attribute.getFrameAttrId();
				
				
				
				mainLogger.info("Validation Type----------------->   " + validationType);

				if (mrbgrid.equalsIgnoreCase("frame")) {
					Controller.frameMap.put(Integer.valueOf(validationId), attribute.getFieldDetails());
				}
				if (validationType.equals("VALIDATION MESSAGE") || validationType.equals("POPUP MESSAGE") || validationType.equals("ALERT") || validationType.equals("TITLE") ){
					
					ValidationDetails(FieldIdentifiers, validationType, comparison_method, windowOrFrame, mrbgrid);
					StatusofValidation01 = (JSONObject) parser1.parse(StatusValidation);
					mainLogger.info("---StatusofValidation---" + StatusofValidation01);
					StatusofValidations = StatusofValidation01.get("status").toString();
					if (StatusofValidations.equalsIgnoreCase("pass")){
						mainLogger.info("Validation - " + validationType + ": Pass" + " Moving to Next Validation or Transaction Activity");
					}
					else if (StatusofValidations.equalsIgnoreCase("fail")){
						mainLogger.info("Validation - " + validationType + ": Fail" + " Skipped the Validation or Transaction Activity");
						Controller.ValidationStatusInd =0;
//						validationstop();
					}
					break;
				}
				
				
				if (valKey == validationId) {
					mainLogger.info("Validation Type------------------>   " + validationType);
					ValidationDetails(FieldIdentifiers, validationType, comparison_method, windowOrFrame, mrbgrid);
					/*ObjectMapper mapperValidation = new ObjectMapper();
					Set testDatakeys = StatusofValidation01.keySet();*/
					mainLogger.info("StatusValidation" + StatusValidation);
					StatusofValidation01 = (JSONObject) parser1.parse(StatusValidation);
					mainLogger.info("---StatusofValidation---" + StatusofValidation01);
					StatusofValidations = StatusofValidation01.get("status").toString();
					if (StatusofValidations.equalsIgnoreCase("pass")){
						mainLogger.info("Validation - " + validationType + ": Pass" + " Moving to Next Validation or Transaction Activity");
					}
					else if (StatusofValidations.equalsIgnoreCase("fail")){
						mainLogger.info("Validation - " + validationType + ": Fail" + " Skipped the Validation or Transaction Activity");
						Controller.ValidationStatusInd =0;
//						validationstop();
					}			
					break;
				}

			}

		}

	}

	public static void ValidationDetails(ArrayList<FieldDetails> fieldIdentifiers, String validationType,
			String comparisonMethod, String windowOrFrame, String fieldType) throws Exception {
		
	    ValidationData = new ArrayList<HashMap<Object, Object>>();
		ValidationStatus = new HashMap<Object, Object>();
		
		String windowshandel = null;
		boolean frameFalg = false;
		if (windowOrFrame != null) {
			if (windowOrFrame.startsWith("window")) {
				windowshandel = BrowserControls.WindowsHandeling();
			} else if (windowOrFrame.contains("frame")) {
				frameFalg = true;
				ArrayList<FieldDetails> fieldDetail = Controller.frameMap.get(Controller.frame_attr_id);

				PageObjectHandler.frameHandle(fieldDetail);

			}
		}
		WebElement Element =null;
		if (!validationType.equalsIgnoreCase("Alert")){
			Element= PageObjectHandler.findobject(fieldIdentifiers);
		}
		
		switch (validationType.toUpperCase()) {
		case "CONTENT":

			controlContent(Element, expectedValue, fieldType, comparisonMethod);
			
			break;
		
		case "EDITABILITY":
			
			ControlEditability(Element);
			
 			break;
			
		case "VISIBILITY":
			
		    ObjectVisibiltyValidation(Element);
		    
			break;

		/*case "ENABALITY":
		
			ObjectEnableValidation(Element);
			
			break;*/

		case "VALIDATION MESSAGE":
			MessageValidation(expectedValue);
			
			break;
			
		case "POPUP MESSAGE":	
			MessageValidation(expectedValue);
			
			break;
			
			
		case "TOOLTIP MESSAGE":
			
			TooltipMessageValidation(Element, expectedValue);
			
			break;

		case "ALERT MESSAGE":
			
			alertTextValidation(expectedValue);
			
			break;

		case "ALERT":
			alertTextValidation(expectedValue);
			//alert();
			break;

		case "CSS":
			
			
			cssValidation(Element, type, expectedValue);
			
			break;


		case "TABLE":
			TableValidation(fieldIdentifiers);
			
			break;

		case "TITLE":
		
			titelValidation(expectedValue);
			
			break;
			
		case "TEXT":
			
			TextValidation(Element, Integer.parseInt(boundary_start), Integer.parseInt(boundary_end), expectedValue);
			
			break;
		/*case "IMAGE VALIDATION":
			ImageValidation(Element,expectedValue);
			break;	*/
			
		default:
			
			try{
				comparisonMethod=comparisonMethod==null? "":comparisonMethod;
				 Object[] obj = {Element,expectedValue,fieldType,comparisonMethod,validationType.toUpperCase()};// for method1()
				 				 
			        Class<?> params[] = new Class[obj.length];
			        for (int i = 0; i < obj.length; i++) {
			            if (obj[i] instanceof Integer) {
			                params[i] = Integer.TYPE;
			            } else if (obj[i] instanceof String) {
			                params[i] = String.class;
			            }else if (obj[i] instanceof WebElement) {
			                params[i] = WebElement.class;
			            }
			        }
				Class<?> CustVal = Class.forName("Custom_Validation");
			if(CustVal!=null){
				 mainLogger.info("class added");
				 Object objCustom = CustVal.newInstance();
				 Method CustomVal =  CustVal.getMethod("CustomValidationMethod",params);
				 if(CustomVal!=null){
					 System.out.println("Custom_Validation- Start");
					 mainLogger.info("class added 2");
					 CustomVal.invoke(objCustom,obj); 
				 }
				 }
			}			 
				 catch (InstantiationException e) {
					 mainLogger.info(e);
					} catch (IllegalAccessException e1) {
						mainLogger.info(e1);
					}
				catch (NoSuchMethodException e2) {
					mainLogger.info(e2);
					} catch (SecurityException e3) {
						mainLogger.info(e3);
					}
					catch (ClassNotFoundException e4) {
						mainLogger.info(e4);
				    }
					catch (IllegalArgumentException e5) {
						mainLogger.info(e5);
					} catch (InvocationTargetException e6) {
						mainLogger.info(e6);
					}
				break;	
					}

		
		if (windowshandel != null) {
			BrowserControls.driver.switchTo().window(windowshandel);
		}
		if (frameFalg) {
			BrowserControls.driver.switchTo().defaultContent();
		}
		  
	}

	public static void ObjectVisibiltyValidation(WebElement ele) throws Exception {

		objectVisible.put("ValidationPosition", Controller.ValidationPosition);
		objectVisible.put("transactionId", Controller.transactionId);
		objectVisible.put("validationStatusId", validationStatusId);
		objectVisible.put("comparisonType", "Equals");
		if(ele != null){
		Boolean currentStatus = ele.isDisplayed();
		if(currentStatus ) {
		objectVisiblevalue = "Visible";
		}
		else {
			objectVisiblevalue = "Not-Visible"; 
			}       
		} else if(fieldIgnoreInd == 0){				
				objectVisiblevalue = "Element not found";
				objectVisible.put("status","skipped");
		} else {
			objectVisiblevalue = "Element not found";
		}
		objectVisible.put("value", objectVisiblevalue);
		if(variableKey != null){			
			Controller.Variablesvalue.put(variableKey,objectVisiblevalue);
		}
		StatusValidation = Controller.validationActualStatus(objectVisible);
	}
	
	public static void ControlEditability(WebElement ele) throws Exception {
		
		
		ControlEditability.put("ValidationPosition", Controller.ValidationPosition);
		ControlEditability.put("transactionId", Controller.transactionId);
		ControlEditability.put("validationStatusId", validationStatusId);
		comparisonType = "Equals";		
		ControlEditability.put("comparisonType", comparisonType);
		if(ele != null){
		Boolean currentStatus = ele.isEnabled();
		mainLogger.info("currentStatus -" + currentStatus);
		if(currentStatus!=true) {
			objecteditvalue = "NonEditable";
			 }
		else {
			objecteditvalue = "Editable";
			 }
		} else if(fieldIgnoreInd == 0){			
				objectVisiblevalue = "Element not found";
				ControlEditability.put("status","skipped");
		}else{
			objectVisiblevalue = "Element not found";
		}
		ControlEditability.put("value", objecteditvalue);
		if(variableKey != null){			
			Controller.Variablesvalue.put(variableKey,objecteditvalue);
		}
		StatusValidation = Controller.validationActualStatus(ControlEditability);
		

	}
	
	public static void controlContent(WebElement Element, String expectedValue, String fieldType,
			String comparisonMethod) throws Exception {

		String transaction_element_tagname = null;
		String transaction_element_type = null;
		String transaction_element_value = null;
		String transaction_element_multiple = null;
		String transaction_element_CHECKED = null;
		String transaction_element_size = null;
		controlContentMap.put("ValidationPosition", Controller.ValidationPosition);
		controlContentMap.put("transactionId", Controller.transactionId);
		controlContentMap.put("validationStatusId", validationStatusId);
		controlContentMap.put("comparisonType", comparisonMethod);
		if(Element != null){
		transaction_element_tagname = Element.getTagName();
		transaction_element_type = Element.getAttribute("type");
		transaction_element_multiple = Element.getAttribute("multiple");
		transaction_element_CHECKED = Element.getAttribute("CHECKED");
		transaction_element_size = Element.getAttribute("size");
		switch (fieldType.toLowerCase()) {
		case "textbox":
			transaction_element_value = Element.getAttribute("value");
			controlContentMap.put("value", transaction_element_value);
			break;

		case "label":
			transaction_element_value = Element.getAttribute("value");
			controlContentMap.put("value", transaction_element_value);

			break;

		case "textarea":
			transaction_element_value = Element.getAttribute("value");
			controlContentMap.put("value", transaction_element_value);
			break;

		case "dropdown":

			Select select = new Select(Element);
			WebElement option = select.getFirstSelectedOption();
			transaction_element_value = option.getText().trim();
			controlContentMap.put("value", transaction_element_value);

			break;
		default:
			transaction_element_value = Element.getText();
			controlContentMap.put("value", transaction_element_value);

			break;
		}
		}else if(fieldIgnoreInd == 0){
			transaction_element_value = "Element not found";
			controlContentMap.put("value", transaction_element_value);
			controlContentMap.put("status", "skipped");
		}else {
			controlContentMap.put("value", transaction_element_value);
		}
		if(variableKey != null){			
			Controller.Variablesvalue.put(variableKey,transaction_element_value);
		}
		StatusValidation = Controller.validationActualStatus(controlContentMap);
		
	}
	
	public static void TooltipMessageValidation(WebElement ele, String expectedText) throws Exception {
		
		toolTipMessage.put("ValidationPosition", Controller.ValidationPosition);
		toolTipMessage.put("transactionId", Controller.transactionId);	
		toolTipMessage.put("validationStatusId", validationStatusId);
		comparisonType = "Equals";	
		toolTipMessage.put("comparisonType", comparisonType);
		String actual = null;
		if(ele != null){			
			String actualTitle = ele.getAttribute("title");
			String actualDataTitle=ele.getAttribute("data-original-title");
			if(actualTitle.equals("") && !actualTitle.equals(expectedText)){
				actual = actualDataTitle;
				toolTipMessage.put("value", actual);
			}else{
				actual = actualTitle;
				toolTipMessage.put("value", actual);
			}
		}else if(fieldIgnoreInd == 0){
			actual = "Element not found";
			toolTipMessage.put("value", actual);
			toolTipMessage.put("status", "skipped");
		}else {
			actual = "Element not found";
			toolTipMessage.put("value", actual);
		}
		if(variableKey != null){			
			Controller.Variablesvalue.put(variableKey,actual);
		}
        StatusValidation = Controller.validationActualStatus(toolTipMessage);
		
        /*return actual;
		returnValidationData.add(toolTipMessage);
        Actions actions = new Actions(BrowserControls.driver);
		actions.moveToElement(TrasnactionElement).perform();*/

	}
	
	public static void cssValidation(WebElement css, String Attribute, String expectedValue) throws Exception {
	
	cssValidation.put("ValidationPosition", Controller.ValidationPosition);
	cssValidation.put("transactionId", Controller.transactionId);
	cssValidation.put("validationStatusId", validationStatusId);
	comparisonType = "Contains";		
	cssValidation.put("comparisonType", comparisonType);
	String cssValidationactual =null;
	if(css != null){
	cssValidationactual = css.getAttribute(Attribute);
	cssValidation.put("value", cssValidationactual);
	}else if(fieldIgnoreInd == 0){
		cssValidationactual = "Element not found";
		cssValidation.put("value", cssValidationactual);
		cssValidation.put("status", "skipped");
	}else {
		cssValidationactual = "Element not found";
		cssValidation.put("value", cssValidationactual);
	}
	if(variableKey != null){			
		Controller.Variablesvalue.put(variableKey,cssValidationactual);
	}
    StatusValidation = Controller.validationActualStatus(cssValidation);

	}

	public static void titelValidation(String expectedTitle) throws Exception {
		
		titleValidation.put("ValidationPosition", Controller.ValidationPosition);
		titleValidation.put("transactionId", Controller.transactionId);
		titleValidation.put("validationStatusId", validationStatusId);
		String titleActualValue = BrowserControls.driver.getTitle();
		comparisonType = "Contains";
		titleValidation.put("value", titleActualValue);
		titleValidation.put("comparisonType", comparisonType);
		if(variableKey != null){			
			Controller.Variablesvalue.put(variableKey,titleActualValue);
		}
		StatusValidation = Controller.validationActualStatus(titleValidation);
	}

	public static void alertTextValidation(String expectedAlertText) throws Exception {

		
		alertText.put("ValidationPosition", Controller.ValidationPosition);
		alertText.put("transactionId", Controller.transactionId);
		alertText.put("validationStatusId", validationStatusId);		
		String alertActualValue = BrowserControls.driver.switchTo().alert().getText();
		comparisonType = "Equals";
		
		alertText.put("value", alertActualValue);
		alertText.put("comparisonType", comparisonType);
		BrowserControls.driver.switchTo().alert().accept();
		if(variableKey != null){			
			Controller.Variablesvalue.put(variableKey,alertActualValue);
		}
		StatusValidation = Controller.validationActualStatus(alertText);
		

	}

	public static void CurrentPageValidation() {
		String value = null;

		@SuppressWarnings("rawtypes")
		HashMap currentPageValidation = Controller.oRObject.getProjectValidation();

		@SuppressWarnings("rawtypes")
		Iterator validatorItr = currentPageValidation.entrySet().iterator();

		while (validatorItr.hasNext()) {
			Map.Entry validationPair = (Map.Entry) validatorItr.next();
			value = validationPair.getValue().toString();

			try {
				String pg = BrowserControls.driver.getPageSource();
				doc1 = Jsoup.parse(BrowserControls.driver.getPageSource());
			} catch (UnhandledAlertException e) {

				doc1 = Jsoup.parse(BrowserControls.driver.getPageSource());
			}
			catch (NullPointerException e) {

				System.out.println("Null in Page source");
			}

			Htmltext = doc1.select("html").text();

			if (Htmltext.contains(value)) {
				mainLogger.info("Fail");
			} else {
				mainLogger.info("Pass");
			}
		}

	}

	/*public static void ObjectNotVisibiltyValidation(WebElement ele) throws Exception {

		
		HashMap<String, Object> objectNotVisible = new HashMap<String, Object>();
		objectNotVisible.put("ValidationPosition", Controller.ValidationPosition);
		objectNotVisible.put("transactionId", Controller.transactionId);
		objectNotVisible.put("validationStatusId", validationStatusId);
		Boolean currentStatus = ele.isDisplayed();
		if(currentStatus!=true) {
			objectVisiblevalue = "Not-Visible";
			objectNotVisible.put("value", objectVisiblevalue); }
		else {
			objectVisiblevalue = "Visible";
			objectNotVisible.put("value", objectVisiblevalue); }
			
			comparisonType = comparison_method;
			
			objectNotVisible.put("comparisonType", comparisonType);
			StatusValidation = Controller.validationActualStatus(objectNotVisible);

	}

	public static void ObjectEnableValidation(WebElement ele) throws Exception {

		
		objectEnable.put("ValidationPosition", Controller.ValidationPosition);
		objectEnable.put("transactionId", Controller.transactionId);		
		objectEnable.put("validationStatusId", validationStatusId);
		comparisonType = comparison_method;		
		objectEnable.put("comparisonType", comparisonType);
		if(ele!=null){
		Boolean currentStatus = ele.isEnabled();
		if(currentStatus!=true) {
			objectVisiblevalue = "Not-Visible";
			objectEnable.put("value", objectVisiblevalue); }
		else {
			objectVisiblevalue = "Visible";
			objectEnable.put("value", objectVisiblevalue); }
		}else if(fieldIgnoreInd){				
				objectVisiblevalue = "Not-Visible";
				objectEnable.put("status","skipped");
		}else {
			
		}
		objectEnable.put("value", objectVisiblevalue);
		StatusValidation = Controller.validationActualStatus(objectEnable);


	}*/
	
	public static void CustomValidation(WebElement ele, String expectedText) throws Exception {

		
		
		CustomValidationMessage.put("ValidationPosition", Controller.ValidationPosition);
		CustomValidationMessage.put("transactionId", Controller.transactionId);
		
		CustomValidationMessage.put("validationStatusId", validationStatusId);
		mainLogger.info("validationId - Custom" + validationId);
		String actual = ele.getAttribute("title");
		mainLogger.info("actual - Custom" + actual);
		CustomValidationMessage.put("value", actual);
		 comparisonType = comparison_method;
			
		 CustomValidationMessage.put("comparisonType", comparisonType);
		 StatusValidation = Controller.validationActualStatus(CustomValidationMessage);
		

	}

	public static void TableValidation(ArrayList<FieldDetails> fieldIdentifiers) throws Exception {

		
		WebElement Table = PageObjectHandler.findobject(fieldIdentifiers);
		List<WebElement> tr = Table.findElements(By.tagName("tr"));
		tableValidation.put("ValidationPosition", Controller.ValidationPosition);
		tableValidation.put("transactionId", Controller.transactionId);
		comparisonType = comparison_method;
		CustomValidationMessage.put("comparisonType", comparisonType);
		tableValidation.put("validationStatusId", validationStatusId);
		tableValidation.put("column_heading", column_heading);
		
		int rownum = 0;
		try {
			rownum = Integer.valueOf(row_number);
		} catch (NumberFormatException e) {

		}
		int columPos = 0;


		String ColumName1 = "";
		
		if (!column_heading.contains(",")) {
			List<WebElement> th = tr.get(0).findElements(By.tagName("th"));

			for (int i = 0; i < th.size(); i++) {
				String ColumName = th.get(i).getText();
				ColumName1 += th.get(i).getText();

				if (column_heading.equalsIgnoreCase(ColumName)) {
					columPos = i;
					break;

				}

			}

			if (!ColumName1.contains(column_heading)) {

				mainLogger.info("Column not present");
			}

		} else if (!column_heading.contains(",")) {

			if (ColumName1.contains(column_heading)) {

				mainLogger.info("Column heading is present");
			} else {

				mainLogger.info("Column heading not present");
			}

		}

		if (rownum > 0) {
			List<WebElement> td = tr.get(rownum).findElements(By.tagName("td"));

			String actColumnData = td.get(columPos).getText();
			tableValidation.put("actColumnData", actColumnData);
		}
		StatusValidation = Controller.validationActualStatus(tableValidation);
			}

	

	public static void MessageValidation(String ExpectedValue) throws Exception {

		try {
			doc1 = Jsoup.parse(BrowserControls.driver.getPageSource());
		} catch (UnhandledAlertException e) {

			doc1 = Jsoup.parse(BrowserControls.driver.getPageSource());
		}

		Htmltext = doc1.select("html").text();
		
		messageValidation.put("ValidationPosition", Controller.ValidationPosition);
		messageValidation.put("transactionId", Controller.transactionId);
		messageValidation.put("validationStatusId", validationStatusId);
		messageValidation.put("value", Htmltext);
        comparisonType = "Contains";
        messageValidation.put("comparisonType", comparisonType);
        if(variableKey != null){			
			Controller.Variablesvalue.put(variableKey,Htmltext);
		}
        StatusValidation = Controller.validationActualStatus(messageValidation);

	}

	public static void ObjectDisabledValidation(WebElement ele) throws Exception {
		HashMap<String, Object> objectDisabledValidation = new HashMap<String, Object>();
		
		objectDisabledValidation.put("ValidationPosition", Controller.ValidationPosition);
		objectDisabledValidation.put("transactionId", Controller.transactionId);
		objectDisabledValidation.put("validationStatusId", validationStatusId);
		Boolean currentStatus = ele.isEnabled();
		if(currentStatus!=true) {
			objecteditvalue = "NonEditable";
			objectDisabledValidation.put("value", objecteditvalue); }
		else {
			objecteditvalue = "Editable";
			objectDisabledValidation.put("value", objecteditvalue); }
	
		comparisonType = "Equals";
		objectDisabledValidation.put("comparisonType", comparisonType);
		StatusValidation = Controller.validationActualStatus(objectDisabledValidation);
	}


	public static boolean alert() {
		try {
			WebDriverWait wait = new WebDriverWait(BrowserControls.driver, 3);
			wait.until(ExpectedConditions.alertIsPresent());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static void TextValidation(WebElement text, int boundaryStart, int boundaryEnd, String expectedValue) throws Exception {
		
		validatiionDetails.put("ValidationPosition", Controller.ValidationPosition);
		validatiionDetails.put("transactionId", Controller.transactionId);
		validatiionDetails.put("validationStatusId", validationStatusId);
		comparisonType="Equals";
		validatiionDetails.put("comparisonType", comparisonType);
		if(text != null){
			String actualText = text.getText().trim();
			String actualValue = actualText.substring(boundaryStart, boundaryEnd);
			validatiionDetails.put("value", actualValue);			
		}else if(fieldIgnoreInd == 0){
			actualValue = "Element not found";
			validatiionDetails.put("value", actualValue);
			validatiionDetails.put("value", "skipped");
		}else {
			actualValue = "Element not found";
			validatiionDetails.put("value", actualValue);
		}
		if(variableKey != null){			
			Controller.Variablesvalue.put(variableKey,actualValue);
		}
		StatusValidation = Controller.validationActualStatus(validatiionDetails);
	}

	public static void validationstop() throws Exception{
		Controller.testRunDetails.put("ValidationStatusData", "Fail");
		Controller.testRunDetails.put("status", "FAIL");
		Controller.completedTime = Utility.completedTime();
		Controller.testRunDetails.put("completedTime",  Controller.completedTime);
		Controller.testRunDetails.put("failureReason", PageObjectHandler.failureReason);
		Controller.sendTestRunStatus(Controller.testRunDetails);
	}
}
