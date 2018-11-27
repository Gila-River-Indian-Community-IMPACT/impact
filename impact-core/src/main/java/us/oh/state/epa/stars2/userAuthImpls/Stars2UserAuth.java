package us.oh.state.epa.stars2.userAuthImpls;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDefBase;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;
import us.oh.state.epa.stars2.framework.userAuth.UserAttributes;
import us.oh.state.epa.stars2.framework.userAuth.UserAuth;
import us.oh.state.epa.stars2.framework.util.CheckVariable;

@Component("userAuth")
@SuppressWarnings("serial")
public class Stars2UserAuth implements UserAuth {
    private transient Logger logger = Logger.getLogger(this.getClass());
    private String instanceName;
    private String encryptionAlgorithm;
    private boolean encrypt;

    @Autowired private InfrastructureService infraBO;

    public boolean start(Properties params, String inInstanceName)
            throws UnableToStartException {
        this.instanceName = inInstanceName;

        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        Node root = cfgMgr.getNode("app");

        root = cfgMgr.getNode("app.authentication");

        CheckVariable.notNull(root);
        encryptionAlgorithm = root.getAsString("passwordEncryptionAlgorithm");
        CheckVariable.notNull(encryptionAlgorithm);
        encryptionAlgorithm = encryptionAlgorithm.toUpperCase();

        if ((encryptionAlgorithm.compareTo("MD5") == 0)
                || encryptionAlgorithm.startsWith("SHA")) {
            encrypt = true;
        } else {
            encrypt = false;
        }
        return true;
    }

    public UserAttributes checkAuthentication(String userName, String password) {
        UserAttributes ret = null;
        UserDefBase userDef = null;

        try {
            String tempPassword = encryptPassword(password);
            userDef = infraBO.checkAuthentication(userName, tempPassword);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            throw new RuntimeException(re);
        }

        ret = populateUserAttributesBase(userDef);

        return ret;
    }
    
    public UserAttributes checkUserExists(String userName){
    	UserAttributes ret = null;
        UserDefBase userDef = null;

        try {
            userDef = infraBO.checkUserExists(userName);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            throw new RuntimeException(re);
        }

        ret = populateUserAttributesBase(userDef);

        return ret;
    	
    }
    
    public boolean checkUserPresent(String userName){
    	boolean isExits;

        try {
            isExits = infraBO.checkUserPresent(userName);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            throw new RuntimeException(re);
        }

        

        return isExits;
    	
    }
    
    public boolean createInternalUser(String userName){
    	boolean isUserCreated = false;
    	try {
            isUserCreated = infraBO.createInternalUser(userName);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            throw new RuntimeException(re);
        }
    	return isUserCreated;
    }

    public UserAttributes checkAuthentication(int userId, String password) {
        UserAttributes ret = null;
        UserDefBase userDef = null;

        try {
            String tempPassword = encryptPassword(password);
            userDef = infraBO.checkAuthentication(userId, tempPassword);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            throw new RuntimeException(re);
        }

        ret = populateUserAttributesBase(userDef);

        return ret;
    }

    public UserAttributes changePassword(UserAttributes currentUserAttr,
            String newPassword) {
        UserAttributes ret = null;

        return ret;
    }

    public UserAttributes retrieveUserAttributes(Long userId) {
        UserAttributes ret = null;
        UserDef userDef = null;

        try {
            userDef = infraBO.retrieveUserDef(userId.intValue());
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            throw new RuntimeException(re);
        }

        ret = populateUserAttributesBase(userDef);

        return ret;
    }

    public UserAttributes retrieveUserAttributes(String networkLoginNm) {
        UserAttributes ret = null;

        return ret;
    }

    /* STARS2 version
    public LinkedHashMap<String, UseCase> retrieveUserUseCases(int userId) {
        LinkedHashMap<String, UseCase> ret = new LinkedHashMap<String, UseCase>();

        try {
            // Admin user gets all usecases, otherwise get the useCases for this
            // user.
            if (userId == 1) {
                UseCase[] tempUseCases = infraBO.retrieveAllUseCases();

                for (UseCase useCase : tempUseCases) {
                    if (useCase.getUseCase().compareToIgnoreCase("readOnly") != 0) {
                        ret.put(useCase.getUseCase(), useCase);
                    }
                }
            } else {
                ret = infraBO.retrieveUseCases(userId);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ret;
    }
    */
    
    // IMPACT version ... in IMPACT, user id 1 belongs to an actual user (NOT Admin)
    public LinkedHashMap<String, UseCase> retrieveUserUseCases(int userId) {
        LinkedHashMap<String, UseCase> ret = new LinkedHashMap<String, UseCase>();

        try {
                ret = infraBO.retrieveUseCases(userId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ret;
    }

    public String getInstanceName() {
        return instanceName;
    }

    private UserAttributes populateUserAttributesBase(UserDef userDef) {
        UserAttributes ret = null;

        if (userDef != null) {
            ret = new UserAttributes(userDef);

            ret.setUserId(userDef.getUserId());
            ret.setUserName(userDef.getNetworkLoginNm());
//            ret.setPassword(userDef.getPasswordVal());
//            ret.setPasswordExpiration(userDef.getPasswordExpDt());
        }

        return ret;
    }
    
    private UserAttributes populateUserAttributesBase(UserDefBase userDef) {
        UserAttributes ret = null;

        if (userDef != null) {
            ret = new UserAttributes(userDef);

            ret.setUserId(userDef.getUserId());
            ret.setUserName(userDef.getNetworkLoginNm());
//            ret.setPasswordExpiration(userDef.getPasswordExpDt());
        }

        return ret;
    }

    private String encryptPassword(String password) {
        String ret = password;

        try {
            if (encrypt) {
                MessageDigest md = MessageDigest
                        .getInstance(encryptionAlgorithm);

                byte[] temp = md.digest(password.getBytes());
                ret = new String(Base64.encodeBase64(temp));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return ret;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
    
    public boolean checkUserActive(String userName){
    	boolean isActive;

        try {
            isActive = infraBO.retrieveUserStatusByLogin(userName);
        } catch (RemoteException re) {
            logger.error(re.getMessage(), re);
            throw new RuntimeException(re);
        }

        return isActive;
    }
}
