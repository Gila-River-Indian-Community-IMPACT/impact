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
import us.oh.state.epa.stars2.database.dao.GdfDAO;
import us.oh.state.epa.stars2.database.dbObjects.ceta.GDF;
import us.oh.state.epa.stars2.framework.exception.DAOException;


@Repository
public class GdfSQLDAO extends AbstractDAO implements GdfDAO {
	
	private Logger logger = Logger.getLogger(GdfSQLDAO.class);

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public GDF createGDF(GDF gdf) throws DAOException {
          checkNull(gdf);
         
	        gdf.setGdfId(nextSequenceVal("CE_gdf_id", gdf.getGdfId()));
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "GdfSQL.createGDF", false);
	        int i = 1;   
	        connHandler.setInteger(i++, gdf.getGdfId());
	        connHandler.setString(i++, gdf.getDoLaaCd());
	        connHandler.setString(i++, gdf.getYear());
	        connHandler.setString(i++, gdf.getMonth());
	        connHandler.setInteger(i++, gdf.getStageOne());
	        connHandler.setInteger(i++, gdf.getStageOneAndTwo());
	        connHandler.setInteger(i++, gdf.getNonStageOneAndTwo());
	        connHandler.update();

	        // If we get here the INSERT must have succeeded, so set the important
	        // data and return the object.
	        gdf.setLastModified(1);

		return gdf;
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

	public void deleteGDF(int gdfId) throws DAOException {
		 checkNull(gdfId);
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "GdfSQL.deleteGDF", false);
	        connHandler.setInteger(1, gdfId);
	        connHandler.remove();
	}

	public boolean modifyGDF(GDF gdf) throws DAOException {
		  checkNull(gdf);	        
	        ConnectionHandler connHandler = new ConnectionHandler(
	                "GdfSQL.modifyGDF", false);
	        int i = 1;
	        connHandler.setString(i++, gdf.getDoLaaCd());
	        connHandler.setString(i++, gdf.getYear());
	        connHandler.setString(i++, gdf.getMonth());
	        connHandler.setInteger(i++, gdf.getStageOne());
	        connHandler.setInteger(i++, gdf.getStageOneAndTwo());
	        connHandler.setInteger(i++, gdf.getNonStageOneAndTwo());  
	        connHandler.setInteger(i++, gdf.getLastModified() + 1);        
	        connHandler.setInteger(i++, gdf.getGdfId());
	        connHandler.setInteger(i++, gdf.getLastModified());
		return connHandler.update();
	}

	public GDF retrieveGDF(int gdfId) throws DAOException {
		 ConnectionHandler connHandler = new ConnectionHandler(
	                "GdfSQL.retrieveGDF", true);
	        connHandler.setInteger(1, gdfId);
	        return (GDF) connHandler
	                .retrieve(GDF.class);
	}
    
    public GDF retrieveGDF(String doLaaCd, String year, String month) throws DAOException {
         ConnectionHandler connHandler = new ConnectionHandler(
                    "GdfSQL.retrieveGDF3Arg", true);
            connHandler.setString(1, doLaaCd);
            connHandler.setString(2, year);
            connHandler.setString(3, month);
            return (GDF) connHandler
                    .retrieve(GDF.class);
    }

	public List<GDF> retrieveGDFByDoLaa(String doLaaCd) throws DAOException {
		List<GDF> results = new ArrayList<GDF>();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			Integer lastId = null;
			GDF gdf = null;
            conn = getReadOnlyConnection();
            pStmt = conn.prepareStatement(loadSQL("GdfSQL.retrieveGDFByDoLaa"));
            pStmt.setString(1, doLaaCd);
            ResultSet rs = pStmt.executeQuery();
            
			while (rs.next()) {
				Integer gdfId = AbstractDAO.getInteger(rs, "gdf_id");
				if (gdfId != null && !gdfId.equals(lastId)) {
	            	gdf = new GDF();
	            	gdf.populate(rs);
	            	lastId = gdfId;
	            	results.add(gdf);
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
