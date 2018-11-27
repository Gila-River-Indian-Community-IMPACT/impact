package us.oh.state.epa.stars2.database.dbObjects.serviceCatalog;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.app.tools.SpatialData;
import us.oh.state.epa.stars2.app.tools.SpatialData.SpatialDataLineItem;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.StringContainer;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.def.ContentTypeDef;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.ExemptStatusDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.ShapeDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.FacesUtil;

public class SCEmissionsReport extends BaseDB {
	
	private static final long serialVersionUID = 8702934836271125870L;

	public static final String SPATIAL_DATA_OUTCOME = "tools.spatialData";
	
    private Integer id;
    private Integer workFlowTemplateId;
    private String contentTypeCd;
    private String regulatoryRequirementCd;
    private String reportName;
    private String reportGroup;
    private String feeType;
    private Integer reportingYear;
    private transient Timestamp effectiveDate;
    private Integer referenceYear;
    private Integer reportFrequencyYear;
    private List<Fee> fees = new ArrayList<Fee>(0);
    private List<SCPollutant> pollutants = new ArrayList<SCPollutant>(0);
    private List<SCNonChargePollutant> ncPollutants = new ArrayList<SCNonChargePollutant>(0);
    private List<StringContainer> exemptions = new ArrayList<StringContainer>(0);
    private List<StringContainer> tvClassifications = new ArrayList<StringContainer>(0);
    
    private Fee feeFirstHalf = new Fee();
    private Fee feeSecondHalf = new Fee();
    private double pollutantCap;
    private BigDecimal eiMinimumFee;
    
    private Integer shapeId; // reference to the project location on the map
    // Facility selection criteria - 
    private List<String> permitClassCds = new ArrayList<String>(); // facility class
    private List<String> facilityTypeCds = new ArrayList<String>(); // facility type
    
    private String treatPartialAsFullPeriodFlag;
    
    private ContentTypeDef contentTypeDef;
    
    public static int FEE_MAX_SCALE = 2;
    
    private List<SCDataImportPollutant> dataImportPollutantList = new ArrayList<SCDataImportPollutant>();

    public SCEmissionsReport() {
        super();
    }

    public SCEmissionsReport(SCEmissionsReport old) {
        super(old);

        if (old != null) {
            setId(old.getId());
            setWorkFlowTemplateId(old.getWorkFlowTemplateId());
            setContentTypeCd(old.getContentTypeCd());
            setRegulatoryRequirementCd(old.getRegulatoryRequirementCd());
            setReportName(old.getReportName());
            setReportGroup(old.getReportGroup());
            setReportingYear(old.getReportingYear());
            setFeeType(old.getFeeType());
            setEffectiveDate(old.getEffectiveDate());
            setReferenceYear(old.getReferenceYear());
            setReportFrequencyYear(old.getReportFrequencyYear());

            int i = 0;
            for (Fee fee : old.fees) {
                this.fees.add(new Fee(fee));
                if (i == 0) {
                	this.setFeeFirstHalf(fee);
                } else if (i == 1) {
                	this.setFeeSecondHalf(fee);
                }
                i++;
            }

            for (SCPollutant pollutant : old.pollutants) {
                this.pollutants.add(new SCPollutant(pollutant));
            }
            for (SCNonChargePollutant ncPollutant : old.ncPollutants) {
                this.ncPollutants.add(new SCNonChargePollutant(ncPollutant));
            }
            for (SCDataImportPollutant dataImportPollutant : old.dataImportPollutantList) {
                this.dataImportPollutantList.add(new SCDataImportPollutant(dataImportPollutant));
            }
            for (StringContainer ex : old.exemptions) {
                this.exemptions.add(new StringContainer(ex.getStr()));
            }
            for (StringContainer tvC : old.tvClassifications) {
                this.tvClassifications.add(new StringContainer(tvC.getStr()));
            }
            
            setPollutantCap(old.getPollutantCap());
            setEiMinimumFee(old.getEiMinimumFee());
            setShapeId(old.getShapeId());
            
            List<String> newPermitClassCds = new ArrayList<String>(); 
            for(String p:old.getPermitClassCds())
            {   
            	newPermitClassCds.add(p);
            }
            setPermitClassCds(newPermitClassCds);
            
            List<String> newFacilityTypeCds = new ArrayList<String>();
            for(String f:old.getFacilityTypeCds())
            {   
            	newFacilityTypeCds.add(f);
            }
            setFacilityTypeCds(newFacilityTypeCds);

            setTreatPartialAsFullPeriodFlag(old.getTreatPartialAsFullPeriodFlag());
            
            setContentTypeDef(ContentTypeDef.getContentTypeDef(getContentTypeCd()));
            
			this.dataImportPollutantList = new ArrayList<SCDataImportPollutant>(old.getDataImportPollutantList());
        }
    }
    
    public final SCPollutant findPollutant(String pollutantCd) {
        for(SCPollutant p : pollutants) {
            if (pollutantCd.equals(p.getPollutantCd())) return p;
        }
        return null;
    }
    
    public final SCDataImportPollutant findDIPollutant(String pollutantCd) {
    	for (SCDataImportPollutant p : dataImportPollutantList) {
            if (pollutantCd.equals(p.getPollutantCd())) return p;    		
    	}
    	return null;
    }
    
    public final SCNonChargePollutant findHAP(String pollutantCd) {
        for(SCNonChargePollutant ncPollutant : ncPollutants) {
            if (pollutantCd.equals(ncPollutant.getPollutantCd())) return ncPollutant;
        }
        return null;
    }

    public final Fee getFee(Integer feeId) {
        return fees.get(feeId);
    }
    
    public final Fee locateFee(Integer feeId) {
        Fee rtn = null;
        for(Fee e : fees) {
            if(feeId.equals(e.getFeeId())) {
                rtn = e;
            }
        }
        return rtn;
    }
    
    // Locate FeeId which has the highest low value
    // that is still is less than or equal to amount
    public final Integer locateFeeId(int amount) {
        Integer rtn = null;
        int lowRangeValue = 0;
        for(Fee e : fees) {
            if(e.getLowRange().intValue() <= amount) {
                if(rtn == null) {
                    rtn = e.getFeeId();
                    lowRangeValue = e.getLowRange().intValue();
                } else {
                    // Handle case of 0-0 and 0-n
                    if(e.getLowRange().intValue() == lowRangeValue && e.getLowRange() != null) {
                        rtn = e.getFeeId();
                        lowRangeValue = e.getLowRange().intValue();
                    } else if(e.getLowRange().intValue() > lowRangeValue) {
                        rtn = e.getFeeId();
                        lowRangeValue = e.getLowRange().intValue();
                    }
                }
            }
        }
        return rtn;
    }

    public final Fee[] getFees() {
    	fees = null;
    	addFee(feeFirstHalf);
    	addFee(feeSecondHalf);
        return fees.toArray(new Fee[0]);
    }
    
    public StringContainer[] getExemptions() {
        return exemptions.toArray(new StringContainer[0]);
    }
    

    public StringContainer[] getTvClassifications() {
        return tvClassifications.toArray(new StringContainer[0]);
    }

    public final void addFee(Fee fee) {
        if (fee != null) {
            if (fees == null) {
                fees = new ArrayList<Fee>(1);
            }

            this.fees.add(fee);
        }
    }
    
    public final void addEx(StringContainer ex) {
        if (ex != null) {
            if (exemptions == null) {
                exemptions = new ArrayList<StringContainer>(1);
            }

            this.exemptions.add(ex);
        }
    }
    
    public final void addTvClass(StringContainer tvClass) {
        if (tvClass != null) {
            if (tvClassifications == null) {
                tvClassifications = new ArrayList<StringContainer>(1);
            }

            this.tvClassifications.add(tvClass);
        }
    }

    public final void setFees(Fee[] fees) {
        this.fees = new ArrayList<Fee>(fees.length);

        int i = 0;
        if (fees != null) {
            for (Fee fee : fees) {
                this.fees.add(fee);
                if (i == 0) {
                	setFeeFirstHalf(fee);
                } else if (i == 1) {
                	setFeeSecondHalf(fee);
                }
                i++;
            }
        }
    }

    public final void removeFee(Fee fee) {
        fees.remove(fee);
    }
    
    public final void removeEx(StringContainer exemption) {
        exemptions.remove(exemption);
    }
    
    public final void removeTvClass(StringContainer tvClass) {
        tvClassifications.remove(tvClass);
    }

    public final SCPollutant[] getPollutants() {
        return pollutants.toArray(new SCPollutant[0]);
    }

    public final void setPollutants(SCPollutant[] pollutants) {
        this.pollutants = new ArrayList<SCPollutant>(pollutants.length);

        for (SCPollutant tempDef : pollutants) {
            this.pollutants.add(tempDef);
        }
    }
    
    public final SCNonChargePollutant[] getNcPollutants() {
        return ncPollutants.toArray(new SCNonChargePollutant[0]);
    }

    public final void setNcPollutants(SCNonChargePollutant[] ncPollutants) {
        this.ncPollutants = new ArrayList<SCNonChargePollutant>(ncPollutants.length);

        for (SCNonChargePollutant tempDef : ncPollutants) {
            this.ncPollutants.add(tempDef);
        }
    }
    
    public void setExemptions(StringContainer[] exemptions) {
        this.exemptions = new ArrayList<StringContainer>(exemptions.length);
    }

    public void setTvClassifications(StringContainer[] tvClassifications) {
        this.tvClassifications = new ArrayList<StringContainer>(tvClassifications.length);;
    }

    public final void addPollutant(SCPollutant pollutant) {
        if (pollutant != null) {
            if (pollutants == null) {
                pollutants = new ArrayList<SCPollutant>(1);
            }

            this.pollutants.add(pollutant);
        }
    }
    
    public final void addNonChargePollutant(SCNonChargePollutant ncPollutant) {
        if (ncPollutant != null) {
            if (ncPollutants == null) {
            	ncPollutants = new ArrayList<SCNonChargePollutant>(1);
            }

            this.ncPollutants.add(ncPollutant);
        }
    }   
    
    public final void addNonChargePollutantByCategory(List <SCNonChargePollutant> ncPollutantsNyCategory) {
        if (ncPollutantsNyCategory != null && ncPollutantsNyCategory.size() > 0) {
            if (ncPollutants == null) {
            	ncPollutants = new ArrayList<SCNonChargePollutant>(1);
            }

            this.ncPollutants.addAll(ncPollutantsNyCategory);
        }
    }  
    public final Collection<SelectItem> getFeePickList(String rptType, int minNeedClause, String clause) {
        TreeMap<Integer, SelectItem> tree = new TreeMap<Integer, SelectItem>();
        String dsc = null;
        Integer k;
        if(this.getFeeType().equals("rnge")) {
            for(Fee e : fees) {
                if(e.getLowRange() == null) {
                    logger.error("SCEmissionsReport " + getReportName()
                            + " beginnng reporting year " + reportingYear
                            // + " for reportingCd " + getReportingCd()
                            + "has a lowRange of null in feeId "
                            + e.getFeeId());
                    // Skip this bad one.
                    continue;
                }
                k = e.getLowRange();
                if(e.getHighRange() == null) {
                    dsc = e.getDescription(rptType, minNeedClause, clause);
                    k++; // keep keys unique
                }
                else if(e.getHighRange().intValue() == 0) {
                    dsc = e.getDescription(rptType, minNeedClause, clause);
                    k = new Integer(-1); // keep keys unique
                }
                else {
                    dsc = e.getDescription(rptType, minNeedClause, clause);
                }
                tree.put(k, new SelectItem(e.getFeeId(), dsc));
            }
        } else {
            k = new Integer(0);
            for(Fee e : fees) {
                tree.put(k++, new SelectItem(
                    e.getFeeId(), e.getDescription(EmissionReportsRealDef.NTV, minNeedClause, clause)));
            }
        }
        ArrayList<SelectItem> tempList = new ArrayList<SelectItem>(tree.values());
        return tempList;
    }

    public final void removePollutant(SCPollutant pollutant) {
        pollutants.remove(pollutant);
    }

    public final void removeNonChargePollutant(SCNonChargePollutant ncPollutant) {
    	ncPollutants.remove(ncPollutant);
    }
    
    public final Timestamp getEffectiveDate() {
    	
    	String inputMMDD = "0101";    	
    	int iMonth = Integer.parseInt(inputMMDD.substring(0, 2))-1;
        int iDay = Integer.parseInt(inputMMDD.substring(2));
        int iYear = reportingYear.intValue();
        
        Calendar calEffectiveDate = Calendar.getInstance();
        calEffectiveDate.set(iYear,iMonth,iDay);
        calEffectiveDate.set(Calendar.HOUR_OF_DAY, 0);
        calEffectiveDate.set(Calendar.MINUTE, 0);
        calEffectiveDate.set(Calendar.SECOND, 0);
        calEffectiveDate.set(Calendar.MILLISECOND, 0);
        effectiveDate = new Timestamp(calEffectiveDate.getTimeInMillis());
        effectiveDate.setNanos(0);
        return effectiveDate;
    }

    public final void setEffectiveDate(Timestamp effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public final String getFeeType() {
        return feeType;
    }

    public final void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final String getReportGroup() {
        return reportGroup;
    }

    public final void setReportGroup(String reportGroup) {
        this.reportGroup = reportGroup;
    }
    
    public final String getContentTypeCd() {
        return contentTypeCd;
    }
    
    public final void setContentTypeCd(String contentTypeCd) {
        this.contentTypeCd = contentTypeCd;
    }

    public final void setRegulatoryRequirementCd(String regulatoryRequirementCd) {
        this.regulatoryRequirementCd = regulatoryRequirementCd;
    }
    
    public final String getRegulatoryRequirementCd() {
        return regulatoryRequirementCd;
    }

    public final String getReportName() {
        return reportName;
    }

    public final void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public final Integer getWorkFlowTemplateId() {
        return workFlowTemplateId;
    }

    public final void setWorkFlowTemplateId(Integer workFlowTemplateId) {
        this.workFlowTemplateId = workFlowTemplateId;
    }

    public final void populate(ResultSet rs) {
        try {
            setId(AbstractDAO.getInteger(rs, "sc_emissions_report_id"));
            setWorkFlowTemplateId(AbstractDAO.getInteger(rs,
                    "process_template_id"));
            setContentTypeCd(rs.getString("content_type_cd"));
            setRegulatoryRequirementCd(rs.getString("regulatory_requirement_cd"));
            setReportName(rs.getString("emissions_report_nm"));
            setReportGroup(rs.getString("report_group_type_cd"));
            setFeeType(rs.getString("fee_type"));
            setReportingYear(AbstractDAO.getInteger(rs, "reporting_yr"));
            setEffectiveDate(rs.getTimestamp("report_effective_dt"));
            setReferenceYear(AbstractDAO.getInteger(rs, "reference_yr"));
            setReportFrequencyYear(AbstractDAO.getInteger(rs, "report_freq_yrs"));
            setPollutantCap(rs.getDouble("pollutant_cap"));
            setEiMinimumFee(rs.getBigDecimal("minimum_ei_charge"));
            setShapeId(AbstractDAO.getInteger(rs, "shape_id"));
            setTreatPartialAsFullPeriodFlag(rs
					.getString("treat_partial_as_full_period_flag"));

            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
        } catch (SQLException sqle) {
            logger.error("Required field error");
        }
        
        try {
            do {
                String pollutantCd = rs.getString("pollutant_cd");

                if (pollutantCd != null) {
                    SCPollutant tempPollutant = new SCPollutant();

                    tempPollutant.populate(rs);

                    if (!pollutants.contains(tempPollutant)) {
                        pollutants.add(tempPollutant);
                    }
                }
            } while (rs.next());
        } catch (SQLException sqle) {
            logger.debug(sqle.getMessage());
        }
    }

    @Override
    public final int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
                + ((effectiveDate == null) ? 0 : effectiveDate.hashCode());
        result = PRIME * result + ((feeType == null) ? 0 : feeType.hashCode());
        result = PRIME * result + ((id == null) ? 0 : id.hashCode());
        result = PRIME * result
                + ((reportGroup == null) ? 0 : reportGroup.hashCode());
        result = PRIME * result
                + ((reportName == null) ? 0 : reportName.hashCode());
        result = PRIME * result + ((reportingYear == null) ? 0 : reportingYear.hashCode());
        result = PRIME * result
                + ((contentTypeCd == null) ? 0 : contentTypeCd.hashCode());
        result = PRIME * result
                + ((regulatoryRequirementCd == null) ? 0 : regulatoryRequirementCd.hashCode());
        result = PRIME
                * result
                + ((workFlowTemplateId == null) ? 0 : workFlowTemplateId
                        .hashCode());
        return result;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SCEmissionsReport other = (SCEmissionsReport) obj;
        if (id != other.id) 
            return false;
        return true;
    }

    public final Integer getReportingYear() {
        return reportingYear;
    }

    public final void setReportingYear(Integer reportingYear) {
        this.reportingYear = reportingYear;
    }

    public final Integer getReferenceYear() {
    	this.referenceYear = this.reportingYear; // not used in IMPACT. default to reporting year
        return this.referenceYear;
    }

    public final void setReferenceYear(Integer referenceYear) {
    	//this.referenceYear = referenceYear;
    	this.referenceYear = this.reportingYear; // not used in IMPACT. default to reporting year
    }

    public final Integer getReportFrequencyYear() {
    	this.reportFrequencyYear = 1;   // not used in IMPACT. default to 1
        return this.reportFrequencyYear;
    }

    public final void setReportFrequencyYear(Integer reportFrequencyYear) {
    	//this.reportFrequencyYear = reportFrequencyYear;
    	this.reportFrequencyYear = 1;  // not used in IMPACT. default to 1
    }


    
    //private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    //    in.defaultReadObject();
        // manually set transient date values since this does not appear to
        // work properly with persistence
    //    setLongEffectiveDate(this.longEffectiveDate);
    //}
    
    //  Is this code one of the reasons to exclude EU?
    public String exemptionReason(String cd) {
        String rtn = "";
        for(StringContainer elm : exemptions) {
            if(elm.getStr().equals(cd)) {
                rtn = ExemptStatusDef.getData().getItems().getItemDesc(cd);
                break;
            }
        }
        return rtn;
    }
    
//  Is this code one of the reasons to exclude EU?
    public String tvClassificationReason(String cd) {
        String rtn = "";
        for(StringContainer elm : tvClassifications) {
            if(elm.getStr().equals(cd)) {
                rtn = TVClassification.getData().getItems().getItemDesc(cd);
                break;
            }
        }
        return rtn;
    }
    
    public boolean isBelowRequirements(EmissionUnit eu) {
        return isEuExempted(eu.getExemptStatusCd()) || isEuTvClassExcluded(eu.getTvClassCd());
    }
    
    private boolean isEuExempted(String exemptionCd) {
        boolean rtn = false;
        for(StringContainer sc : exemptions) {
            if(sc.getStr().equals(exemptionCd)) {
                rtn = true;
                break;
            }
        }
        return rtn;
    }
    
    private boolean isEuTvClassExcluded(String tvClassificationCd) {
        boolean rtn = false;
        for(StringContainer sc : tvClassifications) {
            if(sc.getStr().equals(tvClassificationCd)) {
                rtn = true;
                break;
            }
        }
        return rtn;
    }

    public void setExemptions(List<StringContainer> exemptions) {
        this.exemptions = exemptions;
    }

    public void setTvClassifications(List<StringContainer> tvClassifications) {
        this.tvClassifications = tvClassifications;
    }
    
    public final Fee getFeeFirstHalf() {
        return feeFirstHalf;
    }

    public final void setFeeFirstHalf(Fee feeFirstHalf) {
        this.feeFirstHalf = feeFirstHalf;
    }
    
    public final Fee getFeeSecondHalf() {
        return feeSecondHalf;
    }

    public final void setFeeSecondHalf(Fee feeSecondHalf) {
        this.feeSecondHalf = feeSecondHalf;
    }

	public double getPollutantCap() {
		return pollutantCap;
	}

	public void setPollutantCap(double pollutantCap) {
		this.pollutantCap = pollutantCap;
	}
	
	public Double getEiMinimumFeeDisplayAsDouble() {
		return Double.valueOf(eiMinimumFee.doubleValue());
	}
	
	public BigDecimal getEiMinimumFee() {
		return eiMinimumFee;
	}

	public void setEiMinimumFee(BigDecimal eiMinimumFee) {
		if (eiMinimumFee != null) {
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(FEE_MAX_SCALE);
			df.setMinimumFractionDigits(2);
			df.setGroupingUsed(false);
			eiMinimumFee = new BigDecimal(df.format(eiMinimumFee));
		}
		
		this.eiMinimumFee = eiMinimumFee;
	}

	@Override
	public ValidationMessage[] validate() {
		
		clearValidationMessages(); 
		
		// If any of the PM pollutants are set by the user to be billed based on allowable,
		// we will set all PM pollutants to be billed based on allowable.
		boolean pmBilledBasedOnPermitted = false;
		for (SCPollutant pollutant : pollutants) {
			if (pollutant.isBilledBasedOnPermitted() && pollutant.getPollutantCd().startsWith("PM")) {
				pmBilledBasedOnPermitted = true;
				break;
			}
		}

		for (SCPollutant pollutant : pollutants) {
			if (pollutant.getPollutantCd().startsWith("PM") && pmBilledBasedOnPermitted) {
				if (!pollutant.isBilledBasedOnPermitted()) {
					this.validationMessages.put("scPMPollutant",
							new ValidationMessage("scpollutants",
									"If any of the PM pollutants are set to be billed based on allowable, " 
											+ "then all PM pollutants will be set to be billed based on allowable.",
									ValidationMessage.Severity.ERROR, null));
					
				}
				pollutant.setBilledBasedOnPermitted(true);
			}
		}
		
		// Check for duplicates in non-chargeable table.
		List<String> dupCodes = new ArrayList<String>();
		HashMap<String, HashMap<String, SCNonChargePollutant>> ncPollMap = new HashMap<String, HashMap<String, SCNonChargePollutant>>();

		for (SCNonChargePollutant scnc : ncPollutants) {
			
			HashMap<String, SCNonChargePollutant> scSccMap = ncPollMap.get(scnc.getPollutantCd());
			if (scSccMap == null) {
				scSccMap = new HashMap<String, SCNonChargePollutant>();
				ncPollMap.put(scnc.getPollutantCd(), scSccMap);
			}
			
			SCNonChargePollutant scncOth = null;
			String sccId = null;
			if (scnc.getSccCd() == null || scnc.getSccCd().getSccId() == null) {
				sccId = SccCode.DUMMY_SCC_ID;
			} else {
				sccId = scnc.getSccCd().getSccId();
			}
			scncOth = scSccMap.get(sccId);
			if (scncOth == null) {
				scSccMap.put(sccId, scnc);
			} else {
				// We have a duplicate!
				if (!dupCodes.contains(scnc.getPollutantDsc())) {
					dupCodes.add(scnc.getPollutantDsc());
				}					
			}
			
		}
		
		if (!dupCodes.isEmpty()) {
			String err = null;
			for (String dsc : dupCodes) {
				if (err == null) {
					err = dsc;
				} else {
					err = err + ", " + dsc;
				}
			}
			this.validationMessages.put("ncPollDuplicate",
					new ValidationMessage("nonchargePollutants",
							"Cannot have duplicate pollutants in the non-chargeable pollutant table: " + err + ".",
							ValidationMessage.Severity.ERROR, null));
		}
		
		String ncPollBilledByAllow = "";
		for (String pollCd : ncPollMap.keySet()) {
			for (SCPollutant scPoll : pollutants) {
				if (scPoll.getPollutantCd().equals(pollCd) && scPoll.isBilledBasedOnPermitted()) {
					if (ncPollBilledByAllow.length() > 0) {
						ncPollBilledByAllow = ncPollBilledByAllow + ", ";
					}
					ncPollBilledByAllow = ncPollBilledByAllow + pollCd;
				}
			}
		}
		
		if (ncPollBilledByAllow.length() > 0) {
			this.validationMessages.put("ncPollBilledByAllow",
					new ValidationMessage("nonchargePollutants",
							"A pollutant that is billed based on allowable amount may not be set as a non-chargeable pollutant: " 
									+ ncPollBilledByAllow,
							ValidationMessage.Severity.ERROR, null));					
		}
		
		for (String pollCd : ncPollMap.keySet()) {
			HashMap<String, SCNonChargePollutant> hm = ncPollMap.get(pollCd);
			if (hm.size() > 1 && hm.containsKey(SccCode.DUMMY_SCC_ID)) {
				if (!hm.get(SccCode.DUMMY_SCC_ID).isFugitiveOnly()) {
					String err = hm.get(SccCode.DUMMY_SCC_ID).getPollutantDsc();
					this.validationMessages.put("ncPollNoEffect",
							new ValidationMessage("nonchargePollutants",
									"Pollutant rows with SCC values in the non-chargeable pollutant table for pollutant " + err 
									+ " will have no effect since one row has no SCC value and therefore the exclusion applies "
									+ "to all processes, not just those with a specific SCC.",
									ValidationMessage.Severity.ERROR, null));
				} else {
					for (SCNonChargePollutant chkFug : hm.values()) {
						if (chkFug.getSccCd() == null || chkFug.getSccCd().getSccId() == null 
								|| chkFug.getSccCd().getSccId().equals(SccCode.DUMMY_SCC_ID)) {
							continue;
						}
						if (chkFug.isFugitiveOnly()) {
							String err = hm.get(SccCode.DUMMY_SCC_ID).getPollutantDsc();
							this.validationMessages.put("ncPollNoEffectFug",
									new ValidationMessage("nonchargePollutants",
											"Pollutant rows with SCC values and Fugitive Only in the non-chargeable pollutant "
											+ "table for pollutant " + err + " will have no effect since one row has no SCC value "
											+ "and is also marked Fugitive Only and therefore the Fugitive Only exclusion applies "
											+ "to all processes, not just those with a specific SCC.",
								ValidationMessage.Severity.ERROR, null));
						}
					}
				}
			}
		}

		// Make sure the SCC values are complete.
		for (SCNonChargePollutant scnc : ncPollutants) {
			if (scnc.getSccCd() != null && scnc.getSccCd().getSccId() != null && scnc.getSccCd().getSccIdL1Cd() != null
					&& (scnc.getSccCd().getSccIdL2Cd() == null || scnc.getSccCd().getSccIdL3Cd() == null
							|| scnc.getSccCd().getSccIdL4Cd() == null)) {
				this.validationMessages.put("ncPollIncompleteSCC",
						new ValidationMessage("nonchargePollutants",
								"The SCC value for pollutant " + scnc.getPollutantDsc() + " in the non-chargeable pollutant table "
								+ "has an incomplete SCC value.",
								ValidationMessage.Severity.ERROR, null));
			}
		}
		
		// Check for duplicates in chargeable table.
		if (Utility.hasDuplicate(pollutants)) {
			this.validationMessages
					.put("cPollDuplicate",
							new ValidationMessage(
									"pollutants",
									"Cannot have duplicate pollutants in the chargeable pollutant table.",
									ValidationMessage.Severity.ERROR, null));
		}
		
		// Check for duplicates in Data Import table.
		if (Utility.hasDuplicate(dataImportPollutantList)) {
			this.validationMessages.put("dataImportPollutantDuplicate",
					new ValidationMessage("dataImportPollutants",
							"Cannot have duplicate pollutants in the Data Import pollutant table.",
							ValidationMessage.Severity.ERROR, null));
		}
		
		// Check for duplicated sort order in Data Import table.
		List<Integer> pollutantOrderList = new ArrayList<Integer>(dataImportPollutantList.size());
		for(SCDataImportPollutant pollutant: dataImportPollutantList ) {
			pollutantOrderList.add(pollutant.getSortOrder());
		}
		if(Utility.hasDuplicate(pollutantOrderList)) {
			this.validationMessages.put("dataImportPollutantSortOrderDuplicate",
					new ValidationMessage("dataImportPollutantsSortOrder",
							"Cannot have duplicate sort order in the Data Import pollutant table.",
							ValidationMessage.Severity.ERROR, null));
		}
		
		// Check for duplicate display order in criteria air pollutants/other table.
		List<Integer> displayOrderList = new ArrayList<Integer>(pollutants.size());
		for (SCPollutant pollutant : pollutants){
				displayOrderList.add(pollutant.getDisplayOrder());
		}
		if(Utility.hasDuplicate(displayOrderList)) {
			this.validationMessages.put("capPollutantDuplicateDisplayOrder",
					new ValidationMessage("capPollutantsDisplayOrder",
							"Cannot have duplicate sort order in the Criteria Air Polutants/Other table.",
							ValidationMessage.Severity.ERROR, null));
		}
		
		// Check for criteria air pollutants in the data import file pollutants table.
		HashSet<String> capPollutants = new HashSet<String>(pollutants.size());
		for (SCPollutant pollutant : pollutants) {
			capPollutants.add(pollutant.getPollutantCd());
		}
		for (SCDataImportPollutant pollutant : dataImportPollutantList) {
			if (capPollutants.contains(pollutant.getPollutantCd())) {
				this.validationMessages.put(pollutant.getPollutantCd(),
						new ValidationMessage("invalidDataImportFilePollutant",
								"Pollutant (" + PollutantDef.getData().getItems().getItemDesc(pollutant.getPollutantCd())
										+ ") is present in both Criteria Air Pollutants and Data Import File Pollutants tables."
										+ " Please delete it from one of the tables.",
								ValidationMessage.Severity.ERROR, null));
			}
		}
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
	
	public Integer getShapeId() {
		return shapeId;
	}

	public void setShapeId(Integer shapeId) {
		this.shapeId = shapeId;
	}
	
	public String getStrShapeId() {
		return null != this.shapeId ? this.shapeId.toString() : null;
	}

	public void setStrShapeId(String strShapeId) {
		Integer shapeId = null != strShapeId ? Integer.valueOf(strShapeId)
				: null;
		setShapeId(shapeId);
	}
	
	public List<String> getPermitClassCds() {
		if(null == permitClassCds) {
			setPermitClassCds(new ArrayList<String>());
		}
		return permitClassCds;
	}

	public void setPermitClassCds(List<String> permitClassCds) {
		this.permitClassCds = permitClassCds;
	}
	
	public List<String> getFacilityTypeCds() {
		if(null == facilityTypeCds) {
			setFacilityTypeCds(new ArrayList<String>());
		}
		return facilityTypeCds;
	}

	public void setFacilityTypeCds(List<String> facilityTypeCds) {
		this.facilityTypeCds = facilityTypeCds;
	}
	
	public String getTreatPartialAsFullPeriodFlag() {
		return treatPartialAsFullPeriodFlag;
	}

	public void setTreatPartialAsFullPeriodFlag(
			String treatPartialAsFullPeriodFlag) {
		this.treatPartialAsFullPeriodFlag = treatPartialAsFullPeriodFlag;
	}
	
	public final String getShapeLabel() {
		return ShapeDef.getData().getItems()
				.getItemDesc(this.getStrShapeId());
	}
	
	public String displayFacilityLocationOnMap() {
		SpatialData spatialDataBean = (SpatialData) FacesUtil
				.getManagedBean("spatialData");

		if (null != spatialDataBean) {
			spatialDataBean.refresh();
			Integer shapeId = this.getShapeId();
			for (SpatialDataLineItem item : spatialDataBean.getImportedShapes()) {
				item.setSelected(item.getShapeId().equals(shapeId));
			}
		}

		return SPATIAL_DATA_OUTCOME;
	}
	
	public boolean isSCContentTypeForFullYear() {
		boolean ret = false;
		List<ContentTypeDef> contentTypeList = ContentTypeDef.getDefListItems();

		// If Content Type in the Service Catalog matches the Content Type
		// from the definition list, determine if the Service Catalog template
		// is configured to apply for a full year.

		for (ContentTypeDef c : contentTypeList) {
			if (c.getCode().equals(this.getContentTypeCd())) {

				if (c.getStartDay() == 1 && c.getStartMonth() == 1
						&& c.getEndDay() == 31 && c.getEndMonth() == 12) {
					return true;
				}
			}
		}

		return ret;
	}

	public boolean isItTimeToEnableReportingForPartialCurrentYear() {
		boolean ret = false;
		List<ContentTypeDef> contentTypeList = ContentTypeDef.getDefListItems();

		for (ContentTypeDef c : contentTypeList) {

			// If Content Type in the Service Catalog matches the Content Type
			// from the definition list, determine if it's time to enable
			// reporting for the facilities that match the facility-selection
			// criteria in
			// the Service Catalog.

			// Only return true if the current date is after the End Date for
			// the
			// matching Content Type.

			Calendar currentCalendar = Calendar.getInstance();
			int currentYear = currentCalendar.get(Calendar.YEAR);

			if (c.getCode().equals(this.getContentTypeCd())) {

				// End Date for Content Type
				Calendar endDateCalendar = Calendar.getInstance();
				endDateCalendar.set(currentYear, c.getEndMonth() - 1,
						c.getEndDay());
				Date endDateDate = Utility.formatEndOfDay(endDateCalendar
						.getTime());
				long endDateLong = endDateDate.getTime();

				long todaysDate = System.currentTimeMillis();

				if (todaysDate > endDateLong) {
					ret = true;
				}
			}
		}

		return ret;
	}
	
	public ContentTypeDef getContentTypeDef() {
		return contentTypeDef;
	}

	public void setContentTypeDef(ContentTypeDef contentTypeDef) {
		this.contentTypeDef = contentTypeDef;
	}

	public String getFirstDayOfPeriod() {

		String firstDayOfPeriod = this.getReportingYear() + "-"
				+ this.getContentTypeDef().getStartMonth() + "-"
				+ this.getContentTypeDef().getStartDay()
				+ " 00:00:00.000000";

		return (new String(firstDayOfPeriod));

	}
	
	public String getLastDayOfPeriod() {

		String lastDayOfPeriod = this.getReportingYear() + "-"
				+ this.getContentTypeDef().getEndMonth() + "-"
				+ this.getContentTypeDef().getEndDay()
				+ " 23:59:59.999999";

		return (new String(lastDayOfPeriod));

	}

	public List<SCDataImportPollutant> getDataImportPollutantList() {
		if(null == dataImportPollutantList) {
			dataImportPollutantList = new ArrayList<SCDataImportPollutant>();
		}
		return dataImportPollutantList;
	}

	public void setDataImportPollutantList(List<SCDataImportPollutant> dataImportPollutantList) {
		this.dataImportPollutantList = dataImportPollutantList;
	}
	
	public void addDataImportPollutant(SCDataImportPollutant dataImportPollutant) {
		if (dataImportPollutant != null) {
			if (dataImportPollutantList == null) {
				dataImportPollutantList = new ArrayList<SCDataImportPollutant>();
			}
			this.dataImportPollutantList.add(dataImportPollutant);
		}
	}
	
	public void removeDataImportPollutant(SCDataImportPollutant dataImportPollutant) {
		this.dataImportPollutantList.remove(dataImportPollutant);
    }
    
}
