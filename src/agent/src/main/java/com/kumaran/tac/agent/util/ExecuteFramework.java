package com.kumaran.tac.agent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteFramework implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ExecuteFramework.class);
	private static final Map<String, String> frameworkStatus = new HashMap<>();
	private String frameworkName;
	private String executable;
	private List<String> commandlineArgs;
	private File errFile;
	private File outFile;
	
	public ExecuteFramework( String frameworkName, String executable, String ... commandlineArgs) {
		this.frameworkName = frameworkName;
		this.executable = executable;
		this.commandlineArgs = Arrays.asList( commandlineArgs);
		this.outFile = new File( this.frameworkName + ".out.txt");
		this.errFile = new File( this.frameworkName + ".err.txt");
	}
	
	@Override
	public void run() {
		frameworkStatus.put( frameworkName,  "STARTING");
		try {
			String execFile = executable;
			if( "java".equalsIgnoreCase( execFile)) {
				execFile = getJavaExecutable();
			}
			logger.info( "Running the framework " + frameworkName);
			logger.info( "Command line EXE = " + execFile);
			logger.info( "Command line Parameters = " + commandlineArgs);
			logger.info( "Out File = " + outFile.getAbsolutePath());
			logger.info( "Error File = " + errFile.getAbsolutePath());
			List<String> finalCommand = new ArrayList<>();
			finalCommand.add( execFile);
			finalCommand.addAll( commandlineArgs);
			Process p = new ProcessBuilder( finalCommand)
					.redirectError( errFile)
					.redirectOutput( outFile)
					.start();
			p.waitFor();
			frameworkStatus.put( frameworkName,  "COMPLETED");
		} catch( Exception ex) {
			frameworkStatus.put( frameworkName,  "FAILED");
			logger.error( "Error running framework", ex);;
		} finally {
			if( outFile.exists()) {
				try( InputStream fis = new FileInputStream( outFile)) {
					IOUtils.copyLarge(fis, System.out);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if( errFile.exists()) {
				try( InputStream fis = new FileInputStream( errFile)) {
					IOUtils.copyLarge(fis, System.err);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public File getOutFile() {
		return this.outFile;
	}

	public File getErrorFile() {
		return this.errFile;
	}
	
	private String getJavaExecutable() {
		String libPath = System.getProperty("java.library.path");
		String exePath = null;
		if( System.getProperty( "os.name").toLowerCase().indexOf( "win") >= 0) {
			exePath = libPath.substring(0, libPath.indexOf( File.pathSeparatorChar))
					/*+ ((System.console() == null) ? "\\javaw.exe" : "\\java.exe");*/
					+ "\\java.exe";
		} else {
			exePath = libPath.substring(0, libPath.indexOf( File.pathSeparatorChar))
					/*+ ((System.console() == null) ? "/javaw" : "/java");*/
					+ "/java";
		}
		return exePath;
	}
	
	public static void main( String[] args) throws Exception {
		ExecuteFramework ef = new ExecuteFramework( "TEST", "java", "-version");
		Thread t = new Thread( ef);
		t.start();
		t.join();
	}
}
