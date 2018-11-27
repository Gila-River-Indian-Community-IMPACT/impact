package us.oh.state.epa.stars2.database.dbObjects.infrastructure;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;

/**
 * SecurityGroup.
 * 
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 * 
 * @version $Revision: 1.8 $
 * @author $Author: kbradley $
 */
/**
 * @author Kbradley
 * 
 */
public class SecurityGroup extends BaseDB {
    private Integer securityGroupId;
    private String securityGroupName;
    private String appTypeCd;
    private HashMap<Integer, UseCase> useCases;

    public SecurityGroup() {
        super();
    }

    public SecurityGroup(SecurityGroup old) {
        super(old);

        if (old != null) {
            setSecurityGroupId(old.getSecurityGroupId());
            setSecurityGroupName(old.getSecurityGroupName());
            setAppTypeCd(old.getAppTypeCd());

            useCases = new HashMap<Integer, UseCase>();
            for (UseCase useCase : old.getUseCases().values()) {
                useCases.put(useCase.getSecurityId(), useCase);
            }
        }
    }

    public final String getAppTypeCd() {
        return appTypeCd;
    }

    public final void setAppTypeCd(String appTypeCd) {
        this.appTypeCd = appTypeCd;
    }

    /**
     * @return
     */
    public final HashMap<Integer, UseCase> getUseCases() {
        if (useCases == null) {
            useCases = new HashMap<Integer, UseCase>();
        }

        return useCases;
    }

    /**
     * @param useCases
     */
    public final void setUseCases(HashMap<Integer, UseCase> useCases) {
        this.useCases = useCases;
    }

    /**
     * @param useCase
     */
    public final void addUseCase(UseCase useCase) {
        if (useCases == null) {
            useCases = new HashMap<Integer, UseCase>();
        }

        if (useCase != null) {
            useCases.put(useCase.getSecurityId(), useCase);
        }
    }

    /**
     * SecurityGroupId.
     * 
     * @return Integer
     */
    public final Integer getSecurityGroupId() {
        return securityGroupId;
    }

    /**
     * SecurityGroupId.
     * 
     * @param securityGroupId
     */
    public final void setSecurityGroupId(Integer securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    /**
     * SecurityGroupName.
     * 
     * @return String
     */
    public final String getSecurityGroupName() {
        return securityGroupName;
    }

    /**
     * SecurityGroupName.
     * 
     * @param securityGroupName
     */
    public final void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }

    public final void populate(ResultSet rs) {
        try {
            setSecurityGroupId(AbstractDAO.getInteger(rs, "security_group_id"));
            setSecurityGroupName(rs.getString("security_group_nm"));
            setLastModified(AbstractDAO.getInteger(rs, "security_group_lm"));

            try {
                if (AbstractDAO.getInteger(rs, "security_id") != null) {
                    do {
                        UseCase tempUseCase = new UseCase();
                        tempUseCase.populate(rs);
                        addUseCase(tempUseCase);
                    } while (rs.next());
                }
            } catch (SQLException sqle) {
                logger.warn("Optional field error. " + sqle.getMessage());
            }
		} catch (SQLException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e.getMessage());
			}
		}
    }
}
