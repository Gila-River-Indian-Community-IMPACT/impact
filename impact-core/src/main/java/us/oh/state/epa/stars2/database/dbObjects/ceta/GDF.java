package us.oh.state.epa.stars2.database.dbObjects.ceta;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

@SuppressWarnings("serial")
public class GDF extends BaseDB implements Comparable<GDF> {
	private Integer gdfId;
	private String doLaaCd;
	private String year;
	private String month;
	private Integer stageOne;
	private Integer stageOneAndTwo;
	private Integer nonStageOneAndTwo;
	
	public void populate(ResultSet rs) throws SQLException {
		try {
			setGdfId(AbstractDAO.getInteger(rs, "gdf_id"));
			setDoLaaCd(rs.getString("do_laa_cd"));
			setYear(rs.getString("year"));
			setMonth(rs.getString("month"));
			setStageOne(AbstractDAO.getInteger(rs, "stageOne"));
			setStageOneAndTwo(AbstractDAO.getInteger(rs, "stageOneAndTwo"));
			setNonStageOneAndTwo(AbstractDAO.getInteger(rs, "nonStageOneAndTwo"));
			setLastModified(AbstractDAO.getInteger(rs, "gdf_lm"));
		 } catch (SQLException sqle) {
	            logger.error(sqle.getMessage());
	        }
		}

	public final Integer getGdfId() {
		return gdfId;
	}

	public final void setGdfId(Integer gdfId) {
		this.gdfId = gdfId;
	}

	public final String getDoLaaCd() {
		return doLaaCd;
	}

	public final void setDoLaaCd(String doLaaCd) {
		this.doLaaCd = doLaaCd;
	}

	public final String getYear() {
		return year;
	}

	public final void setYear(String year) {
		this.year = year;
	}

	public final String getMonth() {
		return month;
	}

	public final void setMonth(String month) {
		this.month = month;
	}

	public final Integer getStageOne() {
		return stageOne;
	}

	public final void setStageOne(Integer stageOne) {
		this.stageOne = stageOne;
	}

	public final Integer getStageOneAndTwo() {
		return stageOneAndTwo;
	}

	public final void setStageOneAndTwo(Integer stageOneAndTwo) {
		this.stageOneAndTwo = stageOneAndTwo;
	}

	public final Integer getNonStageOneAndTwo() {
		return nonStageOneAndTwo;
	}

	public final void setNonStageOneAndTwo(Integer nonStageOneAndTwo) {
		this.nonStageOneAndTwo = nonStageOneAndTwo;
	}

	public void copy(GDF c) {
		this.gdfId = c.gdfId;
		this.doLaaCd = c.doLaaCd;
		this.year = c.year;
		this.month = c.month;
		this.stageOne = c.stageOne;
		this.stageOneAndTwo = c.stageOneAndTwo;
		this.nonStageOneAndTwo = c.nonStageOneAndTwo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((gdfId == null) ? 0 : gdfId.hashCode());
		result = prime * result + ((doLaaCd == null) ? 0 : doLaaCd.hashCode());
		result = prime * result
				+ ((stageOne == null) ? 0 : stageOne.hashCode());
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result
				+ ((stageOneAndTwo == null) ? 0 : stageOneAndTwo.hashCode());
		result = prime * result + ((nonStageOneAndTwo == null) ? 0 : nonStageOneAndTwo.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
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
		GDF other = (GDF) obj;
		if (gdfId == null) {
			if (other.gdfId != null)
				return false;
		} else if (!gdfId.equals(other.gdfId))
			return false;
		if (doLaaCd == null) {
			if (other.doLaaCd != null)
				return false;
		} else if (!doLaaCd.equals(other.doLaaCd))
			return false;
		if (stageOne == null) {
			if (other.stageOne != null)
				return false;
		} else if (!stageOne.equals(other.stageOne))
			return false;
		if (month == null) {
			if (other.month != null)
				return false;
		} else if (!month.equals(other.month))
			return false;
		if (stageOneAndTwo == null) {
			if (other.stageOneAndTwo != null)
				return false;
		} else if (!stageOneAndTwo.equals(other.stageOneAndTwo))
			return false;
		if (this.nonStageOneAndTwo == null) {
			if (other.nonStageOneAndTwo != null)
				return false;
		} else if (!this.nonStageOneAndTwo.equals(other.nonStageOneAndTwo))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}
	
    public final ValidationMessage[] validate() {
    	validationMessages.clear();
    	requiredField(year, "yearChoice", "Year");
    	requiredField(month, "monthChoice", "Month");
    	requiredField(doLaaCd, "doLaaChoice", "DO/LAA Code");
        requiredField(stageOne, "stageOneText", "Stage I");
        requiredField(stageOneAndTwo, "stageOneAndTwoText", "Stage I & II");
        requiredField(nonStageOneAndTwo, "nonStageOneAndTwoText" , "Non-Stage I & II");
        return new ArrayList<ValidationMessage>(validationMessages.values())
                .toArray(new ValidationMessage[0]);
    }

	public int compareTo(GDF o) {
		int ret = year.compareTo(o.year);
		if (ret == 0) {
			ret = month.compareTo(o.month);
		}
		return ret;
	}

	
	
}
