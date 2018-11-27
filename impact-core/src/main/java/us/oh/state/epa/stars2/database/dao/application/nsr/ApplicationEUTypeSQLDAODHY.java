package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAODHY;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeDHY;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAODHY extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAODHY {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeDHY) {
			NSRApplicationEUTypeDHY appEuType = (NSRApplicationEUTypeDHY) euType;
			NSRApplicationEUTypeDHY ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeDHY", false);
	
			int i = 1;
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setInteger(i++, appEuType.getTemperatureOfWetGas());
			connHandler.setFloat(i++, appEuType.getPressureOfWetGas());
			connHandler.setBigDecimal(i++, appEuType.getWaterContentOfWetGas());
			connHandler.setFloat(i++, appEuType.getFlowRateOfDryGas());
			connHandler.setFloat(i++, appEuType.getWaterContentOfDryGas());
			connHandler.setString(i++, appEuType.getManufactureNameOfGlycolCircPump());
			connHandler.setString(i++, appEuType.getModelNameAndNoOfGlycolCircPump());
			connHandler.setString(i++, appEuType.getTypeOfGlycolCirculationPumpCd());
			connHandler.setFloat(i++, appEuType.getPumpVolumeRatio());
			connHandler.setBigDecimal(i++, appEuType.getMaxLeanGlycolCirculationRate());
			connHandler.setBigDecimal(i++, appEuType.getActualLeanGlycolCirculationRate());
			connHandler.setString(i++, appEuType.getSourceOfMotiveGasForPump());
			connHandler.setString(i++, appEuType.getAdditionalGasStrippingCd());
			connHandler.setString(i++, appEuType.getSourceOfStrippingGasCd());		
			connHandler.setFloat(i++, appEuType.getStrippingGasRate());
			connHandler.setString(i++, appEuType.getIncludeGlycolFlashTankSeparatorCd());
			connHandler.setFloat(i++, appEuType.getFlashTankOffGasStream());
			connHandler.setString(i++, appEuType.getFlashVaporsRouted());
			connHandler.setInteger(i++, appEuType.getOperatingTemperature());
			connHandler.setFloat(i++, appEuType.getOperatingPressure());
			connHandler.setString(i++, appEuType.getIsVesselHeatedCd());
			
			connHandler.update();
	
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeDHY ret = new NSRApplicationEUTypeDHY();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeDHY"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.DHY);
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
				"ApplicationEUTypeSQL.removeApplicationEUTypeDHY", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}
}