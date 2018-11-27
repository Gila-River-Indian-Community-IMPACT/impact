package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOEGU;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeEGU;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAOEGU extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOEGU {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeEGU) {
			NSRApplicationEUTypeEGU appEuType = (NSRApplicationEUTypeEGU) euType;
			NSRApplicationEUTypeEGU ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeEGU", false);
	
			int i = 1;
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setFloat(i++, appEuType.getBtu());
			connHandler.setString(i++, appEuType.getUnitsBtuCd());
			connHandler.setFloat(i++, appEuType.getFuelSulfur());
			connHandler.setString(i++, appEuType.getUnitsFuelSulfurCd());
			connHandler.setInteger(i++, appEuType.getNetElectricalOutput());
			connHandler.setInteger(i++, appEuType.getGrossElectricalOutput());
			connHandler.setString(i++, appEuType.getTurbineCycleTypeCd());
			connHandler.setString(i++, appEuType.getCoalUsageTypeCd());
	
			connHandler.update();
	
			return ret;
		} 
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeEGU ret = new NSRApplicationEUTypeEGU();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeEGU"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.EGU);
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
				"ApplicationEUTypeSQL.removeApplicationEUTypeEGU", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}

}
