package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.RuleCitationDef;
import us.oh.state.epa.stars2.def.TVRuleCiteTypeDef;

@SuppressWarnings("serial")
public class TVApplicableReq extends BaseDB {
    private Integer tvApplicableReqId;
    private Integer applicationId;
    private Integer applicationEuId;
    private Integer tvEuGroupId;
    private String pollutantCd;
    private boolean stateOnly;
    private String tvRuleCiteTypeCd;
    
    private String allowableRuleCiteCd;
    private String allowablePermitCite;
    private String allowableValue;
    
    private String monitoringRuleCiteCd;
    private String monitoringPermitCite;
    private String monitoringValue;
    
    private String recordKeepingRuleCiteCd;
    private String recordKeepingPermitCite;
    private String recordKeepingValue;
    
    private String reportingRuleCiteCd;
    private String reportingPermitCite;
    private String tvCompRptFreqCd;
    private String reportingOtherDsc;
    
    private String testingRuleCiteCd;
    private String testingPermitCite;
    private String testingValue;
    
    private boolean complianceStatus = true;
    
    private boolean complianceObligationsStatus;
    
    private boolean proposedExemptionsStatus;
    
    private boolean proposedAltLimitsStatus;
    
    private boolean proposedTestChangesStatus;
    
    private List<TVCompliance> complianceReqs;
    private List<TVComplianceObligations> complianceObligationsReqs;
    private List<TVProposedExemptions> proposedExemptionsReqs;
    private List<TVProposedAltLimits> proposedAltLimitsReqs;
    private List<TVProposedTestChanges> proposedTestChangesReqs;
    
    public TVApplicableReq() {
        super();
    }

    public TVApplicableReq(TVApplicableReq old) {
        super(old);

        this.tvApplicableReqId = old.tvApplicableReqId;
        this.applicationId = old.applicationId;
        this.applicationEuId = old.applicationEuId;
        this.tvEuGroupId = old.tvEuGroupId;
        this.pollutantCd = old.pollutantCd;
        this.stateOnly = old.stateOnly;
        this.tvRuleCiteTypeCd = old.tvRuleCiteTypeCd;
        
        this.allowableRuleCiteCd = old.allowableRuleCiteCd;
        this.allowablePermitCite = old.allowablePermitCite;
        this.allowableValue = old.allowableValue;
        
        this.monitoringRuleCiteCd = old.monitoringRuleCiteCd;
        this.monitoringPermitCite = old.monitoringPermitCite;
        this.monitoringValue = old.monitoringValue;
        
        this.recordKeepingRuleCiteCd = old.recordKeepingRuleCiteCd;
        this.recordKeepingPermitCite = old.recordKeepingPermitCite;
        this.recordKeepingValue = old.recordKeepingValue;
        
        this.reportingRuleCiteCd = old.reportingRuleCiteCd;
        this.reportingPermitCite = old.reportingPermitCite;
        this.tvCompRptFreqCd = old.tvCompRptFreqCd;
        this.reportingOtherDsc = old.reportingOtherDsc;
        
        this.testingRuleCiteCd = old.testingRuleCiteCd;
        this.testingPermitCite = old.testingPermitCite;
        this.testingValue = old.testingValue;
        
        this.complianceStatus = old.complianceStatus;
        
        this.complianceObligationsStatus = old.complianceObligationsStatus;
        
        this.proposedExemptionsStatus = old.proposedExemptionsStatus;
        
        this.proposedAltLimitsStatus = old.proposedAltLimitsStatus;
        
        this.proposedTestChangesStatus = old.proposedTestChangesStatus;
    }
    
    public void populate(ResultSet rs) throws SQLException {
        setTvApplicableReqId(AbstractDAO.getInteger(rs, "tv_applicable_req_id"));
        setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
        setApplicationEuId(AbstractDAO.getInteger(rs, "application_eu_id"));
        setTvEuGroupId(AbstractDAO.getInteger(rs, "tv_eu_group_id"));
        setPollutantCd(rs.getString("pollutant_cd"));
        setStateOnly(AbstractDAO.translateIndicatorToBoolean(rs.getString("state_only")));
        setTvRuleCiteTypeCd(rs.getString("tv_rule_cite_type_cd"));
        
        setAllowableRuleCiteCd(rs.getString("allowable_rule_cite_cd"));
        setAllowablePermitCite(rs.getString("allowable_permit_cite"));
        setAllowableValue(rs.getString("allowable_value"));
        
        setMonitoringRuleCiteCd(rs.getString("monitoring_rule_cite_cd"));
        setMonitoringPermitCite(rs.getString("monitoring_permit_cite"));
        setMonitoringValue(rs.getString("monitoring_value"));
        
        setRecordKeepingRuleCiteCd(rs.getString("record_keeping_rule_cite_cd"));
        setRecordKeepingPermitCite(rs.getString("record_keeping_permit_cite"));
        setRecordKeepingValue(rs.getString("record_keeping_value"));
        
        setReportingRuleCiteCd(rs.getString("reporting_rule_cite_cd"));
        setReportingPermitCite(rs.getString("reporting_permit_cite"));
        setTvCompRptFreqCd(rs.getString("tv_comprpt_freq_cd"));
        setReportingOtherDsc(rs.getString("reporting_other_dsc"));
        
        setTestingRuleCiteCd(rs.getString("testing_rule_cite_cd"));
        setTestingPermitCite(rs.getString("testing_permit_cite"));
        setTestingValue(rs.getString("testing_value"));
        
        setComplianceStatus(AbstractDAO.translateIndicatorToBoolean(rs.getString("in_compliance_flag")));
        
        setComplianceObligationsStatus(AbstractDAO.translateIndicatorToBoolean(rs.getString("compliance_obligations_flag")));
        
        setProposedExemptionsStatus(AbstractDAO.translateIndicatorToBoolean(rs.getString("proposed_exemptions_flag")));
        
        setProposedAltLimitsStatus(AbstractDAO.translateIndicatorToBoolean(rs.getString("proposed_alt_limits_flag")));
        
        setProposedTestChangesStatus(AbstractDAO.translateIndicatorToBoolean(rs.getString("proposed_test_changes_flag")));
    }

    public final String getAllowablePermitCite() {
        return allowablePermitCite;
    }

    public final void setAllowablePermitCite(String allowablePermitCite) {
        this.allowablePermitCite = allowablePermitCite;
    }

    public final String getAllowableRuleCiteCd() {
        return allowableRuleCiteCd;
    }

    public final void setAllowableRuleCiteCd(String allowableRuleCiteCd) {
        this.allowableRuleCiteCd = allowableRuleCiteCd;
    }
    
    public final String getAllowableRuleCiteDsc() {
        return getDescForCode(allowableRuleCiteCd);
    }


    public final String getAllowableValue() {
        return allowableValue;
    }

    public final void setAllowableValue(String allowableValue) {
        this.allowableValue = allowableValue;
    }

    public final Integer getApplicationEuId() {
        return applicationEuId;
    }

    public final void setApplicationEuId(Integer applicationEuId) {
        this.applicationEuId = applicationEuId;
    }

    public final Integer getApplicationId() {
        return applicationId;
    }

    public final void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public final String getMonitoringPermitCite() {
        return monitoringPermitCite;
    }

    public final void setMonitoringPermitCite(String monitoringPermitCite) {
        this.monitoringPermitCite = monitoringPermitCite;
    }

    public final String getMonitoringRuleCiteCd() {
        return monitoringRuleCiteCd;
    }

    public final void setMonitoringRuleCiteCd(String monitoringRuleCiteCd) {
        this.monitoringRuleCiteCd = monitoringRuleCiteCd;
    }
    
    public final String getMonitoringRuleCiteDsc() {
        return getDescForCode(monitoringRuleCiteCd);
    }

    public final String getMonitoringValue() {
        return monitoringValue;
    }

    public final void setMonitoringValue(String monitoringValue) {
        this.monitoringValue = monitoringValue;
    }

    public final String getPollutantCd() {
        return pollutantCd;
    }

    public final void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

    public final String getRecordKeepingPermitCite() {
        return recordKeepingPermitCite;
    }

    public final void setRecordKeepingPermitCite(String recordKeepingPermitCite) {
        this.recordKeepingPermitCite = recordKeepingPermitCite;
    }

    public final String getRecordKeepingRuleCiteCd() {
        return recordKeepingRuleCiteCd;
    }

    public final void setRecordKeepingRuleCiteCd(String recordKeepingRuleCiteCd) {
        this.recordKeepingRuleCiteCd = recordKeepingRuleCiteCd;
    }    
    
    public final String getRecordKeepingRuleCiteDsc() {
        return getDescForCode(recordKeepingRuleCiteCd);
    }

    public final String getRecordKeepingValue() {
        return recordKeepingValue;
    }

    public final void setRecordKeepingValue(String recordKeepingValue) {
        this.recordKeepingValue = recordKeepingValue;
    }

    public final String getReportingPermitCite() {
        return reportingPermitCite;
    }

    public final void setReportingPermitCite(String reportingPermitCite) {
        this.reportingPermitCite = reportingPermitCite;
    }

    public final String getReportingRuleCiteCd() {
        return reportingRuleCiteCd;
    }

    public final void setReportingRuleCiteCd(String reportingRuleCiteCd) {
        this.reportingRuleCiteCd = reportingRuleCiteCd;
    }
    
    public final String getReportingRuleCiteDsc() {
        return getDescForCode(reportingRuleCiteCd);
    }
    
    public final String getDescForCode(String ruleCiteCd) {
        String reportingReqDsc = null;
        if (tvRuleCiteTypeCd != null && tvRuleCiteTypeCd != null) {
            if (tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.RULE)) {
                reportingReqDsc = RuleCitationDef.getData().getItems().getItemDesc(ruleCiteCd);
            } else if (tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.MACT)) {
                reportingReqDsc = PTIOMACTSubpartDef.getData().getItems().getItemDesc(ruleCiteCd);
            } else if (tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.NESHAP)) {
                reportingReqDsc = PTIONESHAPSSubpartDef.getData().getItems().getItemDesc(ruleCiteCd);
            } else if (tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.NSPS)) {
                reportingReqDsc = PTIONSPSSubpartDef.getData().getItems().getItemDesc(ruleCiteCd);
            }
        }
        return reportingReqDsc;
    }

    public final String getTvCompRptFreqCd() {
        return tvCompRptFreqCd;
    }

    public final void setTvCompRptFreqCd(String reportingValue) {
        this.tvCompRptFreqCd = reportingValue;
    }

    public final boolean isStateOnly() {
        return stateOnly;
    }

    public final void setStateOnly(boolean stateOnly) {
        this.stateOnly = stateOnly;
    }

    public final String getTestingPermitCite() {
        return testingPermitCite;
    }

    public final void setTestingPermitCite(String testingPermitCite) {
        this.testingPermitCite = testingPermitCite;
    }

    public final String getTestingRuleCiteCd() {
        return testingRuleCiteCd;
    }

    public final void setTestingRuleCiteCd(String testingRuleCiteCd) {
        this.testingRuleCiteCd = testingRuleCiteCd;
    }
    
    public final String getTestingRuleCiteDsc() {
        return getDescForCode(testingRuleCiteCd);
    }

    public final String getTestingValue() {
        return testingValue;
    }

    public final void setTestingValue(String testingValue) {
        this.testingValue = testingValue;
    }

    public final Integer getTvApplicableReqId() {
        return tvApplicableReqId;
    }

    public final void setTvApplicableReqId(Integer tvApplicableReqId) {
        this.tvApplicableReqId = tvApplicableReqId;
    }

    public final Integer getTvEuGroupId() {
        return tvEuGroupId;
    }

    public final void setTvEuGroupId(Integer tvEuGroupId) {
        this.tvEuGroupId = tvEuGroupId;
    }

    public final String getTvRuleCiteTypeCd() {
        return tvRuleCiteTypeCd;
    }

    public final void setTvRuleCiteTypeCd(String tvRuleCiteTypeCd) {
        this.tvRuleCiteTypeCd = tvRuleCiteTypeCd;
    }
    
    // flags used to determine type of rule cite in popup
    public boolean isRuleCitation() {
        return tvRuleCiteTypeCd != null && tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.RULE);
    }
    public boolean isMactCitation() {
        return tvRuleCiteTypeCd != null && tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.MACT);
    }
    public boolean isNeshapCitation() {
        return tvRuleCiteTypeCd != null && tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.NESHAP);
    }
    public boolean isNspsCitation() {
        return tvRuleCiteTypeCd != null && tvRuleCiteTypeCd.equals(TVRuleCiteTypeDef.NSPS);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
                * result
                + ((allowablePermitCite == null) ? 0 : allowablePermitCite
                        .hashCode());
        result = prime
                * result
                + ((allowableRuleCiteCd == null) ? 0 : allowableRuleCiteCd
                        .hashCode());
        result = prime * result
                + ((allowableValue == null) ? 0 : allowableValue.hashCode());
        result = prime * result
                + ((applicationEuId == null) ? 0 : applicationEuId.hashCode());
        result = prime * result
                + ((applicationId == null) ? 0 : applicationId.hashCode());
        result = prime
                * result
                + ((complianceObligationsReqs == null) ? 0
                        : complianceObligationsReqs.hashCode());
        result = prime * result + (complianceObligationsStatus ? 1231 : 1237);
        result = prime * result
                + ((complianceReqs == null) ? 0 : complianceReqs.hashCode());
        result = prime * result + (complianceStatus ? 1231 : 1237);
        result = prime
                * result
                + ((monitoringPermitCite == null) ? 0 : monitoringPermitCite
                        .hashCode());
        result = prime
                * result
                + ((monitoringRuleCiteCd == null) ? 0 : monitoringRuleCiteCd
                        .hashCode());
        result = prime * result
                + ((monitoringValue == null) ? 0 : monitoringValue.hashCode());
        result = prime * result
                + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
        result = prime
                * result
                + ((proposedAltLimitsReqs == null) ? 0 : proposedAltLimitsReqs
                        .hashCode());
        result = prime * result + (proposedAltLimitsStatus ? 1231 : 1237);
        result = prime
                * result
                + ((proposedExemptionsReqs == null) ? 0
                        : proposedExemptionsReqs.hashCode());
        result = prime * result + (proposedExemptionsStatus ? 1231 : 1237);
        result = prime
                * result
                + ((proposedTestChangesReqs == null) ? 0
                        : proposedTestChangesReqs.hashCode());
        result = prime * result + (proposedTestChangesStatus ? 1231 : 1237);
        result = prime
                * result
                + ((recordKeepingPermitCite == null) ? 0
                        : recordKeepingPermitCite.hashCode());
        result = prime
                * result
                + ((recordKeepingRuleCiteCd == null) ? 0
                        : recordKeepingRuleCiteCd.hashCode());
        result = prime
                * result
                + ((recordKeepingValue == null) ? 0 : recordKeepingValue
                        .hashCode());
        result = prime
                * result
                + ((reportingOtherDsc == null) ? 0 : reportingOtherDsc
                        .hashCode());
        result = prime
                * result
                + ((reportingPermitCite == null) ? 0 : reportingPermitCite
                        .hashCode());
        result = prime
                * result
                + ((reportingRuleCiteCd == null) ? 0 : reportingRuleCiteCd
                        .hashCode());
        result = prime * result + (stateOnly ? 1231 : 1237);
        result = prime
                * result
                + ((testingPermitCite == null) ? 0 : testingPermitCite
                        .hashCode());
        result = prime
                * result
                + ((testingRuleCiteCd == null) ? 0 : testingRuleCiteCd
                        .hashCode());
        result = prime * result
                + ((testingValue == null) ? 0 : testingValue.hashCode());
        result = prime
                * result
                + ((tvApplicableReqId == null) ? 0 : tvApplicableReqId
                        .hashCode());
        result = prime * result
                + ((tvCompRptFreqCd == null) ? 0 : tvCompRptFreqCd.hashCode());
        result = prime * result
                + ((tvEuGroupId == null) ? 0 : tvEuGroupId.hashCode());
        result = prime
                * result
                + ((tvRuleCiteTypeCd == null) ? 0 : tvRuleCiteTypeCd.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TVApplicableReq other = (TVApplicableReq) obj;
        if (allowablePermitCite == null) {
            if (other.allowablePermitCite != null)
                return false;
        } else if (!allowablePermitCite.equals(other.allowablePermitCite))
            return false;
        if (allowableRuleCiteCd == null) {
            if (other.allowableRuleCiteCd != null)
                return false;
        } else if (!allowableRuleCiteCd.equals(other.allowableRuleCiteCd))
            return false;
        if (allowableValue == null) {
            if (other.allowableValue != null)
                return false;
        } else if (!allowableValue.equals(other.allowableValue))
            return false;
        if (applicationEuId == null) {
            if (other.applicationEuId != null)
                return false;
        } else if (!applicationEuId.equals(other.applicationEuId))
            return false;
        if (applicationId == null) {
            if (other.applicationId != null)
                return false;
        } else if (!applicationId.equals(other.applicationId))
            return false;
        if (complianceObligationsReqs == null) {
            if (other.complianceObligationsReqs != null)
                return false;
        } else if (!complianceObligationsReqs
                .equals(other.complianceObligationsReqs))
            return false;
        if (complianceObligationsStatus != other.complianceObligationsStatus)
            return false;
        if (complianceReqs == null) {
            if (other.complianceReqs != null)
                return false;
        } else if (!complianceReqs.equals(other.complianceReqs))
            return false;
        if (complianceStatus != other.complianceStatus)
            return false;
        if (monitoringPermitCite == null) {
            if (other.monitoringPermitCite != null)
                return false;
        } else if (!monitoringPermitCite.equals(other.monitoringPermitCite))
            return false;
        if (monitoringRuleCiteCd == null) {
            if (other.monitoringRuleCiteCd != null)
                return false;
        } else if (!monitoringRuleCiteCd.equals(other.monitoringRuleCiteCd))
            return false;
        if (monitoringValue == null) {
            if (other.monitoringValue != null)
                return false;
        } else if (!monitoringValue.equals(other.monitoringValue))
            return false;
        if (pollutantCd == null) {
            if (other.pollutantCd != null)
                return false;
        } else if (!pollutantCd.equals(other.pollutantCd))
            return false;
        if (proposedAltLimitsReqs == null) {
            if (other.proposedAltLimitsReqs != null)
                return false;
        } else if (!proposedAltLimitsReqs.equals(other.proposedAltLimitsReqs))
            return false;
        if (proposedAltLimitsStatus != other.proposedAltLimitsStatus)
            return false;
        if (proposedExemptionsReqs == null) {
            if (other.proposedExemptionsReqs != null)
                return false;
        } else if (!proposedExemptionsReqs.equals(other.proposedExemptionsReqs))
            return false;
        if (proposedExemptionsStatus != other.proposedExemptionsStatus)
            return false;
        if (proposedTestChangesReqs == null) {
            if (other.proposedTestChangesReqs != null)
                return false;
        } else if (!proposedTestChangesReqs
                .equals(other.proposedTestChangesReqs))
            return false;
        if (proposedTestChangesStatus != other.proposedTestChangesStatus)
            return false;
        if (recordKeepingPermitCite == null) {
            if (other.recordKeepingPermitCite != null)
                return false;
        } else if (!recordKeepingPermitCite
                .equals(other.recordKeepingPermitCite))
            return false;
        if (recordKeepingRuleCiteCd == null) {
            if (other.recordKeepingRuleCiteCd != null)
                return false;
        } else if (!recordKeepingRuleCiteCd
                .equals(other.recordKeepingRuleCiteCd))
            return false;
        if (recordKeepingValue == null) {
            if (other.recordKeepingValue != null)
                return false;
        } else if (!recordKeepingValue.equals(other.recordKeepingValue))
            return false;
        if (reportingOtherDsc == null) {
            if (other.reportingOtherDsc != null)
                return false;
        } else if (!reportingOtherDsc.equals(other.reportingOtherDsc))
            return false;
        if (reportingPermitCite == null) {
            if (other.reportingPermitCite != null)
                return false;
        } else if (!reportingPermitCite.equals(other.reportingPermitCite))
            return false;
        if (reportingRuleCiteCd == null) {
            if (other.reportingRuleCiteCd != null)
                return false;
        } else if (!reportingRuleCiteCd.equals(other.reportingRuleCiteCd))
            return false;
        if (stateOnly != other.stateOnly)
            return false;
        if (testingPermitCite == null) {
            if (other.testingPermitCite != null)
                return false;
        } else if (!testingPermitCite.equals(other.testingPermitCite))
            return false;
        if (testingRuleCiteCd == null) {
            if (other.testingRuleCiteCd != null)
                return false;
        } else if (!testingRuleCiteCd.equals(other.testingRuleCiteCd))
            return false;
        if (testingValue == null) {
            if (other.testingValue != null)
                return false;
        } else if (!testingValue.equals(other.testingValue))
            return false;
        if (tvApplicableReqId == null) {
            if (other.tvApplicableReqId != null)
                return false;
        } else if (!tvApplicableReqId.equals(other.tvApplicableReqId))
            return false;
        if (tvCompRptFreqCd == null) {
            if (other.tvCompRptFreqCd != null)
                return false;
        } else if (!tvCompRptFreqCd.equals(other.tvCompRptFreqCd))
            return false;
        if (tvEuGroupId == null) {
            if (other.tvEuGroupId != null)
                return false;
        } else if (!tvEuGroupId.equals(other.tvEuGroupId))
            return false;
        if (tvRuleCiteTypeCd == null) {
            if (other.tvRuleCiteTypeCd != null)
                return false;
        } else if (!tvRuleCiteTypeCd.equals(other.tvRuleCiteTypeCd))
            return false;
        return true;
    }

    public final boolean isComplianceObligationsStatus() {
        return complianceObligationsStatus;
    }

    public final void setComplianceObligationsStatus(
            boolean complianceObligationsStatus) {
        this.complianceObligationsStatus = complianceObligationsStatus;
    }

    public final boolean isComplianceStatus() {
        return complianceStatus;
    }

    public final void setComplianceStatus(boolean complianceStatus) {
        this.complianceStatus = complianceStatus;
    }

    public final boolean isProposedAltLimitsStatus() {
        return proposedAltLimitsStatus;
    }

    public final void setProposedAltLimitsStatus(boolean proposedAltLimitsStatus) {
        this.proposedAltLimitsStatus = proposedAltLimitsStatus;
    }

    public final boolean isProposedExemptionsStatus() {
        return proposedExemptionsStatus;
    }

    public final void setProposedExemptionsStatus(boolean proposedExemptionsStatus) {
        this.proposedExemptionsStatus = proposedExemptionsStatus;
    }

    public final boolean isProposedTestChangesStatus() {
        return proposedTestChangesStatus;
    }

    public final void setProposedTestChangesStatus(boolean proposedTestChangesStatus) {
        this.proposedTestChangesStatus = proposedTestChangesStatus;
    }

    public final List<TVComplianceObligations> getComplianceObligationsReqs() {
        if (complianceObligationsReqs == null) {
            complianceObligationsReqs = new ArrayList<TVComplianceObligations>();
        }
        return complianceObligationsReqs;
    }

    public final void setComplianceObligationsReqs(
            List<TVComplianceObligations> complianceObligationsReqs) {
        this.complianceObligationsReqs = new ArrayList<TVComplianceObligations>();
        if (complianceObligationsReqs != null) {
            this.complianceObligationsReqs.addAll(complianceObligationsReqs);
        }
    }

    public final List<TVCompliance> getComplianceReqs() {
        if (complianceReqs == null) {
            complianceReqs = new ArrayList<TVCompliance>();
        }
        return complianceReqs;
    }

    public final void setComplianceReqs(List<TVCompliance> complianceReqs) {
        this.complianceReqs = new ArrayList<TVCompliance>();
        if (complianceReqs != null) {
            this.complianceReqs.addAll(complianceReqs);
        }
    }

    public final List<TVProposedAltLimits> getProposedAltLimitsReqs() {
        if (proposedAltLimitsReqs == null) {
            proposedAltLimitsReqs = new ArrayList<TVProposedAltLimits>();
        }
        return proposedAltLimitsReqs;
    }

    public final void setProposedAltLimitsReqs(
            List<TVProposedAltLimits> proposedAltLimitsReqs) {
        this.proposedAltLimitsReqs = new ArrayList<TVProposedAltLimits>();
        if (proposedAltLimitsReqs != null) {
            this.proposedAltLimitsReqs.addAll(proposedAltLimitsReqs);
        }
    }

    public final List<TVProposedExemptions> getProposedExemptionsReqs() {
        if (proposedExemptionsReqs == null) {
            proposedExemptionsReqs = new ArrayList<TVProposedExemptions>();
        }
        return proposedExemptionsReqs;
    }

    public final void setProposedExemptionsReqs(
            List<TVProposedExemptions> proposedExemptionsReqs) {
        this.proposedExemptionsReqs = new ArrayList<TVProposedExemptions>();
        if (proposedExemptionsReqs != null) {
            this.proposedExemptionsReqs.addAll(proposedExemptionsReqs);
        }
    }

    public final List<TVProposedTestChanges> getProposedTestChangesReqs() {
        if (proposedTestChangesReqs == null) {
            proposedTestChangesReqs = new ArrayList<TVProposedTestChanges>();
        }
        return proposedTestChangesReqs;
    }

    public final void setProposedTestChangesReqs(
            List<TVProposedTestChanges> proposedTestChangesReqs) {
            this.proposedTestChangesReqs = new ArrayList<TVProposedTestChanges>();
            if (proposedTestChangesReqs != null) {
                this.proposedTestChangesReqs.addAll(proposedTestChangesReqs);
            }
    }

    public final String getReportingOtherDsc() {
        return reportingOtherDsc;
    }

    public final void setReportingOtherDsc(String reportingOtherDsc) {
        this.reportingOtherDsc = reportingOtherDsc;
    }

}
