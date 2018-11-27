package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.util.ResultSetHelper;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.wy.state.deq.impact.def.FCEInspectionReportStateDef;
import us.wy.state.deq.impact.def.FCEPreSnapshotTypeDef;
import us.wy.state.deq.impact.def.OverallInspectionComplianceStatusDef;

public class FullComplianceEval extends CetaBaseDB {
	
	private static final long serialVersionUID = 1057335687987943259L;

	private Integer id;
	private Integer fpId;
	private Integer facilityHistId;
	private String facilityId;
	private transient String facilityNm;
	private transient String scscId;
	private transient Integer versionId;
	private Integer assignedStaff;
	private Timestamp scheduledTimestamp; // first month of quarter scheduled.
	private Timestamp schedAfsDate;
	private String schedAfsId;
	private Integer evaluator;
	private Timestamp dateEvaluated; // completion date
	private Timestamp dateReported; // report completion date
	private Timestamp evalAfsDate;
	private String evalAfsId;
	protected Timestamp createdDt;
	protected Integer createdById;
	private String memo;
	private boolean usEpaCommitted;
	private transient SiteVisit[] assocSiteVisits;
	private transient StackTest[] assocStackTests;
	private transient StackTest[] stackTests; // nonassociated stack tests
	private transient SiteVisit[] siteVisits; // PCE visits
	private boolean pre10Legacy;
	private List<FceAttachment> attachments = null;
	private String fullName; // made up of last and first name of person assigned
	private String evalFullName; // made up of last and first name of person who evaluated
	private FacilityHistory facilityHistory;

	private transient String operatingStatusCd; // set from query for Inspection
	private transient Timestamp lastShutdownDate; // set from query for Inspection

	private transient boolean portable;
	private String inspId;
	private String cmpId;
	private String companyName;
	private Note[] inspectionNotes;
	private Timestamp dateSentToEPA;
	private String inspectionType;
	private boolean legacyInspection;
	private String facilityTypeCd;
	private String permitClassCd;
	private String facilityAfsNumber;

	private transient List<ComplianceStatusEvent> assocComplianceStatusEvents;

	// New fields for Inspection report enhancement
	private String inspectionReportStateCd;
	private boolean privatePropertyAccess;
	private List<Evaluator> additionalAqdStaffPresent = new ArrayList<Evaluator>();
	private String facilityStaffPresent;
	private boolean announcedInspection = true;

	// Ambient Conditions
	private AmbientConditions dayOneAmbientConditions;
	private AmbientConditions dayTwoAmbientConditions;

	// Pre-inspection Data page fields ??
	private Integer lastFceId;
	private String lastInspId;
	private Timestamp lastInspDate;

	private String inspectionConcerns;
	private String fileReview;
	private String regulatoryDiscussion;
	private String physicalInspectionOfPlant;
	private String ambientMonitoring;
	private String otherInformation;

	private FcePreData fcePreData = new FcePreData();

	private List<Integer> associatedPermits;
	private List<FcePermitCondition> fcePermitConditions;
	
	private Timestamp referenceReviewStartDate;
	

	private String complianceStatusCd;
	private String complianceStatusLongDesc;
	
	private List<String> inspectionsReferencedIn;

	public FullComplianceEval() {
		super();
		siteVisits = new SiteVisit[0];
		assocSiteVisits = new SiteVisit[0];
		assocComplianceStatusEvents = new ArrayList<ComplianceStatusEvent>();
		dayOneAmbientConditions = new AmbientConditions(AmbientConditions.AMBIENT_CONDITIONS_DAY_ONE);
		dayTwoAmbientConditions = new AmbientConditions(AmbientConditions.AMBIENT_CONDITIONS_DAY_TWO);
		inspectionReportStateCd = FCEInspectionReportStateDef.INITIAL;
	}

	public FullComplianceEval(Integer fullComplianceEvalId, Integer assignedStaff, String facilityId,
			Timestamp scheduledTimestamp, Integer evaluator, boolean usEpaCommitted, Timestamp dateEvaluated,
			String memo, Timestamp dateSentToEPA, String inspectionType, boolean legacyInspection) {
		super();
		this.id = fullComplianceEvalId;
		this.facilityId = facilityId;
		this.assignedStaff = assignedStaff;
		this.scheduledTimestamp = scheduledTimestamp;
		this.evaluator = evaluator;
		this.dateEvaluated = dateEvaluated;
		this.usEpaCommitted = usEpaCommitted;
		this.memo = memo;
		this.dateSentToEPA = dateSentToEPA;
		this.inspectionType = inspectionType;
		this.legacyInspection = legacyInspection;
	}

	public final ValidationMessage[] validate() {
		validationMessages.clear();
		boolean schedEmpty = false;
		boolean compEmpty = false;
		boolean schedFull = false;
		boolean compFull = false;
		if (assignedStaff != null && scheduledTimestamp != null)
			schedFull = true;
		if (evaluator != null && dateEvaluated != null && inspectionType != null)
			compFull = true;
		if (assignedStaff == null && scheduledTimestamp == null)
			schedEmpty = true;
		if (evaluator == null && dateEvaluated == null && (inspectionType == null || inspectionType.equals("")))
			compEmpty = true;

		if (isLegacyInspection() || isPre10Legacy()) {
			if ((!schedFull && !compFull)) {
				validationMessages.put("errChoice",
						new ValidationMessage("errChoice",
								"Either both Staff Assigned & Quarter Scheduled must be set OR Inspector, Date Inspection Completed, Inspection Type fields must be set",
								ValidationMessage.Severity.ERROR, null));
			} else {
				if (!compEmpty && !compFull) {
					requiredField(evaluator, "evaluator", "Inspector");
					requiredField(dateEvaluated, "dateEvaluated", "Date Inspection Completed");
					if (inspectionType == null || inspectionType.equals("")) {
						validationMessages.put("inspectionType",
								new ValidationMessage("inspectionType", "Inspection Type is not specified.",
										ValidationMessage.Severity.ERROR, "inspectionType"));
					}
				}
			}
		} else {
			if (assignedStaff == null)
				validationMessages.put("staff",
						new ValidationMessage("staff",
								"Staff Assigned must be set",
								ValidationMessage.Severity.ERROR, null));
		}
		
		if (dateEvaluated != null) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			Timestamp today = new Timestamp(cal.getTimeInMillis());
			if (today.before(dateEvaluated)) {
				validationMessages.put("dateEvaluated", new ValidationMessage("dateEvaluated",
						"Date Inspection Completed cannot be in the future", ValidationMessage.Severity.ERROR, null));
			}
			cal.set(Calendar.YEAR, 1900);
			cal.set(Calendar.DAY_OF_YEAR, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Timestamp earlyYear = new Timestamp(cal.getTimeInMillis());
			if (earlyYear.after(dateEvaluated)) {
				validationMessages.put("dateEvaluated", new ValidationMessage("dateEvaluated",
						"Date Inspection Completed cannot be before 1900", ValidationMessage.Severity.ERROR, null));
			}
		}

		if (dateReported != null) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			Timestamp today = new Timestamp(cal.getTimeInMillis());
			if (today.before(dateReported)) {
				validationMessages.put("dateReported", new ValidationMessage("dateReported",
						"Date Report Completed cannot be in the future", ValidationMessage.Severity.ERROR, null));
			}
			cal.set(Calendar.YEAR, 1900);
			cal.set(Calendar.DAY_OF_YEAR, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Timestamp earlyYear = new Timestamp(cal.getTimeInMillis());
			if (earlyYear.after(dateReported)) {
				validationMessages.put("dateReported", new ValidationMessage("dateReported",
						"Date Report Completed cannot be before 1900", ValidationMessage.Severity.ERROR, null));
			}
			if (dateEvaluated != null && dateReported.before(dateEvaluated)) {
				validationMessages.put("invalidDateReported",
						new ValidationMessage("invalidDateReported",
								"Date Report Completed cannot be before Date Inspection Completed",
								ValidationMessage.Severity.ERROR, null));
			}
		}

		HashSet<Integer> staffSet = new HashSet<Integer>();
		for (Evaluator e : getAdditionalAqdStaffPresent()) {
			if (e.getEvaluator() == null)
				continue;
			if (!staffSet.add(e.getEvaluator())) {
				validationMessages.put("additionalAqdStaffPresent", new ValidationMessage("staffTable",
						"There are duplicate Staff.", ValidationMessage.Severity.ERROR, null));
				break;
			}
		}
		if (dayTwoAmbientConditions != null) {
			ValidationMessage[] dayTwoAmbientConditionsValMsg = dayTwoAmbientConditions.validate();
			for (ValidationMessage valMsg : dayTwoAmbientConditionsValMsg) {
				validationMessages.put("dayTwoAmbientConditions" + valMsg.getProperty(), valMsg);
			}
		}
		if (dayOneAmbientConditions != null) {
			ValidationMessage[] dayOneAmbientConditionsValMsg = dayOneAmbientConditions.validate();
			for (ValidationMessage valMsg : dayOneAmbientConditionsValMsg) {
				validationMessages.put("dayOneAmbientConditions" + valMsg.getProperty(), valMsg);
			}
		}
		if (dayTwoAmbientConditions != null && dayOneAmbientConditions != null) {
			if (dayOneAmbientConditions.getInspectionDate() == null
					&& dayTwoAmbientConditions.getInspectionDate() != null) {
				validationMessages.put("ambientConditions",
						new ValidationMessage("ambientConditions",
								"Day Two Ambient Conditions Inspection date filled in, but Day One Ambient Conditions Inspection date is empty",
								ValidationMessage.Severity.ERROR, null));
			}
			if (dayOneAmbientConditions.getInspectionDate() != null
					&& dayTwoAmbientConditions.getInspectionDate() != null && dayTwoAmbientConditions
							.getInspectionDate().compareTo(dayOneAmbientConditions.getInspectionDate()) < 1) {
				validationMessages.put("ambientConditions",
						new ValidationMessage("ambientConditions",
								"Day Two Ambient Conditions Inspection date must be after Day One Ambient Conditions Inspection date",
								ValidationMessage.Severity.ERROR, null));
			}
		}

		return new ArrayList<ValidationMessage>(validationMessages.values()).toArray(new ValidationMessage[0]);
	}

	public String getInspectionReportDocumentUrl() {
		String url = null;
		for (FceAttachment attch : getAttachments()) {
			if ("IR".equals(attch.getAttachmentType())) {
				url = attch.getDocURL();
			}
		}
		return url;
	}

	public String getSchedAfsInfo() {
		if (schedAfsId == null)
			return null;
		String dateStr = "";
		if (schedAfsDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(schedAfsDate.getTime());
			dateStr = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/"
					+ cal.get(Calendar.YEAR);
		}
		return dateStr;
	}

	public String getEvalAfsInfo() {
		if (evalAfsId == null)
			return null;
		String dateStr = "";
		if (evalAfsDate != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(evalAfsDate.getTime());
			dateStr = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/"
					+ cal.get(Calendar.YEAR);
		}
		String rtn = evalAfsId + " " + dateStr;
		return rtn;
	}

	public Timestamp getScheduledTimestamp() {
		return scheduledTimestamp;
	}

	public String getScheduled() {
		return getScheduled(scheduledTimestamp);
	}

	/** Populate this instance from a database ResultSet. */
	public final void populate(java.sql.ResultSet rs) throws SQLException {

		try {
			setId(AbstractDAO.getInteger(rs, "fce_id"));
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setVersionId(AbstractDAO.getInteger(rs, "version_id"));
			setFacilityHistId(AbstractDAO.getInteger(rs, "facility_hist_id"));
			setFacilityId(rs.getString("facility_id"));
			setPermitClassCd(rs.getString("permit_classification_cd"));
			setFacilityTypeCd(rs.getString("facility_type_cd"));
			setScscId(rs.getString("scsc_id"));
			setEvaluator(AbstractDAO.getInteger(rs, "evaluator_id"));
			setAssignedStaff(AbstractDAO.getInteger(rs, "staff_assigned_id"));
			setDateEvaluated(rs.getTimestamp("evaluated_dt"));
			setDateReported(rs.getTimestamp("reported_dt"));
			setScheduledTimestamp(CetaBaseDB.quarterStart(rs.getTimestamp("scheduled_dt")));
			setEvalAfsDate(rs.getTimestamp("afs_dt"));
			setEvalAfsId(rs.getString("afs_id"));
			setSchedAfsDate(rs.getTimestamp("afsSched_dt"));
			setSchedAfsId(rs.getString("afsSched_id"));
			setMemo(rs.getString("memo"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			setFacilityNm(rs.getString("facility_nm"));
			setUsEpaCommitted(AbstractDAO.translateIndicatorToBoolean(rs.getString("us_epa_committed")));
			String inactive = rs.getString("eval_full_name_inact");
			setFullName(WordUtils.capitalize(rs.getString("full_name")));
			if (getFullName() != null) {
				setFullName(getFullName().trim());
			}
			setCreatedDt(rs.getTimestamp("created_dt"));
			if (",".equals(fullName))
				setFullName("");
			else {
				if ("N".equals(inactive)) {
					setFullName(getFullName() + DefSelectItems.INACTIVE);
				}
			}
			inactive = rs.getString("eval_full_name_inact");
			setEvalFullName(WordUtils.capitalize(rs.getString("eval_full_name")));
			if (getEvalFullName() != null) {
				setEvalFullName(getEvalFullName().trim());
			}
			if (",".equals(evalFullName))
				setEvalFullName("");
			else {
				if ("N".equals(inactive)) {
					setEvalFullName(getEvalFullName() + DefSelectItems.INACTIVE);
				}
			}
			setOperatingStatusCd(rs.getString("operating_status_cd"));
			setLastShutdownDate(rs.getTimestamp("last_shutdown_date"));
			setPortable(AbstractDAO.translateIndicatorToBoolean(rs.getString("portable")));
			setInspId(rs.getString("insp_id"));
			setCmpId(rs.getString("cmp_id"));
			setCompanyName(rs.getString("company_nm"));
			setDateSentToEPA(rs.getTimestamp("sent_to_epa_dt"));
			setInspectionType(rs.getString("inspection_type_id"));
			setLegacyInspection(AbstractDAO.translateIndicatorToBoolean(rs.getString("is_legacy_inspection")));
			setFacilityAfsNumber(rs.getString("facility_afs_num"));
			setLastFceId(AbstractDAO.getInteger(rs, "last_fce_id"));
			if (ResultSetHelper.hasDataColumn(rs, "last_insp_id")) {
				setLastInspId(rs.getString("last_insp_id"));
			}
			if (ResultSetHelper.hasDataColumn(rs, "last_insp_date")) {
				setLastInspDate(rs.getTimestamp("last_insp_date"));
			}
			setFacilityStaffPresent(rs.getString("facility_staff_present"));
			setInspectionReportStateCd(rs.getString("inspection_rpt_state_cd"));
			setPrivatePropertyAccess(AbstractDAO.translateIndicatorToBoolean(rs.getString("private_property_access")));
			setAnnouncedInspection(AbstractDAO.translateIndicatorToBoolean(rs.getString("announced_inspection")));
			setInspectionConcerns(rs.getString("concerns"));
			setFileReview(rs.getString("file_review_records_request"));
			setRegulatoryDiscussion(rs.getString("regulatory_discussion"));
			setPhysicalInspectionOfPlant(rs.getString("physical_inspection"));
			setAmbientMonitoring(rs.getString("ambient_monitoring"));
			setOtherInformation(rs.getString("other_information"));
			setPre10Legacy(AbstractDAO.translateIndicatorToBoolean(rs.getString("pre10_legacy")));
			setReferenceReviewStartDate(rs.getTimestamp("reference_review_start_date"));
			setComplianceStatusCd(rs.getString("inspection_compliance_status_cd"));
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public Timestamp getDateEvaluated() {
		return dateEvaluated;
	}

	public void setDateEvaluated(Timestamp dateEvaluated) {
		this.dateEvaluated = dateEvaluated;
	}

	public Timestamp getDateReported() {
		return dateReported;
	}

	public void setDateReported(Timestamp dateReported) {
		this.dateReported = dateReported;
	}

	public String getMemo() {
		return memo;
	}

	public int getMemoRows() {
		int rows = 1;
		if (memo != null && memo.length() > 160) {
			rows = memo.length() / 160;
		}
		rows++; // make sure at least two and long enough
		return rows;
	}

	public String getShortMemo() {
		String rtn = memo;
		if (memo != null && memo.length() > 80) {
			rtn = memo.substring(0, 80) + "...";
		}
		return rtn;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Timestamp getEvalAfsDate() {
		return evalAfsDate;
	}

	public void setEvalAfsDate(Timestamp evalAfsDate) {
		this.evalAfsDate = evalAfsDate;
	}

	public Timestamp getSchedAfsDate() {
		return schedAfsDate;
	}

	public void setSchedAfsDate(Timestamp schedAfsDate) {
		this.schedAfsDate = schedAfsDate;
	}

	public Integer getAssignedStaff() {
		return assignedStaff;
	}

	public void setAssignedStaff(Integer assignedStaff) {
		this.assignedStaff = assignedStaff;
	}

	public Integer getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(Integer evaluator) {
		this.evaluator = evaluator;
	}

	public void setScheduledTimestamp(Timestamp scheduledTimestamp) {
		this.scheduledTimestamp = scheduledTimestamp;
		if (scheduledTimestamp == null) {
			usEpaCommitted = false;
		}
	}

	public String getFacilityNm() {
		return facilityNm;
	}

	public void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}

	public SiteVisit[] getAssocSiteVisits() {
		if (assocSiteVisits == null)
			assocSiteVisits = new SiteVisit[0];
		return assocSiteVisits;
	}

	public void setAssocSiteVisits(SiteVisit[] assocSiteVisits) {
		this.assocSiteVisits = assocSiteVisits;
	}

	public SiteVisit[] getSiteVisits() {
		if (siteVisits == null)
			siteVisits = new SiteVisit[0];
		return siteVisits;
	}

	public int getSiteVisitsSize() {
		if (siteVisits == null)
			return 0;
		else
			return siteVisits.length;
	}

	public void setSiteVisits(SiteVisit[] siteVisits) {
		this.siteVisits = siteVisits;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public StackTest[] getAssocStackTests() {
		if (assocStackTests == null)
			assocStackTests = new StackTest[0];
		return assocStackTests;
	}

	public void setAssocStackTests(StackTest[] assocStackTests) {
		this.assocStackTests = assocStackTests;
	}

	public StackTest[] getStackTests() {
		if (stackTests == null)
			stackTests = new StackTest[0];
		return stackTests;
	}

	public int getStackTestsSize() {
		if (stackTests == null)
			return 0;
		else
			return stackTests.length;
	}

	public void setStackTests(StackTest[] stackTests) {
		this.stackTests = stackTests;
	}

	public boolean isPre10Legacy() {
		return pre10Legacy;
	}

	public void setPre10Legacy(boolean pre10Legacy) {
		this.pre10Legacy = pre10Legacy;
	}

	public String getVersion10InstallDate() {
		return SystemPropertyDef.getSystemPropertyValue("VERSION_10_INSTALL_DATE", "06/01/2018");
	}
	
	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public List<FceAttachment> getAttachments() {
		if (attachments == null) {
			attachments = new ArrayList<FceAttachment>(0);
		}
		return attachments;
	}

	public void setAttachments(List<FceAttachment> attachments) {
		this.attachments = attachments;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEvalFullName() {
		return evalFullName;
	}

	public void setEvalFullName(String evalFullName) {
		this.evalFullName = evalFullName;
	}

	public Integer getFacilityHistId() {
		return facilityHistId;
	}

	public void setFacilityHistId(Integer facilityHistId) {
		this.facilityHistId = facilityHistId;
	}

	public FacilityHistory getFacilityHistory() {
		return facilityHistory;
	}

	public void setFacilityHistory(FacilityHistory facilityHistory) {
		this.facilityHistory = facilityHistory;
	}

	public String getOperatingStatusCd() {
		return operatingStatusCd;
	}

	public void setOperatingStatusCd(String operatingStatusCd) {
		this.operatingStatusCd = operatingStatusCd;
	}

	public Timestamp getLastShutdownDate() {
		return lastShutdownDate;
	}

	public void setLastShutdownDate(Timestamp lastShutdownDate) {
		this.lastShutdownDate = lastShutdownDate;
	}

	public String getEvalAfsId() {
		return evalAfsId;
	}

	public void setEvalAfsId(String evalAfsId) {
		this.evalAfsId = evalAfsId;
	}

	public String getSchedAfsId() {
		return schedAfsId;
	}

	public void setSchedAfsId(String schedAfsId) {
		this.schedAfsId = schedAfsId;
	}

	public Integer getVersionId() {
		return versionId;
	}

	public void setVersionId(Integer versionId) {
		this.versionId = versionId;
	}

	public boolean isUsEpaCommitted() {
		return usEpaCommitted;
	}

	public void setUsEpaCommitted(boolean usEpaCommitted) {
		this.usEpaCommitted = usEpaCommitted;
	}

	public Integer getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}

	public Timestamp getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Timestamp createdDt) {
		this.createdDt = createdDt;
	}

	public String getScscId() {
		return scscId;
	}

	public void setScscId(String scscId) {
		this.scscId = scscId;
	}

	public boolean isPortable() {
		return portable;
	}

	public void setPortable(boolean portable) {
		this.portable = portable;
	}

	public String getInspId() {
		String tempInspId = null;
		if (!Utility.isNullOrZero(id)) {
			String format = "INSP" + "%06d";
			try {
				tempInspId = String.format(format, id);
			} catch (NumberFormatException nfe) {
			}
		}
		return tempInspId;
	}

	public void setInspId(String inspId) {
		this.inspId = inspId;
	}

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Note[] getInspectionNotes() {
		return inspectionNotes;
	}

	public void setInspectionNotes(Note[] inspectionNotes) {
		this.inspectionNotes = inspectionNotes;
		setDirty(true);
	}

	public Timestamp getDateSentToEPA() {
		return dateSentToEPA;
	}

	public void setDateSentToEPA(Timestamp dateSentToEPA) {
		this.dateSentToEPA = dateSentToEPA;
	}

	public String getInspectionType() {
		return inspectionType;
	}

	public void setInspectionType(String inspectionType) {
		this.inspectionType = inspectionType;
	}

	public boolean isLegacyInspection() {
		return legacyInspection;
	}

	public void setLegacyInspection(boolean legacyInspection) {
		this.legacyInspection = legacyInspection;
	}

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

	public String getFacilityAfsNumber() {
		return facilityAfsNumber;
	}

	public void setFacilityAfsNumber(String facilityAfsNumber) {
		this.facilityAfsNumber = facilityAfsNumber;
	}

	public List<ComplianceStatusEvent> getAssocComplianceStatusEvents() {
		if (assocComplianceStatusEvents == null)
			return new ArrayList<ComplianceStatusEvent>();
		return assocComplianceStatusEvents;
	}

	public void setAssocComplianceStatusEvents(List<ComplianceStatusEvent> assocComplianceStatusEvents) {
		this.assocComplianceStatusEvents = assocComplianceStatusEvents;
	}

	public String getLastInspId() {
		return this.lastInspId;
	}

	public void setLastInspId(String lastInspId) {
		this.lastInspId = lastInspId;
	}

	public Timestamp getLastInspDate() {
		return lastInspDate;
	}

	public void setLastInspDate(Timestamp lastInspDate) {
		this.lastInspDate = lastInspDate;
	}

	public boolean isPrivatePropertyAccess() {
		return privatePropertyAccess;
	}

	public void setPrivatePropertyAccess(boolean privatePropertyAccess) {
		this.privatePropertyAccess = privatePropertyAccess;
	}

	public List<Evaluator> getAdditionalAqdStaffPresent() {
		if (additionalAqdStaffPresent == null) {
			return new ArrayList<Evaluator>();
		}
		return additionalAqdStaffPresent;
	}

	public void setAdditionalAqdStaffPresent(List<Evaluator> additionalAqdStaffPresent) {
		this.additionalAqdStaffPresent = additionalAqdStaffPresent;
		if (additionalAqdStaffPresent == null) {
			this.additionalAqdStaffPresent = new ArrayList<Evaluator>();
		}
	}

	public boolean isAdditionalAqdStaffPresentExists() {
		if (getAdditionalAqdStaffPresent() == null || getAdditionalAqdStaffPresent().isEmpty())
			return false;
		return true;
	}

	public String getFacilityStaffPresent() {
		return facilityStaffPresent;
	}

	public void setFacilityStaffPresent(String facilityStaffPresent) {
		this.facilityStaffPresent = facilityStaffPresent;
	}

	public String getInspectionReportStateCd() {
		return inspectionReportStateCd;
	}

	public void setInspectionReportStateCd(String inspectionReportStateCd) {
		this.inspectionReportStateCd = inspectionReportStateCd;
	}

	public boolean isAnnouncedInspection() {
		return announcedInspection;
	}

	public void setAnnouncedInspection(boolean announcedInspection) {
		this.announcedInspection = announcedInspection;
	}

	public AmbientConditions getDayOneAmbientConditions() {
		return dayOneAmbientConditions;
	}

	public void setDayOneAmbientConditions(AmbientConditions dayOneAmbientConditions) {
		this.dayOneAmbientConditions = dayOneAmbientConditions;
	}

	public AmbientConditions getDayTwoAmbientConditions() {
		return dayTwoAmbientConditions;
	}

	public void setDayTwoAmbientConditions(AmbientConditions dayTwoAmbientConditions) {
		this.dayTwoAmbientConditions = dayTwoAmbientConditions;
	}

	public void addAdditionalAqdStaffPresent() {
		if (additionalAqdStaffPresent == null) {
			additionalAqdStaffPresent = new ArrayList<Evaluator>();
		}
		additionalAqdStaffPresent.add(new Evaluator());
	}

	public boolean isReportStateInitial() {
		if (inspectionReportStateCd.equals(FCEInspectionReportStateDef.INITIAL))
			return true;
		return false;
	}

	public boolean isReportStatePrepare() {
		if (inspectionReportStateCd.equals(FCEInspectionReportStateDef.PREPARE))
			return true;
		return false;
	}

	public boolean isReportStateComplete() {
		if (inspectionReportStateCd.equals(FCEInspectionReportStateDef.COMPLETE))
			return true;
		return false;
	}

	public boolean isReportStateFinal() {
		if (inspectionReportStateCd.equals(FCEInspectionReportStateDef.FINAL))
			return true;
		return false;
	}
	
	public boolean isReportStateDeadEnded() {
		if (inspectionReportStateCd.equals(FCEInspectionReportStateDef.DEAD_ENDED))
			return true;
		return false;
	}

	public String getInspectionConcerns() {
		return inspectionConcerns;
	}

	public void setInspectionConcerns(String inspectionConcerns) {
		this.inspectionConcerns = inspectionConcerns;
	}

	public String getFileReview() {
		return fileReview;
	}

	public void setFileReview(String fileReview) {
		this.fileReview = fileReview;
	}

	public String getRegulatoryDiscussion() {
		return regulatoryDiscussion;
	}

	public void setRegulatoryDiscussion(String regulatoryDiscussion) {
		this.regulatoryDiscussion = regulatoryDiscussion;
	}

	public String getPhysicalInspectionOfPlant() {
		return physicalInspectionOfPlant;
	}

	public void setPhysicalInspectionOfPlant(String physicalInspectionOfPlant) {
		this.physicalInspectionOfPlant = physicalInspectionOfPlant;
	}

	public String getAmbientMonitoring() {
		return ambientMonitoring;
	}

	public void setAmbientMonitoring(String ambientMonitoring) {
		this.ambientMonitoring = ambientMonitoring;
	}

	public String getOtherInformation() {
		return otherInformation;
	}

	public void setOtherInformation(String otherInformation) {
		this.otherInformation = otherInformation;
	}

	public List<Integer> getAssociatedPermits() {
		if (associatedPermits == null) {
			associatedPermits = new ArrayList<Integer>();
		}
		return associatedPermits;
	}

	public void setAssociatedPermits(List<Integer> associatedPermits) {
		if (associatedPermits == null) {
			associatedPermits = new ArrayList<Integer>();
		}
		this.associatedPermits = associatedPermits;
	}

	public FcePreData getFcePreData() {
		return fcePreData;
	}

	public void setFcePreData(FcePreData fcePreData) {
		this.fcePreData = fcePreData;
	}

	public List<FcePermitCondition> getFcePermitConditions() {
		return fcePermitConditions;
	}

	public void setFcePermitConditions(List<FcePermitCondition> fcePermitConditions) {
		this.fcePermitConditions = fcePermitConditions;
	}

	
	public Timestamp getReferenceReviewStartDate() {
		return referenceReviewStartDate;
	}

	public void setReferenceReviewStartDate(Timestamp referenceReviewStartDate) {
		this.referenceReviewStartDate = referenceReviewStartDate;
	}
	
	public void setPreDataDefaultDateRange(String snapshot){
        if (FCEPreSnapshotTypeDef.PA.equals(snapshot)){
        	fcePreData.getDateRangePA().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangePA().setEndDt(null);
        } else if (FCEPreSnapshotTypeDef.PT.equals(snapshot)){
        	fcePreData.getDateRangePT().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangePT().setEndDt(null);
        } else if (FCEPreSnapshotTypeDef.ST.equals(snapshot)){
        	fcePreData.getDateRangeST().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangeST().setEndDt(null);
        } else if (FCEPreSnapshotTypeDef.CR.equals(snapshot)){
        	fcePreData.getDateRangeCR().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangeCR().setEndDt(null);
        } else if (FCEPreSnapshotTypeDef.DC.equals(snapshot)){
        	fcePreData.getDateRangeDC().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangeDC().setEndDt(null);
        } else if (FCEPreSnapshotTypeDef.EI.equals(snapshot)){
        	fcePreData.getDateRangeEI().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangeEI().setEndDt(null);
        } else if (FCEPreSnapshotTypeDef.LI.equals(snapshot)){
        	fcePreData.getDateRangeContinuousMonitors().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangeContinuousMonitors().setEndDt(null);
        } else if (FCEPreSnapshotTypeDef.MO.equals(snapshot)){
        	fcePreData.getDateRangeAmbientMonitors().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangeAmbientMonitors().setEndDt(null);
        } else if (FCEPreSnapshotTypeDef.SV.equals(snapshot)){
        	fcePreData.getDateRangeSiteVisits().setStartDt(referenceReviewStartDate);
        	fcePreData.getDateRangeSiteVisits().setEndDt(null);
        }
	}


	public String getComplianceStatusCd() {
		return complianceStatusCd;
	}

	public void setComplianceStatusCd(String complianceStatusCd) {
		this.complianceStatusCd = complianceStatusCd;
		if(complianceStatusCd != null) {
			setComplianceStatusLongDesc(OverallInspectionComplianceStatusDef.getLongDscData().getItems().getItemDesc(complianceStatusCd));
		} else {
			setComplianceStatusLongDesc(null);
		}
	}

	public String getComplianceStatusLongDesc() {
		return complianceStatusLongDesc;
	}

	public void setComplianceStatusLongDesc(String complianceStatusLongDesc) {
		this.complianceStatusLongDesc = complianceStatusLongDesc;
	}

	public Integer getLastFceId() {
		return lastFceId;
	}

	public void setLastFceId(Integer lastFceId) {
		this.lastFceId = lastFceId;
	}

	public List<String> getInspectionsReferencedIn() {
		return inspectionsReferencedIn;
	}

	public void setInspectionsReferencedIn(List<String> inspectionsReferencedIn) {
		this.inspectionsReferencedIn = inspectionsReferencedIn;
	}

}
