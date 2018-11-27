package us.oh.state.epa.stars2.database.dbObjects.serviceCatalog;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;

public class SCDataImportPollutant extends BaseDB implements Comparable<SCDataImportPollutant>{

	private static final long serialVersionUID = -8698853276763163122L;

	private Integer scReportId;
	private String pollutantCd;
	private Integer sortOrder;
	private String pollutantDsc;
	private boolean deprecated;

	public SCDataImportPollutant() {
		super();
	}

	public SCDataImportPollutant(SCDataImportPollutant old) {
		super(old);

		if (old != null) {
			setSCReportId(old.scReportId);
			setPollutantCd(old.pollutantCd);
			setSortOrder(old.sortOrder);
			setPollutantDsc(old.pollutantDsc);
			setDeprecated(old.deprecated);
		}
	}

	public final Integer getSCReportId() {
		return scReportId;
	}

	public final void setSCReportId(Integer scReportId) {
		this.scReportId = scReportId;
	}

	public final String getPollutantCd() {
		return pollutantCd;
	}

	public final void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public final Integer getSortOrder() {
		return sortOrder;
	}

	public final void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		
		if(!super.equals(obj)) {
			return false;
		}
		
		if(getClass() != obj.getClass()) {
			return false;
		}
		
		final SCDataImportPollutant scDataImportPollutant = (SCDataImportPollutant)obj;
		
		if(null == this.pollutantCd) {
			if(null != scDataImportPollutant.getPollutantCd()) {
				return false;
			}
		} else {
			if(!this.pollutantCd.equals(scDataImportPollutant.getPollutantCd())) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (null == this.pollutantCd ? 0 : this.pollutantCd.hashCode());

		return result;
	}

	@Override
	public final void populate(ResultSet rs) throws SQLException {
		if (null != rs) {
			setSCReportId(AbstractDAO.getInteger(rs, "sc_emissions_report_id"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setSortOrder(AbstractDAO.getInteger(rs, "sort_order"));
			setPollutantDsc(rs.getString("pollutant_dsc"));
			setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("deprecated")));
		}
	}

	@Override
	public ValidationMessage[] validate() {
		requiredField(this.scReportId, "Service Catalog Report Id");
		requiredField(this.pollutantCd, "Pollutant");
		requiredField(this.sortOrder, "Sort Order");
		return super.validate();
	}

	@Override
	public int compareTo(SCDataImportPollutant o) {
		return this.sortOrder.compareTo(o.getSortOrder());
	}
	
	public String getPollutantDsc() {
		return pollutantDsc;
	}
	
	public void setPollutantDsc(String pollutantDsc) {
		this.pollutantDsc = pollutantDsc;
	}
	
	public final boolean isDeprecated() {
        return deprecated;
    }
	
	private final void setDeprecated(boolean deprecated) {
		this.deprecated = deprecated;
	}
	
}
