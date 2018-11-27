package us.oh.state.epa.stars2.database.dbObjects.invoice;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;

public class InvoiceNote extends Note{

    private Integer invoiceId;
	
    public InvoiceNote(){
        super();
    }
	
    public InvoiceNote(InvoiceNote old){
        super(old);
		
        if(old !=null){
            setInvoiceId(old.getInvoiceId());
        }
    }

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }
	
    public final void populate(ResultSet rs) {
        try {
            setInvoiceId(AbstractDAO.getInteger(rs, "invoice_id"));
            super.populate(rs);
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    @Override
        public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result
            + ((invoiceId == null) ? 0 : invoiceId.hashCode());
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
        final InvoiceNote other = (InvoiceNote) obj;
        if (invoiceId == null) {
            if (other.invoiceId != null)
                return false;
        } else if (!invoiceId.equals(other.invoiceId))
            return false;
        return true;
    }
}
