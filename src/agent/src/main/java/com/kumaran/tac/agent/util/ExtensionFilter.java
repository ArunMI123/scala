package com.kumaran.tac.agent.util;

import java.io.File;
import java.io.FilenameFilter;

public class ExtensionFilter implements FilenameFilter {
	 
	private String extension;
	
	public ExtensionFilter(String extension) {
		this.extension = extension;
	}
	
	/**
	 * Accepting only files ending with the extension.
	 */
	public boolean accept(File dir, String name) {
		return name.endsWith(extension);
	}
 
}