package us.oh.state.epa.stars2.def;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dbObjects.infrastructure.PredicateDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

@SuppressWarnings("serial")
public class PermitWorkflowActivityBenchmarkDef extends SimpleDef {

	private static final String defName = "PermitWorkflowActivityBenchmarkDef";

	private Integer days;
	
	private Integer activityTemplateId;
	
	private PredicateDef predicateDef;
	
	private Integer displayOrder;
	
	public static DefData getData() {
		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		DefData data = cfgMgr.getDef(defName);
	        
		if (data == null) {
			data = new DefData();
            data.loadFromDB("PermitSQL.retrievePermitWorkflowActivityBenchmarkDefs", 
            		PermitWorkflowActivityBenchmarkDef.class);
			cfgMgr.addDef(defName, data);
		}
		return data;
	}
	

    public void populate(ResultSet rs) {
        super.populate(rs);
        try {
        	setActivityTemplateId(rs.getInt("activity_template_id"));
        	setDays(rs.getInt("days"));
        	setCode(rs.getString("code"));
        	setDisplayOrder(rs.getInt("display_order"));
        	
        	PredicateDef predicateDef = new PredicateDef();
        	predicateDef.populate(rs);
        	setPredicateDef(predicateDef);
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
        }
    }

    
	public Integer getDisplayOrder() {
		return displayOrder;
	}


	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}


	public PredicateDef getPredicateDef() {
		return predicateDef;
	}


	public void setPredicateDef(PredicateDef predicateDef) {
		this.predicateDef = predicateDef;
	}


	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public Integer getActivityTemplateId() {
		return activityTemplateId;
	}

	public void setActivityTemplateId(Integer activityTemplateId) {
		this.activityTemplateId = activityTemplateId;
	}

	public static String getDefname() {
		return defName;
	}


	@Override
	public String toString() {
		return "PermitWorkflowActivityBenchmarkDef [days=" + days
				+ ", activityTemplateId=" + activityTemplateId
				+ ", predicateDef=" + predicateDef + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((activityTemplateId == null) ? 0 : activityTemplateId
						.hashCode());
		result = prime * result + ((days == null) ? 0 : days.hashCode());
		result = prime * result
				+ ((predicateDef == null) ? 0 : predicateDef.hashCode());
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
		PermitWorkflowActivityBenchmarkDef other = (PermitWorkflowActivityBenchmarkDef) obj;
		if (activityTemplateId == null) {
			if (other.activityTemplateId != null)
				return false;
		} else if (!activityTemplateId.equals(other.activityTemplateId))
			return false;
		if (days == null) {
			if (other.days != null)
				return false;
		} else if (!days.equals(other.days))
			return false;
		if (predicateDef == null) {
			if (other.predicateDef != null)
				return false;
		} else if (!predicateDef.equals(other.predicateDef))
			return false;
		return true;
	}
	
}
