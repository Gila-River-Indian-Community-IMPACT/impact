package us.oh.state.epa.stars2.framework.userAuth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;

public class UseCase extends BaseDB implements java.io.Serializable {
    private Integer parentId;
    private String useCase;
    private String useCaseName;
    private Integer securityId;
    private ArrayList<UseCase> children = new ArrayList<UseCase>();
    
    public UseCase() {
        super();
    }
    
    public UseCase(UseCase old) {
        super(old);
        
        if (old != null) {
            setParentId(old.parentId);
            setUseCase(old.useCase);
            setUseCaseName(old.useCaseName);
            setSecurityId(old.securityId);
            setChildren(old.children);
        }
    }
    
    public final ArrayList<UseCase> getChildren() {
        return children;
    }

    public final void addChild(UseCase child) {
        if (child != null) {
            this.children.add(child);
        }
    }

    private void setChildren(ArrayList<UseCase> children) {
        this.children = children;
    }

    public final String getUseCaseName() {
        return useCaseName;
    }

    public final void setUseCaseName(String useCaseName) {
        this.useCaseName = useCaseName;
    }

    public final Integer getParentId() {
        return parentId;
    }

    public final void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public final Integer getSecurityId() {
        return securityId;
    }

    public final void setSecurityId(Integer securityId) {
        this.securityId = securityId;
    }

    public final String getUseCase() {
        return useCase;
    }

    public final void setUseCase(String useCase) {
        this.useCase = useCase;
    }

    public final void populate(ResultSet rs) {
        try {
            setSecurityId(AbstractDAO.getInteger(rs, "security_id"));
            setParentId(AbstractDAO.getInteger(rs, "parent_id"));
            setUseCase(rs.getString("usecase"));
            setUseCaseName(rs.getString("usecase_nm"));
            setLastModified(AbstractDAO.getInteger(rs, "security_template_lm"));
        } catch (SQLException sqle) {
            logger.error(sqle.getMessage(), sqle);
        }
    }
}
