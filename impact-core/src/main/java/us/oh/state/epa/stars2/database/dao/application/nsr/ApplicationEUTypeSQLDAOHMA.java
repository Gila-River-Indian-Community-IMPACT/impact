package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOHMA;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeHMA;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAOHMA extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOHMA {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeHMA) {
			NSRApplicationEUTypeHMA appEuType = (NSRApplicationEUTypeHMA) euType;
			NSRApplicationEUTypeHMA ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeHMA", false);
	
			int i = 1;
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setString(i++, appEuType.getManufactureName());
			connHandler.setString(i++, appEuType.getModelNameAndNum());
			connHandler.setFloat(i++, appEuType.getFuelSulfurContent());
			connHandler.setString(i++, appEuType.getHmaSulfurUnitsCd());
			connHandler.setFloat(i++, appEuType.getFuelConsumption());
			connHandler.setString(i++, appEuType.getHmafuelConsUnitsCd());
			connHandler.setFloat(i++, appEuType.getFuelGasHeatingVal());
			connHandler.setString(i++, appEuType.getFuelGasHeatingUnitsCd());
			connHandler.setFloat(i++, appEuType.getStackVolumetricFlow());
			connHandler.setString(i++, appEuType.getPlantProcessRecycledAsphaltCd());
			connHandler.setInteger(i++, appEuType.getMaxRapPercent());
			connHandler.update();
	
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeHMA ret = new NSRApplicationEUTypeHMA();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeHMA"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.HMA);
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
	public void removeApplicationEUType(Integer appEuId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"ApplicationEUTypeSQL.removeApplicationEUTypeHMA", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}

}
