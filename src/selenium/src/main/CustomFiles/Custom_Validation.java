import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.kumaran.tac.framework.selenium.controller.Controller;
import com.kumaran.tac.framework.selenium.frameworklayer.BrowserControls;
import com.kumaran.tac.framework.selenium.frameworklayer.PageObjectHandler;
import com.kumaran.tac.framework.selenium.frameworklayer.Validations;
import com.kumaran.tac.framework.selenium.entity.Attributes;
import com.kumaran.tac.framework.selenium.entity.ExecuteObj;
import com.kumaran.tac.framework.selenium.entity.FieldDetails;

import com.kumaran.tac.framework.selenium.entity.Transactions;
import com.kumaran.tac.framework.selenium.entity.ValidationModel;

public class Custom_Validation extends Validations{
	public static String control_name = "";
	public static String pre_post = "";

	public static String expected_value = "";
	public static String boundary_start = "";
	public static String boundary_end = "";
	public static String row_number = "";
	public static String content_in_row = "";
	public static String column_heading = "";
	public static String unique_rowdata = "";
	static Document doc1 = null;
	static String Htmltext = null;
	public static HashMap<Object, String> validatiionDetails = new HashMap<Object, String>();
	public static int validationId = 0;
	public static ArrayList<HashMap<Object, Object>> returnValidationData = new ArrayList<HashMap<Object, Object>>();

	public static void ValidationStart(ValidationModel[] validationModelData, ExecuteObj oRObject, int i) {

		System.out.println("Custom Validation Started");
		
		for (int val = 0; val < validationModelData.length; val++) {

			ValidationModel validationData = validationModelData[val];
			int valKey = validationData.getId();

			control_name = validationData.getControl_name();
			pre_post = validationData.getPre_post();
			String comparison_method = validationData.getComparison_method();
			String validationType = validationData.getValidation_type();
			expected_value = validationData.getExpected_value();
			boundary_start = validationData.getBoundary_start();
			boundary_end = validationData.getBoundary_end();
			row_number = validationData.getRow_number();
			content_in_row = validationData.getContent_in_row();
			column_heading = validationData.getColumn_heading();
			unique_rowdata = validationData.getUnique_RowData();
			Transactions transctions = oRObject.getTransactions().get(i);
			ArrayList<Attributes> attributesList = transctions.getAttributes();

			for (int j = 0; j < attributesList.size(); j++) {
				Attributes attribute = attributesList.get(j);
				validationId = attribute.getId();

				String mrbgrid = attribute.getType();
				ArrayList<FieldDetails> FieldIdentifiers = attribute.getFieldDetails();
				String windowOrFrame = attribute.getWindowOrFrame();
				int index = attribute.getIndex();
				Controller.frame_attr_id = attribute.getFrameAttrId();

				if (mrbgrid.equalsIgnoreCase("frame")) {
					Controller.frameMap.put(Integer.valueOf(validationId), attribute.getFieldDetails());
				}
				if (valKey == validationId) {
					System.out.println("Validation Type----------------->   " + pre_post);
					ValidationDetails(FieldIdentifiers, validationType, comparison_method, windowOrFrame, mrbgrid);
					break;
				}

			}

		}

	}

	public static void ValidationDetails(ArrayList<FieldDetails> fieldIdentifiers, String validationType,
			String comparisonMethod, String windowOrFrame, String fieldType) {

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
		WebElement Element = PageObjectHandler.findobject(fieldIdentifiers);
		switch (validationType.toUpperCase()) {
		case "CONTROL_CONTENT":

			controlContent(Element, expected_value, fieldType, comparisonMethod);
			break;
		case "CONTROL_EDITABILITY":
			if (expected_value.equalsIgnoreCase("editable")) {
				ControlEditability(Element);
			} else if (expected_value.equalsIgnoreCase("not editable")) {
				ControlEditability(Element);
			}
			break;
		case "CONTROL_VISIBILITY":
			if (expected_value.equalsIgnoreCase("visible")) {
				ObjectVisibiltyValidation(Element);
			} else if (expected_value.equalsIgnoreCase("not visible")) {
				ObjectNotVisibiltyValidation(Element);
			}

			break;

		case "CONTROL_ENABALITY":
			if (expected_value.equalsIgnoreCase("Enable")) {
				ObjectEnableValidation(Element);
			} else if (expected_value.equalsIgnoreCase("Disable")) {
				ObjectDisabledValidation(Element);
			}

			break;
		case "MESSAGE":
			MessageValidation(expected_value);
			break;
		case "TOOlTIP":
			TooltipMessageValidation(Element, expected_value);
			break;
		case "ALERT_MESSAGE":
			alertTextValidation(expected_value);
			break;
		case "ALERT":
			alert();
			break;
		case "CSS_VALIDATION":
			cssValidation(Element, comparisonMethod, expected_value);
			break;

		case "TABLE_VALIDATION":
			TableValidation(fieldIdentifiers);
			break;

		case "TITLE_VALIDATION":
			titelValidation(expected_value);
			break;
		
		case "CUSTOM_VALIDATION":
			CustomValidation(Element, expected_value);
			break;	
			
		/*
		 * case "BOUNDARY_VALUES":
		 * boundaryValues(Element,boundary_start,boundary_end,expected_value);
		 * break;
		 */
		}

		if (windowshandel != null) {
			BrowserControls.driver.switchTo().window(windowshandel);
		}
		if (frameFalg) {
			BrowserControls.driver.switchTo().defaultContent();
		}

	}

	public static void titelValidation(String expectedTitle) {

		HashMap<Object, Object> titleValidation = new HashMap<Object, Object>();
		titleValidation.put("validationId", validationId);
		String actualTitel = BrowserControls.driver.getTitle();
		titleValidation.put("value", actualTitel);
		returnValidationData.add(titleValidation);
	}

	public static void alertTextValidation(String expectedAlertText) {

		HashMap<Object, Object> alertText = new HashMap<Object, Object>();
		alertText.put("validationId", validationId);
		String actualAlertText = BrowserControls.driver.switchTo().alert().getText();
		alertText.put("value", actualAlertText);
		returnValidationData.add(alertText);

	}

	public static void ObjectVisibiltyValidation(WebElement ele) {

		HashMap<Object, Object> objectVisible = new HashMap<Object, Object>();
		objectVisible.put("validationId", validationId);
		Boolean currentStatus = ele.isDisplayed();
		objectVisible.put("value", currentStatus);
		returnValidationData.add(objectVisible);

	}

	public static void CurrentPageValidation() {
		String value = null;

		HashMap currentPageValidation = Controller.oRObject.getProjectValidation();

		Iterator validatorItr = currentPageValidation.entrySet().iterator();

		while (validatorItr.hasNext()) {
			Map.Entry validationPair = (Map.Entry) validatorItr.next();
			value = validationPair.getValue().toString();

			try {
				doc1 = Jsoup.parse(BrowserControls.driver.getPageSource());
			} catch (UnhandledAlertException e) {

				doc1 = Jsoup.parse(BrowserControls.driver.getPageSource());
			}

			Htmltext = doc1.select("html").text();

			if (Htmltext.contains(value)) {
				System.out.println("Fail");
			} else {
				System.out.println("Pass");
			}
		}

	}

	public static void ObjectNotVisibiltyValidation(WebElement ele) {

		HashMap<Object, Object> objectNotVisible = new HashMap<Object, Object>();
		objectNotVisible.put("validationId", validationId);
		Boolean currentStatus = ele.isDisplayed();
		objectNotVisible.put("value", currentStatus);
		returnValidationData.add(objectNotVisible);

	}

	public static void ObjectEnableValidation(WebElement ele) {

		HashMap<Object, Object> objectEnable = new HashMap<Object, Object>();
		objectEnable.put("validationId", validationId);
		Boolean currentStatus = ele.isEnabled();
		objectEnable.put("value", currentStatus);
		returnValidationData.add(objectEnable);

	}

	public static void TooltipMessageValidation(WebElement ele, String expectedText) {

		HashMap<Object, Object> toolTipMessage = new HashMap<Object, Object>();
		toolTipMessage.put("validationId", validationId);
		String actual = ele.getAttribute("title");
		toolTipMessage.put("value", actual);
		returnValidationData.add(toolTipMessage);

	}

	public static void TableValidation(ArrayList<FieldDetails> fieldIdentifiers) {

		WebElement Table = PageObjectHandler.findobject(fieldIdentifiers);
		List<WebElement> tr = Table.findElements(By.tagName("tr"));
		int rownum = 0;
		try {
			rownum = Integer.valueOf(row_number);
		} catch (NumberFormatException e) {

		}
		int columPos = 0;

		// content_in_row = "";
		// column_heading = "";
		// unique_rowdata = "";
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

				System.out.println("Column not present");
			}

		} else if (!column_heading.contains(",")) {

			if (ColumName1.contains(column_heading)) {

				System.out.println("Column heading is present");
			} else {

				System.out.println("Column heading not present");
			}

		}

		if (rownum > 0) {
			List<WebElement> td = tr.get(rownum).findElements(By.tagName("td"));

			String actColumnData = td.get(columPos).getText();
			if (actColumnData.equalsIgnoreCase(content_in_row)) {

				System.out.println("Pass");
			} else {

				System.out.println("Fail");

			}
		}

		if (!unique_rowdata.equalsIgnoreCase("")) {
			boolean unRowFalg = false;
			boolean unRowFalg1 = false;
			for (int i = 1; i < tr.size(); i++) {
				List<WebElement> td = tr.get(i).findElements(By.tagName("td"));
				String actColumnData = td.get(columPos).getText();
				String getRowDataInTd = "";
				unique_rowdata = unique_rowdata.replace(",", "").trim();
				if (actColumnData.equalsIgnoreCase(content_in_row)) {
					unRowFalg = true;
					for (int j = 0; j < td.size(); j++) {
						getRowDataInTd += td.get(j).getText().trim();
					}
					if (getRowDataInTd.contains(unique_rowdata)) {
						unRowFalg1 = true;

					}
				}

			}
			if (unRowFalg1 && unRowFalg) {
				System.out.println("Pass");

			} else {
				System.out.println("Fail");
			}

		}

	}

	public static void ControlEditability(WebElement ele) {
		HashMap<Object, Object> ControlEditability = new HashMap<Object, Object>();
		ControlEditability.put("validationId", validationId);
		Boolean currentStatus = ele.isEnabled();
		ControlEditability.put("value", currentStatus);
		returnValidationData.add(ControlEditability);

	}

	public static void MessageValidation(String ExpectedValue) {

		try {
			doc1 = Jsoup.parse(BrowserControls.driver.getPageSource());
		} catch (UnhandledAlertException e) {

			doc1 = Jsoup.parse(BrowserControls.driver.getPageSource());
		}

		Htmltext = doc1.select("html").text();

		HashMap<Object, Object> messageValidation = new HashMap<Object, Object>();
		messageValidation.put("validationId", validationId);
		messageValidation.put("value", Htmltext);
		returnValidationData.add(messageValidation);

	}

	public static void ObjectDisabledValidation(WebElement ele) {
		HashMap<Object, Object> objectDisabledValidation = new HashMap<Object, Object>();
		objectDisabledValidation.put("validationId", validationId);
		Boolean currentStatus = ele.isEnabled();
		objectDisabledValidation.put("value", currentStatus);
		returnValidationData.add(objectDisabledValidation);

	}

//	public static boolean alert() {
//
//		try {
//			WebDriverWait wait = new WebDriverWait(BrowserControls.driver, 3);
//			wait.until(ExpectedConditions.alertIsPresent());
//			// transaction_DataLoad_driver.switchTo().alert();
//			return true;
//		} catch (Exception e) {
//			return false;
//		}
//	}

	public static void boundaryValues(WebElement text, int boundaryStart, int boundaryEnd, String expectedValue) {
		String actualText = text.getText().trim();
		String actualValue = actualText.substring(boundaryStart, boundaryEnd);
		validatiionDetails.put(validationId, actualValue);
	}

	public static void cssValidation(WebElement css, String Attribute, String expectedValue) {
		HashMap<Object, Object> cssValidation = new HashMap<Object, Object>();
		cssValidation.put("validationId", validationId);
		String actualValue = css.getAttribute(Attribute);
		cssValidation.put("value", actualValue);
		returnValidationData.add(cssValidation);

	}

	public static void CustomValidation(WebElement ele, String expectedText) {

		HashMap<Object, Object> CustomValidation = new HashMap<Object, Object>();
		CustomValidation.put("validationId", validationId);
		String actual = ele.getAttribute("title");
		CustomValidation.put("value", actual);
		returnValidationData.add(CustomValidation);

	}
	public static void controlContent(WebElement Element, String expectedValue, String fieldType,
			String comparisonMethod) {

		String transaction_element_tagname = null;
		String transaction_element_type = null;
		String transaction_element_value = null;
		String transaction_element_multiple = null;
		String transaction_element_CHECKED = null;
		String transaction_element_size = null;
		transaction_element_tagname = Element.getTagName();
		transaction_element_type = Element.getAttribute("type");

		transaction_element_multiple = Element.getAttribute("multiple");
		transaction_element_CHECKED = Element.getAttribute("CHECKED");
		transaction_element_size = Element.getAttribute("size");
		HashMap<Object, Object> controlContentMap = new HashMap<Object, Object>();
		controlContentMap.put("validationId", validationId);

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

			String Extracted_Application_Field_Value = option.getText().trim();
			transaction_element_value = Extracted_Application_Field_Value;
			controlContentMap.put("value", transaction_element_value);

			break;
		default:
			transaction_element_value = Element.getText();
			controlContentMap.put("value", transaction_element_value);

			break;
		}

		returnValidationData.add(controlContentMap);

		int actNumber = 0;
		int exptNumber = 0;
		String ActFalg = "";
		String exctFalg = "";
		if (comparisonMethod.equalsIgnoreCase("=")) {
			if (expectedValue.equalsIgnoreCase(transaction_element_value)) {
				System.out.println("Pass");
			} else {

				System.out.println("Fail");
			}

		} else {

			try {
				actNumber = Integer.valueOf(transaction_element_value);
				ActFalg = "Convert";
			} catch (NumberFormatException e) {
				ActFalg = "notConvert";

			}

			try {
				exptNumber = Integer.valueOf(expectedValue);
				exctFalg = "Convert";
			} catch (NumberFormatException e) {
				exctFalg = "notConvert";
			}
		}

		if (ActFalg.equalsIgnoreCase("notConvert") || exctFalg.equalsIgnoreCase("notConvert")) {

			System.out.println("Fail");

		}
		if (ActFalg.equalsIgnoreCase("Convert") && exctFalg.equalsIgnoreCase("Convert")) {
			switch (comparisonMethod.toLowerCase()) {

			case ">":
				if (actNumber > exptNumber) {
					System.out.println("Pass");
				} else {
					System.out.println("Fail");
				}
				break;
			case "<":
				if (actNumber < exptNumber) {
					System.out.println("Pass");
				} else {
					System.out.println("Fail");
				}
				break;
			case ">=":
				if (actNumber >= exptNumber) {
					System.out.println("Pass");
				} else {
					System.out.println("Fail");
				}
				break;
			case "<=":
				if (actNumber <= exptNumber) {
					System.out.println("Pass");
				} else {
					System.out.println("Fail");
				}
				break;
			case "<>":
				if (actNumber != exptNumber) {
					System.out.println("Pass");
				} else {
					System.out.println("Fail");
				}
				break;

			}
		}
	}

}
