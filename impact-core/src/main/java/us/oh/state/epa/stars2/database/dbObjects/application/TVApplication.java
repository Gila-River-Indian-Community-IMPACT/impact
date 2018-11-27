package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.TVApplicationPurposeDef;
import us.oh.state.epa.stars2.def.TVFacWideReqOptionDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class TVApplication extends Application {
	private String tvApplicationPurposeCd;
	private String operationsDsc;
	private String aqdTvApplicationNumber;
	private String nspsApplicableFlag;
	private String neshapApplicableFlag;
	private String mactApplicableFlag;
	private String psdApplicableFlag;
	private String nsrApplicableFlag;
	private String facilityWideRequirementFlag;
	private String proposedExemptions;
	private String subjectTo112R;
    private String planSubmittedUnder112R;
    private String subjectToTIV;
    private String ambientMonitoring;
	private String alternateOperatingScenarios;
	private boolean legacyStateTVApp;
	
	private Timestamp riskManagementPlanSubmitDate;
	private String complianceCertSubmitFrequency;
	private String complianceWithApplicableEnhancedMonitoring;
	private String complianceRequirementsNotMet;
	private String subjectToEngineConfigRestrictions;
	private String nsrPermitNumber;
	private String subjectToWAQSR;
	
    private String permitReasonCd;
    private List<TVApplicableReq> applicableRequirements;
    private List<TVApplicableReq> stateOnlyRequirements;
    private List<TVEUGroup> euGroups;
    private List<TVPteAdjustment> capPteTotals;
    private List<TVPteAdjustment> hapPteTotals;
    private List<TVPteAdjustment> ghgPteTotals;
    private List<TVPteAdjustment> othPteTotals;
	private List<FacilityWideRequirement> facilityWideRequirements;
	private List<String> nspsSubpartCodes;
	private List<String> neshapSubpartCodes;
	private List<String> mactSubpartCodes;

	/**
     * Copy Constructor
     * 
     * @param tVApplication
     *            a <code>TVApplication</code> object
     */
    public TVApplication(TVApplication app) {
        super(app);
        if (app != null) {
            this.tvApplicationPurposeCd = app.tvApplicationPurposeCd;
            this.permitReasonCd = app.permitReasonCd;
            this.operationsDsc = app.operationsDsc;
            this.aqdTvApplicationNumber = app.aqdTvApplicationNumber;
            this.nspsApplicableFlag = app.nspsApplicableFlag;
            this.neshapApplicableFlag = app.neshapApplicableFlag;
            this.mactApplicableFlag = app.mactApplicableFlag;
            this.psdApplicableFlag = app.psdApplicableFlag;
            this.nsrApplicableFlag = app.nsrApplicableFlag;
            this.facilityWideRequirementFlag = app.facilityWideRequirementFlag;
            this.proposedExemptions = app.proposedExemptions;
            this.subjectTo112R = app.subjectTo112R;
            this.planSubmittedUnder112R = app.planSubmittedUnder112R;
            this.subjectToTIV = app.subjectToTIV;
            this.ambientMonitoring = app.ambientMonitoring;
            this.riskManagementPlanSubmitDate = app.riskManagementPlanSubmitDate;
            this.complianceCertSubmitFrequency = app.complianceCertSubmitFrequency;
            this.complianceWithApplicableEnhancedMonitoring = app.complianceWithApplicableEnhancedMonitoring;
            this.complianceRequirementsNotMet = app.complianceRequirementsNotMet;
            this.subjectToEngineConfigRestrictions = app.subjectToEngineConfigRestrictions;
            this.nsrPermitNumber = app.nsrPermitNumber;
            this.subjectToWAQSR = app.subjectToWAQSR;
            setAlternateOperatingScenarios(app.getAlternateOperatingScenarios());
            setApplicableRequirements(app.getApplicableRequirements());
            setStateOnlyRequirements(app.getStateOnlyRequirements());
            setEuGroups(app.getEuGroups());
            setCapPteTotals(app.getCapPteTotals());
            setHapPteTotals(app.getHapPteTotals());
            setGhgPteTotals(app.getGhgPteTotals());
            setOthPteTotals(app.getOthPteTotals());
            setNspsSubpartCodes(app.getNspsSubpartCodes());
    		setNeshapSubpartCodes(app.getNeshapSubpartCodes());
    		setMactSubpartCodes(app.getMactSubpartCodes());
    		setLegacyStateTVApp(app.isLegacyStateTVApp());
    		setFacilityWideRequirements(app.getFacilityWideRequirements());
        }
    }
    
    /**
     * Create a TVApplication from a PTIO application
     * @param ptioApp
     */
    public TVApplication(PTIOApplication ptioApp) {
        // most of the real copy code is in ApplicationBO
        setApplicationTypeCD("TV");
        setApplicationDesc(ptioApp.getApplicationDesc());
    }

    public TVApplication() {
        super();
        setApplicationTypeCD("TV");
        requiredFields();
    }

    private void requiredFields() {
    	requiredFieldPurposeCd();
    	requiredFieldNSPS();
    	requiredFieldNESHAP();
    	requiredFieldMACT();
    	requiredFieldPSD();
    	requiredFieldNSR();
    	requiredFieldFacWideReq();
    	requiredFieldSubjectTo112R();
    	requiredFieldSubjectToTIV();
    	requiredFieldComplianceCertSubmitFrequency();
    	requiredFieldComplianceWithApplicableEnhancedMonitoring();
    	requiredFieldSubjectToEngineConfigRestrictions();
    	requiredFieldSubjectToWAQSR();
    	requiredFieldAmbientMonitoring();
    	requiredFieldAlternateOperatingScenarios();
	}
    
    public final String getTvApplicationPurposeCd() {
        return tvApplicationPurposeCd;
	}
    
    public final void setTvApplicationPurposeCd(String tvApplicationPurposeCD) {
        this.tvApplicationPurposeCd = tvApplicationPurposeCD;
        if (TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING.equals(this.tvApplicationPurposeCd)) {
        	requiredFieldPurposeCd();
		} else {
			requiredFields();
		}
    }

	@Override
	public final void populate(java.sql.ResultSet rs) {
		try {
			super.populate(rs);
			setTvApplicationPurposeCd(rs.getString("tv_app_purpose_cd"));
			setOperationsDsc(rs.getString("operations_dsc"));
			setAqdTvApplicationNumber(rs.getString("aqd_tv_application_nbr"));
			setNspsApplicableFlag(rs.getString("tv_nsps_applicable_flag"));
			setNeshapApplicableFlag(rs.getString("tv_neshap_applicable_flag"));
			setMactApplicableFlag(rs.getString("tv_mact_applicable_flag"));
			setPsdApplicableFlag(rs.getString("tv_psd_applicable_flag"));
			setNsrApplicableFlag(rs.getString("tv_nsr_applicable_flag"));
			setFacilityWideRequirementFlag(rs
					.getString("facility_wide_requirement_flag"));
			setProposedExemptions(rs.getString("proposed_exemptions"));
			setSubjectTo112R(rs.getString("subject_to_112R"));
			setPlanSubmittedUnder112R(rs.getString("plan_submitted_under_112R"));
			setSubjectToTIV(rs.getString("subject_to_tiv"));
			setAmbientMonitoring(rs.getString("ambient_monitoring"));
			setAlternateOperatingScenarios(rs.getString("alternate_operating_scenarios"));
			setLegacyStateTVApp(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("legacy_st_tv_app_flag")));
			setRiskManagementPlanSubmitDate(rs
					.getTimestamp("risk_management_plan_submit_date"));
			setComplianceCertSubmitFrequency(rs.
					getString("compliance_cert_submit_frequency"));
			setComplianceWithApplicableEnhancedMonitoring(rs.
					getString("compliance_with_applicable_enhanced_monitoring"));
			setComplianceRequirementsNotMet(rs.
					getString("compliance_requirements_not_met"));
			setSubjectToEngineConfigRestrictions(rs.
					getString("subject_to_engine_config_restrictions"));
			setNsrPermitNumber(rs.getString("nsr_permit_number"));
			setSubjectToWAQSR(rs.getString("subject_to_waqsr"));

		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}
	
	public final String getOperationsDsc() {
		return operationsDsc;
	}

	public final void setOperationsDsc(String operationsDsc) {
		this.operationsDsc = operationsDsc;
	}

	public String getAqdTvApplicationNumber() {
		return aqdTvApplicationNumber;
	}

	public void setAqdTvApplicationNumber(String aqdTvApplicationNumber) {
		this.aqdTvApplicationNumber = aqdTvApplicationNumber;
	}

	public String getNspsApplicableFlag() {
		return nspsApplicableFlag;
	}

	public void setNspsApplicableFlag(String nspsApplicableFlag) {
		this.nspsApplicableFlag = nspsApplicableFlag;
		requiredFieldNSPS();
	}

	public String getNeshapApplicableFlag() {
		return neshapApplicableFlag;
	}

	public void setNeshapApplicableFlag(String neshapApplicableFlag) {
		this.neshapApplicableFlag = neshapApplicableFlag;
		requiredFieldNESHAP();
	}

	public String getMactApplicableFlag() {
		return mactApplicableFlag;
	}

	public void setMactApplicableFlag(String mactApplicableFlag) {
		this.mactApplicableFlag = mactApplicableFlag;
		requiredFieldMACT();
	}

	public String getPsdApplicableFlag() {
		return psdApplicableFlag;
	}

	public void setPsdApplicableFlag(String psdApplicableFlag) {
		this.psdApplicableFlag = psdApplicableFlag;
		requiredFieldPSD();
	}

	public String getNsrApplicableFlag() {
		return nsrApplicableFlag;
	}

	public void setNsrApplicableFlag(String nsrApplicableFlag) {
		this.nsrApplicableFlag = nsrApplicableFlag;
		requiredFieldNSR();
	}

	public String getFacilityWideRequirementFlag() {
		return facilityWideRequirementFlag;
	}

	public void setFacilityWideRequirementFlag(String facilityWideRequirementFlag) {
		if (Utility.isNullOrEmpty(facilityWideRequirementFlag) || 
				!facilityWideRequirementFlag.equals(TVFacWideReqOptionDef.SUBJECT)) {
    		facilityWideRequirements = new ArrayList<FacilityWideRequirement>();
		}
		
		this.facilityWideRequirementFlag = facilityWideRequirementFlag;
		requiredFieldFacWideReq();
	}
	
	public final List<String> getNspsSubpartCodes() {
		if (nspsSubpartCodes == null) {
			nspsSubpartCodes = new ArrayList<String>();
		}
		return nspsSubpartCodes;
	}

	public final void setNspsSubpartCodes(List<String> nspsSubpartCodes) {
		this.nspsSubpartCodes = new ArrayList<String>();
		if (nspsSubpartCodes != null) {
			this.nspsSubpartCodes.addAll(nspsSubpartCodes);
		}
	}

	public final List<String> getMactSubpartCodes() {
		if (mactSubpartCodes == null) {
			mactSubpartCodes = new ArrayList<String>();
		}
		return mactSubpartCodes;
	}

	public final void setMactSubpartCodes(List<String> mactSubpartCodes) {
		this.mactSubpartCodes = new ArrayList<String>();
		if (mactSubpartCodes != null) {
			this.mactSubpartCodes.addAll(mactSubpartCodes);
		}
	}

	public final List<String> getNeshapSubpartCodes() {
		if (neshapSubpartCodes == null) {
			neshapSubpartCodes = new ArrayList<String>();
		}
		return neshapSubpartCodes;
	}

	public final void setNeshapSubpartCodes(List<String> neshapSubpartCodes) {
		this.neshapSubpartCodes = new ArrayList<String>();
		if (neshapSubpartCodes != null) {
			this.neshapSubpartCodes.addAll(neshapSubpartCodes);
		}
	}
	
	
    public String getProposedExemptions() {
		return proposedExemptions;
	}

	public void setProposedExemptions(String proposedExemptions) {
		this.proposedExemptions = proposedExemptions;
	}

	public final String getSubjectTo112R() {
		return subjectTo112R;
	}
	
	public final void setSubjectTo112R(String subjectTo112R) {
		// clear planSubmittedUnder112R and riskManagementPlanSubmitDate
		// if these two were previously set and now subjectTo112R is changed
		// from yes to no
		if(isSubjectTo112RAct() &&
				!AbstractDAO.translateIndicatorToBoolean(subjectTo112R)) {
			this.planSubmittedUnder112R = null;
			this.riskManagementPlanSubmitDate = null;
		}
		
		this.subjectTo112R = subjectTo112R;
		requiredFieldSubjectTo112R();
	}
	
	public final boolean isSubjectTo112RAct() {
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(subjectTo112R) &&
				AbstractDAO.translateIndicatorToBoolean(subjectTo112R);
		return ret;
	}

	public final String getPlanSubmittedUnder112R() {
		return planSubmittedUnder112R;
	}

	public final void setPlanSubmittedUnder112R(String planSubmittedUnder112R) {
		// clear riskManagementPlanSubmitDate if it was previously set and 
		// now planSubmittedUnder112R is changed from yes to no
		if(isPlanSubmittedUnder112RAct() &&
				!AbstractDAO.translateIndicatorToBoolean(planSubmittedUnder112R)) {
			this.riskManagementPlanSubmitDate = null;
		}
		
		this.planSubmittedUnder112R = planSubmittedUnder112R;
	}
	
	public final boolean isPlanSubmittedUnder112RAct() {
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(planSubmittedUnder112R) &&
			AbstractDAO.translateIndicatorToBoolean(planSubmittedUnder112R);
		return ret;
	}

	public Timestamp getRiskManagementPlanSubmitDate() {
		return riskManagementPlanSubmitDate;
	}
	
	public final void setRiskManagementPlanSubmitDate(Timestamp riskManagementPlanSubmitDate) {
		this.riskManagementPlanSubmitDate = riskManagementPlanSubmitDate;
	}
	
	public final String getSubjectToTIV() {
		return subjectToTIV;
	}

	public final void setSubjectToTIV(String subjectToTIV) {
		this.subjectToTIV = subjectToTIV;
		requiredFieldSubjectToTIV();
	}
	
	public final boolean isSubjectToTIVAct() {
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(subjectToTIV) &&
				AbstractDAO.translateIndicatorToBoolean(subjectToTIV);
		return ret;
	}
	
	public String getComplianceCertSubmitFrequency() {
		return complianceCertSubmitFrequency;
	}

	public final void setComplianceCertSubmitFrequency(String complianceCertSubmitFrequency) {
		this.complianceCertSubmitFrequency = complianceCertSubmitFrequency;
		requiredFieldComplianceCertSubmitFrequency();
	}
	
	public String getComplianceWithApplicableEnhancedMonitoring() {
		return complianceWithApplicableEnhancedMonitoring;
	}
	
	public final void setComplianceWithApplicableEnhancedMonitoring(String complianceWithApplicableEnhancedMonitoring) {
		// clear complianceRequirementsNotMet if complianceWithApplicableEnhancedMonitoring
		// is changed from no to yes
		if(!isCompWithApplicableEnhancedMonitoring() &&
				AbstractDAO.translateIndicatorToBoolean(complianceWithApplicableEnhancedMonitoring)) {
			this.complianceRequirementsNotMet = null;
		}
		
		this.complianceWithApplicableEnhancedMonitoring = complianceWithApplicableEnhancedMonitoring;
		requiredFieldComplianceWithApplicableEnhancedMonitoring();
	}
	
	public final boolean isCompWithApplicableEnhancedMonitoring(){
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(complianceWithApplicableEnhancedMonitoring) &&
				AbstractDAO.translateIndicatorToBoolean(complianceWithApplicableEnhancedMonitoring);
		return ret;
	}
	
	public String getComplianceRequirementsNotMet() {
		return complianceRequirementsNotMet;
	}
	
	public final void setComplianceRequirementsNotMet(String complianceRequirementsNotMet) {
		this.complianceRequirementsNotMet = complianceRequirementsNotMet;
	}
	
	public String getSubjectToEngineConfigRestrictions() {
		return subjectToEngineConfigRestrictions;
	}
	
	public final void setSubjectToEngineConfigRestrictions(String subjectToEngineConfigRestrictions) {
		// clear nsrPermitNumber if subjectToEngineConfigRestrictions is changed
		// from yes to no
		if(isSubjectToEngConfigRestrictions() &&
				!AbstractDAO.translateIndicatorToBoolean(subjectToEngineConfigRestrictions)) {
			this.nsrPermitNumber = null;
		}
		
		this.subjectToEngineConfigRestrictions = subjectToEngineConfigRestrictions;
		requiredFieldSubjectToEngineConfigRestrictions();
	}

	public final boolean isSubjectToEngConfigRestrictions(){
		boolean ret = false;
		ret = !Utility.isNullOrEmpty(subjectToEngineConfigRestrictions) &&
				AbstractDAO.translateIndicatorToBoolean(subjectToEngineConfigRestrictions);
		return ret;
	}
	
	public String getNsrPermitNumber() {
		return nsrPermitNumber;
	}
	
	public final void setNsrPermitNumber(String nsrPermitNumber) {
		this.nsrPermitNumber = nsrPermitNumber;
	}
	
	public String getSubjectToWAQSR() {
		return subjectToWAQSR;
	}
	
	public final void setSubjectToWAQSR(String subjectToWAQSR) {
		this.subjectToWAQSR = subjectToWAQSR;
		requiredFieldSubjectToWAQSR();
	}

	/**
	 * Get the total Cap PTE values for all EUs in the application.
	 * 
	 * @return list of summed emissions values.
	 */
	public final List<TVPteAdjustment> getCapPteTotals() {
		if (capPteTotals == null) {
			capPteTotals = new ArrayList<TVPteAdjustment>();
		}
		return capPteTotals;
	}

	public final void setCapPteTotals(List<TVPteAdjustment> capPteTotals) {
		this.capPteTotals = new ArrayList<TVPteAdjustment>();
		if (capPteTotals != null) {
			this.capPteTotals.addAll(capPteTotals);
		}
	}

	public final List<TVPteAdjustment> getHapPteTotals() {
		if (hapPteTotals == null) {
			hapPteTotals = new ArrayList<TVPteAdjustment>();
		}
		return hapPteTotals;
	}

	public final void setHapPteTotals(List<TVPteAdjustment> hapPteTotals) {

		this.hapPteTotals = new ArrayList<TVPteAdjustment>();
		if (hapPteTotals != null) {
			this.hapPteTotals.addAll(hapPteTotals);
		}
	}

	public final List<TVPteAdjustment> getGhgPteTotals() {
		if (ghgPteTotals == null) {
			ghgPteTotals = new ArrayList<TVPteAdjustment>();
		}
		return ghgPteTotals;
	}

	public final void setGhgPteTotals(List<TVPteAdjustment> ghgPteTotals) {

		this.ghgPteTotals = new ArrayList<TVPteAdjustment>();
		if (ghgPteTotals != null) {
			this.ghgPteTotals.addAll(ghgPteTotals);
		}
	}
	
	public final List<TVPteAdjustment> getOthPteTotals() {
		if (othPteTotals == null) {
			othPteTotals = new ArrayList<TVPteAdjustment>();
		}
		return othPteTotals;
	}

	public final void setOthPteTotals(List<TVPteAdjustment> othPteTotals) {

		this.othPteTotals = new ArrayList<TVPteAdjustment>();
		if (othPteTotals != null) {
			this.othPteTotals.addAll(othPteTotals);
		}
	}

    public List<FacilityWideRequirement> getFacilityWideRequirements() {
    	if (facilityWideRequirements == null) {
    		facilityWideRequirements = new ArrayList<FacilityWideRequirement>();
        }
    	
		return facilityWideRequirements;
	}

	public void setFacilityWideRequirements(
			List<FacilityWideRequirement> facilityWideRequirements) {
		this.facilityWideRequirements = facilityWideRequirements;
	}

	public final List<TVApplicableReq> getApplicableRequirements() {
		if (applicableRequirements == null) {
			applicableRequirements = new ArrayList<TVApplicableReq>();
		}
		return applicableRequirements;
	}

	public final void setApplicableRequirements(
			List<TVApplicableReq> applicableRequirements) {
		this.applicableRequirements = new ArrayList<TVApplicableReq>();
		if (applicableRequirements != null) {
			this.applicableRequirements.addAll(applicableRequirements);
		}
	}

	public final List<TVApplicableReq> getStateOnlyRequirements() {
		if (stateOnlyRequirements == null) {
			stateOnlyRequirements = new ArrayList<TVApplicableReq>();
		}
		return stateOnlyRequirements;
	}

	public final void setStateOnlyRequirements(
			List<TVApplicableReq> stateOnlyRequirements) {
		this.stateOnlyRequirements = new ArrayList<TVApplicableReq>();
		if (stateOnlyRequirements != null) {
			this.stateOnlyRequirements.addAll(stateOnlyRequirements);
		}
	}

	public final List<TVEUGroup> getEuGroups() {
		if (euGroups == null) {
			euGroups = new ArrayList<TVEUGroup>();
		}
		return euGroups;
	}

	public final void setEuGroups(List<TVEUGroup> euGroups) {
		this.euGroups = new ArrayList<TVEUGroup>();
		if (euGroups != null) {
			this.euGroups.addAll(euGroups);
		}
	}

	public final String getPermitReasonCd() {
		return permitReasonCd;
	}

	public final void setPermitReasonCd(String permitReasonCd) {
		this.permitReasonCd = permitReasonCd;
		if (!PermitReasonsDef.SPM.equals(permitReasonCd)) {
			requiredFieldPurposeCd();
			if (PermitReasonsDef.REOPENING.equals(permitReasonCd)) {
				requiredFieldNSPS();
		    	requiredFieldNESHAP();
		    	requiredFieldMACT();
		    	requiredFieldFacWideReq();
			}
		} else {
			requiredFields();
		}
	}
	
	@Override
	public String getApplicationPurposeDesc() {
		String result = "";
		StringBuffer sb = new StringBuffer();
		if (tvApplicationPurposeCd != null) {
			sb.append(TVApplicationPurposeDef.getData().getItems()
					.getItemDesc(tvApplicationPurposeCd));
			if (TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING
					.equals(tvApplicationPurposeCd)) {
				sb.append(": ");
				if (!Utility.isNullOrEmpty(permitReasonCd)) {
					sb.append(PermitReasonsDef.getData().getItems().getItemDesc(permitReasonCd));
				}
			}
			result = sb.toString();
		}
		return result;
	}
    
	public final void addFacilityWideRequirement(FacilityWideRequirement facWideReq) {
		if (facWideReq == null)
			return;
		
		if (facilityWideRequirements == null) {
			facilityWideRequirements = new ArrayList<FacilityWideRequirement>();
		}

		facilityWideRequirements.add(facWideReq);
	}

	public final void removeFacilityWideRequirement(FacilityWideRequirement facWideReq) {
		facilityWideRequirements.remove(facWideReq);
	}

	public final String getAmbientMonitoring() {
		return ambientMonitoring;
	}
	
	public final boolean isAmbientMonitoringAllowed() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(this.ambientMonitoring) &&
				AbstractDAO.translateIndicatorToBoolean(this.ambientMonitoring);
		return ret;
	}

	public final void setAmbientMonitoring(String ambientMonitoring) {
		this.ambientMonitoring = ambientMonitoring;
		requiredFieldAmbientMonitoring();
	}

	public final String getAlternateOperatingScenarios() {
		return alternateOperatingScenarios;
	}
	
	public final boolean isAlternateOperatingScenariosAuthorized() {
		boolean ret;
		ret = !Utility.isNullOrEmpty(this.alternateOperatingScenarios) &&
				AbstractDAO.translateIndicatorToBoolean(this.alternateOperatingScenarios);
		return ret;
	}

	public final void setAlternateOperatingScenarios(
			String alternateOperatingScenarios) {
		this.alternateOperatingScenarios = alternateOperatingScenarios;
		requiredFieldAlternateOperatingScenarios();
	}
	
	private void requiredFieldNSPS() {
		requiredField(this.nspsApplicableFlag,"nspsApplicableFlag","Application NSPS", "nspsApplicableFlag");
	}
	
	private void requiredFieldFacWideReq() {
		requiredField(this.facilityWideRequirementFlag,"facilityWideRequirementFlag","Other Facility-Wide", "facilityWideRequirementFlag");
	}

	private void requiredFieldNSR() {
		requiredField(this.nsrApplicableFlag,"nsrApplicableFlag","Non-Attainment New Source Review", "nsrApplicableFlag");
	}

	private void requiredFieldPSD() {
		requiredField(this.psdApplicableFlag,"psdApplicableFlag","Application PSD", "psdApplicableFlag");
	}

	private void requiredFieldMACT() {
		requiredField(this.mactApplicableFlag,"mactApplicableFlag","Application NESHAP Part 63", "mactApplicableFlag");
	}

	private void requiredFieldNESHAP() {
		requiredField(this.neshapApplicableFlag,"neshapApplicableFlag","Application NESHAP Part 61", "neshapApplicableFlag");
	}

	private void requiredFieldPurposeCd() {
		requiredField(this.tvApplicationPurposeCd,"tvApplicationPurposeCd","Application Reason", "tvApplicationPurposeCd");
	}
	
	public final boolean isLegacyStateTVApp() {
		return legacyStateTVApp;
	}
	public final void setLegacyStateTVApp(boolean legacyStateTVApp) {
		this.legacyStateTVApp = legacyStateTVApp;
		if (this.legacyStateTVApp)
			setLegacy(true);
	}
	
	public void requiredFieldSubjectTo112R() {
		requiredField(this.subjectTo112R,"subject112RBox","Subject to 112(r) Act", "subjectTo112R");
	}
	
	public void requiredFieldSubjectToTIV(){
		requiredField(this.subjectToTIV, "subjectTIVBox", "Subject to TIV", "subjectToTIV");
	}
	
	public void requiredFieldComplianceCertSubmitFrequency(){
		requiredField(this.complianceCertSubmitFrequency, "complianceCertSubmitFrequency", 
				"Compliance Certification Submission Frequency", "complianceCertSubmitFrequency");
	}
	
	public void requiredFieldComplianceWithApplicableEnhancedMonitoring(){
		requiredField(this.complianceWithApplicableEnhancedMonitoring, "complianceWithApplicableEnhancedMonitoring", 
				"Compliance with applicable enhanced monitoring", "complianceWithApplicableEnhancedMonitoring");
	}
	
	public void requiredFieldSubjectToEngineConfigRestrictions(){
		requiredField(this.subjectToEngineConfigRestrictions, "subjectToEngineConfigRestrictions", 
				"Subject to engine configuration restrictions", "subjectToEngineConfigRestrictions");
	}
	
	public void requiredFieldSubjectToWAQSR(){
		requiredField(this.subjectToWAQSR, "subjectToWAQSR", 
				"Subject to WAQSR Chapter 14, Section 3", "subjectToWAQSR");
	}
	
	public void requiredFieldAmbientMonitoring() {
		requiredField(this.ambientMonitoring, "ambientMonitoringBox", "Ambient monitoring", "ambientMonitoring");
	}
	
	public void requiredFieldAlternateOperatingScenarios() {
		requiredField(this.alternateOperatingScenarios, "AlternateOperatingScenariosRBox", 
				"Alternate Operating Scenarios", "alternateOperatingScenarios");
	}
	
	public final void removeRevisionRequiredFields() {
		if (TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING.equals(this.tvApplicationPurposeCd) && !PermitReasonsDef.SPM.equals(this.permitReasonCd)) {
			this.operationsDsc = null;			
			this.psdApplicableFlag = null;
			this.nsrApplicableFlag = null;
			this.subjectTo112R = null;
			this.planSubmittedUnder112R = null;
			this.subjectToTIV = null;
			this.ambientMonitoring = null;
			this.alternateOperatingScenarios = null;
			this.riskManagementPlanSubmitDate = null;
			this.complianceCertSubmitFrequency = null;
			this.complianceWithApplicableEnhancedMonitoring = null;
			this.complianceRequirementsNotMet = null;
			this.subjectToEngineConfigRestrictions = null;
			this.nsrPermitNumber = null;
			this.subjectToWAQSR = null;
			
			validationMessages.remove("psdApplicableFlag");
			validationMessages.remove("nsrApplicableFlag");
			validationMessages.remove("subject112RBox");
			validationMessages.remove("subjectTIVBox");
			validationMessages.remove("complianceCertSubmitFrequency");			
			validationMessages.remove("complianceWithApplicableEnhancedMonitoring");
			validationMessages.remove("subjectToEngineConfigRestrictions");
			validationMessages.remove("subjectToWAQSR");
			validationMessages.remove("ambientMonitoringBox");
			validationMessages.remove("AlternateOperatingScenariosRBox");
			if (!PermitReasonsDef.REOPENING.equals(this.permitReasonCd)) {
				this.nspsApplicableFlag = null;
				this.neshapApplicableFlag = null;
				this.mactApplicableFlag = null;
				this.facilityWideRequirementFlag = null;
				this.proposedExemptions = null;
				
				validationMessages.remove("nspsApplicableFlag");
				validationMessages.remove("neshapApplicableFlag");
				validationMessages.remove("mactApplicableFlag");
				validationMessages.remove("facilityWideRequirementFlag");
			}
		}
	}
}
