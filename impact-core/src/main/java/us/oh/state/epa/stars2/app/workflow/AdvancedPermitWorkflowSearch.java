package us.oh.state.epa.stars2.app.workflow;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.math.NumberUtils;

import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.PredicateDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitWorkflowBenchmark;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitWorkflowSearchAnalysis;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitWorkflowSearchResult;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PermitWorkflowActivityBenchmarkDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;


@SuppressWarnings("serial")
public class AdvancedPermitWorkflowSearch extends AppBase {

	private String facilityId;
	private String facilityNm;
	private String permitNumber;
	private String applicationNumber;
	private Integer userId;
	private String permitType;
	private String activityNm;
	private String activityStatusCd;
	private Date startDateFrom;
	private Date startDateTo;
	private Date endDateFrom;
	private Date endDateTo;
	private Integer processId;
	private boolean filterSkipped;
	private boolean filterNonStarted;
	
	private PermitWorkflowSearchResult[] searchResults;
	
	private boolean hasSearchResults;
	
	private PermitService permitService;

	public PermitService getPermitService() {
		return permitService;
	}

	public void setPermitService(PermitService permitService) {
		this.permitService = permitService;
	}

	public PermitWorkflowSearchResult[] getSearchResults() {
		return searchResults;
	}
	
	public PermitWorkflowSearchAnalysis[] getSearchAnalyses() {
		PermitWorkflowSearchAnalysis[] searchAnalyses = 
				new PermitWorkflowSearchAnalysis[0];

		Map<String,PermitWorkflowSearchAnalysis> activityAnalyses = 
				new HashMap<String,PermitWorkflowSearchAnalysis>();

		PermitWorkflowActivityBenchmarkDef[] benchmarkDefs = null;
		try {
			benchmarkDefs = getPermitService().retrievePermitWorkflowActivityBenchmarkDefs();
		} catch (DAOException e) {
			logger.error(e);
			DisplayUtil.displayError("Unable to retrieve benchmark definitions.");
			return searchAnalyses;
		}
		
		if (null != searchResults && searchResults.length > 0) {
			
			for (PermitWorkflowSearchResult result : searchResults) {

				for (PermitWorkflowActivityBenchmarkDef benchmarkDef : benchmarkDefs) {
					if (!benchmarkDef.isDeprecated() && benchmarkDef.getActivityTemplateId().equals(
							NumberUtils.toInt(result.getActivityTemplateId(), -1))) {

						PermitWorkflowSearchAnalysis analysis = 
								activityAnalyses.get(result.getActivityTemplateId());
						if (null == analysis) {
							analysis = new PermitWorkflowSearchAnalysis();
							analysis.setActivityName(result.getActivityNm());
							activityAnalyses.put(result.getActivityTemplateId(), analysis);
						}
						analysis.getApplications().add(result.getApplicationNbr());
		
						PermitWorkflowBenchmark benchmark = analysis.getBenchmarks().get(benchmarkDef);
						if (null == benchmark) {
							benchmark = new PermitWorkflowBenchmark(benchmarkDef);
							analysis.getBenchmarks().put(benchmarkDef,benchmark);
						}
						
						Map<String,Integer> applicationDays = 
								analysis.getBenchmarkDays().get(benchmark);
						if (null == applicationDays) {
							applicationDays = new HashMap<String,Integer>();
							analysis.getBenchmarkDays().put(benchmark,applicationDays);
						}
						
						Integer days = applicationDays.get(result.getApplicationNbr());
						if (null == days) {
							days = result.getAgencyDays();
						} else {
							days += result.getAgencyDays();
						}
						applicationDays.put(result.getApplicationNbr(), days);
						logger.debug("benchmark = " + benchmark + 
								"; applicationNbr = " + result.getApplicationNbr() + 
								"; days = " + days);
					}
				}
			}
			for (PermitWorkflowSearchAnalysis analysis : activityAnalyses.values()) {
				for (PermitWorkflowBenchmark benchmark : analysis.getBenchmarks().values()) {
					Map<String,Integer> applicationDays = analysis.getBenchmarkDays().get(benchmark);
					if (null != applicationDays) {
						for (String applicationNbr : applicationDays.keySet()) {
							int days = applicationDays.get(applicationNbr);
							int value = benchmark.getValue();
							if (benchmark.getPredicate().equals(PredicateDef.GREATER_THAN)) {
								if (days > value) {
									benchmark.incrementApplicationCount();
								}
							} else
							if (benchmark.getPredicate().equals(PredicateDef.LESS_THAN)) {
								if (days < value) {
									benchmark.incrementApplicationCount();
								}
							} else
							if (benchmark.getPredicate().equals(PredicateDef.EQUALS)) {
								if (days == value) {
									benchmark.incrementApplicationCount();
								}
							} else
							if (benchmark.getPredicate().equals(PredicateDef.GREATER_THAN_OR_EQUALS)) {
								if (days >= value) {
									benchmark.incrementApplicationCount();
								}
							} else
							if (benchmark.getPredicate().equals(PredicateDef.LESS_THAN_OR_EQUALS)) {
								if (days <= value) {
									benchmark.incrementApplicationCount();
								}
							}
						}
					}
				}
			}
			searchAnalyses = activityAnalyses.values().toArray(searchAnalyses);
		}
		

		return searchAnalyses;
	}

    public final String submit() {
        try {
        	
            searchResults = getPermitService().searchPermitWorkflows(
            		facilityId, facilityNm, permitNumber, applicationNumber, 
            		userId, permitType, activityNm, activityStatusCd, 
            		startDateFrom, startDateTo, endDateFrom, 
            		endDateTo, processId, filterSkipped, filterNonStarted).toArray(new PermitWorkflowSearchResult[0]);
            
            DisplayUtil.displayHitLimit(searchResults.length);

            if (searchResults.length == 0) {
                DisplayUtil.displayNoRecords();
            }

            hasSearchResults = true;
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error. Please contact system administrator");
        }
        return SUCCESS;
    }
	
	public final String reset() {
		facilityId = null;
		facilityNm = null;
		permitNumber = null;
		applicationNumber = null;
        userId = null;
        permitType = null;
        activityNm = null;
        activityStatusCd = null;
        startDateFrom = null;
        startDateTo = null;
        endDateFrom = null;
        endDateTo = null;
        hasSearchResults = false;
        processId = null;
        filterNonStarted = true;
        filterSkipped = true;
        return SUCCESS;
    }
	
	public boolean isHasSearchResults() {
		return hasSearchResults;
	}

	public final List<SelectItem> getPermitTypes() {
		List<SelectItem> permitTypes = new ArrayList<SelectItem>();
		for (SelectItem si : PermitTypeDef.getData().getItems()
				.getItems(permitType, true)) {
			if (!si.getValue().equals(PermitTypeDef.NSR)
						&& !si.getValue().equals(PermitTypeDef.TV_PTO))
				continue;

			permitTypes.add(si);
		}

		return permitTypes;
	}


	public Integer getProcessId() {
		return processId;
	}

	public void setProcessId(Integer processId) {
		this.processId = processId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getFacilityNm() {
		return facilityNm;
	}

	public void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}

	public String getPermitNumber() {
		return permitNumber;
	}

	public void setPermitNumber(String permitNumber) {
		this.permitNumber = permitNumber;
	}

	public String getApplicationNumber() {
		return applicationNumber;
	}

	public void setApplicationNumber(String applicationNumber) {
		this.applicationNumber = applicationNumber;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getPermitType() {
		return permitType;
	}

	public void setPermitType(String permitType) {
		this.permitType = permitType;
	}

	public String getActivityNm() {
		return activityNm;
	}

	public void setActivityNm(String activityNm) {
		this.activityNm = activityNm;
	}

	public String getActivityStatusCd() {
		return activityStatusCd;
	}

	public void setActivityStatusCd(String activityStatusCd) {
		this.activityStatusCd = activityStatusCd;
	}

	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public void setStartDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public void setStartDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}

	public boolean isFilterSkipped() {
		return filterSkipped;
	}

	public void setFilterSkipped(boolean filterSkipped) {
		this.filterSkipped = filterSkipped;
	}

	public boolean isFilterNonStarted() {
		return filterNonStarted;
	}

	public void setFilterNonStarted(boolean filterNonStarted) {
		this.filterNonStarted = filterNonStarted;
	}
	
	
}
