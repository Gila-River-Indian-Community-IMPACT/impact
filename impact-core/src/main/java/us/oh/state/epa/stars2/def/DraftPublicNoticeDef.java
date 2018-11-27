package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class DraftPublicNoticeDef {
    private static final String defName = "DraftPublicNoticeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("cm_wrapn_def", "wrapn_cd",
                            "default_public_notice_text", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static DefData getData(String type) {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
            data = new DefData();
            if(type != null && type.equals(PublicNoticeTypeDef.PSD_STANDARD_WORDING)){
                data.loadFromDB("cm_wrapn_def", "wrapn_cd",
                        "default_psd_public_notice_text", "deprecated");
            } else {
                data.loadFromDB("cm_wrapn_def", "wrapn_cd",
                        "default_public_notice_text", "deprecated");
            }

            cfgMgr.addDef(defName, data);
            
        return data;
    }

    public static String doSubstitution(UserDef user, String doLaaCd, String def) {

        // DOLAA_PERMIT_WRITER = first name and last name of the person assigned 
        // to the DO/LAA Permit Writer role in the facility inventory for the facility.
        String replacement = "WRITER_UNKNOWN";
        if (user != null){
            replacement = user.getUserFirstNm() +" "+ user.getUserLastNm();
        }
        def = def.replaceAll("DOLAA_PERMIT_WRITER", replacement);
        
        DoLaaDef doLaaDef = null;
        if (doLaaCd != null) {
            DefData doLaaData = DoLaaDef.getData();
            if (doLaaData != null) {
                doLaaDef = (DoLaaDef) doLaaData.getItem(doLaaCd);
            }
        }

        // DOLAA_LONG_NAME = the long name of the DO/LAA that has jurisdiction over 
        // that facility (you can tell by county location or the first two characters
        // of the facility ID).
        replacement = "DOLAA_UNKNOWN";
        if (doLaaDef != null) {
            replacement = doLaaDef.getDescription();
        }
        def = def.replaceAll("DOLAA_LONG_NAME", replacement);

        //DOLAA_ADDRESS = address (line 1 and line 2 if there is one), city, state, zip 
        // (If it is a 9 digit zip then #####-####).
        replacement = "ADDRESS_UNKNOWN";
        if (doLaaDef != null) {
            if(doLaaDef.getAddressLine1() != null){
            	replacement = doLaaDef.getAddressLine1();
        	}
            if (doLaaDef.getAddressLine2() != null 
                && doLaaDef.getAddressLine2().length() > 0) {
                replacement = replacement + " " + doLaaDef.getAddressLine2();
            }
            if (doLaaDef.getAddressLine3() != null 
            		&& doLaaDef.getAddressLine3().length() > 0) {
            	replacement = replacement + ", " + doLaaDef.getAddressLine3();
            }
        }
        def = def.replaceAll("DOLAA_ADDRESS", replacement);
        
        //DOLAA_PHONE = phone number for the DO/LAA (###) ###-replacment.
        replacement = "PHONE_UNKNOWN";
        if (doLaaDef != null) {
            replacement = doLaaDef.getPhoneNumber();
        }
        def = def.replaceAll("DOLAA_PHONE", replacement);
        
        return def;
    }
}
