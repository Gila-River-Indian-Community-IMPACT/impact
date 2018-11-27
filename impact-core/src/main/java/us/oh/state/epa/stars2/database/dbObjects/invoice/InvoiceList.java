package us.oh.state.epa.stars2.database.dbObjects.invoice;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.def.InvoiceState;

public class InvoiceList extends BaseDB {

    private Integer invoiceId;
    private String facilityId;
    private String doLaaCd;
    private Integer permitId;
    private Integer emissionsRptId;
    private String invoiceStateCd;
    private String revenueStateCd;
    private Integer revenueId;
    private String revenueTypeCd;
    private Double origAmount;  // Stars2 Amount
    private Timestamp creationDate;
    private Timestamp dueDate;
    private String facilityName;
    private Integer fpId;
    private String invoiceStateDesc;
    private String revenueStateDesc;   //  will specify paid or not paid
    private String revenueStateDesc2;  //  will specify AGO or null
    private Double originalAmount; // from Revenues System
    private Double currentAmount;  // from Revenues System
    private Date beginDt;
    private Date endDt;
    private String emissionsRptCd;
    private boolean intraStateVoucherFlag;
    private PermitDocument permitInvDocument = new PermitDocument();
    private EmissionsDocument reportInvDocument = new EmissionsDocument();
    
    //for workflow review
    private Integer processId;
    private Double invoiceDifference;
    private String reportYear;
    private Integer prevReport;
    private Float totalTons;
	
	
    public InvoiceList() {
    }

    public InvoiceList(InvoiceList old) {
        super(old);

        if (old != null) {
            setInvoiceId(old.getInvoiceId());
            setFacilityId(old.getFacilityId());
            setPermitId(old.getPermitId());
            setEmissionsRptId(old.getEmissionsRptId());
            setRevenueId(old.getRevenueId());
            setRevenueTypeCd(old.getRevenueTypeCd());
            setOrigAmount(old.getOrigAmount());
            setCreationDate(old.getCreationDate());
            setDueDate(old.getDueDate());
            setFacilityName(old.getFacilityName());
            setFpId(old.getFpId());
            setDoLaaCd(old.getDoLaaCd());
            setInvoiceStateDesc(old.getInvoiceStateDesc());
            setCurrentAmount(old.getCurrentAmount());
            setEmissionsRptCd(old.getEmissionsRptCd());
            setBeginDt(old.getBeginDt());
            setEndDt(old.getEndDt());
            setPermitInvDocument(old.getPermitInvDocument());
            setReportInvDocument(old.getReportInvDocument());
        }
    }

    public final String getEmissionsRptCd() {
        return emissionsRptCd;
    }

    public final void setEmissionsRptCd(String emissionsRptCd) {
        this.emissionsRptCd = emissionsRptCd;
    }

    public final String getInvoiceStateDesc() {
        return invoiceStateDesc;
    }

    public final void setInvoiceStateDesc(String invoiceStateDesc) {
        this.invoiceStateDesc = invoiceStateDesc;
    }

    public final String getRevenueStateDesc() {
        return revenueStateDesc;
    }

    public void setRevenueStateDesc(String revenueStateDesc) {
        this.revenueStateDesc = revenueStateDesc;
    }

    public final Double getCurrentAmount() {
        return currentAmount;
    }

    public final void setCurrentAmount(Double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public final Date getBeginDt() {
        return beginDt;
    }

    public final void setBeginDt(Date beginDt) {
        this.beginDt = beginDt;
    }

    public final Date getEndDt() {
        return endDt;
    }

    public final void setEndDt(Date endDt) {
        this.endDt = endDt;
    }

    public PermitDocument getPermitInvDocument() {
		return permitInvDocument;
	}

	public void setPermitInvDocument(PermitDocument permitInvDocument) {
		this.permitInvDocument = permitInvDocument;
	}

	public EmissionsDocument getReportInvDocument() {
		return reportInvDocument;
	}

	public void setReportInvDocument(EmissionsDocument reportInvDocument) {
		this.reportInvDocument = reportInvDocument;
	}

	public final void populate(ResultSet rs) throws SQLException {
        try {
            setInvoiceId(AbstractDAO.getInteger(rs, "invoice_id"));
            setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setFacilityId(rs.getString("facility_id"));
            setRevenueId(AbstractDAO.getInteger(rs, "revenue_id"));
            setInvoiceStateCd(rs.getString("invoice_state_cd"));
            setCreationDate(rs.getTimestamp("creation_dt"));
            setDueDate(rs.getTimestamp("due_dt"));
            setRevenueTypeCd(rs.getString("revenue_type_cd"));
            setOrigAmount(AbstractDAO.getDouble(rs, "orig_amount"));
            setFacilityName(rs.getString("facility_nm"));
            setFpId(AbstractDAO.getInteger(rs, "fp_id"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setEmissionsRptCd(rs.getString("emissions_rpt_cd"));
            setInvoiceStateDesc(rs.getString("invoice_state_dsc"));
            if (AbstractDAO.getInteger(rs, "document_id") != null
                    && AbstractDAO.getInteger(rs, "permit_id") != null) {
                    permitInvDocument.populate(rs);
                }
                if (AbstractDAO.getInteger(rs, "document_id") != null
                    && AbstractDAO.getInteger(rs, "emissions_rpt_id") != null) {
                    reportInvDocument.populate(rs);
            }	
            setLastModified(AbstractDAO.getInteger(rs, "invoice_lm"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
            + ((doLaaCd == null) ? 0 : doLaaCd.hashCode());
        result = PRIME * result
            + ((invoiceId == null) ? 0 : invoiceId.hashCode());
        result = PRIME * result
            + ((facilityId == null) ? 0 : facilityId.hashCode());
        result = PRIME * result
            + ((facilityName == null) ? 0 : facilityName.hashCode());
        result = PRIME * result
            + ((invoiceStateCd == null) ? 0 : invoiceStateCd.hashCode());
        result = PRIME * result
            + ((origAmount == null) ? 0 : origAmount.hashCode());
        result = PRIME * result
            + ((currentAmount == null) ? 0 : currentAmount.hashCode());
        result = PRIME * result
            + ((emissionsRptCd == null) ? 0 : emissionsRptCd.hashCode());
        result = PRIME * result
            + ((invoiceStateDesc == null) ? 0 : invoiceStateDesc.hashCode());

        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InvoiceList other = (InvoiceList) obj;
        if (doLaaCd == null) {
            if (other.doLaaCd != null)
                return false;
        } else if (!doLaaCd.equals(other.doLaaCd))
            return false;
        if (emissionsRptCd == null) {
            if (other.emissionsRptCd != null)
                return false;
        } else if (!emissionsRptCd.equals(other.emissionsRptCd))
            return false;
        if (invoiceId == null) {
            if (other.invoiceId != null)
                return false;
        } else if (!invoiceId.equals(other.invoiceId))
            return false;
        if (invoiceStateDesc == null) {
            if (other.invoiceStateDesc != null)
                return false;
        } else if (!invoiceStateDesc.equals(other.invoiceStateDesc))
            return false;

        return true;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
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

    public Integer getFpId() {
        return fpId;
    }

    public void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Double getOrigAmount() {
        return origAmount;
    }

    public void setOrigAmount(Double origAmount) {
        this.origAmount = origAmount;
    }

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public Integer getRevenueId() {
        return revenueId;
    }

    public void setRevenueId(Integer revenueId) {
        this.revenueId = revenueId;
    }

    public String getRevenueTypeCd() {
        return revenueTypeCd;
    }

    public void setRevenueTypeCd(String revenueTypeCd) {
        this.revenueTypeCd = revenueTypeCd;
    }

    public String getInvoiceStateCd() {
        return invoiceStateCd;
    }

    public void setInvoiceStateCd(String invoiceStateCd) {
        this.invoiceStateCd = invoiceStateCd;
    }
    
    public boolean isPosted() {
        if(InvoiceState.hasBeenPosted(invoiceStateCd)) {
            return true;
        }
        return false;
    }

    public String getRevenueStateCd() {
        return revenueStateCd;
    }

    public void setRevenueStateCd(String revenueStateCd) {
        this.revenueStateCd = revenueStateCd;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Double getInvoiceDifference() {
        return invoiceDifference;
    }

    public void setInvoiceDifference(Double invoiceDifference) {
        this.invoiceDifference = invoiceDifference;
    }

    public Integer getPrevReport() {
        return prevReport;
    }

    public void setPrevReport(Integer prevReport) {
        this.prevReport = prevReport;
    }

    public Float getTotalTons() {
        return totalTons;
    }

    public void setTotalTons(Float totalTons) {
        this.totalTons = totalTons;
    }

    public boolean isIntraStateVoucherFlag() {
        return intraStateVoucherFlag;
    }

    public void setIntraStateVoucherFlag(boolean intraStateVoucherFlag) {
        this.intraStateVoucherFlag = intraStateVoucherFlag;
    }

    public String getReportYear() {
        return reportYear;
    }

    public void setReportYear(String reportYear) {
        this.reportYear = reportYear;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    public String getRevenueStateDesc2() {
        return revenueStateDesc2;
    }

    public void setRevenueStateDesc2(String revenueStateDesc2) {
        this.revenueStateDesc2 = revenueStateDesc2;
    }

    public Double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(Double originalAmount) {
        this.originalAmount = originalAmount;
    }
	
}
