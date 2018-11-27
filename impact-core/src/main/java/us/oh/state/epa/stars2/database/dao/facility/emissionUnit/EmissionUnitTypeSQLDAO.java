package us.oh.state.epa.stars2.database.dao.facility.emissionUnit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.EmissionUnitTypeDAO;
import us.oh.state.epa.stars2.database.dao.AbstractDAO.ConnectionHandler;
import us.oh.state.epa.stars2.database.dbObjects.ceta.FceAmbientMonitorLineItem;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.Component;
import us.oh.state.epa.stars2.database.dbObjects.facility.emissionUnit.EmissionUnitType;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public abstract class EmissionUnitTypeSQLDAO extends AbstractDAO implements
		EmissionUnitTypeDAO {

	@Override
	public abstract EmissionUnitType createEmissionUnitType(EmissionUnitType euType)
			throws DAOException;

	@Override
	public void modifyEmissionUnitType(EmissionUnitType euType)
			throws DAOException {
		// Prevent the dirty data
		if (euType == null || euType.getEmuId() == null) {
			return;
		}
		
		checkNull(euType.getEmuId());
		removeEmissionUnitType(euType.getEmuId());
		createEmissionUnitType(euType);
	}

	@Override
	public EmissionUnitType retrieveEmissionUnitType(Integer emuId)
			throws DAOException {
		EmissionUnitType ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (emuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("EmissionUnitTypeSQL.retrieveEmissionUnitType"));

				pStmt.setInt(1, emuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret = new EmissionUnitType();
					ret.populate(rs);
				}

				rs.close();
			} catch (Exception e) {
				handleException(e, conn);
			} finally {
				closeStatement(pStmt);
				handleClosing(conn);
			}
		}

		return ret;
	}

	@Override
	public void removeEmissionUnitType(Integer emuId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"EmissionUnitTypeSQL.removeEmissionUnitType", false);

		connHandler.setInteger(1, emuId);

		connHandler.remove();
	}

	@Override
	public void addComponentCount(Component component) throws DAOException{
		checkNull(component.getEmuId());
		checkNull(component.getComponentCd());
		checkNull(component.getGas());
		checkNull(component.getHeavyOil());
		checkNull(component.getLightOil());
		checkNull(component.getWater());
		
		ConnectionHandler connHandler = new ConnectionHandler("EmissionUnitTypeSQL.addFugComponent", false);
		int i = 1;
		connHandler.setInteger(i++, component.getEmuId());
		connHandler.setString(i++, component.getComponentCd());
		connHandler.setInteger(i++, component.getGas());
		connHandler.setInteger(i++, component.getHeavyOil());
		connHandler.setInteger(i++, component.getLightOil());
		connHandler.setInteger(i++, component.getWater());
		
		
		connHandler.update();
	}
	
	
	@Override
	public void deleteComponentCountByEmuId(Integer emuId) throws DAOException{
		checkNull(emuId);
		
		ConnectionHandler connHandler = new ConnectionHandler("EmissionUnitTypeSQL.deleteFugComponent", false);
		int i = 1;
		connHandler.setInteger(i++, emuId);
		connHandler.remove();
	}
	
	@Override
	public List<Component> retrieveComponentsByEmuId(Integer emuId) throws DAOException{
		checkNull(emuId);
		
		ConnectionHandler connHandler = new ConnectionHandler("EmissionUnitTypeSQL.retrieveComponentsByEmuId", true);
		connHandler.setInteger(1, emuId);
		List<Component> ret = connHandler.retrieveArray(Component.class); 
		if (ret.isEmpty()){
			ret = EmissionUnitType.initializeComponentList(emuId);
		}
		return ret;
	}
}
