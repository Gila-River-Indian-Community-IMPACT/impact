package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeRESSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		
		EmissionUnitType ret = euType;

		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeRES", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getNameAndTypeOfMaterial());
		connHandler.setInteger(i++, euType.getMaxAnnualUsage());
		connHandler.setInteger(i++, euType.getMaxPctVOC());
		connHandler.setInteger(i++, euType.getMaxPctStyrene());
		connHandler.setString(i++, euType.getApplicationMethod());
		connHandler.update();
		return ret;
	}

}
