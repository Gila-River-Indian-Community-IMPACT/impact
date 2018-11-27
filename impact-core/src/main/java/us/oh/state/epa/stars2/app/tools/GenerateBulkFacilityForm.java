package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Calendar;

import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.def.CorrespondenceDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentUtil;

@SuppressWarnings("serial")
public class GenerateBulkFacilityForm extends BulkCorrespondence {

	/**
	 * CERT_NOT_SUBMITTED const must match key id cm_bulk_def.bulk_id for NOV -
	 * Title V Compliance Certification Not Submitted.
	 */
	public static int CERT_NOT_SUBMITTED = 174;
	public static int CERT_NOT_SUBMITTED_NOT_LOGGED = 177;

	/**
	 * CERT_REMINDER const must match key id cm_bulk_def.bulk_id for NOV - Title
	 * V Compliance Certification Reminder.
	 */
	public static int CERT_REMINDER = 175;

	private Integer bulkID;
	private TemplateDocument templateDoc;
	private String fileBaseName;

	private String tmpDirName;
	private String urlDirName;

	private FacilityList[] facilities;
	private FormResult[] results;
	private String currentFile;

	private Integer reportYear;

	public GenerateBulkFacilityForm() {
		super();
		setButtonName("Generate Document(s)");
		setNavigation("dialog:generateBulkForm");
	}

	/**
	 * Initialize the BulkOperation. Typically, the BulkOperation may need to
	 * initialize default values in one or more of the BulkOperationsCatalog
	 * search fields. This method is called by the BulkOperationsCatalog after
	 * the BulkOperation is selected, but before the search screen fields are
	 * displayed.
	 */
	public final void init(BulkOperationsCatalog catalog) {
		return;
	}

	/**
	 * Each bulk operation is responsible for providing a search operation based
	 * on the paramters gathered by the BulkOperationsCatalog bean. The search
	 * is run when the "Select" button on the Bulk Operations screen is clicked.
	 * The BulkOperationsCatalog will then display the results of the search.
	 * Below the results is a "Bulk Operation" button.
	 */
	public final void searchFacilities(BulkOperationsCatalog catalog)
			throws RemoteException {

		this.catalog = catalog;

		setCorrespondenceDate(catalog.getCorrespondenceDate());

		BulkDef bulkOpDef = catalog.getBulkDef();
		bulkID = bulkOpDef.getBulkId();

		setCorrespondenceTypeCode(bulkOpDef.getCorrespondenceTypeCd());

		reportYear = catalog.getYear();
		if (reportYear == null) {
			reportYear = Calendar.getInstance().get(Calendar.YEAR) - 1;
		}
		
		DefSelectItems templateDocTypeCdItems = CorrespondenceDef.getTemplateDocTypeCdData().getItems();
        String templateDocTypeCd = templateDocTypeCdItems.getItemDesc(bulkOpDef.getCorrespondenceTypeCd());

		if (templateDocTypeCd != null) {
			templateDoc = getDocumentService().retrieveTemplateDocument(
					templateDocTypeCd);
		}
		logger.debug("templateDoc: "+templateDoc);
		String fPath = templateDoc.getTemplateDocPath();//.getPath();
		int lastSlash = fPath.lastIndexOf('/');
		if (lastSlash < 0) {
			lastSlash = fPath.lastIndexOf('\\');
		}
		int lastDot = fPath.lastIndexOf('.');
		fileBaseName = fPath.substring(lastSlash + 1, lastDot);

		if (bulkID == CERT_NOT_SUBMITTED
				|| bulkID == CERT_NOT_SUBMITTED_NOT_LOGGED) {
			facilities = getFacilityService().retrieveTvCertLate(reportYear,
					catalog.getFacilityId(), catalog.getFacilityNm(),
					catalog.getOperatingStatus());
		} else if (bulkID == CERT_REMINDER) {
			facilities = getFacilityService().retrieveTvCertReminder(reportYear,
					catalog.getFacilityId(), catalog.getFacilityNm());
		} else {

			facilities = getFacilityService().searchFacilities(catalog.getFacilityNm(),
					catalog.getFacilityId(), catalog.getCmpId(), null, catalog.getCounty(),
					catalog.getOperatingStatus(), catalog.getDoLaa(),
					catalog.getNaicsCd(), catalog.getPermittingClassCd(),
					catalog.getTvPermitStatus(), null, catalog.getCity(),
					catalog.getZipCode(), null, null, true, catalog.getFacilityTypeCd());
		}

		setMaximum(facilities.length);
		setValue(facilities.length);

		setSearchStarted(true);
		catalog.setFacilities(facilities);
		setSearchCompleted(true);

	}

	/**
	 * When the "Bulk Operation" button is clicked, the BulkOperationsCatalog
	 * class will call this method, ensure that the BulkOperation class is
	 * placed into the jsf context, and return the value of the getNavigation()
	 * method to the jsf controller. Any further requests from the page that is
	 * navigated to are handled by this bean in the normal way. The default
	 * version does no preliminary work.
	 */
	public final void performPreliminaryWork(BulkOperationsCatalog catalog) throws IOException {

		facilities = catalog.getSelectedFacilities();
		results = new FormResult[facilities.length + 1];

		try {
			tmpDirName = getDocumentService().createTmpDir(getCurrentUser());
			urlDirName = tmpDirName.replace('\\', '/');
		} catch (Exception e) {
			logger.error("Could not create tmp directory.", e);
		}

		if (facilities.length != 0) {

			for (int i = 0; i < facilities.length; i++) {
				try {

					String facilityId = facilities[i].getFacilityId();
					Facility facility = getFacilityService().retrieveFacility(
							facilityId);

					String errors = generateForm(facility);
					results[i + 1] = new FormResult();
					results[i + 1].setId(facilities[i].getName());
					if (urlDirName == null)
						urlDirName = "";
					results[i + 1].setFormURL(DocumentUtil
							.getFileStoreBaseURL()
							+ urlDirName
							+ "/"
							+ facility.getFacilityId()
							+ "-"
							+ fileBaseName
							+ ".docx");
					results[i + 1].setFileName(facility.getFacilityId() + "-"
							+ fileBaseName + ".docx");
					results[i + 1].setNotes(errors);

					if (getCorrespondenceTypeCode() != null) {
						addCorrespondence(facilityId, null, tmpDirName
								+ File.separator + facility.getFacilityId()
								+ "-" + fileBaseName + ".docx");
					}

				}catch (IOException ioe) {
					String logStr = "Exception type = "
							+ ioe.getClass().getName() + ", Msg = "
							+ ioe.getMessage();
					logger.error(logStr, ioe);
					if (results[i + 1] != null) {
						results[i + 1].setNotes(logStr);
					}
					throw (ioe);
				} catch (Exception e) {
					String logStr = "Exception type = "
							+ e.getClass().getName() + ", Msg = "
							+ e.getMessage();
					logger.error(logStr, e);
					if (results[i + 1] != null) {
						results[i + 1].setNotes(logStr);
					}
				}
			}

			try {

				results[0] = new FormResult();
				results[0].setId("Zip File");
				results[0].setNotes("All Forms");
				Calendar cal = Calendar.getInstance();
				String date = Integer.toString(cal.get(Calendar.YEAR)) + "-"
						+ Integer.toString((cal.get(Calendar.MONTH) + 1)) + "-"
						+ Integer.toString(cal.get(Calendar.DAY_OF_MONTH))
						+ "-" + Integer.toString(cal.get(Calendar.HOUR_OF_DAY))
						+ "-" + Integer.toString(cal.get(Calendar.MINUTE))
						+ "-" + Integer.toString(cal.get(Calendar.SECOND));
				String zipFileName = fileBaseName + "-" + date + ".zip";
				DocumentUtil.createZipFile(tmpDirName, File.separator + "tmp"
						+ File.separator + getCurrentUser() + File.separator
						+ zipFileName, fileBaseName);
				results[0].setFormURL(DocumentUtil.getFileStoreBaseURL()
						+ "/tmp/" + getCurrentUser() + "/" + zipFileName);
				results[0].setFileName(fileBaseName + ".zip");
			} catch (Exception e) {
				String logStr = "Exception " + e.getClass().getName()
						+ ", Msg = " + e.getMessage();
				logger.error(logStr, e);
				results[0].setNotes(logStr);
			}
		}

		return;
	}

	/** Generate a form for a facility and/or permit. */
	protected String generateForm(Facility facility) throws Exception {

		StringBuffer errors = new StringBuffer();

		try {
			DocumentGenerationBean dataBean = new DocumentGenerationBean();
			if (getCorrespondenceDate() != null
					&& getCorrespondenceDate().length() > 0) {
				dataBean.setCorrespondenceDate(getCorrespondenceDate());
			}
			dataBean.setFacility(facility);
			dataBean.getProperties().put("reportYear", reportYear.toString());
			if (tmpDirName == null)
				tmpDirName = "";
			DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), dataBean,
					tmpDirName + File.separator + facility.getFacilityId()
							+ "-" + fileBaseName + ".docx");
		} catch (Exception e) {
			throw e;
		}

		return errors.toString();
	}

	/**
	 * When the "Apply Operation" button is clicked, the BulkOperationsCatalog
	 * class will call this method and return the value of the getNavigation()
	 * method to the jsf controller. Any further requests from the page that is
	 * navigated to are handled by this (the derived) class in the normal way.
	 * The default version does no preliminary work.
	 * 
	 * @param catalog
	 * @return
	 */
	public String performPostWork(BulkOperationsCatalog catalog) {
		try {
			DocumentUtil.rmDir(tmpDirName);
		} catch (Exception e) {
			logger.error("Unable to remove temporary directory " + tmpDirName,
					e);
		}
		return SUCCESS;
	}

	public final FormResult[] getResults() {
		return results;
	}

	public final void setFile(String fileName) {
		currentFile = fileName;
	}

	public final String getFile() {
		return currentFile;
	}

}
