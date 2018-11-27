package us.wy.state.deq.impact.webcommon.company;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.event.ReturnEvent;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.framework.exception.DataStoreConcurrencyException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.oh.state.epa.stars2.webcommon.bean.ConfirmWindow;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;
import us.oh.state.epa.stars2.webcommon.menu.SimpleMenuItem;
import us.wy.state.deq.impact.app.company.CompanySearch;
import us.wy.state.deq.impact.bo.CompanyService;
import us.wy.state.deq.impact.database.dbObjects.company.AreaEmissionsOffset;
import us.wy.state.deq.impact.database.dbObjects.company.Company;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyEmissionsOffsetRow;
import us.wy.state.deq.impact.database.dbObjects.company.CompanyNote;
import us.wy.state.deq.impact.database.dbObjects.company.EmissionsOffsetAdjustment;

public class CompanyProfileBase extends TreeBase {

	private static final long serialVersionUID = 7731360460062436330L;

	public static final String CMP_OUTCOME = "companyProfile";
	public static final String CMP_DIALOG_OUTCOME = "dialog:companyProfile";
	public static final String OFFSET_ADJUSTMENT_DIALOG = "dialog:OffsetAdjustmentDetail";
	protected Company company;
	private boolean editable;
	protected Integer companyId;
	protected String cmpId;
	protected int currentUserId = InfrastructureDefs.getCurrentUserId();
	protected boolean staging;

	// note initialization
	private TableSorter notesWrapper;
	private TableSorter notesCmpWrapper;
	private TableSorter cmpFacWrapper;
	private CompanyNote[] companyNotes = new CompanyNote[0];
	private CompanyNote companyNote;
	private CompanyNote modifyCompanyNote;
	private FacilityList[] companyFacilities = new FacilityList[0];
	private boolean noteModify;
	private boolean noteFromCompany;
	private boolean editable1;

	// Contacts
	private TableSorter cmpContactsWrapper;
	private Contact[] companyContacts = new Contact[0];
	private String message;
	private String popupRedirect;

	private CompanyService companyService;
	private FacilityService facilityService;
	
	// emissions offsets
	private CompanyEmissionsOffsetRow[] companyEmissionsOffsetRows = new CompanyEmissionsOffsetRow[0];
	private HashMap<String, List<CompanyEmissionsOffsetRow>> emissionsOffsetMap;
	private List<AreaEmissionsOffset> areaEmissionsOffsetList;
	
	// emissions offset adjustments
	private TableSorter emissionsOffsetAdjustmentWrapper;
	private EmissionsOffsetAdjustment modifyEmissionsOffsetAdjustment;

	public CompanyProfileBase() {
		notesWrapper = new TableSorter();
		notesCmpWrapper = new TableSorter();
		setCmpFacWrapper(new TableSorter());
		setCmpContactsWrapper(new TableSorter());
		setEmissionsOffsetAdjustmentWrapper(new TableSorter());
		popupRedirect = null;
	}

	// *************************\\
	// Profile-related methods \\
	// *************************\\

	public Integer getCompanyId() {
		return companyId;
	}

	public CompanyService getCompanyService() {
		return companyService;
	}

	public void setCompanyService(CompanyService companyService) {
		this.companyService = companyService;
	}
	
	public FacilityService getFacilityService() {
		return facilityService;
	}

	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}

	public final String submitProfile() {
		editable = false;
		company = null;
		treeData = null;
		company = getCompany();
		if (company == null) {
			logger.error("Unexpected NULL pointer return from accesing existing company from Database.");
			DisplayUtil
					.displayError("Company is not found with that number.");
			return null;
		}
		return getCompanyProfileOutcome();
	}

	public final String goToProfile() {
		editable = false;
		company = null;
		treeData = null;
		company = getCompany();
		if (company == null) {
			logger.error("Unexpected NULL pointer return from accesing existing company from Database.");
			DisplayUtil
					.displayError("Company is not found due to unexpected system problem; Please try again.");
		}

		return CMP_DIALOG_OUTCOME;
	}

	public final Company getCompany() {
		if (company == null) {
			logger.debug("getCompany: company is null " + ", companyId = "
					+ companyId);
		}
		if ((company == null && cmpId != null)
				|| (companyId != null && !companyId.equals(company
						.getCompanyId()))) {
			logger.debug("companyId = " + companyId);
			logger.debug("cmpId = " + cmpId);
			try {
				company = getCompanyService().retrieveCompanyProfile(cmpId);

				if (company != null) {
					// get other parts of a company
					cmpId = company.getCmpId();
					companyId = company.getCompanyId();

					companyNotes = company.getNotes().toArray(
							new CompanyNote[0]);
					setNotesInCompany();

					companyFacilities = company.getFacilities();
					setFacilitiesInCompany();

					companyContacts = company.getContacts();
					setContactsInCompany();

					logger.debug("Company Name: " + company.getName());
					if (isInternalApp()) {
						// get a reference to the UndeliveredMail backend-bean
						// and
						// set its companyID
					}
					
					companyEmissionsOffsetRows = company.getEmissionsOffsetRows();
					setupAreaEmissionsOffsetList();
					
					emissionsOffsetAdjustmentWrapper.setWrappedData(company.getEmissionsOffsetAdjustments());
					
				} else {
					logger.error("No company found for companyId = "
							+ companyId);
				}
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("Accessing company failed");
			}
		}

		if (company == null) {
			logger.error("getCompany is returning NULL!!!  companyId = "
					+ companyId);
		}

		return company;
	}

	public final void setCompany(Company company) {
		this.company = company;
		// facProfDocuments = null;
	}

	public final boolean getEditable() {
		return editable;
	}

	public final void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Allows current company to be editable. Also clears previous validation
	 * messages.
	 */
	public final void editCompany() {
		setEditable(true);
		company.clearValidationMessages();
	}

	public final String saveCompanyProfile() {
		boolean operationOk = true;

		if (!validateCompany()) {
			return getCompanyProfileOutcome();
		}

		setEditable(false);

		try {
			if (!getCompanyService().modifyCompany(company)) {
				DisplayUtil.displayInfo("Company update failed.");
			}
		} catch (RemoteException re) {
			handleException(re, "Saving company data failed");
			operationOk = false;
		}

		refreshCompany();
		if (operationOk) {
			DisplayUtil.displayInfo("Company data saved successfully");
		}

		return getCompanyProfileOutcome();
	}

	private void handleException(RemoteException re, String message) {
		if (re != null) {
			if (re instanceof DataStoreConcurrencyException) {
				DisplayUtil
						.displayError(message
								+ " - more than one user was attempting to edit and save company information at one time. Please re-enter your changes and submit again.");
				logger.warn(re.getMessage(), re);
			} else {
				DisplayUtil
						.displayError(message
								+ " -  system error has occurred. Please contact System Administrator.");
				logger.error(re.getMessage(), re);
			}
		}
	}

	public final String cancelEditCompany() {
		cancelEdit();
		current = cmpId;
		return getCompanyProfileOutcome();
	}

	public final String cancelEdit() {
		setEditable(false);
		refreshCompany();
		return CANCELLED;
	}

	protected final void refreshCompany() {
		company = null;
		treeData = null;
		getTreeData();
	}

	protected final boolean validateCompany() {
		company.clearValidationMessages();
		boolean isValid = true;
		ValidationMessage[] validationMessages;
		try {
			validationMessages = getCompanyService().validateCompany(company);

			if (displayValidationMessages("editCompany:", validationMessages)) {
				isValid = false;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("validate Company failed");
		}
		return isValid;
	}

	/**
	 * Determines whether or not updating the company is allowed.
	 * 
	 * @return true if button is disabled
	 */
	public final boolean isDisabledUpdateButton() {
		if (isPublicApp()) {
			return true;
		}
		if (InfrastructureDefs.getCurrentUserAttrs().isCurrentUseCaseValid(
				"companies.profile.editProfile")) {
			return false;
		}

		if (isReadOnlyUser()) {
			return true;
		}

		if (isReleasePlannerUser()) {
			return true;
		}

		// handle when not internal
		if (!isInternalApp()) {
			return true;
		}

		if (company != null
				&& (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin())) {
			return false;
		}

		return true;
	}

	public final boolean isDemReadOnlyAttr() {
		if (isInternalApp()) {
			return !editable;
		}

		return true;
	}

	public final boolean isDapcUser() {
		return isInternalApp();
	}

	public final String getCompanyProfileOutcome() {
		return CompanyProfileBase.CMP_OUTCOME;
	}

	public final Integer getUserID() {
		return currentUserId;
	}

	// **********************\\
	// Note-related methods \\
	// **********************\\
	public final String startViewNote() {
		setEditable1(false);
		noteModify = false;
		companyNote = new CompanyNote(modifyCompanyNote);
		return "dialog:companyNoteDetail";
	}

	public final String startAddNote() {
		setEditable1(true);
		noteModify = false;
		companyNote = new CompanyNote();
		companyNote.setCompanyId(companyId);
		logger.debug("cmp_id=" + cmpId);
		companyNote.setCmpId(cmpId);
		companyNote.setUserId(getUserID());
		companyNote.setNoteTypeCd(NoteType.DAPC);
		companyNote.setDateEntered(new Timestamp(System.currentTimeMillis()));
		return "dialog:companyNoteDetail";
	}

	public CompanyNote getCompanyNote() {
		return companyNote;
	}

	public void setCompanyNote(CompanyNote companyNote) {
		this.companyNote = companyNote;
	}

	public final CompanyNote getModifyCompanyNote() {
        return modifyCompanyNote;
    }

    public final void setModifyCompanyNote(CompanyNote modifyCompanyNote) {
        this.modifyCompanyNote = modifyCompanyNote;
    }
    
	public final CompanyNote[] getCompanyNotes() {
		return companyNotes;
	}

	public final void setCompanyNotes(CompanyNote[] companyNotes) {
		this.companyNotes = companyNotes;
	}

	public boolean isEditable1() {
		return editable1;
	}

	public void setEditable1(boolean editable1) {
		this.editable1 = editable1;
	}

	public TableSorter getNotesWrapper() {
		return notesWrapper;
	}

	public TableSorter getNotesCmpWrapper() {
		return notesCmpWrapper;
	}

	protected final void setNotesInCompany() {
		if (this.isInternalApp()) {
			notesWrapper.setWrappedData(companyNotes);
		}
		notesCmpWrapper.setWrappedData(companyNotes);
	}

	public final boolean isReadOnlyNote() {
		if (!editable1) {
			return true;
		} else if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		} else if (!companyNote.getUserId().equals(getUserID())) {
			return true;
		}
		return false;
	}

	public final void dialogDone() {
		return;
	}

	public final String startEditNote(ActionEvent actionEvent) {
		setEditable1(true);
		noteModify = true;
		logger.debug("Made it startEditNote");
		return "dialog:companyNoteDetail";
	}

	public final void applyEditNote(ActionEvent actionEvent) {
		boolean operationOK = true;

		// make sure note is provided
		if (companyNote.getNoteTxt() == null || companyNote.getNoteTxt().trim().equals("")) {
			DisplayUtil.displayError("Attribute Note is not set.");
			return;
		}
		  
		ValidationMessage[] validationMessages = companyNote.validate();

		if (validationMessages.length > 0) {
			if (displayValidationMessages("", validationMessages)) {
				return;
			}
		}

		try {

			if (noteModify == false) {
				getCompanyService().createCompanyNote(companyNote);
			} else {
				// edit
				getCompanyService().modifyCompanyNote(companyNote);
			}
			companyNotes = getCompanyService().retrieveCompanyNotes(cmpId);
			setNotesInCompany();
		} catch (RemoteException re) {
			handleException(re);
			operationOK = false;
		}

		if (operationOK) {
			DisplayUtil.displayInfo("Company notes updated successfully");
		} else {
			cancelEditNote();
			DisplayUtil.displayError("Updating company notes failed");
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

	public final void closeDialog() {
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final boolean isDisableNoteButton() {
		if (noteFromCompany) {
			return true;
		} else if (isReadOnlyUser()) {
			return true;
		} else if (InfrastructureDefs.getCurrentUserAttrs().isStars2Admin()) {
			return false;
		} else if (!companyNote.getUserId().equals(getUserID())) {
			return true;
		}
		return false;
	}

	public final boolean isNoteFromCompany() {
		return noteFromCompany;
	}

	public final void setNoteFromCompany(boolean noteFromCompany) {
		this.noteFromCompany = noteFromCompany;
	}

	// *************************\\
	// Facility-related methods \\
	// *************************\\

	public TableSorter getCmpFacWrapper() {
		return cmpFacWrapper;
	}

	public void setCmpFacWrapper(TableSorter cmpFacWrapper) {
		this.cmpFacWrapper = cmpFacWrapper;
	}

	public FacilityList[] getCompanyFacilities() {
		return companyFacilities;
	}

	public void setCompanyFacilities(FacilityList[] companyFacilities) {
		this.companyFacilities = companyFacilities;
	}

	protected final void setFacilitiesInCompany() {
		cmpFacWrapper.setWrappedData(companyFacilities);
	}

	public TableSorter getCmpContactsWrapper() {
		return cmpContactsWrapper;
	}

	public void setCmpContactsWrapper(TableSorter cmpContactsWrapper) {
		this.cmpContactsWrapper = cmpContactsWrapper;
	}

	protected final void setContactsInCompany() {
		cmpContactsWrapper.setWrappedData(companyContacts);
	}

	public String getPopupRedirect() {
		if (popupRedirect != null) {
			FacesUtil.setOutcome(null, popupRedirect);
			popupRedirect = null;
		}

		return popupRedirect;
	}

	public void setPopupRedirect(String popupRedirect) {
		this.popupRedirect = popupRedirect;
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	public final String deleteCompany() {
		setMessage("You are about to delete this company. Deleting a company will permanently remove it and any of its notes from the system.");
		return "dialog:deleteCompany";
	}

	public final void applyCompanyDelete() {
		try {
			Company company = getCompanyService().retrieveCompanyProfile(cmpId);
			getCompanyService().deleteCompany(company);

			// TODO make this a managed-property
			FacesContext context = FacesContext.getCurrentInstance();
			ValueBinding vl = context.getApplication().createValueBinding(
					"#{companySearch}");
			CompanySearch companySearch = (CompanySearch) vl.getValue(context);

			companySearch.submitSearch();
			DisplayUtil.displayInfo("Company has been deleted");
			setPopupRedirect("companySearch");

			// TODO make this a managed-property
			((SimpleMenuItem) FacesUtil
					.getManagedBean("menuItem_companyProfile"))
					.setDisabled(true);

		} catch (RemoteException e) {
			handleException(e);
			DisplayUtil.displayError("Deleting company failed");
		} finally {
			setMessage(null);
		}
		refreshCompany();
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void cancelCompanyDelete() {
		setMessage(null);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public Contact[] getCompanyContacts() {
		return companyContacts;
	}

	public void setCompanyContacts(Contact[] companyContacts) {
		this.companyContacts = companyContacts;
	}

	public boolean isCompanyMergeable() {
		return false;
	}
	
	public boolean getCompanyHasNotes() {
		boolean ret = false;
		
		ret = companyNotes != null && companyNotes.length > 0;
		
		return ret;
	}
	
	public CompanyEmissionsOffsetRow[] getCompanyEmissionsOffsetRows() {
		return companyEmissionsOffsetRows;
	}

	public void setCompanyEmissionsOffsetRows(
			CompanyEmissionsOffsetRow[] companyEmissionsOffsetRows) {
		this.companyEmissionsOffsetRows = companyEmissionsOffsetRows;
	}
	
	public List<AreaEmissionsOffset> getAreaEmissionsOffsetList() {
		return areaEmissionsOffsetList;
	}

	public void setAreaEmissionsOffsetList(
			List<AreaEmissionsOffset> areaEmissionsOffsetList) {
		this.areaEmissionsOffsetList = areaEmissionsOffsetList;
	}

	public TableSorter getEmissionsOffsetAdjustmentWrapper() {
		return emissionsOffsetAdjustmentWrapper;
	}

	public void setEmissionsOffsetAdjustmentWrapper(
			TableSorter emissionsOffsetAdjustmentWrapper) {
		this.emissionsOffsetAdjustmentWrapper = emissionsOffsetAdjustmentWrapper;
	}
	
	public EmissionsOffsetAdjustment getModifyEmissionsOffsetAdjustment() {
		return modifyEmissionsOffsetAdjustment;
	}

	public void setModifyEmissionsOffsetAdjustment(
			EmissionsOffsetAdjustment modifyEmissionsOffsetAdjustment) {
		this.modifyEmissionsOffsetAdjustment = modifyEmissionsOffsetAdjustment;
	}

	/** 
	 * Builds a hashmap of non-attainment area (key) and the associated emissions offset (values)
	 * for a given company
	 */
	private void buildEmissionsOffsetMap() {
			
			// clear old data
			if(null == emissionsOffsetMap) {
				emissionsOffsetMap =
						new HashMap<String, List<CompanyEmissionsOffsetRow>>();
			} else {
				emissionsOffsetMap.clear();
			}
			
			for(CompanyEmissionsOffsetRow row : companyEmissionsOffsetRows) {
				String areaCd = row.getNonAttainmentAreaCd();
				List<CompanyEmissionsOffsetRow> emissionsOffsetRowList = emissionsOffsetMap.get(areaCd);
				
				if(null == emissionsOffsetRowList) {
					emissionsOffsetRowList = new ArrayList<CompanyEmissionsOffsetRow>();
				}
				
				emissionsOffsetRowList.add(row);
				emissionsOffsetMap.put(areaCd, emissionsOffsetRowList);
			}
	}
	
	public void setupAreaEmissionsOffsetList() {
		// build a map of non-attainment area and the associated emissions offsets for the 
		// given permit
		buildEmissionsOffsetMap();
		
		Iterator<String> iter = emissionsOffsetMap.keySet().iterator();
		areaEmissionsOffsetList = new ArrayList<AreaEmissionsOffset>();
		
		while(iter.hasNext()) {
			String areaCd = iter.next();
			List<CompanyEmissionsOffsetRow> emissionsOffsetRowList = emissionsOffsetMap.get(areaCd);
			AreaEmissionsOffset areaEmissionsOffset = new AreaEmissionsOffset();
			
			if(null != emissionsOffsetRowList && emissionsOffsetRowList.size() > 0) {
				// get the non-attainment area and attainment standard from the first item in the list
				// note - the value of non-attainment area and attainment standard will be same for all the
				// items in the list for a given non-attainment area
				CompanyEmissionsOffsetRow row = emissionsOffsetRowList.get(0);
				
				areaEmissionsOffset.setNonAttainmentAreaCd(row.getNonAttainmentAreaCd());
				areaEmissionsOffset.setAttainmentStandardCd(row.getAttainmentStandardCd());
				areaEmissionsOffset.setEmissionsOffsetRowList(emissionsOffsetRowList);
				
				areaEmissionsOffsetList.add(areaEmissionsOffset);
			}
		}
	}
	
	public final String startToViewEmissionsOffsetAdjustment() {
		int index = emissionsOffsetAdjustmentWrapper.getRowIndex();
		modifyEmissionsOffsetAdjustment = 
				(EmissionsOffsetAdjustment)emissionsOffsetAdjustmentWrapper.getRowData(index);
		modifyEmissionsOffsetAdjustment.setNewObject(false);
		
		return OFFSET_ADJUSTMENT_DIALOG;
	}
	
	public final String startToAddEmissionsOffsetAdjustment() {
		modifyEmissionsOffsetAdjustment = new EmissionsOffsetAdjustment();
		modifyEmissionsOffsetAdjustment.setCompanyId(company.getCompanyId());
		modifyEmissionsOffsetAdjustment.setNewObject(true);
		setEditable1(true);

		return OFFSET_ADJUSTMENT_DIALOG;
	}
	
	public final void startToEditEmissionsOffsetAdjustment() {
		modifyEmissionsOffsetAdjustment.setNewObject(false);
		setEditable1(true);
	}
	
	public void addEmissionsOffsetAdjustment() {
		if(validateEmissionsOffsetAdjustment()) {
			// validation is successful, continue to save the object
			try{
				modifyEmissionsOffsetAdjustment =
						getCompanyService().createEmissionsOffsetAdjustment(modifyEmissionsOffsetAdjustment);
				setEditable1(false);
				refreshEmissionsOffsetAdjustments();
				DisplayUtil.displayInfo("Emissions offset adjustment successfully created");
			} catch(RemoteException re) {
				DisplayUtil.displayError("Failed to create the emissions offset adjustment");
				handleException(re);
			}
		}
	}
	
	public void saveEmissionsOffsetAdjustment() {
		if(validateEmissionsOffsetAdjustment()) {
			// validation is successful, continue to save the object
			try{
				if(getCompanyService().
						modifyEmissionsOffsetAdjustment(modifyEmissionsOffsetAdjustment)) {
					// get updated emissions offset adjustment
					modifyEmissionsOffsetAdjustment = getCompanyService().
							retrieveEmissionsOffsetAdjustment(modifyEmissionsOffsetAdjustment.getEmissionsOffsetAdjustmentId());
					DisplayUtil.displayInfo("Emissions offset adjustment information successfully saved");
				}
				setEditable1(false);
			} catch(RemoteException re) {
				DisplayUtil.displayError("Failed to save the emissions offset adjustment information");
				handleException(re);
			} finally {
				refreshEmissionsOffsetAdjustments();
			}
		}
		
	}
	
	public void deleteEmissionsOffsetAdjustment(ReturnEvent re) {
		ConfirmWindow cw = (ConfirmWindow) FacesUtil
				.getManagedBean("confirmWindow");
		if (cw.getSelection().equalsIgnoreCase(ConfirmWindow.NO)) {
			return;
		}
		
		// user's response to the delete prompt is affirmative, proceed with the delete
		try {
			getCompanyService().
				deleteEmissionsOffsetAdjustment(modifyEmissionsOffsetAdjustment.getEmissionsOffsetAdjustmentId());
			DisplayUtil.displayInfo("Emissions offset adjustment successfully deleted");
		} catch(RemoteException e) {
			DisplayUtil.displayError("Failed to delete the emissions offset adjustment");
			handleException(e);
		} finally {
			refreshEmissionsOffsetAdjustments();
		}
	}
	
	public void cancelEditEmissionsOffsetAdjustment() {
		setEditable1(false);
		refreshEmissionsOffsetAdjustments();
	}
	
	public void refreshEmissionsOffsetAdjustments() {
		try {
			EmissionsOffsetAdjustment[] emissionsOffsetAdjustments =
					getCompanyService().retrieveEmissionsOffsetAdjustmentsByCompanyId(company.getCompanyId());
			company.setEmissionsOffsetAdjustments(emissionsOffsetAdjustments);
			
			// update the wrapper
			emissionsOffsetAdjustmentWrapper.clearWrappedData();
			emissionsOffsetAdjustmentWrapper.setWrappedData(company.getEmissionsOffsetAdjustments());
		} catch(RemoteException re) {
			DisplayUtil.displayError("Failed to retrieve the emissions offset adjustments");
			handleException(re);
		} finally {
			closeDialog();
		}
	}
	
	public List<SelectItem> getNonAttainmentAreaPollutants() {
		List<SelectItem> pollutants = new ArrayList<SelectItem>();
		try{
			PollutantDef[] pdefs = getCompanyService()
					.findPollutantsByNonattainmentArea(modifyEmissionsOffsetAdjustment.getNonAttainmentAreaCd());
			for(PollutantDef def : pdefs) {
				pollutants.add(new SelectItem(def.getCode(), def.getDescription(), "", def.isDeprecated()));
			}
			
		}catch(RemoteException re) {
			handleException (re);
		}
		
		return pollutants;
	}
	
	private boolean validateEmissionsOffsetAdjustment() {
		boolean ret = true;
		
		if(null != modifyEmissionsOffsetAdjustment) {
			ValidationMessage[] valMsgs = modifyEmissionsOffsetAdjustment.validate();
			if(null != valMsgs && valMsgs.length > 0) {
				ret = false;
				for(ValidationMessage msg : valMsgs) {
					DisplayUtil.displayError(msg.getMessage(), msg.getProperty());
				}
			}
		}
		return ret;
	}
	
	public Timestamp getMaxDate() {
		return new Timestamp(System.currentTimeMillis());
	}
	
}
