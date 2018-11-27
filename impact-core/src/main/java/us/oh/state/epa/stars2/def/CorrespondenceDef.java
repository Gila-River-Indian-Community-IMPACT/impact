package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * Provides an allowed list of types for the CORRESPONDENCE_TYPE_CD field
 * of the DC_CORRESPONDENCE table.  This field describes the type of correspondence
 * sent by a STARS2 application.  The codes declared in this class must match
 * the content of the DC_CORRESPONDENCE_TYPE_DEF table.
 */
public class CorrespondenceDef {

    private static final String defName = "CorrespondenceDef";
    private static final String defSavedDocName = "CorrespondenceSavedDocDef";
    private static final String deflinkedToTypeName = "CorrespondenceLinkedToTypeDef";
    private static final String defTemplateDocTypeCd = "CorrespondenceTemplateDocTypeCd";
    
    /** Code -> Description match where desc is CORRESPONDENCE_TYPE_DSC. */
    public static DefData getDescriptionData() {
        return getData();
    }

    /**
     * Code -> Description match where desc is SAVED_DOC_REQD. Describes whether
     * an application must also save a copy of a document when writing a new row
     * in the DC_CORRESPONDENCE table.
     */
    public static DefData getSavedDocReqdData() {

        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defSavedDocName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("dc_correspondence_type_def",
                            "correspondence_type_cd", "saved_doc_reqd",
                            "deprecated");

            cfgMgr.addDef(defSavedDocName, data);
        }
        return data;
    }
    
    public static DefData getLinkedToTypeData() {

        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(deflinkedToTypeName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("dc_correspondence_type_def",
                            "correspondence_type_cd", "linked_to_type_cd",
                            "deprecated");

            cfgMgr.addDef(deflinkedToTypeName, data);
        }
        return data;
    }
    
    public static DefData getTemplateDocTypeCdData() {

        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defTemplateDocTypeCd);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("dc_correspondence_type_def",
                            "correspondence_type_cd", "template_doc_type_cd",
                            "deprecated");

            cfgMgr.addDef(defTemplateDocTypeCd, data);
        }
        return data;
    }
    
	/**
	 * This method generally loads definition data; however, this one will
	 * actually reset our template doc definition data. This could be expanded
	 * to reset more as necessary.
	 * 
	 * @return null
	 */
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        if (data == null) {
            data = new DefData();
            data.loadFromDB("dc_correspondence_type_def",
                            "correspondence_type_cd", "correspondence_type_dsc",
                            "deprecated");
            cfgMgr.addDef(defName, data);
        }
        
		DefData docTemplateDefData = cfgMgr.getDef(defTemplateDocTypeCd);
		if (docTemplateDefData != null) {
			docTemplateDefData.reload();
		}

		return data;
	}
}
