package us.oh.state.epa.stars2.database.dao;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dbObjects.ceta.GDF;
import us.oh.state.epa.stars2.framework.exception.DAOException;


public class GdfDAOTest {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	// TODO: Fix against database
	@Test
	@Ignore
	public void testGDF() {
		GdfDAO gdfDAO = (GdfDAO)DAOFactory.getDAO("GdfDAO");
		try {
			// delete information that may be in database from a previous test
			gdfDAO.deleteGDF(99);
			
		} catch (DAOException e) {
			fail();
			e.printStackTrace();
		}
					
			// initialize an GDF object
			GDF gdf = new GDF();				
			gdf.setGdfId(99);
			gdf.setDoLaaCd("CDO");
			gdf.setYear("2011");
			gdf.setMonth("01");
			gdf.setStageOne(99);
			gdf.setStageOneAndTwo(99);
			gdf.setNonStageOneAndTwo(99);
			gdf.setLastModified(1);
			
			try{	
				
				// create an GDF record in the database and ensure
				// the data is the same once it's created
				GDF checkGdf = gdfDAO.createGDF(gdf);
				assertNotNull(checkGdf);
				assertEquals(gdf, checkGdf);
				
				// retrieve a GDF record (this will also retrieve the modified action)
				// and ensure GDF data has not changed
				GDF gdf2 = gdfDAO.retrieveGDF(99);
				assertEquals(gdf, gdf2);
			
				// modification GDF
					
				gdf.setYear("2012");
				gdf.setMonth("01");
				gdf.setStageOne(100);
				gdf.setStageOneAndTwo(100);
				gdf.setNonStageOneAndTwo(100);
				gdfDAO.modifyGDF(gdf);
				GDF gdf3 = gdfDAO.retrieveGDF(99);
				assertEquals(gdf3.getYear(), "2012");
				assertEquals(gdf3.getMonth(), "01");
				assertEquals(gdf3.getStageOne().intValue(), 100);
				assertEquals(gdf3.getStageOneAndTwo().intValue(), 100);
				assertEquals(gdf3.getNonStageOneAndTwo().intValue(), 100);
				assertEquals(gdf3.getLastModified().intValue(), 2);
				
				
				
				//clean up
				gdfDAO.deleteGDF(99);
			
		} catch (DAOException e) {
			fail();
			e.printStackTrace();
		}
	}

	private final Timestamp makeDateFromString(String dateStr) {
		Timestamp ts = null;
		try {
			ts = new Timestamp(dateFormat.parse(dateStr).getTime());
		} catch (ParseException e) {
			System.err.println("Failed parsing date string " + dateStr);
		}
		return ts;
	}

}

