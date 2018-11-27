package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class BulkDef extends BaseDB {

    private Integer _bulkId;
    private String _name;
    private String _groupNm;
    private String _menu;
    private String _dsc;
    private String _classname;
    private String _searchType;
    private String _templateCd;
    private String _templateDocId;
    private String _correspondenceCd;

    private Map<String, SimpleDef> _attributes 
        = new HashMap<String, SimpleDef>();

    public BulkDef() {
        super();
    }

    public BulkDef(BulkDef old) {
        super(old);
        
        if (old != null) {
            setBulkId(old.getBulkId());
            setName(old.getName());
            setGroupNm(old.getGroupNm());
            setMenu(old.getMenu());
            setDsc(old.getDsc());
            setClassname(old.getClassname());
            setSearchType(old.getSearchType());
            setTemplateCd(old.getTemplateCd());
            setTemplateDocId(old.getTemplateDocId());
            setCorrespondenceTypeCd(old.getCorrespondenceTypeCd());
            setAttributes(old.getAttributes());
        }
    }

    public final void populate(ResultSet rs) {

        try {
            setBulkId(AbstractDAO.getInteger(rs, "bulk_id"));
            setName(rs.getString("bulk_nm"));
            setGroupNm(rs.getString("bulk_group_nm"));
            setMenu(rs.getString("bulk_menu"));
            setDsc(rs.getString("bulk_dsc"));
            setClassname(rs.getString("bulk_class"));
            setSearchType(rs.getString("bulk_search_type"));
            setTemplateCd(rs.getString("template_doc_type_cd"));
            setTemplateDocId(rs.getString("document_id"));
            setCorrespondenceTypeCd(rs.getString("correspondence_type_cd"));
            
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));

            try {
                if (rs.getString("code") != null) {
                    SimpleDef tempAttribute;
                    do {
                        tempAttribute = new SimpleDef();
                        tempAttribute.populate(rs);
                        setAttributeEnabled(tempAttribute);
                    } while (rs.next());
                }
            }
            catch (SQLException sqle) {
                logger.debug("Optional field error: " + sqle.getMessage());
            }
        } 
        catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }
    }

    public final Integer getBulkId() {
        return _bulkId;
    }

    public final void setBulkId(Integer id) {
        _bulkId = id;
    }

    public final String getGroupNm() {
        return _groupNm;
    }

    public final void setGroupNm(String groupNm) {
        _groupNm = groupNm;
    }

    public final String getMenu() {
        return _menu;
    }

    public final void setMenu(String menu) {
        _menu = menu;
    }

    public final String getName() {
        return _name;
    }

    public final void setName(String name) {
        _name = name;
    }

    public final String getDsc() {
        return _dsc;
    }

    public final void setDsc(String dsc) {
        _dsc = dsc;
    }

    public final String getClassname() {
        return _classname;
    }

    public final void setClassname(String classname) {
        _classname = classname;
    }

    public final String getSearchType() {
        return _searchType;
    }

    public final void setSearchType(String searchType) {
        _searchType = searchType;
    }

    public final String getTemplateCd() {
        return _templateCd;
    }

    public final void setTemplateCd(String templateCd) {
        _templateCd = templateCd;
    }

    public final String getTemplateDocId() {
        return _templateDocId;
    }

    public final void setTemplateDocId(String templateDocId) {
        _templateDocId = templateDocId;
    }

    public final String getCorrespondenceTypeCd() {
        return _correspondenceCd;
    }

    public final void setCorrespondenceTypeCd(String correspondenceCd) {
        _correspondenceCd = correspondenceCd;
    }

    public final void setAttributes(List<SimpleDef> attributes) {
        _attributes = new HashMap<String, SimpleDef>();
        if (attributes != null) {
            for (SimpleDef attr : attributes) {
                setAttributeEnabled(attr);
            }
        }
    }

    public final List<SimpleDef> getAttributes() {
        return new ArrayList<SimpleDef>(_attributes.values());
    }

    public final boolean isAttributeEnabled(String code) {
        return _attributes.containsKey(code);
    }

    public final boolean isAttributeEnabled(SimpleDef attribute) {
        return _attributes.containsKey(attribute.getCode());
    }

    public final void setAttributeEnabled(SimpleDef attribute) {
        if (attribute == null || attribute.getCode() == null) {
            throw new IllegalArgumentException("SimpleDef is either null or is missing an attribute code.");
        }
        _attributes.put(attribute.getCode(), attribute);
    }

    /** Silently ignores attribute if not previously enabled. */
    public final void setAttributeDisabled(String code) {
        _attributes.remove(code);
    }

    /** Silently ignores attribute if not previously enabled. */
    public final void setAttributeDisabled(SimpleDef attribute) {
        _attributes.remove(attribute.getCode());
    }

}
