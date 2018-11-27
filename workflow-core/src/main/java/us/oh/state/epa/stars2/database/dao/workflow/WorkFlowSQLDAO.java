package us.oh.state.epa.stars2.database.dao.workflow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.DetailDataDAO;
import us.oh.state.epa.stars2.database.dao.WorkFlowDAO;
import us.oh.state.epa.stars2.database.dao.AbstractDAO.ConnectionHandler;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityReferralType;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ActivityTemplateDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ControllerConfig;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataDetailBase;
import us.oh.state.epa.stars2.database.dbObjects.workflow.DataField;
import us.oh.state.epa.stars2.database.dbObjects.workflow.PerformerDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivityLight;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessData;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessDataTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessTemplate;
import us.oh.state.epa.stars2.database.dbObjects.workflow.Transition;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TransitionDef;
import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.def.FacilityRoleDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

/**
 * <p>
 * Title: WorkflowSQLDAO
 * </p>
 * 
 * <p>
 * Description: This is the SQL implementation of the WorkFlowDAO.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author J. E. Collier
 * @version 1.0
 */
@Repository
public class WorkFlowSQLDAO extends AbstractDAO implements WorkFlowDAO {

	@Resource
	private DetailDataDAO readOnlyDetailDataDAO;

	public ActivityTemplate[] retrieveActivityTemplates()
			throws DAOException {
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActivityTemplates", true);

		ArrayList<ActivityTemplate> ret = connHandler
				.retrieveArray(ActivityTemplate.class);

		return ret.toArray(new ActivityTemplate[0]);
	}

	
	public final FacilityRoleDef[] retrieveFacilityRoleDefs()
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveFacilityRoleDefs", true);

		ArrayList<FacilityRoleDef> ret = connHandler
				.retrieveArray(FacilityRoleDef.class);

		return ret.toArray(new FacilityRoleDef[0]);
	}

	public final SimpleIdDef[] retrieveWorkflowTempIdAndNm()
			throws DAOException {
		return retrieveDescAndId("WorkFlowSQL.retrieveWorkflowTempIdAndNm");
	}

	public final SimpleIdDef[] retrieveProcessGroupByType() throws DAOException {
		return retrieveDescAndId("WorkFlowSQL.retrieveProcessGroupByType");
	}

	private SimpleIdDef[] retrieveDescAndId(String sql) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(sql, true);
		ArrayList<SimpleIdDef> ret = connHandler
				.retrieveArray(SimpleIdDef.class);
		return ret.toArray(new SimpleIdDef[0]);
	}

	/**
	 * @see WorkFlowDAO#createProcessTemplate(ProcessTemplate pt)
	 */
	public final ProcessTemplate createProcessTemplate(ProcessTemplate pt)
			throws DAOException {
		checkNull(pt);
		ProcessTemplate ret = pt;
		// First, insert the ProcessTemplate into the database.
		Integer key = nextSequenceVal("S_Process_Template_Id");

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createProcessTemplate", false);

		connHandler.setInteger(1, key);
		connHandler.setString(2, ret.getProcessCd());
		connHandler.setString(3, ret.getProcessTemplateNm());
		connHandler.setString(4, ret.getProcessTemplateDsc());
		connHandler.setInteger(5, ret.getExpectedDuration());
		connHandler.setInteger(6, ret.getJeopardyDuration());
		connHandler.setString(7, ret.getDynamicInd());
		connHandler.setString(8, ret.getDeprecatedInd());
		connHandler.setInteger(9, ret.getEarliestProvInterval());
		connHandler.setInteger(10, ret.getMinProvInterval());

		connHandler.update();

		ret.setProcessTemplateId(key);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#createProcessDataTemplate(ProcessDataTemplate ptd).
	 */
	public final ProcessDataTemplate createProcessDataTemplate(
			ProcessDataTemplate ptd) throws DAOException {
		checkNull(ptd);
		ProcessDataTemplate ret = ptd;

		Integer key = nextSequenceVal("S_Process_Data_Id");

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createProcessDataTemplate", false);

		connHandler.setInteger(1, key);
		connHandler.setInteger(2, ret.getServiceDetailDefId());
		connHandler.setInteger(3, ret.getProcessTemplateId());
		connHandler.setString(4, ret.getDataTemplateNm());
		connHandler.setString(5, ret.getDeprecatedInd());
		connHandler.setInteger(6, ret.getLineOrderNum());
		connHandler.setString(7, ret.getOeRequiredInd());
		connHandler.setInteger(8, ret.getActivityTemplateId());

		connHandler.update();

		ret.setDataTemplateId(key);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#createActivityTemplate(ActivityTemplate at)
	 */
	public final ActivityTemplate createActivityTemplate(ActivityTemplate at)
			throws DAOException {
		checkNull(at);
		ActivityTemplate ret = at;
		Integer key = nextSequenceVal("S_Activity_Template_Id");

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createActivityTemplate", false);

		connHandler.setInteger(1, key);
		connHandler.setInteger(2, ret.getProcessTemplateId());
		connHandler.setInteger(3, ret.getActivityTemplateDefId());
		connHandler.setInteger(4, ret.getServiceId());
		connHandler.setInteger(5, ret.getExpectedDuration());
		connHandler.setString(6, ret.getMilestoneInd());
		connHandler.setString(7, ret.getInitTaskInd());
		connHandler.setString(8, ret.getInTransitionDefCd());
		connHandler.setString(9, ret.getOutTransitionDefCd());
		connHandler.setString(10, ret.getAggregate());
		connHandler.setString(11, ret.getRoleCd());
		connHandler.setInteger(12, ret.getXloc());
		connHandler.setInteger(13, ret.getYloc());
		connHandler.setString(14, ret.getActivityTemplateNm());
		connHandler.setString(15, ret.getDeprecatedInd());
		connHandler.setString(16, ret.getActivityData());
		connHandler.setInteger(17, ret.getJeopardyDuration());

		connHandler.update();

		ret.setActivityTemplateId(key);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#createActivityTemplateDef(ActivityTemplateDef atd)
	 */
	public final ActivityTemplateDef createActivityTemplateDef(
			ActivityTemplateDef atd) throws DAOException {
		checkNull(atd);
		ActivityTemplateDef ret = atd;
		Integer key = nextSequenceVal("S_Activity_Template_Def_Id");

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createActivityTemplateDef", false);

		connHandler.setInteger(1, key);
		connHandler.setString(2, ret.getActivityTemplateNm());
		connHandler.setString(3, ret.getActivityTemplateDsc());
		connHandler.setString(4, ret.getPerformerTypeCd());
		connHandler.setString(5, ret.getAutomaticClassNm());
		connHandler.setInteger(6, ret.getProcessTemplateId());
		connHandler.setString(7, ret.getHiddenIndStr());
		connHandler.setString(8, ret.getActivityUrl());
		connHandler.setString(9, ret.getTerminalInd());
		connHandler.setInteger(10, ret.getExpectedDuration());
		connHandler.setString(11, ret.getActivityTemplateTypeCd());
		connHandler.setInteger(12, ret.getNumberOfRetries());
		connHandler.setInteger(13, ret.getRetryInterval());
		connHandler.setString(14, ret.getDeprecatedInd());
		connHandler.setInteger(15, ret.getJeopardyDuration());
		connHandler.setInteger(16, ret.getUndoActivityTemplateDefId());

		connHandler.update();

		atd.setActivityTemplateDefId(key);
		atd.setLastModified(1);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#createProcessActivity(ProcessActivity pa)
	 */
	public final ProcessActivity createProcessActivity(ProcessActivity pa)
			throws DAOException {
		checkNull(pa);
		ProcessActivity ret = pa;

		// logger.error("DWL:  ActivityStatusCd=" + pa.getActivityStatusCd() +
		// ", userId=" + pa.getUserId() + ", loopCnt=" + pa.getLoopCnt() +
		// ", processId=" + pa.getProcessId() + ", processTemplateId=" +
		// pa.getProcessTemplateId(), new Exception()); //DWL debug 3206

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createProcessActivity", false);

		connHandler.setInteger(1, ret.getActivityTemplateId());
		connHandler.setInteger(2, ret.getProcessId());
		connHandler.setInteger(3, ret.getUserId());
		connHandler.setInteger(4, ret.getLoopCnt());
		connHandler.setString(5, ret.getActivityStatusCd());
		connHandler.setTimestamp(6, ret.getStartDt());
		connHandler.setTimestamp(7, ret.getReadyDt());
		connHandler.setTimestamp(8, ret.getEndDt());
		connHandler.setTimestamp(9, ret.getDueDt());
		connHandler.setString(10, ret.getPerformerTypeCd());
		connHandler.setInteger(11, ret.getNumberOfRetries());
		connHandler.setInteger(12, ret.getRetryInterval());
		connHandler.setTimestamp(13, ret.getJeopardyDt());
		connHandler.setString(14, ret.getCurrent());
		connHandler.setInteger(15, null); // CONTACT_ID
		connHandler.setString(16, ret.getExtendProcessEndDate());
		connHandler.setInteger(17, ret.getActivityReferralTypeId());

		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#createProcess(WorkFlowProcess p)
	 */
	public final WorkFlowProcess createProcess(WorkFlowProcess p)
			throws DAOException {
		checkNull(p);
		WorkFlowProcess ret = p;

		Integer key = nextSequenceVal("S_Process_Id");

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createProcess", false);

		connHandler.setInteger(1, key);
		connHandler.setInteger(2, ret.getUserId());
		connHandler.setInteger(3, ret.getFacilityId());
		connHandler.setInteger(4, ret.getServiceId());
		connHandler.setInteger(5, ret.getProcessTemplateId());
		connHandler.setInteger(6, ret.getParentProcessId());
		connHandler.setInteger(7, ret.getActivityTemplateId());
		connHandler.setInteger(8, ret.getExternalId());
		connHandler.setString(9, ret.getExpedite());
		connHandler.setTimestamp(10, ret.getStartDt());
		connHandler.setTimestamp(11, ret.getEndDt());
		connHandler.setTimestamp(12, ret.getDueDt());
		connHandler.setTimestamp(13, ret.getJeopardyDt());
		connHandler.setTimestamp(14, ret.getReadyDt());

		connHandler.update();

		ret.setProcessId(key);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#createTransition(Transition trans).
	 */
	public final Transition createTransition(Transition trans)
			throws DAOException {
		checkNull(trans);
		Transition ret = trans;

		String deprecated = translateBooleanToIndicator(trans.isDeprecated());

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createTransitionData", false);

		connHandler.setInteger(1, ret.getFromId());
		connHandler.setInteger(2, ret.getToId());
		connHandler.setInteger(3, ret.getProcessTemplateId());
		connHandler.setString(4, ret.getTransitionCode());
		connHandler.setString(5, ret.getCondition());
		connHandler.setString(6, deprecated);

		connHandler.update();
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#createDataField(DataField df).
	 */
	public final DataField createDataField(DataField df) throws DAOException {
		checkNull(df);
		DataField ret = df;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createProcessData", false);

		connHandler.setInteger(1, ret.getDataTemplateId());
		connHandler.setInteger(2, ret.getProcessId());
		connHandler.setInteger(3, ret.getServiceDetailId());
		connHandler.setString(4, ret.getDataValue());

		connHandler.update();

		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#deprecateProcessTemplate(ProcessTemplate pt).
	 */
	public final boolean deprecateProcessTemplate(ProcessTemplate pt)
			throws DAOException {
		return deprecateProcessTemplateItems(
				"WorkFlowSQL.deprecateProcessTemplate", pt);
	}

	/**
	 * @see WorkFlowDAO#deprecateActivityTemplates(ProcessTemplate pt).
	 */
	public final boolean deprecateActivityTemplates(ProcessTemplate pt)
			throws DAOException {
		return deprecateProcessTemplateItems(
				"WorkFlowSQL.deprecateProcessTemplates", pt);
	}

	/**
	 * @see WorkFlowDAO#deprecateTransitions(ProcessTemplate pt).
	 */
	public final boolean deprecateTransitions(ProcessTemplate pt)
			throws DAOException {
		return deprecateProcessTemplateItems(
				"WorkFlowSQL.deprecateTransitions", pt);
	}

	/**
	 * @see WorkFlowDAO#deprecateSubFlowTemplate(ProcessTemplate pt).
	 */
	public final boolean deprecateSubFlowTemplate(ProcessTemplate pt)
			throws DAOException {
		return deprecateProcessTemplateItems(
				"WorkFlowSQL.deprecateSubflowTemplate", pt);
	}

	private boolean deprecateProcessTemplateItems(String sqlString,
			ProcessTemplate pt) throws DAOException {
		checkNull(pt);
		ConnectionHandler connHandler = new ConnectionHandler(sqlString, false);

		connHandler.setInteger(1, pt.getLastModified() + 1);
		connHandler.setInteger(2, pt.getProcessTemplateId());
		connHandler.setInteger(3, pt.getLastModified());

		return connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#deprecateSubFlowTemplate(ProcessTemplate pt).
	 */
	public final boolean updateInPlaceActivityTemplate(ProcessTemplate pt,
			ActivityTemplate at) throws DAOException {
		return updateInPlaceActivityTemplate(
				"WorkFlowSQL.updateInPlaceActivityTemplate", pt, at);
	}

	private boolean updateInPlaceActivityTemplate(String sqlString,
			ProcessTemplate pt, ActivityTemplate at) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(sqlString, false);

		connHandler.setInteger(1, at.getXloc());
		connHandler.setInteger(2, at.getYloc());
		connHandler.setInteger(3,
				(at.getLastModified() == null ? 1 : at.getLastModified() + 1));
		connHandler.setInteger(4, pt.getProcessTemplateId());
		connHandler.setInteger(5, at.getActivityTemplateId());
		connHandler.setInteger(6, at.getLastModified());

		return connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#deprecateProcessDataTemplate(Integer processTemplateId).
	 */
	public final boolean deprecateProcessDataTemplate(Integer processTemplateId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.deprecateProcessDataTemplateByPTID", false);

		connHandler.setInteger(1, processTemplateId);

		return connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessActivities(Integer processId).
	 */
	public final ProcessActivity[] retrieveProcessActivities(Integer processId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveProcessActivitiesForPid", true);

		connHandler.setInteger(1, processId);

		ArrayList<ProcessActivity> ret = connHandler
				.retrieveArray(ProcessActivity.class);

		return ret.toArray(new ProcessActivity[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveBlockedActivityList(String subSystem,String
	 *      type)
	 */
	public final ProcessActivity[] retrieveBlockedActivityList(
			String subSystem, String type) throws DAOException {
		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveBlockedList"));

		query.append("order by PA.Ready_Dt");
		ConnectionHandler connHandler = new ConnectionHandler(query.toString(),
				true);

		connHandler.setString(1, subSystem);
		connHandler.setString(2, type);

		ArrayList<ProcessActivity> ret = connHandler
				.retrieveArray(ProcessActivity.class);

		return ret.toArray(new ProcessActivity[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveActivitiesToUnDeadend(int permitId).
	 */
	public final ProcessActivity[] retrieveActivitiesToUnDeadend(int permitId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActivitiesToUnDeadend", true);

		connHandler.setInteger(1, permitId);

		ArrayList<ProcessActivity> ret = connHandler
				.retrieveArray(ProcessActivity.class);

		return ret.toArray(new ProcessActivity[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityList(String activityName, String
	 *      activityStatusCd).
	 */
	public final ProcessActivity[] retrieveActivityList(String activityName,
			String activityStatusCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.searchActivityByNameAndStatus", true);

		connHandler.setString(1, activityName);
		connHandler.setString(2, activityStatusCd);

		ArrayList<ProcessActivity> ret = connHandler
				.retrieveArray(ProcessActivity.class);

		return ret.toArray(new ProcessActivity[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityList(String subSystem, String type,
	 *      Integer userId)
	 */
	public final ProcessActivity[] retrieveActivityList(String subSystem,
			String type, Integer userId) throws DAOException {

		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveWorkingList"));

		query.append("order by PA.Ready_Dt");
		ConnectionHandler connHandler = new ConnectionHandler(query.toString(),
				true);

		connHandler.setString(1, subSystem);
		connHandler.setString(2, type);
		if (userId != null) {
			connHandler.setInteger(3, userId.intValue());
		} else {
			connHandler.setInteger(3, 0);
		}

		ArrayList<ProcessActivity> ret = connHandler
				.retrieveArray(ProcessActivity.class);

		return ret.toArray(new ProcessActivity[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessActivity(Integer processId, Integer
	 *      activityTemplId).
	 */
	public final ProcessActivity retrieveProcessActivity(Integer processId,
			Integer activityTemplId) throws DAOException {
		ProcessActivity ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveProcessActivity", true);

		connHandler.setInteger(1, processId);
		connHandler.setInteger(2, activityTemplId);

		ret = (ProcessActivity) connHandler.retrieve(ProcessActivity.class);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#modifyTransition(Transition trans).
	 */
	public final Transition modifyTransition(Transition trans)
			throws DAOException {
		checkNull(trans);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyTransition", false);

		String deprecated = translateBooleanToIndicator(trans.isDeprecated());

		connHandler.setString(1, trans.getTransitionCode());
		connHandler.setString(2, trans.getCondition());
		connHandler.setString(3, deprecated);
		connHandler.setInteger(4, trans.getFromId());
		connHandler.setInteger(5, trans.getToId());
		connHandler.setInteger(6, trans.getProcessTemplateId());

		connHandler.update();
		trans.setLastModified(trans.getLastModified() + 1);
		return trans;
	}

	/**
	 * @see WorkFlowDAO#modifyProcessActivity(ProcessActivity pa).
	 */
	public final ProcessActivity modifyProcessActivity(ProcessActivity pa)
			throws DAOException {
		checkNull(pa);
		// logger.error("DWL:  ActivityStatusCd=" + pa.getActivityStatusCd() +
		// ", userId=" + pa.getUserId() + ", processId=" + pa.getProcessId() +
		// ", processTemplateId=" +
		// pa.getProcessTemplateId(), new Exception()); //DWL debug 3206
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyProcessActivity", false);

		connHandler.setInteger(1, pa.getUserId());
		connHandler.setString(2, pa.getActivityStatusCd());
		connHandler.setTimestamp(3, pa.getStartDt());
		connHandler.setTimestamp(4, pa.getReadyDt());
		connHandler.setTimestamp(5, pa.getEndDt());
		connHandler.setTimestamp(6, pa.getDueDt());
		connHandler.setInteger(7, pa.getNumberOfRetries());
		connHandler.setInteger(8, pa.getRetryInterval());
		connHandler.setTimestamp(9, pa.getJeopardyDt());
		connHandler.setInteger(10, pa.getLastModified() + 1);
		connHandler.setString(11, pa.getCurrent());
		connHandler.setString(12, pa.getExtendProcessEndDate());
		connHandler.setInteger(13, pa.getContactId());
		connHandler.setInteger(14, pa.getActivityReferralTypeId());
		connHandler.setInteger(15, pa.getProcessId());
		connHandler.setInteger(16, pa.getLoopCnt());
		connHandler.setInteger(17, pa.getActivityTemplateId());
		connHandler.setInteger(18, pa.getLastModified());

		connHandler.update();
		pa.setLastModified(pa.getLastModified() + 1);
		return pa;
	}

	/**
	 * @see WorkFlowDAO#modifyProcessActivityViewedState(ProcessActivity pa).
	 */
	public final ProcessActivity modifyProcessActivityViewedState(
			ProcessActivity pa) throws DAOException {
		checkNull(pa);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyProcessActivityViewedState", false);

		connHandler.setInteger(1, pa.getProcessId());
		connHandler.setInteger(2, pa.getLoopCnt());
		connHandler.setInteger(3, pa.getActivityTemplateId());
		connHandler.setInteger(4, pa.getLastModified());

		connHandler.update();
		pa.setViewed(true);
		return pa;
	}

	/**
	 * @see WorkFlowDAO#modifyProcessActivityViewedState(ProcessActivity pa).
	 */
	public final ProcessActivity modifyProcessActivityEndDate(ProcessActivity pa)
			throws DAOException {
		checkNull(pa);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyProcessActivityEndDate", false);

		connHandler.setTimestamp(1, pa.getEndDt());
		connHandler.setInteger(2, pa.getProcessId());
		connHandler.setInteger(3, pa.getLoopCnt());
		connHandler.setInteger(4, pa.getActivityTemplateId());

		connHandler.update();
		return pa;
	}

	/**
	 * @see WorkFlowDAO#removeActivityDetailData(ActivityTemplateDef atd)
	 */
	public final void removeActivityDetailData(ActivityTemplateDef atd)
			throws DAOException {
		checkNull(atd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.removeActivityDetailData", false);

		connHandler.setInteger(1, atd.getActivityTemplateDefId());

		connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#modifyActivityTemplate(ActivityTemplate at).
	 */
	public final ActivityTemplate modifyActivityTemplate(ActivityTemplate at)
			throws DAOException {
		checkNull(at);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyActivityTemplate", false);

		connHandler.setInteger(1, at.getProcessTemplateId());
		connHandler.setInteger(2, at.getActivityTemplateDefId());
		connHandler.setInteger(3, at.getServiceId());
		connHandler.setInteger(4, at.getExpectedDuration());
		connHandler.setString(5, at.getMilestoneInd());
		connHandler.setString(6, at.getInitTaskInd());
		connHandler.setString(7, at.getInTransitionDefCd());
		connHandler.setString(8, at.getOutTransitionDefCd());
		connHandler.setString(9, at.getAggregate());
		connHandler.setString(10, at.getRoleCd());
		connHandler.setInteger(11, at.getXloc());
		connHandler.setInteger(12, at.getYloc());
		connHandler.setString(13, at.getActivityTemplateNm());
		connHandler.setString(14, at.getDeprecatedInd());
		connHandler.setString(15, at.getActivityData());
		connHandler.setInteger(16, at.getJeopardyDuration());
		connHandler.setInteger(17, at.getLastModified() + 1);
		connHandler.setInteger(18, at.getActivityTemplateId());
		connHandler.setInteger(19, at.getLastModified());

		connHandler.update();
		at.setLastModified(at.getLastModified() + 1);
		return at;
	}

	/**
	 * @see WorkFlowDAO#modifyActivityTemplateDef(ActivityTemplateDef at).
	 */
	public final ActivityTemplateDef modifyActivityTemplateDef(
			ActivityTemplateDef atd) throws DAOException {
		checkNull(atd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyActivityTemplateDef", false);

		connHandler.setString(1, atd.getActivityTemplateNm());
		connHandler.setString(2, atd.getActivityTemplateDsc());
		connHandler.setString(3, atd.getPerformerTypeCd());
		connHandler.setString(4, atd.getAutomaticClassNm());
		connHandler.setInteger(5, atd.getProcessTemplateId());
		connHandler.setString(6, atd.getHiddenIndStr());
		connHandler.setString(7, atd.getTerminalInd());
		connHandler.setString(8, atd.getActivityTemplateTypeCd());
		connHandler.setInteger(9, atd.getUndoActivityTemplateDefId());
		connHandler.setInteger(10, atd.getExpectedDuration());
		connHandler.setInteger(11, atd.getNumberOfRetries());
		connHandler.setInteger(12, atd.getRetryInterval());
		connHandler.setString(13, atd.getDeprecatedInd());
		connHandler.setInteger(14, atd.getJeopardyDuration());
		connHandler.setInteger(15, atd.getLastModified() + 1);
		connHandler.setInteger(16, atd.getActivityTemplateDefId());
		connHandler.setInteger(17, atd.getLastModified());

		connHandler.update();
		atd.setLastModified(atd.getLastModified() + 1);
		return atd;
	}

	/**
	 * @see WorkFlowDAO#modifyDataField(DataField df).
	 */
	public final DataField modifyDataField(DataField df) throws DAOException {
		checkNull(df);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyProcessData", false);

		connHandler.setInteger(1, df.getCustomDetailTypeId());
		// Database on this colum only can have 2500
		String data = df.getDataValue();
		if (data.length() >= 2500) {
			data = data.substring(0, 2490);
			data = data + "...";
		}
		connHandler.setString(2, data);
		connHandler.setInteger(3, df.getLastModified() + 1);
		connHandler.setInteger(4, df.getDataTemplateId());
		connHandler.setInteger(5, df.getProcessId());
		connHandler.setInteger(6, df.getLastModified());

		connHandler.update();
		df.setLastModified(df.getLastModified() + 1);
		return df;
	}

	/**
	 * @see WorkFlowDAO#modifyProcess(Process p).
	 */
	public final WorkFlowProcess modifyProcess(WorkFlowProcess p)
			throws DAOException {
		checkNull(p);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyProcess", false);

		connHandler.setInteger(1, p.getUserId());
		connHandler.setInteger(2, p.getFacilityId());
		connHandler.setInteger(3, p.getServiceId());
		connHandler.setInteger(4, p.getProcessTemplateId());
		connHandler.setInteger(5, p.getParentProcessId());
		connHandler.setInteger(6, p.getActivityTemplateId());
		connHandler.setInteger(7, p.getExternalId());
		connHandler.setString(8, p.getExpedite());
		connHandler.setTimestamp(9, p.getStartDt());
		connHandler.setTimestamp(10, p.getEndDt());
		connHandler.setTimestamp(11, p.getDueDt());
		connHandler.setTimestamp(12, p.getJeopardyDt());
		connHandler.setTimestamp(13, p.getReadyDt());
		connHandler.setInteger(14, p.getLastModified() + 1);
		connHandler.setInteger(15, p.getProcessId());
		connHandler.setInteger(16, p.getLastModified());

		connHandler.update();
		p.setLastModified(p.getLastModified() + 1);
		return p;
	}

	/**
	 * @see WorkFlowDAO#modifyProcessTemplate(ProcessTemplate pt).
	 */
	public final ProcessTemplate modifyProcessTemplate(ProcessTemplate pt)
			throws DAOException {
		checkNull(pt);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyProcessTemplate", false);

		connHandler.setString(1, pt.getProcessCd());
		connHandler.setString(2, pt.getProcessTemplateNm());
		connHandler.setString(3, pt.getProcessTemplateDsc());
		connHandler.setInteger(4, pt.getExpectedDuration());
		connHandler.setInteger(5, pt.getJeopardyDuration());
		connHandler.setString(6, pt.getDynamicInd());
		connHandler.setString(7, pt.getDeprecatedInd());
		connHandler.setInteger(8, pt.getEarliestProvInterval());
		connHandler.setInteger(9, pt.getMinProvInterval());
		connHandler.setInteger(10, pt.getLastModified() + 1);
		connHandler.setInteger(11, pt.getProcessTemplateId());
		connHandler.setInteger(12, pt.getLastModified());

		connHandler.update();
		pt.setLastModified(pt.getLastModified() + 1);
		return pt;
	}

	/**
	 * @see WorkFlowDAO#modifyProcessDataTemplate(ProcessDataTemplate ptd).
	 */
	public final ProcessDataTemplate modifyProcessDataTemplate(
			ProcessDataTemplate ptd) throws DAOException {
		checkNull(ptd);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.deprecateProcessDataTemplate", false);

		connHandler.setInteger(1, ptd.getLastModified() + 1);
		connHandler.setInteger(2, ptd.getDataTemplateId());
		connHandler.setInteger(3, ptd.getLastModified());

		connHandler.update();
		ptd.setLastModified(ptd.getLastModified() + 1);
		return ptd;
	}

	/**
	 * @see WorkFlowDAO#retrieveTransitions(Integer processTemplateId, boolean
	 *      includeAll).
	 */
	public final Transition[] retrieveTransitions(Integer processTemplateId,
			boolean includeAll) throws DAOException {
		if (includeAll) {
			return retrieveTransitions(processTemplateId,
					"WorkFlowSQL.retrieveAllTransitionsForPTID");
		}

		return retrieveTransitions(processTemplateId,
				"WorkFlowSQL.retrieveActiveTransitionsForPTID");
	}

	/**
	 * @see WorkFlowDAO#retrieveDataFieldsForProcess(Integer processId).
	 */
	public final DataField[] retrieveDataFieldsForProcess(Integer processId)
			throws DAOException {
		return retrieveDataFields(processId,
				"WorkFlowSQL.retrieveAllDatafieldsForProcess");
	}

	/**
	 * @see WorkFlowDAO#retrieveDataFieldsForProcessTemplate(Integer processId).
	 */
	public final DataField[] retrieveDataFieldsForProcessTemplate(
			Integer processTemplateId) throws DAOException {
		return retrieveDataFields(processTemplateId,
				"WorkFlowSQL.retrieveAllDatafieldsForProcessTemplate");
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityTemplateDef(Integer atdId).
	 */
	public final ActivityTemplateDef retrieveActivityTemplateDef(Integer atdId)
			throws DAOException {
		ActivityTemplateDef ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActivityTemplateDef", true);

		connHandler.setInteger(1, atdId);

		ret = (ActivityTemplateDef) connHandler
				.retrieve(ActivityTemplateDef.class);
		populateDependentDetails(ret);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#retrieveSubFlowDef(String subflowName)
	 */
	public final ActivityTemplateDef retrieveSubFlowDef(String subflowName)
			throws DAOException {
		ActivityTemplateDef ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveSubflowAtdByName", true);

		connHandler.setString(1, subflowName);

		ret = (ActivityTemplateDef) connHandler
				.retrieve(ActivityTemplateDef.class);
		populateDependentDetails(ret);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityTemplateDef(Integer atdId).
	 */
	public final ActivityTemplateDef retrieveSubflowActivityTemplateDef(
			Integer processTemplateId, String processTemplateNm)
			throws DAOException {
		ActivityTemplateDef ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveSubflowActivityTemplateDef", true);

		connHandler.setInteger(1, processTemplateId);
		// connHandler.setString(2, processTemplateNm);

		ret = (ActivityTemplateDef) connHandler
				.retrieve(ActivityTemplateDef.class);
		populateDependentDetails(ret);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#retrieveSubFlowsByProcessCd(String processCd).
	 */
	public final ActivityTemplateDef[] retrieveSubFlowsByProcessCd(
			String processCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveSubflowByProcessCd", true);

		connHandler.setString(1, processCd);

		ArrayList<ActivityTemplateDef> ret = connHandler
				.retrieveArray(ActivityTemplateDef.class);
		for (BaseDBObject bdo : ret) {
			ActivityTemplateDef atd = (ActivityTemplateDef) bdo;
			populateDependentDetails(atd);
		}

		return ret.toArray(new ActivityTemplateDef[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityTemplateDefs(String templateCd).
	 */
	public final ActivityTemplateDef[] retrieveActivityTemplateDefs(
			String templateCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActivityTemplateDefsByType", true);

		connHandler.setString(1, templateCd);

		ArrayList<ActivityTemplateDef> ret = connHandler
				.retrieveArray(ActivityTemplateDef.class);
		for (BaseDBObject bdo : ret) {
			ActivityTemplateDef atd = (ActivityTemplateDef) bdo;
			populateDependentDetails(atd);
		}

		return ret.toArray(new ActivityTemplateDef[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityTemplate(Integer activityTemplateId).
	 */
	public final ActivityTemplate retrieveActivityTemplate(
			Integer activityTemplateId) throws DAOException {
		ActivityTemplate ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActivityTemplate", true);

		connHandler.setInteger(1, activityTemplateId);

		ret = (ActivityTemplate) connHandler.retrieve(ActivityTemplate.class);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityTemplatesForProcessTemplate(Integer
	 *      processTemplateId).
	 */
	public final ActivityTemplate[] retrieveActivityTemplatesForProcessTemplate(
			Integer processTemplateId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActvitiyTemplateForPTID", true);

		connHandler.setInteger(1, processTemplateId);

		ArrayList<ActivityTemplate> ret = connHandler
				.retrieveArray(ActivityTemplate.class);

		return ret.toArray(new ActivityTemplate[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessDataTemplates(Integer procTemplId).
	 */
	public final ProcessDataTemplate[] retrieveProcessDataTemplates(
			Integer procTemplId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveProcessDataTemplateForPTID", true);

		connHandler.setInteger(1, procTemplId);

		ArrayList<ProcessDataTemplate> ret = connHandler
				.retrieveArray(ProcessDataTemplate.class);

		return ret.toArray(new ProcessDataTemplate[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessDataTemplatesViaCatalog(Integer catDefId,
	 *      String wfType).
	 */
	public final ProcessDataTemplate[] retrieveProcessDataTemplatesViaCatalog(
			Integer catDefId, String wfType) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrievePDTForCatalogId", true);

		connHandler.setInteger(1, catDefId);
		connHandler.setString(2, wfType);

		ArrayList<ProcessDataTemplate> ret = connHandler
				.retrieveArray(ProcessDataTemplate.class);

		return ret.toArray(new ProcessDataTemplate[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveProcess(Integer processId).
	 */
	public final WorkFlowProcess retrieveProcess(Integer processId)
			throws DAOException {
		WorkFlowProcess ret = null;

		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveProcessById"));

		query.append(" AND P.process_id = ?");
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(query.toString());

		connHandler.setInteger(1, processId);

		ret = (WorkFlowProcess) connHandler.retrieve(WorkFlowProcess.class);

		return ret;
	}

	public WorkFlowProcess[] retrieveParentActiveProcessFlow(
			Integer parentProcessId) throws DAOException {
		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveProcessById"));
		query.append(" AND P.PARENT_PROCESS_ID = ?");
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(query.toString());
		connHandler.setInteger(1, parentProcessId);
		ArrayList<WorkFlowProcess> ret = connHandler
				.retrieveArray(WorkFlowProcess.class);
		return ret.toArray(new WorkFlowProcess[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveInWorkProcesses().
	 */
	public final WorkFlowProcess[] retrieveInWorkProcesses()
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveInworkProcesses", true);

		ArrayList<WorkFlowProcess> ret = connHandler
				.retrieveArray(WorkFlowProcess.class);

		return ret.toArray(new WorkFlowProcess[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveSubFlowProcess(Integer, Integer).
	 */
	public final WorkFlowProcess retrieveSubFlowProcess(Integer processId,
			Integer actTemplId) throws DAOException {
		WorkFlowProcess ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveSubflowProcess", true);

		connHandler.setInteger(1, processId);
		connHandler.setInteger(2, actTemplId);

		ret = (WorkFlowProcess) connHandler.retrieve(WorkFlowProcess.class);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessTemplate(Integer procTemplId).
	 */
	public final ProcessTemplate retrieveProcessTemplate(Integer procTemplId)
			throws DAOException {
		ProcessTemplate ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveProcessTemplateById", true);

		connHandler.setInteger(1, procTemplId);

		ret = (ProcessTemplate) connHandler.retrieve(ProcessTemplate.class);

		if (ret != null) {
			ActivityTemplate[] ats = retrieveActivityTemplatesForProcessTemplate(procTemplId);

			ret.setActivityTemplate(ats);
		}

		return ret;
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessTemplates (boolean includeDeprecated,
	 *      boolean includeDynamic).
	 */
	public final ProcessTemplate[] retrieveProcessTemplates(
			boolean includeDeprecated, boolean includeDynamic)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveAllProcessTemplates", true);

		ArrayList<ProcessTemplate> ret = connHandler
				.retrieveArray(ProcessTemplate.class);

		for (int i = 0; i < ret.size(); i++) {
			ProcessTemplate pt = ret.get(i);

			// Remove deprecated and dynamic if that's what is requested.
			if ((!includeDeprecated && (pt.getDeprecatedInd().equals("Y")))
					|| (!includeDynamic && (pt.getDynamicInd().equals("Y")))) {
				ret.remove(i--);
			}
		}
		return ret.toArray(new ProcessTemplate[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessTemplatesByType (String subSystem,
	 *      boolean includeDeprecated, boolean includeDynamic).
	 */
	public final ProcessTemplate[] retrieveProcessTemplatesByType(
			String subSystem, boolean includeDeprecated, boolean includeDynamic)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveAllProcessTemplatesByType", true);

		connHandler.setString(1, subSystem);

		ArrayList<ProcessTemplate> ret = connHandler
				.retrieveArray(ProcessTemplate.class);

		for (int i = 0; i < ret.size(); i++) {
			ProcessTemplate pt = ret.get(i);

			// Remove deprecated and dynamic if that's what is requested.
			if ((!includeDeprecated && (pt.getDeprecatedInd().equals("Y")))
					|| (!includeDynamic && (pt.getDynamicInd().equals("Y")))) {
				ret.remove(i--);
			}
		}
		return ret.toArray(new ProcessTemplate[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessTemplateForComposite (String type,
	 *      Integer id).
	 */
	public final ProcessTemplate retrieveProcessTemplateForComposite(
			String type, Integer id) throws DAOException {
		ProcessTemplate ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveCompositeProcesssTemplate", true);

		connHandler.setString(1, type + ": " + id.toString());

		ret = (ProcessTemplate) connHandler.retrieve(ProcessTemplate.class);

		return ret;
	}

	/**
	 * @see WorkFlowDAO#moveActivity(Integer newParentProcessId, Integer
	 *      newActivityTemplateId, Integer newOrderId, Integer subflowProcessId)
	 *      .
	 */
	public final void moveSubFlow(Integer newParentProcessId,
			Integer newActivityTemplateId, Integer newOrderId,
			Integer subflowProcessId) throws DAOException {
		checkNull(newParentProcessId);
		checkNull(newActivityTemplateId);
		checkNull(newOrderId);
		checkNull(subflowProcessId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.moveSubflow", false);

		connHandler.setInteger(1, newParentProcessId);
		connHandler.setInteger(2, newActivityTemplateId);
		connHandler.setInteger(3, newOrderId);
		connHandler.setInteger(4, subflowProcessId);

		connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#removeProcessActivities(Integer processId)
	 */
	public final void removeProcessActivities(Integer processId)
			throws DAOException {
		checkNull(processId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.removeProcessActivities", false);

		connHandler.setInteger(1, processId);

		connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#removeProcess(Integer processId)
	 */
	public final void removeProcess(Integer processId) throws DAOException {
		checkNull(processId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.removeProcess", false);

		connHandler.setInteger(1, processId);

		connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#removeTransitions(Integer actTemplId, Integer
	 *      parentPtId)
	 */
	public final void removeTransitions(Integer actTemplId, Integer parentPtId)
			throws DAOException {
		checkNull(actTemplId);
		checkNull(parentPtId);
		ConnectionHandler connHandler1 = new ConnectionHandler(
				"WorkFlowSQL.removeFromTransitions", false);
		ConnectionHandler connHandler2 = new ConnectionHandler(
				"WorkFlowSQL.removeToTransitions", false);

		connHandler1.setInteger(1, actTemplId);
		connHandler1.setInteger(2, parentPtId);
		connHandler2.setInteger(1, actTemplId);
		connHandler2.setInteger(2, parentPtId);

		connHandler1.remove();
		connHandler2.remove();
	}

	/**
	 * @see WorkFlowDAO#removeProcessActivity(Integer actTemplId, Integer
	 *      actProcessId)
	 */
	public final void removeProcessActivity(Integer actTemplId,
			Integer actProcessId, Integer actLoopCnt) throws DAOException {
		checkNull(actTemplId);
		checkNull(actProcessId);
		checkNull(actLoopCnt);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.removeProcessActivity", false);

		connHandler.setInteger(1, actTemplId);
		connHandler.setInteger(2, actProcessId);
		connHandler.setInteger(3, actLoopCnt);

		connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#removeActivityTemplate(Integer actTemplId)
	 */
	public final void removeActivityTemplate(Integer actTemplId)
			throws DAOException {
		checkNull(actTemplId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.removeActivityTemplate", false);

		connHandler.setInteger(1, actTemplId);

		connHandler.remove();
	}

	/**
	 * @see WorkFlowDAO#createDependentDetailData(ActivityTemplateDef atd)
	 */
	public final ActivityTemplateDef createDependentDetailData(
			ActivityTemplateDef atd) throws DAOException {
		checkNull(atd);
		Integer[] detailDataIds = atd.getDependentDetailIds();

		// If we don't have any dependencies, then we are done.
		if ((detailDataIds != null) && (detailDataIds.length > 0)) {
			ConnectionHandler connHandler = new ConnectionHandler(
					"WorkFlowSQL.createATDServiceDataDetailIds", false);

			try {
				for (Integer i : detailDataIds) {
					connHandler.setInteger(1, atd.getActivityTemplateDefId());
					connHandler.setInteger(2, i);
					connHandler.updateNoClose();
				}
			} finally {
				connHandler.close();
			}
		}
		return atd;
	}

	private Transition[] retrieveTransitions(Integer processTemplateId,
			String sqlStmt) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(sqlStmt, true);

		connHandler.setInteger(1, processTemplateId);

		ArrayList<Transition> ret = connHandler.retrieveArray(Transition.class);

		return ret.toArray(new Transition[0]);
	}

	private DataField[] retrieveDataFields(Integer id, String sqlStmt)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(sqlStmt, true);

		connHandler.setInteger(1, id);

		ArrayList<DataField> ret = connHandler.retrieveArray(DataField.class);

		for (BaseDBObject dbo : ret) {
			DataField df = (DataField) dbo;
			populateDataDetail(df);
		}

		return ret.toArray(new DataField[0]);
	}

	/**
	 * @param df
	 * @throws DAOException
	 */
	private void populateDataDetail(DataField df) throws DAOException {
		// DetailDataDAO ddDao = (DetailDataDAO) (DAOFactory
		// .getDAO("DetailDataDAO", CommonConst.READONLY_SCHEMA));

		df.setDataDetail(readOnlyDetailDataDAO.retrieveDataDetail(df
				.getCustomDetailTypeId()));
	}

	/**
	 * @see WorkFlowDAO#retrieveServiceDetailDefBases(Integer atdId)
	 */
	public final DataDetailBase[] retrieveServiceDetailDefBases(Integer atdId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveDepDataDetailBases", true);

		connHandler.setInteger(1, atdId);

		ArrayList<DataDetailBase> ret = connHandler
				.retrieveArray(DataDetailBase.class);

		return ret.toArray(new DataDetailBase[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveProcessDefs()
	 */
	public final SimpleDef[] retrieveProcessDefs() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveProcessDefs", true);

		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see WorkFlowDAO#retrievePerformerDefs()
	 */
	public final PerformerDef[] retrievePerformerDefs() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrievePerformerDefs", true);

		ArrayList<PerformerDef> ret = connHandler
				.retrieveArray(PerformerDef.class);

		return ret.toArray(new PerformerDef[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveTransitionDef()
	 */
	public final TransitionDef[] retrieveTransitionDef() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveTransitionDef", true);

		ArrayList<TransitionDef> ret = connHandler
				.retrieveArray(TransitionDef.class);

		return ret.toArray(new TransitionDef[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityStatusDef()
	 */
	public final SimpleDef[] retrieveActivityStatusDef() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActivityStatusDef", true);

		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see WorkFlowDAO#retrieveActivityTypes()
	 */
	public final ActivityTemplateDef[] retrieveActivityTypes()
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveManualActivityTemplateDef", true);

		ArrayList<ActivityTemplateDef> ret = connHandler
				.retrieveArray(ActivityTemplateDef.class);

		for (BaseDBObject bdo : ret) {
			ActivityTemplateDef atd = (ActivityTemplateDef) bdo;
			populateDependentDetails(atd);
		}

		return ret.toArray(new ActivityTemplateDef[0]);
	}

	/**
	 * One final little thing we need to do: retrieve the list of service detail
	 * def Ids for service details that are required for check-in of this
	 * activity.
	 * 
	 * @param atd
	 * @throws DAOException
	 */
	private void populateDependentDetails(ActivityTemplateDef atd)
			throws DAOException {
		DataDetailBase[] tempBases = retrieveServiceDetailDefBases(atd
				.getActivityTemplateDefId());

		if (tempBases.length > 0) {
			for (DataDetailBase tempBase : tempBases) {
				atd.addDependentDetailId(tempBase.getDataDetailId());
				atd.addDependentDetailName(tempBase.getDataDetailLbl());
			}
		}
	}

	/**
	 * @see WorkFlowDAO#retrieveToDoStatusDef()
	 */
	public final SimpleDef[] retrieveToDoStatusDef() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActivityStatusDef", true);

		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		for (SimpleDef tempDef : ret.toArray(new SimpleDef[0])) {
			String code = tempDef.getCode();
			if (!code.equals("CM") && !code.equals("IP") && !code.equals("ND")) {
				ret.remove(tempDef);
			}
		}

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see WorkFlowDAO.retreiveAllControllerConfigs().
	 */
	public final ControllerConfig[] retrieveAllControllerConfigs()
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveAllConfigs", true);

		ArrayList<ControllerConfig> ret = connHandler
				.retrieveArray(ControllerConfig.class);

		return ret.toArray(new ControllerConfig[0]);
	}

	/**
	 * @see WorkFlowDAO.retreiveControllerConfigs(String).
	 */
	public final ControllerConfig[] retrieveControllerConfigs(String objectType)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveConfigsByObject", true);

		connHandler.setString(1, objectType);

		ArrayList<ControllerConfig> ret = connHandler
				.retrieveArray(ControllerConfig.class);

		return ret.toArray(new ControllerConfig[0]);
	}

	/**
	 * @see WorkFlowDAO.retrieveObjectTypes().
	 */
	public final String[] retrieveObjectTypes() throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		ArrayList<String> temp = new ArrayList<String>();

		try {
			conn = getReadOnlyConnection();

			ps = conn
					.prepareStatement(loadSQL("WorkFlowSQL.retrieveUniqueObjectTypes"));

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				temp.add(rs.getString("object"));
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(ps);
			handleClosing(conn);
		}

		return temp.toArray(new String[0]);
	}

	/**
	 * @see WorkFlowDAO.retrieveProcessActivityByDate()
	 * 
	 * @param beginDate
	 * @param endDate
	 * @param subSystem
	 * @return
	 * @throws DAOException
	 */
	public final ProcessActivity[] retrieveProcessActivityByDate(
			Timestamp beginDate, Timestamp endDate, String subSystem)
			throws DAOException {
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet results = null;
		ArrayList<ProcessActivity> activities = new ArrayList<ProcessActivity>();

		try {
			conn = getReadOnlyConnection();
			StringBuffer query = new StringBuffer(
					loadSQL("WorkFlowSQL.retrieveProcessActivityByDate"));

			pStmt = conn.prepareStatement(query.toString());
			pStmt.setTimestamp(1, beginDate);
			pStmt.setTimestamp(2, endDate);
			pStmt.setString(3, subSystem);

			results = pStmt.executeQuery();
			ProcessActivity activity = null;

			while (results.next()) {
				activity = new ProcessActivity();
				activity.setActivityTemplateNm(results
						.getString("activity_template_nm"));
				activity.setActivityTemplateId(getInteger(results,
						"activity_template_id"));
				activity.setStartDt(results.getTimestamp("Ready_Dt"));
				activity.setEndDt(results.getTimestamp("End_Dt"));
				activity.setLoopCnt(getInteger(results, "Loop_Cnt"));
				activities.add(activity);
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return activities.toArray(new ProcessActivity[0]);
	}

	private String buildActivityListQuery(StringBuffer query,
			ProcessActivity pa, String orderByString) {
		
		String facilityId = pa.getFacilityId();
		boolean orderBy = false;
		if (pa.getFacilityId() != null && facilityId.trim().length() > 0) {
			facilityId = formatFacilityId(facilityId);
			query.append(" AND LOWER(A.FACILITY_ID) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(facilityId.replace("*", "%")));
			query.append("')");
			orderBy = true;
		}
		
		String facilityName = pa.getFacilityNm();
		if (facilityName != null && facilityName.trim().length() > 0) {
			query.append(" AND LOWER(A.facility_Nm) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(facilityName.replace("*", "%")));
			query.append("')");
			orderBy = true;
		}
		
		String doLaaCd = pa.getDoLaaCd();
		if (doLaaCd != null && doLaaCd.trim().length() > 0) {
			query.append(" AND A.DO_LAA_CD = '");
			query.append(doLaaCd);
			query.append("'");
			orderBy = true;
		}
		
		if (pa.getUserId() != null) {
			query.append(" AND PA.User_Id = ");
			query.append(pa.getUserId());
			orderBy = true;
		}
		
		if (pa.getActivityStatusCd() != null) {
			query.append(" AND PA.ACTIVITY_STATUS_CD = '");
			query.append(SQLizeString(pa.getActivityStatusCd()));
			query.append("'");
			orderBy = true;
		}
		
		if (pa.getActivityTemplateNm() != null) {
			//
			// Special case for workload mgmt report
			//
			if ("Total".equalsIgnoreCase(pa.getActivityTemplateNm())) {
				query.append(" and "
						+ "(AT.activity_template_nm='Technically Complete/T&C ready to review' or"
						+ " AT.activity_template_nm='DO/LAA Permit Approval' or"
						+ " AT.activity_template_nm='CO Permit Review' or"
						+ " AT.activity_template_nm='CO Permit Approval') ");
			} else {
				query.append(" AND AT.ACTIVITY_TEMPLATE_NM = '");
				query.append(SQLizeString(pa.getActivityTemplateNm()));
				query.append("'");
			}
			orderBy = true;
		}
		
		if (pa.getProcessCd() != null) {
			query.append(" AND PT.PROCESS_CD = '");
			query.append(SQLizeString(pa.getProcessCd()));
			query.append("'");
			orderBy = true;
		}
		
		if (pa.getProcessId() != null) {
			query.append(" AND pa.process_id = ");
			query.append(pa.getProcessId());
			orderBy = true;
		}
		
		if (pa.getProcessTemplateId() != null) {
			query.append(" AND PT.Process_Template_Id = ");
			query.append(pa.getProcessTemplateId());
			orderBy = true;
		}
		
		String processTemplateNm = pa.getProcessTemplateNm();
		if (processTemplateNm != null) {
			query.append(" AND LOWER(PT.PROCESS_TEMPLATE_NM) LIKE ");
			query.append("LOWER('");
			// temporary for *[DRAFT]* Permitting
			String processTemplateName = processTemplateNm.replace("*", "%")
					.replace("[", "%").replaceAll("]", "%");
			query.append(SQLizeString(processTemplateName));
			query.append("')");
			orderBy = true;
		}
		
		if (pa.getActivityTemplateId() != null) {
			query.append(" AND PA.Activity_Template_Id = ");
			query.append(pa.getActivityTemplateId());
			orderBy = true;
		}
		
		if (pa.getCurrent() != null) {
			query.append(" AND pa.is_current = '");
			query.append(SQLizeString(pa.getCurrent()));
			query.append("'");
			orderBy = true;
		}
		
		if (pa.getAggregate() != null) {
			query.append(" AND AT.AGGREGATE = '");
			query.append(SQLizeString(pa.getAggregate()));
			query.append("'");
			orderBy = true;
		}
		
		if (pa.getPerformerTypeCd() != null) { // A or M
			query.append(" AND PA.PERFORMER_TYPE_CD = '");
			query.append(SQLizeString(pa.getPerformerTypeCd()));
			query.append("'");
			orderBy = true;
		}
		
		if (pa.getExternalId() != null) {
			query.append(" AND P.External_Id = ");
			query.append(pa.getExternalId());
			orderBy = true;
		}
		
		if (pa.getLoopCnt() != null) {
			query.append(" AND PA.loop_cnt = ");
			query.append(pa.getLoopCnt());
			orderBy = true;
		}
		
		if (pa.isLooped()) {
			query.append(" AND PA.loop_cnt > 1 ");
			orderBy = true;
		}

		ArrayList<String> status = pa.getActivityStatusCds();
		if (status != null && status.size() > 0) {
			query.append(" AND ");
			query.append(formatQuery(status, "activity_status_cd",
					pa.isInStatus()));
			orderBy = true;
		}
		
		if (pa.getEndDt() != null) {
			query.append(" AND (PA.END_DT >= ?");
			query.append(" AND PA.END_DT <= ?)");
		}

		if (orderBy)
			query.append(" ORDER BY " + orderByString);
		return query.toString();
	}

	private void setActivityListQueryValue(ConnectionHandler connHandler,
			ProcessActivity pa) throws DAOException {
		int i = 1;
		if (pa.getEndDt() != null) {
			connHandler.setTimestamp(i, formatBeginOfDay(pa.getEndDt()));
			i++;
			connHandler.setTimestamp(i, formatEndOfDay(pa.getEndDt()));
			i++;
		}
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#retrieveActivityList(us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity)
	 */
	public final ProcessActivity[] retrieveActivityList(ProcessActivity pa) throws DAOException {

		StringBuffer query = new StringBuffer(loadSQL("WorkFlowSQL.retrieveActivities"));

		if (pa.isUnlimitedResults()) {
			setDefaultSearchLimit(-1);
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(buildActivityListQuery(query, pa, "pa.END_DT, pa.start_dt"));
		setActivityListQueryValue(connHandler, pa);

		ArrayList<ProcessActivity> ret = connHandler.retrieveArray(ProcessActivity.class, defaultSearchLimit);
		return ret.toArray(new ProcessActivity[0]);
	}

	public final ProcessActivity[] retrieveActivityListForFacilities(ProcessActivity pa, String[] facilities, Integer userIdForSearch) throws DAOException {

		int remainder = facilities.length;
		int startIndex = 0;
		while (remainder > 0) {

			int batchSize = calculateBatchSize(remainder);
			remainder = remainder - batchSize;

			ConnectionHandler connHandler0 = new ConnectionHandler("FacilitySQL.insertFacilityIdsByUser", true);

			int endIndex = startIndex + batchSize;
			for (int i = startIndex; i < endIndex; i++) {
				connHandler0.setString(1, facilities[i]);
				connHandler0.setInteger(2, userIdForSearch);
				connHandler0.addBatch();
				startIndex++;
			}
			connHandler0.executeBatchUpdate();
			//logger.debug("batchSize: " + batchSize + "; total: " + facilities.length + "; processed: " + startIndex
			//		+ "; remainder: " + remainder);
		}
			
		StringBuffer query = new StringBuffer(loadSQL("WorkFlowSQL.retrieveActivitiesForFacilities"));

		if (pa.isUnlimitedResults()) {
			setDefaultSearchLimit(-1);
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(buildActivityListQuery(query, pa, "pa.END_DT, pa.start_dt"));
		int i = 1;
		connHandler.setInteger(i++, userIdForSearch);
		if (pa.getEndDt() != null) {
			connHandler.setTimestamp(i++, formatBeginOfDay(pa.getEndDt()));
			connHandler.setTimestamp(i++, formatEndOfDay(pa.getEndDt()));
		}

		ArrayList<ProcessActivity> ret = connHandler.retrieveArray(ProcessActivity.class, defaultSearchLimit);
		
		ConnectionHandler connHandler2 = new ConnectionHandler(
				"FacilitySQL.deleteFacilityIdsByUser", true);
		connHandler2.setInteger(1, userIdForSearch);
		connHandler2.remove();

		return ret.toArray(new ProcessActivity[0]);
	}

	public ProcessActivityLight[] retrieveActivityListLight(ProcessActivity pa)
			throws DAOException {
		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveActivitiesLight"));

		if (pa.isUnlimitedResults()) {
			setDefaultSearchLimit(-1);
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(buildActivityListQuery(query, pa,
				"pa.DUE_DT"));
		setActivityListQueryValue(connHandler, pa);

		ArrayList<ProcessActivityLight> ret = connHandler.retrieveArray(
				ProcessActivityLight.class, defaultSearchLimit);
		return ret.toArray(new ProcessActivityLight[0]);
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#createProcessDatas(us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessData[])
	 */
	public final void createProcessDatas(ProcessData[] pds) throws DAOException {
		for (ProcessData pd : pds) {
			ConnectionHandler connHandler = new ConnectionHandler(
					"WorkFlowSQL.createProcessData", false);

			connHandler.setInteger(1, pd.getDataTemplateId());
			connHandler.setInteger(2, pd.getProcessId());
			connHandler.setInteger(3, pd.getDataDetailId());
			connHandler.setString(4, pd.getDataValue());

			connHandler.update();
		}
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#retrieveProcessList(us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess)
	 */
	public final WorkFlowProcess[] retrieveProcessList(WorkFlowProcess wp)
			throws DAOException {
		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveProcessListBySearch"));

		if (wp.isUnlimitedResults()) {
			setDefaultSearchLimit(-1);
		}

		String facilityId = wp.getFacilityIdString();
		if (facilityId != null && facilityId.trim().length() > 0) {
			facilityId = formatFacilityId(facilityId);
			query.append(" AND LOWER(A.FACILITY_ID) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(facilityId.replace("*", "%")));
			query.append("')");
		}
		String facilityName = wp.getFacilityNm();
		if (facilityName != null && facilityName.trim().length() > 0) {
			query.append(" AND LOWER(A.facility_Nm) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(facilityName.replace("*", "%")));
			query.append("')");
		}
		String permitNumber = wp.getPermitNumber();
		if (permitNumber != null && permitNumber.trim().length() > 0) {
			permitNumber = permitNumber.trim();
			query.append(" AND LOWER(pp.PERMIT_NBR) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(permitNumber.replace("*", "%")));
			query.append("')");
		}
		if (wp.getUserId() != null) {
			query.append(" AND p.User_Id = ");
			query.append(wp.getUserId());
		}
		if (wp.getExternalId() != null) {
			query.append(" AND p.EXTERNAL_ID = ");
			query.append(wp.getExternalId());
		}
		if (wp.getProcessTemplateId() != null) {
			query.append(" AND p.process_template_id = ");
			query.append(wp.getProcessTemplateId());
		}
		String processTemplateNm = wp.getProcessTemplateNm();
		if (processTemplateNm != null) {
			query.append(" AND LOWER(PT.PROCESS_TEMPLATE_NM) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(processTemplateNm.replace("*", "%")));
			query.append("')");
		}
		if (wp.getProcessCd() != null) {
			query.append(" AND PT.PROCESS_CD = '");
			query.append(SQLizeString(wp.getProcessCd()));
			query.append("'");
		}
		if (wp.getExpedite() != null) {
			query.append(" AND p.EXPEDITE = '");
			query.append(SQLizeString(wp.getExpedite()));
			query.append("'");
		}
		if (wp.isCurrent()) {
			query.append(" AND pa.IS_CURRENT = 'Y' ");
		}
		if (wp.getState() != null) {
			String st = wp.getState();
			if (st.equalsIgnoreCase(WorkFlowProcess.STATE_COMPLETED_CD)) {
				query.append(" AND p.end_dt is not null ");
			} else {
				query.append(" AND p.end_dt is null ");
			}
		}
		boolean setTime = false;
		boolean setDue = false;
		if (wp.getStatus() != null) {
			String st = wp.getStatus();
			setTime = true;
			if (st.equalsIgnoreCase(WorkFlowProcess.STATUS_LATE_CD)) {
				query.append(" AND p.DUE_DT <= ?");
			} else if (st.equalsIgnoreCase(WorkFlowProcess.STATUS_JEOPARDY_CD)) {
				query.append(" AND p.JEOPARDY_DT <= ?");
				query.append(" AND p.DUE_DT >= ?");
				setDue = true;
			} else {
				if (wp.getState() != null) {
					String stt = wp.getState();
					if (stt.equalsIgnoreCase(WorkFlowProcess.STATE_COMPLETED_CD)) {
						setTime = false;
						query.append(" AND p.JEOPARDY_DT < p.end_dt ");
					} else {
						query.append(" AND p.JEOPARDY_DT > ?");
					}
				} else {
					query.append(" AND ((p.end_dt is not null ");
					query.append(" AND p.JEOPARDY_DT < p.end_dt) ");
					query.append(" OR (p.end_dt is null ");
					query.append(" AND p.JEOPARDY_DT > ?))");
				}
			}
		}
		Timestamp beginDate = wp.getStartDt();
		Timestamp endDate = wp.getEndDt();
		if (beginDate != null || endDate != null) {
			query.append("AND (");
			if (beginDate != null) {
				query.append("p.START_DT >= ? ");
				if (endDate != null) {
					query.append(" AND ");
				}
			}
			if (endDate != null) {
				query.append("p.START_DT <= ? ");
			}
			query.append(" ) ");
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(query.toString());

		int index = 1;
		if (setTime) {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			connHandler.setTimestamp(index, now);
			index++;
			if (setDue) {
				connHandler.setTimestamp(index, now);
				index++;
			}
		}
		if (beginDate != null) {
			connHandler.setTimestamp(index, wp.getStartDt());
			index++;
		}
		if (endDate != null) {
			connHandler.setTimestamp(index, formatEndOfDay(wp.getEndDt()));
			index++;
		}

		ArrayList<WorkFlowProcess> ret = connHandler.retrieveArray(
				WorkFlowProcess.class, defaultSearchLimit);
		return ret.toArray(new WorkFlowProcess[0]);
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#retrieveChildrenProcess(java.lang.Integer)
	 */
	public final WorkFlowProcess[] retrieveChildrenProcess(Integer processId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveChildrenProcessById", true);
		connHandler.setInteger(1, processId);

		ArrayList<WorkFlowProcess> ret = connHandler
				.retrieveArray(WorkFlowProcess.class);

		return ret.toArray(new WorkFlowProcess[0]);
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#removeProcessData(java.lang.Integer)
	 */
	public final void removeProcessData(Integer processId) throws DAOException {
		checkNull(processId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.removeProcessData", false);

		connHandler.setInteger(1, processId);

		connHandler.remove();
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#retrieveUserIdsOfFacility(java.lang.Integer,
	 *      java.lang.String)
	 */
    //DONE 2475
	// new role discrim arg
	public final Integer[] retrieveUserIdsOfFacility(Integer fpId,
			String facilityRoleCd) throws DAOException {
		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveUserIdsOfFacility"));
		if (facilityRoleCd != null) {
			query.append("and FACILITY_ROLE_CD = '");
			query.append(facilityRoleCd);
			query.append("'");
		}
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(query.toString());

		connHandler.setInteger(1, fpId);

		ArrayList<? extends Object> ret = connHandler
				.retrieveJavaObjectArray(Integer.class);

		return ret.toArray(new Integer[0]);
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#createProcessNote(us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote)
	 */
	public final void createProcessNote(ProcessNote pn) throws DAOException {
		checkNull(pn);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.createProcessNote", false);
		pn.setNoteId(nextSequenceVal("S_Process_Note_Id"));

		connHandler.setInteger(1, pn.getNoteId());
		connHandler.setInteger(2, pn.getProcessId());
		connHandler.setInteger(3, pn.getUserId());
		connHandler.setTimestamp(4, pn.getPostedDt());
		connHandler.setString(5, pn.getNote());

		connHandler.update();

		pn.setLastModified(1);
	}

	/**
	 * @see WorkFlowDAO#modifyProcessNote(ProcessNote pn).
	 */
	public final boolean modifyProcessNote(ProcessNote pn) throws DAOException {
		checkNull(pn);

		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.modifyProcessNote", false);

		connHandler.setString(1, pn.getNote());
		connHandler.setInteger(2, pn.getLastModified() + 1);
		connHandler.setInteger(3, pn.getNoteId());
		connHandler.setInteger(4, pn.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#retrieveProcessNotes(us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote)
	 */
	public final ProcessNote[] retrieveProcessNotes(ProcessNote pn)
			throws DAOException {
		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveProcessNotes"));

		if (pn.getProcessId() != null) {
			query.append(" AND PROCESS_ID = ");
			query.append(pn.getProcessId());
		}

		query.append(" ORDER BY NOTE_ID DESC");
		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(query.toString());

		ArrayList<ProcessNote> ret = connHandler
				.retrieveArray(ProcessNote.class);
		return ret.toArray(new ProcessNote[0]);
	}

	public final ActivityReferralType[] retrieveActivityReferralTypes()
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveActivityReferralTypes", true);

		ArrayList<ActivityReferralType> ret = connHandler
				.retrieveArray(ActivityReferralType.class);

		return ret.toArray(new ActivityReferralType[0]);
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.WorkFlowDAO#removeProcessNotes(java.lang.Integer)
	 */
	public final void removeProcessNotes(Integer processId) throws DAOException {
		checkNull(processId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.removeProcessNotes", false);

		connHandler.setInteger(1, processId);

		connHandler.remove();
	}

	public ProcessActivity retrieveActivity(ProcessActivity pa)
			throws DAOException {
		ProcessActivity ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(true);
		StringBuffer query = new StringBuffer(
				loadSQL("WorkFlowSQL.retrieveActivity"));

		if (pa.getProcessId() != null) {
			query.append(" AND pa.process_id = ");
			query.append(pa.getProcessId());
		}
		if (pa.getActivityTemplateId() != null) {
			query.append(" AND PA.Activity_Template_Id = ");
			query.append(pa.getActivityTemplateId());
		}
		if (pa.getCurrent() != null) {
			query.append(" AND pa.is_current = '");
			query.append(SQLizeString(pa.getCurrent()));
			query.append("'");
		}
		if (pa.getLoopCnt() != null) {
			query.append(" AND PA.loop_cnt = ");
			query.append(pa.getLoopCnt());
		}

		connHandler.setSQLStringRaw(query.toString());
		ret = (ProcessActivity) connHandler.retrieve(ProcessActivity.class);

		return ret;
	}

	@Override
	public String retrieveRoleCdByParent(String facilityRoleCd, String roleDiscrim)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"WorkFlowSQL.retrieveRoleCdByParent", true);

		connHandler.setString(1, facilityRoleCd);
		connHandler.setString(2, roleDiscrim);

		return (String) connHandler.retrieveJavaObject(String.class);
	}


	@Override
	public Map<Integer, String> retrievePermittingActivityTypes() throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		Map<Integer, String> ret = new LinkedHashMap<Integer, String>();

		try {
			conn = getReadOnlyConnection();

			ps = conn
					.prepareStatement(loadSQL("WorkFlowSQL.retrievePermittingActivityTypes"));

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ret.put(rs.getInt("ACTIVITY_TEMPLATE_ID"), rs.getString("ACTIVITY_TEMPLATE_NM"));
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(ps);
			handleClosing(conn);
		}

		return ret;
	}


	@Override
	public Integer findActiveProcessByExternalObject(Integer processTemplateId, 
			Integer externalId) throws DAOException {
		Connection conn = null;
		PreparedStatement ps = null;
		Integer ret = null;
		try {
			conn = getReadOnlyConnection();

			ps = conn
					.prepareStatement(loadSQL("WorkFlowSQL.findActiveProcessByExternalObject"));
			
			ps.setInt(1, processTemplateId);
			ps.setInt(2, externalId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				ret = rs.getInt("PROCESS_ID");
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(ps);
			handleClosing(conn);
		}

		return ret;
	}

}
