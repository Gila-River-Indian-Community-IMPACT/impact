package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeDRYSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		
		EmissionUnitType ret = euType;

		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeDRY", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getDryCleaningType());
		connHandler.setInteger(i++, euType.getMaxAnnualUsage());
		connHandler.setString(i++, euType.getOperationType());
		connHandler.update();
		
		return ret;
	}
}