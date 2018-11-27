package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeENGSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeENG", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setBigDecimal(i++, euType.getNamePlateRating());
		connHandler.setString(i++, euType.getUnitCd());
		connHandler.setString(i++, euType.getPrimaryFuelType());
		connHandler.setString(i++, euType.getSecondaryFuelType());
		connHandler.setString(i++, euType.getSerialNumber());
		connHandler.setTimestamp(i++, euType.getSerialNumberEffectiveDate());
		connHandler.setString(i++, euType.getManufacturerName());
		connHandler.setString(i++, euType.getModelNameNumber());
		connHandler.setString(i++, euType.getEngineType());
		connHandler.setBigDecimal(i++, euType.getSiteRating());
		connHandler.setString(i++, euType.getSiteRatingUnitCd());
		connHandler.setString(i++, euType.getTypeOfUse());
		

		connHandler.update();

		return ret;
	}

}
