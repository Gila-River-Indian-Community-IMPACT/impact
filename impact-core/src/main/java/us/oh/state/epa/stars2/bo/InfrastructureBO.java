package us.oh.state.epa.stars2.bo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.schemas.sharepoint.soap.Copy;
import com.microsoft.schemas.sharepoint.soap.CopyErrorCode;
import com.microsoft.schemas.sharepoint.soap.CopyResult;
import com.microsoft.schemas.sharepoint.soap.CopyResultCollection;
import com.microsoft.schemas.sharepoint.soap.CopySoap;
import com.microsoft.schemas.sharepoint.soap.DestinationUrlCollection;
import com.microsoft.schemas.sharepoint.soap.FieldInformation;
import com.microsoft.schemas.sharepoint.soap.FieldInformationCollection;
import com.microsoft.schemas.sharepoint.soap.FieldType;

import net.sf.jasperreports.engine.JasperPrint;
import us.oh.state.epa.aport.admin.domain.User;
import us.oh.state.epa.common.biz.Money;
import us.oh.state.epa.common.data.OID;
import us.oh.state.epa.common.util.DateRange;
import us.oh.state.epa.common.util.Range;
import us.oh.state.epa.revenues.domain.Revenue;
import us.oh.state.epa.revenues.domain.RevenueSortObject;
import us.oh.state.epa.revenues.domain.RevenueType;
import us.oh.state.epa.revenues.service.RevenuesService;
import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.helpers.FacilityHelper;
import us.oh.state.epa.stars2.bo.helpers.InfrastructureHelper;
import us.oh.state.epa.stars2.database.adhoc.DataGridCellDef;
import us.oh.state.epa.stars2.database.adhoc.DataGridRow;
import us.oh.state.epa.stars2.database.adhoc.DataSet;
import us.oh.state.epa.stars2.database.dao.CorrespondenceDAO;
import us.oh.state.epa.stars2.database.dao.DetailDataDAO;
import us.oh.state.epa.stars2.database.dao.DocumentDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.InfrastructureDAO;
import us.oh.state.epa.stars2.database.dao.InvoiceDAO;
import us.oh.state.epa.stars2.database.dao.ServiceCatalogDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.Task;
import us.oh.state.epa.stars2.database.dbObjects.Task.TaskType;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.RelocateRequest;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTest;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReport;
import us.oh.state.epa.stars2.database.dbObjects.document.Correspondence;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.document.ReportDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.TemplateDocument;
import us.oh.state.epa.stars2.database.dbObjects.document.TmpDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsDocument;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.EmissionsReportSearch;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.StringContainer;
import us.oh.state.epa.stars2.database.dbObjects.facility.EventLog;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.SubmissionLog;
import us.oh.state.epa.stars2.database.dbObjects.facilityRequest.FacilityRequest;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.BulkDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ContactType;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountryDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.CountyDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DaemonInfo;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.FieldAuditLog;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ForeignKeyReference;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.NewspaperDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.PredicateDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ReportAttribute;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.ReportDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccLevel;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SchedulerJob;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SecurityGroup;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Shape;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.StateDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDefBase;
import us.oh.state.epa.stars2.database.dbObjects.invoice.Invoice;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceDetail;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceList;
import us.oh.state.epa.stars2.database.dbObjects.invoice.InvoiceNote;
import us.oh.state.epa.stars2.database.dbObjects.invoice.MultiInvoiceList;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEUCategory;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCNonChargePollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCDataImportPollutant;
import us.oh.state.epa.stars2.database.dbObjects.workflow.EnumDetail;
import us.oh.state.epa.stars2.def.AdjustmentType;
import us.oh.state.epa.stars2.def.ApplicationTypeDef;
import us.oh.state.epa.stars2.def.BaseDef;
import us.oh.state.epa.stars2.def.ComplianceReportStatusDef;
import us.oh.state.epa.stars2.def.ContactTypeDef;
import us.oh.state.epa.stars2.def.CorrespondenceDirectionDef;
import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.EmissionReportsRealDef;
import us.oh.state.epa.stars2.def.EventLogTypeDef;
import us.oh.state.epa.stars2.def.GeoPolygonDef;
import us.oh.state.epa.stars2.def.InvoiceState;
import us.oh.state.epa.stars2.def.PermitClassDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.RelocationAttachmentTypeDef;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.def.RevenueSearchState;
import us.oh.state.epa.stars2.def.RevenueState;
import us.oh.state.epa.stars2.def.RevenueTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.def.TemplateDocTypeDef;
import us.oh.state.epa.stars2.def.WrapnDef;
import us.oh.state.epa.stars2.fileIndexer.FileDocument;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.config.Config;
import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;
import us.oh.state.epa.stars2.framework.config.Node;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.exception.UnableToStartException;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;
import us.oh.state.epa.stars2.framework.util.CheckVariable;
import us.oh.state.epa.stars2.portal.application.ApplicationDetail;
import us.oh.state.epa.stars2.portal.client.ImpactPortalClient;
import us.oh.state.epa.stars2.portal.compliance.ComplianceReports;
import us.oh.state.epa.stars2.scheduler.Stars2Scheduler;
import us.oh.state.epa.stars2.util.DocumentGenerationBean;
import us.oh.state.epa.stars2.util.DocumentGenerationException;
import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.ContactUtil;
import us.oh.state.epa.stars2.webcommon.DisplayUtil;
import us.oh.state.epa.stars2.webcommon.FacesUtil;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;
import us.oh.state.epa.stars2.webcommon.ValidationBase;
import us.oh.state.epa.stars2.webcommon.reports.NtvReport;
import us.wy.state.deq.impact.database.dao.ContactDAO;
import us.wy.state.deq.impact.database.dao.EnviteDAO;
import us.wy.state.deq.impact.database.dao.envite.EnviteRestDAO;
import us.wy.state.deq.impact.database.dbObjects.infrastructure.DistrictDef;
import us.wy.state.deq.impact.database.dbObjects.monitoring.MonitorReport;

/**
 * <p>
 * Title: InfrastructureBO
 * </p>
 * 
 * <p>
 * Description: This is the Business Object for infrastructure.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author kbradley
 * @version 1.0
 */
@Transactional(rollbackFor=Exception.class)
@Service
public class InfrastructureBO extends BaseBO implements InfrastructureService {
	
	static private int kludgeDennis = 125;
	static public boolean testWithoutRevenues = true; // TODO DENNIS change to
														// -->true for testing
														// without Revenues
														// System;
	private boolean deleteAttachmentFiles = true;
	private final static DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yyyy hh:mm:ss");
	private boolean trace = false; // true; // for debugging

	private boolean debugTaskDelete = false; // for debugging.

	@Autowired private InfrastructureHelper infrastructureHelper;
	@Autowired private FacilityHelper facilityHelper;

    /**
     * 
     * @ejb.interface-method view-type="remote"
     * @ejb.transaction type="NotSupported"
     */
    public LinkedHashMap<String, Integer> retrieveFpIdAndNm()
            throws DAOException {
        SimpleIdDef[] sid = infrastructureDAO()
                .retrieveDescAndId("InfrastructureSQL.retrieveFpIdAndNm");

        LinkedHashMap<String, Integer> ret = new LinkedHashMap<String, Integer>();
        for (SimpleIdDef temp : sid) {
            ret.put(temp.getDescription(), temp.getId());
        }

        return ret;
    }
    
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SccLevel[] retrieveSccLevelCodes(int level, String level1Desc,
			String level2Desc, String level3Desc, String level4Desc,
			String sccId) throws DAOException {
		return infrastructureDAO().retrieveSccLevelCodes(level, level1Desc,
				level2Desc, level3Desc, level4Desc, sccId);
	}

	/**
	 * This method is for ITS to add users to Stars2. The only things that are
	 * required are a numeric userId, which must match ITS's, and the loginName,
	 * which doesn't have to match ITS's, but probably should.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean createUser(int userId, String loginName, int[] roles)
			throws DAOException {
		boolean ret = false;

		if (loginName != null) {
			Transaction trans = TransactionFactory.createTransaction();
			InfrastructureDAO infrastructureDAO = infrastructureDAO(trans);
			UserDef userDef = new UserDef();

			userDef.setUserId(userId);
			userDef.setNetworkLoginNm(loginName);
			userDef.setPasswordVal(encryptPassword("NoLogin4U"));

			try {
				infrastructureDAO.createUserDef(userDef);

				if (roles != null && roles.length > 0) {
					for (int roleId : roles) {
						infrastructureDAO.addUserRole(userId, roleId);
					}
				}
				trans.complete();
				ret = true;
			} catch (DAOException de) {
				cancelTransaction(trans, de);
			} finally {
				closeTransaction(trans);
			}
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public UseCase createUseCase(UseCase newUseCase) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infrastructureDAO = infrastructureDAO(trans);
		UseCase ret = null;

		try {
			ret = infrastructureDAO.createUseCase(newUseCase);

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public UseCase[] retrieveAllUseCases() throws DAOException {
		UseCase[] ret = infrastructureDAO().retrieveAllUseCases();

		LinkedHashMap<Integer, UseCase> tempMap = new LinkedHashMap<Integer, UseCase>();

		// Find highest level parents first.
		for (UseCase tempUseCase : ret) {
			if (tempUseCase.getParentId() == null) {
				tempMap.put(tempUseCase.getSecurityId(), tempUseCase);
			}
		}

		// Now add the rest of the usecases and build the parent child
		// relationships.
		for (UseCase tempUseCase : ret) {
			if (tempUseCase.getParentId() != null) {
				tempMap.get(tempUseCase.getParentId()).addChild(tempUseCase);
				tempMap.put(tempUseCase.getSecurityId(), tempUseCase);
			}
		}

		return tempMap.values().toArray(new UseCase[0]);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public LinkedHashMap<String, UseCase> retrieveUseCases(int userId)
			throws DAOException {
		return infrastructureDAO().retrieveUseCases(userId);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public UseCase[] retrieveUseCases(Integer securityGroupId)
			throws DAOException {

		return infrastructureDAO().retrieveUseCases(securityGroupId);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef getSimpleDefMetaData(SimpleDef simpleDef)
			throws DAOException {
		return infrastructureDAO().getSimpleDefMetaData(simpleDef);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public UserDefBase checkAuthentication(String userName, String password) {
		UserDefBase ret = null;

		try {
			ret = infrastructureDAO().retrieveUserDefByLoginAndPassword(
					userName, password);
		} catch (DAOException de) {
			logger.error(de.getMessage() + " for " + userName, de);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public UserDefBase checkUserExists(String userName) {
		UserDefBase ret = null;

		try {
			ret = infrastructureDAO().retrieveUserDefByLogin(userName);
		} catch (DAOException de) {
			logger.error(de.getMessage() + " for " + userName, de);
		}

		return ret;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean checkUserPresent(String userName) {
		boolean ret=false;

		try {
			ret = infrastructureDAO().retrieveUserByLogin(userName);
		} catch (DAOException de) {
			logger.error(de.getMessage() + " for " + userName, de);
		}

		return ret;
	}

	public boolean createInternalUser(String userName) {

		boolean ret = false;

		try {
			ret = infrastructureDAO().createInternalUser(userName);
		} catch (DAOException de) {
			logger.error(de.getMessage() + " for " + userName, de);
		}

		return ret;

	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public UserDefBase checkAuthentication(int userId, String password) {
		UserDefBase ret = null;

		try {
			ret = infrastructureDAO().retrieveUserDefByIdAndPassword(userId,
					password);
		} catch (DAOException de) {
			logger.error(de.getMessage() + " for " + userId, de);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyUser(UserDef userDef) throws DAOException {
		boolean ret = false;
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		//check whether a user have both admin and non-admin role
		int admin = 0;
		int nonadmin = 0;
		for (SecurityGroup tempGroup : userDef.getSecurityGroups()) {
			if (tempGroup.getSecurityGroupId()==0){
				admin++;
			}else {
				nonadmin++;
			}
		}
		//prevent assigning user both admin and non-admin role
		if (admin>0 && nonadmin>0){
			DisplayUtil.displayError("A user cannot have Administrator Role with other roles.");
			ret = false;
		} else {
			try {
				Integer userId = userDef.getUserId();
				// Remove all the current roles for this user, will add them back
				// later
				infraDAO.removeAllUserRoles(userId);
	
				for (SecurityGroup tempGroup : userDef.getSecurityGroups()) {
					infraDAO.addUserRole(userId, tempGroup.getSecurityGroupId());
				}
	
				ret = infraDAO.modifyUser(userDef);
	
				trans.complete();
			} catch (DAOException de) {
				cancelTransaction(trans, de);
			} finally {
				closeTransaction(trans);
			}
		}
		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public UseCase retrieveUseCase(String useCase) throws DAOException {
		return infrastructureDAO().retrieveUseCase(useCase);
	}

	/**
	 * @param userId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public UserDef retrieveUserDef(Integer userId) throws DAOException {
		return infrastructureDAO().retrieveUserDef(userId);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SecurityGroup retrieveSecurityGroup(Integer securityGroupId)
			throws DAOException {
		return infrastructureDAO().retrieveSecurityGroup(securityGroupId);
	}

	/**
	 * Retrieve SecurityGroups for given application type.
	 * 
	 * @param String
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SecurityGroup[] retrieveSecurityGroups() throws DAOException {
		return infrastructureDAO().retrieveSecurityGroups();
	}

	/**
	 * Retrieves County associated with given countyId
	 * 
	 * @param countyId
	 * @return County associated with given countyId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public CountyDef retrieveCounty(String countyCd) throws DAOException {
		return infrastructureDAO().retrieveCounty(countyCd);
	}
	
	public PredicateDef[] retrievePredicates() throws DAOException {
		return infrastructureDAO().retrievePredicates();
	}

	/**
	 * Retrieves NewspaperDef associated with given countyId
	 * 
	 * @param countyId
	 * @return NewspaperDef associated with given countyId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public NewspaperDef retrieveNewspaper(String countyCd) throws DAOException {
		return infrastructureDAO().retrieveNewspaper(countyCd);
	}

	/**
	 * Retrieves all defined Counties
	 * 
	 * @return CountyDef[] an array of all defined counties
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public CountyDef[] retrieveCounties() throws DAOException {
		return infrastructureDAO().retrieveCounties();
	}
	
	/**
	 * Retrieves all Counties for given District
	 * 
	 * @return CountyDef[] an array of all defined counties for given district
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public CountyDef[] retrieveDistrictCounties(String districtCd) throws DAOException {
		return infrastructureDAO().retrieveDistrictCounties(districtCd);
	}
	
//	/**
//	 * Retrieves all defined Cities
//	 * 
//	 * @return CityDef[] an array of all defined cities
//	 * 
//	 * @ejb.interface-method view-type="remote"
//	 * @ejb.transaction type="NotSupported"
//	 */
//	public CityDef[] retrieveCities() throws DAOException {
//		return infrastructureDAO().retrieveCities();
//	}
//	
//	/**
//	 * Retrieves all Cities for given County
//	 * 
//	 * @return CityDef[] an array of all defined cities for given county
//	 * 
//	 * @ejb.interface-method view-type="remote"
//	 * @ejb.transaction type="NotSupported"
//	 */
//	public CityDef[] retrieveCountyCities(String countyCd) throws DAOException {
//		return infrastructureDAO().retrieveCountyCities(countyCd);
//	}

	/**
	 * Retrieves All defined States
	 * 
	 * @return StateDef[] an array of all defined states
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public StateDef[] retrieveStates() throws DAOException {
		return infrastructureDAO().retrieveStates();
	}

	public DistrictDef[] retrieveDistricts() throws DAOException {
		return infrastructureDAO().retrieveDistricts();
	}

	/**
	 * Returns an array of all countries contained in persistent storage. In the
	 * event of a data access failure, the exception is logged and then rethrown
	 * as a <tt>DAOException</tt>.
	 * 
	 * @return CountryDef[] An array of CountryDef objects.
	 * 
	 * @throws DAOException
	 *             Data retrieval failed.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public CountryDef[] retrieveCountries() throws DAOException {
		return infrastructureDAO().retrieveCountries();
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public SecurityGroup createSecurityGroup(SecurityGroup newGroup)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		SecurityGroup ret = null;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			SecurityGroup tempGrp = infraDAO.createSecurityGroup(newGroup);

			for (UseCase tempUseCase : tempGrp.getUseCases().values()) {
				infraDAO.addUseCase(tempUseCase.getSecurityId(),
						tempGrp.getSecurityGroupId());
			}

			trans.complete();
			ret = tempGrp;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifySecurityGroup(SecurityGroup role) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			infraDAO.removeAllRoleUseCases(role.getSecurityGroupId());

			HashMap<Integer, UseCase> useCases = role.getUseCases();

			for (UseCase tempUseCase : useCases.values()) {
				infraDAO.addUseCase(tempUseCase.getSecurityId(),
						role.getSecurityGroupId());
			}

			trans.complete();
			ret = true;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeSecurityGroup(SecurityGroup group) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			// First remove any users from this group.
			infraDAO.removeUsersFromGroup(group.getSecurityGroupId());

			// Then remove all usecases from this group.
			infraDAO.removeAllRoleUseCases(group.getSecurityGroupId());

			// Lastly remove the group.
			infraDAO.removeSecurityGroup(group);

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}
		return;
	}

	/**
	 * Saves the given Address
	 * 
	 * @param address
	 *            address to save
	 * @return Address address saved, complete with addressId
	 * 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Address createAddress(Address address) throws DAOException {
		return infrastructureDAO().createAddress(address);
	}

	/**
	 * Returns contact for a give contact ID.
	 * 
	 * @return Contact
	 * 
	 * @throws DAOException
	 *             Data retrieval failed.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Contact retrieveContact(Integer contactId) throws DAOException {
		return infrastructureDAO().retrieveContact(contactId);
	}

	/**
	 * Creates the given Contact
	 * 
	 * @param contact
	 *            contact to create
	 * @return Contact contact created, complete with contactId
	 * 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Contact createContact(Contact contact) throws DAOException {

		Contact ret = null;
		Transaction trans = TransactionFactory.createTransaction();

		try {
//			InfrastructureHelper infraHelper = new InfrastructureHelper();
			ret = infrastructureHelper.createContact(contact, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * Saves the given Contact
	 * 
	 * @param contact
	 *            contact to save
	 * @return boolean
	 * 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyContact(Contact contact) throws DAOException {
		boolean ret = false;
		Transaction trans = TransactionFactory.createTransaction();
		try {
			ret = modifyContact(contact, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * Saves the given Contact
	 * 
	 * @param contact
	 *            contact to save trans
	 * @return boolean
	 * 
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyContact(Contact contact, Transaction trans)
			throws DAOException {
		boolean ret = false;

		if (contact != null) {
			InfrastructureDAO infrastructureDAO = infrastructureDAO(trans);
			try {
				Address contactAddress = contact.getAddress();

				if (contactAddress != null) {
					if (contactAddress.getAddressId() == null) {
						infrastructureDAO.createAddress(contactAddress);
					} else {
						infrastructureDAO.modifyAddress(contactAddress);
					}
				}

				infrastructureDAO.modifyContact(contact);

				if (contact.getContactTypes().size() > 0) {
					List<ContactType> existingTypes = infrastructureDAO
							.retrieveContactTypes(contact.getContactId());

					for (ContactType contactType : contact.getContactTypes()) {
						boolean needToAdd = true;
						for (ContactType exType : existingTypes) {
							if (contactType.getContactTypeCd().equals(
									exType.getContactTypeCd())
									&& contactType.getStartDate().equals(
											exType.getStartDate())) {
								needToAdd = false;
								if (contactType.getEndDate() != null) {
									exType.setEndDate(contactType.getEndDate());
									infrastructureDAO
											.deprecateContactType(exType);
								}
							}
						}
						if (needToAdd) {
							if (contactType.getEndDate() != null) {
								infrastructureDAO.addContactType(
										contact.getContactId(), contactType);
							} else {
								infrastructureDAO.addContactType(
										contact.getContactId(),
										contactType.getContactTypeCd(),
										contactType.getStartDate());
							}
						}

					}
				}

				ret = true;
			} catch (DAOException de) {
				throw de;
			}
		}

		return ret;
	}

	/**
	 * Returns an array of user names, formatted "lastName, firstName" and their
	 * corresponding userId.
	 * 
	 * @return SimpleIdDef[] Array of userId's and user name formatted as
	 *         "lastName, firstName"
	 * 
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveUserList(boolean excludeInactiveUsers)
			throws DAOException {
		return infrastructureDAO().retrieveUserList(excludeInactiveUsers);
	}

	/**
	 * Returns the <code>EnumDetail</code> object currently associated with this
	 * enum detail id. If no enum details are found for this enum detail id,
	 * then <tt>null</tt> is returned.
	 * 
	 * @param enumDetailId
	 *            the enum detail id of the object requested.
	 * 
	 * @return EnumDetail the enum detail object for this enum detail id.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public EnumDetail retrieveEnumDetail(int retrieveEnumDetail)
			throws DAOException {
		return detailDataDAO().retrieveEnumDetail(retrieveEnumDetail);
	}

	/**
	 * Returns all of the <code>EnumDetail</code> objects currently associated
	 * with this enum code. If no enum details are found for this enum code,
	 * then <tt>null</tt> is returned.
	 * 
	 * @param enumCd
	 *            The enum code of the object requested.
	 * 
	 * @return EnumDetail[] All enum details for this enum cd.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */

	public EnumDetail[] retrieveEnumDetails(String enumCd) throws DAOException {
		return detailDataDAO().retrieveEnumDetails(enumCd);
	}

	/**
	 * Returns all of the <code>EnumDetail</code> objects currently defined in
	 * the system. If no enum details are found, then an empty array is
	 * returned.
	 * 
	 * @return EnumDetail[] All enum details in the system.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public EnumDetail[] retrieveAllEnumDetails() throws DAOException {
		return detailDataDAO().retrieveAllEnumDetails();
	}

	/**
	 * Returns all of the <code>EnumDef</code> objects currently defined in the
	 * system. If no enum defs are found, then an empty array is returned.
	 * 
	 * @return EnumDef[] All enum details in the system.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveAllEnumDefs() throws DAOException {
		return detailDataDAO().retrieveAllEnumDefs();
	}

	/**
	 * Inserts a record into the Enum_Detail_Def table of the database.
	 * 
	 * @param ed
	 *            EnumDetail object to be inserted into the database.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public EnumDetail createEnumDetail(EnumDetail ed) throws DAOException {
		return detailDataDAO().createEnumDetail(ed);
	}

	/**
	 * Inserts a record into the Enum_Def table of the database.
	 * 
	 * @param enumCd
	 *            enum code.
	 * @param enumDsc
	 *            enum description.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public SimpleDef createEnumDef(String enumCd, String enumDsc)
			throws DAOException {
		return detailDataDAO().createEnumDef(enumCd, enumDsc);
	}

	/**
	 * Updates the corresponding Enum_Detail_Def record in the database to
	 * correspond to the contents of <code>ed</code>. Returns <tt>true</tt> if a
	 * record was updated and <tt>false</tt> if no actual database update
	 * occurred, i.e., the enum Id was not found in the database.
	 * 
	 * @param ed
	 *            EnumDetail that needs to be updated in the database.
	 * 
	 * @return <tt>true</tt> if a database record was updated, otherwise
	 *         <tt>false</tt>.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyEnumDetail(EnumDetail ed) throws DAOException {
		return detailDataDAO().modifyEnumDetail(ed);
	}

	/**
	 * Returns a single DaemonInfo object given its <tt>daemonCode</tt>
	 * identifier. If no corresponding object was found, returns <tt>null</tt>.
	 * A valid code has a maximum of four characters.
	 * 
	 * @param daemonCode
	 *            Daemon code.
	 * 
	 * @return DaemonInfo Corresponding info object.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public DaemonInfo retrieveDaemonInfo(String daemonCode) throws DAOException {
		return infrastructureDAO(CommonConst.READONLY_SCHEMA)
				.retrieveDaemonInfo(daemonCode);
	}

	/**
	 * Creates a new Daemon object in the database from this <tt>info</tt>. Note
	 * that the daemon code must be unique or the insertion attempt will fail.
	 * The description is not inserted by this request.
	 * 
	 * @param info
	 *            DaemonInfo to be inserted.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void createDaemonInfo(DaemonInfo info) throws DAOException {
		infrastructureDAO().createDaemonInfo(info);
	}

	/**
	 * Updates a Daemon object in the database with the values contained in
	 * <code>info</code>. Returns <tt>true</tt> if a record was actually updated
	 * and <tt>false</tt> if no update occurred, i.e., no corresponding
	 * "daemon code" was found in the database.
	 * 
	 * @param info
	 *            Info object containing updated values.
	 * 
	 * @return boolean <tt>true</tt> if a record was updated.
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyDaemonInfo(DaemonInfo info) throws DAOException {
		return infrastructureDAO().modifyDaemonInfo(info);
	}

	/**
	 * Returns all of the EnumDef objects currently in the database.
	 * 
	 * @return EnumDef[] All enumeration set definitions.
	 * 
	 * @throws Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveEnumDefs() throws DAOException {
		return detailDataDAO().retrieveAllEnumDefs();
	}

	/**
	 * Returns all ReportDefs currently in the database.
	 * 
	 * @return ReportDef[] All report definitions.
	 * 
	 * @throws Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ReportDef[] retrieveReportDefs() throws DAOException {
		ReportDef[] reports = infrastructureDAO().retrieveReportDefs();
		for (ReportDef report : reports) {
			report.setReportDocument(
					documentDAO().retrieveDocument(
							report.getReportDocumentId()));
		}
		return reports;
	}

	/**
	 * Returns all BulkDefs currently in the database.
	 * 
	 * @return BulkDef[] All report definitions.
	 * 
	 * @throws Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public BulkDef[] retrieveBulkDefs(String bulkMenu) throws DAOException {
		return infrastructureDAO().retrieveBulkDefs(bulkMenu);
	}

	/**
	 * Creates a new enumeration set in the database. The "enumDef" defines the
	 * code and description for the enumeration set. The "details" objects
	 * define the members of the enumeration set. Returns "true" if the set was
	 * successfully added to the system.
	 * 
	 * @param enumDef
	 *            EnumDef Enumeration definition object.
	 * @param details
	 *            EnumDetail[] Enumeration members.
	 * 
	 * @return boolean "True" if enumeration set was added.
	 * 
	 * @throws Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean createEnumeration(SimpleDef enumDef, EnumDetail[] details)
			throws DAOException {

		boolean result = false;
		// If we don't have all the "parts" for this enumeration, then don't
		// even bother.
		if ((enumDef != null) && (details != null) || (details.length == 0)) {
			Transaction trans = TransactionFactory.createTransaction();
			DetailDataDAO ddDAO = detailDataDAO(trans);

			try {
				// First, create the Enum_Def record.
				ddDAO.createEnumDef(enumDef.getCode(), enumDef.getDescription());

				String enumCd = enumDef.getCode();

				// Now, create the Enum_Details_Def records.
				for (EnumDetail ed : details) {
					ed.setEnumCd(enumCd);
					ddDAO.createEnumDetail(ed);
				}

				trans.complete();
				result = true;
			} catch (DAOException de) {
				cancelTransaction(trans, de);
			} finally {
				closeTransaction(trans);
			}
		}

		return result;
	}

	/**
	 * Updates an enumeration set in the database. The "enumDef" defines the
	 * code and description for the enumeration set. The "details" objects
	 * define the members of the enumeration set. Returns "true" if the set was
	 * successfully added to the system.
	 * 
	 * @param enumDef
	 *            EnumDef Enumeration definition object.
	 * @param details
	 *            EnumDetail[] Enumeration members.
	 * 
	 * @return boolean "True" if enumeration set was added.
	 * 
	 * @throws Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyEnumeration(SimpleDef enumDef, EnumDetail[] details)
			throws DAOException {
		boolean result = false;

		if (details != null) {
			Transaction trans = TransactionFactory.createTransaction();
			DetailDataDAO ddDAO = detailDataDAO(trans);
			try {
				// first, delete all enum details associated with this code
				ddDAO.removeEnumDetails(enumDef.getCode());

				// get new detail id
				for (EnumDetail ed : details) {
					ddDAO.createEnumDetail(ed);
				}

				trans.complete();
				result = true;
			} catch (DAOException de) {
				cancelTransaction(trans, de);
			} finally {
				closeTransaction(trans);
			}
		}

		return result;
	}

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleIdDef
	 * 
	 * @return SimpleIdDef[] an array containing all the values
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveSimpleIdDefs(String dbTable,
			String dbIdColumn, String dbDescColumn, String dbDeprecatedColumn,
			String sortByColumn) throws DAOException {

		return infrastructureDAO().retrieveSimpleIdDefs(dbTable, dbIdColumn,
				dbDescColumn, dbDeprecatedColumn, sortByColumn);
	}

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleDef
	 * 
	 * @return SimpleDef[] an array containing all the values
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveSimpleDefs(String dbTable, String dbCodeColumn,
			String dbDescColumn, String dbDeprecatedColumn, String sortByColumn)
			throws DAOException {

		return infrastructureDAO().retrieveSimpleDefs(dbTable, dbCodeColumn,
				dbDescColumn, dbDeprecatedColumn, sortByColumn);
	}

	/**
	 * Retrieves all values for a definition that has a code of type string -
	 * i.e., a SimpleDef which matches the where attribute value
	 * 
	 * @return SimpleDef[] an array containing all the values
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveSimpleDefs(String dbTable, String dbCodeColumn,
			String dbDescColumn, String dbDeprecatedColumn,
			String sortByColumn, String whereColumn, String whereValue)
			throws DAOException {

		return infrastructureDAO().retrieveSimpleDefs(dbTable, dbCodeColumn,
				dbDescColumn, dbDeprecatedColumn, sortByColumn, whereColumn,
				whereValue);
	}

	/**
	 * Creates a SimpleDef table entry i.e., a SimpleDef
	 * 
	 * @return SimpleDef
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public SimpleDef createSimpleDef(String dbTable, String dbCodeColumn,
			String dbDescColumn, SimpleDef sd) throws DAOException {

		return infrastructureDAO().createSimpleDef(dbTable, dbCodeColumn,
				dbDescColumn, sd);
	}

	/**
	 * Modifies a column table entry i.e., a for definition and reference tables
	 * 
	 * @return boolean
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyColumn(String dbTable, String keyColumn,
			String keyColumnType, String keyValue, String column,
			String columnType, String columnValue) throws DAOException {
		return infrastructureDAO().modifyColumn(dbTable, keyColumn,
				keyColumnType, keyValue, column, columnType, columnValue);
	}

	/**
	 * Modifies a SimpleDef table entry i.e., a SimpleDef
	 * 
	 * @return boolean
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifySimpleDef(String dbTable, String dbCodeColumn,
			String dbDescColumn, String dbDeprecatedColumn, SimpleDef sd)
			throws DAOException {
		return infrastructureDAO().modifySimpleDef(dbTable, dbCodeColumn,
				dbDescColumn, dbDeprecatedColumn, sd);
	}

	/**
	 * @return ReportDef
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ReportDef createReport(ReportDef report) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		ReportDef ret = null;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			if (null != report.getReportFile()) {
				Document reportDocument = new ReportDocument();
				reportDocument.setLastModifiedBy(1);
				reportDocument.setLastModifiedTS(new Timestamp(new Date().getTime()));
				reportDocument.setTemporary(false);
				reportDocument.setUploadDate(new Timestamp(new Date().getTime()));
				String fileName = report.getReportFile().getFilename();
				String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
				reportDocument.setExtension(ext);
				
				reportDocument = documentDAO().createDocument(reportDocument);
				DocumentUtil.createDocument(reportDocument.getPath(), report.getReportFile().getInputStream());
				
				report.setReportDocumentId(reportDocument.getDocumentID());
			}

			ret = infraDAO.createReport(report);

			for (ReportAttribute attr : report.getAttributes()) {
				infraDAO.addReportAttribute(ret.getId(), attr);
			}

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} catch (Exception e) {
			throw new DAOException(e.toString(),e);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @return boolean
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyReport(ReportDef report) throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			if (null != report.getReportFile()) {
				Document reportDocument = new ReportDocument();
				reportDocument.setLastModifiedBy(1);
				reportDocument.setLastModifiedTS(new Timestamp(new Date().getTime()));
				reportDocument.setTemporary(false);
				reportDocument.setUploadDate(new Timestamp(new Date().getTime()));
				String fileName = report.getReportFile().getFilename();
				String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
				reportDocument.setExtension(ext);
				
				reportDocument = documentDAO().createDocument(reportDocument);
				DocumentUtil.createDocument(reportDocument.getPath(), report.getReportFile().getInputStream());
				
				report.setReportDocumentId(reportDocument.getDocumentID());
			}

			infraDAO.modifyReport(report);

			if (null != report.getReportFile()) {
				ReportDef currentReport = infraDAO.retrieveReport(report.getId());
				if (null != currentReport && null != currentReport.getReportDocument()) {
					documentDAO().removeDocument(currentReport.getReportDocument());
				}
			}

			infraDAO.removeReportAttributes(report.getId());

			for (ReportAttribute attr : report.getAttributes()) {
				infraDAO.addReportAttribute(report.getId(), attr);
			}

			trans.complete();
			ret = true;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} catch (Exception e) {
			throw new DAOException(e.toString(),e);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @return boolean
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean removeReport(ReportDef report) throws DAOException {

		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			infraDAO.removeReportAttributes(report.getId());

			infraDAO.removeReport(report);

			if (null != report.getReportDocument()) {
				documentDAO().removeDocument(report.getReportDocument());
			}

			trans.complete();
			ret = true;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @return BulkDef
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public BulkDef createBulkDef(BulkDef bulkOperation) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		BulkDef ret = null;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			ret = infraDAO.createBulkDef(bulkOperation);

			for (SimpleDef attr : bulkOperation.getAttributes()) {
				infraDAO.addBulkAttribute(ret.getBulkId(), attr.getCode());
			}

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @return boolean
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyBulkDef(BulkDef bulkOperation) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			infraDAO.modifyBulkDef(bulkOperation);

			infraDAO.removeReportAttributes(bulkOperation.getBulkId());

			for (SimpleDef attr : bulkOperation.getAttributes()) {
				infraDAO.addBulkAttribute(bulkOperation.getBulkId(),
						attr.getCode());
			}

			trans.complete();
			ret = true;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param reportId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ReportDef retrieveReport(int reportId) throws DAOException {
		ReportDef report = null;
		report = infrastructureDAO().retrieveReport(reportId);

		if (null != report.getReportDocumentId()) {
			Document reportDocument = 
					documentDAO().retrieveDocument(report.getReportDocumentId());
			report.setReportDocument(reportDocument);
		}
		
		return report;
	}

	/**
	 * @param bulkId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public BulkDef retrieveBulkDef(int bulkId) throws DAOException {
		return infrastructureDAO().retrieveBulkDef(bulkId);
	}

	/**
	 * This method will generate the requested JasperReport and return a
	 * JasperPrint Object.
	 * 
	 * @param jasperReportFileNm
	 * @param reportParms
	 * @return JasperPrint
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public JasperPrint retrieveReport(String jasperReportFileNm,
			Map<String, Object> reportParms) throws DAOException {
		return infrastructureDAO().retrieveReport(jasperReportFileNm,
				reportParms);
	}

	/**
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SCEmissionsReport[] retrieveSCEmissionsReports() throws DAOException {
		return serviceCatalogDAO().retrieveEmissionsReports();
	}
	
	/**
	 * @param feeId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Fee retrieveFee(int feeId) throws DAOException {
		return serviceCatalogDAO().retrieveFee(feeId);
	}

	/**
	 * @param feeId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SCEUCategory retrieveCategoryByFeeId(int feeId) throws DAOException {
		return serviceCatalogDAO().retrieveCategoryByFeeId(feeId);
	}

	/**
	 * @param newFee
	 * @param categoryId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Fee createCategoryFee(Fee newFee, int categoryId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		Fee ret = null;
		ServiceCatalogDAO servCatalogDAO = serviceCatalogDAO(trans);

		try {
			ret = servCatalogDAO.createFee(newFee);

			servCatalogDAO.addFeeToCategory(categoryId, ret.getFeeId());

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param newcategory
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public SCEUCategory createSCEUCategory(SCEUCategory newCategory)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		SCEUCategory ret = null;
		ServiceCatalogDAO servCatalogDAO = serviceCatalogDAO(trans);

		try {
			ret = servCatalogDAO.createSCEUCategory(newCategory);

			processCategoryFees(newCategory, servCatalogDAO);

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param category
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifySCEUCategory(SCEUCategory category)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		ServiceCatalogDAO servCatalogDAO = serviceCatalogDAO(trans);

		try {
			servCatalogDAO.modifySCEUCategory(category);

			processCategoryFees(category, servCatalogDAO);

			trans.complete();
			ret = true;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param task
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyTask(Task task) throws DAOException {
		Transaction trans = null;
		boolean ret = false;

		try {
			trans = TransactionFactory.createTransaction();
			ret = modifyTask(task, trans);
			trans.complete();
		} catch (ServiceFactoryException sfe) {
			throw new DAOException(sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @return Task
	 * @throws ServiceFactoryException
	 * @throws RemoteException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyTask(Task task, Transaction trans)
			throws ServiceFactoryException, RemoteException {
		boolean ret = false;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			infraDAO.modifyTask(task);
			trans.complete();
			ret = true;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param fee
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyCategoryFee(Fee fee) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		boolean ret = false;
		ServiceCatalogDAO servCatalogDAO = serviceCatalogDAO(trans);

		try {
			servCatalogDAO.modifyFee(fee);

			trans.complete();
			ret = true;
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	private void processCategoryFees(SCEUCategory category,
			ServiceCatalogDAO servCatalogDAO) throws DAOException {
		if (category.getFees().size() > 0) {
			servCatalogDAO.removeSCCategoryFees(category.getCategoryId());

			for (Fee fee : category.getFees()) {
				if (fee.getFeeId() == null) {
					fee = servCatalogDAO.createFee(fee);
				}

				servCatalogDAO.addFeeToCategory(category.getCategoryId(),
						fee.getFeeId());
			}
		}
		return;
	}

	/**
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SCEUCategory[] retrieveEUCategories() throws DAOException {
		return serviceCatalogDAO().retrieveEUCategories();
	}

	/**
	 * @param categoryId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SCEUCategory retrieveEUCategory(int categoryId) throws DAOException {
		return serviceCatalogDAO().retrieveEUCategory(categoryId);
	}

	/**
	 * @param reportId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SCEmissionsReport retrieveSCEmissionsReport(Integer reportId)
			throws DAOException {
		return serviceCatalogDAO().retrieveEmissionsReport(reportId);
	}

	/**
	 * @param newReport
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public SCEmissionsReport createSCEmissionsReport(SCEmissionsReport newReport)
			throws DAOException {
		SCEmissionsReport ret = null;
		Transaction trans = TransactionFactory.createTransaction();
		ServiceCatalogDAO serviceDAO = serviceCatalogDAO(trans);

		try {
			ret = serviceDAO.createEmissionsReport(newReport);
			
			// If any of the PM pollutants are set by the user to be billed
			// based on allowable, we will set all PM pollutants to be billed
			// based on allowable.
			boolean pmBilledBasedOnPermitted = false;
			for (SCPollutant pollutant : ret.getPollutants()) {
				if (pollutant.isBilledBasedOnPermitted()
						&& pollutant.getPollutantCd().startsWith("PM")) {
					pmBilledBasedOnPermitted = true;
					break;
				}
			}

			for (SCPollutant pollutant : ret.getPollutants()) {
				if (pollutant.getPollutantCd().startsWith("PM") && pmBilledBasedOnPermitted) {
					pollutant.setBilledBasedOnPermitted(true);
				}
				pollutant.setSCReportId(ret.getId());
				serviceDAO.addPollutantToEmissionsReport(pollutant);
			}
			
			for (SCNonChargePollutant ncPollutant : ret.getNcPollutants()) {
				ncPollutant.setSCReportId(ret.getId());
				serviceDAO.addNonChargePollutantToEmissionsReport(ncPollutant);
			}
			
			for (SCDataImportPollutant scDataImportPollutant : ret.getDataImportPollutantList()) {
				scDataImportPollutant.setSCReportId(ret.getId());
				serviceDAO.addDataImportPollutantToEmissionsReport(scDataImportPollutant);
			}

			for (Fee fee : ret.getFees()) {
				fee = serviceDAO.createFee(fee);

				serviceDAO.addFeeToEmissionsReport(ret.getId(), fee.getFeeId());
			}

			for (StringContainer exemption : ret.getExemptions()) {
				serviceDAO.addExemptionToEmissionsReport(ret.getId(),
						exemption.getStr());
			}

			for (StringContainer tvClass : ret.getTvClassifications()) {
				serviceDAO.addTvClassificationToEmissionsReport(ret.getId(),
						tvClass.getStr());
			}
			
			// add Facility Selection Criteria data
			// Permit Class (Facility Class)
			serviceDAO.createPermitClassXrefs(ret.getId(),
					ret.getPermitClassCds());
					
			// Facility Type
			serviceDAO.createFacilityTypeXrefs(ret.getId(),
					ret.getFacilityTypeCds());

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param report
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifySCEmissionsReport(SCEmissionsReport report)
			throws DAOException {
		
		boolean ret = false;
		Transaction trans = TransactionFactory.createTransaction();
		ServiceCatalogDAO serviceDAO = serviceCatalogDAO(trans);

		try {
			serviceDAO.removeEmissionsReportPollutants(report.getId());
			
			// If any of the PM pollutants are set by the user to be billed based on allowable, we will set all PM pollutants to be billed based on allowable.
			boolean pmBilledBasedOnPermitted = false;
			for (SCPollutant pollutant : report.getPollutants()) {
				if (pollutant.isBilledBasedOnPermitted() && pollutant.getPollutantCd().startsWith("PM")) {
					pmBilledBasedOnPermitted = true;
					break;
				}
			}

			for (SCPollutant pollutant : report.getPollutants()) {
				if (pollutant.getPollutantCd().startsWith("PM") && pmBilledBasedOnPermitted) {
					pollutant.setBilledBasedOnPermitted(true);
				}
				pollutant.setSCReportId(report.getId());
				serviceDAO.addPollutantToEmissionsReport(pollutant);
			}
			
			serviceDAO.removeEmissionsReportNonChargePollutants(report.getId());
			for (SCNonChargePollutant ncPollutant : report.getNcPollutants()) {
				ncPollutant.setSCReportId(report.getId());
				serviceDAO.addNonChargePollutantToEmissionsReport(ncPollutant);
			}
			
			serviceDAO.removeEmissionsReportDataImportPollutants(report.getId());
			for (SCDataImportPollutant scDataImportPollutant : report.getDataImportPollutantList()) {
				scDataImportPollutant.setSCReportId(report.getId());
				serviceDAO.addDataImportPollutantToEmissionsReport(scDataImportPollutant);
			}

			serviceDAO.removeEmissionsReportFees(report.getId());
			for (Fee fee : report.getFees()) {
				if (fee.getFeeId() == null) {
					fee = serviceDAO.createFee(fee);
					trans.complete();
				} else {
					serviceDAO.modifyFee(fee);
					trans.complete();
				}

				serviceDAO.addFeeToEmissionsReport(report.getId(),
						fee.getFeeId());
			}
			/*
			serviceDAO.removeEmissionsReportExemptions(report.getId());

			for (StringContainer exemption : report.getExemptions()) {
				serviceDAO.addExemptionToEmissionsReport(report.getId(),
						exemption.getStr());
			}

			serviceDAO.removeEmissionsReportTvClassifications(report.getId());

			for (StringContainer tvClass : report.getTvClassifications()) {
				serviceDAO.addTvClassificationToEmissionsReport(report.getId(),
						tvClass.getStr());
			}
			*/
			
			// update Facility Selection Criteria data
			// Permit Class (Facility Class)
			serviceDAO.deletePermitClassXrefs(report.getId());
			serviceDAO.createPermitClassXrefs(report.getId(),
					report.getPermitClassCds());
					
			// Facility Type
			serviceDAO.deleteFacilityTypeXrefs(report.getId());
			serviceDAO.createFacilityTypeXrefs(report.getId(),
					report.getFacilityTypeCds());

			ret = serviceDAO.modifyEmissionsReport(report);

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}
	
	/**
	 * @param report
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean removeSCEmissionsReport(SCEmissionsReport report)
			throws DAOException {
		boolean ret = false;
		Transaction trans = TransactionFactory.createTransaction();
		ServiceCatalogDAO serviceDAO = serviceCatalogDAO(trans);

		try {
			serviceDAO.removeEmissionsReportPollutants(report.getId());

			serviceDAO.removeEmissionsReportNonChargePollutants(report.getId());
			
			serviceDAO.removeEmissionsReportDataImportPollutants(report.getId());
			
			// First, remove rows from sc_report_fee_xref table.
			serviceDAO.removeEmissionsReportFees(report.getId());
			
			// Second, remove rows from sc_fee table.
			serviceDAO.removeFee(report.getFeeFirstHalf().getFeeId());
			serviceDAO.removeFee(report.getFeeSecondHalf().getFeeId());

			// remove Facility Selection Criteria data
			// Permit Class (Facility Class)
			serviceDAO.deletePermitClassXrefs(report.getId());

			// Facility Type
			serviceDAO.deleteFacilityTypeXrefs(report.getId());

			serviceDAO.removeEmissionsReport(report.getId());

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
			ret = true;
		}

		return ret;
	}

	/**
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrievePollutants() throws DAOException {
		return infrastructureDAO().retrieveSimpleDefs("cm_pollutant_def",
				"pollutant_cd", "pollutant_dsc", "deprecated", null);
	}

	/**
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleDef[] retrieveReportPollutants() throws DAOException {
		return serviceCatalogDAO().retrieveReportPollutants();
	}

	/**
	 * @param name
	 * @param jobClass
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void createSchedulerJob(JobDetail job) throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((job != null) && (job.getKey().getName() != null)
				&& (job.getJobClass() != null)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");
			} catch (UnableToStartException utse) {
				logger.error(utse);
				throw new DAOException(utse.getMessage(), utse);
			}

//			if (job instanceof JobDetailImpl) {
			//TODO perform the cast unchecked; curious to see any other impls
			//used here
				((JobDetailImpl)job).setDurability(true);
//			}

			scheduler.addJob(job);
		}

		return;
	}

	/**
	 * @param dateToRun
	 * @param job
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void scheduleOneTimeTrigger(String triggerName, Date dateToRun,
			HashMap<String, String> dataMap, String jobName)
			throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((triggerName != null) && (jobName != null) && (dateToRun != null)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");

				scheduler.scheduleOneTimeTrigger(triggerName, dateToRun,
						dataMap, jobName);
			} catch (SchedulerException se) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, se);
				throw new DAOException(se.getMessage(), se);
			} catch (UnableToStartException utse) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, utse);
				throw new DAOException(utse.getMessage(), utse);
			}
		}

		return;
	}

	/**
	 * @param triggerName
	 * @param hour
	 * @param minute
	 * @param dataMap
	 * @param jobName
	 * @throws SchedulerException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void scheduleDailyTrigger(String triggerName, Integer hour,
			Integer minute, HashMap<String, String> dataMap, String jobName)
			throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((triggerName != null) && (jobName != null) && (hour != null)
				&& (minute != null)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");

				scheduler.scheduleDailyTrigger(triggerName, hour, minute,
						dataMap, jobName);
			} catch (SchedulerException se) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, se);
				throw new DAOException(se.getMessage(), se);
			} catch (UnableToStartException utse) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, utse);
				throw new DAOException(utse.getMessage(), utse);
			}
		}

		return;
	}

	/**
	 * @param triggerName
	 * @param dayOfWeek
	 * @param hour
	 * @param minute
	 * @param dataMap
	 * @param jobName
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void scheduleWeeklyTrigger(String triggerName, Integer dayOfWeek,
			Integer hour, Integer minute, HashMap<String, String> dataMap,
			String jobName) throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((triggerName != null) && (jobName != null)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");

				scheduler.scheduleWeeklyTrigger(triggerName, dayOfWeek, hour,
						minute, dataMap, jobName);
			} catch (SchedulerException se) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, se);
				throw new DAOException(se.getMessage(), se);
			} catch (UnableToStartException utse) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, utse);
				throw new DAOException(utse.getMessage(), utse);
			}
		}

		return;
	}

	/**
	 * @param triggerName
	 * @param dayOfMonth
	 * @param hour
	 * @param minute
	 * @param dataMap
	 * @param jobName
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void scheduleMonthlyTrigger(String triggerName, Integer dayOfMonth,
			Integer hour, Integer minute, HashMap<String, String> dataMap,
			String jobName) throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((triggerName != null) && (jobName != null)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");

				scheduler.scheduleMonthlyTrigger(triggerName, dayOfMonth, hour,
						minute, dataMap, jobName);
			} catch (SchedulerException se) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, se);
				throw new DAOException(se.getMessage(), se);
			} catch (UnableToStartException utse) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, utse);
				throw new DAOException(utse.getMessage(), utse);
			}
		}

		return;
	}

	/**
	 * @param triggerName
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param hour
	 * @param minute
	 * @param dataMap
	 * @param jobName
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void scheduleYearlyTrigger(String triggerName, Integer monthOfYear,
			Integer dayOfMonth, Integer hour, Integer minute,
			HashMap<String, String> dataMap, String jobName)
			throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((triggerName != null) && (jobName != null)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");

				scheduler.scheduleYearlyTrigger(triggerName, monthOfYear,
						dayOfMonth, hour, minute, dataMap, jobName);
			} catch (SchedulerException se) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, se);
				throw new DAOException(se.getMessage(), se);
			} catch (UnableToStartException utse) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, utse);
				throw new DAOException(utse.getMessage(), utse);
			}
		}

		return;
	}

	/**
	 * @param trigger
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void createSchedulerJobTrigger(Trigger trigger) throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((trigger != null) && (trigger.getKey().getName() != null)
				&& (trigger.getJobKey().getName() != null)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");

				Trigger tempTrigger = scheduler.getScheduler().getTrigger(
						trigger.getKey());

				if (tempTrigger != null) {
					scheduler.getScheduler().rescheduleJob(trigger.getKey(),
							trigger);
				} else {
					scheduler.getScheduler().scheduleJob(trigger);
				}
			} catch (SchedulerException se) {
				logger.error(
						"Exception for triggerName " + trigger.getJobKey().getName(), se);
				throw new DAOException(se.getMessage(), se);
			} catch (UnableToStartException utse) {
				logger.error(
						"Exception for triggerName " + trigger.getJobKey().getName(),
						utse);
				throw new DAOException(utse.getMessage(), utse);
			}
		}
	}

	/**
	 * @param jobName
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void removeSchedulerJob(String jobName) throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((jobName != null) && (jobName.compareTo("") != 0)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");
			} catch (UnableToStartException utse) {
				logger.error("Exception: jobname " + jobName, utse);
				throw new DAOException(utse.getMessage() + ", jobname "
						+ jobName, utse);
			}

			scheduler.removeJob(jobName);
		}
	}

	/**
	 * @param triggerName
	 * @param jobName
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void removeScheduledTrigger(String triggerName, String jobName)
			throws DAOException {
		Stars2Scheduler scheduler = null;

		if ((jobName != null) && (jobName.compareTo("") != 0)) {
			try {
				scheduler = (Stars2Scheduler) CompMgr
						.newInstance("app.Scheduler");
			} catch (UnableToStartException utse) {
				logger.error("Exception for triggerName " + triggerName
						+ " and jobName " + jobName, utse);
				throw new DAOException("Exception for triggerName "
						+ triggerName + " and jobName " + jobName, utse);
			}

			scheduler.removeTrigger(triggerName, jobName);
		}

		return;
	}

	/**
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SchedulerJob[] retrieveSchedulerJobs() throws DAOException {
		SchedulerJob[] ret = null;
		Stars2Scheduler scheduler = null;
		ArrayList<SchedulerJob> jobs = new ArrayList<SchedulerJob>();

		try {
			scheduler = (Stars2Scheduler) CompMgr.newInstance("app.Scheduler");

			if (scheduler != null) {
				JobDetail[] tempJobs = scheduler.getJobs();

				for (JobDetail job : tempJobs) {
					SchedulerJob newJob = new SchedulerJob();

					newJob.setJob(job);
					newJob.setTriggers(scheduler.getTriggers(job.getKey().getName()));

					jobs.add(newJob);
				}
				ret = jobs.toArray(new SchedulerJob[0]);
			} else {
				logger.error("Scheduler not running, please start");
			}
		} catch (UnableToStartException utse) {
			logger.error(utse);
			throw new DAOException(utse.getMessage(), utse);
		} catch (SchedulerException se) {
			logger.error(se);
			throw new DAOException(se.getMessage(), se);
		}

		return ret;
	}

	/**
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean isSchedulerRunning() throws DAOException {
		boolean ret = false;
		try {
			Stars2Scheduler scheduler = (Stars2Scheduler) CompMgr
					.newInstance("app.Scheduler");

			if (scheduler != null) {
				ret = true;
			}
		} catch (UnableToStartException utse) {
			logger.error(utse);
			throw new DAOException(utse.getMessage(), utse);
		}

		return ret;
	}

	/**
	 * @param searchObj
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public InvoiceList[] searchInvoices(User user, InvoiceList invSearchObj,
			boolean revenuesFirst) throws DAOException {
		InvoiceList ret[] = new InvoiceList[0];

		if (user != null) {
			try {
				InvoiceList[] starsResults = null;
				// RevenueSummary[] revenueList = null;
				// RevenueSearchObject revenueSearchObj = null;
				RevenuesService revenueService = null;
				revenueService = getRevenueService();

				RevenueSortObject revenueSortObj = new RevenueSortObject();
				revenueSortObj.setByID();
				revenueSortObj.setByType();

				Range<Money> balanceDue = new Range<Money>();
				DateRange effectiveDate = new DateRange();
				// OID[] revenueIdList = null;
				HashSet<OID> rIds = new HashSet<OID>();
				HashSet<Integer> unqRevenueIds = new HashSet<Integer>();
				RevenueType[] revenueType = null;

				if (invSearchObj.getRevenueTypeCd() != null) {
					revenueType = new RevenueType[1];
					revenueType[0] = new RevenueType(
							invSearchObj.getRevenueTypeCd());
				}

				starsResults = invoiceDAO().searchInvoices(invSearchObj);
				if (trace)
					logger.debug(" Stars2 Results len:"
							+ starsResults.length);
				if (revenueService == null) {
					// used for testing but can be left in place because value
					// will not be null
					// unless change made for testing in getRevenueService()
					logger.error("Revenue service not found, returning stars2 results");
					ret = starsResults;
					return ret;
				}

				if (invSearchObj.getInvoiceStateCd() != null
						&& invSearchObj.getInvoiceStateCd().equals(
								InvoiceState.READY_TO_INVOICE)) {
					return starsResults;
				} else if (!revenuesFirst) {
					if (trace)
						logger.error("!revenuesFirst");
					for (int i = 0; i < starsResults.length; i++) {
						if (starsResults[i].getRevenueId() != null) {
							// Only unique revenueIds will be sent to revenues.
							// rIds.add(new
							// OID(starsResults[i].getRevenueId()));
							unqRevenueIds.add(starsResults[i].getRevenueId());
						}
					}

					Iterator<Integer> iter = unqRevenueIds.iterator();
					while (iter.hasNext()) {
						Integer revId = iter.next();
						if (revId != null) {
							OID o = new OID(revId);
							rIds.add(o);
						}
					}

					if (rIds.size() > 20000) {
						// Revenues cannot return more than 20000 revenueSummary
						// records,
						// instead of revenues throwing a exception stars2 will
						// return without searching revenues.
						ret = new InvoiceList[20001];
						return ret;
					} else if (rIds.size() == 0) {
						ret = new InvoiceList[0];
						if (starsResults.length > 0) {
							// if searched by 'Ready to Invoice', no need to
							// search Revenues but return the results.
							// check is already done, this is unreachable
							// statement.
							ret = starsResults;
						}
						return ret;
					} else {
						// TODO ? DENNIS
						// TODO ? Either don't do all the work to get
						// revenueIdList in those
						// TODO ? cases where it won't be used OR see if it can
						// be used in all
						// TODO ? cases. Maybe a test to see if too long to
						// use????
						// revenueIdList = rIds.toArray(new OID[0]);

					}
				}

				if (invSearchObj.getBeginDt() != null) {
					Calendar calS = Calendar.getInstance();
					calS.setTime(invSearchObj.getBeginDt());
					effectiveDate.setStart(calS);
				}
				if (invSearchObj.getEndDt() != null) {
					Calendar calE = Calendar.getInstance();
					calE.setTime(invSearchObj.getEndDt());
					effectiveDate.setEnd(calE);
				}

				if (invSearchObj.getRevenueStateCd() != null
						&& invSearchObj.getRevenueStateCd().equals(
								RevenueState.PD)) {
					if (trace)
						logger.error("revenue state code is PD");

					// When end is set to zero, Revenues won't take negative
					// values, so the start and end values will be zero.
					// effectiveDate is to restrict the no of records returned.
					balanceDue.setEnd(new Money(0));

					// revenueSearchObj = new RevenueSearchObject(null,
					// revenueType, null, null, null, null, effectiveDate,
					// null, balanceDue, null, null, null);

				} else if (invSearchObj.getRevenueStateCd() != null
						&& invSearchObj.getRevenueStateCd().equals(
								RevenueState.AGO)) {
					if (trace)
						logger.error("revenue state code is AGO");
					// setting the last parameter to true, returns only
					// Certified to AGO invoices.
					// revenueSearchObj = new RevenueSearchObject(null,
					// revenueType, null, null, null, null, effectiveDate,
					// null, null, null, CertificationType.CERTIFIED, null);

				} else if (invSearchObj.getRevenueStateCd() != null
						&& invSearchObj.getRevenueStateCd().equals(
								RevenueState.AGO_POTENTIAL)) {
					if (trace)
						logger.trace("revenue state code is AGO");
					// setting the last parameter to true, returns only
					// Certified to AGO invoices.
					// revenueSearchObj = new RevenueSearchObject(null,
					// revenueType, null, null, null, null, effectiveDate,
					// null, null, null, CertificationType.POTENTIAL, null);

				} else if (invSearchObj.getRevenueStateCd() != null
						&& (invSearchObj.getRevenueStateCd().equals(
								RevenueSearchState.AGO_POTENTIAL_PAID) || invSearchObj
								.getRevenueStateCd()
								.equals(RevenueSearchState.AGO_POTENTIAL_UNPAID))) {
					if (invSearchObj.getRevenueStateCd().equals(
							RevenueSearchState.AGO_POTENTIAL_PAID)) {
						balanceDue.setEnd(new Money(0));
						if (trace)
							logger.trace("revenue state code is Potential-AGO-PAID");
					} else {
						balanceDue.setStart(new Money(0.1));
						if (trace)
							logger.trace("revenue state code is Potential-AGO-UNPAID");
					}
					// setting the last parameter to true, returns only
					// Certified to AGO invoices.
					// revenueSearchObj = new RevenueSearchObject(null,
					// revenueType, null, null, null, null, effectiveDate,
					// null, balanceDue, null,
					// CertificationType.POTENTIAL, null);

				} else if (invSearchObj.getRevenueStateCd() != null
						&& invSearchObj.getRevenueStateCd().equals(
								RevenueState.NP)) {
					if (trace)
						logger.error("revenue state code is NP");
					// per ethomas - cents need to show up in search and dapc
					// will make an adjustment to write it off, since cents
					// balance is not sent for collection.
					balanceDue.setStart(new Money(0.1));

					// revenueSearchObj = new RevenueSearchObject(null,
					// revenueType, null, null, null, null, effectiveDate,
					// null, balanceDue, null, null, null);

				} else if (invSearchObj.getBeginDt() != null
						|| invSearchObj.getEndDt() != null) {
					if (trace)
						logger.error("search with effective date");
					// revenueSearchObj = new RevenueSearchObject(null,
					// revenueType, null, null, null, null, effectiveDate,
					// null, null, null, null, null);
				} else {
					if (trace)
						logger.error("search with NO effective date");
					// revenueSearchObj = new RevenueSearchObject(revenueIdList,
					// revenueType, null, null, null, null, null, null,
					// null, null, null, null);
				}

				logger.error("logic not implemented.");
				// try {
				// revenueList = revenueService.retrieveDAPCRevenueList(user,
				// revenueSearchObj, revenueSortObj);
				// if(trace) logger.error("" + revenueList.length +
				// " items in revenueList");
				//
				// } catch (EPAException epaoe) {
				// logger.error("Cannot retrieve more than 20000 revenues at once "
				// + epaoe.getMessage(), epaoe);
				// throw new
				// DAOException("Cannot retrieve more than 20000 revenues at once, optimize your search criteria ",
				// epaoe);
				// }

				// TODO remove missingRevenueIds once problem has been figured
				// out.
				HashSet<Integer> missingRevenueIds = new HashSet<Integer>();
				if (starsResults.length > 0) {// && revenueList.length > 0
					InvoiceList row;
					ArrayList<InvoiceList> mRow = new ArrayList<InvoiceList>();

					if (trace) {
						// limit the amount generated.
						StringBuffer sb = new StringBuffer(1000);
						sb.append("INFO ONLY for Testing: ");
						if (starsResults.length < 100) {
							sb.append("RevenueIds from starsResults: ");
							for (int i = 0; i < starsResults.length; i++) {
								sb.append(starsResults[i].getRevenueId() + ":");
							}
						}
						sb.append("  RevenueIds from Revenues:");
						// for (int j = 0; j < revenueList.length; j++) {
						// sb.append(revenueList[j].getOID().toString()
						// + ";"
						// + Integer.parseInt(revenueList[j].getOID()
						// .toString()) + ":");
						// }
						logger.error(sb.toString());
					}

					for (int i = 0; i < starsResults.length; i++) {
						if (starsResults[i].getRevenueId() != null) {
							// boolean found = false;
							missingRevenueIds.add(starsResults[i]
									.getRevenueId()); // SUGGESTION: why not use
														// a
														// flag and add only if
														// actually missing?
														// for (int j = 0; j <
														// revenueList.length;
														// j++) {
							// if (starsResults[i].getRevenueId() == (Integer
							// .parseInt(revenueList[j].getOID()
							// .toString()))) {
							// missingRevenueIds.remove(starsResults[i]
							// .getRevenueId());
							// row = new InvoiceList();
							// row = starsResults[i];
							// row.setOriginalAmount(revenueList[j]
							// .getOriginalAmount().getAsDouble());
							// row.setCurrentAmount(revenueList[j]
							// .getCurrentAmount().getAsDouble());
							// row.setCreationDate(new Timestamp(
							// revenueList[j].getEffectiveDate()
							// .getTimeInMillis()));
							// row.setDueDate(new Timestamp(revenueList[j]
							// .getDueDate().getTimeInMillis()));
							//
							// if (revenueList[j].getBalanceDue()
							// .getAsDouble() < .1d) {
							// row.setRevenueStateDesc(RevenueSearchState
							// .getData().getItems()
							// .getItemDesc(RevenueState.PD));
							// } else {
							// row.setRevenueStateDesc(RevenueSearchState
							// .getData().getItems()
							// .getItemDesc(RevenueState.NP));
							// }
							// if (revenueList[j].isCertified()) {
							// row.setRevenueStateDesc2(RevenueSearchState
							// .getData().getItems()
							// .getItemDesc(RevenueState.AGO));
							// } else if (revenueList[j].isPotential()) {
							// row.setRevenueStateDesc2(RevenueSearchState
							// .getData()
							// .getItems()
							// .getItemDesc(
							// RevenueState.AGO_POTENTIAL));
							// } else {
							// row.setRevenueStateDesc2(null);
							// }
							//
							// mRow.add(row);
							// found = true;
							// break;
							// }
							// }
							// Not found only means that Stars2 found the
							// invoice (with incomplete search criteria)
							// Revenues finds invoices (with incomplete search
							// criteria).
							// It is the intersection that are the correct
							// invoices.
							// if (!found) {
							// logger.debug(" revenue id " +
							// starsResults[i].getRevenueId() +
							// " not found for "
							// + starsResults[i].getDoLaaCd() + ", " +
							// starsResults[i].getFacilityId());
							// }
						} else {
							// logger.debug(" no revenue id found for: "
							// + starsResults[i].getDoLaaCd() + ", "
							// + starsResults[i].getFacilityId());
							if (!revenuesFirst) {// Add not posted records only
													// when not searched by PD,
													// NP, AGO revenue states.
								row = new InvoiceList();
								row = starsResults[i];
								mRow.add(row);
							}
						}
					}
					// for (Integer revenueId : missingRevenueIds) {
					// logger.error("Missing revenue id: " + revenueId);
					// }
					ret = mRow.toArray(new InvoiceList[0]);
				}
			} catch (RemoteException re) {
				logger.error(re.getMessage(), re);
				throw new DAOException(re.getMessage(), re);
			} catch (Exception epaex) {
				logger.error(epaex.getMessage(), epaex);
				throw new DAOException(epaex.getMessage(), epaex);
			}

		} else {
			ret = invoiceDAO().searchInvoices(invSearchObj);
		}
		return ret;
	}

	/**
	 * @param invoiceId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Invoice retrieveInvoiceRecord(int invoiceId, boolean isAtomicAction)
			throws DAOException {

		Invoice ret = null;
		ret = invoiceDAO().retrieveInvoice(invoiceId);
		return ret;
	}

	/**
	 * @param invoiceId
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Invoice retrieveInvoice(User user, int invoiceId,
			boolean isAtomicAction) throws DAOException {

		Invoice ret = null;
		ret = invoiceDAO().retrieveInvoice(invoiceId);
		if (ret == null) {
			throw new DAOException("failed to retrieveInvoice(), invoice id = "
					+ invoiceId);
		}

		if (ret.getEmissionsRptId() != null) {
			ret.setPrevInvoice(retrievePreviousInvoice(ret));
			if (ret.getPrevInvoice() != null) {
				ret.setCompInvoice(compareInvoice(ret, ret.getPrevInvoice()));
				if (ret.getInvoiceStateCd().equalsIgnoreCase(
						InvoiceState.READY_TO_INVOICE)) {
					ret.setAdjustedInvoice(true);
				}

				if (ret.getPrevInvoice().getRevenueId() == null
						&& !ret.getPrevInvoice().getInvoiceStateCd()
								.equals(InvoiceState.CANCELLED)) {
					ret.setAllowOperations(false);
					String breakStr = "";
					if (ret.getPrevInvoiceFailureMsg() != null) {
						breakStr = ret.getPrevInvoiceFailureMsg() + "<br><br>";
					}
					ret.setPrevInvoiceFailureMsg(breakStr
							+ "Invoice has previous Invoice- "
							+ ret.getPrevInvoice().getInvoiceId()
							+ " which is not processed yet.");
				}
			}
		}

		for (InvoiceNote note : invoiceDAO().retrieveInvoiceNotes(invoiceId)) {
			ret.addNote(note);
		}

		if (ret.getRevenueId() != null && !isAtomicAction) { // did not test for
																// user != null
			ret.setRevenue(retrieveRevenue(user, ret.getRevenueId()));
			ret.setRevenueImmediate(null);
			if (InvoiceState.hasBeenPosted(ret.getInvoiceStateCd())) {
				ret.setRevenueImmediate(ret.getRevenue());
			}
			ret.checkPostCompleted();
		}

		return ret;
	}

	/**
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void setInvoicePaymentLateInfo(int invoiceId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			Invoice invoice = retrieveInvoiceRecord(invoiceId, false);
			invoice.setInvoiceStateCd(InvoiceState.LATE_LETTER_INVOICE);
			modifyInvoiceRecord(invoice, trans);
		} catch (DAOException de) {
			logger.error("Problem updating Payment Late Invoice for "
					+ invoiceId, de);
			cancelTransaction(trans, de);
			throw de;
		} catch (Exception ex) {
			String error = "Problem updating Payment Late Invoice for "
					+ invoiceId + ". " + ex.getMessage();
			DAOException d = new DAOException(error, ex);
			logger.error(error);
			cancelTransaction(trans, d);
			throw d;
		}
	}

	/**
	 * @param facility
	 * @param permit
	 * @param invoice
	 * @param templateDoc
	 * 
	 * @return PermitDocument
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ContactUtil locateInvoiceContact(Invoice invoice,
			EmissionsReport tvSmtvRpt, NtvReport ntvReport) throws DAOException {

		ContactUtil activeConU = null;
		FacilityDAO facilityDAO = null;
		facilityDAO = facilityDAO((String) null);
		Facility facility = new Facility();
		facility.setFacilityId(invoice.getFacilityId());
		// need facility shutdown date
		facilityDAO.retrieveFacilityTable(facility.getFacilityId());
		// need facility contacts
		facility.setAllContacts(facilityDAO.retrieveFacilityContacts(facility
				.getFacilityId()));
		Calendar cal = Calendar.getInstance();
		Timestamp anchor = null;
		if (ntvReport != null) {
			ntvReport.determineReportable(facility);
			anchor = ntvReport.getAnchor();
			if (ntvReport.getPrimary().getTransferDate() != null) {
				if (ntvReport.getPrimary().isNewOwner()) { // is new owner
					anchor = ntvReport.getPrimary().getTransferDate();
				} else {
					// need one day earlier
					Calendar calA = Calendar.getInstance();
					Date date = new Date(ntvReport.getPrimary()
							.getTransferDate().getTime());
					calA.setTime(date);
					calA.add(Calendar.HOUR_OF_DAY, -24);
					calA.set(Calendar.HOUR_OF_DAY, 23);
					calA.set(Calendar.MINUTE, 59);
					calA.set(Calendar.SECOND, 59);
					calA.set(Calendar.MILLISECOND, 0);
					anchor = new Timestamp(calA.getTimeInMillis());
				}
			}
		} else if (tvSmtvRpt != null) {
			cal.set(tvSmtvRpt.getReportYear(), Calendar.DECEMBER, 31, 23, 59,
					59);
			cal.set(Calendar.MILLISECOND, 999);
			anchor = new Timestamp(cal.getTimeInMillis());
			anchor.setNanos(0);
		} else {
			// this is a permit
			invoice.setContact(null);
			String errMsg = null;
			activeConU = facility.getActiveContact(ContactTypeDef.BILL, null);
			if (activeConU != null) {
				invoice.setContact(activeConU.getContact());
			} else {
				invoice.setHaveBillingAddress(false);
				errMsg = "No current billing contact.";
				if (invoice.getPrevInvoiceFailureMsg() != null) {
					invoice.setPrevInvoiceFailureMsg(invoice
							.getPrevInvoiceFailureMsg() + "<br><br>" + errMsg);
				} else {
					invoice.setPrevInvoiceFailureMsg(errMsg);
				}
			}
			return activeConU;
		}
		// continue with invoice for emission reporting
		invoice.setContact(null);
		String errMsg = null;
		// Is there an owner at the end of the reporting period?
		/*ContactUtil ownerAt = facility.getActiveContact(ContactTypeDef.OWNR,
				anchor);
		if (ownerAt != null) {
			Timestamp contactRefDate = facility.latestOwnerRefDate(anchor);
			activeConU = facility.getActiveContact(ContactTypeDef.BILL,
					contactRefDate);
			if (activeConU != null) {
				invoice.setContact(activeConU.getContact());
			} else {
				invoice.setHaveBillingAddress(false);
				if (contactRefDate != null) {
					errMsg = "No billing contact active on date "
							+ ContactUtil.datePrtFormat(contactRefDate)
							+ " (which is the last date this owner owned the facility).";
				} else {
					errMsg = "No current billing contact.";
				}
			}
		} else {*/
			// There is no owner
			invoice.setHaveBillingAddress(false);
			errMsg = "There is no owner on date "
					+ ContactUtil.datePrtFormat(anchor) + ".";
		//}
		if (errMsg != null) {
			if (invoice.getPrevInvoiceFailureMsg() != null) {
				invoice.setPrevInvoiceFailureMsg(invoice
						.getPrevInvoiceFailureMsg() + "<br><br>" + errMsg);
			} else {
				invoice.setPrevInvoiceFailureMsg(errMsg);
			}
		}
		return activeConU;
	}

	/**
	 * Invoice creation method for use by migration code. Does not require the
	 * billing contact for the facility to be set.
	 * 
	 * @param invoice
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Invoice createLegacyInvoice(Invoice invoice) throws DAOException {
		Invoice result = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			result = createLegacyInvoice(invoice, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return result;
	}

	/**
	 * Invoice creation method for use by migration code. Does not require the
	 * billing contact for the facility to be set.
	 * 
	 * @param invoice
	 * @param trans
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Invoice createLegacyInvoice(Invoice invoice, Transaction trans)
			throws DAOException {
		Invoice newInvoice = null;

//		InfrastructureHelper infraHelper = new InfrastructureHelper();
		newInvoice = infrastructureHelper.createInvoice(invoice, trans);

		return newInvoice;
	}

	/**
	 * @param facility
	 * @param permit
	 * @param invoice
	 * @param templateDoc
	 * 
	 * @return PermitDocument
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitDocument generatePermitInvDocument(Facility facility,
			Permit permit, Invoice inv, String corrTypeCD,
			TemplateDocument templateDoc) throws DAOException,
			DocumentGenerationException {
		PermitDocument permitDoc = new PermitDocument();
		CorrespondenceDAO cDao = correspondenceDAO();

		try {
			permitDoc.setFacilityID(inv.getFacilityId());
			permitDoc.setTemporary(false);
			permitDoc.setExtension(DocumentUtil.getFileExtension(templateDoc
					.getPath()));
			permitDoc.setLastModifiedBy(1);
			permitDoc.setLastModifiedTS(new Timestamp(System
					.currentTimeMillis()));
			permitDoc.setUploadDate(permitDoc.getLastModifiedTS());
			permitDoc.setDescription("Invoice Document");
			permitDoc.setPermitId(inv.getPermitId());
			permitDoc = (PermitDocument) documentDAO()
					.createDocument(permitDoc);
			permitDoc.setPermitDocTypeCD(PermitDocTypeDef.INVOICE);
			permitDoc.setIssuanceStageFlag(PermitIssuanceTypeDef.Final);
			permitDAO().createPermitDocument(permitDoc);

			DocumentGenerationBean invoiceBean = new DocumentGenerationBean();
			invoiceBean.setFacility(facility);
			invoiceBean.setPermit(permit);
			invoiceBean.setInvoice(facility, inv);
			/*DocumentUtil.generateDocument(templateDoc.getPath(), invoiceBean,
					permitDoc.getPath());*/
			DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), invoiceBean, permitDoc.getPath());

			if (corrTypeCD != null) {
				Correspondence correspondence = new Correspondence();
				correspondence.setFacilityID(inv.getFacilityId());
				correspondence.setCorrespondenceTypeCode(corrTypeCD);
				correspondence.setDateGenerated(new Timestamp(Calendar
						.getInstance().getTimeInMillis()));
				correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
				correspondence.setDocument(permitDoc);
				correspondence
						.setAdditionalInfo("Invoice Document for Invoice Id("
								+ inv.getInvoiceId() + ")" + " Revenue Id("
								+ inv.getRevenueId() + ")" + " Permit Id("
								+ permit.getPermitNumber() + ")");
				cDao.createCorrespondence(correspondence);
			}

		} catch (IOException ioe) {
			logger.error("Exception for invoice " + inv.getInvoiceId(), ioe);
			throw new DAOException(ioe.getMessage(), ioe);
		} catch (DocumentGenerationException dge) {
			logger.error("Exception for invoice " + inv.getInvoiceId(), dge);
			throw dge;
		} catch (Exception ex) {
			logger.error("Exception for invoice " + inv.getInvoiceId(), ex);
			throw new DAOException(ex.getMessage(), ex);
		}

		return permitDoc;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	private EmissionsDocument generateReportInvDocument(Facility facility,
			EmissionsReport report, Invoice inv, String corrTypeCD,
			TemplateDocument templateDoc, Transaction trans)
			throws DAOException, DocumentGenerationException {

		EmissionsDocument reportDoc = new EmissionsDocument();
		DocumentDAO documentDAO = documentDAO(trans);
		CorrespondenceDAO cDao = correspondenceDAO(trans);

		reportDoc.setFacilityID(inv.getFacilityId());
		reportDoc.setTemporary(false);
		reportDoc.setExtension(DocumentUtil.getFileExtension(templateDoc
				.getPath()));
		reportDoc.setLastModifiedBy(1);
		reportDoc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
		reportDoc.setUploadDate(reportDoc.getLastModifiedTS());
		reportDoc.setDescription("Invoice Document");
		reportDoc.setEmissionsRptId(inv.getEmissionsRptId());

		try {
			DocumentGenerationBean invoiceBean = new DocumentGenerationBean();
			invoiceBean.setFacility(facility);
			if (report != null) {// This check is done coz for some migrated
									// invoices there is no Emissions inventory.
				invoiceBean.setEmissionsReport(report);
			}
			invoiceBean.setInvoice(facility, inv);

			reportDoc = (EmissionsDocument) documentDAO
					.createDocument(reportDoc);
			/*DocumentUtil.generateDocument(templateDoc.getPath(), invoiceBean,
					reportDoc.getPath());*/
			DocumentUtil.generateAsposeDocument(templateDoc.getTemplateDocPath(), invoiceBean,reportDoc.getPath());
		} catch (IOException ioe) {
			logger.error("Exception for invoice " + inv.getInvoiceId(), ioe);
			throw new DAOException(ioe.getMessage(), ioe);
		} catch (DocumentGenerationException dge) {
			logger.error("Exception for invoice " + inv.getInvoiceId(), dge);
			throw dge;
		} catch (Exception ex) {
			logger.error("Exception for invoice " + inv.getInvoiceId(), ex);
			throw new DAOException(ex.getMessage(), ex);
		}

		if (corrTypeCD != null) {
			Correspondence correspondence = new Correspondence();
			correspondence.setFacilityID(inv.getFacilityId());
			correspondence.setCorrespondenceTypeCode(corrTypeCD);
			correspondence.setDateGenerated(new Timestamp(Calendar
					.getInstance().getTimeInMillis()));
			correspondence.setDirectionCd(CorrespondenceDirectionDef.OUTGOING);
			correspondence.setDocument(reportDoc);
			correspondence.setAdditionalInfo("Invoice Document for Invoice Id("
					+ inv.getInvoiceId() + ")" + " Revenue Id("
					+ inv.getRevenueId() + ")" + " Report Id("
					+ report.getEmissionsRptId() + ")");
			cDao.createCorrespondence(correspondence);
		}
		return reportDoc;
	}

	/**
	 * @param user
	 * @param processIDs
	 * 
	 * @returns ValidationMessage
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public List<ValidationMessage> prepareReportInvDocument(User user,
			List<Integer> pIDs) {
		TemplateDocument templateDoc;
		String templateTypeCD = null;
		FacilityService facilityBO = null;
		EmissionsReportService emissionsRptBO = null;

		ValidationMessage vm;
		List<ValidationMessage> retVal = new ArrayList<ValidationMessage>();
		List<Integer> failedPIDs = new ArrayList<Integer>();
		boolean exceptionHappened = false;
		String serviceEx = null;
		try {
			facilityBO = ServiceFactory.getInstance().getFacilityService();
			emissionsRptBO = ServiceFactory.getInstance()
					.getEmissionsReportService();

		} catch (ServiceFactoryException sfe) {
			logger.error("Exception :Cannot locate Service.", sfe);
			exceptionHappened = true;
			serviceEx = "Exception :Cannot locate Service." + sfe.getMessage();
		}
		for (Integer pID : pIDs) {
			if (exceptionHappened) {
				String msg;
				if (serviceEx != null) {
					msg = serviceEx;
					serviceEx = null;
				} else {
					msg = "Did not complete report " + pID
							+ " because of previous problem";
				}
				ValidationMessage vmsg = new ValidationMessage(
						"Emissions Inventory ", msg,
						ValidationMessage.Severity.ERROR);
				retVal.add(vmsg);
				failedPIDs.add(pID);
				continue;
			}
			try { // catch all errors to fail the rest of the pIDs
				EmissionsReport report = null;
				Invoice invoice = null;
				Facility facility = null;
				vm = null;

				if (pID != null) {// This check is necessary coz some migrated
									// invoices won't have Emissions Inventory.
					try {
						report = emissionsRptBO.retrieveEmissionsReport(pID,
								false);
					} catch (Exception ex) {
						logger.error(
								"Exception :Cannot retrieve report." + pID, ex);
						ValidationMessage vmsg = new ValidationMessage(
								"Emissions Inventory", "Cannot retrieve report "
										+ pID,
								ValidationMessage.Severity.ERROR, null);
						retVal.add(vmsg);
						failedPIDs.add(pID);
						continue;
					}
				}

				if (report != null) {
					invoice = retrieveInvoiceByReportID(user,
							report.getEmissionsRptId());
					try {
						facility = facilityBO
								.retrieveFacility(report.getFpId());
					} catch (RemoteException re) {
						String s = "Failed to retrieveFacility(), fpId="
								+ report.getFpId();
						logger.error(s, re);
						ValidationMessage vmsg = new ValidationMessage(
								"Emissions Inventory", s,
								ValidationMessage.Severity.ERROR, null);
						retVal.add(vmsg);
						failedPIDs.add(pID);
						continue;
					}
					if (invoice != null) {
						if (!invoice.getInvoiceStateCd().equals(
								InvoiceState.CANCELLED)
								&& !invoice.getInvoiceStateCd().equals(
										InvoiceState.READY_TO_INVOICE)) {
							String corrTypeCD = null;
							String reportCategory = RevenueTypeDef
									.getReportCategory(invoice
											.getRevenueTypeCd());

							if (reportCategory
									.equals(EmissionReportsRealDef.TV)) {
								templateTypeCD = TemplateDocTypeDef.TV_FEE_INVOICE;
								corrTypeCD = "20";

							} else if (reportCategory
									.equals(EmissionReportsRealDef.SMTV)) {
								templateTypeCD = TemplateDocTypeDef.SMTV_FEE_INVOICE;
								corrTypeCD = "14";

							} else if (reportCategory
									.equals(EmissionReportsRealDef.NTV)) {
								templateTypeCD = TemplateDocTypeDef.NTV_FEE_INVOICE;
								corrTypeCD = "11";
							}

							templateDoc = TemplateDocTypeDef
									.getTemplate(templateTypeCD);

							try {
								generateReportInvDocument(facility, report,
										invoice, corrTypeCD, templateDoc);

							} catch (DAOException ex) {
								ValidationMessage vmsg = new ValidationMessage(
										"Emissions Inventory",
										"Unable to generate Invoice Document: "
												+ ex.getMessage(),
										ValidationMessage.Severity.ERROR, null);
								retVal.add(vmsg);
								logger.error(
										"Exception :Unable to generate Invoice Document for invoice "
												+ invoice.getFacilityId(), ex);
								failedPIDs.add(pID);
								continue;
							} catch (DocumentGenerationException dge) {
								if (invoice != null
										&& invoice.getContact() == null) {
									ValidationMessage vmsg = new ValidationMessage(
											"Emissions Inventory",
											"No billing contact for invoice "
													+ invoice.getInvoiceId()
													+ ", Emissions Inventory- "
													+ pID + " for "
													+ facility.getName() + " #"
													+ facility.getFacilityId(),
											ValidationMessage.Severity.ERROR,
											null);
									retVal.add(vmsg);
									failedPIDs.add(pID);
									continue;
								} else {
									ValidationMessage vmsg = new ValidationMessage(
											"Emissions Inventory",
											"Unable to generate Invoice Document: "
													+ dge.getMessage(),
											ValidationMessage.Severity.ERROR,
											null);
									retVal.add(vmsg);
									logger.error(
											"Exception :Unable to generate Invoice Document for invoice "
													+ invoice.getFacilityId(),
											dge);
									failedPIDs.add(pID);
									continue;
								}
							}
						} else {
							String s = "";
							if (invoice.getInvoiceStateCd() != null) {
								DefData dd = InvoiceState.getData();
								if (dd != null) {
									DefSelectItems dsi = dd.getItems();
									if (dsi != null) {
										s = dsi.getItemDesc(invoice
												.getInvoiceStateCd());
									}
								}
							}
							String msg = "This invoice in state "
									+ s
									+ ". Invoice document is generated for only posted Invoices. Invoice Id- "
									+ invoice.getInvoiceId()
									+ ", Emissions Inventory- " + pID + " for "
									+ facility.getName() + " #"
									+ facility.getFacilityId();

							vm = new ValidationMessage("Emissions Inventory ",
									msg, ValidationMessage.Severity.INFO);
						}

					} else {
						// Are fees owed but no invoice exists
						boolean feeOwed = false;
						boolean feeNull = false;
							Integer feeId = report.getFeeId();
							if (feeId == null) {
								// should have a feeId
								feeNull = true;
							} else {
								Fee f = null;
								f = serviceCatalogDAO().retrieveFee(feeId);
								if (f.getLowRange() == null
										&& f.getHighRange() == null) {
									// price per ton
									if (report.getTotalEmissions() != null
											&& report.getTotalEmissions() > 0.f) {
										feeOwed = true;
									}
								} else if (f.getAmount() > 0d) {
									// Fee per range
									feeOwed = true;
								}
							}
						if (feeNull) {
							String msg = "No Fee Id found in Emissions Inventory- "
									+ pID
									+ " - "
									+ " for "
									+ facility.getName()
									+ " #"
									+ facility.getFacilityId();
							ValidationMessage vmsg = new ValidationMessage(
									"Emissions Inventory ", msg,
									ValidationMessage.Severity.ERROR);
							retVal.add(vmsg);
							failedPIDs.add(pID);
							continue;
						}
						if (feeOwed) {
							String msg = "No Invoice found for Emissions Inventory- "
									+ pID
									+ " - "
									+ " fee owed by "
									+ facility.getName()
									+ " #"
									+ facility.getFacilityId();
							ValidationMessage vmsg = new ValidationMessage(
									"Emissions Inventory ", msg,
									ValidationMessage.Severity.ERROR);
							retVal.add(vmsg);
							failedPIDs.add(pID);
							continue;
						} else {
							String msg = "No Invoice found for Emissions Inventory- "
									+ pID
									+ " - "
									+ " and no fee owed by "
									+ facility.getName()
									+ " #"
									+ facility.getFacilityId();
							ValidationMessage vmsg = new ValidationMessage(
									"Emissions Inventory ", msg,
									ValidationMessage.Severity.INFO);
							retVal.add(vmsg);
							continue;
						}
					}
				} else {
					String s = "Unable to read report " + pID;
					ValidationMessage vmsg = new ValidationMessage(
							"Emissions Inventory", s,
							ValidationMessage.Severity.ERROR, null);
					retVal.add(vmsg);
					logger.error(s);
					failedPIDs.add(pID);
					continue;
				}

				if (vm != null) {
					retVal.add(vm);
				}
			} catch (Exception e) {
				String msg = "Did not complete report " + pID
						+ " because got exception: " + e.getMessage();
				ValidationMessage vmsg = new ValidationMessage(
						"Emissions Inventory ", msg,
						ValidationMessage.Severity.ERROR);
				retVal.add(vmsg);
				failedPIDs.add(pID);
				exceptionHappened = true;
			}
		}

		for (Integer pID : failedPIDs) {
			pIDs.remove(pID);
		}
		return retVal;
	}

	/**
	 * @param facility
	 * @param report
	 * @param invoice
	 * @param templateDoc
	 * 
	 * @return EmissionsDocument
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public EmissionsDocument generateReportInvDocument(Facility facility,
			EmissionsReport report, Invoice inv, String corrTypeCD,
			TemplateDocument templateDoc) throws DAOException,
			DocumentGenerationException {
		EmissionsDocument eDoc = null;
		Transaction trans = null;
		try {
			trans = TransactionFactory.createTransaction();
			eDoc = generateReportInvDocument(facility, report, inv, corrTypeCD,
					templateDoc, trans);

			if (eDoc != null) {
				inv.setReportInvDocument(eDoc);
				invoiceDAO(trans).modifyInvoiceDocument(inv);
			}
			trans.complete();
		} catch (DAOException de) {
			if (eDoc != null && eDoc.getPath() != null) {
				try {
					DocumentUtil.removeDocument(eDoc.getPath());
				} catch (IOException ioe) {
					logger.warn("Exception deleting document " + eDoc.getPath()
							+ " for invoice " + inv.getInvoiceId());
				}
			}
			cancelTransaction(trans, de);
			throw de;
		} finally {
			closeTransaction(trans);
		}

		return eDoc;
	}

	/**
	 * @param user
	 * @param report
	 * @param invoice
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public MultiInvoiceList[] retrieveOtherInvoices(User user,
			EmissionsReport report, Invoice invoice) throws DAOException {
		Integer reportId = invoice.getEmissionsRptId();
		checkNull(report);
		Integer revenueId = invoice.getRevenueId();
		if (!InvoiceState.hasBeenPosted(invoice.getInvoiceStateCd())) {
			// Don't count on revenue Id unless it has been posted
			revenueId = null;
		}
		boolean isSingle = false;
		int singleYear = 0;
		Integer firstTwoYearRptId = null;
		boolean haveTwoYearRevenueId = false;
		MultiInvoiceList[] ret = null;
		List<MultiInvoiceList> invoiceLst = new ArrayList<MultiInvoiceList>();

		if (report.getCompanionReport() == null) {
			isSingle = true;
			singleYear = report.getReportYear();
		}
		try {
			EmissionsReportService emissionsRptBO = ServiceFactory
					.getInstance().getEmissionsReportService();

			while (true) {
				if (report.getReportModified() != null) {
					// Get previous invoice
					Invoice inv = invoiceDAO().retrieveInvoiceByReportID(
							report.getReportModified());
					if (inv != null) {
						// locate the Revenue id.
						if (revenueId == null
								&& InvoiceState.hasBeenPosted(inv
										.getInvoiceStateCd())) {
							revenueId = inv.getRevenueId();
						} else {
							if (inv.getRevenueId() != null
									&& InvoiceState.hasBeenPosted(inv
											.getInvoiceStateCd())) {
								if (!revenueId.equals(inv.getRevenueId())) {
									// Don't inlcude this and look further
									break;
								}
							}
						}
						if (firstTwoYearRptId != null
								&& inv.getRevenueId() != null) {
							haveTwoYearRevenueId = true;
						}

						MultiInvoiceList ml = new MultiInvoiceList();
						ml.setInvoiceId(inv.getInvoiceId());
						ml.setInvoiceStateCd(inv.getInvoiceStateCd());
						ml.setOrigAmount(inv.getOrigAmount());
						ml.setEmissionsRptId(inv.getEmissionsRptId());
						ml.setEmissionsRptCd(null);
						ml.setCreationDate(inv.getCreationDate());

						invoiceLst.add(ml);
					}

				} else {
					break;
				}
				// get previous report so we can then get its invoice.
				reportId = report.getReportModified();
				report = emissionsRptBO
						.retrieveEmissionsReport(reportId, false);
				checkNull(report);
				if (isSingle && firstTwoYearRptId == null
						&& report.getCompanionReport() != null) {
					// found first two year report, keep higher report id
					firstTwoYearRptId = report.getEmissionsRptId();
					if (0 < firstTwoYearRptId.compareTo(report
							.getCompanionReport())) {
						firstTwoYearRptId = report.getCompanionReport();
					}
				}
			}
			// Keep the first Revenue id found.
			invoice.setRevenueId(revenueId);
			// If we have not already gotten the Revenue object, get it now.
			if (revenueId != null && user != null
					&& invoice.getRevenue() == null) {
				invoice.setRevenue(retrieveRevenue(user, revenueId));
				invoice.setRevenueImmediate(null);
				if (InvoiceState.hasBeenPosted(invoice.getInvoiceStateCd())) {
					invoice.setRevenueImmediate(invoice.getRevenue());
				}
			}
			if (invoiceLst.size() > 0) {
				ret = invoiceLst.toArray(new MultiInvoiceList[0]);
			}

			if (haveTwoYearRevenueId) {
				// Determine whether there is another report branch.
				// Determine what year to look for.
				int missingYear;
				if (singleYear % 2 == 0) {
					missingYear = singleYear + 1;
				} else {
					missingYear = singleYear - 1;
				}
				// look for a report with this year.
				EmissionsReportSearch[] reports = null;
				try {
					EmissionsReportSearch searchObj = new EmissionsReportSearch();
					searchObj.setYear(missingYear);
					searchObj.setFacilityId(invoice.getFacilityId());
					searchObj.setUnlimitedResults(true);
					reports = emissionsRptBO.searchEmissionsReports(searchObj,
							false);
				} catch (RemoteException e) {
					String s = "Failed on conflictingReportForApproval() for emissionsRptId "
							+ report.getEmissionsRptId()
							+ ",  invoice Id ="
							+ invoice.getInvoiceId();
					logger.error(s, e);
					throw new DAOException(s, e);
				}
				if (reports != null && reports.length > 0) {
					StringBuffer b = new StringBuffer(100);
					if (invoice.getPrevInvoiceFailureMsg() != null) {
						b.append(invoice.getPrevInvoiceFailureMsg()
								+ "<br><br>");
					}
					b.append("Caution: report");
					if (reports.length > 1) {
						b.append("s");
					}
					for (int i = 0; i < reports.length - 1; i++) {
						b.append(", " + reports[i].getEmissionsRptId());
					}
					if (reports.length > 1) {
						b.append(" and");
					}
					b.append(" "
							+ reports[reports.length - 1].getEmissionsRptId()
									.toString());
					b.append(" for the year "
							+ missingYear
							+ " are earlier versions of this report for facility #"
							+ invoice.getFacilityId()
							+ ".  Consider removing the Revenue Id (by clicking \"Prepare for New Revenue Id\") from a Stars2 Invoice that starts one of the branches for it to use its own new Revenue Id.");
					invoice.setPrevInvoiceFailureMsg(b.toString());
				}
			}
		} catch (Exception ex) {
			String s = "Unable to retrieveOtherInvoices.  Invoice Id ="
					+ invoice.getInvoiceId() + ". ";
			logger.error(s, ex);
			throw new DAOException(s + ex.getMessage(), ex);
		}

		// ret = invoiceDAO.retrieveOtherInvoices(invoice.getRevenueId(),
		// invoice.getInvoiceId());

		return ret;
	}

	/**
	 * @param report
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean previousInvoiceExists(EmissionsReport report)
			throws DAOException {
		boolean ret = false;
		EmissionsReport report2 = report;
		try {
			EmissionsReportService emissionsRptBO = ServiceFactory
					.getInstance().getEmissionsReportService();
			while (true) {
				if (report2.getReportModified() != null) {
					// Get previous invoice
					Invoice inv = invoiceDAO().retrieveInvoiceByReportID(
							report2.getReportModified());
					if (inv != null) {
						// found an invoice
						ret = true;
						break;
					}
				} else {
					break;
				}
				// get previous report so we can then get its invoice.
				report2 = emissionsRptBO.retrieveEmissionsReport(
						report2.getReportModified(), false);
				checkNull(report2);
			}
		} catch (Exception ex) {
			String s = "previousInvoiceExists failed, reportId = "
					+ report.getEmissionsRptId() + "; reportId2 = "
					+ report2.getEmissionsRptId() + ". ";
			logger.error(s, ex);
			throw new DAOException(s + ex.getMessage(), ex);
		}
		return ret;
	}

	/**
	 * @param invoice
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Invoice retrievePreviousInvoice(Invoice invoice) throws DAOException {
		Invoice ret = null;
		Integer reportId = invoice.getEmissionsRptId();
		boolean firstOne = true;
		try {
			EmissionsReportService emissionsRptBO = ServiceFactory
					.getInstance().getEmissionsReportService();
			while (reportId > 0) {
				EmissionsReport report = emissionsRptBO
						.retrieveEmissionsReportRow(reportId, false);
				if (firstOne) {
					firstOne = false;
					// We need the report category in case the user wants to
					// reach the report from the Invoice Detail Page.
					invoice.setEmissionsRptCd(null);
				}
				if (report.getReportModified() != null) {
					Invoice inv = invoiceDAO().retrieveInvoiceByReportID(
							report.getReportModified());

					// If a invoice has previous invoice which is posted and has
					// a Revenue Id, then same Reveue Id will be maintained.
					if (inv != null
							&& !inv.getInvoiceStateCd().equals(
									InvoiceState.CANCELLED)) {
						ret = inv;
						reportId = 0;
					} else {
						reportId = report.getReportModified();
					}
				} else {
					reportId = 0;
				}
			}
		} catch (Exception de) {
			logger.error(de.getMessage(), de);
			throw new DAOException(
					"Unable to retrievePreviousInvoice for report " + reportId
							+ ". " + de.getMessage(), de);
		}
		return ret;
	}

	/**
	 * @param reportId
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Invoice retrieveInvoiceByReportID(Integer reportId)
			throws DAOException {
		return invoiceDAO().retrieveInvoiceByReportID(reportId);
	}

	/**
	 * @param reportId
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Invoice retrieveInvoiceByReportID(User user, Integer reportId)
			throws DAOException {
		Invoice ret = null;
		try {
			ret = invoiceDAO().retrieveInvoiceByReportID(reportId);

			if (ret != null) {
				if (ret.getEmissionsRptId() != null) {
					ret.setPrevInvoice(retrievePreviousInvoice(ret));

					// When there is a previous invoice, need to compare the
					// differences
					// between the current invoice and previous invoice.
					if (ret.getPrevInvoice() != null) {
						ret.setCompInvoice(compareInvoice(ret,
								ret.getPrevInvoice()));
						ret.setAdjustedInvoice(true);
						if (ret.getPrevInvoice().getRevenueId() == null
								&& !ret.getPrevInvoice().getInvoiceStateCd()
										.equals(InvoiceState.CANCELLED)) {
							ret.setAllowOperations(false);
							String breakStr = "";
							if (ret.getPrevInvoiceFailureMsg() != null) {
								breakStr = ret.getPrevInvoiceFailureMsg()
										+ "<br><br>";
							}
							ret.setPrevInvoiceFailureMsg(breakStr
									+ "Invoice has previous Invoice- "
									+ ret.getPrevInvoice().getInvoiceId()
									+ " which is not processed yet.");
						}
					}
				}

				if (user != null && ret.getRevenueId() != null) {
					ret.setRevenue(retrieveRevenue(user, ret.getRevenueId()));
					ret.setRevenueImmediate(null);
					if (InvoiceState.hasBeenPosted(ret.getInvoiceStateCd())) {
						ret.setRevenueImmediate(ret.getRevenue());
					}
					ret.checkPostCompleted();
				}

				for (InvoiceNote note : invoiceDAO().retrieveInvoiceNotes(
						ret.getInvoiceId())) {
					ret.addNote(note);
				}
			}
		} catch (Exception de) {
			logger.error("Unable to retrieveInvoiceByReportID " + reportId
					+ ". " + de.getMessage(), de);
			throw new DAOException("Unable to retrieveInvoiceByReportID "
					+ reportId + ". " + de.getMessage(), de);
		}
		return ret;
	}

	/**
	 * @param permitId
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Invoice retrieveInvoiceByPermitID(User user, Integer permitID)
			throws DAOException {
		Invoice ret = null;
		try {
			ret = invoiceDAO().retrieveInvoiceByPermitID(permitID);
			if (ret != null) {
				if (user != null && ret.getRevenueId() != null) {
					ret.setRevenue(retrieveRevenue(user, ret.getRevenueId()));
					ret.setRevenueImmediate(null);
					if (InvoiceState.hasBeenPosted(ret.getInvoiceStateCd())) {
						ret.setRevenueImmediate(ret.getRevenue());
					}
					ret.checkPostCompleted();
				}

				for (InvoiceNote note : invoiceDAO().retrieveInvoiceNotes(
						ret.getInvoiceId())) {
					ret.addNote(note);
				}
			}
		} catch (Exception de) {
			logger.error("Unable to retrieveInvoiceByPermitID " + permitID
					+ ". " + de.getMessage(), de);
			throw new DAOException("Unable to retrieveInvoiceByPermitID "
					+ permitID + ". " + de.getMessage(), de);
		}
		return ret;
	}

	/**
	 * @param facilityId
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public InvoiceList[] retrieveInvoicesByFacilityID(String facilityID)
			throws DAOException {
		InvoiceList[] invoices;

		try {
			InvoiceList invSearchObj = new InvoiceList();
			invSearchObj.setFacilityId(facilityID);
			// Late Latter is not generated when an invoice is in
			// 'Ready_To_Invoice' or 'Late_Letter_Invoice' states.
			invSearchObj.setInvoiceStateCd(InvoiceState.POSTED_TO_REVENUES);

			invoices = invoiceDAO().searchInvoices(invSearchObj);

		} catch (Exception de) {
			logger.error("Unable to retrieveInvoiceByFacilityID " + facilityID
					+ ". " + de.getMessage(), de);
			throw new DAOException("Unable to retrieveInvoiceByFacilityID "
					+ facilityID + ". " + de.getMessage(), de);
		}
		return invoices;
	}

	/**
	 * @param invoice
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Invoice compareInvoice(Invoice invoice, Invoice previousInvoice) {
		Invoice ret = new Invoice();
		try {
			ret.setInvoiceId(previousInvoice.getInvoiceId());
			ret.setOrigAmount(invoice.getOrigAmount()
					- previousInvoice.getOrigAmount());

			for (InvoiceDetail oInv : invoice.getInvoiceDetails()) {
				InvoiceDetail d = new InvoiceDetail();
				d = oInv;

				for (InvoiceDetail pInv : previousInvoice.getInvoiceDetails()) {
					if (oInv.getDescription().equals(pInv.getDescription())) {
						d.setDifference(oInv.getAmount() - pInv.getAmount());
					}
				}
				ret.addInvoiceDetail(d);
			}
		} catch (Exception e) {
			logger.error("Unable to compare Invoices "
					+ previousInvoice.getInvoiceId() + " and "
					+ invoice.getInvoiceId() + ": " + e);
		}
		return ret;
	}

	/**
	 * @return RevenuesService
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public RevenuesService getRevenueService() throws DAOException {
		logger.error("method not implemented.");
		// RevenuesService revenueService = null;
		// try {
		// revenueService = ServiceFactory.getInstance().getRevenuesService();
		// if (revenueService == null) {
		// if(!testWithoutRevenues) {
		// logger.error("RevenuesService is null.");
		// // generate pretty form
		// throw new DAOException("RevenuesService is null.",
		// Invoice.RevenueNotAvail);
		// }
		// }
		// } catch (ServiceFactoryException sfe) {
		// if(!testWithoutRevenues) {
		// logger.error("Cannot locate RevenuesService:" + sfe);
		// // generate pretty form
		// throw new DAOException("Cannot locate RevenuesService."
		// + sfe.getMessage(), Invoice.RevenueNotAvail, sfe);
		// }
		// }
		// return revenueService;
		return null;
	}

	/**
	 * Handles Emissions Inventory Workflow Aggregate task - Returns a Revenue ID
	 * for new Invoices, and makes an adjustment to Adjusted Invoice which has a
	 * Revenue ID from its previous Invoice.
	 * 
	 * @param processIDs
	 * @param user
	 * 
	 * @return ValidationMessage
	 * @throw DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public List<ValidationMessage> preparePostToRevenues(User user,
			List<Integer> pIDs, Timestamp invoiceDate) {

		EmissionsReport report = null;
		Facility facility = null;

		List<Integer> failedPIDs = new ArrayList<Integer>();
		List<ValidationMessage> retVal = new ArrayList<ValidationMessage>();
		String adjustmentType = AdjustmentType.ADJUST_CURRENT_AMOUNT;

		for (Integer pID : pIDs) {
			Invoice invoice = null;
			ValidationMessage vm = null;

			try {
				report = emissionsReportDAO().retrieveEmissionsReport(pID);
			} catch (Exception ex) {
				String msg = "Exception while attempting to read emissions inventory "
						+ pID;
				logger.error(msg, ex);
				vm = new ValidationMessage("Emissions Inventory ", msg,
						ValidationMessage.Severity.ERROR,
						ValidationBase.REPORT_TAG + ":"
								+ report.getEmissionsRptId());
				retVal.add(vm);
				failedPIDs.add(pID);
				continue;
			}
			if (report == null) {
				ValidationMessage vmsg = new ValidationMessage(
						"Emissions Inventory", "Failed to read emissions inventory "
								+ pID, ValidationMessage.Severity.ERROR,
						ValidationBase.REPORT_TAG + ":" + pID);
				retVal.add(vmsg);
				failedPIDs.add(pID);
				continue;
			}
			try {
				facility = facilityDAO().retrieveFacility(report.getFpId());
			} catch (Exception ex) {
				String msg = "Exception while attempting to read facility, fpId="
						+ report.getFpId() + ", for report " + pID;
				logger.error(msg, ex);
				vm = new ValidationMessage("Emissions Inventory ", msg,
						ValidationMessage.Severity.ERROR,
						ValidationBase.REPORT_TAG + ":"
								+ report.getEmissionsRptId());
				retVal.add(vm);
				failedPIDs.add(pID);
				continue;
			}
			if (facility == null) {
				ValidationMessage vmsg = new ValidationMessage(
						"Emissions Inventory", "Failed to read facility, fpId="
								+ report.getFpId() + ", for report " + pID,
						ValidationMessage.Severity.ERROR,
						ValidationBase.REPORT_TAG + ":"
								+ report.getEmissionsRptId());
				retVal.add(vmsg);
				failedPIDs.add(pID);
				continue;
			}
			try {
				invoice = retrieveInvoiceByReportID(user,
						report.getEmissionsRptId());
			} catch (Exception ex) {
				String msg = "Exception while attempting to read invoice for report "
						+ pID
						+ ", for "
						+ facility.getName()
						+ " #"
						+ facility.getFacilityId();
				logger.error(msg, ex);
				vm = new ValidationMessage("Emissions Inventory ", msg,
						ValidationMessage.Severity.ERROR,
						ValidationBase.REPORT_TAG + ":"
								+ report.getEmissionsRptId());
				retVal.add(vm);
				failedPIDs.add(pID);
				continue;
			}
			if (!report.isApproved()) {
				ValidationMessage vmsg = new ValidationMessage(
						"Emissions Inventory", "Emissions inventory " + pID
								+ " has not been approved ",
						ValidationMessage.Severity.ERROR,
						ValidationBase.REPORT_TAG + ":"
								+ report.getEmissionsRptId());
				retVal.add(vmsg);
				failedPIDs.add(pID);
				continue;
			}

			if (invoice != null) {
				// Determine if branching
				try {
					retrieveOtherInvoices(user, report, invoice);
				} catch (RemoteException re) {
					String msg = "Exception while attempting to retrieveOtherInvoices for report "
							+ pID
							+ " and invoice "
							+ invoice.getInvoiceId()
							+ ", for "
							+ facility.getName()
							+ " #"
							+ facility.getFacilityId();
					logger.error(msg, re);
					vm = new ValidationMessage("Emissions Inventory ", msg,
							ValidationMessage.Severity.ERROR,
							ValidationBase.REPORT_TAG + ":"
									+ report.getEmissionsRptId());
					retVal.add(vm);
					failedPIDs.add(pID);
					continue;
				}
				if (invoice.getPrevInvoiceFailureMsg() != null) {
					// skip this invoice because there
					// is branching and must be done manually.
					ValidationMessage vmsg = new ValidationMessage(
							"Emissions Inventory",
							invoice.getPrevInvoiceFailureMsg()
									+ "  This must be processed from the Invoice Detail page.",
							ValidationMessage.Severity.ERROR,
							ValidationBase.REPORT_TAG + ":"
									+ report.getEmissionsRptId());
					retVal.add(vmsg);
					failedPIDs.add(pID);
					continue;
				}
				if (InvoiceState.hasBeenPosted(invoice.getInvoiceStateCd())
						&& invoice.getRevenueId() == null) {
					ValidationMessage vmsg = new ValidationMessage(
							"Emissions Inventory", "Invoice Id "
									+ invoice.getInvoiceId() + ":"
									+ Invoice.lostRevenueMsg(),
							ValidationMessage.Severity.ERROR,
							ValidationBase.REPORT_TAG + ":"
									+ report.getEmissionsRptId());
					retVal.add(vmsg);
					failedPIDs.add(pID);
					continue;
				}
				String errStr;
				try {
					errStr = newerAvailable(report.getEmissionsRptId());
				} catch (Exception ex) {
					String msg = "Exception while checking for newer reports/invoices. Invoice, Id="
							+ invoice.getInvoiceId()
							+ " Report- "
							+ invoice.getEmissionsRptId()
							+ ", for "
							+ facility.getName()
							+ " #"
							+ facility.getFacilityId();
					logger.error(msg, ex);
					vm = new ValidationMessage("Emissions Inventory ", msg,
							ValidationMessage.Severity.ERROR,
							ValidationBase.REPORT_TAG + ":"
									+ report.getEmissionsRptId());
					retVal.add(vm);
					failedPIDs.add(pID);
					continue;
				}
				if (errStr != null && errStr.length() > 0) {
					String msg = " Invoice invoiceId- "
							+ invoice.getInvoiceId() + " Report- "
							+ invoice.getEmissionsRptId() + ", for "
							+ facility.getName() + " #"
							+ facility.getFacilityId() + " is not the latest: "
							+ errStr;

					vm = new ValidationMessage("Emissions Inventory ", msg,
							ValidationMessage.Severity.ERROR,
							ValidationBase.REPORT_TAG + ":"
									+ report.getEmissionsRptId());
					retVal.add(vm);
					failedPIDs.add(pID);
					continue;
				}
				if (!invoice.isAdjustedInvoice()) {
					if (invoice.getInvoiceStateCd().equalsIgnoreCase(
							InvoiceState.READY_TO_INVOICE)
							&& invoice.getRevenueId() == null) {

						try {
							ValidationMessage pvm = preparePostToRevenue(user,
									invoice, invoiceDate, false);

							if (pvm != null) {
								retVal.add(pvm);
								failedPIDs.add(pID);
								continue;
							}

						} catch (Exception e) {
							String msg = "Exception while attempting to preparePostToRevenue for for report "
									+ pID
									+ " and invoice "
									+ invoice.getInvoiceId()
									+ ", for "
									+ facility.getName()
									+ " #"
									+ facility.getFacilityId();
							ValidationMessage vmsg = new ValidationMessage(
									"Emissions Inventory", msg,
									ValidationMessage.Severity.ERROR,
									ValidationBase.REPORT_TAG + ":"
											+ report.getEmissionsRptId());
							logger.error(msg, e);
							retVal.add(vmsg);
							failedPIDs.add(pID);
							continue;
							// If an invoice fails to post in aggregate display
							// error and continue.
						}
					} else if (InvoiceState.hasBeenPosted(invoice
							.getInvoiceStateCd())) {
						String msg = "Invoice for Emissions Inventory- "
								+ report.getEmissionsRptId()
								+ " has already been posted. Invoice Id- "
								+ invoice.getInvoiceId() + ", Revenue Id- "
								+ invoice.getRevenueId() + ", Report- "
								+ invoice.getEmissionsRptId() + " for "
								+ facility.getName() + " #"
								+ facility.getFacilityId();

						vm = new ValidationMessage("Emissions Inventory ", msg,
								ValidationMessage.Severity.INFO,
								ValidationBase.REPORT_TAG + ":"
										+ report.getEmissionsRptId());
						retVal.add(vm);
						continue;
					} else {
						// String msg =
						// "Cannot perform any activity on a Cancelled or Late Letter invoice,Emissions Inventory- "
						// + report.getEmissionsRptId() + ". Invoice Id- "
						// + invoice.getInvoiceId() + ", for "
						// + facility.getName() + " #" +
						// facility.getFacilityId();

						// vm = new ValidationMessage("Emissions Inventory ", msg,
						// ValidationMessage.Severity.INFO);

					}
				} else if (invoice.isAdjustedInvoice()) {
					Integer revenueId = null;

					if (invoice.getRevenueId() != null) {
						revenueId = invoice.getRevenueId();
					} else if (invoice.getPrevInvoice().getRevenueId() != null) {
						revenueId = invoice.getPrevInvoice().getRevenueId();
					}
					if (invoice.getInvoiceStateCd().equals(
							InvoiceState.CANCELLED)) {
						String msg = "Cannot perform any activity on a Cancelled or Late Letter invoice,Emissions Inventory- "
								+ report.getEmissionsRptId()
								+ ". Invoice Id- "
								+ invoice.getInvoiceId()
								+ " ,Report- "
								+ invoice.getEmissionsRptId()
								+ " for "
								+ facility.getName()
								+ " #"
								+ facility.getFacilityId();

						vm = new ValidationMessage("Emissions Inventory ", msg,
								ValidationMessage.Severity.INFO,
								ValidationBase.REPORT_TAG + ":"
										+ report.getEmissionsRptId());
						retVal.add(vm);
						continue;
					} else if (InvoiceState.hasBeenPosted(invoice
							.getInvoiceStateCd())) {
						String msg = "Cannot perform any activity on an Invoice that is already posted/adjusted. Invoice Id- "
								+ invoice.getInvoiceId()
								+ ", Report- "
								+ invoice.getEmissionsRptId()
								+ " for "
								+ facility.getName()
								+ " #"
								+ facility.getFacilityId();

						vm = new ValidationMessage("Emissions Inventory ", msg,
								ValidationMessage.Severity.INFO,
								ValidationBase.REPORT_TAG + ":"
										+ report.getEmissionsRptId());
						retVal.add(vm);
						continue;
					} else if (invoice.getPrevInvoice().getInvoiceStateCd()
							.equals(InvoiceState.READY_TO_INVOICE)) {
						String msg = "Previous Invoice- "
								+ invoice.getPrevInvoice().getInvoiceId()
								+ " has not been posted yet, Report- "
								+ invoice.getPrevInvoice().getEmissionsRptId()
								+ "." + " Invoice- " + invoice.getInvoiceId()
								+ " Report- " + invoice.getEmissionsRptId()
								+ ", for " + facility.getName() + " #"
								+ facility.getFacilityId();

						vm = new ValidationMessage("Emissions Inventory ", msg,
								ValidationMessage.Severity.ERROR,
								ValidationBase.REPORT_TAG + ":"
										+ report.getEmissionsRptId());
						retVal.add(vm);
						failedPIDs.add(pID);
						continue;
					} else if (revenueId == null) {
						String msg = " Invoice has no RevenueId, invoiceId- "
								+ invoice.getInvoiceId() + " Report- "
								+ invoice.getEmissionsRptId() + ", for "
								+ facility.getName() + " #"
								+ facility.getFacilityId();

						vm = new ValidationMessage("Emissions Inventory ", msg,
								ValidationMessage.Severity.ERROR,
								ValidationBase.REPORT_TAG + ":"
										+ report.getEmissionsRptId());
						retVal.add(vm);
						failedPIDs.add(pID);
						continue;
					} else {
						/*
						 * Same Revenue Id will be maintained for an adjusted
						 * invoice which will be created as a result of Revised
						 * Emissions Inventory and will perform an adjustment to
						 * Revenues. An adjustment will be performed only when
						 * there is a positive or negative amount if zero, skip
						 * making an Adjustment and change the state to
						 * 'Posted'. checking origAmount != 0 to avoid
						 * *EXCEPTION* EPAIllegalValueException : No need to
						 * adjust a revenue by zero.
						 */

						if (invoice.getCompInvoice().getOrigAmount() != 0) {
							String documentID = SystemPropertyDef.getSystemPropertyValue("Revenue_Adjustment_DocumentID", null);
							String reason = "";
							try { // update invoice when amount not zero -- done
									// before revenue update
									// invoice.setRevenueId(revenueId);
								invoice.setInvoiceStateCd(InvoiceState.POSTED_TO_REVENUES);
								invoice.setDueDate(invoiceDate);
								int nextAdj = invoice.highestAdjustment() + 1;
								invoice.setAdjustmentNum(nextAdj);
								reason = Invoice.prefix
										+ nextAdj
										+ "): "
										+ SystemPropertyDef.getSystemPropertyValue("RevisedRptAdjustment_Reason", null);
								// Due Date field in db is only a placeholder
								// for adjustment date on which an adjusted
								// invoice is posted and
								// this date will be used as Invoice date for
								// printed invoice. This applies only to
								// adjusted invoice.
								modifyInvoice(invoice);
							} catch (Exception ex) {
								String msg = "Exception while modifying Invoice, Id="
										+ invoice.getInvoiceId()
										+ " Report- "
										+ invoice.getEmissionsRptId()
										+ ", for "
										+ facility.getName()
										+ " #"
										+ facility.getFacilityId();
								logger.error(msg, ex);
								vm = new ValidationMessage("Emissions Inventory ",
										msg, ValidationMessage.Severity.ERROR,
										ValidationBase.REPORT_TAG + ":"
												+ report.getEmissionsRptId());
								retVal.add(vm);
								failedPIDs.add(pID);
								continue;
							}
							try {
								ValidationMessage avm = createAdjustment(user,
										adjustmentType, invoice, invoice
												.getCompInvoice()
												.getOrigAmount(), documentID,
										reason);

								if (avm != null) {
									retVal.add(avm);
									if (avm.getSeverity() == ValidationMessage.Severity.ERROR) {
										failedPIDs.add(pID);
										continue;
									}
								}
							} catch (Exception e) {
								String msg = "Exception calling createAdjustment, InvoiceId="
										+ invoice.getInvoiceId()
										+ " Report- "
										+ invoice.getEmissionsRptId()
										+ ", for "
										+ facility.getName()
										+ " #"
										+ facility.getFacilityId();
								logger.error(msg, e);
								vm = new ValidationMessage("Emissions Inventory ",
										msg, ValidationMessage.Severity.ERROR,
										ValidationBase.REPORT_TAG + ":"
												+ report.getEmissionsRptId());
								retVal.add(vm);
								failedPIDs.add(pID);
								continue;
							}
						} else { // origAmount == 0
							String msg = "Revised Emissions Inventory "
									+ report.getEmissionsRptId()
									+ " created Invoice with "
									+ (report.getTotalEmissions() == null ? "zero"
											: report.getTotalEmissions())
									+ " billable Emissions and Invoice difference amount is - "
									+ invoice.getCompInvoice().getOrigAmount()
									+ ", for " + facility.getName() + " #"
									+ facility.getFacilityId();

							vm = new ValidationMessage("Emissions Inventory ",
									msg, ValidationMessage.Severity.INFO,
									ValidationBase.REPORT_TAG + ":"
											+ report.getEmissionsRptId());
							try { // update invoice when amount is zero
									// invoice.setRevenueId(revenueId);
								invoice.setInvoiceStateCd(InvoiceState.POSTED_TO_REVENUES);
								invoice.setDueDate(invoiceDate);
								// Due Date field in db is only a placeholder
								// for adjustment date on which an adjusted
								// invoice is posted and
								// this date will be used as Invoice date for
								// printed invoice. This applies only to
								// adjusted invoice.
								modifyInvoice(invoice);
								retVal.add(vm);
								continue;
							} catch (Exception ex) {
								msg = "Exception while modifying Invoice, Id="
										+ invoice.getInvoiceId() + " Report- "
										+ invoice.getEmissionsRptId()
										+ ", for " + facility.getName() + " #"
										+ facility.getFacilityId();
								logger.error(msg, ex);
								vm = new ValidationMessage("Emissions Inventory ",
										msg, ValidationMessage.Severity.ERROR,
										ValidationBase.REPORT_TAG + ":"
												+ report.getEmissionsRptId());
								retVal.add(vm);
								failedPIDs.add(pID);
								continue;
							}
						}
					}
				}
			} else { // there is no invoice
				if (report.getTotalEmissions() != null
						&& report.getTotalEmissions() > 0) {
					String msg = "No Invoice found for Emissions Inventory "
							+ report.getEmissionsRptId() + " - "
							+ report.getTotalEmissions() + " Emissions, for "
							+ facility.getName() + " #"
							+ facility.getFacilityId();

					vm = new ValidationMessage("Emissions Inventory ", msg,
							ValidationMessage.Severity.ERROR,
							ValidationBase.REPORT_TAG + ":"
									+ report.getEmissionsRptId());
					retVal.add(vm);
					failedPIDs.add(pID);
					continue;
				} else if (report.getFeeId() != null) { // null or zero total
														// emissions
					// check fee to see if any charge
					Fee fee = null;
					try {
						fee = serviceCatalogDAO()
								.retrieveFee(report.getFeeId());
					} catch (RemoteException re) {
						String msg = "Exception while attempting to retrieveFee for report "
								+ pID
								+ ", for "
								+ facility.getName()
								+ " #"
								+ facility.getFacilityId();
						logger.error(msg, re);
						vm = new ValidationMessage("Emissions Inventory ", msg,
								ValidationMessage.Severity.ERROR,
								ValidationBase.REPORT_TAG + ":"
										+ report.getEmissionsRptId());
						retVal.add(vm);
						failedPIDs.add(pID);
						continue;
					}
					if (fee == null) {
						String msg = "No Invoice found and Fee not found for Emissions Inventory "
								+ report.getEmissionsRptId()
								+ " - "
								+ " for "
								+ facility.getName()
								+ " #"
								+ facility.getFacilityId();
						vm = new ValidationMessage("Emissions Inventory ", msg,
								ValidationMessage.Severity.ERROR,
								ValidationBase.REPORT_TAG + ":"
										+ report.getEmissionsRptId());
						retVal.add(vm);
						failedPIDs.add(pID);
						continue;
					} else {
						if (report.getTotalEmissions() != null
								&& report.getTotalEmissions() > 0.) { // no
																		// invoice,
																		// but
																		// there
																		// were
																		// emissions.
							// There may be other cases where there should be a
							// fee but we don't
							// expect this error condition and it is not worth
							// checking in detail.
							String msg = "No Invoice found for Emissions Inventory "
									+ report.getEmissionsRptId()
									+ " - "
									+ " for "
									+ facility.getName()
									+ " #"
									+ facility.getFacilityId();
							vm = new ValidationMessage("Emissions Inventory ",
									msg, ValidationMessage.Severity.ERROR,
									ValidationBase.REPORT_TAG + ":"
											+ report.getEmissionsRptId());
							retVal.add(vm);
							failedPIDs.add(pID);
							continue;
						}
					}
				} else { // no feeId
					String msg = "No Invoice found for Emissions Inventory "
							+ report.getEmissionsRptId() + " for "
							+ facility.getName() + " #"
							+ facility.getFacilityId();
					vm = new ValidationMessage("Emissions Inventory ", msg,
							ValidationMessage.Severity.ERROR,
							ValidationBase.REPORT_TAG + ":"
									+ report.getEmissionsRptId());
					retVal.add(vm);
					failedPIDs.add(pID);
					continue;
				}
			}
		}
		for (Integer pID : failedPIDs) {
			pIDs.remove(pID);
		}
		return retVal;
	}

	/**
	 * @return String
	 * @throws DAOException
	 * @param rptId
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public String newerAvailable(Integer rptId) throws DAOException {
		StringBuffer errMsg = new StringBuffer();
		newerAvailable(errMsg, rptId);
		return errMsg.toString();
	}

	public void newerAvailable(StringBuffer errMsg, Integer rptId)
			throws DAOException {
		try {
			EmissionsReport r = new EmissionsReport();
			Invoice i = new Invoice();
			EmissionsReport r2 = new EmissionsReport();
			Invoice i2 = new Invoice();
			emissionsReportDAO().retrieveReportInvoicePair(r, r2, i, i2, rptId);
			if (r.getEmissionsRptId() != null) {
				if (ReportReceivedStatusDef.SUBMITTED_CAUTION.equals(r
						.getRptReceivedStatusCd())
						|| ReportReceivedStatusDef.SUBMITTED.equals(r
								.getRptReceivedStatusCd())) {
					addErrorSeparator(errMsg);
					errMsg.append("report "
							+ r.getEmissionsRptId()
							+ " is in state "
							+ ReportReceivedStatusDef
									.getData()
									.getItems()
									.getDescFromAllItem(
											r.getRptReceivedStatusCd()));
				}
			}
			if (i != null) {
				// If there is an invoice check its state
				if (InvoiceState.READY_TO_INVOICE.equals(i.getInvoiceStateCd())) {
					addErrorSeparator(errMsg);
					errMsg.append("invoice "
							+ i.getInvoiceId()
							+ " is in state "
							+ InvoiceState.getData().getItems()
									.getDescFromAllItem(i.getInvoiceStateCd()));
				}
			}
			if (r2.getEmissionsRptId() != null) {
				if (ReportReceivedStatusDef.SUBMITTED_CAUTION.equals(r2
						.getRptReceivedStatusCd())
						|| ReportReceivedStatusDef.SUBMITTED.equals(r2
								.getRptReceivedStatusCd())) {
					addErrorSeparator(errMsg);
					errMsg.append("report "
							+ r2.getEmissionsRptId()
							+ " is in state "
							+ ReportReceivedStatusDef
									.getData()
									.getItems()
									.getDescFromAllItem(
											r2.getRptReceivedStatusCd()));
				}
			}
			if (i2.getInvoiceId() != null) {
				// If there is an invoice check its state
				if (InvoiceState.READY_TO_INVOICE
						.equals(i2.getInvoiceStateCd())) {
					addErrorSeparator(errMsg);
					errMsg.append("invoice "
							+ i2.getInvoiceId()
							+ " is in state "
							+ InvoiceState.getData().getItems()
									.getDescFromAllItem(i2.getInvoiceStateCd()));
				}
			}

			if (r.getEmissionsRptId() != null) {
				newerAvailable(errMsg, r.getEmissionsRptId());
			}
			if (r2.getEmissionsRptId() != null) {
				newerAvailable(errMsg, r2.getEmissionsRptId());
			}
		} catch (DAOException daoe) {
			String msg = "newerAvailable() failed on report " + rptId;
			logger.error(msg, daoe);
			throw new DAOException(msg, daoe);
		}
	}

	void addErrorSeparator(StringBuffer sb) {
		if (sb.length() != 0) {
			sb.append("; ");
		}
	}

	/**
	 * @param user
	 * @param invoice
	 * @param effectiveDate
	 * @param isAtomicAction
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ValidationMessage preparePostToRevenue(User user, Invoice invoice,
			Timestamp effectiveDate, boolean isAtomicAction)
			throws DAOException {
		OID revenueId = null;
		PermitDocument tempDoc = null;
		String templateTypeCD = null;

		ValidationMessage postMsg = null;
		if (user == null && !testWithoutRevenues) {
			throw new DAOException("User is null", "User is null");
		}

		try {
			String methodCode = SystemPropertyDef.getSystemPropertyValue("MethodCode", null);
			String dataSource = SystemPropertyDef.getSystemPropertyValue("DataSource", null);
			String documentId = SystemPropertyDef.getSystemPropertyValue("Revenue_DocumentID", null);
			String reason = SystemPropertyDef.getSystemPropertyValue("Revenue_Reason", null);

			Facility facility = facilityDAO().retrieveFacility(
					invoice.getFacilityId());

			String revenueType = invoice.getRevenueTypeCd();
			Integer corePlaceId = facility.getCorePlaceId();
			Double amount = invoice.getOrigAmount();

			if (amount == null) {
				throw new DAOException("Current Invoice Total Amount is null");
			}
			if (methodCode == null) {
				throw new DAOException("Method Code is null");
			}
			if (dataSource == null) {
				throw new DAOException("Data Source is null");
			}
			if (effectiveDate == null) {
				throw new DAOException("Invoice Date is null");
			}
			if (documentId == null) {
				throw new DAOException("Document Id is null");
			}
			if (revenueType == null) {
				throw new DAOException("Revenue Type Code is null");
			}
			if (corePlaceId == null) {
				throw new DAOException("CorePlace Id is null");
			}
			if (reason == null) {
				throw new DAOException("Reason is null");
			}
			Calendar effDate = Calendar.getInstance();
			effDate.setTime(effectiveDate);
			Calendar dueDate = Calendar.getInstance();

			if (!isAtomicAction) {
				Integer days = SystemPropertyDef.getSystemPropertyValueAsInteger("ER_PayableDays", null);
				dueDate.setTime(Invoice.calculateDueDate(
						new Timestamp(effDate.getTimeInMillis()), days));

			} else {
				dueDate.setTime(invoice.getDueDate());
			}

			// if (user != null) {
			// First mark posted prior to getting revenue Id.
			invoice.setInvoiceStateCd(InvoiceState.POSTED_TO_REVENUES);
			modifyInvoiceRecord(invoice);
			invoice = retrieveInvoiceRecord(invoice.getInvoiceId(),
					isAtomicAction);
			if (invoice == null) {
				throw new DAOException(
						"Failed to retrieveInvoiceRecord() for invoice");
			}
			if (!testWithoutRevenues) {
				revenueId = postToRevenues(user, revenueType, corePlaceId,
						documentId, methodCode, amount, effDate, dueDate,
						dataSource, reason, isAtomicAction);
			} else {
				revenueId = new OID("999999");
			}

			if (revenueId != null) {
				logger.error("INFO: For Stars2 invoice "
						+ invoice.getInvoiceId() + ", Revenue Id - "
						+ revenueId + " for RevenueType - " + revenueType);
				invoice.setRevenueId(new Integer(revenueId.toString()));
				invoice.setInvoiceStateCd(InvoiceState.POSTED_TO_REVENUES);

				if (invoice.getPermitId() != null
						&& invoice.getRevenueId() != null) {
					Permit permit = permitDAO().retrievePermit(
							invoice.getPermitId());
					if (permit instanceof PTIOPermit
							//|| permit instanceof RPEPermit
							) {
						templateTypeCD = TemplateDocTypeDef.PERMIT_INVOICE;
					}
					TemplateDocument templateDoc = TemplateDocTypeDef
							.getTemplate(templateTypeCD);

					// When a revenue is created original amt and balance due
					// amt will be same,
					// since some issues arised while setting revenue in
					// permit's invoice object
					// invoice orig amt is used as balance due in permit's
					// invoice document.

					if (invoice != null) {
						invoice.setContact(new Contact()); // so we know if
															// DocumentGenerationException
															// was because
															// invoice had no
															// contact
					}
					if ((tempDoc = generatePermitInvDocument(facility, permit,
							invoice, "15", templateDoc)) != null) {
						invoice.setPermitInvDocument(tempDoc);
					}
				}

				modifyInvoiceRecord(invoice);

				// String msg = "Post to Revenues successful for Invoice Id- "
				// + invoice.getInvoiceId() + ", Revenue Id- " +
				// invoice.getRevenueId() + ","
				// + " for " + facility.getName() + " #" +
				// invoice.getFacilityId();

				// postMsg = new ValidationMessage("Invoice ", msg,
				// ValidationMessage.Severity.INFO);

			} else {
				throw new DAOException(
						"postToRevenues() returned null for invoice "
								+ invoice.getInvoiceId());
			}
			// } // end of: if (user != null)

		} catch (DocumentGenerationException dge) {
			if (invoice != null && invoice.getContact() == null) {
				// problem is no billing address is null
				if (isAtomicAction) {
					String msg = "No active billing contact for invoice "
							+ invoice.getInvoiceId();
					logger.error(msg, dge);
					// try {
					// if (tempDoc != null) {
					// DocumentUtil.removeDocument(tempDoc.getPath());
					// }
					// } catch (IOException io) {
					// logger.error(
					// "Exception deleting document "
					// + tempDoc.getPath(), io);
					// }
					// throw new DAOException(msg, dge);
				}
				String message = "No active billing contact for invoice "
						+ invoice.getInvoiceId() + ", Facility- "
						+ invoice.getFacilityName() + " #"
						+ invoice.getFacilityId() + "- " + dge.getMessage();
				postMsg = new ValidationMessage("Emissions Inventory ", message,
						ValidationMessage.Severity.ERROR);
				logger.error(message, dge);
			} else {
				// Some other problem.
				if (isAtomicAction) {
					String msg = "Exception in Post to Revenues : Revenue Id - "
							+ revenueId;
					logger.error(msg, dge);
					// try {
					// if (tempDoc != null) {
					// DocumentUtil.removeDocument(tempDoc.getPath());
					// }
					// } catch (IOException io) {
					// logger.error(
					// "Exception deleting document "
					// + tempDoc.getPath(), io);
					// }
					throw new DAOException(msg, dge);
				}
			}
			String message = "Invoice failed to post to Revenues. invoice- "
					+ invoice.getInvoiceId() + ", Facility- "
					+ invoice.getFacilityName() + " #"
					+ invoice.getFacilityId() + "- " + dge.getMessage();
			postMsg = new ValidationMessage("Emissions Inventory ", message,
					ValidationMessage.Severity.ERROR);
			logger.error(message, dge);
		} catch (Exception ex) {
			if (isAtomicAction) {
				String msg = "Exception in Post to Revenues : Revenue Id - "
						+ revenueId;
				logger.error(msg, ex);
				try {
					if (tempDoc != null) {
						DocumentUtil.removeDocument(tempDoc.getPath());
					}
				} catch (IOException io) {
					logger.warn("Exception deleting document "
							+ tempDoc.getPath());
				}
				throw new DAOException(msg, ex);
			}
			String message = "Invoice failed to post to Revenues. invoice- "
					+ invoice.getInvoiceId() + ", Facility- "
					+ invoice.getFacilityName() + " #"
					+ invoice.getFacilityId() + "- " + ex.getMessage();
			postMsg = new ValidationMessage("Emissions Inventory ", message,
					ValidationMessage.Severity.ERROR);
			logger.error(message, ex);
		}

		return postMsg;
	}

	/**
	 * @param user
	 * @param revenueType
	 * @param corePlaceId
	 * @param documentId
	 * @param methodCode
	 * @param OrigAmount
	 * @param effDate
	 * @param dueDate
	 * @param dataSource
	 * @param reason
	 * 
	 * @return OID
	 * 
	 *         throws EPAException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	private OID postToRevenues(User user, String revenueType,
			Integer corePlaceId, String documentId, String methodCode,
			Double origAmount, Calendar effDate, Calendar dueDate,
			String dataSource, String reason, boolean isAtomicAction)
			throws DAOException {

		// RevenuesService revenueService = null;
		OID oid = null;
		try {
			// revenueService = getRevenueService();

			logger.error("logic not implemented");
			// oid = revenueService.createReceivable(user, revenueType,
			// null, null, null, null, new OID(corePlaceId),
			// new OID(documentId), methodCode, new Money(origAmount),
			// effDate, dueDate, dataSource, reason);
		} catch (Exception ex) {
			if (isAtomicAction) {
				logger.error("Post to Revenues failed for corePlaceId "
						+ corePlaceId + ". " + ex.getMessage(), ex);
			}
			throwNewDAOException("Post to Revenues failed for corePlaceId "
					+ corePlaceId + ". " + ex.getMessage(), ex);
		}
		return oid;
	}

	private void throwNewDAOException(String msg, Exception ex)
			throws DAOException {
		if (ex.getClass().equals(DAOException.class)
				&& !((DAOException) ex).prettyMsgIsNull()) {
			String s = ((DAOException) ex).getPrettyMsg();
			// throw the same pretty exception
			throw new DAOException(s, s, ex);
		} else {
			// otherwise turn the exception into a plain DAOException
			throw new DAOException(msg, ex);
		}
	}

	private void conditionalThrowNewDAOException(String msg, Exception ex)
			throws DAOException {
		if (ex.getClass().equals(DAOException.class)
				&& !((DAOException) ex).prettyMsgIsNull()) {
			String s = ((DAOException) ex).getPrettyMsg();
			// throw the same pretty exception
			throw new DAOException(s, s, ex);
		} else {
			// otherwise just return.
			return;
		}
	}

	/**
	 * @param user
	 * @param revenueId
	 * @param amount
	 * @param reason
	 * 
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public ValidationMessage createAdjustment(User user, String adjustmentType,
			Invoice invoice, Double amount, String documentID, String reason)
			throws DAOException {
		// RevenuesService revenueService = null;
		ValidationMessage adjustmentMsg = null;

		try {
			if (user == null && !testWithoutRevenues) {
				throw new DAOException("User is null", "User is null");
			}
			String methodCode = SystemPropertyDef.getSystemPropertyValue("MethodCode", null);
			String dataSource = SystemPropertyDef.getSystemPropertyValue("DataSource", null);

			if (methodCode == null) {
				throw new DAOException("Method Code is null");
			}
			if (dataSource == null) {
				throw new DAOException("Data Source is null");
			}
			if (documentID == null) {
				throw new DAOException("Document ID is null");
			}
			if (reason == null) {
				throw new DAOException("Adjustment Reason is null");
			}

			// revenueService = getRevenueService();
			boolean adjusted = false;
			if (!testWithoutRevenues) {
				logger.error("logic not implemented.");
				// if
				// (adjustmentType.equals(AdjustmentType.SET_ORIGINAL_AMOUNT)) {
				// revenueService.adjustOriginalAmount(user, new
				// OID(invoice.getRevenueId()),
				// new Money(amount), methodCode, reason, dataSource,
				// new OID(documentID));
				// adjusted = true;
				// } else if (adjustmentType
				// .equals(AdjustmentType.ADJUST_CURRENT_AMOUNT)) {
				//
				// revenueService.adjustCurrentAmount(user, new
				// OID(invoice.getRevenueId()),
				// new Money(amount), methodCode, reason, dataSource,
				// new OID(documentID));
				// adjusted = true;
				// }
			} else {
				adjusted = true;
			}
			if (adjusted) {
				String msg = "Adjustment successfully made for Invoice Id- "
						+ invoice.getInvoiceId() + ", Revenue Id- "
						+ invoice.getRevenueId() + "," + " Facility ID #"
						+ invoice.getFacilityId();

				adjustmentMsg = new ValidationMessage("Emissions Inventory ", msg,
						ValidationMessage.Severity.INFO);
			} else {
				String msg = "Adjustment failed for Invoice Id- "
						+ invoice.getInvoiceId() + ", Revenue Id- "
						+ invoice.getRevenueId() + "," + " Facility ID #"
						+ invoice.getFacilityId();

				adjustmentMsg = new ValidationMessage("Emissions Inventory ", msg,
						ValidationMessage.Severity.ERROR);
			}
			// } catch(EPAValidationException vex){
			// String message = "Adjustment failed for Invoice Id- "
			// + invoice.getInvoiceId() + ", Revenue Id- " +
			// invoice.getRevenueId() + ","
			// + " Facility ID #" + invoice.getFacilityId() +
			// " -- Check Document Id.  Reason: "
			// + vex.getMessage();
			// adjustmentMsg = new ValidationMessage("Emissions Inventory ",
			// message, ValidationMessage.Severity.ERROR);
			// logger.error(message, vex);
		} catch (Exception ex) {
			String message = "Adjustment failed for Invoice Id- "
					+ invoice.getInvoiceId() + ", Revenue Id- "
					+ invoice.getRevenueId() + "," + " Facility ID #"
					+ invoice.getFacilityId() + ".  Reason: " + ex.getMessage();
			adjustmentMsg = new ValidationMessage("Emissions Inventory ", message,
					ValidationMessage.Severity.ERROR);
			logger.error(message, ex);
			conditionalThrowNewDAOException(message, ex);
		}
		return adjustmentMsg;
	}

	/**
	 * @param user
	 * @param revenueId
	 * @param reason
	 * 
	 *            return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void performIndicatorAdjustment(User user, Integer revenueId,
			String adjustmentType) throws DAOException {
		// RevenuesService revenueService = null;
		try {
			if (user == null && !testWithoutRevenues) {
				throw new DAOException("User is null", "User is null");
			}
			// String dataSource = getParameter("DataSource");
			// String documentId = getParameter("Revenue_Indicator_DocumentID");
			// String reason = getParameter("Revenue_Indicator_Reason");

			if (!testWithoutRevenues) {
				logger.error("logic not implemented.");
				// revenueService = getRevenueService();
				// if (adjustmentType.equals("activate")) {
				// revenueService.activateRevenue(user, new OID(revenueId),
				// reason, dataSource, new OID(documentId));
				//
				// } else if (adjustmentType.equals("deActivate")) {
				// revenueService.deactivateRevenue(user, new OID(revenueId),
				// reason, dataSource, new OID(documentId));
				//
				// }
			}
		} catch (Exception ex) {
			logger.error("Cannot perform IndicatorAdjustment for Revenue Id - "
					+ revenueId, ex);
			throwNewDAOException(
					"Cannot perform IndicatorAdjustment for Revenue Id - "
							+ revenueId + ex.getMessage(), ex);
		}
	}

	/**
	 * @param user
	 * @param revenueId
	 * 
	 * @return revenue
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public Revenue retrieveRevenue(User user, Integer revenueId)
			throws DAOException {
		RevenuesService revenueService = null;
		Revenue revenue = null;
		try {
			revenueService = getRevenueService();
			if (revenueService == null) {
				// only true in test mode
				revenue = new Revenue();
				revenue.setBalanceDue(new Money("10.99"));
			} else {
				logger.error("logic not implemented.");
				// if (user == null) {
				// throw new DAOException("User is null", "User is null");
				// }
				// revenue = revenueService.retrieveRevenue(user, new
				// OID(revenueId));
			}
		} catch (Exception ex) {
			logger.error("Unable to retrieve revenue for Revenue Id - "
					+ revenueId, ex);
			throwNewDAOException("Unable to retrieve revenue for Revenue Id - "
					+ revenueId + ". " + ex.getMessage(), ex);
		}
		return revenue;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyInvoiceRecord(Invoice invoice) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			modifyInvoiceRecord(invoice, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
			throw de;
		} finally {
			closeTransaction(trans);
		}
		return true;
	}

	/**
	 * @param invoice
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyInvoiceRecord(Invoice invoice, Transaction trans)
			throws DAOException {
		InvoiceDAO invoiceDAO = invoiceDAO(trans);

		try {
			invoiceDAO.modifyInvoice(invoice);
		} catch (DAOException de) {
			logger.error("Exception in modifyInvoice " + invoice.getInvoiceId()
					+ ". " + de.getMessage(), de);
			throw de;
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyInvoice(Invoice invoice) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();

		try {
			modifyInvoice(invoice, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
			throw de;
		} finally {
			closeTransaction(trans);
		}
		return true;
	}

	/**
	 * @param invoice
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyInvoice(Invoice invoice, Transaction trans)
			throws DAOException {
		InvoiceDAO invoiceDAO = invoiceDAO(trans);

		try {
			invoiceDAO.modifyInvoice(invoice);
			// Remove detail lines
			invoiceDAO.removeDetailLines(invoice.getInvoiceId());
			// recreate detail lines
			for (InvoiceDetail i : invoice.getInvoiceDetails()) {
				i.setInvoiceId(invoice.getInvoiceId());
				invoiceDAO.createInvoiceDetail(i);
			}

			// for (InvoiceDetail i : invoice.getInvoiceDetails()) {
			// invoiceDAO.modifyInvoiceDetail(i);
			// }
		} catch (DAOException de) {
			logger.error("Exception in modifyInvoice " + invoice.getInvoiceId()
					+ ". " + de.getMessage(), de);
			throw de;
		}
	}

	/**
	 * @param invoiceNote
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public InvoiceNote createInvoiceNote(InvoiceNote invoiceNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InvoiceNote ret = null;

		try {
			ret = createInvoiceNote(invoiceNote, trans);

			if (ret != null) {
				trans.complete();
			} else {
				trans.cancel();
				logger.error("Failed to insert Invoice Note "
						+ invoiceNote.getInvoiceId());
			}
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @param invoiceNote
	 * @return
	 * @throws DAOException
	 */
	private InvoiceNote createInvoiceNote(InvoiceNote invoiceNote,
			Transaction trans) throws DAOException {
		InvoiceNote ret = null;
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		InvoiceDAO invoiceDAO = invoiceDAO(trans);
		try {
			Note tempNote = infraDAO.createNote(invoiceNote);

			if (tempNote != null) {
				ret = invoiceNote;
				ret.setNoteId(tempNote.getNoteId());

				invoiceDAO.addInvoiceNote(ret);
			} else {
				logger.error("Failed to create Invoice Note "
						+ invoiceNote.getInvoiceId());
				throw new DAOException("Failed to create Invoice Note "
						+ invoiceNote.getInvoiceId());

			}
		} catch (DAOException de) {
			logger.error(
					"Failed to create Invoice Note "
							+ invoiceNote.getInvoiceId(), de);
			throw de;
		}
		return ret;
	}

	/**
	 * @param invoiceNote
	 * @return boolean
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean modifyInvoiceNote(InvoiceNote invoiceNote)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		boolean ret = true;

		try {
			ret = infraDAO.modifyNote(invoiceNote);
			trans.complete();
		} catch (DAOException de) {
			logger.error("Exception while modifying Invoice Note "
					+ invoiceNote.getInvoiceId() + ". " + de.getMessage(), de);
			cancelTransaction(trans, de);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
		return ret;
	}

	/**
	 * @param invoiceId
	 * @reurn @ throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public InvoiceNote[] retrieveInvoiceNotes(Integer invoiceId)
			throws DAOException {
		InvoiceNote[] ret = null;

		try {
			ret = invoiceDAO().retrieveInvoiceNotes(invoiceId);

		} catch (DAOException de) {
			logger.error("Exception while retrieving Invoice Notes for "
					+ invoiceId + ". " + de.getMessage(), de);
			throw new DAOException("Unable to retrieve Invoice Notes for "
					+ invoiceId + ". " + de.getMessage(), de);
		}

		return ret;
	}

	/**
	 * @param invoice
	 * @return boolean
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public boolean modifyInvoiceDocument(Invoice invoice) throws DAOException {
		return invoiceDAO().modifyInvoiceDocument(invoice);
	}

	/**
	 * @param contact
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Contact modifyContactData(Contact contact) throws DAOException {
		Contact ret = null;
		Transaction trans = TransactionFactory.createTransaction();
		try {
//			InfrastructureHelper infraHelper = new InfrastructureHelper();
			ret = infrastructureHelper.modifyContactData(contact, trans);
			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * validate contact
	 * 
	 * @param contact
	 *            contact
	 * @return ValidationMessage[]
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ValidationMessage[] validateContact(Contact contact)
			throws DAOException {
		ArrayList<ValidationMessage> validMessages = new ArrayList<ValidationMessage>();
		ValidationMessage validationMessage;
		Integer contactId = contact.getContactId();
		// if (contact == null) {
		// DAOException e = new DAOException("invalid contact (null)");
		// throw e;
		// }

		ValidationMessage[] validationMessages = contact.validate();
		for (ValidationMessage tempMsg : validationMessages) {
			validMessages.add(tempMsg);
		}

		InfrastructureDAO infraDAO = infrastructureDAO();

		Contact tempContact = infraDAO.retrieveContact(contactId);
		if (tempContact != null
				&& (!tempContact.getContactId().equals(contact.getContactId()))) {
			validationMessage = new ValidationMessage("lastNm",
					"Duplicate contact", ValidationMessage.Severity.ERROR,
					"contact:" + contact.getContactId());
			validMessages.add(validationMessage);
			validationMessage = new ValidationMessage("firstNm",
					"Duplicate contact", ValidationMessage.Severity.ERROR,
					"contact:" + contact.getContactId());
			validMessages.add(validationMessage);
		}

		return validMessages.toArray(new ValidationMessage[0]);
	}

	/**
	 * @parm oldContactType
	 * @param contactType
	 * @param fpId
	 * @param userId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void modifyContactType(ContactType oldContactType,
			ContactType contactType, Integer fpId, int userId)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
//		InfrastructureHelper infraHelper = new InfrastructureHelper();
		infrastructureHelper.modifyContactType(oldContactType, contactType, fpId,
				userId, trans);
	}

	/**
	 * @param contactType
	 * @param fpId
	 * @param userId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void deleteContactType(ContactType contactType, Integer fpId,
			int userId) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
//		InfrastructureHelper infraHelper = new InfrastructureHelper();
		infrastructureHelper.deleteContactType(contactType, fpId, userId, trans);
	}

	/**
	 * @param contactType
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public ContactType addContactType(ContactType contactType, Integer fpId,
			int userId, String facilityId) throws DAOException {
		ContactType ret = null;
		Transaction trans = TransactionFactory.createTransaction();

		ContactDAO contactDAO = contactDAO(trans);

		try {

			contactDAO.addContactType(contactType.getContactId(),
					contactType.getContactTypeCd(), contactType.getStartDate(),
					facilityId);
			if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
				if (contactType.getContactTypeCd().equals(
								ContactTypeDef.BILL)) {
					Contact contact = contactDAO.retrieveContact(contactType
							.getContactId());

					List<EventLog> eventLogs = new ArrayList<EventLog>();
					EventLog eventLog = new EventLog();

					eventLog.setFpId(fpId);
					eventLog.setEventTypeDefCd(EventLogTypeDef.FAC_CHG);
					eventLog.setDate(new Timestamp(System.currentTimeMillis()));
					eventLog.setUserId(userId);
					/*if (contactType.getContactTypeCd().equals(
							ContactTypeDef.PRIM)) {
						eventLog.setNote("Primary contact changed to: ["
								+ contact.getName()
								+ "] as of: ["
								+ dateFormat.format(contactType.getStartDate())
										.toString() + "]");
					} else*/ if (contactType.getContactTypeCd().equals(
							ContactTypeDef.BILL)) {
						eventLog.setNote("Billing contact changed to: ["
								+ contact.getName()
								+ "] as of: ["
								+ dateFormat.format(contactType.getStartDate())
										.toString() + "]");
					}
					eventLogs.add(eventLog);

//					FacilityHelper facHelper = new FacilityHelper();
					for (EventLog el : eventLogs.toArray(new EventLog[0])) {
						facilityHelper.createEventLog(el);
					}
				}
			}
			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	@Override
	public ContactType addContactType(ContactType contactType, Integer fpId,
			int userId, String facilityId, Transaction trans)
			throws DAOException {
		ContactType ret = null;

		ContactDAO contactDAO = contactDAO(trans);

		contactDAO.addContactType(contactType.getContactId(),
				contactType.getContactTypeCd(), contactType.getStartDate(),
				facilityId);

		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {
			if ( contactType.getContactTypeCd().equals(
							ContactTypeDef.BILL)) {
				Contact contact = contactDAO.retrieveContact(contactType
						.getContactId());

				List<EventLog> eventLogs = new ArrayList<EventLog>();
				EventLog eventLog = new EventLog();

				eventLog.setFpId(fpId);
				eventLog.setEventTypeDefCd(EventLogTypeDef.FAC_CHG);
				eventLog.setDate(new Timestamp(System.currentTimeMillis()));
				eventLog.setUserId(userId);
				/*if (contactType.getContactTypeCd().equals(ContactTypeDef.PRIM)) {
					eventLog.setNote("Primary contact changed to: ["
							+ contact.getName()
							+ "] as of: ["
							+ dateFormat.format(contactType.getStartDate())
									.toString() + "]");
				} else*/ if (contactType.getContactTypeCd().equals(
						ContactTypeDef.BILL)) {
					eventLog.setNote("Billing contact changed to: ["
							+ contact.getName()
							+ "] as of: ["
							+ dateFormat.format(contactType.getStartDate())
									.toString() + "]");
				}
				eventLogs.add(eventLog);

//				FacilityHelper facHelper = new FacilityHelper();
				for (EventLog el : eventLogs.toArray(new EventLog[0])) {
					facilityHelper.createEventLog(el);
				}
			}
		}

		trans.complete();

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SccCode[] retrieveSccCodes(List<String> searchSccList)
			throws DAOException {
		return infrastructureDAO().retrieveSccCodes(searchSccList);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SccCode retrieveSccCode(String sccId) throws DAOException {
		return infrastructureDAO().retrieveSccCode(sccId);
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FieldAuditLog[] searchFieldAuditLog(FieldAuditLog searchFieldAuditLog)
			throws DAOException {
		return infrastructureDAO().searchFieldAuditLog(searchFieldAuditLog);
	}

	/**
	 * @param sqlLoadString
	 * @param objectToRetrieve
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public BaseDef[] retrieveBaseDB(String sqlLoadString, Class objectToRetrieve)
			throws DAOException {
		return infrastructureDAO().retrieveBaseDB(sqlLoadString,
				objectToRetrieve);
	}

	/**
	 * @param sqlLoadString
	 * @param objectToRetrieve
	 * @return SimpleIdDef
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SimpleIdDef[] retrieveBaseIdDB(String sqlLoadString,
			Class objectToRetrieve) throws DAOException {
		return infrastructureDAO().retrieveBaseIdDB(sqlLoadString,
				objectToRetrieve);
	}

	/**
	 * @param wrapnCD
	 * @return
	 * @throws DAOException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public WrapnDef retrieveWrapnDef(String wrapnCD) throws DAOException {
		return infrastructureDAO().retrieveWrapnDef(wrapnCD);
	}

	/**
	 * @param sequenceId
	 * @return
	 * @throws DAOException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Integer nextSequenceVal(String sequenceId) throws DAOException {
		Integer ret = null;
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			ret = infraDAO.nextSequenceIdValue(sequenceId);

			trans.complete();
		} catch (DAOException de) {
			cancelTransaction(trans, de);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public void incrementReferenceCount(String taskId, Transaction trans)
			throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			Task task = infraDAO.retrieveTask(taskId);
			switch (task.getTaskType()) {
			case FC:
			case FCH:
			case FCC:
				int refCount = task.getReferenceCount().intValue();

				// just increment the count and update task
				task.setReferenceCount(refCount + 1);
				infraDAO.modifyTask(task);
			}
		} catch (DAOException e) {
			throw e;
		}
	}

	// Returns false if the facility was deleted (reference count had been 1).
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public void decrementReferenceCount(String taskId, Transaction trans)
			throws RemoteException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		FacilityService facilityBO = null;
		int refCount;
		Task task = null;
		try {
			facilityBO = ServiceFactory.getInstance().getFacilityService();
			task = infraDAO.retrieveTask(taskId);
			switch (task.getTaskType()) {
			case FC:
			case FCH:
				refCount = task.getReferenceCount().intValue();
				if (refCount > 1) {
					// just decrement the count and update task
					task.setReferenceCount(refCount - 1);
					infraDAO.modifyTask(task);
				} else {
					// this is common to some types and it will delete task too.
					facilityBO.removeFacility(task.getFpId(), trans);
					decrementReferenceCount(task.getDependentTaskId(), trans);
				}
				break;
			case FCC:
				refCount = task.getReferenceCount().intValue();
				if (refCount > 1) {
					// just decrement the count and update task
					task.setReferenceCount(refCount - 1);
					infraDAO.modifyTask(task);
				} else {
					// this is common to some types and it will delete task too.
					facilityBO.removeFacilityContacts(task.getFacilityId(),
							trans);
					infraDAO.deleteTask(task.getTaskId());
				}
				break;
			}
		} catch (RemoteException e) {
			String error = "RemoteException in decrementReferenceCount for taskId "
					+ taskId
					+ " facility "
					+ task.getFacilityId()
					+ ". Message = " + e.getMessage();
			logger.error(error);
			throw e;
		} catch (ServiceFactoryException e) {
			// String error =
			// "ServiceFactoryException in decrementReferenceCount for taskId "
			// + taskId
			// + " facility "
			// + task.getFacilityId()
			// + ". Message = " + e.getMessage();
			// logger.error(error);
			// throw new RemoteException(error, e);
			throw new RemoteException("error", e);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Supports"
	 */
	public void decrementReferenceCountOnly(String taskId, Transaction trans)
			throws RemoteException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		int refCount;
		Task task = null;
		try {
			task = infraDAO.retrieveTask(taskId);
			switch (task.getTaskType()) {
			case FC:
				refCount = task.getReferenceCount().intValue();
				// just decrement the count and update task
				task.setReferenceCount(refCount - 1);
				infraDAO.modifyTask(task);
				break;
			case FCH:
			case FCC:
			default:
				logger.error("Task Type " + task.getTaskType()
						+ " not expected in decrementReferenceCountOnly.");
				break;
			}
		} catch (RemoteException e) {
			String error = "RemoteException in decrementReferenceCountOnly for taskId "
					+ taskId
					+ " facility "
					+ task.getFacilityId()
					+ ". Message = " + e.getMessage();
			logger.error(error);
			throw e;
		}
	}

	/**
	 * @return
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void deleteTask(Task task, boolean deleteAttachmentFiles) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO("Staging");
		infraDAO.setTransaction(trans);
		Task facTask = null;
		if (debugTaskDelete) {
			logger.error(
					"deleteTask() called" + task.getTaskId()
							+ task.getFacilityId() + "; "
							+ task.getTaskDescription() + "; "
							+ task.getTaskInternalId(), new Exception());
		}
		try {
			Task task1 = infraDAO.retrieveTask(task.getTaskId());
			if (task1 == null) {
				throw new DAOException("Task: " + task.getTaskId()
						+ " not found.");
			}

			if (task1.getDependent()) {
				facTask = infraDAO.retrieveTask(task1.getDependentTaskId());
				if (facTask == null) {
					throw new DAOException("Dependent Task: "
							+ task1.getDependentTaskId() + " not found.");
				}
			}

			deleteTask(task1, trans, deleteAttachmentFiles);
			// deleteDataSet(task1.getTaskId(), task1.getUserName());

			/*
			 * if the task is other than FC type, need to see if the FC dataset
			 * needs to be deleted only when no other task dependent on FC one
			 */

			if (facTask != null) {
				Task task2 = infraDAO.retrieveTask(facTask.getTaskId());
				if (task2 == null) {
					deleteDataSet(facTask.getTaskId(), facTask.getUserName());
				}
				if (facTask.getDependentTaskId() != null) {
					task2 = infraDAO.retrieveTask(facTask.getDependentTaskId());
					if (task2 == null) {
						deleteDataSet(facTask.getDependentTaskId(),
								facTask.getUserName());
					}
				}
			}
			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
			logger.error("Exception Task: " + task.getTaskId(), re);
		} finally { // Clean up our transaction stuff
			closeTransaction(trans);
		}
	}

	/*
	 * This is a wrapper to deleteTask to help instruct attachment-based DAO's
	 * whether to delete attachments (e.g. they *should* be deleted if the user
	 * deletes the parent record without submittal but should *not* be deleted
	 * if the user submits the task.
	 */
//	public void deleteTask(Task task, Transaction trans,
//			boolean deleteAttachmentFiles) throws DAOException {
//		this.deleteAttachmentFiles = deleteAttachmentFiles;
//		deleteTask(task, trans);
//	}

	/*
	 * This is a wrapper to deleteTask to help instruct attachment-based DAO's
	 * whether to delete attachments (e.g. they *should* be deleted if the user
	 * deletes the parent record without submittal but should *not* be deleted
	 * if the user submits the task.
	 */

	/**
	 * @return
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
//	public void deleteTask(Task task, boolean deleteAttachmentFiles)
//			throws DAOException {
//		this.deleteAttachmentFiles = deleteAttachmentFiles;
//		deleteTask(task);
//	}

	public void deleteTask(Task task, Transaction trans,
			boolean deleteAttachmentFiles) throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		FacilityService facilityBO = null;
		if (debugTaskDelete) {
			logger.error(
					"deleteTask() called" + task.getTaskId()
							+ task.getFacilityId() + "; "
							+ task.getTaskDescription() + "; "
							+ task.getTaskInternalId(), new Exception());
		}
		boolean ok = true;
		try {
			switch (task.getTaskType()) {
			case FC:
			case FCH:
				// it will delete task too.
				facilityBO = ServiceFactory.getInstance().getFacilityService();
				facilityBO.removeFacility(task.getFpId(), trans);
				break;
			case FCC:
				facilityBO = ServiceFactory.getInstance().getFacilityService();
				if (facilityBO.retrieveFacilityContacts(task.getFacilityId(),
						true).size() > 0) {
					facilityBO.removeFacilityContacts(task.getFacilityId(),
							trans);
				}
				infraDAO.deleteTask(task.getTaskId());
				break;
			case PBR:
			case PTPA:
			case COPY_PTPA:
			case RPC:
			case TVPA:
			case TIVPA:
			case COPY_TVPA:
				ApplicationService applicationBO = null;
				applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
				Application app = applicationBO.retrieveApplication(task
					.getTaskInternalId());
				ApplicationDetail ad = (ApplicationDetail) FacesUtil
				.getManagedBean("applicationDetail");				
				
				if (app != null) {
					applicationBO.removeApplicationDocuments(app, trans, deleteAttachmentFiles);
					applicationBO.removeApplication(app, trans);
					ad.setApplicationDeleted(true);
					
					// TODO find a way to determine if this is called from the
					// delete task operation or called task submission
					// try {
					// // delete all attachments associated with the
					// // application since there is no way for the document
					// cleanup
					// // daemon to find them otherwise
					// applicationBO.removeApplicationDocuments(app, trans);
					// } catch (DAOException e) {
					// logger.warn("Exception while attempting to remove " +
					// "documents for portal application: " +
					// app.getApplicationNumber(), e);
					// }
					infraDAO.deleteTask(task.getTaskId());
				} else {
					// this should never happen
					logger.error("Task " + task.getTaskId()
							+ " pointing to invalid application id");
				}
				break;
			case ER:
			case R_ER:
				EmissionsReportService eBO = ServiceFactory.getInstance()
						.getEmissionsReportService();
				Integer rptId = task.getTaskInternalId();
				EmissionsReport erRow = eBO.retrieveEmissionsReportRow(rptId,
						true);
				if (erRow == null) {
					ok = false;
				}
				if (ok) {
						EmissionsReport rpt = eBO.retrieveEmissionsReport(
								rptId, true);
						eBO.deleteEmissionsReport(rpt, trans, deleteAttachmentFiles);
					infraDAO.deleteTask(task.getTaskId());
				} else {
					DisplayUtil.displayError("Failed to read emissions inventory report "
							+ rptId);
				}
				break;
			case CR_TVCC:
			case CR_PER:
			case CR_OTHR:
			case CR_TEST:
			case CR_CEMS:
			case CR_SMBR:
			case CR_ONE:
			case CR_GENERIC:	
				ComplianceReportService crBO = ServiceFactory.getInstance()
						.getComplianceReportService();
				ComplianceReport compReport = crBO.retrieveComplianceReport(
						task.getTaskInternalId());
				ComplianceReports cr = (ComplianceReports) FacesUtil
						.getManagedBean("complianceReport");

				if (compReport != null) {
					crBO.deleteComplianceReport(compReport, trans, deleteAttachmentFiles);
					cr.setComplianceReportDeleted(true);
					infraDAO.deleteTask(task.getTaskId());
				} else {
					// this should never happen
					logger.error("Task " + task.getTaskId()
							+ " pointing to invalid compliance report id");
				}
				break;
			case REL:
				RelocateRequestService relocateBO = ServiceFactory
						.getInstance().getRelocateRequestService();
				RelocateRequest relocateRequest = relocateBO
						.retrieveRelocateRequest(task.getTaskInternalId(),
								task.getFacilityId());
				relocateBO.deleteRelocateRequest(relocateRequest, false);
				infraDAO.deleteTask(task.getTaskId());
				relocateBO.RemoveRelocationRequestDirs(relocateRequest,
						task.getFacilityId(), deleteAttachmentFiles);
				break;
			case ST:
				StackTestService stBO = ServiceFactory.getInstance()
						.getStackTestService();
				StackTest stackTest = stBO.retrieveStackTest(
						task.getTaskInternalId(), false);                 // PORTAL REFACTOR opportunity
				//stBO.deleteStackTest(stackTest, trans,                  //    make sure we don't need to pass trans
				//		deleteAttachmentFiles);
				stBO.deleteStackTest(task.getFacility(), stackTest, false, deleteAttachmentFiles);
				infraDAO.deleteTask(task.getTaskId());
				break;
			case MRPT:
				MonitoringService monBO = ServiceFactory.getInstance().getMonitoringService();
				MonitorReport[] monitorReports = null;
				
				monitorReports = monBO.retrieveMonitorReports(task.getTaskInternalId(), true);
	
				if(null != monitorReports) {
					for(MonitorReport mr : monitorReports) {
						if(mr.isStaging()) {
							monBO.deleteMonitorReport(mr, deleteAttachmentFiles);
						}
					}
				}
				infraDAO.deleteTask(task.getTaskId());
				break;
			}

			if (ok) {
				if (task.getDependent() && task.getDependentTaskId() != null) {
					decrementReferenceCount(task.getDependentTaskId(), trans);
				}
			}
		} catch (ServiceFactoryException sfe) {
			logger.error(sfe);
			throw new DAOException(sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			logger.error("Exception taskId " + task.getTaskId() + ", facility "
					+ task.getFacilityId(), re);
			throw new DAOException("Exception taskId " + task.getTaskId()
					+ ", facility " + task.getFacilityId(), re);
		}
	}

	/**
	 * @param filterTask
	 * @return
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Task[] retrieveTasks(Task filterTask) throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO("Staging");
		Task[] tasks = infraDAO.retrieveTasks(filterTask);
		boolean refreshNeeded = false;
		// check to see if task has already completed
		for (Task task : tasks) {
			if (!"0".equals(task.getTaskName()) && task.getTaskType() != null) {
				logger.error("Checking task " + task.getTaskId()
						+ " to see if it has been submitted");
				refreshNeeded = checkForSubmittedTask(task);
			}
		}

		// refresh list of tasks if one or more have been deleted.
		if (refreshNeeded) {
			tasks = infraDAO.retrieveTasks(filterTask);
		}
		return tasks;
	}

	private boolean checkForSubmittedTask(Task task) throws DAOException {
		boolean refreshNeeded = false;
		if (task.getTaskType().toString().endsWith("PA")
				|| Task.TaskType.PBR.equals(task.getTaskType())
				|| Task.TaskType.RPC.equals(task.getTaskType())) {
			// Permit applications
			logger.error("Checking Application Task");
			int applicationId = task.getTaskInternalId();
			ApplicationService applicationBO = null;
			try {
				applicationBO = ServiceFactory.getInstance()
						.getApplicationService();
				Application portalApp = applicationBO
						.retrieveApplication(applicationId);
				if (portalApp != null) {
					// check for existence of application in internal DB
					Application internalApp = applicationBO
							.retrieveApplicationSummary(portalApp
									.getApplicationNumber());
					if (internalApp != null) {
						logger.error("In-progress application "
								+ portalApp.getApplicationNumber()
								+ " exists in the internal system. Its task will be removed from IMPACT.");
						removeSubmittedTask(task);
					} else {
						logger.error("No internal application was found with number "
								+ portalApp.getApplicationNumber());
					}
				} else {
					logger.error("No application found with id = "
							+ applicationId);
				}
				refreshNeeded = true;
			} catch (ServiceFactoryException sfe) {
				logger.error("Caught a serviceFacoryException: message = "
						+ sfe.getMessage(), sfe);
			} catch (RemoteException e) {
				logger.error("Exception while retrieving application with id "
						+ applicationId, e);
			}
		} else if (task.getTaskType().toString().startsWith("CR")) {
			// Compliance reports
			logger.error("Checking Compliance Report Task");
			int reportId = task.getTaskInternalId();
			ComplianceReportService complianceBO = null;
			try {
				complianceBO = ServiceFactory.getInstance()
						.getComplianceReportService();
				ComplianceReport portalReport = complianceBO
						.retrieveComplianceReport(reportId, false);
				if (portalReport != null) {
					// check for existence of compliance report in internal DB
					ComplianceReport internalReport = complianceBO
							.retrieveComplianceReport(reportId, true);
					if (internalReport != null) {
						logger.error("In-progress compliance report "
								+ reportId
								+ " exists in the internal system. Its task will be removed from IMPACT.");
						removeSubmittedTask(task);
					} else {
						logger.error("No internal compliance report was found with id =  "
								+ reportId);
					}
				} else {
					logger.error("No compliance report found with id = "
							+ reportId);
				}
				refreshNeeded = true;
			} catch (ServiceFactoryException sfe) {
				logger.error("Caught a serviceFacoryException: message = "
						+ sfe.getMessage(), sfe);
			} catch (RemoteException e) {
				logger.error(
						"Exception while retrieving compliance report with id "
								+ reportId, e);
			}
		} else if (task.getTaskType().toString().endsWith("ER")) {
			// Emissions Inventory
			logger.error("Checking Emissions Inventory Task");
			int reportId = task.getTaskInternalId();
			EmissionsReportService erBO = null;
			try {
				erBO = ServiceFactory.getInstance().getEmissionsReportService();
				EmissionsReport portalReport = erBO.retrieveEmissionsReport(
						reportId, true);
				if (portalReport != null) {
					// check for existence of application in internal DB
					EmissionsReport internalReport = erBO
							.retrieveEmissionsReport(reportId, false);
					if (internalReport != null) {
						logger.error("In-progress emissions inventory "
								+ reportId
								+ " exists in the internal system. Its task will be removed from IMPACT.");
						removeSubmittedTask(task);
					} else {
						logger.error("No internal emissions inventory was found with id =  "
								+ reportId);
					}
				} else {
					logger.error("No emissions inventory found with id = "
							+ reportId);
				}
				refreshNeeded = true;
			} catch (ServiceFactoryException sfe) {
				logger.error("Caught a serviceFacoryException: message = "
						+ sfe.getMessage(), sfe);
			} catch (RemoteException e) {
				logger.error(
						"Exception while retrieving emissions inventory with id "
								+ reportId, e);
			}
		} else if (Task.TaskType.ST.equals(task.getTaskType())) {
			// Stack Tests
			//logger.debug("Checking Stack Test Task");
			int stackTestId = task.getTaskInternalId();
			StackTestService stackTestBO = null;
			try {
				stackTestBO = ServiceFactory.getInstance()
						.getStackTestService();
				StackTest portalSt = stackTestBO.retrieveStackTest(stackTestId,
						false);
				if (portalSt != null) {
					// check for existence of stack test in internal DB
					StackTest internalSt = stackTestBO.retrieveStackTest(
							portalSt.getId(), true);
					if (internalSt != null) {
						logger.debug("In-progress stack test "
								+ portalSt.getId()
								+ " exists in the internal system. Its task will be removed from IMPACT.");
						removeSubmittedTask(task);
					} else {
						logger.debug("No internal stack test was found with number "
								+ portalSt.getId());
					}
				} else {
					logger.error("No stack test found with id = " + stackTestId);
				}
				refreshNeeded = true;
			} catch (ServiceFactoryException sfe) {
				logger.error("Caught a serviceFacoryException: message = "
						+ sfe.getMessage(), sfe);
			} catch (RemoteException e) {
				logger.error("Exception while retrieving stack test with id "
						+ stackTestId, e);
			}

		} else { 
			logger.error("Not checking for submitted task for "
					+ task.getTaskType());
		}
		return refreshNeeded;
	}

	/**
	 * @param taskId
	 * @return
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Task retrieveTask(String taskId) throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO("Staging");
		return infraDAO.retrieveTask(taskId);
	}

	/**
	 * @return Task
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Task createTask(Task newTask) throws DAOException {
		Transaction trans = null;
		Task ret = null;

		try {
			trans = TransactionFactory.createTransaction();
			ret = createTask(newTask, trans);
			trans.complete();
		} catch (ServiceFactoryException sfe) {
			throw new DAOException(sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	private Task createNewTask(String taskDesc, Task.TaskType taskType,
			boolean taskDepend, String taskDependId, String facId,
			Integer fpId, String version, Integer corePlaceId, String loginName) {
		Task newTask = new Task();

		newTask.setTaskDescription(taskDesc);
		newTask.setTaskType(taskType);
		newTask.setDependent(taskDepend);
		newTask.setDependentTaskId(taskDependId);
		newTask.setVersion(version);
		newTask.setFacilityId(facId);
		newTask.setFpId(fpId);
		newTask.setCorePlaceId(corePlaceId);
		newTask.setUserName(loginName);

		return newTask;
	}

	/**
	 * @return Facility Task and dependent Contact
	 * @throws ServiceFactoryException
	 * @throws RemoteException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Task createFacilityTask(Task newTask, Transaction trans)
			throws DAOException {
		Task filterTask = new Task();
		filterTask.setFacilityId(newTask.getFacilityId());
		filterTask.setCorePlaceId(newTask.getCorePlaceId());
		Task contactTask = null;
		Task ret = null;
		try {
			Task[] tasks1 = retrieveTasks(filterTask);
			for (Task task : tasks1) {
				if (task.getTaskType().equals(Task.TaskType.FCC)) {
					task.setFacility(newTask.getFacility()); // so facility name
																// will appear
																// with task
																// description
					contactTask = task;
					break;
				}
			}
			if (contactTask == null) {
				contactTask = createNewTask("Facility Contact Change",
						Task.TaskType.FCC, false, null,
						newTask.getFacilityId(), null, "current",
						newTask.getCorePlaceId(), newTask.getUserName());
				ret = createTask(newTask, contactTask, trans);
			} else {
				boolean facTaskFound = false;
				if (newTask.getTaskType().equals(Task.TaskType.FC)) {
					for (Task task : tasks1) {
						if (task.getTaskType().equals(Task.TaskType.FC)) {
							facTaskFound = true;
							break;
						}
					}
				} else if (newTask.getTaskType().equals(Task.TaskType.FCH)) {
					for (Task task : tasks1) {
						if (task.getTaskType().equals(Task.TaskType.FCH)
								&& task.getFpId().equals(newTask.getFpId())) {
							facTaskFound = true;
							break;
						}
					}
				}
				if (!facTaskFound) {
					newTask.setDependentTaskId(contactTask.getTaskId());
					ret = createTask(newTask, trans, true);
				}
			}
		} catch (ServiceFactoryException sfe) {
			throw new DAOException(sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			throw new DAOException(re.getMessage(), re);
		}
		return ret;
	}

	/**
	 * @return Task
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Task createFacilityTask(Task newTask) throws DAOException {
		Transaction trans = null;
		Task ret = null;
		try {
			trans = TransactionFactory.createTransaction();
			ret = createFacilityTask(newTask, trans);
			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	private Task createTask(Task newTask, Task depFacTask, Transaction trans)
			throws DAOException {
		Task ret = null;
		Task retFacTask = null;
		try {
			if (depFacTask.getTaskId() == null) {
				if (!depFacTask.getTaskType().equals(Task.TaskType.FCC)) {
					if (depFacTask.getDependentTaskId() == null) {
						Task contactTask = createNewTask(
								"Facility Contact Change", Task.TaskType.FCC,
								false, null, depFacTask.getFacilityId(), null,
								"current", depFacTask.getCorePlaceId(),
								depFacTask.getUserName());
						contactTask.setReferenceCount(0);
						contactTask.setFacility(newTask.getFacility());
						Task retContTask = createTask(contactTask, trans);
						depFacTask.setDependentTaskId(retContTask.getTaskId());
					}
				}
				retFacTask = createTask(depFacTask, trans);
				depFacTask.setTaskId(retFacTask.getTaskId());
			}
			newTask.setDependentTaskId(depFacTask.getTaskId());
			ret = createTask(newTask, trans);
		} catch (ServiceFactoryException sfe) {
			throw new DAOException(sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			throw new DAOException(re.getMessage(), re);
		} finally {
			// closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @return Task
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Task createTask(Task newTask, Task depFacTask) throws DAOException {
		Transaction trans = null;
		Task ret = null;
		try {
			trans = TransactionFactory.createTransaction();
			ret = createTask(newTask, depFacTask, trans);
			trans.complete();
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			closeTransaction(trans);
		}

		return ret;
	}

	/**
	 * @throws DAOException
	 * @throws RemoteException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public void deleteDataSet(String dataSetId, String userId)
			throws RemoteException {
		if (debugTaskDelete) {
			logger.error("deleteDataSet() called dataSetId=" + dataSetId,
					new Exception());
		}
	}

	/**
	 * @return Task
	 * @throws ServiceFactoryException
	 * @throws RemoteException
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Task createTask(Task newTask, Transaction trans)
			throws ServiceFactoryException, RemoteException {
		return createTask(newTask, trans, true);
	}

	private Task createTask(Task newTask, Transaction trans, boolean creObj)
			throws ServiceFactoryException, RemoteException {
		Task ret = null;
		Application app = null;
		ApplicationService applicationBO = null;

		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		// later set values based on type
		// String datasetType = Constants.DATASET_TYPE_DAPC_FACILITY_PROFILE;
		// String datasetStatus = Constants.DATASET_STATUS_EDIT;
		// String datasetAssociation =
		// Constants.DATASET_ASSOCIATION_TYPE_FACILITY;
		FacilityService facilityBO;

		switch (newTask.getTaskType()) {
		case FC:
		case FCH:
			// create Facility in the staging area if needed
			facilityBO = ServiceFactory.getInstance().getFacilityService();
			newTask.setFpId(facilityBO.createStagingFacilityProfile(
					newTask.getFpId(), trans));
			newTask.setFacility(facilityBO.retrieveFacility(newTask.getFpId())); // so
																					// facility
																					// name
																					// will
																					// appear
																					// with
																					// task
																					// description
																					// datasetType
																					// =
																					// Constants.DATASET_TYPE_DAPC_FACILITY_PROFILE;
			break;
		case FCC:
			if (creObj) {
				facilityBO = ServiceFactory.getInstance().getFacilityService();
				facilityBO.createStagingFacilityContacts(
						newTask.getFacilityId(), trans);
			}
			facilityBO = ServiceFactory.getInstance().getFacilityService();
			newTask.setFacility(facilityBO.retrieveFacilityData(
					newTask.getFacilityId(), -1)); // so facility name will
													// appear with task
													// description
			break;

		case PBR:
		case PTPA:
		case RPC:
		case TVPA:
		case TIVPA:
		case DOR:
			applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			app = applicationBO.createStagingApplication(
					newTask.getApplication(), newTask.getFpId(), trans);
			newTask.setTaskInternalId(app.getApplicationID());
			newTask.setReferenceCount(0);
			newTask.setTaskDescription(newTask.getTaskDescription() + " ("
					+ app.getApplicationNumber() + ")");
			break;
		case COPY_PTPA:
		case COPY_TVPA:
			applicationBO = ServiceFactory.getInstance()
					.getApplicationService();
			Application oldApp = newTask.getApplication();
			// load all document information for application to be copied
			// this is needed to ensure that attachments added after the
			// submission
			// date are not copied to the new application (Mantis 2635)
			applicationBO.loadAllDocuments(oldApp, true);
			app = applicationBO.createApplicationCopy(oldApp,
					oldApp.isApplicationCorrected(),
					oldApp.getApplicationCorrectedReason(),
					oldApp.isApplicationAmended(), trans);
			newTask.setTaskInternalId(app.getApplicationID());
			newTask.setReferenceCount(0);
			newTask.setTaskDescription(newTask.getTaskDescription() + " ("
					+ app.getApplicationNumber() + ")");
			break;
		case ER:
		case R_ER:
			EmissionsReportService erBO = ServiceFactory.getInstance()
					.getEmissionsReportService();
			erBO.performTask(newTask, trans);
			newTask.setReferenceCount(0);
			newTask.setTaskDescription(newTask.getTaskDescription());
			break;
		case CR_OTHR:
		case CR_TVCC:
		case CR_PER:
		case CR_TEST:
		case CR_CEMS:
		case CR_SMBR:
		case CR_ONE:
		case CR_GENERIC:	
			ComplianceReportService complianceReportBO = ServiceFactory
					.getInstance().getComplianceReportService();
			logger.debug("creating comliance report");
			ComplianceReport complianceReport = complianceReportBO
					.createComplianceReport(newTask.getComplianceReport(),
							newTask.getFacility());
			logger.debug("CR created. editing task description...");
			newTask.setTaskDescription(newTask.getTaskDescription() + " ("
					+ complianceReport.getReportId() + ")");
			newTask.setTaskInternalId(complianceReport.getReportId());
			newTask.setReferenceCount(0);
			logger.debug("About to break");
			break;

		case REL:
			RelocateRequestService relocateBO;
			relocateBO = ServiceFactory.getInstance()
					.getRelocateRequestService();
			newTask.getRelocateRequest().setFacility(newTask.getFacility());
			RelocateRequest relReq = relocateBO.createRelocateRequest(newTask
					.getRelocateRequest());
			newTask.setRelocateRequest(relReq);
			newTask.setTaskInternalId(relReq.getRequestId());
			newTask.setReferenceCount(0);
			newTask.setTaskDescription(newTask.getTaskDescription() + " ("
					+ relReq.getApplicationNumber() + ")");
			break;
		case MRPT:
//			StackTestService stackTestBO = ServiceFactory.getInstance()
//			.getStackTestService();
//			StackTest stackTest = stackTestBO.createStagingStackTest(
//					newTask.getStackTest(), newTask.getFpId(), trans);
//			newTask.setTaskInternalId(stackTest.getId());
			newTask.setReferenceCount(0);
//			newTask.setTaskDescription(newTask.getTaskDescription() + " ("
//					+ stackTest.getId() + ")");
			break;
		case ST:
			StackTestService stackTestBO = ServiceFactory.getInstance()
			.getStackTestService();
			StackTest stackTest = stackTestBO.createStagingStackTest(
					newTask.getStackTest(), newTask.getFpId(), trans);
			newTask.setTaskInternalId(stackTest.getId());
			newTask.setReferenceCount(0);
			newTask.setTaskDescription(newTask.getTaskDescription() + " ("
					+ stackTest.getId() + ")");
			break;
		}

		if (newTask.getCorePlaceId() == null) { // check for problem
			logger.debug("newTask.getCorePlaceId() == null");
		}

		logger.debug(" Creating new data set for user: "
				+ newTask.getUserName());
		String id = new Integer(kludgeDennis++).toString();

		if (id != null) {
			try {
				newTask.setTaskId(id);
				newTask.setCreateDate(new Timestamp(System.currentTimeMillis()));
				ret = infraDAO.createTask(newTask);
				if (newTask.getDependentTaskId() != null && creObj) {
					incrementReferenceCount(newTask.getDependentTaskId(), trans);
				}
			} catch (DAOException de) {
				throw de;
			}
		} else {
			throw new DAOException("Failed to get Task ID from service");
		}
		logger.debug("Done creating task object. returning " + ret);
		return ret;
	}

	private void removeTaskUpdateDepTasks(Task task, Transaction trans)
			throws RemoteException {
		InfrastructureDAO infraDAO = infrastructureDAO("Staging");
		infraDAO.setTransaction(trans);
		try {
			infraDAO.deleteTask(task.getTaskId());
			deleteDataSet(task.getTaskId(), task.getUserName());

			String oldTaskId = task.getTaskId();
			task.setTaskId(null);
			Task newTask = createTask(task, trans, false);
			infraDAO.updateTasksDepTaskId(oldTaskId, newTask.getTaskId());
		} catch (ServiceFactoryException sfe) {
			throw new DAOException(sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			throw re;
		}
	}

	private void removeSubmittedTask(Task task) throws RemoteException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			removeSubmittedTask(task, trans);
			trans.complete();
		} catch (RemoteException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
	}

	private void removeSubmittedTask(Task task, Transaction trans)
			throws RemoteException {
		logger.debug(" removing submitted task: " + task.getTaskId() + " ...");

		InfrastructureDAO infraDAO = infrastructureDAO("Staging");
		infraDAO.setTransaction(trans);
		Task task1 = infraDAO.retrieveTask(task.getTaskId());
		if (task1 == null) {
			throw new DAOException("Task: " + task.getTaskId() + " not found.");
		}

		Task facTask = null;
		Task contactTask = null;
		Task task2 = null;

		if (task1.getDependent()) {
			switch (task1.getTaskType()) {
			case FC:
			case FCH:
				contactTask = infraDAO.retrieveTask(task1.getDependentTaskId());
				if (contactTask == null) {
					throw new DAOException("Dependent Task: "
							+ task1.getDependentTaskId() + " not found.");
				}

				if (!task1.getReferenceCount().equals(0)) {
					// must delete dependent task and dataset and create a
					// new task and dataset
					// update dependent task id in other tasks
					removeTaskUpdateDepTasks(task1, trans);
				} else {
					deleteTask(task1, trans, false);
					// deleteDataSet(task1.getTaskId(), task1.getUserName());
				}

				/*
				 * We need to see if the Contact dataset needs to be deleted
				 * only when no other task dependent on contact one
				 */

				task2 = infraDAO.retrieveTask(contactTask.getTaskId());
				if (task2 == null) {
					deleteDataSet(contactTask.getTaskId(),
							contactTask.getUserName());
				} else {
					removeTaskUpdateDepTasks(task2, trans);
				}

				break;
			default:
				facTask = infraDAO.retrieveTask(task1.getDependentTaskId());
				if (facTask == null) {
					throw new DAOException("Dependent Task: "
							+ task1.getDependentTaskId() + " not found.");
				}

				deleteTask(task1, trans, false);
				deleteDataSet(task1.getTaskId(), task1.getUserName());

				/*
				 * if the task is other than FC type, need to see if the FC
				 * dataset needs to be deleted only when no other task dependent
				 * on FC one
				 */

				if (facTask != null) {
					task2 = infraDAO.retrieveTask(facTask.getTaskId());
					if (task2 == null) {
						deleteDataSet(facTask.getTaskId(),
								facTask.getUserName());
					} else {
						// must delete dependent task and dataset and create a
						// new task and dataset
						// update dependent task id in other tasks
						removeTaskUpdateDepTasks(task2, trans);
					}
					if (facTask.getDependentTaskId() != null) {
						task2 = infraDAO.retrieveTask(facTask
								.getDependentTaskId());
						if (task2 == null) {
							deleteDataSet(facTask.getDependentTaskId(),
									facTask.getUserName());
						} else {
							// must delete dependent task and dataset and create
							// a
							// new task and dataset
							// update dependent task id in other tasks
							removeTaskUpdateDepTasks(task2, trans);
						}
					}
				}
			}
		} else {
			//
			if ((task.getTaskType().equals(Task.TaskType.CR_OTHR))
					|| (task.getTaskType().equals(Task.TaskType.CR_TEST))
					|| (task.getTaskType().equals(Task.TaskType.CR_CEMS))
					|| (task.getTaskType().equals(Task.TaskType.CR_SMBR))
					|| (task.getTaskType().equals(Task.TaskType.CR_PER))
					|| (task.getTaskType().equals(Task.TaskType.CR_TVCC))
					|| (task.getTaskType().equals(Task.TaskType.CR_ONE))
					|| (task.getTaskType().equals(Task.TaskType.CR_GENERIC))
					|| task.getNtvReport() != null
					|| (task.getTaskType().equals(Task.TaskType.REL))
					|| (task.getTaskType().equals(Task.TaskType.MRPT))
					) {
				deleteTask(task1, trans, false); 
				//deleteDataSet(task1.getTaskId(), task1.getUserName());
			} else if (task.getTaskType().equals(Task.TaskType.FCC)) {
				if (task.getReferenceCount().equals(0)) {
					deleteTask(task1, trans, false);
					// deleteDataSet(task1.getTaskId(), task1.getUserName());
				} else {
					// must delete dependent task and dataset and create a
					// new task and dataset
					// update dependent task id in other tasks
					removeTaskUpdateDepTasks(task1, trans);
					return;
				}
			}
		}
		logger.debug(" Done removing submitted task: " + task.getTaskId());
	}

	/**
	 * @return BulkDef
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public String submitTask(Task task, String userId, String pin,
			String securityQuesAns) throws DAOException {

		byte[] xmlData;
		String ret = null;
		Transaction trans = TransactionFactory.createTransaction();
		Task contactTask = null;
		Task facTask = null;
		task.setNewFpId(null);
		Task readTask = null;

		readTask = retrieveTask(task.getTaskId());
		if (readTask == null) {
			throw new DAOException("Submitted Task ID: " + task.getTaskId()
					+ " not found.");
		}

		if (readTask.getDependent()) {
			switch (readTask.getTaskType()) {
			case FC:
			case FCH:
				contactTask = retrieveTask(readTask.getDependentTaskId());
				if (contactTask == null) {
					throw new DAOException("Contact Task ID: "
							+ readTask.getDependentTaskId() + " not found.");
				}
				break;
			default:
				facTask = retrieveTask(readTask.getDependentTaskId());
				if (facTask == null) {
					throw new DAOException("Facility Task ID: "
							+ readTask.getDependentTaskId() + " not found.");
				}
				contactTask = retrieveTask(facTask.getDependentTaskId());
				if (contactTask == null) {
					throw new DAOException("Contact Task ID: "
							+ facTask.getDependentTaskId() + " not found.");
				}
			}
		}


		ImpactPortalClient portalClient = new ImpactPortalClient();

		if (userId.equals("dapc2") && !pin.equals("PIN-4545")) {
			throw new DAOException("The pin entered is not valid for account");
		}
		try {
			if (task.getDependent()) {
				FacilityService facilityBO = ServiceFactory.getInstance()
						.getFacilityService();
				contactTask.setFacility(null);
				contactTask.setFacilityContacts(facilityBO
						.retrieveFacilityContacts(contactTask.getFacilityId(),
								true));
				List<Contact> submittedContacts = contactTask
						.getFacilityContacts();
				for (Contact submittedContact : submittedContacts) {
					System.out.println("Submitted Contact Id: "
							+ submittedContact.getCntId() + " Contact Name: "
							+ submittedContact.getName());
					logger.debug("Submitted Contact Id: "
							+ submittedContact.getCntId() + " Contact Name: "
							+ submittedContact.getName());
				}

				String contactDataSubmitId = "PS:"
						+ contactTask.getFacilityId() + ":"
						+ contactTask.getTaskId() + ":"
						+ contactTask.getTaskName();

				contactTask.setGatewaySubmiterUserNm(userId);
				contactTask.setSubmissionId(contactDataSubmitId);
				xmlData = contactTask.toXMLStream();
				portalClient.processSubmittedTask(xmlData);

				if (facTask != null) {
					Boolean submitFac = true;
					facTask.setFacility(task.getFacility());

					logger.debug(" submit task type = " + facTask.getTaskType());
					switch (facTask.getTaskType()) {
					case FCH:
						Facility dapcFac = facilityDAO("ReadOnly")
								.retrieveFacility(facTask.getFpId());
						Facility gateWayFacility = task.getFacility();
						if (gateWayFacility.getLastSubmissionType().equals(
								dapcFac.getLastSubmissionType())
								&& (gateWayFacility.getLastSubmissionVersion()
										.equals(dapcFac
												.getLastSubmissionVersion()))) {
							task.setNewFpId(task.getFpId());
							submitFac = false;
						} else {
							Integer newFpId = nextSequenceVal("S_Fp_Id");
							facTask.setNewFpId(newFpId);
							task.setNewFpId(newFpId);
						}
						break;
					default:
						facTask.setNewFpId(null);
					}

					if (submitFac) {
						String dataSubmitId = "PS:" + task.getFacilityId()
								+ ":" + task.getTaskId() + ":"
								+ task.getTaskName();
						facTask.setGatewaySubmiterUserNm(userId);
						facTask.setSubmissionId(dataSubmitId);

						xmlData = facTask.toXMLStream();
						saveXmlDataInFile(xmlData, facTask, dataSubmitId);
						portalClient.processSubmittedTask(xmlData);
						task.setFacility(null);
					}
				}
			} else {
				// make sure facility is null since it is set to have name
				// in ID for Portal when task created.
				switch (task.getTaskType()) {
				case FCC:
				case CR_OTHR:
				case CR_TEST:
				case CR_CEMS:
				case CR_SMBR:
				case CR_TVCC:
				case CR_PER:
				case CR_ONE:
				case REL:
				case MRPT:
				case CR_GENERIC:	
				//case ST:       // Is this needed?  What is the significance of this, and why don't
					             // we do this for applications and Emissions Inventory.
					             // TO DO: Confirm if this line should be removed
					task.setFacility(null);
				}
			}
			String dataSubmitId = "PS:" + task.getFacilityId() + ":"
					+ task.getTaskId() + ":" + task.getTaskName();
			task.setGatewaySubmiterUserNm(userId);
			task.setSubmissionId(dataSubmitId);
			
			// don't want to serialize testedPollutantWrapper in stack test
			// marking the attribute as transient is not working for reason not known yet
			// once that is found and fixed, below if block can be removed.
			if(task.getTaskType() ==TaskType.ST) {
				task.getStackTest().setTestedPollutantsWrapper(null);
			}
			
			xmlData = task.toXMLStream();
			portalClient.processSubmittedTask(xmlData);
			
			removeSubmittedTask(task, trans);
            
			trans.complete();
		} catch (ServiceFactoryException sfe) {
			cancelTransaction(trans, new DAOException(sfe.getMessage(), sfe));
		} catch (RemoteException re) {
			cancelTransaction(trans, re);
		} finally {
			CompMgr.setAppName(CommonConst.EXTERNAL_APP);
			closeTransaction(trans);
		}

		logger.debug(" end submitTask for task id " + task.getTaskId());
		return ret;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 * @param task
	 * @throws DAOException
	 */
	public void incrementTaskCounter(Task task) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		logger.debug(" incrementTaskCounter taskId=" + task.getTaskId()
				+ " start taskName=" + task.getTaskName());
		try {
			if (task.getDependent()) {
				Task facTask = retrieveTask(task.getDependentTaskId());
				incrementTaskCounter(facTask);
			}
			incrementTaskCounter(task, trans);
			// trans.complete();
			logger.debug(" incrementTaskCounter taskId=" + task.getTaskId()
					+ " end taskName=" + task.getTaskName());
		} catch (RemoteException e) {
			cancelTransaction(trans, e);
		} catch (ServiceFactoryException sfe) {
			cancelTransaction(trans, new RemoteException(sfe.getMessage(), sfe));
		}

	}

	/**
	 * @return getMetaData
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Hashtable<String, DataGridCellDef> getMetaData(String table)
			throws DAOException {
		return infrastructureDAO().getMetaData(table);
	}

	/**
	 * @return insertAdHoc
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean insertAdHoc(DataGridRow dgr, String table,
			DataGridRow dgrDefinition) throws DAOException {
		return adHocDAO().insert(dgr, table, dgrDefinition);
	}

	/**
	 * @return updateAdHoc
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean updateAdHoc(DataGridRow dgr, String table,
			DataGridRow dgrDefinition) throws DAOException {
		return adHocDAO().update(dgr, table, dgrDefinition);
	}

	/**
	 * @return retrievePickList
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<SelectItem> retrievePickList(DataGridRow dgrDefinition,
			String field) throws DAOException {
		return adHocDAO().retrievePickList(dgrDefinition, field);
	}

	/**
	 * @return retrieveDataSet
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public DataSet retrieveDataSet(DataSet ds) throws DAOException {
		return adHocDAO().retrieve(ds);
	}

	private void incrementTaskCounter(Task task, Transaction trans)
			throws RemoteException, ServiceFactoryException {
		int taskNo = 0;
		try {
			taskNo = Integer.parseInt(task.getTaskName());
		} catch (NumberFormatException nfe) {
			// log error and default to 0
			logger.error("Invalid value for task name: " + task.getTaskName());
		}
		task.setTaskName(new String("" + (taskNo + 1)));
		modifyTask(task, trans);
	}

	private String encryptPassword(String password) {
		String ret = password;
		String encryptionAlgorithm;
		boolean encrypt;

		ConfigManager cfgMgr = ConfigManagerFactory.configManager();
		Node root = cfgMgr.getNode("app.authentication");

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

		try {
			if (encrypt) {
				MessageDigest md = MessageDigest
						.getInstance(encryptionAlgorithm);

				byte[] temp = md.digest(password.getBytes());
				ret = new String(Base64.encodeBase64(temp));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return ret;
	}

	/**
	 * @return
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="RequiresNew"
	 */
	public void processSubmittedTask(byte[] fromXmlTask) throws DAOException {

		logger.debug(" Starting processing.");
		
		Transaction trans = TransactionFactory.createTransaction();
		ByteArrayInputStream fromXmlInput = new ByteArrayInputStream(
				fromXmlTask);
		FacilityService facilityBO = null;
		EmissionsReportService erBO = null;
		Facility facility = null;
		Task fromTask = null;

		try {

			facilityBO = ServiceFactory.getInstance().getFacilityService();
			fromTask = (Task) BaseDB.fromXMLStream(fromXmlInput);
			if (fromTask == null) {
				throw new DAOException("Unable to get task from from xml data.");
			}

			FacilityDAO facilityDAO = facilityDAO(trans);

			if (fromTask.getFacility() != null) {
				logger.debug(" Calling createFacilityProfileFromGateWay: new Fp ID = "
						+ fromTask.getNewFpId() + ".");
				facilityBO.createFacilityProfileFromGateWay(
						fromTask.getFacility(), fromTask.getNewFpId(), trans);
			}

			if (fromTask.getFacilityContacts() != null) {
				logger.debug(" Calling updateFacilityContactFromGateWay for facilityId="
						+ fromTask.getFacilityId());
				facilityBO.updateFacilityContactFromGateWay(
						fromTask.getFacilityId(), fromTask.getCorePlaceId(),
						fromTask.getFacilityContacts(), trans);

				// add attestation document to facility (if there is one)
				if (fromTask.getAttestationDoc() != null) {
					logger.debug(" loading attestation document for contact change request. Doc id = "
							+ fromTask.getAttestationDoc().getDocumentID());
					us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment facilityAttachment = (us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment) documentDAO()
							.createDocument(fromTask.getAttestationDoc());
					facilityAttachment = facilityDAO
							.createFacilityAttachment(facilityAttachment);
				} else {
					logger.debug(" No attestation document found for contact change request.");
				}
			}

			if (fromTask.getApplication() != null) {
				logger.debug(" Calling createApplicationFromGateway.  Application "
						+ fromTask.getApplication().getApplicationNumber());
				facility = facilityBO
						.retrieveFacility(fromTask.getFacilityId());
				ApplicationService appBO = ServiceFactory.getInstance()
						.getApplicationService();
				appBO.createApplicationFromGateway(fromTask.getApplication(),
						facility, trans);
				logger.debug(" Finished creating the application internally");
			}

			if (fromTask.getReport() != null) {
				logger.debug(" Calling submitReportFromPortal.  Report "
						+ fromTask.getReport().getEmissionsRptId());
				if (fromTask.getNewFpId() == null) {
					// report tied to current facility inventory
					facility = facilityBO.retrieveFacility(fromTask
							.getFacilityId());
					fromTask.getReport().setFpId(facility.getFpId());
				} else {
					// report tied to historic facility inventory
					facility = facilityBO.retrieveFacility(fromTask
							.getNewFpId());
					// Set in the new fpId.
					fromTask.getReport().setFpId(fromTask.getNewFpId());
				}
				erBO = ServiceFactory.getInstance().getEmissionsReportService();
				erBO.submitReportFromPortal(facility, fromTask);
			}

			if (fromTask.getNtvReport() != null) {
				try {
					String r1Id = "null";
					String r2Id = "null";
					if (fromTask.getNtvReport().getReport1() != null)
						r1Id = fromTask.getNtvReport().getReport1()
								.getEmissionsRptId().toString();
					if (fromTask.getNtvReport().getReport2() != null)
						r2Id = fromTask.getNtvReport().getReport2()
								.getEmissionsRptId().toString();
					logger.debug(" Calling submitNtvReportFromPortal.  Reports "
							+ r1Id + " & " + r2Id);
				} catch (Exception e) {
					;
				}
				erBO = ServiceFactory.getInstance().getEmissionsReportService();
				erBO.submitReportFromPortal(fromTask);
			}

			/*
			 * For Compliance Reports we need to first set the status of the
			 * task to 'submitted'. This needs to be wrapped around the task's
			 * transaction in case either fails.
			 */
			if (fromTask.getComplianceReport() != null) {
				logger.debug(" Calling createComplianceReportFromPortal.  Compliance Report "
						+ fromTask.getComplianceReport().getReportId());

				// update the task to submitted
				// ASSUMES THE REPORT HAS ALREADY BEEN VALIDATED
				fromTask.getComplianceReport().setReportStatus(
						ComplianceReportStatusDef.COMPLIANCE_STATUS_SUBMITTED);
				fromTask.getComplianceReport().setSubmittedDate(
						new Timestamp(System.currentTimeMillis()));
				fromTask.getComplianceReport().setReceivedDate(
						fromTask.getComplianceReport().getSubmittedDate()); // set
																			// received
																			// date
																			// the
																			// same
																			// for
																			// portal
				ComplianceReportService crBO = ServiceFactory.getInstance()
						.getComplianceReportService();
				
				facility = facilityBO
						.retrieveFacilityWithMonitorsAndLimits(fromTask.getFacilityId());
				
				String[] targetFacilityIds = 
						fromTask.getComplianceReport().getTargetFacilityIds();
				
				crBO.createComplianceReportFromPortal(
						fromTask.getComplianceReport(), 
						facility, targetFacilityIds, trans);

				SubmissionLog submissionLog = new SubmissionLog(fromTask);
				int ix = 0;
				for (String facId : targetFacilityIds) {
					submissionLog.setFacilityId(facId);
					submissionLog.setGatewaySubmissionId(fromTask.getSubmissionId() + '[' + ix++ + ']');
					facilityDAO.createSubmissionLog(submissionLog);
				}
			}
			if (fromTask.getRelocateRequest() != null) {
				logger.debug(" Calling createRelocationRequestFromPortal.  RelocationRequest "
						+ fromTask.getRelocateRequest().getApplicationNumber());
				fromTask.getRelocateRequest().getFacility()
						.setFacilityId(fromTask.getFacilityId());
				RelocateRequestService relReq = ServiceFactory.getInstance()
						.getRelocateRequestService();
				relReq.createRelocationRequestFromPortal(
						fromTask.getRelocateRequest(), trans);
			}
			
			if (fromTask.getStackTest() != null) {
				logger.debug(" Calling createStackTestFromGateway.  Stack Test "
						+ fromTask.getStackTest().getId());
				facility = facilityBO
						.retrieveFacility(fromTask.getFacilityId());
				StackTestService stBO = ServiceFactory.getInstance()
						.getStackTestService();
				stBO.createStackTestFromGateway(fromTask.getStackTest(),
						facility, trans);
				logger.debug(" Finished creating the stack test internally");
			}

			if (fromTask.getMonitorReports() != null) {
				logger.debug(" Calling createMonitorReportsFromGateway.");
				MonitoringService monBO = ServiceFactory.getInstance().getMonitoringService();
				monBO.createMonitorReportsFromGateway(fromTask.getMonitorReports());
				logger.debug(" Finished creating the monitor reports internally");
			}

			// add entry to submission log
			SubmissionLog submissionLog = new SubmissionLog(fromTask);
			facilityDAO.createSubmissionLog(submissionLog);

			trans.complete();
			logger.debug(" processSubmittedTask transaction completed.");
		} catch (ServiceFactoryException sfe) {
			logger.error(
					"Caught a serviceFacoryException: message = "
							+ sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			logger.error(
					"Caught a remoteException for task " + fromTask.getTaskId()
							+ " message = " + re.getMessage(), re);
			cancelTransaction(trans, re);
		} catch (Exception e) {
			String error = "Caught an Exception of type "
					+ e.getClass().getName() + " for task "
					+ fromTask.getTaskId() + " message = " + e.getMessage();
			logger.error(error, e);
			DAOException daoe = new DAOException(error, e);
			cancelTransaction(trans, daoe);
		} finally {
			// Clean up our transaction stuff.
			closeTransaction(trans);
		}

		logger.debug(" Finished processing, exiting normally.");
	}

	/**
	 * @return boolean (true if fixed)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public boolean fixFacilityContact(Facility facility) throws DAOException {
		boolean ret = false;
		String facilityId = facility.getFacilityId();
		Integer corePlaceId = facility.getCorePlaceId();

		Task[] tasks;
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);

		try {
			Task filterTask = new Task();
			filterTask.setCorePlaceId(corePlaceId);
			tasks = retrieveTasks(filterTask);

			String userName = null;
			ArrayList<Task> refTasks = new ArrayList<Task>(1);
			int refCount = 0;
			Task contactTask = null;
			for (Task task : tasks) {
				if (task.getTaskType().equals(Task.TaskType.FCC)) {
					contactTask = task;
				}
			}

			for (Task task : tasks) {
				if (task.getTaskType().equals(Task.TaskType.FC)
						|| task.getTaskType().equals(Task.TaskType.FCH)) {
					if (userName == null) {
						userName = task.getUserName();
					}
					if (!task.getDependent()
							|| (contactTask != null && !task
									.getDependentTaskId().equals(
											contactTask.getTaskId()))) {
						refTasks.add(task);
						refCount++;
					}
				} else if (task.getTaskType().equals(Task.TaskType.FCC)) {
					contactTask = task;
				}
			}

			if (refCount == 0) {
				logger.debug(" Populating contacts in staging is not needed for facility : "
						+ facilityId);
				return ret;
			}

			if (contactTask == null) {
				Task newTask = createNewTask("Facility Contact Change",
						Task.TaskType.FCC, false, null, facilityId, null,
						"current", corePlaceId, userName);
				newTask.setReferenceCount(refCount);
				newTask.setFacility(facility);
				contactTask = createTask(newTask, trans);
				ret = true;
			}

			for (Task facTask : refTasks) {
				facTask.setDependent(true);
				facTask.setDependentTaskId(contactTask.getTaskId());
				infraDAO.modifyTask(facTask);
			}

			trans.complete();
		} catch (Exception e) {
			String error = "retrieve tasks failed for facility: " + facilityId
					+ " : " + e.getMessage();
			DAOException daoe = new DAOException(error, e);
			cancelTransaction(trans, daoe);
		}

		return ret;
	}

	/**
	 * @return boolean (true if fixed)
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Document[] keyWordSearch(String query, int hitLimit)
			throws DAOException {
		ArrayList<Document> ret = new ArrayList<Document>();

		ConfigManager cfgMgr = ConfigManagerFactory.configManager();

		// Get the index location.
		Node root = cfgMgr.getNode("app.IndexDirectoryLocation");
		String index = root.getAsString("value");
		File indexDirFile = new File(index);
		Directory indexDir = null;
		try {
			indexDir = FSDirectory.open(indexDirFile);
		} catch (IOException e1) {
			throw new DAOException("Unable to open index directory", e1);
		}

		IndexReader reader = null;
		Searcher searcher = null;

		try {
			reader = IndexReader.open(indexDir);

			searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
			SortField sortField = new SortField("LastModifiedTS",
					SortField.STRING, true);
			Sort sort = new Sort(sortField);
			SortField[] fields = sort.getSort();
			for (SortField sf : fields) {
				logger.debug("Sorting results by " + sf.getField());
			}
			QueryParser parser = new QueryParser(Version.LUCENE_30, "contents",
					analyzer);
			Query luceneQuery = null;

			luceneQuery = parser.parse(query);

			TopFieldDocs topDocs = searcher.search(luceneQuery, null, hitLimit,
					sort);

			// TopDocCollector collector = new TopDocCollector(hitLimit);
			// searcher.search(luceneQuery, collector);
			// ScoreDoc[] hits = collector.topDocs().scoreDocs;
			ScoreDoc[] hits = topDocs.scoreDocs;

			Document tempDoc = null;
			for (ScoreDoc scoreDoc : hits) {
				org.apache.lucene.document.Document doc = searcher
						.doc(scoreDoc.doc);

				tempDoc = new Document();

				tempDoc.setDocumentID(new Integer(doc.get("DocumentId")));
				tempDoc.setFacilityID(doc.get("FacilityId"));
				tempDoc.setDescription(doc.get("Description"));
				tempDoc.setBasePath(doc.get("Path"));
				String lastModifiedBy = doc.get("LastModifiedBy");

				if (lastModifiedBy != null) {
					try {
						tempDoc.setLastModifiedBy(Integer
								.parseInt(lastModifiedBy));
					} catch (NumberFormatException nfe) {
						logger.error(
								"Bad value in LastModifiedBy for document "
										+ tempDoc.getDocumentID(), nfe);
					}
				}

				String lastModifedTS = doc.get("LastModifiedTS");
				if (lastModifedTS != null) {
					try {
						Date lmDate = FileDocument.DateFormat
								.parse(lastModifedTS);
						if (lmDate != null) {
							tempDoc.setLastModifiedTS(new Timestamp(lmDate
									.getTime()));
						}
					} catch (java.text.ParseException e) {
						logger.error(
								"Bad value in LastModifiedTS for document "
										+ tempDoc.getDocumentID(), e);
					}
				}

				ret.add(tempDoc);
			}
		} catch (CorruptIndexException cie) {
			logger.error(cie.getMessage(), cie);
		} catch (ParseException pe) {
			logger.error(pe.getMessage(), pe);
		} catch (IOException ioe) {
			logger.error(ioe.getMessage(), ioe);
		} finally {
			try {
				searcher.close();
			} catch (IOException e) {
			}
		}
		return ret.toArray(new Document[0]);
	}

	private void saveXmlDataInFile(byte[] xmlData, Task task,
			String dataSubmitId) {
		logger.debug(" Saving XML data into file");
		String parmVal = SystemPropertyDef.getSystemPropertyValue("SaveXmlDataInFile", null);
		if (parmVal != null && parmVal.equalsIgnoreCase("false")) {
			return;
		}

		try {
			String tmpDataSubmitId = dataSubmitId.replaceAll(":", "");
			TmpDocument appDoc = new TmpDocument();
			// Set the path elements of the temp doc.
			appDoc.setDescription("XML data for submit-" + tmpDataSubmitId);
			appDoc.setFacilityID(task.getFacilityId());
			appDoc.setTemporary(true);
			appDoc.setTmpFileName(tmpDataSubmitId + ".xml");

			// make sure temporary directory exists
			DocumentUtil.mkDirs(appDoc.getDirName());
			OutputStream os = DocumentUtil.createDocumentStream(appDoc
					.getPath());
			os.write(xmlData);
			os.close();
		} catch (Exception e) {
			logger.error(
					"saveXmlDataInFile failed; facility ID: "
							+ task.getFacilityId() + " dataSubmitId : "
							+ dataSubmitId, e);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void addAttestationDocumentToTask(Task task, Document doc,
			String filename, InputStream is) throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			if (task.getApplication() != null) {
				ApplicationDocument appDoc = new ApplicationDocument(doc);
				appDoc.setApplicationId(task.getApplication()
						.getApplicationID());
				uploadDocument(appDoc, filename, is, trans);
				task.setDocumentId(appDoc.getDocumentID());
				ApplicationService appBO = ServiceFactory.getInstance()
						.getApplicationService();
				appBO.addAttestationDocument(task.getApplication(), appDoc,
						trans);
			}
			if (task.getReport() != null) {
				EmissionsDocument erDoc = new EmissionsDocument(doc);
				erDoc.setEmissionsRptId(task.getReport().getEmissionsRptId());
				uploadDocument(erDoc, filename, is, trans);
				task.setDocumentId(erDoc.getDocumentID());
				EmissionsReportService emissionsReportBO = ServiceFactory
						.getInstance().getEmissionsReportService();
				emissionsReportBO.addAttestationDocument(task.getReport(),
						erDoc, trans);
			}
			if (task.getNtvReport() != null) {
				EmissionsReport report = task.getNtvReport().getPrimary();
				EmissionsDocument erDoc = new EmissionsDocument(doc);
				erDoc.setEmissionsRptId(report.getEmissionsRptId());
				uploadDocument(erDoc, filename, is, trans);
				task.setDocumentId(erDoc.getDocumentID());
				EmissionsReportService emissionsReportBO = ServiceFactory
						.getInstance().getEmissionsReportService();
				emissionsReportBO.addAttestationDocument(report, erDoc, trans);
			}
			if (task.getComplianceReport() != null) {
				us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment attachment = new us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment(
						doc);
				attachment.setFacilityID(task.getComplianceReport()
						.getFacilityId());
				attachment.setSubPath("ComplianceReports");
				attachment.setObjectId(task.getComplianceReport().getReportId()
						.toString());
				uploadDocument(attachment, filename, is, trans);
				task.setDocumentId(attachment.getDocumentID());
				ComplianceReportService complianceReportBO = ServiceFactory
						.getInstance().getComplianceReportService();
				complianceReportBO.addAttestationDocument(
						task.getComplianceReport(), attachment, trans);
			}
			if (task.getRelocateRequest() != null) {
				us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment attachment = new us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment(
						doc);
				attachment.setFacilityID(task.getFacilityId());
				attachment
						.setDocTypeCd(RelocationAttachmentTypeDef.RO_SIGNATURE);
				attachment.setSubPath("Applications");
				attachment.setObjectId(task.getRelocateRequest()
						.getApplicationID().toString());
				uploadDocument(attachment, filename, is, trans);
				task.setDocumentId(attachment.getDocumentID());
				RelocateRequestService relocateBO = ServiceFactory
						.getInstance().getRelocateRequestService();
				relocateBO.addAttestationDocument(task.getRelocateRequest(),
						attachment, trans);
			}
			if (TaskType.FC.equals(task.getTaskType())
					|| TaskType.FCC.equals(task.getTaskType())
					|| TaskType.FCH.equals(task.getTaskType())) {
				Facility facility = task.getFacility();
				if (facility != null) {
					us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment attachment = new us.oh.state.epa.stars2.database.dbObjects.attachments.Attachment(
							doc);
					attachment.setFacilityID(facility.getFacilityId());
					attachment.setSubPath("Attachments");
					uploadDocument(attachment, filename, is, trans);
					task.setDocumentId(attachment.getDocumentID());
					FacilityService facilityBO = ServiceFactory.getInstance()
							.getFacilityService();
					facilityBO.addAttestationDocument(facility, attachment,
							trans);
				} else {
					logger.error("Facility is null for task "
							+ task.getTaskId());
				}
			}
			task.setNonROSubmission(true);
			modifyTask(task, trans);
		} catch (RemoteException e) {
			cancelTransaction(trans, e);
		} catch (ServiceFactoryException e) {
			cancelTransaction(trans, new RemoteException(
					"Exception creating service", e));
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public void removeAttestationDocumentFromTask(Task task)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		InfrastructureDAO infraDAO = infrastructureDAO(trans);
		try {
			if (task.getApplication() != null) {
				ApplicationService appBO = ServiceFactory.getInstance()
						.getApplicationService();
				appBO.removeAttestationDocument(task.getApplication(), trans);
			}
			if (task.getReport() != null) {
				EmissionsReportService emissionsReportBO = ServiceFactory
						.getInstance().getEmissionsReportService();
				emissionsReportBO.removeAttestationDocument(task.getReport(),
						trans);
			}
			if (task.getNtvReport() != null) {
				EmissionsReportService emissionsReportBO = ServiceFactory
						.getInstance().getEmissionsReportService();
				emissionsReportBO.removeAttestationDocument(task.getNtvReport()
						.getPrimary(), trans);
			}
			if (task.getComplianceReport() != null) {
				ComplianceReportService complianceReportBO = ServiceFactory
						.getInstance().getComplianceReportService();
				complianceReportBO.removeAttestationDocument(
						task.getComplianceReport(), trans);
			}
			if (task.getRelocateRequest() != null) {
				RelocateRequestService relocateBO = ServiceFactory
						.getInstance().getRelocateRequestService();
				relocateBO.removeAttestationDocument(task.getRelocateRequest(),
						trans);
			}
			if (TaskType.FCC.equals(task.getTaskType())
					|| TaskType.FCH.equals(task.getTaskType())) {
				FacilityService facilityBO = ServiceFactory.getInstance()
						.getFacilityService();
				facilityBO.removeAttestationDocument(task.getFacility(), trans);
			} else if (TaskType.FC.equals(task.getTaskType())) {
				task.setAttestationDoc(null);
			}
			task.setDocumentId(null);
			infraDAO.modifyTask(task);
		} catch (RemoteException e) {
			cancelTransaction(trans, e);
		} catch (ServiceFactoryException e) {
			cancelTransaction(trans, new RemoteException(
					"Exception creating service", e));
		} finally {
			closeTransaction(trans);
		}
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Document uploadDocument(Document doc, String filename, InputStream is)
			throws DAOException {
		Transaction trans = TransactionFactory.createTransaction();
		try {
			doc = uploadDocument(doc, filename, is, trans);
			trans.complete();
		} catch (DAOException e) {
			cancelTransaction(trans, e);
		} finally {
			closeTransaction(trans);
		}
		return doc;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	public Document uploadDocument(Document doc, String filename,
			InputStream is, Transaction trans) throws DAOException {
		if (is == null) {
			throw new DAOException("InputStream for uploaded document "
					+ filename + " is null");
		}
		DocumentDAO docDAO = documentDAO(trans);

		// Set the path elements of the temp doc.
		doc.setExtension(DocumentUtil.getFileExtension(filename));

		doc.setLastModifiedTS(new Timestamp(System.currentTimeMillis()));
		doc.setUploadDate(doc.getLastModifiedTS());

		// add document info to database
		// NOTE: This needs to be done before file is created in file store
		// since document id obtained from createDocument method is used as
		// the file name for the file store file
		docDAO.createDocument(doc);

		// copy document to file store
		try {
			DocumentUtil.createDocument(doc.getPath(), is);
		} catch (IOException e) {
			logger.error(
					"Exception creating document in file store: "
							+ doc.getPath(), e);
			throw new DAOException("Exception creating document in file store",
					e);
		}

		return doc;
	}

	public String getDAPCAttestationMessage(String permitClassCd) throws DAOException {
		 
		String message = null;
		String confidentialMsg = SystemPropertyDef.getSystemPropertyValue("ConfidentialAttestationMessage", null);
        if (confidentialMsg == null) {
        	confidentialMsg = "";
        }
        
		if (permitClassCd != null) {
			if (PermitClassDef.TV.equals(permitClassCd)) {
				message = SystemPropertyDef.getSystemPropertyValue("TVFacilityAttestationMessage", null);
			} else if (PermitClassDef.SMTV.equals(permitClassCd)
					|| PermitClassDef.NTV.equals(permitClassCd)) {
				message = SystemPropertyDef.getSystemPropertyValue("NonTVFacilityAttestationMessage", null);
			}
		}
		
		if (message == null) {
			message = confidentialMsg;
		} else {
			message = confidentialMsg + message;
		}
		
		return message;

	}

	/**
	 * @throws ServiceFactoryException
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String getCROMERRAttestationMessage(String loginName)
			throws ServiceFactoryException, RemoteException {
		String message = "Are you sure you want to Submit the changes?";
		/*
		 * AccountService accountService; accountService =
		 * ServiceFactory.getInstance().getAccountService(); Attestation
		 * pinAttestation = accountService.retrieveAttestationByType( loginName,
		 * Constants.ATTESTATION_PIN); if (pinAttestation != null) { message =
		 * pinAttestation.getText(); } else {
		 * logger.error("CROMERR Attestation language not found."); }
		 */
		return message;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public String[] retreiveSubmittingAppUser(Application app) {
		// Used for Emissions Inventories and Applications
		// This function will not be called if the object has not been submitted
		// or was submitted prior to Stars2 becoming operational.
		String[] array = new String[2];
		array[0] = "Submitted by: ";
		array[1] = "";
		try {
			Integer userId = null;
			InfrastructureDAO infraDAO = infrastructureDAO();
			if (ApplicationTypeDef.DELEGATION_OF_RESPONSIBILITY.equals(app
					.getApplicationTypeCD())) {
				// Delegation of Responsibility
				userId = infraDAO.retrieveDorUserId(app.getApplicationID());
			} else if (ApplicationTypeDef.isRelocation(app
					.getApplicationTypeCD())) {
				// Intent to relocate to site not pre-approved
				// Relocate to pre-approved site
				// Relocation Site(s) Pre-Approval
				Integer requestId = infraDAO.retrieveReqestIdFromRelocate(app
						.getApplicationID());
				if (requestId != null) {
					userId = infraDAO.applicationUserIdFromRelocate(requestId);
				}
			} else {
				// Request Administrative Permit Modification
				// Request Permit Extension
				// Request Permit Revocation
				// Permit-by-rule Notification
				// ACID Rain Application
				// PTI/PTIO Application
				// Title V Permit Application
				Integer permitId = infraDAO.retrievePermitId(app
						.getApplicationID());
				if (permitId != null) {
					userId = infraDAO.applicationUserIdFromPermit(permitId);
				}
			}
			if (userId == null) {
				// don't generate any errors
			} else {
				if (userId.intValue() == CommonConst.GATEWAY_USER_ID) {
					UserDef c = infraDAO.retrieveContactByUserId(userId);
					if (c == null) {
						// don't generate any errors
					} else {
						array[1] = c.getNameOnly();
					}
				} else {
					array[0] = SystemPropertyDef.getSystemPropertyValue("EnteredByDapcLabel", null);
					if (!isInternalApp()) {
						array[1] = SystemPropertyDef.getSystemPropertyValue("EnteredByDapcValue", null);
					} else {
						UserDef c = infraDAO.retrieveContactByUserId(userId);
						if (c == null) {
							// don't generate any errors
						} else {
							array[1] = c.getNameOnly();
						}
					}
				}
			}
		} catch (DAOException e) {
			// don't generate any errors
		}
		return array;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */

	public String[] retreiveSubmittingRptUser(Integer id) {
		// Used for Emissions Inventories and Applications
		// This function will not be called if the object has not been submitted
		// or was submitted prior to Stars2 becoming operational.
		String[] array = new String[2];
		array[0] = "Submitted by: ";
		array[1] = "";
		try {
			InfrastructureDAO infraDAO = infrastructureDAO();
			Integer userId = infraDAO.retrieveRptUserId(id);
			if (userId == null) {
				// don't generate any errors
			} else {
				if (userId.intValue() == CommonConst.GATEWAY_USER_ID) {
					UserDef c = infraDAO.retrieveContactByUserId(userId);
					if (c == null) {
						// don't generate any errors
					} else {
						array[1] = c.getNameOnly();
					}
				} else {
					array[0] = SystemPropertyDef.getSystemPropertyValue("EnteredByDapcLabel", null);
					if (!isInternalApp()) {
						array[1] = SystemPropertyDef.getSystemPropertyValue("EnteredByDapcValue", null);
					} else {
						UserDef c = infraDAO.retrieveContactByUserId(userId);
						if (c == null) {
							// don't generate any errors
						} else {
							array[1] = c.getNameOnly();
						}
					}
				}
			}
		} catch (DAOException e) {
			// don't generate any errors
		}
		return array;
	}

	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public Integer[] retrieveUsersWithUseCase(String useCase)
			throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO();
		return infraDAO.retrieveUsersWithUseCase(useCase);
	}
	
	public String retrieveUserWithPositionNumber(Integer positionNumber)
			throws DAOException {
		InfrastructureDAO infraDAO = infrastructureDAO();
		return infraDAO.retrieveUserWithPositionNumber(positionNumber);
	}
	
	public int getNextSequenceNumber(String sequenceName) throws DAOException {
	    return infrastructureDAO().getNextSequenceNumber(sequenceName);
	    }

	@Override
	public DaemonInfo retrieveDaemonInfo(String daemonCode, String hostname)
			throws DAOException {
		return infrastructureDAO(CommonConst.READONLY_SCHEMA)
				.retrieveDaemonInfo(daemonCode, hostname);
	}

	@Override
	public String registerDocument(String path) throws DAOException {
		String documentUrl = null;
		String applicationDocumentId = 
				Math.abs(new Long(new Date().getTime()).intValue()) + "";
		try {
			CopySoap p;
			p = getCopySoap();
			documentUrl = uploadDocument(p, path, applicationDocumentId);
			logger.debug("---> registerDocument: doc uploaded, no error");
		} catch (Exception e) {
			logger.error("Error while copying document to sharepoint: " + e,e);
			throw new DAOException(e.toString());
		}
		
		String documentId;
		if (!isTestMode()) {
			EnviteDAO enviteDao = new EnviteRestDAO();
			documentId =
					enviteDao.registerDocument(documentUrl, applicationDocumentId);
		} else {
			documentId = "--test-document-id--";
		}
		return documentId;
	}
	
	@Override
	public String registerDocument(Document doc) throws DAOException {
		logger.debug("---> registerDocument");
		ImpactPortalClient portalClient = new ImpactPortalClient();
		String documentId = portalClient.registerDocument(doc.getPath()); // doc path, md5, etc?
		logger.debug("---> registerDocument: register doc rest service called");
		return documentId;
	}

	private CopySoap getCopySoap() throws Exception {
		logger.debug(" Creating a CopySoap instance...");
		String username = (String)Config.getEnvEntry("app/sharePoint/username");
		String password = (String)Config.getEnvEntry("app/sharePoint/password");
		String wsdl = (String)Config.getEnvEntry("app/sharePoint/copy/wsdl");
		String endpoint = (String)Config.getEnvEntry("app/sharePoint/copy/endpoint");
		Copy service = new Copy(new URL(wsdl), new QName("http://schemas.microsoft.com/sharepoint/soap/", "Copy"));
		CopySoap copySoap = service.getCopySoap();
		BindingProvider bp = (BindingProvider) copySoap;
		bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
		bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
		bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint);
		return copySoap;
	}

	private String uploadDocument(CopySoap port, String sourceUrl, 
			String applicationDocumentId) throws Exception {
		File f = new File(DocumentUtil.getFileStoreRootPath() + sourceUrl);
		logger.debug("Uploading: " + f.getName());
		logger.debug("----> f.absolutePath: " + f.getAbsolutePath());
		
		String target =  (String)Config.getEnvEntry("app/sharePoint/folder");

		String url = target + applicationDocumentId + '-' + f.getName();
		DestinationUrlCollection destinationUrlCollection = new DestinationUrlCollection();
		destinationUrlCollection.getString().add(url);

		FieldInformation titleFieldInformation = new FieldInformation();
		titleFieldInformation.setDisplayName("Title");
		titleFieldInformation.setType(FieldType.TEXT);
		titleFieldInformation.setValue(f.getName());

		FieldInformationCollection fields = new FieldInformationCollection();
		fields.getFieldInformation().add(titleFieldInformation);

		CopyResultCollection results = new CopyResultCollection();
		Holder<CopyResultCollection> resultHolder = new Holder<CopyResultCollection>(results);
		Holder<Long> longHolder = new Holder<Long>(new Long(-1));
		
		//make the call to upload
		port.copyIntoItems(sourceUrl, destinationUrlCollection, fields, readAll(f), longHolder,resultHolder);
		
		//does not seem to change based on different CopyResults
		logger.debug("Long holder: " + longHolder.value);
		
		//do something meaningful here
		for (CopyResult copyResult : resultHolder.value.getCopyResult()) {				
			logger.debug("Destination: " + copyResult.getDestinationUrl());
			logger.debug("Error Message: " + copyResult.getErrorMessage());
			logger.debug("Error Code: " + copyResult.getErrorCode());
			if(copyResult.getErrorCode() != CopyErrorCode.SUCCESS)
				throw new Exception("Upload failed for: " + copyResult.getDestinationUrl() + " Message: " 
						+ copyResult.getErrorMessage() + " Code: " +   copyResult.getErrorCode() );
		}
		
		return url;
	}
	
	private byte[] readAll(File file) throws IOException {
		logger.debug("readAll()..." + file.getAbsolutePath());
		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1)
				ous.write(buffer, 0, read);
		} finally {
			try {
				if (ous != null)
					ous.close();
			} finally {
				if (ios != null)
					ios.close();
			}
		}
		return ous.toByteArray();
	}


	@Override
	public String registerSignature(String documentId, String organizationId, 
			String username, String redirectUrl) throws DAOException {
		logger.debug("---> registerSignature");
		String signatureId = null;
		if (!isInternalApp()) {
			ImpactPortalClient portalClient = new ImpactPortalClient();
			signatureId = 
					portalClient.registerSignature(documentId, organizationId, 
							username, redirectUrl);
		} else {
			if (!isTestMode()) {
				EnviteDAO enviteDao = new EnviteRestDAO();
				signatureId = 
						enviteDao.registerSignature(documentId,organizationId, 
								username, redirectUrl);
			} else {
				signatureId = "--test-signature-id--"; //test mode only
			}
		}
		return signatureId;
	}
	
	public List<SCNonChargePollutant> retrievePollutantsByCategory(String category, int reportId) throws DAOException {
		List <SCNonChargePollutant> sCNonChargePollutant = new ArrayList<SCNonChargePollutant>(0);;
		List <PollutantDef> pollutantDefs = infrastructureDAO().retrievePollutantsByCategory(category);
		for(PollutantDef pollutantDef: pollutantDefs) 
		{
			SCNonChargePollutant chargePollutant = null;
			if (pollutantDef != null)  {
				chargePollutant = new SCNonChargePollutant();
				chargePollutant.setSCReportId(reportId);
				chargePollutant.setPollutantCd(pollutantDef.getCode());
				chargePollutant.setPollutantDsc(pollutantDef.getDescription());
				chargePollutant.setDeprecated(pollutantDef.isDeprecated());
			}
			sCNonChargePollutant.add(chargePollutant);	
		}
		return sCNonChargePollutant;
	}
	
	public List<ForeignKeyReference> retrieveForeignKeyReferences(String tableName, String columnName, 
			String schema) throws DAOException {
		
		List <ForeignKeyReference> fkRefs = new ArrayList<ForeignKeyReference>(0);
		List<ForeignKeyReference> fkReferences = infrastructureDAO().retrieveForeignKeyReferences(tableName, columnName, schema);
		if(fkReferences != null && fkReferences.size() > 0) {
			// update objects with the name of the referring object (needed for friendlier warning message)
			for(ForeignKeyReference fkRef : fkReferences) {
				if(fkRef.getFkTableName().equalsIgnoreCase("PA_EU"))
					fkRef.setFkObjectName("Permit Application(s)");
				else if(fkRef.getFkTableName().equalsIgnoreCase("PT_EU"))
					fkRef.setFkObjectName("Permit(s)");
				else if(fkRef.getFkTableName().equalsIgnoreCase("RP_REPORT_EU"))
					fkRef.setFkObjectName("Emissions Inventory");
				else if(fkRef.getFkTableName().equalsIgnoreCase("CE_STACK_TEST_POLLUTANT_XREF"))
					fkRef.setFkObjectName("Stack Test(s)");
				else if(fkRef.getFkTableName().equalsIgnoreCase("FP_CONTINUOUS_MONITOR_EU_XREF")
						|| fkRef.getFkTableName().equalsIgnoreCase("FP_CONTINUOUS_MONITOR_EGRESS_POINT_XREF"))
					fkRef.setFkObjectName("CEM/COM/CMS Monitor(s)");
				else if(fkRef.getFkTableName().equalsIgnoreCase("PT_PERMIT_CONDITION_EU_XREF"))
					fkRef.setFkObjectName("Permit Condition(s)");
				else 
					fkRef.setFkObjectName(fkRef.getFkTableName());
				
				fkRefs.add(fkRef);
			}
		}
	
		return fkRefs;
	}
	
	public boolean checkForeignKeyReferencedData(String tableName, String columnName,
			Integer fkValue, String schema) throws DAOException {
		
		return infrastructureDAO(schema).checkForeignKeyReferencedData(tableName, columnName, fkValue);
	}
	
	public boolean checkForeignKeyReferencedData(String tableName, String columnName,
			Integer fkValue) throws DAOException {
		
		return infrastructureDAO().checkForeignKeyReferencedData(tableName, columnName, fkValue);
	}
	
	@Override
	public GeoPolygonDef[] retrieveGeoPolygonDefs() throws DAOException {
		return facilityDAO().retrieveGeoPolygonDefs();
	}
	
	/**
	 * @return String
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="Required"
	 */
	@Override
	public String submitNewFacilityRequest(FacilityRequest newFacilityRequest) 
					throws DAOException {

		byte[] xmlData;
		String requestId = null;
		
		ImpactPortalClient portalClient = new ImpactPortalClient();

		try {
			FacilityRequest facilityRequest = new FacilityRequest(newFacilityRequest);
			xmlData = facilityRequest.toXMLStream();
			requestId = portalClient.processNewFacilityRequest(xmlData);
		
		} catch (RemoteException re) {
			logger.error(re.getMessage(), re);
	        throw new DAOException(re.getMessage());
		} finally {
			CompMgr.setAppName(CommonConst.EXTERNAL_APP);
		}

		logger.debug(" end submitNewFacilityRequest for requestId " + requestId);
		return requestId;
	}
	
	/**
	 * @return String
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="RequiresNew"
	 */
	public String processNewFacilityRequest(byte[] fromXmlTask) throws DAOException {

		logger.debug(" Starting processing.");
		String requestId = null;
		
		ByteArrayInputStream fromXmlInput = new ByteArrayInputStream(
				fromXmlTask);
		FacilityService facilityBO = null;
		EmissionsReportService erBO = null;
		Facility facility = null;
		FacilityRequest fromRequest = null;

		try {

			facilityBO = ServiceFactory.getInstance().getFacilityService();
			fromRequest = (FacilityRequest) BaseDB.fromXMLStream(fromXmlInput);
			if (fromRequest == null) {
				throw new DAOException("Unable to get new facility request from from xml data.");
			}

			if (fromRequest != null) {
				logger.debug(" Calling createNewFacilityRequestFromGateWay:  ");

				requestId = facilityBO.createNewFacilityRequestFromGateWay(
						fromRequest);
			}

			// add entry to submission log
			//SubmissionLog submissionLog = new SubmissionLog(fromTask);
			//FacilityDAO facilityDAO = facilityDAO(trans);
			//facilityDAO.createSubmissionLog(submissionLog);

			logger.debug(" processNewFacilityRequest transaction completed. requestId = " + requestId);
		} catch (ServiceFactoryException sfe) {
			logger.error(
					"Caught a serviceFacoryException for request to create a new facility named: "
							+ fromRequest.getName() 
							+ " : message = "
							+ sfe.getMessage(), sfe);
		} catch (RemoteException re) {
			logger.error(
					"Caught a remoteException for request to create a new facility named: " 
							+ fromRequest.getName()
							+ " message = " + re.getMessage(), re);
		} catch (Exception e) {
			String error = "Caught an Exception of type "
					+ e.getClass().getName() + " for request to create a new facility named: "
					+ fromRequest.getName() 
					+ " message = " + e.getMessage();
			logger.error(error, e);
			DAOException daoe = new DAOException(error, e);
		} finally {
			
		}
		
		logger.debug(" Finished processing processNewFacilityRequest, exiting normally.");
		return requestId;
	}
	
	/**
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public boolean retrieveUserStatusByLogin(String login) {
		boolean ret = false;
		try {
			ret = infrastructureDAO().retrieveUserStatusByLogin(login);
		} catch(DAOException de) {
			logger.error(de.getMessage() + " for " + login, de);
		}

		return ret;
	}
	
	
	public TimeSheetRow[] retrieveTimeSheetEntries(Integer userId) throws DAOException {
		return infrastructureDAO().retrieveTimeSheetEntries(userId);
	}


	@Override
	public TimeSheetRow createTimesheetEntry(TimeSheetRow row)
			throws DAOException {
		return infrastructureDAO().createTimesheetEntry(row);
	}

	@Override
	public void removeTimesheetEntry(TimeSheetRow modifyRow)
			throws DAOException {
		infrastructureDAO().removeTimesheetEntry(modifyRow);
	}

	@Override
	public boolean modifyTimesheetEntry(TimeSheetRow modifyRow)
			throws DAOException {
		return infrastructureDAO().modifyTimesheetEntry(modifyRow);
	}

	@Override
	public TimeSheetRow retrieveTimesheetEntry(Integer rowId)
			throws DAOException {
		return infrastructureDAO().retrieveTimesheetEntry(rowId);
	}

	@Override
	public boolean addressIntersectsShape(Integer addressId, Integer shapeId)
			throws DAOException {
		return infrastructureDAO().addressIntersectsShape(addressId,shapeId);
	}

	@Override
	public Shape[] retrieveShapes() throws DAOException {
		return infrastructureDAO().retrieveShapes();
	}

	@Override
	public boolean modifyShape(Shape modifyShape) throws DAOException {
		return infrastructureDAO().modifyShape(modifyShape);
	}

	@Override
	public Shape retrieveShape(Integer shapeId) throws DAOException {
		return infrastructureDAO().retrieveShape(shapeId);
	}

	@Override
	public void removeShape(Shape shape) throws DAOException {
		infrastructureDAO().removeShape(shape);
	}

	@Override
	public String getTriggerStatus(TriggerKey triggerKey) throws DAOException,
			RemoteException {
		String status = null;
		Stars2Scheduler scheduler = null;
		try {
			scheduler = (Stars2Scheduler) CompMgr
					.newInstance("app.Scheduler");

			TriggerState triggerState = 
					scheduler.getScheduler().getTriggerState(triggerKey);
			status = triggerState.name();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}		
		return status;
	}

	@Override
	public List<String> retrieveProjectIdsAssociatedWithShape(Integer shapeId)
			throws DAOException {
		return infrastructureDAO().retrieveProjectIdsAssociatedWithShape(
				shapeId);
	}

	@Override
	public List<SimpleDef> retrieveProjectTypesByAppUsr(Integer userId)
			throws DAOException {
		return infrastructureDAO().retrieveProjectTypesByAppUsr(userId);
	}

	@Override
	public List<String> retrieveInactiveProjectIdsAssociatedWithShape(
			Integer shapeId) throws DAOException {
		return infrastructureDAO()
				.retrieveInactiveProjectIdsAssociatedWithShape(shapeId);
	}
	
	@Override
	public boolean okToSaveServiceCatalogTemplate(
			us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport newReport)
			throws us.oh.state.epa.stars2.framework.exception.DAOException {

		InfrastructureDAO infrastructionDAO = infrastructureDAO(CommonConst.READONLY_SCHEMA);
		boolean ret = serviceCatalogDAO().okToSaveServiceCatalogTemplate(
				newReport);
		return ret;

	}
	
	public Integer retrieveSCEmissionsReportId(Integer reportingYear,
			String contentTypeCd, String regulatoryRequirementCd)
			throws DAOException {
		Integer ret = 0;

		try {
			ret = serviceCatalogDAO().retrieveSCEmissionsReportId(
					reportingYear, contentTypeCd, regulatoryRequirementCd);

		} catch (DAOException de) {
			logger.error(
					"retrieve Service Catalog Id failed for reporting year = ["
							+ reportingYear + "] : contentTypeCd = ["
							+ contentTypeCd + "] : regulatoryRequirementCd = ["
							+ regulatoryRequirementCd + "] :" + de.getMessage(),
					de);
			throw de;
		}

		return ret;
	}
	
	public Integer retrieveHighestPriorityRptReportId(Integer reportingYear,
			String contentTypeCd, String facilityId)
			throws DAOException {
		Integer ret = 0;

		try {
			ret = serviceCatalogDAO().retrieveHighestPriorityRptReportId(
					reportingYear, contentTypeCd, facilityId);

		} catch (DAOException de) {
			logger.error(
					"retrieve Service Catalog Id failed for reporting year = ["
							+ reportingYear + "] : contentTypeCd = ["
							+ contentTypeCd + "] : facilityId = ["
							+ facilityId + "] :" + de.getMessage(),
					de);
			throw de;
		}

		return ret;
	}

	public Integer getDefaultSearchLimit() {
		
		Integer ret = -1;

		try {
			ret = infrastructureDAO().getDefaultSearchLimit();
		} catch (DAOException daoe) {
			logger.error("Could not obtain defaultSearchLimit. " + daoe.getMessage(), daoe);
		}
		
		return ret;
		
	}

	@Override
	public List<Integer> retrieveIndianReservationShapeIds() throws DAOException{
		return infrastructureDAO().retrieveIndianReservationShapeIds();
	}
}