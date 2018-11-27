package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeFLRSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeFLR", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setBigDecimal(i++, euType.getMaxDesignCapacity());
		connHandler.setBigDecimal(i++, euType.getMinDesignCapacity());
		connHandler.setBigDecimal(i++, euType.getPilotGasVolume());
		connHandler.setString(i++, euType.getUnitCd());
		connHandler.setString(i++, euType.getCapacityUnit());
		
		connHandler.update();

		return ret;
	}

}
