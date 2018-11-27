package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeWWESQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeWWE", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setBigDecimal(i++, euType.getMaxAnnualWoodDustGenerated());
		connHandler.setString(i++, euType.getEquipmentType());
		connHandler.setString(i++, euType.getPowerRatingFlag());
		connHandler.update();

		return ret;
	}

}
