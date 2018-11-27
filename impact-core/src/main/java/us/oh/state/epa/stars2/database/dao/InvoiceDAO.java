package us.oh.state.epa.stars2.database.dao;

import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceDetail;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceList;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceNote;
import us.oh.state.epa.stars2.database.dbObjects.invoice.MultiInvoiceList;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface InvoiceDAO extends TransactableDAO {
    /**
     * @param invoiceID
     * @return
     * @throws DAOException
     */
    Invoice retrieveInvoice(Integer invoiceID) throws DAOException;

    /**
     * @param facilityName
     * @param facilityId
     * @param invoiceId
     * @param countyId
     * @param year
     * @param emissionRptCd
     * @param invoiceStateCd
     * @param revenueTypeCd
     * @return
     * @throws DAOException
     */
    InvoiceList[] searchInvoices(InvoiceList searchObj) throws DAOException;

    /**
     * @param newInv
     * @return
     * @throws DAOException
     */
    Invoice createInvoice(Invoice newInv) throws DAOException;

    /**
     * @param inv
     * @return
     * @throws DAOException
     */
    boolean modifyInvoice(Invoice inv) throws DAOException;

    /**
     * @param newInvDtl
     * @return
     * @throws DAOException
     */
    InvoiceDetail createInvoiceDetail(InvoiceDetail newInvDtl)
            throws DAOException;
    
    /**
     * @param inv
     * @throws DAOException
     */
    void deleteInvoice(Invoice inv) throws DAOException;

    /**
     * 
     * @param permitID
     * @return
     * @throws DAOException
     */
    Invoice retrieveInvoiceByPermitID(Integer permitID) throws DAOException;
    
    /**
     * @param invoice 
     * @return MultiInvoiceList[] 
     * @throws DAOException
     */
    MultiInvoiceList[] retrieveOtherInvoices(Integer reveunueId, Integer invoiceID)
            throws DAOException;
    
    /**
     * @param inv
     * @return
     * @throws DAOException
     */
    boolean modifyInvoiceDocument(Invoice inv) throws DAOException;
    
    /**
     * @param reportID
     * @return
     * @throws DAOException
     */
    Invoice retrieveInvoiceByReportID(Integer reportID) throws DAOException;
    
    /**
     * @param invoiceId
     * @return
     * @throws DAOException
     */
     void removeDetailLines(int invoiceId) throws DAOException;
    
    /**
     * @param invDtl
     * @return
     * @throws DAOException
     */
    boolean modifyInvoiceDetail(InvoiceDetail invDtl) throws DAOException;    
 
    /**
     * @param invoiceNote
     * @throws DAOException
     */
    void addInvoiceNote(InvoiceNote note) throws DAOException;
    
    /**
     * @param invoiceID
     * @return
     * @throws DAOException
     */
    InvoiceNote[] retrieveInvoiceNotes(Integer invoiceID) throws DAOException;   

}
