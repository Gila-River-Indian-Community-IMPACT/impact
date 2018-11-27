package us.oh.state.epa.stars2.bo;

import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityPurgeSearchLineItem;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface FacilityPurgeService {

	boolean purgeFacility(final String facilityId) throws DAOException; 
	
	public boolean purgeFacility(FacilityPurgeSearchLineItem facility) throws DAOException;
}
