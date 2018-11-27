package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeTKOSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeTKO", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getMaterialType());
		connHandler.setString(i++, euType.getPlatingLineFlag());
		connHandler.setString(i++, euType.getPlatingType());
		connHandler.setString(i++, euType.getPlatingTypeOther());
		connHandler.setString(i++, euType.getMetalType());
		connHandler.setString(i++, euType.getMetalTypeOther());
		connHandler.setString(i++, euType.getCapacity());
		connHandler.setString(i++, euType.getMaxAnnualUsage());
		connHandler.setString(i++, euType.getConcentrationPct());
		
		connHandler.update();

		return ret;
	}

}
