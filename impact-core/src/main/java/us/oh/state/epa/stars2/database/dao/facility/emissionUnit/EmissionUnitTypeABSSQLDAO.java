package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class EmissionUnitTypeABSSQLDAO extends EmissionUnitTypeSQLDAO implements
		EmissionUnitTypeDAO {

	@Override
	public EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		
		EmissionUnitType ret = euType;
		
		checkNull(euType.getEmuId());

		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.createEmissionUnitTypeABS", false);

		int i = 1;
		connHandler.setInteger(i++, euType.getEmuId());
		connHandler.setString(i++, euType.getNameAndTypeOfMaterial());
		connHandler.setString(i++, euType.getSubstrateBlasted());
		connHandler.setString(i++, euType.getSubstrateRemoved());
		connHandler.setString(i++, euType.getConcentrationOfLeadPct());
		connHandler.setBigDecimal(i++, euType.getMaxAnnualUsageAbs());
		connHandler.setString(i++, euType.getBlastMediaCARBCertifiedFlag());
		connHandler.setInteger(i++, euType.getMaxNumberOfTimesBlastMediaReclaimedForReuse());
		connHandler.setString(i++, euType.getApplicationMethod());
		connHandler.setString(i++, euType.getSubstrateRemovedOther());
		connHandler.setString(i++, euType.getSubstrateBlastedOther());
		connHandler.update();
		
		return ret;
	}

}
