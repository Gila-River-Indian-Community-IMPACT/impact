package us.wy.state.deq.impact.app.emissionsReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.Ostermiller.util.ExcelCSVParser;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCDataImportPollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.def.ContentTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionsAttachmentTypeDef;
import us.oh.state.epa.stars2.def.RegulatoryRequirementTypeDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.ValidationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;
import us.oh.state.epa.stars2.webcommon.reports.ReportTemplates;
import us.wy.state.deq.impact.database.dbObjects.report.EiDataImportFacilityInformation;

@SuppressWarnings("serial")
public class EiDataImport extends AppBase implements IAttachmentListener {

	static String EI_DATA_IMPORT_PATH = File.separator + "tmp" + File.separator + "EiDataImport";
	private static Integer MINIMUN_REQUIRED_COLUMNS = 24;
	private static String logFileString = "eiDataImportLog";
	private static String errFileString = "eiDataImportErr";
	private transient Logger logger = Logger.getLogger(this.getClass());

	private FacilityService facilityService;
	private EmissionsReportService emissionsReportService;
	private InfrastructureService infrastructureService;

	private EiDataImportCriteria importCriteria;
	private Integer fileStatus = new Integer(1); // 0=in progress, 1=previous
													// results, 2=new results.

	private String refreshStr = " ";
	private String refreshStrOn = "<META HTTP-EQUIV=\"refresh\" CONTENT=\"5\" />"
			+ "<META HTTP-EQUIV=\"Pragma\" CONTENT=\"no-cache\" />" + "<META HTTP-EQUIV=\"Expires\" CONTENT=\"-1\" />"
			+ "<META HTTP-EQUIV=\"Cache-Control\" "
			+ "CONTENT=\"no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\" />";

	private EiDataImport eiDataImport = null;
	private String displayResults = "";
	private String displayLogResults = "";
	private String displayErr = "";

	private FileWriter writer;
	private FileWriter errWriter;
	private boolean noErrors = false;
	private boolean noWarnings = false;

	private long percentProg = 0;
	private long csvParseProgress = 0;
	private long fetchFacilityProfilesProgress = 0;
	private long fileLength = 1;
	private long fileLengthRead = 0;
	private final int csvParseProgressWeight = 20;
	private final int fetchFacilityProfilesProgressWeight = 30;
	private final int validateAndImportEIProgressWeight = 50;

	private Map<Integer, ArrayList<EiDataImportPeriod>> dataImportRowsByFacility;
	private Map<String, EiDataImportFacilityInformation> processInformationForAllFacilities;
	private Map<String, Integer> enabledEmissionReportsForAllFacilities;

	private SCEmissionsReport report;
	private List<SCDataImportPollutant> dataImportPollutants;
	private List<SCPollutant> capPollutants;
	private Integer totalRequiredColumns = MINIMUN_REQUIRED_COLUMNS;
	private double maxOperatingHoursInReportingPeriod;
	private Set<String> processesAdded;
	private Map<Integer, Facility> facilityInfo;
	private Integer currentUserId;
	private List<ReportTemplates> scReportsList;

	public String initReportsEiDataImport() {
		if (getImportCriteria() == null) {
			setImportCriteria(new EiDataImportCriteria());
		}
		initializeAttachmentBean();
		setRefreshStr(" ");
		setCurrentUserId(InfrastructureDefs.getCurrentUserId());

		return "reportsImportEiData";
	}

	public String resetReportsEiDataImport() {
		setImportCriteria(new EiDataImportCriteria());
		initializeAttachmentBean();
		setFileStatus(new Integer(1));
		
		return "reportsImportEiData";
	}
	
	public void initializeAttachmentBean() {
		Attachments attachments = (Attachments) FacesUtil.getManagedBean("attachments");
		attachments.addAttachmentListener(this);
		attachments.setSubPath("tmp" + File.separator + "EiDataImport"); // file path:
																			// \tmp\EiDataImport
		attachments.setObjectId("");
		attachments.setAttachmentTypesDef(EmissionsAttachmentTypeDef.getData().getItems());
		attachments.setNewPermitted(true);
		attachments.setUpdatePermitted(true);
		attachments.setDeletePermitted(true);
		attachments.setTradeSecretSupported(true);
		attachments.setHasDocType(true);
		// use the List<Attachment> data in eiDataImportCriteria DTO for seting up Attachments bean
		attachments.setAttachmentList(getImportCriteria().getAttachmentDocs().toArray(new Attachment[0]));
		attachments.cleanup();
		attachments.setFileToUpload(null);
		attachments.setTsFileToUpload(null);
		attachments.setDocument(null);
	}

	public final LinkedHashMap<String, Integer> getEiImportYears() {
		Integer year = Calendar.getInstance().get(Calendar.YEAR); // current year
		String key = year.toString();

		LinkedHashMap<String, Integer> eiImportYears = new LinkedHashMap<String, Integer>();
		if (getImportCriteria().getEiImportYear() == null || null == eiImportYears.get(key)) {
			while (year.intValue() >= 2017) {
				eiImportYears.put(year.toString(), year--);
			}
		}
		return eiImportYears; // return year 2017 to current year (inclusive)
	}

	public List<SelectItem> getEiImportContentTypes() {
		return ContentTypeDef.getData().getItems().getCurrentItems();
	}

	public List<SelectItem> getEiImportRegulatoryRequirementTypes() {
		DefSelectItems items = RegulatoryRequirementTypeDef.getAllowFileImportData().getItems();
		return items.getCurrentItems();
	}

	public String startOperation() {

		logger.info("startOperation");
		setEiDataImport(this);
		setNoErrors(true);
		setNoWarnings(true);
		setDisplayErr("");
		setDisplayResults("");
		setDisplayLogResults("");

		// Start import only if page validations are successful
		if (getImportCriteria().isValidEiImportFields()) {
			getImportCriteria().setEiDataInfo(new UploadedFileInfo(importCriteria.getEiDataFileToUpload()));

			String timestamp = new SimpleDateFormat("MMddyyyy_HHmmssSSS").format(Calendar.getInstance().getTime());
			getImportCriteria().setLogFileString(logFileString + timestamp + ".txt");
			getImportCriteria().setErrFileString(errFileString + timestamp + ".txt");

			// resetting variables
			setFileStatus(new Integer(0)); // in progress
			setRefreshStr(refreshStrOn);
			setDataImportRowsByFacility(new HashMap<Integer, ArrayList<EiDataImportPeriod>>());
			setFacilityInfo(new HashMap<Integer, Facility>());
			setProcessesAdded(new HashSet<String>());
			setFileLength(getImportCriteria().getEiDataInfo().getLength());
			setFileLengthRead(0);
			setPercentProg(0);
			setCsvParseProgress(0);
			setFetchFacilityProfilesProgress(0);
			this.setValue(getPercentProg());
			this.setMaximum(100);
			
			RunImport importThread = new RunImport();
			importThread.start();
		}

		return "reportsImportEiData";
	}

	class RunImport extends Thread {
		public void run() {
			getEiDataImport().importEiData(getImportCriteria().getEiDataInfo());
			setFileStatus(new Integer(2)); // have results
			setRefreshStr(" ");
		}
	}

	public void importEiData(UploadedFileInfo eiDataInfo) {
		String[] rowData;
		setWriter(null);
		setErrWriter(null);

		try {
			setupLogFiles();
			writeLog("Starting Import for file: " + eiDataInfo.getFileName());

			// Reset variables
			setReport(null);
			setEnabledEmissionReportsForAllFacilities(null);
			setScReportsList(new ArrayList<ReportTemplates>());
			setReport(retrieveServiceCatalog());
			setDataImportPollutants(new ArrayList<SCDataImportPollutant>());
			setCapPollutants(new ArrayList<SCPollutant>());
			maxOperatingHoursInReportingPeriod = calculateMaxOperatingHoursInReportingPeriod();

			if (isValidServiceCatalog()) {
				Collections.sort(getDataImportPollutants());
				Collections.sort(getCapPollutants(), new Comparator<SCPollutant>() {
					public int compare(SCPollutant pol1, SCPollutant pol2) {
						return pol1.getDisplayOrder().compareTo(pol2.getDisplayOrder());
					};
				});
			} else {
				// Error with SC
				closeLogFileWriters();
				return;
			}

			// Retrieve Facility info and add to map processInformationForFacilities. Used for
			// validation.
			populateProcessInformationForAllFacilities();
			populateEnabledReportingForFacilities();

		} catch (RemoteException e1) {
			logger.error("Unable to retrieve Service catalog", e1);
			populateErrorDisplay(e1);
			return;
		} catch (IOException e) {
			logger.error("Error with log files", e);
			populateErrorDisplay(e);
			return;
		}

		int rowCt = 0;
		int tooShortCnt = 0;
		int validationCnt = 0;
		int warningsCnt = 0;
		List<String> warnings = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(eiDataInfo.getFileReader());

			rowData = readRecord(br);
			while (rowData != null) {
				ArrayList<String> validationMessage = new ArrayList<String>();
				ArrayList<String> warningMessage = new ArrayList<String>();
				EiDataImportRow dataImportRow = null;
				boolean isSkipRow = false;
				try {
					// Perform basic validations for row.
					// Validate row has at least the minimum number of required columns
					if (rowData.length < getTotalRequiredColumns()) {
						validationMessage.add("Row does not have expected number of columns. "
								+ getTotalRequiredColumns().toString() + " columns expected.");
						tooShortCnt++;
					} else {
						// Add warning if row has more columns than expected.
						if (rowData.length > getTotalRequiredColumns()) {
							warningMessage.add(
									"Row has more columns than expected: " + rowData.length + ". Columns expected: "
											+ getTotalRequiredColumns().toString() + ". Ignoring excess columns.");
						}

						String processKey = getProcessKey(rowData[0], rowData[1], rowData[2]);
						if (getProcessesAdded().contains(processKey)) {
							warningMessage
									.add("Duplicate row found for combination: " + processKey + ". Skipping row.");
							isSkipRow = true;
						} else {
							// Validate individual fields and get DTO
							dataImportRow = new EiDataImportRow(rowData, getDataImportPollutants(), getCapPollutants(),
									getMaxOperatingHoursInReportingPeriod(), getEnabledEmissionReportsForAllFacilities());
							validationMessage = dataImportRow.getValidationMessages();
							warningMessage.addAll(dataImportRow.getWarningMessages());
							getProcessesAdded().add(processKey);
						}
					}

					if (!isSkipRow && validationMessage.isEmpty()) {
						// Basic validations passed. Perform DB validations.
						String processKey = getProcessKey(dataImportRow.getFacilityId(), dataImportRow.getEuId(),
								dataImportRow.getProcessId());
						if (!getProcessInformationForAllFacilities().containsKey(processKey)) {
							validationMessage
									.add("Facility id, EU id and Process Id combination does not exist in IMPACT for Facility id: "
											+ dataImportRow.getFacilityId() + ", EU Id: " + dataImportRow.getEuId()
											+ ", Process Id: " + dataImportRow.getProcessId());
						}
					}
				} catch (Exception e) {
					if (getDisplayErr().length() == 0) {
						logger.error("Unable to validateRow()", e);
						writeLog("Process errored out.");
						populateErrorDisplay(e);
					}
					br.close();
					return;
				}

				if (isSkipRow) {
					// do nothing
				} else if (!validationMessage.isEmpty()) {
					// Row is Invalid. (validationMessage is not empty)
					validationCnt++;
					String errors = String.join("\n ", validationMessage);
					String errStr = "Validation error in Row " + (rowCt + 1) + " with the following errors: " + errors;

					writeError(errStr);
				} else {
					// DB validations passed. Add to map.
					// Verify if there is a duplicate triplet of FacilityId,
					// EuId and ProcessId
					String processKey = getProcessKey(dataImportRow.getFacilityId(), dataImportRow.getEuId(),
							dataImportRow.getProcessId());
					EiDataImportFacilityInformation processInformation = getProcessInformationForAllFacilities()
							.get(processKey);
					Integer fpId = processInformation.getFpId();

					EiDataImportPeriod eiDataImportPeriod = new EiDataImportPeriod(dataImportRow,
							processInformation.getSccId(), processInformation.getCorrEpaEmuId());

					if (!getDataImportRowsByFacility().containsKey(fpId)) {
						getDataImportRowsByFacility().put(fpId, new ArrayList<EiDataImportPeriod>());
					}

					getDataImportRowsByFacility().get(fpId).add(eiDataImportPeriod);
				}

				if (!warningMessage.isEmpty()) {
					// Write warning messages to log
					warningsCnt++;
					String warning = String.join("\n ", warningMessage);
					String warningsStr = "Warning in Row " + (rowCt + 1) + ": " + warning;
					warnings.add(warningsStr);
				}

				// Read next row
				rowCt++;
				rowData = readRecord(br);
			}

			setCsvParseProgress(getPercentProg());
			// All rows have been parsed. Proceed with import based on user selection.
			if (isNoErrors() && getImportCriteria().getImportChoice().equals(EiDataImportCriteria.PERFORM_IMPORT)) {
				// No csv validation errors and operation is Perform Import.
				writeLog("Primary validations passed. Starting Perform EI data import. Number of rows: " + rowCt
						+ ". Number of facilities: " + getDataImportRowsByFacility().size());
				int facilityCount = 0;
				for (Integer fpId : getDataImportRowsByFacility().keySet()) {
					getFacilityInfo().put(fpId, facilityService.retrieveFacilityProfile(fpId, false));
					facilityCount++;
					updateFetchFacilityProfileProgress(facilityCount, getDataImportRowsByFacility().size());
				}

				// Create EmissionsReport objects.
				List<EmissionsReport> emissionsReports = getEmissionsReportService()
						.createEmissionsReportFromCSV(getEiDataImport());
				int emissionRptCreatedCount = 0;
				setFetchFacilityProfilesProgress(getPercentProg());
				for (int currentRptCount = 1; currentRptCount <= emissionsReports.size(); currentRptCount++) {
					// Perform validation similar to clicking validate button in the UI.
					EmissionsReport emissionRpt = emissionsReports.get(currentRptCount-1);
					Facility facilityProfile = getFacilityInfo().get(emissionRpt.getFpId());
					if(facilityProfile == null) {
						continue;
					}
					boolean isEICreated = validateAndPerformImport(emissionRpt, facilityProfile, warnings);
					if(isEICreated){
						emissionRptCreatedCount++;
					}
					updateValidateAndImportProgress(currentRptCount, emissionsReports.size());
				}
				populatePerformImportResults(rowCt, emissionsReports.size(), emissionRptCreatedCount);
				writeLog("Process Complete.");
				writeLog(getDisplayLogResults());
			} else {
				// Populate displayResults to be display results on the screen.
				populateAnalyzeResults(rowCt, validationCnt, tooShortCnt, warningsCnt);
				writeLog("Process Complete. Number of rows: " + rowCt);
				writeLog(getDisplayLogResults());
			}

			// Write warnings to error file.
			writeWarning(warnings);

			// closing writer and errWriter
			closeLogFileWriters();
		} catch (IOException e) {
			String s = "\n\nIOException:  " + ", rowCt=" + (rowCt + 1) + "\n";
			logger.error(s, e);
			try {
				writeLog("Process errored out.");
				writeError(s);
				getErrWriter().flush();
			} catch (IOException ee) {
				logger.error("Unable to write err log", ee);
			}
			populateErrorDisplay(e);
			return;
		} catch (Exception e) {
			logger.error("Exception generated: ", e);
			try {
				writeLog("Process errored out.");
				writeError("System Error: " + e.getMessage());
				getErrWriter().flush();
			} catch (IOException ee) {
				logger.error("Unable to write err log", ee);
			}
			populateErrorDisplay(e);
			return;
		}
	}

	/**
	 * @param warningMsgs
	 * @return
	 * @throws ApplicationException
	 * @throws IOException
	 */
	private boolean validateAndPerformImport(EmissionsReport emissionRpt, Facility facilityProfile,
			List<String> warningMsgs) throws IOException {
		logger.info("Starting Perform EI data import for facility : " + facilityProfile.getFacilityId());
		boolean isEICreated = false;
		Map<String, ArrayList<String>> validationMessages = new HashMap<String, ArrayList<String>>();

		try {
			validationMessages = validateEmissionsReport(emissionRpt, facilityProfile);
			ArrayList<String> validationMessagesErr = validationMessages.get(ERROR);
			ArrayList<String> validationMessagesWarn = validationMessages.get(WARNING);

			if (validationMessagesWarn.size() != 0) {
				writeLog("Validation has warnings for Facility: " + facilityProfile.getFacilityId() + ".");
				String warnStr = "Validation Warnings for Facility: " + facilityProfile.getFacilityId() + ". "
						+ String.join("\n ", validationMessagesWarn);
				warningMsgs.add(warnStr);
			}

			if (validationMessagesErr.size() == 0) {
				// Validations passed. Proceed with inserting EI Report to DB.
				writeLog("Validation passed for Facility: " + facilityProfile.getFacilityId()
						+ ". Creating Emissions Report.");
				emissionRpt.setValidated(true);

				EmissionsReport createdRpt = getEmissionsReportService().createEmissionsReportFromCSV(facilityProfile,
						emissionRpt, getScReportsList());
				writeLog("Emission Inventory created: " + createdRpt.getEmissionsInventoryId());
				isEICreated = true;
			} else {
				// Validations failed. Create validation result and add to error log.
				writeLog("Validation failed for Facility: " + facilityProfile.getFacilityId()
						+ ". Emission Inventory report not created.");
				String errStr = "Validation error for Facility: " + facilityProfile.getFacilityId()
						+ ". Emission Inventory not created.\n " + String.join("\n ", validationMessagesErr);
				writeError(errStr);
				isEICreated = false;
			}
		} catch (ValidationException | ApplicationException | DAOException e) {
			writeError("Failed to create EI report for facility: " + facilityProfile.getFacilityId()
					+ " due to system error: " + e.getMessage());
			logger.error(
					"Exception occured while validating or creating EI object for facility: " + facilityProfile.getFacilityId(), e);
		}

		return isEICreated;
	}

	private String getProcessKey(String facilityId, String euId, String processId) {
		String ret = null;
		ret = facilityId.toUpperCase() + "-" + euId.toUpperCase() + "-" + processId.toUpperCase();
		return ret;
	}

	private void populateProcessInformationForAllFacilities() throws DAOException {
		ArrayList<EiDataImportFacilityInformation> facilityInformation = null;

		setProcessInformationForAllFacilities(new HashMap<String, EiDataImportFacilityInformation>());
		facilityInformation = facilityService.retrieveEiDataImportFacilityInformation();
		for (EiDataImportFacilityInformation currentEntry : facilityInformation) {
			String key = getProcessKey(currentEntry.getFacilityId(), currentEntry.getEuId(),
					currentEntry.getProcessId().toUpperCase());
			getProcessInformationForAllFacilities().put(key, currentEntry);
		}
	}

	private String[] readRecord(BufferedReader br) throws IOException {
		String[][] values = new String[0][0];
		try {
			String rec = br.readLine();
			updateCSVParseProgress(rec);
			if (Utility.isNullOrEmpty(rec)) {
				return null; // end of file
			}
			values = ExcelCSVParser.parse(rec);
		} catch (IOException ioe) {
			logger.error("Unable to read a Data import record", ioe);
			populateErrorDisplay(ioe);
			throw ioe;
		}
		return values[0];
	}

	private SCEmissionsReport retrieveServiceCatalog() throws RemoteException {
		Integer sCEmissionsReportId = null;

		sCEmissionsReportId = infrastructureService.retrieveSCEmissionsReportId(getImportCriteria().getEiImportYear(),
				getImportCriteria().getEiImportContentTypeCd(), importCriteria.getEiImportRegulatoryRequirement());
		if (sCEmissionsReportId == null) {
			return null;
		} else {
			return infrastructureService.retrieveSCEmissionsReport(sCEmissionsReportId);
		}
	}
	
	private void populateEnabledReportingForFacilities() throws RemoteException {
		Integer reportingYear = getImportCriteria().getEiImportYear();
		String contentTypeCd = getImportCriteria().getEiImportContentTypeCd();
		setEnabledEmissionReportsForAllFacilities(
				emissionsReportService.retrieveEnabledEmissionRptsForYearAndContentType(reportingYear, contentTypeCd));
	}
	
	private long calculateMaxOperatingHoursInReportingPeriod() {
		ContentTypeDef contentTypeDef = ContentTypeDef.getContentTypeDef(getImportCriteria().getEiImportContentTypeCd());

		LocalDate periodStarDate = LocalDate.of(getImportCriteria().getEiImportYear(), contentTypeDef.getStartMonth(),
				contentTypeDef.getStartDay());

		LocalDate periodEndDate = LocalDate.of(getImportCriteria().getEiImportYear(), contentTypeDef.getEndMonth(),
				contentTypeDef.getEndDay());

		// add 1 for the days to be inclusive
		long daysInBetween = ChronoUnit.DAYS.between(periodStarDate, periodEndDate) + 1;

		return daysInBetween * 24;
	}

	/**************************************************
	 * Validation Methods - Start
	 **************************************************/

	private boolean isValidServiceCatalog() throws IOException {
		boolean ret = true;
		if (getReport() != null) {
			List<SCDataImportPollutant> dataImportPollutants = getReport().getDataImportPollutantList();
			List<SCPollutant> capPollutants = Arrays.asList(getReport().getPollutants());
			setTotalRequiredColumns(
					MINIMUN_REQUIRED_COLUMNS + (capPollutants.size() * 2) + (dataImportPollutants.size() * 2));
			getDataImportPollutants().addAll(dataImportPollutants);
			getCapPollutants().addAll(capPollutants);
			writeLog("Service catalog found. Number of pollutants in Criteria Air Pollutants/Other table: "
					+ capPollutants.size() + ", Number of pollutants in Data Import File Pollutants table: "
					+ dataImportPollutants.size() + ". Number of columns expected per row: " + getTotalRequiredColumns());
		} else {
			// Service catalog not found for Import criteria
			ret = false;
			String errStr = "No Service catalog found for : " + getImportCriteria().getEiImportYear() + " "
					+ getImportCriteria().getEiImportContentTypeCd() + " "
					+ getImportCriteria().getEiImportRegulatoryRequirement() + ". 0 rows processed.";
			logger.error(errStr);
			writeError(errStr);
			populateErrorDisplay(errStr);
		}
		return ret;
	}

	private Map<String, ArrayList<String>> validateEmissionsReport(EmissionsReport emissionRpt, Facility facility)
			throws ValidationException, ApplicationException, DAOException {

		List<ValidationMessage> emissionRptValidationMessages = new ArrayList<ValidationMessage>();
		Map<String, ArrayList<String>> valMessages = new HashMap<String, ArrayList<String>>();
		valMessages.put(ERROR, new ArrayList<String>());
		valMessages.put(WARNING, new ArrayList<String>());

		// Check if there were any validation issues when EI object was created.
		HashMap<String, ValidationMessage> emissionRptValidationMsgs = emissionRpt.getValidationMessages();

		if (!emissionRptValidationMsgs.isEmpty()) {
			ArrayList<ValidationMessage> emissionRptValidations = new ArrayList<ValidationMessage>(
					emissionRptValidationMsgs.values());
			populateValidationMessages(valMessages, emissionRptValidations, false);
			emissionRpt.getValidationMessages().clear();
		}

		ReportTemplates scReports = new ReportTemplates();
		scReports.setSc(getReport());
		getScReportsList().add(scReports);

		ArrayList<Integer> euListBasic = new ArrayList<Integer>();
		ArrayList<Integer> euList = new ArrayList<Integer>();
		emissionRpt.getReportEmuIds(facility, scReports, euListBasic, euList);

		// Facility validation
		List<ValidationMessage> facilityValidationMessages = new ArrayList<ValidationMessage>();
		facilityValidationMessages = FacilityValidation.validateEISemissionReport(emissionRpt, facility, euListBasic,
				euList, true);

		// Billing contact validation - facility
		if (getEmissionsReportService().isEmissionsReportBillable(getScReportsList())) {
			List<ValidationMessage> billingErr = FacilityValidation.determineMissingBilling(facility,
					emissionRpt.getReportYear(), true);
			if (!billingErr.isEmpty())
				facilityValidationMessages.addAll(billingErr);
		}

		// Emission Report validation
		emissionRptValidationMessages = emissionRpt.submitVerify(emissionsReportService,
				facilityValidationMessages.size() != 0);

		populateValidationMessages(valMessages, facilityValidationMessages, false);
		populateValidationMessages(valMessages, emissionRptValidationMessages, true);

		return valMessages;
	}

	private void populateValidationMessages(Map<String, ArrayList<String>> valMessageMap,
			List<ValidationMessage> validationMessages, boolean isAddEUToMsg) {
		for (ValidationMessage valMsg : validationMessages) {
			String euId = isAddEUToMsg ? valMsg.getEuId() + ": " : "";
			if (valMsg.getSeverity().equals(ValidationMessage.Severity.ERROR)) {
				valMessageMap.get(ERROR).add(euId + valMsg.getMessage());
			} else if (valMsg.getSeverity().equals(ValidationMessage.Severity.WARNING)) {
				valMessageMap.get(WARNING).add(euId + valMsg.getMessage());
			}
		}
	}

	/**************************************************
	 * Validation Methods - End
	 **************************************************/

	/**************************************************
	 * Utility Methods - Start
	 **************************************************/

	private void setupLogFiles() throws IOException {
		String filePath = DocumentUtil.getFileStoreRootPath() + EI_DATA_IMPORT_PATH;
		String logFile = filePath + File.separatorChar + getImportCriteria().getLogFileString();
		String errFile = filePath + File.separatorChar + getImportCriteria().getErrFileString();
		DocumentUtil.mkDir(EI_DATA_IMPORT_PATH);
		setWriter(new FileWriter(logFile));
		setErrWriter(new FileWriter(errFile));
	}

	private void closeLogFileWriters() throws IOException {
		getWriter().close();
		getErrWriter().close();
	}

	private void populateAnalyzeResults(int rowCt, int validationCnt, int tooShortCnt, int warningsCnt) {
		StringBuffer results = new StringBuffer(1000);
		StringBuffer logResults = new StringBuffer(1000);

		results.append("Results of processing file <b>" + getImportCriteria().getEiDataInfo().getFileName()
				+ "</b> and operation <b>" + getImportCriteria().getOperation() + " :</b><br><br>");
		logResults.append("Results of processing file " + getImportCriteria().getEiDataInfo().getFileName()
				+ " and operation " + getImportCriteria().getOperation() + " :\n");

		results.append(validationCnt + " Records skipped because of Validation Problems<br>");
		logResults.append("\t" + validationCnt + " Records skipped because of Validation Problems\n");

		results.append(tooShortCnt + " Records skipped because too short (not enough fields)<br>");
		logResults.append("\t" + tooShortCnt + " Records skipped because too short (not enough fields)\n");

		results.append(warningsCnt + " Records had warnings<br>");
		logResults.append("\t" + warningsCnt + " Records had warnings\n");

		results.append(rowCt + " Total row(s) processed from import file<br>");
		logResults.append("\t" + rowCt + " Total row(s) processed from import file\n");

		setDisplayResults(results.toString());
		setDisplayLogResults(logResults.toString());
	}

	private void populatePerformImportResults(int rowCt, int emissionsReportsCount, int emissionRptCreatedCount) {
		StringBuffer results = new StringBuffer(1000);
		StringBuffer logResults = new StringBuffer(1000);

		results.append("Results of processing file <b>" + getImportCriteria().getEiDataInfo().getFileName()
				+ "</b> and operation <b>" + getImportCriteria().getOperation() + " :</b><br><br>");
		logResults.append("Results of processing file " + getImportCriteria().getEiDataInfo().getFileName()
				+ " and operation " + getImportCriteria().getOperation() + " :\n");

		results.append(rowCt + " Total row(s) processed from import file<br>");
		logResults.append("\t" + rowCt + " Total row(s) processed from import file\n");

		results.append(emissionsReportsCount + " Total Emission Report(s) processed from import file<br>");
		logResults.append("\t" + emissionsReportsCount + " Total Emission Report(s) processed from import file\n");

		results.append(emissionRptCreatedCount + " Total Emission Report(s) successfully created<br>");
		logResults.append("\t" + emissionRptCreatedCount + " Total Emission Report(s) successfully created\n");

		setDisplayResults(results.toString());
		setDisplayLogResults(logResults.toString());
	}

	private void updateCSVParseProgress(String rec) {
		int weight = getImportCriteria().getImportChoice().equals(EiDataImportCriteria.ANALYZE_IMPORT) ? 100 : csvParseProgressWeight;
		if (rec != null) {
			setFileLengthRead(getFileLengthRead() + rec.length());
			setPercentProg((getFileLengthRead() * weight) / getFileLength());
			this.setValue(getPercentProg());
		}
	}
	
	private void updateFetchFacilityProfileProgress(int currentFacilityNum, int totalFacilities) {
		long percentProgress = getCsvParseProgress() + (currentFacilityNum * fetchFacilityProfilesProgressWeight) / totalFacilities;
		setPercentProg(percentProgress);
		this.setValue(getPercentProg());
	}
	
	private void updateValidateAndImportProgress(int currentRptNum, int totalRpts) {
		long percentProgress = getFetchFacilityProfilesProgress() + (currentRptNum * validateAndImportEIProgressWeight) / totalRpts;
		setPercentProg(percentProgress);
		this.setValue(getPercentProg());
	}
	
	private void writeLog(String str) throws IOException {
		getWriter().write(getCurrentTimeStamp() + " : " + str);
		getWriter().write("\n\n");
	}

	private void writeError(String str) throws IOException {
		if (isNoErrors()) {
			setNoErrors(false);
			getErrWriter().write("ERRORS: ");
			getErrWriter().write("\n\n");
		}
		getErrWriter().write(str);
		getErrWriter().write("\n\n");
	}

	private void writeWarning(List<String> warnings) throws IOException {
		if (!warnings.isEmpty()) {
			setNoWarnings(false);
			getErrWriter().write("\n\n");
			getErrWriter().write("WARNINGS:");
			getErrWriter().write("\n\n");
			for (String warning : warnings) {
				getErrWriter().write(warning);
				getErrWriter().write("\n\n");
			}
		}
	}

	private void populateErrorDisplay(Exception e) {
		setDisplayErr("An error has occurred:  " + e.getMessage());
		try {
			getWriter().close();
			getErrWriter().close();
		} catch (Exception ee) {
			;
		}
	}

	private void populateErrorDisplay(String errorMsg) {
		setDisplayErr("An error has occurred:  " + errorMsg);
	}

	private String getCurrentTimeStamp() {
		return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
	}

	/**************************************************
	 * Utility Methods - End
	 **************************************************/

	/* START CODE: attachments */
	public AttachmentEvent createAttachment(Attachments attachments) throws AttachmentException {// return
																									// null
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		boolean ok = true;
		if (attachments.getDocument() == null) {
			// should never happen
			logger.error("Attempt to process null attachment");
			ok = false;
		} else {
			if (attachments.isTradeSecretAllowed() && attachments.getTradeSecretAttachmentInfo() == null
					&& attachments.getTsFileToUpload() != null) {
				attachments.setTradeSecretAttachmentInfo(new UploadedFileInfo(attachments.getTsFileToUpload()));
			}
			if (attachments.getPublicAttachmentInfo() == null && attachments.getFileToUpload() != null) {
				attachments.setPublicAttachmentInfo(new UploadedFileInfo(attachments.getFileToUpload()));
			}

			// make sure document description and type are provided
			if (attachments.getTempDoc().getDescription() == null
					|| attachments.getTempDoc().getDescription().trim().equals("")) {
				validationMessages
						.add(new ValidationMessage("description", "Attribute " + "Description" + " is not set.",
								ValidationMessage.Severity.ERROR, "descriptionText"));
			}

			if (attachments.getTempDoc().getDocTypeCd() == null
					|| attachments.getTempDoc().getDocTypeCd().trim().equals("")) {
				validationMessages
						.add(new ValidationMessage("docTypeCd", "Attribute " + "Attachment Type" + " is not set.",
								ValidationMessage.Severity.ERROR, "reportType"));
			}

			// make sure document file is provided
			if (attachments.getPublicAttachmentInfo() == null && attachments.getTempDoc().getDocumentID() == null) {
				validationMessages.add(new ValidationMessage("documentID", "Please specify a Public File to Upload",
						ValidationMessage.Severity.ERROR, "publicFile"));
			}

			// make sure a justification is provided for trade secret document
			if (attachments.isTradeSecretAllowed()) {
				if (attachments.getTradeSecretAttachmentInfo() != null
						|| attachments.getTempDoc().getTradeSecretDocId() != null) {
					if (attachments.getTempDoc().getTradeSecretJustification() == null
							|| attachments.getTempDoc().getTradeSecretJustification().trim().equals("")) {
						validationMessages.add(new ValidationMessage("tradeSecretJustification",
								"Attribute " + "Trade Secret Justification" + " is not set.",
								ValidationMessage.Severity.ERROR, "tradeSecretReason"));
					}
				} else if (attachments.getTempDoc().getTradeSecretJustification() != null
						&& attachments.getTempDoc().getTradeSecretJustification().trim().length() > 0) {
					validationMessages.add(new ValidationMessage("tradeSecretJustification",
							"The Trade Secret Justification field should only be populated when a trade secret document is specified.",
							ValidationMessage.Severity.ERROR, "tradeSecretReason"));
				}
			}
		}

		if (validationMessages.size() == 0 && ok) {
			try {
				try {
					// process trade secret attachment (if there is one)
					Attachment tsAttachment = null;
					InputStream tsInputStream = null;
					if (attachments.isTradeSecretAllowed() && attachments.getTradeSecretAttachmentInfo() != null) {
						tsAttachment = new Attachment();
						tsAttachment.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
						tsAttachment.setUploadDate(tsAttachment.getLastModifiedTS());
						tsAttachment.setExtension(DocumentUtil
								.getFileExtension(attachments.getTradeSecretAttachmentInfo().getFileName()));
						if (isInternalApp()) {
							tsAttachment.setLastModifiedBy(InfrastructureDefs.getCurrentUserId());
						} else {
							tsAttachment.setLastModifiedBy(CommonConst.GATEWAY_USER_ID);
						}
						// need object id to be set to put file in correct
						// directory
						tsAttachment.setSubPath("tmp" + File.separator + "EiDataImport");
						tsInputStream = attachments.getTradeSecretAttachmentInfo().getInputStream();
					}
					Attachment publicAttachment = attachments.getTempDoc();
					InputStream publicInputStream = attachments.getPublicAttachmentInfo().getInputStream();
					getEmissionsReportService().createEiDataImportAttachment(getImportCriteria(), publicAttachment,
							publicInputStream, tsAttachment, tsInputStream);
				} catch (IOException e) {
					throw new RemoteException(e.getMessage(), e);
				}
				// Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
				// a.setAttachmentList(importCriteria.getAttachmentDocs().toArray(new
				// Attachment[0]));

				attachments.setAttachmentList(getImportCriteria().getAttachmentDocs().toArray(new Attachment[0]));
				attachments.cleanup();
				attachments.setFileToUpload(null);
				attachments.setTsFileToUpload(null);
				attachments.setDocument(null);
				DisplayUtil.displayInfo("The attachment has been uploaded.");
				FacesUtil.returnFromDialogAndRefresh();

			} catch (RemoteException e) {
				handleException(e);
			}
		} else {
			displayValidationMessages("", validationMessages.toArray(new ValidationMessage[0]));
		}
		return null;
	}

	public AttachmentEvent updateAttachment(Attachments attachments) {
		boolean ok = true;
		// make sure document description is provided
		if (attachments.getDocument().getDescription() == null
				|| attachments.getDocument().getDescription().trim().equals("")) {
			DisplayUtil.displayError("Please specify the description for this attachment");
			ok = false;
		}
		if (attachments.isTradeSecretAllowed() && attachments.getDocument().getTradeSecretDocId() != null) {
			if (attachments.getDocument().getTradeSecretJustification() == null
					|| attachments.getDocument().getTradeSecretJustification().trim().equals("")) {
				DisplayUtil.displayError("Please specify the Trade Secret Justification for this attachment");
				ok = false;
			}
		}
		if (ok) {
			DisplayUtil.displayInfo("Attachment information was successfully updated");
			FacesUtil.returnFromDialogAndRefresh();
		}
		return null;
	}

	public AttachmentEvent deleteAttachment(Attachments attachments) {
		getImportCriteria().getAttachmentDocs().remove(attachments.getTempDoc());
		attachments.setAttachmentList(getImportCriteria().getAttachmentDocs().toArray(new Attachment[0]));
		attachments.cleanup();
		DisplayUtil.displayInfo("The attachment has been removed.");
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public void cancelAttachment() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	/**************************************************
	 * Getters and Setters
	 **************************************************/
	public synchronized String getRefreshStr() {
		return refreshStr;
	}

	public synchronized void setRefreshStr(String refreshStr) {
		this.refreshStr = refreshStr;
	}
	
	public synchronized Integer getFileStatus() {
		return fileStatus;
	}

	public synchronized void setFileStatus(Integer fileStatus) {
		this.fileStatus = fileStatus;
	}

	public static String getLogFileString() {
		return logFileString;
	}

	public static void setLogFileString(String logFileString) {
		EiDataImport.logFileString = logFileString;
	}

	public static String getErrFileString() {
		return errFileString;
	}

	public static void setErrFileString(String errFileString) {
		EiDataImport.errFileString = errFileString;
	}

	public synchronized String getDisplayResults() {
		return displayResults;
	}

	public synchronized void setDisplayResults(String displayResults) {
		this.displayResults = displayResults;
	}

	public synchronized String getDisplayLogResults() {
		return displayLogResults;
	}

	public synchronized void setDisplayLogResults(String displayLogResults) {
		this.displayLogResults = displayLogResults;
	}

	public synchronized String getDisplayErr() {
		return displayErr;
	}

	public synchronized void setDisplayErr(String displayErr) {
		this.displayErr = displayErr;
	}

	public synchronized boolean isNoErrors() {
		return noErrors;
	}

	public synchronized void setNoErrors(boolean noErrors) {
		this.noErrors = noErrors;
	}

	public synchronized EiDataImportCriteria getImportCriteria() {
		return importCriteria;
	}

	public synchronized void setImportCriteria(EiDataImportCriteria importCriteria) {
		this.importCriteria = importCriteria;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public EmissionsReportService getEmissionsReportService() {
		return emissionsReportService;
	}

	public void setEmissionsReportService(EmissionsReportService emissionsReportService) {
		this.emissionsReportService = emissionsReportService;
	}

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}

	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}

	public Integer getTotalRequiredColumns() {
		return totalRequiredColumns;
	}

	public void setTotalRequiredColumns(Integer totalRequiredColumns) {
		this.totalRequiredColumns = totalRequiredColumns;
	}

	public synchronized Map<Integer, ArrayList<EiDataImportPeriod>> getDataImportRowsByFacility() {
		return dataImportRowsByFacility;
	}

	public synchronized void setDataImportRowsByFacility(Map<Integer, ArrayList<EiDataImportPeriod>> dataImportRowsByFacility) {
		this.dataImportRowsByFacility = dataImportRowsByFacility;
	}

	public synchronized FileWriter getWriter() {
		return writer;
	}

	public synchronized void setWriter(FileWriter writer) {
		this.writer = writer;
	}

	public synchronized FileWriter getErrWriter() {
		return errWriter;
	}

	public synchronized void setErrWriter(FileWriter errWriter) {
		this.errWriter = errWriter;
	}

	public synchronized Map<String, EiDataImportFacilityInformation> getProcessInformationForAllFacilities() {
		return processInformationForAllFacilities;
	}

	public synchronized void setProcessInformationForAllFacilities(
			Map<String, EiDataImportFacilityInformation> processInformationForFacilities) {
		this.processInformationForAllFacilities = processInformationForFacilities;
	}

	public double getMaxOperatingHoursInReportingPeriod() {
		return maxOperatingHoursInReportingPeriod;
	}

	public void setMaxOperatingHoursInReportingPeriod(double maxOperatingHoursInReportingPeriod) {
		this.maxOperatingHoursInReportingPeriod = maxOperatingHoursInReportingPeriod;
	}

	public synchronized Map<Integer, Facility> getFacilityInfo() {
		return facilityInfo;
	}

	public synchronized void setFacilityInfo(Map<Integer, Facility> facilityInfo) {
		this.facilityInfo = facilityInfo;
	}

	public synchronized boolean isNoWarnings() {
		return noWarnings;
	}

	public synchronized void setNoWarnings(boolean noWarnings) {
		this.noWarnings = noWarnings;
	}

	public synchronized Integer getCurrentUserId() {
		return currentUserId;
	}

	public synchronized void setCurrentUserId(Integer currentUserId) {
		this.currentUserId = currentUserId;
	}

	public synchronized List<SCDataImportPollutant> getDataImportPollutants() {
		return dataImportPollutants;
	}

	public synchronized void setDataImportPollutants(List<SCDataImportPollutant> dataImportPollutants) {
		this.dataImportPollutants = dataImportPollutants;
	}

	public synchronized List<SCPollutant> getCapPollutants() {
		return capPollutants;
	}

	public synchronized void setCapPollutants(List<SCPollutant> capPollutants) {
		this.capPollutants = capPollutants;
	}

	public synchronized List<ReportTemplates> getScReportsList() {
		return scReportsList;
	}

	public synchronized void setScReportsList(List<ReportTemplates> scReportsList) {
		this.scReportsList = scReportsList;
	}

	public synchronized Map<String, Integer> getEnabledEmissionReportsForAllFacilities() {
		return enabledEmissionReportsForAllFacilities;
	}

	public synchronized void setEnabledEmissionReportsForAllFacilities(Map<String, Integer> enabledEmissionReportsForAllFacilities) {
		this.enabledEmissionReportsForAllFacilities = enabledEmissionReportsForAllFacilities;
	}

	public synchronized long getPercentProg() {
		return percentProg;
	}

	public synchronized void setPercentProg(long percentProg) {
		this.percentProg = percentProg;
	}

	public synchronized long getCsvParseProgress() {
		return csvParseProgress;
	}

	public synchronized void setCsvParseProgress(long csvParseProgress) {
		this.csvParseProgress = csvParseProgress;
	}

	public synchronized long getFetchFacilityProfilesProgress() {
		return fetchFacilityProfilesProgress;
	}

	public synchronized void setFetchFacilityProfilesProgress(long fetchFacilityProfilesProgress) {
		this.fetchFacilityProfilesProgress = fetchFacilityProfilesProgress;
	}

	public synchronized Set<String> getProcessesAdded() {
		return processesAdded;
	}

	public synchronized void setProcessesAdded(Set<String> processesAdded) {
		this.processesAdded = processesAdded;
	}

	public synchronized long getFileLength() {
		return fileLength;
	}

	public synchronized void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public synchronized long getFileLengthRead() {
		return fileLengthRead;
	}

	public synchronized void setFileLengthRead(long fileLengthRead) {
		this.fileLengthRead = fileLengthRead;
	}

	public synchronized EiDataImport getEiDataImport() {
		return eiDataImport;
	}

	public synchronized void setEiDataImport(EiDataImport eiDataImport) {
		this.eiDataImport = eiDataImport;
	}

	public synchronized SCEmissionsReport getReport() {
		return report;
	}

	public synchronized void setReport(SCEmissionsReport report) {
		this.report = report;
	}
}
