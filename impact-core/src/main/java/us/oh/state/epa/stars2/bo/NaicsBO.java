package us.oh.state.epa.stars2.bo;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Transactional(rollbackFor=Exception.class)
@Service
public class NaicsBO extends BaseBO implements NaicsService {
	
//	private FacilityDAO getFacilityDAO() throws DAOException {
//		String schema = null;
//		if (!CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
//			schema = "Staging";
//		}
//
//		return facilityDAO(schema);
//	}
	
	@Override
	public void addFacilityNAICSs(Integer fpId, List<String> nasics)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facDAO = facilityDAO(trans);

		try {

			facDAO.removeFacilityNAICSs(fpId);

			for (String naicsCd : nasics) {
				facDAO.addFacilityNAICS(fpId, naicsCd);
			}
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("FP_ID = " + fpId, trans, e);
		} catch (Exception e) {
			cancelTransaction("FP_ID = " + fpId, trans,
					new DAOException(e.getMessage(), e));
		} finally { 
			closeTransaction(trans);
		}

	}
	
	@Override
	public List<String> retrieveNAICSCodes(Integer fpId)
			throws DAOException {
		List<String> naicsCds = null;
		FacilityDAO facDAO = getFacilityDAO();
		naicsCds= facDAO.retrieveFacilityNAICSCodes(fpId);
		return naicsCds;
	}
	
	@Override
	public void deleteFacilityNaics(Integer fpId, String naics) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		FacilityDAO facDAO = facilityDAO(trans);
		try {
			facDAO.deleteFacilityNaics(fpId, naics);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction("FP_ID = " + fpId, trans, e);
		} catch (Exception e) {
			cancelTransaction("FP_ID = " + fpId, trans,
					new DAOException(e.getMessage(), e));
		} finally { 
			closeTransaction(trans);
		}
	}
}
