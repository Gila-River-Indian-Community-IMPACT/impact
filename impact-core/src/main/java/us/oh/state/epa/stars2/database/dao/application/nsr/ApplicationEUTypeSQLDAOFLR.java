package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOFLR;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeFLR;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAOFLR extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOFLR {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeFLR) {
			NSRApplicationEUTypeFLR appEuType = (NSRApplicationEUTypeFLR) euType;
			NSRApplicationEUTypeFLR ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeFLR", false);
	
			int i = 1;
			
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setString(i++, appEuType.getEmergencyFlareOnlyCd());
			connHandler.setString(i++, appEuType.getIgnitionDeviceTypeCd());
			connHandler.setFloat(i++, appEuType.getBtuContent());
			connHandler.setString(i++, appEuType.getSmokelessDesignCd());
			connHandler.setString(i++, appEuType.getAssitGasUtilizedCd());
			connHandler.setFloat(i++, appEuType.getAssistGasUtilizedBtu());
			connHandler.setFloat(i++, appEuType.getFuelSulfurContent());
			connHandler.setString(i++, appEuType.getFuelSulfurContentUnitsCd());
			connHandler.setFloat(i++, appEuType.getWasteGasVolume());
			connHandler.setString(i++, appEuType.getWasteGasVolumeUnitsCd());
			connHandler.setTimestamp(i++, appEuType.getInstallationDate());
			connHandler.setString(i++, appEuType.getContinuouslyMonitoredCd());
			connHandler.setString(i++, appEuType.getContinuousMonitoringDesc());
			
			connHandler.update();
	
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeFLR ret = new NSRApplicationEUTypeFLR();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeFLR"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.FLR);
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
				"ApplicationEUTypeSQL.removeApplicationEUTypeFLR", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}

}
