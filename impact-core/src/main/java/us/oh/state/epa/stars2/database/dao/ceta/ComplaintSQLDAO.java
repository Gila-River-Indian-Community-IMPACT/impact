package us.oh.state.epa.stars2.database.dao.ceta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ComplaintDAO;
import us.oh.state.epa.stars2.database.dbObjects.ceta.Complaint;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class ComplaintSQLDAO extends AbstractDAO implements ComplaintDAO {
	
	private Logger logger = Logger.getLogger(ComplaintSQLDAO.class);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public Complaint createComplaint(Complaint complaint) throws DAOException {
		  checkNull(complaint);
	         
		  complaint.setComplaintId(nextSequenceVal("CE_complaint_id", complaint.getComplaintId()));
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "ComplaintSQL.createComplaint", false);
	        int i = 1;
	        connHandler.setInteger(i++, complaint.getComplaintId());
	        connHandler.setString(i++, complaint.getDoLaaCd());
	        connHandler.setString(i++, complaint.getYear());
	        connHandler.setString(i++, complaint.getMonth());
	        connHandler.setInteger(i++, complaint.getHighPriority());
	        connHandler.setInteger(i++, complaint.getNonHighPriority());
	        connHandler.setInteger(i++, complaint.getOther());
	        connHandler.setInteger(i++, complaint.getOpenBurning());
	        connHandler.setInteger(i++, complaint.getAntiTamperingInspections());
	        connHandler.setInteger(i++, complaint.getAsbestos());
	        connHandler.setInteger(i++, complaint.getAsbestosNonNotifier());
	        //connHandler.setInteger(i++, complaint.getTotal());
	        connHandler.update();

	        // If we get here the INSERT must have succeeded, so set the important
	        // data and return the object.
	        complaint.setLastModified(1);

		return complaint;
	}

	
	private final Timestamp makeDateFromString(String dateStr) 
	{
		  Timestamp ts = null;
		  try {
		   ts = new Timestamp(dateFormat.parse(dateStr).getTime());
		  } catch (ParseException e) {
		   logger.error("Failed parsing date string " + dateStr);
		  }
		  return ts;
		 }

	public void deleteComplaint(int complaintId) throws DAOException {
		 checkNull(complaintId);
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "ComplaintSQL.deleteComplaint", false);
	        connHandler.setInteger(1, complaintId);
	        connHandler.remove();

	}

	public boolean modifyComplaint(Complaint complaint) throws DAOException {
		  checkNull(complaint);	        
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "ComplaintSQL.modifyComplaint", false);
	        int i = 1;
	        connHandler.setString(i++, complaint.getDoLaaCd());
	        connHandler.setString(i++, complaint.getYear());
	        connHandler.setString(i++, complaint.getMonth());
	        connHandler.setInteger(i++, complaint.getHighPriority());
	        connHandler.setInteger(i++, complaint.getNonHighPriority());
	        connHandler.setInteger(i++, complaint.getOther());
	        connHandler.setInteger(i++, complaint.getOpenBurning());
	        connHandler.setInteger(i++, complaint.getAntiTamperingInspections());
	        connHandler.setInteger(i++, complaint.getAsbestos());
	        connHandler.setInteger(i++, complaint.getAsbestosNonNotifier());
	        connHandler.setInteger(i++, complaint.getLastModified() + 1);        
	        connHandler.setInteger(i++, complaint.getComplaintId());
	        connHandler.setInteger(i++, complaint.getLastModified());
		return connHandler.update();
	}
	
	public Complaint retrieveComplaint(int complaintId) throws DAOException {
		 ConnectionHandler connHandler = new ConnectionHandler(
	                "ComplaintSQL.retrieveComplaint", true);
	        connHandler.setInteger(1, complaintId);
	        return (Complaint) connHandler
	                .retrieve(Complaint.class);
	}

    
    public Complaint retrieveComplaint(String doLaaCd, String year, String month) throws DAOException {
         ConnectionHandler connHandler = new ConnectionHandler(
                    "ComplaintSQL.retrieveComplaint3Arg", true);
            connHandler.setString(1, doLaaCd);
            connHandler.setString(2, year);
            connHandler.setString(3, month);
            return (Complaint) connHandler
            .retrieve(Complaint.class);
    }

	public List<Complaint> retrieveComplaintByDoLaa(String doLaaCd) throws DAOException {
	List<Complaint> results = new ArrayList<Complaint>();
	Connection conn = null;
	PreparedStatement pStmt = null;

	try {
		Integer lastId = null;
		Complaint complaint = null;
        conn = getReadOnlyConnection();
        pStmt = conn.prepareStatement(loadSQL("ComplaintSQL.retrieveComplaintByDoLaa"));
        pStmt.setString(1, doLaaCd);
        ResultSet rs = pStmt.executeQuery();
        
		while (rs.next()) {
			Integer complaintId = AbstractDAO.getInteger(rs, "complaint_id");
			if (complaintId != null && !complaintId.equals(lastId)) {
				complaint = new Complaint();
				complaint.populate(rs);
            	lastId = complaintId;
            	results.add(complaint);
			}			
			
        }
        rs.close();
	} catch (Exception e) {
		handleException(e, conn);
	} finally {
		closeStatement(pStmt);
		handleClosing(conn);
	}
	return results;
	}

	
}

