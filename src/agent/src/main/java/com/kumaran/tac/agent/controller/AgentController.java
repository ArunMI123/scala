package com.kumaran.tac.agent.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumaran.tac.agent.model.Agent;
import com.kumaran.tac.agent.service.AgentService;
import com.kumaran.tac.agent.util.AgentExecutor;
import com.kumaran.tac.agent.util.FrameworkQueueDetails;

@RestController
@CrossOrigin
@RequestMapping("/agent/v1")
public class AgentController {

	@Autowired
	private AgentService agentService;
	
	@Autowired                
	private Environment env;  
	
	@Autowired
	ApplicationContext applicationContext; 
	
	public static final String home = System.getProperty("user.home");
	
	private static final Logger logger = LogManager.getLogger(AgentController.class);
	
	@RequestMapping(value = "/getScreenShot/{imgName}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
	public void getScreenshot(HttpServletResponse response, @PathVariable String imgName) {
		FileOutputStream outputStream = null;
		try {
			byte[] stream = agentService.getScreenshot();
			response.addHeader("Content-disposition", "attachment;filename=" + imgName + ".jpg");
			response.setContentType("application/octet-stream");
			
			String filePath = "C:/Users/sesh2158/file.jpg";

			File targetFile = new File(filePath);
			targetFile.createNewFile();
			outputStream = new FileOutputStream(targetFile);
			outputStream.write(stream);
			outputStream.flush();
			outputStream.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}
	

	@RequestMapping(value = "/addscriptvalue", method = RequestMethod.GET)
	public ResponseEntity<String> addScriptvale() throws IOException {

		String val = agentService.addScriptvale();
		return new ResponseEntity<String>(val, HttpStatus.OK);

	}

	@RequestMapping(value = "/updatescriptstatus", method = RequestMethod.GET)
	public ResponseEntity<String> updateScriptStatus() throws IOException {

		String val = agentService.updateScriptStatus();
		return new ResponseEntity<String>(val, HttpStatus.OK);
	}

	@RequestMapping(value = "/getNextCommand/{testRunId}/{machinestype}", method = RequestMethod.GET)
	public ResponseEntity<String> getNextCommand(@PathVariable("testRunId") String testRunId, @PathVariable("machinestype") String machinestype) throws IOException {

		try {
			return agentService.getTestStepData(testRunId);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/initiateQtp/{uniqueProjectId}",  method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
	public void startQtp(@RequestBody HashMap<String,byte[]> customFiles,@PathVariable("uniqueProjectId") String uniqueProjectId) {
		agentService.startQtp(customFiles);
	}

	@RequestMapping(value = "/uploadFramework", method = RequestMethod.POST)
	public int frameworkUpload(@RequestParam("file") MultipartFile file) {
		int rtnVal = agentService.frameworkUpload(file);
		return rtnVal;
	}
	@RequestMapping(value = "/abortConfirmation/{testRunId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> abortConfirmation(@PathVariable("testRunId") String testRunId) throws Exception{
			ObjectMapper objMap = new ObjectMapper();
			String result = agentService.abortConfirmation(Integer.valueOf(testRunId));
			return new ResponseEntity<String>(objMap.writeValueAsString(result), HttpStatus.OK);
	}

	@RequestMapping(value = "/initiateCodedUi", method = RequestMethod.GET)
	public void startCodedUi() {
		agentService.startCodedUi();
	}

	@RequestMapping(value = "/getTestRunDetails/{testRunId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTestRunDetails(@PathVariable("testRunId") String testRunId) {
		try {
			return agentService.getTestRunOR(testRunId);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/getTestData/{stepId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTestData(@PathVariable String stepId) {
		try {
			String testdatafromJsonFile = getJsonFromFile("D:/Sivaprasad/BackupofD/AutomationSVN/chk/agent/src/main/resources/TestData_OR_Json/Testdata"+stepId+".json");
			return new ResponseEntity<String>(testdatafromJsonFile, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public String getJsonFromFile(String fileName) throws Exception {
		System.out.println("...Reading file....");
		StringBuilder result = new StringBuilder("");
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(fileName);
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("...content on file...." + result.toString());	
		return result.toString();
	}
	
	@RequestMapping(value = "/executeTestRun/{testRunId}/{uniqueProjectId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<String> executeTestRun(@PathVariable("testRunId") String testRunId,@RequestBody HashMap<String,byte[]> customFiles,
			@PathVariable("uniqueProjectId") String uniqueProjectId) {
		try {
			ObjectMapper objMap = new ObjectMapper();
			agentService.invokeSelenium(testRunId,customFiles,uniqueProjectId);			
			return new ResponseEntity<String>(objMap.writeValueAsString("success"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/saveTestStepResult", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveTransactionStep(@RequestBody HashMap<String,String> testStepResult) {
		try {
			ObjectMapper objMap = new ObjectMapper();
			String screenshotPath = ""; 
			if(testStepResult.get("status").equals("FAIL")) {
				screenshotPath = (String) agentService.saveFailScreenshot(testStepResult.get("testRunStepId"), agentService.getScreenshot());
			}
			testStepResult.put("screenshotPath", screenshotPath);
			agentService.saveTestStepResult(testStepResult);
			return new ResponseEntity<String>(objMap.writeValueAsString("success"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/getTestRunId", method = RequestMethod.GET)
	public ResponseEntity<String> getTestRunId() throws IOException {

		try {
			return agentService.getTestRunId();
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/checkHealth", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkHealth() {
		try {
			ObjectMapper objMap = new ObjectMapper();
			String agentstatus = "success";
			return new ResponseEntity<String>(objMap.writeValueAsString(agentstatus), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/executeAPITestRun/{testRunId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
	public ResponseEntity<String> executeAPITestRun(@PathVariable("testRunId") String testRunId) {
		try {
			ObjectMapper objMap = new ObjectMapper();
			agentService.startAPI(testRunId);			
			return new ResponseEntity<String>(objMap.writeValueAsString("success"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/agentValidation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> agentValidation(@RequestBody HashMap<String,String> validationDetials) {
		try {
			ObjectMapper objMap = new ObjectMapper();
			HashMap<String, String> result = agentService.getValidationStatus(validationDetials);
			return new ResponseEntity<String>(objMap.writeValueAsString(result), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/saveResponseData", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> saveResponseData(@RequestBody HashMap<String,Object> ResponseDataJson) {
		try {
			ObjectMapper objMap = new ObjectMapper();
			agentService.saveResponseData(ResponseDataJson);
			return new ResponseEntity<String>(objMap.writeValueAsString("success"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// Newly Added
	
	// No Need
	/*@PostMapping(value = "/startFramework/{testRunExecutionId}/{uniqueProjectId}/{tool}", consumes = "application/json" )
	public ResponseEntity<String> startFramework(@PathVariable("testRunExecutionId") String testRunExecutionId,@RequestBody HashMap<String,byte[]> customFiles,
			@PathVariable("uniqueProjectId") String uniqueProjectId,
			@PathVariable("tool") String tool) {
		try {
			ObjectMapper objMap = new ObjectMapper();
			agentService.invokeFramework(testRunExecutionId,customFiles,uniqueProjectId,tool);
			return new ResponseEntity<String>(objMap.writeValueAsString("success"), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/

	// Return Agent Registration Page
	@GetMapping(value = "/agentRegistration")
	public ModelAndView  registration(ModelAndView modelAndView, Agent agent)  {
		modelAndView.addObject("agent", agent);
	    modelAndView.setViewName("registration");
        return modelAndView;
	}
	
	// Save Agent Information
	@PostMapping(value = "/saveAgent")
	public ModelAndView saveAgentDetail(ModelAndView modelAndView, Agent agentInfo) {
		try {
			HashMap<String, String> agent = agentService.saveAgentDetail(agentInfo);
			if (agent == null) {
				modelAndView.setViewName("error");
			} else {
				modelAndView.setViewName("success");
			}
			return modelAndView;
		} catch (Exception exception) {
			return null;
		}
	}

	/*@GetMapping(value = "/getTeststepData", produces = "application/json")
	public ResponseEntity<String> getTeststepData() {
		try {
			ObjectMapper objMap = new ObjectMapper();
			int agentId = LoadConfigFile.getInstance().getPropertyAsIntger("agentId");
			Object teststepData = agentService.getTeststepData(agentId);
			return new ResponseEntity<String>(objMap.writeValueAsString(teststepData), HttpStatus.OK);
		} catch (Exception exception) {
			return new ResponseEntity<String>(exception.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}*/
	
	
	@GetMapping(value = "/getNextStep/{frameworkName}", produces = "applicaton/json")
	public ResponseEntity<String> getNextStep(@PathVariable("frameworkName") String frameworkName) throws Exception {
		String dataFromAgentTriggerQueue = AgentExecutor.agentTriggerQueue.poll(2, TimeUnit.SECONDS);
		ObjectMapper objMap = new ObjectMapper();
		HashMap<String, Object> data = new HashMap<>();
		if(dataFromAgentTriggerQueue !=null && dataFromAgentTriggerQueue.contains("testRunAborted")){					
			if (dataFromAgentTriggerQueue.contains("testRunAborted")) {
				FrameworkQueueDetails frameworkQueue = applicationContext.getBean(FrameworkQueueDetails.class);
				frameworkQueue.getFrameworkQueueMap().clear();
				dataFromAgentTriggerQueue=null;
				data.clear();
				data.put("abort", "abort");
			} 
		}else{
			
			try {
				
			AgentExecutor.agentTriggerQueue.put("nextStepData");
			Object nextStepdata =  null;
			nextStepdata =  agentService.getNextStepDataFromQueue(frameworkName);
			System.out.println("NextStep From Queue:"+nextStepdata);
			if(frameworkName.contains("testRunComplete")) {
				FrameworkQueueDetails frameworkQueue = applicationContext.getBean(FrameworkQueueDetails.class);
				frameworkQueue.getFrameworkQueueMap().clear();
				return null;
			}
			if(nextStepdata==null) {
					data.clear();
					data.put("wait", "wait");
					return new ResponseEntity<String>(objMap.writeValueAsString(data), HttpStatus.OK);
				}else if(nextStepdata.equals("testRunAborted")){
					data.clear();
					data.put("abort", "abort");
					return new ResponseEntity<String>(objMap.writeValueAsString(data), HttpStatus.OK);
				}
				agentService.formValidationJson(nextStepdata);
				return new ResponseEntity<String>(objMap.writeValueAsString(nextStepdata), HttpStatus.OK);
			} catch (Exception e) {
				return new ResponseEntity<String>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<String>(objMap.writeValueAsString(data), HttpStatus.OK);
	}
	
}