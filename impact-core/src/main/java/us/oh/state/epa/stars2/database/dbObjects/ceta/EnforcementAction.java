package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.EnforcementActionReferralTypeDef;
import us.oh.state.epa.stars2.def.EnforcementActionTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;

@SuppressWarnings("serial")
public class EnforcementAction extends BaseDB implements Comparable<EnforcementAction> {
	
	// STARS2 Attributes
	private Integer actionId;
	private Integer enforcementId;
	private List<Integer> evaluators;
	private String enfActionTypeCd;
	private Timestamp actionDate;
	private String finalCashAmount;
	private String finalSepAmount;
	private String enfDiscoveryTypeCd;
	private Integer enfDiscoveryId;

	private String afsMemo;
	private String afsId;
	private Timestamp afsSentDate;
	private Integer createdById;
	private Timestamp createdDt;
	private Integer facilityHistId;
	private String enfFormOfActionCd;
	private String enfViolationTypeCd;
	
	private transient String oldEnfDiscoveryTypeCd;
	private transient Integer oldEnfDiscoveryId;
    private transient String discoveryObjAfsId;  // used in AFS Export to the discovery object that has already been sent to AFS.
    private transient Timestamp discoveryObjDate;  // The date in the discovery object to send to AFS as the discovery date.
	
	private transient String orginalEnfActionTypeCd;
    private transient FacilityHistory facilityHistory;
	
	// IMPACT Attributes - Begin
	private Integer enforcementActionId;
	private String enfId;
	private String facilityId;
	private String facilityNm;
	private String companyName;
	private String companyId;
	private String countyCd;
	private String countyNm;
	private String districtCd;
	private Integer creatorId;
	
	private List<String> ReferralTypes = new ArrayList<String>();
	private String otherDescription;
	private String potentialViolationDescription;
	private String evidence;
	private String environmentalImpactDescription;
	private String enforcementActionType;
	private String enforcementActionTypeDsc;
	private String enforcementActionHPVCriterion;
	private String enforcementActionFRVType;
	private String docketNumber;
	private String memo;
	
	private String otherSARequirements;
	private String otherSARequirementsMet;
	private String evidenceAttached;
	private String environmentalImpact;
		
	private Timestamp createdDate;
	private Timestamp potentialViolationStartDate;
	private Timestamp potentialViolationEndDate;
	
	private boolean referralTypeOther;
	
	private List<EnforcementAttachment> attachments;
	private List<EnforcementNote> notes;
	
	private List<EnforcementActionEvent> _enforcementActionEventList;
	
	private boolean legacyFlag;
	
	private String sepFlag;
	private Double sepOffsetAmount;
	private Double penaltyAmount;
	
	private String dateNOVIssuedString;
	private String dateDayZeroString;
	private String dateReferredToAGString;
	private String dateFinalSettlementAgreementString;
	private String dateCheckReceivedString;
	private String dateNOVClosedString;
	
	private Date dateNOVIssued;
	private Date dateDayZero;
	private Date dateReferredToAG;
	private Date dateFinalSettlementAgreement;
	private Date dateCheckReceived;
	private Date dateNOVClosed;
	
	public EnforcementAction() {
		super();
	}
	
	public EnforcementAction(EnforcementAction old) {
		if(old != null) {
			setEnforcementActionId(old.getEnforcementActionId());
			setEnfId(old.getEnfId());
			setFacilityId(old.getFacilityId());
			setCreatorId(old.getCreatorId());
			setReferralTypes(old.getReferralTypes());
			setOtherDescription(old.getOtherDescription());
			setPotentialViolationDescription(old.getPotentialViolationDescription());
			setEvidence(old.getEvidence());
			setEnvironmentalImpactDescription(old.getEnvironmentalImpactDescription());
			setEnforcementActionType(old.getEnforcementActionType());
			setEnforcementActionTypeDsc(old.getEnforcementActionTypeDsc());
			setEnforcementActionHPVCriterion(old.getEnforcementActionHPVCriterion());
			setEnforcementActionFRVType(old.getEnforcementActionFRVType());
			setDocketNumber(old.getDocketNumber());
			setMemo(old.getMemo());
			setOtherSARequirements(old.getOtherSARequirements());
			setOtherSARequirementsMet(old.getOtherSARequirementsMet());
			setEvidenceAttached(old.getEvidenceAttached());
			setEnvironmentalImpact(old.getEnvironmentalImpact());
			setCreatedDate(old.getCreatedDate());
			setPotentialViolationStartDate(old.getPotentialViolationStartDate());
			setPotentialViolationEndDate(old.getPotentialViolationEndDate());
			setLegacyFlag(old.isLegacyFlag());
			setSepFlag(old.getSepFlag());
			setSepOffsetAmount(old.getSepOffsetAmount());
			setPenaltyAmount(old.getPenaltyAmount());
			setDateNOVIssuedString(old.getDateNOVIssuedString());
			setDateDayZeroString(old.getDateDayZeroString());
			setDateReferredToAGString(old.getDateReferredToAGString());
			setDateFinalSettlementAgreementString(old.getDateFinalSettlementAgreementString());
			setDateCheckReceivedString(old.getDateCheckReceivedString());
			setDateNOVClosedString(old.getDateNOVClosedString());
		}
	}

	public void populate(ResultSet rs) throws SQLException {
		try {
				setEnforcementActionId(AbstractDAO.getInteger(rs, "enforcement_action_id"));
				setEnfId(rs.getString("enf_id"));
				setFacilityId(rs.getString("facility_id"));
				setFacilityNm(rs.getString("facility_nm"));
				setCompanyName(rs.getString("company_nm"));
				setCompanyId(rs.getString("cmp_id"));
				setCreatorId(AbstractDAO.getInteger(rs, "creator_id"));
				setDistrictCd(rs.getString("district_cd"));
				setCountyCd(rs.getString("county_cd"));
				setCountyNm(rs.getString("county_nm"));
				
				setOtherDescription(rs.getString("other_description"));
				setPotentialViolationDescription(rs.getString("potential_violation_description"));
				setEvidence(rs.getString("evidence"));
				setEnvironmentalImpactDescription(rs.getString("environmental_impact_description"));
				setEnforcementActionType(rs.getString("enforcement_action_type"));
				try {
					setEnforcementActionTypeDsc(rs.getString("enforcement_action_type_dsc"));  // this value is not retrieved by all queries
				} catch (SQLException e) {
					;
				}
				setEnforcementActionHPVCriterion(rs.getString("enforcement_action_hpv_criterion"));
				setEnforcementActionFRVType(rs.getString("enforcement_action_frv_type"));
				setDocketNumber(rs.getString("docket_number"));
				setMemo(rs.getString("memo"));
				
				setCreatedDate(rs.getTimestamp("created_date"));
				setPotentialViolationStartDate(rs.getTimestamp("potential_violation_start_dt"));
				setPotentialViolationEndDate(rs.getTimestamp("potential_violation_end_dt"));
				
				setOtherSARequirements(rs.getString("other_sa_requirements"));
				setOtherSARequirementsMet(rs.getString("other_sa_requirements_met"));
				setEvidenceAttached(rs.getString("evidence_attached"));
				setEnvironmentalImpact(rs.getString("environmental_impact"));
				
				setLegacyFlag(AbstractDAO.translateIndicatorToBoolean(rs.getString("legacy_flag")));
				setSepFlag(rs.getString("sep_flag"));
				setSepOffsetAmount(AbstractDAO.getDouble(rs, "sep_offset_amt"));
				setPenaltyAmount(AbstractDAO.getDouble(rs, "penalty_amt"));
				
				setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
				
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        } finally {
    
        }
	}
	
	public int compareTo(EnforcementAction match) {
		int ret = -1;
		/*if (match != null) {
			if (match.getActionDate() != null && this.getActionDate() != null) {
				return match.getActionDate().compareTo(this.getActionDate());
			}
		}*/
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((enforcementActionId == null) ? 0 : enforcementActionId.hashCode());
		result = prime * result
				+ ((enfId == null) ? 0 : enfId.hashCode());
		result = prime * result
				+ ((facilityId == null) ? 0 : facilityId.hashCode());
		result = prime * result
				+ ((creatorId == null) ? 0 : creatorId.hashCode());
		result = prime * result
				+ ((ReferralTypes == null) ? 0 : ReferralTypes.hashCode());
		result = prime * result
				+ ((otherDescription == null) ? 0 : otherDescription.hashCode());
		result = prime * result
				+ ((potentialViolationDescription == null) ? 0 : potentialViolationDescription.hashCode());
		result = prime * result
				+ ((environmentalImpactDescription == null) ? 0 : environmentalImpactDescription.hashCode());
		result = prime * result
				+ ((evidence == null) ? 0 : evidence.hashCode());
		result = prime * result
				+ ((enforcementActionType == null) ? 0 : enforcementActionType.hashCode());
		result = prime * result
				+ ((enforcementActionTypeDsc == null) ? 0 : enforcementActionTypeDsc.hashCode());
		result = prime * result
				+ ((enforcementActionHPVCriterion == null) ? 0 : enforcementActionHPVCriterion.hashCode());
		result = prime * result
				+ ((enforcementActionFRVType == null) ? 0 : enforcementActionFRVType.hashCode());
		result = prime * result
				+ ((docketNumber == null) ? 0 : docketNumber.hashCode());
		result = prime * result
				+ ((memo == null) ? 0 : memo.hashCode());
		result = prime * result
				+ ((createdDate == null) ? 0 : createdDate.hashCode());
		result = prime * result
				+ ((potentialViolationStartDate == null) ? 0 : potentialViolationStartDate.hashCode());
		result = prime * result
				+ ((potentialViolationEndDate == null) ? 0 : potentialViolationEndDate.hashCode());
		result = prime * result
				+ ((sepOffsetAmount == null) ? 0 : sepOffsetAmount.hashCode());
		result = prime * result
				+ ((penaltyAmount == null) ? 0 : penaltyAmount.hashCode());
		result = prime * result
				+ ((dateNOVIssuedString == null) ? 0 : dateNOVIssuedString.hashCode());
		result = prime * result
				+ ((dateDayZeroString == null) ? 0 : dateDayZeroString.hashCode());
		result = prime * result
				+ ((dateReferredToAGString == null) ? 0 : dateReferredToAGString.hashCode());
		result = prime * result
				+ ((dateFinalSettlementAgreementString == null) ? 0 : dateFinalSettlementAgreementString.hashCode());
		result = prime * result
				+ ((dateCheckReceivedString == null) ? 0 : dateCheckReceivedString.hashCode());
		result = prime * result
				+ ((dateNOVClosedString == null) ? 0 : dateNOVClosedString.hashCode());
	
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
		EnforcementAction other = (EnforcementAction) obj;
		if (enforcementActionId == null) {
			if (other.enforcementActionId != null)
				return false;
		} else if (!enforcementActionId.equals(other.enforcementActionId))
			return false;
		
		if (enfId == null) {
			if (other.enfId != null)
				return false;
		} else if (!enfId.equals(other.enfId))
			return false;
		
		if (facilityId == null) {
			if (other.facilityId != null)
				return false;
		} else if (!facilityId.equals(other.facilityId))
			return false;
		
		if (creatorId == null) {
			if (other.creatorId != null)
				return false;
		} else if (!creatorId.equals(other.creatorId))
			return false;
		
		if (otherDescription == null) {
			if (other.otherDescription != null)
				return false;
		} else if (!otherDescription.equals(other.otherDescription))
			return false;
		
		if (potentialViolationDescription == null) {
			if (other.potentialViolationDescription != null)
				return false;
		} else if (!potentialViolationDescription.equals(other.potentialViolationDescription))
			return false;
		
		if (evidence == null) {
			if (other.evidence != null)
				return false;
		} else if (!evidence.equals(other.evidence))
			return false;
		
		if (environmentalImpactDescription == null) {
			if (other.environmentalImpactDescription != null)
				return false;
		} else if (!environmentalImpactDescription.equals(other.environmentalImpactDescription))
			return false;
		
		if (enforcementActionType == null) {
			if (other.enforcementActionType != null)
				return false;
		} else if (!enforcementActionType.equals(other.enforcementActionType))
			return false;
		
		if (enforcementActionTypeDsc == null) {
			if (other.enforcementActionTypeDsc != null)
				return false;
		} else if (!enforcementActionTypeDsc.equals(other.enforcementActionTypeDsc))
			return false;
		
		if (enforcementActionHPVCriterion == null) {
			if (other.enforcementActionHPVCriterion != null)
				return false;
		} else if (!enforcementActionHPVCriterion.equals(other.enforcementActionHPVCriterion))
			return false;
		
		if (enforcementActionFRVType == null) {
			if (other.enforcementActionFRVType != null)
				return false;
		} else if (!enforcementActionFRVType.equals(other.enforcementActionFRVType))
			return false;
		
		if (docketNumber == null) {
			if (other.docketNumber != null)
				return false;
		} else if (!docketNumber.equals(other.docketNumber))
			return false;
		
		if (memo == null) {
			if (other.memo != null)
				return false;
		} else if (!memo.equals(other.memo))
			return false;
		
		if (createdDate == null) {
			if (other.createdDate != null)
				return false;
		} else if (!createdDate.equals(other.createdDate))
			return false;
		
		if (potentialViolationStartDate == null) {
			if (other.potentialViolationStartDate != null)
				return false;
		} else if (!potentialViolationStartDate.equals(other.potentialViolationStartDate))
			return false;
		
		if (potentialViolationEndDate == null) {
			if (other.potentialViolationEndDate != null)
				return false;
		} else if (!potentialViolationEndDate.equals(other.potentialViolationEndDate))
			return false;
		
		if (sepOffsetAmount == null) {
			if (other.sepOffsetAmount != null)
				return false;
		} else if (!sepOffsetAmount.equals(other.sepOffsetAmount))
			return false;
		
		if (penaltyAmount == null) {
			if (other.penaltyAmount != null)
				return false;
		} else if (!penaltyAmount.equals(other.penaltyAmount))
			return false;
		
		if (dateNOVIssuedString == null) {
			if (other.dateNOVIssuedString != null)
				return false;
		} else if (!dateNOVIssuedString.equals(other.dateNOVIssuedString))
			return false;
			
		if (dateDayZeroString == null) {
			if (other.dateDayZeroString != null)
				return false;
		} else if (!dateDayZeroString.equals(other.dateDayZeroString))
			return false;
		
		if (dateReferredToAGString == null) {
			if (other.dateReferredToAGString != null)
				return false;
		} else if (!dateReferredToAGString.equals(other.dateReferredToAGString))
			return false;
		
		if (dateFinalSettlementAgreementString == null) {
			if (other.dateFinalSettlementAgreementString != null)
				return false;
		} else if (!dateFinalSettlementAgreementString.equals(other.dateFinalSettlementAgreementString))
			return false;

		if (dateCheckReceivedString == null) {
			if (other.dateCheckReceivedString != null)
				return false;
		} else if (!dateCheckReceivedString.equals(other.dateCheckReceivedString))
			return false;
		
		if (dateNOVClosedString == null) {
			if (other.dateNOVClosedString != null)
				return false;
		} else if (!dateNOVClosedString.equals(other.dateNOVClosedString))
			return false;

		return true;
	}
	
	public final ValidationMessage[] validate() {

		validationMessages.clear();
		
		if (Utility.isNullOrEmpty(this.getPotentialViolationDescription())) {
			ValidationMessage valMsg = new ValidationMessage("potentialViolationDescription",
					"Description of Potential Violations and Relevant Citations must be entered.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("potentialViolationDescription", valMsg);
		} 
		
		if (!this.legacyFlag){
			if (potentialViolationStartDate == null
					&& potentialViolationEndDate != null) {
				ValidationMessage valMsg = new ValidationMessage(
						"potentialViolationStartDate",
						"The \"Potential Violation Start Date\" must have a value since a value has been entered for \"Potential Violation End Date\"",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("potentialViolationStartDate", valMsg);
			}
	
			if (potentialViolationStartDate != null
					&& potentialViolationEndDate != null
					&& potentialViolationEndDate
							.before(potentialViolationStartDate)) {
				ValidationMessage valMsg = new ValidationMessage(
						"potentialViolationEndDate",
						"The \"Potential Violation End Date\" cannot be before \"Potential Violation Start Date\"",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("potentialViolationEndDate", valMsg);
			}
			
			if ((!Utility.isNullOrEmpty(this
					.getEnvironmentalImpact()) && this
					.getEnvironmentalImpact().equals("Y"))
					&& Utility.isNullOrEmpty(this
							.getEnvironmentalImpactDescription())) {
	
				ValidationMessage valMsg = new ValidationMessage(
						"environmentalImpactDescription",
						"Since Environmental Impact is Yes, a value must be entered for Environmental Impact Description.",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("environmentalImpactDescription", valMsg);
			}
			
			if (!Utility.isNullOrEmpty(this.getEnforcementActionType())
					&& EnforcementActionTypeDef.needHPVCriterion(
							this.getEnforcementActionType(), logger)
					&& Utility.isNullOrEmpty(this
							.getEnforcementActionHPVCriterion())) {
				ValidationMessage valMsg = new ValidationMessage(
						"enforcementActionHPVCriterion",
						"Since the selected Enforcement Action Type requires an HPV Criterion, an HPV Criterion must be selected.",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("otherDescription", valMsg);
			}
			
			if (this.isReferralTypeOther() && Utility.isNullOrEmpty(this.getOtherDescription())) {
				ValidationMessage valMsg = new ValidationMessage("otherDescription",
						"Since Referral Type Other has been selected, Description of Other must be entered.", ValidationMessage.Severity.ERROR);
				this.validationMessages.put("otherDescription", valMsg);
			} 
			if (!Utility.isNullOrEmpty(this.getDocketNumber()) && !validateDocketNumberFormat(this.getDocketNumber())) {
				ValidationMessage valMsg = new ValidationMessage("docketNumber",
						"Docket Number must be of the format XXXX-XX, where each X is a number.",
						ValidationMessage.Severity.ERROR);
				this.validationMessages.put("docketNumber", valMsg);
			}
			
			// SEP Offset Amount validations
			if (!Utility.isNullOrEmpty(this.getSepFlag())) {
				if (this.getSepFlag().equals("Y")
						&& (this.getSepOffsetAmount() == null || Utility
								.isNullOrEmpty(this.getSepOffsetAmount().toString()))) {
					ValidationMessage valMsg = new ValidationMessage(
							"sepOffsetAmount",
							"Since SEP is Yes, a non-zero value must be entered for SEP Offset Amount.",
							ValidationMessage.Severity.ERROR);
					this.validationMessages.put("sepOffsetAmount", valMsg);
					return new ArrayList<ValidationMessage>(
							validationMessages.values())
							.toArray(new ValidationMessage[0]);
				}
			}
	
			if (!Utility.isNullOrEmpty(this.getSepFlag())) {
				if (this.getSepFlag().equals("Y")
						&& this.getSepOffsetAmount() != null) {
					Double d1 = this.getSepOffsetAmount();
					Double d2 = 9999999.99;
					int i1 = Double.compare(d1, d2);
					if (i1 > 0) {
						ValidationMessage valMsg = new ValidationMessage(
								"sepOffsetAmount",
								"SEP Offset Amount cannot be greater than $9,999,999.99.",
								ValidationMessage.Severity.ERROR);
						this.validationMessages.put("sepOffsetAmount", valMsg);
					}
	
					Double d3 = 0d;
					Double d4 = this.getSepOffsetAmount();
					int i2 = Double.compare(d3, d4);
					if (i2 >= 0) {
						ValidationMessage valMsg = new ValidationMessage(
								"sepOffsetAmount",
								"SEP Offset Amount must be greater than zero.",
								ValidationMessage.Severity.ERROR);
						this.validationMessages.put("sepOffsetAmount", valMsg);
					}
				}
			}
	
			if (this.getPenaltyAmount() != null) {
				Double d10 = this.getPenaltyAmount();
				Double d20 = 9999999.99;
				int i10 = Double.compare(d10, d20);
				if (i10 > 0) {
					ValidationMessage valMsg = new ValidationMessage(
							"penaltyAmount",
							"Penalty Amount cannot be greater than $9,999,999.99.",
							ValidationMessage.Severity.ERROR);
					this.validationMessages.put("penaltyAmount", valMsg);
				}
	
				Double d30 = 0d;
				Double d40 = this.getPenaltyAmount();
				int i20 = Double.compare(d30, d40);
				if (i20 > 0) {
					ValidationMessage valMsg = new ValidationMessage(
							"penaltyAmount",
							"Penalty Amount must be greater than or equal to zero.",
							ValidationMessage.Severity.ERROR);
					this.validationMessages.put("penaltyAmount", valMsg);
				}
			}
		}
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}

	public Integer getEnforcementActionId() {
		return enforcementActionId;
	}

	public String getEnfId() {
		return enfId;
	}

	public void setEnfId(String enfId) {
		this.enfId = enfId;
	}

	public void setEnforcementActionId(Integer enforcementActionId) {
		this.enforcementActionId = enforcementActionId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public Integer getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Integer creatorId) {
		this.creatorId = creatorId;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public Timestamp getPotentialViolationStartDate() {
		return potentialViolationStartDate;
	}

	public void setPotentialViolationStartDate(Timestamp potentialViolationStartDate) {
		this.potentialViolationStartDate = potentialViolationStartDate;
	}

	public Timestamp getPotentialViolationEndDate() {
		return potentialViolationEndDate;
	}

	public void setPotentialViolationEndDate(Timestamp potentialViolationEndDate) {
		this.potentialViolationEndDate = potentialViolationEndDate;
	}
	
	public List<String> getReferralTypes() {
		return ReferralTypes;
	}

	public void setReferralTypes(List<String> referralTypes) {
		ReferralTypes = referralTypes;
		if (ReferralTypes == null){
			ReferralTypes = new ArrayList<String>();
		}
		if(referralTypes !=null && referralTypes.contains(EnforcementActionReferralTypeDef.OTHER)) {
			setReferralTypeOther(true);
		} else {
			setReferralTypeOther(false);
		}
	}

	public String getOtherDescription() {
		return otherDescription;
	}

	public void setOtherDescription(String otherDescription) {
		this.otherDescription = otherDescription;
	}

	public String getPotentialViolationDescription() {
		return potentialViolationDescription;
	}

	public void setPotentialViolationDescription(
			String potentialViolationDescription) {
		this.potentialViolationDescription = potentialViolationDescription;
	}

	public String getEvidence() {
		return evidence;
	}

	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}

	public String getEnvironmentalImpactDescription() {
		return environmentalImpactDescription;
	}

	public void setEnvironmentalImpactDescription(
			String environmentalImpactDescription) {
		this.environmentalImpactDescription = environmentalImpactDescription;
	}

	public String getEnforcementActionType() {
		return enforcementActionType;
	}

	public void setEnforcementActionType(String enforcementActionType) {
		this.enforcementActionType = enforcementActionType;
	}
	
	public String getEnforcementActionTypeDsc() {
		return enforcementActionTypeDsc;
	}

	public void setEnforcementActionTypeDsc(String enforcementActionTypeDsc) {
		this.enforcementActionTypeDsc = enforcementActionTypeDsc;
	}

	public String getEnforcementActionHPVCriterion() {
		return enforcementActionHPVCriterion;
	}

	public void setEnforcementActionHPVCriterion(
			String enforcementActionHPVCriterion) {
		this.enforcementActionHPVCriterion = enforcementActionHPVCriterion;
	}

	public String getEnforcementActionFRVType() {
		return enforcementActionFRVType;
	}

	public void setEnforcementActionFRVType(String enforcementActionFRVType) {
		this.enforcementActionFRVType = enforcementActionFRVType;
	}

	public String getDocketNumber() {
		return docketNumber;
	}

	public void setDocketNumber(String docketNumber) {
		this.docketNumber = docketNumber;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getOtherSARequirements() {
		return otherSARequirements;
	}

	public void setOtherSARequirements(String otherSARequirements) {
		this.otherSARequirements = otherSARequirements;
	}
	
	public String getOtherSARequirementsMet() {
		return otherSARequirementsMet;
	}

	public void setOtherSARequirementsMet(String otherSARequirementsMet) {
		this.otherSARequirementsMet = otherSARequirementsMet;
	}
	

	public String getEvidenceAttached() {
		return evidenceAttached;
	}

	public void setEvidenceAttached(String evidenceAttached) {
		this.evidenceAttached = evidenceAttached;
	}

	public String getEnvironmentalImpact() {
		return environmentalImpact;
	}

	public void setEnvironmentalImpact(String environmentalImpact) {
		this.environmentalImpact = environmentalImpact;
	}

	public boolean isReferralTypeOther() {
		return referralTypeOther;
	}

	public void setReferralTypeOther(boolean referralTypeOther) {
		this.referralTypeOther = referralTypeOther;
	}
	
	public boolean isLegacyFlag() {
		return legacyFlag;
	}

	public void setLegacyFlag(boolean legacyFlag) {
		this.legacyFlag = legacyFlag;
	}
	
	public String getSepFlag() {
		return sepFlag;
	}

	public void setSepFlag(String sepFlag) {
		this.sepFlag = sepFlag;
	}

	public Double getSepOffsetAmount() {
		return sepOffsetAmount;
	}

	public void setSepOffsetAmount(Double sepOffsetAmount) {
		this.sepOffsetAmount = sepOffsetAmount;
	}

	public Double getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Double penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}
	
	public String getDateNOVIssuedString() {
		return dateNOVIssuedString;
	}

	public void setDateNOVIssuedString(String dateNOVIssuedString) {
		this.dateNOVIssuedString = dateNOVIssuedString;
	}
	
	public String getDateDayZeroString() {
		return dateDayZeroString;
	}

	public void setDateDayZeroString(String dateDayZeroString) {
		this.dateDayZeroString = dateDayZeroString;
	}
	
	public String getDateReferredToAGString() {
		return dateReferredToAGString;
	}

	public void setDateReferredToAGString(String dateReferredToAGString) {
		this.dateReferredToAGString = dateReferredToAGString;
	}
	
	public String getDateFinalSettlementAgreementString() {
		return dateFinalSettlementAgreementString;
	}

	public void setDateFinalSettlementAgreementString(
			String dateFinalSettlementAgreementString) {
		this.dateFinalSettlementAgreementString = dateFinalSettlementAgreementString;
	}

	public String getDateCheckReceivedString() {
		return dateCheckReceivedString;
	}

	public void setDateCheckReceivedString(String dateCheckReceivedString) {
		this.dateCheckReceivedString = dateCheckReceivedString;
	}

	public String getDateNOVClosedString() {
		return dateNOVClosedString;
	}

	public void setDateNOVClosedString(String dateNOVClosedString) {
		this.dateNOVClosedString = dateNOVClosedString;
	}

	// END IMPACT CHANGES
	public final Integer getEnforcementId() {
		return enforcementId;
	}

	public final void setEnforcementId(Integer enforcementId) {
		this.enforcementId = enforcementId;
	}

	public final Integer getActionId() {
		return actionId;
	}

	public final void setActionId(Integer actionId) {
		this.actionId = actionId;
	}

	public final List<Integer> getEvaluators() {
		if (evaluators == null) {
			evaluators = new ArrayList<Integer>();
		}
		return evaluators;
	}

	public final void setEvaluators(List<Integer> evaluators) {
		this.evaluators = new ArrayList<Integer>();
		if (evaluators != null) {
			this.evaluators.addAll(evaluators);
		}
	}
	
	public final void addEvaluator(Integer evaluator) {
		if (evaluators == null) {
			evaluators = new ArrayList<Integer>();
		}
		if (evaluator != null) {
			evaluators.add(evaluator);
		}
	}

	public final String getEnfActionTypeCd() {
		return enfActionTypeCd;
	}

	public final void setEnfActionTypeCd(String actionTypeCd) {
		this.enfActionTypeCd = actionTypeCd;
	}

	public final Timestamp getActionDate() {
		return actionDate;
	}

	public final void setActionDate(Timestamp actionDate) {
		this.actionDate = actionDate;
	}

	public final String getFinalCashAmount() {
		return finalCashAmount;
	}

	public final void setFinalCashAmount(String finalCashAmount) {
		this.finalCashAmount = finalCashAmount;
	}

	public final String getFinalSepAmount() {
		return finalSepAmount;
	}

	public final void setFinalSepAmount(String finalSepAmount) {
		this.finalSepAmount = finalSepAmount;
	}

	public final String getAfsMemo() {
		return afsMemo;
	}

	public final void setAfsMemo(String afsMemo) {
		this.afsMemo = afsMemo;
	}

	public final String getAfsId() {
		return afsId;
	}

	public final void setAfsId(String afsId) {
		this.afsId = afsId;
	}

	public final Timestamp getAfsSentDate() {
		return afsSentDate;
	}

	public final void setAfsSentDate(Timestamp afsSentDate) {
		this.afsSentDate = afsSentDate;
	}

	public final Integer getCreatedById() {
		return createdById;
	}

	public final void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}

	public final Timestamp getCreatedDt() {
		return createdDt;
	}

	public final void setCreatedDt(Timestamp createdDt) {
		this.createdDt = createdDt;
	}

	public final Integer getFacilityHistId() {
		return facilityHistId;
	}

	public final void setFacilityHistId(Integer facilityHistId) {
		this.facilityHistId = facilityHistId;
	}
    
    // these methods are used to facilitate sorting in the search table
    //public final boolean isAddToHPVList() {
    //	boolean isAddToHPVList = false;
    //	if (enforcement != null) {
    //		isAddToHPVList = enforcement.isAddToHPVList();
    //	}
    //	return isAddToHPVList;
    //}
    
	public final String getFormOfActionCd() {
		return enfFormOfActionCd;
	}
    
	public final String getFacilityNm() {
		return facilityNm;
	}
	
	public final void setFacilityNm(String facilityNm) {
		this.facilityNm = facilityNm;
	}
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public String getCountyCd() {
		return countyCd;
	}

	public void setCountyCd(String countyCd) {
		this.countyCd = countyCd;
	}
	
	public String getCountyNm() {
		return countyNm;
	}

	public void setCountyNm(String countyNm) {
		this.countyNm = countyNm;
	}

	public String getDistrictCd() {
		return districtCd;
	}

	public void setDistrictCd(String districtCd) {
		this.districtCd = districtCd;
	}
    

	public final String getEnfFormOfActionCd() {
		return enfFormOfActionCd;
	}

	public final void setEnfFormOfActionCd(String enfFormOfActionCd) {
		this.enfFormOfActionCd = enfFormOfActionCd;
	}

	public final String getOrginalEnfActionTypeCd() {
		return orginalEnfActionTypeCd;
	}

	public final void setOrginalEnfActionTypeCd(String orginalEnfActionTypeCd) {
		this.orginalEnfActionTypeCd = orginalEnfActionTypeCd;
	}

	public final String getEnfViolationTypeCd() {
		return enfViolationTypeCd;
	}

	public final void setEnfViolationTypeCd(String enfViolationTypeCd) {
		this.enfViolationTypeCd = enfViolationTypeCd;
	}
	
	public final String getEnfDiscoveryTypeCd() {
		return enfDiscoveryTypeCd;
	}

	public final void setEnfDiscoveryTypeCd(String enfDiscoveryTypeCd) {
        // If the type changes then null out the Id.
        if(enfDiscoveryTypeCd == null) enfDiscoveryId = null;
        else if(!enfDiscoveryTypeCd.equals(this.enfDiscoveryTypeCd)) enfDiscoveryId = null;
		this.enfDiscoveryTypeCd = enfDiscoveryTypeCd;
	}
	
	public final Integer getEnfDiscoveryId() {
		return enfDiscoveryId;
	}

	public final void setEnfDiscoveryId(Integer enfDiscoveryId) {
		this.enfDiscoveryId = enfDiscoveryId;
	}
	
	public final String getOldEnfDiscoveryTypeCd() {
		return oldEnfDiscoveryTypeCd;
	}

	public final void setOldEnfDiscoveryTypeCd(String oldEnfDiscoveryTypeCd) {
		this.oldEnfDiscoveryTypeCd = oldEnfDiscoveryTypeCd;
	}

	public final Integer getOldEnfDiscoveryId() {
		return oldEnfDiscoveryId;
	}

	public final void setOldEnfDiscoveryId(Integer oldEnfDiscoveryId) {
		this.oldEnfDiscoveryId = oldEnfDiscoveryId;
	}

	// TODO will probably want to get rid of this once testing is done
	public void copy(EnforcementAction action) {
		this.enforcementId = action.enforcementId;
		this.actionId = action.actionId;
		setEvaluators(evaluators);
		this.enfActionTypeCd = action.enfActionTypeCd;
		this.actionDate = action.actionDate;
		this.finalCashAmount = action.finalCashAmount;
		this.finalSepAmount = action.finalSepAmount;
		this.afsMemo = action.afsMemo;
		this.afsId = action.afsId;
		this.afsSentDate = action.afsSentDate;
		this.createdById = action.createdById;
		this.createdDt = action.createdDt;
		this.enfFormOfActionCd = action.enfFormOfActionCd;
		this.enfViolationTypeCd = action.enfViolationTypeCd;
	}
	
    public FacilityHistory getFacilityHistory() {
        return facilityHistory;
    }

    public void setFacilityHistory(FacilityHistory facilityHistory) {
        this.facilityHistory = facilityHistory;
    }
	
    //public String getEnfCaseState() {
    //	return enforcement.getEnfCaseState();
    //}
    
    //public String getAddToHPVList() {
    //	return enforcement.isAddToHPVList() ? "Yes" : "No";
    //}

    public String getDiscoveryObjAfsId() {
        return discoveryObjAfsId;
    }

    public void setDiscoveryObjAfsId(String discoveryObjAfsId) {
        this.discoveryObjAfsId = discoveryObjAfsId;
    }
    
    //public Integer getFpId() {
    //	return enforcement.getFpId();
    //}

    public Timestamp getDiscoveryObjDate() {
        return discoveryObjDate;
    }

    public void setDiscoveryObjDate(Timestamp discoveryObjDate) {
        this.discoveryObjDate = discoveryObjDate;
    }
    
    public final List<EnforcementAttachment> getAttachments() {
		if (attachments == null) {
			attachments = new ArrayList<EnforcementAttachment>();
		}
		return attachments;
	}

	public final void setAttachments(List<EnforcementAttachment> attachments) {
		this.attachments = new ArrayList<EnforcementAttachment>();
		if (attachments != null) {
			this.attachments.addAll(attachments);
		}
	}
	
	public final void addAttachment(EnforcementAttachment a) {
		if (attachments == null) {
			attachments = new ArrayList<EnforcementAttachment>();
		}
		if (a != null) {
			attachments.add(a);
		}
	}
	
	public final boolean hasAttachments() {
        boolean rtn = false;
        if (attachments != null && attachments.size() > 0) {
        	rtn = true;
        }
        return rtn;
    }

	public final List<EnforcementNote> getNotes() {
		if (notes == null) {
			notes = new ArrayList<EnforcementNote>();
		}
		return notes;
	}

	public final void setNotes(List<EnforcementNote> notes) {
		this.notes = new ArrayList<EnforcementNote>();
		if (notes != null) {
			this.notes.addAll(notes);
		}
	}
	
	public final void addNote(EnforcementNote a) {
		if (notes == null) {
			notes = new ArrayList<EnforcementNote>();
		}
		if (a != null) {
			notes.add(a);
		}
	}
	
	private boolean validateDocketNumberFormat(String docketNumber) {
		// regex: (\d{4})-(\d{2})
		return null == docketNumber? false : docketNumber.matches("(\\d{4})-(\\d{2})");
	}
	
	public final List<EnforcementActionEvent> getEnforcementActionEventList() {

		if (_enforcementActionEventList == null) {
			_enforcementActionEventList = new ArrayList<EnforcementActionEvent>(0);
		}
		return _enforcementActionEventList;
	}

	public final void setEnforcementActionEventList(
			List<EnforcementActionEvent> cpList) {
		_enforcementActionEventList = cpList;
		if (_enforcementActionEventList == null) {
			_enforcementActionEventList = new ArrayList<EnforcementActionEvent>(0);
		}
	}
	
	public String getSepOffsetAmountString() {
		// format the amount as USD
		Locale locale = new Locale("en", "US");
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		String stringAmount;
		if (getSepOffsetAmount() == null) {
			stringAmount = "";
		} else {
			stringAmount = nf.format(getSepOffsetAmount());
		}
		
		return stringAmount;
	}
	
	public String getPenaltyAmountString() {
		// format the amount as USD
		Locale locale = new Locale("en", "US");
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		String stringAmount;
		if (getPenaltyAmount() == null) {
			stringAmount = "";
		} else {
			stringAmount = nf.format(getPenaltyAmount());
		}
		
		return stringAmount;
	}
	
	// Used to sort the columns in the Search grid as a Date instead of String.
	public Date convertStringToDate(String str) {
		Date date = new Date();
		
		if (str != null) {
			DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			
			try {
				date = formatter.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			date = null;
		}
		
		return date;
	}
	
	public Date getDateNOVIssued() {
		dateNOVIssued = convertStringToDate(dateNOVIssuedString);
		return dateNOVIssued;
	}
	
	public void setDateNOVIssued(Date dateNOVIssued) {
		this.dateNOVIssued = dateNOVIssued;
	}
	
	public Date getDateDayZero() {
		dateDayZero = convertStringToDate(dateDayZeroString);
		return dateDayZero;
	}
	
	public void setDateDayZero(Date dateDayZero) {
		this.dateDayZero = dateDayZero;
	}
	
	public Date getDateReferredToAG() {
		dateReferredToAG = convertStringToDate(dateReferredToAGString);
		return dateReferredToAG;
	}
	
	public void setDateReferredToAG(Date dateReferredToAG) {
		this.dateReferredToAG = dateReferredToAG;
	}
	
	public Date getDateFinalSettlementAgreement() {
		dateFinalSettlementAgreement = convertStringToDate(dateFinalSettlementAgreementString);
		return dateFinalSettlementAgreement;
	}
	
	public void setDateFinalSettlementAgreement(Date dateFinalSettlementAgreement) {
		this.dateFinalSettlementAgreement = dateFinalSettlementAgreement;
	}
	
	public Date getDateCheckReceived() {
		dateCheckReceived = convertStringToDate(dateCheckReceivedString);
		return dateCheckReceived;
	}
	
	public void setDateCheckReceived(Date dateCheckReceived) {
		this.dateCheckReceived = dateCheckReceived;
	}
	
	public Date getDateNOVClosed() {
		dateNOVClosed = convertStringToDate(dateNOVClosedString);
		return dateNOVClosed;
	}
	
	public void setDateNOVClosed(Date dateNOVClosed) {
		this.dateNOVClosed = dateNOVClosed;
	}
	
}
