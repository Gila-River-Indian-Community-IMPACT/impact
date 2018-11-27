package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAODefault;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeDHY;
import us.oh.state.epa.stars2.framework.exception.DAOException;

public class ApplicationEUTypeSQLDAODefault extends ApplicationEUTypeSQLDAO
	implements ApplicationEUTypeDAODefault {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeDHY) {
			NSRApplicationEUType appEuType = (NSRApplicationEUType) euType;
			NSRApplicationEUType ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeDefault", false);
	
			int i = 1;
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setString(i++, appEuType.getEmissionUnitTypeCd());
			
			connHandler.update();
	
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUType ret = new NSRApplicationEUType();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(
								loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeDefault"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(rs.getString("eu_type_cd"));
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
				"ApplicationEUTypeSQL.removeApplicationEUTypeDefault", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}
}
