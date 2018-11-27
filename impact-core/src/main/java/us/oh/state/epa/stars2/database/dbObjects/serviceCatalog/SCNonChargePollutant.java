package us.oh.state.epa.stars2.database.dbObjects.serviceCatalog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

public class SCNonChargePollutant extends BaseDB {
	
	private static final long serialVersionUID = 4844071195436801151L;

	private Integer scReportId;
    private String pollutantCd;
    private String pollutantDsc;
    private SccCode sccCd;
    private String sccDesc1;
    private String sccDesc2;
    private String sccDesc3;
    private String sccDesc4;
    private boolean fugitiveOnly;
    private boolean deprecated;
    private boolean sccRequired;

    public SCNonChargePollutant() {
        super();
		sccCd = new SccCode();
    }

	public SCNonChargePollutant(Integer scReportId, String pollutantCd, String pollutantDsc, String sccId,
			String sccDesc1, String sccDesc2, String sccDesc3, String sccDesc4, boolean fugitiveOnly, 
			boolean deprecated) {
		
		super();
		
		setSCReportId(scReportId);
		setPollutantCd(pollutantCd);
		setPollutantDsc(pollutantDsc);
		sccCd = new SccCode();
		sccCd.setSccId(sccId);
		setSccDesc1(sccDesc1);
		setSccDesc2(sccDesc2);
		setSccDesc3(sccDesc3);
		setSccDesc4(sccDesc4);
		setFugitiveOnly(fugitiveOnly);
		setDeprecated(deprecated);
	}

	public SCNonChargePollutant(SCNonChargePollutant old) {
		
        super(old);

        if (old != null) {
            setSCReportId(old.getSCReportId());
            setPollutantCd(old.getPollutantCd());
            setPollutantDsc(old.getPollutantDsc());
            setSccCd(old.getSccCd());
            setSccDesc1(old.getSccDesc1());
            setSccDesc2(old.getSccDesc2());
            setSccDesc3(old.getSccDesc3());
            setSccDesc4(old.getSccDesc4());
            setFugitiveOnly(old.isFugitiveOnly());
            setDeprecated(old.isDeprecated());
        }
    }

    public final String getPollutantCd() {
        return pollutantCd;
    }

    public final void setPollutantCd(String pollutantCd) {
        this.pollutantCd = pollutantCd;
    }

    public final String getPollutantDsc() {
    	String ret = pollutantDsc;
    	if (isDeprecated() && pollutantDsc.lastIndexOf("(inactive)") < 0) {
    		ret = pollutantDsc + "(inactive)";
    	}
        return ret;
    }

    public final void setPollutantDsc(String pollutantDsc) {
    	if (pollutantDsc != null && pollutantDsc.length() > 11 && pollutantDsc.lastIndexOf("(inactive)") > 0) {
    		this.pollutantDsc = pollutantDsc.substring(0, pollutantDsc.lastIndexOf("(inactive)") - 1);
    	}
        this.pollutantDsc = pollutantDsc;
    } 

    public final Integer getSCReportId() {
        return scReportId;
    }

    public final void setSCReportId(Integer scReportId) {
        this.scReportId = scReportId;
    }

    public final SccCode getSccCd() {
		return sccCd;
	}

	public final void setSccCd(SccCode sccCd) {
		this.sccCd = sccCd;
	}

	public final String getSccDesc() {
		
		String ret = null;
		if (sccCd != null) {
			if (sccCd.getSccLevel1Desc() != null) {
				ret = sccCd.getSccLevel1Desc();
				if (sccCd.getSccLevel2Desc() != null) {
					ret = ret + ", " + sccCd.getSccLevel2Desc();
					if (sccCd.getSccLevel3Desc() != null) {
						ret = ret + ", " + sccCd.getSccLevel3Desc();
						if (sccCd.getSccLevel4Desc() != null) {
							ret = ret + ", " + sccCd.getSccLevel4Desc();
						}
					}
				}
			}
		}
		return ret;
	}

	public final String getSccDesc1() {
		return this.sccDesc1;
	}

	public final void setSccDesc1(String sccDesc1) {
		
		if (sccDesc1 == null || sccDesc1.length() == 0) {
			this.sccDesc1 = null;
			setSccDesc2(null);
			setSccDesc3(null);
			setSccDesc4(null);
		} else {
			this.sccDesc1 = sccDesc1;
		}

		if (sccCd == null) {
			sccCd = new SccCode();
		}
		sccCd.setSccIdL1Cd(sccDesc1);

	}

	public final String getSccDesc2() {
		return sccDesc2;
	}

	public final void setSccDesc2(String sccDesc2) {
		
		if (sccDesc2 == null || sccDesc2.length() == 0) {
			this.sccDesc2 = null;
			setSccDesc3(null);
			setSccDesc4(null);
		} else {
			this.sccDesc2 = sccDesc2;
		}

		if (sccCd == null) {
			sccCd = new SccCode();
		}
		sccCd.setSccIdL2Cd(sccDesc2);

	}
	
	public final LinkedHashMap<String, String> getSccLevel2Codes() {
		
		if (sccCd == null || sccCd.getSccIdL1Cd() == null) {
			return null;
		}

		return ((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getSccLevelsCodes(
				2, sccCd.getSccLevel1Desc(), 
				null, null, null, this.getSccCd().getSccLevel1Desc());
		
	}

	public final String getSccDesc3() {
		return sccDesc3;
	}

	public final void setSccDesc3(String sccDesc3) {
		
		if (sccDesc3 == null || sccDesc3.length() == 0) {
			this.sccDesc3 = null;
			setSccDesc4(null);
		} else {
			this.sccDesc3 = sccDesc3;
		}

		if (sccCd == null) {
			sccCd = new SccCode();
		}
		sccCd.setSccIdL3Cd(sccDesc3);

	}

	public final LinkedHashMap<String, String> getSccLevel3Codes() {

		if (sccCd == null || sccCd.getSccIdL2Cd() == null) {
			return null;
		}

		return ((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getSccLevelsCodes(
				3, sccCd.getSccLevel1Desc(), sccCd.getSccLevel2Desc(), 
				null, null, this.getSccCd().getSccLevel2Desc());
		
	}

	public final String getSccDesc4() {
		return sccDesc4;
	}

	public final void setSccDesc4(String sccDesc4) {

		this.sccDesc4 = sccDesc4;
		if (sccCd != null) {
			sccCd.setSccIdL4Cd(sccDesc4);			
		}

	}

	public final LinkedHashMap<String, String> getSccLevel4Codes() {

		if (sccCd == null || sccCd.getSccIdL3Cd() == null) {
			return null;
		}

		return ((InfrastructureDefs) FacesUtil.getManagedBean("infraDefs")).getSccLevelsCodes(
				4, sccCd.getSccLevel1Desc(), sccCd.getSccLevel2Desc(), sccCd.getSccLevel3Desc(),
				null, this.getSccCd().getSccLevel3Desc());
		
	}

	public final boolean isFugitiveOnly() {
		return fugitiveOnly;
	}

	public final void setFugitiveOnly(boolean fugitiveOnly) {
		this.fugitiveOnly = fugitiveOnly;
	}

    public final boolean isDeprecated() {
        return deprecated;
    }

    public final void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
    
    public final void populate(ResultSet rs) throws SQLException {

    	try {
    		
            setSCReportId(AbstractDAO.getInteger(rs, "nc_pollutant_emissions_report_id"));
            setPollutantCd(rs.getString("nc_pollutant_pollutant_cd"));
            setPollutantDsc(rs.getString("nc_pollutant_pollutant_dsc"));
            setFugitiveOnly(AbstractDAO.translateIndicatorToBoolean(rs.getString("nc_fugitive_only")));
            setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("nc_pollutant_deprecated")));
            sccCd.setSccId(rs.getString("nc_scc_id"));
            sccCd.setSccIdL1Cd(rs.getString("level1_dsc"));
            sccCd.setSccIdL2Cd(rs.getString("level2_dsc"));
            sccCd.setSccIdL3Cd(rs.getString("level3_dsc"));
            sccCd.setSccIdL4Cd(rs.getString("level4_dsc"));

        } catch (SQLException sqle) {
            logger.debug(sqle.getMessage());
        }
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (deprecated ? 1231 : 1237);
		result = prime * result + (fugitiveOnly ? 1231 : 1237);
		result = prime * result + ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
		result = prime * result + ((pollutantDsc == null) ? 0 : pollutantDsc.hashCode());
		result = prime * result + ((scReportId == null) ? 0 : scReportId.hashCode());
		result = prime * result + ((sccCd == null || sccCd.getSccId() == null) ? 0 : sccCd.getSccId().hashCode());
		return result;
	}

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SCNonChargePollutant other = (SCNonChargePollutant) obj;
		if (deprecated != other.deprecated)
			return false;
		if (fugitiveOnly != other.fugitiveOnly)
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		if (pollutantDsc == null) {
			if (other.pollutantDsc != null)
				return false;
		} else if (!pollutantDsc.equals(other.pollutantDsc))
			return false;
		if (scReportId == null) {
			if (other.scReportId != null)
				return false;
		} else if (!scReportId.equals(other.scReportId))
			return false;
		if (sccCd == null) {
			if (other.sccCd != null)
				return false;
		} else if (!sccCd.equals(other.sccCd))
			return false;
		return true;
	}

}
