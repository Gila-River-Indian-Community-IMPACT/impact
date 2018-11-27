package us.oh.state.epa.stars2.def;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.bo.EmissionsReportService;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.HydrocarbonAnalysisPollutant;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.exception.ApplicationException;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.reports.ReportBaseCommon;
import us.wy.state.deq.impact.App;

/*
 * The codes declared in this class must match the content of the
 * CM_POLLUTANT_DEF table
 */
@SuppressWarnings("serial")
public class PollutantDef extends SimpleDef {
    // data items (besides code, desc and deprecated)
    private String category;
    private  Integer sortCategory;
    private boolean ptioApp1;
    private boolean tvptoAppSec12;
    private boolean tvptoAppSec13;
    private boolean nsrAppBact;
    private boolean nsrAppLaer;
    private boolean tvptoAppSec3;
    private boolean emissionsTable;
    private boolean nsps;
    private boolean nonAttainNsr;
    private boolean psd;
    private String afsEquivPollutantCd;
    private String neiCode;
    private String hapCategory;
    private String casNo;
    private String formula;
    private Integer globalWarmingPotential;
    private boolean ghg;
    private boolean otherRegulatedPollutant;
    private boolean pollutantLimit;
    private boolean includeInHCAnalysis;
    private Short HCAnalysisSortOrder;
    
    
    public static final String PE_CD = "PM-PRI";
    public static final String PE_DSC = "Particulate emissions (PE/PM) (formerly particulate matter, PM)";
    public static final String PM10_CD = "PM10-PRI";
    public static final String PM10_DSC = "PM # 10 microns in diameter (PE/PM10)";  // TODO figure out subscript
    public static final String PM25_CD = "PM25-PRI";
    public static final String PM25_DSC = "PM # 2.5 microns in diameter (PE/PM2.5)";  // TODO figure out subscript
    public static final String SO2_CD = "SO2";
    public static final String SO2_DSC = "Sulfur dioxide (SO2)";
    public static final String NOX_CD = "NOX";
    public static final String NOX_DSC = "Nitrogen oxides (NOx)";
    public static final String CO_CD = "CO";
    public static final String CO_DSC = "Carbon monoxide (CO)";
    public static final String OC_CD = "OC";
    public static final String OC_DSC = "Organic compounds (OC)";
    public static final String VOC_CD = "VOC";
    public static final String VOC_DSC = "Volatile organic compounds (VOC)";
    public static final String PB_CD = "7439921";
    public static final String PB_DSC = "Lead (Pb)";
    public static final String HMAX_CD = "HAP";
    public static final String HMAX_DSC = "Highest single HAP";
    public static final String HTOT_CD = "HAPs";
    public static final String HTOT_DSC = "Total Hazardous Air Pollutants (HAPs)";
    
    public static final String FL_CD = "FL";
    public static final String FL_DSC ="Fluoride (F)";
    
    public static final String H2S_CD = "7783064";
    public static final String H2S_DSC ="Hydrogen Sulfide (H2S)";
    
    public static final String HG_CD = "199";
    public static final String HG_DSC ="Mercury (Hg)";
    
    public static final String TRS_CD = "Total Reduced Sulfur";
    public static final String TRS_DSC ="Total Reduced Sulfur (TRS)";
    
    public static final String SAM_CD = "7664939";
    public static final String SAM_DSC ="Sulfuric Acid Mist (SAM)";
    
    private static final String defName = "PollutantDef";
    public static final String PMCOND_CD = "PM-CON";
    public static final String PMFIL_CD = "PM-FIL";
    public static final String PM25FIL_CD = "PM25-FIL";
    public static final String GHG_TOT_CD = "GHG_TOT";
    public static final String HAPS_TOT_CD= "HAPS_TOT";
    
    public static final String THC_CD = "HC";
    public static final String HCL_CD= "7647010";
    public static final String CO2_CD= "CO2";
    
    public static final String TOTAL = "Total";
    
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###,###,###,###,###.#####");
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("InfrastructureSQL.retrievePollutants", 
                    PollutantDef.class);

            cfgMgr.addDef(defName, data);
        } 
        return data;
    }

    public void populate(ResultSet rs) {

        super.populate(rs);

        try {
            setHapCategory(rs.getString("hap_category"));
            setCategory(rs.getString("category"));
            setSortCategory(AbstractDAO.getInteger(rs, "sort_category"));
            setPtioApp1(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("ptio_app_1")));
            setTvptoAppSec12(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("tvpto_app_sec12")));
            setTvptoAppSec13(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("tvpto_app_sec13")));
            setNsrAppBact(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("nsr_app_bact")));
            setNsrAppLaer(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("nsr_app_laer")));
            setTvptoAppSec3(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("tvpto_app_sec3")));
            setEmissionsTable(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("emissions_table")));
            setNsps(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("nsps")));
            setNonAttainNsr(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("non_attain_nsr")));
            setPsd(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("psd")));
            setNeiCode(rs.getString("nei_code"));
            setAfsEquivPollutantCd(rs.getString("afs_equiv_pollutant_cd"));
            setCasNo(rs.getString("cas_no"));
            setFormula(rs.getString("formula"));
            setGlobalWarmingPotential(AbstractDAO.getInteger(rs, "global_warming_potential"));
            setGhg(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("ghg")));
            setOtherRegulatedPollutant(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("other_regulated_pollutant")));
            setPollutantLimit(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("tv_pollutant_limit")));
            setIncludeInHCAnalysis(AbstractDAO.translateIndicatorToBoolean(rs.getString("include_in_hc_analysis")));
            setHCAnalysisSortOrder(AbstractDAO.getShort(rs, "hc_analysis_sort_order"));
        } catch (SQLException sqle) {
            logger.warn("Optional field: " + sqle.getMessage());
        }

    }
    
    public static String getTheCategory(String cd) throws ApplicationException {
        return getPollutantBaseDef(cd).getCategory();
    }
    
    public static String getNeiCode(String cd) throws ApplicationException {
        String rtnCd = null;
        try {
            rtnCd =  getPollutantBaseDef(cd).getNeiCode();
        } catch(Exception e) {
            ;
        }
        return rtnCd;
    }
    
    
    public static String getAfsEquivPollutantCd(String cd) throws ApplicationException {
        String rtnCd = null;
        try {
            rtnCd =  getPollutantBaseDef(cd).getAfsEquivPollutantCd();
        } catch(Exception e) {
            ;
        }
        return rtnCd;
    }
    
    public static PollutantDef getPollutantBaseDef(String cd)
    throws ApplicationException{
        PollutantDef bd = (PollutantDef)PollutantDef.getData().getItems().getItem(cd);
        if(null == bd){
            throw new ApplicationException(" (" + ReportBaseCommon.dataProb +
                    ": pollutant_cd \"" + cd + "\" not found)");
        }
        return bd;
    }
    
    public boolean isPsdDeprecated() {
        return ( super.isDeprecated() || ( !isPsd() ));
    }
    
    public boolean isNsrDeprecated() {
        return ( super.isDeprecated() || ( !isNonAttainNsr() ));
    }
    
    public boolean isNspsDeprecated() {
        return ( super.isDeprecated() || ( !isNsps() ));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isEmissionsTable() {
        return emissionsTable;
    }

    public void setEmissionsTable(boolean emissionsTable) {
        this.emissionsTable = emissionsTable;
    }

    public String getHapCategory() {
        return hapCategory;
    }

    public void setHapCategory(String hapCategory) {
        this.hapCategory = hapCategory;
    }

    public String getNeiCode() {
        return neiCode;
    }

    public void setNeiCode(String neiCode) {
        this.neiCode = neiCode;
    }

    public boolean isNonAttainNsr() {
        return nonAttainNsr;
    }

    public void setNonAttainNsr(boolean nonAttainNsr) {
        this.nonAttainNsr = nonAttainNsr;
    }

    public boolean isNsps() {
        return nsps;
    }

    public void setNsps(boolean nsps) {
        this.nsps = nsps;
    }

    public boolean isPsd() {
        return psd;
    }

    public void setPsd(boolean psd) {
        this.psd = psd;
    }

    public boolean isPtioApp1() {
        return ptioApp1;
    }

    public void setPtioApp1(boolean ptioApp1) {
        this.ptioApp1 = ptioApp1;
    }

    public Integer getSortCategory() {
        return sortCategory;
    }

    public void setSortCategory(Integer sortCategory) {
        this.sortCategory = sortCategory;
    }

    public boolean isTvptoAppSec12() {
        return tvptoAppSec12;
    }

    public void setTvptoAppSec12(boolean tvptoAppSec12) {
        this.tvptoAppSec12 = tvptoAppSec12;
    }

    public boolean isTvptoAppSec13() {
        return tvptoAppSec13;
    }

    public void setTvptoAppSec13(boolean tvptoAppSec13) {
        this.tvptoAppSec13 = tvptoAppSec13;
    }

    public boolean isTvptoAppSec3() {
        return tvptoAppSec3;
    }

    public void setTvptoAppSec3(boolean tvptoAppSec3) {
        this.tvptoAppSec3 = tvptoAppSec3;
    }

	public final String getCasNo() {
		return casNo;
	}

	public final void setCasNo(String casNo) {
		this.casNo = casNo;
	}

	public final String getFormula() {
		return formula;
	}

	public final void setFormula(String formula) {
		this.formula = formula;
	}

	public final Integer getGlobalWarmingPotential() {
		return globalWarmingPotential;
	}

	public final void setGlobalWarmingPotential(Integer globalWarmingPotential) {
		this.globalWarmingPotential = globalWarmingPotential;
	}

	public final boolean isGhg() {
		return ghg;
	}

	public final void setGhg(boolean ghg) {
		this.ghg = ghg;
	}
	
	public static float computeCO2Equivalent(String pollutantCd, String pteString) throws ParseException {
		float res = 0f;
		PollutantDef pd = (PollutantDef)PollutantDef.getData().getItem(pollutantCd);
		if (pd != null && pteString != null) {
			Number num = DECIMAL_FORMAT.parse(pteString);
			res = num.floatValue() * (float)pd.getGlobalWarmingPotential();
		}
		return res;
	}
	
	public static BigDecimal computeBigDecimalCO2Equivalent(String pollutantCd, String pteString) throws NumberFormatException {
		BigDecimal res = new BigDecimal("0");
		PollutantDef pd = (PollutantDef)PollutantDef.getData().getItem(pollutantCd);
		if (pd != null && pteString != null) {
			BigDecimal num = new BigDecimal(pteString.replaceAll(",", ""));
			res = num.multiply(new BigDecimal(pd.getGlobalWarmingPotential()));
		}
		return res;
	}

    public String getAfsEquivPollutantCd() {
        return afsEquivPollutantCd;
    }

    public void setAfsEquivPollutantCd(String afsEquivPollutantCd) {
        this.afsEquivPollutantCd = afsEquivPollutantCd;
    }

	public boolean isOtherRegulatedPollutant() {
		return otherRegulatedPollutant;
	}

	public void setOtherRegulatedPollutant(boolean otherRegulatedPollutant) {
		this.otherRegulatedPollutant = otherRegulatedPollutant;
	}

	public boolean isPollutantLimit() {
		return pollutantLimit;
	}

	public void setPollutantLimit(boolean pollutantLimit) {
		this.pollutantLimit = pollutantLimit;
	}

	public boolean isNsrAppBact() {
		return nsrAppBact;
	}

	public void setNsrAppBact(boolean nsrAppBact) {
		this.nsrAppBact = nsrAppBact;
	}

	public boolean isNsrAppLaer() {
		return nsrAppLaer;
	}

	public void setNsrAppLaer(boolean nsrAppLaer) {
		this.nsrAppLaer = nsrAppLaer;
	}
	
	public static List<SelectItem> getAllPollutants() {
        return getData().getItems().getAllItems();
    }
	
	public static List<SelectItem> getSccPollutants(String sccId, List<String> materialCds) {

		List<SelectItem> pollutants = new ArrayList<SelectItem>();

		if (Utility.isNullOrEmpty(sccId)) {
			return getAllPollutants();
		}

		try {

			SimpleDef[] tempArray = null;
			if (materialCds == null || materialCds.size() == 0) {
				tempArray = App.getApplicationContext().getBean(EmissionsReportService.class)
						.retrieveSccPollutants(sccId);
			} else {
				tempArray = App.getApplicationContext().getBean(EmissionsReportService.class)
						.retrieveSccPollutants(sccId, materialCds);
			}

			for (SimpleDef tempState : tempArray) {
				SelectItem si = new SelectItem(tempState.getCode(),
						tempState.getDescription());
				pollutants.add(si);
			}

		} catch (RemoteException re) {
			// logger.error(re.getMessage(), re);
			DisplayUtil
					.displayError("System error. Please contact system administrator");
		}

		return pollutants;
	}

	public boolean isIncludeInHCAnalysis() {
		return includeInHCAnalysis;
	}

	public void setIncludeInHCAnalysis(boolean includeInHCAnalysis) {
		this.includeInHCAnalysis = includeInHCAnalysis;
	}

	public Short getHCAnalysisSortOrder() {
		return HCAnalysisSortOrder;
	}

	public void setHCAnalysisSortOrder(Short hCAnalysisSortOrder) {
		HCAnalysisSortOrder = hCAnalysisSortOrder;
	}
	
	public static List<PollutantDef> getHCAnalysisPollutantDefs(){
		List<PollutantDef> ret = new ArrayList<PollutantDef>();
		Collection<BaseDef> bdc = PollutantDef.getData().getItems().getCompleteItems().values();            
		for (BaseDef bd: bdc) {
			 PollutantDef pd = (PollutantDef) bd;
			 if (!pd.isDeprecated() && pd.isIncludeInHCAnalysis()) {
				 ret.add(pd);
             }
		}
		return ret;
	}
}
