package us.wy.state.deq.impact.webcommon.monitoring;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.model.SelectItem;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.def.AdditionalReportAttachmentTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.MonitorReportTypeDef;
import us.oh.state.epa.stars2.def.ReportAcceptedStatusDef;
import us.oh.state.epa.stars2.def.ReportComplianceStatusDef;
import us.oh.state.epa.stars2.def.ReportReviewStatusDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.bo.ReadWorkFlowService;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;

@SuppressWarnings("serial")
public class MonitorReportDetail extends TaskBase implements
IAttachmentListener {

	private String pageRedirect;

	public static final String MONITOR_REPORT_OUTCOME = "dialog:reportDetail";
	public static final String HOME_MONITOR_REPORT_OUTCOME = "dialog:homeReportDetail";
	public static final String EDIT_ATTACHMENT_DIALOG = "dialog:editReportAttachment";
	public static final String MONITOR_REPORT_DETAIL = "monitoring.monitorReportDetail";

	private MonitorReport monitorReport;
	private MonitorGroupDetail monitorGroupDetail;

	Integer monitorReportId;
	String monitorGroupId;
	String facilityId;

	private MonitoringService monitoringService;
	private DocumentService documentService;
	private ReadWorkFlowService workFlowService;
	private FacilityService facilityService;

	// local attachments bean. we don't want to use the attachments managed bean
	// since
	// it is already in use by the monitor group detail. trying to use that
	// managed
	// bean causes the attachments at the monitor group to be replaced by the
	// attachments
	// of the current report (only on the UI not in the DB) causing confusion.
	private Attachments attachments;

	private Attachment curAttachment;

	private boolean editable = false;

	private boolean staging = false;

	public MonitorReport getMonitorReport() {
		return monitorReport;
	}

	public void setMonitorReport(MonitorReport monitorReport) {
		this.monitorReport = monitorReport;
	}

	public Integer getMonitorReportId() {
		return monitorReportId;
	}

	public void setMonitorReportId(Integer monitorReportId) {
		this.monitorReportId = monitorReportId;
	}

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	public DocumentService getDocumentService() {
		return documentService;
	}

	public void setDocumentService(DocumentService documentService) {
		this.documentService = documentService;
	}

	public ReadWorkFlowService getWorkFlowService() {
		return workFlowService;
	}

	public void setWorkFlowService(ReadWorkFlowService workFlowService) {
		this.workFlowService = workFlowService;
	}
	
	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public MonitorGroupDetail getMonitorGroupDetail() {
		return monitorGroupDetail;
	}

	public void setMonitorGroupDetail(MonitorGroupDetail monitorGroupDetail) {
		this.monitorGroupDetail = monitorGroupDetail;
	}

	public boolean isStaging() {
		return staging;
	}

	public void setStaging(boolean staging) {
		this.staging = staging;
	}

	public Attachment getCurAttachment() {
		return curAttachment;
	}

	public void setCurAttachment(Attachment curAttachment) {
		this.curAttachment = curAttachment;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String refresh() {
		if (null != getMonitorReport()) {
			return MONITOR_REPORT_DETAIL;
		} else {
			return null;
		}
	}

	public String getPageRedirect() {
		return pageRedirect;
	}

	public void setPageRedirect(String pageRedirect) {
		this.pageRedirect = pageRedirect;
	}

	public Attachments getAttachments() {
		return attachments;
	}

	public void setAttachments(Attachments attachments) {
		this.attachments = attachments;
	}

	public String getMonitorGroupId() {
		return monitorGroupId;
	}

	public void setMonitorGroupId(String monitorGroupId) {
		this.monitorGroupId = monitorGroupId;
	}
	
	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public final String startAddReport() {
		boolean ok = true;
		// boolean staging = true;

		setMonitorReport(new MonitorReport());
		getMonitorReport().setMonitorGroupId(getMonitorGroupDetail()
				.getMonitorGroup().getGroupId());
		try {
			if (isInternalApp()) {
				getMonitorReport().setCreatedById(InfrastructureDefs.getCurrentUserId());
			} else {
				getMonitorReport().setCreatedById(CommonConst.GATEWAY_USER_ID);
			}
			setMonitorReport(getMonitoringService().createMonitorReport(
					monitorReport));
			// get the updated report
			setMonitorReport(getMonitoringService().retrieveMonitorReport(
					getMonitorReport().getReportId(), isStaging()));
			monitorGroupId = getMonitorGroupDetail().getMonitorGroup()
					.getMgrpId();
			DisplayUtil.displayInfo("Monitor report created successfully");
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to create monitor report");
			ok = false;
		}

		if (ok) {
			initializeAttachmentBean();
			getMonitorGroupDetail().refreshAssociatedAmbientMonitorReports();
			setFromTODOList(false);
			enterEditMode();
			return MONITOR_REPORT_OUTCOME;
		} else {
			return null;
		}

	}

	public final String startEditReport() {
		enterEditMode();
		return null; // stay on the same page
	}

	public final String startViewReport() {
		initializeAttachmentBean();
		setFromTODOList(false);
		monitorGroupId = getMonitorGroupDetail().getMonitorGroup().getMgrpId();
		if (isInternalApp()) {
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_monitorReportDetail"))
					.setDisabled(false);
		}
		return MONITOR_REPORT_OUTCOME;
	}

	public final String startViewReportHome() {
		startViewReport();
		return HOME_MONITOR_REPORT_OUTCOME;
	}

	public final String validateReport() {
		boolean ret = false;
		if (isValidReport()) {
			getMonitorReport().setValidated(true);
			// update the report
			try {
				if (getMonitoringService().modifyMonitorReport(getMonitorReport())) {
					// get the updated report
					setMonitorReport(getMonitoringService()
							.retrieveMonitorReport(getMonitorReport().getReportId(),
									isStaging()));
					getMonitorGroupDetail().refreshAssociatedAmbientMonitorReports();
					ret = true;
				}
			} catch (Exception ex) {
				DisplayUtil.displayError("Failed to update monitor report");
			}
		}

		if (ret) {
			DisplayUtil.displayInfo("Report validation successful");
		} else {
			DisplayUtil.displayError("Report validation failed");
		}

		return null; // stay on the same page
	}

	public String saveReport() {
		boolean ok = false;

		// invalidate the report if it is not submitted
		if (!getMonitorReport().isSubmitted()) {
			getMonitorReport().setValidated(false);
		}

		// validations
		if (getMonitorReport().isSubmitted()) {
			boolean ko = false;
			if (!Utility.isNullOrZero(getMonitorReport().getComplianceReviewerId())
					&& null == getMonitorReport().getComplianceReviewDate()) {
				ko = true;
				DisplayUtil
				.displayError("Input required for Compliance Review Date when Compliance Reviewer is set");

			} else if (null != getMonitorReport().getComplianceReviewDate()
					&& Utility.isNullOrZero(getMonitorReport()
							.getComplianceReviewerId())) {
				ko = true;
				DisplayUtil
				.displayError("Input required for Compliance Reviewer when Compliance Review Date is set");
			}

			if (!Utility.isNullOrZero(getMonitorReport().getMonitoringReviewerId())
					&& null == getMonitorReport().getMonitoringReviewDate()) {
				ko = true;
				DisplayUtil
				.displayError("Input required for Monitor Review Date when Monitor Reviewer is set");
			} else if (null != getMonitorReport().getMonitoringReviewDate()
					&& Utility.isNullOrZero(getMonitorReport()
							.getMonitoringReviewerId())) {
				ko = true;
				DisplayUtil
				.displayError("Input required for Monitor Reviewer when Monitor Review Date is set");
			}

			if (ko) {
				return null; // stay on the same page
			}
		}

		try {
			if (getMonitoringService().modifyMonitorReport(getMonitorReport())) {
				// get the updated report
				setMonitorReport(getMonitoringService().retrieveMonitorReport(
						getMonitorReport().getReportId(), isStaging()));
				ok = true;
			}
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to update monitor report");
		}

		if (ok) {
			getMonitorGroupDetail().refreshAssociatedAmbientMonitorReports();
			leaveEditMode();
		} else {
			DisplayUtil.displayError("Failed to update monitor report");
		}

		return null; // stay on the same page
	}

	public String cancel() {
		leaveEditMode();
		try {
			setMonitorReport(getMonitoringService().retrieveMonitorReport(
					getMonitorReport().getReportId(), getMonitorReport().isStaging()));
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to retrieve monitor report");
		}

		return null; // stay on the same page
	}
	
	// used with popup monitorReport.jsp
	public String submitReport() {
		
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}
		
		boolean ok = false;
		
		ok = submitReportI();
		
		if (ok) {
			getMonitorGroupDetail().submitFromJsp();
			FacesUtil.returnFromDialogAndRefresh();
		}
		
		clearButtonClicked();
		return null; // stay on the same page
	}
	
	// used with non-popup monitorReportDetail.jsp
	public String submitReport2() {
		boolean ok = false;

		// First, refresh the Monitor Group, in case the current monitor group
		// is not the monitor group associated with the monitor report.
		// This will ensure that the correct facility is associated with the 
		// monitor report workflow.
		getMonitorGroupDetail().setGroupId(getMonitorReport().getMonitorGroupId());
		getMonitorGroupDetail().submit(false); // Retrieve new object 

		ok = submitReportI();

		if (ok) {

			// refresh the associated ambient monitor reports since one of the monitor reports has been submitted.
			getMonitorGroupDetail().refreshAssociatedAmbientMonitorReports();

			return SUCCESS;
		} else {
			return null; // stay on the same page
		}
	}

	public boolean submitReportI() {
		boolean ok = false;
		try {
			Integer assignedUserId = getMonitorGroupDetail().getMonitorGroup()
					.getMonitorReviewerId();
			
			// Set current fpId in monitorReport so it can be used in workflow
			// createProcess.
			facilityId = getMonitorGroupDetail().getMonitorGroup()
					.getFacilityId();
			if (!Utility.isNullOrEmpty(facilityId)) {
				// logger.debug("facilityId = " + facilityId);
				Facility fp = null;
				try {
					fp = facilityService.retrieveFacility(facilityId);
				} catch (Exception ex) {
					DisplayUtil.displayError("Failed to retrieve facility.");
				}
				Integer currentFpId = fp.getFpId();
				// logger.debug("currentFpId = " + currentFpId);
				getMonitorReport().setCurrentFpId(currentFpId);
			} else {
				getMonitorReport().setCurrentFpId(null);
			}
			// END Set current fpId in monitorReport 
			
			setMonitorReport(getMonitoringService().submitReport(monitorReport,
					assignedUserId));
			if (null != getMonitorReport()) {
				ok = true;
				DisplayUtil
				.displayInfo("Monitor report submitted successfully");
			} else {
				DisplayUtil.displayError("Monitor report submission failed");
			}
		} catch (Exception ex) {
			ok = false;
			DisplayUtil.displayError("Monitor report submission failed");
		}
		
		return ok;
	}

	public String close() {
		if (isInternalApp()) {
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_monitorReportDetail"))
					.setDisabled(false);
		}
		getMonitorGroupDetail().submitFromJsp();
		FacesUtil.returnFromDialogAndRefresh();
		return null;
	}

	// used with popup monitorReport.jsp confirm window
	public void deleteReport(ReturnEvent returnEvent) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}

		deleteMonitorReportI();

		getMonitorGroupDetail().refreshAssociatedAmbientMonitorReports();
		FacesUtil.returnFromDialogAndRefresh();
	}

	// Used with non-popup monitorReportDetail.jsp
	public void deleteMonitorReport() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		
		// First, refresh the Monitor Group, in case the current monitor group
		// is not the monitor group associated with the monitor report.
		// This will ensure that the correct facility is associated with the 
		// monitor report workflow.
		getMonitorGroupDetail().setGroupId(getMonitorReport().getMonitorGroupId());
		getMonitorGroupDetail().submit(false); // Retrieve new object in case current
											// group is not the group associated
											// with the report.

		deleteMonitorReportI();

		getMonitorGroupDetail().refreshAssociatedAmbientMonitorReports();
		getMonitorGroupDetail().setPopupRedirectOutcome(
				MonitorGroupDetail.DETAIL_OUTCOME);

		clearButtonClicked();
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public void deleteMonitorReportI() {
		try {
			getMonitoringService().deleteMonitorReport(getMonitorReport(), isPortalApp());
			setMonitorReport(null);
			DisplayUtil.displayInfo("Monitor Report successfully deleted.");
			if (isInternalApp()) {
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_monitorReportDetail"))
						.setDisabled(true);
			}
		} catch (DAOException re) {
			handleException(
					"Failed to delete Monitor Report: "
							+ getMonitorReport().getMrptId(), re);
			DisplayUtil.displayError("Failed to delete Monitor Report: "
					+ getMonitorReport().getMrptId());
		}
		return;
	}
	
	// used with popup monitorReport.jsp
	public final void closeDialog(ReturnEvent re) {
		getMonitorGroupDetail().refreshAssociatedAmbientMonitorReports();
		
		SubmitTask submitTask = (SubmitTask)FacesUtil.getManagedBean("submitTask");
		if(null != submitTask 
				&& submitTask.isCompleted()) {
			// if the report task was submitted successfully then navigate to the home tab
			// otherwise stay on the same page i.e., report detail
			FacesUtil.returnFromDialogAndRefresh();
		}
	}
	
	// Used with non-popup monitorReportDetail.jsp and deleteMonitorReport.jsp
	public final void closeDialog() {
		getMonitorGroupDetail().refreshAssociatedAmbientMonitorReports();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void enterEditMode() {
		editable = true;
	}

	public void leaveEditMode() {
		editable = false;
	}

	public final List<SelectItem> getMonitorReportTypeDef() {
		return MonitorReportTypeDef.getData().getItems()
				.getItems(getParent().getValue());
	}

	public final List<SelectItem> getReportAcceptedStatusDef() {
		return ReportAcceptedStatusDef.getData().getItems()
				.getItems(getParent().getValue());
	}

	public final List<SelectItem> getReportReviewStatusDef() {
		return ReportReviewStatusDef.getData().getItems()
				.getItems(getParent().getValue());
	}

	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();
		MonitorGroup monitorGroup = null;
		Integer monitorGroupId = getMonitorReport().getMonitorGroupId();

		try {
			monitorGroup = getMonitoringService().retrieveMonitorGroup(
					monitorGroupId);
		} catch (DAOException daoe) {
			logger.error(daoe.getMessage());
			DisplayUtil
			.displayError("Failed to retrieve monitor group with id: "
					+ monitorGroupId);
		}

		if (null != monitorGroup) {
			if (null != monitorGroup.getFacilityId()) {
				// check for compliance reviewer id and compliance review date
				// if the
				// monitor group is associated with a facility
				if (Utility.isNullOrZero(getMonitorReport()
						.getComplianceReviewerId())) {
					valMsgs.add(new ValidationMessage("MonitoringReport",
							"Compliance reviewer must be filled.",
							ValidationMessage.Severity.ERROR,
							MONITOR_REPORT_DETAIL));
				}

				if (null == getMonitorReport().getComplianceReviewDate()) {
					valMsgs.add(new ValidationMessage("MonitoringReport",
							"Compliance review date must be filled.",
							ValidationMessage.Severity.ERROR,
							MONITOR_REPORT_DETAIL));
				}
			}

			// check for monitor reviewer and monitor reviewer id
			if (Utility.isNullOrZero(getMonitorReport().getMonitoringReviewerId())) {
				valMsgs.add(new ValidationMessage("MonitoringReport",
						"Monitor reviewer must be filled.",
						ValidationMessage.Severity.ERROR, MONITOR_REPORT_DETAIL));
			}

			if (null == getMonitorReport().getMonitoringReviewDate()) {
				valMsgs.add(new ValidationMessage("MonitoringReport",
						"Monitor review date must be filled.",
						ValidationMessage.Severity.ERROR, MONITOR_REPORT_DETAIL));
			}
		} else {
			valMsgs.add(new ValidationMessage("MonitoringReport",
					"Failed to retrieve associated monitor group "
							+ monitorGroupId, ValidationMessage.Severity.ERROR,
							null));
		}

		return valMsgs;
	}

	@Override
	public String findOutcome(String url, String ret) {
		return MONITOR_REPORT_DETAIL;
	}

	@Override
	public Integer getExternalId() {
		return getMonitorReportId();
	}

	@Override
	public void setExternalId(Integer externalId) {
		try {
			setMonitorReport(getMonitoringService().retrieveMonitorReport(
					externalId));
			MonitorGroup monitorGroup = getMonitoringService()
					.retrieveMonitorGroup(getMonitorReport().getMonitorGroupId());
			monitorGroupId = monitorGroup.getMgrpId();
		} catch (DAOException daoe) {
			logger.error(daoe.getMessage());
			DisplayUtil
			.displayError("Failed to retrieve ambient monitoring report with id "
					+ externalId);
			return;
		}

		setMonitorReportId(externalId);
		initializeAttachmentBean();
		((SimpleMenuItem) FacesUtil
				.getManagedBean("menuItem_monitorReportDetail"))
				.setDisabled(false);
	}

	public final boolean getSubmitAllowed() {
		if (isPublicApp()) {
			return false;
		} else if (isInternalApp() && isReadOnlyUser()) {
			return false;
		} else {
			if (getMonitorReport() == null) {
				return false;
			} else {
				return !isEditable() && getMonitorReport().isValidated()
						&& !getMonitorReport().isSubmitted()
						&& (isInternalApp() || getMonitorReport().isStaging());
			}
		}

	}

	public final boolean getValidateAllowed() {
		if (isPublicApp()) {
			return false;
		} else if (isInternalApp() && isReadOnlyUser()) {
			return false;
		} else {
			if (getMonitorReport() == null) {
				return false;
			} else {
				return !isEditable()
						&& !getMonitorReport().isSubmitted()
						&& (!isStaging() || (isStaging() && getMonitorReport()
								.isStaging()));
			}
		}
	}

	public void initializeAttachmentBean() {
		// Attachments attachments = (Attachments)
		// FacesUtil.getManagedBean("attachments");
		if (null == attachments) {
			attachments = new Attachments();
			attachments.setDocumentService(documentService);
		}

		attachments.addAttachmentListener(this);

		if (null != getMonitorReport().getReportId()) {
			attachments.setSubPath("MonitorGroup" + File.separator
					+ getMonitorReport().getMonitorGroupId() + File.separator
					+ "MonitorReport" + File.separator
					+ getMonitorReport().getReportId());
		} else {
			attachments.setSubPath("MonitorGroup");
		}

		attachments.setAttachmentTypesDef(AdditionalReportAttachmentTypeDef
				.getData().getItems());

		boolean attachmentsLink = !isReadOnlyUser();
		if (isPublicApp()){
		attachmentsLink = false;
		} 
		attachments.setNewPermitted(attachmentsLink);
		attachments.setUpdatePermitted(attachmentsLink);
		attachments.setDeletePermitted(attachmentsLink);

		if (isPublicApp()) {
			attachments.setStaging(false);
			attachments.setTradeSecretSupported(false);
		} else if (isInternalApp()) {
			attachments.setStaging(false);
		} else if (isPortalApp()) {
			attachments.setStaging(true);
		}
		attachments.setHasDocType(true);

		try {
			attachments.setAttachmentList(getMonitoringService()
					.retrieveMonitorReportAttachments(
							getMonitorReport().getReportId(), getMonitorReport().isStaging()));
		} catch (DAOException daoe) {
			handleException(daoe);
			DisplayUtil
			.displayError("Cannot access ambient monitor report attachments");
		}
	}

	@Override
	public AttachmentEvent createAttachment(Attachments attachment)
			throws AttachmentException {
		boolean ok = true;
		Integer reportId = getMonitorReport().getReportId();

		if (attachment.getDocument() == null) {
			// should never happen
			logger.error("Attempt to process null attachment");
			ok = false;
		} else {
			// make sure document description and type are provided
			if (attachment.getDocument().getDescription() == null
					|| attachment.getDocument().getDescription().trim()
					.equals("")) {
				DisplayUtil
				.displayError("Please specify the description for this attachment");
				ok = false;
			}
			if (attachment.getDocument().getDocTypeCd() == null
					|| attachment.getDocument().getDocTypeCd().trim()
					.equals("")) {
				DisplayUtil
				.displayError("Please specify an attachment type for this attachment");
				ok = false;
			}

			if (attachment.getFileToUpload() == null) {
				DisplayUtil
				.displayError("Please specify the file to upload for this attachment");
				ok = false;
			}
		}

		if (ok) {
			try {
				attachment.getTempDoc().setObjectId(null);
				getMonitoringService().createMonitorReportAttachment(reportId,
						attachment.getTempDoc(),
						attachment.getFileToUpload().getInputStream());
				attachment
				.setAttachmentList(getMonitoringService()
						.retrieveMonitorReportAttachments(reportId,
								isStaging()));
				getMonitorReport().setAttachments(attachment.getAttachmentList());
			} catch (DAOException daoe) {
				handleException(daoe);
				DisplayUtil
				.displayError("Create monitor report attachment failed");
			} catch (IOException ioe) {
				handleException(new RemoteException(ioe.getMessage()));
				DisplayUtil
				.displayError("Create monitor report attachment failed");
			}

			FacesUtil.returnFromDialogAndRefresh();
		}
		return null;
	}

	@Override
	public AttachmentEvent updateAttachment(Attachments attachment) {
		boolean ok = true;
		Integer reportId = getMonitorReport().getReportId();

		// make sure document description is provided
		if (attachment.getDocument().getDescription() == null
				|| attachment.getDocument().getDescription().trim().equals("")) {
			DisplayUtil
			.displayError("Please specify the description for this attachment");
			ok = false;
		}

		if (ok) {
			try {
				getMonitoringService().updateMonitorReportAttachment(
						attachment.getTempDoc());
				attachment
				.setAttachmentList(getMonitoringService()
						.retrieveMonitorReportAttachments(reportId,
								getMonitorReport().isStaging()));
				getMonitorReport().setAttachments(attachment.getAttachmentList());
			} catch (DAOException daoe) {
				handleException(daoe);
				DisplayUtil
				.displayError("Update monitor report attachment failed");
			}

			FacesUtil.returnFromDialogAndRefresh();
		}

		return null;
	}

	@Override
	public AttachmentEvent deleteAttachment(Attachments attachment) {
		Integer reportId = getMonitorReport().getReportId();
		try {
			getMonitoringService().removeMonitorReportAttachment(
					attachment.getTempDoc(), isPortalApp());
			attachment.setAttachmentList(getMonitoringService()
					.retrieveMonitorReportAttachments(reportId, getMonitorReport().isStaging()));
			getMonitorReport().setAttachments(attachment.getAttachmentList());
		} catch (DAOException daoe) {
			handleException(daoe);
			DisplayUtil.displayError("Delete monitor report attachment failed");
		}

		FacesUtil.returnFromDialogAndRefresh();

		return null;
	}

	@Override
	public void cancelAttachment() {
		Attachments attachments = (Attachments) FacesUtil
				.getManagedBean("attachments");
		Integer reportId = getMonitorReport().getReportId();
		try {
			attachments.setAttachmentList(getMonitoringService()
					.retrieveMonitorReportAttachments(reportId, getMonitorReport().isStaging()));
		} catch (DAOException daoe) {
			handleException(daoe);
			DisplayUtil
			.displayError("Refreshing monitor report attachment(s) failed");
		}

		FacesUtil.returnFromDialogAndRefresh();

	}

	public final String startEditAttachment() {
		if (null != attachments) {
			attachments.startEditAttachment();
			return EDIT_ATTACHMENT_DIALOG;
		} else {
			return null;
		}
	}

	public final String startNewAttachment() {
		if (null != attachments) {
			curAttachment = null;
			attachments.startNewAttachment();
			return EDIT_ATTACHMENT_DIALOG;
		} else {
			return null;
		}
	}

	public final Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}

	public final Timestamp getMinDate() {
		Timestamp reportSubmittedDate = getMonitorReport().getSubmittedDate();
		if (null != reportSubmittedDate) {
			// set the time to 00:00:00.000
			// this is necessary to allow the user to pick the same date as
			// submitted date
			// for compliance and monitor review dates
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(reportSubmittedDate.getTime());
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			return new Timestamp(cal.getTimeInMillis());
		} else {
			return null;
		}
	}

	public final void returnFirstSelected(ReturnEvent re) {
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}

	public boolean isActiveWorkFlowProcessExist() {
		Integer processTemplateId = 300; // template id of monitoring report
		// review wf process
		Integer externalId = getMonitorReport().getReportId();

		try {
			return null == workFlowService.findActiveProcessByExternalObject(
					processTemplateId, externalId) ? false : true;
		} catch (RemoteException e) {
			String errMsg = "Error occurred while checking if an active "
					+ "workflow process exists for this ambient monitor report; "
					+ "report deletion will not be allowed.";
			logger.error("error while checking for active wf process: ", e);
			DisplayUtil.displayError(errMsg);
			handleException(e);
			return true;
		}
	}

	public final DefSelectItems getComplianceStatusDef() {
		return ReportComplianceStatusDef.getData().getItems();
	}

	public final boolean isEditAllowed() {
		boolean ret = false;
		
		if (isPublicApp()) {
			return false;
		}
		
		if(isStars2Admin()) {
			// admin user
			return true;
		}
		
		if(isInternalApp()
				&& !isReadOnlyUser()
				&& !getMonitorReport().isSubmitted()) {
			return true;
		}
		
		if(isPortalApp()
				&& getMonitorReport().isStaging()) {
			// report created on the external system
			return true;
		}
		
		return ret;
	}

	public final boolean isEditAllowedbyAqd() {
		return (isStars2Admin() || (isInternalApp() && !isReadOnlyUser()));
	}

	public final boolean isAttachmentEditAllowed() {
		if (isPublicApp()) {
			return false;
		}
		if (isReadOnlyUser()) {
			return false;
		}

		if (isStars2Admin()) {
			return true;
		}
		
		if (null != curAttachment) {
			// editing existing attachment
			if(isPortalApp()) {
				// portal app - allow editing of only staging reports
				return getMonitorReport().isStaging() ? true : false;
			} else {
				// internal app
				if (null != getMonitorReport().getSubmittedDate()) {
					// report is submitted in which case allow to edit only those
					// attachments that were uploaded after the report was submitted
					long submittedTimeLong = getMonitorReport().getSubmittedDate()
							.getTime();
					long docUploadTimeLong = curAttachment.getUploadDateLong();
					
					return docUploadTimeLong <= submittedTimeLong ? false : true;
				} else {
					// allow editing for un-submitted report
					return true;
				}
			} 
		} else {
			// new attachment
			return true;
		}
	}

	public final boolean isDeleteAllowed() {
		
		if (isPublicApp()) {
			return false;
		}
		// NOTE: This is a kludge.
		// monitorReport is null after a delete and we return from dialog
		if (getMonitorReport() == null) {
			return false;
		}
		
		if(isInternalApp() && isReadOnlyUser()) {
			return false;
		} else if (isInternalApp() && !isStars2Admin() && isSubmittedFromPortal()) {
			return false;
		} else if(isInternalApp() && isMonitorReportUploadUser() && getMonitorReport().isSubmitted()) {
			return false;
		} else {
			return !isEditable() && ((!isStaging()) || 
					(!getMonitorReport().isSubmitted() && getMonitorReport().isStaging()));
		}	
	}

	@Override
	public String getExternalName(ProcessActivity activity) {
		return "Ambient Monitor Report";
	}

	@Override
	public String getExternalNum() {
		return getMonitorReport() != null ? getMonitorReport().getMrptId() : null;
	}

	@Override
	public String toExternal() {
		return MONITOR_REPORT_DETAIL;
	}
	
	public final boolean getDisplayEditBtn() {
		if (isPublicApp()) {
			return false;
		}
		// NOTE: This is a kludge.
		// monitorReport is null after a delete and we return from dialog
		if (getMonitorReport() == null) {
			return false;
		}
				
		if(isInternalApp()) {
			if((isMonitorReportUploadUser() && getMonitorReport().isSubmitted()) || isReadOnlyUser()) {
				return false;
			} else {
				return !editable;
			}	
		} else {
			return !editable && getMonitorReport().isStaging();
		}
	}
	
	public final boolean isAddAttachmentAllowed() {
		if (isPublicApp()) {
			return false;
		} else if(isInternalApp() && !isReadOnlyUser()) {
			return true;
		} else {
			return getMonitorReport().isStaging();
		}
		
	}
	
	public boolean isValidReport() {
		boolean ret = true;

		if (null != getMonitorReport()) {

			if (Utility.isNullOrEmpty(getMonitorReport().getReportTypeCd())) {
				DisplayUtil
				.displayError("Input required for attribute Report Type");
				ret = false;
			}

			if (!Utility.isNullOrEmpty(getMonitorReport().getReportTypeCd())
					&& getMonitorReport().getReportTypeCd().equalsIgnoreCase(
							MonitorReportTypeDef.QUARTERLY)
							&& Utility.isNullOrZero(getMonitorReport().getQuarter())) {
				DisplayUtil
				.displayError("Input required for attribute Quarter when Report Type is Quarterly");
				ret = false;
			}

			if (Utility.isNullOrEmpty(getMonitorReport().getDescription())) {
				DisplayUtil
				.displayError("Input required for attribute Description");
				ret = false;
			}

			if (!Utility.isNullOrEmpty(getMonitorReport().getReportTypeCd())
					&& (getMonitorReport().getReportTypeCd().equalsIgnoreCase(
							MonitorReportTypeDef.ANNUAL) || getMonitorReport()
							.getReportTypeCd().equalsIgnoreCase(
									MonitorReportTypeDef.QUARTERLY))) {

				if (Utility.isNullOrZero(getMonitorReport().getYear())) {
					DisplayUtil
					.displayError("Input required for attribute Year when Report Type is Annual or Quarterly");
					ret = false;
				}

				// validate required attachments for non-legacy annual and
				// quarterly report type
				if (!getMonitorReport().isLegacyReport()) {
					boolean dataSummaryRptAttachmentFound = false;
					boolean rawDataFileAttachmentFound = false;
					boolean precisionAccuracyAttachmentFound = false;

					if (null != attachments) {
						for (Attachment attachment : attachments
								.getAttachmentList()) {
							if (attachment
									.getDocTypeCd()
									.equalsIgnoreCase(
											AdditionalReportAttachmentTypeDef.DATA_SUMMARY_REPORT)) {
								dataSummaryRptAttachmentFound = true;
							} else if (attachment
									.getDocTypeCd()
									.equalsIgnoreCase(
											AdditionalReportAttachmentTypeDef.RAW_DATA_FILE)) {
								rawDataFileAttachmentFound = true;
							} else if (attachment
									.getDocTypeCd()
									.equalsIgnoreCase(
											AdditionalReportAttachmentTypeDef.PRECISION_AND_ACCURACY)) {
								precisionAccuracyAttachmentFound = true;
							}
						}
					}

					if (!dataSummaryRptAttachmentFound) {
						DisplayUtil
						.displayWarning(AdditionalReportAttachmentTypeDef.getData()
								.getItems().getItemDesc(AdditionalReportAttachmentTypeDef.DATA_SUMMARY_REPORT)
								+ " attachment is not present");
						
					}

					if (!rawDataFileAttachmentFound) {
						DisplayUtil
						.displayWarning(AdditionalReportAttachmentTypeDef.getData()
								.getItems().getItemDesc(AdditionalReportAttachmentTypeDef.RAW_DATA_FILE)
								+ " attachment is not present");
					}

					if (!precisionAccuracyAttachmentFound) {
						DisplayUtil
						.displayWarning(AdditionalReportAttachmentTypeDef.getData()
								.getItems().getItemDesc(AdditionalReportAttachmentTypeDef.PRECISION_AND_ACCURACY)
								+ " attachment is not present");
					}
				}
			}

		} else {
			ret = false;
		}

		return ret;
	}
	
	public String showAssociatedGroup() {
		MonitorGroupDetail monitorGroupDetail = (MonitorGroupDetail) FacesUtil
				.getManagedBean("monitorGroupDetail");
		monitorGroupDetail.setGroupId(getMonitorReport().getMonitorGroupId());
		monitorGroupDetail.submitFromJsp();
		return SUCCESS;
	}
	
	public boolean isSubmittedFromPortal() {
		// NOTE: This is a kludge.
		// monitorReport is null after a delete and we return from dialog
		if (getMonitorReport() == null) {
			return false;
		}

		Integer userId = getMonitorReport().getCreatedById();
		if (userId != null) {
			if (userId.equals(CommonConst.GATEWAY_USER_ID)) {
				return true;
			}
		}
		return false;
	}
}
