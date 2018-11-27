package us.wy.state.deq.impact.webcommon.projectTracking;

import java.rmi.RemoteException;
import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import us.oh.state.epa.stars2.bo.ProjectTrackingService;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.ProjectTrackingEventTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.TableSorter;
import us.wy.state.deq.impact.database.dbObjects.projectTracking.ProjectSearchResult;

@SuppressWarnings("serial")
public class ProjectTrackingSearch extends AppBase {

	private ProjectTrackingService projectTrackingService;
	
	private ProjectTrackingSearchCriteria criteria = 
			new ProjectTrackingSearchCriteria();
	
	private TableSorter resultsWrapper = new TableSorter();
	
	private ProjectSearchResult[] results = new ProjectSearchResult[0];
	
	private boolean hasSearchResults;

	public String submitSearch() {
		results = null;
		hasSearchResults = false;

		try {
			results = getProjectTrackingService().searchProjects(criteria);
			DisplayUtil.displayHitLimit(results.length);
			resultsWrapper.setWrappedData(results);
			if (results.length == 0) {
				DisplayUtil
						.displayInfo("There are no projects that match the search criteria");
			} else {
				hasSearchResults = true;
			}
		} catch (RemoteException re) {
			handleException(re);
			DisplayUtil.displayError("Search failed");
			results = new ProjectSearchResult[0];
			resultsWrapper.setWrappedData(results);
		}

		return SUCCESS;
	}
	
	public void projectTypeChanged(ValueChangeEvent e) {
		setProjectNEPALevelCd(null);
		setProjectStageCd(null);
		setEventTypeCd(null);
		setEventDateFrom(null);
		setEventDateTo(null);
		setProjectOutreachCategoryCd(null);
		setProjectGrantStatusCd(null);
		setProjectLetterTypeCd(null);
		setProjectContractTypeCd(null);
	}
	
	public String getProjectId() {
		return criteria.getProjectId();
	}

	public void setProjectId(String projectId) {
		criteria.setProjectId(projectId);
	}

	public String getProjectName() {
		return criteria.getProjectName();
	}

	public void setProjectName(String projectName) {
		criteria.setProjectName(projectName);
	}

	public String getProjectDescription() {
		return criteria.getProjectDescription();
	}

	public void setProjectDescription(String projectDescription) {
		criteria.setProjectDescription(projectDescription);
	}

	public String getProjectSummary() {
		return criteria.getProjectSummary();
	}

	public void setProjectSummary(String projectSummary) {
		criteria.setProjectSummary(projectSummary);
	}

	public Date getEventDateFrom() {
		return criteria.getEventDateFrom();
	}

	public void setEventDateFrom(Date eventDateFrom) {
		criteria.setEventDateFrom(eventDateFrom);
	}

	public Date getEventDateTo() {
		return criteria.getEventDateTo();
	}

	public void setEventDateTo(Date eventDateTo) {
		criteria.setEventDateTo(eventDateTo);
	}

	public String getProjectStateCd() {
		return criteria.getProjectStateCd();
	}

	public void setProjectStateCd(String projectStateCd) {
		criteria.setProjectStateCd(projectStateCd);
	}

	public String getEventTypeCd() {
		return criteria.getEventTypeCd();
	}

	public void setEventTypeCd(String eventTypeCd) {
		criteria.setEventTypeCd(eventTypeCd);
	}

	public String getProjectTypeCd() {
		return criteria.getProjectTypeCd();
	}

	public void setProjectTypeCd(String projectTypeCd) {
		criteria.setProjectTypeCd(projectTypeCd);
	}

	public String getProjectNEPALevelCd() {
		return criteria.getProjectNEPALevelCd();
	}

	public void setProjectNEPALevelCd(String projectNEPALevelCd) {
		criteria.setProjectNEPALevelCd(projectNEPALevelCd);
	}

	public String getProjectStageCd() {
		return criteria.getProjectStageCd();
	}

	public void setProjectStageCd(String projectStageCd) {
		criteria.setProjectStageCd(projectStageCd);
	}

	public String getProjectOutreachCategoryCd() {
		return criteria.getProjectOutreachCategoryCd();
	}

	public void setProjectOutreachCategoryCd(String projectOutreachCategoryCd) {
		criteria.setProjectOutreachCategoryCd(projectOutreachCategoryCd);
	}

	public String getProjectGrantStatusCd() {
		return criteria.getProjectGrantStatusCd();
	}

	public void setProjectGrantStatusCd(String projectGrantStatusCd) {
		criteria.setProjectGrantStatusCd(projectGrantStatusCd);
	}

	public String getProjectLetterTypeCd() {
		return criteria.getProjectLetterTypeCd();
	}

	public void setProjectLetterTypeCd(String projectLetterTypeCd) {
		criteria.setProjectLetterTypeCd(projectLetterTypeCd);
	}

	public String getProjectContractTypeCd() {
		return criteria.getProjectContractTypeCd();
	}

	public void setProjectContractTypeCd(String projectContractTypeCd) {
		criteria.setProjectContractTypeCd(projectContractTypeCd);
	}

	public Integer getProjectLeadId() {
		return null == criteria.getProjectLeadId()? 
				null : Integer.parseInt(criteria.getProjectLeadId());
	}

	public void setProjectLeadId(Integer projectLeadId) {
		criteria.setProjectLeadId(null == projectLeadId? null : String.valueOf(projectLeadId));
	}

	public void reset() {
		criteria = new ProjectTrackingSearchCriteria();
		results = new ProjectSearchResult[0];
		resultsWrapper.setWrappedData(results);
		setHasSearchResults(false);
	}

	public ProjectTrackingService getProjectTrackingService() {
		return projectTrackingService;
	}

	public void setProjectTrackingService(
			ProjectTrackingService projectTrackingService) {
		this.projectTrackingService = projectTrackingService;
	}

	public boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public void setHasSearchResults(boolean hasSearchResults) {
		this.hasSearchResults = hasSearchResults;
	}

	public TableSorter getResultsWrapper() {
		return resultsWrapper;
	}

	public void setResultsWrapper(TableSorter resultsWrapper) {
		this.resultsWrapper = resultsWrapper;
	}

	public final DefSelectItems getProjectTypeTrackingEventDefs() {
		return null != getProjectTypeCd()?
				ProjectTrackingEventTypeDef.getProjectTypeTrackingEventDefs(getProjectTypeCd()) :
				ProjectTrackingEventTypeDef.getData().getItems();
	}


}
