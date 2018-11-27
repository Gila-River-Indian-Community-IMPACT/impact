package com.jw.sharepoint.examples;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.microsoft.schemas.sharepoint.soap.CopySoap;

public class SharePointUploadDocumentExample extends SharePointBaseExample {

	private static Properties properties = new Properties();
	private static final Log logger = LogFactory.getLog(SharePointUploadDocumentExample.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		logger.debug("main...");		
		try {		
			SharePointUploadDocumentExample example = new SharePointUploadDocumentExample();
			example.initialize();
			CopySoap p = example.getCopySoap();
			example.uploadDocument(p, properties.getProperty("copy.sourceFile"));
		} catch (Exception ex) {
			logger.error("Error caught in main: ",ex);
		}
	}

	public Properties getProperties() {
		return properties;
	}

	


	protected void initialize() throws Exception {
		logger.debug("initialize()...");
		properties.load(getClass().getResourceAsStream("/SharePointUploadDocumentExample.properties"));
		super.initialize();		
	}
}
