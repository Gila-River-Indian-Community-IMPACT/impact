package us.wy.state.deq.impact.webcommon.monitoring;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import us.oh.state.epa.stars2.app.TaskBase;
import us.oh.state.epa.stars2.app.facility.FacilitySearch;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.MonitoringService;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.def.MonitorGroupAttachmentTypeDef;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.portal.bean.SubmitTask;
import us.oh.state.epa.stars2.portal.facility.FacilityProfile;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.document.AttachmentEvent;
import us.oh.state.epa.stars2.webcommon.document.AttachmentException;
import us.oh.state.epa.stars2.webcommon.document.Attachments;
import us.oh.state.epa.stars2.webcommon.document.IAttachmentListener;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroup;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorGroupNote;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorSite;

@SuppressWarnings("serial")
public class MonitorGroupDetail extends TaskBase implements IAttachmentListener {

    public static final String DETAIL_OUTCOME = "monitoring.monitorGroupDetail";

    private Integer groupId;

    private String pageRedirect;
    
    private MonitorGroup monitorGroup;
    
	private TableSorter notesWrapper = new TableSorter();
	
	private TableSorter associatedSitesWrapper = new TableSorter();
	
	private TableSorter associatedAmbientMonitorReportsWrapper = new TableSorter();
	
	private boolean disabledUpdateButton = true;
	
	private boolean editable1;

	private boolean noteModify;

	private boolean siteModify;

	private MonitorSite modifySite;
	
	private MonitorReport modifyReport;

	private MonitorGroupNote modifyNote;

	private boolean editable = false;

    private MonitoringService monitoringService;

    private FacilityService facilityService;
    
    private FacilitySearch facilitySearch;
    
    private MonitorGroupNote monitorGroupNote;
    
    private MyTasks myTasks;

    private boolean staging = false;

    private Task reportsTask;
    
    private String popupRedirectOutcome;

	private List<Document> monitorReportDocuments; // used for print/download

	private List<Document> monitorReportAttachments; // used for print/download

	public FacilitySearch getFacilitySearch() {
		return facilitySearch;
	}

	public void setFacilitySearch(FacilitySearch facilitySearch) {
		this.facilitySearch = facilitySearch;
	}

	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	public void setMonitoringService(MonitoringService monitoringService) {
		this.monitoringService = monitoringService;
	}

	public boolean isStaging() {
		return staging;
	}

	public void setStaging(boolean staging) {
		this.staging = staging;
	}

	
	
	public Task getReportsTask() {
		return reportsTask;
	}

	public void setReportsTask(Task reportsTask) {
		this.reportsTask = reportsTask;
	}

	public MyTasks getMyTasks() {
		return myTasks;
	}

	public void setMyTasks(MyTasks myTasks) {
		this.myTasks = myTasks;
	}

	
	public boolean isFacilityAssociatedWithOtherGroup() throws DAOException {
		boolean ret = false;
		if (null != getMonitorGroup().getFacilityId()) {
			MonitorGroup searchObj = new MonitorGroup();
			searchObj.setFacilityId(getMonitorGroup().getFacilityId());
			MonitorGroup[] groups = getMonitoringService().searchMonitorGroups(searchObj);
			for (MonitorGroup group : groups) {
				if (!getMonitorGroup().getGroupId().equals(group.getGroupId())) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	public MonitorReport getModifyReport() {
		return modifyReport;
	}

	public void setModifyReport(MonitorReport modifyReport) {
		this.modifyReport = modifyReport;
	}

	public boolean isSiteModify() {
		return siteModify;
	}

	public void setSiteModify(boolean siteModify) {
		this.siteModify = siteModify;
	}

	public MonitorSite getModifySite() {
		return modifySite;
	}

	public void setModifySite(MonitorSite modifySite) {
		this.modifySite = modifySite;
	}

	public MonitorGroupNote getModifyNote() {
		return modifyNote;
	}

	public void setModifyNote(MonitorGroupNote modifyNote) {
		this.modifyNote = modifyNote;
	}
	
	public MonitorGroupNote getMonitorGroupNote() {
		return monitorGroupNote;
	}

	public void setMonitorGroupNote(MonitorGroupNote monitorGroupNote) {
		this.monitorGroupNote = monitorGroupNote;
	}

	public final void associatedSitesDialogDone() {
		refreshAssociatedSites();
	}

	public void refreshAssociatedSites() {
		MonitorSite searchObj = new MonitorSite();
		searchObj.setGroupId(groupId);
		MonitorSite[] sites;
		try {
			sites = getMonitoringService().searchMonitorSites(searchObj);
			getAssociatedSitesWrapper().setWrappedData(sites);
		} catch (DAOException e) {
			DisplayUtil.displayError("Error occurred while refreshing associated sites.");
		}
	}
    
	public void deleteMonitorGroup() {
		if (!firstButtonClick()) { // protect from multiple clicks
			return;
		}
		try {
			boolean ok = deleteMonitorGroupI();
			if (ok) {
				MonitorGroupSearch monitorGroupSearch = (MonitorGroupSearch) FacesUtil
						.getManagedBean("monitorGroupSearch");
				monitorGroupSearch
						.setPopupRedirectOutcome(MonitorGroupSearch.SEARCH_OUTCOME);
				monitorGroupSearch.submitSearch();
				DisplayUtil.displayInfo("Monitor Group successfully deleted");
				((SimpleMenuItem) FacesUtil
						.getManagedBean("menuItem_monitorGroupDetail"))
						.setDisabled(true);
			} else {
				DisplayUtil.displayError("Failed to delete Monitor Group");
			}
			FacesUtil.returnFromDialogAndRefresh();
		} finally {
			clearButtonClicked();
		}
	}

	public boolean deleteMonitorGroupI() {
		boolean ok = true;
		try {
			getMonitoringService().deleteMonitorGroup(monitorGroup);
			groupId = null;
		} catch (RemoteException re) {
			handleException(
					"exception in Monitoring " + monitorGroup.getGroupId(), re);
			ok = false;
		}
		return ok;
	}
    
    public boolean isAssociatedWithSites() {
    	return getAssociatedSitesWrapper().getData() != null?
    			!getAssociatedSitesWrapper().getData().isEmpty() : false;
    }

	public final String startViewSite() {
		setEditable1(false);
		siteModify = false;
		return "dialog:siteDetail";
	}
	public final String startEditSite(ActionEvent actionEvent) {
		setEditable1(true);
		siteModify = true;
		return "dialog:siteDetail";
	}
	
	public final String startAddSite() {
		setEditable1(true);
		return "dialog:createMonitorSite";
	}
	


	
	public final String startViewReport() {
		setEditable1(false);
		siteModify = false;
		return "dialog:reportDetail";
	}
	public final String startEditReport(ActionEvent actionEvent) {
		setEditable1(true);
		siteModify = true;
		return "dialog:reportDetail";
	}
	
	public final String startAddReport() {
		setEditable1(true);
		return "dialog:createMonitorReport";
	}
	
	public TableSorter getAssociatedSitesWrapper() {
		return associatedSitesWrapper;
	}

	public void setAssociatedSitesWrapper(TableSorter associatedSitesWrapper) {
		this.associatedSitesWrapper = associatedSitesWrapper;
	}

	public MonitorGroup getMonitorGroup() {
		return monitorGroup;
	}

	public void setMonitorGroup(MonitorGroup monitorGroup) {
		this.monitorGroup = monitorGroup;
	}
	
    public String editGroup() {
        setEditable(true);
        
        
        return null;
    }

	private String selectedCompany;
	
	private String selectedContractor;
	
	public String getSelectedCompany() {
		return selectedCompany;
	}

	public void setSelectedCompany(String selectedCompany) {
		this.selectedCompany = selectedCompany;
	}

	public String getSelectedContractor() {
		return selectedContractor;
	}

	public void setSelectedContractor(String selectedContractor) {
		this.selectedContractor = selectedContractor;
	}

	public void companySelected(ValueChangeEvent e) {
		setSelectedCompany((String)e.getNewValue());
		getFacilitySearch().setOperatingStatusCd(null);
		if (getMonitorGroup() != null) {
			getMonitorGroup().setFacilityClass(null);
			getMonitorGroup().setFacilityId(null);
			getMonitorGroup().setFacilityName(null);
			getMonitorGroup().setFacilityType(null);
		}
	}
	
	public void contractorSelected(ValueChangeEvent e) {
		getFacilitySearch().setOperatingStatusCd(null);
		if (getMonitorGroup() != null) {
			getMonitorGroup().setCmpId(null);
			getMonitorGroup().setCompanyId(null);
			getMonitorGroup().setCompanyName(null);
			getMonitorGroup().setFacilityClass(null);
			getMonitorGroup().setFacilityId(null);
			getMonitorGroup().setFacilityName(null);
			getMonitorGroup().setFacilityType(null);
		}
	}

	public LinkedHashMap<String, String> getFacilitiesByCompany() {
		return getFacilitySearch().getFacilitiesByCompany(getSelectedCompany());
	}
	
	public TableSorter getAssociatedAmbientMonitorReportsWrapper() {
		return associatedAmbientMonitorReportsWrapper;
	}

	public void setAssociatedAmbientMonitorReportsWrapper(
			TableSorter associatedAmbientMonitorReportsWrapper) {
		this.associatedAmbientMonitorReportsWrapper = associatedAmbientMonitorReportsWrapper;
	}

	public void facilitySelected(ValueChangeEvent event) 
			throws DAOException, RemoteException {
		String facilityId = (String)event.getNewValue();
    	if (!StringUtils.isBlank(facilityId)) {
	    	Facility facility = 
	    			getFacilityService().retrieveFacility(facilityId);
	    	getMonitorGroup().setFacilityClass(facility.getPermitClassCd());
	    	getMonitorGroup().setFacilityType(facility.getFacilityTypeCd());
	    	getMonitorGroup().setFacilityName(facility.getName());
    	} else {
    		getMonitorGroup().setFacilityClass(null);
    		getMonitorGroup().setFacilityType(null);
    		getMonitorGroup().setFacilityName(null);
    	}
    }

	public String getPageRedirect() {
		return pageRedirect;
	}

	public void setPageRedirect(String pageRedirect) {
		this.pageRedirect = pageRedirect;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String refresh() {
		if(groupId != null) {
			if (FAIL == submit(false)) {
				return null;
			}
		}
		if (isInternalApp()) {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_monitorGroupDetail")).setDisabled(false);
		}
		if (isPublicApp() && monitorGroup.isAqdOwned()){
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_aqdMonitorGroupDetail")).setDisabled(false);
		}
		return DETAIL_OUTCOME;
	}
	
	public String refreshPortal() {
		setGroupId(getMyTasks().getMonitorGroupId());
		return refresh();
	}
	
	public String fromHomeAmbientMonitoring() {
		if (isPortalApp()) {
			setGroupId(getMyTasks().getMonitorGroupId());
		} else if (isPublicApp()) {
			FacilityProfile facilityProfile = (FacilityProfile) FacesUtil.getManagedBean("facilityProfile");
			setGroupId(facilityProfile.getFacility().getAssociatedMonitorGroup().getGroupId());
		}
		((SimpleMenuItem)FacesUtil.getManagedBean("menuItem_homeAmbientMonitoring")).setSelected(true);

		if(groupId != null) {
			if (FAIL == submit(false)) {
				return null;
			}
		}
		return "home.ambientMonitoring";
	}
	
	public TableSorter getNotesWrapper() {
		return notesWrapper;
	}

	public void setNotesWrapper(TableSorter notesWrapper) {
		this.notesWrapper = notesWrapper;
	}

	public boolean isDisabledUpdateButton() {
		return disabledUpdateButton;
	}

	public void setDisabledUpdateButton(boolean disabledUpdateButton) {
		this.disabledUpdateButton = disabledUpdateButton;
	}

	public boolean isEditable1() {
		return editable1;
	}

	public void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}

	public boolean isNoteModify() {
		return noteModify;
	}

	public void setNoteModify(boolean noteModify) {
		this.noteModify = noteModify;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public List<ValidationMessage> validate(Integer inActivityTemplateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String findOutcome(String url, String ret) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getExternalId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setExternalId(Integer externalId) {
		// TODO Auto-generated method stub
		
	}
	
	public void aqdOwnedSelected(ValueChangeEvent event) {
		
			getMonitorGroup().setCmpId(null);
			getMonitorGroup().setCompanyId(null);
			getMonitorGroup().setCompanyName(null);
			getMonitorGroup().setFacilityClass(null);
			getMonitorGroup().setFacilityId(null);
			getMonitorGroup().setFacilityName(null);
			getMonitorGroup().setFacilityType(null);
		
			getMonitorGroup().setContractor(null);
	}
	
    public String saveEditGroup() {
        boolean operationOK = true;
//        
//    	// clean up orphan data
////    	cleanOrphanData();
//        
        String errorClientIdPrefix = "monitorSite:";
//        
    	try {
			getMonitorGroup().setFacilityAssociatedWithOtherGroup(isFacilityAssociatedWithOtherGroup());
		} catch (Exception ex) {
			DisplayUtil.displayError("Failed to save monitor group ");
			operationOK = false;
		} finally {
//			clearButtonClicked();
		}
        if(displayValidationMessages(errorClientIdPrefix,
        		monitorGroup.validate())) {
        	operationOK = false;
        }
//        
        if(operationOK) {
            try {
            	getMonitoringService().modifyMonitorGroup(monitorGroup);

                submit(false);  //   Retrieve new object
            } 
            catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                operationOK = false;
            }
            setEditable(false);
            if (operationOK) {
                DisplayUtil.displayInfo("Monitor group updated successfully");
            }
            else {
                cancelEdit();
                DisplayUtil.displayError("Monitor group update failed");
            }
        }
        return SUCCESS;
    }

    public String cancelEdit() {
    	setEditable(false);
    	return submit(false); //TODO return val?
    }

	public final boolean isSubmitReportsAllowed() {
		return isStaging() && isStagingReports() && isReportsValidated();
	}

    public final boolean isValidateReportsAllowed() {
    	return isStaging() && isStagingReports();
	}
    
    public boolean isStagingReports() {
    	boolean staging = false;

    	List<MonitorReport> reports = 
    			(List<MonitorReport>)getAssociatedAmbientMonitorReportsWrapper().getWrappedData();

		if (reports != null) {
			for (MonitorReport mrpt : reports) {
				if (mrpt.isStaging()) {
					staging = true;
					break;
				}
			}
		}
    	return staging;
    }

    public boolean isReportsValidated() {
    	boolean valid = true;

    	List<MonitorReport> reports = 
    			(List<MonitorReport>)getAssociatedAmbientMonitorReportsWrapper().getWrappedData();

		if (reports != null) {
			for (MonitorReport mrpt : reports) {
				if (mrpt.isStaging() && !mrpt.isValidated()) {
					valid = false;
					break;
				}
			}
		} else {
			valid = false;
		}

    	return valid;
    }

	public String submitReports() {
		String ret = null;
        // don't want duplicate attachments
        reportsTask.getAttachments().clear();
		List<MonitorReport> allMonitorReports = (List<MonitorReport>)getAssociatedAmbientMonitorReportsWrapper().getWrappedData();
        List<MonitorReport> stagingMonitorReport = new ArrayList<MonitorReport>();
		if (allMonitorReports != null) {
			for (MonitorReport sMrpt : allMonitorReports) {
				if (sMrpt.isStaging()) {
					stagingMonitorReport.add(sMrpt);
				}
			}
		}

        reportsTask.setMonitorReports(stagingMonitorReport);
		
		MyTasks myTasks = (MyTasks)FacesUtil.getManagedBean("myTasks");
		SubmitTask submitTask = (SubmitTask) FacesUtil.getManagedBean("submitTask");
		submitTask.setDapcAttestationRequired(true);
		submitTask.setRoSubmissionRequired(true);
		submitTask.setNonROSubmission(!myTasks.isHasSubmit());
		submitTask.setTitle("Submit Monitor Reports");

        printMonitorReports();
        submitTask.setDocuments(monitorReportDocuments);
        ret = submitTask.confirm();
        
        return ret;
	}

	public String printMonitorReports() {
		monitorReportAttachments = prepareMonitorReportAttachments();
		monitorReportDocuments = prepareMonitorReportDocuments();
		if(monitorReportAttachments==null){
			DisplayUtil
			.displayWarning("There is no trade secret information in the Monitor Report or file attachments.. Use Download/Print button instead.");	
			return null;
		}
		return "dialog:prtMonitorReports";
	}

	private List<Document> prepareMonitorReportAttachments() {
		// Always get the documents again, in case user added/deleted/modified
		// attachments.
		List<Document> attachments = new ArrayList<Document>();
		boolean useReadonlyDB = false;
		if ((!this.isStaging() && isPortalApp()) || isInternalApp() || isPublicApp()) {
			useReadonlyDB = true;
		}
		try {
			List<MonitorReport> monitorReports = 
					(List<MonitorReport>)associatedAmbientMonitorReportsWrapper.getWrappedData();

			if (monitorReports != null) {
				for (MonitorReport mrpt : monitorReports) {
					if (mrpt.isStaging()) {
						attachments
								.addAll(getMonitoringService()
										.getPrintableAttachmentList(mrpt,
												useReadonlyDB));
					}
				}
			}

		} catch (RemoteException re) {
			logger.error("getPrintableAttachmentList() failed for Monitor Report for Monitor Group "
					+ monitorGroup.getMgrpId(), re);
			DisplayUtil
					.displayError("Unable to generate monitor report attachments");
		}		
		
		return attachments;
	}

	private List<Document> prepareMonitorReportDocuments() {
		boolean useReadonlyDB = false;
		if ((!this.isStaging() && isPortalApp()) || isInternalApp() || isPublicApp()) {
			useReadonlyDB = true;
		}
		// Always get the documents again, in case user added/deleted/modified
		// attachments.
		String user = InfrastructureDefs.getCurrentUserAttrs().getUserName();
		List<Document> documents = new ArrayList<Document>();
//		TmpDocument facilityDoc = null;
//
//		try {
//			facilityDoc = getFacilityService()
//					.generateTempFacilityProfileReport(facility);
//			facilityDoc.setDescription("Printable View of Facility Inventory");
//		} catch (RemoteException re) {
//			logger.error(
//					"generateTempFacilityProfileReport() failed for report "
//							+ stackTest.getId(), re);
//			DisplayUtil
//					.displayError("Unable to generate facility inventory document");
//		}

		// Since the above code populated the attachment bean with facility
		// attachment info refresh the attachment bean.
		initializeAttachmentBean();

		try {
			List<MonitorReport> monitorReports = 
					(List<MonitorReport>)associatedAmbientMonitorReportsWrapper.getWrappedData();
			
			if (monitorReports != null) {

				for (MonitorReport mrpt : monitorReports) {
					if (mrpt.isStaging()) {
						mrpt.setFacilityId(getMonitorGroup().getFacilityId());
						mrpt.setFacilityName(getMonitorGroup()
								.getFacilityName());
						mrpt.setMgrpId(getMonitorGroup().getMgrpId());
						documents.addAll(getMonitoringService()
								.getPrintableDocumentList(mrpt, user,
										useReadonlyDB));
					}
				}
			}
		} catch (RemoteException re) {
			DisplayUtil
					.displayError("Unable to generate monitor report documents");
			handleException("Monitor Report within Monitor Group " + 
					monitorGroup.getMgrpId().toString(), re);
		}
		return documents;
	}


	public final String validateReports() {
    	List<MonitorReport> reports = 
    			(List<MonitorReport>)getAssociatedAmbientMonitorReportsWrapper().getWrappedData();

    	boolean valid = true;
		if (reports != null) {
			for (MonitorReport mrpt : reports) {
				if (mrpt.isStaging() && !mrpt.isValidated()) {
					valid = false;
					DisplayUtil
							.displayError("Monitor report "
									+ mrpt.getMrptId()
									+ " is not valid. Please validate the report before submitting.");
				}
			}
		} else {
			valid = false;
		}
    	
    	if (valid) {
			DisplayUtil.displayInfo("Monitor report is valid.");
    	}
    	
		return null; // stay on the same page
	}

    public final String submitFromJsp() { // from Search Datagrid
    	String rtn = submit(false);
		setFromTODOList(false);
		if (isInternalApp()) {
			((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_monitorGroupDetail")).setDisabled(false);
		}
    	if (isPublicApp()&& monitorGroup.isAqdOwned()){
    		((SimpleMenuItem) FacesUtil.getManagedBean("menuItem_aqdMonitorGroupDetail")).setDisabled(false);
    	}
        return rtn;
    }
    
    public final String submit(boolean readOnlyDB) { 
//      savedDesName = null;
//      associatedWithFacility = null;
//      allowedToChangeFacility = false;
		try {
			monitorGroup = getMonitoringService().retrieveMonitorGroup(groupId);
			if(monitorGroup == null) {
				DisplayUtil.displayError("No monitor group found with id: "
						+ groupId + ".");
				return FAIL;
			} else {
		        refreshAssociatedSites();
		        refreshAssociatedAmbientMonitorReports();
		        initializeAttachmentBean();
			}
	    } catch (RemoteException re) {
	    	DisplayUtil.displayError("Retrieval of Correspondence failed");
	        logger.error(re.getMessage(), re);
	    }
//	 
//      useDescription = desName;
//      if(savedDesName != null) {
//      	useDescription = savedDesName; 
//      }
		setNotesInMonitorGroup();
		setSelectedCompany(monitorGroup.getCmpId());
		getFacilitySearch().setOperatingStatusCd(null);
      return SUCCESS;
  }
    
    public final void closeDialog() {
        FacesUtil.returnFromDialogAndRefresh();
    }
    
     public void initializeAttachmentBean() {
		Attachments attachments = (Attachments) FacesUtil.getManagedBean("attachments");
		attachments.addAttachmentListener(this);
		
		if(null != monitorGroup.getGroupId()) {
			attachments.setSubPath("MonitorGroup" + File.separator + monitorGroup.getGroupId());
		} else {
			attachments.setSubPath("MonitorGroup");
		}
		
		attachments.setAttachmentTypesDef(MonitorGroupAttachmentTypeDef.getData()
                .getItems());
		
		boolean attachmentsLink = false;
		if (!isReadOnlyUser() && isInternalApp()){
			attachmentsLink = true;
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
			attachments.setAttachmentList(getMonitoringService().retrieveMonitorGroupAttachments(groupId));
		} catch (DAOException daoe) {
			handleException(daoe);
			DisplayUtil.displayError("Cannot access monitor group attachments");
		}
    }

	@Override
	public AttachmentEvent createAttachment(Attachments attachment)
			throws AttachmentException {
		boolean ok = true;
        if (attachment.getDocument() == null) {
            // should never happen
            logger.error("Attempt to process null attachment");
            ok = false;
        } else {
        	// make sure document description and type are provided
            if (attachment.getDocument().getDescription() == null || attachment.getDocument().getDescription().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify the description for this attachment");
                ok = false;
            }
            if (attachment.getDocument().getDocTypeCd() == null || attachment.getDocument().getDocTypeCd().trim().equals("")) {
                DisplayUtil
                        .displayError("Please specify an attachment type for this attachment");
                ok = false;
            }

            if(attachment.getFileToUpload() == null) {
            	DisplayUtil
				.displayError("Please specify the file to upload for this attachment");
            	ok = false;
            }
        }
		
		if(ok) {
			try {
				attachment.getTempDoc().setObjectId(null);
				getMonitoringService().createMonitorGroupAttachment(groupId, attachment.getTempDoc(),
																		attachment.getPublicAttachmentInfo().getInputStream());
				attachment.setAttachmentList(getMonitoringService().retrieveMonitorGroupAttachments(groupId));
			} catch (DAOException daoe) {
				handleException(daoe);
				DisplayUtil.displayError("Create monitor group attachment failed");
			} catch (IOException ioe) {
				handleException(new RemoteException(ioe.getMessage()));
				DisplayUtil.displayError("Create monitor group attachment failed");
			}
			
			FacesUtil.returnFromDialogAndRefresh();
		}
		
		return null;
		
	}

	@Override
	public AttachmentEvent updateAttachment(Attachments attachment) {
		boolean ok = true;
		
		// make sure document description is provided
        if (attachment.getDocument().getDescription() == null || attachment.getDocument().getDescription().trim().equals("")) {
            DisplayUtil
                    .displayError("Please specify the description for this attachment");
            ok = false;
        }
        
        if(ok) {
			try {
				getMonitoringService().updateMonitorGroupAttachment(attachment.getTempDoc());
				attachment.setAttachmentList(getMonitoringService().retrieveMonitorGroupAttachments(groupId));
			} catch (DAOException daoe) {
				handleException(daoe);
				DisplayUtil.displayError("Update monitor group attachment failed");
			}
			
			FacesUtil.returnFromDialogAndRefresh();
        }
        
		return null;
		
	}

	@Override
	public AttachmentEvent deleteAttachment(Attachments attachment) {
		try {
			getMonitoringService().removeMonitorGroupAttachment(attachment.getTempDoc());
			attachment.setAttachmentList(getMonitoringService().retrieveMonitorGroupAttachments(groupId));
		} catch (DAOException daoe) {
			handleException(daoe);
			DisplayUtil.displayError("Delete monitor group attachment failed");
		}
		
		FacesUtil.returnFromDialogAndRefresh();
		
		return null;
	}

	@Override
	public void cancelAttachment() {
		Attachments attachments = (Attachments) FacesUtil.getManagedBean("attachments");
		try {
			attachments.setAttachmentList(getMonitoringService().retrieveMonitorGroupAttachments(groupId));
		} catch (DAOException daoe) {
			handleException(daoe);
			DisplayUtil.displayError("Refreshing monitor group(s) failed");
		}
		
		FacesUtil.returnFromDialogAndRefresh();
	}
    
	/* START CODE: monitor group notes */

	public final String startViewNote() {
		setEditable1(false);
		noteModify = false;
		monitorGroupNote = new MonitorGroupNote(modifyNote);
		return "dialog:monitorGroupNoteDetail";
	}

	public final String startEditNote(ActionEvent actionEvent) {
		setEditable1(true);
		noteModify = true;
		return "dialog:monitorGroupNoteDetail";
	}

	public final String startAddNote() {
		setEditable1(true);
		noteModify = false;
		monitorGroupNote = new MonitorGroupNote();
		monitorGroupNote.setMonitorGroupId(groupId);
		monitorGroupNote.setUserId(InfrastructureDefs.getCurrentUserId());
		monitorGroupNote.setNoteTypeCd(NoteType.DAPC);
		monitorGroupNote.setDateEntered(new Timestamp(System
				.currentTimeMillis()));
		return "dialog:monitorGroupNoteDetail";
	}

	public final boolean isDisableNoteButton() {
		boolean isAdmin = InfrastructureDefs.getCurrentUserAttrs()
				.isStars2Admin();

		if (isReadOnlyUser()) {
			return true;
		}

		if (isAdmin)
			return false;

		boolean isCreatedUser = monitorGroupNote.getUserId().equals(
				InfrastructureDefs.getCurrentUserId());
		if (isCreatedUser)
			return false;

		return true;
	}

	public final boolean isReadOnlyNote() {
		if (!editable1) {
			return true;
		} else if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		} else if (!monitorGroupNote.getUserId().equals(
				InfrastructureDefs.getCurrentUserId())) {
			return true;
		}
		return false;
	}

	public final void applyEditNote(ActionEvent actionEvent) {
		boolean operationOK = true;

		// make sure note is provided
		if (monitorGroupNote.getNoteTxt() == null
				|| monitorGroupNote.getNoteTxt().trim().equals("")) {
			DisplayUtil.displayError("Attribute Note is not set.");
			return;
		}

		ValidationMessage[] validationMessages = monitorGroupNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}

		try {
			if (noteModify == false) {
				getMonitoringService().createMonitorGroupNote(monitorGroupNote);
			} else {
				// edit
				getMonitoringService().modifyMonitorGroupNote(monitorGroupNote);
			}

			refreshNotes();

		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil.displayInfo("Monitor Group notes updated successfully");
		} else {
			cancelEditNote();
			DisplayUtil.displayError("Updating Monitor Group notes failed");
		}

		noteModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelEditNote() {
		noteModify = false;
		setEditable1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	private void refreshNotes() {

		monitorGroupNote = null;

		MonitorGroupNote[] monitorGroupNotes;
		try {
			monitorGroupNotes = getMonitoringService()
					.retrieveMonitorGroupNotes(groupId);
			this.monitorGroup.setNotes(new ArrayList<MonitorGroupNote>(Arrays
					.asList(monitorGroupNotes)));
			setNotesInMonitorGroup();
		} catch (RemoteException re) {
			handleException(re);
		}
	}

	protected final void setNotesInMonitorGroup() {
		if (this.isInternalApp()) {
			notesWrapper.setWrappedData(monitorGroup.getNotes());
		}
	}
	
	public final void dialogDone() {
		return;
	}
	
	public void refreshAssociatedAmbientMonitorReports() {
		MonitorReport[] monitorReports;
		try {
			monitorReports = getMonitoringService().retrieveMonitorReports(groupId,isStaging());
			setAssociatedAmbientMonitorReportsWrapper(new TableSorter());
			getAssociatedAmbientMonitorReportsWrapper().setWrappedData(monitorReports);
		} catch (DAOException e) {
			DisplayUtil.displayError("Error occurred while refreshing associated ambient monitor reports.");
		}
	}
	
	 public boolean isAssociatedWithAmbientMonitorReports() {
	    	return getAssociatedAmbientMonitorReportsWrapper().getData() != null?
	    			!getAssociatedAmbientMonitorReportsWrapper().getData().isEmpty() : false;
	    }
	 
	public final String getPopupRedirect() {
		if (popupRedirectOutcome != null) {
			FacesUtil.setOutcome(null, popupRedirectOutcome);
			popupRedirectOutcome = null;
		}
		return null;
	}

	public final void setPopupRedirectOutcome(String popupRedirectOutcome) {
		this.popupRedirectOutcome = popupRedirectOutcome;
	}
	
}