package com.kumaran.tac.framework.selenium.frameworklayer;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import  com.kumaran.tac.framework.selenium.controller.Controller;
import com.kumaran.tac.framework.selenium.entity.FieldDetails;
import org.apache.log4j.Logger;

@SuppressWarnings("unused")
public class PageObjectHandler {
	public static WebElement TrasnactionElement = null;
	public static String failureReason = null;
	public static String CustomAtt;
	public static Boolean IgnoreAttrSts=null;
	public static Logger mainLogger = Logger.getLogger(PageObjectHandler.class);
	// Find pageObject for WebApplication
	public static WebElement findobject(ArrayList<FieldDetails> fieldDetail) {
		WebElement element = null;
		List<WebElement> elements;
		fielDetailsloop: for (int i = 0; i < fieldDetail.size(); i++) {

			FieldDetails fieldIdentifier = fieldDetail.get(i);

			BrowserControls.waitPageloadComplete();
			By valueBy = findobjectBy(fieldIdentifier);
			BrowserControls.explicitWaitForObjectFoundBy(valueBy);
			if (valueBy == null) {
				continue fielDetailsloop;
			}
			elements = BrowserControls.driver.findElements(valueBy);
			if (Controller.multiElement > 0) {
				try {

					if (element != null && elements.size() > 0) {

						element = elements.get(Controller.multiElement - 1);
						break;
					}

				} catch (Exception e) {

				}

			} else {
				try {

					element = BrowserControls.driver.findElement(valueBy);
					
				} catch (ElementNotVisibleException e) {
					mainLogger.info(e);

				} catch (NoSuchElementException noElementException) {
					mainLogger.info("----------------not found--------------------");
				} catch (NoSuchWindowException noWindowEx) {
					new NoSuchWindowException("window has closed unfortunately or due to manual intraction");
				}
			}
		}
		String objectdata = "";
		for (int i = 0; i < fieldDetail.size(); i++) {
			String fieldType = fieldDetail.get(i).getType();
			String fieldValue = fieldDetail.get(i).getValue();
			objectdata += fieldType + ":" + fieldValue + ",";

		}

		if (element == null) {

			if (Controller.multiElement > 0) {
				throw new NoSuchElementException("Multielement position of " + Controller.multiElement
						+ " Field  \"" + Controller.transactionColumnName + "\" element not found in "
						+ Controller.transactionName + " transaction of step id" + Controller.transactionStepId);

			} else {
					failureReason = "Field name \"" + objectdata + " " + Controller.transactionColumnName
							+ "\" element not found in " + Controller.transactionName + " transaction of step id "
							+ Controller.transactionStepId;
					
					throw new NoSuchElementException(failureReason);
			}

		} else {
			TrasnactionElement = element;
		}
		return element;
	}

	public static By findobjectBy(FieldDetails fieldIdentifier) {

		By valueBy = null;
		switch (fieldIdentifier.getType().toUpperCase()) {
		case "ID":
			valueBy = By.id(fieldIdentifier.getValue());
			break;
		case "CSSSELECTOR":
			valueBy = By.cssSelector(fieldIdentifier.getValue());
			break;
		case "XPATH":
			valueBy = By.xpath(fieldIdentifier.getValue());
			break;
		case "NAME":
			valueBy = By.name(fieldIdentifier.getValue());
			break;
		case "CLASSNAME":
			valueBy = By.className(fieldIdentifier.getValue());
			break;
		case "LINKTEXT":
			valueBy = By.linkText(fieldIdentifier.getValue());
			break;

		case "PARTIALLINKTEXT":
			valueBy = By.partialLinkText(fieldIdentifier.getValue());
			break;
		case "TAGNAME":
			valueBy = By.tagName(fieldIdentifier.getValue());
			break;
		}

		return valueBy;
	}

	public static void fieldAction(String action, String testData) throws InterruptedException, AWTException {

		BrowserControls.waitPageloadComplete();
		BrowserControls.explicitWaitForObjectFound(TrasnactionElement);
		//System.out.println("inside Method");
		BrowserControls.highlightElements(TrasnactionElement, "feed");
		switch (action.toUpperCase()) {
		case "TEXTBOX":
			TrasnactionElement.click();
			TrasnactionElement.clear();
			TrasnactionElement.sendKeys(testData);
			break;
		case "LINK":
		case "BUTTON":	
		case "RADIO":
		case "CHECKBOX":
			TrasnactionElement.click();
			break;
		case "DROPDOWN":
			if (testData.trim().length() > 0) {
				int i;
				for (i = 1; i < 11; i++) {
					try {
						Select dropdown = new Select(TrasnactionElement);
						List<WebElement> options = dropdown.getOptions();
						mainLogger.info(" i - " + i);

						dropdown.selectByVisibleText(testData);

						break;
					} catch (Exception e) {
						mainLogger.info("Drop down is not loaded");
						Thread.sleep(2000);
					}
				}
				if (i == 11) {

					throw new ElementNotVisibleException(Controller.transactionColumnName + " Dropdown value not found "
							+ testData + " with step Id " + Controller.transactionStepId);
				}
			}
			break;
		case "label":
			TrasnactionElement.getAttribute("value");
			break;
		default:
			
			try{
				
				 Object[] obj = {action,testData,TrasnactionElement};// for method1()
				 				 
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
				Class<?> fa = Class.forName("Custom_Attributes");
			if(fa!=null){
				  
				 mainLogger.info("class added");
				 Object objfa = fa.newInstance();
				 Method fam =  fa.getMethod("Custom_fieldAction",params);
				 if(fam!=null){
					 System.out.println("Custom_fieldAction- Start TOGGLE");
					 mainLogger.info("class added 2");
					 fam.invoke(objfa,obj); 
				 }
				 }
			}		 
				 catch (InstantiationException e) {
						// TODO Auto-generated catch block
					 mainLogger.info(e);
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						mainLogger.info(e1);
					}
				catch (NoSuchMethodException e2) {
						// TODO Auto-generated catch block
					mainLogger.info(e2);
					} catch (SecurityException e3) {
						// TODO Auto-generated catch block
						mainLogger.info(e3);
					}
					catch (ClassNotFoundException e4) {
					// TODO Auto-generated catch block
						mainLogger.info(e4);
				    }
					catch (IllegalArgumentException e5) {
						// TODO Auto-generated catch block
						mainLogger.info(e5);
					} catch (InvocationTargetException e6) {
						// TODO Auto-generated catch block
						mainLogger.info(e6);
					} 
				break;	
					}

	}
	

	public static void HiddenElementfieldAction(String action, String testData) throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) BrowserControls.driver;
		switch (action.toUpperCase()) {

		case "TEXTBOX":

			js.executeScript("arguments[0].setAttribute('value','" + testData + "')", TrasnactionElement);

			break;
		case "BUTTON":
			js.executeScript("arguments[0].click();", TrasnactionElement);

			break;
		case "LINK":
			js.executeScript("arguments[0].click();", TrasnactionElement);

			break;

		case "RADIO":
			js.executeScript("arguments[0].click();", TrasnactionElement);

			break;
		case "CHECKBOX":
			js.executeScript("arguments[0].click();", TrasnactionElement);
			break;

		default:
			
			try{
				Class<?> fa = Class.forName("Custom_Attributes");
			if(fa!=null){
				 mainLogger.info("class added");
				 Object objfa = fa.newInstance();
				 Method fam =  fa.getMethod("Custom_fieldAction");
				 if(fam!=null){
					 fam.invoke(objfa); 
				 }
				 }
				 }catch (NoClassDefFoundError e){
						mainLogger.info("Cls def err");
					}
				 catch (InstantiationException e) {
						// TODO Auto-generated catch block
					 mainLogger.info(e);
					} catch (IllegalAccessException e1) {
						// TODO Auto-generated catch block
						mainLogger.info(e1);
					}
				catch (NoSuchMethodException e2) {
						// TODO Auto-generated catch block
					mainLogger.info(e2);
					} catch (SecurityException e3) {
						// TODO Auto-generated catch block
						mainLogger.info(e3);
					}
					catch (ClassNotFoundException e4) {
					// TODO Auto-generated catch block
						mainLogger.info(e4);
				    }
					catch (IllegalArgumentException e5) {
						// TODO Auto-generated catch block
						mainLogger.info(e5);
					} catch (InvocationTargetException e6) {
						// TODO Auto-generated catch block
						mainLogger.info(e6);
					} catch (Exception e7){
						mainLogger.info(e7);
					}
				break;	
			
		}

	}

	public static void datafeed(String testdata, ArrayList<FieldDetails> fieldIdentifiers, String action,
			String windowOrFrame) throws AWTException {

		String windowshandel = null;
		boolean frameFalg = false;

		if (windowOrFrame != null) {

			if (windowOrFrame.startsWith("window")) {

				windowshandel = BrowserControls.WindowsHandeling();
			} else if (windowOrFrame.contains("frame")) {
				frameFalg = true;
				ArrayList<FieldDetails> fieldDetail = Controller.frameMap.get(Controller.frame_attr_id);

				// -------------------------Need to Rewart code
				frameHandle(fieldDetail);

			}
		}
		if (!action.equalsIgnoreCase("alert")) {

			try {
				IgnoreAttrSts=null;
				WebElement ele = findobject(fieldIdentifiers);
				if(IgnoreAttrSts==null){					
					fieldAction(action, testdata);
				}
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		} else {

			if (testdata.contains("|")) {

				String[] alertSplit = testdata.split("\\|");
				applicationPopup(alertSplit[0], alertSplit[1]);
			} else {
				applicationPopup(testdata, "");
			}

		}
		if (windowshandel != null) {
			BrowserControls.driver.switchTo().window(windowshandel);
		}
		if (frameFalg) {
			BrowserControls.driver.switchTo().defaultContent();
		}

	}

	// UnExpected alert to be handle
	public static void unExpectedAlert(String action) {

		if (IsAlertPresents()) {

			Alert alert = BrowserControls.driver.switchTo().alert();

			if (action.equalsIgnoreCase("OK") || action.equalsIgnoreCase("Yes")) {

				alert.accept();
			} else if (action.equalsIgnoreCase("cancel")) {
				alert.dismiss();

			}

		}
	}

	// Handle the popup in the application
	public static void applicationPopup(String action, String popUptext) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (IsAlertPresents()) {

			Alert alert = BrowserControls.driver.switchTo().alert();

			if (!popUptext.equalsIgnoreCase("")) {

				alert.sendKeys(popUptext);

			}

			if (action.equalsIgnoreCase("OK") || action.equalsIgnoreCase("Yes")) {
				System.out.print("<----------------- alert is present ------------------->");
				alert.accept();
			} else if (action.equalsIgnoreCase("cancel")) {
				alert.dismiss();

			}

		} else {
			System.out.print("<-------------- alert is not present --------------->");
			// throw new NoAlertPresentException("Alert not presented");
		}
	}

	// alert is Alert Present or not
	public static boolean IsAlertPresents() {

		try {
			WebDriverWait wait = new WebDriverWait(BrowserControls.driver, 3);
			wait.until(ExpectedConditions.alertIsPresent());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// Switch to Frame

	public static void frameHandle(ArrayList<FieldDetails> fieldDeatils) {

		WebElement Frame = findobject(fieldDeatils);
		BrowserControls.driver.switchTo().frame(Frame);
	}

	public static void tableAction(String TestData, ArrayList<String> ColumnName, String tableData,
			ArrayList<FieldDetails> gridFieldIdentifiers, String actionType, int action, String WindowOrFrame,
			ArrayList<FieldDetails> fieldIdentifiers) throws Exception {

		boolean frameFalg = false;
		BrowserControls.waitPageloadComplete();
		String windowshandel = null;
		BrowserControls.waitPageloadComplete();
		if (WindowOrFrame != null) {
			if (WindowOrFrame.startsWith("window")) {
				windowshandel = BrowserControls.WindowsHandeling();
			} else if (WindowOrFrame.toLowerCase().contains("frame")) {
				frameFalg = true;

				ArrayList<FieldDetails> fieldDetail = Controller.frameMap.get(Controller.frame_attr_id);

				frameHandle(fieldDetail);

			}
		}


		ArrayList<Integer> postionOfTableColumn = new ArrayList<>();

		WebElement table = findobject(gridFieldIdentifiers);
			BrowserControls.highlightElements(table, "feed");
		List<WebElement> tr = table.findElements(By.tagName("tr"));

		List<WebElement> th = tr.get(0).findElements(By.tagName("th"));
		if (th.size() == 0) {
			th = tr.get(0).findElements(By.tagName("td"));
		}
		for (int i = 0; i < th.size(); i++) {

			for (String TableHeader : ColumnName) {

//				if (th.get(i).getText().equalsIgnoreCase(TableHeader)) {
				if (TableHeader.equalsIgnoreCase(th.get(i).getText().trim())) {
					postionOfTableColumn.add(i);
				}

			}
		}

		if (postionOfTableColumn.size() == 0 || ColumnName.size() != postionOfTableColumn.size()) {

			throw new Exception("Column name not found in Table " + ColumnName.toString());

		}
		WebElement actionElement = null;
		outerloop: for (int i = 1; i < tr.size(); i++) {

			List<WebElement> td = tr.get(i).findElements(By.tagName("td"));
			String tableCloumnData = "";
			for (int j = 0; j < postionOfTableColumn.size(); j++) {
				try{
					tableCloumnData += td.get(postionOfTableColumn.get(j)).getText();
				}catch(Exception e){
					tableCloumnData="";
				}
				
			}

			if (tableCloumnData.equalsIgnoreCase(tableData)) {

				WebElement actionColumn = td.get(action - 1);

				if (fieldIdentifiers.size() == 0) {

					if (actionType.equalsIgnoreCase("textbox")) {
						actionElement = actionColumn.findElement(By.cssSelector(("input[type='text']")));

					} 
					else if (actionType.equalsIgnoreCase("button")) {					

						List<WebElement> li=actionColumn.findElements(By.cssSelector(("input[type='button']")));
						
						List<WebElement> li1=actionColumn.findElements(By.tagName(("button")));
				
					
						if(li.size()>1 && !TestData.equalsIgnoreCase("click")){
							actionElement = actionColumn.findElement(By.cssSelector(("input[type='button'][value*='"+ TestData +"']")));
							
						}else if(li.size()==1) {
							
							actionElement= actionColumn.findElement(By.cssSelector(("input[type='button'][value*='"+ TestData +"']")));
						}
						
						if(li1.size()>1  && !TestData.equalsIgnoreCase("click")){
						actionElement = actionColumn.findElement(By.cssSelector(("button[text()*='"+ TestData +"']")));
							
						}else if(li1.size()==1 ){
							
							actionElement = actionColumn.findElement(By.tagName("button"));
						}

					} else if (actionType.equalsIgnoreCase("checkbox")) {

						actionElement = actionColumn.findElement(By.cssSelector(("input[type='checkbox']")));

					} else if (actionType.equalsIgnoreCase("radio")) {

						actionElement = actionColumn.findElement(By.cssSelector(("input[type='radio']")));

					} else if (actionType.equalsIgnoreCase("dropdown")) {

						actionElement = actionColumn.findElement(By.tagName(("select")));

					} else if (actionType.equalsIgnoreCase("link")) {

						actionElement = actionColumn.findElement(By.tagName("a"));

					}
				} else {
					for (int field = 0; field < fieldIdentifiers.size(); field++) {
						FieldDetails fieldIdentifier = fieldIdentifiers.get(field);
						By valueBy = findobjectBy(fieldIdentifier);
						if (actionType.equalsIgnoreCase("textbox")) {

							actionElement = actionColumn.findElement(valueBy);
						} else if (actionType.equalsIgnoreCase("button")) {

							actionElement = actionColumn.findElement(valueBy);

						} else if (actionType.equalsIgnoreCase("checkbox")) {

							actionElement = actionColumn.findElement(valueBy);

						} else if (actionType.equalsIgnoreCase("radio")) {

							actionElement = actionColumn.findElement(valueBy);

						} else if (actionType.equalsIgnoreCase("dropdown")) {

							actionElement = actionColumn.findElement(valueBy);

						} else if (actionType.equalsIgnoreCase("link")) {

							actionElement = actionColumn.findElement(valueBy);

						}
						}
				}
				TrasnactionElement = actionElement;

				break outerloop;
			}

		}
		if (actionElement == null) {

			throw new Exception("Table Column " + Controller.transactionColumnName + " " + actionType + "not found in "
					+ Controller.transactionName + "with step id" + Controller.transactionStepId);

		}
		fieldAction(actionType, TestData);

		if (windowshandel != null) {
			BrowserControls.driver.switchTo().window(windowshandel);
		}
		if (frameFalg) {
			BrowserControls.driver.switchTo().defaultContent();

		}

	}

}
