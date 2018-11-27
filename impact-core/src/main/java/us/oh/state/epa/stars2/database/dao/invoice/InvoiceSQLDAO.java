package us.oh.state.epa.stars2.database.dao.invoice;

import java.util.ArrayList;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.InvoiceDAO;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceDetail;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceList;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceNote;
import us.oh.state.epa.stars2.database.dbObjects.invoice.MultiInvoiceList;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class InvoiceSQLDAO extends AbstractDAO implements InvoiceDAO {
	/**
	 * @see InvoiceDAO#createInvoice(Invoice)
	 */
	public final Invoice createInvoice(Invoice newInv) throws DAOException {
		checkNull(newInv);
		checkNull(newInv.getContact());		
		Integer id = nextSequenceVal("S_Invoice_Id");
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"InvoiceSQL.createInvoice", false);

		connHandler.setInteger(1, id);
		connHandler.setInteger(2, newInv.getPermitId());
		connHandler.setInteger(3, newInv.getEmissionsRptId());
		connHandler.setString(4, newInv.getInvoiceStateCd());
		connHandler.setInteger(5, newInv.getRevenueId());
		connHandler.setString(6, newInv.getRevenueTypeCd());
		connHandler.setTimestamp(7, newInv.getCreationDate());
		connHandler.setDouble(8, newInv.getOrigAmount());
		connHandler.setString(9, newInv.getFacilityId());
		connHandler.setTimestamp(10, newInv.getDueDate());
        connHandler.setInteger(11, newInv.getAdjustmentNum());
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		Invoice ret = newInv;
		ret.setInvoiceId(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see InvoiceDAO#createInvoiceDetail(InvoiceDetail)
	 */
	public final InvoiceDetail createInvoiceDetail(InvoiceDetail newInvDtl)
			throws DAOException {
		checkNull(newInvDtl);
		InvoiceDetail ret = newInvDtl;

		Integer id = nextSequenceVal("S_Invoice_Detail_Id");

		ConnectionHandler connHandler = new ConnectionHandler(
				"InvoiceSQL.createInvoiceDetail", false);

		connHandler.setInteger(1, id);
		connHandler.setInteger(2, newInvDtl.getInvoiceId());
		connHandler.setString(3, newInvDtl.getDescription());
		connHandler.setFloat(4, newInvDtl.getAmount());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setInvoiceDetailId(id);
		ret.setLastModified(1);

		return ret;
	}
    
    public final void deleteInvoice(Invoice inv) throws DAOException {
        checkNull(inv);
        if(!inv.getInvoiceStateCd().equals(InvoiceState.READY_TO_INVOICE)) {
            throw new DAOException("Attempt to delete invoice " + inv.getInvoiceId() +
                    ", but not in state READY_TO_INVOICE");
        }
        removeRows("iv_invoice_detail", "invoice_id", inv.getInvoiceId());
        removeRows("iv_invoice", "invoice_id", inv.getInvoiceId());
    }

	public final boolean modifyInvoice(Invoice inv) throws DAOException {
		boolean ret = false;
		checkNull(inv);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InvoiceSQL.modifyInvoice", false);

		connHandler.setDouble(1, inv.getOrigAmount());
		connHandler.setString(2, inv.getInvoiceStateCd());
		connHandler.setInteger(3, inv.getRevenueId());
		if (inv.getPermitInvDocument() != null) {
			connHandler.setInteger(4, inv.getPermitInvDocument().getDocumentID());
		} else if(inv.getReportInvDocument() != null){
			connHandler.setInteger(4, inv.getReportInvDocument().getDocumentID());
		} else{
			connHandler.setInteger(4, null);
		}
		connHandler.setTimestamp(5, inv.getDueDate());
        connHandler.setInteger(6, inv.getAdjustmentNum());
		connHandler.setInteger(7, inv.getLastModified() + 1);
		connHandler.setInteger(8, inv.getInvoiceId());
		connHandler.setInteger(9, inv.getLastModified());

		connHandler.update();
		ret = true;

		return ret;
	}
    
    /**
     * @see InvoiceDAO#removeDetailLines(int)
     */
    public void removeDetailLines(int invoiceId) throws DAOException {
        removeRows("iv_invoice_detail", "invoice_id", invoiceId);
        return;
    }

	public final boolean modifyInvoiceDetail(InvoiceDetail invDtl)
			throws DAOException {
		boolean ret = false;
		checkNull(invDtl);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InvoiceSQL.modifyInvoiceDetail", false);

		connHandler.setFloat(1, invDtl.getAmount());
		connHandler.setInteger(2, invDtl.getLastModified() + 1);
		connHandler.setInteger(3, invDtl.getInvoiceDetailId());
		connHandler.setInteger(4, invDtl.getLastModified());

		connHandler.update();
		ret = true;

		return ret;
	}

	/**
	 * 
	 */
	public final void addInvoiceNote(InvoiceNote newNote) throws DAOException{
		checkNull(newNote);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"InvoiceSQL.addInvoiceNote", false);
		
		connHandler.setInteger(1, newNote.getInvoiceId());
		connHandler.setInteger(2, newNote.getNoteId());
		
		connHandler.update();
		
		return;
	}
	
	/**
	 * 
	 */
	public final InvoiceNote[] retrieveInvoiceNotes(Integer invoiceID) throws DAOException{
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"InvoiceSQL.retrieveInvoiceNotes", false);
		
		connHandler.setInteger(1, invoiceID);
		
		ArrayList<InvoiceNote> ret = connHandler.retrieveArray(InvoiceNote.class);
		return ret.toArray(new InvoiceNote[0]);
	}
	
	/**
	 * @see InvoiceDAO#retrieveInvoice(int)
	 */
	public final Invoice retrieveInvoice(Integer invoiceID) throws DAOException {
		StringBuffer statementSQL = new StringBuffer(
				loadSQL("InvoiceSQL.retrieveInvoice"));
		statementSQL.append(" AND ii.invoice_id = ?");

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL.toString());

		connHandler.setInteger(1, invoiceID);

		return (Invoice) connHandler.retrieve(Invoice.class);
	}

	/**
	 * @see InvoiceDAO#retrieveInvoiceByPermit(int)
	 */
	public final Invoice retrieveInvoiceByPermitID(Integer permitID)
			throws DAOException {

		StringBuffer statementSQL = new StringBuffer(
				loadSQL("InvoiceSQL.retrieveInvoiceByPermitID"));
		statementSQL.append(" AND ii.PERMIT_ID = ?");

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL.toString());

		connHandler.setInteger(1, permitID);

		return (Invoice) connHandler.retrieve(Invoice.class);
	}

	/**
	 * @see InvoiceDAO#searchInvoices(InvoiceList)
	 */
	public final InvoiceList[] searchInvoices(InvoiceList searchObj)
			throws DAOException {

            checkNull(searchObj);
            
            if (searchObj.isUnlimitedResults()) {
                setDefaultSearchLimit(-1);
            }
        
		StringBuffer statementSQL = new StringBuffer(
				loadSQL("InvoiceSQL.searchInvoices"));

		if (searchObj.getFacilityName() != null
				&& searchObj.getFacilityName().trim().length() > 0) {
			statementSQL.append(" AND LOWER(facility_nm) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(searchObj.getFacilityName()
					.replace("*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getFacilityId() != null
				&& searchObj.getFacilityId().trim().length() > 0) {
			statementSQL.append(" AND LOWER(ff.facility_id) LIKE ");
			statementSQL.append("LOWER('");
			statementSQL.append(SQLizeString(formatFacilityId(searchObj.getFacilityId()).replace(
					"*", "%")));
			statementSQL.append("')");
		}
		if (searchObj.getInvoiceId() != null) {
			statementSQL.append(" AND ii.invoice_id = "
					+ searchObj.getInvoiceId());
		}
		if (searchObj.getRevenueId() != null) {
			statementSQL.append(" AND ii.revenue_id = "
					+ searchObj.getRevenueId());
		}
		if (searchObj.getDoLaaCd() != null) {
			statementSQL.append(" AND ff.do_laa_cd = '"
					+ searchObj.getDoLaaCd() + "'");
		}
		if (searchObj.getInvoiceStateCd() != null) {
			statementSQL.append(" AND ii.invoice_state_cd = '"
					+ searchObj.getInvoiceStateCd() + "'");
		}
		if (searchObj.getRevenueTypeCd() != null) {
			statementSQL.append(" AND ii.revenue_type_cd = '"
					+ searchObj.getRevenueTypeCd() + "'");
		}

		statementSQL.append(" ORDER BY ff.facility_id");

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL.toString());

		ArrayList<InvoiceList> ret = connHandler
				.retrieveArray(InvoiceList.class, defaultSearchLimit);

		return ret.toArray(new InvoiceList[0]);
	}

	/**
	 * @see InvoiceDAO#modifyInvoiceDocument(Invoice)
	 */
	public final boolean modifyInvoiceDocument(Invoice inv) throws DAOException {
		checkNull(inv);

		ConnectionHandler connHandler = new ConnectionHandler(
				"InvoiceSQL.modifyInvoiceDocument", false);

		if (inv.getPermitId() != null) {
			connHandler.setInteger(1, inv.getPermitInvDocument()
					.getDocumentID());
		} else {
			connHandler.setInteger(1, inv.getReportInvDocument()
					.getDocumentID());
		}
		connHandler.setInteger(2, inv.getLastModified() + 1);
		connHandler.setInteger(3, inv.getInvoiceId());
		connHandler.setInteger(4, inv.getLastModified());
		return connHandler.update();
	}

	public final MultiInvoiceList[] retrieveOtherInvoices(Integer revenueID,
			Integer invoiceID) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"InvoiceSQL.retrieveOtherInvoices", true);

		connHandler.setInteger(1, revenueID);
		connHandler.setInteger(2, invoiceID);

		ArrayList<MultiInvoiceList> ret = connHandler
				.retrieveArray(MultiInvoiceList.class);

		return ret.toArray(new MultiInvoiceList[0]);
	}

	public final Invoice retrieveInvoiceByReportID(Integer reportId)
			throws DAOException {
		StringBuffer statementSQL = new StringBuffer(
				loadSQL("InvoiceSQL.retrieveInvoice"));
		statementSQL.append(" AND ii.EMISSIONS_RPT_ID = ?");

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(statementSQL.toString());

		connHandler.setInteger(1, reportId);

		return (Invoice) connHandler.retrieve(Invoice.class);
	}
}
