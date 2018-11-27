package us.oh.state.epa.stars2.bo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor=Exception.class)
@Service
public class AppendixKBO  {  // extends BaseBO implements AppendixKService {
//	private static HashMap<String,List<AppendixK> > appendixKMap;
//
//	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//	private static int appendixKId = 100;
//	// TODO get rid of this dummy constructor - it's only needed for testing
//	public AppendixKBO() {
//		appendixKMap = new HashMap<String, List<AppendixK> >();
//	}
//	
//	// TODO get rid of this method once testing is done
//	private final Timestamp makeDateFromString(String dateStr) {
//		Timestamp ts = null;
//		try {
//			ts = new Timestamp(dateFormat.parse(dateStr).getTime());
//		} catch (ParseException e) {
//			logger.error("Failed parsing date string " + dateStr, e);
//		}
//		return ts;
//	}
//	
//	private final void createAppendixKsForFacility(FacilityList facility) {
//		List<AppendixK> stList = new ArrayList<AppendixK>();
//		AppendixK st = new AppendixK();
//		st.setFacilityId(facility.getFacilityId());
//		st.setFacilityNm(facility.getName());
//		st.setAppendixKId(appendixKId++);
//		st.setAfsSentDate(makeDateFromString("2009-10-27"));
//		st.setAfsId(23145);
//		st.setSiteVisitId(100);
//		st.setEuId("P038");
//		List<String> pollutantList = new ArrayList<String>();
//		pollutantList.add(PollutantDef.VOC_DSC);
//		st.setEuDescription("Gundgeon Thermfire (72 tons per day) thermal sand reclamation unit controlled by a fabric filter baghouse. The terms of this permit supercede those identified in PTI 01-08850 issued July 29, 2004");
//		st.setScheduledDate(makeDateFromString("2009-08-28"));
//		st.setTestDate(makeDateFromString("2009-08-28"));
//		st.setReceivedDate(makeDateFromString("2009-10-06"));
//		st.setCompany("Air Compliance Testing, Inc.");
//		st.setScc("30400350");
//		st.setStackTestMethodCd("9");
//		st.setMonitoringEquipUsed("pressure drop");
//		st.setControlEquipUsed("fabric filter baghouse");
//		st.setAllowableEmissionsRate("no visible emissions");
//		st.setMaxOperatingRate("72 tons/day");
//		st.setAvgTestedEmissionsRate("0% opacity (maximum 6-minute average)");
//		st.setAvgTestedOperatingRate("2.76 tons/hr");
//		st.setWitnessed(true);
//		st.setAuditsSubmitted(true);
//		st.setAuditPassed("Y");
//		st.setTestResultsCd("P");
//		st.setConformedToMethod(true);
//		List<Integer> witnessList = new ArrayList<Integer>();
//		witnessList.add(4280);
//		st.setWitnesses(witnessList);
//		st.setReviewer(4280);
//		st.setReviewDate(makeDateFromString("2009-10-23"));
//		st.setTestingToComplyWithPTI(true);
//		stList.add(st);
//
//		st = new AppendixK();
//		st.setFacilityId(facility.getFacilityId());
//		st.setFacilityNm(facility.getName());
//		st.setAppendixKId(appendixKId++);
//		st.setSiteVisitId(100);
//		st.setEuId("P038");
//		st.setEuDescription("Gundgeon Thermfire (72 tons per day) thermal sand reclamation unit controlled by a fabric filter baghouse. The terms of this permit supercede those identified in PTI 01-08850 issued July 29, 2004");
//		st.setScheduledDate(makeDateFromString("2009-08-28"));
//		st.setTestDate(makeDateFromString("2009-08-28"));
//		st.setReceivedDate(makeDateFromString("2009-10-06"));
//		st.setCompany("Air Compliance Testing, Inc.");
//		st.setScc("30400350");
//		st.setStackTestMethodCd("5");
//		pollutantList = new ArrayList<String>();
//		pollutantList.add(PollutantDef.PE_CD);
//		st.setPollutants(pollutantList);
//		st.setMonitoringEquipUsed("pressure drop");
//		st.setControlEquipUsed("fabric filter baghouse");
//		st.setAllowableEmissionsRate("1.1 lb/hr");
//		st.setMaxOperatingRate("72 tons/day");
//		st.setAvgTestedEmissionsRate("0.42 lb/hr)");
//		st.setAvgTestedOperatingRate("2.76 tons/hr");
//		st.setWitnessed(true);
//		st.setAuditsSubmitted(true);
//		st.setAuditPassed("Y");
//		st.setTestResultsCd("P");
//		st.setConformedToMethod(true);
//		witnessList = new ArrayList<Integer>();
//		witnessList.add(4280);
//		st.setWitnesses(witnessList);
//		st.setReviewer(4280);
//		st.setReviewDate(makeDateFromString("2009-10-23"));
//		st.setTestingToComplyWithPTI(true);
//		stList.add(st);
//
//		st = new AppendixK();
//		st.setFacilityId(facility.getFacilityId());
//		st.setFacilityNm(facility.getName());
//		st.setAppendixKId(appendixKId++);
//		st.setAfsSentDate(makeDateFromString("2009-08-28"));
//		st.setAfsId(23145);
//		st.setSiteVisitId(101);
//		st.setEuId("P059");
//		st.setEuDescription("Virgin sand and reclaimed sand are mixed and transported to the mold machines, sand from the shakeout is put in the vibramills and sand reclaimer and sent to the reclaim silos");
//		st.setScheduledDate(makeDateFromString("2009-05-21"));
//		st.setTestDate(makeDateFromString("2009-05-21"));
//		st.setReceivedDate(makeDateFromString("2009-06-30"));
//		st.setCompany("Air Compliance Testing, Inc.");
//		st.setScc("30400720");
//		st.setStackTestMethodCd("9");
//		pollutantList = new ArrayList<String>();
//		pollutantList.add(PollutantDef.VOC_DSC);
//		st.setPollutants(pollutantList);
//		st.setMonitoringEquipUsed("pressure drop");
//		st.setControlEquipUsed("fabric filter baghouse");
//		st.setAllowableEmissionsRate("10% opacity");
//		st.setMaxOperatingRate("see memo");
//		st.setAvgTestedEmissionsRate("0% opacity");
//		st.setAvgTestedOperatingRate("13 tons/hr");
//		st.setWitnessed(true);
//		st.setAuditsSubmitted(true);
//		st.setAuditPassed("Y");
//		st.setTestResultsCd("P");
//		st.setConformedToMethod(true);
//		witnessList = new ArrayList<Integer>();
//		witnessList.add(4280);
//		witnessList.add(3913);
//		st.setWitnesses(witnessList);
//		st.setReviewer(3913);
//		st.setReviewDate(makeDateFromString("2009-08-11"));
//		st.setTestingToComplyWithPTI(true);
//		stList.add(st);
//		st.setMemo("Maximum amount of sand processed by emissions units P058, P059, P060, P061, P062, and P063 combined shall not exceed 146,333 tons per rolling 12-month summation.");
//
//		
//		appendixKMap.put(facility.getFacilityId(), stList);
//	}
//	
//	/**
//	 * 
//	 * @param facilityId
//	 * @return
//	 * @throws DAOException
//	 * 
//     * @ejb.interface-method view-type="remote"
//	 */
//	public List<AppendixK> searchAppendixKs(String facilityId, String facilityName, String doLaaCd,
//			String countyCd, String permittingClassCd, Timestamp beginTestDt, Timestamp endTestDt, 
//			Integer reviewer) throws DAOException {
//		List<AppendixK> result = new ArrayList<AppendixK>();
//		FacilityList[] facList = facilityDAO().searchFacilities(facilityName, facilityId, null, countyCd,
//                null, doLaaCd, null, permittingClassCd, null,  null, null, null, null, null, true);
//		for (FacilityList fl : facList) {
//			if (appendixKMap.get(fl.getFacilityId()) == null) {
//				createAppendixKsForFacility(fl);
//			}
//			for (AppendixK st : appendixKMap.get(fl.getFacilityId())) {
//				if (beginTestDt != null && beginTestDt.after(st.getTestDate())) {
//					continue;
//				}
//				if (endTestDt != null && endTestDt.before(st.getTestDate())) {
//					continue;
//				}
//				if (reviewer != null && !reviewer.equals(st.getReviewer())) {
//					continue;
//				}
//				result.add(st);
//			}
//		}
//		return result;
//	}
//	
//	/**
//	 * 
//	 * @param appendixKId
//	 * @return
//	 * @throws DAOException
//	 * 
//     * @ejb.interface-method view-type="remote"
//	 */
//	public AppendixK retrieveAppendixK(int appendixKId) throws DAOException {
//		// TODO make sure this method retrieves the actions for this stack test
//		// as well as the stack test data
//		AppendixK ret = null;
//		for (List<AppendixK> stList : appendixKMap.values()) {
//			for (AppendixK st : stList) {
//				if (st.getAppendixKId().equals(appendixKId)) {
//					ret = st;
//				}
//			}
//		}
//		if (ret == null) {
//			throw new DAOException("No stack test found for id " + appendixKId);
//		}
//		return ret;
//	}
//	
//	/**
//	 * 
//	 * @param st
//	 * @throws DAOException
//	 * 
//     * @ejb.interface-method view-type="remote"
//	 */
//	public AppendixK createAppendixK(AppendixK st) throws DAOException {
//		if (st.getFacilityId() != null) {
//			st.setAppendixKId(appendixKId++);
//			List<AppendixK> stList = appendixKMap.get(st.getFacilityId());
//			if (stList == null) {
//				stList = new ArrayList<AppendixK>();
//				appendixKMap.put(st.getFacilityId(), stList);
//			}
//			stList.add(st);
//			return st;
//		} else {
//			throw new DAOException("No facility id defined for stack test");
//		}
//	}
//	
//	/**
//	 * 
//	 * @param st
//	 * @throws DAOException
//	 * 
//     * @ejb.interface-method view-type="remote"
//	 */
//	public void modifyAppendixK(AppendixK st) throws DAOException {
//		AppendixK match = retrieveAppendixK(st.getAppendixKId());
//		match.copy(st);
//	}
}
