package us.oh.state.epa.stars2.database.dbObjects.application;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;

@SuppressWarnings("serial")
public class FacilityWideRequirement extends BaseDB {

	// ********** Variables **********
	private Integer requirementId;
	private Integer applicationId;
	private String description;
	private String proposedMethod;

	// ********** Properties **********
	public Integer getRequirementId() {
		return requirementId;
	}

	public void setRequirementId(Integer requirementId) {
		this.requirementId = requirementId;
	}

	public Integer getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Integer applicationId) {
		this.applicationId = applicationId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		requiredFieldDesc();
	}

	public String getProposedMethod() {
		return proposedMethod;
	}

	public void setProposedMethod(String proposedMethod) {
		this.proposedMethod = proposedMethod;
		requiredFieldProposeMethod();
	}
	
	// ********** Constructor **********
	public FacilityWideRequirement() {
		super();
		requiredFields();
	}
	
	public FacilityWideRequirement(FacilityWideRequirement old) {
		super(old);
		
		if (old == null)
			return;
		
		setRequirementId(old.requirementId);
		setApplicationId(old.applicationId);
		setDescription(old.description);
		setProposedMethod(old.proposedMethod);
		requiredFields();
	}

	// ********** Implement - BaseDB abstract class **********
	@Override
	public void populate(ResultSet rs) throws SQLException {
		try {
			setRequirementId(AbstractDAO.getInteger(rs, "requirement_id"));
			setApplicationId(AbstractDAO.getInteger(rs, "application_id"));
			setDescription(rs.getString("description"));
			setProposedMethod(rs.getString("proposed_method"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		} catch (SQLException sqle) {
			logger.warn(sqle.getMessage());
		}
		
	}

	// ********** Public Methods **********
	public void requiredFields() {
		requiredFieldDesc();
		requiredFieldProposeMethod();
	}

	private void requiredFieldProposeMethod() {
		requiredField(
				this.proposedMethod,
				"proposedMethod",
				"Proposed Method to Demonstrate Compliance",
				"proposedMethod");
	}

	private void requiredFieldDesc() {
		requiredField(
				this.description,
				"description",
				"The Facility-Wide Requirement",
				"description");
	}
}
