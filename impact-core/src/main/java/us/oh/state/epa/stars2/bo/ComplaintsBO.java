package us.oh.state.epa.stars2.bo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.ComplaintDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Complaint;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Transactional(rollbackFor=Exception.class)
@Service
public class ComplaintsBO extends BaseBO implements ComplaintsService {
	/**
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
	 */
	public List<Complaint> retrieveComplaints(String doLaaCd) throws DAOException {
		List<Complaint> results = complaintDAO().retrieveComplaintByDoLaa(doLaaCd);
		if (results == null) {
			results = new ArrayList<Complaint>();
		}
		return results;
	}
	/**
	 * 
	 * @param complaintId
	 * @return
	 * @throws DAOException
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
	 */
	public Complaint retrieveComplaint(int complaintId) throws DAOException {
		Complaint ret = null;
		try {
		ret = complaintDAO().retrieveComplaint(complaintId);
		} catch(DAOException de) {
            logger.error("Complaint Id=" + complaintId + " " + de.getMessage(), de);
            throw de;
        }
		return ret;
	}
	/**
	 * 
	 * @param doLaaCd
	 * @param year
	 * @param month
	 * @return
	 * @throws DAOException
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
	 */
	public Complaint retrieveComplaint(String doLaaCd, String year, String month) throws DAOException {
		Complaint result = null;
        Transaction trans = null;
        try {
            trans = TransactionFactory.createTransaction(); 
            ComplaintDAO complaintDAO = complaintDAO(trans); 
            result = complaintDAO.retrieveComplaint(doLaaCd, year, month);
        } catch (DAOException e) {
            cancelTransaction(trans, e);
        } finally {
            closeTransaction(trans);
        }
		return result;
	}
	
	/**
	 * 
	 * @param complaint
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public Complaint createComplaint(Complaint complaint) throws DAOException {
//		if (complaint == null || complaint.getDoLaaCd() == null) {
//			throw new DAOException("Attempt to create an invalid complaint");
//		}
//		List<Complaint> complaints = complaintsMap.get(complaint.getDoLaaCd());
//		if (complaints == null) {
//			complaints = new ArrayList<Complaint>();
//			complaintsMap.put(complaint.getDoLaaCd(), complaints);
//		}
//		complaint.setComplaintId(complaintId++);
//		complaints.add(complaint);
//		return complaint;
		
		Transaction trans = null;
        try {
        	trans = TransactionFactory.createTransaction();	
        	ComplaintDAO complaintDAO = complaintDAO(trans); 
        	complaint = complaintDAO.createComplaint(complaint);
        } catch (DAOException e) {
        	cancelTransaction(trans, e);
        } finally {
        	closeTransaction(trans);
        }
		return complaint;
	}
	
	/**
	 * 
	 * @param complaint
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public void modifyComplaint(Complaint complaint) throws DAOException {
//		if (complaint == null || complaint.getDoLaaCd() == null) {
//			throw new DAOException("Attempt to modify an invalid complaint");
//		}
//		List<Complaint> complaints = complaintsMap.get(complaint.getDoLaaCd());
//		if (complaints == null) {
//			throw new DAOException("Attempt to modify an invalid complaint");
//		}
//		for (Complaint match : complaints) {
//			if (match.getComplaintId().equals(complaint.getComplaintId())) {
//				match.copy(complaint);
//				break;
//			}
//		}
		
		 Transaction trans = null;
	        try {
	        	trans = TransactionFactory.createTransaction();		
	        	ComplaintDAO complaintDAO = complaintDAO(trans);
	        	complaintDAO.modifyComplaint(complaint);
	        	
	        } catch (DAOException e) {
	        	cancelTransaction(trans, e);
	        } finally {
	        	closeTransaction(trans);
	        }
	        
	        
	}
	
	/**
	 * 
	 * @param complaint
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public void removeComplaint(Complaint complaint) throws DAOException {
//		if (complaint == null || complaint.getDoLaaCd() == null) {
//			throw new DAOException("Attempt to remove an invalid complaint");
//		}
//		List<Complaint> complaints = complaintsMap.get(complaint.getDoLaaCd());
//		if (complaints == null) {
//			throw new DAOException("Attempt to remove an invalid complaint");
//		}
//		complaints.remove(complaint);
		
		
		 Transaction trans = null;
	        try {
	        	trans = TransactionFactory.createTransaction();	
	        	complaintDAO().deleteComplaint(complaint.getComplaintId());
	        } catch (DAOException e) {
	        	cancelTransaction(trans, e);
	        } finally {
	        	closeTransaction(trans);
	        }
		
	}

}
