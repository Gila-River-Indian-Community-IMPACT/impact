package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeTNKSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeTNK", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getMaterialTypeStored());
		connHandler.setString(i++, euType.getLiquidMaterialTypeStored());
		connHandler.setString(i++, euType.getMaterialTypeStoredDesc());
		connHandler.setString(i++, euType.getSubmergedFillPipeFlag());
		connHandler.setInteger(i++, euType.getCapacity());
		connHandler.setString(i++, euType.getCapacityUnit());
		connHandler.setBigDecimal(i++, euType.getMaxThroughput());
		connHandler.setString(i++, euType.getUnitCd());
		connHandler.setString(i++, euType.getTankLocation());

		connHandler.update();

		return ret;
	}

}
