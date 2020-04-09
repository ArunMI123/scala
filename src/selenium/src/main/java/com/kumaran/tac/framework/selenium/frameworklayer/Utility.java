package com.kumaran.tac.framework.selenium.frameworklayer;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONObject;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebElement;
import com.kumaran.tac.framework.selenium.controller.Controller;
import com.kumaran.tac.framework.selenium.controller.Verification;
import com.kumaran.tac.framework.selenium.entity.Attributes;
import com.kumaran.tac.framework.selenium.entity.FieldDetails;


public class Utility extends Verification {

	public static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
	public static DateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
	static Date date = new Date();

	public static String AplliactionUrl;
	public static String AplliactionType = "";
	public static String AplliactionWindowPath = "";
	public static String AplliactionBrowserExePath = "";
	public static String tableLookupRowData = "";
	public static ArrayList<String> columnNames = new ArrayList<>();

	public static String startTime = "";
	public static String completedTime = "";
	public static int attributeId;

	public static Logger mainLogger = Logger.getLogger(Utility.class);


	public static void variableDictionary(JSONObject testDataJson) {
		JSONParser parser = new JSONParser();
		if (testDataJson.get("variables") != null) {
			String varibaleData = testDataJson.get("variables").toString();
			JSONArray variables = null;
			try {
				variables = (JSONArray) parser.parse(varibaleData);

				Iterator<JSONObject> iterator = variables.iterator();

				while (iterator.hasNext()) {
					JSONObject variable = iterator.next();

					for (Object e : variable.entrySet()) {
						Map.Entry entry = (Map.Entry) e;
						Controller.Variablesvalue.put(entry.getKey().toString(), entry.getValue().toString());

					}

				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public static String convertTestDatafromVariable(String transactionData) {

		if (transactionData.startsWith("${") && transactionData.endsWith("}")) {
			transactionData=transactionData.substring(2, transactionData.length()-1);
			transactionData = Controller.Variablesvalue.get(transactionData);
		}
		return transactionData;
	}

	public static void TransactionDataHandel(Attributes arrtibutes, JSONObject arrtibuteJson) throws Exception {

		attributeId = arrtibutes.getId();
		mainLogger.info("Traction key------" + attributeId);// and
		String transactionData = arrtibuteJson.get(String.valueOf(attributeId)) == null ? ""
				: arrtibuteJson.get(String.valueOf(attributeId)).toString();
		transactionData = Utility.convertTestDatafromVariable(transactionData);

		ArrayList<FieldDetails> fieldDeatils = arrtibutes.getFieldDetails();
		String action = arrtibutes.getType();
		String windowOrFrame = arrtibutes.getWindowOrFrame();
//		Integer wait = arrtibutes.getWaitTime();
//		boolean waitFlag = Controller.escapeWaitTime;
		Integer wait=0;
		if(arrtibutes.getFixedwait() != null){
			wait = arrtibutes.getFixedwait();			
		}
		Controller.multiElement = 0;
		Controller.frame_attr_id = arrtibutes.getFrameAttrId();

		if (transactionData != null) {
			if ((DataVerification.equals("Verification"))) {
				if (!transactionData.equalsIgnoreCase("") && !transactionData.equalsIgnoreCase("null")) {
					Controller.transactionColumnName = arrtibutes.getName();
					if (wait > 0 ) {
						Thread.sleep(wait * 1000);
					}
					Verification.dataVerificationfeed(transactionData, fieldDeatils, action, windowOrFrame);

				}
			} else {

				if (!transactionData.equalsIgnoreCase("") && !transactionData.equalsIgnoreCase("null")) {
					Controller.transactionColumnName = arrtibutes.getName();
					if (wait > 0 ) {
						//System.out.println(waitFlag);
						Thread.sleep(wait * 1000);
					}
					PageObjectHandler.datafeed(transactionData, fieldDeatils, action, windowOrFrame);
					// Need to add a Dataverification
				}
			}
		}

	}

	public static void mrbTransactionDataHandel(Attributes arrtibutes, JSONObject arrtibuteJson,
			ArrayList<FieldDetails> FieldDetailsList) throws Exception {

		String SubMrebssnew = arrtibutes.getType();
		int frameAttribuetId = arrtibutes.getId();

		String action = arrtibutes.getType();
		String windowOrFrame = arrtibutes.getWindowOrFrame();
		Controller.multiElement = 0;
		int actionField = arrtibutes.getActionField();
		Controller.frame_attr_id = arrtibutes.getParentAttrId();
		String actionColumn = arrtibutes.getColumnName();
		String Name = arrtibutes.getName();

		if (Controller.frameMap.containsKey(mrbpid)) {
			Controller.frame_attr_id = mrbpid;
			windowOrFrame = "frame";
		}

		if (Controller.frameMap.containsKey(Controller.frame_attr_id)) {
			windowOrFrame = "frame";
		}
//		Integer wait = arrtibutes.getWaitTime();
		Integer wait=0;
		if(arrtibutes.getFixedwait() != null){
			wait = arrtibutes.getFixedwait();			
		}
//		boolean waitFlag = Controller.escapeWaitTime;
		System.out.println(Name + " " + "aDJAkgdlkjgaKJDGlajkgdagd------------->" + wait);
		String transactionData = arrtibuteJson.get(String.valueOf(frameAttribuetId)) == null ? ""
				: arrtibuteJson.get(String.valueOf(frameAttribuetId)).toString();
		transactionData = Utility.convertTestDatafromVariable(transactionData);

		ArrayList<FieldDetails> fieldDetailsList = arrtibutes.getFieldDetails();

		if (SubMrebssnew.equalsIgnoreCase("COLUMNNAME")) {
			tableLookupRowData += transactionData;
			columnNames.add(Name);
		}
		if (SubMrebssnew.equalsIgnoreCase("Frame")) {
			Controller.frameMap.put(Integer.valueOf(frameAttribuetId), arrtibutes.getFieldDetails());
		}
		if (!SubMrebssnew.equalsIgnoreCase("grid"))

		{
			if (!transactionData.equalsIgnoreCase("null")) {
				if (actionField == 0 && !SubMrebssnew.equalsIgnoreCase("ColumnName")
						&& !transactionData.equalsIgnoreCase("")) {
					Controller.transactionColumnName = arrtibutes.getColumnName();
					if (wait > 0) {

						Thread.sleep(wait * 1000);
					}
					PageObjectHandler.datafeed(transactionData, fieldDetailsList, action, windowOrFrame);

				} else if (actionField > 0) {

					System.out.println("mrbgridtype : " + mrbgridtype);

					Controller.transactionColumnName = arrtibutes.getName();

					if (Controller.mrbgridtype == null) {
						Controller.transactionColumnName = arrtibutes.getName();
						if (wait > 0 ) {

							Thread.sleep(wait * 1000);
						}
						PageObjectHandler.tableAction(transactionData, columnNames, tableLookupRowData,
								FieldDetailsList, action, actionField, windowOrFrame, fieldDetailsList);
					} else {
						switch (mrbgridtype.toUpperCase()) {
						case "STANDARD":
							if (wait > 0 ) {

								Thread.sleep(wait * 1000);
							}

							PageObjectHandler.tableAction(transactionData, columnNames, tableLookupRowData,
									FieldDetailsList, action, actionField, windowOrFrame, fieldDetailsList);
							break;

						default:
							if (wait > 0 ) {

								Thread.sleep(wait * 1000);
							}
							try {
								windowOrFrame=windowOrFrame==null? "":windowOrFrame;
								Object[] obj = { transactionData, columnNames, tableLookupRowData, FieldDetailsList,
										action, actionField, windowOrFrame, fieldDetailsList };// for
																								// method1()

								Class<?> params[] = new Class[obj.length];
								for (int i = 0; i < obj.length; i++) {
									if (obj[i] instanceof Integer) {
										params[i] = Integer.TYPE;
									} else if (obj[i] instanceof String) {
										params[i] = String.class;
									} else if (obj[i] instanceof WebElement) {
										params[i] = WebElement.class;
									} else if (obj[i] instanceof ArrayList) {
										params[i] = ArrayList.class;
									}
								}

								Class<?> cv = Class.forName("Custom_Attributes");
								if (cv != null) {
									mainLogger.info("class added");
									Object objcva = cv.newInstance();
									Method cvmang = cv.getMethod("Custom_tableAction", params);
									if (objcva != null) {
										cvmang.invoke(objcva, (Object[])obj);
									}
								}
							} catch (ClassNotFoundException e) {
								System.out.println("Custom Attribute for table not exists");
							}catch (NoClassDefFoundError e){
								mainLogger.info("Cls def err");
							}

							break;
						}
					}

				}
			}
		}
		mainLogger
				.info(frameAttribuetId + "   Key of mrb filed  " + arrtibuteJson.get(String.valueOf(frameAttribuetId)));

	}

	public static String getRunDuration(String startTime, String endTime) {

		Date date1 = null, date2 = null;
		long diff = 0;
		String diffSeconds = "", diffMinutes = "", diffHours = "";

		try {
			date1 = dateFormat1.parse(startTime);
			date2 = dateFormat1.parse(endTime);
			diff = date2.getTime() - date1.getTime();
			diffSeconds = String.valueOf(diff / 1000 % 60);
			diffMinutes = String.valueOf(diff / (60 * 1000) % 60);
			diffHours = String.valueOf(diff / (60 * 60 * 1000) % 24);
			if (diffSeconds != null && diffSeconds.length() < 2) {
				diffSeconds = "0" + diffSeconds;
			}
			if (diffMinutes != null && diffMinutes.length() < 2) {
				diffMinutes = "0" + diffMinutes;
			}
			if (diffHours != null && diffHours.length() < 2) {
				diffHours = "0" + diffHours;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mainLogger
				.info("Actual time taken to execute transaction: " + diffHours + ":" + diffMinutes + ":" + diffSeconds);
		return diffHours + ":" + diffMinutes + ":" + diffSeconds;
	}

	// start Time
	public static String startTime() {

		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		startTime = formatter.format(date);

		return startTime;
	}

	// End Time
	public static String completedTime() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		completedTime = formatter.format(date);
		return completedTime;

	}

}
