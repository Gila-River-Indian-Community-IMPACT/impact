package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.WordUtils;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.SiteVisitTypeDef;
import us.oh.state.epa.stars2.framework.util.Utility;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;


@SuppressWarnings("serial")
public abstract class VisitBase extends CetaBaseDB {
	protected Integer id;
    protected Integer fceId;
    private Integer facilityHistId;
    protected Integer fpId;
    protected transient String facilityNm;
    protected Timestamp visitDate;
    protected String visitType;
    protected List<Evaluator> evaluators;
    protected String announced;
    protected boolean legacyFlag;
    protected String memo;
    protected Timestamp createdDt;
    protected Integer createdById;
    private String facilityTypeCd;
    private String permitClassCd;
    protected String siteVisitVeCd;
    
    private FacilityHistory facilityHistory;
    private transient boolean evaluatorsExist;
    private transient String facilityId;  // not part of object
    private transient String scscId;
    private transient Integer versionId;
    private transient String operatingStatusCd;
    private transient Timestamp lastShutdownDate;
    private String inspId;
    private String cmpId;
    private String companyName;
    private String siteId;
    protected String complianceIssued;

    public VisitBase() {
        super();
        Calendar cal = Calendar.getInstance();
        visitDate = new Timestamp(cal.getTime().getTime());
        ArrayList<TestVisitDate> vdList = new ArrayList<TestVisitDate>();
        vdList.add(new TestVisitDate(visitDate));
        evaluators = new ArrayList<Evaluator>();
    }
    
//    public VisitBase(VisitBase old) {
//        super();
//        id = old.id;
//        fceId = old.fceId;
//        fpId = old.fpId;
//        visitDate = old.visitDate;
//        visitType = old.visitType;
//        evaluators = old.evaluators;
//        announced = old.announced;
//        memo = old.memo;
//        visitDate = old.visitDate;
//    }
    
    public VisitBase(Integer id, Integer fceId, Integer fpId, List<Evaluator> evaluators, Timestamp visitDate, 
            String announced, String visitType, String memo) {
        super();
        this.visitType = visitType;
        this.id = id;
        this.fceId = fceId;
        this.fpId = fpId;
        this.visitDate = visitDate;
        this.announced = announced;
        this.evaluators = evaluators;
        ArrayList<TestVisitDate> vdList = new ArrayList<TestVisitDate>();
        vdList.add(new TestVisitDate(visitDate));
        this.memo = memo;
    }
    
    public void calcEvaluatorsExist() {
        evaluatorsExist = evaluators != null && evaluators.size() != 0;
    }

    public String getMemo() {
        return memo;
    }

    public String getShortMemo() {
        String rtn = memo;
        if(memo != null && memo.length()> 75) {
            rtn = memo.substring(0, 75) + "...";
        }
        return rtn;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public String getAnnounced() {
        return announced;
    }

    public void setAnnounced(String announced) {
        this.announced = announced;
    }

    public Timestamp getVisitDate() {
        return visitDate;
    }
    
    public String getVisitDateStr() {
        String dateStr = "";
        if(visitDate != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(visitDate.getTime());
            dateStr = (cal.get(Calendar.MONTH)+1) + "/" + cal.get(Calendar.DAY_OF_MONTH) +
            "/" + cal.get(Calendar.YEAR);
        }
        return dateStr;
    }

    public void setVisitDate(Timestamp visitDate) {
        this.visitDate = visitDate;
    }
    
    public boolean isStackTest() {
        return SiteVisitTypeDef.isStackTest(visitType);
    }

    public Integer getFceId() {
        return fceId;
    }

    public void setFceId(Integer fceId) {
        this.fceId = fceId;
    }

    public List<Evaluator> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(List<Evaluator> evaluators) {
        this.evaluators = evaluators;
    }
    
    public String getEvaluatorNames() {
        return WordUtils.capitalize(getUserNames(evaluators));
    }

    public void addEvaluator() {
        if(evaluators == null) {
            evaluators = new ArrayList<Evaluator>();
        }
        evaluators.add(new Evaluator());
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public boolean isEvaluatorsExist() {
        return evaluatorsExist;
    }

    public void setEvaluatorsExist(boolean evaluatorsExist) {
        this.evaluatorsExist = evaluatorsExist;
    }

    public String getFacilityNm() {
        return facilityNm;
    }

    public void setFacilityNm(String facilityNm) {
        this.facilityNm = facilityNm;
    }

    public Integer getFpId() {
        return fpId;
    }

    public void setFpId(Integer fpId) {
        this.fpId = fpId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public boolean isLegacyFlag() {
        return legacyFlag;
    }

    public void setLegacyFlag(boolean legacyFlag) {
        this.legacyFlag = legacyFlag;
    }

    public Integer getFacilityHistId() {
        return facilityHistId;
    }

    public void setFacilityHistId(Integer facilityHistId) {
        this.facilityHistId = facilityHistId;
    }

    public FacilityHistory getFacilityHistory() {
        return facilityHistory;
    }

    public void setFacilityHistory(FacilityHistory facilityHistory) {
        this.facilityHistory = facilityHistory;
    }

    public String getOperatingStatusCd() {
        return operatingStatusCd;
    }

    public void setOperatingStatusCd(String operatingStatusCd) {
        this.operatingStatusCd = operatingStatusCd;
    }

    public Timestamp getLastShutdownDate() {
        return lastShutdownDate;
    }

    public void setLastShutdownDate(Timestamp lastShutdownDate) {
        this.lastShutdownDate = lastShutdownDate;
    }

    public Integer getVersionId() {
        return versionId;
    }

    public void setVersionId(Integer versionId) {
        this.versionId = versionId;
    }

    public Integer getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }

    public Timestamp getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Timestamp createdDt) {
        this.createdDt = createdDt;
    }

    public String getScscId() {
        return scscId;
    }

    public void setScscId(String scscId) {
        this.scscId = scscId;
    }

    public String getSiteVisitVeCd() {
        return siteVisitVeCd;
    }

    public void setSiteVisitVeCd(String siteVisitVeCd) {
        this.siteVisitVeCd = siteVisitVeCd;
    }

	public String getInspId() {
		return inspId;
	}

	public void setInspId(String inspId) {
		this.inspId = inspId;
	}
	
	 public String getCmpId() {
  		return cmpId;
  	}
  
  	public void setCmpId(String cmpId) {
  		this.cmpId = cmpId;
  	}
  
  	public String getCompanyName() {
  		return companyName;
  	}
  
  	public void setCompanyName(String companyName) {
  		this.companyName = companyName;
  	}

  	public String getSiteId() {
  		String	tempSiteId = null;
  		if (!Utility.isNullOrZero(id)) {
  			String format = "SITE" + "%06d";
  			try {
  				tempSiteId = String.format(format, id);
  			} catch (NumberFormatException nfe) {
  			}
  		}
  		return tempSiteId;

  	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getComplianceIssued() {
		return complianceIssued;
	}

	public void setComplianceIssued(String complianceIssued) {
		this.complianceIssued = complianceIssued;
	}

	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}

	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}

	public String getPermitClassCd() {
		return permitClassCd;
	}

	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}
}
