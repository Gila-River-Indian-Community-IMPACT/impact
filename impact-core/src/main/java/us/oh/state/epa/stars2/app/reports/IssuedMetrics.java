package us.oh.state.epa.stars2.app.reports;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.ReportService;
import us.oh.state.epa.stars2.database.dao.reports.ReportsSQLDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuedMetricsData;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.PermitActionTypeDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.wy.state.deq.impact.def.IssuedMetricsBenchmarkDef;

@SuppressWarnings("serial")
public class IssuedMetrics extends AppBase {

    private List<String> selectedDoLaas = new ArrayList<String>();
    private String selectedPermitType;
    private List<String> selectedReasonCds = new ArrayList<String>();
    private List<String> selectedCountyCds = new ArrayList<String>();
    private List<String> selectedPermitActionTypes = new ArrayList<String>();
    
    private String doLaaCd;
    private String permitTypeCd;
    private String reasonCd;
    private String dateBy;
    private Timestamp fromDate;
    private Timestamp toDate;
    
    private IssuedMetricsData[] details;
    private IssuedMetricsData[] notIssuedDetails;
    private IssuedMetricsData[] analysisDetails;
    private IssuedMetricsData[] notIssuedAnalysisDetails;
    private boolean hasSearchResults;
    private boolean hasNotIssuedSearchResults;
    private boolean showChartResults;
    private String hideShowTitle = "Show Charts";
    private boolean showList;
    private boolean showNotes;
    private ReportService reportService;
    private boolean excludeDeadEnded;
    private boolean excludeIssuedWithdrawal;
    
    public static enum PermitType {
        WAIVER, PSD, MINORNSR, TV, UNKNOWN
    }

    public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}
    
    public IssuedMetrics() {
        super();
        reset();
    }
    
    public final String submit() {

        try {
            details = getReportService().retrieveIssuedMetricsData(
                    selectedDoLaas, selectedPermitType, selectedReasonCds,
                    selectedCountyCds, selectedPermitActionTypes, dateBy,
                    fromDate, toDate, showNotes);

            hasSearchResults = true;
            showChartResults = false;
            hideShowTitle = "Show Charts";
            
            analyzeIssuedMetricsData();
            
            notIssuedDetails = getReportService().retrieveNotIssuedMetricsData(
            		selectedDoLaas, selectedPermitType, selectedReasonCds,
                    selectedCountyCds, selectedPermitActionTypes, dateBy,
                    fromDate, toDate, showNotes, excludeDeadEnded, excludeIssuedWithdrawal);
            
            hasNotIssuedSearchResults = true;
            
            analyzeNotIssuedMetricsData();
            
        } 
        catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil.displayError("System error: " + re.getMessage());
        }

        return SUCCESS;
    }
    
    private BigDecimal getIssuedMetricsBenchmarkDays(String code) {

    	BigDecimal ret = null;

    	BaseDef tmpDef = IssuedMetricsBenchmarkDef.getData().getItems().getItem(code);
    	if (tmpDef != null) {
    		IssuedMetricsBenchmarkDef tmpIssuedMetricsBenchmarkDef = (IssuedMetricsBenchmarkDef)tmpDef;
    		ret = tmpIssuedMetricsBenchmarkDef.getBenchmarkDays();
    	}

    	return ret;
    }
    
    public void analyzeIssuedMetricsData() {

		// Consolidate the Permit IssuedMetricsData (this.details) (which has one 
    	// entry per permit) into a Permit Type level view (single entry per permit
    	// type) ... one entry each for Waivers, PSD, Minor NSR (non-PSD).
    	
    	if (details == null) {
    		return;
    	}
    	
		HashMap<PermitType, IssuedMetricsData> psummaryData = new HashMap<PermitType, IssuedMetricsData>();
	
		for (IssuedMetricsData pdet : details) {
			
			if (pdet == null) {
				continue;
			}
			
			BigDecimal daysWaiversBenchmark = null;
			BigDecimal daysPsdBenchmark = null;
			BigDecimal daysMinorNsrBenchmark = null;
			BigDecimal daysTitleVBenchmark = null;

			daysWaiversBenchmark = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.WAIVER);
			daysPsdBenchmark = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.PSD);
			daysMinorNsrBenchmark = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.MINOR_NSR);
			daysTitleVBenchmark = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.TV);
			
			PermitType pType = PermitType.UNKNOWN;
			String descLabel = "";
			Integer benchmarkDays = 0;
			
			if (pdet != null && pdet.getPermitType().equals(PermitTypeDef.NSR)) {
				if (pdet != null && pdet.getPermitActionType() != null) {
					if ((pdet.getPermitActionType()).equals("1")) { // Waiver
						pType = PermitType.WAIVER;
						descLabel = "Waivers";
						benchmarkDays = daysWaiversBenchmark.intValue();
					} else if ((pdet.getPermitActionType()).equals("0")) { // Permit
						if (pdet.getSubjectToPsd() != null) {
							if (pdet.getSubjectToPsd().equals("Y")) {
								pType = PermitType.PSD;
								descLabel = "PSD";
								benchmarkDays = daysPsdBenchmark.intValue();
							} else if (pdet.getSubjectToPsd().equals("N")) {
								pType = PermitType.MINORNSR;
								descLabel = "Minor NSR";
								benchmarkDays = daysMinorNsrBenchmark
										.intValue();
							}
						}
					}
				}
			} else {
				pType = PermitType.TV;
				descLabel = "Title V";
				benchmarkDays = daysTitleVBenchmark.intValue();
			}
			
			IssuedMetricsData psum = psummaryData.get(pType);

			if (psum == null) {
				psummaryData.put(pType, pdet);
				psum = psummaryData.get(pType);
				psum.setPermitCount(new Integer(1));
				psum.setExcessivePreliminaryCount(new Integer(0));
				psum.setExcessiveCount(new Integer(0));
				psum.setTotalAgencyDaysForAnalysis(new Long(0));
				psum.setTotalNonAgencyDaysForAnalysis(new Long(0));
				psum.setAverageAqdDays(new Double(0));
				psum.setAverageIssuanceDays(new Double(0));
			} else {
				psum.setPermitCount(new Integer(psum.getPermitCount() + 1));
			}
			
			psum.setDescLabel(descLabel);
			psum.setBenchmarkDays(benchmarkDays);

			psum.setTotalAgencyDaysForAnalysis(new Long(psum.getTotalAgencyDaysForAnalysis() + pdet.getTotalAgencyDays()));
			psum.setTotalNonAgencyDaysForAnalysis(new Long(psum.getTotalNonAgencyDaysForAnalysis() + pdet.getTotalNonAgencyDays()));
				
			if (pdet.getTotalAgencyDays() > benchmarkDays) {
				psum.setExcessiveCount(new Integer(psum.getExcessiveCount() + 1));
			}
			
			BigDecimal daysThroughCompletionReview = null;

			daysThroughCompletionReview = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.COMP);
			if (pdet.getPreliminaryDays() > daysThroughCompletionReview.longValue()) {
				psum.setExcessivePreliminaryCount(new Integer(psum.getExcessivePreliminaryCount() + 1));
			}

			psum.setPrelimBenchmarkDays(daysThroughCompletionReview.intValue());
			
			if (psum.getPermitCount() > 0) {
				Double totalAgencyDaysForAnalysis = psum.getTotalAgencyDaysForAnalysis().doubleValue();
				psum.setAverageAqdDays(new Double((totalAgencyDaysForAnalysis / psum.getPermitCount())));
				Double getTotalNonAgencyDaysForAnalysis = psum.getTotalNonAgencyDaysForAnalysis().doubleValue();
				psum.setAverageIssuanceDays(new Double ((totalAgencyDaysForAnalysis + getTotalNonAgencyDaysForAnalysis) / psum.getPermitCount()));
			}
		}

		analysisDetails = psummaryData.values().toArray(new IssuedMetricsData[0]);
		
	}
    
    public void analyzeNotIssuedMetricsData() {

		// Consolidate the Permit IssuedMetricsData (this.details) (which has one 
    	// entry per permit) into a Permit Type level view (single entry per permit
    	// type) ... one entry each for Waivers, PSD, Minor NSR (non-PSD).
    	
    	if (notIssuedDetails == null) {
    		return;
    	}
    	
		HashMap<PermitType, IssuedMetricsData> psummaryData = new HashMap<PermitType, IssuedMetricsData>();
	
		for (IssuedMetricsData pdet : notIssuedDetails) {
			
			if (pdet == null) {
				continue;
			}
			
			BigDecimal daysWaiversBenchmark = null;
			BigDecimal daysPsdBenchmark = null;
			BigDecimal daysMinorNsrBenchmark = null;
			BigDecimal daysTitleVBenchmark = null;

			daysWaiversBenchmark = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.WAIVER);
			daysPsdBenchmark = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.PSD);
			daysMinorNsrBenchmark = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.MINOR_NSR);
			daysTitleVBenchmark = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.TV);
			
			PermitType pType = PermitType.UNKNOWN;
			String descLabel = "";
			Integer benchmarkDays = 0;
			
			if (pdet != null && pdet.getPermitType().equals(PermitTypeDef.NSR)) {
				if (pdet != null && pdet.getPermitActionType() != null) {
					if ((pdet.getPermitActionType()).equals("1")) { // Waiver
						pType = PermitType.WAIVER;
						descLabel = "Waivers";
						benchmarkDays = daysWaiversBenchmark.intValue();
						;
					} else if ((pdet.getPermitActionType()).equals("0")) { // Permit
						if (pdet.getSubjectToPsd() != null) {
							if (pdet.getSubjectToPsd().equals("Y")) {
								pType = PermitType.PSD;
								descLabel = "PSD";
								benchmarkDays = daysPsdBenchmark.intValue();
								;
							} else if (pdet.getSubjectToPsd().equals("N")) {
								pType = PermitType.MINORNSR;
								descLabel = "Minor NSR";
								benchmarkDays = daysMinorNsrBenchmark
										.intValue();
							}
						}
					}
				}
			} else {
				pType = PermitType.TV;
				descLabel = "Title V";
				benchmarkDays = daysTitleVBenchmark.intValue();
			}
			
			IssuedMetricsData psum = psummaryData.get(pType);

			if (psum == null) {
				psummaryData.put(pType, pdet);
				psum = psummaryData.get(pType);
				psum.setPermitCount(new Integer(1));
				psum.setExcessivePreliminaryCount(new Integer(0));
				psum.setExcessiveCount(new Integer(0));
				psum.setTotalAgencyDaysForAnalysis(new Long(0));
				psum.setTotalNonAgencyDaysForAnalysis(new Long(0));
				psum.setAverageAqdDays(new Double(0));
				psum.setAverageIssuanceDays(new Double(0));
			} else {
				psum.setPermitCount(new Integer(psum.getPermitCount() + 1));
			}
			
			psum.setDescLabel(descLabel);
			psum.setBenchmarkDays(benchmarkDays);

			psum.setTotalAgencyDaysForAnalysis(new Long(psum.getTotalAgencyDaysForAnalysis() + pdet.getTotalAgencyDays()));
			psum.setTotalNonAgencyDaysForAnalysis(new Long(psum.getTotalNonAgencyDaysForAnalysis() + pdet.getTotalNonAgencyDays()));
			
			if (pdet.getTotalAgencyDays() > benchmarkDays) {
				psum.setExcessiveCount(new Integer(psum.getExcessiveCount() + 1));
			}
			
			BigDecimal daysThroughCompletionReview = null;

			daysThroughCompletionReview = getIssuedMetricsBenchmarkDays(IssuedMetricsBenchmarkDef.COMP);
			
			if (pdet.getPreliminaryDays() > daysThroughCompletionReview.longValue()) {
				psum.setExcessivePreliminaryCount(new Integer(psum.getExcessivePreliminaryCount() + 1));
			}
			psum.setPrelimBenchmarkDays(daysThroughCompletionReview.intValue());
			
			if (psum.getPermitCount() > 0) {
				Double totalAgencyDaysForAnalysis = psum.getTotalAgencyDaysForAnalysis().doubleValue();
				psum.setAverageAqdDays(new Double((totalAgencyDaysForAnalysis / psum.getPermitCount())));
				Double getTotalNonAgencyDaysForAnalysis = psum.getTotalNonAgencyDaysForAnalysis().doubleValue();
				psum.setAverageIssuanceDays(new Double ((totalAgencyDaysForAnalysis + getTotalNonAgencyDaysForAnalysis) / psum.getPermitCount()));
			}
		}

		notIssuedAnalysisDetails = psummaryData.values().toArray(new IssuedMetricsData[0]);
		
	}

    public final String submitChart() {

        if (showChartResults) {
            showChartResults = false;
            hideShowTitle = "Show Charts";
        } else {
            showChartResults = true;
            hideShowTitle = "Hide Charts";
        }

        return SUCCESS;
    }

    public final String reset() {
        details = null;
        notIssuedDetails = null;
        analysisDetails = null;
        notIssuedAnalysisDetails = null;
        hasSearchResults = false;
        hasNotIssuedSearchResults = false;
        showChartResults = false;
        hideShowTitle = "Show Charts";

        selectedDoLaas = null;
        selectedPermitType = null;
        selectedReasonCds = null;
        selectedCountyCds = null;
        selectedPermitActionTypes = null;
        fromDate = null;
        toDate = null;
        
        excludeDeadEnded = true;
        excludeIssuedWithdrawal = true;
        
        dateBy = ReportsSQLDAO.END_DATE;
        
        showNotes = false;
        
        return SUCCESS;
    }
    
    public final String getDoLaaCd() {
        return doLaaCd;
    }

    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public final List<SelectItem> getDoLaas() {
        return DoLaaDef.getData().getItems().getAllSearchItems();
    }

    public final List<String> getSelectedDoLaas() {
        return selectedDoLaas;
    }

    public final void setSelectedDoLaas(List<String> selectedDoLaas) {
        this.selectedDoLaas = selectedDoLaas;
    }

    public final String getPermitTypeCd() {
        return permitTypeCd;
    }
    
    public final void setPermitTypeCd(String permitTypeCd) {
        this.permitTypeCd = permitTypeCd;
    }
    
    public final List<SelectItem> getPermitTypes() {
        List<SelectItem> ret = new ArrayList<SelectItem>();
        List<SelectItem> tmp = getPermitTypes(getPermitTypeCd());
        // 2427 remove the PBR from the issued metric's picklist
        for (SelectItem si : tmp)
            if ( 
            		//!si.getValue().equals(PermitTypeDef.SPTO)
                    //&& !si.getValue().equals(PermitTypeDef.REG)
                    //&& !si.getValue().equals(PermitTypeDef.RPE) &&
                     !si.getValue().equals(PermitTypeDef.RPR)
                    )
                ret.add(si);
        return ret;
    }
    
    public final List<SelectItem> getPermitActionTypes() {
        return PermitActionTypeDef.getData().getItems().getCurrentItems();
    }
    
    public final List<SelectItem> getPermitGlobalStatusDefs() {
        return PermitGlobalStatusDef.getData().getItems().getCurrentItems();
    }
    

    public final String getReasonCd() {
        return reasonCd;
    }

    public final void setReasonCd(String reasonCd) {
        this.reasonCd = reasonCd;
    }
    
    public final List<SelectItem> getReasonCds() {
		List<SelectItem> ret = PermitReasonsDef.getPermitReasons(getSelectedPermitType(),
				null);
		for (SelectItem si : ret)
			si.setDisabled(false);
		return ret;
	}

    public final List<String> getSelectedReasonCds() {
        return selectedReasonCds;
    }

    public final void setShowChartResults(boolean showChartResults) {
        this.showChartResults = showChartResults;
    }
    
	public final String getSelectedPermitType() {
		return selectedPermitType;
	}

	public final void setSelectedPermitType(String selectedPermitType) {
		this.selectedPermitType = selectedPermitType;
	}

    public final void setSelectedReasonCds(List<String> selectedReasonCds) {
        this.selectedReasonCds = selectedReasonCds;
    }


    public final boolean isHasSearchResults() {
        return hasSearchResults;
    }
    
    public final boolean isHasNotIssuedSearchResults() {
        return hasNotIssuedSearchResults;
    }

    public final boolean isShowChartResults() {
        return showChartResults && hasSearchResults;
    }

    public final void setHasChartResults(boolean showChartResults) {
        this.showChartResults = showChartResults;
    }

    public final void setHasSearchResults(boolean hasSearchResults) {
        this.hasSearchResults = hasSearchResults;
    }

    public final String getHideShowTitle() {
        return hideShowTitle;
    }

    public final void setHideShowTitle(String hideShowTitle) {
        this.hideShowTitle = hideShowTitle;
    }

    public final boolean isShowList() {
        return showList;
    }

    public final void setShowList(boolean showList) {
        this.showList = showList;
    }
    
    public final IssuedMetricsData[] getDetails() {
        return this.details;
    }

    public final void setDetails(IssuedMetricsData[] details) {
        this.details = details;
    }
    
    public final IssuedMetricsData[] getAnalysisDetails() {
        return this.analysisDetails;
    }

    public final void setAnalysisDetails(IssuedMetricsData[] details) {
        this.analysisDetails = details;
    }
    
    public final IssuedMetricsData[] getNotIssuedDetails() {
        return this.notIssuedDetails;
    }

    public final void setNotIssuedDetails(IssuedMetricsData[] details) {
        this.notIssuedDetails = details;
    }
    
    public final IssuedMetricsData[] getNotIssuedAnalysisDetails() {
        return this.notIssuedAnalysisDetails;
    }

    public final void setNotIssuedAnalysisDetails(IssuedMetricsData[] details) {
        this.notIssuedAnalysisDetails = details;
    }

    /**
     * @return the fromDate
     */
    public final Timestamp getFromDate() {
        return fromDate;
    }

    /**
     * @param fromDate the fromDate to set
     */
    public final void setFromDate(Timestamp fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * @return the toDate
     */
    public final Timestamp getToDate() {
        return toDate;
    }

    /**
     * @param toDate the toDate to set
     */
    public final void setToDate(Timestamp toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the showNotes
     */
    public final boolean isShowNotes() {
        return showNotes;
    }

    /**
     * @param showNotes the showNotes to set
     */
    public final void setShowNotes(boolean showNotes) {
        this.showNotes = showNotes;
    }

    public final String getWidth(){
        String ret = "1100";
        if (showNotes)
            ret = "2000";
        return ret;
    }
	
	public final List<String> getSelectedCountyCds() {
        return selectedCountyCds;
    }
	
	public final void setSelectedCountyCds(List<String> selectedCountyCds) {
        this.selectedCountyCds = selectedCountyCds;
    }
	
	public final List<String> getSelectedPermitActionTypes() {
        return selectedPermitActionTypes;
    }
	
	public final void setSelectedPermitActionTypes(List<String> selectedPermitActionTypes) {
        this.selectedPermitActionTypes = selectedPermitActionTypes;
    }
	
    public final boolean isExcludeDeadEnded() {
        return excludeDeadEnded;
    }
    
    public final void setExcludeDeadEnded(boolean excludeDeadEnded) {
        this.excludeDeadEnded = excludeDeadEnded;
    }
    
    public final boolean isExcludeIssuedWithdrawal() {
        return excludeIssuedWithdrawal;
    }
    
    public final void setExcludeIssuedWithdrawal(boolean excludeIssuedWithdrawal) {
        this.excludeIssuedWithdrawal = excludeIssuedWithdrawal;
    }
    
	public final LinkedHashMap<String, String> getPermitDateBy() {
		return buildPermitDateBy(permitTypeCd);
	}

	public static LinkedHashMap<String, String> buildPermitDateBy(
			String permitType) {
		LinkedHashMap<String, String> permitDateBy = new LinkedHashMap<String, String>();
		
		permitDateBy.put("Receipt Date", ReportsSQLDAO.START_DATE);
		permitDateBy.put("Public Notice Date",
				ReportsSQLDAO.PUBLIC_NOTICE_PUBLISH_DATE);
		permitDateBy.put("Final Issuance Date",
				ReportsSQLDAO.FINAL_ISSUANCE_DATE);
		permitDateBy.put("Workflow End Date", ReportsSQLDAO.END_DATE);
		return permitDateBy;
	}

	public final String getDateBy() {
		return dateBy;
	}

	public final void setDateBy(String dateBy) {
		if (dateBy == null || (dateBy != null && dateBy.trim().length() == 0)) {
			fromDate = null;
			toDate = null;
			dateBy = null;
		}
		this.dateBy = dateBy;
	}
}
