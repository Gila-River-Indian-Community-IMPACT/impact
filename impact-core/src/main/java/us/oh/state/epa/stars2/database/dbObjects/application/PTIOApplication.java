package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.def.PTIOApplicationPurposeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationSageGrouseDef;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl2Def;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class PTIOApplication extends Application {
	private List<String> applicationPurposeCDs;
	private String otherPurposeDesc;
	private String generalPermitTypeCD;
	private String requestedPERDueDateCD;
	private String changedPERDueDateCD;
	private String nspsApplicableFlag;
	private List<String> nspsSubpartCodes;
	private String neshapApplicableFlag;
	private List<String> neshapSubpartCodes;
	private String mactApplicableFlag;
	private List<String> mactSubpartCodes;
	private String psdApplicableFlag;
	private String ghgApplicableFlag;
	private String nsrApplicableFlag;
	private String riskManagementPlanFlag;
	private String titleIVFlag;
	private String federalRuleApplicabilityExplanation;
	private boolean legacyStatePTOApp;
	private boolean qualifyExpress;
	private boolean requestExpress;
	private boolean tradeSecret;
	private String landUsePlanningFlag;
	private String facilityChangedLocationFlag;
	private String potentialTitleVFlag;
	private String containH2SFlag;
	private String divisionContacedFlag;
	private String sageGrouseCd;
	private String sageGrouseAgencyName;
	private String modelingContactFlag;
	private String modelingAnalysisFlag;
	private String preventionPsdFlag;
	private String preAppMeetingFlag;
	private String modelingProtocolSubmitFlag;
	private String aqrvAnalysisSubmitFlag;
	
	private boolean knownIncompleteNSRApp;

	public PTIOApplication() {
		super();
		setApplicationTypeCD("PTIO");
	}

	/**
	 * @param old
	 */
	public PTIOApplication(PTIOApplication old) {
		super(old);
		if (old != null) {
			setApplicationPurposeCDs(old.getApplicationPurposeCDs());
			setOtherPurposeDesc(old.getOtherPurposeDesc());
			setGeneralPermitTypeCD(old.getGeneralPermitTypeCD());
			setRequestedPERDueDateCD(old.getRequestedPERDueDateCD());
			setChangedPERDueDateCD(old.getChangedPERDueDateCD());
			setNspsApplicableFlag(old.getNspsApplicableFlag());
			setNspsSubpartCodes(old.getNspsSubpartCodes());
			setNeshapApplicableFlag(old.getNeshapApplicableFlag());
			setNeshapSubpartCodes(old.getNeshapSubpartCodes());
			setMactApplicableFlag(old.getMactApplicableFlag());
			setMactSubpartCodes(old.getMactSubpartCodes());
			setPsdApplicableFlag(old.getPsdApplicableFlag());
			setGhgApplicableFlag(old.getGhgApplicableFlag());
			setNsrApplicableFlag(old.getNsrApplicableFlag());
			setRiskManagementPlanFlag(old.getRiskManagementPlanFlag());
			setTitleIVFlag(old.getTitleIVFlag());
			setFederalRuleApplicabilityExplanation(old
					.getFederalRuleApplicabilityExplanation());
			setLegacyStatePTOApp(old.isLegacyStatePTOApp());
			setQualifyExpress(old.isQualifyExpress());
			setRequestExpress(old.isRequestExpress());
			setTradeSecret(old.isTradeSecret());
			setLandUsePlanningFlag(old.getLandUsePlanningFlag());
			setFacilityChangedLocationFlag(old.getFacilityChangedLocationFlag());
			setPotentialTitleVFlag(old.getPotentialTitleVFlag());
			setContainH2SFlag(old.getContainH2SFlag());
			setDivisionContacedFlag(old.getDivisionContacedFlag());
			setSageGrouseCd(old.getSageGrouseCd());
			setSageGrouseAgencyName(old.getSageGrouseAgencyName());
			setModelingContactFlag(old.getModelingContactFlag());
			setModelingAnalysisFlag(old.getModelingAnalysisFlag());
			setPreventionPsdFlag(old.getPreventionPsdFlag());
			setPreAppMeetingFlag(old.getPreAppMeetingFlag());
			setModelingProtocolSubmitFlag(old.getModelingProtocolSubmitFlag());
			setAqrvAnalysisSubmitFlag(old.getAqrvAnalysisSubmitFlag());
			setKnownIncompleteNSRApp(old.isKnownIncompleteNSRApp());
		}
	}

	/**
	 * Create a PTIOApplication from a TVApplication
	 * 
	 * @param tvApp
	 */
	public PTIOApplication(TVApplication tvApp) {
		// most of the real copy code is in ApplicationBO
		setApplicationTypeCD("PTIO");
		setApplicationDesc(tvApp.getApplicationDesc());
	}

	@Override
	public final String getApplicationPurposeDesc() {
		StringBuffer result = new StringBuffer();
		if (applicationPurposeCDs != null) {
			for (String purposeCd : applicationPurposeCDs) {
				result.append(PTIOApplicationPurposeDef.getData().getItems()
						.getItemDesc(purposeCd));
				result.append(",");
			}
			if (result.length() > 0) {
				result.deleteCharAt(result.length() - 1);
			}
		}
		return result.toString();
	}

	@Override
	public final List<String> getApplicationPurposeCDs() {
		if (applicationPurposeCDs == null) {
			applicationPurposeCDs = new ArrayList<String>();
		}
		return applicationPurposeCDs;
	}

	public final void setApplicationPurposeCDs(
			List<String> ptioApplicationPurposeCDs) {
		this.applicationPurposeCDs = new ArrayList<String>();
		if (ptioApplicationPurposeCDs != null) {
			this.applicationPurposeCDs.addAll(ptioApplicationPurposeCDs);
		}
	}

	public final String getOtherPurposeDesc() {
		return otherPurposeDesc;
	}

	public final void setOtherPurposeDesc(String otherPurposeDesc) {
		this.otherPurposeDesc = otherPurposeDesc;
	}

	public final String getGeneralPermitTypeCD() {
		return generalPermitTypeCD;
	}

	public final void setGeneralPermitTypeCD(String generalPermitTypeCD) {
		this.generalPermitTypeCD = generalPermitTypeCD;
	}

	public final String getRequestedPERDueDateCD() {
		return requestedPERDueDateCD;
	}

	public final void setRequestedPERDueDateCD(String requestedPERDueDateCD) {
		this.requestedPERDueDateCD = requestedPERDueDateCD;
	}

	public final String getChangedPERDueDateCD() {
		return changedPERDueDateCD;
	}

	public final void setChangedPERDueDateCD(String changedPERDueDateCD) {
		this.changedPERDueDateCD = changedPERDueDateCD;
	}

	public final String getFederalRuleApplicabilityExplanation() {
		if (null != federalRuleApplicabilityExplanation
				&& !getFedRulesExemption()) {
			federalRuleApplicabilityExplanation = null;
		}
		return federalRuleApplicabilityExplanation;
	}

	public final void setFederalRuleApplicabilityExplanation(
			String federalRuleApplicabilityExplanation) {
		this.federalRuleApplicabilityExplanation = federalRuleApplicabilityExplanation;
	}

	public final boolean isQualifyExpress() {
		return qualifyExpress;
	}

	public final void setQualifyExpress(boolean qualifyExpress) {
		this.qualifyExpress = qualifyExpress;
	}

	public final boolean isRequestExpress() {
		return requestExpress;
	}

	public final void setRequestExpress(boolean requestExpress) {
		this.requestExpress = requestExpress;
	}

	public final boolean isTradeSecret() {
		return tradeSecret;
	}

	public final void setTradeSecret(boolean tradeSecret) {
		this.tradeSecret = tradeSecret;
	}

	public final boolean getFedRulesExemption() {
		boolean exempt = false;
		exempt = (getNspsApplicableFlag() != null && (getNspsApplicableFlag()
				.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT)))
				|| (getNeshapApplicableFlag() != null && getNeshapApplicableFlag()
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT))
				|| (getMactApplicableFlag() != null && getMactApplicableFlag()
						.equals(PTIOFedRuleAppl1Def.SUBJECT_BUT_EXEMPT));
		return exempt;
	}

	public final String getNspsApplicableFlag() {
		return nspsApplicableFlag;
	}

	public final void setNspsApplicableFlag(String nspsApplicableFlag) {
		this.nspsApplicableFlag = nspsApplicableFlag;
	}

	public final String getNeshapApplicableFlag() {
		return neshapApplicableFlag;
	}

	public final void setNeshapApplicableFlag(String neshapApplicableFlag) {
		this.neshapApplicableFlag = neshapApplicableFlag;
	}

	public final String getMactApplicableFlag() {
		return mactApplicableFlag;
	}

	public final void setMactApplicableFlag(String mactApplicableFlag) {
		this.mactApplicableFlag = mactApplicableFlag;
	}

	public final String getPsdApplicableFlag() {
		return psdApplicableFlag;
	}

	public final void setPsdApplicableFlag(String psdApplicableFlag) {
		this.psdApplicableFlag = psdApplicableFlag;
	}

	public final String getGhgApplicableFlag() {
		return ghgApplicableFlag;
	}

	public final void setGhgApplicableFlag(String ghgApplicableFlag) {
		this.ghgApplicableFlag = ghgApplicableFlag;
	}

	public final String getNsrApplicableFlag() {
		return nsrApplicableFlag;
	}

	public final void setNsrApplicableFlag(String nsrApplicableFlag) {
		this.nsrApplicableFlag = nsrApplicableFlag;
	}

	@Override
	public final void populate(java.sql.ResultSet rs) {
		try {
			super.populate(rs);

			setOtherPurposeDesc(rs.getString("other_purpose_desc"));
			setGeneralPermitTypeCD(rs.getString("general_permit_type_cd"));
			setRequestedPERDueDateCD(rs.getString("requested_per_due_date_cd"));
			setChangedPERDueDateCD(rs.getString("changed_per_due_date_cd"));
			setNspsApplicableFlag(rs.getString("nsps_applicable_flag"));
			setNeshapApplicableFlag(rs.getString("neshaps_applicable_flag"));
			setMactApplicableFlag(rs.getString("mact_applicable_flag"));
			setPsdApplicableFlag(rs.getString("psd_applicable_flag"));
			try {
				setGhgApplicableFlag(rs.getString("ghg_applicable_flag"));
			} catch (SQLException sqle) {
				logger.error("Cannot find field ghg_applicable_flag", sqle);
			}
			setNsrApplicableFlag(rs.getString("nsr_applicable_flag"));
			setRiskManagementPlanFlag(rs.getString("rm_applicable_flag"));
			setTitleIVFlag(rs.getString("tiv_applicable_flag"));
			setFederalRuleApplicabilityExplanation(rs
					.getString("federal_rule_appl_explanation"));
			setLegacyStatePTOApp(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("legacy_st_pto_app_flag")));
			setQualifyExpress(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("qualify_express_flag")));
			setRequestExpress(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("request_express_flag")));
			setTradeSecret(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("trade_secret_flag")));
			setLandUsePlanningFlag(rs.getString("land_use_planning_flag"));
			setFacilityChangedLocationFlag(rs
					.getString("facility_Changed_Location_Flag"));
			setPotentialTitleVFlag(rs
					.getString("potential_title_v_facility_flag"));
			setContainH2SFlag(rs.getString("contain_h2s_flag"));
			setDivisionContacedFlag(rs.getString("division_contaced_flag"));
			setSageGrouseCd(rs.getString("ptio_app_sagegrouse_cd"));
			setSageGrouseAgencyName(rs.getString("ptio_app_sagegrouse_agency_nm"));
			setModelingContactFlag(rs.getString("modeling_contact_flag"));
			setModelingAnalysisFlag(rs.getString("modeling_analysis_flag"));
			setPreventionPsdFlag(rs.getString("prevention_psd_flag"));
			setPreAppMeetingFlag(rs.getString("pre_app_meeting_flag"));
			setModelingProtocolSubmitFlag(rs.getString("modeling_protocol_submit_flag"));
			setAqrvAnalysisSubmitFlag(rs.getString("aqrv_analysis_submit_flag"));
			setKnownIncompleteNSRApp(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("known_incomplete_nsr_app_flag")));
			
			setLastModified(AbstractDAO.getInteger(rs, "ptioapp_lm"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME
				* result
				+ ((changedPERDueDateCD == null) ? 0 : changedPERDueDateCD
						.hashCode());
		result = PRIME
				* result
				+ ((federalRuleApplicabilityExplanation == null) ? 0
						: federalRuleApplicabilityExplanation.hashCode());
		result = PRIME
				* result
				+ ((generalPermitTypeCD == null) ? 0 : generalPermitTypeCD
						.hashCode());
		result = PRIME
				* result
				+ ((mactApplicableFlag == null) ? 0 : mactApplicableFlag
						.hashCode());
		result = PRIME
				* result
				+ ((neshapApplicableFlag == null) ? 0 : neshapApplicableFlag
						.hashCode());
		result = PRIME
				* result
				+ ((nspsApplicableFlag == null) ? 0 : nspsApplicableFlag
						.hashCode());
		result = PRIME
				* result
				+ ((nsrApplicableFlag == null) ? 0 : nsrApplicableFlag
						.hashCode());
		result = PRIME
				* result
				+ ((otherPurposeDesc == null) ? 0 : otherPurposeDesc.hashCode());
		result = PRIME
				* result
				+ ((psdApplicableFlag == null) ? 0 : psdApplicableFlag
						.hashCode());
		result = PRIME
				* result
				+ ((ghgApplicableFlag == null) ? 0 : ghgApplicableFlag
						.hashCode());
		result = PRIME * result + (qualifyExpress ? 1231 : 1237);
		result = PRIME * result + (requestExpress ? 1231 : 1237);
		result = PRIME
				* result
				+ ((requestedPERDueDateCD == null) ? 0 : requestedPERDueDateCD
						.hashCode());
		result = PRIME
				* result
				+ ((riskManagementPlanFlag == null) ? 0
						: riskManagementPlanFlag.hashCode());
		result = PRIME * result
				+ ((titleIVFlag == null) ? 0 : titleIVFlag.hashCode());
		result = PRIME * result + (tradeSecret ? 1231 : 1237);
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
		final PTIOApplication other = (PTIOApplication) obj;
		if (changedPERDueDateCD == null) {
			if (other.changedPERDueDateCD != null)
				return false;
		} else if (!changedPERDueDateCD.equals(other.changedPERDueDateCD))
			return false;
		if (federalRuleApplicabilityExplanation == null) {
			if (other.federalRuleApplicabilityExplanation != null)
				return false;
		} else if (!federalRuleApplicabilityExplanation
				.equals(other.federalRuleApplicabilityExplanation))
			return false;
		if (generalPermitTypeCD == null) {
			if (other.generalPermitTypeCD != null)
				return false;
		} else if (!generalPermitTypeCD.equals(other.generalPermitTypeCD))
			return false;
		if (mactApplicableFlag == null) {
			if (other.mactApplicableFlag != null)
				return false;
		} else if (!mactApplicableFlag.equals(other.mactApplicableFlag))
			return false;
		if (neshapApplicableFlag == null) {
			if (other.neshapApplicableFlag != null)
				return false;
		} else if (!neshapApplicableFlag.equals(other.neshapApplicableFlag))
			return false;
		if (nspsApplicableFlag == null) {
			if (other.nspsApplicableFlag != null)
				return false;
		} else if (!nspsApplicableFlag.equals(other.nspsApplicableFlag))
			return false;
		if (nsrApplicableFlag == null) {
			if (other.nsrApplicableFlag != null)
				return false;
		} else if (!nsrApplicableFlag.equals(other.nsrApplicableFlag))
			return false;
		if (otherPurposeDesc == null) {
			if (other.otherPurposeDesc != null)
				return false;
		} else if (!otherPurposeDesc.equals(other.otherPurposeDesc))
			return false;
		if (psdApplicableFlag == null) {
			if (other.psdApplicableFlag != null)
				return false;
		} else if (!psdApplicableFlag.equals(other.psdApplicableFlag))
			return false;
		if (ghgApplicableFlag == null) {
			if (other.ghgApplicableFlag != null)
				return false;
		} else if (!ghgApplicableFlag.equals(other.ghgApplicableFlag))
			return false;
		if (qualifyExpress != other.qualifyExpress)
			return false;
		if (requestExpress != other.requestExpress)
			return false;
		if (requestedPERDueDateCD == null) {
			if (other.requestedPERDueDateCD != null)
				return false;
		} else if (!requestedPERDueDateCD.equals(other.requestedPERDueDateCD))
			return false;
		if (riskManagementPlanFlag == null) {
			if (other.riskManagementPlanFlag != null)
				return false;
		} else if (!riskManagementPlanFlag.equals(other.riskManagementPlanFlag))
			return false;
		if (titleIVFlag == null) {
			if (other.titleIVFlag != null)
				return false;
		} else if (!titleIVFlag.equals(other.titleIVFlag))
			return false;
		if (tradeSecret != other.tradeSecret)
			return false;
		return true;
	}

	public final String getRiskManagementPlanFlag() {
		return riskManagementPlanFlag;
	}

	public final void setRiskManagementPlanFlag(String riskManagementPlanFlag) {
		this.riskManagementPlanFlag = riskManagementPlanFlag;
	}

	public final String getTitleIVFlag() {
		return titleIVFlag;
	}

	public final void setTitleIVFlag(String titleIVFlag) {
		this.titleIVFlag = titleIVFlag;
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

	public final boolean isLegacyStatePTOApp() {
		return legacyStatePTOApp;
	}

	public final void setLegacyStatePTOApp(boolean legacyStatePTOApp) {
		
		try{
			this.legacyStatePTOApp = legacyStatePTOApp;
		
		if(this.legacyStatePTOApp)
			setLegacy(true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public final boolean isKnownIncompleteNSRApp() {
		return knownIncompleteNSRApp;
	}

	public final void setKnownIncompleteNSRApp(boolean knownIncompleteNSRApp) {

		try {
			this.knownIncompleteNSRApp = knownIncompleteNSRApp;

			if (this.knownIncompleteNSRApp) {
				setKnownIncomplete(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getLandUsePlanningFlag() {
		return landUsePlanningFlag;
	}

	public void setLandUsePlanningFlag(String landUsePlanningFlag) {
		this.landUsePlanningFlag = landUsePlanningFlag;
	}

	public String getFacilityChangedLocationFlag() {
		return facilityChangedLocationFlag;
	}

	public void setFacilityChangedLocationFlag(
			String facilityChangedLocationFlag) {
		this.facilityChangedLocationFlag = facilityChangedLocationFlag;

		if (Utility.isNullOrEmpty(facilityChangedLocationFlag)
				|| facilityChangedLocationFlag.equalsIgnoreCase("N")) {
			this.landUsePlanningFlag = null;
		}
		requiredField(
				this.facilityChangedLocationFlag,
				"facilityChangedLocationFlag",
				"Has the facility changed location or is it a new/greenfield facility",
				"facilityChangedLocationFlag");
	}

	public String getPotentialTitleVFlag() {
		return potentialTitleVFlag;
	}

	public void setPotentialTitleVFlag(String potentialTitleVFlag) {
		this.potentialTitleVFlag = potentialTitleVFlag;
	}

	public String getContainH2SFlag() {
		return containH2SFlag;
	}

	public void setContainH2SFlag(String containH2SFlag) {
		this.containH2SFlag = containH2SFlag;

		if (Utility.isNullOrEmpty(containH2SFlag)
				|| containH2SFlag.equalsIgnoreCase("N")) {
			this.divisionContacedFlag = null;
		}
		requiredField(this.containH2SFlag, "containH2SFlag",
				"Does production at this facility contain H2S",
				"containH2SFlag");
	}

	public String getDivisionContacedFlag() {
		return divisionContacedFlag;
	}

	public void setDivisionContacedFlag(String divisionContacedFlag) {
		this.divisionContacedFlag = divisionContacedFlag;
	}

	public void requiredFields() {
		requiredField(
				this.facilityChangedLocationFlag,
				"facilityChangedLocationFlag",
				"Has the facility changed location or is it a new/greenfield facility",
				"facilityChangedLocationFlag");
		
		requiredField(this.containH2SFlag, "containH2SFlag",
				"Does production at this facility contain H2S",
				"containH2SFlag");
		
		requiredField(this.modelingContactFlag, "modelingContactFlag",
				"Has the applicant contacted AQD to determine if modeling is required",
				"modelingContactFlag");
		
		requiredField(this.modelingAnalysisFlag, "modelingAnalysisFlag",
				"Is a modeling analysis part of this application",
				"modelingAnalysisFlag");
		
		requiredField(this.preventionPsdFlag, "preventionPsdFlag",
				"Is the proposed project subject to Prevention of Significant Deterioration (PSD) requirement",
				"preventionPsdFlag");
	}

	public final boolean isPsdSubjectToReg() {
		boolean ret = false;

		if (this.psdApplicableFlag != null) {
			ret = this.psdApplicableFlag
					.equals(PTIOFedRuleAppl2Def.SUBJECT_TO_REG);
		}

		return ret;
	}

	public String getSageGrouseCd() {
		return sageGrouseCd;
	}

	public void setSageGrouseCd(String sageGrouseCd) {
		this.sageGrouseCd = sageGrouseCd;
		if (Utility.isNullOrEmpty(sageGrouseCd)
				|| !sageGrouseCd
						.equalsIgnoreCase(PTIOApplicationSageGrouseDef.CHECK_COMPLETED_BY_ANOTHER_AGENCY)) {
			this.sageGrouseAgencyName = null;
		}
	}

	public String getSageGrouseAgencyName() {
		return sageGrouseAgencyName;
	}

	public void setSageGrouseAgencyName(String sageGrouseAgencyName) {
		this.sageGrouseAgencyName = sageGrouseAgencyName;
	}

	public String getModelingContactFlag() {
		return modelingContactFlag;
	}

	public void setModelingContactFlag(String modelingContactFlag) {
		this.modelingContactFlag = modelingContactFlag;
		
		requiredField(this.modelingContactFlag, "modelingContactFlag",
				"Has the applicant contacted AQD to determine if modeling is required",
				"modelingContactFlag");
	}
	
	public String getModelingAnalysisFlag() {
		return modelingAnalysisFlag;
	}

	public void setModelingAnalysisFlag(String modelingAnalysisFlag) {
		this.modelingAnalysisFlag = modelingAnalysisFlag;
		
		requiredField(this.modelingAnalysisFlag, "modelingAnalysisFlag",
				"Is a modeling analysis part of this application",
				"modelingAnalysisFlag");
	}

	public String getPreventionPsdFlag() {
		return preventionPsdFlag;
	}

	public void setPreventionPsdFlag(String preventionPsdFlag) {
		this.preventionPsdFlag = preventionPsdFlag;
		
		if (Utility.isNullOrEmpty(preventionPsdFlag)
				|| preventionPsdFlag.equalsIgnoreCase("N")) {
			this.preAppMeetingFlag = null;
			this.modelingProtocolSubmitFlag = null;
			this.aqrvAnalysisSubmitFlag = null;
		}
		
		requiredField(this.preventionPsdFlag, "preventionPsdFlag",
				"Is the proposed project subject to Prevention of Significant Deterioration (PSD) requirement",
				"preventionPsdFlag");
	}

	public String getPreAppMeetingFlag() {
		return preAppMeetingFlag;
	}

	public void setPreAppMeetingFlag(String preAppMeetingFlag) {
		this.preAppMeetingFlag = preAppMeetingFlag;
	}

	public String getModelingProtocolSubmitFlag() {
		return modelingProtocolSubmitFlag;
	}

	public void setModelingProtocolSubmitFlag(String modelingProtocolSubmitFlag) {
		this.modelingProtocolSubmitFlag = modelingProtocolSubmitFlag;
	}

	public String getAqrvAnalysisSubmitFlag() {
		return aqrvAnalysisSubmitFlag;
	}

	public void setAqrvAnalysisSubmitFlag(String aqrvAnalysisSubmitFlag) {
		this.aqrvAnalysisSubmitFlag = aqrvAnalysisSubmitFlag;
	}
	
	public final boolean isNsrSubjectToReg() {
		boolean ret = false;
		
		if(this.nsrApplicableFlag != null){
			ret = this.nsrApplicableFlag.equals(PTIOFedRuleAppl2Def.SUBJECT_TO_REG);
		}
		
		return ret;
	}	
}
