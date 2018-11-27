package us.oh.state.epa.stars2.app.compliance;

import java.rmi.RemoteException;
import java.sql.Date;

import us.oh.state.epa.stars2.app.facility.FacilityProfile;
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
import us.oh.state.epa.stars2.webcommon.AppBase;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

@SuppressWarnings("serial")
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
    
    private String facilityTypeCd;
    private String permitClassCd;
    
    private boolean portal=false;
    private Integer reportId;
    private String reportCRPTId;
    private boolean enforcementLink = false;
    private String cmpId;
    
    private String dapcReviewComments;
    
    private ComplianceReportService complianceReportService;
    
    public ComplianceReportSearch()
    {
    	super();
    	cacheViewIDs.add("/compliance/compReportSearch.jsp");
    }
    
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

//    public final List<SelectItem> getOtherCategoryCurrentChoices() {
//        if(otherCategoryCurrentChoices == null) {
//            otherCategoryCurrentChoices =  getOtherCategoryDefItems(ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER, true);
//        }
//        return otherCategoryCurrentChoices;
//    }
//    
//    public final List<SelectItem> getOtherCategoryAllChoices() {
//        if(otherCategoryAllChoices == null) {
//            otherCategoryAllChoices =  getOtherCategoryDefItems(ComplianceReportTypeDef.COMPLIANCE_TYPE_OTHER, false);
//        }
//        return otherCategoryAllChoices;
//    }
//    
//    public final List<SelectItem> getTestingCategoryCurrentChoices() {
//        if(testingCategoryCurrentChoices == null) {
//            testingCategoryCurrentChoices =  getOtherCategoryDefItems(ComplianceReportTypeDef.COMPLIANCE_TYPE_TESTING, true);
//        }
//        return testingCategoryCurrentChoices;
//    }
//    
//    public final List<SelectItem> getTestingCategoryAllChoices() {
//        if(testingCategoryAllChoices == null) {
//            testingCategoryAllChoices =  getOtherCategoryDefItems(ComplianceReportTypeDef.COMPLIANCE_TYPE_TESTING, false);
//        }
//        return testingCategoryAllChoices;
//    }
//    
//    public final List<SelectItem> getCemsCategoryCurrentChoices() {
//        if(cemsCategoryCurrentChoices == null) {
//            cemsCategoryCurrentChoices =  getOtherCategoryDefItems(ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS, true);
//        }
//        return cemsCategoryCurrentChoices;
//    }
//    
//    public final List<SelectItem> getCemsCategoryAllChoices() {
//        if(cemsCategoryAllChoices == null) {
//            cemsCategoryAllChoices =  getOtherCategoryDefItems(ComplianceReportTypeDef.COMPLIANCE_TYPE_CEMS, false);
//        }
//        return cemsCategoryAllChoices;
//    }
//    
//    private final List<SelectItem> getOtherCategoryDefItems(String type, boolean skipDep) {
//        Collection<BaseDef> c = ComplianceOtherTypeDef.getData().getItems().getCompleteItems();
//        ArrayList<ComplianceOtherTypeDef> array = new ArrayList<ComplianceOtherTypeDef>();
//        for(BaseDef bd : c) {
//            ComplianceOtherTypeDef cptd = (ComplianceOtherTypeDef)bd;
//            if(type.equals(cptd.getReportTypeCd())) {
//                if(!skipDep && !cptd.isDeprecated()) {
//                    array.add(cptd);
//                }
//            }
//        }
//        Collections.sort(array);
//        ArrayList<SelectItem> itemArray = new ArrayList<SelectItem>();
//        for(ComplianceOtherTypeDef cotd : array) {
//            SelectItem si = new SelectItem(cotd.getCode(), cotd.getDescription());
//            itemArray.add(si);
//        }
//        return itemArray;
//    }
    
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
        logger.debug("Attempting to get FP Bean");
        FacilityProfile fp = (FacilityProfile) FacesUtil
        .getManagedBean("facilityProfile");
        ComplianceReports cr = (ComplianceReports) FacesUtil
        .getManagedBean("complianceReport");
        logger.debug("Gpt bean. setting facility and doinog search");
        cr.setFacility(fp.getFacility());
        this.reset();
        this.setSingleFacility(true);
        this.setFacilityId(fp.getFacilityId());
        this.submitSearch();
        /*
if (applSearch.getApplications().length == 0) {
    DisplayUtil
            .displayInfo("This facility does not have any compliance reports");
}
*/
        logger.debug("returning nav rule");
        return "facilities.profile.compReports";
    }
    
    public String initializeSearch() {
        //this.resetPartial();
        this.setSingleFacility(false);
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
                handleException(re);
            }
        } else {
            try {
                complianceReportList = getComplianceReportService().searchComplianceReports(reportCRPTId, facilityId,
                        facilityName, doLaaCd, reportType, reportStatus, reportingStartYear, 
                        deviationsReported,dateBy,dtBegin,dtEnd,reportAccepted, otherCategoryCd, false, cmpId, permitClassCd, facilityTypeCd,
                        dapcReviewComments);
                if (complianceReportList.length == 0) {
                	 DisplayUtil.displayInfo("There are no Compliance Reports that match the search criteria.");
                	return null;
                   
                    
                } else {
                    hasSearchResults = true;
                }
            } catch (RemoteException re) {
                handleException(re);
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
        cmpId = null;
        permitClassCd = null;
        facilityTypeCd = null;
        dapcReviewComments = null;
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
    
    public final boolean isEnforcementLink() {
		return enforcementLink;
	}

	public final void setEnforcementLink(boolean enforcementLink) {
		this.enforcementLink = enforcementLink;
	}

	public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }
    
    public String getCmpId() {
		return cmpId;
	}

	public void setCmpId(String cmpId) {
		this.cmpId = cmpId;
	}
	
	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}

	public String getDapcReviewComments() {
		return dapcReviewComments;
	}

	public void setDapcReviewComments(String dapcReviewComments) {
		this.dapcReviewComments = dapcReviewComments;
	}
	
}
