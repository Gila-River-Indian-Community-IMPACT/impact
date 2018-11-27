package us.oh.state.epa.stars2.database.dbObjects.permit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.wy.state.deq.impact.def.PermitConditionSupersedenceStatusDef;
import us.wy.state.deq.impact.def.PermitConditionSupersedenceStatusDef.PermitConditionSupersedenceStatus;

public class PermitConditionSupersession extends BaseDB  {

	private static final long serialVersionUID = 1130425977847785196L;

	private Integer supersededPermitConditionId;
	private String supersededpcondId;
	private String supersededPermitCondiditionNumber;
	private Integer supersededPermitId;
	private String supersededPermitNumber;

	private Integer supersedingPermitConditionId;
	private String supersedingpcondId;
	private String supersedingPermitCondiditionNumber;
	private Integer supersedingPermitId;
	private String supersedingPermitNumber;
	
	private String supersedingPermitType;
	private Timestamp supersedingPermitIssuanceDate;

	// @see PermitConditionStatusDef 
	// This is a required field (assuming that the user has selected 
	// a permit number).
	private String supersedingOption;

	// Comments –text field, max length of 4000 characters. 
	// This field is mandatory if the user has set the value 
	// for the Superseding Option to “Partial”.
	private String comments;
	
	/*
	 * used only when creating a new permit condition to allow the user to superseded more than 
	 * one permit condition. this is not stored in the DB.
	 */
	private List<Integer> supersededPermitConditionIds = new ArrayList<Integer>();

	public PermitConditionSupersession() {
		super();
	}

	public PermitConditionSupersession(Integer supersededPermitConditionId, String supersededpcondId, Integer supersededPermitId, 
			String supersededPermitNumber, Integer supersedingPermitConditionId, String supersedingpcondId, Integer supersedingPermitId,
			String supersedingPermitNumber, String supersedingPermitType, Timestamp supersedingPermitIssuanceDate,
			String supersedingOption, String comments) {
		
		super();

		this.supersededPermitConditionId = supersededPermitConditionId;
		this.supersededpcondId = supersededpcondId;
		this.supersededPermitId = supersededPermitId;
		this.supersededPermitNumber = supersededPermitNumber;
		this.supersedingPermitConditionId = supersedingPermitConditionId;
		this.supersedingpcondId = supersedingpcondId;
		this.supersedingPermitId = supersedingPermitId;
		this.supersedingPermitNumber = supersedingPermitNumber;
		this.supersedingPermitType = supersedingPermitType;
		this.supersedingPermitIssuanceDate = supersedingPermitIssuanceDate;
		this.supersedingOption = supersedingOption;
		this.comments = comments;
	}

	public PermitConditionSupersession(BaseDB old) {
		super(old);
		
		if(old instanceof PermitConditionSupersession) {
			this.supersededPermitConditionId = ((PermitConditionSupersession) old).getSupersededPermitConditionId(); 
			this.supersededpcondId =  ((PermitConditionSupersession) old).getSupersededpcondId();
			this.supersededPermitId = ((PermitConditionSupersession) old).getSupersededPermitId();
			this.supersededPermitNumber = ((PermitConditionSupersession) old).getSupersededPermitNumber();
			this.supersedingPermitConditionId = ((PermitConditionSupersession) old).getSupersedingPermitConditionId();
			this.supersedingpcondId = ((PermitConditionSupersession) old).getSupersedingpcondId();
			this.supersedingPermitId = ((PermitConditionSupersession) old).getSupersedingPermitId();
			this.supersedingPermitNumber = ((PermitConditionSupersession) old).getSupersedingPermitNumber();
			this.supersedingPermitType = ((PermitConditionSupersession) old).getSupersedingPermitType();
			this.supersedingPermitIssuanceDate = ((PermitConditionSupersession) old).getSupersedingPermitIssuanceDate();
			this.supersedingOption = ((PermitConditionSupersession) old).getSupersedingOption();
			this.comments = ((PermitConditionSupersession) old).getComments();
		}
	}

	public Integer getSupersededPermitConditionId() {
		return supersededPermitConditionId;
	}

	public void setSupersededPermitConditionId(Integer supersededPermitConditionId) {
		this.supersededPermitConditionId = supersededPermitConditionId;
	}

	public String getSupersededpcondId() {
		return supersededpcondId;
	}

	public void setSupersededpcondId(String supersededpcondId) {
		this.supersededpcondId = supersededpcondId;
	}

	public String getSupersededPermitCondiditionNumber() {
		return supersededPermitCondiditionNumber;
	}

	public void setSupersededPermitCondiditionNumber(String supersededPermitCondiditionNumber) {
		this.supersededPermitCondiditionNumber = supersededPermitCondiditionNumber;
	}

	public Integer getSupersededPermitId(){
		return supersededPermitId;
	}
	
	public void setSupersededPermitId(Integer supersededPermitId){
		this.supersededPermitId = supersededPermitId;
	}
	
	public String getSupersededPermitNumber() {
		return supersededPermitNumber;
	}

	public void setSupersededPermitNumber(String supersededPermitNumber) {
		this.supersededPermitNumber = supersededPermitNumber;
	}

	public Integer getSupersedingPermitConditionId() {
		return supersedingPermitConditionId;
	}

	public void setSupersedingPermitConditionId(Integer supersedingPermitConditionId) {
		this.supersedingPermitConditionId = supersedingPermitConditionId;
	}

	public String getSupersedingpcondId() {
		return supersedingpcondId;
	}

	public void setSupersedingpcondId(String supersedingpcondId) {
		this.supersedingpcondId = supersedingpcondId;
	}

	public Integer getSupersedingPermitId(){
		return supersedingPermitId;
	}
	
	public void setSupersedingPermitId(Integer supersedingPermitId){
		this.supersedingPermitId = supersedingPermitId;
	}
	
	public String getSupersedingPermitNumber() {
		return supersedingPermitNumber;
	}

	public void setSupersedingPermitNumber(String supersedingPermitNumber) {
		this.supersedingPermitNumber = supersedingPermitNumber;
	}

	public String getSupersedingPermitType() {
		return supersedingPermitType;
	}

	public void setSupersedingPermitType(String supersedingPermitType) {
		this.supersedingPermitType = supersedingPermitType;
	}

	public Timestamp getSupersedingPermitIssuanceDate() {
		return supersedingPermitIssuanceDate;
	}

	public void setSupersedingPermitIssuanceDate(Timestamp supersedingPermitIssuanceDate) {
		this.supersedingPermitIssuanceDate = supersedingPermitIssuanceDate;
	}

	/**
	 * @see us.wy.state.deq.impact.def.PermitConditionStatusDef 
	 * @return
	 */
	public String getSupersedingOption() {
		return supersedingOption;
	}

	/**
	 * @see us.wy.state.deq.impact.def.PermitConditionStatusDef 
	 * @return
	 */
	public void setSupersedingOption(String supersedingOption) {
		if (supersedingOption != null) {
			this.supersedingOption = supersedingOption;
		}
	}

	public String getComments() {
		return (comments == null ? "" : comments);
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((supersededPermitId == null) ? 0 : supersededPermitId.hashCode());
		result = prime * result + ((supersededPermitConditionId == null) ? 0 : supersededPermitConditionId.hashCode()); //added???
		result = prime * result + ((supersededPermitNumber == null) ? 0 : supersededPermitNumber.hashCode());
		result = prime * result + ((supersededpcondId == null) ? 0 : supersededpcondId.hashCode());
		result = prime * result + ((supersedingOption == null) ? 0 : supersedingOption.hashCode());
		result = prime * result + ((supersedingPermitId == null) ? 0 : supersedingPermitId.hashCode());
		result = prime * result
				+ ((supersedingPermitConditionId == null) ? 0 : supersedingPermitConditionId.hashCode());
		result = prime * result
				+ ((supersedingPermitIssuanceDate == null) ? 0 : supersedingPermitIssuanceDate.hashCode());
		result = prime * result + ((supersedingPermitNumber == null) ? 0 : supersedingPermitNumber.hashCode());
		result = prime * result + ((supersedingPermitType == null) ? 0 : supersedingPermitType.hashCode());
		result = prime * result + ((supersedingpcondId == null) ? 0 : supersedingpcondId.hashCode());
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
		PermitConditionSupersession other = (PermitConditionSupersession) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (supersededPermitId ==null){
			if (other.supersededPermitId != null)
				return false;
		} else if (!supersededPermitId.equals(other.supersededPermitId))
			return false;
		if (supersededPermitConditionId == null) {
			if (other.supersededPermitConditionId != null)
				return false;
		} else if (!supersededPermitConditionId.equals(other.supersededPermitConditionId))
			return false;
		if (supersededPermitNumber == null) {
			if (other.supersededPermitNumber != null)
				return false;
		} else if (!supersededPermitNumber.equals(other.supersededPermitNumber))
			return false;
		if (supersededpcondId == null) {
			if (other.supersededpcondId != null)
				return false;
		} else if (!supersededpcondId.equals(other.supersededpcondId))
			return false;
		if (supersedingOption == null) {
			if (other.supersedingOption != null)
				return false;
		} else if (!supersedingOption.equals(other.supersedingOption))
			return false;
		if (supersedingPermitId ==null){
			if (other.supersedingPermitId != null)
				return false;
		} else if (!supersedingPermitId.equals(other.supersedingPermitId))
			return false;
		if (supersedingPermitConditionId == null) {
			if (other.supersedingPermitConditionId != null)
				return false;
		} else if (!supersedingPermitConditionId.equals(other.supersedingPermitConditionId))
			return false;
		if (supersedingPermitIssuanceDate == null) {
			if (other.supersedingPermitIssuanceDate != null)
				return false;
		} else if (!supersedingPermitIssuanceDate.equals(other.supersedingPermitIssuanceDate))
			return false;
		if (supersedingPermitNumber == null) {
			if (other.supersedingPermitNumber != null)
				return false;
		} else if (!supersedingPermitNumber.equals(other.supersedingPermitNumber))
			return false;
		if (supersedingPermitType == null) {
			if (other.supersedingPermitType != null)
				return false;
		} else if (!supersedingPermitType.equals(other.supersedingPermitType))
			return false;
		if (supersedingpcondId == null) {
			if (other.supersedingpcondId != null)
				return false;
		} else if (!supersedingpcondId.equals(other.supersedingpcondId))
			return false;
		return true;
	}

	@Override
	public void populate(ResultSet rs) throws SQLException {
		
		if (rs != null) {

			setSupersededPermitConditionId(AbstractDAO.getInteger(rs, "superseded_permit_condition_id"));
			setSupersededpcondId(rs.getString("superseded_pcond_id"));
			setSupersededPermitCondiditionNumber(rs.getString("superseded_pcond_nbr"));
			setSupersededPermitId(AbstractDAO.getInteger(rs, "superseded_permit_id"));
			setSupersededPermitNumber(rs.getString("superseded_permit_nbr"));
			setSupersedingPermitConditionId(AbstractDAO.getInteger(rs, "superseding_permit_condition_id"));
			setSupersedingpcondId(rs.getString("superseding_pcond_id"));
			setSupersedingPermitCondiditionNumber(rs.getString("superseding_pcond_nbr"));
			setSupersedingPermitId(AbstractDAO.getInteger(rs, "superseding_permit_id"));
			setSupersedingPermitNumber(rs.getString("superseding_permit_nbr"));
			setSupersedingPermitType(rs.getString("permit_type_cd"));
			setSupersedingPermitIssuanceDate(rs.getTimestamp("issuance_date"));
			setSupersedingOption(rs.getString("superseding_option"));
			setComments(rs.getString("comments"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
			
		}

	}

	public String getSupersedingPermitCondiditionNumber() {
		return supersedingPermitCondiditionNumber;
	}
	
	public void setSupersedingPermitCondiditionNumber(String supersedingPermitCondiditionNumber) {
		this.supersedingPermitCondiditionNumber = supersedingPermitCondiditionNumber;
	}

	public List<Integer> getSupersededPermitConditionIds() {
		if(null == supersededPermitConditionIds) {
			setSupersededPermitConditionIds(new ArrayList<Integer>(0));
		}
		return supersededPermitConditionIds;
	}

	public void setSupersededPermitConditionIds(List<Integer> supersededPermitConditionIds) {
		this.supersededPermitConditionIds = supersededPermitConditionIds;
	}
	
}
