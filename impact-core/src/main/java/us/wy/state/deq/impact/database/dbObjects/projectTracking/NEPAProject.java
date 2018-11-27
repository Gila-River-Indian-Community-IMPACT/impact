package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;

@SuppressWarnings("serial")
public class NEPAProject extends Project {
	
	private String NEPAId;
	private String categoryCd;
	private String projectStageCd;
	private String extAgencyContact;
	private String extAgencyContactPhone;
	private String modelingRequired;
	private Integer shapeId; // reference to the project location on the map
	
	private List<String> levelCds = new ArrayList<String>();
	private List<String> leadAgencyCds = new ArrayList<String>();
	private List<String> BLMFieldOfficeCds = new ArrayList<String>();
	private List<String> nationalForestCds = new ArrayList<String>();
	private List<String> nationalParkCds = new ArrayList<String>();
	
	public NEPAProject() {
		super();
	}
	
	public NEPAProject(NEPAProject old) {
		super(old);
		if(null != old) {
			setNEPAId(old.getNEPAId());
			setCategoryCd(old.getCategoryCd());
			setProjectStageCd(old.getProjectStageCd());
			setExtAgencyContact(old.getExtAgencyContact());
			setExtAgencyContactPhone(old.getExtAgencyContactPhone());
			setModelingRequired(old.getModelingRequired());
			setShapeId(old.getShapeId());
			setLevelCds(old.getLevelCds());
			setLeadAgencyCds(old.getLeadAgencyCds());
			setBLMFieldOfficeCds(old.getBLMFieldOfficeCds());
			setNationalForestCds(old.getNationalForestCds());
			setNationalParkCds(old.getNationalParkCds());
			setLastModified(old.getLastModified());
		}
	}
	
	public String getNEPAId() {
		return NEPAId;
	}

	public void setNEPAId(String NEPAId) {
		this.NEPAId = NEPAId;
	}

	public String getCategoryCd() {
		return categoryCd;
	}

	public void setCategoryCd(String categoryCd) {
		this.categoryCd = categoryCd;
	}

	public String getProjectStageCd() {
		return projectStageCd;
	}

	public void setProjectStageCd(String projectStageCd) {
		this.projectStageCd = projectStageCd;
	}

	public String getExtAgencyContact() {
		return extAgencyContact;
	}

	public void setExtAgencyContact(String extAgencyContact) {
		this.extAgencyContact = extAgencyContact;
	}

	public String getExtAgencyContactPhone() {
		return extAgencyContactPhone;
	}

	public void setExtAgencyContactPhone(String extAgencyContactPhone) {
		this.extAgencyContactPhone = extAgencyContactPhone;
	}
	
	public String getModelingRequired() {
		return modelingRequired;
	}

	public void setModelingRequired(String modelingRequired) {
		this.modelingRequired = modelingRequired;
	}
	
	public Integer getShapeId() {
		return shapeId;
	}

	public void setShapeId(Integer shapeId) {
		this.shapeId = shapeId;
	}

	public List<String> getLevelCds() {
		if(null == levelCds) {
			setLevelCds(new ArrayList<String>());
		}
		return levelCds;
	}

	public void setLevelCds(List<String> levelCds) {
		this.levelCds = levelCds;
	}

	public List<String> getLeadAgencyCds() {
		if(null == leadAgencyCds) {
			setLeadAgencyCds(new ArrayList<String>());
		}
		return leadAgencyCds;
	}

	public void setLeadAgencyCds(List<String> leadAgencyCds) {
		this.leadAgencyCds = leadAgencyCds;
	}

	public List<String> getBLMFieldOfficeCds() {
		if(null == BLMFieldOfficeCds) {
			setBLMFieldOfficeCds(new ArrayList<String>());
		}
		return BLMFieldOfficeCds;
	}

	public void setBLMFieldOfficeCds(List<String> bLMFieldOfficeCds) {
		BLMFieldOfficeCds = bLMFieldOfficeCds;
	}

	public List<String> getNationalForestCds() {
		if(null == nationalForestCds) {
			setNationalForestCds(new ArrayList<String>());
		}
		return nationalForestCds;
	}

	public void setNationalForestCds(List<String> nationalForestCds) {
		this.nationalForestCds = nationalForestCds;
	}

	public List<String> getNationalParkCds() {
		if(null == nationalParkCds) {
			setNationalParkCds(new ArrayList<String>());
		}
		return nationalParkCds;
	}

	public void setNationalParkCds(List<String> nationalParkCds) {
		this.nationalParkCds = nationalParkCds;
	}

	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			super.populate(rs);
			setNEPAId(rs.getString("nepa_id"));
			setCategoryCd(rs.getString("category_cd"));
			setProjectStageCd(rs.getString("project_stage_cd"));
			setExtAgencyContact(rs.getString("ext_agency_contact"));
			setExtAgencyContactPhone(rs.getString("ext_agency_contact_phone"));
			setModelingRequired(rs.getString("modeling_required"));
			setShapeId(AbstractDAO.getInteger(rs, "shape_id"));
		}
	}
	
	public String getStrShapeId() {
		return null != this.shapeId ? this.shapeId.toString() : null;
	}

	public void setStrShapeId(String strShapeId) {
		Integer shapeId = null != strShapeId ? Integer.valueOf(strShapeId)
				: null;
		setShapeId(shapeId);
	}
}
