package us.oh.state.epa.stars2.portal.compliance;

import java.rmi.RemoteException;
import java.sql.Date;

import us.oh.state.epa.stars2.bo.ComplianceReportService;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.def.ComplianceCemsTypeDef;
import us.oh.state.epa.stars2.def.ComplianceOneTypeDef;
import us.oh.state.epa.stars2.def.ComplianceOtherTypeDef;
import us.oh.state.epa.stars2.def.ComplianceReportAcceptedDef;
import us.oh.state.epa.stars2.def.ComplianceReportAllSubtypesDef;
import us.oh.state.epa.stars2.def.ComplianceReportSearchDateByDef;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.ComplianceSmbrTypeDef;
import us.oh.state.epa.stars2.def.ComplianceTestingTypeDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.YearsForOEPA;
import us.oh.state.epa.stars2.portal.facility.FacilityProfile;
import us.oh.state.epa.stars2.portal.home.MyTasks;
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

public class ComplianceReportSearch extends AppBase {
    private boolean hasSearchResults;
    private boolean singleFacility = true;
    private ComplianceReportList[] complianceReportList;

    //search parameters
    private String facilityName;
    private String facilityId;
    private String doLaaCd;
    private String reportStatus;
    private String reportAccepted;
    private String reportType;
    private String deviationsReported;
    private String reportingStartYear;
    private Date dtBegin;
    private Date dtEnd;
    private String dateBy;
    private String otherCategoryCd;
    private String popupRedirectOutcome;
    
    private boolean portal=false;
    private Integer reportId;
    private String reportCRPTId;
    
    private ComplianceReportService complianceReportService;
    
    private String fromFacility = "false";
    private boolean enforcementLink = false;
    
	public ComplianceReportService getComplianceReportService() {
		return complianceReportService;
	}

	public void setComplianceReportService(
			ComplianceReportService complianceReportService) {
		this.complianceReportService = complianceReportService;
	}
    public String getOtherCategoryCd() {
        return otherCategoryCd;
    }

    public void setOtherCategoryCd(String otherCategoryCd) {
        this.otherCategoryCd = otherCategoryCd;
    }

    public Date getdtBegin() {
        return dtBegin;
    }

    public void setDtBegin(Date dtBegin) {
        this.dtBegin = dtBegin;
    }

    public Date getdtEnd() {
        return dtEnd;
    }

    public void setDtEnd(Date dtEnd) {
        this.dtEnd = dtEnd;
    }

    public final DefSelectItems getReportingYearsDef() {
        return YearsForOEPA.getData().getItems();
    }
    
    public final DefSelectItems getComplianceCemsTypeDef() {
        return ComplianceCemsTypeDef.getData().getItems();
    }
    
    public final DefSelectItems getComplianceSmbrTypeDef() {
        return ComplianceSmbrTypeDef.getData().getItems();
    }
    
    public final DefSelectItems getComplianceOneTypeDef() {
        return ComplianceOneTypeDef.getData().getItems();
    }
    
    public final DefSelectItems getComplianceTestingTypeDef() {
        return ComplianceTestingTypeDef.getData().getItems();
    }
    
    public final DefSelectItems getComplianceOtherTypeDef() {
        return ComplianceOtherTypeDef.getData().getItems();
    }
    
    public final DefSelectItems getComplianceReportAllSubtypesDef() {
        return ComplianceReportAllSubtypesDef.getData().getItems();
    }
    
    public final DefSelectItems getDateFieldsDef() {
        return ComplianceReportSearchDateByDef.getData().getItems();
    }
    public final DefSelectItems getStatusDef() {
        return ComplianceReportStatusDef.getData().getItems();
    }
    
    public final DefSelectItems getAcceptedDef() {
        return ComplianceReportAcceptedDef.getData().getItems();
    }
    
    public String getReportingStartYear() {
        return reportingStartYear;
    }

    public void setReportingStartYear(String reportingStartYear) {
        this.reportingStartYear = reportingStartYear;
    }

    public String getFacilityComplianceReports() {
        logger.error("Attempting to get FP Bean");
        FacilityProfile fp = (FacilityProfile) FacesUtil
        .getManagedBean("facilityProfile");
        ComplianceReports cr = (ComplianceReports) FacesUtil
        .getManagedBean("complianceReport");
        logger.error("Gpt bean. setting facility and doinog search");
        cr.setFacility(fp.getFacility());
        this.reset();
        this.setSingleFacility(false);
        this.setFacilityId(fp.getFacilityId());
        //this.setReportStatus(ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED);
        // IMPACT - do not filter on status for portal search
        this.setReportStatus(null);
        this.submitSearch();
        this.setSingleFacility(true);
        
        logger.debug("returning nav rule");
        return "facilities.profile.compReports";
    }
    
    public String initializePortalSearch() {
        this.reset();   
        this.setSingleFacility(false);
        this.setPortal(true);
        
        ComplianceReports cr = (ComplianceReports) FacesUtil.getManagedBean("complianceReport");
        cr.setEditable(true);
        try {
            MyTasks myTask = (MyTasks) FacesUtil.getManagedBean("myTasks");
            this.setFacilityId(myTask.getFacility().getFacilityId());
            this.setReportStatus(ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED);
            this.submitSearch();
            this.setSingleFacility(true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            DisplayUtil.displayError("Search failed: System Error");
        }
        return "complianceSearch";
    }
    
    public String submitSearch() {
        complianceReportList = null;
        hasSearchResults = false;
       
        if (this.isSingleFacility()) {
            try {
                complianceReportList = getComplianceReportService().searchComplianceReportByFacility(facilityId);
                if (complianceReportList.length > 0) {
                    hasSearchResults = true;
                } else {
                	DisplayUtil.displayInfo("There are no Compliance Reports available for this facility.");
                }
            } catch (RemoteException re) {
                DisplayUtil.displayError("Search failed: System Error");
                logger.error(re.getMessage(), re);
            }
        } else {
            try {
                complianceReportList = getComplianceReportService().searchComplianceReports(reportCRPTId, facilityId, facilityName, doLaaCd, reportType, reportStatus, reportingStartYear, deviationsReported,dateBy,dtBegin,dtEnd,reportAccepted,otherCategoryCd, true, false, null, null, null, null);
                if (complianceReportList.length == 0) {
                    DisplayUtil
                            .displayInfo("Cannot find any reports for this search");
                } else {
                    hasSearchResults = true;
                }
            } catch (RemoteException re) {
                DisplayUtil.displayError("Search failed: System Error");
                logger.error(re.getMessage(), re);
            }
        }
        return SUCCESS;
    }

    public String reset() {
        doLaaCd = null;
        facilityName = null;
        facilityId = null;
        reportStatus = null;
        reportAccepted = null;
        complianceReportList = null;
        hasSearchResults = false;
        reportingStartYear = null;
        reportType = null;
        dtBegin = null;
        dtEnd = null;
        otherCategoryCd=null;
        dateBy = null;
        popupRedirectOutcome = null;
        reportId = null;
        reportCRPTId = null;
        return SUCCESS;  
    }
    
    public String resetPartial() {
        /*
         * leaves the seearch criteria in memory in case they want to repeat a search
         */
        complianceReportList = null;
        hasSearchResults = false;
        popupRedirectOutcome = null;
        return SUCCESS;
    }

    public final boolean getHasSearchResults() {
        return hasSearchResults;
    }

    public ComplianceReportList[] getComplianceReportList() {
        if (complianceReportList==null) {
            complianceReportList = new ComplianceReportList[0];
        }
        return complianceReportList;
    }

    public void setComplianceReportList(ComplianceReportList[] complianceReportList) {
        this.complianceReportList = complianceReportList;
    }

    public boolean isSingleFacility() {
        return singleFacility;
    }

    public void setSingleFacility(boolean singleFacility) {
        this.singleFacility = singleFacility;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDeviationsReported() {
        return deviationsReported;
    }

    public void setDeviationsReported(String deviationsReported) {
        this.deviationsReported = deviationsReported;
    }

    public String getReportAccepted() {
        return reportAccepted;
    }

    public void setReportAccepted(String reportAccepted) {
        this.reportAccepted = reportAccepted;
    }

    public String getDateBy() {
        return dateBy;
    }

    public void setDateBy(String dateBy) {
        this.dateBy = dateBy;
    }
    public final String getPopupRedirect() {
        if (popupRedirectOutcome != null) {
            FacesUtil.setOutcome(null, popupRedirectOutcome);
            popupRedirectOutcome = null;
        }
        return null;
    }

    public boolean isPortal() {
        return portal;
    }

    public void setPortal(boolean portal) {
        this.portal = portal;
    }

    /**
     * @return the reportId
     */
    public final Integer getReportId() {
        return reportId;
    }

    /**
     * @param reportId the reportId to set
     */
    public final void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
    
    /**
     * @return the reportCRPTId
     */
    public final String getReportCRPTId() {
        return reportCRPTId;
    }

    /**
     * @param reportCRPTId the reportCRPTId to set
     */
    public final void setReportCRPTId(String reportCRPTId) {
        this.reportCRPTId = reportCRPTId;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }
    
    public final String getFromFacility() {
        return fromFacility;
    }

    public final void setFromFacility(String fromFacility) {
        this.fromFacility = fromFacility;
    }
    
    public final boolean isEnforcementLink() {
		return enforcementLink;
	}

	public final void setEnforcementLink(boolean enforcementLink) {
		this.enforcementLink = enforcementLink;
	}

}
