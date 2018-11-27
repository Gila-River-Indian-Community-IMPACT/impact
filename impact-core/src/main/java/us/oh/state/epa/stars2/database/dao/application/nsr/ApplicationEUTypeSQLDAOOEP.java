package us.oh.state.epa.stars2.database.dao.application.nsr;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOOEP;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeOEP;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class ApplicationEUTypeSQLDAOOEP extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOOEP {

	// Just a placeholder class. Not in scope for AZ
	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeOEP ret = new NSRApplicationEUTypeOEP();
		return ret;
	}

	@Override
	public void removeApplicationEUType(Integer appEuId) throws DAOException {

	}

}
