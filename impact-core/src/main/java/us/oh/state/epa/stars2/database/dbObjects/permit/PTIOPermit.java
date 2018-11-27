package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.NSRBillingChargePaymentTypeDef;
import us.oh.state.epa.stars2.def.PTIOPERDueDateDef;
import us.oh.state.epa.stars2.def.PermitActionTypeDef;
import us.oh.state.epa.stars2.def.PermitReceivedCommentsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class PTIOPermit extends Permit {
	private boolean tv;
	private boolean convertedToPTI;
	private Timestamp convertedToPTIDate;
	private String perDueDateCD;
	private String tmpPERDueDateCD;
	private boolean netting;
	private boolean emissionsOffsets;
	private boolean smtv;
	private boolean cem;
	private boolean modelingSubmitted;
	private boolean psd;
	private boolean toxicReview;
	private boolean feptio; // PTIO only
	private boolean nonFeptio5YrRenewal; // PTIO Only
	private boolean avoidMajorGHGSM;
	private String ptioRenewalType;

	private boolean fePtioTvAvoid;
	private boolean fullCostRecovery;
	private boolean express;
	private Double otherAdjustment = new Double(0);
	private Double initialInvoice = new Double(0);
	private Double finalInvoice = new Double(0);
	private boolean majorNonAttainment;
	private boolean generalPermit;
	private ArrayList<PermitEU> withFeeEus;
	private Double totalAmount = new Double(0);
	private String permitActionType;
	private String subjectToPSD;
	private String subjectToNANSR;
	private String otherTypeOfDemonstrationReq;
	private boolean offsetInformationVerified;
	private String modelingRequired;
	private Timestamp modelingCompletedDate;
	private boolean actionTypePermit;
	private Timestamp permitSentOutDate;
	private String invoicePaid;
	private String commentTransmittalLettersSentFlag;
	private Timestamp _recissionDate;
	private boolean billable;
	private Timestamp lastInvoiceRefDate;
	
	// NSR Billing
	private List<NSRFixedCharge> _nsrFixedChargeList;
	private List<TimeSheetRow> _nsrTimeSheetRowList;
	private List<PermitChargePayment> _permitChargePaymentList;
	private boolean timeCardInfoRetrieved;
	
	private BigDecimal currentTotal;
	
	private String companyPayKey;
	private Long companyVendorNumber;
	
	private List<EmissionsOffset> emissionsOffsetList;
	
	/**
     * 
     */
	public PTIOPermit() {
		super();
		setPermitType(PermitTypeDef.NSR);
		setBillable(true);
		// validationMessages.put("perDueDateCD",
		// new ValidationMessage("PER Due Date", "Missing PER due date.",
		// ValidationMessage.Severity.ERROR));
		setDirty(false);
	}

	/**
	 * @param old
	 */
	public PTIOPermit(PTIOPermit old) {

		super(old);
		if (old != null) {
			setTv(old.isTv());
			setConvertedToPTI(old.isConvertedToPTI());
			setPerDueDateCD(old.getPerDueDateCD());
			setTmpPERDueDateCD(old.getTmpPERDueDateCD());
			setNetting(old.isNetting());
			setEmissionsOffsets(old.isEmissionsOffsets());
			setSmtv(old.isSmtv());
			setCem(old.isCem());
			setModelingSubmitted(old.isModelingSubmitted());
			setPsd(old.isPsd());
			setToxicReview(old.isToxicReview());
			setFeptio(old.isFeptio());
			setNonFeptio5YrRenewal(old.isNonFeptio5YrRenewal());
			setAvoidMajorGHGSM(old.isAvoidMajorGHGSM());
			setFePtioTvAvoid(old.isFePtioTvAvoid());
			setFullCostRecovery(old.isFullCostRecovery());
			setExpress(old.isExpress());
			setOtherAdjustment(old.getOtherAdjustment());
			setInitialInvoice(old.getInitialInvoice());
			setFinalInvoice(old.getFinalInvoice());
			setGeneralPermit(old.isGeneralPermit());
			setMajorNonAttainment(old.isMajorNonAttainment());
			setLastModified(old.getLastModified());
			setDirty(old.isDirty());
			setPermitActionType(old.getPermitActionType());
			setSubjectToPSD(old.getSubjectToPSD());
			setSubjectToNANSR(old.getSubjectToNANSR());
			setOtherTypeOfDemonstrationReq(old.getOtherTypeOfDemonstrationReq());
			setOffsetInformationVerified(old.getOffsetInformationVerified());
			setModelingRequired(old.getModelingRequired());
			setModelingCompletedDate(old.getModelingCompletedDate());
			setPermitSentOutDate(old.getPermitSentOutDate());
			setInvoicePaid(old.getInvoicePaid());
			setCommentTransmittalLettersSentFlag(old
					.getCommentTransmittalLettersSentFlag());
			setRecissionDate(old.getRecissionDate());
			setBillable(old.isBillable());
			setLastInvoiceRefDate(old.getLastInvoiceRefDate());
			setNSRFixedChargeList(old.getNSRFixedChargeList());
			setNSRTimeSheetRowList(old.getNSRTimeSheetRowList());
			setPermitChargePaymentList(old.getPermitChargePaymentList());
			setCompanyPayKey(old.getCompanyPayKey());
			setCompanyVendorNumber(old.getCompanyVendorNumber());
			setEmissionsOffsetList(old.getEmissionsOffsetList());
		}
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dbObjects.permit.Permit#populate(java.sql.ResultSet)
	 */
	public void populate(ResultSet rs) {

		try {
			super.populate(rs);

			setTmpPERDueDateCD(rs.getString("per_due_date_cd"));
			setTv(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("tv_flag")));
			setConvertedToPTI(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("converted_to_pti")));
			setConvertedToPTIDate(rs.getTimestamp("CONVERTED_TO_PTI_DATE"));
			setNetting(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("netting_flag")));
			setEmissionsOffsets(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("emissions_offsets_flag")));
			setSmtv(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("smtv_flag")));
			setCem(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("cem_flag")));
			setModelingSubmitted(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("modeling_submitted_flag")));
			setPsd(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("psd_flag")));
			setToxicReview(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("toxic_review_flag")));
			setFeptio(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("fe_ptio_flag")));
			setNonFeptio5YrRenewal(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("non_feptio_5yr_renewal_flag")));
			// TODO get rid of try catch when new field is added
			try {
				setAvoidMajorGHGSM(AbstractDAO.translateIndicatorToBoolean(rs
						.getString("avoid_major_ghg_sm_flag")));
			} catch (SQLException sqle) {
				logger.error("Required field error [avoid_major_ghg_sm_flag]: "
						+ sqle.getMessage(), sqle);
			}
			setFePtioTvAvoid(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("fe_ptio_tv_avoid_flag")));
			setFullCostRecovery(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("full_cost_recovery_flag")));
			setExpress(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("express_flag")));
			setOtherAdjustment(AbstractDAO.getDouble(rs, "other_adjustment"));
			setInitialInvoice(AbstractDAO.getDouble(rs, "initial_invoice"));
			setFinalInvoice(AbstractDAO.getDouble(rs, "final_invoice"));
			setMajorNonAttainment(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("major_non_attainment_flag")));
			setGeneralPermit(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("general_permit_flag")));
			setModelingRequired(rs.getString("modeling_required_flag"));
			setModelingCompletedDate(rs.getTimestamp("modeling_complete_date"));
			setPermitActionType(rs.getString("action_type"));
			setIssueDraft(AbstractDAO.translateIndicatorToBoolean(rs.getString("issue_draft_flag")));
			setSubjectToPSD(rs.getString("subject_to_psd_flag"));
			setSubjectToNANSR(rs.getString("subject_to_nansr_flag"));
			setOtherTypeOfDemonstrationReq(rs
					.getString("other_type_demonstration_required_flag"));
			setOffsetInformationVerified(AbstractDAO.translateIndicatorToBoolean(rs
					.getString("offset_information_verified")));
			setPermitSentOutDate(rs.getTimestamp("permit_sent_out_date"));
			setInvoicePaid(rs.getString("invoice_paid_cd"));
			setCommentTransmittalLettersSentFlag(rs
					.getString("comment_transmittal_letters_sent_flag"));

			setLastModified(AbstractDAO.getInteger(rs, "ppp_lm"));
			setRecissionDate(rs.getTimestamp("recission_date"));
			setBillable(AbstractDAO.translateIndicatorToBoolean(rs.getString("billable")));
			setLastInvoiceRefDate(rs.getTimestamp("last_invoice_ref_date"));
			setCompanyPayKey(rs.getString("company_paykey"));
			setCompanyVendorNumber(AbstractDAO.getLong(rs, "company_vendor_number"));
			setDirty(false);

		} catch (SQLException sqle) {
			logger.error("Required field error: " + sqle.getMessage(), sqle);
		}
	}

	public void otherTypeOfDemonstrationReqChanged(ValueChangeEvent event) {
		if (!"Y".equals(event.getNewValue())) {
			setOffsetInformationVerified(false);
		}
	}
	
	public boolean getOffsetInformationVerified() {
		return offsetInformationVerified;
	}

	public String getOffsetInformationVerifiedYesNo() {
		return offsetInformationVerified? "Yes" : "No";
	}

	public void setOffsetInformationVerified(boolean offsetInformationVerified) {
		this.offsetInformationVerified = offsetInformationVerified;
	}

	public final boolean isNetting() {
		return netting;
	}

	public final void setNetting(boolean netting) {
		this.netting = netting;
		setDirty(true);
	}

	public final boolean isEmissionsOffsets() {
		return emissionsOffsets;
	}

	public final void setEmissionsOffsets(boolean emissionsOffsets) {
		this.emissionsOffsets = emissionsOffsets;
		setDirty(true);
	}

	public final boolean isSmtv() {
		return smtv;
	}

	public final void setSmtv(boolean smtv) {
		this.smtv = smtv;
		setDirty(true);
	}

	public final boolean isCem() {
		return cem;
	}

	public final void setCem(boolean cem) {
		this.cem = cem;
		setDirty(true);
	}

	public final boolean isModelingSubmitted() {
		return modelingSubmitted;
	}

	public final void setModelingSubmitted(boolean modelingSubmitted) {
		this.modelingSubmitted = modelingSubmitted;
		setDirty(true);
	}

	public final boolean isPsd() {
		return psd;
	}

	public final void setPsd(boolean psd) {
		this.psd = psd;
		setDirty(true);
	}

	public final boolean isToxicReview() {
		return toxicReview;
	}

	public final void setToxicReview(boolean toxicReview) {
		this.toxicReview = toxicReview;
		setDirty(true);
	}

	public final boolean isTv() {
		return tv;
	}

	public final void setTv(boolean tv) {
		this.tv = tv;
		String pt = getPermitType();
		//if (pt.equals(PermitTypeDef.TVPTI) || pt.equals(PermitTypeDef.NSR)) {
		if (pt.equals(PermitTypeDef.NSR)) {
			//if (tv) {
			//	setPermitType(PermitTypeDef.TVPTI);
			//	validationMessages.remove("perDueDateCD");
			//} else {
				setPermitType(PermitTypeDef.NSR);
				// if (perDueDateCD == null) {
				// validationMessages.put("perDueDateCD",
				// new ValidationMessage("PER Due Date",
				// "Missing PER due date.",
				// ValidationMessage.Severity.ERROR));
				// }
			//}
		}
		setDirty(true);
	}

	public final boolean isConvertedToPTI() {
		return convertedToPTI;
	}

	public final void setConvertedToPTI(boolean convertedToPTI) {

		this.convertedToPTI = convertedToPTI;
		if (convertedToPTI && convertedToPTIDate == null) {
			validationMessages.put("convertedToPTIDate", new ValidationMessage(
					"Converted To PTI Date",
					"Missing missing PTI conversion date.",
					ValidationMessage.Severity.ERROR));
			setTv(convertedToPTI);
		} else {
			validationMessages.remove("convertedToPTIDate");
		}
		setDirty(true);
	}

	public final Timestamp getConvertedToPTIDate() {
		return convertedToPTIDate;
	}

	public final void setConvertedToPTIDate(Timestamp convertedToPTIDate) {

		this.convertedToPTIDate = convertedToPTIDate;
		if (convertedToPTI && convertedToPTIDate == null) {
			validationMessages.put("convertedToPTIDate", new ValidationMessage(
					"Converted To PTI Date", "Missing PTI conversion date.",
					ValidationMessage.Severity.ERROR));
			setTv(convertedToPTI);
		} else {
			validationMessages.remove("convertedToPTIDate");
		}
		setDirty(true);
	}

	public final boolean isFeptio() {
		return feptio;
	}

	public final void setFeptio(boolean feptio) {
		this.feptio = feptio;
		if (!this.feptio) {
			setFePtioTvAvoid(false);
		}
		setDirty(true);
	}

	public final boolean isNonFeptio5YrRenewal() {
		return nonFeptio5YrRenewal;
	}

	public final void setNonFeptio5YrRenewal(boolean nonFeptio5YrRenewal) {
		this.nonFeptio5YrRenewal = nonFeptio5YrRenewal;
	}

	public final boolean isAvoidMajorGHGSM() {
		return avoidMajorGHGSM;
	}

	public final void setAvoidMajorGHGSM(boolean avoidMajorGHGSM) {
		this.avoidMajorGHGSM = avoidMajorGHGSM;
	}

	public final String getPtioRenewalType() {
		if (feptio) {
			ptioRenewalType = "feptio5YrRenewal";
		} else if (nonFeptio5YrRenewal) {
			ptioRenewalType = "nonFeptio5YrRenewal";
		} else {
			ptioRenewalType = "ptio10YrRenewal";
		}
		return ptioRenewalType;
	}

	public final void setPtioRenewalType(String ptioRenewalType) {
		setFeptio("feptio5YrRenewal".equals(ptioRenewalType));
		setNonFeptio5YrRenewal("nonFeptio5YrRenewal".equals(ptioRenewalType));
		this.ptioRenewalType = ptioRenewalType;
	}

	public final boolean isFePtioTvAvoid() {
		return fePtioTvAvoid;
	}

	public final void setFePtioTvAvoid(boolean avoid) {
		fePtioTvAvoid = avoid;
		setDirty(true);
	}

	public final boolean isFullCostRecovery() {
		return fullCostRecovery;
	}

	public final void setFullCostRecovery(boolean fullCostRecovery) {
		this.fullCostRecovery = fullCostRecovery;
		setDirty(true);
	}

	public final boolean isExpress() {
		return express;
	}

	public final void setExpress(boolean express) {
		this.express = express;
		setDirty(true);
	}

	public final Double getOtherAdjustment() {
		if (otherAdjustment == null) {
			otherAdjustment = new Double(0);
		}
		return otherAdjustment;
	}

	public final Double getInitialInvoice() {
		if (initialInvoice == null) {
			initialInvoice = new Double(0);
		}
		return initialInvoice;
	}

	public final Double getFinalInvoice() {
		if (finalInvoice == null) {
			finalInvoice = new Double(0);
		}
		return finalInvoice;
	}

	public final void setOtherAdjustment(Double otherAdjustment) {
		this.otherAdjustment = otherAdjustment;
		if (otherAdjustment == null) {
			otherAdjustment = new Double(0);
		}
		setDirty(true);
	}

	public final void setInitialInvoice(Double initialInvoice) {
		this.initialInvoice = initialInvoice;
		if (initialInvoice == null) {
			initialInvoice = new Double(0);
		}
		setDirty(true);
	}

	public final void setFinalInvoice(Double finalInvoice) {
		this.finalInvoice = finalInvoice;
		if (finalInvoice == null) {
			finalInvoice = new Double(0);
		}
		setDirty(true);
	}

	public final boolean isGeneralPermit() {
		return generalPermit;
	}

	public final void setGeneralPermit(boolean generalPermit) {
		this.generalPermit = generalPermit;
		setDirty(true);
	}

	public final boolean isMajorNonAttainment() {
		return majorNonAttainment;
	}

	public final void setMajorNonAttainment(boolean majorNonAttainment) {
		this.majorNonAttainment = majorNonAttainment;
		setDirty(true);
	}

	public final String getPerDueDateCD() {
		return perDueDateCD;
	}

	public final void setPerDueDateCD(String perDueDateCD) {

		this.perDueDateCD = perDueDateCD;

		if (this.perDueDateCD == null && getPermitType() != null
				&& getPermitType().equals(PermitTypeDef.NSR)) {
			// validationMessages.put("perDueDateCD",
			// new ValidationMessage("PER Due Date", "Missing PER due date.",
			// ValidationMessage.Severity.ERROR));
		} else {
			validationMessages.remove("perDueDateCD");
		}

		setDirty(true);
	}

	public final String getTmpPERDueDateCD() {
		if (tmpPERDueDateCD == null || tmpPERDueDateCD.length() < 1) {
			tmpPERDueDateCD = getPerDueDateCD();
		}
		if (tmpPERDueDateCD == null || tmpPERDueDateCD.length() < 1) {
			tmpPERDueDateCD = PTIOPERDueDateDef.NOT_APPLICABLE;
		}
		return tmpPERDueDateCD;
	}

	public final void setTmpPERDueDateCD(String perDueDateCD) {

		this.tmpPERDueDateCD = perDueDateCD;
		setDirty(true);
	}
	
	public List<EmissionsOffset> getEmissionsOffsetList() {
		return emissionsOffsetList;
	}

	public void setEmissionsOffsetList(List<EmissionsOffset> emissionsOffsetList) {
		this.emissionsOffsetList = emissionsOffsetList;
	}

	@Override
	public int hashCode() {

		final int PRIME = 31;
		int result = super.hashCode();

		result = PRIME * result + (tv ? 1 : 0);
		result = PRIME * result + (convertedToPTI ? 1 : 0);
		result = PRIME
				* result
				+ ((convertedToPTIDate == null) ? 0 : convertedToPTIDate
						.hashCode());
		result = PRIME * result
				+ ((perDueDateCD == null) ? 0 : perDueDateCD.hashCode());
		result = PRIME * result + (netting ? 1 : 0);
		result = PRIME * result + (emissionsOffsets ? 1 : 0);
		result = PRIME * result + (smtv ? 1 : 0);
		result = PRIME * result + (cem ? 1 : 0);
		result = PRIME * result + (modelingSubmitted ? 1 : 0);
		result = PRIME * result + (psd ? 1 : 0);
		result = PRIME * result + (toxicReview ? 1 : 0);
		result = PRIME * result + (feptio ? 1 : 0);
		result = PRIME * result + (fullCostRecovery ? 1 : 0);
		result = PRIME * result + (express ? 1 : 0);
		result = PRIME * result
				+ ((otherAdjustment == null) ? 0 : otherAdjustment.hashCode());
		result = PRIME * result
				+ ((initialInvoice == null) ? 0 : initialInvoice.hashCode());
		result = PRIME * result
				+ ((finalInvoice == null) ? 0 : finalInvoice.hashCode());
		result = PRIME * result + (majorNonAttainment ? 1 : 0);
		result = PRIME * result + (generalPermit ? 1 : 0);
		result = PRIME
				* result
				+ ((modelingRequired == null) ? 0 : modelingRequired.hashCode());
		result = PRIME
				* result
				+ ((modelingCompletedDate == null) ? 0 : modelingCompletedDate
						.hashCode());
		result = PRIME
				* result
				+ ((modelingRequired == null) ? 0 : modelingRequired.hashCode());
		result = PRIME
				* result
				+ ((modelingCompletedDate == null) ? 0 : modelingCompletedDate
						.hashCode());
		result = PRIME
				* result
				+ ((permitActionType == null) ? 0 : permitActionType.hashCode());
		result = PRIME * result
				+ ((subjectToPSD == null) ? 0 : subjectToPSD.hashCode());
		result = PRIME * result
				+ ((subjectToNANSR == null) ? 0 : subjectToNANSR.hashCode());
		result = PRIME
				* result
				+ ((otherTypeOfDemonstrationReq == null) ? 0
						: otherTypeOfDemonstrationReq.hashCode());
		result = PRIME
				* result
				+ ((offsetInformationVerified) ? 1 : 0);
		result = PRIME
				* result
				+ ((permitSentOutDate == null) ? 0 : permitSentOutDate
						.hashCode());
		result = PRIME * result
				+ ((invoicePaid == null) ? 0 : invoicePaid.hashCode());
		result = PRIME
				* result
				+ ((commentTransmittalLettersSentFlag == null) ? 0
						: commentTransmittalLettersSentFlag.hashCode());
		result = PRIME * result
                + ((_recissionDate == null) ? 0 : _recissionDate.hashCode());
		
		result = PRIME * result + (billable ? 1 : 0);
		
		result = PRIME * result
                + ((lastInvoiceRefDate == null) ? 0 : lastInvoiceRefDate.hashCode());
		
		result = PRIME * result + ((companyPayKey == null) ? 0 : companyPayKey.hashCode());
		
		result = PRIME * result + ((companyVendorNumber == null) ? 0 : companyVendorNumber.hashCode());
		

		return result;

	} // END: public int hashCode()

	@Override
	public final boolean equals(Object obj) {

		if ((obj == null) || !(super.equals(obj))
				|| (getClass() != obj.getClass())) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		final PTIOPermit other = (PTIOPermit) obj;

		if ((tv != other.isTv())
				|| (convertedToPTI != other.isConvertedToPTI())
				|| (netting != other.isNetting())
				|| (emissionsOffsets != other.isEmissionsOffsets())
				|| (smtv != other.isSmtv()) || (cem != other.isCem())
				|| (modelingSubmitted != other.isModelingSubmitted())
				|| (psd != other.isPsd())
				|| (toxicReview != other.isToxicReview())
				|| (feptio != other.isFeptio())
				|| (fullCostRecovery != other.isFullCostRecovery())
				|| (express != other.isExpress())
				|| (majorNonAttainment != other.isMajorNonAttainment())
				|| (generalPermit != other.isGeneralPermit())
				|| (billable != other.isBillable())) {

			return false;
		}

		// Either both null or equal values.
		if (((convertedToPTIDate == null) && (other.getConvertedToPTIDate() != null))
				|| ((convertedToPTIDate != null) && (other
						.getConvertedToPTIDate() == null))
				|| ((convertedToPTIDate != null)
						&& (other.getConvertedToPTIDate() != null) && !(convertedToPTIDate
							.equals(other.getConvertedToPTIDate())))) {

			return false;
		}

		// Either both null or equal values.
		if (((perDueDateCD == null) && (other.getPerDueDateCD() != null))
				|| ((perDueDateCD != null) && (other.getPerDueDateCD() == null))
				|| ((perDueDateCD != null) && (other.getPerDueDateCD() != null) && !(perDueDateCD
						.equals(other.getPerDueDateCD())))) {

			return false;
		}

		// Either both null or equal values.
		if (((otherAdjustment == null) && (other.getOtherAdjustment() != null))
				|| ((otherAdjustment != null) && (other.getOtherAdjustment() == null))
				|| ((otherAdjustment != null)
						&& (other.getOtherAdjustment() != null) && !(otherAdjustment
							.equals(other.getOtherAdjustment())))) {

			return false;
		}

		// Either both null or equal values.
		if (((initialInvoice == null) && (other.getInitialInvoice() != null))
				|| ((initialInvoice != null) && (other.getInitialInvoice() == null))
				|| ((initialInvoice != null)
						&& (other.getInitialInvoice() != null) && !(initialInvoice
							.equals(other.getInitialInvoice())))) {

			return false;
		}

		// Either both null or equal values.
		if (((finalInvoice == null) && (other.getFinalInvoice() != null))
				|| ((finalInvoice != null) && (other.getFinalInvoice() == null))
				|| ((finalInvoice != null) && (other.getFinalInvoice() != null) && !(finalInvoice
						.equals(other.getFinalInvoice())))) {

			return false;
		}

		// Either both null or equal values.
		if (((permitSentOutDate == null) && (other.getPermitSentOutDate() != null))
				|| ((permitSentOutDate != null) && (other
						.getPermitSentOutDate() == null))
				|| ((permitSentOutDate != null)
						&& (other.getPermitSentOutDate() != null) && !(permitSentOutDate
							.equals(other.getPermitSentOutDate())))) {

			return false;
		}

		// Either both null or equal values.
		if (((invoicePaid == null) && (other.getInvoicePaid() != null))
				|| ((invoicePaid != null) && (other.getInvoicePaid() == null))
				|| ((invoicePaid != null) && (other.getInvoicePaid() != null) && !(invoicePaid
						.equals(other.getInvoicePaid())))) {

			return false;
		}

		// Either both null or equal values.
		if (((commentTransmittalLettersSentFlag == null) && (other
				.getCommentTransmittalLettersSentFlag() != null))
				|| ((commentTransmittalLettersSentFlag != null) && (other
						.getCommentTransmittalLettersSentFlag() == null))
				|| ((commentTransmittalLettersSentFlag != null)
						&& (other.getCommentTransmittalLettersSentFlag() != null) && !(commentTransmittalLettersSentFlag
							.equals(other
									.getCommentTransmittalLettersSentFlag())))) {

			return false;
		}
		
		// Either both null or equal values.
        if (((_recissionDate == null) && (other.getRecissionDate() != null))
                || ((_recissionDate != null) && (other
                        .getRecissionDate() == null))
                || ((_recissionDate != null)
                        && (other.getRecissionDate() != null) && !(_recissionDate
                        .equals(other.getRecissionDate())))) {
            return false;
        }
        
     // Either both null or equal values.
        if (((lastInvoiceRefDate == null) && (other.getLastInvoiceRefDate() != null))
                || ((lastInvoiceRefDate != null) && (other
                        .getRecissionDate() == null))
                || ((lastInvoiceRefDate != null)
                        && (other.getRecissionDate() != null) && !(lastInvoiceRefDate
                        .equals(other.getRecissionDate())))) {
            return false;
        }
        
     // Either both null or equal values.
        if (((companyPayKey == null) && (other.getCompanyPayKey() != null))
                || ((companyPayKey != null) && (other
                        .getCompanyPayKey() == null))
                || ((companyPayKey != null)
                        && (other.getCompanyPayKey() != null) && !(companyPayKey
                        .equals(other.getCompanyPayKey())))) {
            return false;
        }
        
     // Either both null or equal values.
        if (((companyVendorNumber == null) && (other.getCompanyVendorNumber() != null))
                || ((companyVendorNumber != null) && (other
                        .getCompanyVendorNumber() == null))
                || ((companyVendorNumber != null)
                        && (other.getCompanyVendorNumber() != null) && !(companyVendorNumber
                        .equals(other.getCompanyVendorNumber())))) {
            return false;
        }

		return true;

	} // END: public final boolean equals(Object obj)

	public final String toXML() {

		StringBuffer sb = new StringBuffer();
		sb.append(tv ? "<pti>" : "<ptio>");
		sb.append(super.toXML());

		sb.append("<toxicReview>" + (toxicReview ? "Yes" : "No")
				+ "</toxicReview>");
		sb.append("<psd>" + (psd ? "Yes" : "No") + "</psd>");
		sb.append("<syntheticMinor>" + (smtv ? "Yes" : "No")
				+ "</syntheticMinor>");
		sb.append("<cems>" + (cem ? "Yes" : "No") + "</cems>");
		sb.append("<netting>" + (netting ? "Yes" : "No") + "</netting>");
		sb.append("<majorNonAttainment>" + (majorNonAttainment ? "Yes" : "No")
				+ "</majorNonAttainment>");
		sb.append("<modelingSubmitted>" + (modelingSubmitted ? "Yes" : "No")
				+ "</modelingSubmitted>");

		sb.append((tv ? "</pti>" : "</ptio>"));
		return sb.toString();
	}

	/**
	 * TODO: Check applicability rules. Does this apply to all PTIOs?
	 */
	public final void createEUFees() {

		withFeeEus = new ArrayList<PermitEU>();
		totalAmount = new Double(0);

		for (PermitEUGroup eg : getEuGroups()) {
			for (PermitEU e : eg.getPermitEUs())
				if (e.getEuFee().getEUFeeId() != null) {
					withFeeEus.add(e);
					// totalAmount += e.getEuFee().getAdjustedAmount();
				}
		}
		totalAmount += /* getOtherAdjustment() + */getInitialInvoice()
				+ getFinalInvoice();
	}

	public Double getTotalAmount() {
		Double ret = totalAmount;
		/*
		 * if (fullCostRecovery) { ret = otherAdjustment; }
		 */
		return ret;
	}

	public ArrayList<PermitEU> getWithFeeEus() {
		return withFeeEus;
	}

	public String getPermitActionType() {
		return permitActionType;
	}

	public void setPermitActionType(String permitActionType) {
		this.permitActionType = permitActionType;
		if (this.permitActionType != null
				&& this.permitActionType.equals(PermitActionTypeDef.PERMIT)) {
			setActionTypePermit(true);
			setIssueDraft(true);
		} else if (this.permitActionType != null
				&& this.permitActionType.equals(PermitActionTypeDef.WAIVER)) {
			setActionTypePermit(false);
			setIssueDraft(false);
		}
	}

	public String getSubjectToPSD() {
		return subjectToPSD;
	}

	public void setSubjectToPSD(String subjectToPSD) {
		this.subjectToPSD = subjectToPSD;
	}

	public String getSubjectToNANSR() {
		return subjectToNANSR;
	}

	public void setSubjectToNANSR(String subjectToNANSR) {
		this.subjectToNANSR = subjectToNANSR;
	}

	public String getOtherTypeOfDemonstrationReq() {
		return otherTypeOfDemonstrationReq;
	}

	public void setOtherTypeOfDemonstrationReq(
			String otherTypeOfDemonstrationReq) {
		this.otherTypeOfDemonstrationReq = otherTypeOfDemonstrationReq;
	}

	public String getModelingRequired() {
		return modelingRequired;
	}

	public void setModelingRequired(String modelingRequired) {
		this.modelingRequired = modelingRequired;
	}

	public Timestamp getModelingCompletedDate() {
		return modelingCompletedDate;
	}

	public void setModelingCompletedDate(Timestamp modelingCompletedDate) {
		this.modelingCompletedDate = modelingCompletedDate;
	}

	public boolean isActionTypePermit() {
		return actionTypePermit;
	}

	public void setActionTypePermit(boolean actionTypePermit) {
		this.actionTypePermit = actionTypePermit;
	}
	
	@Override 
	public boolean isWaiver() {
		return !isActionTypePermit();
	}

	public Timestamp getPermitSentOutDate() {
		return permitSentOutDate;
	}

	public void setPermitSentOutDate(Timestamp permitSentOutDate) {
		this.permitSentOutDate = permitSentOutDate;
		setDirty(true);
	}

	public String getInvoicePaid() {
		return invoicePaid;
	}

	public void setInvoicePaid(String invoicePaid) {
		this.invoicePaid = invoicePaid;
		setDirty(true);
	}

	public String getCommentTransmittalLettersSentFlag() {
		return commentTransmittalLettersSentFlag;
	}

	public void setCommentTransmittalLettersSentFlag(
			String commentTransmittalLettersSentFlag) {
		this.commentTransmittalLettersSentFlag = commentTransmittalLettersSentFlag;
		setDirty(true);
	}

	public boolean isCommentTransmittalRequired() {
		boolean ret = false;
		String receivedComments = this.getReceivedComments();
		if (receivedComments != null) {
			ret = !receivedComments
					.equalsIgnoreCase(PermitReceivedCommentsDef.NO);
		}
		return ret;
	}
	
	public final Timestamp getRecissionDate() {
        return _recissionDate;
    }

    public final void setRecissionDate(Timestamp recissionDate) {
        _recissionDate = recissionDate;
        setDirty(true);
    }

	public boolean isBillable() {
		return billable;
	}

	public void setBillable(boolean billable) {
		this.billable = billable;
	}

	public Timestamp getLastInvoiceRefDate() {
		return lastInvoiceRefDate;
	}

	public void setLastInvoiceRefDate(Timestamp lastInvoiceRefDate) {
		if(null != lastInvoiceRefDate) {
			// set the time to 11:59:59 PM
			Calendar cal = new GregorianCalendar();
			cal.setTimeInMillis(lastInvoiceRefDate.getTime());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			this.lastInvoiceRefDate = new Timestamp(cal.getTimeInMillis());
		} else {
			this.lastInvoiceRefDate = lastInvoiceRefDate;
		}
	}
	
	public final List<NSRFixedCharge> getNSRFixedChargeList() {

		if (_nsrFixedChargeList == null) {
			_nsrFixedChargeList = new ArrayList<NSRFixedCharge>(0);
		}
		return _nsrFixedChargeList;
	}

	public final void setNSRFixedChargeList(
			List<NSRFixedCharge> cpList) {
		_nsrFixedChargeList = cpList;
		if (_nsrFixedChargeList == null) {
			_nsrFixedChargeList = new ArrayList<NSRFixedCharge>(0);
		}
	}
	
	public final List<TimeSheetRow> getNSRTimeSheetRowList() {

		if (_nsrTimeSheetRowList == null) {
			_nsrTimeSheetRowList = new ArrayList<TimeSheetRow>(0);
		}
		return _nsrTimeSheetRowList;
	}

	public final void setNSRTimeSheetRowList(List<TimeSheetRow> tcList) {
		_nsrTimeSheetRowList = tcList;
		if (_nsrTimeSheetRowList == null) {
			_nsrTimeSheetRowList = new ArrayList<TimeSheetRow>(0);
		}
	}
	
	public final List<PermitChargePayment> getPermitChargePaymentList() {

		if (_permitChargePaymentList == null) {
			_permitChargePaymentList = new ArrayList<PermitChargePayment>(0);
		}
		return _permitChargePaymentList;
	}

	public final void setPermitChargePaymentList(
			List<PermitChargePayment> cpList) {
		_permitChargePaymentList = cpList;
		if (_permitChargePaymentList == null) {
			_permitChargePaymentList = new ArrayList<PermitChargePayment>(0);
		}
	}
	
	public final boolean isFeeNullOrEmpty() {
		
		if (initialInvoice != null && !Utility.isNullOrEmpty(initialInvoice.toString()) && initialInvoice != 0.0) {
			return false;
		}
		
		if (finalInvoice != null && !Utility.isNullOrEmpty(finalInvoice.toString()) && finalInvoice != 0.0) {
			return false;
		}
		
		return true;
	}
	
	public Double getInitialInvoiceAmount() {
		Double initialInvoiceAmount = null;
		for(PermitChargePayment pcp : getPermitChargePaymentList()) {
			if(pcp.getTransactionType().equalsIgnoreCase(NSRBillingChargePaymentTypeDef.INITIAL_INVOICE)) {
				initialInvoiceAmount = pcp.getTransactionAmount();
				break;
			}
		}
		
		return initialInvoiceAmount;
	}
	
	public Double getFinalInvoiceAmount() {
		Double finalInvoiceAmount = null;
		for(PermitChargePayment pcp : getPermitChargePaymentList()) {
			if(pcp.getTransactionType().equalsIgnoreCase(NSRBillingChargePaymentTypeDef.FINAL_INVOICE)) {
				finalInvoiceAmount = pcp.getTransactionAmount();
				break;
			}
		}
		
		return finalInvoiceAmount;
	}

	public final BigDecimal getCurrentTotal() {
		return currentTotal;
	}

	public final void setCurrentTotal(BigDecimal currentTotal) {
		this.currentTotal = currentTotal;
	}

	public boolean isTimeCardInfoRetrieved() {
		return timeCardInfoRetrieved;
	}

	public void setTimeCardInfoRetrieved(boolean timeCardInfoRetrieved) {
		this.timeCardInfoRetrieved = timeCardInfoRetrieved;
	}

	public String getCompanyPayKey() {
		return companyPayKey;
	}

	public void setCompanyPayKey(String companyPayKey) {
		this.companyPayKey = companyPayKey;
	}

	public Long getCompanyVendorNumber() {
		return companyVendorNumber;
	}

	public void setCompanyVendorNumber(Long companyVendorNumber) {
		this.companyVendorNumber = companyVendorNumber;
	}
	
	
	
	
}
