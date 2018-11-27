package us.oh.state.epa.stars2.framework.userAuth;

import java.util.LinkedHashMap;

import us.oh.state.epa.stars2.framework.config.Component;

/**
 * User Authentication and Authorization Interface.
 *
 * <DL>
 * <DT><B>Copyright:</B></DT>
 * <DD>Copyright 2002 Mentorgen, LLC</DD>
 * <DT><B>Company:</B></DT>
 * <DD>Mentorgen, LLC</DD>
 * </DL>
 *
 * @version 1.0
 * @author Ken Bradley
 */

public interface UserAuth extends Component {
    final String DEFAULT = "app.authentication";

    /**
     * @param userName
     * @param password
     * @return
     */
    UserAttributes checkAuthentication(String userName, String password);
    
    /**
     * @param userName
     * @return
     */
    UserAttributes checkUserExists(String userName);
    
    /**
     * @param userName
     * @return
     */
    boolean checkUserPresent(String userName);
    
    /**
     * @param userName
     * @return
     */
    boolean createInternalUser(String userName);

    /**
     * @param userId
     * @param password
     * @return
     */
    UserAttributes checkAuthentication(int userId, String password);

    /**
     * @param currentUserAttr
     * @param newPassword
     * @return
     */
    UserAttributes changePassword(UserAttributes currentUserAttr,
            String newPassword);

    /**
     * @param userId
     * @return
     */
    UserAttributes retrieveUserAttributes(Long userId);

    /**
     * @param networkLoginNm
     * @return
     */
    UserAttributes retrieveUserAttributes(String networkLoginNm);

    /**
     * @param userId
     * @return
     */
    LinkedHashMap<String, UseCase> retrieveUserUseCases(int userId);
    
    /**
     * @param userName
     * @return boolean
     */
    public boolean checkUserActive(String userName);
}
