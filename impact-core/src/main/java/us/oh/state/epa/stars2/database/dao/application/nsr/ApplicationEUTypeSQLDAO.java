package us.oh.state.epa.stars2.database.dao.application.nsr;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public abstract class ApplicationEUTypeSQLDAO extends AbstractDAO implements
		ApplicationEUTypeDAO {

	@Override
	public abstract NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException;

	@Override
	public void modifyApplicationEUType(NSRApplicationEUType euType)
			throws DAOException {
		// Prevent the dirty data
		if (euType == null || euType.getApplicationEuId() == null) {
			return;
		}

		checkNull(euType.getApplicationEuId());
		removeApplicationEUType(euType.getApplicationEuId());
		createApplicationEUType(euType);
	}

	@Override
	public abstract NSRApplicationEUType retrieveApplicationEUType(
			Integer appEuId) throws DAOException;

	@Override
	public abstract void removeApplicationEUType(Integer emuId)
			throws DAOException;

}
