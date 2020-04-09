package com.kumaran.tac.agent.service;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumaran.tac.agent.controller.AgentController;
import com.kumaran.tac.agent.model.Agent;
import com.kumaran.tac.agent.model.ScriptList;
import com.kumaran.tac.agent.model.ValidationModel;
import com.kumaran.tac.agent.util.ExecuteFramework;
import com.kumaran.tac.agent.util.FileUnzipUtil;
import com.kumaran.tac.agent.util.FrameworkQueueDetails;
import com.kumaran.tac.agent.util.LoadConfigFile;


@Component
@Service
public class AgentServiceImpl implements AgentService {

	@Autowired
	private Environment env;
	
	@Autowired
	FrameworkQueueDetails framworkQueueDetail;
	
	
	private static ExecutorService executor = Executors.newCachedThreadPool();
	
	private static final String filePath = AgentController.home + "/agent/agent.properties";
	
	private static final Logger logger = LoggerFactory.getLogger(AgentServiceImpl.class);

	public RestTemplate SSLConnection() {
		try {
			SSLContextBuilder SSLBuilder = SSLContexts.custom();
			ClassPathResource url = new ClassPathResource("kumaran_2019.jks");
			SSLBuilder = SSLBuilder.loadTrustMaterial(url.getURL(), "Welcome@321".toCharArray());
			SSLContext sslcontext = SSLBuilder.build();
			SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslcontext,
					new NoopHostnameVerifier());
			HttpClientBuilder clientbuilder = HttpClients.custom();
			clientbuilder = clientbuilder.setSSLSocketFactory(sslConSocFactory);
			CloseableHttpClient httpclient = clientbuilder.build();
			final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
			requestFactory.setHttpClient(httpclient);
			return new RestTemplate(requestFactory);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	List<ScriptList> scriptList = new ArrayList<ScriptList>();
	public Map<String, Object> validationData = new HashMap<String, Object>();

	@Override
	public byte[] getScreenshot() {
		try {
			System.setProperty("java.awt.headless", "false");
			Thread.sleep(120);
			Robot rbt = new Robot();
			Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage Image = rbt.createScreenCapture(capture);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(Image, "jpg", baos);
			// InputStream inputStrm = new ByteArrayInputStream(baos.toByteArray());
			baos.flush();
			baos.close();
			return baos.toByteArray();
		} catch (AWTException | InterruptedException | IOException ex) {
			System.out.println(ex);
		}
		return null;
	}

	@Override
	public String getTestScriptData(String machinestype) {

		// Map<Integer, HashMap> excelValue = poiExcelRead.excelReader();
		// for (Entry<Integer, HashMap> entry : excelValue.entrySet()) {
		// HashMap<String, String> keyValue = entry.getValue();
		// if(keyValue.get("ScriptType").equalsIgnoreCase(machinestype) &&
		// keyValue.get("Status").equals("Not Completed")){
		// return keyValue.get("ID")+" script id to process";
		// } else if( !keyValue.get("ScriptType").equalsIgnoreCase(machinestype) &&
		// keyValue.get("Status").equals("Not Completed")){
		// return env.getProperty("status.pending");
		// }
		// }
		// return env.getProperty("status.teriminate");

		int index = 0;
		for (ScriptList obj : scriptList) {
			if (obj.getScriptType().equalsIgnoreCase(machinestype)
					&& obj.getStatus().equalsIgnoreCase("Not Completed")) {
				if (index == 0) {
					return obj.getId() + " script id to process";
				} else {
					if (scriptList.get(index - 1).getStatus().equalsIgnoreCase("Completed")) {
						return obj.getId() + " script id to process";
					} else {
						return env.getProperty("status.pending");
					}
				}
			}
			index++;
		}
		return env.getProperty("status.teriminate");
	}

	@Override
	public String addScriptvale() {
		ScriptList val = new ScriptList();
		val.setId("TCO1");
		val.setScriptType("QTP");
		val.setStatus("Not Completed");
		scriptList.add(val);
		val = new ScriptList();
		val.setId("TCO2");
		val.setScriptType("QTP");
		val.setStatus("Not Completed");
		scriptList.add(val);
		val = new ScriptList();
		val.setId("TCO3");
		val.setScriptType("Selinium");
		val.setStatus("Not Completed");
		scriptList.add(val);
		val = new ScriptList();
		val.setId("TCO4");
		val.setScriptType("QTP");
		val.setStatus("Not Completed");
		scriptList.add(val);
		val = new ScriptList();
		val.setId("TCO5");
		val.setScriptType("Selinium");
		val.setStatus("Not Completed");
		scriptList.add(val);

		return "success";
	}

	@Override
	public String updateScriptStatus() {
		if (!scriptList.isEmpty()) {
			int index = 0;
			for (ScriptList obj : scriptList) {
				if (obj.getStatus().equalsIgnoreCase("Not Completed")) {
					scriptList.get(index).setStatus("Completed");
					return "updated";
				}
				index++;
			}
		}
		return "All scripts are completed";
	}

	@Override
	public int frameworkUpload(MultipartFile file) {
		try {

			byte[] srcBytes = file.getBytes();

			String destpath = env.getProperty("fileDownloadPath");
			String srcPath = env.getProperty("zipSrcPath");

			File destFile = new File(destpath + "/" + file.getOriginalFilename());
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destFile));
			stream.write(srcBytes);
			stream.close();

			// FileUnzipUtil.unzip(srcPath, destpath);

			return 1;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public void startCodedUi() {
		//try {
			String path = env.getProperty("batFilepath");
			String fileName = env.getProperty("batFileName");
			//String commandLine = "cmd.exe /c cd \"" + path + "\" &start  " + fileName;
			String[] commandLine = new String[] { "/c", "cd", "\"" + path + "\"", "&", "start", fileName}; 
			//Process proc = Runtime.getRuntime().exec(commandLine);
			//proc.waitFor();
			new Thread( new ExecuteFramework( "CODEDUI", "cmd.exe", commandLine)).start();
		//} catch (IOException | InterruptedException ex) {
		//	ex.printStackTrace();
		//}
	}

	@Override
	public void startQtp(HashMap<String, byte[]> customFiles) {
		try {
			// delete previous files and unzip the new custom files.
			String customFilesPath = env.getProperty("customFilesPath");
			FileUnzipUtil.deleteFileWithExtension(customFilesPath, ".qfl");
			extractCustomFiles(customFiles, customFilesPath);

			// start qtp
			String qptScrtPath = env.getProperty("qtpScrtPath");
			Runtime.getRuntime().exec("wscript " + qptScrtPath);

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public void startSelenium(String testRunId, String customFilesPath) {
		try {
			String jarPath = env.getProperty("seleniumServerPath");
			String propertyPath = env.getProperty("propertyPath");
			String mainClassName = env.getProperty("mainClass");
			
			
//			String commandLine = " -Dconfig.location=" + propertyPath + " -cp \"" + jarPath + "\";" + "\""
//					+ customFilesPath + "\" " + mainClassName + " " + testRunId;
			String[] commandLine = new String[] { "-Dconfig.location=" + propertyPath, "-cp", jarPath + File.pathSeparator + customFilesPath, mainClassName, testRunId};
			System.out.println(commandLine);
		  
			//Process proc = Runtime.getRuntime().exec(commandLine);
			new Thread( new ExecuteFramework( "API", "java", commandLine)).start();
//			
//			// Then retreive the process output
//			InputStream in = proc.getInputStream();
//			InputStream err = proc.getErrorStream();
//
//			System.out.println("Input stream ---" + IOUtils.toString(in));
//			System.out.println("Error stream ---" + IOUtils.toString(err));

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	@Override
	public void invokeSelenium(String testRunId, HashMap<String, byte[]> customFiles, String uniqueProjectId) {
		String customFilesPath = env.getProperty("customFilesPath") + "\\" + uniqueProjectId;
		try {
			  File directory = new File(env.getProperty("customFilesPath"));
			    if (! directory.exists()){
			        directory.mkdir();
			    }
			    
			FileUnzipUtil.deleteFileWithExtension(customFilesPath, ".class");
			extractCustomFiles(customFiles, customFilesPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startSelenium(testRunId, customFilesPath);
	}

	private void extractCustomFiles(HashMap<String, byte[]> customFiles, String customFilesPath) throws IOException {

		byte[] identifierFile = customFiles.get("identifier");
		byte[] validationFile = customFiles.get("validation");

		if (identifierFile != null) {
			FileUnzipUtil.unzip(identifierFile, customFilesPath);
		}
		if (validationFile != null) {
			FileUnzipUtil.unzip(validationFile, customFilesPath);
		}

	}

	@Override
	public ResponseEntity<String> getTestRunOR(String testRunId) {
		ResponseEntity<String> completeORJson = null;
		try {
			if (!testRunId.isEmpty()) {
				RestTemplate rest = SSLConnection();
				String serverWebURL = env.getProperty("serverWebURL");
				completeORJson = rest.getForEntity(serverWebURL + "/v1/testRunOR/" + testRunId, String.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return completeORJson;
	}

	@Override
	public ResponseEntity<String> getTestStepData(String testRunId) throws Exception {
		ResponseEntity<String> nextTestStepJson = null;
		try {
			if (!testRunId.isEmpty()) {
				RestTemplate rest = SSLConnection();
				String serverWebURL = env.getProperty("serverWebURL");
				nextTestStepJson = rest.getForEntity(serverWebURL + "/v1/nextTestStep/" + testRunId, String.class);
				formValidationJson(nextTestStepJson);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextTestStepJson;
	}

	@Override
	public void saveTestStepResult(HashMap<String, String> testStepResult) throws Exception {
		try {
			RestTemplate rest = SSLConnection();
			String serverWebURL = env.getProperty("serverWebURL");
			rest.postForObject(serverWebURL + "/v1/saveTestStepResult/", testStepResult, String.class);
			validationData = new HashMap<String, Object>();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object saveFailScreenshot(String testStepResultId, byte[] imageArray) throws Exception {
		try {
			RestTemplate rest = SSLConnection();
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.MULTIPART_FORM_DATA);
			String serverWebURL = env.getProperty("serverWebURL");
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("testStepResultId", testStepResultId);
			body.add("file", getUserFileResource(imageArray));
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
					body, header);
			// restTemplate.postForObject("http://" + serverWebURL +
			// "/v1/saveFailScreenshot/", map, String.class);
			validationData = new HashMap<String, Object>();
			return rest.postForObject(serverWebURL + "/v1/saveFailScreenshot/", requestEntity, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Resource getUserFileResource(byte[] byteArray) throws IOException {
		// todo replace tempFile with a real file
		Path tempFile = Files.createTempFile("test", ".jpg");
		Files.write(tempFile, byteArray);
		File file = tempFile.toFile();
		// to upload in-memory bytes use ByteArrayResource instead
		return new FileSystemResource(file);
	}

	@Override
	public ResponseEntity<String> getTestRunId() throws Exception {
		ResponseEntity<String> testRunId = null;
		try {
			RestTemplate rest = SSLConnection();
			String serverWebURL = env.getProperty("serverWebURL");
			testRunId = rest.getForEntity(serverWebURL + "/v1/getAgentCurrentRunId", String.class);

			System.out.println(": testRunId : " + testRunId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return testRunId;
	}

	@Override
	public void startAPI(String testRunId) {
		// TODO Auto-generated method stub
		String apiJarPath = env.getProperty("apiJarPath");
		String propertyPath = env.getProperty("propertyPath");
		String apiMainClass = env.getProperty("apimainClass");
//		try {
//			String commandLine = " -Dconfig.location=" + propertyPath + " -cp " + apiJarPath
//					+ "; " + apiMainClass + " " + testRunId;
			String[] commandLine = new String[] { "-Dconfig.location=" + propertyPath, "-cp ", apiJarPath, apiMainClass, testRunId};
			System.out.println( commandLine);
			//Process proc = Runtime.getRuntime().exec(commandLine);
			// Process proc = Runtime.getRuntime().exec("java -jar " + apiJarPath + " " +
			// testRunId);
			new Thread( new ExecuteFramework( "API", "java", commandLine)).start();
//			
//			// Then retreive the process output
//			InputStream in = proc.getInputStream();
//			InputStream err = proc.getErrorStream();
//
//			System.out.println("Input stream ---" + IOUtils.toString(in));
//			System.out.println("Error stream ---" + IOUtils.toString(err));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void formValidationJson(ResponseEntity<String> nextTestStepJson) {
		JSONObject validations = null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> variableList = null;
		try {
			JSONParser parser = new JSONParser();
			JSONObject nextCommandJSON = (JSONObject) parser.parse(nextTestStepJson.getBody());
			System.out.println(nextCommandJSON);
			
			/*if (nextCommandJSON.get("attributes") != null) {
				List<Map<String, Object>> attribute = mapper.readValue(nextCommandJSON.get("attributes").toString(),
						new TypeReference<List<Map<String, Object>>>() {
						});
				if (nextCommandJSON.get("variablePair") != null) {
					variableList = mapper.readValue(nextCommandJSON.get("variablePair").toString(),
							new TypeReference<Map<String, String>>() {
							});
				}
				for (int attrSize = 0; attrSize < attribute.size(); attrSize++) {
					
					validations = null;
					HashMap<String, Object> singleTestDataJsonObject = (HashMap<String, Object>) attribute
							.get(attrSize);
					validations =  (JSONObject) parser.parse(mapper.writeValueAsString(singleTestDataJsonObject.get("validations")));
					validationData.put("validations", validations);
					validationData.put("transactionId", nextCommandJSON.get("transactionId").toString());
					validationData.put("testRunDetailID", nextCommandJSON.get("testRunStepId").toString());
					validationData.put("variableList", variableList);
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, String> getValidationStatus(HashMap<String, String> actualVal) {
		List<ValidationModel> ValidData = new ArrayList<ValidationModel>();
		JSONObject ValData = null;
		ValidationModel expectVal = null;
		ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> saveValidationResult = new HashMap<String, Object>();
		try {
			if (actualVal.get("transactionId").equals(validationData.get("transactionId"))) {
				if (validationData.get("validations") != null) {
					ValData = (JSONObject) validationData.get("validations");
					ValidData=  Arrays.asList(mapper.readValue(ValData.get(actualVal.get("ValidationPosition").toString()).toString(),ValidationModel[].class));
					expectVal = findValidationObject(actualVal.get("validationStatusId"), ValidData);
					if (expectVal != null) {
						saveValidationResult = basicValInfo(expectVal, actualVal);
						comparisionMethod(expectVal, actualVal, saveValidationResult);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return actualVal;
	}

	private void comparisionMethod(ValidationModel expectVal, HashMap<String, String> actualVal,
			HashMap<String, Object> saveValidationResult) throws Exception {
		String validationStatus = null;
		if (actualVal.get("comparisonType").equalsIgnoreCase("equals")) {
			commonKeys(expectVal, actualVal, saveValidationResult);
			if(!actualVal.containsKey("status")){
				validationStatus = checkEquivalences(actualVal.get("value"), expectVal.getExpectedValue());
				actualVal.put("status", validationStatus);
			}
			saveScreen(saveValidationResult, validationStatus);
		} else if (actualVal.get("comparisonType").equalsIgnoreCase("contains")) {
			commonKeys(expectVal, actualVal, saveValidationResult);
			if(!actualVal.containsKey("status")){
			validationStatus = checkContains(actualVal.get("value"), expectVal.getExpectedValue());
			actualVal.put("status", validationStatus);
			}
			saveScreen(saveValidationResult, validationStatus);
		} else if (actualVal.get("comparisonType").equalsIgnoreCase("lesserThan")) {
			commonKeys(expectVal, actualVal, saveValidationResult);
			if(!actualVal.containsKey("status")){
			validationStatus = checkLesserThan(actualVal.get("value"), expectVal.getExpectedValue());
			actualVal.put("status", validationStatus);
			}
			saveScreen(saveValidationResult, validationStatus);
		} else if (actualVal.get("comparisonType").equalsIgnoreCase("greaterThan")) {
			commonKeys(expectVal, actualVal, saveValidationResult);
			if(!actualVal.containsKey("status")){
			validationStatus = checkGreaterThan(actualVal.get("value"), expectVal.getExpectedValue());
			actualVal.put("status", validationStatus);
			}
			saveScreen(saveValidationResult, validationStatus);
		} else if (actualVal.get("comparisonType").equalsIgnoreCase("greaterOrEqual")) {
			commonKeys(expectVal, actualVal, saveValidationResult);
			if(!actualVal.containsKey("status")){
			validationStatus = checkGreaterOrEqual(actualVal.get("value"), expectVal.getExpectedValue());
			actualVal.put("status", validationStatus);
			}
			saveScreen(saveValidationResult, validationStatus);
		} else if (actualVal.get("comparisonType").equalsIgnoreCase("lesserOrEqual")) {
			commonKeys(expectVal, actualVal, saveValidationResult);
			if(!actualVal.containsKey("status")){
			validationStatus = checkLesserOrEqual(actualVal.get("value"), expectVal.getExpectedValue());
			actualVal.put("status", validationStatus);
			}
			saveScreen(saveValidationResult, validationStatus);
		} else if (actualVal.get("comparisonType").equalsIgnoreCase("notEqual")) {
			commonKeys(expectVal, actualVal, saveValidationResult);
			if(!actualVal.containsKey("status")){
			validationStatus = checkNotEqual(actualVal.get("value"), expectVal.getExpectedValue());
			actualVal.put("status", validationStatus);
			}
			saveScreen(saveValidationResult, validationStatus);
		}else if (actualVal.get("comparisonType").equalsIgnoreCase("none")) {
			if(!actualVal.containsKey("status")){
			actualVal.put("status", "pass");
			}
			saveScreen(saveValidationResult, validationStatus);
		}
	}

	private void saveScreen(HashMap<String, Object> saveValidationResult, String validationStatus) throws Exception {
		saveValidationResult.put("status", validationStatus);
		String validationId = sendSaveRequest(saveValidationResult);
		saveValidationFailScreenshot(validationId, validationStatus);
	}

	private void commonKeys(ValidationModel expectVal, HashMap<String, String> actualVal,
			HashMap<String, Object> saveValidationResult) throws Exception {
		if (expectVal.getVariableName() != null){
		saveValidationResult.put("outputValue", actualVal.get("value"));
		saveValidationResult.put("inputValue", expectVal.getExpectedValue());
		saveValidationResult.put("keyValuePair",
				formVariableValue(expectVal.getVariableName(), actualVal.get("value")));
		}
	}

	/*@SuppressWarnings("unchecked")
	private HashMap<String, Object> failStatus(Map<String, Object> validationData,
			HashMap<String, Object> saveValidationResult) throws Exception {
		List<Integer> preValidationId = new ArrayList<Integer>();
		List<Integer> postValidationId = new ArrayList<Integer>();
		List<ValidationModel> preValidData = (List<ValidationModel>) validationData.get("preValidation");
		List<ValidationModel> postValidData = (List<ValidationModel>) validationData.get("postValidation");
		for (ValidationModel preObj : preValidData) {
			preValidationId.add(preObj.getValidationId());
		}
		for (ValidationModel postObj : postValidData) {
			postValidationId.add(postObj.getValidationId());
		}
		saveValidationResult.put("preData", preValidationId);
		saveValidationResult.put("postData", postValidationId);
		return saveValidationResult;
	}*/
	
	private HashMap<String, Object> basicValInfo(ValidationModel expectVal, HashMap<String, String> actualVal)throws Exception {
		HashMap<String, Object> valData = new HashMap<String, Object>();
		valData.put("position", actualVal.get("ValidationPosition"));
		valData.put("transactionValidationId", expectVal.getValidationId());
		valData.put("testRunDetailID", validationData.get("testRunDetailID"));
		valData.put("expectValue", expectVal.getExpectedValue());
		valData.put("actualValue", actualVal.get("value"));
		return valData;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, String> formVariableValue(String variableName, String ExpectVal) {
		HashMap<String, String> variables = (HashMap<String, String>) validationData.get("variableList");
		if (validationData.get("variableList") != null) {
			variables.put(variableName, ExpectVal);
		}
		return variables;
	}

	private String sendSaveRequest(HashMap<String, Object> saveValidationResult) {
		RestTemplate rest = SSLConnection();
		String serverWebURL = env.getProperty("serverWebURL");
		String result = rest.postForObject(serverWebURL + "/v1/saveTestStepResultValidation/", saveValidationResult,
				String.class);
		return result;
	}

	private String checkEquivalences(String validationVal, String expectedValue) {
		if (validationVal.equalsIgnoreCase(expectedValue)) {
			return "pass";
		} else {
			return "fail";
		}
	}

	private String checkContains(String validationVal, String expectedValue) {
		if (validationVal.contains(expectedValue)) {
			return "pass";
		} else {
			return "fail";
		}
	}

	private String checkLesserThan(String validationVal, String expectedValue) {
		int actual = Integer.parseInt(validationVal);
		int expect = Integer.parseInt(expectedValue);
		if (actual < expect) {
			return "pass";
		} else {
			return "fail";
		}
	}

	private String checkGreaterThan(String validationVal, String expectedValue) {
		int actual = Integer.parseInt(validationVal);
		int expect = Integer.parseInt(expectedValue);
		if (actual > expect) {
			return "pass";
		} else {
			return "fail";
		}
	}

	private String checkGreaterOrEqual(String validationVal, String expectedValue) {
		int actual = Integer.parseInt(validationVal);
		int expect = Integer.parseInt(expectedValue);
		if (actual >= expect) {
			return "pass";
		} else {
			return "fail";
		}
	}

	private String checkLesserOrEqual(String validationVal, String expectedValue) {
		int actual = Integer.parseInt(validationVal);
		int expect = Integer.parseInt(expectedValue);
		if (actual <= expect) {
			return "pass";
		} else {
			return "fail";
		}
	}

	private String checkNotEqual(String validationVal, String expectedValue) {
		if (!validationVal.equalsIgnoreCase(expectedValue)) {
			return "pass";
		} else {
			return "fail";
		}
	}

	private ValidationModel findValidationObject(String validationStatusId, List<ValidationModel> ValidData) {
		for (ValidationModel obj : ValidData) {
			if (Integer.parseInt(validationStatusId) == obj.getValidationId()) {
				return obj;
			}
		}
		return null;
	}

	public Object saveValidationFailScreenshot(String validationId, String status) throws Exception {
		try {
			if ("fail".equalsIgnoreCase(status)) {
				RestTemplate rest = SSLConnection();
				byte[] imageArray = getScreenshot();
				HttpHeaders header = new HttpHeaders();
				header.setContentType(MediaType.MULTIPART_FORM_DATA);
				String serverWebURL = env.getProperty("serverWebURL");
				MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
				body.add("validationId", validationId);
				body.add("file", getUserFileResource(imageArray));
				HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(
						body, header);
//				validationData = new HashMap<String, Object>();
				return rest.postForObject(serverWebURL + "/v1/saveValidationScreenshot/", requestEntity, String.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void saveResponseData(HashMap<String, Object> responseDataJson) {
		try {
			RestTemplate rest = SSLConnection();
			responseDataJson.put("testRunDetailID", validationData.get("testRunDetailID"));
			String serverWebURL = env.getProperty("serverWebURL");
			rest.postForObject(serverWebURL + "/v1/saveResponseData/", responseDataJson, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public HashMap<String, String> saveAgentDetail(Agent agentInfo) throws Exception {
		RestTemplate rest = SSLConnection();
		ObjectMapper mapper = new ObjectMapper();
		String serverURL = agentInfo.getServerURL();
		String url = serverURL + "/v1/saveAgentFromAgent";
		HashMap<String, Object> sendAgentMap = new HashMap<>();
		sendAgentMap.put("agentName", agentInfo.getAgentName());
		sendAgentMap.put("agentURL", agentInfo.getAgentURL());
		sendAgentMap.put("serverURL", serverURL);
		String agent = rest.postForObject(url, sendAgentMap, String.class);
		System.out.println("agent:"+agent);
		HashMap<String, String> savedAgentDetail = mapper.readValue(agent,
				new TypeReference<HashMap<String, String>>() {
				});
		if (savedAgentDetail != null) {
			File file = new File(filePath);
			createPropertyFile(file);
			writePropertyFile(savedAgentDetail, file);
		}
		return savedAgentDetail;

	}
	
	private void createPropertyFile(File file) throws Exception {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdir();
		}
		Files.deleteIfExists(file.toPath());
		file.createNewFile();

	}

	private void writePropertyFile(HashMap<String, String> dataToWrite, File file) throws Exception {
		PropertiesConfiguration config = new PropertiesConfiguration(file);
		for (Map.Entry<String, String> keyVal : dataToWrite.entrySet()) {
			config.addProperty(keyVal.getKey(), keyVal.getValue());
		}
		config.save();
	}

	@Override
	public String getTeststepData(int agentId) throws Exception {
		RestTemplate rest = SSLConnection();
		String serverWebURL = null;
		String agentURL = LoadConfigFile.getInstance().getPropertyAsString("serverUrl");
		if(agentURL!=null) {
			serverWebURL = agentURL+"/v1/getTeststepDataByAgentId/"+agentId;
//			String agentUrl = "https://localhost:8080/v1/getTeststepDataByAgentId/"+agentId;
			return rest.getForObject(serverWebURL, String.class, agentId);
		}
		return null;
	}
	
	
	private  void startSelenium() {
		try {
			String jarPath = env.getProperty("seleniumServerPath");
			String propertyPath = env.getProperty("propertyPath");
			String mainClassName = env.getProperty("mainClass");
			//String commandLine = " -Dconfig.location=" + propertyPath + " -cp \"" + jarPath + "\" " + mainClassName;
			String[] commandLine = new String[] { "-Dconfig.location=" + propertyPath, "-cp", jarPath, mainClassName};
			System.out.println(commandLine);
			//Runtime.getRuntime().exec(commandLine);
			new Thread( new ExecuteFramework( "SELENIUM", "java", commandLine)).start();
			logger.info("Started Selenium ");
//			logger.info("InputStream:"+IOUtils.toString(proc.getInputStream()));
//			logger.info("ErrorStream:"+IOUtils.toString(proc.getErrorStream()));

			// Then retreive the process output
//			InputStream in = proc.getInputStream();
//			InputStream err = proc.getErrorStream();

//			System.out.println("Input stream ---" + IOUtils.toString(in));
//			System.out.println("Error stream ---" + IOUtils.toString(err));
			
//			System.out.println("Input stream ---" + IOUtils.toString(proc.getInputStream()));
//			System.out.println("Error stream ---" + IOUtils.toString(proc.getErrorStream()));

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	
	private void startQtp(){
			try {
				// delete previous files and unzip the new custom files.
//				String customFilesPath = env.getProperty("customFilesPath");
//				FileUnzipUtil.deleteFileWithExtension(customFilesPath, ".qfl");
//				extractCustomFiles(customFiles, customFilesPath);

				// start qtp
				String qtpScrtPath = env.getProperty("qtpScrtPath");
//				Process proc = Runtime.getRuntime().exec("wscript " + qptScrtPath);
//				proc.waitFor();
				//String commandLine = " " + qptScrtPath;
				String[] commandLine = new String[] { qtpScrtPath};
				//Runtime.getRuntime().exec(commandLine);
				new Thread( new ExecuteFramework( "QTP", "wscript", commandLine)).start();
				logger.info("Started Qtp");
				
			} catch (Exception ex) {
				System.out.println(ex);
			}
	}
	
	
	private  void startAPI() {
		// TODO Auto-generated method stub
		String apiJarPath = env.getProperty("apiJarPath");
		String propertyPath = env.getProperty("propertyPath");
		String apiMainClass = env.getProperty("apimainClass");
		try {
			String[] commandLine = new String[] { "-Dconfig.location=" + propertyPath, "-cp", apiJarPath, apiMainClass};
			System.out.println(commandLine);
			//Process proc = Runtime.getRuntime().exec(commandLine);
			new Thread( new ExecuteFramework( "API", "java", commandLine)).start();
			// Process proc = Runtime.getRuntime().exec("java -jar " + apiJarPath + " " +
			// testRunId);

			// Then retreive the process output
			//InputStream in = proc.getInputStream();
			//InputStream err = proc.getErrorStream();

//			System.out.println("Input stream ---" + IOUtils.toString(in));
//			System.out.println("Error stream ---" + IOUtils.toString(err));
			//System.out.println("Input stream ---" + in);
			//System.out.println("Error stream ---" + err);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void invokeFramework(String tool) {
		if ("selenium".equalsIgnoreCase(tool)) {
			startSelenium();
		} else if ("uft".equalsIgnoreCase(tool)) {
			startQtp();
		} else if ("api".equalsIgnoreCase(tool)) {
			startAPI();
		}
	}

	@Override
	public Object getNextStepDataFromQueue(String frameworkName) throws InterruptedException {
		return framworkQueueDetail.getPendingDataToFramework(frameworkName);
	}
	
	@Override
	public void formValidationJson(Object nextTestStepJson) {
		JSONObject validations = null;
		JSONObject nextCommandJSON = null;
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> variableList = null;
		try {
			JSONParser parser = new JSONParser();
			if(!nextTestStepJson.toString().contains("MSG:")){
				nextCommandJSON = (JSONObject) parser.parse(mapper.writeValueAsString(nextTestStepJson));
				nextCommandJSON = (JSONObject) nextCommandJSON.get("teststepData");
				System.out.println(nextCommandJSON);
			if (nextCommandJSON.get("attributes") != null) {
				List<Map<String, Object>> attribute = mapper.readValue(nextCommandJSON.get("attributes").toString(),
						new TypeReference<List<Map<String, Object>>>() {
						});
				if (nextCommandJSON.get("variablePair") != null) {
					variableList = mapper.readValue(nextCommandJSON.get("variablePair").toString(),
							new TypeReference<Map<String, String>>() {
							});
				}
				for (int attrSize = 0; attrSize < attribute.size(); attrSize++) {
					
					validations = null;
					HashMap<String, Object> singleTestDataJsonObject = (HashMap<String, Object>) attribute
							.get(attrSize);
					validations =  (JSONObject) parser.parse(mapper.writeValueAsString(singleTestDataJsonObject.get("validations")));
					validationData.put("validations", validations);
					validationData.put("transactionId", nextCommandJSON.get("transactionId").toString());
					validationData.put("testRunDetailID", nextCommandJSON.get("testRunStepId").toString());
					validationData.put("variableList", variableList);
				}
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public String abortConfirmation(int testRunId){
		RestTemplate rest = SSLConnection();
		String serverWebURL = env.getProperty("serverWebURL");
		rest.getForEntity(serverWebURL + "/v1/abortConfirmation/" + testRunId, String.class);
		return null;
		
	}
}
