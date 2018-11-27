package us.oh.state.epa.stars2.def;

import java.lang.ref.SoftReference;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

/*
 * The codes declared in this class must match the content of the
 * pt_reason_def table.
 */
public class PermitReasonsDef {
    //public static final String INITIAL_INSTALLATION = "II";
    public static final String INITIAL = "I";
    public static final String RENEWAL = "R";
    public static final String SPM = "S";
    //public static final String STATE_MOD = "SM";
    public static final String REOPENING = "RO";
    //public static final String REVOKE_REISSUE = "RR";
    public static final String MPM = "M";
    public static final String APA = "AP";
    //public static final String OFF_PERMIT_CHANGE = "O";
    public static final String CHANGE_502_B_10 = "502";
    //public static final String CHAPTER_31_MOD = "C";
    //public static final String ADMIN_MOD = "A";
    public static final String NOT_ASSIGNED = "NA";
    public static final String CONSTRUCTION = "CON";
    public static final String SYNTHETIC_MINOR = "SYM";
    public static final String MODIFICATION = "MOD";
    public static final String TEMPORARY_PERMIT = "TEP";
    public static final String RECONSTRUCTION = "REC";
    public static final String OTHER = "OTH";
    // public static final String RESCIND = "RS";
    private static Logger logger = Logger.getLogger(PermitReasonsDef.class);
    private static List<String> modReasons;
    private static SoftReference<HashMap<String, SimpleDef[]>> srPerTypeReasons;
    private static final String defName = "PermitReasonsDef";
    private static final String defNSRName = "PermitReasonsDefNSR";
    private static final String defTVPTOName = "PermitReasonsDefTVPTO";    
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("pt_reason_def", "reason_cd",
                            "reason_dsc", "deprecated", "line_order");

            cfgMgr.addDef(defName, data);
        }

        return data;
    }
    
    public static DefData getNSRData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defNSRName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PermitSQL.retrieveNSRPermitReasons", 
            		SimpleDef.class);
            cfgMgr.addDef(defNSRName, data);
        } 
        return data;
    }
    
    public static DefData getTVPTOData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defTVPTOName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PermitSQL.retrieveTVPTOPermitReasons", 
            		SimpleDef.class);
            cfgMgr.addDef(defTVPTOName, data);
        } 
        return data;
    }
    
    public static List<SelectItem> getAllPermitReasons() {
        return getData().getItems().getAllItems();
    }
    
    public static List<SelectItem> getPermitReasonsByPermitType(String permitType){
        if (permitType == null) {
            return getAllPermitReasons();
        } 
        List<SelectItem> ret = new ArrayList<SelectItem>();
        if (permitType.equals(PermitTypeDef.NSR)){
        	ret = getNSRPermitReasons();
        } else if (permitType.equals(PermitTypeDef.TV_PTO)){
        	ret = getTVPTOPermitReasons();
        }
        return ret;
    }
    
    public static List<SelectItem> getNSRPermitReasons(){
    	return getNSRData().getItems().getAllItems();
    }
    
    public static List<SelectItem> getTVPTOPermitReasons(){
    	return getTVPTOData().getItems().getAllItems();
    }

    public static List<SelectItem> getPermitReasons(String permitType,
                                                    List<String> selectedCDs) {

        List<SelectItem> permitReasons = new ArrayList<SelectItem>();
        //List<SelectItem> permitReasons = getAllPermitReasons();
        Map<String, SelectItem> reasonsMap = new LinkedHashMap<String, SelectItem>();

        if (permitType == null) {
            return getAllPermitReasons();
        } 
        List<SelectItem> temp = getPermitReasonsByPermitType(permitType);

        for (SelectItem i : temp){
        	SelectItem si = new SelectItem(i.getValue(), i.getLabel(), i.getDescription(), i.isDisabled());
        	if (selectedCDs!=null && selectedCDs.contains(si.getValue())){
        		si.setDisabled(false);
        	}
        	permitReasons.add(si);
        	reasonsMap.put((String)si.getValue(), si);
        }

//        try {
//
//            HashMap<String, SimpleDef[]> perTypeMap 
//                = (srPerTypeReasons == null) ? null : srPerTypeReasons.get();
//            if (perTypeMap == null) {
//                perTypeMap = new HashMap<String, SimpleDef[]>();
//                srPerTypeReasons = new SoftReference<HashMap<String, SimpleDef[]>>(perTypeMap);
//            }
//            
//            SimpleDef[] tempArray = perTypeMap.get(permitType);
//            if (tempArray == null) {
//                tempArray  = ServiceFactory.getInstance().getPermitService().retrievePermitReasons(permitType);
//                perTypeMap.put(permitType, tempArray);
//            }
        	

//            for (SimpleDef tempState : tempArray) {
//                SelectItem si = new SelectItem(tempState.getCode(), tempState.getDescription());
//                // exclude renewal from TVPTI permit reason list
//                //if (PermitTypeDef.TVPTI.equals(permitType) && 
//                //        PermitReasonsDef.RENEWAL.equals(tempState.getCode())) {
//                //    continue;
//                //}
//                permitReasons.add(si);
//                reasonsMap.put(tempState.getCode(), si);
//            }
            
//            if(!permitType.equalsIgnoreCase(PermitTypeDef.NSR)) {
//            	SelectItem na = reasonsMap.get(NOT_ASSIGNED);
//                if (na != null)
//                    na.setDisabled(true);
//                else{
//                    na = new SelectItem(NOT_ASSIGNED, "Not yet assigned");
//                    na.setDisabled(true);
//                    reasonsMap.put(NOT_ASSIGNED, na);
//                    permitReasons.add(na);
//                }
//            }
//        }
//        catch (ServiceFactoryException sfe) {
//            logger.error(sfe.getMessage(), sfe);
//            DisplayUtil.displayError("System error. Please contact system administrator");
//        }
//        catch (RemoteException re) {
//            logger.error(re.getMessage(), re);
//            DisplayUtil.displayError("System error. Please contact system administrator");
//        }

        if (selectedCDs != null) {
            if (permitType.equalsIgnoreCase(PermitTypeDef.NSR)) {
                disableNotAllowedPTIO(selectedCDs, reasonsMap);
            }
            //else if (permitType.equalsIgnoreCase(PermitTypeDef.TVPTI)) {
            //    disableNotAllowedTVPTI(selectedCDs, reasonsMap);
            //}
            else if (permitType.equalsIgnoreCase(PermitTypeDef.TV_PTO) 
            		//||
                    //permitType.equalsIgnoreCase(PermitTypeDef.TIV_PTO)
                    ) {
                disableNotAllowedTVPTO(selectedCDs, reasonsMap);
            }
        }
        return permitReasons;
    }

    public static String getInvalidDescription(String reasonCD,
                                               String permitType) {

        if (permitType == null || permitType.length() < 1
            || PermitTypeDef.RPR.equalsIgnoreCase(permitType)
            //|| PermitTypeDef.RPE.equalsIgnoreCase(permitType)
            ) {
            return null;
        }
            
//        try {
//
//            HashMap<String, SimpleDef[]> perTypeMap 
//                = (srPerTypeReasons == null) ? null : srPerTypeReasons.get();
//            if (perTypeMap == null) {
//                perTypeMap = new HashMap<String, SimpleDef[]>();
//                srPerTypeReasons = new SoftReference<HashMap<String, SimpleDef[]>>(perTypeMap);
//            }
//            
//            SimpleDef[] tempArray = perTypeMap.get(permitType);
//            if (tempArray == null) {
//                tempArray  = ServiceFactory.getInstance().getPermitService().retrievePermitReasons(permitType);
//                perTypeMap.put(permitType, tempArray);
//            }
//
//            for (SimpleDef code : tempArray) {
//                if (code.getCode().equals(reasonCD)) {
//                    return null;
//                }
//            }
        	List<SelectItem> temp = getPermitReasonsByPermitType(permitType);
        	for (SelectItem si : temp) {
                if (((String) si.getValue()).equals(reasonCD)) {
                    return null;
                }
            }
            getAllPermitReasons();
            for (SelectItem si : getAllPermitReasons()) {
                if (((String) si.getValue()).equals(reasonCD)) {
                    return si.getLabel();
                }
            }
            return "Unknown reason code";
//        }
//        catch (ServiceFactoryException sfe) {
//            logger.error(sfe.getMessage(), sfe);
//            DisplayUtil.displayError("System error. Please contact system administrator");
//        }
//        catch (RemoteException re) {
//            logger.error(re.getMessage(), re);
//            DisplayUtil.displayError("System error. Please contact system administrator");
//        }

//        return null;
    }

    public static List<String> getReasonDescriptions(List<String> reasonCDs,
                                                     String permitType) {

        init();
        ArrayList<String> descriptions = new ArrayList<String>();

//        try {
//
//            HashMap<String, SimpleDef[]> perTypeMap 
//                = (srPerTypeReasons == null) ? null : srPerTypeReasons.get();
//            if (perTypeMap == null) {
//                perTypeMap = new HashMap<String, SimpleDef[]>();
//                srPerTypeReasons = new SoftReference<HashMap<String, SimpleDef[]>>(perTypeMap);
//            }
//            
//            SimpleDef[] tempArray = perTypeMap.get(permitType);
//            if (tempArray == null) {
//                tempArray  = ServiceFactory.getInstance().getPermitService().retrievePermitReasons(permitType);
//                perTypeMap.put(permitType, tempArray);
//            }

        List<SelectItem> temp = getPermitReasonsByPermitType(permitType);
        
        for (String reasonCd : reasonCDs) {
        	for (SelectItem si : temp) {
        		if (si.getValue().equals(reasonCd)) {
        			descriptions.add(si.getLabel());
        			break;
        		}
        	}
        }
//        }
//        catch (ServiceFactoryException sfe) {
//            logger.error(sfe.getMessage(), sfe);
//            DisplayUtil.displayError("System error. Please contact system administrator");
//        }
//        catch (RemoteException re) {
//            logger.error(re.getMessage(), re);
//            DisplayUtil.displayError("System error. Please contact system administrator");
//        }

        return descriptions;
    }

    /**
     * @param selectedCDs
     * @return
     * 
     */
    // TO DO verify ... not sure the purpose of this
    private static void disableNotAllowedTVPTO(List<String> selectedCDs,
                                               Map<String, SelectItem> reasonsMap) {
        if (selectedCDs.contains(INITIAL)) {
            disabledOther(reasonsMap, INITIAL);
        }
        else if (selectedCDs.contains(RENEWAL)) {
            disabledOther(reasonsMap, RENEWAL);
        }
        else if (!selectedCDs.contains(NOT_ASSIGNED)) {
            reasonsMap.get(INITIAL).setDisabled(true);
            reasonsMap.get(RENEWAL).setDisabled(true);
        }
    }

    private static void disabledOther(Map<String, SelectItem> reasonsMap,
                                      String key) {
        for (String k : reasonsMap.keySet()) {
            if (!k.equalsIgnoreCase(key))
                reasonsMap.get(k).setDisabled(true);
        }
    }

    /**
     * @param selectedCDs
     * @return
     * 
     */
    /*
    private static void disableNotAllowedTVPTI(List<String> selectedCDs,
                                               Map<String, SelectItem> reasonsMap) {

        if (selectedCDs.contains(INITIAL_INSTALLATION)) {
            disabledOther(reasonsMap, INITIAL_INSTALLATION);
            // reasonsMap.get(ADMIN_MOD).setDisabled(true);
            selectedCDs.remove(ADMIN_MOD);
        }
        if (selectedCDs.contains(ADMIN_MOD)) {
            disabledOther(reasonsMap, ADMIN_MOD);
            // reasonsMap.get(INITIAL_INSTALLATION).setDisabled(true);
            selectedCDs.remove(INITIAL_INSTALLATION);
        }
        if (selectedCDs.contains(CHAPTER_31_MOD)) {
            disabledOther(reasonsMap, CHAPTER_31_MOD);
        }
        if (reasonsMap.get(RENEWAL) != null) {
            reasonsMap.get(RENEWAL).setDisabled(true);
        }
    }
*/
    private static void init() {

        modReasons = new ArrayList<String>();
        modReasons.add(SPM);
        modReasons.add(REOPENING);
        //modReasons.add(REVOKE_REISSUE);
        modReasons.add(MPM);
        modReasons.add(APA);
        //modReasons.add(OFF_PERMIT_CHANGE);
        //modReasons.add(CHAPTER_31_MOD);
        //modReasons.add(ADMIN_MOD);
        modReasons.add(CHANGE_502_B_10);

    }

    /**
     * @param selectedCDs
     * @return
     * 
     */
    private static void disableNotAllowedPTIO(List<String> selectedCDs,
                                              Map<String, SelectItem> reasonsMap) {
        if (selectedCDs.contains(CONSTRUCTION)) {
            disabledOther(reasonsMap, CONSTRUCTION);
        }
        if (selectedCDs.contains(SYNTHETIC_MINOR)) {
            disabledOther(reasonsMap, SYNTHETIC_MINOR);
        }
        if (selectedCDs.contains(MODIFICATION)) {
            disabledOther(reasonsMap, MODIFICATION);
        }
        if (selectedCDs.contains(TEMPORARY_PERMIT)) {
            disabledOther(reasonsMap, TEMPORARY_PERMIT);
        }
        if (selectedCDs.contains(RECONSTRUCTION)) {
            disabledOther(reasonsMap, RECONSTRUCTION);
        }
        if (selectedCDs.contains(OTHER)) {
            disabledOther(reasonsMap, OTHER);
        }
    }

    public static boolean checkPrimary(String oldReason, String newReason) {

        getAllPermitReasons();
        int oldIndex = -1;
        int newIndex = -1;
        List<SelectItem> reasons = getAllPermitReasons();
        int i = 0;
        for (SelectItem reasonCD : reasons) {
            if (reasonCD.getValue().equals(oldReason)) {
            	oldIndex = i;
               //oldIndex = reasons.indexOf(reasonCD);
            }
            if (reasonCD.getValue().equals(newReason)) {
            	newIndex = i;
                //newIndex = reasons.indexOf(reasonCD);
            }
            i++;
        }
        if (newIndex == -1) {
            return false;
        }
        return ((oldIndex > newIndex) ? true : false);
    }

    public static boolean isModReason(String primaryReasonCD) {
        init();
        return modReasons.contains(primaryReasonCD);
    }

}
