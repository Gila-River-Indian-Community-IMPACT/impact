package us.oh.state.epa.stars2.database.dbObjects.facility;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.PollutantDef;

@SuppressWarnings("serial")
public class HydrocarbonAnalysisPollutant extends BaseDB{

	private Integer fpId;
	private String pollutantCd;
	private BigDecimal gas;
	private BigDecimal oil;
	private BigDecimal producedWater;
	private Short sortOrder;
	private boolean readOnly = false;
	
	public static final BigDecimal HC_MIN = new BigDecimal(0);
	public static final BigDecimal HC_MAX = new BigDecimal(100);
	
	public static final BigDecimal HC_COLUMN_TOTAL_MIN = new BigDecimal(90);
	public static final BigDecimal HC_COLUMN_TOTAL_MAX = new BigDecimal(110);
	
	public Integer getFpId() {
		return fpId;
	}


	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}


	public String getPollutantCd() {
		return pollutantCd;
	}


	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}


	public BigDecimal getGas() {
		return gas;
	}


	public void setGas(BigDecimal gas) {
		this.gas = gas;
	}


	public BigDecimal getOil() {
		return oil;
	}


	public void setOil(BigDecimal oil) {
		this.oil = oil;
	}


	public BigDecimal getProducedWater() {
		return producedWater;
	}


	public void setProducedWater(BigDecimal producedWater) {
		this.producedWater = producedWater;
	}


	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setFpId(AbstractDAO.getInteger(rs, "fp_id"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setGas(rs.getBigDecimal("gas"));
			setOil(rs.getBigDecimal("oil"));
			setProducedWater(rs.getBigDecimal("produced_water"));
			setSortOrder(AbstractDAO.getShort(rs, "hc_analysis_sort_order"));
		} catch (SQLException sqle) {
			logger.error("Required field error");
		}
	}

	public ValidationMessage[] validate(){
		this.clearValidationMessages();
		if (gas != null)
			checkRangeValues(gas, HC_MIN, HC_MAX, "gas", getPollutantDesc() + " - Gas");
		if (oil != null)
			checkRangeValues(oil, HC_MIN, HC_MAX, "oil", getPollutantDesc() + " - Oil");
		if (producedWater != null)
			checkRangeValues(producedWater, HC_MIN, HC_MAX, "producedWater", getPollutantDesc() + " - Produced Water");
		return validationMessages.values().toArray(new ValidationMessage[0]);
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public String getPollutantDesc(){
		if (PollutantDef.TOTAL.equals(pollutantCd)){
			return PollutantDef.TOTAL;
		} else{
			return PollutantDef.getData().getItems().getItemDesc(pollutantCd);
		}
		
	}
	
	public Short getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Short sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}


