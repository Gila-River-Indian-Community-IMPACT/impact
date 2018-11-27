package us.oh.state.epa.stars2.database.dao;

import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.ceta.Complaint;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface ComplaintDAO extends TransactableDAO {
	
	Complaint createComplaint(Complaint complaint) throws DAOException;
	Complaint retrieveComplaint(int complaintId) throws DAOException;
	boolean modifyComplaint(Complaint complaint) throws DAOException;
	void deleteComplaint(int complaintId) throws DAOException;
	List<Complaint> retrieveComplaintByDoLaa(String doLaaCd) throws DAOException;
    Complaint retrieveComplaint(String doLaaCd, String year, String month) throws DAOException;
}
