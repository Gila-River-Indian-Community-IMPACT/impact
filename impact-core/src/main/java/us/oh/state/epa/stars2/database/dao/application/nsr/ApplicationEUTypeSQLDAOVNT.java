package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOVNT;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeVNT;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAOVNT extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOVNT {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeVNT) {
			NSRApplicationEUTypeVNT appEuType = (NSRApplicationEUTypeVNT) euType;
			NSRApplicationEUTypeVNT ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeVNT", false);
	
			int i = 1;
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setFloat(i++, appEuType.getFlowRateThroughput());
			connHandler.setString(i++, appEuType.getFlowRateThroughputUnitsCd());
			connHandler.setBigDecimal(i++, appEuType.getVocConc());
			connHandler.setBigDecimal(i++, appEuType.getHapsConc());
	
			connHandler.update();
	
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeVNT ret = new NSRApplicationEUTypeVNT();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeVNT"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.VNT);
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
				"ApplicationEUTypeSQL.removeApplicationEUTypeVNT", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}

}
