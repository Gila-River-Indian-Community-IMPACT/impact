package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeINCSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeINC", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getIncineratorType());
		connHandler.setString(i++, euType.getBurnerSystem());
		connHandler.setBigDecimal(i++, euType.getMaxDesignCapacity());
		connHandler.setString(i++, euType.getUnitCd());
		connHandler.setBigDecimal(i++, euType.getMinDesignCapacity());
		connHandler.setString(i++, euType.getCapacityUnit());
		connHandler.setBigDecimal(i++, euType.getPilotGasVolume());
		connHandler.setInteger(i++, euType.getPrimaryChamberOpTemp());
		connHandler.setInteger(i++, euType.getSecondaryChamberOpTemp());

		connHandler.update();

		return ret;
	}

}
