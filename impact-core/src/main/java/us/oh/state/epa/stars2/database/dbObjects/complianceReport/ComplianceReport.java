package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDefBase;
import us.oh.state.epa.stars2.database.dbObjects.permit.ComplianceStatusEvent;
import us.oh.state.epa.stars2.def.ComplianceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.ComplianceReportAcceptedDef;
import us.oh.state.epa.stars2.def.ComplianceReportAllSubtypesDef;
import us.oh.state.epa.stars2.def.ComplianceReportCategoriesTypeDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.ReportComplianceStatusDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.facility.FacilityValidation;

/**
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2006 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @author slopez
 * 
 */
@SuppressWarnings("serial")
public class ComplianceReport extends BaseDB {
	
	// common fields for all reports
	private Integer reportId;
	private String reportCRPTId;
	private String facilityId;
	private transient String scscId;
	private String facilityNm;
	private String facilityTypeCd;
	private String permitClassCd;
	private int userId;
	private transient String submitLabel;
	private transient String submitValue;
	private transient Timestamp submittedDate;
	private long submittedDateLong;
	private transient Timestamp receivedDate;
	private long receivedDateLong;
	private String comments;
	private String reportType;
	// DENNIS private boolean deviations;
	private ComplianceDeviation[] deviationReports;
	private String reportStatus;
	private boolean correctedReport = false;
	private String reportingPeriodDesc;
	protected InfrastructureService infraBO;
	private boolean reportBeingSubmitted = false;
	private String reportTypeDesc;
	// private boolean staging=false;

	// TV fields
	private String tvccReportingYear;
	private String tvccDeviationDeclaration;
	private String tvccAfsId;
	private Timestamp tvccAfsSentDate;

	// PER fields
	private transient Timestamp perStartDate;
	private long perStartDateLong;
	private transient Timestamp perEndDate;
	private long perEndDateLong;
	private String perDueDateCd;
	private transient Timestamp perDueDate;
	private long perDueDateLong;
	private CompliancePerDetail[] perDetails;
	private int perEUId;
	private int perPTIOPermitNumber;
	private transient Timestamp perPTIOIssueDate;
	private long perPTIOIssueDateLong;
	private String perOperate;
	private String perShutdown;
	private transient Timestamp perShutdownDate;
	private long perShutdownDateLong;
	private String perPhysicalChange;
	private String perMRRDeviations;
	private String perComments;
	private String perAirToxicsChange;

	// OTHER fields
	private String otherCategoryCd;

	// Maintance Fields
	private Timestamp finalActionDate;

	// Staff fields
	private int dapcReviewer;
	private transient Timestamp dapcDateReviewed;
	private long dapcDateReviewedLong;
	private String dapcDeviationsReported;
	private String complianceStatusCd;
	private String dapcReviewResultsTVCC;
	private String dapcReviewComments;
	private String dapcAccepted;
	private ComplianceReportAttachment[] attachments;
	private Note[] notes;
	protected boolean validated = false;

	// misc fields
	private transient boolean createFileDir = true;

	// user name is only set by Portal - this is not stored in the database
	private String userName;

	private String permitNumber;

	private boolean legacyFlag;
	private static final String LEGACY_USER = "dlegacy";
	
	private Integer reportYear;

	private Integer reportQuarter;

	protected final static String COMPLIANCE_REPORT = "complianceDetail";
	
	private Integer fpId; // used by quarterly cem/com monitoring and annual RATA reports only
	
	public FacilityList[] getTargetFacilities() {
		return targetFacilities;
	}
	public void setTargetFacilities(FacilityList[] targetFacilities) {
		this.targetFacilities = targetFacilities;
	}

	private ComplianceReportMonitor[] monitors;
	
	private ComplianceReportLimit[] limits;
	
	private transient List<ComplianceReportMonitor> compReportMonitorList;
	private transient HashMap<ComplianceReportMonitor, List<ComplianceReportLimit>> compReportMonitorMap = 
			new HashMap<ComplianceReportMonitor, List<ComplianceReportLimit>>();

	
	private Boolean template;

    private String[] targetFacilityIds = new String[0];
    
    private FacilityList[] targetFacilities = new FacilityList[0];

    private transient List<ComplianceStatusEvent> assocComplianceStatusEvents;
    private List<String> inspectionsReferencedIn;
    
    public String[] getTargetFacilityIds() {
		return targetFacilityIds;
	}
	public void setTargetFacilityIds(String[] targetFacilityIds) {
		this.targetFacilityIds = targetFacilityIds;
	}
	
	public ComplianceReport() {
		super();
		compReportMonitorList = new ArrayList<ComplianceReportMonitor>();
	}

	public ComplianceReport(ComplianceReport old) {
		super(old);
		setReportId(old.getReportId());
		setReportCRPTId(old.getReportCRPTId());
		setPerDueDateCd(old.getPerDueDateCd());
		setOtherCategoryCd(old.getOtherCategoryCd());
		setReportType(old.getReportType());
		setUserId(old.getUserId());
		setFacilityId(old.getFacilityId());
		setReceivedDate(old.getReceivedDate());
		setSubmittedDate(old.getSubmittedDate());
		setComments(old.getComments());
		setReportStatus(old.getReportStatus());
		setDapcReviewer(old.getDapcReviewer());
		setDapcDateReviewed(old.getDapcDateReviewed());
		setDapcDeviationsReported(old.getDapcDeviationsReported());
		setDapcReviewComments(old.getDapcReviewComments());
		setDapcAccepted(old.getDapcAccepted());
		setTvccReportingYear(old.getTvccReportingYear());
		setPerStartDate(old.getPerStartDate());
		setPerEndDate(old.getPerEndDate());
		setPerDueDate(old.getPerDueDate());
		setTvccAfsId(old.getTvccAfsId());
		setTvccAfsSentDate(old.getTvccAfsSentDate());
		setComplianceStatusCd(old.getComplianceStatusCd());

		setReportTypeDesc(old.getReportTypeDesc());
		// logger.error("INFO: value of receivedDate is " +
		// old.getReceivedDate() + " for compliance report " +
		// old.getReportId());
		setPermitNumber(old.getPermitNumber());
		setValidated(old.validated);
		setLegacyFlag(old.legacyFlag);
		
		setReportYear(old.reportYear);
		setReportQuarter(old.reportQuarter);
		
		setFpId(old.getFpId());

	}

	public boolean isBulkEnabled() {
		return ComplianceReportCategoriesTypeDef.isBulkEnabled(
				getOtherCategoryCd());
	}

	public Boolean getTemplate() {
		return template;
	}



	public void setTemplate(Boolean template) {
		this.template = template;
	}
	

	public final ComplianceReportAttachment[] getAttachments() {
		return attachments;
	}

	public final boolean hasNonXAttachments() {
		boolean rtn = false;
		if (attachments != null) {
			for (ComplianceReportAttachment a : attachments) {
				if (DefData.isDapcAttachmentOnly(a.getAttachmentType()))
					continue;
				rtn = true;
				break;
			}
		}
		return rtn;
	}

	public final boolean hasAttachments() {
		boolean rtn = false;
		if (attachments != null && attachments.length > 0) {
			rtn = true;
		}
		return rtn;
	}

	public final void setAttachments(ComplianceReportAttachment[] attachments) {
		this.attachments = attachments;
	}

	public final String getComments() {
		return comments;
	}

	public final void setComments(String comments) {
		this.comments = comments;
	}

	public final String getPerDueDateCd() {
		return perDueDateCd;
	}

	public final void setPerDueDateCd(String perDueDateCd) {
		this.perDueDateCd = perDueDateCd;
		if (this.perDueDate != null) {
			this.perDueDateLong = this.perDueDate.getTime();
		} else {
			this.perDueDateLong = 0;
		}
	}

	public final String getFacilityId() {
		return facilityId;
	}

	public final void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final CompliancePerDetail[] getPerDetails() {
		return perDetails;
	}

	public final void setPerDetails(CompliancePerDetail[] perDetails) {
		this.perDetails = perDetails;
	}

	public final Timestamp getSubmittedDate() {
		return submittedDate;
	}

	public final void setSubmittedDate(Timestamp submittedDate) {
		this.submittedDate = submittedDate;
		if (this.submittedDate != null) {
			this.submittedDateLong = this.submittedDate.getTime();
		} else {
			this.submittedDateLong = 0;
		}
	}

	public final Integer getReportId() {
		return reportId;
	}

	public final void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public final String getReportCRPTId() {
		return reportCRPTId;
	}

	public final void setReportCRPTId(String reportCRPTId) {
		this.reportCRPTId = reportCRPTId;
	}

	public final String getReportType() {
		return reportType;
	}

	public final void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public final int getUserId() {
		return userId;
	}

	public final void setUserId(int userId) {
		this.userId = userId;
	}

	public Timestamp getDapcDateReviewed() {
		return dapcDateReviewed;
	}

	public void setDapcDateReviewed(Timestamp dapcDateReviewed) {
		checkDirty("rd", this.getReportCRPTId(), this.dapcDateReviewed,
				dapcDateReviewed);
		this.dapcDateReviewed = dapcDateReviewed;
		if (this.dapcDateReviewed != null) {
			this.dapcDateReviewedLong = this.dapcDateReviewed.getTime();
		}
	}

	public String getDapcDeviationsReported() {
		return dapcDeviationsReported;
	}

	public void setDapcDeviationsReported(String dapcDeviationsReported) {
		this.dapcDeviationsReported = dapcDeviationsReported;
	}

	public int getDapcReviewer() {
		return dapcReviewer;
	}

	public void setDapcReviewer(int dapcReviewer) {
		this.dapcReviewer = dapcReviewer;
	}

	public String getDapcReviewResultsTVCC() {
		return dapcReviewResultsTVCC;
	}

	public void setDapcReviewResultsTVCC(String dapcReviewResultsTVCC) {
		this.dapcReviewResultsTVCC = dapcReviewResultsTVCC;
	}

	public Timestamp getPerEndDate() {
		return perEndDate;
	}

	public void setPerEndDate(Timestamp perEndDate) {
		this.perEndDate = perEndDate;
	}

	public Timestamp getPerStartDate() {
		return perStartDate;
	}

	public void setPerStartDate(Timestamp perStartDate) {
		this.perStartDate = perStartDate;
	}

	public String getTvccDeviationDeclaration() {
		return tvccDeviationDeclaration;
	}

	public void setTvccDeviationDeclaration(String tvccDeviationDeclaration) {
		this.tvccDeviationDeclaration = tvccDeviationDeclaration;
	}

	public String getTvccReportingYear() {
		return tvccReportingYear;
	}

	public void setTvccReportingYear(String tvccReportingYear) {
		this.tvccReportingYear = tvccReportingYear;
	}

	public String getDapcAccepted() {
		return dapcAccepted;
	}

	public void setDapcAccepted(String dapcAccepted) {
		String uniqueId = null;
		if (this.getReportId() != null && this.getReportCRPTId() != null) {
			uniqueId = this.getReportCRPTId();
		}
		checkDirty(
				"rra",
				uniqueId,
				ComplianceReportAcceptedDef.getData().getItems()
						.getItemDesc(this.dapcAccepted),
				ComplianceReportAcceptedDef.getData().getItems()
						.getItemDesc(dapcAccepted));
		this.dapcAccepted = dapcAccepted;
	}

	public String getDapcReviewComments() {
		return dapcReviewComments;
	}

	public void setDapcReviewComments(String dapcReviewComments) {
		this.dapcReviewComments = dapcReviewComments;
	}

	// DENNIS
	// public boolean isDeviations() {
	// return deviations;
	// }
	//
	// public void setDeviations(boolean deviations) {
	// this.deviations = deviations;
	// }

	public String getPerAirToxicsChange() {
		return perAirToxicsChange;
	}

	public void setPerAirToxicsChange(String perAirToxicsChange) {
		this.perAirToxicsChange = perAirToxicsChange;
	}

	public String getPerComments() {
		return perComments;
	}

	public void setPerComments(String perComments) {
		this.perComments = perComments;
	}

	public int getPerEUId() {
		return perEUId;
	}

	public void setPerEUId(int perEUId) {
		this.perEUId = perEUId;
	}

	public String getPerMRRDeviations() {
		return perMRRDeviations;
	}

	public void setPerMRRDeviations(String perMRRDeviations) {
		this.perMRRDeviations = perMRRDeviations;
	}

	public String getPerOperate() {
		return perOperate;
	}

	public void setPerOperate(String perOperate) {
		this.perOperate = perOperate;
	}

	public String getPerPhysicalChange() {
		return perPhysicalChange;
	}

	public void setPerPhysicalChange(String perPhysicalChange) {
		this.perPhysicalChange = perPhysicalChange;
	}

	public Timestamp getPerPTIOIssueDate() {
		return perPTIOIssueDate;
	}

	public void setPerPTIOIssueDate(Timestamp perPTIOIssueDate) {
		this.perPTIOIssueDate = perPTIOIssueDate;
	}

	public int getPerPTIOPermitNumber() {
		return perPTIOPermitNumber;
	}

	public void setPerPTIOPermitNumber(int perPTIOPermitNumber) {
		this.perPTIOPermitNumber = perPTIOPermitNumber;
	}

	public String getPerShutdown() {
		return perShutdown;
	}

	public void setPerShutdown(String perShutdown) {
		this.perShutdown = perShutdown;
	}

	public Timestamp getPerShutdownDate() {
		return perShutdownDate;
	}

	public void setPerShutdownDate(Timestamp perShutdownDate) {
		this.perShutdownDate = perShutdownDate;
	}

	public void populate(ResultSet rs) throws SQLException {
		try {
			setFacilityId(rs.getString("facility_id"));
			setDapcAccepted(rs.getString("dapc_accepted"));
			// DENNIS setDeviations(true);
			setReportId(AbstractDAO.getInteger(rs, "report_id"));
			setReportCRPTId(rs.getString("crpt_id"));
			setReportType(rs.getString("report_type_cd"));
			reportTypeDesc = ComplianceReportTypeDef.getData().getItems()
					.getDescFromAllItem(reportType);
			// if (reportType.equals("per")) {
			// reportTypeDesc= "Permit Evaluation Report";
			// } else if (reportType.equals("othr")) {
			// reportTypeDesc= "Other Compliance Report";
			// } else if (reportType.equals("tvcc")) {
			// reportTypeDesc= "Title V Annual Compliance Certification";
			// } else {
			// DisplayUtil.displayError("Compliance Report Error");
			// reportTypeDesc= "Undefined report type";
			// }
			setUserId(AbstractDAO.getInteger(rs, "user_id"));
			setReportStatus(rs.getString("report_status"));
			setDapcDateReviewed(rs.getTimestamp("dapc_reviewed_date"));
			setSubmittedDate(rs.getTimestamp("submitted_date"));
			setReceivedDate(rs.getTimestamp("received_date"));
			setComments(rs.getString("comments"));
			setDapcReviewComments(rs.getString("dapc_comments"));
			setDapcDeviationsReported(rs.getString("dapc_deviations"));
			setComplianceStatusCd(rs.getString("compliance_status_cd"));
			setLastModified(AbstractDAO.getInteger(rs, "cr_lm"));
			setDapcAccepted(rs.getString("dapc_accepted"));
			setPermitNumber(rs.getString("permit_number"));
			setValidated(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("validated_flag")));
			// logger.error("INFO: value of receivedDate is " +
			// getReceivedDate() + " for compliance report " + reportId);
			try {
				setDapcReviewer(AbstractDAO.getInteger(rs, "dapc_reviewer"));
			} catch (Exception e) {
				// reviewer hasn't been set yet
			}
			try {
				setScscId(rs.getString("scsc_id"));
			} catch (Exception e) {
				; // Retrieved only in some cases (needed for AFS export)
			}
			/*
			 * if
			 * (getReportType().equals(ComplianceReportTypeDef.COMPLIANCE_TYPE_SMBR
			 * )) { setFinalActionDate(rs.getTimestamp("final_action_date")); }
			 */
			/*
			 * if
			 * (getReportType().equals(ComplianceReportTypeDef.COMPLIANCE_TYPE_PER
			 * )) { SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			 * try { setPerDueDateCd(rs.getString("per_due_date_cd")); } catch
			 * (SQLException e) {logger.error("bad due date CD",e);} try {
			 * setPerStartDate(rs.getTimestamp("per_start_date")); } catch
			 * (SQLException e) {logger.error("bad start date",e);} try {
			 * setPerEndDate(rs.getTimestamp("per_end_date")); } catch
			 * (SQLException e) {logger.error("bad end date",e);} try {
			 * setPerDueDate(rs.getTimestamp("per_due_date")); } catch
			 * (SQLException e) {logger.error("bad due date",e);} try {
			 * reportingPeriodDesc= sdf.format(getPerStartDate()) + "-"+
			 * sdf.format(getPerEndDate()); } catch (Exception e) {
			 * reportingPeriodDesc= "ERROR";
			 * logger.error("Unable to compute PER period",e); } } else if
			 * (getReportType
			 * ().equals(ComplianceReportTypeDef.COMPLIANCE_TYPE_TVCC)) { try {
			 * setPerStartDate(rs.getTimestamp("per_start_date")); } catch
			 * (SQLException e) {} //the per_start_date field is used to store
			 * TVCC data due to changes by AQD Feb '08
			 * 
			 * try {
			 * setTvccReportingYear(Integer.toString(AbstractDAO.getInteger
			 * (rs,"tvcc_reporting_year"))); } catch (SQLException e) {}
			 * reportingPeriodDesc=getTvccReportingYear();
			 * 
			 * setTvccAfsId(rs.getString("tvcc_afs_id"));
			 * setTvccAfsSentDate(rs.getTimestamp("tvcc_afs_sent_date"));
			 * 
			 * } else {
			 */
			reportingPeriodDesc = "";
			try {
				setOtherCategoryCd(rs.getString("other_type_cd"));
				if (otherCategoryCd != null) {
					reportTypeDesc = ComplianceReportAllSubtypesDef.getData()
							.getItems().getItemDesc(otherCategoryCd);
				}
			} catch (SQLException e) {
			}
			/* } */

			try {
				legacyFlag = AbstractDAO.translateIndicatorToBoolean(rs
						.getString("legacy_flag"));
			} catch (SQLException e) {
			}

			setReportYear(AbstractDAO.getInteger(rs, "report_yr"));
			setReportQuarter(AbstractDAO.getInteger(rs, "report_qtr"));
			
			setFpId(AbstractDAO.getInteger(rs, "fp_Id"));
				
			setDirty(false);
		} catch (SQLException sqle) {
			logger.error(sqle.getMessage(), sqle);
		} finally {
			newObject = false;
		}
	}

	public ComplianceDeviation[] getDeviationReports() {
		return deviationReports;
	}

	public void setDeviationReports(ComplianceDeviation[] deviationReports) {
		this.deviationReports = deviationReports;
	}

	public String getFacilityNm() {
		if (facilityNm == null && facilityId != null) {
			try {
				// TODO revisit this logic
				FacilityService facilityBO = ServiceFactory.getInstance()
						.getFacilityService();
				Facility facility = facilityBO
						.retrieveFacilityTable(facilityId);
				if (facility != null) {
					facilityNm = facility.getName();
					setPermitClassCd(facility.getPermitClassCd());
					setFacilityTypeCd(facility.getFacilityTypeCd());
				} else {
					facility = facilityBO.retrieveFacility(facilityId, false);
					if (facility != null) {
						facilityNm = facility.getName();
						setPermitClassCd(facility.getPermitClassCd());
						setFacilityTypeCd(facility.getFacilityTypeCd());
					} else {
						logger.error("No facility data found for id "
								+ facilityId);
						facilityNm = "Unknown";
					}
				}
			} catch (ServiceFactoryException sfe) {
				logger.error("Exception accessing facility service", sfe);
				facilityNm = "Unknown";
			} catch (RemoteException re) {
				logger.error("Exception retrieving facility data for id "
						+ facilityId, re);
				facilityNm = "Unknown";
			}
		}
		return facilityNm;
	}

	public void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getReportTypeDesc() {
		return reportTypeDesc;
	}

	public boolean isCorrectedReport() {
		return correctedReport;
	}

	public void setCorrectedReport(boolean correctedReport) {
		this.correctedReport = correctedReport;
	}

	public String getTitle() {
		if (correctedReport) {
			return "Submit Corrected Report";
		}

		return "Submit New Report";
	}

	public String getOtherCategoryCd() {
		return otherCategoryCd;
	}

	public void setOtherCategoryCd(String otherCategoryCd) {
		this.otherCategoryCd = otherCategoryCd;
	}

	public Timestamp getPerDueDate() {
		return perDueDate;
	}

	public void setPerDueDate(Timestamp perDueDate) {
		this.perDueDate = perDueDate;
	}

	public String getReportingPeriodDesc() {
		return reportingPeriodDesc;
	}

	public boolean isReportBeingSubmitted() {
		return reportBeingSubmitted;
	}

	public void setReportBeingSubmitted(boolean reportBeingSubmitted) {
		this.reportBeingSubmitted = reportBeingSubmitted;
	}

	public void setReportTypeDesc(String reportTypeDesc) {
		this.reportTypeDesc = reportTypeDesc;
	}

	public void setReportingPeriodDesc(String reportingPeriodDesc) {
		this.reportingPeriodDesc = reportingPeriodDesc;
	}

	public long getSubmittedDateLong() {
		long ret = 0;
		if (submittedDate != null) {
			ret = submittedDate.getTime();
		}

		return ret;
	}

	public void setSubmittedDateLong(long submittedDateLong) {
		submittedDate = null;
		if (submittedDateLong > 0) {
			submittedDate = new Timestamp(submittedDateLong);
		}
	}

	public long getPerStartDateLong() {
		long ret = 0;
		if (perStartDate != null) {
			ret = perStartDate.getTime();
		}

		return ret;
	}

	public void setPerStartDateLong(long perStartDateLong) {
		perStartDate = null;
		if (perStartDateLong > 0) {
			perStartDate = new Timestamp(perStartDateLong);
		}
	}

	public long getPerEndDateLong() {
		long ret = 0;

		if (perEndDate != null) {
			ret = perEndDate.getTime();
		}

		return ret;
	}

	public void setPerEndDateLong(long perEndDateLong) {
		perEndDate = null;
		if (perEndDateLong > 0) {
			perEndDate = new Timestamp(perEndDateLong);
		}
	}

	public long getPerDueDateLong() {
		long ret = 0;

		if (perDueDate != null) {
			ret = perDueDate.getTime();
		}

		return ret;
	}

	public void setPerDueDateLong(long perDueDateLong) {
		perDueDate = null;
		if (perDueDateLong > 0) {
			perDueDate = new Timestamp(perDueDateLong);
		}
	}

	public long getPerPTIOIssueDateLong() {
		long ret = 0;
		if (perPTIOIssueDate != null) {
			ret = perPTIOIssueDate.getTime();
		}

		return ret;
	}

	public void setPerPTIOIssueDateLong(long perPTIOIssueDateLong) {
		perPTIOIssueDate = null;
		if (perPTIOIssueDateLong > 0) {
			perPTIOIssueDate = new Timestamp(perPTIOIssueDateLong);
		}
	}

	public long getPerShutdownDateLong() {
		long ret = 0;

		if (perShutdownDate != null) {
			ret = perShutdownDate.getTime();
		}

		return ret;
	}

	public void setPerShutdownDateLong(long perShutdownDateLong) {
		perShutdownDate = null;
		if (perShutdownDateLong > 0) {
			perShutdownDate = new Timestamp(perShutdownDateLong);
		}
	}

	public long getDapcDateReviewedLong() {
		long ret = 0;

		if (dapcDateReviewed != null) {
			ret = dapcDateReviewed.getTime();
		}

		return ret;
	}

	public void setDapcDateReviewedLong(long dapcDateReviewedLong) {
		dapcDateReviewed = null;
		if (dapcDateReviewedLong > 0) {
			dapcDateReviewed = new Timestamp(dapcDateReviewedLong);
		}
	}

	/*
	 * public boolean isStaging() { return staging; }
	 * 
	 * public void setStaging(boolean staging) { this.staging = staging; }
	 */
	public boolean isCreateFileDir() {
		return createFileDir;
	}

	public void setCreateFileDir(boolean createFileDir) {
		this.createFileDir = createFileDir;
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		// manually set transient date values since this does not appear to
		// work properly with persistence
		setDapcDateReviewedLong(this.dapcDateReviewedLong);
		setPerDueDateLong(this.perDueDateLong);
		setPerEndDateLong(this.perEndDateLong);
		setPerPTIOIssueDateLong(this.perPTIOIssueDateLong);
		setPerShutdownDateLong(this.perShutdownDateLong);
		setPerStartDateLong(this.perStartDateLong);
		setSubmittedDateLong(this.submittedDateLong);
		setReceivedDateLong(this.receivedDateLong);
		// logger.error("INFO: value of receivedDate is " + getReceivedDate() +
		// " for compliance report " + reportId);
	}

	public final String getTvccAfsId() {
		return tvccAfsId;
	}

	public final void setTvccAfsId(String tvccAfsId) {
		this.tvccAfsId = tvccAfsId;
	}

	public final Timestamp getTvccAfsSentDate() {
		return tvccAfsSentDate;
	}

	public final void setTvccAfsSentDate(Timestamp tvccAfsSentDate) {
		this.tvccAfsSentDate = tvccAfsSentDate;
	}

	public final String getComplianceStatusCd() {
		return complianceStatusCd;
	}

	public final void setComplianceStatusCd(String complianceStatus) {
		this.complianceStatusCd = complianceStatus;
	}

	public final String getUserName() {
		return userName;
	}

	public final void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSubmitLabel() {
		return submitLabel;
	}

	public void setSubmitLabel(String submitLabel) {
		this.submitLabel = submitLabel;
	}

	public String getSubmitValue() {
		return submitValue;
	}

	public void setSubmitValue(String submitValue) {
		this.submitValue = submitValue;
	}

	public Timestamp getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Timestamp receivedDate) {
		this.receivedDate = receivedDate;
		if (this.receivedDate != null) {
			this.receivedDateLong = this.receivedDate.getTime();
		} else {
			this.receivedDateLong = 0;
		}
	}

	public long getReceivedDateLong() {
		long ret = 0;
		if (receivedDate != null) {
			ret = receivedDate.getTime();
		}

		return ret;
	}

	public void setReceivedDateLong(long receivedDateLong) {
		receivedDate = null;
		if (receivedDateLong > 0) {
			receivedDate = new Timestamp(receivedDateLong);
		}
	}

	public Timestamp getFinalActionDate() {
		return finalActionDate;
	}

	public void setFinalActionDate(Timestamp finalActionDate) {
		this.finalActionDate = finalActionDate;
	}

	public String getScscId() {
		return scscId;
	}

	public void setScscId(String scscId) {
		this.scscId = scscId;
	}

	public boolean containsTradeSecrets() {
		boolean rtn = false;
		for (ComplianceReportAttachment a : getAttachments()) {
			if (a.getTradeSecretDocId() != null)
				rtn = true;
		}
		return rtn;
	}

	public String getPermitNumber() {
		return permitNumber;
	}

	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	public Note[] getNotes() {
		return notes;
	}

	public void setNotes(Note[] notes) {
		this.notes = notes;
		setDirty(true);
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

	public boolean getValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public boolean isLegacyFlag() {
		return legacyFlag;
	}

	public void setLegacyFlag(boolean legacyFlag) {
		this.legacyFlag = legacyFlag;
	}

	public void updateDefaults(ValueChangeEvent ve) {

		boolean legacyFlag = (boolean) ve.getNewValue();
		if (legacyFlag) {
			if (getDapcDateReviewed() == null && getReceivedDate() != null) {
				setDapcDateReviewed(new Timestamp(getReceivedDate().getTime()));
			}
			setDapcAccepted(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_ACCEPTED);
			if (this.dapcReviewer == 0) {
				try {
					infraBO = ServiceFactory.getInstance().getInfrastructureService();
					UserDefBase userDef = infraBO.checkUserExists(LEGACY_USER);
					if (userDef != null) {
						setDapcReviewer(userDef.getUserId());
					} else {
						String msg = "The Legacy Data user is not defined. Please contact the system administrator.";
						logger.error(msg);
						DisplayUtil.displayError(msg);
					}
				} catch (Exception e) {
					String msg = "An error occurred while retrieving the Legacy Data user.";
					logger.error(msg, e);
					DisplayUtil.displayError(msg);
				}
			}
		} else {
			// setReceivedDate(null);
			setDapcDateReviewed(null);
			setDapcAccepted(ComplianceReportAcceptedDef.COMPLIANCE_REPORT_UNREVIEWED);
			setDapcReviewer(0);
		}
	}

	public void updateReviewedDate(ValueChangeEvent ve) {
		Timestamp receivedDate = (Timestamp) ve.getNewValue();
		if (legacyFlag && getDapcDateReviewed() == null) {
			setDapcDateReviewed(receivedDate);
		}
	}

	public boolean isReceivedDateEntered() {
		boolean ret = false;
		if (receivedDate != null
				&& !Utility.isNullOrEmpty(receivedDate.toString())) {
			ret = true;
		}
		return ret;
	}

	public Integer getReportYear() {
		return reportYear;
	}

	public void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}

	public Integer getReportQuarter() {
		return reportQuarter;
	}

	public void setReportQuarter(Integer reportQuarter) {
		this.reportQuarter = reportQuarter;
	}


	@Override
	public ValidationMessage[] validate() {
		this.validationMessages.clear();


		// SCG trim?
		requiredField(this.getOtherCategoryCd(), "otherCategoryCd",
				"Report Category");
		
		if (isCemsComsRataRpt()) {
			requiredField(this.getReportYear(), "reportYear", "Report Year");
			requiredField(this.getReportQuarter(), "reportQuarter",
					"Report Quarter");
		
			if (isSecondGenerationCemComRataRpt()) {
				// validate facility inventory
				List<ValidationMessage>	fpVal = 
						FacilityValidation.validateCemsComsRataReport(getFpId());
				
				// add facility validation messages to validation message popup
				for (ValidationMessage fpMsg : fpVal) {
					this.validationMessages.put(fpMsg.getReferenceID(),fpMsg);
					if (fpMsg.getSeverity()
							.equals(ValidationMessage.Severity.ERROR)) {
					}
				}
				
				if(!fpVal.isEmpty()
						&& CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
					this.validationMessages
							.put("fac_error_info",
									new ValidationMessage(
											null,
											"If the facility inventory has recently been updated, "
													+ "you may need to click the 'Associate with Current Facility Inventory' "
													+ "button to avoid reporting errors that have already been corrected.",
											ValidationMessage.Severity.INFO));
				}
				
			}
			if (isSecondGenerationAnnualRATARpt()) {
				validateCompReportMonitorListAnnualRATARpt();
			}
			if (isSecondGenerationQuarterlyCemComRpt()) {
				validateCompReportMonitorListQuarterlyCemComRpt();
			}
		}
		
		// SCG trim?
		requiredField(this.getComments(), "comments",
				"Description, Reporting Period and/or date(s)");
		/*
		 * if (Utility.isNullOrEmpty(this.getOtherCategoryCd())) {
		 * ValidationMessage valMsg = new ValidationMessage("otherCategoryCd",
		 * "The report category is required.", ValidationMessage.Severity.ERROR,
		 * ValidationBase.COMPLIANCE_TAG);
		 * this.validationMessages.put("otherCategoryCd", valMsg); }
		 */

		/*
		 * if(Utility.isNullOrEmpty(this.getComments())) { ValidationMessage
		 * valMsg = new ValidationMessage("comments",
		 * "You must provide the Description, Reporting Period and/or date(s).",
		 * ValidationMessage.Severity.ERROR, ValidationBase.COMPLIANCE_TAG);
		 * this.validationMessages.put("comments", valMsg); }
		 */

		if (!hasAttachments()) {
			ValidationMessage valMsg = new ValidationMessage(
					"doc_attachments",
					"This Compliance Report must have at least one attachment.",
					ValidationMessage.Severity.ERROR);
			this.validationMessages.put("doc_attachments", valMsg);
		}

		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {

			if (this.getReceivedDate() == null
					|| Utility.isNullOrEmpty(this.getReceivedDate().toString())) {

				ValidationMessage valMsg = new ValidationMessage(
						"receivedDate", "The Received Date is not set.",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("receivedDate", valMsg);
			}

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			Timestamp today = new Timestamp(cal.getTimeInMillis());

			if (this.getReceivedDate() != null) {
				if (today.before(this.getReceivedDate())) {

					ValidationMessage valMsg = new ValidationMessage(
							"receivedDate",
							"The Received Date cannot be in the future.",
							ValidationMessage.Severity.ERROR);
					this.validationMessages.put("receivedDate", valMsg);

				}
			}

			if (ComplianceReportTypeDef.anyOtherType(this.getReportType())
					&& this.getComplianceStatusCd() != null
					&& this.getComplianceStatusCd().equalsIgnoreCase(
							ReportComplianceStatusDef.UNDETERMINED)) {
				ComplianceReportAttachment[] crAttachments = this
						.getAttachments();
				boolean hasMemo = false;
				if (crAttachments != null) {
					String memoCd = "";
					if (this.getReportType().equalsIgnoreCase(
							ComplianceReportTypeDef.COMPLIANCE_TYPE_ONE)) {
						memoCd = ComplianceAttachmentTypeDef.ONE_ATTACHMENT_TYPE_MEMO;
					} else if (this.getReportType().equalsIgnoreCase(
							ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS)) {
						memoCd = ComplianceAttachmentTypeDef.CEMS_ATTACHMENT_TYPE_MEMO;
					} else if (this.getReportType().equalsIgnoreCase(
							ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER)) {
						memoCd = ComplianceAttachmentTypeDef.OTHER_ATTACHMENT_TYPE_MEMO;
					}

					for (ComplianceReportAttachment crAttachment : crAttachments) {
						if (crAttachment.getDocTypeCd()
								.equalsIgnoreCase(memoCd)) {
							hasMemo = true;
						}
					}
				}

				if (!hasMemo) {
					ValidationMessage valMsg = new ValidationMessage(
							null,
							"A Memo for \"Undetermined\" Compliance Status attachment is required",
							ValidationMessage.Severity.ERROR);
					this.validationMessages.put(null, valMsg);
				}

			}
		}

		for (ValidationMessage vm : validationMessages.values()) {
			if (null == vm.getReferenceID()) {
				vm.setReferenceID(ValidationBase.COMPLIANCE_TAG + ":" + reportId
						+ ":" + COMPLIANCE_REPORT);
			}
		}

		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	public final boolean isCemsComsRataRpt() {
		boolean ret = false;

		// Make sure report type and category have values.
		// This check is necessary for proper navigation after a delete.
		// Otherwise, we get NPE.
		if (getReportType() == null
				|| Utility.isNullOrEmpty(getReportType())) {
			return ret;
		}
		
		if (getOtherCategoryCd() == null
				|| Utility.isNullOrEmpty(getOtherCategoryCd())) {
			return ret;
		}
		
		if (getReportType()
				.equals(ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS)
				&& (getOtherCategoryCd().equals("24")
				|| getOtherCategoryCd().equals("21"))) {
			ret = true;
		}
		return ret;
	}
	
	public final boolean isQuarterlyCemComRpt() {
		boolean ret = false;

		// Make sure report type and category have values.
		// This check is necessary for proper navigation after a delete.
		// Otherwise, we get NPE.
		if (getReportType() == null
				|| Utility.isNullOrEmpty(getReportType())) {
			return ret;
		}
		
		if (getOtherCategoryCd() == null
				|| Utility.isNullOrEmpty(getOtherCategoryCd())) {
			return ret;
		}
		
		if (getReportType()
				.equals(ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS)
				&& (getOtherCategoryCd().equals("24"))) {
			ret = true;
		}
		return ret;
	}
	
	public final boolean isAnnualRATARpt() {
		boolean ret = false;

		// Make sure report type and category have values.
		// This check is necessary for proper navigation after a delete.
		// Otherwise, we get NPE.
		if (getReportType() == null
				|| Utility.isNullOrEmpty(getReportType())) {
			return ret;
		}
		
		if (getOtherCategoryCd() == null
				|| Utility.isNullOrEmpty(getOtherCategoryCd())) {
			return ret;
		}
		
		if (getReportType()
				.equals(ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS)
				&& (getOtherCategoryCd().equals("21"))) {
			ret = true;
		}
		return ret;
	}

	public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}
	
	/**
	 * Returns true if the quarterly cem/com monitoring or annual RATA report
	 * is a second generation report i.e., report is associated with a specific
	 * version of the facility. 
	 */
	public boolean isSecondGenerationCemComRataRpt() {
		return isCemsComsRataRpt() && null != getFpId();
	}
	
	public boolean isSecondGenerationQuarterlyCemComRpt() {
		return isQuarterlyCemComRpt() && null != getFpId();
	}
	
	public boolean isSecondGenerationAnnualRATARpt() {
		return isAnnualRATARpt() && null != getFpId();
	}
	
	public ComplianceReportMonitor[] getMonitors() {
		return monitors;
	}

	public void setMonitors(ComplianceReportMonitor[] monitors) {
		this.monitors = monitors;
		setDirty(true);
	}
	
	public ComplianceReportLimit[] getLimits() {
		return limits;
	}

	public void setLimits(ComplianceReportLimit[] limits) {
		this.limits = limits;
		setDirty(true);
	}
	
	private void buildCompReportMonitorMap() {
		if (this.isSecondGenerationCemComRataRpt()) {
			compReportMonitorMap.clear();
			ComplianceReportLimit limits[] = this.getLimits();
			ComplianceReportMonitor monitors[] = this.getMonitors();

			if (limits != null) {

				for (ComplianceReportLimit crl : limits) {
					if (monitors != null) {
						for (ComplianceReportMonitor crm : monitors) {
							if (crm.getCrMonitorId().equals(
									crl.getCrMonitorId())) {
								logger.debug("found monitor " + crm.getMonId()
										+ " associated with limit "
										+ crl.getLimId());
								List<ComplianceReportLimit> limitList = compReportMonitorMap
										.get(crm);

								if (null == limitList) {
									limitList = new ArrayList<ComplianceReportLimit>();
								}

								limitList.add(crl);
								compReportMonitorMap.put(crm, limitList);
								break;
							}
						}
					}
				}
			}

			// add monitor(s) that don't have associated limit(s)
			if (monitors != null) {
				for (ComplianceReportMonitor crm : monitors) {
					List<ComplianceReportLimit> limitList = compReportMonitorMap
							.get(crm);

					if (null == limitList) {
						limitList = new ArrayList<ComplianceReportLimit>();
						compReportMonitorMap.put(crm, limitList);
						logger.debug("found monitor " + crm.getMonId()
								+ " with no limits");
					}
				}
			}
		}
	}

	public void refreshCompReportMonitorList() {
		if (this.isSecondGenerationCemComRataRpt()) {
			// build a map of compliance report monitors and the associated
			// limits
			// for the given compliance report
			buildCompReportMonitorMap();

			Iterator<ComplianceReportMonitor> iter = compReportMonitorMap
					.keySet().iterator();
			compReportMonitorList.clear();

			while (iter.hasNext()) {
				ComplianceReportMonitor monitor = iter.next();
				List<ComplianceReportLimit> limits = compReportMonitorMap
						.get(monitor);
				ComplianceReportMonitor complianceReportMonitor = new ComplianceReportMonitor(
						monitor);

				complianceReportMonitor.setComplianceReportLimitList(limits);

				compReportMonitorList.add(complianceReportMonitor);
			}
			Collections.sort(compReportMonitorList);
		}
	}

	public List<ComplianceReportMonitor> getCompReportMonitorList() {
		return this.compReportMonitorList;
	}

	public void setCompReportMonitorList(
			List<ComplianceReportMonitor> compReportMonitorList) {
		this.compReportMonitorList = compReportMonitorList;
	}
	
	public void validateCompReportMonitorListAnnualRATARpt() {
		int countLimitInlucded = 0;
		List<ComplianceReportMonitor> monitors = getCompReportMonitorList();
		for (ComplianceReportMonitor m : monitors) {

			List<ComplianceReportLimit> limits = m
					.getComplianceReportLimitList();
			
			for (ComplianceReportLimit l : limits) {
				if (l.isIncludedFlag()){
					++countLimitInlucded;
				}
			}
			
			if (m.getTestDate() != null && !Utility.isNullOrEmpty(m.getTestDate().toString())) {
				boolean includesLimits = false;
				for (ComplianceReportLimit l : limits) {
					if (l.isIncludedFlag()) {
						includesLimits = true;
						break;
					}
				}
				if (!includesLimits) {
					String property = "testDate1" + m.getMonId();
					String msg = "Since a Test Date is provided for monitor "
							+ m.getMonId()
							+ ", at least one limit must be included.";
					ValidationMessage valMsg = new ValidationMessage(property,
							msg, ValidationMessage.Severity.ERROR);
					this.validationMessages.put(property, valMsg);
				}
			}

			for (ComplianceReportLimit l : limits) {
				if (l.isIncludedFlag()
						&& (Utility.isNullOrEmpty(l.getLimitStatus()))) {
					String msg = "Included limit(s) for monitor "
							+ m.getMonId()
							+ ", must have a value for RATA Result.";
					String property = "limitStatus" + m.getMonId()
							+ l.getLimId();
					ValidationMessage valMsg = new ValidationMessage(property,
							msg, ValidationMessage.Severity.ERROR);
					this.validationMessages.put(property, valMsg);
					break;
				}
			}

			for (ComplianceReportLimit l : limits) {
				if (!l.isIncludedFlag()
						&& (!Utility.isNullOrEmpty(l.getLimitStatus()))) {
					String msg = "Limit "
							+ l.getLimId()
							+ " for monitor "
							+ m.getMonId()
							+ ", must be included, since it has a value for RATA Result.";
					String property = "includedFlag" + m.getMonId()
							+ l.getLimId();
					ValidationMessage valMsg = new ValidationMessage(property,
							msg, ValidationMessage.Severity.ERROR);
					this.validationMessages.put(property, valMsg);
				}
			}

			for (ComplianceReportLimit l : limits) {
				String property = "testDate2" + m.getMonId() + l.getLimId();
				if (l.isIncludedFlag()
						&& (m.getTestDate() == null || Utility.isNullOrEmpty(m
								.getTestDate().toString()))) {
					String msg = "Since limit(s) are included in the report for monitor "
							+ m.getMonId()
							+ ", Test Date for this monitor must be provided.";
					ValidationMessage valMsg = new ValidationMessage(property,
							msg, ValidationMessage.Severity.ERROR);
					this.validationMessages.put(property, valMsg);
					break;
				}
			}

			if (m.isCertificationFlag()) {
				boolean includesLimits = false;
				for (ComplianceReportLimit l : limits) {
					if (l.isIncludedFlag()) {
						includesLimits = true;
						break;
					}
				}
				if (!includesLimits) {
					String property = "certificationFlag" + m.getMonId();
					String msg = "Since this is a certification for monitor "
							+ m.getMonId()
							+ ", at least one limit must be included.";
					ValidationMessage valMsg = new ValidationMessage(property,
							msg, ValidationMessage.Severity.ERROR);
					this.validationMessages.put(property, valMsg);
				}
			}
		}
		
		if (this.limits.length > 0){
			if (countLimitInlucded==0){
				String property = "continuous_monitors";
				String msg = "There must be at least one Limit included in th report.";
				ValidationMessage valMsg = new ValidationMessage(property, msg, ValidationMessage.Severity.ERROR);
				this.validationMessages.put(property, valMsg);
			}
		}
	}

	public void validateCompReportMonitorListQuarterlyCemComRpt() {
		int countLimitInlucded = 0;
		List<ComplianceReportMonitor> monitors = getCompReportMonitorList();
		for (ComplianceReportMonitor m : monitors) {
			
			List<ComplianceReportLimit> limits = m.getComplianceReportLimitList();
			
			for (ComplianceReportLimit l : limits) {
				if (l.isIncludedFlag()){
					++countLimitInlucded;
				}
			}
			
			for (ComplianceReportLimit l : limits) {
				String property = "auditStatus" + m.getMonId() + l.getLimId();
				if (l.isIncludedFlag()
						&& (Utility.isNullOrEmpty(m.getAuditStatus()))) {
					String msg = "Since limit(s) are included in the report for monitor "
							+ m.getMonId()
							+ ", Audit Result for this monitor must be provided.";
					ValidationMessage valMsg = new ValidationMessage(property,
							msg, ValidationMessage.Severity.ERROR);
					this.validationMessages.put(property, valMsg);
					break;
				}
			}

			if (!Utility.isNullOrEmpty(m.getAuditStatus())
					&& (m.getTestDate() == null || Utility.isNullOrEmpty(m
							.getTestDate().toString()))) {
				String property = "auditStatus1" + m.getMonId();
				String msg = "Since an Audit Result is provided for monitor "
						+ m.getMonId() + ", Audit Date must also be provided.";
				ValidationMessage valMsg = new ValidationMessage(property, msg,
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put(property, valMsg);
			}

			if (Utility.isNullOrEmpty(m.getAuditStatus())
					&& (m.getTestDate() != null && !Utility.isNullOrEmpty(m
							.getTestDate().toString()))) {
				String property = "auditStatus2" + m.getMonId();
				String msg = "Since an Audit Date is provided for monitor "
						+ m.getMonId()
						+ ", Audit Result must also be provided.";
				ValidationMessage valMsg = new ValidationMessage(property, msg,
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put(property, valMsg);
			}

			if (!Utility.isNullOrEmpty(m.getAuditStatus())) {
				boolean includesLimits = false;
				for (ComplianceReportLimit l : limits) {
					if (l.isIncludedFlag()) {
						includesLimits = true;
						break;
					}
				}
				if (!includesLimits) {
					String property = "auditStatus3" + m.getMonId();
					String msg = "Since an Audit Result is provided for monitor "
							+ m.getMonId()
							+ ", at least one limit must be included.";
					ValidationMessage valMsg = new ValidationMessage(property,
							msg, ValidationMessage.Severity.ERROR);
					this.validationMessages.put(property, valMsg);
				}
			}

			for (ComplianceReportLimit l : limits) {
				String property = "testDate" + m.getMonId() + l.getLimId();
				if (l.isIncludedFlag()
						&& (m.getTestDate() == null || Utility.isNullOrEmpty(m
								.getTestDate().toString()))) {
					String msg = "Since limit(s) are included in the report for monitor "
							+ m.getMonId()
							+ ", Audit Date for this monitor must be provided.";
					ValidationMessage valMsg = new ValidationMessage(property,
							msg, ValidationMessage.Severity.ERROR);
					this.validationMessages.put(property, valMsg);
					break;
				}
			}
		}
		
		if (this.limits.length > 0){
			if (countLimitInlucded==0){
				String property = "continuous_monitors";
				String msg = "There must be at least one Limit included in th report.";
				ValidationMessage valMsg = new ValidationMessage(property, msg, ValidationMessage.Severity.ERROR);
				this.validationMessages.put(property, valMsg);
			}
		}
	}
	
	public List<ComplianceStatusEvent> getAssocComplianceStatusEvents() {
		if(assocComplianceStatusEvents == null) return new ArrayList<ComplianceStatusEvent>();
		return assocComplianceStatusEvents;
	}
	
	public void setAssocComplianceStatusEvents(List<ComplianceStatusEvent> assocComplianceStatusEvents) {
		this.assocComplianceStatusEvents = assocComplianceStatusEvents;
	}
	
	public List<String> getInspectionsReferencedIn() {
		if(inspectionsReferencedIn == null) return new ArrayList<String>();
		return inspectionsReferencedIn;
	}
	public void setInspectionsReferencedIn(List<String> inspectionsReferencedIn) {
		this.inspectionsReferencedIn = inspectionsReferencedIn;
	}
	
}
