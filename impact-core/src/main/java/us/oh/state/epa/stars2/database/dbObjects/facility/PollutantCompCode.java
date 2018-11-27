package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class PollutantCompCode extends BaseDB{
	private Integer fpId;
	private String pollutantCd;
	private String pollutantCompCd;
    private transient String oldCompCd;
	
	private transient boolean delete;
    
    public PollutantCompCode() {
        pollutantCompCd = "Y";
        oldCompCd = "Y";
    }
	
	public void populate(ResultSet rs) throws SQLException {
		try {
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setPollutantCompCd(rs.getString("pollutant_comp_cd"));
            oldCompCd = pollutantCompCd;
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage());
        } finally {
        }
	}

	public final Integer getFpId() {
		return fpId;
	}


	public final void setFpId(Integer fpId) {
		this.fpId = fpId;
	}


	public final String getPollutantCd() {
		return pollutantCd;
	}

	public final void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public final String getPollutantCompCd() {
		return pollutantCompCd;
	}

	public final void setPollutantCompCd(String pollutantCompCd) {
		this.pollutantCompCd = pollutantCompCd;
	}
	
	public boolean isDelete() {
        return delete;
    }
	
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public String getOldCompCd() {
        return oldCompCd;
    }

    public void setOldCompCd(String oldCompCd) {
        this.oldCompCd = oldCompCd;
    }
}
