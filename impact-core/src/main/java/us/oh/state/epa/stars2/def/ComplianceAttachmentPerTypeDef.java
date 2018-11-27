package us.oh.state.epa.stars2.def;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class ComplianceAttachmentPerTypeDef {
    private static final String defName = "ComplianceAttachmentPerTypeDef";
    public static final String RO_SIGNATURE = "10";
    private static DefData excludeAQDTypeDefData;

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("cr_per_attachment_type_def", "per_attachment_type_cd", "per_attachment_type_dsc", "deprecated");
            cfgMgr.addDef(defName, data);
            
            excludeAQDTypeDefData = new DefData();
            DefSelectItems attachItems = data.getItems();
            String attachCode;
            for (SelectItem selItem:  attachItems.getAllItems()) {
            	attachCode = (String)selItem.getValue();
            	if (!DefData.isDapcAttachmentOnly(attachCode)) {
            		excludeAQDTypeDefData.addItem(attachCode, selItem.getLabel(), selItem.isDisabled());
            	} else {
            		excludeAQDTypeDefData.addItem(attachCode, selItem.getLabel(), true);
            	}
            }
        } 
        
        return data;
    }
    
    public static DefData getExcludeAQDTypeData() {
    	if (excludeAQDTypeDefData == null) {
    		getData();
    	}
    	return excludeAQDTypeDefData;
    }
}
