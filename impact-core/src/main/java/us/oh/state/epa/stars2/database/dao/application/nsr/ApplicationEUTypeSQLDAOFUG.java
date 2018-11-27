package us.oh.state.epa.stars2.database.dao.application.nsr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationEUTypeDAOFUG;
import us.oh.state.epa.stars2.database.dao.AbstractDAO.ConnectionHandler;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUFugitiveLeaks;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUType;
import us.oh.state.epa.stars2.database.dbObjects.application.nsr.NSRApplicationEUTypeFUG;
import us.oh.state.epa.stars2.def.AppEUFUGEmissionTypeDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.wy.state.deq.impact.def.EmissionUnitTypeDef;

@Repository
public class ApplicationEUTypeSQLDAOFUG extends ApplicationEUTypeSQLDAO
		implements ApplicationEUTypeDAOFUG {

	@Override
	public NSRApplicationEUType createApplicationEUType(
			NSRApplicationEUType euType) throws DAOException {
		if(euType instanceof NSRApplicationEUTypeFUG) {
			NSRApplicationEUTypeFUG appEuType = (NSRApplicationEUTypeFUG) euType;
			NSRApplicationEUTypeFUG ret = appEuType;
	
			checkNull(appEuType.getApplicationEuId());
	
			ConnectionHandler connHandler = new ConnectionHandler(
					"ApplicationEUTypeSQL.createApplicationEUTypeFUG", false);
	
			int i = 1;
			connHandler.setInteger(i++, appEuType.getApplicationEuId());
			connHandler.setString(i++, appEuType.getFugitiveEmissionTypeCd());
			connHandler.setFloat(i++, appEuType.getMaxDistanceMateriaHauled());
			connHandler.setString(i++, appEuType.getTruckType1());
			connHandler.setInteger(i++, appEuType.getType1TrucksCount());
			connHandler.setInteger(i++, appEuType.getType1TrucksCapacity());
			connHandler.setInteger(i++, appEuType.getType1TrucksEmptyWeight());
			connHandler.setString(i++, appEuType.getTruckType2());
			connHandler.setInteger(i++, appEuType.getType2TrucksCount());
			connHandler.setInteger(i++, appEuType.getType2TrucksCapacity());
			connHandler.setInteger(i++, appEuType.getType2TrucksEmptyWeight());
			connHandler.setString(i++, appEuType.getTruckType3());
			connHandler.setInteger(i++, appEuType.getType3TrucksCount());
			connHandler.setInteger(i++, appEuType.getType3TrucksCapacity());
			connHandler.setInteger(i++, appEuType.getType3TrucksEmptyWeight());
			connHandler.setString(i++, appEuType.getTruckType4());
			connHandler.setInteger(i++, appEuType.getType4TrucksCount());
			connHandler.setInteger(i++, appEuType.getType4TrucksCapacity());
			connHandler.setInteger(i++, appEuType.getType4TrucksEmptyWeight());
			connHandler.setFloat(i++, appEuType.getAcreageSubjectToWindErosion());
			connHandler.setString(i++, appEuType.getStockPileTypeCd());
			connHandler.setFloat(i++, appEuType.getMaterialAddedRemovedFromPileDay());
			connHandler.setFloat(i++, appEuType.getMaterialAddedRemovedFromPileYr());
			connHandler.setInteger(i++, appEuType.getStockPilesCount());
			connHandler.setInteger(i++, appEuType.getStockPileSize());
			connHandler.setString(i++, appEuType.getStockPileUnitCd());
			connHandler.setInteger(i++, appEuType.getBlastsPerYearNumber());
			connHandler.setString(i++, appEuType.getBlastingAgentUsedType());
			connHandler.setFloat(i++, appEuType.getBlastingAgentUsedAmount());
			connHandler.setFloat(i++, appEuType.getBlastHorizontalArea());
			connHandler.setString(i++, appEuType.getMaterialBlastedTypeCd());
			connHandler.setString(i++, appEuType.getFugitiveSourceDescription());
			connHandler.update();
			return ret;
		}
		else
			return euType;
	}

	@Override
	public NSRApplicationEUType retrieveApplicationEUType(Integer appEuId)
			throws DAOException {
		NSRApplicationEUTypeFUG ret = new NSRApplicationEUTypeFUG();
		Connection conn = null;
		PreparedStatement pStmt = null;

		if (appEuId != null) {
			try {
				conn = getReadOnlyConnection();
				pStmt = conn
						.prepareStatement(loadSQL("ApplicationEUTypeSQL.retrieveApplicationEUTypeFUG"));

				pStmt.setInt(1, appEuId);

				ResultSet rs = pStmt.executeQuery();

				if (rs.next()) {
					ret.populate(rs);
					ret.setEmissionUnitTypeCd(EmissionUnitTypeDef.FUG);
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
				"ApplicationEUTypeSQL.removeApplicationEUTypeFUG", false);

		connHandler.setInteger(1, appEuId);

		connHandler.remove();

	}

}
