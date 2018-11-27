package us.oh.state.epa.stars2.database.dao.ceta;

import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.CompEnfFacilityDAO;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityComplianceStatus;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FacilityHistory;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class CompEnfFacilitySQLDAO extends AbstractDAO implements
		CompEnfFacilityDAO {

	
	public FacilityComplianceStatus createFacHistNeshaps(
			FacilityComplianceStatus status) throws DAOException {
		checkNull(status);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.createFacHistNeshaps", false);

        int i = 1;
        connHandler.setInteger(i++, status.getFacilityHistoryId());
        connHandler.setString(i++, status.getPollutantCd());
        connHandler.setString(i++, status.getComplianceCd());
        connHandler.update();   
        
        return status;
	}

	
	public FacilityComplianceStatus createFacHistNsps(
			FacilityComplianceStatus status) throws DAOException {
		checkNull(status);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.createFacHistNsps", false);

        int i = 1;
        connHandler.setInteger(i++, status.getFacilityHistoryId());
        connHandler.setString(i++, status.getPollutantCd());
        connHandler.setString(i++, status.getComplianceCd());
        connHandler.update();   
        
        return status;
	}

	
	public FacilityComplianceStatus createFacHistNsr(
			FacilityComplianceStatus status) throws DAOException {
		checkNull(status);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.createFacHistNsr", false);

        int i = 1;
        connHandler.setInteger(i++, status.getFacilityHistoryId());
        connHandler.setString(i++, status.getPollutantCd());
        connHandler.setString(i++, status.getComplianceCd());
        connHandler.update();   
        
        return status;
	}

	
	public FacilityComplianceStatus createFacHistPsd(
			FacilityComplianceStatus status) throws DAOException {
		checkNull(status);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.createFacHistPsd", false);

        int i = 1;
        connHandler.setInteger(i++, status.getFacilityHistoryId());
        connHandler.setString(i++, status.getPollutantCd());
        connHandler.setString(i++, status.getComplianceCd());
        connHandler.update();   
        
        return status;
	}

	
	public FacilityHistory createFacilityHistory(FacilityHistory facilityHistory)
			throws DAOException {
        checkNull(facilityHistory);
        
        facilityHistory.setFacilityHistId(nextSequenceVal("CE_facility_hist_id", 
        		facilityHistory.getFacilityHistId()));

        ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.createFacilityHistory", false);

        int i = 1;
        connHandler.setInteger(i++, facilityHistory.getFacilityHistId());
        connHandler.setString(i++, facilityHistory.getAirProgramCd());
        connHandler.setString(i++, facilityHistory.getAirProgramCompCd());
        connHandler.setString(i++, facilityHistory.getSipCompCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isMact()));
        connHandler.setString(i++, facilityHistory.getMactCompCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isNeshaps()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isNsps()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isPsd()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isNsrNonAttainment()));
        connHandler.setTimestamp(i++, facilityHistory.getStartDate());
        connHandler.setTimestamp(i++, facilityHistory.getEndDate());

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        facilityHistory.setLastModified(1);

        return facilityHistory;
	}

	
	public void deleteFacHistNeshaps(int facilityHistoryId) throws DAOException {
		checkNull(facilityHistoryId);
	    ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.deleteFacHistNeshaps", false);
        connHandler.setInteger(1, facilityHistoryId);
        connHandler.remove();

	}

	
	public void deleteFacHistNsps(int facilityHistoryId) throws DAOException {
		checkNull(facilityHistoryId);
	    ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.deleteFacHistNsps", false);
        connHandler.setInteger(1, facilityHistoryId);
        connHandler.remove();

	}

	
	public void deleteFacHistNsr(int facilityHistoryId) throws DAOException {
		checkNull(facilityHistoryId);
	    ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.deleteFacHistNsr", false);
        connHandler.setInteger(1, facilityHistoryId);
        connHandler.remove();

	}

	
	public void deleteFacHistPsd(int facilityHistoryId) throws DAOException {
		checkNull(facilityHistoryId);
	    ConnectionHandler connHandler = new ConnectionHandler(
                "CompEnfFacilitySQL.deleteFacHistPsd", false);
        connHandler.setInteger(1, facilityHistoryId);
        connHandler.remove();

	}

	
	public void deleteFacilityHistory(int facilityHistoryId)
			throws DAOException {
		 checkNull(facilityHistoryId);
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "CompEnfFacilitySQL.deleteFacilityHistory", false);
	        connHandler.setInteger(1, facilityHistoryId);
	        connHandler.remove();
	}

	
	public boolean modifyFacHistNeshaps(FacilityComplianceStatus status)
			throws DAOException {
		checkNull(status);

        ConnectionHandler connHandler = new ConnectionHandler(
        		"CompEnfFacilitySQL.modifyFacHistNeshaps", false);

        int i = 1;   
        
        connHandler.setString(i++, status.getPollutantCd());
        connHandler.setString(i++, status.getComplianceCd());
        connHandler.setInteger(i++, status.getLastModified() + 1);
        connHandler.setInteger(i++, status.getFacilityHistoryId());
        connHandler.setInteger(i++, status.getLastModified());
       
        return connHandler.update();
		
	}

	
	public boolean modifyFacHistNsps(FacilityComplianceStatus status)
			throws DAOException {
		checkNull(status);

        ConnectionHandler connHandler = new ConnectionHandler(
        		"CompEnfFacilitySQL.modifyFacHistNsps", false);

        int i = 1;   
        
        connHandler.setString(i++, status.getPollutantCd());
        connHandler.setString(i++, status.getComplianceCd());
        connHandler.setInteger(i++, status.getLastModified() + 1);
        connHandler.setInteger(i++, status.getFacilityHistoryId());
        connHandler.setInteger(i++, status.getLastModified());
       
        return connHandler.update();
	}

	
	public boolean modifyFacHistNsr(FacilityComplianceStatus status)
			throws DAOException {
		checkNull(status);

        ConnectionHandler connHandler = new ConnectionHandler(
        		"CompEnfFacilitySQL.modifyFacHistNsr", false);

        int i = 1;   
        
        connHandler.setString(i++, status.getPollutantCd());
        connHandler.setString(i++, status.getComplianceCd());
        connHandler.setInteger(i++, status.getLastModified() + 1);
        connHandler.setInteger(i++, status.getFacilityHistoryId());
        connHandler.setInteger(i++, status.getLastModified());
       
        return connHandler.update();
	}

	
	public boolean modifyFacHistPsd(FacilityComplianceStatus status)
			throws DAOException {
		checkNull(status);

        ConnectionHandler connHandler = new ConnectionHandler(
        		"CompEnfFacilitySQL.modifyFacHistPsd", false);

        int i = 1;   
        
        connHandler.setString(i++, status.getPollutantCd());
        connHandler.setString(i++, status.getComplianceCd());
        connHandler.setInteger(i++, status.getLastModified() + 1);
        connHandler.setInteger(i++, status.getFacilityHistoryId());
        connHandler.setInteger(i++, status.getLastModified());
       
        return connHandler.update();
	}

	
	public boolean modifyFacilityHistory(FacilityHistory facilityHistory)
			throws DAOException {
		checkNull(facilityHistory);

        ConnectionHandler connHandler = new ConnectionHandler(
        		"CompEnfFacilitySQL.modifyFacilityHistory", false);

        int i = 1;   
       
        connHandler.setString(i++, facilityHistory.getAirProgramCd());
        connHandler.setString(i++, facilityHistory.getAirProgramCompCd());
        connHandler.setString(i++, facilityHistory.getSipCompCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isMact()));
        connHandler.setString(i++, facilityHistory.getMactCompCd());
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isNeshaps()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isNsps()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isPsd()));
        connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(facilityHistory.isNsrNonAttainment()));
        connHandler.setTimestamp(i++, facilityHistory.getStartDate());
        connHandler.setTimestamp(i++, facilityHistory.getEndDate()); 
        connHandler.setInteger(i++, facilityHistory.getLastModified() + 1);
        connHandler.setInteger(i++, facilityHistory.getFacilityHistId());
        connHandler.setInteger(i++, facilityHistory.getLastModified());
       
        return connHandler.update();
	}

	
	public List<FacilityComplianceStatus> retrieveFacHistNeshaps(
			int facilityHistoryId) throws DAOException {
		 ConnectionHandler connHandler = new ConnectionHandler(
	                "CompEnfFacilitySQL.retrieveFacHistNeshaps", true);
	        connHandler.setInteger(1, facilityHistoryId);
	        return connHandler.retrieveArray(FacilityComplianceStatus.class);
	              
	       
	}

	
	public List<FacilityComplianceStatus> retrieveFacHistNsps(
			int facilityHistoryId) throws DAOException {
		 ConnectionHandler connHandler = new ConnectionHandler(
	                "CompEnfFacilitySQL.retrieveFacHistNsps", true);
	        connHandler.setInteger(1, facilityHistoryId);
	        return connHandler.retrieveArray(FacilityComplianceStatus.class);
	}

	
	public List<FacilityComplianceStatus> retrieveFacHistNsr(
			int facilityHistoryId) throws DAOException {
		 ConnectionHandler connHandler = new ConnectionHandler(
	                "CompEnfFacilitySQL.retrieveFacHistNsr", true);
	        connHandler.setInteger(1, facilityHistoryId);
	        return connHandler.retrieveArray(FacilityComplianceStatus.class);
	}

	
	public List<FacilityComplianceStatus> retrieveFacHistPsd(
			int facilityHistoryId) throws DAOException {
		 ConnectionHandler connHandler = new ConnectionHandler(
	                "CompEnfFacilitySQL.retrieveFacHistPsd", true);
	        connHandler.setInteger(1, facilityHistoryId);
	        return connHandler.retrieveArray(FacilityComplianceStatus.class);
	}

	
	public FacilityHistory retrieveFacilityHistory(int facilityHistoryId)
			throws DAOException {
		 ConnectionHandler connHandler = new ConnectionHandler(
	                "CompEnfFacilitySQL.retrieveFacilityHistory", true);
		 connHandler.setInteger(1, facilityHistoryId);
	        return (FacilityHistory) connHandler
	                .retrieve(FacilityHistory.class);
	}
}
