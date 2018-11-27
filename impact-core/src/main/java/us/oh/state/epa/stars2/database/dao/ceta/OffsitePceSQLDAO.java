package us.oh.state.epa.stars2.database.dao.ceta;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.OffsitePceDAO;
import us.oh.state.epa.stars2.database.dbObjects.ceta.OffsitePCE;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class OffsitePceSQLDAO extends AbstractDAO implements OffsitePceDAO {
	public OffsitePCE createOffsitePCE(int fpId, Timestamp actionDate) throws DAOException {
		OffsitePCE offsitePCE = new OffsitePCE();
		offsitePCE.setOffsitePceId(nextSequenceVal("CE_offsite_pce_id", offsitePCE.getOffsitePceId()));
		ConnectionHandler connHandler = new ConnectionHandler("OffsitePceSQL.createOffsitePce", false);
		int i = 1;
        connHandler.setInteger(i++, offsitePCE.getOffsitePceId());
        connHandler.setInteger(i++, fpId);
        connHandler.setTimestamp(i++, actionDate);
        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        offsitePCE.setLastModified(1);
        
		return offsitePCE;
	}
	
	public boolean  modifyOffsitePce(OffsitePCE offsitePCE) throws DAOException {
		checkNull(offsitePCE);
        checkNull(offsitePCE.getOffsitePceId());
        ConnectionHandler connHandler = new ConnectionHandler(
                "OffsitePceSQL.modifyOffsitePce", false);
        int i=1;
        connHandler.setString(i++, offsitePCE.getAfsId());
        connHandler.setTimestamp(i++, offsitePCE.getAfsdate());
        connHandler.setInteger(i++, offsitePCE.getFpId());
        connHandler.setTimestamp(i++, offsitePCE.getActionDate());
        connHandler.setInteger(i++, offsitePCE.getLastModified() + 1);
        connHandler.setInteger(i++, offsitePCE.getOffsitePceId());
        connHandler.setInteger(i++, offsitePCE.getLastModified());
        return connHandler.update();
	}
	
	public void deleteOffsitePCE(int offsitePceId) throws DAOException {
		checkNull(offsitePceId);
		ConnectionHandler connHandler = new ConnectionHandler(
                "OffsitePceSQL.deleteOffsitePce", false);
        connHandler.setInteger(1, offsitePceId);
        connHandler.remove();
	}

	public OffsitePCE retrieveOffsitePce(int offsitePceId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
                "OffsitePceSQL.retrieveOffsitePCE", true);
        connHandler.setInteger(1, offsitePceId);
        return (OffsitePCE) connHandler.retrieve(OffsitePCE.class);
	}
    
    public List<OffsitePCE> newAfsOffsitePCEs() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "OffsitePceSQL.newAfsOffsitePCEs", true);
        List<OffsitePCE> rtn = connHandler.retrieveArray(OffsitePCE.class);
        return rtn;
    }
    
    public boolean afsLockOffSiteVisit(OffsitePCE offSite, Integer afsId)
    throws DAOException {
        checkNull(offSite);
        checkNull(afsId);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.afsLockOffSiteVisit", false);

        int i=1;
        connHandler.setString(i++, convertAfsIdToString(afsId));
        connHandler.setInteger(i++, offSite.getLastModified() + 1);
        connHandler.setString(i++, offSite.getOffsitePceId());
        connHandler.setInteger(i++, offSite.getLastModified());
        return connHandler.update();
    }
    
    public String offSiteVisitAfsLocked(Integer offsitePceId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.offSiteVisitAfsLockedId", true);
        connHandler.setInteger(1, offsitePceId);
        ArrayList<? extends Object> afsIds = connHandler
                .retrieveJavaObjectArray(String.class);
        if(afsIds.size() != 1) return null;
        if(afsIds.get(0) != null) return (String)afsIds.get(0);
        return null;
    }
    
    public boolean afsSetDateOffsitePCE(OffsitePCE offsite)
    throws DAOException {
        checkNull(offsite);
        ConnectionHandler connHandler = new ConnectionHandler(
                "CetaSQL.afsSetDateOffsitePCE", false);

        int i=1;
        connHandler.setTimestamp(i++, offsite.getAfsdate());
        connHandler.setInteger(i++, offsite.getLastModified() + 1);
        connHandler.setString(i++, offsite.getOffsitePceId());
        connHandler.setInteger(i++, offsite.getLastModified());
        return connHandler.update();
    }
    
    public List<OffsitePCE> retrieveOffSiteVisitsByAfsId(String scscId, String afsId)
    throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler(
                "OffsitePceSQL.retrieveOffSiteVisitsByAfsId", true);
        connHandler.setString(1, scscId);
        connHandler.setString(2, afsId);
        ArrayList<OffsitePCE> ret = connHandler.retrieveArray(OffsitePCE.class);
        return ret;
    }
}
