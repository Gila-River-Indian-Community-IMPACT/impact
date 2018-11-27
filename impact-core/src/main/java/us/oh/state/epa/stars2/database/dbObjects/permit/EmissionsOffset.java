package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;

public class EmissionsOffset extends BaseDB {
	
	private static final long serialVersionUID = 2776631877263355143L;

	public static final Double MIN_OFFSET_VALUE = 0.0;
	public static final Double MAX_OFFSET_VALUE = 100000.0;
	
	private Integer emissionsOffsetId;
	private Integer permitId;
	
	private String nonAttainmentAreaCd;
	private String attainmentStandardCd;
	private String pollutantCd;
	private Double currentOffset;
	private Double baseOffset;
	private Double delta;
	private Double emissionsReductionMultiple;
	private Double offsetMultiple;
	private Double offsetAmount;
	private String comment;
	
	public EmissionsOffset() {
		super();
	}
	
	public EmissionsOffset(EmissionsOffset old) {
		super(old);
		if(null != old) {
			setEmissionsOffsetId(old.getEmissionsOffsetId());
			setPermitId(old.getPermitId());
			setNonAttainmentAreaCd(old.getNonAttainmentAreaCd());
			setAttainmentStandardCd(old.getAttainmentStandardCd());
			setPollutantCd(old.getPollutantCd());
			setCurrentOffset(old.getCurrentOffset());
			setBaseOffset(old.getBaseOffset());
			setDelta(old.getDelta());
			setEmissionsReductionMultiple(old.getEmissionsReductionMultiple());
			setOffsetMultiple(old.getOffsetMultiple());
			setOffsetAmount(old.getOffsetAmount());
			setComment(old.getComment());
			
			setLastModified(old.getLastModified());
		}
	}
	
	public Integer getEmissionsOffsetId() {
		return emissionsOffsetId;
	}

	public void setEmissionsOffsetId(Integer emissionsOffsetId) {
		this.emissionsOffsetId = emissionsOffsetId;
	}

	public Integer getPermitId() {
		return permitId;
	}

	public void setPermitId(Integer permitId) {
		this.permitId = permitId;
	}

	public String getNonAttainmentAreaCd() {
		return nonAttainmentAreaCd;
	}

	public void setNonAttainmentAreaCd(String nonAttainmentAreaCd) {
		this.nonAttainmentAreaCd = nonAttainmentAreaCd;
	}

	public String getAttainmentStandardCd() {
		return attainmentStandardCd;
	}

	public void setAttainmentStandardCd(String attainmentStandardCd) {
		this.attainmentStandardCd = attainmentStandardCd;
	}

	public String getPollutantCd() {
		return pollutantCd;
	}

	public void setPollutantCd(String pollutantCd) {
		this.pollutantCd = pollutantCd;
	}

	public Double getCurrentOffset() {
		return currentOffset;
	}

	public void setCurrentOffset(Double currentOffset) {
		this.currentOffset = currentOffset;
	}

	public Double getBaseOffset() {
		return baseOffset;
	}

	public void setBaseOffset(Double baseOffset) {
		this.baseOffset = baseOffset;
	}

	public Double getDelta() {
		return delta;
	}

	public void setDelta(Double delta) {
		this.delta = delta;
	}
	
	public Double getEmissionsReductionMultiple() {
		return emissionsReductionMultiple;
	}

	public void setEmissionsReductionMultiple(Double emissionsReductionMultiple) {
		this.emissionsReductionMultiple = emissionsReductionMultiple;
	}

	public Double getOffsetMultiple() {
		return offsetMultiple;
	}

	public void setOffsetMultiple(Double offsetMultiple) {
		this.offsetMultiple = offsetMultiple;
	}

	public Double getOffsetAmount() {
		return offsetAmount;
	}

	public void setOffsetAmount(Double offsetAmount) {
		this.offsetAmount = offsetAmount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((attainmentStandardCd == null) ? 0 : attainmentStandardCd
						.hashCode());
		result = prime * result
				+ ((baseOffset == null) ? 0 : baseOffset.hashCode());
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result
				+ ((currentOffset == null) ? 0 : currentOffset.hashCode());
		result = prime * result + ((delta == null) ? 0 : delta.hashCode());
		result = prime
				* result
				+ ((emissionsOffsetId == null) ? 0 : emissionsOffsetId
						.hashCode());
		result = prime
				* result
				+ ((emissionsReductionMultiple == null) ? 0
						: emissionsReductionMultiple.hashCode());
		result = prime
				* result
				+ ((nonAttainmentAreaCd == null) ? 0 : nonAttainmentAreaCd
						.hashCode());
		result = prime * result
				+ ((offsetAmount == null) ? 0 : offsetAmount.hashCode());
		result = prime * result
				+ ((offsetMultiple == null) ? 0 : offsetMultiple.hashCode());
		result = prime * result
				+ ((permitId == null) ? 0 : permitId.hashCode());
		result = prime * result
				+ ((pollutantCd == null) ? 0 : pollutantCd.hashCode());
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
		EmissionsOffset other = (EmissionsOffset) obj;
		if (attainmentStandardCd == null) {
			if (other.attainmentStandardCd != null)
				return false;
		} else if (!attainmentStandardCd.equals(other.attainmentStandardCd))
			return false;
		if (baseOffset == null) {
			if (other.baseOffset != null)
				return false;
		} else if (!baseOffset.equals(other.baseOffset))
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (currentOffset == null) {
			if (other.currentOffset != null)
				return false;
		} else if (!currentOffset.equals(other.currentOffset))
			return false;
		if (delta == null) {
			if (other.delta != null)
				return false;
		} else if (!delta.equals(other.delta))
			return false;
		if (emissionsOffsetId == null) {
			if (other.emissionsOffsetId != null)
				return false;
		} else if (!emissionsOffsetId.equals(other.emissionsOffsetId))
			return false;
		if (emissionsReductionMultiple == null) {
			if (other.emissionsReductionMultiple != null)
				return false;
		} else if (!emissionsReductionMultiple
				.equals(other.emissionsReductionMultiple))
			return false;
		if (nonAttainmentAreaCd == null) {
			if (other.nonAttainmentAreaCd != null)
				return false;
		} else if (!nonAttainmentAreaCd.equals(other.nonAttainmentAreaCd))
			return false;
		if (offsetAmount == null) {
			if (other.offsetAmount != null)
				return false;
		} else if (!offsetAmount.equals(other.offsetAmount))
			return false;
		if (offsetMultiple == null) {
			if (other.offsetMultiple != null)
				return false;
		} else if (!offsetMultiple.equals(other.offsetMultiple))
			return false;
		if (permitId == null) {
			if (other.permitId != null)
				return false;
		} else if (!permitId.equals(other.permitId))
			return false;
		if (pollutantCd == null) {
			if (other.pollutantCd != null)
				return false;
		} else if (!pollutantCd.equals(other.pollutantCd))
			return false;
		return true;
	}

	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setEmissionsOffsetId(AbstractDAO.getInteger(rs, "emissions_offset_id"));
			setPermitId(AbstractDAO.getInteger(rs, "permit_id"));
			setNonAttainmentAreaCd(rs.getString("area_cd"));
			setAttainmentStandardCd(rs.getString("attainment_standard_cd"));
			setPollutantCd(rs.getString("pollutant_cd"));
			setCurrentOffset(AbstractDAO.getDouble(rs, "current_offset"));
			setBaseOffset(AbstractDAO.getDouble(rs, "base_offset"));
			setEmissionsReductionMultiple(AbstractDAO.getDouble(rs, "emissions_reduction_multiple"));
			setOffsetMultiple(AbstractDAO.getDouble(rs, "offset_multiple"));
			setDelta(AbstractDAO.getDouble(rs, "delta"));
			setOffsetAmount(AbstractDAO.getDouble(rs, "offset_amount"));
			setComment(rs.getString("comments"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		}
	}
	
	@Override
	public ValidationMessage[] validate() {
		List<ValidationMessage> valMsgs = new ArrayList<ValidationMessage>();
		
		if(Utility.isNullOrEmpty(pollutantCd)) {
			valMsgs.add(new ValidationMessage(
					"pollutantCd", 
					"Attribute " + "Pollutant" + " is not set", 
					ValidationMessage.Severity.ERROR,
					"pollutantCd"));
		}
		
		if(null == offsetMultiple) {
			valMsgs.add(new ValidationMessage(
					"offsetMultiple", 
					"Attribute " + "Offset Multiplier" + " is not set", 
					ValidationMessage.Severity.ERROR,
					"offsetMultiple"));
		}
		
		if(null != currentOffset
				&& (currentOffset < MIN_OFFSET_VALUE
						|| currentOffset > MAX_OFFSET_VALUE)) {
			valMsgs.add(new ValidationMessage(
					"currentOffset", 
					"Attribute " + "Current" + " should be between " + MIN_OFFSET_VALUE + " and " + MAX_OFFSET_VALUE, 
					ValidationMessage.Severity.ERROR,
					"currentOffset"));
		}
		
		if(null != baseOffset
				&& (baseOffset < MIN_OFFSET_VALUE
						|| baseOffset > MAX_OFFSET_VALUE)) {
			valMsgs.add(new ValidationMessage(
					"baseOffset", 
					"Attribute " + "Base" + " should be between " + MIN_OFFSET_VALUE + " and " + MAX_OFFSET_VALUE, 
					ValidationMessage.Severity.ERROR,
					"baseOffset"));
		}
		
		return valMsgs.toArray(new ValidationMessage[0]);
	}
}
