package us.oh.state.epa.stars2.database.dbObjects.invoice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.InvoiceState;

public class MultiInvoiceList extends BaseDB {

    private Integer invoiceId;
    private String invoiceStateCd;
    private Double origAmount;
    private Integer permitId;
    private Integer emissionsRptId;
    private String emissionsRptCd;
    private Timestamp creationDate;

    public MultiInvoiceList() {
        super();
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getInvoiceStateCd() {
        String ret = "";

        if (invoiceStateCd != null) {
            ret = InvoiceState.getData().getItems().getItemDesc(invoiceStateCd);
        }

        return ret;
    }

    public void setInvoiceStateCd(String invoiceStateCd) {
        this.invoiceStateCd = invoiceStateCd;
    }

    public Double getOrigAmount() {
        return origAmount;
    }

    public void setOrigAmount(Double origAmount) {
        this.origAmount = origAmount;
    }

    public void populate(ResultSet rs) {
        try {
            setInvoiceId(AbstractDAO.getInteger(rs, "invoice_id"));
            setInvoiceStateCd(rs.getString("invoice_state_cd"));
            setOrigAmount(AbstractDAO.getDouble(rs, "orig_amount"));
            setEmissionsRptId(AbstractDAO.getInteger(rs, "emissions_rpt_id"));
            setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
            setCreationDate(rs.getTimestamp("creation_dt"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getEmissionsRptId() {
        return emissionsRptId;
    }

    public void setEmissionsRptId(Integer emissionsRptId) {
        this.emissionsRptId = emissionsRptId;
    }

    public Integer getPermitId() {
        return permitId;
    }

    public void setPermitId(Integer permitId) {
        this.permitId = permitId;
    }

    public String getEmissionsRptCd() {
        return emissionsRptCd;
    }

    public void setEmissionsRptCd(String emissionsRptCd) {
        this.emissionsRptCd = emissionsRptCd;
    }
}
