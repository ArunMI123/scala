package com.kumaran.tac.framework.selenium.frameworklayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.kumaran.tac.framework.selenium.controller.Controller;

public class BrowserControls {

	public static WebDriver driver = null;

	public static Logger mainLogger = Logger.getLogger(BrowserControls.class);
	// Open Differnt type of browser
	public static void OpenBrowser(String BrowserName) throws IOException {
		// get current project path

		if (Utility.AplliactionType.equalsIgnoreCase("WEB")) {

			//Debugging purpose code//
			mainLogger.info("BrowserName" + BrowserName);
			
			switch (BrowserName.toUpperCase()) {
			case "INTERNET EXPLORER":
				Properties p = new Properties();
			
			
				//********************Selenium Debugging code to use -  below block of code************				
				
				/*InputStream fin = new FileInputStream(new File("D:\\Selenium\\config.properties"));
				p.load(fin);
				String ieDriverPath = p.getProperty("ieDriverPath");
				Utility.AplliactionBrowserExePath = ieDriverPath+"IEDriverServer.exe";*/
				
				//********************Selenium Runnable jar code to use -  below block of code************
				
				String externalFileName = System.getProperty("config.location");
				
				 InputStream fin = new FileInputStream(new File(externalFileName));
				p.load(fin);
				String ieDriverPath = p.getProperty("ieDriverPath");
				Utility.AplliactionBrowserExePath = ieDriverPath+"IEDriverServer.exe";
				
				//********************Selenium Runnable jar code to use - End of block of code************
				
				
				mainLogger.info("IEDRIVER" + Utility.AplliactionBrowserExePath);
				
				System.setProperty("webdriver.ie.driver", Utility.AplliactionBrowserExePath);
				InternetExplorerOptions capab = new InternetExplorerOptions();
				capab.setCapability("handlesAlerts", true);
				capab.setCapability("ignoreZoomSetting", true);
				capab.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
				capab.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
				capab.setCapability("requireWindowFocus", true);
				capab.setCapability("enablePersistentHover", false);
				capab.setCapability("nativeEvents", true);
				driver = new InternetExplorerDriver(capab);
				driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL, "0"));
				driver.manage().window().setSize(new Dimension(1280, 1024));
				driver.manage().window().maximize();
				driver.get(Utility.AplliactionUrl);
				break;
			case "CHROME":

			
 
             //********************Selenium Runnable jar code to use -  below block of code************
         	
                Properties prop = new Properties();
				String externalFileNameC = System.getProperty("config.location");
				
				 InputStream Chromefin = new FileInputStream(new File(externalFileNameC));
				 prop.load(Chromefin);
				String ChromeDriverPath = prop.getProperty("ChromeDriverPath");
				
				
				//********************Selenium Debugging code to use -  below block of code************
			
				 
					/*Properties pc = new Properties();
				 InputStream finc = new FileInputStream(new File("D:\\Selenium\\config.properties"));
				pc.load(finc);
				String ChromeDriverPath = pc.getProperty("ChromeDriverPath");*/
				
				
				//*****************************Common Block *********************************************
				Utility.AplliactionBrowserExePath = ChromeDriverPath+"chromedriver.exe";
				mainLogger.info("CHROMEdIVER" + Utility.AplliactionBrowserExePath);
				System.setProperty("webdriver.chrome.driver", Utility.AplliactionBrowserExePath);
				ChromeOptions chromecap = new ChromeOptions();
				chromecap.setCapability("handlesAlerts", false);
				driver = new ChromeDriver(chromecap);
				//driver.manage().window().maximize();
				driver.get(Utility.AplliactionUrl);
				driver.manage().window().maximize();
				break;
			case "FIREFOX":
				

				

	             //********************Selenium Runnable jar code to use -  below block of code************
	         	
	                Properties props = new Properties();
					String externalFileNameF = System.getProperty("config.location");
					
					 InputStream FireFoxfin = new FileInputStream(new File(externalFileNameF));
					 props.load(FireFoxfin);
					String FireFoxDriverPath = props.getProperty("FireFoxDriverPath");
					
					
					//********************Selenium Debugging code to use -  below block of code************
				
					 
						/*Properties pf = new Properties();
					 InputStream finf = new FileInputStream(new File("D:\\Selenium\\config.properties"));
					pf.load(finf);
					String FireFoxDriverPath = pf.getProperty("FireFoxDriverPath");*/
					
					
					//*****************************Common Block *********************************************
					Utility.AplliactionBrowserExePath = FireFoxDriverPath+"geckodriver.exe";
					mainLogger.info("FFXDRIVER" + Utility.AplliactionBrowserExePath);
				System.setProperty("webdriver.gecko.driver", Utility.AplliactionBrowserExePath);
				FirefoxOptions fireFoxCap = new FirefoxOptions();
				fireFoxCap.setCapability("handlesAlerts", false);
				driver = new FirefoxDriver(fireFoxCap);
				driver.manage().window().maximize();
				driver.get(Utility.AplliactionUrl);
				break;
			
			default:
				try{
					
					 Object[] obj = {BrowserName};// for method1()
					 				 
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
					 Method fam =  fa.getMethod("CustomBrowser",params);
					 if(fam!=null){
					
						 System.out.println("CustomBrowse");
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

	}
	// Close Differnt type of browser
	public static void closeBrowser(String BrowserName){
		if (!BrowserName.equalsIgnoreCase("firefox")) {
			BrowserControls.driver.close();
		}
		BrowserControls.driver.quit();
		try {
			switch(BrowserName.toUpperCase()){
			case "INTERNET EXPLORER":
				Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
				Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
			break;
			case "CHROME":
				Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
			break;			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	// hightLight the object in Webpage
	public static void highlightElements(WebElement element, String ActionColor) {
		String color = "";

		if (ActionColor.equalsIgnoreCase("feed") || ActionColor.equalsIgnoreCase("Pass")) {
			color = "green";

		} else if (ActionColor.equalsIgnoreCase("Fail")) {
			color = "red";

		} else if (ActionColor.equalsIgnoreCase("Table")) {

			color = "yellow";

		}
		 else if (ActionColor.equalsIgnoreCase("Verification")) {

				color = "blue";

			}

		if (driver instanceof JavascriptExecutor) {

			((JavascriptExecutor) driver).executeScript("arguments[0].style.border='1px solid " + color + "'", element);

		}

	}

	// wait Untill pageLoding Completed
	public static boolean waitPageloadComplete() {

		try {

			ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
				@Override
				public Boolean apply(WebDriver driver) {

					return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");

				}
			};
			Integer timeOut = 0;
			if(Controller.attributeMaxWait !=null ){
				timeOut = Controller.attributeMaxWait;
			}else{
				timeOut = 40;
			}
			Wait<WebDriver> wait = new WebDriverWait(BrowserControls.driver, timeOut);
			wait.until(expectation);
			wait = null;
			expectation = null;

			return true;

		} catch (Exception e) {
			mainLogger.info(e.getMessage());
		}
		return false;

	}

	// Explicit wait for 45 sec till object to be found
	public static void explicitWaitForObjectFoundBy(By by) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 10);

			wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		}catch (ElementNotVisibleException e) {
			PageObjectHandler.failureReason = "element not visible";
		} catch (Exception e) {


		}

	}

	public static void explicitWaitForObjectFound(WebElement element) {
		try {
			Integer timeOut =0;
			if(Controller.attributeMaxWait != null && Controller.attributeMaxWait != 0){
				timeOut = Controller.attributeMaxWait;
			}else {
				timeOut = 45;
			}
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf(element));
		}catch (TimeoutException e){
			PageObjectHandler.failureReason = "wait timeout element not visible";
			throw new TimeoutException();
		}
		catch (ElementNotVisibleException e) {
			PageObjectHandler.failureReason = "element not visible";
			throw new ElementNotVisibleException("element not visible");
		}
	}


	public static String multipleWindowsHandeling(String windowPos) {

		int i = 1;
		int switchWindow = Integer.valueOf(windowPos);
		String parentwinow = driver.getWindowHandle();

		Set<String> allWindow = driver.getWindowHandles();

		for (String window : allWindow) {

			if (switchWindow == i) {
				driver.switchTo().window(window);

				break;
			}
			i++;
		}

		return parentwinow;

	}

	// Switch to one window to another window
	public static String WindowsHandeling() {

		String parentwinow = driver.getWindowHandle();

		Set<String> allWindow = driver.getWindowHandles();
		while (allWindow.size() > 0) {

			for (String window : allWindow) {

				if (!parentwinow.equalsIgnoreCase(window)) {
					driver.switchTo().window(window);

					break;
				}

			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			if (allWindow.size() > 1) {
				break;

			}
			allWindow = driver.getWindowHandles();
		}
		mainLogger.info("window size--------------->" + allWindow.size());
		return parentwinow;

	}

}
