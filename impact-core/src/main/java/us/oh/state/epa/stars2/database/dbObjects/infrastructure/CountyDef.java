package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

@SuppressWarnings("serial")
public class CountyDef extends BaseDB {
    private String countyCd;
    private String stateCd;
    private String countyNm;
    private String doLaaCd;
    private String fipsCountyCd;
    private String ptioCCList;
    private String doLaaCCList;
    private String ptoCCList;
    private String countySeat;
    
    private Integer shapeId;


    public String getCountySeat() {
		return countySeat;
	}

	public void setCountySeat(String countySeat) {
		this.countySeat = countySeat;
	}

	public CountyDef() {
    }

    public CountyDef(CountyDef old) {
        super(old);

        if (old != null) {
            setCountyCd(old.getCountyCd());
            setStateCd(old.getStateCd());
            setCountyNm(old.getCountyNm());
            setDoLaaCd(old.getDoLaaCd());
            setFipsCountyCd(old.getFipsCountyCd());
            setPtioCCList(old.getPtioCCList());
            setDoLaaCCList(old.getDoLaaCCList());
            setPtoCCList(old.getPtoCCList());
            setCountySeat(old.getCountySeat());
            setShapeId(old.getShapeId());
        }
    }

    public final String getDoLaaCd() {
        return doLaaCd;
    }

    public final void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }

    /**
     * CountyId.
     * 
     * @return Integer
     */
    public final String getCountyCd() {
        return countyCd;
    }

    /**
     * CountyId.
     * 
     * @param countyId
     */
    public final void setCountyCd(String countyCd) {
        this.countyCd = countyCd;
    }

    /**
     * StateCd.
     * 
     * @return String
     */
    public final String getStateCd() {
        return stateCd;
    }

    /**
     * StateCd.
     * 
     * @param stateCd
     */
    public final void setStateCd(String stateCd) {
        this.stateCd = stateCd;
    }

    /**
     * CountyNm.
     * 
     * @return String
     */
    public final String getCountyNm() {
        return countyNm;
    }

    /**
     * CountyNm.
     * 
     * @param countyNm
     */
    public final void setCountyNm(String countyNm) {
        this.countyNm = countyNm;
    }

    public final String getPtioCCList() {
        return ptioCCList;
    }

    public final void setPtioCCList(String ccList) {
        ptioCCList = ccList;
    }

    public final String getDoLaaCCList() {
        return doLaaCCList;
    }

    public final void setDoLaaCCList(String ccList) {
        doLaaCCList = ccList;
    }

    public final String getPtoCCList() {
        return ptoCCList;
    }

    public final void setPtoCCList(String ccList) {
        ptoCCList = ccList;
    }
    
    public String getFipsCountyCd() {
		return fipsCountyCd;
	}

	public void setFipsCountyCd(String fipsCountyCd) {
		this.fipsCountyCd = fipsCountyCd;
	}

	public final void populate(ResultSet rs) {
        try {
            setCountyCd(rs.getString("county_cd"));
            setCountyNm(rs.getString("county_nm"));
            setStateCd(rs.getString("state_cd"));
            setDoLaaCd(rs.getString("do_laa_cd"));
            setFipsCountyCd(rs.getString("fips_county_cd"));
            setPtioCCList(rs.getString("pti_ptio_draft"));
            setDoLaaCCList(rs.getString("dolaa"));
            setPtoCCList(rs.getString("title_v_draft"));
            setCountySeat(rs.getString("county_seat"));
            setShapeId(AbstractDAO.getInteger(rs, "shape_id"));
            setLastModified(AbstractDAO.getInteger(rs, "county_lm"));
        } catch (SQLException sqle) {
            logger.warn("Required field error: " + sqle.getMessage(), sqle);
        }
    }
	

    public Integer getShapeId() {
		return shapeId;
	}

	public void setShapeId(Integer shapeId) {
		this.shapeId = shapeId;
	}

}
