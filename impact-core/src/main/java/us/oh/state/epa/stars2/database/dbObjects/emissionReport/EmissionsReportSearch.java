package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.def.RegulatoryRequirementTypeDef;
import us.oh.state.epa.stars2.def.ReportEisStatusDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;

@SuppressWarnings("serial")
public class EmissionsReportSearch extends BaseDB {
    private Integer emissionsRptId;
    private String emissionsInventoryId;
    private Integer reportModified;
    private String emissionsInventoryModifiedId;
    private Integer companionReport;
    private Integer fpId;
    private String facilityId;
    private String facilityName;
    private String countyCd;
    private Integer year;
    private Integer versionId;
    private String dolaaCd;
    private String reportingState;
    private Timestamp receivedDate;
    private Timestamp rptReceivedStatusDate;  // submitted date
    private Timestamp approvedDate;
    private Integer daysToApprove;
    private int mark = 0;
    private String eisStatusCd;
    private String pollutantCd;
    private Integer feeId;
    private Integer lowRange;
    private Integer highRange;
    private Integer emissions;
    private Float totalEmissions;
    private Float minEmissions;
    private Float maxEmissions;
    private Timestamp beginDt;
    private Timestamp endDt;
    private boolean stagingDBQuery;
    private String cmpId;
    private String companyName;
    private Float totalReportedEmissions;
    private String submitter;
    private String submitterNetworkLoginName;
    private String submitterExternalUsername;
    private String submitterCompanyName;
    private Double invoiceAmount;
    private Timestamp invoiceDate;
    private Timestamp paymentReceivedDate;
    private String contentTypeCd;
    private String regulatoryRequirementCd; // search criteria
    private Set<String> regulatoryRequirementCds = new TreeSet<String>(); // search result

    public EmissionsReportSearch() {
        super();
    }

    public EmissionsReportSearch(EmissionsReportSearch old) {
        super(old);

        if (old != null) {
            setEmissionsRptId(old.getEmissionsRptId());
            setEmissionsInventoryId(old.getEmissionsInventoryId());
            setReportModified(old.getReportModified());
            setEmissionsInventoryModifiedId(old.getEmissionsInventoryModifiedId());
            setFpId(old.getFpId());
            setFacilityId(old.getFacilityId());
            setFacilityName(old.getFacilityName());
            setCountyCd(old.getCountyCd());
            setYear(old.getYear());
            setDolaaCd(old.getDolaaCd());
            setReportingState(old.getReportingState());
            setPollutantCd(old.getPollutantCd());
            setEmissions(old.getEmissions());
            setTotalEmissions(old.getTotalEmissions());
            setEisStatusCd(old.getEisStatusCd());
            setReceivedDate(old.getReceivedDate());
            setApprovedDate(old.getApprovedDate());
            setRptReceivedStatusDate(old.getRptReceivedStatusDate());
            setDaysToApprove(old.getDaysToApprove());
            setMark(old.getMark());
            setCmpId(old.getCmpId());
            setCompanyName(old.getCompanyName());
            setTotalReportedEmissions(old.getTotalReportedEmissions());
            setSubmitter(old.getSubmitter());
            setSubmitterNetworkLoginName(old.getSubmitterNetworkLoginName());
            setSubmitterExternalUsername(old.getSubmitterExternalUsername());
            setSubmitterCompanyName(old.getSubmitterCompanyName());
            setInvoiceAmount(old.getInvoiceAmount());
            setInvoiceDate(old.getInvoiceDate());
            setPaymentReceivedDate(old.getPaymentReceivedDate());
            setContentTypeCd(old.getContentTypeCd());
            setRegulatoryRequirementCd(old.getRegulatoryRequirementCd());
            setRegulatoryRequirementCds(old.getRegulatoryRequirementCds());
        }
    }
    
    public String getRegulatoryRequirementCdsString() {
    	StringBuffer sb = new StringBuffer();
    	Set<String> cds = getRegulatoryRequirementCds();
    	Iterator<String> iter = cds.iterator();
    	int i = 0;
    	while (iter.hasNext()) {
    		String cd = iter.next();
    		sb.append(RegulatoryRequirementTypeDef.getData().getItems().getItemDesc(cd));
    		if (++i < cds.size()) {
    			sb.append("; ");
    		}
    	}
    	return sb.toString();
    }

	public Set<String> getRegulatoryRequirementCds() {
		return regulatoryRequirementCds;
	}

	public void setRegulatoryRequirementCds(Set<String> regulatoryRequirementCds) {
		this.regulatoryRequirementCds = regulatoryRequirementCds;
	}

	public String getContentTypeCd() {
		return contentTypeCd;
	}

	public void setContentTypeCd(String contentTypeCd) {
		this.contentTypeCd = contentTypeCd;
	}

	public String getRegulatoryRequirementCd() {
		return regulatoryRequirementCd;
	}

	public void setRegulatoryRequirementCd(String regulatoryRequirementCd) {
		this.regulatoryRequirementCd = regulatoryRequirementCd;
	}

	public String getSubmitterCompanyName() {
		return submitterCompanyName;
	}

	public void setSubmitterCompanyName(String submitterCompanyName) {
		this.submitterCompanyName = submitterCompanyName;
	}

	public String getSubmitterNetworkLoginName() {
		return submitterNetworkLoginName;
	}

	public void setSubmitterNetworkLoginName(String submitterNetworkLoginName) {
		this.submitterNetworkLoginName = submitterNetworkLoginName;
	}

	public String getSubmitterExternalUsername() {
		return submitterExternalUsername;
	}

	public void setSubmitterExternalUsername(String submitterExternalUsername) {
		this.submitterExternalUsername = submitterExternalUsername;
	}

	public String getSubmitter() {
		return submitter;
	}

	public void setSubmitter(String submitter) {
		this.submitter = submitter;
	}

	public String getYears() {
        return year.toString();
    }

    public final Float getTotalEmissions() {
        return totalEmissions;
    }
    
    public final String getTotalEmissionsDigits() {
        if(totalEmissions == null) {
            return null;
        }
        return EmissionsReport.numberToString(totalEmissions.doubleValue());
    }

    public final void setTotalEmissions(Float totalEmissions) {
        this.totalEmissions = totalEmissions;
    }

    public final Integer getFpId() {
        return fpId;
    }

    public final void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public final String getCountyCd() {
        return countyCd;
    }

    public final void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    public final String getDolaaCd() {
        return dolaaCd;
    }

    public final void setDolaaCd(String dolaaCd) {
        this.dolaaCd = dolaaCd;
    }

    public final Integer getEmissions() {
        return emissions;
    }

    public final void setEmissions(Integer emissions) {
        this.emissions = emissions;
    }

    public final String getFacilityId() {
        return facilityId;
    }

    public final void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public final String getFacilityName() {
        return facilityName;
    }

    public final void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public final String getPollutantCd() {
        return pollutantCd;
    }

    public final void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

    public final String getReportingState() {
        return reportingState;
    }

    public final void setReportingState(String reportingState) {
        this.reportingState = reportingState;
    }

    public final String getReportingStateDesc(){
    	String ret = "";
    	
    	ret = ReportReceivedStatusDef.getData().getItems().getItemDesc(
    					getReportingState());
    	
    	return ret;
    }
    
    public final String getEmissionRange() {
        if(feeId != null) {
            return Fee.getDescription(lowRange, highRange);
        } else {
            return null;
        }
    }
    
    public final Integer getYear() {
        return year;
    }

    public final void setYear(Integer year) {
        this.year = year;
    }

    public final Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public final void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
    }
    
    public final String getEmissionsInventoryId() {
        return emissionsInventoryId;
    }


    public final void setEmissionsInventoryId(String emissionsInventoryId) {
        this.emissionsInventoryId = emissionsInventoryId;
    }
    
    public final String getEmissionsInventoryModifiedId() {
        return emissionsInventoryModifiedId;
    }


    public final void setEmissionsInventoryModifiedId(String emissionsInventoryModifiedId) {
        this.emissionsInventoryModifiedId = emissionsInventoryModifiedId;
    }

    public final void populate(ResultSet rs) {
        boolean readyForFee = false;
        try {
            setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));
            setEmissionsInventoryId(rs.getString("ei_id"));
            setCompanionReport(AbstractDAO.getInteger(rs, "companion_report"));
            Integer report_modified = AbstractDAO.getInteger(rs, "report_modified");
            setReportModified(report_modified);
            if (report_modified != null) {
            	setEmissionsInventoryModifiedId(AbstractDAO.formatId("EI", "%07d", report_modified.toString()));
            }
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setFacilityId(rs.getString("facility_id"));
            setFacilityName(rs.getString("facility_nm"));
            setVersionId(AbstractDAO.getInteger(rs, "version_id"));
            setDolaaCd(rs.getString("do_laa_cd"));
            setReportingState(rs.getString("rpt_received_status_cd"));
            setEisStatusCd(rs.getString("eis_status_cd"));
            setYear(AbstractDAO.getInteger(rs, "report_year"));
            setTotalEmissions(AbstractDAO.getFloat(rs, "total_emissions"));
            setReceivedDate(rs.getTimestamp("received_date"));
            setApprovedDate(rs.getTimestamp("report_approved_status_dt"));
            setRptReceivedStatusDate(rs.getTimestamp("rpt_received_status_dt"));
            readyForFee = true;
            setCmpId(rs.getString("cmp_id"));
            setCompanyName(rs.getString("company_nm"));
            setTotalReportedEmissions(AbstractDAO.getFloat(rs, "total_reported_emissions"));

            setSubmitterExternalUsername(rs.getString("submitter_envite_username"));
            setSubmitterNetworkLoginName(rs.getString("submitter_network_login_nm"));
            
            setSubmitterCompanyName(rs.getString("submitter_company_name"));
            if (null != getSubmitterExternalUsername()) {
            	String companyName = 
            			getSubmitterCompanyName() == null? 
            					"Non-AQD" : getSubmitterCompanyName();
            	setSubmitter(companyName + " (" + getSubmitterExternalUsername() + ")");
            } else
            if (null != getSubmitterNetworkLoginName()) {
            	setSubmitter("AQD (" + getSubmitterNetworkLoginName() + ")");
            }
            
            setInvoiceAmount(rs.getDouble("invoice_amount"));
            setInvoiceDate(rs.getTimestamp("invoice_date"));
            setPaymentReceivedDate(rs.getTimestamp("payment_received_date"));
        } catch (SQLException sqle) {
            if(!readyForFee) {
                logger.error(sqle.getMessage(), sqle);
            }
        }
    }

    public final Integer getReportModified() {
        return reportModified;
    }

    public final void setReportModified(Integer reportModified) {
        this.reportModified = reportModified;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getFeeId() {
        return feeId;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }

    public Integer getCompanionReport() {
        return companionReport;
    }

    public void setCompanionReport(Integer companionReport) {
        this.companionReport = companionReport;
    }

    public String getEisStatusCd() {
        return eisStatusCd;
    }

    public void setEisStatusCd(String eisStatusCd) {
        this.eisStatusCd = eisStatusCd;
    }

    public final String getEisStatusDesc(){
    	String ret = "";
    	
    	ret = ReportEisStatusDef.getData().getItems().getItemDesc(
    					getEisStatusCd());
    	return ret;
    }
    
    public Float getMaxEmissions() {
        return maxEmissions;
    }

    public void setMaxEmissions(Float maxEmissions) {
        this.maxEmissions = maxEmissions;
    }

    public Float getMinEmissions() {
        return minEmissions;
    }

    public void setMinEmissions(Float minEmissions) {
        this.minEmissions = minEmissions;
    }

    public Timestamp getBeginDt() {
        return beginDt;
    }

    public void setBeginDt(Timestamp beginDt) {
        this.beginDt = beginDt;
    }

    public Timestamp getEndDt() {
        return endDt;
    }

    public void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
    }

    public Timestamp getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Integer getHighRange() {
        return highRange;
    }

    public void setHighRange(Integer highRange) {
        this.highRange = highRange;
    }

    public Integer getLowRange() {
        return lowRange;
    }

    public void setLowRange(Integer lowRange) {
        this.lowRange = lowRange;
    }

    public boolean isStagingDBQuery() {
        return stagingDBQuery;
    }

    public void setStagingDBQuery(boolean stagingDBQuery) {
        this.stagingDBQuery = stagingDBQuery;
    }

    public Timestamp getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Timestamp approvedDate) {
        this.approvedDate = approvedDate;
    }

    public Integer getDaysToApprove() {
        return daysToApprove;
    }

    public void setDaysToApprove(Integer daysToApprove) {
        this.daysToApprove = daysToApprove;
    }

    public Timestamp getRptReceivedStatusDate() {
        return rptReceivedStatusDate;
    }

    public void setRptReceivedStatusDate(Timestamp rptReceivedStatusDate) {
        this.rptReceivedStatusDate = rptReceivedStatusDate;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
    
    public String getBackground() {
        if(mark == 1) return "background-color:#FF9900;";  // 50% level
        if(mark == 2) return "background-color:#FF5050;";  //100 % level
        return "background-color:#FFFFFF;";
    }
    
    public final String getCmpId() {
        return cmpId;
    }

    public final void setCmpId(String cmpId) {
        this.cmpId = cmpId;
    }
    
    public final String getCompanyName() {
        return companyName;
    }

    public final void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public final Float getTotalReportedEmissions() {
        return totalReportedEmissions;
    }
    
    public final String getTotalReportedEmissionsDigits() {
        if(totalReportedEmissions == null) {
            return null;
        }
        return EmissionsReport.numberToString(totalReportedEmissions.doubleValue());
    }

    public final void setTotalReportedEmissions(Float totalReportedEmissions) {
        this.totalReportedEmissions = totalReportedEmissions;
    }
    
	public Double getInvoiceAmount() {
		return invoiceAmount;
	}

	public void setInvoiceAmount(Double invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	public Timestamp getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Timestamp invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Timestamp getPaymentReceivedDate() {
		return paymentReceivedDate;
	}

	public void setPaymentReceivedDate(Timestamp paymentReceivedDate) {
		this.paymentReceivedDate = paymentReceivedDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((approvedDate == null) ? 0 : approvedDate.hashCode());
		result = prime * result + ((beginDt == null) ? 0 : beginDt.hashCode());
		result = prime * result + ((cmpId == null) ? 0 : cmpId.hashCode());
		result = prime * result
				+ ((companionReport == null) ? 0 : companionReport.hashCode());
		result = prime * result
				+ ((companyName == null) ? 0 : companyName.hashCode());
		result = prime * result
				+ ((countyCd == null) ? 0 : countyCd.hashCode());
		result = prime * result
				+ ((daysToApprove == null) ? 0 : daysToApprove.hashCode());
		result = prime * result + ((dolaaCd == null) ? 0 : dolaaCd.hashCode());
		result = prime * result
				+ ((eisStatusCd == null) ? 0 : eisStatusCd.hashCode());
		result = prime * result
				+ ((emissions == null) ? 0 : emissions.hashCode());
		result = prime
				* result
				+ ((emissionsInventoryId == null) ? 0 : emissionsInventoryId
						.hashCode());
		result = prime
				* result
				+ ((emissionsInventoryModifiedId == null) ? 0
						: emissionsInventoryModifiedId.hashCode());
		result = prime * result
				+ ((emissionsRptId == null) ? 0 : emissionsRptId.hashCode());
		result = prime * result + ((endDt == null) ? 0 : endDt.hashCode());
		result = prime * result
				+ ((facilityId == null) ? 0 : facilityId.hashCode());
		result = prime * result
				+ ((facilityName == null) ? 0 : facilityName.hashCode());
		result = prime * result + ((feeId == null) ? 0 : feeId.hashCode());
		result = prime * result + ((fpId == null) ? 0 : fpId.hashCode());
		result = prime * result
				+ ((highRange == null) ? 0 : highRange.hashCode());
		result = prime * result
				+ ((lowRange == null) ? 0 : lowRange.hashCode());
		result = prime * result + mark;
		result = prime * result
				+ ((maxEmissions == null) ? 0 : maxEmissions.hashCode());
		result = prime * result
				+ ((minEmissions == null) ? 0 : minEmissions.hashCode());
		result = prime * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = prime * result
				+ ((receivedDate == null) ? 0 : receivedDate.hashCode());
		result = prime * result
				+ ((reportModified == null) ? 0 : reportModified.hashCode());
		result = prime * result
				+ ((reportingState == null) ? 0 : reportingState.hashCode());
		result = prime
				* result
				+ ((rptReceivedStatusDate == null) ? 0 : rptReceivedStatusDate
						.hashCode());
		result = prime * result + (stagingDBQuery ? 1231 : 1237);
		result = prime * result
				+ ((submitter == null) ? 0 : submitter.hashCode());
		result = prime
				* result
				+ ((submitterCompanyName == null) ? 0 : submitterCompanyName
						.hashCode());
		result = prime
				* result
				+ ((submitterExternalUsername == null) ? 0
						: submitterExternalUsername.hashCode());
		result = prime
				* result
				+ ((submitterNetworkLoginName == null) ? 0
						: submitterNetworkLoginName.hashCode());
		result = prime * result
				+ ((totalEmissions == null) ? 0 : totalEmissions.hashCode());
		result = prime
				* result
				+ ((totalReportedEmissions == null) ? 0
						: totalReportedEmissions.hashCode());
		result = prime * result
				+ ((versionId == null) ? 0 : versionId.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		result = prime
				* result
				+ ((invoiceAmount == null) ? 0 : invoiceAmount
						.hashCode());
		result = prime
				* result
				+ ((invoiceDate == null) ? 0 : invoiceDate
						.hashCode());
		result = prime
				* result
				+ ((paymentReceivedDate == null) ? 0 : paymentReceivedDate
						.hashCode());
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
		EmissionsReportSearch other = (EmissionsReportSearch) obj;
		if (approvedDate == null) {
			if (other.approvedDate != null)
				return false;
		} else if (!approvedDate.equals(other.approvedDate))
			return false;
		if (beginDt == null) {
			if (other.beginDt != null)
				return false;
		} else if (!beginDt.equals(other.beginDt))
			return false;
		if (cmpId == null) {
			if (other.cmpId != null)
				return false;
		} else if (!cmpId.equals(other.cmpId))
			return false;
		if (companionReport == null) {
			if (other.companionReport != null)
				return false;
		} else if (!companionReport.equals(other.companionReport))
			return false;
		if (companyName == null) {
			if (other.companyName != null)
				return false;
		} else if (!companyName.equals(other.companyName))
			return false;
		if (countyCd == null) {
			if (other.countyCd != null)
				return false;
		} else if (!countyCd.equals(other.countyCd))
			return false;
		if (daysToApprove == null) {
			if (other.daysToApprove != null)
				return false;
		} else if (!daysToApprove.equals(other.daysToApprove))
			return false;
		if (dolaaCd == null) {
			if (other.dolaaCd != null)
				return false;
		} else if (!dolaaCd.equals(other.dolaaCd))
			return false;
		if (eisStatusCd == null) {
			if (other.eisStatusCd != null)
				return false;
		} else if (!eisStatusCd.equals(other.eisStatusCd))
			return false;
		if (emissions == null) {
			if (other.emissions != null)
				return false;
		} else if (!emissions.equals(other.emissions))
			return false;
		if (emissionsInventoryId == null) {
			if (other.emissionsInventoryId != null)
				return false;
		} else if (!emissionsInventoryId.equals(other.emissionsInventoryId))
			return false;
		if (emissionsInventoryModifiedId == null) {
			if (other.emissionsInventoryModifiedId != null)
				return false;
		} else if (!emissionsInventoryModifiedId
				.equals(other.emissionsInventoryModifiedId))
			return false;
		if (emissionsRptId == null) {
			if (other.emissionsRptId != null)
				return false;
		} else if (!emissionsRptId.equals(other.emissionsRptId))
			return false;
		if (endDt == null) {
			if (other.endDt != null)
				return false;
		} else if (!endDt.equals(other.endDt))
			return false;
		if (facilityId == null) {
			if (other.facilityId != null)
				return false;
		} else if (!facilityId.equals(other.facilityId))
			return false;
		if (facilityName == null) {
			if (other.facilityName != null)
				return false;
		} else if (!facilityName.equals(other.facilityName))
			return false;
		if (feeId == null) {
			if (other.feeId != null)
				return false;
		} else if (!feeId.equals(other.feeId))
			return false;
		if (fpId == null) {
			if (other.fpId != null)
				return false;
		} else if (!fpId.equals(other.fpId))
			return false;
		if (highRange == null) {
			if (other.highRange != null)
				return false;
		} else if (!highRange.equals(other.highRange))
			return false;
		if (lowRange == null) {
			if (other.lowRange != null)
				return false;
		} else if (!lowRange.equals(other.lowRange))
			return false;
		if (mark != other.mark)
			return false;
		if (maxEmissions == null) {
			if (other.maxEmissions != null)
				return false;
		} else if (!maxEmissions.equals(other.maxEmissions))
			return false;
		if (minEmissions == null) {
			if (other.minEmissions != null)
				return false;
		} else if (!minEmissions.equals(other.minEmissions))
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		if (receivedDate == null) {
			if (other.receivedDate != null)
				return false;
		} else if (!receivedDate.equals(other.receivedDate))
			return false;
		if (reportModified == null) {
			if (other.reportModified != null)
				return false;
		} else if (!reportModified.equals(other.reportModified))
			return false;
		if (reportingState == null) {
			if (other.reportingState != null)
				return false;
		} else if (!reportingState.equals(other.reportingState))
			return false;
		if (rptReceivedStatusDate == null) {
			if (other.rptReceivedStatusDate != null)
				return false;
		} else if (!rptReceivedStatusDate.equals(other.rptReceivedStatusDate))
			return false;
		if (stagingDBQuery != other.stagingDBQuery)
			return false;
		if (submitter == null) {
			if (other.submitter != null)
				return false;
		} else if (!submitter.equals(other.submitter))
			return false;
		if (submitterCompanyName == null) {
			if (other.submitterCompanyName != null)
				return false;
		} else if (!submitterCompanyName.equals(other.submitterCompanyName))
			return false;
		if (submitterExternalUsername == null) {
			if (other.submitterExternalUsername != null)
				return false;
		} else if (!submitterExternalUsername
				.equals(other.submitterExternalUsername))
			return false;
		if (submitterNetworkLoginName == null) {
			if (other.submitterNetworkLoginName != null)
				return false;
		} else if (!submitterNetworkLoginName
				.equals(other.submitterNetworkLoginName))
			return false;
		if (totalEmissions == null) {
			if (other.totalEmissions != null)
				return false;
		} else if (!totalEmissions.equals(other.totalEmissions))
			return false;
		if (totalReportedEmissions == null) {
			if (other.totalReportedEmissions != null)
				return false;
		} else if (!totalReportedEmissions.equals(other.totalReportedEmissions))
			return false;
		if (versionId == null) {
			if (other.versionId != null)
				return false;
		} else if (!versionId.equals(other.versionId))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		if (invoiceAmount == null) {
			if (other.invoiceAmount != null)
				return false;
		} else if (!invoiceAmount.equals(other.invoiceAmount))
			return false;
		if (invoiceDate == null) {
			if (other.invoiceDate != null)
				return false;
		} else if (!invoiceDate.equals(other.invoiceDate))
			return false;
		if (paymentReceivedDate == null) {
			if (other.paymentReceivedDate != null)
				return false;
		} else if (!paymentReceivedDate.equals(other.paymentReceivedDate))
			return false;
		return true;
	}
    
    
}
