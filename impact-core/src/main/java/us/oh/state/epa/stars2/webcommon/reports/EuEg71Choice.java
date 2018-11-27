package us.oh.state.epa.stars2.webcommon.reports;

import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEU;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportPeriod;
import us.oh.state.epa.stars2.def.ExemptStatusDef;

public class EuEg71Choice {
    private EmissionsReportEU eu;
    private ReportTemplates scRpts;
    private String epaEmuId;
    private boolean deMinimis;
    private boolean forceDetails;
    private String exemption;
    private String tvClassification;
    private boolean excludedDueToAttributes;
    private String description;
    private boolean hasProcessInfo;
    private boolean include;
    private Integer eg71Zero; // 0 - zero emissions (did not operate); 1 - eg#71
    private boolean inGroup;

    
    public EuEg71Choice(EmissionsReport rpt, EmissionsReportEU eu, ReportTemplates scReports) {
        this.eu = eu;
        this.scRpts = scReports;
        this.epaEmuId = eu.getEpaEmuId();
        this.deMinimis = scReports.getSc().isBelowRequirements(eu);
        this.forceDetails = eu.isForceDetailedReporting();
        this.exemption = scReports.getSc().exemptionReason(eu.getExemptStatusCd());
        this.tvClassification = scReports.getSc().tvClassificationReason(eu.getTvClassCd());
        excludedDueToAttributes = exemption.length() > 0 || tvClassification.length() > 0;
        this.description = eu.getCompanyId();
        hasProcessInfo = false;
        if(eu.getPeriods() != null) {
            for(EmissionsReportPeriod p : eu.getPeriods()) {
                if(!p.zeroHours()) {
                    hasProcessInfo = p.anyValuesSet()  || !p.zeroEmissions();
                    if(hasProcessInfo) {
                        break;
                    }
                }
            }
        }
        include = true;
        eg71Zero = null;
        if(!forceDetails) {
            if(eu.getExemptEG71()) { 
                include = false;
                eg71Zero = 1;
            }
            if(eu.isZeroEmissions()) {
                include = false;
                eg71Zero = 0;
            }
        }
        this.inGroup = rpt.belongsToGroup(eu.getCorrEpaEmuId());
    }
    
    public boolean isDeleteEmissions() {
        return !include && !forceDetails && eg71Zero != null && hasProcessInfo;
    }

    public String getDescription() {
        return description;
    }

    public String getEpaEmuId() {
        return epaEmuId;
    }

    public EmissionsReportEU getEu() {
        return eu;
    }

    public boolean isDeMinimis() {
        return deMinimis;
    }

    public boolean isInclude() {
        return include;
    }

    public void setInclude(boolean include) {
        if(include) {
            eg71Zero = null;
        }
        this.include = include;
    }

    public Integer getEg71Zero() {
        return eg71Zero;
    }

    public void setEg71Zero(Integer eg71Zero) {
        if(eg71Zero != null) {
            include = false;
        }
        this.eg71Zero = eg71Zero;
    }

    public boolean isInGroup() {
        return inGroup;
    }

    public boolean isForceDetails() {
        return forceDetails;
    }

    public void setForceDetails(boolean forceDetails) {
        this.forceDetails = forceDetails;
        if(forceDetails) {
            include = true;
            eg71Zero = null;
        } else {
            if(scRpts != null && scRpts.getSc() != null) {
                if(scRpts.getSc().isBelowRequirements(eu)) {
                    eg71Zero = new Integer(1);
                    include = false;
                }
            }
        }
    }

    public String getExemption() {
        return exemption;
    }

    public void setExemption(String exemption) {
        this.exemption = exemption;
    }

    public String getTvClassification() {
        return tvClassification;
    }

    public void setTvClassification(String tvClassification) {
        this.tvClassification = tvClassification;
    }

    public boolean isExcludedDueToAttributes() {
        return excludedDueToAttributes;
    }   
}