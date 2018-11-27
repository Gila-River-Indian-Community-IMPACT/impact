package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOCSH;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeCSH;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAOCSH extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOCSH {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeCSH) {
			NSRApplicationEUTypeCSH appEuType = (NSRApplicationEUTypeCSH) euType;
			NSRApplicationEUTypeCSH ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeCSH", false);
	
			int i = 1;
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setString(i++, appEuType.getCrushedMaterialType());
			connHandler.setString(i++, appEuType.getCrusherTypeCd());
			connHandler.setTimestamp(i++, appEuType.getCrusherManufactureDate());
			connHandler.setString(i++, appEuType.getCrusherPowerSourceCd());
			connHandler.setInteger(i++, appEuType.getMaxCrusherCapacity());
			connHandler.setString(i++, appEuType.getScreenCd());
			connHandler.setString(i++, appEuType.getScreenType());
			connHandler.setString(i++, appEuType.getMaterialScreenedType());
			connHandler.setTimestamp(i++, appEuType.getScreenManufactureDate());
			connHandler.setString(i++, appEuType.getScreenPowerSourceCd());
			connHandler.setString(i++, appEuType.getOperatingInConjunctionCd());
			connHandler.setInteger(i++, appEuType.getMaxScreeingCapacity());
			connHandler.setInteger(i++, appEuType.getConveyorTransferDropPoints());
			connHandler.setString(i++, appEuType.getMaterialTransferredType());
			connHandler.setString(i++, appEuType.getDetailedDescription());
			
			connHandler.update();
	
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeCSH ret = new NSRApplicationEUTypeCSH();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeCSH"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.CSH);
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
				"ApplicationEUTypeSQL.removeApplicationEUTypeCSH", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}
}
