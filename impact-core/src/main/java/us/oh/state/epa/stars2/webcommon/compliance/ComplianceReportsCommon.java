package us.oh.state.epa.stars2.webcommon.compliance;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportAttachment;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.def.ComplianceAttachmentTypeDef;
import us.oh.state.epa.stars2.def.ComplianceReportAcceptedDef;
import us.oh.state.epa.stars2.def.ComplianceReportAcceptedReviewedDef;
import us.oh.state.epa.stars2.def.ComplianceReportCategoriesTypeDef;
import us.oh.state.epa.stars2.def.ComplianceReportMonitorAndLimitAuditStatusDef;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypeDef;
import us.oh.state.epa.stars2.def.ComplianceReportTypePartialDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.YearsForOEPA;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.AppValidationMsg;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.UploadedFileInfo;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.facility.FacilityProfileBase;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;

@SuppressWarnings("serial")
public abstract class ComplianceReportsCommon extends TaskBase implements
		IAttachmentListener {

	private int reportId;
	protected ComplianceReport complianceReport;
	private Facility facility;
	private List<Integer> complianceReportIdList = null;
	protected boolean passedValidation = false;
	private String popupRedirectOutcome;
	private boolean editable = true;
	private boolean editMode = false;
	private boolean readOnlyReport = false;
	private Boolean hideTradeSecret;
	boolean blankOutPage = false; // not used much
	protected boolean complianceReportDeleted;
	protected boolean readOnly;

	private ComplianceReportService complianceReportService;
	private FacilityService facilityService;
	private InfrastructureDefs infraDefs;

	protected final static String COMPLIANCE_REPORT = "complianceDetail";
	
	
	// Attachments
	List<Document> printableDocList = null;
	List<Document> reportAttachments = null;
	
	public ComplianceReportsCommon() {
		super();
		blankOutPage = false;
	}
	
	
	public void reportYearChanged(ValueChangeEvent event) {
		if (null != getComplianceReport()) {
			getComplianceReport().setReportQuarter(null);
		}
	}

	private final int currentQtr = LocalDate.now().get(IsoFields.QUARTER_OF_YEAR);
	private final int currentYear = LocalDate.now().get(ChronoField.YEAR);

	public final LinkedHashMap<String, Integer> getYears() {
		return new LinkedHashMap<String, Integer>(getInfraDefs().getYears());
	}


	public final LinkedHashMap<String, Integer> getYearQuarters() {
		LinkedHashMap<String, Integer> yearQuarters = 
				new LinkedHashMap<String, Integer>(getInfraDefs().getYearQuarters());

		if (null != getComplianceReport()) {
			Integer reportYear = getComplianceReport().getReportYear();
			if (null != reportYear) {
				if (reportYear == currentYear) {
					if (currentQtr < 4) {
						yearQuarters.remove(String.valueOf(4));
					}
					if (currentQtr < 3) {
						yearQuarters.remove(String.valueOf(3));
					}
					if (currentQtr < 2) {
						yearQuarters.remove(String.valueOf(2));
					}
				}
			}
		}
		return yearQuarters;
	}
	
	protected boolean schemaFlag() {
		boolean useReadOnlySchema = true;

		if (isPortalApp()) {
			useReadOnlySchema = false;
		}

		return useReadOnlySchema;
	}

	public InfrastructureDefs getInfraDefs() {
		return infraDefs;
	}

	public void setInfraDefs(InfrastructureDefs infraDefs) {
		this.infraDefs = infraDefs;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public ComplianceReportService getComplianceReportService() {
		return complianceReportService;
	}

	public void setComplianceReportService(
			ComplianceReportService complianceReportService) {
		this.complianceReportService = complianceReportService;
	}

	// BEGIN OF TaskBase method implementations
	// These are just stubs. Implementations are in
	// app\compliance\ComplianceReports.java

	@Override
	public String findOutcome(String url, String ret) {
		return "complianceDetail";
	}

	@Override
	public Integer getExternalId() {
		return complianceReport.getReportId();
	}

	@Override
	public void setExternalId(Integer externalId) {
		// setReportId(externalId);
	}

	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		return messages;
	}

	// END OF TaskBase method implementations

	public ComplianceReport getComplianceReport() {
		if (complianceReport == null) {
			complianceReport = new ComplianceReport();
		}
		return complianceReport;
	}

	public void setComplianceReport(ComplianceReport complianceReport) {
		this.complianceReport = complianceReport;
	}

	public Facility getFacility() {
		return facility;
	}

	public void setFacility(Facility facility) {
		this.facility = facility;
	}

	public boolean isPassedValidation() {
		return passedValidation;
	}

	public void setPassedValidation(boolean passedValidation) {
		this.passedValidation = passedValidation;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean getEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isReadOnlyReport() {
		return readOnlyReport;
	}

	public void setReadOnlyReport(boolean readOnlyReport) {
		this.readOnlyReport = readOnlyReport;
	}

	public String edit() {
		setEditMode(true);
		if (complianceReport.getReportStatus().equalsIgnoreCase(ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT)) {
			setPassedValidation(false);
			complianceReport.setValidated(false);
		}
		return null;
	}

	public final boolean isCloneable() {
		// complianceReportIdList is set to null when facility id changes,
		// so we test for null here to see if we need to re-query for the
		// list of reports for this facility.
		if (complianceReportIdList == null) {
			complianceReportIdList = new ArrayList<Integer>();
			if (facility != null) {
				try {
					ComplianceReportList[] complianceReportList = getComplianceReportService()
							.searchComplianceReportByFacility(
									facility.getFacilityId());
					for (ComplianceReportList cr : complianceReportList) {
						if (cr.getReportType().equals(
								complianceReport.getReportType())) {
							complianceReportIdList.add(cr.getReportId());
						}
					}
				} catch (RemoteException e) {
					handleException(e);
				}
			} else {
				logger.error("Facility is null in ComplianceReports.isCloneable");
			}
		}
		return complianceReportIdList.size() > 0;
	}

	public final List<SelectItem> getComplianceReportIdList() {
		List<SelectItem> idList = new ArrayList<SelectItem>();
		if (complianceReportIdList != null) {
			for (Integer id : complianceReportIdList) {
				idList.add(new SelectItem(id, id.toString()));
			}
		}
		return idList;
	}

	public void initializeAttachmentBean() {
		Attachments a = (Attachments) FacesUtil.getManagedBean("attachments");
		a.addAttachmentListener(this);
		if (isPublicApp()) {
			a.setStaging(false);
		} else if (isInternalApp()) {
			a.setStaging(false);
		} else if (isPortalApp()) {
			a.setStaging(true);
		}

		a.setNewPermitted(!isReadOnlyUser());
		a.setUpdatePermitted(!isReadOnlyUser());
		a.setDeletePermitted(!isReadOnlyUser());

		a.setFacilityId(this.getComplianceReport().getFacilityId());
		a.setSubPath("ComplianceReports");
		if (this.getComplianceReport().getReportId() != null)
			a.setObjectId(Integer.toString(this.getComplianceReport()
					.getReportId()));
		else
			a.setObjectId("");
		a.setSubtitle(null);
		if (isPublicApp()) {
			a.setTradeSecretSupported(false);
		} else {
			a.setTradeSecretSupported(true);
		}
		a.setHasDocType(true);
		a.setAttachmentTypesDef(ComplianceAttachmentTypeDef
				.getReportyTypeAttachments(this.getComplianceReport()
						.getReportType()));
		a.setAttachmentList(complianceReport.getAttachments());
		// clean up temporary variables
		a.cleanup();
		a.setFileToUpload(null);
		a.setTsFileToUpload(null);
		a.setDocument(null);
	}

	public AttachmentEvent createAttachment(Attachments attachment) {

		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		boolean ok = true;
		if (attachment.getDocument() == null) {
			// should never happen
			logger.debug("Attempt to process null attachment");
			ok = false;
		} else {
			if (attachment.isTradeSecretAllowed()
					&& attachment.getTradeSecretAttachmentInfo() == null
					&& attachment.getTsFileToUpload() != null) {
				attachment.setTradeSecretAttachmentInfo(new UploadedFileInfo(
						attachment.getTsFileToUpload()));
			}
			if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getFileToUpload() != null) {
				attachment.setPublicAttachmentInfo(new UploadedFileInfo(
						attachment.getFileToUpload()));
			}

			// make sure document description and type are provided
			if (attachment.getTempDoc().getDescription() == null
					|| attachment.getTempDoc().getDescription().trim()
							.equals("")) {
				validationMessages.add(new ValidationMessage("description",
						"Attribute " + "Description" + " is not set.",
						ValidationMessage.Severity.ERROR, "descriptionText"));
			}

			if (attachment.getTempDoc().getDocTypeCd() == null
					|| attachment.getTempDoc().getDocTypeCd().trim().equals("")) {
				validationMessages.add(new ValidationMessage("docTypeCd",
						"Attribute " + "Attachment Type" + " is not set.",
						ValidationMessage.Severity.ERROR, "reportType"));
			}

			// make sure document file is provided
			if (attachment.getPublicAttachmentInfo() == null
					&& attachment.getTempDoc().getDocumentID() == null) {
				validationMessages.add(new ValidationMessage("documentID",
						"Please specify a Public File to Upload",
						ValidationMessage.Severity.ERROR, "publicFile"));
			}

			// make sure a justification is provided for trade secret document
			if (attachment.isTradeSecretAllowed()) {
				if (attachment.getTradeSecretAttachmentInfo() != null
						|| attachment.getTempDoc().getTradeSecretDocId() != null) {
					if (attachment.getTempDoc().getTradeSecretJustification() == null
							|| attachment.getTempDoc()
									.getTradeSecretJustification().trim()
									.equals("")) {
						validationMessages.add(new ValidationMessage(
								"tradeSecretJustification", "Attribute "
										+ "Trade Secret Justification"
										+ " is not set.",
								ValidationMessage.Severity.ERROR,
								"tradeSecretReason"));
					}
				} else if (attachment.getTempDoc()
						.getTradeSecretJustification() != null
						&& attachment.getTempDoc()
								.getTradeSecretJustification().trim().length() > 0) {
					validationMessages
							.add(new ValidationMessage(
									"tradeSecretJustification",
									"The Trade Secret Justification field should only be populated when a trade secret document is specified.",
									ValidationMessage.Severity.ERROR,
									"tradeSecretReason"));
				}
			}
		}

		if (validationMessages.size() == 0 && ok) {

			try {

				try {
					// process trade secret attachment (if there is one)
					ComplianceReportAttachment tsAttachment = null;
					InputStream tsInputStream = null;
					if (attachment.isTradeSecretAllowed()
							&& attachment.getTradeSecretAttachmentInfo() != null) {
						tsAttachment = new ComplianceReportAttachment();
						tsAttachment.setLastModifiedTS(new Timestamp(System
								.currentTimeMillis()));
						tsAttachment.setUploadDate(tsAttachment
								.getLastModifiedTS());
						tsAttachment.setExtension(DocumentUtil
								.getFileExtension(attachment
										.getTradeSecretAttachmentInfo()
										.getFileName()));
						if (isInternalApp()) {
							tsAttachment.setLastModifiedBy(InfrastructureDefs
									.getCurrentUserId());
						} else {
							tsAttachment
									.setLastModifiedBy(CommonConst.GATEWAY_USER_ID);
						}
						tsAttachment
								.setReportId(complianceReport.getReportId());
						// need object id to be set to put file in correct
						// directory
						tsAttachment.setObjectId(complianceReport.getReportId()
								.toString());
						tsAttachment.setFacilityID(complianceReport
								.getFacilityId());
						tsAttachment.setSubPath("ComplianceReports");
						if (attachment.getTradeSecretAttachmentInfo() != null) {
							tsInputStream = attachment
									.getTradeSecretAttachmentInfo()
									.getInputStream();
						}
					}
					getComplianceReportService().createComplianceAttachment(
							complianceReport,
							attachment.getTempDoc(),
							attachment.getPublicAttachmentInfo()
									.getInputStream(), tsAttachment,
							tsInputStream);
				} catch (IOException e) {
					throw new RemoteException(e.getMessage(), e);
				}

				// Save the values in potentially-editable fields so that they
				// are not lost after reading compliance report back from the
				// database. The set of fields that are editable varies,
				// depending on whether user in on portal or internal app and
				// for internal user, whether user is general user or Admin.
				boolean origLegacyFlag = complianceReport.isLegacyFlag();
				int origDapcReviewer = complianceReport.getDapcReviewer();
				String origDapcAccepted = complianceReport.getDapcAccepted();
				Timestamp origReceivedDate = complianceReport.getReceivedDate();
				Timestamp origDapcDateReviewed = complianceReport
						.getDapcDateReviewed();
				String origComments = complianceReport.getComments();
				String origPermitNumber = complianceReport.getPermitNumber();
				String origComplianceStatusCd = complianceReport
						.getComplianceStatusCd();
				String origDapcReviewComments = complianceReport
						.getDapcReviewComments();
				String origOtherCategoryCd = complianceReport
						.getOtherCategoryCd();
				
				Integer origReportYear = complianceReport.getReportYear();
				Integer origReportQuarter = complianceReport.getReportQuarter();

				if (ok) {

					loadComplianceReport(getComplianceReport().getReportId(),
							schemaFlag());

					// Restore values to potentially-editable fields. Note: we do it this way instead of saving the values to the db,
					// in case the user decides to Cancel instead of Save after adding attachment(s) in edit mode.
					if (complianceReport != null) {
						
						complianceReport.setLegacyFlag(origLegacyFlag);
						complianceReport.setDapcReviewer(origDapcReviewer);
						complianceReport.setDapcAccepted(origDapcAccepted);
						complianceReport.setReceivedDate(origReceivedDate);
						complianceReport.setDapcDateReviewed(origDapcDateReviewed);
						complianceReport.setComments(origComments);
						complianceReport.setPermitNumber(origPermitNumber);
						complianceReport.setComplianceStatusCd(origComplianceStatusCd);
						complianceReport.setDapcReviewComments(origDapcReviewComments);
						complianceReport.setOtherCategoryCd(origOtherCategoryCd);
						
						complianceReport.setReportYear(origReportYear);
						complianceReport.setReportQuarter(origReportQuarter);

						if (complianceReport
								.getReportStatus()
								.equalsIgnoreCase(
										ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT)) {
							setPassedValidation(false);
							complianceReport.setValidated(false);

							// update the compliance report validated flag
							try {
								getComplianceReportService().setValidatedFlag(
										complianceReport,
										this.passedValidation, schemaFlag());
							} catch (RemoteException e) {
								setPassedValidation(false);
								complianceReport.setValidated(false);
								handleException(e);
							}
						}

						DisplayUtil
								.displayInfo("The attachment has been added to the compliance report.");
					}
				} else {
					DisplayUtil
							.displayError("Compliance Report is not found with that number.");
					return null;
				}
				FacesUtil.returnFromDialogAndRefresh();
			} catch (RemoteException e) {
				handleException(e);
			}
		} else {
			displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0]));
		}
		return null;
	}

	public AttachmentEvent updateAttachment(Attachments attachment) {

		boolean ok = true;
		List<ValidationMessage> validationMessages = new ArrayList<ValidationMessage>();

		if (attachment.getDocument() == null) {
			// should never happen
			logger.debug("Attempt to process null attachment");
			ok = false;
		} else {

			// make sure document description is provided
			if (attachment.getTempDoc().getDescription() == null
					|| attachment.getTempDoc().getDescription().trim()
							.equals("")) {
				validationMessages.add(new ValidationMessage("description",
						"Attribute " + "Description" + " is not set.",
						ValidationMessage.Severity.ERROR, "descriptionText"));
			}

			// make sure a justification is provided for trade secret document
			if (attachment.getTradeSecretAttachmentInfo() != null
					|| attachment.getTempDoc().getTradeSecretDocId() != null) {
				if (attachment.getTempDoc().getTradeSecretJustification() == null
						|| attachment.getTempDoc()
								.getTradeSecretJustification().trim()
								.equals("")) {
					validationMessages.add(new ValidationMessage(
							"tradeSecretJustification", "Attribute "
									+ "Trade Secret Justification"
									+ " is not set.",
							ValidationMessage.Severity.ERROR,
							"tradeSecretReason"));
				}
			}
		}

		if (validationMessages.size() == 0 && ok) {
			try {
				Attachment doc = attachment.getTempDoc();
				ComplianceReportAttachment ca = new ComplianceReportAttachment(
						doc);
				ca.setTradeSecretDocId(doc.getTradeSecretDocId());
				ca.setTradeSecretJustification(doc
						.getTradeSecretJustification());

				getComplianceReportService().modifyComplianceAttachment(
						complianceReport, ca);
			} catch (RemoteException e) {
				ok = false;
				handleException(e);
			}
			// reload attachments
			try {
				complianceReport.setAttachments(getComplianceReportService()
						.retrieveCrAttachments(complianceReport, schemaFlag()));
				Attachments a = (Attachments) FacesUtil
						.getManagedBean("attachments");
				a.setAttachmentList(complianceReport.getAttachments());
				// clean up temporary variables
				a.cleanup();
				a.setFileToUpload(null);
				a.setTsFileToUpload(null);
				a.setDocument(null);
			} catch (RemoteException e) {
				setEditable(false);
				// turn it into an exception we can handle.
				ok = false;
				handleException(e);
			}

			// update the search bean (this is redundant on the portal side
			if (isInternalApp()) {
				updateSearch();
			}

			initializeAttachmentBean();

			if (complianceReport.getReportStatus().equalsIgnoreCase(
					ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT)) {
				setPassedValidation(false);
				complianceReport.setValidated(false);

				try {
					getComplianceReportService().setValidatedFlag(
							complianceReport, this.passedValidation,
							schemaFlag());
				} catch (RemoteException e) {
					setPassedValidation(false);
					complianceReport.setValidated(false);
					handleException(e);
				}
			}
			DisplayUtil
					.displayInfo("The attachment information has been updated.");
			FacesUtil.returnFromDialogAndRefresh();

		} else {
			displayValidationMessages("",
					validationMessages.toArray(new ValidationMessage[0]));
		}

		return null;
	}

	public AttachmentEvent deleteAttachment(Attachments attachment) {
		boolean ok = true;
		try {
			Attachment doc = attachment.getTempDoc();
			ComplianceReportAttachment ca = new ComplianceReportAttachment(doc);
			ca.setTradeSecretDocId(doc.getTradeSecretDocId());
			getComplianceReportService().deleteComplianceAttachment(
					complianceReport, ca);
		} catch (RemoteException e) {
			ok = false;
			handleException(e);
		}
		// reload attachments
		try {
			complianceReport.setAttachments(getComplianceReportService()
					.retrieveCrAttachments(complianceReport, schemaFlag()));
			Attachments a = (Attachments) FacesUtil
					.getManagedBean("attachments");
			a.setAttachmentList(complianceReport.getAttachments());
			// clean up temporary variables
			a.cleanup();
			a.setFileToUpload(null);
			a.setTsFileToUpload(null);
			a.setDocument(null);
		} catch (RemoteException e) {
			setEditable(false);
			// turn it into an exception we can handle.
			ok = false;
			handleException(e);
		}

		initializeAttachmentBean();

		if (complianceReport.getReportStatus().equalsIgnoreCase(
				ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT)) {
			setPassedValidation(false);
			complianceReport.setValidated(false);

			try {
				getComplianceReportService().setValidatedFlag(complianceReport,
						this.passedValidation, schemaFlag());
			} catch (RemoteException e) {
				setPassedValidation(false);
				complianceReport.setValidated(false);
				handleException(e);
			}
		}
		
		// update the search bean (this is redundant on the portal side
		if (isInternalApp()) {
			updateSearch();
		}

		if (ok) {
			DisplayUtil.displayInfo("The attachment has been removed.");
			FacesUtil.returnFromDialogAndRefresh();

		}
		return null;
	}

	@Override
	public void cancelAttachment() {
		popupRedirectOutcome = null;
		FacesUtil.returnFromDialogAndRefresh();

		boolean stagingSchema = isPortalApp() && isEditable();
		
		try {
			complianceReport.setAttachments(getComplianceReportService()
					.retrieveCrAttachments(complianceReport, !stagingSchema));
			Attachments a = (Attachments) FacesUtil
					.getManagedBean("attachments");
			a.setAttachmentList(complianceReport.getAttachments());
			a.cleanup();
			a.setFileToUpload(null);
			a.setTsFileToUpload(null);
			a.setDocument(null);
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil
					.displayError("Refreshing facility attachment(s) failed");
		}
		FacesUtil.returnFromDialogAndRefresh();

	}

	public final String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
			FacesUtil.setOutcome(null, popupRedirectOutcome);
			popupRedirectOutcome = null;
		}

		return null;
	}

	public void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}

	public final TableSorter getComplianceReportCategoryInfoWrapper() {
		TableSorter tableSorter = new TableSorter();
		try {
			tableSorter.setWrappedData(getComplianceReportService()
					.retrieveComplianceReportCategoryInfo());
		} catch (DAOException daoe) {
			logger.error(daoe.getMessage(), daoe);
		}

		return tableSorter;
	}

	public final String displayComplianceReportTypeHelpInfo() {
		return "dialog:complianceReportTypeHelpInfo";
	}

	public final void dialogDone() {
		return;
	}

	public final List<SelectItem> getReportTypeSubCategories() {
		String reportType = complianceReport.getReportType();
		logger.debug("Report Type: = " + reportType);
		if (null != reportType) {
			return ComplianceReportCategoriesTypeDef
					.getReportyTypeCategories(reportType);
		} else {
			return null;
		}
	}

	public final DefSelectItems getComplianceReportTypesDef() {
		return ComplianceReportTypeDef.getData().getItems();
	}

	public final DefSelectItems getComplianceReportTypesPartialDef() {
		return ComplianceReportTypePartialDef.getData().getItems();
	}

	public final DefSelectItems getComplianceReportAcceptedDef() {
		return ComplianceReportAcceptedDef.getData().getItems();
	}

	public final DefSelectItems getComplianceReportAcceptedReviewedDef() {
		return ComplianceReportAcceptedReviewedDef.getData().getItems();
	}

	public final DefSelectItems getComplianceReportStatusDef() {
		return ComplianceReportStatusDef.getData().getItems();
	}

	public boolean isLocked() {
		boolean ret = true;
		/*
		 * This returns true if the record has been reviewed. It ensures
		 * customer-facing fields are not editable by AQD unless they are Admin
		 * once the report has been reviewed
		 */
		if (isPublicApp()) {
			ret = true;
		} else if (isInternalApp() && isStars2Admin()) {
			ret = false;
		} else if (isInternalApp()
				&& ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT
						.equals(getComplianceReport().getReportStatus())
				&& !isReadOnlyUser()) {
			// the report is being created internally - they must be able to
			// edit this
			ret = false;
		} else if (isPortalApp() && !isEditable()) { // what scenario does
														// this cover?
			ret = true;
		} else if (isPortalApp()
				&& ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT
						.equals(getComplianceReport().getReportStatus())) {
			ret = false;
		}

		return ret;
	}

	public final Boolean isHideTradeSecret() {
		return hideTradeSecret;
	}

	public final void setHideTradeSecret(Boolean hideTradeSecret) {
		this.hideTradeSecret = hideTradeSecret;
	}

	public final boolean isTradeSecretVisible() {
		// Note that this Use Case is used both to protect Trade Secret
		// Application attachments and Compliance attachments.
		if (isPublicApp()) {
			return false;
		}
		return complianceReport != null
				&& (isPortalApp() || InfrastructureDefs
						.getCurrentUserAttrs().isCurrentUseCaseValid(
								"applications.viewtradesecret"))
				&& !getEditMode();
	}
	
	public boolean isBlankOutPage() {
		return blankOutPage;
	}

	public void setBlankOutPage(boolean blankOutPage) {
		this.blankOutPage = blankOutPage;
	}

	public String initializeTvccPeriods() {
		return YearsForOEPA.getDefaultYear();
	}

	protected final void disableThirdLevelMenu() {
		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_compliance"))
				.setRendered(false);
	}

	protected void updateSearch() {

	}

	public String validationDlgAction() {
		String rtn = super.validationDlgAction();
		if (null != rtn)
			return rtn;

		logger.debug("newValidationDlgReference 1 = "
				+ newValidationDlgReference);

		if (newValidationDlgReference == null
				|| newValidationDlgReference
						.equals(getValidationDlgReference())) {
			return null; // stay on same page
		}

		StringTokenizer st = new StringTokenizer(newValidationDlgReference, ":");
		String subsystem = st.nextToken();
		if (!subsystem.equals(ValidationBase.COMPLIANCE_TAG)) {
			logger.error("Compliance Report reference is in error: "
					+ newValidationDlgReference);
			DisplayUtil.displayError("Error processing validation message");
			return null;
		}

		String valReportId = st.nextToken();
		if (!valReportId.equalsIgnoreCase(String.valueOf(getComplianceReport()
				.getReportId()))) {
			DisplayUtil
					.displayError("Validation message is for Compliance Report: "
							+ valReportId);
			return null;
		}
		
		loadComplianceReport(getComplianceReport().getReportId(), schemaFlag());

		// enter edit mode
		setEditMode(true);

		return st.nextToken(); // stay on same page
	}

	/*
	 * Clear out values that may have been carried over from a previous
	 * application displayed in UI
	 */
	protected void reset() {
		if (isPublicApp()) {
			readOnly = true;
		} else {
			readOnly = isInternalApp() && isReadOnlyUser();
		}
		complianceReportDeleted = false;
	}

	public boolean getEditAllowed() {
		// admin is ALWAYS allowed to edit
		return isStars2Admin()
				|| (!readOnly && !complianceReportDeleted && (complianceReport
						.getSubmittedDate() == null));
	}

	public boolean getValidateAllowed() {
		return !readOnly && !complianceReportDeleted
				&& complianceReport.getSubmittedDate() == null;
	}

	public boolean getSubmitAllowed() {
		return !readOnly && !complianceReportDeleted
				&& complianceReport.getValidated()
				&& complianceReport.getSubmittedDate() == null;
	}

	public boolean getDeleteAllowed() {
		return !readOnly && !complianceReportDeleted
				&& complianceReport.getSubmittedDate() == null;
	}

	public boolean isComplianceReportDeleted() {
		return complianceReportDeleted;
	}

	public void setComplianceReportDeleted(boolean complianceReportDeleted) {
		this.complianceReportDeleted = complianceReportDeleted;
	}

	public int getReportId() {
		return reportId;
	}

	public void setReportId(int reportId) {
		this.reportId = reportId;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getPopupRedirectOutcome() {
		return popupRedirectOutcome;
	}

	public Boolean getHideTradeSecret() {
		return hideTradeSecret;
	}

	public void setComplianceReportIdList(List<Integer> complianceReportIdList) {
		this.complianceReportIdList = complianceReportIdList;
	}

	private boolean printValidationMessages(
			List<ValidationMessage> validationMessages) {
		String refID;
		List<ValidationMessage> valMessages = new ArrayList<ValidationMessage>();
		for (ValidationMessage msg : validationMessages) {
			refID = msg.getReferenceID();
			if (!refID.startsWith(ValidationBase.FACILITY_TAG)) {
				if (null != msg.getProperty() && msg.getProperty().equalsIgnoreCase("compDetail")) {
					msg.setReferenceID(ValidationBase.COMPLIANCE_TAG + ":"
							+ this.complianceReport.getReportId().toString()
							+ ":" + msg.getProperty());
				} else {
					msg.setReferenceID(refID);
				}

			}
			valMessages.add(msg);
		}

		return AppValidationMsg.validate(valMessages, true);
	}

	public List<ValidationMessage> validateComplianceReport() {
		List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

		boolean useReadonlyDB = false;
		if (isPublicApp()) {
			useReadonlyDB = true;
		} else if (isInternalApp()) {
			useReadonlyDB = true;
		} else if (isPortalApp()) {
			useReadonlyDB = false;
		}
		try {
			messages = getComplianceReportService().validateComplianceReport(
					complianceReport.getReportId(), useReadonlyDB);
		} catch (RemoteException e) {
			handleException("exception in compliance report "
					+ complianceReport.getReportId(), e);
		} catch (ServiceFactoryException e) {
			handleException(new RemoteException(e.getMessage(), e));
		} finally {

		}

		boolean ok = true;

		List<ValidationMessage> msgs = new ArrayList<ValidationMessage>();
		for (ValidationMessage valMsg : messages) {
			if (valMsg.getReferenceID() == null) {
				valMsg.setReferenceID(ValidationBase.COMPLIANCE_TAG + ":"
						+ complianceReport.getReportId() + ":"
						+ COMPLIANCE_REPORT);
			}

			msgs.add(valMsg);
		}

		return msgs;
	}

	public String validate() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		if (validateReport()) {
			logger.debug("Compliance Report successfully validated.");
		} else {
			logger.debug("Compliance Report was not successfully validated.");
		}
		setEditMode(false);
		clearButtonClicked();

		if (isPortalApp() && passedValidation
				&& complianceReport.containsTradeSecrets()) {
			return "validationTradeSecretNotification";
		} else {
			return "complianceDetailNoRedirect";
		}
	}

	protected boolean validateReport() {

		boolean ok = true;
		setPassedValidation(false);
		complianceReport.setValidated(false);
		
		// for second generation quartely and annual RATA reports, check if the
		// associated facility has since changed. if yes, then the user must
		// re-validate the report
		if (isInternalApp() 
				&& complianceReport.isSecondGenerationCemComRataRpt()) {
			try {
				Integer fpId = getFacility().getFpId();
				Integer lastModified = getFacility().getLastModified();
				Facility facility = getFacilityService().retrieveFacility(fpId);
				if (!facility.getLastModified().equals(lastModified)) {
					// re-load the compliance report from the db so that any
					// newly added monitors and limits can be pulled into the report
					loadComplianceReport(complianceReport.getReportId(), true);
					DisplayUtil
					.displayWarning("Compliance report has been updated because the associated " +
							"facility inventory has been modified. Please re-validate and submit the " +
							"compliance report again.");
					return false;
				}
			} catch (RemoteException re) {
				DisplayUtil.displayError("Error in accessing facility");
				handleException(re);
			}
		}

		try {
			List<ValidationMessage> messages = new ArrayList<ValidationMessage>();

			messages = validateComplianceReport();

			ok = printValidationMessages(messages);
			if (ok) {
				setPassedValidation(true);
				complianceReport.setValidated(true);

				Object close_validation_dialog = FacesContext
						.getCurrentInstance().getExternalContext()
						.getSessionMap()
						.get(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
				if (close_validation_dialog != null
						&& (messages != null && messages.isEmpty())) {
					FacesUtil
							.startAndCloseModelessDialog(AppValidationMsg.VALIDATION_RESULTS_URL);
					FacesContext.getCurrentInstance().getExternalContext()
							.getSessionMap()
							.remove(AppValidationMsg.CLOSE_VALIDATION_DIALOG);
				}

				if (ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT
						.equals(getComplianceReport().getReportStatus())) {
					DisplayUtil
							.displayInfo("Report is valid and ready to submit.");
				} else {
					DisplayUtil.displayInfo("Report is valid.");
				}
			} else {
				setPassedValidation(false);
				complianceReport.setValidated(false);
				getComplianceReportService().setValidatedFlag(complianceReport,
						this.passedValidation, schemaFlag());
				DisplayUtil.displayInfo("Report is not valid.");
			}

		} catch (RemoteException e) {
			setPassedValidation(false);
			complianceReport.setValidated(false);
			handleException(e);
		}

		return ok;
	}

	// probably don't need to retrieve ComplianceReportOnly because we don't
	// need to know the
	// facility in order to retrieve the compliance report. Rework.
	public void loadComplianceReport(int reportId, boolean useReadOnlySchema) {
		this.reportId = reportId;
		passedValidation = false;
		setComplianceReportDeleted(true);
		try {

			ComplianceReport c = getComplianceReportService()
					.retrieveComplianceReportOnly(reportId, useReadOnlySchema);

			if (c != null) {
				// find the facility id
				Facility f = null;
				// need to go retrieve the facility
				if (isPortalApp() && !useReadOnlySchema) {

					// look for a current facility in staging
					if(c.isSecondGenerationCemComRataRpt()) {
						f = getFacilityService().retrieveFacilityProfile(c.getFpId(), true);
					} else {
						f = getFacilityService().retrieveFacility(
								c.getFacilityId(), true);
					}
					logger.debug("Using facility from staging schema.");
				}
				// If there was no facility in staging OR this is internal app,
				// or we are viewing a read-only CR from the portal,
				// retrieve the current facility from readOnly db.
				if (f == null) {
					if(c.isSecondGenerationCemComRataRpt()) {
						f = getFacilityService().retrieveFacilityProfile(c.getFpId(), false);
					} else {
					f = getFacilityService().retrieveFacility(
							c.getFacilityId(), false);
					}
					logger.debug("Using facility from readOnly schema.");
				}
				if (f != null) {
					setFacility(f);
					complianceReport = getComplianceReportService()
							.retrieveComplianceReport(reportId,
									useReadOnlySchema);
					if (complianceReport != null) {
						// we've successfully retrieved the data so initialize
						// the
						// attachment backing bean
						passedValidation = complianceReport.getValidated();
						reset();
						initializeAttachmentBean();
						// make sure we don't allow edits to attachments if
						// we've been submitted
						if (isLocked()) {
							Attachments a = (Attachments) FacesUtil
									.getManagedBean("attachments");
							a.setUpdatePermitted(false);
							a.setNewPermitted(false);
						}
						// complianceReport.setStaging(this.isStaging());
						complianceReport.refreshCompReportMonitorList();
						
						// if it is a  second generation draft quarterly or annual RATA 
						// report on the internal system, then automatically synchronize
						// with the associated facility inventory so that any monitor or
						// limits added to the associated facility inventory is added to
						// the compliance report
						if (isInternalApp()
								&& complianceReport
										.isSecondGenerationCemComRataRpt()
								&& complianceReport
										.getReportStatus()
										.equals(ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT)
								&& !complianceReport.isLegacyFlag()) {
							logger.debug("Syncing compliance report monitors and limits with the facility");
							complianceReport = getComplianceReportService()
									.refreshMonitorsAndLimits(complianceReport,
											facility);
							passedValidation = complianceReport.getValidated();
						}
					} else {
						complianceReport = null;
						DisplayUtil
								.displayWarning("Selected compliance report does not exist. ");
					}

				} else {
					DisplayUtil.displayWarning("Failed to find facility "
							+ c.getFacilityId() + ", for compliance report "
							+ reportId + " Refresh data and try again.");
				}
			} else {
				complianceReport = null;
				DisplayUtil
						.displayWarning("Selected compliance report "
								+ reportId
								+ " does not exist. "
								+ "If the associated workflow is not active, it may have been intentionally deleted.");
			}
		} catch (RemoteException re) {
			handleException(re);
		}
	}
	
	public boolean hasTradeSecretDocuments() {
		boolean hasTS = false;

		ComplianceReportAttachment attachment[] = complianceReport
				.getAttachments();
		if (attachment.length == 0) {
			logger.debug("No attachments found.");
		} else {
			for (ComplianceReportAttachment doc : attachment) {
				if (doc.getTradeSecretDocId() != null
						&& ((complianceReport.getSubmittedDate() != null
								&& doc.getLastModifiedTS() != null && doc
								.getLastModifiedTS().before(
										complianceReport.getSubmittedDate())) || doc
									.isTradeSecretAllowed())) {
					hasTS = true;
				}
			}
		}
		return hasTS;
	}
	
	public String initFacilityProfile() {
		String ret = FacilityProfileBase.FAC_OUTCOME;
		FacilityProfileBase fp = (FacilityProfileBase) FacesUtil
				.getManagedBean("facilityProfile");
		
		Integer fpId = null != getComplianceReport().getFpId() ? getComplianceReport()
				.getFpId() : getFacility().getFpId();
		
		// set staging to true only if it is a new compliance report on the portal
		// in all other cases staging will be false
		boolean staging = isPortalApp() && !isReadOnlyReport();
		
		logger.debug("Navigating to fpId: " + fpId + " (staging = " + staging + ")");
		fp.initFacilityProfile(fpId, staging);

		if (!isInternalApp()) {
			ret = FacilityProfileBase.HOME_FAC_OUTCOME;
		}
		
		return ret;
	}

	/**
	 * Returns true if all of the following conditions are true
	 * <br>
	 *  - is internal application<br>
	 *  - is second generation quarterly CEM/COM monitoring or annual RATA report<br>
	 *  - report status is draft and <br>
	 *  - not in edit mode<br>
	 */
	public boolean isAllowedToUpdateFacilityVersion() {
		return isInternalApp()
				&& getComplianceReport().isSecondGenerationCemComRataRpt()
				&& getComplianceReport().getReportStatus().equals(ComplianceReportStatusDef.COMPLIANCE_STATUS_DRAFT)
				&& !getEditMode();
	}
	
	public List<Document> getReportAttachments() {
		return reportAttachments;
	}

	public List<Document> getPrintableDocumentList() {
		return printableDocList;
	}
	
	public void setPrintableDocumentList(List<Document> printableDocList) {
		this.printableDocList = printableDocList;
	}
	
	public final String printComplianceReport() {
		logger.debug("Inside ComplianceReportsCommon->printComplianceReport()");
		
		if (!firstButtonClick()) { // protect from multiple clicks
			return null;
		}

		if (isHideTradeSecret() != null && !isHideTradeSecret()
				&& !hasTradeSecretDocuments()) {
			// Since no trade secrets, give error instead;
			DisplayUtil
					.displayWarning("There is no trade secret information in the Compliance Report or file attachments.  Use Download/Print button instead.");
			clearButtonClicked();
			return null;
		}
		try {
			printableDocList = preparePrintableDocumentList();

		} finally {
			clearButtonClicked();
		}
		return "dialog:viewPDF";
	}

	protected List<Document> preparePrintableDocumentList() {
		List<Document> docList = null;
		boolean readOnly = true;
		if (complianceReport != null) {
			reportAttachments = new ArrayList<Document>();
			try {
				// Specify which schema to use ... use staging schema (readOnly
				// = false) unless the compliance report is not editable, which
				// means that it is being accessed from the Compliance Reports
				// second level or Facility->Compliance Reports third level menu.
				if (isPortalApp() && this.isEditable()) {
					readOnly = false;
				}
				logger.debug("Getting documents from readOnly schema" + readOnly);
				
				// add facility inventory pdf for second generation quarterly cem/com monitoring
				// and annual RATA reports
				Document facilityDoc = null; 
				if(complianceReport.isSecondGenerationCemComRataRpt()) {
					facilityDoc = getFacilityService().generateTempFacilityProfileReport(facility, null);
					// re-initialize the attachments bean with compliance report attachments
					// since the compliance report attachments in the bean are replaced with 
					// the facility attachments during the generation of facility pdf
					initializeAttachmentBean();
				}
				
				setTargetedFacilityIds();
				
				docList = getComplianceReportService()
						.getPrintableDocumentList(complianceReport, facilityDoc,
								reportAttachments, readOnly,
								isHideTradeSecret());
			} catch (RemoteException e) {
				handleException(e);
			}
		}
		return docList;
	}
	
	protected abstract void setTargetedFacilityIds();
	
	public final DefSelectItems getComplianceReportMonitorAndLimitAuditStatusDef() {
		return ComplianceReportMonitorAndLimitAuditStatusDef.getData().getItems();
	}
	
	

	public String selectAllF() {
		FacilityList[] facilities = getBulkComplianceReportFacilities();
		if (facilities != null) {
			for (FacilityList fl : facilities) {
				fl.setSelected(true);
			}
		}
		return null;
	}

	public String selectNoneF() {
		FacilityList[] facilities = getBulkComplianceReportFacilities();
		if (facilities != null) {
			for (FacilityList fl : facilities) {
				fl.setSelected(false);
			}
		}
		return null;
	}
	
	public abstract FacilityList[] getBulkComplianceReportFacilities();
	
	public void templateValueChanged(ValueChangeEvent event) {
		selectNoneF();
		complianceReport.setTargetFacilityIds(new String[0]);
		complianceReport.setTargetFacilities(new FacilityList[0]);
	}
	
	public final DefSelectItems getComplianceReportTypeCategoryDefs() {
		return ComplianceReportCategoriesTypeDef.getData().getItems();
	}

}
