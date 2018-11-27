package us.oh.state.epa.stars2.database.dao.application.nsr;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAODIS;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOPAM;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeMAT;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypePAM;
import us.oh.state.epa.stars2.framework.exception.DAOException;

//Just a place holder class.. not in scope for AZ
@Repository
public class ApplicationEUTypeSQLDAOPAM extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOPAM {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		return euType;

	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypePAM ret = new NSRApplicationEUTypePAM();
		return ret;
	}

	@Override
	public void removeApplicationEUType(Integer appEuId) throws DAOException {
	}

}
