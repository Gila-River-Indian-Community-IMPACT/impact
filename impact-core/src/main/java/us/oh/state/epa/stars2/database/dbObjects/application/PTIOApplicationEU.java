package us.oh.state.epa.stars2.database.dbObjects.application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.PTIOApplicationEUPurposeDef;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;

@SuppressWarnings("serial")
public class PTIOApplicationEU extends ApplicationEU {
	private String ptioEUPurposeCD;
	private boolean generalPermit;
	private transient Timestamp workStartDate;

	// declare dummy version of workStartDate for XML serialization
	private long workStartDateLong;
	private boolean workStartAfterPermit;
	private transient Timestamp operationBeginDate;

	// declare dummy version of operationBeginDate for XML serialization
	private long operationBeginDateLong;

	private String modificationDesc;
	private String generalPermitTypeCd;
	private String modelGeneralPermitCd;

	private String reconstructionDesc;
	private Integer shutdownYears;
	private String requestingFederalLimitsFlag;
	private String federalLimitsOtherReasonDesc;
	private List<String> federalLimitsReasonCDs;
	private List<ApplicationEUEmissions> capEmissions;
	private List<ApplicationEUEmissions> hapTacEmissions;
	private List<ApplicationEUEmissions> ghgEmissions;
	private List<ApplicationEUEmissions> othEmissions;
	private String bactFlag;
	private String laerFlag;
	private String psdBACTFlag;
	private String nsrLAERFlag;

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

	private List<NSRApplicationBACTEmission> bactEmissions;
	private List<NSRApplicationLAEREmission> laerEmissions;

	private Integer opSchedHrsDay;
	private Integer opSchedHrsYr;

	private NSRApplicationEUType euType;
    private List<ApplicationEUFugitiveLeaks> applicationEUFugitiveLeaks = new ArrayList<ApplicationEUFugitiveLeaks>(0);
    private List<ApplicationEUMaterialUsed> materialUsed;

	/**
	 * Copy constructor
	 * 
	 * @param old
	 */
	public PTIOApplicationEU(PTIOApplicationEU old) {
		super(old);
		if (old != null) {
			setPtioEUPurposeCD(old.getPtioEUPurposeCD());
			setPtioEUPurposeCDs(old.getPtioEUPurposeCDs());
			setGeneralPermit(old.isGeneralPermit());
			setWorkStartDate(old.getWorkStartDate());
			// setWorkStartDateLong(old.getWorkStartDateLong());  This is set as a side-effect of setWorkStartDate
			setWorkStartAfterPermit(old.isWorkStartAfterPermit());
			setOperationBeginDate(old.getOperationBeginDate());
			// setOperationBeginDateLong(old.getOperationBeginDateLong()); This is set as a side-effect of setOperationBeginDate
			// setPreviousPermits(old.getPreviousPermits());
			setModificationDesc(old.getModificationDesc());
			setGeneralPermitTypeCd(old.getGeneralPermitTypeCd());
			setModelGeneralPermitCd(old.getModelGeneralPermitCd());
			setReconstructionDesc(old.getReconstructionDesc());
			setShutdownYears(old.getShutdownYears());
			setRequestingFederalLimitsFlag(old.getRequestingFederalLimitsFlag());
			setFederalLimitsOtherReasonDesc(old
					.getFederalLimitsOtherReasonDesc());
			
			setFederalLimitsReasonCDs(old.getFederalLimitsReasonCDs());
			setCapEmissions(old.getCapEmissions());
			setHapTacEmissions(old.getHapTacEmissions());
			setGhgEmissions(old.getGhgEmissions());
			setOthEmissions(old.getOthEmissions());
			
			setBactFlag(old.getBactFlag());
			setLaerFlag(old.getLaerFlag());
			setPsdBACTFlag(old.getPsdBACTFlag());
			setNsrLAERFlag(old.getNsrLAERFlag());

			setNspsApplicableFlag(old.getNspsApplicableFlag());
			setNspsSubpartCodes(old.getNspsSubpartCodes());//
			setNeshapApplicableFlag(old.getNeshapApplicableFlag());
			setNeshapSubpartCodes(old.getNeshapSubpartCodes());//
			setMactApplicableFlag(old.getMactApplicableFlag());
			setMactSubpartCodes(old.getMactSubpartCodes());//
			setPsdApplicableFlag(old.getPsdApplicableFlag());
			setGhgApplicableFlag(old.getGhgApplicableFlag());
			setNsrApplicableFlag(old.getNsrApplicableFlag());
			setRiskManagementPlanFlag(old.getRiskManagementPlanFlag());
			setTitleIVFlag(old.getTitleIVFlag());
			setFederalRuleApplicabilityExplanation(old
					.getFederalRuleApplicabilityExplanation());

			setBactEmissions(old.getBactEmissions());//
			setLaerEmissions(old.getLaerEmissions());//
			//setPsdBACTFlag(old.getPsdBACTFlag());
			//setNsrLAERFlag(old.getNsrLAERFlag());

			setOpSchedHrsDay(old.getOpSchedHrsDay());
			setOpSchedHrsYr(old.getOpSchedHrsYr());

			setEuType(old.getEuType());
			setApplicationEUFugitiveLeaks(old.getApplicationEUFugitiveLeaks());//
			setMaterialUsed(old.getMaterialUsed());//
		}
		logger.debug("--> PTIOApplicationEU(PTIOApplicationEU old)");
	}

	/**
	 * Copy a TVApplicationEU.
	 * 
	 * @param tvEU
	 */
	public PTIOApplicationEU(TVApplicationEU tvEU) {
		// most of the real copy code is in ApplicationBO
		setFpEU(tvEU.getFpEU());
		logger.debug("--> PTIOApplicationEU(TVApplicationEU tvEU)");
	}

	/**
     * 
     */
	public PTIOApplicationEU() {
		super();
		bactEmissions = new ArrayList<NSRApplicationBACTEmission>();
		logger.debug("--> PTIOApplicationEU()");
	}

	// NOTE:
	// purpose codes have been split into a mutually exclusive set of
	// values plus "general permit". To avoid a schema change (and DAO changes),
	// we're treating these values (exclusive set plus general permit) as
	// a single list in the DB and populate methods, and splitting this
	// data within this class.
	// So, the methods getPtioEUPurposeCDs and setPtioEUPurposeCDs should only
	// be used to read data from or write data to the database

	public final List<String> getPtioEUPurposeCDs() {
		ArrayList<String> purposeCds = new ArrayList<String>();
		if (ptioEUPurposeCD != null) {
			purposeCds.add(ptioEUPurposeCD);
		}
		if (generalPermit) {
			purposeCds.add(PTIOApplicationEUPurposeDef.GENERAL_PERMIT);
		}
		return purposeCds;
	}

	public final void setPtioEUPurposeCDs(List<String> ptioEUPurposeCDs) {
		generalPermit = false;
		for (String purposeCd : ptioEUPurposeCDs) {
			if (purposeCd.equals(PTIOApplicationEUPurposeDef.GENERAL_PERMIT)) {
				generalPermit = true;
			} else {
				ptioEUPurposeCD = purposeCd;
			}
		}
	}

	public final Timestamp getWorkStartDate() {
		return workStartDate;
	}

	public final void setWorkStartDate(Timestamp workStartDate) {
		this.workStartDate = workStartDate;
		if (this.workStartDate != null) {
			// clear workStartAfterPermit flag if work start date is set
			// to a non-null value
			workStartAfterPermit = false;
			this.workStartDateLong = this.workStartDate.getTime();
		} else {
			this.workStartDateLong = 0;
		}
	}

	public final String getReconstructionDesc() {
		return reconstructionDesc;
	}

	public final void setReconstructionDesc(String reconstructionDesc) {
		this.reconstructionDesc = reconstructionDesc;
	}

	public final Integer getShutdownYears() {
		return shutdownYears;
	}

	public final void setShutdownYears(Integer shutdownYears) {
		this.shutdownYears = shutdownYears;
	}

	public final String getRequestingFederalLimitsFlag() {
		return requestingFederalLimitsFlag;
	}

	public final void setRequestingFederalLimitsFlag(
			String requestingFederalLimitsFlag) {
		this.requestingFederalLimitsFlag = requestingFederalLimitsFlag;
	}

	public final String getFederalLimitsOtherReasonDesc() {
		return federalLimitsOtherReasonDesc;
	}

	public final void setFederalLimitsOtherReasonDesc(
			String federalLimitsOtherReasonDesc) {
		this.federalLimitsOtherReasonDesc = federalLimitsOtherReasonDesc;
	}

	public final List<String> getFederalLimitsReasonCDs() {
		if (federalLimitsReasonCDs == null) {
			federalLimitsReasonCDs = new ArrayList<String>();
		}
		return federalLimitsReasonCDs;
	}

	public final void setFederalLimitsReasonCDs(
			List<String> federalLimitsReasonCDs) {
		this.federalLimitsReasonCDs = new ArrayList<String>();
		if (federalLimitsReasonCDs != null) {
			this.federalLimitsReasonCDs.addAll(federalLimitsReasonCDs);
		}
	}

	@Override
	public final void populate(java.sql.ResultSet rs) {
		try {
			super.populate(rs);
			setWorkStartDate(rs.getTimestamp("work_start_date"));
			setWorkStartAfterPermit(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("work_start_after_permit_flag")));
			setOperationBeginDate(rs.getTimestamp("operation_begin_dt"));
			setReconstructionDesc(rs.getString("reconstruction_desc"));
			setShutdownYears(AbstractDAO.getInteger(rs, "shutdown_years_qty"));
			setFederalLimitsOtherReasonDesc(rs
					.getString("fed_limits_other_reason_desc"));
			setModificationDesc(rs.getString("modification_dsc"));
			setLastModified(AbstractDAO.getInteger(rs, "ppe_lm"));
			setGeneralPermitTypeCd(rs.getString("general_permit_type_cd"));
			setModelGeneralPermitCd(rs.getString("model_general_permit_cd"));
			setRequestingFederalLimitsFlag(rs
					.getString("requesting_fed_limits_flag"));
			setBactFlag(rs.getString("bact_flag"));
			setLaerFlag(rs.getString("laer_flag"));

			setNspsApplicableFlag(rs.getString("nsps_applicable_flag"));
			setNeshapApplicableFlag(rs.getString("neshaps_applicable_flag"));
			setMactApplicableFlag(rs.getString("mact_applicable_flag"));
			setPsdApplicableFlag(rs.getString("psd_applicable_flag"));
			setGhgApplicableFlag(rs.getString("ghg_applicable_flag"));
			setNsrApplicableFlag(rs.getString("nsr_applicable_flag"));
			setRiskManagementPlanFlag(rs.getString("rm_applicable_flag"));
			setTitleIVFlag(rs.getString("tiv_applicable_flag"));
			setFederalRuleApplicabilityExplanation(rs
					.getString("federal_rule_appl_explanation"));
			setPsdBACTFlag(rs.getString("psd_bact_flag"));
			setNsrLAERFlag(rs.getString("nsr_laer_flag"));

			setOpSchedHrsDay(AbstractDAO.getInteger(rs, "op_sched_hrs_day"));
			setOpSchedHrsYr(AbstractDAO.getInteger(rs, "op_sched_hrs_yr"));

		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result
				+ ((bactFlag == null) ? 0 : bactFlag.hashCode());
		result = PRIME * result
				+ ((laerFlag == null) ? 0 : laerFlag.hashCode());
		result = PRIME * result
				+ ((psdBACTFlag == null) ? 0 : psdBACTFlag.hashCode());
		result = PRIME * result
				+ ((nsrLAERFlag == null) ? 0 : nsrLAERFlag.hashCode());
		result = PRIME
				* result
				+ ((federalLimitsOtherReasonDesc == null) ? 0
						: federalLimitsOtherReasonDesc.hashCode());
		result = PRIME * result + (generalPermit ? 1231 : 1237);
		result = PRIME
				* result
				+ ((generalPermitTypeCd == null) ? 0 : generalPermitTypeCd
						.hashCode());
		result = PRIME
				* result
				+ ((modelGeneralPermitCd == null) ? 0 : modelGeneralPermitCd
						.hashCode());
		result = PRIME
				* result
				+ ((modificationDesc == null) ? 0 : modificationDesc.hashCode());
		result = PRIME
				* result
				+ ((operationBeginDate == null) ? 0 : operationBeginDate
						.hashCode());
		result = PRIME * result
				+ ((ptioEUPurposeCD == null) ? 0 : ptioEUPurposeCD.hashCode());
		result = PRIME
				* result
				+ ((reconstructionDesc == null) ? 0 : reconstructionDesc
						.hashCode());
		result = PRIME
				* result
				+ ((requestingFederalLimitsFlag == null) ? 0
						: requestingFederalLimitsFlag.hashCode());
		result = PRIME * result
				+ ((shutdownYears == null) ? 0 : shutdownYears.hashCode());
		result = PRIME * result + (workStartAfterPermit ? 1231 : 1237);
		result = PRIME * result
				+ ((workStartDate == null) ? 0 : workStartDate.hashCode());
		result = PRIME * result + ((euType == null) ? 0 : euType.hashCode());
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
		final PTIOApplicationEU other = (PTIOApplicationEU) obj;
		if (bactFlag == null) {
			if (other.bactFlag != null)
				return false;
		} else if (!bactFlag.equals(other.bactFlag))
			return false;
		if (federalLimitsOtherReasonDesc == null) {
			if (other.federalLimitsOtherReasonDesc != null)
				return false;
		} else if (!federalLimitsOtherReasonDesc
				.equals(other.federalLimitsOtherReasonDesc))
			return false;
		if (generalPermit != other.generalPermit)
			return false;
		if (generalPermitTypeCd == null) {
			if (other.generalPermitTypeCd != null)
				return false;
		} else if (!generalPermitTypeCd.equals(other.generalPermitTypeCd))
			return false;
		if (modelGeneralPermitCd == null) {
			if (other.modelGeneralPermitCd != null)
				return false;
		} else if (!modelGeneralPermitCd.equals(other.modelGeneralPermitCd))
			return false;
		if (modificationDesc == null) {
			if (other.modificationDesc != null)
				return false;
		} else if (!modificationDesc.equals(other.modificationDesc))
			return false;
		if (operationBeginDate == null) {
			if (other.operationBeginDate != null)
				return false;
		} else if (!operationBeginDate.equals(other.operationBeginDate))
			return false;
		if (ptioEUPurposeCD == null) {
			if (other.ptioEUPurposeCD != null)
				return false;
		} else if (!ptioEUPurposeCD.equals(other.ptioEUPurposeCD))
			return false;
		if (reconstructionDesc == null) {
			if (other.reconstructionDesc != null)
				return false;
		} else if (!reconstructionDesc.equals(other.reconstructionDesc))
			return false;
		if (requestingFederalLimitsFlag == null) {
			if (other.requestingFederalLimitsFlag != null)
				return false;
		} else if (!requestingFederalLimitsFlag
				.equals(other.requestingFederalLimitsFlag))
			return false;
		if (shutdownYears == null) {
			if (other.shutdownYears != null)
				return false;
		} else if (!shutdownYears.equals(other.shutdownYears))
			return false;
		if (workStartAfterPermit != other.workStartAfterPermit)
			return false;
		if (workStartDate == null) {
			if (other.workStartDate != null)
				return false;
		} else if (!workStartDate.equals(other.workStartDate))
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
		if (psdBACTFlag == null) {
			if (other.psdBACTFlag != null)
				return false;
		} else if (!psdBACTFlag.equals(other.psdBACTFlag))
			return false;
		if (nsrLAERFlag == null) {
			if (other.nsrLAERFlag != null)
				return false;
		} else if (!nsrLAERFlag.equals(other.nsrLAERFlag))
			return false;
		if (laerFlag == null) {
			if (other.laerFlag != null)
				return false;
		} else if (!laerFlag.equals(other.laerFlag))
			return false;
		if (euType == null) {
			if (other.euType != null)
				return false;
		} else if (!euType.equals(other.euType))
			return false;
		return true;
	}

	public final Timestamp getOperationBeginDate() {
		return operationBeginDate;
	}

	public final void setOperationBeginDate(Timestamp operationBeginDate) {
		this.operationBeginDate = operationBeginDate;
		if (this.operationBeginDate != null) {
			this.operationBeginDateLong = this.operationBeginDate.getTime();
		} else {
			this.operationBeginDateLong = 0;
		}
	}

	public final boolean isWorkStartAfterPermit() {
		return workStartAfterPermit;
	}

	public final void setWorkStartAfterPermit(boolean workStartAfterPermit) {
		if (workStartAfterPermit) {
			workStartDate = null;
			workStartDateLong = 0;
		}
		this.workStartAfterPermit = workStartAfterPermit;
	}

	public final String getModificationDesc() {
		return modificationDesc;
	}

	public final void setModificationDesc(String modificationDesc) {
		this.modificationDesc = modificationDesc;
	}

	public final String getGeneralPermitTypeCd() {
		return generalPermitTypeCd;
	}

	public final void setGeneralPermitTypeCd(String generalPermitTypeCd) {
		this.generalPermitTypeCd = generalPermitTypeCd;
	}

	public final String getModelGeneralPermitCd() {
		return modelGeneralPermitCd;
	}

	public final void setModelGeneralPermitCd(String modelGeneralPermitCd) {
		this.modelGeneralPermitCd = modelGeneralPermitCd;
	}

	public final List<ApplicationEUEmissions> getCapEmissions() {
		if (capEmissions == null) {
			capEmissions = new ArrayList<ApplicationEUEmissions>();
		}
		return capEmissions;
	}

	public final void setCapEmissions(List<ApplicationEUEmissions> capEmissions) {
		this.capEmissions = new ArrayList<ApplicationEUEmissions>();
		if (capEmissions != null) {
			this.capEmissions.addAll(capEmissions);
		}
	}

	public final String getBactFlag() {
		return bactFlag;
	}

	public final void setBactFlag(String bactFlag) {
		this.bactFlag = bactFlag;
	}

	public final boolean isGeneralPermit() {
		return generalPermit;
	}

	public final void setGeneralPermit(boolean generalPermit) {
		this.generalPermit = generalPermit;
	}

	public final String getPtioEUPurposeCD() {
		return ptioEUPurposeCD;
	}

	public final void setPtioEUPurposeCD(String ptioEUPurposeCD) {
		this.ptioEUPurposeCD = ptioEUPurposeCD;
	}

	public final long getOperationBeginDateLong() {
		long date = 0;
		if (operationBeginDate != null) {
			date = operationBeginDate.getTime();
		}
		return date;
	}

	public final void setOperationBeginDateLong(long operationBeginDateLong) {
		operationBeginDate = null;
		if (operationBeginDateLong > 0) {
			operationBeginDate = new Timestamp(operationBeginDateLong);
		}
	}

	public final long getWorkStartDateLong() {
		long date = 0;
		if (workStartDate != null) {
			date = workStartDate.getTime();
		}
		return date;
	}

	public final void setWorkStartDateLong(long workStartDateLong) {
		workStartDate = null;
		if (workStartDateLong > 0) {
			workStartDate = new Timestamp(workStartDateLong);
		}
	}

	public final List<ApplicationEUEmissions> getHapTacEmissions() {
		if (hapTacEmissions == null) {
			hapTacEmissions = new ArrayList<ApplicationEUEmissions>();
		}
		return hapTacEmissions;
	}

	public final void setHapTacEmissions(
			List<ApplicationEUEmissions> hapEmissions) {
		hapTacEmissions = new ArrayList<ApplicationEUEmissions>();
		if (hapEmissions != null) {
			hapTacEmissions.addAll(hapEmissions);
		}
	}

	public final List<ApplicationEUEmissions> getGhgEmissions() {
		if (ghgEmissions == null) {
			ghgEmissions = new ArrayList<ApplicationEUEmissions>();
		}
		return ghgEmissions;
	}

	public final void setOthEmissions(List<ApplicationEUEmissions> othEmissions) {
		this.othEmissions = new ArrayList<ApplicationEUEmissions>();
		if (othEmissions != null) {
			this.othEmissions.addAll(othEmissions);
		}
	}

	public final List<ApplicationEUEmissions> getOthEmissions() {
		if (othEmissions == null) {
			othEmissions = new ArrayList<ApplicationEUEmissions>();
		}
		return othEmissions;
	}

	public final void setGhgEmissions(List<ApplicationEUEmissions> ghgEmissions) {
		this.ghgEmissions = new ArrayList<ApplicationEUEmissions>();
		if (ghgEmissions != null) {
			this.ghgEmissions.addAll(ghgEmissions);
		}
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		in.defaultReadObject();
		// manually set transient date values since this does not appear to
		// work properly with persistence
		setWorkStartDateLong(this.workStartDateLong);
		setOperationBeginDateLong(this.operationBeginDateLong);
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

	public final String getFederalRuleApplicabilityExplanation() {
		if (null != federalRuleApplicabilityExplanation &&
				!getFedRulesExemption()) {
			federalRuleApplicabilityExplanation = null;
		}
		return federalRuleApplicabilityExplanation;
	}

	public final void setFederalRuleApplicabilityExplanation(
			String federalRuleApplicabilityExplanation) {
		this.federalRuleApplicabilityExplanation = federalRuleApplicabilityExplanation;
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

	public List<NSRApplicationBACTEmission> getBactEmissions() {
		if (bactEmissions == null) {
			bactEmissions = new ArrayList<NSRApplicationBACTEmission>();
		}

		return bactEmissions;
	}

	public void setBactEmissions(List<NSRApplicationBACTEmission> bactEmissions) {
		this.bactEmissions = new ArrayList<NSRApplicationBACTEmission>();

		if (bactEmissions != null) {
			this.bactEmissions.addAll(bactEmissions);
		}
	}

	public final void addBACTEmission(NSRApplicationBACTEmission emission) {
		if (bactEmissions == null) {
			bactEmissions = new ArrayList<NSRApplicationBACTEmission>();
		}

		if (emission != null) {
			bactEmissions.add(emission);
		}
	}

	public final void removeBACTEmission(NSRApplicationBACTEmission emission) {
		bactEmissions.remove(emission);
	}

	public final boolean isBactAnalysisCompleted() {
		return AbstractDAO.translateIndicatorToBoolean(bactFlag);
	}

	public final boolean isBactAnalysisAttached() {
		boolean ret = false;
		List<ApplicationDocumentRef> euDocs = getEuDocuments();
		for (ApplicationDocumentRef doc : euDocs) {
			if (doc.getApplicationDocumentTypeCD().equals(
					ApplicationDocumentTypeDef.BACT_ANALYSIS)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	public List<NSRApplicationLAEREmission> getLaerEmissions() {
		if (laerEmissions == null) {
			laerEmissions = new ArrayList<NSRApplicationLAEREmission>();
		}

		return laerEmissions;
	}

	public void setLaerEmissions(List<NSRApplicationLAEREmission> laerEmissions) {
		this.laerEmissions = new ArrayList<NSRApplicationLAEREmission>();

		if (laerEmissions != null) {
			this.laerEmissions.addAll(laerEmissions);
		}
	}

	public final void addLAEREmission(NSRApplicationLAEREmission emission) {
		if (laerEmissions == null) {
			laerEmissions = new ArrayList<NSRApplicationLAEREmission>();
		}

		if (emission != null) {
			laerEmissions.add(emission);
		}
	}

	public final void removeLAEREmission(NSRApplicationLAEREmission emission) {
		laerEmissions.remove(emission);
	}

	public final boolean isLaerAnalysisCompleted() {
		return AbstractDAO.translateIndicatorToBoolean(laerFlag);
	}

	public String getPsdBACTFlag() {
		return psdBACTFlag;
	}

	public void setPsdBACTFlag(String psdBACTFlag) {
		this.psdBACTFlag = psdBACTFlag;
	}

	public String getNsrLAERFlag() {
		return nsrLAERFlag;
	}

	public void setNsrLAERFlag(String nsrLAERFlag) {
		this.nsrLAERFlag = nsrLAERFlag;
	}

	public String getLaerFlag() {
		return laerFlag;
	}

	public void setLaerFlag(String laerFlag) {
		this.laerFlag = laerFlag;
	}

	public Integer getOpSchedHrsDay() {
		return opSchedHrsDay;
	}

	public void setOpSchedHrsDay(Integer opSchedHrsDay) {
		this.opSchedHrsDay = opSchedHrsDay;
	}

	public Integer getOpSchedHrsYr() {
		return opSchedHrsYr;
	}

	public void setOpSchedHrsYr(Integer opSchedHrsYr) {
		this.opSchedHrsYr = opSchedHrsYr;
	}

	public NSRApplicationEUType getEuType() {
		logger.debug("--> getEuType() -> " + euType);
		return euType;
	}

	public void setEuType(NSRApplicationEUType euType) {
		this.euType = euType;
		logger.debug("--> setEuType() -> " + euType);
	}
	

	public final void addApplicationEUFugitiveLeaks(
			ApplicationEUFugitiveLeaks fugitiveLeaks) {
		if (applicationEUFugitiveLeaks != null) {
			applicationEUFugitiveLeaks.add(fugitiveLeaks);
		}
	}

	public final void removeApplicationEUFugitiveLeaks(
			ApplicationEUFugitiveLeaks fugitiveLeaks) {
		applicationEUFugitiveLeaks.remove(fugitiveLeaks);
	}

	public List<ApplicationEUFugitiveLeaks> getApplicationEUFugitiveLeaks() {
		return applicationEUFugitiveLeaks;
	}

	public void setApplicationEUFugitiveLeaks(
			List<ApplicationEUFugitiveLeaks> applicationEUFugitiveLeaks) {
		this.applicationEUFugitiveLeaks = applicationEUFugitiveLeaks;
	}

	public boolean hasUniqueBACTPollutants() {
		boolean ret = true;

		List<String> bactPollutantList = new ArrayList<String>();
		for (NSRApplicationBACTEmission bact : getBactEmissions()) {
			bactPollutantList.add(bact.getPollutantCd());
		}

		Set<String> bactPollutantSet = new HashSet<String>(bactPollutantList);

		if (bactPollutantSet.size() < bactPollutantList.size()) {
			ret = false;
		}

		return ret;
	}
	
	public boolean hasUniqueLAERPollutants() {
		boolean ret = true;

		List<String> laerPollutantList = new ArrayList<String>();
		for (NSRApplicationLAEREmission laer : getLaerEmissions()) {
			laerPollutantList.add(laer.getPollutantCd());
		}

		Set<String> laerPollutantSet = new HashSet<String>(laerPollutantList);

		if (laerPollutantSet.size() < laerPollutantList.size()) {
			ret = false;
		}

		return ret;
	}
	
	public final List<ApplicationEUMaterialUsed> getMaterialUsed() {
		if (materialUsed == null) {
			materialUsed = new ArrayList<ApplicationEUMaterialUsed>();
		}
		return materialUsed;
	}

	public final void setMaterialUsed(List<ApplicationEUMaterialUsed> materialUsed) {
		this.materialUsed = new ArrayList<ApplicationEUMaterialUsed>();
		if (materialUsed != null) {
			this.materialUsed.addAll(materialUsed);
		}
	}
}
