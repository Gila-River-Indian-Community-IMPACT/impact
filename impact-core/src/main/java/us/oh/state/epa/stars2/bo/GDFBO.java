package us.oh.state.epa.stars2.bo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.GdfDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.ceta.GDF;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Transactional(rollbackFor=Exception.class)
@Service
public class GDFBO extends BaseBO implements GDFService {

	/**
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
	 */
	public List<GDF> retrieveGDFs(String doLaaCd) throws DAOException {
		List<GDF> results = gdfDAO().retrieveGDFByDoLaa(doLaaCd);
		if (results == null) {
			results = new ArrayList<GDF>();
		}
		return results;
	}
	/**
	 * 
	 * @param gdfId
	 * @return
	 * @throws DAOException
	 * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Supports"
	 */
	public GDF retrieveGDF(int gdfId) throws DAOException {
		GDF ret = null;
		try {
		ret = gdfDAO().retrieveGDF(gdfId);
		} catch(DAOException de) {
            logger.error("GDF Id=" + gdfId + " " + de.getMessage(), de);
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
     * 
     *  */
	
	public GDF retrieveGDF(String doLaaCd, String year, String month) throws DAOException {
        GDF gdf = null;
	    Transaction trans = null;
	    try {
	        trans = TransactionFactory.createTransaction(); 
	        GdfDAO gdfDAO = gdfDAO(trans); 
	        gdf = gdfDAO.retrieveGDF(doLaaCd, year, month);
	    } catch (DAOException e) {
	        cancelTransaction(trans, e);
	    } finally {
	        closeTransaction(trans);
	    }
	    return gdf;
	}
	
	/**
	 * 
	 * @param gdf
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public GDF createGDF(GDF gdf) throws DAOException {
        Transaction trans = null;
        try {
        	trans = TransactionFactory.createTransaction();	
        	GdfDAO gdfDAO = gdfDAO(trans); 
        	gdf = gdfDAO.createGDF(gdf);
        } catch (DAOException e) {
        	cancelTransaction(trans, e);
        } finally {
        	closeTransaction(trans);
        }
		return gdf;
	}

	/**
	 * 
	 * @param gdf
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public void modifyGDF(GDF gdf) throws DAOException {
        Transaction trans = null;
        try {
        	trans = TransactionFactory.createTransaction();		
        	GdfDAO gdfDAO = gdfDAO(trans);
        	gdfDAO.modifyGDF(gdf);
        	
        } catch (DAOException e) {
        	cancelTransaction(trans, e);
        } finally {
        	closeTransaction(trans);
        }
	}
	
	/**
	 * 
	 * @param gdf
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="Required"
	 */
	public void removeGDF(GDF gdf) throws DAOException {
        Transaction trans = null;
        try {
        	trans = TransactionFactory.createTransaction();	
        	gdfDAO().deleteGDF(gdf.getGdfId());
        } catch (DAOException e) {
        	cancelTransaction(trans, e);
        } finally {
        	closeTransaction(trans);
        }
	}

}
