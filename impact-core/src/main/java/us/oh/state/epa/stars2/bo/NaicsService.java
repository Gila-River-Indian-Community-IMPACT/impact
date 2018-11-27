package us.oh.state.epa.stars2.bo;

import java.util.List;

import us.oh.state.epa.stars2.framework.exception.DAOException;

public interface NaicsService {
	public void addFacilityNAICSs(Integer fpId, List<String> nasics) throws DAOException;
	public List<String> retrieveNAICSCodes(Integer fpId) throws DAOException;
	public void deleteFacilityNaics(Integer fpId, String naics) throws DAOException;
}
