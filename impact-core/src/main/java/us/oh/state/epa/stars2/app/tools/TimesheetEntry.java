package us.oh.state.epa.stars2.app.tools;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import oracle.adf.view.faces.context.AdfFacesContext;
import oracle.adf.view.faces.event.ReturnEvent;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import us.oh.state.epa.stars2.app.facility.FacilitySearch;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.facility.AppPermitSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.MonthDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.TimesheetFunctionTypeDef;
import us.oh.state.epa.stars2.def.TimesheetMonthlyReportTypeDef;
import us.oh.state.epa.stars2.def.TimesheetSectionTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.oh.state.epa.stars2.webcommon.TreeBase;
import us.wy.state.deq.impact.App;

import com.microsoft.sqlserver.jdbc.SQLServerException;

@SuppressWarnings("serial")
public class TimesheetEntry extends TreeBase {

	private UserAttributes userAttrs;
	
	private FacilitySearch facilitySearch;
	
	private FacilityService facilityService;
	
	private PermitService permitService;
	
	private ApplicationService applicationService;
	
	private InfrastructureService infrastructureService;
	
	private Integer aqdStaff;
	
	private TimeSheetRow row = new TimeSheetRow();
	
	private TimeSheetRow modifyRow;
	
	private Date minDate;
	
	private Date maxDate;
	
	private TableSorter rows = new TableSorter();
	
	private TableSorter appPermitSearchResults = new TableSorter();
	
	private Integer rowId;
	
    private boolean editMode1;

	private boolean newTimesheetEntry;

	private int modifyIndex;
	
	private String appPermitSearchCmpId;
	
	private String appPermitSearchFacilityId;
	
	private Integer appPermitSearchAppPermitType = AppPermitSearchResult.APP_PERMIT_TYPE_BOTH;
	
	private Integer appPermitSearchType = AppPermitSearchResult.SEARCH_TYPE_BOTH;
	
	private ValueChangeEvent invoicedValueChangedEvent;
	
	private String reportMonth;
	
	private Integer reportYear;
	
	private String reportTypeCd;
	
	private TimesheetReport monthlyReport;
	
	
    public TimesheetEntry() {
	}
    
	
    
	public Integer getAppPermitSearchAppPermitType() {
		return appPermitSearchAppPermitType;
	}



	public void setAppPermitSearchAppPermitType(Integer appPermitSearchAppPermitType) {
		this.appPermitSearchAppPermitType = appPermitSearchAppPermitType;
	}



	public PermitService getPermitService() {
		return permitService;
	}



	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}



	public ApplicationService getApplicationService() {
		return applicationService;
	}



	public void setApplicationService(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}



	public FacilityService getFacilityService() {
		return facilityService;
	}



	public void setFacilityService(FacilityService facilityService) {
		this.facilityService = facilityService;
	}
	

	public InfrastructureService getInfrastructureService() {
		return infrastructureService;
	}



	public void setInfrastructureService(InfrastructureService infrastructureService) {
		this.infrastructureService = infrastructureService;
	}



	public ValueChangeEvent getInvoicedValueChangedEvent() {
		return invoicedValueChangedEvent;
	}



	public void setInvoicedValueChangedEvent(
			ValueChangeEvent invoicedValueChangedEvent) {
		this.invoicedValueChangedEvent = invoicedValueChangedEvent;
	}



	public Integer getAppPermitSearchType() {
		return appPermitSearchType;
	}



	public void setAppPermitSearchType(Integer searchType) {
		this.appPermitSearchType = searchType;
	}



	public TableSorter getAppPermitSearchResults() {
		return appPermitSearchResults;
	}


	public void setAppPermitSearchResults(TableSorter searchResults) {
		this.appPermitSearchResults = searchResults;
	}
	
	public String getReportMonth() {
		return reportMonth;
	}

	public void setReportMonth(String reportMonth) {
		this.reportMonth = reportMonth;
	}
	
	public Integer getReportYear() {
		return reportYear;
	}

	public void setReportYear(Integer reportYear) {
		this.reportYear = reportYear;
	}
	
	public String getReportTypeCd() {
		return reportTypeCd;
	}
	
	public void setReportTypeCd(String reportTypeCd) {
		this.reportTypeCd = reportTypeCd;
	}
	
	public TimesheetReport getMonthlyReport() {
		return monthlyReport;
	}

	public void setMonthlyReport(TimesheetReport monthlyReport) {
		this.monthlyReport = monthlyReport;
	}

	public void cancelAppPermitSearch() {
		setNsrAppPermitSearchResultApplied(false);
		setTvAppPermitSearchResultApplied(false);
		closeAppPermitSearchDialog();
	}

	public void dialogDone() {
		return;		
	}

	//TODO
	public void nsrAppPermitSearchDialogDone() {
		return;		
	}
	//TODO
	public void tvAppPermitSearchDialogDone() {
		return;		
	}

	public String startToEditTimesheetEntry() {
		modifyIndex = this.rows.getRowIndex();
		modifyRow = 
				new TimeSheetRow((TimeSheetRow) this.rows.getRowData(modifyIndex));
		
		this.editMode1 = false;

		return "dialog:timesheetEntryDetail";
	}
	
	public String startNsrAppPermitSearch() {
		setNsrAppPermitSearchResultApplied(false);
		setTvAppPermitSearchResultApplied(false);
		setAppPermitSearchAppPermitType(AppPermitSearchResult.APP_PERMIT_TYPE_NSR);
		return "dialog:timesheetAppPermitSearch";
	}

	public String startTvAppPermitSearch() {
		setTvAppPermitSearchResultApplied(false);
		setNsrAppPermitSearchResultApplied(false);
		setAppPermitSearchAppPermitType(AppPermitSearchResult.APP_PERMIT_TYPE_TV);
		return "dialog:timesheetAppPermitSearch";
	}
	
	public final boolean getTimesheetEntryEditAllowed() {
		boolean allowed = false;
		if (isAdmin()) {
			allowed = true;
		} else {
			allowed = isWithinAllowableTime() && !getModifyRow().isInvoiced();
		}
        return allowed;
    }



	private boolean isWithinAllowableTime() {
		boolean allowed = false;
		DateTime todayDate = new DateTime(new Date());
		DateTime twoWeeksAgoDate = todayDate.minusWeeks(2);
		DateTime rowDate = new DateTime(modifyRow.getDate());
		if (!rowDate.withTimeAtStartOfDay().isBefore(
				twoWeeksAgoDate.withTimeAtStartOfDay())) {
			allowed = true;
		}
		return allowed;
	}
	

	public final void editTimesheetEntry() {
		this.editMode1 = true;
		this.newTimesheetEntry = false;
	}


	public final void saveTimesheetEntry() {

		DisplayUtil.clearQueuedMessages();

		if (null != modifyRow.getNsrId()) {
			modifyRow.setNsrId(modifyRow.getNsrId().toUpperCase());
		}
		if (null != modifyRow.getTvId()) {
			modifyRow.setTvId(modifyRow.getTvId().toUpperCase());
		}

		if (null != getInvoicedValueChangedEvent()) {
			DisplayUtil.displayWarning("*** Warning, you have changed the invoiced status of an NSR timesheet record. Please re-issue the invoice for the NSR permit/waiver if necessary***");
			setInvoicedValueChangedEvent(null);
		} else if (modifyRow.isInvoiced()) {
			displayInvoicedEntryChangedWarning();
		}
		
		ValidationMessage[] validationMessages;
		try {
			validationMessages = validateTimesheetEntry(modifyRow,true);
			if (displayValidationMessages("submitTimesheetEntry:", validationMessages)) {
				return;
			}
			
			modifyRow.setUserId(aqdStaff);
			
			boolean updated = 
					getInfrastructureService().modifyTimesheetEntry(modifyRow);
			if (!updated) {
				String msg = "Update timesheet entry failed.";
				DisplayUtil.displayError(msg);
				logger.error(msg);
				return;
			} else {
				modifyRow = 
						getInfrastructureService().retrieveTimesheetEntry(modifyRow.getRowId());
			}

		} catch (RemoteException re) {
			if (!handleNsrIdNotFoundException(re)) {
				handleException(re);
			}
			return;
		}

		DisplayUtil.displayInfo("Timesheet entry updated successfully");

		this.editMode1 = false;
		closeTimesheetEntryDialog();
	}
	
	public void cancelTimesheetEntry() {
		editMode1 = false;
		FacesUtil.returnFromDialogAndRefresh();
	}

	protected final void refreshTimesheetEntries() {
		this.editMode1 = false;
		loadTimesheetEntries();
	}
	
	public final void closeDialog() {
		setEditMode1(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public final void closeAppPermitSearchDialog() {
		resetAppPermitSearch();
		setAppPermitSearchResultSelected(false);
		FacesUtil.returnFromDialogAndRefresh();
	}

	public void closeTimesheetEntryDialog() {
		refreshTimesheetEntries();
		closeDialog();
	}

	private void displayInvoicedEntryChangedWarning() {
		DisplayUtil.displayWarning("*** Warning, you have modified or deleted an NSR timesheet record that was marked as invoiced. Please re-issue the invoice for the NSR permit/waiver if necessary***");
	}
	
	public void deleteTimesheetEntry() {

		DisplayUtil.clearQueuedMessages();

		if (modifyRow.isInvoiced()) {
			displayInvoicedEntryChangedWarning();
		}
		
		try {
			getInfrastructureService().removeTimesheetEntry(modifyRow);
			DisplayUtil.displayInfo("Timesheet entry deleted successfully.");

		} catch (DAOException e) {
			logger.error("Delete timesheet entry failed", e);
			DisplayUtil.displayError("Delete timesheet entry failed");

		} finally {

			refreshTimesheetEntries();
			closeDialog();
		}
	}

	public void invoicedValueChanged(ValueChangeEvent event) {
		setInvoicedValueChangedEvent(event);
	}
	
	public void appPermitSearchFacilitySelected(ValueChangeEvent event) 
			throws DAOException, RemoteException {
    }

	public String getAppPermitSearchFacilityId() {
		return appPermitSearchFacilityId;
	}

	public void setAppPermitSearchFacilityId(String searchFacilityId) {
		this.appPermitSearchFacilityId = searchFacilityId;
	}

	public TimeSheetRow getModifyRow() {
		return modifyRow;
	}

	public void setModifyRow(TimeSheetRow modifyRow) {
		this.modifyRow = modifyRow;
	}

	public boolean isNewTimesheetEntry() {
		return newTimesheetEntry;
	}

	public void setNewTimesheetEntry(boolean newTimesheetEntry) {
		this.newTimesheetEntry = newTimesheetEntry;
	}

	public boolean isEditMode1() {
		return editMode1;
	}

	public void setEditMode1(boolean editMode1) {
		this.editMode1 = editMode1;
	}

	public boolean isHasRows() {
		return (rows.getRowCount() > 0);
	}

	public boolean isAppPermitSearchHasResults() {
		return (appPermitSearchResults.getRowCount() > 0);
	}

	public FacilitySearch getFacilitySearch() {
		return facilitySearch;
	}

	public void setFacilitySearch(FacilitySearch facilitySearch) {
		this.facilitySearch = facilitySearch;
	}

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public TableSorter getRows() {
		return rows;
	}

	public void setRows(TableSorter rows) {
		this.rows = rows;
	}

	
	public Date getMaxDate() {
		return maxDate;
	}



	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}



	public Date getMinDate() {
		return minDate;
	}


	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}
	
	public String getAppPermitSearchCmpId() {
		return appPermitSearchCmpId;
	}

	public void setAppPermitSearchCmpId(String searchCmpId) {
		this.appPermitSearchCmpId = searchCmpId;
	}

	public TimeSheetRow getRow() {
		return row;
	}

	public void setRow(TimeSheetRow row) {
		this.row = row;
	}

	public Integer getAqdStaff() {
		return aqdStaff;
	}

	public void setAqdStaff(Integer aqdStaff) {
		this.aqdStaff = aqdStaff;
	}

	public UserAttributes getUserAttrs() {
		return userAttrs;
	}

	public void setUserAttrs(UserAttributes userAttrs) {
		this.userAttrs = userAttrs;
		if (null == aqdStaff) {
			 aqdStaff = userAttrs.getUserId();
			 loadTimesheetEntries();
		}
		LocalDate localDate = new LocalDate(new Date());
		DateTime min = localDate.toDateTimeAtStartOfDay();
		DateTime max = localDate.toDateTimeAtStartOfDay();
		if (isAdmin()) {
			min = min.minusYears(2^7);
			max = max.plusYears(2^7);
		} else {
			min = min.minusDays(14);
			max = max.plusDays(30);
		}
		setMinDate(min.toDate());
		setMaxDate(max.toDate());
	}
	
	public boolean isAdmin() {
		return userAttrs.isTimesheetAdminUser() || userAttrs.isStars2Admin();
	}
	
	public final List<SelectItem> getFunctions() {
		return sort(TimesheetFunctionTypeDef.getData().getItems().getItems(getParent().getValue()));
	}
	
	private List<SelectItem> sort(List<SelectItem> items) {
		Collections.sort(items,new Comparator<SelectItem>() {
			@Override
			public int compare(SelectItem item1, SelectItem item2) {
				return item1.getLabel().compareToIgnoreCase(item2.getLabel());
			}
		});
		return items;
	}



	public final List<SelectItem> getSections() {
		return sort(TimesheetSectionTypeDef.getData().getItems().getItems(getParent().getValue()));
	}
	
	public void aqdStaffSelected(ValueChangeEvent event) {
		reset((Integer)event.getNewValue());
	}		
	
	public void functionSelected(ValueChangeEvent event) {
		row.setFunction((String)event.getNewValue());
		row.setSection(null);
		row.setNsrId(null);
		row.setTvId(null);
		sectionSelectedEvent = null;
	}		
	
	public void sectionSelected(ValueChangeEvent event) {
		row.setSection((String)event.getNewValue());
		row.setNsrId(null);
		row.setTvId(null);
		sectionSelectedEvent = event;
	}		

	public void modifyFunctionSelected(ValueChangeEvent event) {
		modifyRow.setFunction((String)event.getNewValue());
		modifyRow.setSection(null);
		modifyRow.setNsrId(null);
		modifyRow.setTvId(null);
		modifySectionSelectedEvent = null;
	}		
	
	public void modifySectionSelected(ValueChangeEvent event) {
		modifyRow.setSection((String)event.getNewValue());
		modifyRow.setNsrId(null);
		modifyRow.setTvId(null);
		modifySectionSelectedEvent = event;
	}		

	public boolean isModifyNsrRequired() {
		return isNsrRequired(true);
	}

	public boolean isNsrRequired() {
		return isNsrRequired(false);
	}
	
	public boolean isNsrRequired(boolean modifyMode) {
		String section = modifyMode? modifyRow.getSection() : row.getSection();
		for (SelectItem i : TimesheetSectionTypeDef.getNSRValueRequiredData().getItems().getCurrentItems()) {
			if (i.getValue().equals(section) && i.getLabel().equals("Y")) {
				return true;
			}
		}
		return false;
	}
	public boolean isModifyTvRequired() {
		return isTvRequired(true);
	}

	public boolean isTvRequired() {
		return isTvRequired(false);
	}
	
	public boolean isTvRequired(boolean modifyMode) {
		String section = modifyMode? modifyRow.getSection() : row.getSection();
		for (SelectItem i : TimesheetSectionTypeDef.getTvValueRequiredData().getItems().getCurrentItems()) {
			if (i.getValue() != null && i.getValue().equals(section) && i.getLabel().equals("Y")) {
				return true;
			}
		}
		return false;
	}
	public boolean isModifySectionRequired() {
		return isSectionRequired(true);
	}

	public boolean isSectionRequired() {
		return isSectionRequired(false);
	}
	
	
	public boolean isSectionRequired(boolean modifyMode) {
		String function = modifyMode? modifyRow.getFunction() : row.getFunction();
		for (SelectItem i : TimesheetFunctionTypeDef.getSectionRequiredData().getItems().getCurrentItems()) {
			if (i.getValue().equals(function) && i.getLabel().equals("Y")) {
				return true;
			}
		}
		return false;
	}
	
	private ValueChangeEvent sectionSelectedEvent;
	
	private ValueChangeEvent modifySectionSelectedEvent;
	
	public final List<SelectItem> getSectionsByFunction() {
		return getSectionsByFunction(false);
	}
	
	public final List<SelectItem> getModifySectionsByFunction() {
		return getSectionsByFunction(true);
	}
	
	
	public final List<SelectItem> getSectionsByFunction(boolean modifyMode) {
		List<SelectItem> sections = new ArrayList<SelectItem>();
		TimeSheetRow row = modifyMode? modifyRow : this.row;
		ValueChangeEvent sectionSelectedEvent = 
				modifyMode? modifySectionSelectedEvent : this.sectionSelectedEvent;
		String function = row.getFunction();
		for (SelectItem i : TimesheetSectionTypeDef.getFunctionData().getItems().getCurrentItems()) {
			SelectItem uiSelectItem = null;
			for (SelectItem j : TimesheetSectionTypeDef.getData().getItems().getCurrentItems()) {
				if (j.getValue().equals(i.getValue())) {
					uiSelectItem = j;
					if (null == sectionSelectedEvent) {
						for (SelectItem k : TimesheetSectionTypeDef.getDefaultFunctionData().getItems().getCurrentItems()) {
							if (k.getValue().equals(j.getValue()) && 
									i.getLabel().equals(row.getFunction()) && 
									k.getLabel().equals("Y")) {
								if (null == row.getSection()) {
									row.setSection((String)k.getValue());
								}
							}
						}
					}
				}
			}
			if (function != null) {
				 if (function.equals(i.getLabel())) {
					 sections.add(uiSelectItem);
				 }
			}
		}
		sectionSelectedEvent = null;
		return sort(sections);
	}

	public LinkedHashMap<String, String> getFacilitiesByCompany() {
		return getFacilitySearch().getFacilitiesByCompany(getSelectedCompany());
	}

	private String selectedCompany;
	
	public String getSelectedCompany() {
		return selectedCompany;
	}

	public void setSelectedCompany(String selectedCompany) {
		this.selectedCompany = selectedCompany;
	}

	public void appPermitSearchCompanySelected(ValueChangeEvent e) {
		setSelectedCompany((String)e.getNewValue());
	}


	public void resetAppPermitSearch() {
		setAppPermitSearchCmpId(null);
		setAppPermitSearchFacilityId(null);
		setAppPermitSearchType(AppPermitSearchResult.SEARCH_TYPE_BOTH);
		setAppPermitSearchResults(new TableSorter());
		setSelectedCompany(null);
	}
	
	public void reset() {
		reset(null);
	}
	
	public void reset(Integer userId) {
		setAqdStaff(null == userId? getUserAttrs().getUserId() : userId);
		loadTimesheetEntries();
		setNsrAppPermitSearchResultApplied(false);
		setTvAppPermitSearchResultApplied(false);

		row.setDate(new Timestamp((new Date()).getTime()));
		row.setFunction(null);
		row.setSection(null);
		row.setAppNumber(null);
		row.setHours(null);
		row.setOvertime(false);
		row.setComments(null);
		row.setNsrId(null);
		row.setTvId(null);
	}

	private void loadTimesheetEntries() {
		TimeSheetRow[] aqdStaffRows = null;
		try {
			//TODO resolve timing issue with injected beans
			aqdStaffRows = 
					App.getApplicationContext().getBean(InfrastructureService.class).retrieveTimeSheetEntries(getAqdStaff());
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("Timesheet entry retrieval failed.");
		}
		if (aqdStaffRows.length == 0) {
			DisplayUtil.displayInfo("No timesheet entries found for the selected user.");
		}
		getRows().setWrappedData(aqdStaffRows);
	}
	
	public ValidationMessage[] validateTimesheetEntry(TimeSheetRow row)
		throws DAOException {
		return validateTimesheetEntry(row,false);
	}
	
	public ValidationMessage[] validateTimesheetEntry(TimeSheetRow row,
			boolean modifyMode) throws DAOException {
		if (row == null) {
			DAOException e = new DAOException("invalid timesheet entry");
			throw e;
		}

		ValidationMessage[] validationMessages = row.validate();

		if (null != row.getHours()) {
			ValidationMessage dailyTotalValidationMessage = 
					validateDailyHoursTotal(row);
			if (null != dailyTotalValidationMessage) {
				validationMessages = addValidationMessage(validationMessages,
						dailyTotalValidationMessage);
			}
		}
		
		if (!Utility.isNullOrEmpty(row.getTvId())) {
			boolean exists = false;
			try {
				AppPermitSearchResult[] searchResults = 
						getFacilityService().appPermitSearch(null, null, 
								AppPermitSearchResult.SEARCH_TYPE_BOTH,
								AppPermitSearchResult.APP_PERMIT_TYPE_TV);
				for (AppPermitSearchResult result : searchResults) {
					if (row.getTvId().equalsIgnoreCase(result.getId())) {
						exists = true;
						break;
					}
				}
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("System error occurred during validation.  Please try again.");
			}
			
			if (!exists) {
				if (isAdmin()) {
					DisplayUtil.displayWarning("No such Title V ID exists.");					
				} else {
					ValidationMessage noSuchTvValidationMessage = 
							new ValidationMessage("tv", 
									"No such Title V ID exists.", 
							ValidationMessage.Severity.ERROR);
					validationMessages = addValidationMessage(validationMessages,
							noSuchTvValidationMessage);
				}
			}
		} else {
			if (isTvRequired(modifyMode)) {
				ValidationMessage tvRequiredMessage = 
						new ValidationMessage("tv", "Title V ID is required.", 
						ValidationMessage.Severity.ERROR);
				validationMessages = addValidationMessage(validationMessages,
						tvRequiredMessage);
			}
		}
		
		if (!Utility.isNullOrEmpty(row.getNsrId())) {
			boolean exists = false;
			try {
				AppPermitSearchResult[] searchResults = 
						getFacilityService().appPermitSearch(null, null, 
								AppPermitSearchResult.SEARCH_TYPE_BOTH,
								AppPermitSearchResult.APP_PERMIT_TYPE_NSR);
				for (AppPermitSearchResult result : searchResults) {
					if (row.getNsrId().equalsIgnoreCase(result.getId())) {
						exists = true;
						break;
					}
				}
			} catch (RemoteException re) {
				handleException(re);
				DisplayUtil.displayError("System error occurred during validation.  Please try again.");
			}
			
			if (!exists) {
				if (isAdmin()) {
					DisplayUtil.displayWarning("No such valid, non-legacy, billable NSR ID without a final invoice exists.");					
				} else {
					ValidationMessage noSuchNsrValidationMessage = 
							new ValidationMessage("nsr", 
									"No such valid, non-legacy, billable NSR ID without a final invoice exists.", 
							ValidationMessage.Severity.ERROR);
					validationMessages = addValidationMessage(validationMessages,
							noSuchNsrValidationMessage);
				}
			}
		} else {
			if (isNsrRequired(modifyMode)) {
				ValidationMessage nsrRequiredMessage = 
						new ValidationMessage("nsr", "NSR ID is required.", 
						ValidationMessage.Severity.ERROR);
				validationMessages = addValidationMessage(validationMessages,
						nsrRequiredMessage);
			}
		}

		if (Utility.isNullOrEmpty(row.getSection()) && isSectionRequired(modifyMode)) {
			ValidationMessage sectionRequiredMessage = 
					new ValidationMessage("section", "Section is required.", 
					ValidationMessage.Severity.ERROR);
			validationMessages = addValidationMessage(validationMessages,
					sectionRequiredMessage);
		}
		
		if (isSectionRequired(modifyMode) && !validateFunctionSectionCombination(row,modifyMode)) {
			ValidationMessage invalidFunctionSectionCombinationMessage = 
					new ValidationMessage("section", "Invalid function section combination.", 
					ValidationMessage.Severity.ERROR);
			validationMessages = addValidationMessage(validationMessages,
					invalidFunctionSectionCombinationMessage);
		}
		
		return validationMessages;
	}

	private boolean validateFunctionSectionCombination(TimeSheetRow row, boolean modifyMode) {
		List<SelectItem> validSections = getSectionsByFunction(modifyMode);
		for (SelectItem validSection : validSections) {
			if (validSection.getValue().equals(row.getSection())) {
				return true;
			}
		}
		return false;
	}



	private ValidationMessage[] addValidationMessage(
			ValidationMessage[] validationMessages,
			ValidationMessage dailyTotalValidationMessage) {
		ValidationMessage[] allMessages = 
				new ValidationMessage[validationMessages.length + 1];
		for (int i=0; i<validationMessages.length; i++) {
			allMessages[i] = validationMessages[i];
		}
		allMessages[allMessages.length - 1] = 
				dailyTotalValidationMessage;
		validationMessages = allMessages;
		return validationMessages;
	}

	@SuppressWarnings("unchecked")
	private ValidationMessage validateDailyHoursTotal(TimeSheetRow row) {
		ValidationMessage dailyTotalValidationMessage = null;
		float dailyTotal = row.getHours();
		for (TimeSheetRow r : (ArrayList<TimeSheetRow>)rows.getWrappedData()) {
			DateTime dt = new DateTime(r.getDate());
			if (r.getRowId() != row.getRowId() && 
					dt.withTimeAtStartOfDay().isEqual(
							new DateTime(row.getDate()).withTimeAtStartOfDay())) {
				dailyTotal += r.getHours();
			}
		}
		if (dailyTotal > 24) {
			dailyTotalValidationMessage = new ValidationMessage("hours", 
					"Daily total cannot exceed 24 hours.", 
					ValidationMessage.Severity.ERROR);
		}
		return dailyTotalValidationMessage;
	}
	private boolean nsrAppPermitSearchResultApplied;
	
	
	
	public boolean isNsrAppPermitSearchResultApplied() {
		return nsrAppPermitSearchResultApplied;
	}


	public void setNsrAppPermitSearchResultApplied(boolean searchResultApplied) {
		this.nsrAppPermitSearchResultApplied = searchResultApplied;
	}

	private boolean tvAppPermitSearchResultApplied;
	
	
	
	public boolean isTvAppPermitSearchResultApplied() {
		return tvAppPermitSearchResultApplied;
	}


	public void setTvAppPermitSearchResultApplied(boolean searchResultApplied) {
		this.tvAppPermitSearchResultApplied = searchResultApplied;
	}


	public void submitAppPermitSearch() {
		AppPermitSearchResult[] results = null;
		try {
			results = 
					getFacilityService().appPermitSearch(appPermitSearchCmpId,
							appPermitSearchFacilityId,appPermitSearchType,
							appPermitSearchAppPermitType);
		} catch (DAOException e) {
			handleException(e);
			DisplayUtil.displayError("App/Permit search failed.");
		}
		if (results.length == 0) {
			DisplayUtil.displayInfo("No App/Permit selections found.");
		}
		getAppPermitSearchResults().setWrappedData(results);
	}
	
	public void submit() {
		ValidationMessage[] validationMessages;
		if (null != row.getNsrId()) {
			row.setNsrId(row.getNsrId().toUpperCase());
		}
		if (null != row.getTvId()) {
			row.setTvId(row.getTvId().toUpperCase());
		}
		try {
			validationMessages = validateTimesheetEntry(row);
			if (displayValidationMessages("submitTimesheetEntry:", validationMessages)) {
				return;
			}
			
			row.setUserId(aqdStaff);
			
			TimeSheetRow createdRow = getInfrastructureService().createTimesheetEntry(row);
			if (createdRow == null) {
				String msg = "Create timesheet entry failed.";
				DisplayUtil.displayError(msg);
				logger.error(msg);
				return;
			} else {
				row = new TimeSheetRow();
			}

		} catch (RemoteException re) {
			if (!handleNsrIdNotFoundException(re)) {
				handleException(re);
			}
			return;
		}

		DisplayUtil.displayInfo("Timesheet entry submitted successfully");

		reset(aqdStaff);
	}



	private boolean handleNsrIdNotFoundException(RemoteException re) {
		if (re instanceof DAOException) {
			Throwable cause = re.getCause();
			if (cause instanceof SQLServerException) {
				SQLServerException sqlServerException = (SQLServerException)cause;
				String message = sqlServerException.getMessage();
				if (null != message) {
					if (message.contains("SECTION_NSR_ID") &&
							message.contains("CHK_SECTION_NSR_ID")) {
						DisplayUtil.displayError("Timesheet not saved.");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean appPermitSearchResultSelected;
	
	public boolean isAppPermitSearchResultSelected() {
		return appPermitSearchResultSelected;
	}

	public void setAppPermitSearchResultSelected(boolean searchResultSelected) {
		this.appPermitSearchResultSelected = searchResultSelected;
	}

	public void appPermitSearchResultSelected(ValueChangeEvent e) {
		setAppPermitSearchResultSelected(true);
	}
	
	public void applyAppPermitSearchSelection() {
		AppPermitSearchResult result = null;
		for (Object obj : getAppPermitSearchResults().getData()) {
			result = (AppPermitSearchResult)obj;
			if (result.isSelected()) {
				if (result.isNsr()) {
					getRow().setNsrId(result.getId());
				} else if (result.isTv()) {
					getRow().setTvId(result.getId());
				}
				break;
			}
		}
		if (null != result && result.isSelected()) {
			if (result.isNsr()) {
				setNsrAppPermitSearchResultApplied(true);
				setTvAppPermitSearchResultApplied(false);
			} else if (result.isTv()) {
				setTvAppPermitSearchResultApplied(true);
				setNsrAppPermitSearchResultApplied(false);
			}
			closeAppPermitSearchDialog();
		} else {
			DisplayUtil.displayInfo("Please make a selection or cancel.");
		}
	}
	
	public String generateMonthlyTimesheetReport() {
		boolean ok = true;
		if(!isPositionNumberPresent()) {
			DisplayUtil.displayError("This employee does not have a position number configured " +
										"in the User Catalog – no report can be generated. " +
										"Please contact your system administrator.");
			ok = false;
		}
		if(isUserNameEmpty()) {
			DisplayUtil.displayError("This employee does not have a first name and a last name configured " +
					"in the User Catalog – no report can be generated. " +
					"Please contact your system administrator.");
			ok = false;
		}
		if(ok) {
			return "dialog:monthlyTimesheetReport";
		} else {
			return null;
		}
	}
	
	public final List<SelectItem> getMonthDefs() {
		return MonthDef.getData().getItems().getCurrentItems();
	}
	
	public final List<SelectItem> getMonthlyReportTypeDefs() {
		return sort(TimesheetMonthlyReportTypeDef.getData().getItems().getCurrentItems());
	}
	
	@SuppressWarnings("unchecked")
	public String generateMonthlyReport() {
		boolean ok = true;
		
		if(Utility.isNullOrEmpty(getReportMonth())) {
			DisplayUtil.displayError("Attribute Month is not set");
			ok = false;
		}
		
		if(Utility.isNullOrZero(getReportYear())) {
			DisplayUtil.displayError("Attribute Year is not set");
			ok = false;
		}
		
		if(Utility.isNullOrEmpty(getReportTypeCd())) {
			DisplayUtil.displayError("Attribute Report Type is not set");
			ok = false;
		}
		
		if(ok) {
			try{
				UserDef userDef;
				userDef = getInfrastructureService().retrieveUserDef(aqdStaff);
				monthlyReport = new TimesheetReport(reportMonth, reportYear, reportTypeCd, userDef,
						(ArrayList<TimeSheetRow>)rows.getWrappedData());
				return monthlyReport.generateMonthlyReport();
			}catch(RemoteException re) {
				handleException(re);
				return null;
			}
		} else {
			return null;
		}	
	}
	
	public final void cancelMonthlyReportGeneration() { 
		setReportMonth(null);
		setReportYear(null);
		setReportTypeCd(null);
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public final void cancelDownload() { 
		setReportMonth(null);
		setReportYear(null);
		setReportTypeCd(null);
		AdfFacesContext.getCurrentInstance().returnFromDialog(null, null);
	}
	
	public final void closeDownloadDialog(ReturnEvent re) { 
		FacesUtil.returnFromDialogAndRefresh();
	}
	
	public final boolean isPositionNumberPresent() {
		boolean ret = false;
		try{
			UserDef userDef = getInfrastructureService().retrieveUserDef(aqdStaff);
			ret = Utility.isNullOrEmpty(userDef.getPositionNumberString()) ? false : true;
		}catch(RemoteException re) {
			handleException(re);
		}
		
		return ret;
	}
	
	public final boolean isUserNameEmpty() {
		boolean ret = false;
		try{
			UserDef userDef = getInfrastructureService().retrieveUserDef(aqdStaff);
			ret = Utility.isNullOrEmpty(userDef.getUserFirstNm()) 
					&& Utility.isNullOrEmpty(userDef.getUserLastNm()) ? true : false;
		}catch(RemoteException re) {
			handleException(re);
		}
		
		return ret;
	}
	
	public final boolean isFunctionsDefEmpty() {
		return (TimesheetFunctionTypeDef.getData().getItems().getCurrentItems().size() == 0) ? true : false;
	}
	
}
