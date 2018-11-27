package us.oh.state.epa.stars2.database.dao;

import java.sql.Timestamp;
import java.util.List;

import us.oh.state.epa.stars2.database.dbObjects.ceta.OffsitePCE;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface OffsitePceDAO extends TransactableDAO {
	/**
	 * Create a new Offsite PCE object.
	 * @param offsitePce
	 * @return
	 * @throws DAOException
	 */
	OffsitePCE createOffsitePCE(int fpId, Timestamp actionDate) throws DAOException;
	
	/**
	 * Modify an existing Offsite PCE object.
	 * @param offsitePce
	 * @return
	 * @throws DAOException
	 */
	boolean  modifyOffsitePce(OffsitePCE offsitePCE) throws DAOException;
	
	/**
	 * Delete the Offsite PCE object identified by offsitePceId.
	 * @param offsitePceId
	 * @throws DAOException
	 */
	void deleteOffsitePCE(int offsitePceId) throws DAOException;
	
	/**
	 * Retrieve the OffsitePCE identified by offsitePceId.
	 * @param offsitePceid
	 * @return
	 * @throws DAOException
	 */
	OffsitePCE retrieveOffsitePce(int offsitePceId) throws DAOException;
    List<OffsitePCE> newAfsOffsitePCEs() throws DAOException ;
    String offSiteVisitAfsLocked(Integer offsitePceId) throws DAOException ;
    boolean afsLockOffSiteVisit(OffsitePCE offSite, Integer afsId) throws DAOException ;
    boolean afsSetDateOffsitePCE(OffsitePCE offsite) throws DAOException ;
    List<OffsitePCE> retrieveOffSiteVisitsByAfsId(String scscId, String afsId) throws DAOException ;
}
