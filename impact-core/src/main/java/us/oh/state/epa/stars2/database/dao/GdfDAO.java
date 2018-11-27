package us.oh.state.epa.stars2.database.dao;

import java.util.List;
import us.oh.state.epa.stars2.database.dbObjects.ceta.GDF;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface GdfDAO extends TransactableDAO {
	
	GDF createGDF(GDF gdf) throws DAOException;
	GDF retrieveGDF(int gdfId) throws DAOException;
	boolean modifyGDF(GDF gdf) throws DAOException;
	void deleteGDF(int gdfId) throws DAOException;
	List<GDF> retrieveGDFByDoLaa(String doLaaCd) throws DAOException;
    GDF retrieveGDF(String doLaaCd, String year, String month) throws DAOException;
	
}
