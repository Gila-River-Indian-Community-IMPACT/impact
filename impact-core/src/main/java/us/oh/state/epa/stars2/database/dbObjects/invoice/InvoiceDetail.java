package us.oh.state.epa.stars2.database.dbObjects.invoice;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.NonToxicPollutantDef;

public class InvoiceDetail extends BaseDB {

    private Integer invoiceDetailId;
    private Integer invoiceId;
    private String description;
    private transient String printableDesc;
    private Float amount;
    private Float difference;
	
    public InvoiceDetail() {
        super();
    }

    public InvoiceDetail(InvoiceDetail old) {
        super(old);

        if (old != null) {
            setInvoiceDetailId(old.getInvoiceDetailId());
            setInvoiceId(old.getInvoiceId());
            setDescription(old.getDescription());
            setAmount(old.getAmount());
        }
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((amount == null) ? 0 : amount.hashCode());
        result = PRIME * result
            + ((description == null) ? 0 : description.hashCode());
        result = PRIME * result
            + ((invoiceDetailId == null) ? 0 : invoiceDetailId.hashCode());
        result = PRIME * result
            + ((invoiceId == null) ? 0 : invoiceId.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InvoiceDetail other = (InvoiceDetail) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (invoiceDetailId == null) {
            if (other.invoiceDetailId != null)
                return false;
        } else if (!invoiceDetailId.equals(other.invoiceDetailId))
            return false;
        if (invoiceId == null) {
            if (other.invoiceId != null)
                return false;
        } else if (!invoiceId.equals(other.invoiceId))
            return false;
        return true;
    }

    public final Float getAmount() {
        return amount;
    }

    public final void setAmount(Float amount) {
        this.amount = amount;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final Integer getInvoiceDetailId() {
        return invoiceDetailId;
    }

    public final void setInvoiceDetailId(Integer invoiceDetailId) {
        this.invoiceDetailId = invoiceDetailId;
    }

    public final Integer getInvoiceId() {
        return invoiceId;
    }

    public final void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Float getDifference() {
        return difference;
    }

    public void setDifference(Float difference) {
        this.difference = difference;
    }

    public final void populate(ResultSet rs) throws SQLException {
        try {
            setInvoiceDetailId(AbstractDAO.getInteger(rs, "invoice_detail_id"));
            setInvoiceId(AbstractDAO.getInteger(rs, "invoice_id"));
            setPrintableDesc(NonToxicPollutantDef.getData().getItems().getItemShortDesc(rs.getString("invoice_dtl_desc")));
            setDescription(rs.getString("invoice_dtl_desc"));
            setAmount(AbstractDAO.getFloat(rs, "amount"));
            setLastModified(AbstractDAO.getInteger(rs, "invoice_dtl_lm"));
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public String getPrintableDesc() {
        return printableDesc;
    }

    public void setPrintableDesc(String printableDesc) {
        this.printableDesc = printableDesc;
    }
}
