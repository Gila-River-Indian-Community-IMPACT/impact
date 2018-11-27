package us.wy.state.deq.impact.webcommon.tools;

import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import oracle.adf.view.faces.model.UploadedFile;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TreeBase;

public class MigrateWiseView extends TreeBase {

	// *********** Variables Region ***********
	private static final long serialVersionUID = -2471578930962021713L;
	private transient Logger logger = Logger.getLogger(this.getClass());

	private static final String PAGE_URL = "tools.wiseViewMigration";

	private UploadedFile fileToUpload;
	private UploadedFile fileToDeleteFacilityInventory;

	private boolean deleteExistingData;

	private static String migrationTempFolder = DocumentUtil.getFileStoreRootPath() + "\\tmp\\WiseviewImport";
	private String logFileName = "wiseview-migration-log.txt";
	private String logFilePath = migrationTempFolder + "\\" + logFileName;

	// 0=in progress, 1=previous results, 2=new results.
	private Integer migrateStatus = new Integer(1);

	private String refreshStr = " ";
	private String refreshStrOn = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
			+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />" + "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
			+ "<META HTTP-EQUIV=\"Cache-Control\" "
			+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";

	private String displayResults = "";
	private MigrateWiseView migrateWiseView = null;

	private FacilityService facilityService;

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	// *********** Properties ***********
	public synchronized UploadedFile getFileToUpload() {
		return fileToUpload;
	}

	public synchronized void setFileToUpload(UploadedFile fileToUpload) {
		this.fileToUpload = fileToUpload;
	}

	public synchronized UploadedFile getFileToDeleteFacilityInventory() {
		return fileToDeleteFacilityInventory;
	}

	public synchronized void setFileToDeleteFacilityInventory(UploadedFile fileToDeleteFacilityInventory) {
		this.fileToDeleteFacilityInventory = fileToDeleteFacilityInventory;
	}

	public synchronized boolean isDeleteExistingData() {
		return deleteExistingData;
	}

	public synchronized void setDeleteExistingData(boolean deleteExistingData) {
		this.deleteExistingData = deleteExistingData;
	}

	public synchronized String getLogFilePath() {
		return logFilePath;
	}

	public synchronized Integer getMigrateStatus() {
		return migrateStatus;
	}

	public synchronized void setMigrateStatus(Integer migrateStatus) {
		this.migrateStatus = migrateStatus;
	}

	public synchronized String getRefreshStr() {
		return refreshStr;
	}

	public synchronized void setRefreshStr(String refreshStr) {
		this.refreshStr = refreshStr;
	}

	public synchronized String getDisplayResults() {
		return displayResults;
	}

	public synchronized void setDisplayResults(String displayResults) {
		this.displayResults = displayResults;
	}

	public synchronized MigrateWiseView getMigrateWiseView() {
		return migrateWiseView;
	}

	public synchronized void setMigrateWiseView(MigrateWiseView migrateWiseView) {
		this.migrateWiseView = migrateWiseView;
	}

	// *********** Public Method Region ***********

	public String initMigrateWiseView() {
		setRefreshStr(" ");
		setDeleteExistingData(true);

		return PAGE_URL;
	}

	public String startMigrateOperation() {
		logger.info("startMigrateOperation started");
		setMigrateWiseView(this);
		setDisplayResults("");
		boolean noError = true;

		if (fileToUpload == null) {
			DisplayUtil.displayError("Please add a migartion zip file.");
			noError = false;
		}

		if (noError && !DocumentUtil.getFileExtension(fileToUpload.getFilename()).equalsIgnoreCase("zip")) {
			DisplayUtil.displayError("The migration file type is incorrect. It must be a zip file.");
			noError = false;
		}

		if (noError) {
			try {
				Utility.unZipIt(fileToUpload.getInputStream(), migrationTempFolder);

				// resetting variables
				setMigrateStatus(new Integer(0)); // in progress
				setRefreshStr(refreshStrOn);

				RunImport importThread = new RunImport();
				importThread.start();
			} catch (IOException exio) {
				DisplayUtil.displayError(exio.getMessage());
			}
		}

		return PAGE_URL;
	}

	class RunImport extends Thread {
		public void run() {
			getMigrateWiseView().migrateWiseViewData();
			setMigrateStatus(new Integer(2)); // have results
			setRefreshStr(" ");
			setDisplayResults("<b>Import process completed.</b>");
		}
	}

	public void migrateWiseViewData() {
		try {
			getFacilityService().migrateWiseViewData(deleteExistingData, migrationTempFolder, logFilePath);
		} catch (RemoteException ex) {
			DisplayUtil.displayError(ex.getMessage());
			return;
		} catch (Exception e) {
			logger.error("Error while importing facility", e);
			return;
		}
	}

	public String startDeleteOperation() {
		logger.info("startDeleteOperation started");
		setMigrateWiseView(this);
		setDisplayResults("");

		if (fileToDeleteFacilityInventory == null) {
			DisplayUtil.displayError("Please choose a facility-id file.");
		} else {
			try {
				Utility.copyIt(fileToDeleteFacilityInventory.getInputStream(), migrationTempFolder);

				// resetting variables
				setMigrateStatus(new Integer(0)); // in progress
				setRefreshStr(refreshStrOn);

				RunDelete deleteThread = new RunDelete();
				deleteThread.start();
			} catch (IOException exio) {
				DisplayUtil.displayError(exio.getMessage());
			}
		}

		return PAGE_URL;
	}

	class RunDelete extends Thread {
		public void run() {
			getMigrateWiseView().deleteWiseViewData();
			setMigrateStatus(new Integer(2)); // have results
			setRefreshStr(" ");
			setDisplayResults("<b>Delete process completed.</b>");
		}
	}

	public void deleteWiseViewData() {
		try {
			getFacilityService().deleteFacilityInventory(migrationTempFolder, logFilePath);
		} catch (RemoteException ex) {
			DisplayUtil.displayError(ex.getMessage());
			return;
		} catch (Exception e) {
			logger.error("Error while deleting facility", e);
			return;
		}
	}

	public String getLogFileURI() {
		String uri = DocumentUtil.getFileStoreBaseURL() + "\\tmp\\WiseviewImport\\" + logFileName;
		return uri.replace('\\', '/');
	}
	
	/*
	 * public final void startMigrateWiseViewData() {
	 * 
	 * if (fileToUpload == null) {
	 * DisplayUtil.displayError("Please add a migartion zip file."); return; }
	 * 
	 * if (!DocumentUtil.getFileExtension(fileToUpload.getFilename()).
	 * equalsIgnoreCase("zip")) { DisplayUtil.
	 * displayError("The migration file type is incorrect. It must be a zip file."
	 * ); return; }
	 * 
	 * try { migrateStatus = 0; Utility.unZipIt(fileToUpload.getInputStream(),
	 * migrationTempFolder); } catch (IOException exio) {
	 * DisplayUtil.displayError(exio.getMessage()); migrateStatus = 2; return; }
	 * 
	 * try { getFacilityService().migrateWiseViewData(deleteExistingData,
	 * migrationTempFolder, logFilePath); } catch (RemoteException ex) {
	 * DisplayUtil.displayError(ex.getMessage()); return; } finally {
	 * migrateStatus = 2; } }
	 * 
	 * 
	 * public final void startDeleteFacilityInventory() {
	 * 
	 * inProcessing = true; if (fileToDeleteFacilityInventory == null) {
	 * DisplayUtil.displayError("Please chose a facility-id file."); return; }
	 * 
	 * try { Utility.copyIt(fileToDeleteFacilityInventory.getInputStream(),
	 * migrationTempFolder); } catch (IOException exio) {
	 * DisplayUtil.displayError(exio.getMessage()); return; }
	 * 
	 * try { getFacilityService().deleteFacilityInventory(migrationTempFolder,
	 * logFilePath);
	 * 
	 * } catch (RemoteException ex) { DisplayUtil.displayError(ex.getMessage());
	 * 
	 * return; } finally { inProcessing = false; }
	 * 
	 * }
	 */

	/*
	 * public final void preserveFacilityInventory(){
	 * 
	 * inProcessing = true; if (fileToPreserveFacilityInventory == null) {
	 * DisplayUtil.displayError("Please chose a facility-id file."); return; }
	 * 
	 * try { Utility.copyIt(fileToPreserveFacilityInventory.getInputStream(),
	 * migrationTempFolder); } catch (IOException exio) {
	 * DisplayUtil.displayError(exio.getMessage()); return; }
	 * 
	 * try { Integer oldFpId =
	 * getFacilityService().preserveFacilityInventory(migrationTempFolder,
	 * logFilePath);
	 * getFacilityService().startMigrateNewFacilityData(deleteExistingData,
	 * oldFpId, migrationTempFolder, logFilePath); } catch (RemoteException ex)
	 * { DisplayUtil.displayError(ex.getMessage());
	 * 
	 * return; } finally { inProcessing = false; }
	 * 
	 * }
	 */

}
