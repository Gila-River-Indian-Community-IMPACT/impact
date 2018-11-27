package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeHETSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;

		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeHET", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getFiringType());
		connHandler.setBigDecimal(i++, euType.getHeatInputRating());
		connHandler.setString(i++, euType.getUnitCd());
		connHandler.setString(i++, euType.getPrimaryFuelType());
		connHandler.setString(i++, euType.getSecondaryFuelType());
		connHandler.setInteger(i++, euType.getFuelHeatContent());
		connHandler.setString(i++, euType.getFuelHeatContentUnitsCd());
		
		connHandler.update();

		return ret;
	}
}