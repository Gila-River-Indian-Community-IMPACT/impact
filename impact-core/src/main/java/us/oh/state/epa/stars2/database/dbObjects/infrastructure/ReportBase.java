package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.framework.util.Utility;

public class ReportBase extends BaseDB {
    private Integer id;
    private String name;
    private String groupNm;
    private List<ReportAttribute> attributes = new ArrayList<ReportAttribute>(0);

    public ReportBase() {
    }

    public ReportBase(ReportBase old) {
        if (old != null) {
            setId(old.getId());
            setName(old.getName());
            setGroupNm(old.getGroupNm());
            setAttributes(old.getAttributes());
        }
    }

    public final List<ReportAttribute> getAttributes() {
        return attributes;
    }

    public final void addAttribute(ReportAttribute newAttribute) {
        if (newAttribute != null) {
            attributes.add(newAttribute);
        }
    }

    public final void removeAttribute(ReportAttribute attribute) {
        attributes.remove(attribute);
    }

    public final void setAttributes(List<ReportAttribute> attributes) {
        if (attributes == null) {
            this.attributes = new ArrayList<ReportAttribute>(0);
        } else {
            this.attributes = attributes;
        }
    }

    public final boolean isAttributeEnabled(String attribute) {
        return attributes.contains(attribute);
    }

    public final void setAttributeEnabled(ReportAttribute attribute) {
        attributes.add(attribute);
    }

    public final void setAttributeDisabled(String attribute) {
        attributes.remove(attribute);
    }

    public final Integer getId() {
        return id;
    }

    public final void setId(Integer id) {
        this.id = id;
    }

    public final String getGroupNm() {
        return groupNm;
    }

    public final void setGroupNm(String groupNm) {
        this.groupNm = groupNm;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public void populate(ResultSet rs) {

        try {
            setId(AbstractDAO.getInteger(rs, "report_id"));
            setName(rs.getString("report_nm"));
            setGroupNm(rs.getString("report_group_nm"));
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));

            attributes = new ArrayList<ReportAttribute>(0);

            try {
                if (rs.getString("code") != null) {
                    ReportAttribute tempAttribute;
                    do {
                        tempAttribute = new ReportAttribute();
                        tempAttribute.populate(rs);
                        addAttribute(tempAttribute);
                    } while (rs.next());
                }
            }
            catch (SQLException sqle) {
                logger.debug("Optional field error. " + sqle.getMessage());
            }
        }
        catch (SQLException sqle) {
            logger.error("Required field error.", sqle);
        }
    }
    
	@Override
	public ValidationMessage[] validate() {
		this.validationMessages.clear();
		
		if(Utility.isNullOrEmpty(this.getName())) {
			ValidationMessage valMsg = new ValidationMessage("name", "Set a report name.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("name", valMsg);
		}
		
		if(Utility.isNullOrEmpty(this.getGroupNm())) {
			ValidationMessage valMsg = new ValidationMessage("groupNm", "Set a group name.", ValidationMessage.Severity.ERROR);
			this.validationMessages.put("groupNm", valMsg);
		}
		
		
		return new ArrayList<ValidationMessage>(validationMessages.values())
				.toArray(new ValidationMessage[0]);
	}
}
