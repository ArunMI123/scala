package com.kumaran.tac.framework.selenium.frameworklayer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class OpenBrowser {

	public static WebDriver driver = null;;

	public static void main(String[] args) {

		OpenBrowser lb = new OpenBrowser();
		lb.LaunchBrowser("IE", "http://www.google.com");
		lb.CloseBrowser();

	}

	public void LaunchBrowser(String LnBrowser, String URL) {

		// String Browser = "Chrome";

		String DriverPath;

		switch (LnBrowser) {

		case "Chrome":

			DriverPath = "D:\\AutomationFramework\\Lib\\chromedriver.exe";

			System.setProperty("webdriver.chrome.driver", DriverPath);

			driver = new ChromeDriver();

			DesiredCapabilities chromecaps = DesiredCapabilities.chrome();

			chromecaps.setCapability("ignoreZoomSetting", true);

			// Maximize browser
			driver.manage().window().maximize();

			// Open Google
			driver.get(URL);

			break;

		case "IE":

			DriverPath = "D:\\AutomationFramework\\Lib\\IEDriverServer.exe";

			System.setProperty("webdriver.ie.driver", DriverPath);

			DesiredCapabilities iecaps = DesiredCapabilities.internetExplorer();

			iecaps.setCapability("handlesAlerts", true);

			iecaps.setCapability("ignoreZoomSetting", true);

			iecaps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

			iecaps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

			iecaps.setCapability("requireWindowFocus", true);
			iecaps.setCapability("enablePersistentHover", false);

			driver = new InternetExplorerDriver();

			// Maximize browser
			driver.manage().window().maximize();

			// Open Google
			driver.get(URL);

			break;

		case "Firefox":

			driver = new FirefoxDriver();

			// Maximize browser
			driver.manage().window().maximize();

			// Open Google
			driver.get(URL);

			break;

		}

	}

	public void CloseBrowser() {

		driver.close();
		driver.quit();

	}

}
