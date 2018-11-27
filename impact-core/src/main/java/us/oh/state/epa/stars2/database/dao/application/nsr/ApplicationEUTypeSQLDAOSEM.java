package us.oh.state.epa.stars2.database.dao.application.nsr;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOSEM;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeSEM;
import us.oh.state.epa.stars2.framework.exception.DAOException;

//Just a place holder class.. not in scope for AZ
@Repository
public class ApplicationEUTypeSQLDAOSEM extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOSEM {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		return euType;

	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeSEM ret = new NSRApplicationEUTypeSEM();
		return ret;
	}

	@Override
	public void removeApplicationEUType(Integer appEuId) throws DAOException {
	}

}
