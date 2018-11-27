package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOAMN;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeAMN;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAOAMN extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOAMN {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeAMN) {
			NSRApplicationEUTypeAMN appEuType = (NSRApplicationEUTypeAMN) euType;
			NSRApplicationEUTypeAMN ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeAMN", false);
	
			int i = 1;
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setBigDecimal(i++, appEuType.getInletCO2Conc());
			connHandler.setBigDecimal(i++, appEuType.getInletH2SConc());
			connHandler.setBigDecimal(i++, appEuType.getAcidGasCO2Conc());
			connHandler.setBigDecimal(i++, appEuType.getAcidGasH2SConc());
			connHandler.setFloat(i++, appEuType.getAmineCircRate());
			connHandler.setString(i++, appEuType.getAmineCircUnitsCd());
			connHandler.setString(i++, appEuType.getAmineTypeCd());
			connHandler.setInteger(i++, appEuType.getInletGasTemp());
			connHandler.setFloat(i++, appEuType.getInletGasPressure());
			connHandler.setFloat(i++, appEuType.getOutletGasFlowRate());
			connHandler.setFloat(i++, appEuType.getAcidGasFlowRate());
			connHandler.setString(i++, appEuType.getAmineCircPumpTypeCd());
			connHandler.setFloat(i++, appEuType.getPumpVolumeRatio());
			connHandler.setBigDecimal(i++, appEuType.getMaxLeanAmineCircRate());
			connHandler.setBigDecimal(i++, appEuType.getActualLeanAmineCircRate());
			connHandler.setString(i++, appEuType.getMotiveGasPumpSource());
	
			connHandler.update();
	
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeAMN ret = new NSRApplicationEUTypeAMN();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeAMN"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.AMN);
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
				"ApplicationEUTypeSQL.removeApplicationEUTypeAMN", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}

}
