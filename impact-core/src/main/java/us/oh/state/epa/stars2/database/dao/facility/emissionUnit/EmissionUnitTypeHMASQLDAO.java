package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeHMASQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeHMA", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getPlantType());
		connHandler.setInteger(i++, euType.getMaxProductionRate());
		connHandler.setString(i++, euType.getUnitCd());
		connHandler.setBigDecimal(i++, euType.getMaxBurnerDesignRate());
		connHandler.setString(i++, euType.getPowerSource());
		connHandler.setString(i++, euType.getFuelType());
		
		connHandler.update();

		return ret;
	}

}
