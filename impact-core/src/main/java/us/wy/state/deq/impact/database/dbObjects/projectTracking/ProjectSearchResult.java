package us.wy.state.deq.impact.database.dbObjects.projectTracking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.webcommon.bean.Stars2Object;

@SuppressWarnings("serial")
public class ProjectSearchResult extends BaseDB {
	
	private Integer id;
	private String number;
	private String name;
	private String description;
	private String typeCd;
	private String stateCd;
	private String summary;
	private String stageCd;
	private String outreachCategoryCd;
	private String grantStatusCd;
	private String letterTypeCd;
	private String contractTypeCd;
	
		
	public ProjectSearchResult() {
		super();
	}
	
	public ProjectSearchResult(ProjectSearchResult p) {
		super();
		if(null != p) {
			setId(p.getId());
			setNumber(p.getNumber());
			setName(p.getName());
			setDescription(p.getDescription());
			setTypeCd(p.getTypeCd());
			setStateCd(p.getStateCd());
			setSummary(p.getSummary());
			setStageCd(p.getStageCd());
			setOutreachCategoryCd(p.getOutreachCategoryCd());
			setGrantStatusCd(p.getGrantStatusCd());
			setLetterTypeCd(p.getLetterTypeCd());
			setContractTypeCd(p.getContractTypeCd());
		}
	}
	
	@Override
	public void populate(ResultSet rs) throws SQLException {
		if(null != rs) {
			setId(AbstractDAO.getInteger(rs, "project_id"));
			setNumber(rs.getString("project_nbr"));
			setName(rs.getString("project_name"));
			setDescription(rs.getString("project_dsc"));
			setTypeCd(rs.getString("project_type_cd"));
			setStateCd(rs.getString("project_state_cd"));
			setSummary(rs.getString("project_summary"));
			setStageCd(rs.getString("project_stage_cd"));
			setOutreachCategoryCd(rs.getString("outreach_category_cd"));
			setGrantStatusCd(rs.getString("grant_status_cd"));
			setLetterTypeCd(rs.getString("letter_type_cd"));
			setContractTypeCd(rs.getString("contract_type_cd"));
			setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
		}
	}
	

	public String getContractTypeCd() {
		return contractTypeCd;
	}

	public void setContractTypeCd(String contractTypeCd) {
		this.contractTypeCd = contractTypeCd;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStageCd() {
		return stageCd;
	}

	public void setStageCd(String stageCd) {
		this.stageCd = stageCd;
	}

	public String getOutreachCategoryCd() {
		return outreachCategoryCd;
	}

	public void setOutreachCategoryCd(String outreachCategoryCd) {
		this.outreachCategoryCd = outreachCategoryCd;
	}

	public String getGrantStatusCd() {
		return grantStatusCd;
	}

	public void setGrantStatusCd(String grantStatusCd) {
		this.grantStatusCd = grantStatusCd;
	}

	public String getLetterTypeCd() {
		return letterTypeCd;
	}

	public void setLetterTypeCd(String letterTypeCd) {
		this.letterTypeCd = letterTypeCd;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTypeCd() {
		return typeCd;
	}

	public void setTypeCd(String typeCd) {
		this.typeCd = typeCd;
	}

	public String getStateCd() {
		return stateCd;
	}

	public void setStateCd(String stateCd) {
		this.stateCd = stateCd;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	
}
