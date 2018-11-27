package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class FceAttachmentTypeDef {
	
	public static final String INSPECTION_REPORT = "IR";
	public static final String INSPECTION_REPORT_APPENDICES = "IRA";
	public static final String INSPECTION_SIGNATURE_PAGE = "ISP";
	public static final String OTHER = "OTH";
	public static final String PHOTO = "PHO";
	
    private static final String defName = "FceAttachmentTypeDef";
    private static final String defDocumentTemplate = "FceAttachmentTemplateTypeDef";

    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_FCE_ATTACHMENT_TYPE_DEF", "fce_attachment_type_cd", 
            		"fce_attachment_type_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static DefData getDocumentTemplateData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defDocumentTemplate);

        if (data == null) {
            data = new DefData();
            data.loadFromDB("CE_FCE_ATTACHMENT_TYPE_DEF", "fce_attachment_type_cd", 
            		"document_id", "deprecated");

            cfgMgr.addDef(defDocumentTemplate, data);
        }
        return data;
    }

}
