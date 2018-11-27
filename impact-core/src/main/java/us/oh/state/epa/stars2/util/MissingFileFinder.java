package us.oh.state.epa.stars2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;

@Component
public class MissingFileFinder {
	private File searchDir;
	private File outputFile;
	private File notMissingFile;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	@Resource private ApplicationDAO readOnlyApplicationDAO;
	
	public boolean init() {
		boolean ok = false;
		searchDir = new File("C:\\work\\submissions\\dapc-submissions-031610");
		if (!searchDir.exists() || !searchDir.isDirectory()) {
			System.err.println("Unable to read directory " + searchDir.getAbsolutePath());
		} else {
			outputFile = new File("C:\\work\\submissions\\dapc-submissions-031610\\MissingAttachmentInfo3.txt");
			notMissingFile = new File("C:\\work\\submissions\\dapc-submissions-031610\\NotMissing3.txt");
			ok = true;
		}
		return ok;
	}
	
	public void findFilesMissingFiles() throws IOException {
		BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));
		BufferedWriter notMissingWriter = new BufferedWriter(new FileWriter(notMissingFile));
		for (File file : searchDir.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".xml")) {
//				System.out.println("Scanning file " + file.getAbsolutePath());
				findMissingFiles(file, outputWriter, notMissingWriter);
			} else if (file.isDirectory()) {
//				System.out.println("Searching directory " + file.getAbsolutePath());
				findMissingFilesInDir(file, outputWriter, notMissingWriter);
			}
		}
		outputWriter.close();
		notMissingWriter.close();
	}
	
	private void findMissingFilesInDir(File dir, BufferedWriter outputWriter, BufferedWriter notMissingWriter) throws IOException {
		for (File file : dir.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".xml")) {
//				System.out.println("Scanning file " + file.getAbsolutePath());
				findMissingFiles(file, outputWriter, notMissingWriter);
			}
		}
	}
	
	private void findMissingFiles(File file, BufferedWriter outputWriter, BufferedWriter notMissingWriter) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		String basePath = null;
		String applicationId = null;
		String documentId = null;
		String tradeSecretDocId = null;
		String applicationDocId = null;
		String extension = null;
		String tsExtension = null;
		String facilityId = null;
		String lastModifiedTSLong = null;
		String uploadDateLong = null;
		String applicationEUId = null;
		String tradeSecretReason = null;
		String description = null;
		String applicationDocumentTypeCD = null;
		String eacFormTypeCD = null;
		HashSet<String> docIdSet = new HashSet<String>();
		boolean record = false;
		boolean discard = true;
		boolean tradeSecret = false;
		int level = 0;
//		ApplicationDAO appDAO = (ApplicationDAO)DAOFactory.getDAO("ApplicationDAO");
		while ((line = br.readLine()) != null) {
			if (line.contains("ApplicationDocumentRef")) {
				record = true;
				++level;
				continue;
			}
			if (line.contains("\"tradeSecretDoc\"")) {
				tradeSecret = true;
				continue;
			}
			if (line.contains("ApplicationDocument")) {
				++level;
				continue;
			}
			if (record) {
				if (line.contains("facilityID")) {
					// value is on the next line
					line = br.readLine();
					facilityId = line.replace("<string>", "").replace("</string>", "").trim();
				} else if (line.contains("applicationId")) {
					// value is on the next line
					line = br.readLine();
					applicationId = line.replace("<int>", "").replace("</int>", "").trim();
				} else if (line.contains("applicationEUId")) {
					// value is on the next line
					line = br.readLine();
					applicationEUId = line.replace("<int>", "").replace("</int>", "").trim();
				} else if (line.contains("applicationDocId")) {
					// value is on the next line
					line = br.readLine();
					applicationDocId = line.replace("<int>", "").replace("</int>", "").trim();
				}  else if (line.contains("documentId")) {
					// value is on the next line
					line = br.readLine();
					documentId = line.replace("<int>", "").replace("</int>", "").trim();
				} else if (line.contains("tradeSecretDocId")) {
					// value is on the next line
					line = br.readLine();
					tradeSecretDocId = line.replace("<int>", "").replace("</int>", "").trim();
				} else if (line.contains("basePath")) {
					// value is on the next line
					line = br.readLine();
					basePath = line.replace("<string>", "").replace("</string>", "").trim();
					// use base path to find extension if not explicitly provided
					if (basePath != null) {
						int dotIdx = basePath.lastIndexOf(".");
						if (dotIdx > 0) {
							extension = basePath.substring(dotIdx + 1);
						}
					}
				} else if (line.contains("extension")) {
					// value is on the next line
					line = br.readLine();
					if (!tradeSecret) {
						extension = line.replace("<string>", "").replace("</string>", "").trim();
					} else {
						tsExtension = line.replace("<string>", "").replace("</string>", "").trim();
					}
				} else if (line.contains("applicationDocumentTypeCD")) {
					// value is on the next line
					line = br.readLine();
					applicationDocumentTypeCD = line.replace("<string>", "").replace("</string>", "").trim();
				} else if (line.contains("eacFormTypeCD")) {
					// value is on the next line
					line = br.readLine();
					eacFormTypeCD = line.replace("<string>", "").replace("</string>", "").trim();
				} else if (line.contains("description")) {
					// value is on the next line
					line = br.readLine();
					description = line.replace("<string>", "").replace("</string>", "").trim();
				} else if (line.contains("tradeSecretReason")) {
					// value is on the next line
					line = br.readLine();
					tradeSecretReason = line.replace("<string>", "").replace("</string>", "").trim();
				} else if (line.contains("lastModifiedTSLong")) {
					// value is on the next line
					line = br.readLine();
					lastModifiedTSLong = line.replace("<long>", "").replace("</long>", "").trim();
				} else if (line.contains("uploadDateLong")) {
					// value is on the next line
					line = br.readLine();
					uploadDateLong = line.replace("<long>", "").replace("</long>", "").trim();
				} else if (line.contains("temporary")) {
					discard = false;
				} else if (line.contains("</object>")) {
					if (--level == 0) {
						record = false;
						if (!discard) {
							if (docIdSet.contains(documentId)) {
//								System.err.println("repeat documentId: " + documentId);
								continue;
							}
							docIdSet.add(documentId);
							if (tradeSecret) {
								writeTSFileInfo(outputWriter, notMissingWriter, facilityId, applicationId, tradeSecretDocId, tsExtension, uploadDateLong, 
										lastModifiedTSLong, description);
							}
							writeFileInfo(outputWriter, notMissingWriter, facilityId, applicationId, documentId, extension, uploadDateLong, 
									lastModifiedTSLong, description, applicationDocId, applicationEUId, applicationDocumentTypeCD, 
									eacFormTypeCD, tradeSecretDocId, tradeSecretReason);
							// check to see if file may have been replaced
							for (ApplicationDocumentRef appDoc : readOnlyApplicationDAO.retrieveApplicationDocuments(Integer.decode(applicationId))) {
								if (appDoc.getDescription() != null && appDoc.getDescription().equals(description)) {
									System.out.println("!!!! " + "For facility " + facilityId + ", application id " + applicationId +
											": Document " + applicationDocId + ", " + documentId + " may be replaced by document " +
											appDoc.getApplicationId() + ", " + appDoc.getDocumentId());
								}
							}
						}
						applicationId = null;
						documentId = null;
						tradeSecretDocId = null;
						applicationDocId = null;
						extension = null;
						facilityId = null;
						lastModifiedTSLong = null;
						uploadDateLong = null;
						applicationEUId = null;
						tradeSecretReason = null;
						description = null;
						applicationDocumentTypeCD = null;
						eacFormTypeCD = null;
						discard = true;
						tradeSecret = false;
					}
				}
			}
		}
		br.close();
	}
	
	private void writeFileInfo(BufferedWriter bw, BufferedWriter notMissingWriter, String facilityId, String applicationId, String documentId, String extension,
			String uploadDateLong, String lastModifiedTSLong, String description, String applicationDocId, String applicationEUId, 
			String applicationDocumentTypeCD, String eacFormTypeCD, String tradeSecretDocId, String tradeSecretReason) throws IOException {
		String basePath = "/Applications/" + applicationId + "/" + documentId + (extension == null ? "" : "." + extension);
		String relativePath = "attachments/Facilities/" + facilityId + basePath;
		boolean fileExists = fileExists(notMissingWriter, relativePath, dateFormat.format(new Timestamp(Long.parseLong(lastModifiedTSLong))));
		if (!fileExists) {
			bw.write(relativePath);
			if (uploadDateLong != null) {
				Timestamp uploadDate = new Timestamp(Long.parseLong(uploadDateLong));
				bw.write("\t" + dateFormat.format(uploadDate));
			}
			String docSQL = "INSERT INTO stars2.dc_document (document_id, facility_id, last_modified_by, " +
				"last_modified_ts, path, upload_dt, description, temp_flag) VALUES (" +
				documentId + ", '" + facilityId + "', " + CommonConst.GATEWAY_USER_ID + ", " +
				"TO_DATE('" + dateFormat.format(new Timestamp(Long.parseLong(lastModifiedTSLong))) + "', 'MM/DD/YYYY'), '" +
				basePath + "', " +
				"TO_DATE('" + dateFormat.format(new Timestamp(Long.parseLong(uploadDateLong))) + "', 'MM/DD/YYYY'), '" +
				description + "', 'N');";
			
			bw.write("\t" + docSQL.toString());
			String appDocSQL = "INSERT INTO stars2.pa_application_document (application_doc_id, document_id, " +
				"application_doc_type_cd, application_id, application_eu_id, eac_form_type_cd, trade_secret_doc_id, " +
				" trade_secret_reason, description) VALUES (" +
				applicationDocId + ", " + documentId + ", '" + applicationDocumentTypeCD + "', " + applicationId + ", " +
				(applicationEUId == null ? "NULL" : applicationEUId)  + ", '" + (eacFormTypeCD == null ? "" : eacFormTypeCD) + 
				"', " + (tradeSecretDocId == null ? "NULL" : tradeSecretDocId) + 
				", '" + (tradeSecretReason == null ? "" : tradeSecretReason) + "', '" + 
				description + "');";
			bw.write(" " + appDocSQL);
			bw.newLine();
			bw.flush();
		}
	}
	
	private boolean fileExists(BufferedWriter notMissingWriter, String relativePath, String date) throws IOException {
		boolean fileExists = false;
		String checkPath = relativePath;
		// TODO change this back to include the extension
		int dotIdx = relativePath.lastIndexOf('.');
		if (dotIdx > 0) {
			checkPath = relativePath.substring(0, dotIdx);
		}
		File checkFile = new File("S:/prod/" + checkPath);
		if (checkFile.exists()) {
			notMissingWriter.write("File not Missing: " + checkFile.getAbsolutePath() + " " + date);
			notMissingWriter.newLine();
			notMissingWriter.flush();
			fileExists = true;
		} else if (!checkFile.getName().contains(".")) {
			// scan directory if file has no extension to see if there is a match
			File parentDir = new File(checkFile.getParent());
			if (parentDir.isDirectory()) {
				for (File tmpFile : parentDir.listFiles()) {
					if (tmpFile.getName().startsWith(checkFile.getName())) {
						notMissingWriter.write("File not Missing: " + tmpFile.getAbsolutePath() + " (" + checkFile.getName() + ") " + date);
						notMissingWriter.newLine();
						fileExists = true;
						break;
					}
				}
			}
			notMissingWriter.flush();
		}
		return fileExists;
	}
	
	private void writeTSFileInfo(BufferedWriter bw, BufferedWriter notMissingWriter, String facilityId, String applicationId, String documentId, String extension,
			String uploadDateLong, String lastModifiedTSLong, String description) throws IOException {
		String basePath = "/Applications/" + applicationId + "/" + documentId + (extension == null ? "" : "." + extension);
		String relativePath = "attachments/Facilities/" + facilityId + basePath;
		boolean fileExists = fileExists(notMissingWriter, relativePath, dateFormat.format(new Timestamp(Long.parseLong(lastModifiedTSLong))));
		if (!fileExists) {
			bw.write(relativePath);
			if (uploadDateLong != null) {
				Timestamp uploadDate = new Timestamp(Long.parseLong(uploadDateLong));
				bw.write("\t" + dateFormat.format(uploadDate));
			}
			String docSQL = "INSERT INTO stars2.dc_document (document_id, facility_id, last_modified_by, " +
				"last_modified_ts, path, upload_dt, description, temp_flag) VALUES (" +
				documentId + ", '" + facilityId + "', " + CommonConst.GATEWAY_USER_ID + ", " +
				"TO_DATE('" + dateFormat.format(new Timestamp(Long.parseLong(lastModifiedTSLong))) + "', 'MM/DD/YYYY'), '" +
				basePath + "', " +
				"TO_DATE('" + dateFormat.format(new Timestamp(Long.parseLong(uploadDateLong))) + "', 'MM/DD/YYYY'), '" +
				description + "', 'N');";
			
			bw.write("\t" + docSQL.toString());
			bw.newLine();
			bw.flush();
		}
	}

	public final static void main(String[] args) {
		MissingFileFinder finder = new MissingFileFinder();
		if (finder.init()) {
			try {
				finder.findFilesMissingFiles();
			} catch (IOException e) {
				System.err.println("Exception while searching for missing files");
				e.printStackTrace();
			}
		}
	}

}
