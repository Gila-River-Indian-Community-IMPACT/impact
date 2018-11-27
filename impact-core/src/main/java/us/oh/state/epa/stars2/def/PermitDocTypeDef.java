package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

/*
 * The codes declared in this class must match the content of the
 * PT_PERMIT_DOC_TYPE_DEF table
 */
public class PermitDocTypeDef extends SimpleDef {
    public static final String ISSUANCE_DOCUMENT = "D";
    public static final String INTRO_PACKAGE = "I";
    public static final String TERMS_CONDITIONS = "T";
    public static final String PREVIOUSLY_ISSUED = "PI";
    public static final String DRAFT_STATEMENT_BASIS = "DSB";
    public static final String PROPOSED_STATEMENT_BASIS = "PSB";
    public static final String FINAL_STATEMENT_BASIS = "FSB";
    public static final String DRAFT_TV_PERMIT_DOCUMENT = "DTVP";
    public static final String PROPOSED_TV_PERMIT_DOCUMENT = "PTVP";
    public static final String FINAL_TV_PERMIT_DOCUMENT = "FTVP";
    public static final String FORM_B = "B";
    public static final String PUBLIC_NOTICE = "N";
    public static final String HEARING_NOTICE = "H";
    public static final String INSTALLATION_CERTIFICATE = "C";
    public static final String ATTACHMENT = "A";
    public static final String NSR_RESPONSE_TO_COMMENTS = "NRCM";
    public static final String TV_RESPONSE_TO_COMMENTS = "TRCM";
    public static final String PERMIT_STRATEGY_SUMMARY_WRITE_UP = "PS";
    public static final String ADDRESS_LABELS = "L";
    public static final String INVOICE = "V";
    public static final String FAX_COVER_SHEET = "FAX";
    public static final String MULTIMEDIA_LETTER = "MML";
    public static final String NSR_COMPLETENESS_LETTER = "NCL";
    public static final String TV_COMPLETENESS_LETTER = "TVCL";
    public static final String EPA_RECEIPT_NOTIFICATION ="ERN";
    public static final String FLM_RECEIPT_NOTIFICATION ="FRN";
    public static final String EPA_DRAFT_NOTIFICATION = "EDN";
    public static final String FLM_DRAFT_NOTIFICATION ="FDN";
    public static final String ANALYSIS_DOCUMENT = "AD";
	public static final String EPA_TRANSMITTAL_LETTER = "ETLF";
    public static final String FLM_TRANSMITTAL_LETTER ="FTN";
    public static final String COMPANY_COVER_LETTER_DRAFT ="CCLD";
    public static final String COMPANY_COVER_LETTER_PROPOSED ="CCLP";
    public static final String COMPANY_COVER_LETTER_FINAL ="CCLF";
    public static final String NEWS_PAPER_AFFADAVIT ="NPA";
    public static final String COUNTY_CLERK_TRANSMITTAL_LETTER ="CTL";
    public static final String PERMIT_DOCUMENT ="PD";
    public static final String NSR_RECEIPT_LETTER ="NRL";
    public static final String TV_RECEIPT_LETTER ="TVRL";
    public static final String INACTIVE_WITHDRAWN_DOCUMENT ="IWD";
    public static final String TV_COMMENTS = "TCOM";
    public static final String NSR_COMMENTS = "NCOM";
    public static final String PERMIT_DOCUMENT_INFO ="PDO";
    public static final String PERMIT_DOCUMENT_PAA ="PAA";
    
    public static final String NSR_FINAL_PERMIT_WAIVER_PACKAGE = "FPP";
    public static final String EPA_COVER_LETTER_PP = "ECLP";
    public static final String NSR_PUBLIC_NOTICE_DOCUMENT = "NPND";
    public static final String TV_PUBLIC_NOTICE_DOCUMENT = "TPND";
    public static final String COMMENT_TRANSMITTAL_LETTER = "COTL";
    
    public static final String INITIAL_INVOICE = "IINV";
    public static final String FINAL_INVOICE = "FINV";
    public static final String SPECIAL_INVOICE = "SINV";
    
    private static final String defName = "PermitDocTypeDef";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("PermitSQL.retrieveDocumentTypes", 
            		PermitDocTypeDef.class);

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static List<SelectItem> getFixedData() {
    	List<SelectItem> si = new ArrayList<SelectItem>();
    	DefSelectItems permitDocTypeDefs =  getData().getItems();
    	for (SelectItem item : permitDocTypeDefs.getCurrentItems()) {
    		PermitDocTypeDef permitDocTypeDef = (PermitDocTypeDef) permitDocTypeDefs
    				.getItem(item.getValue().toString());
    		if (permitDocTypeDef.isFixed()) {
    			si.add(item);

    		}
    	}
     	return si;
    }
    public static List<SelectItem> getDynamicData(String permitType, String currentPage, PermitDocument singleDoc){
    	List<SelectItem> si = new ArrayList<SelectItem>();
    	DefSelectItems permitDocTypeDefs =  getData().getItems();
    	for (SelectItem item : permitDocTypeDefs.getCurrentItems()) {
    		PermitDocTypeDef permitDocTypeDef = (PermitDocTypeDef) permitDocTypeDefs
    				.getItem(item.getValue().toString());
    		if (singleDoc != null)
    		{
    			if (singleDoc.getPermitDocTypeCD() != null && permitDocTypeDef.getPermitDocTypeCD().equalsIgnoreCase(singleDoc.getPermitDocTypeCD() ))
    			{
    				si.add(item);
    				continue;
    			}
    		} 
    		if (!permitDocTypeDef.isFixed()) {
    			if (permitType.equalsIgnoreCase(PermitTypeDef.NSR))
    			{
    				if (permitDocTypeDef.isNsr())
    				{
    					if (currentPage != null && currentPage.equalsIgnoreCase("permitProfile"))
    					{
    						if (permitDocTypeDef.isPermitDetail()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.draftIssuance"))
    					{
    						if (permitDocTypeDef.isDraftPublication()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.PPIssuance"))
    					{
    						if (permitDocTypeDef.isPpPublication()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.finalIssuance"))
    					{
    						if (permitDocTypeDef.isFinalIssuance()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.denialIssuance"))
    					{
    						if (permitDocTypeDef.isWithdrawalIssuance()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.feeSummary"))
    					{
    						if (permitDocTypeDef.isFeeSummary()) {
    							si.add(item);
    						}
    						continue;
    					}


    				}
    			}
    			else
    			{
    				if (permitDocTypeDef.isTitleV())
    				{
    					if (currentPage != null && currentPage.equalsIgnoreCase("permitProfile"))
    					{
    						if (permitDocTypeDef.isPermitDetail()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.draftIssuance"))
    					{
    						if (permitDocTypeDef.isDraftPublication()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.PPIssuance"))
    					{
    						if (permitDocTypeDef.isPpPublication()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.finalIssuance"))
    					{
    						if (permitDocTypeDef.isFinalIssuance()) {
    							si.add(item);
    						}
    						continue;
    					} else if (currentPage != null && currentPage.equalsIgnoreCase("permits.detail.denialIssuance"))
    					{
    						if (permitDocTypeDef.isWithdrawalIssuance()) {
    							si.add(item);
    						}
    						continue;
    					}

    				}
    			
    			}



    		}
    	}
    	return si;
    }
    
    
   

        private Integer _permitId;
        private String _permitDocTypeCD;
        private String _permitDocTypeDsc;
        private String _issuanceStageFlag;
        private boolean fixed;
        private boolean nsr;
        private boolean titleV;
        private boolean permitDetail;
        private boolean draftPublication;
        private boolean ppPublication;
        private boolean finalIssuance;
        private boolean withdrawalIssuance;
        private Integer sortOrder;
        private boolean feeSummary;

        public Integer getSortOrder() {
			return sortOrder;
		}

		public void setSortOrder(Integer sortOrder) {
			this.sortOrder = sortOrder;
		}




		private String permitDocumentid;
       

		

		public String getPermitDocumentid() {
			return permitDocumentid;
		}

		public void setPermitDocumentid(String permitDocumentid) {
			this.permitDocumentid = permitDocumentid;
		}

		public void populate(ResultSet rs) {

            try {
                super.populate(rs);
                setPermitDocTypeCD(rs.getString("code"));
                setPermitDocTypeDsc(rs.getString("description"));
                setFixed(AbstractDAO.translateIndicatorToBoolean(rs.getString("fixed")));
                setNsr(AbstractDAO.translateIndicatorToBoolean(rs.getString("nsr")));
                setTittleV(AbstractDAO.translateIndicatorToBoolean(rs.getString("title_v")));
                setPermitDetail(AbstractDAO.translateIndicatorToBoolean(rs.getString("permit_detail")));
                setDraftPublication(AbstractDAO.translateIndicatorToBoolean(rs.getString("draft_publication")));
                setPpPublication(AbstractDAO.translateIndicatorToBoolean(rs.getString("pp_publication")));
                setFinalIssuance(AbstractDAO.translateIndicatorToBoolean(rs.getString("final_issuance")));
                setWithdrawalIssuance(AbstractDAO.translateIndicatorToBoolean(rs.getString("withdrawal_issuance")));
                setFeeSummary(AbstractDAO.translateIndicatorToBoolean(rs.getString("fee_summary")));
                setSortOrder(AbstractDAO.getInteger(rs,"sort_order"));
                //setGenerateTemplate(AbstractDAO.translateIndicatorToBoolean(rs.getString("generate_template")));
                //setPathname(rs.getString("Pathname"));
                //setDocument_id(AbstractDAO.getInteger(rs,"document_id"));
                setPermitDocumentid(rs.getString("document_id"));
                setDirty(false);
                
            }
            catch (SQLException sqle) {
                logger.error("Required field error: " + sqle.getMessage(), sqle);
            }
        }
    	
        public final String getPermitDocTypeCD() {
            return _permitDocTypeCD;
        }

        public final void setPermitDocTypeCD(String permitDocTypeCD) {
            this._permitDocTypeCD = permitDocTypeCD;
            this.requiredField(_permitDocTypeCD, "permitDocTypeCD");
            setDirty(true);
        }

        public final String getIssuanceStageFlag() {
            return _issuanceStageFlag;
        }

        public final void setIssuanceStageFlag(String issuanceStageCD) {
            this._issuanceStageFlag = issuanceStageCD;
            setDirty(true);
        }

        public final Integer getPermitId() {
            return _permitId;
        }

        public final void setPermitId(Integer permitId) {
            this._permitId = permitId;
            this.requiredField(_permitId, "permitId");
            setDirty(true);
        }


    	public boolean isNsr() {
    		return nsr;
    	}

    	public void setNsr(boolean nsr) {
    		this.nsr = nsr;
    	}

    	public boolean isTitleV() {
    		return titleV;
    	}

    	public void setTittleV(boolean titleV) {
    		this.titleV = titleV;
    	}

    	public boolean isPermitDetail() {
    		return permitDetail;
    	}

    	public void setPermitDetail(boolean permitDetail) {
    		this.permitDetail = permitDetail;
    	}

    	public boolean isDraftPublication() {
    		return draftPublication;
    	}

    	public void setDraftPublication(boolean draftPublication) {
    		this.draftPublication = draftPublication;
    	}

    	public boolean isPpPublication() {
    		return ppPublication;
    	}

    	public void setPpPublication(boolean ppPublication) {
    		this.ppPublication = ppPublication;
    	}

    	public boolean isFinalIssuance() {
    		return finalIssuance;
    	}

    	public void setFinalIssuance(boolean finalIssuance) {
    		this.finalIssuance = finalIssuance;
    	}

    	public boolean isWithdrawalIssuance() {
    		return withdrawalIssuance;
    	}

    	public void setWithdrawalIssuance(boolean withdrawalIssuance) {
    		this.withdrawalIssuance = withdrawalIssuance;
    	}    	

    	public boolean isFixed() {
    		return fixed;
    	}

    	public void setFixed(boolean fixed) {
    		this.fixed = fixed;
    	}    	

    	public String getPermitDocTypeDsc() {
    		return _permitDocTypeDsc;
    	}

    	public void setPermitDocTypeDsc(String permitDocTypeDsc) {
    		this._permitDocTypeDsc = permitDocTypeDsc;
    	}  	
    	
    	public boolean isFeeSummary() {
    		return feeSummary;
    	}

    	public void setFeeSummary(boolean feeSummary) {
    		this.feeSummary = feeSummary;
    	} 
 		
 		
}
