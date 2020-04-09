package com.kumaran.tac.agent;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.kumaran.tac.agent.controller.AgentController;

@RunWith(SpringRunner.class)
@SpringBootTest
//@ContextConfiguration(classes = {AgentController.class,AgentService.class,AgentServiceImpl.class })
public class AgentApplicationTests {

@Autowired
	AgentController agentController;

	@Test
	@Ignore
	public void testScriptReturnId() throws IOException {
		agentController.addScriptvale();
		agentController.getNextCommand("85", "QTP");
		agentController.updateScriptStatus();
	}
	
	@Test
	@Ignore
	public void testScriptReturnWait() throws IOException {
		agentController.addScriptvale();
		ResponseEntity<String> waitCheck = agentController.getNextCommand("85", "Selinium");
		assertEquals("Wait previous script in progress", waitCheck.getBody());
		agentController.updateScriptStatus();
		agentController.updateScriptStatus();
		agentController.getNextCommand("85", "Selinium");
	}
	
	@Test
	@Ignore
	public void testScriptReturnTerimination() throws IOException {
		agentController.addScriptvale();
		ResponseEntity<String> terminationCheck = agentController.getNextCommand("85", "Coded UI");
		assertEquals("Teriminate no more scripts", terminationCheck.getBody());
		
	}


}
