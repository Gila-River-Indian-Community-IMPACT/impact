package us.oh.state.epa.stars2.database.dao.application.nsr;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOBVC;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeBVC;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAOBVC extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOBVC {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeBVC) {
			NSRApplicationEUTypeBVC appEuType = (NSRApplicationEUTypeBVC) euType;
			NSRApplicationEUTypeBVC ret = appEuType;
	
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeBVC ret = new NSRApplicationEUTypeBVC();
		
		ret.setApplicationEuId(appEuId);
		ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.BVC);

		return ret;
	}

	@Override
	public void removeApplicationEUType(Integer appEuId) throws DAOException {
		return;
	}

}
