package us.oh.state.epa.stars2.webcommon.reports;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.Emissions;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.PollutantPair;
import us.oh.state.epa.stars2.def.EmissionUnitReportingDef;
import us.oh.state.epa.stars2.def.NonToxicPollutantDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

@SuppressWarnings("serial")
public class PollutantPartOf implements java.io.Serializable {
	
    private Logger logger = Logger.getLogger(PollutantPartOf.class);
    private Map<String, Set<String>> subsets = new HashMap<String, Set<String>>();
    
    // Hack to cache data from rp_part_of table. Otherwise this full
    // table is loaded each time a pollutant must be checked in bulk 
    // EI imports (and maybe other places). The cache is refreshed if 
    // it is more than an hour old.
	private static PollutantPair[] pairs = null;
	private static Timestamp pairAge = null;

    public String verifySubpartTotals(Emissions reference,
            HashMap<String, Emissions> map) {
        try {
			mapSubsets();
		} catch (Exception e) {
			return e.getMessage();
		}
        String rtn = null;
        boolean moreThanOne = false;
        StringBuffer b = new StringBuffer(150);
        if(null != reference.getFugitiveEmissions() &&
                null != reference.getStackEmissions()){
            double rf = EmissionsReport.convertStringToNum(reference.getFugitiveEmissions(), logger);
            double rs = EmissionsReport.convertStringToNum(reference.getStackEmissions(), logger);
            double v = EmissionUnitReportingDef.convert(
                    reference.getEmissionsUnitNumerator(),
                    rf + rs,
                    EmissionUnitReportingDef.TONS);
            Set<String> sP = subsets.get(reference.getPollutantCd());

            if (null != sP) {
                Emissions row = null;

                for(String p : sP){
                    row = map.get(p);
                    if(null != row && p.equals(row.getPollutantCd())){
                        if(null == row.getStackEmissions() &&
                                null == row.getFugitiveEmissions()) {
                            continue;
                        }
                        double f = EmissionsReport.convertStringToNum(row.getFugitiveEmissions(), logger);
                        double s = EmissionsReport.convertStringToNum(row.getStackEmissions(), logger);
                        double total = EmissionUnitReportingDef.convert(
                                row.getEmissionsUnitNumerator(),
                                f + s,
                                EmissionUnitReportingDef.TONS);

                        if(v < total) {
                            if(b.length() > 0) {
                                b.append(",  ");
                                moreThanOne = true;
                            }
                            b.append("<");
                            b.append(NonToxicPollutantDef.getTheDescription(p));
                            b.append(">");
                        }

                    }
                }

            }
        }
        if(b.length() > 0) {
            if(moreThanOne){
                rtn = "cannot be less than any of " + b;
            } else {
                rtn = "cannot be less than " + b;
            }
        }
        return rtn;
    }
    
	private void mapSubsets() throws Exception {
		
        try {
        	PollutantPair[] pairs = retrievePollutantPairs();
            // TODO: verify adequate cleanup
            visited.clear();
            subsets.clear();
            
            Map<String, Set<String>> directSubsets = new HashMap<String, Set<String>>();
            for (int i = 0; i < pairs.length; i++) {
            	String sup = pairs[i].getGroupCd();
                Set<String> subs = new HashSet<String>();
                directSubsets.put(sup, subs);
                for(int j=0; j < pairs.length; j++){
                    if(sup.equals(pairs[j].getGroupCd())){
                        subs.add(pairs[j].getPollutantCd());
                    }
                }
            }
            
            Map<String, Set<String>> allSubsets = new HashMap<String, Set<String>>();
            for (String sup : directSubsets.keySet()) {
                mapTransitiveSubsets(directSubsets, allSubsets, sup);
            }
            subsets = allSubsets;
            
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
            DisplayUtil
            .displayError("Major application error, please notify system administrator");
        } catch (DAOException e) {
            logger.error(e.getMessage(), e);
            DisplayUtil
            .displayError("Major application error, please notify system administrator");
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil
            .displayError("Major application error, please notify system administrator");
        }
	}
	
	private static synchronized PollutantPair[] retrievePollutantPairs() throws Exception {

		if (pairs != null && pairAge.after(new Timestamp(System.currentTimeMillis() - 6000))) {
			return pairs;
		}

		EmissionsReportService emissionsReportBO = ServiceFactory.getInstance().getEmissionsReportService();
		pairs = emissionsReportBO.retrievePollutantPartOf();
		pairAge = new Timestamp(System.currentTimeMillis());
		
		return pairs;
	}
	
	public Map<String, HashSet<String>> mapSupersets() throws Exception {
		
		Map<String, HashSet<String>> supersets = new HashMap<String, HashSet<String>>();
        try {
            EmissionsReportService emissionsReportBO =
                ServiceFactory.getInstance().getEmissionsReportService();
            PollutantPair[] pairs =
                emissionsReportBO.retrievePollutantPartOf();
            // TODO: verify adequate cleanup
            visited.clear();
            supersets.clear();
            
            Map<String, HashSet<String>> directSupersets = new HashMap<String, HashSet<String>>();
            for (int i = 0; i < pairs.length; i++) {
            	String sub = pairs[i].getPollutantCd();
            	HashSet<String> sups = new HashSet<String>();
                directSupersets.put(sub, sups);
                for(int j=0; j < pairs.length; j++){
                    if(sub.equals(pairs[j].getPollutantCd())){
                        sups.add(pairs[j].getGroupCd());
                    }
                }
            }
            
            Map<String, HashSet<String>> allSupersets = new HashMap<String, HashSet<String>>();
            for (String sub : directSupersets.keySet()) {
                mapTransitiveSupersets(directSupersets, allSupersets, sub);
            }
            supersets = allSupersets;
            
        } catch (ServiceFactoryException sfe) {
            logger.error(sfe.getMessage(), sfe);
            DisplayUtil
            .displayError("Major application error, please notify system administrator");
        } catch (DAOException e) {
            logger.error(e.getMessage(), e);
            DisplayUtil
            .displayError("Major application error, please notify system administrator");
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            DisplayUtil
            .displayError("Major application error, please notify system administrator");
        }
        return supersets;
	}

	private Stack<String> visited = new Stack<String>();
	
	HashSet<String> mapTransitiveSubsets(Map<String, Set<String>> direct, 
			Map<String, Set<String>> all, String sup) throws Exception {
		checkForCycle(sup);
		visited.push(sup);
		HashSet<String> allsubs = new HashSet<String>();
		all.put(sup, allsubs);
		Set<String> subs = direct.get(sup);
		for (String sub : subs) {
			allsubs.add(sub);
			if (direct.containsKey(sub)) {
				allsubs.addAll(mapTransitiveSubsets(direct,all,sub));
			}
		}
		visited.pop();
		return allsubs;
	}
	
	HashSet<String> mapTransitiveSupersets(Map<String, HashSet<String>> direct, 
			Map<String, HashSet<String>> all, String sub) throws Exception {
		checkForCycle(sub);
		visited.push(sub);
		HashSet<String> allsups = new HashSet<String>();
		all.put(sub, allsups);
		HashSet<String> sups = direct.get(sub);
		for (String sup : sups) {
			allsups.add(sup);
			if (direct.containsKey(sup)) {
				allsups.addAll(mapTransitiveSupersets(direct,all,sup));
			}
		}
		visited.pop();
		return allsups;
	}

	private void checkForCycle(String sup) throws Exception {
		if (visited.contains(sup)) {
			StringBuffer msg = new StringBuffer();
			msg.append("Major application error, please notify system administrator.  Invalid pollutant set relationship.  ");
			msg.append("Cycle detected in pollutant superset/subset graph: ");
			for (String s : visited) {
				msg.append(s + " -> ");
			}
			msg.append(sup);
			throw new Exception(msg.toString());
		}
	}
	
	public static PollutantDef retrieveParent(String subCode) {
		
		PollutantDef ret = null;

		try {
			EmissionsReportService emissionsReportBO = ServiceFactory.getInstance().getEmissionsReportService();
			PollutantPair pair = emissionsReportBO.retrievePollutantParent(subCode);
			if (pair != null && pair.getGroupCd() != null) {
				ret = PollutantDef.getPollutantBaseDef(pair.getGroupCd());
			}
		} catch (DAOException daoe) {

		} catch (ServiceFactoryException sfe) {

		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ret;

	}

}


