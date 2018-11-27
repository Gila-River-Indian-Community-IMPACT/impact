package us.oh.state.epa.stars2.database.dbObjects.document;

import java.sql.ResultSet;
import java.sql.SQLException;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;

public class TemplateDocument extends Document {

    private String templateDocTypeCD;
    private String templateDocTypeDsc;
    private String templateDocPath;
    private boolean deprecated;
    
    public String getTemplateDocPath() {
		return templateDocPath;
	}

	public void setTemplateDocPath(String templateDocPath) {
		this.templateDocPath = templateDocPath;
	}

	

    public TemplateDocument() {
        super();
    }

    public TemplateDocument(TemplateDocument old) {

        super(old);

        if (old != null) {
            setTemplateDocTypeCD(old.getTemplateDocTypeCD());
            setDeprecated(old.getDeprecated());
        }
    }

    @Override
    public final void populate(ResultSet rs) {

        super.populate(rs);

        try {
            setTemplateDocTypeCD(rs.getString("template_doc_type_cd"));
            setTemplateDocTypeDsc(rs.getString("template_doc_type_dsc"));
            setTemplateDocPath(rs.getString("template_path"));
            setDeprecated(AbstractDAO.translateIndicatorToBoolean(rs.getString("deprecated")));
            setDirty(false);
        }
        catch (SQLException sqle) {
            logger.error("Required field error");
        }
    }

    public final String getTemplateDocTypeCD() {
        return templateDocTypeCD;
    }

    public final void setTemplateDocTypeCD(String templateDocTypeCD) {
        this.templateDocTypeCD = templateDocTypeCD;
    }

    public final String getTemplateDocTypeDsc() {
        return templateDocTypeDsc;
    }

    public final void setTemplateDocTypeDsc(String templateDocTypeDsc) {
        this.templateDocTypeDsc = templateDocTypeDsc;
    }

    public final boolean getDeprecated() {
        return deprecated;
    }

    public final void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    /**
     * Templates are stored with the full path in the db.
     */
    public final String getDirName() {
        return "";
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + (deprecated ? 1231 : 1237);
        result = PRIME
                * result
                + ((templateDocTypeCD == null) ? 0 : templateDocTypeCD
                        .hashCode());
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
        final TemplateDocument other = (TemplateDocument) obj;
        if (deprecated != other.deprecated)
            return false;
        if (templateDocTypeCD == null) {
            if (other.templateDocTypeCD != null)
                return false;
        } else if (!templateDocTypeCD.equals(other.templateDocTypeCD))
            return false;
        return true;
    }
}
