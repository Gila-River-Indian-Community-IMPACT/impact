package us.oh.state.epa.stars2.app.facility;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.app.application.ApplicationSearch;
import us.oh.state.epa.stars2.app.compliance.ComplianceReports;
import us.oh.state.epa.stars2.app.invoice.InvoiceSearch;
import us.oh.state.epa.stars2.app.permit.PermitDetail;
import us.oh.state.epa.stars2.app.permit.PermitSearch;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.LimitTrendReportLineItem;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.def.FacilityAttachmentTypeDef;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.correspondence.CorrespondenceSearch;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportProfileBase;
import us.oh.state.epa.stars2.webcommon.reports.ReportSearch;
import us.wy.state.deq.impact.database.dbObjects.continuousMonitoring.ContinuousMonitor;
import us.wy.state.deq.impact.webcommon.monitoring.MonitorGroupDetail;

public class FacilityProfile extends FacilityProfileBase implements
		IAttachmentListener {

	private static final long serialVersionUID = 3322818091907416988L;

	private List<Permit> allEuPermits;
	private String refreshStr = " "; // used by bulk operations and some custom
										// reports.
	
	// used for limit trend reports only
	private String trendRptLimId;
	private String trendRptMonId;
	private Integer trendRptMonitorId;
	private String trendRptLimitDesc;
	private String trendRptMonitorDetails;
	private Integer trendRptCorrLimitId;
	private Integer trendRptComplianceReportId;
	private String fromFacility = "true";
	
	private TableSorter periodicLimitTrendWrapper;
	private TableSorter auditLimitTrendWrapper;


	public FacilityProfile() {
		super();
		// allEuPermitsWrapper = new TableSorter();
		staging = false;
	}
//
//	public final String refreshFacilityProfile() {
//		if (fpId != null) {
//			TreeNode tempSelectedTreeNode = selectedTreeNode;
//			String tempCurrent = current;
//			refreshFacility();
//			selectedTreeNode = tempSelectedTreeNode;
//			current = tempCurrent;
//			nodeClicked();
//		}
//		return FAC_OUTCOME;
//	}

	/* START CODE: facility applications, permits, reports, invoices */
	/* NOTE: some can go to base if have common Search code */

	public final String getApplications() {
		ApplicationSearch applSearch = (ApplicationSearch) FacesUtil
				.getManagedBean("applicationSearch");
		ApplicationSearch backup = new ApplicationSearch();
		backup.copy(applSearch);
		applSearch.setBackup(backup);
		applSearch.reset();
		applSearch.setApplicationNumber(null);
		applSearch.setApplicationType(null);
		applSearch.setCountyCd(null);
		applSearch.setDoLaaCd(null);
		applSearch.setFacilityName(null);
		applSearch.setFromFacility("true");
		applSearch.setFacilityID(facilityId);
		applSearch.setNewApplicationFacilityID(facilityId);
		applSearch.search();
		if (applSearch.getApplications() == null) {
			DisplayUtil
					.displayError("Search application for the facility failed.");
		}
		return "facilities.profile.applications";
	}

	public final String getPermits() {
		PermitSearch permitSearch = (PermitSearch) FacesUtil
				.getManagedBean("permitSearch");
		// 2598
		PermitSearch backup = new PermitSearch();
		backup.copy(permitSearch);
		permitSearch.setBackup(backup);
		permitSearch.reset();
		permitSearch.setFromFacility("true");
		permitSearch.setFacilityID(facilityId);
		permitSearch.search();
		if (permitSearch.getPermits() == null) {
			DisplayUtil.displayError("Search permits for the facility failed.");
		} else if (permitSearch.getPermits().size() == 0) {
			if (fromFacility.equals("true")) {
        		DisplayUtil.displayInfo("There are no Permits available for this facility.");
        	} else {
        		DisplayUtil.displayInfo("There are no Permits that match the search criteria.");
        	}
		}
		return "facilities.profile.permits";
	}

	public final String getEmissionsReports() {
		ReportSearch reportSearch = (ReportSearch) FacesUtil
				.getManagedBean("reportSearch");
		// 2598
		EmissionsReportSearch ers = reportSearch.getSearchObj();
		reportSearch.reset();
		reportSearch.setSearchObj(new EmissionsReportSearch());
		reportSearch.setFromFacility("true");
		reportSearch.setFacilityId(facilityId);
		reportSearch.submitSearch();
		reportSearch.setSearchObj(ers);
		if (reportSearch.getReports() == null) {
			DisplayUtil
					.displayError("Search emissions inventories for the facility failed.");
		} else if (reportSearch.getReports().length == 0) {
			if (fromFacility.equals("true")) {
                DisplayUtil.displayInfo("There are no Emissions Inventories available for this facility.");
            } else {
                DisplayUtil.displayInfo("There are no Emissions Inventories that match the search criteria.");
            }
		}
		return "facilities.profile.emissionReports";
	}

	public final String createNewEmissionReport() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		String s = null;
		try {
			ReportProfileBase reportProfile = (ReportProfileBase) FacesUtil
					.getManagedBean("reportProfile");
			reportProfile.setInStaging(false);
			s = reportProfile.createNewEmissionReport(facilityId);
		} finally {
			clearButtonClicked();
		}
		return s;
	}

	public final String getReportingCategory() {
		ReportProfileBase reportProfile = (ReportProfileBase) FacesUtil
				.getManagedBean("reportProfile");
		reportProfile.setFpId(fpId);
		reportProfile.setSavedFpId(null);
		return "facilities.profile.reportingCategory";
	}

	public final String getInvoices() {
		InvoiceSearch invoiceSearch = (InvoiceSearch) FacesUtil
				.getManagedBean("invoiceSearch");
		invoiceSearch.reset();
		invoiceSearch.setFromFacility("true");
		invoiceSearch.setFacilityId(facilityId);
		invoiceSearch.submitSearch();
		return "facilities.profile.invoices";
	}

	public final String getCorrespondence() {
		CorrespondenceSearch corrSearch = (CorrespondenceSearch) FacesUtil
				.getManagedBean("correspondenceSearch");
		CorrespondenceSearch backup = new CorrespondenceSearch();
		backup.copy(corrSearch);
		corrSearch.setBackup(backup);
		corrSearch.reset();
		corrSearch.setFromFacility("true");
		corrSearch.setFromEnforcementAction("false");
		corrSearch.setFacilityId(facilityId);
		corrSearch.submitSearch();
		if (corrSearch.getCorrespondence() == null) {
			DisplayUtil
					.displayError("Search correspondence for the facility failed.");
		}
		return "facilities.profile.correspondence";
	}
	

	/* START CODE: attachments */

	public final String getAttachments() {
		/*
		 * STEP 1 Get a reference to the Attachment backing bean
		 */

		Attachments attachments = (Attachments) FacesUtil
				.getManagedBean("attachments");
		attachments.addAttachmentListener(this);

		/*
		 * STEP 2 Create a new, empty Document object, set its FacilityID and
		 * give the attachment backing bean a reference to it.
		 */

		attachments.setSubPath("Attachments");
		attachments.setFacilityId(facilityId);

		/*
		 * STEP 3 Set the picklist in the backing bean for the document types
		 */

		attachments.setAttachmentTypesDef(FacilityAttachmentTypeDef.getData()
				.getItems());

		attachments.setNewPermitted(!isReadOnlyUser());
		attachments.setUpdatePermitted(!isReadOnlyUser());
		attachments.setDeletePermitted(!isReadOnlyUser());
		attachments.setHasDocType(true);

		try {
			attachments.setAttachmentList(getFacilityService()
					.retrieveFacilityAttachments(facilityId));
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Cannot access facility attachments.");
		}

		return "facilities.profile.attachments";
	}

	public AttachmentEvent createAttachment(Attachments attachments) {
		boolean ok = true;
        if (attachments.getDocument() == null) {
            // should never happen
            logger.error("Attempt to process null attachment");
            ok = false;
        } else {
        	        	
        	// make sure document description and type are provided
            if (attachments.getDocument().getDescription() == null || attachments.getDocument().getDescription().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify the description for this attachment.");
                ok = false;
            }
            if (attachments.getDocument().getDocTypeCd() == null || attachments.getDocument().getDocTypeCd().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify an attachment type for this attachment.");
                ok = false;
            }
            if(attachments.getFileToUpload() == null) {
            	DisplayUtil
				.displayError("Please specify the file to upload for this attachment.");
            	ok = false;
            }
        }
		if(ok) {
			try {
				attachments.getTempDoc().setObjectId(null);
				getFacilityService().createFacilityAttachment(attachments.getTempDoc(),
						attachments.getFileToUpload().getInputStream());
				attachments.setAttachmentList(getFacilityService()
						.retrieveFacilityAttachments(facilityId));
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Create facility attachment failed.");
			} catch (IOException ioe) {
				handleException(new RemoteException(ioe.getMessage()));
				DisplayUtil.displayError("Create facility attachment failed.");
			}
			FacesUtil.returnFromDialogAndRefresh();
		}
		return null;
	}

	public void cancelAttachment() {
		Attachments attachments = (Attachments) FacesUtil.getManagedBean("attachments");
		try {
			attachments.setAttachmentList(getFacilityService().retrieveFacilityAttachments(facilityId));
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Refreshing facility attachment(s) failed.");
		}
		FacesUtil.returnFromDialogAndRefresh();
	}

	public AttachmentEvent deleteAttachment(Attachments attachments) {
		try {
			getFacilityService().removeFacilityAttachment(attachments.getTempDoc());
			attachments.setAttachmentList(getFacilityService()
					.retrieveFacilityAttachments(facilityId));
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Delete facility attachment failed.");
		}
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	public AttachmentEvent updateAttachment(Attachments attachments) {
		boolean ok = true;
		// make sure document description is provided
        if (attachments.getDocument().getDescription() == null || attachments.getDocument().getDescription().trim().equals("")) {
            DisplayUtil
                    .displayError("Please specify the description for this attachment.");
            ok = false;
        }
		if(ok) {
			try {
				getFacilityService().updateFacilityAttachment(attachments.getTempDoc());
				attachments.setAttachmentList(getFacilityService()
						.retrieveFacilityAttachments(facilityId));
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Update facility attachment failed.");
			}
			FacesUtil.returnFromDialogAndRefresh();
		}
		return null;
	}

	/* END CODE: attachments */

	public boolean isFacilityRolesUpdatable() {
		return InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(
				"facilities.profile.facilityRoles.editFacilityRoles");
	}

	public String getAllEuPermitDialog() {
		try {
			allEuPermits = getPermitService().searchAllEuPermits(
					emissionUnit.getCorrEpaEmuId());
			for (Permit permit : allEuPermits) {
				permit.setPermitEU(emissionUnit.getCorrEpaEmuId());
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Accessing EU Permits failed.");
			return null;
		}
		return "dialog:allEuPermits";
	}

	// public TableSorter getAllEuPermitsWrapper() {
	// return allEuPermitsWrapper;
	// }
	//
	// public void setAllEuPermitsWrapper(TableSorter allEuPermitsWrapper) {
	// this.allEuPermitsWrapper = allEuPermitsWrapper;
	// }

	public void loadEuPermit() {
		PermitDetail permitDet = (PermitDetail) FacesUtil
				.getManagedBean("permitDetail");
		popupRedirect = permitDet.loadPermit();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final List<Permit> getAllEuPermits() {
		return allEuPermits;
	}

	public final void setAllEuPermits(List<Permit> allEuPermits) {
		this.allEuPermits = allEuPermits;
	}
	
	public String submitHistoryProfile() {
		// reset facility in case of splitting current and immediately
		// navigating to
		// old current.
		return excuteSubmitHistoryProfile();
	}

	public String getOverallComplianceStatus() {
		try {
			return getFacilityService().getOverallComplianceStatus(facilityId);

		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Checking Open Enforcement failed.");
		}
		return null;
	}

	public final String getRefreshStr() {
		return refreshStr;
	}

	public final void setRefreshStr(String refreshStr) {
		if (refreshStr == null || refreshStr.length() < 1) {
			this.refreshStr = " ";
		} else {
			this.refreshStr = refreshStr;
		}
	}
	
	public final boolean isMigrationHappened(){
		if(UserAttributes.bolMigrated){
				return true;
		}
		
		return false;
	}
	
	public final String submitMonitorGroup() {
		MonitorGroupDetail monitorGroupDetail = (MonitorGroupDetail) FacesUtil
				.getManagedBean("monitorGroupDetail");
		
		monitorGroupDetail.setGroupId(facility.getAssociatedMonitorGroup().getGroupId());
		String ret = monitorGroupDetail.submitFromJsp();
		
		return ret;
	}

	// Get all the limits for the Facility
	public final String getFacilityCemComLimits() {
		staging = false;
		initializeFacilityCemComLimitList();
		return "facilities.profile.facilityCemComLimits";
	}

	public TableSorter getPeriodicLimitTrendWrapper() {
		return periodicLimitTrendWrapper;
	}

	public TableSorter getAuditLimitTrendWrapper() {
		return auditLimitTrendWrapper;
	}

	public String getTrendRptLimId() {
		return trendRptLimId;
	}

	public void setTrendRptLimId(String trendRptLimId) {
		this.trendRptLimId = trendRptLimId;
	}

	public String getTrendRptMonId() {
		return trendRptMonId;
	}

	public void setTrendRptMonId(String trendRptMonId) {
		this.trendRptMonId = trendRptMonId;
	}
	
	public Integer getTrendRptMonitorId() {
		return trendRptMonitorId;
	}

	public void setTrendRptMonitorId(Integer trendRptMonitorId) {
		this.trendRptMonitorId = trendRptMonitorId;
	}

	public String getTrendRptLimitDesc() {
		return trendRptLimitDesc;
	}

	public void setTrendRptLimitDesc(String trendRptLimitDesc) {
		this.trendRptLimitDesc = trendRptLimitDesc;
	}

	public String getTrendRptMonitorDetails() {
		return trendRptMonitorDetails;
	}

	public void setTrendRptMonitorDetails(String trendRptMonitorDetails) {
		this.trendRptMonitorDetails = trendRptMonitorDetails;
	}
	
	public Integer getTrendRptCorrLimitId() {
		return trendRptCorrLimitId;
	}

	public void setTrendRptCorrLimitId(Integer trendRptCorrLimitId) {
		this.trendRptCorrLimitId = trendRptCorrLimitId;
	}

	public void showPeriodicLimitTrendReport() {
		// get the associated monitor detail info so that it can
		// be displayed on the report popup
		if(null != trendRptMonitorId) {
			try {
				ContinuousMonitor cm = getContinuousMonitorService()
						.retrieveContinuousMonitor(trendRptMonitorId);
				if (null != cm) {
					setTrendRptMonitorDetails(cm.getMonitorDetails());
				}
				// get the limit trend data for the given limit
				getPeriodicLimitTrendData(trendRptCorrLimitId, null);

				String url = "../facilities/periodicLimitTrendReport.jsf";
				FacesUtil.startModelessDialog(url, 700, 900);
			} catch (RemoteException re) {
				DisplayUtil
						.displayError("Could not retrieve continuous monitor : " + trendRptMonId);
				handleException(re);
			}
		}
	}
	
	public void showAuditLimitTrendReport() {
		// get the associated monitor detail info so that it can
		// be displayed on the report popup
		if(null != trendRptMonitorId) {
			try {
				ContinuousMonitor cm = getContinuousMonitorService()
						.retrieveContinuousMonitor(trendRptMonitorId);
				if (null != cm) {
					setTrendRptMonitorDetails(cm.getMonitorDetails());
				}
				// get the limit trend data for the given limit
				getAuditLimitTrendData(trendRptCorrLimitId, null);
				
				String url = "../facilities/auditLimitTrendReport.jsf";
				FacesUtil.startModelessDialog(url, 700, 900);
			} catch (RemoteException re) {
				DisplayUtil
						.displayError("Could not retrieve continuous monitor : " + trendRptMonId);
				handleException(re);
			}
		}
	}
	
	public void showPeriodicFacilityTrendReport() {
		String url = "../facilities/periodicFacilityTrendReport.jsf";
		getPeriodicLimitTrendData(null, facilityId);
		FacesUtil.startModelessDialog(url, 900, 1025);
	}
	
	public void showAuditOverallFacilityTrendReport() {
		String url = "../facilities/auditOverallFacilityTrendReport.jsf";
		getAuditLimitTrendData(null, facilityId);
		FacesUtil.startModelessDialog(url, 900, 1050);
	}
	
	public void closeTrendReportDialog() {
		setTrendRptLimId(null);
		setTrendRptLimitDesc(null);
		setTrendRptMonId(null);
		setTrendRptMonitorDetails(null);
		setTrendRptMonitorId(null);
		setTrendRptCorrLimitId(null);
		closeDialog();
	}
	
	private void getPeriodicLimitTrendData(Integer corrLimitId, String facilityId) {
		periodicLimitTrendWrapper = new TableSorter();
		ArrayList<LimitTrendReportLineItem> ret = new ArrayList<LimitTrendReportLineItem>();
		try {
			logger.debug("Retrieving limit trend for correleated limit id = "
					+ corrLimitId + " facility id = " + facilityId);
			ret = getFacilityService().retrievePeriodicLimitTrendData(corrLimitId, facilityId);
			if (null != ret) {
				periodicLimitTrendWrapper.setWrappedData(ret);
			}
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Failed to retrieve limit trend data.");
			handleException(re);
		}
	}
	
	private void getAuditLimitTrendData(Integer corrLimitId, String facilityId) {
		auditLimitTrendWrapper = new TableSorter();
		ArrayList<LimitTrendReportLineItem> ret = new ArrayList<LimitTrendReportLineItem>();
		try {
			logger.debug("Retrieving limit trend for correleated limit id = "
					+ corrLimitId + " facility id = " + facilityId);
			ret = getFacilityService().retrieveLimitTrendData(corrLimitId, facilityId);
			if (null != ret) {
				auditLimitTrendWrapper.setWrappedData(ret);
			}
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Failed to retrieve limit trend data.");
			handleException(re);
		}
	}
	
	public Integer getTrendRptComplianceReportId() {
		return trendRptComplianceReportId;
	}

	public void setTrendRptComplianceReportId(Integer trendRptComplianceReportId) {
		this.trendRptComplianceReportId = trendRptComplianceReportId;
	}

	public String showAssociatedComplianceReport() {
		String ret = null;
		if(null != trendRptComplianceReportId) {
			logger.debug("Navigating to compliance report id " + trendRptComplianceReportId);
			ComplianceReports cr = (ComplianceReports)FacesUtil.getManagedBean("complianceReport");
			cr.setReportId(trendRptComplianceReportId);
			cr.setFromTODOList(false);
			ret = cr.viewDetail();
		}
		
		return ret;
	}
	
	public final String getFromFacility() {
        return fromFacility;
    }

    public final void setFromFacility(String fromFacility) {
        this.fromFacility = fromFacility;
    }
	
}
