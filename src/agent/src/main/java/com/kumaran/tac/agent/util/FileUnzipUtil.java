package com.kumaran.tac.agent.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUnzipUtil {

	private static final int BUFFER_SIZE = 4096;

	/**
	 * Extracts a zip file specified by the zipFilePath to a directory specified by
	 * destDirectory (will be created if does not exists)
	 * @param zipFilePath
	 * @param destDirectory
	 * @throws IOException
	 */
	public static void unzip(byte[] zipFile, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		
		ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(zipFile));
		ZipEntry entry = zipIn.getNextEntry();

		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				if(dir.exists()){
					File[] listFiles = dir.listFiles();
					for(File file : listFiles){
						file.delete();
					}
				}
				dir.mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	
	private static void extractFile(ZipInputStream zipIn, String filePath)  {
		BufferedOutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filePath));
			byte[] bytesIn = new byte[BUFFER_SIZE];
			int read = 0;
			while ((read = zipIn.read(bytesIn)) != -1) {
				bos.write(bytesIn, 0, read);
			}
			bos.close();
	}
		catch (IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
}
	
public static void deleteFileWithExtension(String directory, String extension) throws IOException {
		
		File dir = new File(directory);
		
		if(dir.exists()){
		// Listing only files having the extension.
		File[] filesToDelete = dir.listFiles(new ExtensionFilter(extension));
		
		// Using the custom filenameFilter : ExtensionFilter.
		for(File file : filesToDelete) {
			if(!file.delete()) {
				throw new IOException();
			}
		}
		}
	}

}