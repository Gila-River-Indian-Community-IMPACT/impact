package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeCKDSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeCKD", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getUnitType());
		connHandler.setString(i++, euType.getMaxAnnualThroughput());
		connHandler.setString(i++, euType.getUnitCd());
		connHandler.setBigDecimal(i++, euType.getHeatInputRating());
		connHandler.setString(i++, euType.getPrimaryFuelType());
		connHandler.setString(i++, euType.getSecondaryFuelType());
		connHandler.setString(i++, euType.getSerialNumber());
		connHandler.setTimestamp(i++, euType.getSerialNumberEffectiveDate());
		connHandler.update();

		return ret;
	}

}
