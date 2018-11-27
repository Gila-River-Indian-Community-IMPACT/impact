package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeCOTSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		
		EmissionUnitType ret = euType;

		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeCOT", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getTypeOfCoatingOperation());
		connHandler.setString(i++,euType.getTypeOfMaterialBeingCoated());
		connHandler.setString(i++,euType.getTypeOfMaterialBeingCoatedOther());
		connHandler.setString(i++, euType.getTypeOfProductBeingCoated());
		connHandler.setString(i++, euType.getNameAndTypeOfMaterial());
		connHandler.setLong(i++, euType.getMaxAnnualThroughput());
		connHandler.setInteger(i++, euType.getMaxPctVOC());
		connHandler.setString(i++, euType.getApplicationMethod());
		connHandler.setString(i++, euType.getApplicationMethodOther());
		connHandler.update();
		
		return ret;
	}

}