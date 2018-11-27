package us.oh.state.epa.stars2.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import com.aspose.words.Document;
import com.aspose.words.License;

import us.oh.state.epa.stars2.bo.DocumentService;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

/*
 * The codes declared in this class must match the content of the
 * DC_TEMPLATE_DOC_TYPE_DEF table
 */
public class TemplateDocTypeDef {
    public static final String PTI_T_AND_C            = "11";
    public static final String PTI_DRAFT_ISSUANCE_PKG = "12";
    public static final String PTI_FINAL_ISSUANCE_PKG = "15";

    public static final String PTIO_T_AND_C            = "21";
    public static final String PTIO_DRAFT_ISSUANCE_PKG = "22";
    public static final String PTIO_FINAL_ISSUANCE_PKG = "25";

    public static final String TVPTO_T_AND_C            = "31";
    public static final String TVPTO_DRAFT_ISSUANCE_PKG = "32";
    public static final String TVPTO_PPP_ISSUANCE_PKG   = "35";
    public static final String TVPTO_PP_ISSUANCE_PKG    = "38";
    public static final String TVPTO_FINAL_ISSUANCE_PKG = "41";
    
    //public static final String TIVPTO_T_AND_C            = "51";
    //public static final String TIVPTO_DRAFT_ISSUANCE_PKG = "52";
    //public static final String TIVPTO_PPP_ISSUANCE_PKG   = "55";
    //public static final String TIVPTO_PP_ISSUANCE_PKG    = "58";
    //public static final String TIVPTO_FINAL_ISSUANCE_PKG = "59";

    public static final String ADDRESS_LABELS    = "67";
    public static final String PERMIT_INVOICE    = "68";
    public static final String FAX_COVER_SHEET   = "69";
    public static final String MULTIMEDIA_LETTER = "70";
    public static final String TV_FEE_INVOICE     = "71";
    public static final String SMTV_FEE_INVOICE  = "72";
    public static final String NTV_FEE_INVOICE    = "73";
    public static final String ITR_FAX_AND_ADDRESS_LABELS    = "360";
    public static final String DOR_MAILING_SHEETS    = "374";
    public static final String PTI_PID="376";
    public static final String PTI_PAA="09";
    public static final String PTI_CL="151";
    public static final String PTI_PND="159";
    public static final String PTI_CNL="160";
    public static final String PTI_RL="161";
    
    public static final String NSR_BILLING_INITIAL_INVOICE = "800";
    public static final String NSR_BILLING_FINAL_INVOICE = "801";
    
    public static final String EXEMPT_GROUPED_TIMESHEET = "802";
    public static final String NON_EXEMPT_GROUPED_TIMESHEET = "803";
    public static final String INSPECTION_REPORT = "500";
    public static final String INSPECTION_INFO_DOCUMENT = "501";

    private static Logger logger = Logger.getLogger(TemplateDocTypeDef.class);

    private static Map<String, List<SelectItem>> itemsMap;
    private static final long REFRESH_INTERVAL = 10 * 60 * 1000; // 10 minutes

    private static long refreshTime;
    private static final String defName = "TemplateDocTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("dc_template_doc_type_def", "template_doc_type_cd",
                            "template_doc_type_dsc", "deprecated","Template_Path");

            cfgMgr.addDef(defName, data);
        }
        return data;

    }

    public static List<SelectItem> getTemplates(String templateDocTypeCD) {
        if (System.currentTimeMillis() > refreshTime) {
            refresh();
        }

        return itemsMap.get(templateDocTypeCD);
    }

    public static TemplateDocument getTemplate(String templateDocTypeCD) {
        // commented the below lines to immediately refresh the templates whenever 
    	// they are updated
    	/*if (System.currentTimeMillis() > refreshTime) {
            refresh();
        }
        if (itemsMap.size() == 0)
            refresh();*/
        refresh();
        List<SelectItem> selectItems = itemsMap.get(templateDocTypeCD);
        if (selectItems == null || selectItems.isEmpty()) {
            return null;
        }

        return (TemplateDocument) selectItems.get(0).getValue();
    }

    private static void refresh() {

        TemplateDocument[] templates;

        try {
            DocumentService svc = ServiceFactory.getInstance()
                    .getDocumentService();
            templates = svc.getTemplateDocuments();
            logger.debug("templates.length: "+templates.length);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            templates = new TemplateDocument[0];
        }

        refreshTime = System.currentTimeMillis() + REFRESH_INTERVAL;

        itemsMap = new HashMap<String, List<SelectItem>>();
        for (TemplateDocument template : templates) {
            List<SelectItem> items = itemsMap.get(template
                    .getTemplateDocTypeCD());
            if (items == null) {
                items = new ArrayList<SelectItem>();
                itemsMap.put(template.getTemplateDocTypeCD(), items);
            }
            logger.debug("template.getTemplateDocTypeCD(): "+template.getTemplateDocTypeCD());            
            logger.debug("template.getTemplateDocTypeDsc(): "+template.getTemplateDocTypeDsc());
            items.add(new SelectItem(template, template.getTemplateDocTypeDsc()));
           
        }
    }

    /**
     * @param issuanceType
     * @return
     * @throws DAOException
     * 
     */
    public static String getPTIO_INTRO(String issuanceType) throws DAOException {

        if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.Draft)) {
            return PTIO_DRAFT_ISSUANCE_PKG;
        } else if (issuanceType.equalsIgnoreCase(PermitIssuanceTypeDef.Final)) {
            return PTIO_FINAL_ISSUANCE_PKG;
        } else {
            throw new DAOException("Wrong issuance type for PTIO INTRO");
        }
    }
    
    

	/**
	 * Returns a given template as an aspose document
	 * 
	 * @param templateDocTypeCd
	 * @return Document
	 */
	public static Document getTemplateAsAsposeDocument(final String templateDocTypeCd) {

		Document template = null;

		if (!Utility.isNullOrEmpty(templateDocTypeCd)) {

			TemplateDocument templateDoc = TemplateDocTypeDef.getTemplate(templateDocTypeCd);

			if (null != templateDoc && !Utility.isNullOrEmpty(templateDoc.getTemplateDocPath())) {

				String absTemplatePath = DocumentUtil.getFileStoreRootPath() + templateDoc.getTemplateDocPath();

				try {
					License license = new License();
					license.setLicense("../classes/Aspose.Words.lic");
					template = new Document(absTemplatePath);
				} catch (Exception e) {
					logger.error("Could not read the template document " + absTemplatePath, e);
				}
			}
		}

		return template;
	}
}
