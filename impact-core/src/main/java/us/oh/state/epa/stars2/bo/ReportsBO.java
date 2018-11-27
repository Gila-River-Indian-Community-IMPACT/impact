package us.oh.state.epa.stars2.bo;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.faces.model.SelectItem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.ReportsDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dao.TransactionFactory;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.DoLaaDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.UserDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.report.ApplicationCount;
import us.oh.state.epa.stars2.database.dbObjects.report.FacilityPermitCount;
import us.oh.state.epa.stars2.database.dbObjects.report.GenericIssuanceCount;
import us.oh.state.epa.stars2.database.dbObjects.report.Issuance;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtioDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceTvDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.IssuedMetricsData;
import us.oh.state.epa.stars2.database.dbObjects.report.PEROverdueDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitCount;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitExpirationDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitSOPData;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitSOPParams;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitTime;
import us.oh.state.epa.stars2.database.dbObjects.report.PermitWorkers;
import us.oh.state.epa.stars2.database.dbObjects.report.SimpleIssuanceReason;
import us.oh.state.epa.stars2.database.dbObjects.report.SimplePermitId;
import us.oh.state.epa.stars2.database.dbObjects.report.TOPSData;
import us.oh.state.epa.stars2.database.dbObjects.report.WorkloadData;
import us.oh.state.epa.stars2.database.dbObjects.report.WorkloadDetails;
import us.oh.state.epa.stars2.database.dbObjects.report.WorkloadTrend;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessActivity;
import us.oh.state.epa.stars2.database.dbObjects.workflow.ProcessNote;
import us.oh.state.epa.stars2.database.dbObjects.workflow.TimeSpan;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitSOPTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.SystemPropertyDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePbrDetails;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuancePtiDetails;
//import us.oh.state.epa.stars2.database.dbObjects.report.IssuanceSptoDetails;
//import us.oh.state.epa.stars2.database.dbObjects.report.PBRCount;

/**
 * <p>
 * Title: ReportsBO
 * </p>
 * 
 * <p>
 * Description: This is the Business Object for Management Reports.
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: MentorGen, LLC
 * </p>
 * 
 * @author swooster
 */
@Transactional(rollbackFor=Exception.class)
@Service
public class ReportsBO extends BaseBO implements ReportService {

	/**
	 * Returns an array IssuanceDetails objects. IssuanceDetails are derrived
	 * data used to populate the Permit Issunace report.
	 * 
	 * @return issuanceDetail[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public IssuanceDetails[] retrieveIssuanceDetails(Timestamp startDt,
			Timestamp endDt) throws DAOException {

		DoLaaDef[] defs = infrastructureDAO().retrieveDoLaas();
		HashMap<String, IssuanceDetails> details = new HashMap<String, IssuanceDetails>();

		IssuanceDetails id;
		for (DoLaaDef d : defs) {
			id = new IssuanceDetails();
			id.setDoLaaName(d.getDescription());
			id.setDoLaaShortDsc(d.getDoLaaShortDsc());
			id.setDoLaaCd(d.getCode());
			details.put(d.getDoLaaID(), id);
		}
		id = new IssuanceDetails();
		id.setDoLaaShortDsc("Total");
		id.setDoLaaName("Total");
		details.put("zzz", id);

		Issuance[] issuePermits = reportsDAO().retrieveIssuedPermits(startDt,
				endDt);
		IssuanceDetails colTotal = details.get("zzz");
		for (Issuance data : issuePermits) {
			IssuanceDetails x = details.get(data.getDoLaaId());
/*
			if (data.getPermitType().equalsIgnoreCase(PermitTypeDef.TVPTI)
					&& data.getIssuanceType().equalsIgnoreCase("D")) {
				x.setPtiDraft(x.getPtiDraft() + 1);
				x.setTotalPti(x.getTotalPti() + 1);
				colTotal.setPtiDraft(colTotal.getPtiDraft() + 1);
				colTotal.setTotalPti(colTotal.getTotalPti() + 1);
			} else if (data.getPermitType().equalsIgnoreCase(
					PermitTypeDef.TVPTI)
					&& data.getIssuanceType().equalsIgnoreCase("F")) {
				x.setPtiFinal(x.getPtiFinal() + 1);
				x.setTotalPti(x.getTotalPti() + 1);
				colTotal.setPtiFinal(colTotal.getPtiFinal() + 1);
				colTotal.setTotalPti(colTotal.getTotalPti() + 1);
			} else */ if (data.getPermitType()
					.equalsIgnoreCase(PermitTypeDef.NSR)
					&& data.getIssuanceType().equalsIgnoreCase("D")) {
				x.setPtioDraft(x.getPtioDraft() + 1);
				x.setTotalPtio(x.getTotalPtio() + 1);
				colTotal.setPtioDraft(colTotal.getPtioDraft() + 1);
				colTotal.setTotalPtio(colTotal.getTotalPtio() + 1);
			} else if (data.getPermitType()
					.equalsIgnoreCase(PermitTypeDef.NSR)
					&& data.getIssuanceType().equalsIgnoreCase("F")) {
				x.setPtioFinal(x.getPtioFinal() + 1);
				x.setTotalPtio(x.getTotalPtio() + 1);
				colTotal.setPtioFinal(colTotal.getPtioFinal() + 1);
				colTotal.setTotalPtio(colTotal.getTotalPtio() + 1);
			} else if (data.getPermitType().equalsIgnoreCase(
					PermitTypeDef.TV_PTO)
					&& data.getIssuanceType().equalsIgnoreCase("D")) {
				x.setTvDraft(x.getTvDraft() + 1);
				x.setTotalTv(x.getTotalTv() + 1);
				colTotal.setTvDraft(colTotal.getTvDraft() + 1);
				colTotal.setTotalTv(colTotal.getTotalTv() + 1);
			/*
			} else if (data.getPermitType().equalsIgnoreCase(
					PermitTypeDef.TV_PTO)
					&& data.getIssuanceType().equalsIgnoreCase("PPP")) {
				x.setTvPpp(x.getTvPpp() + 1);
				x.setTotalTv(x.getTotalTv() + 1);
				colTotal.setTvPpp(colTotal.getTvPpp() + 1);
				colTotal.setTotalTv(colTotal.getTotalTv() + 1);
			*/
			} else if (data.getPermitType().equalsIgnoreCase(
					PermitTypeDef.TV_PTO)
					&& data.getIssuanceType().equalsIgnoreCase("PP")) {
				x.setTvPp(x.getTvPp() + 1);
				x.setTotalTv(x.getTotalTv() + 1);
				colTotal.setTvPp(colTotal.getTvPp() + 1);
				colTotal.setTotalTv(colTotal.getTotalTv() + 1);
			} else if (data.getPermitType().equalsIgnoreCase(
					PermitTypeDef.TV_PTO)
					&& data.getIssuanceType().equalsIgnoreCase("F")) {
				x.setTvFinal(x.getTvFinal() + 1);
				x.setTotalTv(x.getTotalTv() + 1);
				colTotal.setTvFinal(colTotal.getTvFinal() + 1);
				colTotal.setTotalTv(colTotal.getTotalTv() + 1);
				/*
			} else if (data.getPermitType()
					.equalsIgnoreCase(PermitTypeDef.SPTO)
					&& data.getIssuanceType().equalsIgnoreCase("F")) {
				x.setSpto(x.getSpto() + 1);
				x.setTotalSpto(x.getTotalSpto() + 1);
				colTotal.setSpto(colTotal.getSpto() + 1);
				colTotal.setTotalSpto(colTotal.getTotalSpto() + 1);
				*/
				/*
			} else if (data.getPermitType().equalsIgnoreCase(PermitTypeDef.REG)
					&& data.getIssuanceType().equalsIgnoreCase("F")) {
				x.setReg(x.getReg() + 1);
				x.setTotalReg(x.getTotalReg() + 1);
				colTotal.setReg(colTotal.getReg() + 1);
				colTotal.setTotalReg(colTotal.getTotalReg() + 1);
				*/
			}
			/*
			 * It is doing the count later again with retrieveIssuancePbrDetails
			 * Don't know why it is doing here too. else if
			 * (data.getPermitType().equalsIgnoreCase(PermitTypeDef.PBR)) {
			 * x.setPbr(x.getPbr() + 1); x.setTotalPbr(x.getTotalPbr() + 1);
			 * colTotal.setPbr(colTotal.getPbr() + 1);
			 * colTotal.setTotalPbr(colTotal.getTotalPbr() + 1); }
			 */
		}
		
		/*

		IssuancePbrDetails[] pbrs = retrieveIssuancePbrDetails(startDt, endDt,
				null);

		for (IssuancePbrDetails data : pbrs) {
			IssuanceDetails x = details.get(data.getDoLaaId());
			x.setPbr(x.getPbr() + 1);
			x.setTotalPbr(x.getTotalPbr() + 1);
			colTotal.setPbr(colTotal.getPbr() + 1);
			colTotal.setTotalPbr(colTotal.getTotalPbr() + 1);
		}
		
		*/

		// Sort the Colletion by DO_LAA_CODE - make sure that Total is
		// the last row header.
		Set<String> HostKeys = details.keySet();

		Iterator<String> It = HostKeys.iterator();
		Vector<String> HKeys = new Vector<String>();
		while (It.hasNext()) {
			HKeys.add(It.next());
		}

		Collections.sort(HKeys);

		IssuanceDetails[] data = new IssuanceDetails[details.size()];

		for (int ii = 0; ii < HKeys.size(); ii++) {
			String K = HKeys.elementAt(ii);
			data[ii] = details.get(K);
		}

		return data;
	}

	/**
	 * Returns an array WorkflowDetails objects. IssuanceDetails are derrived
	 * data used to populate the Permit Issunace report.
	 * 
	 * @return issuanceDetail[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public WorkloadDetails[] retrieveWorkloadDetails(List<String> doLaas,
			String activityStatusCd) throws DAOException {
		ReportsDAO repDao = reportsDAO();

		PermitWorkers[] defs = repDao.retrievePermitWorkers(doLaas);
		HashMap<String, WorkloadDetails> details = new HashMap<String, WorkloadDetails>();

		WorkloadDetails id = new WorkloadDetails();
		id.setUserId(-1);
		for (PermitWorkers d : defs) {

			//String key = d.getDoLaaId() + "." + d.getActivityName() + "."
			//		+ d.getUserId();
			String key = d.getUserId() + "." + d.getActivityName();

			WorkloadDetails x = details.get(key);
			if (x == null) {
				id = new WorkloadDetails();
				id.setActivityName(d.getActivityName());
				id.setUserId(d.getUserId());
				id.setFirstNm(d.getFirstName());
				id.setLastNm(d.getLastName());
				id.setDoLaaName(d.getDoLaaName());
				id.setDoLaaCd(d.getDoLaaCd());
				id.setDoLaaShortDsc(d.getDoLaaShortDsc());
				//details.put(d.getDoLaaId() + "." + d.getActivityName() + "."
					//	+ d.getUserId(), id);
				details.put(d.getUserId() + "." + d.getActivityName(), id); 
			}
		}
		id = new WorkloadDetails();
		StringBuffer doLaaCds = null;
		if (doLaas != null) {
			doLaaCds = new StringBuffer("");
			for (String dolaa : doLaas) {
				doLaaCds.append(dolaa + ",");
			}
			doLaaCds.replace(doLaaCds.length() - 1, doLaaCds.length(), "");
		}

		WorkloadData[] wfData = repDao.retrievePermitWorkload(doLaas,
				activityStatusCd);
		id.setActivityName("Total");
		id.setDoLaaName("All");
		if (doLaaCds == null)
			id.setDoLaaCd(" ");
		else
			id.setDoLaaCd(doLaaCds.toString());

		details.put("99", id);

		WorkloadDetails colTotal = details.get("99");
		for (WorkloadData data : wfData) {
			//String key = data.getDoLaaId() + "." + data.getActivityName() + "."
				//	+ data.getUserId();
			String key = data.getUserId() + "." + data.getActivityName();

			WorkloadDetails x = details.get(key);

			//
			// This user is not usually supposed to have this kind of task
			// but may have had it assigned to him/her for some reason
			if (x == null) {
				// find the user for this task from the list of users in def
				// if it's not found, we don't want to include this task
				for (PermitWorkers worker : defs) {
					if (worker.getUserId().equals(data.getUserId())) {
						x = new WorkloadDetails();
						x.setFirstNm(worker.getFirstName());
						x.setLastNm(worker.getLastName());
						break;
					}
				}
				// look up user
				if (x == null) {
					UserDef userDef = infrastructureDAO().retrieveUserDef(
							data.getUserId());
					if (userDef != null) {
						Contact contact = userDef.getContact();
						if (contact != null) {
							x = new WorkloadDetails();
							x.setFirstNm(contact.getFirstNm());
							x.setLastNm(contact.getLastNm());
						}
					}
				}
				if (x != null) {
					x.setActivityName(data.getActivityName());
					x.setUserId(data.getUserId());
					x.setDoLaaName(data.getDoLaaName());
					x.setDoLaaShortDsc(data.getDoLaaShortDsc());
					x.setDoLaaCd(data.getDoLaaCd());
				} else {
					continue;
				}
			}

			// TVPTI
			/*
			if (data.getPermitType().equalsIgnoreCase(PermitTypeDef.TVPTI)) {
				if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.ISSUED_DRAFT)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setPtiDraft(x.getPtiDraft() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setPtiDraft(colTotal.getPtiDraft() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setPtiDraftNow(x.getPtiDraftNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setPtiDraftNow(colTotal.getPtiDraftNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setPtiDraftPre(x.getPtiDraftPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setPtiDraftPre(colTotal.getPtiDraftPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				} else if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.NONE)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setPtiWorking(x.getPtiWorking() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setPtiWorking(colTotal.getPtiWorking() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}

					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setPtiWorkingNow(x.getPtiWorkingNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setPtiWorkingNow(colTotal.getPtiWorkingNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}

					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setPtiWorkingPre(x.getPtiWorkingPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setPtiWorkingPre(colTotal.getPtiWorkingPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				}
*/
				// PTIO

			//} else 
				if (data.getPermitType()
					.equalsIgnoreCase(PermitTypeDef.NSR)) {
				if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.ISSUED_DRAFT)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setPtioDraft(x.getPtioDraft() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setPtioDraft(colTotal.getPtioDraft() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setPtioDraftNow(x.getPtioDraftNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setPtioDraftNow(colTotal.getPtioDraftNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setPtioDraftPre(x.getPtioDraftPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setPtioDraftPre(colTotal.getPtioDraftPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				} else if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.NONE)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setPtioWorking(x.getPtioWorking() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setPtioWorking(colTotal.getPtioWorking() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setPtioWorkingNow(x.getPtioWorkingNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setPtioWorkingNow(colTotal.getPtioWorkingNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setPtioWorkingPre(x.getPtioWorkingPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setPtioWorkingPre(colTotal.getPtioWorkingPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				}
				// TIV_PTO
				/*
			} else if (data.getPermitType().equalsIgnoreCase(
					PermitTypeDef.TIV_PTO)) {
				if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.NONE)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTivWorking(x.getTivWorking() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setTivWorking(colTotal.getTivWorking() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setTivWorkingNow(x.getTivWorkingNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setTivWorkingNow(colTotal.getTivWorkingNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTivWorkingPre(x.getTivWorkingPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setTivWorkingPre(colTotal.getTivWorkingPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				} else if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.ISSUED_DRAFT)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTivDraft(x.getTivDraft() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setTivDraft(colTotal.getTivDraft() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setTivDraftNow(x.getTivDraftNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setTivDraftNow(colTotal.getTivDraftNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTivDraftPre(x.getTivDraftPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setTivDraftPre(colTotal.getTivDraftPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				
				} else if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.ISSUED_PPP)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTivPpp(x.getTivPpp() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setTivPpp(colTotal.getTivPpp() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setTivPppNow(x.getTivPppNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setTivPppNow(colTotal.getTivPppNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTivPppPre(x.getTivPppPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setTivPppPre(colTotal.getTivPppPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
					
				} else if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.ISSUED_PP)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTivPp(x.getTivPp() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setTivPp(colTotal.getTivPp() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setTivPpNow(x.getTivPpNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setTivPpNow(colTotal.getTivPpNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTivPpPre(x.getTivPpPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setTivPpPre(colTotal.getTivPpPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				}
		*/

				// TV_PTO
			} else if (data.getPermitType().equalsIgnoreCase(
					PermitTypeDef.TV_PTO)) {
				if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.NONE)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTvWorking(x.getTvWorking() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setTvWorking(colTotal.getTvWorking() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setTvWorkingNow(x.getTvWorkingNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setTvWorkingNow(colTotal.getTvWorkingNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTvWorkingPre(x.getTvWorkingPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setTvWorkingPre(colTotal.getTvWorkingPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				} else if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.ISSUED_DRAFT)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTvDraft(x.getTvDraft() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setTvDraft(colTotal.getTvDraft() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setTvDraftNow(x.getTvDraftNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setTvDraftNow(colTotal.getTvDraftNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTvDraftPre(x.getTvDraftPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setTvDraftPre(colTotal.getTvDraftPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				/*
				} else if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.ISSUED_PPP)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTvPpp(x.getTvPpp() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setTvPpp(colTotal.getTvPpp() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setTvPppNow(x.getTvPppNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setTvPppNow(colTotal.getTvPppNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTvPppPre(x.getTvPppPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setTvPppPre(colTotal.getTvPppPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
					*/
				} else if (data.getPermitIssuanceStatusCd().equalsIgnoreCase(
						PermitGlobalStatusDef.ISSUED_PP)) {
					if (ActivityStatusDef.COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTvPp(x.getTvPp() + 1);
						x.setTotal(x.getTotal() + 1);
						colTotal.setTvPp(colTotal.getTvPp() + 1);
						colTotal.setTotal(colTotal.getTotal() + 1);
					}
					if (ActivityStatusDef.IN_PROCESS.equalsIgnoreCase(data
							.getActivityStatusCd())
							|| ActivityStatusDef.REFERRED.equalsIgnoreCase(data
									.getActivityStatusCd())) {
						x.setTvPpNow(x.getTvPpNow() + 1);
						x.setTotalNow(x.getTotalNow() + 1);
						colTotal.setTvPpNow(colTotal.getTvPpNow() + 1);
						colTotal.setTotalNow(colTotal.getTotalNow() + 1);
					}
					if (ActivityStatusDef.NOT_COMPLETED.equalsIgnoreCase(data
							.getActivityStatusCd())) {
						x.setTvPpPre(x.getTvPpPre() + 1);
						x.setTotalPre(x.getTotalPre() + 1);
						colTotal.setTvPpPre(colTotal.getTvPpPre() + 1);
						colTotal.setTotalPre(colTotal.getTotalPre() + 1);
					}
				}
			}
			details.put(key, x);
		}
		//
		// Sort the Colletion by DO_LAA_CODE - make sure that Total is
		// the last row header.
		//
		Set<String> HostKeys = details.keySet();

		Iterator<String> It = HostKeys.iterator();
		Vector<String> HKeys = new Vector<String>();
		while (It.hasNext()) {
			HKeys.add(It.next());
		}

		Collections.sort(HKeys);

		WorkloadDetails[] data = new WorkloadDetails[details.size()];

		// int jj = 0;
		// for (int ii=HKeys.size()-1; ii>=0; ii--) {
		// String K = (String)(HKeys.elementAt(ii));
		// data[jj++] = (details.get(K));
		// }
		for (int ii = 0; ii < HKeys.size(); ii++) {
			String K = HKeys.elementAt(ii);
			data[ii] = details.get(K);
		}

		return data;
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveIssuedPermitsByDoLaaByType(Timestamp startDt,
			Timestamp endDt, String permitType, String issuanceType,
			String doLaaName) throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		//if ("PBR".equalsIgnoreCase(permitType))
		//	return reportsDAO().retrieveIssuedPbrsByDoLaa(startDt, endDt,
		//			doLaaName);

		return reportsDAO().retrieveIssuedPermitsByDoLaaByType(startDt, endDt,
				permitType, issuanceType, doLaaName);
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveExpiredPermitsByDoLaaByType(Timestamp startDt,
			Timestamp endDt, String permitType, String issuanceType,
			String doLaaName) throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		//if ("PBR".equalsIgnoreCase(permitType))
		//	return reportsDAO().retrieveIssuedPbrsByDoLaa(startDt, endDt,
		//			doLaaName);

		return reportsDAO().retrieveExpiredPermitsByDoLaaByType(startDt, endDt,
				permitType, issuanceType, doLaaName);
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveActivePermitsByDoLaaByType(String permitType,
			String issuanceType, String doLaas, Integer userId,
			String activityName, String activityStatusCd) throws DAOException {

		if (("Total".equalsIgnoreCase(doLaas))
				|| (" ".equalsIgnoreCase(doLaas)))
			doLaas = null;

		if ("Total".equalsIgnoreCase(activityName))
			activityName = null;

		if ("TOTAL".equalsIgnoreCase(permitType))
			permitType = null;

		return reportsDAO().retrieveActivePermitsByDoLaaByType(permitType,
				issuanceType, doLaas, userId, activityName, activityStatusCd);
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveIssuedPermitsByIssuanceType(Timestamp startDt,
			Timestamp endDt, String permitType, String doLaaName)
			throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		return reportsDAO().retrieveIssuedPermitsByIssuanceType(startDt, endDt,
				permitType, doLaaName);
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveExpiredPermitsByIssuanceType(
			Timestamp startDt, Timestamp endDt, String permitType,
			String doLaaName) throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		return reportsDAO().retrieveExpiredPermitsByIssuanceType(startDt,
				endDt, permitType, doLaaName);
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveActivePermitsByIssuanceType(String permitType,
			String doLaas, Integer userId, String activityName,
			String activityStatusCd) throws DAOException {

		if ("Total".equalsIgnoreCase(activityName))
			activityName = null;

		if ("Total".equalsIgnoreCase(permitType))
			permitType = null;

		return reportsDAO().retrieveActivePermitsByIssuanceType(permitType,
				doLaas, userId, activityName, activityStatusCd);
	}

	/**
	 * Returns an array of ApplicationCount objects.
	 * 
	 * @return ApplicationCount[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ApplicationCount[] retrieveApplicationCountByType(Timestamp startDt,
			Timestamp endDt) throws DAOException {
		return reportsDAO().retrieveApplicationCountByType(startDt, endDt);
	}

	/**
	 * Returns an array of PermitCount objects.
	 * 
	 * @return PermitCount[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitCount[] retrieveIssuedFinalPermitsCountByType(
			Timestamp startDt, Timestamp endDt) throws DAOException {
		return reportsDAO().retrieveIssuedFinalPermitsCountByType(startDt,
				endDt);
	}

	/**
	 * Returns an array of PBRCount objects.
	 * 
	 * @return PBRCount[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	/*
	public PBRCount[] retrievePbrCountsByType(Timestamp startDt, Timestamp endDt)
			throws DAOException {
		return reportsDAO().retrievePbrCountsByType(startDt, endDt);
	}
	*/

	/**
	 * Returns an array of FacilityPermitCount objects.
	 * 
	 * @return FacilityPermitCount[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public FacilityPermitCount[] retrieveFacilityCountsByPermitType(
			Timestamp endDt) throws DAOException {

		return reportsDAO().retrieveFacilityCountsByPermitType(endDt);
	}

	/**
	 * Returns an array of PermitTime objects.
	 * 
	 * @return FacilityPermitCount[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitTime[] retrieveInProcessPermitTime() throws DAOException {

		return reportsDAO().retrieveInProcessPermitTime();
	}

	/**
	 * Returns an array of IssuancePtiDetails objects.
	 * 
	 * @return IssuancePtiDetails[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	/*
	public IssuancePtiDetails[] retrieveIssuancePtiDetails(Timestamp startDt,
			Timestamp endDt, String doLaaName, String issuanceType)
			throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		IssuancePtiDetails[] aa = reportsDAO().retrieveIssuancePtiDetails(
				startDt, endDt, doLaaName, issuanceType);

		HashMap<Integer, IssuancePtiDetails> details = new HashMap<Integer, IssuancePtiDetails>();

		for (IssuancePtiDetails a : aa) {
			IssuancePtiDetails ipd = details.get(a.getPermitId());

			details.put(a.getPermitId(), a);
			ipd = a;

			if ("D".equalsIgnoreCase(a.getIssuanceTypeCd())) {
				ipd.setPublicationDt(a.getPublicationDt());
				ipd.setEndOfCommentDt(a.getEndOfCommentDt());
			}

		}

		return details.values().toArray(new IssuancePtiDetails[0]);
	}
	*/

	/**
	 * Returns an array of IssuancePtiDetails objects.
	 * 
	 * @return IssuancePtiDetails[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	/*
	public IssuancePtiDetails[] retrieveExpiredPtiDetails(Timestamp startDt,
			Timestamp endDt, String doLaaName, String issuanceType)
			throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		IssuancePtiDetails[] aa = reportsDAO().retrieveExpiredPtiDetails(
				startDt, endDt, doLaaName, issuanceType);

		HashMap<Integer, IssuancePtiDetails> details = new HashMap<Integer, IssuancePtiDetails>();

		for (IssuancePtiDetails a : aa) {
			IssuancePtiDetails ipd = details.get(a.getPermitId());

			details.put(a.getPermitId(), a);
			ipd = a;

			if ("D".equalsIgnoreCase(a.getIssuanceTypeCd())) {
				ipd.setPublicationDt(a.getIssuanceDt());
				ipd.setEndOfCommentDt(a.getEndOfCommentDt());
			}

		}

		return details.values().toArray(new IssuancePtiDetails[0]);
	}
	*/

	/**
	 * Returns an array of IssuancePtioDetails objects.
	 * 
	 * @return IssuancePtioDetails[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public IssuancePtioDetails[] retrieveIssuancePtioDetails(Timestamp startDt,
			Timestamp endDt, String doLaaName, String issuanceType)
			throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		IssuancePtioDetails[] aa = reportsDAO().retrieveIssuancePtioDetails(
				startDt, endDt, doLaaName, issuanceType);

		return aa;
	}

	/**
	 * Returns an array of IssuancePtioDetails objects.
	 * 
	 * @return IssuancePtioDetails[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public IssuancePtioDetails[] retrieveExpiredPtioDetails(Timestamp startDt,
			Timestamp endDt, String doLaaName, boolean hideShutdownFacility,
			boolean hideExemptPBR, boolean hideEUPermitStatusTRS,
			boolean hideEUExemptionDmPe, boolean hideEUShutdownInvalid,
			boolean hidePtoPtioEuActivePBR, String issuanceType)
			throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		IssuancePtioDetails[] aa = reportsDAO().retrieveExpiredPtioDetails(
				startDt, endDt, doLaaName, hideShutdownFacility, hideExemptPBR,
				hideEUPermitStatusTRS, hideEUExemptionDmPe,
				hideEUShutdownInvalid, hidePtoPtioEuActivePBR, issuanceType);

		return aa;
	}

	/**
	 * Returns an array of IssuanceSptoDetails objects.
	 * 
	 * @return IssuanceSptoDetails[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	/*
	public IssuanceSptoDetails[] retrieveExpiredSptoDetails(Timestamp startDt,
			Timestamp endDt, String doLaaName, boolean hideShutdownFacility,
			boolean hideExemptPBR, boolean hideEUPermitStatusTRS,
			boolean hideEUExemptionDmPe, boolean hideEUShutdownInvalid,
			boolean hidePtoPtioEuActivePBR, String issuanceType)
			throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		IssuanceSptoDetails[] aa = reportsDAO().retrieveExpiredSptoDetails(
				startDt, endDt, doLaaName, hideShutdownFacility, hideExemptPBR,
				hideEUPermitStatusTRS, hideEUExemptionDmPe,
				hideEUShutdownInvalid, hidePtoPtioEuActivePBR, issuanceType);

		return aa;
	}
	*/

	/**
	 * Returns an array of IssuanceTvDetails objects.
	 * 
	 * @return IssuanceTvDetails[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public IssuanceTvDetails[] retrieveIssuanceTvDetails(Timestamp startDt,
			Timestamp endDt, String doLaaName, String issuanceType)
			throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		IssuanceTvDetails[] aa = reportsDAO().retrieveIssuanceTvDetails(
				startDt, endDt, doLaaName, issuanceType);

		return aa;
	}

	/**
	 * Returns an array of IssuanceTvDetails objects.
	 * 
	 * @return IssuanceTvDetails[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public IssuanceTvDetails[] retrieveExpiredTvDetails(Timestamp startDt,
			Timestamp endDt, String doLaaName, boolean hideShutdownFacility,
			boolean hideExemptPBR, boolean hideEUPermitStatusTRS,
			boolean hideEUExemptionDmPe, boolean hideEUShutdownInvalid,
			boolean hidePtoPtioEuActivePBR, String issuanceType)
			throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		IssuanceTvDetails[] aa = reportsDAO().retrieveExpiredTvDetails(startDt,
				endDt, doLaaName, hideShutdownFacility, hideExemptPBR,
				hideEUPermitStatusTRS, hideEUExemptionDmPe,
				hideEUShutdownInvalid, hidePtoPtioEuActivePBR, issuanceType);

		return aa;
	}

	/**
	 * Returns an array of IssuancePbrDetails objects.
	 * 
	 * @return IssuancePbrDetails[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	/*
	public IssuancePbrDetails[] retrieveIssuancePbrDetails(Timestamp startDt,
			Timestamp endDt, String doLaaName) throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		IssuancePbrDetails[] aa = reportsDAO().retrieveIssuancePbrDetails(
				startDt, endDt, doLaaName);

		HashMap<Integer, IssuancePbrDetails> details = new HashMap<Integer, IssuancePbrDetails>();

		for (IssuancePbrDetails a : aa) {
			IssuancePbrDetails ipd = details.get(a.getPbrId());

			if (ipd == null) {
				details.put(a.getPbrId(), a);
				ipd = a;
			}
		}

		return details.values().toArray(new IssuancePbrDetails[0]);
	}
	*/

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveIssuedPermitsByGeneralByType(
			Timestamp startDt, Timestamp endDt, String permitType,
			String issuanceType, String doLaaName) throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		return reportsDAO().retrieveIssuedPermitsByGeneralByType(startDt,
				endDt, permitType, issuanceType, doLaaName);
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveExpiredPermitsByGeneralByType(
			Timestamp startDt, Timestamp endDt, String permitType,
			String issuanceType, String doLaaName) throws DAOException {
		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		return reportsDAO().retrieveExpiredPermitsByGeneralByType(startDt,
				endDt, permitType, issuanceType, doLaaName);
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveActivePermitsByGeneralByType(
			String permitType, String issuanceType, String doLaas,
			Integer userId, String activityName, String activityStatusCd)
			throws DAOException {

		if ("Total".equalsIgnoreCase(doLaas) || (" ".equalsIgnoreCase(doLaas)))
			doLaas = null;

		if ("Total".equalsIgnoreCase(activityName))
			activityName = null;

		return reportsDAO().retrieveActivePermitsByGeneralByType(permitType,
				issuanceType, doLaas, userId, activityName, activityStatusCd);
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveIssuedPermitsByReasonByType(Timestamp startDt,
			Timestamp endDt, String permitType, String issuanceType,
			String doLaaName) throws DAOException {
		HashMap<String, Integer> details = new HashMap<String, Integer>();

		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		SimpleIssuanceReason[] aa = reportsDAO()
				.retrieveIssuedPermitsByReasonByType(startDt, endDt,
						permitType, issuanceType, doLaaName);

		//
		// We are retrieving the Reason codes for all Issued permits for our
		// interval range. The result set is ordered by Permit Id and Reason
		// code.
		for (SimpleIssuanceReason a : aa) {
			String reason = a.getReasonDsc();
			if (details.containsKey(reason))
				details.put(reason, details.get(reason) + 1);
			else
				details.put(reason, new Integer(1));
		}
		SimpleIdDef[] sd = new SimpleIdDef[details.size()];
		Set<String> HostKeys = details.keySet();

		Iterator<String> It = HostKeys.iterator();
		int ii = 0;
		while (It.hasNext()) {
			String key = It.next();
			sd[ii] = new SimpleIdDef(details.get(key), key);
			ii++;
		}

		return sd;
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveExpiredPermitsByReasonByType(
			Timestamp startDt, Timestamp endDt, String permitType,
			String issuanceType, String doLaaName) throws DAOException {
		HashMap<String, Integer> details = new HashMap<String, Integer>();

		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		SimpleIssuanceReason[] aa = reportsDAO()
				.retrieveExpiredPermitsByReasonByType(startDt, endDt,
						permitType, issuanceType, doLaaName);

		//
		// We are retrieving the Reason codes for all Issued permits for our
		// interval range. The result set is ordered by Permit Id and Reason
		// code.
		for (SimpleIssuanceReason a : aa) {
			String reason = a.getReasonDsc();
			if (details.containsKey(reason))
				details.put(reason, details.get(reason) + 1);
			else
				details.put(reason, new Integer(1));
		}
		SimpleIdDef[] sd = new SimpleIdDef[details.size()];
		Set<String> HostKeys = details.keySet();

		Iterator<String> It = HostKeys.iterator();
		int ii = 0;
		while (It.hasNext()) {
			String key = It.next();
			sd[ii] = new SimpleIdDef(details.get(key), key);
			ii++;
		}

		return sd;
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveActivePermitsByReasonByType(String permitType,
			String issuanceState, String doLaas, Integer userId,
			String activityName, String activityStatusCd) throws DAOException {
		HashMap<String, Integer> details = new HashMap<String, Integer>();

		if ("Total".equalsIgnoreCase(doLaas) || (" ".equalsIgnoreCase(doLaas)))
			doLaas = null;

		if ("Total".equalsIgnoreCase(activityName))
			activityName = null;

		SimpleIssuanceReason[] aa = reportsDAO()
				.retrieveActivePermitsByReasonByType(permitType, issuanceState,
						doLaas, userId, activityName, activityStatusCd);

		//
		// We are retrieving the Reason codes for all Issued permits for our
		// interval range. The result set is ordered by Permit Id and Reason
		// code.
		for (SimpleIssuanceReason a : aa) {
			String reason = a.getReasonDsc();
			if (details.containsKey(reason))
				details.put(reason, details.get(reason) + 1);
			else
				details.put(reason, new Integer(1));

		}
		SimpleIdDef[] sd = new SimpleIdDef[details.size()];
		Set<?> HostKeys = details.keySet();

		Iterator<?> It = HostKeys.iterator();
		int ii = 0;
		while (It.hasNext()) {
			String key = (String) (It.next());
			sd[ii] = new SimpleIdDef(details.get(key), key);
			ii++;
		}

		return sd;
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveIssuedPermitsByDirectFinalByType(
			Timestamp startDt, Timestamp endDt, String permitType,
			String issuanceType, String doLaaName) throws DAOException {
		SimpleIdDef[] sd = new SimpleIdDef[2];

		sd[0] = new SimpleIdDef(0, "Direct Final");
		sd[1] = new SimpleIdDef(0, "Final after Draft");

		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		ReportsDAO repDao = reportsDAO();

		SimplePermitId[] aa = repDao.retrieveIssuedPermitsByFinalByType(
				startDt, endDt, permitType, issuanceType, doLaaName);

		SimplePermitId[] bb = repDao
				.retrieveIssuedPermitsByDraftByType(permitType);

		for (SimplePermitId a : aa) {
			//
			// Assume we not find a Draft Issuance
			//
			sd[0].setId(sd[0].getId() + 1);
			for (SimplePermitId b : bb) {
				if (a.getPermitId().equals(b.getPermitId())) {
					sd[1].setId(sd[1].getId() + 1);

					//
					// Back out our assumption - this permit is Final after
					// Draft
					sd[0].setId(sd[0].getId() - 1);
					break;
				}
			}
		}

		return sd;
	}

	/**
	 * Returns an array of SimepleIdDef objects.
	 * 
	 * @return SimpleIdDef[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public SimpleIdDef[] retrieveExpiredPermitsByDirectFinalByType(
			Timestamp startDt, Timestamp endDt, String permitType,
			String issuanceType, String doLaaName) throws DAOException {
		SimpleIdDef[] sd = new SimpleIdDef[2];

		sd[0] = new SimpleIdDef(0, "Direct Final");
		sd[1] = new SimpleIdDef(0, "Final after Draft");

		if ("Total".equalsIgnoreCase(doLaaName))
			doLaaName = null;

		ReportsDAO repDao = reportsDAO();

		SimplePermitId[] aa = repDao.retrieveExpiredPermitsByFinalByType(
				startDt, endDt, permitType, issuanceType, doLaaName);

		SimplePermitId[] bb = repDao.retrieveExpiredPermitsByDraftByType(
				permitType, startDt, endDt);

		HashSet<Integer> permitIdSet = new HashSet<Integer>();
		for (SimplePermitId a : aa) {
			permitIdSet.add(a.getPermitId());
			//
			// Assume we not find a Draft Issuance
			//
			sd[0].setId(sd[0].getId() + 1);
			for (SimplePermitId b : bb) {
				if (a.getPermitId().equals(b.getPermitId())) {
					sd[1].setId(sd[1].getId() + 1);
					permitIdSet.remove(a.getPermitId());

					//
					// Back out our assumption - this permit is Final after
					// Draft
					sd[0].setId(sd[0].getId() - 1);
					break;
				}
			}
		}

		// int s = permitIdSet.size();

		return sd;
	}

	/**
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public ProcessActivity[] retrieveActivityList(ProcessActivity pa,
			String doLaas) throws DAOException {
		return reportsDAO().retrieveActivityList(pa, doLaas);
	}

	/**
	 * Returns an array PermitSOPData objects. PermitSOPData are derrived data
	 * used to populate the Permit SOP report.
	 * 
	 * @param showNotes
	 * 
	 * @return PermitSOPData[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitSOPData[] retrievePermitSOPData(List<String> doLaas,
			List<String> permitTypes, List<String> reasonCds, boolean general,
			boolean express, boolean hideShutdownFacility,
			boolean hideDeadEndedPermit, boolean hideShutDownInvalidEU,
			boolean backlogged, boolean showAll, String type, boolean showNotes)
			throws DAOException {

		String intervalString = SystemPropertyDef.getSystemPropertyValue("PreliminaryCompletionInterval", null);
		int interval = new Integer(intervalString);

		String task = SystemPropertyDef.getSystemPropertyValue("PreliminaryCompletionTask", null);

		ReportsDAO repDao = reportsDAO();

		Long tNow = System.currentTimeMillis();

		HashMap<String, PermitSOPParams> sopMap = new HashMap<String, PermitSOPParams>();

		// Cache the duration filters to be used to match current incompleted
		// permit workflow tasks.
		//
		// The PermitSOPParams records contain the criteria to determine
		// the warning and danger durations of a given task within a given
		// permit workflow. The "in process" permit is consider in danger if
		// an incompleted task is has not been completed by the given interval.
		//
		PermitSOPParams[] params = repDao.retrievePermitParams();
		for (PermitSOPParams pd : params) {
			String key = pd.getSchedule() + "." + pd.getActivityName() + "."
					+ pd.getPermitGlobalStatusCd();
			sopMap.put(key, pd);
		}

		// Retrieve all permit workflow tasks plus related information
		// about the permit. Match the details of the permit (such as
		// permit type, reason_cd etc.) plus the task name with a duration
		// filter. A task is marked as a state of "ok", "warning", or
		// in "danger" depending on if the current date is past the
		// workflow process start date, plus the duration contain within
		// the duration filter.
		//
		PermitSOPData[] wfData = repDao.retrievePermitSOPData(doLaas,
				permitTypes, reasonCds, general, express, hideShutdownFacility,
				hideDeadEndedPermit);

		// Consolidate the Permit data (which has one entry per task)
		// into a Permit Process level view (single entry process view).
		//
		HashMap<Integer, PermitSOPData> pflowData = new HashMap<Integer, PermitSOPData>();
		Integer lastProcessId = null;
		Timestamp preliminaryCompleteDt = null;
		for (int i = 0; i < wfData.length; i++) {
			PermitSOPData wfd = wfData[i];
			// by default, display total count of permit EUs
			wfd.setPermitEuCount(wfd.getTotalPermitEuCount());
			if (hideShutDownInvalidEU) {
				if (wfd.getTotalPermitEuCount() != 0
						&& wfd.getValidPermitEuCount() == 0) {
					// skip records where there are permit EUs, but they
					// are all shutdown or invalid
					wfd.setExclude(true);
					continue;
				} else if (wfd.getValidPermitEuCount() > 0) {
					// display count of valid EUs if there are any
					wfd.setPermitEuCount(wfd.getValidPermitEuCount());
				}
			}

			// If starting next workflow, then determine preliminary complete
			// date
			if (lastProcessId == null
					|| !wfd.getProcessId().equals(lastProcessId)) {
				lastProcessId = wfd.getProcessId();
				// determine preliminary complete date which is the date that
				// the first
				// permitting step is finished (and the next started) Step 381
				// ("DO/LAA Preliminary Review")
				preliminaryCompleteDt = null;
				for (int j = i; j < wfData.length; j++) {
					PermitSOPData wfdJ = wfData[j];
					if (!wfdJ.getProcessId().equals(lastProcessId))
						break;
					if ("DO/LAA Preliminary Review".equalsIgnoreCase(wfdJ
							.getActivityName())) {
						// the last end date is the final end date for this
						// step; it is null if we are still in this step
						preliminaryCompleteDt = wfdJ.getTaskEndDt();
					} else
						break;
				}
			}

			// skip non-backlogged permits if backlogged flag is set
			if (backlogged
					&& !isPermitBacklogged(wfd.getApplicationReceivedDt(),
							wfd.getRenewalReceivedDt(), preliminaryCompleteDt,
							wfd.getPermitType())) {
				wfd.setExclude(true);
				continue;
			}

			PermitSOPData pdet = pflowData.get(wfd.getPermitId());
			if (pdet == null) {
				pflowData.put(wfd.getPermitId(), wfd);
				pdet = wfd;
			}

			//
			// Calculate the time any task has been referred (that is off the
			// clock.
			calculateCompanyTime(pdet, wfd, tNow, task);

			//
			// We want to show the current activity - this assumes that in the
			// permit workflow, one task is in process at any particular time.
			//
			if (wfd.getActivityStatusCd().equalsIgnoreCase("IP")
					|| wfd.getActivityStatusCd().equalsIgnoreCase("RF")) {
				pdet.setActivityName(wfd.getActivityName());
				pdet.setActivityStatusCd(wfd.getActivityStatusCd());
				pdet.setTaskStartDt(wfd.getTaskStartDt());
				pdet.setTaskEndDt(wfd.getTaskEndDt());
				pdet.setUserId(wfd.getUserId());
			}

			if (wfd.getActivityName().equalsIgnoreCase(
					"Technically Complete/T&C ready to review")) {
				pdet.setReviewerId(wfd.getUserId());
			} else if (wfd.getActivityName().equalsIgnoreCase(
					"CO Permit Review")) {
				if (type != null && type.equals(PermitSOPTypeDef.CO)) {
					pdet.setReviewerId(wfd.getUserId());
				}
			}
		}

		//
		// Based on the total referred time (i.e. company time), reset the
		// actual start time of the permit (works the same as updating the
		// due date).
		// setStartDt(pflowData, interval);

		preliminaryCompleteDt = null;
		lastProcessId = null;
		for (int i = 0; i < wfData.length; i++) {
			PermitSOPData wfd = wfData[i];
			// skip the same ones skipped above
			if(wfd.isExclude()) continue;

//    		if (hideShutDownInvalidEU) {
//				if (wfd.getTotalPermitEuCount() != 0 && wfd.getValidPermitEuCount() == 0) {
//					// skip records where there are permit EUs, but they
//					// are all shutdown or invalid
//					continue;
//				}
//			}
//
//			// If starting next workflow, then determine preliminary complete date
//			if(lastProcessId == null || !wfd.getProcessId().equals(lastProcessId)) {
//				lastProcessId = wfd.getProcessId();
//				// determine preliminary complete date which is the date that the first
//				// permitting step is finished (and the next started)  Step 381 ("DO/LAA Preliminary Review")
//				preliminaryCompleteDt = null;
//				for(int j = i; j < wfData.length; j++) {
//					PermitSOPData wfdJ = wfData[j];
//					if(!wfdJ.getProcessId().equals(lastProcessId)) break;
//					if("DO/LAA Preliminary Review".equalsIgnoreCase(wfdJ.getActivityName())) {
//						// the last end date is the final end date for this step; it is null if we are still in this step
//						preliminaryCompleteDt = wfdJ.getTaskEndDt();
//					} else break;
//				}
//			}
//
//			// skip non-backlogged permits if backlogged flag is set
//			if (backlogged && 
//					!isPermitBacklogged(wfd.getApplicationReceivedDt(), wfd.getRenewalReceivedDt(), preliminaryCompleteDt, wfd.getPermitType())) {
//				continue;
//			}			

			PermitSOPData pdet = pflowData.get(wfd.getPermitId());
			if (pdet == null) {
				continue;
			}

			//
			// Assume this task is OK - on time and the permit workflow is
			// not in any danger of meeting its SLA commitment.
			wfd.setStatus(PermitSOPData.OK);

			//
			// We are not interested in tasks that have already been
			// completed. If they were completed late and we have made up
			// the processing time, the permit is no longer in danger.
			if (wfd.getActivityStatusCd().equalsIgnoreCase("CM")
					|| wfd.getActivityStatusCd().equalsIgnoreCase("URF"))
				continue;

			pdet.setDays(new Long(new TimeSpan(new Timestamp(tNow), pdet
					.getStartDt()).getDays()));

			String schedule = lookUpSchedule(wfd);

			String key = schedule + "." + wfd.getActivityName() + "."
					+ wfd.getPermitGlobalStatusCd();

			//
			// Match this task to a cached duration filter
			//
			// If this task is not of interest (i.e. did not match a duration
			// filter) then we need to skip it.
			//
			PermitSOPParams pd = sopMap.get(key);
			if (pd == null)
				continue;

			long day = 0l;
			if (pd.getWarningDuration() != null) {
				day = pd.getWarningDuration().longValue();
			}
			Long warning = pdet.getStartDt().getTime()
					+ (day * 1000l * 3600l * 24l);

			day = 0l;
			if (pd.getDangerDuration() != null) {
				day = pd.getDangerDuration().longValue();
			}
			Long danger = pdet.getStartDt().getTime()
					+ (day * 1000l * 3600l * 24l);

			Long rDays = pdet.getReferredDays();
			Long pcrDays = pdet.getPcrRdays();
			//
			// If the initial review was not complete in 14 days and
			// this is not a TV permit then reset the number of referred days
			//
			if (!PermitTypeDef.TV_PTO.equalsIgnoreCase(pdet.getPermitType())
					&& pdet.getActivityName().equalsIgnoreCase(task)) {
				if (pcrDays > interval)
					rDays = rDays - pcrDays + interval;
			}

			long tAdj = (tNow - (rDays * 1000l * 3600l * 24l));

			if (tAdj > warning) {
				wfd.setStatus(PermitSOPData.WARNING);
			}
			if (tAdj > danger) {
				wfd.setStatus(PermitSOPData.DANGER);
			}

			//
			// We want to show the current activity - this assumes that in the
			// permit workflow, one task is in process at any particular time.
			//
			if (wfd.getStatus() > pdet.getStatus())
				pdet.setStatus(wfd.getStatus());
		}

		PermitSOPData[] det = pflowData.values().toArray(new PermitSOPData[0]);
		HashMap<String, PermitSOPData> sortMap = new HashMap<String, PermitSOPData>();

		//
		// Prepare a sort based on the sortKey combination attribute
		for (PermitSOPData wfd : det) {
			//
			// Filter out the Permits which are OK
			if (!showAll) {
				if (wfd.getStatus().equals(PermitSOPData.OK))
					continue;
			}

			String sortKey = wfd.getFacilityId() + "." + wfd.getPermitId();

			if (type != null && type.equals(PermitSOPTypeDef.PRELIMINARY)) {
				if (!wfd.getActivityName().equalsIgnoreCase(
						"DO/LAA Preliminary Review")) {
					continue;
				}
			} else if (type != null && type.equals(PermitSOPTypeDef.CO)) {
				if (!wfd.getActivityName().equalsIgnoreCase(
						"CO Review Determination")
						&& !wfd.getActivityName().equalsIgnoreCase(
								"CO Permit Review")
						&& !wfd.getActivityName().equalsIgnoreCase(
								"CO Permit Approval")) {
					continue;
				}
			}
			sortMap.put(sortKey, wfd);
		}

		Set<String> HostKeys = sortMap.keySet();
		Iterator<String> It = HostKeys.iterator();
		Vector<String> HKeys = new Vector<String>();
		while (It.hasNext()) {
			HKeys.add(It.next());
		}

		Collections.sort(HKeys);

		PermitSOPData[] data = new PermitSOPData[sortMap.size()];

		for (int ii = 0; ii < HKeys.size(); ii++) {
			String K = HKeys.elementAt(ii);
			data[ii] = sortMap.get(K);
		}

		if (showNotes) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
			for (int l = 0; l < data.length; l++) {
				PermitSOPData tpd = data[l];
				ProcessNote pn = new ProcessNote();
				pn.setProcessId(tpd.getProcessId());
				ProcessNote[] pns = workFlowDAO().retrieveProcessNotes(pn);
				for (ProcessNote tpn : pns)
					tpd.addNote("[" + sdf.format(tpn.getPostedDt()) + "]: "
							+ tpn.getNote() + "\n");
			}
		}
		return data;
	}

	/**
	 * Determine whether a permit with the specified parameter is backlogged.
	 * The criteria for a backlogged permit are:
	 * 
	 * * For PTI/PTIO it is most recent application's preliminary completeness
	 * date [from workflow] + 180 days, or, the most recent application received
	 * date [in app] + 60 days + 180 days, whichever is smaller.
	 * 
	 * * For Title V Permit it is 540 days + the application received date for the
	 * OLDEST RENEWAL application received that is associated with the permit.
	 * 
	 * @param applicationReceivedDt
	 * @param preliminaryCompleteDt
	 * @param permitType
	 * @return
	 */
	private boolean isPermitBacklogged(Timestamp applicationReceivedDt,
			Timestamp renewalReceivedDt, Timestamp preliminaryCompleteDt,
			String permitType) {
		Calendar backlogDate = null;
		Calendar appBacklogDate = null;
		Calendar renewalDate = null;
		Calendar wfBacklogDate = null;
		Calendar now = Calendar.getInstance();

		if (applicationReceivedDt != null) {
			appBacklogDate = Calendar.getInstance();
			appBacklogDate.setTime(applicationReceivedDt);
		}
		if (renewalReceivedDt != null) {
			renewalDate = Calendar.getInstance();
			renewalDate.setTime(renewalReceivedDt);
		}
		if (preliminaryCompleteDt != null) {
			wfBacklogDate = Calendar.getInstance();
			wfBacklogDate.setTime(preliminaryCompleteDt);
		}
		if (PermitTypeDef.TV_PTO.equals(permitType)) {
			if (renewalDate != null) {
				backlogDate = (Calendar) renewalDate.clone();
				backlogDate.add(Calendar.DAY_OF_YEAR, 540);
			}
		} else if (
				//PermitTypeDef.TVPTI.equals(permitType)
				//|| 
				PermitTypeDef.NSR.equals(permitType)
				) {
			if (appBacklogDate != null) {
				appBacklogDate.add(Calendar.DAY_OF_YEAR, 60 + 180);
				if (wfBacklogDate != null) {
					wfBacklogDate.add(Calendar.DAY_OF_YEAR, 180);
					if (wfBacklogDate.before(appBacklogDate)) {
						backlogDate = wfBacklogDate;
					} else {
						backlogDate = appBacklogDate;
					}
				} else {
					// no workflow backlog date, use application backlog date
					backlogDate = appBacklogDate;
				}
			}
		}
		// if no backlog date was found, we're assuming the permit is backlogged
		return (backlogDate == null || backlogDate.before(now));
	}

	/**
	 * Returns an array PermitSOPData objects. PermitSOPData are derived data
	 * used to populate the LAte Permits report.
	 * 
	 * @return PermitSOPData[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitSOPData[] retrieveLatePermitsData(List<String> doLaas,
			List<String> permitTypes, List<String> reasonCds, boolean general,
			boolean express, boolean showAll, String type) throws DAOException {

		String intervalString = SystemPropertyDef.getSystemPropertyValue("PreliminaryCompletionInterval", null);
		int interval = new Integer(intervalString);

		String task = SystemPropertyDef.getSystemPropertyValue("PreliminaryCompletionTask", null);

		ReportsDAO repDao = reportsDAO();

		Long tNow = System.currentTimeMillis();

		HashMap<String, PermitSOPParams> sopMap = new HashMap<String, PermitSOPParams>();

		// Cache the duration filters to be used to match current incompleted
		// permit workflow tasks.
		//
		// The PermitSOPParams records contain the criteria to determine
		// the warning and danger durations of a given task within a given
		// permit workflow. The "in process" permit is consider in danger if
		// an incompleted task is has not been completed by the given interval.
		//
		PermitSOPParams[] params = repDao.retrievePermitParams();
		for (PermitSOPParams pd : params) {
			String key = pd.getSchedule() + "." + pd.getActivityName() + "."
					+ pd.getPermitGlobalStatusCd();
			sopMap.put(key, pd);
		}

		// Retrieve all permit workflow tasks plus related information
		// about the permit. Match the details of the permit (such as
		// permit type, reason_cd etc.) plus the task name with a duration
		// filter. A task is marked as a state of "ok", "warning", or
		// in "danger" depending on if the current date is past the
		// workflow process start date, plus the duration contain within
		// the duration filter.
		//
		PermitSOPData[] wfData = repDao.retrievePermitSOPData(doLaas,
				permitTypes, reasonCds, general, express, false, false);

		// Consolidate the Permit data (which has one entry per task)
		// into a Permit Process level view (single entry process view).
		//
		HashMap<Integer, PermitSOPData> pflowData = new HashMap<Integer, PermitSOPData>();

		for (PermitSOPData wfd : wfData) {
			PermitSOPData pdet = pflowData.get(wfd.getPermitId());

			if (pdet == null) {
				pflowData.put(wfd.getPermitId(), wfd);
				pdet = wfd;
				continue;
			}

			//
			// Calculate the time any task has been referred (that is off the
			// clock.
			calculateCompanyTime(pdet, wfd, tNow, task);

			//
			// We want to show the current activity - this assumes that in the
			// permit workflow, one task is in process at any particular time.
			//
			if (wfd.getActivityStatusCd().equalsIgnoreCase("IP")
					|| wfd.getActivityStatusCd().equalsIgnoreCase("RF")) {
				pdet.setActivityName(wfd.getActivityName());
				pdet.setActivityStatusCd(wfd.getActivityStatusCd());
				pdet.setTaskStartDt(wfd.getTaskStartDt());
				pdet.setTaskEndDt(wfd.getTaskEndDt());
				pdet.setUserId(wfd.getUserId());
			}
			if (wfd.getActivityName().equalsIgnoreCase(
					"Technically Complete/T&C ready to review")) {
				pdet.setReviewerId(wfd.getUserId());
			} else if (wfd.getActivityName().equalsIgnoreCase(
					"CO Permit Review")) {
				if (type != null && type.equals(PermitSOPTypeDef.CO)) {
					pdet.setReviewerId(wfd.getUserId());
				}
			}
		}

		//
		// Based on the total referred time (i.e. company time), reset the
		// actual start time of the permit (works the same as updating the
		// due date).
		// setStartDt(pflowData, interval);

		for (PermitSOPData wfd : wfData) {
			//
			// Assume this task is OK - on time and the permit workflow is
			// not in any danger of meeting its SLA commitment.
			wfd.setStatus(PermitSOPData.OK);

			//
			// We are not interested in tasks that have already been
			// completed. If they were completed late and we have made up
			// the processing time, the permit is no longer in danger.
			if (wfd.getActivityStatusCd().equalsIgnoreCase("CM")
					|| wfd.getActivityStatusCd().equalsIgnoreCase("URF"))
				continue;

			PermitSOPData pdet = pflowData.get(wfd.getPermitId());
			pdet.setDays(new Long(new TimeSpan(new Timestamp(tNow), pdet
					.getStartDt()).getDays()));

			String schedule = lookUpSchedule(wfd);

			String key = schedule + "." + wfd.getActivityName() + "."
					+ wfd.getPermitGlobalStatusCd();

			//
			// Match this task to a cached duration filter
			//
			// If this task is not of interest (i.e. did not match a duration
			// filter) then we need to skip it.
			//
			PermitSOPParams pd = sopMap.get(key);
			if (pd == null)
				continue;

			Integer warningDuration = pd.getWarningDuration();
			long day = 0l;
			if (warningDuration != null) {
				day = warningDuration.longValue();
			} else {
				// should never happen
				logger.error("Null warning duration for Permit SOP param with key: "
						+ key);
			}
			Long warning = pdet.getStartDt().getTime()
					+ (day * 1000l * 3600l * 24l);

			day = 0l;
			Integer dangerDuration = pd.getDangerDuration();
			if (dangerDuration != null) {
				day = dangerDuration.longValue();
			} else {
				// should never happen
				logger.error("Null danger duration for Permit SOP param with key: "
						+ key);
			}
			Long danger = pdet.getStartDt().getTime()
					+ (day * 1000l * 3600l * 24l);

			Long rDays = pdet.getReferredDays();
			Long pcrDays = pdet.getPcrRdays();
			//
			// If the initial review was not complete in 14 days and
			// this is not a TV permit then reset the number of referred days
			//
			if (!PermitTypeDef.TV_PTO.equalsIgnoreCase(pdet.getPermitType())
					&& pdet.getActivityName().equalsIgnoreCase(task)) {
				if (pcrDays > interval)
					rDays = rDays - pcrDays + interval;
			}

			long tAdj = (tNow - (rDays * 1000l * 3600l * 24l));

			if (tAdj > warning) {
				wfd.setStatus(PermitSOPData.WARNING);
			}
			if (tAdj > danger) {
				wfd.setStatus(PermitSOPData.DANGER);
			}

			//
			// We want to show the current activity - this assumes that in the
			// permit workflow, one task is in process at any particular time.
			//
			if (wfd.getStatus() > pdet.getStatus())
				pdet.setStatus(wfd.getStatus());
		}

		PermitSOPData[] det = pflowData.values().toArray(new PermitSOPData[0]);
		HashMap<String, PermitSOPData> sortMap = new HashMap<String, PermitSOPData>();

		//
		// Prepare a sort based on the sortKey combination attribute
		for (PermitSOPData wfd : det) {
			//
			// Filter out the Permits which are OK
			if (!showAll) {
				if (wfd.getStatus().equals(PermitSOPData.OK))
					continue;
			}

			String sortKey = wfd.getFacilityId() + "." + wfd.getPermitId();
			if (type != null && type.equals(PermitSOPTypeDef.PRELIMINARY)) {
				if (!wfd.getActivityName().equalsIgnoreCase(
						"DO/LAA Preliminary Review")) {
					continue;
				}
			} else if (type != null && type.equals(PermitSOPTypeDef.CO)) {
				if (!wfd.getActivityName().equalsIgnoreCase(
						"CO Review Determination")
						|| wfd.getActivityName().equalsIgnoreCase(
								"CO Permit Review")
						|| wfd.getActivityName().equalsIgnoreCase(
								"CO Permit Approval")) {
					continue;
				}
			} else {

			}
			sortMap.put(sortKey, wfd);
		}

		Set<String> HostKeys = sortMap.keySet();
		Iterator<String> It = HostKeys.iterator();
		Vector<String> HKeys = new Vector<String>();
		while (It.hasNext()) {
			HKeys.add(It.next());
		}

		Collections.sort(HKeys);

		PermitSOPData[] data = new PermitSOPData[sortMap.size()];

		for (int ii = 0; ii < HKeys.size(); ii++) {
			String K = HKeys.elementAt(ii);
			data[ii] = sortMap.get(K);
		}

		return data;
	}

	/**
	 * 
	 * 
	 * @return PermitSOPData[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitSOPData[] retrievePreliminaryReviewCompletedData(
			List<String> doLaas, List<String> permitTypes,
			List<String> reasonCds, Timestamp fromDate, Timestamp toDate)
			throws DAOException {

		ReportsDAO repDao = reportsDAO();
		Timestamp tNow = new Timestamp(System.currentTimeMillis());
		HashMap<String, PermitSOPParams> sopMap = new HashMap<String, PermitSOPParams>();

		// Cache the duration filters to be used to match current incompleted
		// permit workflow tasks.
		//
		// The PermitSOPParams records contain the criteria to determine
		// the warning and danger durations of a given task within a given
		// permit workflow. The "in process" permit is consider in danger if
		// an incompleted task is has not been completed by the given interval.
		//
		PermitSOPParams[] params = repDao.retrievePermitParams();
		for (PermitSOPParams pd : params) {
			String key = pd.getSchedule() + "." + pd.getActivityName() + "."
					+ pd.getPermitGlobalStatusCd();
			sopMap.put(key, pd);
		}

		// Retrieve all permit workflow tasks plus related information
		// about the permit. Match the details of the permit (such as
		// permit type, reason_cd etc.) plus the task name with a duration
		// filter. A task is marked as a state of "ok", "warning", or
		// in "danger" depending on if the current date is past the
		// workflow process start date, plus the duration contain within
		// the duration filter.
		//
		PermitSOPData[] wfData = repDao.retrievePreliminaryReviewCompletedData(
				doLaas, permitTypes, reasonCds, null, toDate);

		// Consolidate the Permit data (which has one entry per task)
		// into a Permit Process level view (single entry process view).
		//
		HashMap<Integer, PermitSOPData> pflowData = new HashMap<Integer, PermitSOPData>();
		ArrayList<Integer> removeList = new ArrayList<Integer>();

		for (PermitSOPData wfd : wfData) {
			PermitSOPData pdet = pflowData.get(wfd.getProcessId());

			if (pdet == null) {
				pflowData.put(wfd.getProcessId(), wfd);
				pdet = wfd;
			}

			if (wfd.getActivityName().equalsIgnoreCase(
					"Technically Complete/T&C ready to review")) {
				if (wfd.isCurrent()) {
					pdet.setReviewerId(wfd.getUserId());
				}
				continue;
			}

			if (wfd.getActivityName().equalsIgnoreCase(
					"DO/LAA Preliminary Review")) {
				if (wfd.isCurrent()) {
					pdet.setActivityName(wfd.getActivityName());
					pdet.setActivityStatusCd(wfd.getActivityStatusCd());
					pdet.setTaskStartDt(wfd.getTaskStartDt());
					pdet.setTaskEndDt(wfd.getTaskEndDt());
					pdet.setUserId(wfd.getUserId());
					if (pdet.getTaskEndDt() == null
							|| (fromDate != null && pdet.getTaskEndDt().before(
									fromDate))
							|| (toDate != null && pdet.getTaskEndDt().after(
									toDate))) {
						removeList.add(pdet.getProcessId());
					}
				}
				Long addDays = new Long(new TimeSpan(wfd.getTaskEndDt(),
						wfd.getTaskStartDt()).getDays());
				pdet.setDays(pdet.getDays() + addDays);
				//
				// Calculate the time any task has been referred (that is off
				// the
				// clock.
				calculateCompanyTime(pdet, wfd, tNow.getTime(),
						"DO/LAA Preliminary Review");
			}
		}

		for (Integer k : removeList)
			pflowData.remove(k);

		return pflowData.values().toArray(new PermitSOPData[0]);
	}

	private void calculateCompanyTime(PermitSOPData pdet, PermitSOPData wfd,
			Long tNow, String task) {
		//
		// If the Permit has been referred and to the company
		// determine the total time that is considered to be company time.
		// This time is essentially ignored with respect to the SLA
		// established for a given milestone.
		if ((wfd.getActivityStatusCd().equalsIgnoreCase("RF") || wfd
				.getActivityStatusCd().equalsIgnoreCase("URF"))) {
			Integer refId = wfd.getActivityReferralTypeId();
			// DENNIS FINISH THIS TO GET FROM REFERENCE TABLE--based upon
			// activity referral type id.
			boolean b = "Y".equalsIgnoreCase(wfd.getExtendProcessEndDate());
			if (b) {
				if (wfd.getTaskEndDt() != null)
					tNow = wfd.getTaskEndDt().getTime();

				// long rDays = Math.round((tNow -
				// wfd.getTaskStartDt().getTime()) / (1000 * 3600 * 24));
				Integer rDays = new TimeSpan(new Timestamp(tNow),
						wfd.getTaskStartDt()).getDays();

				//
				// Calculate how many days that this process has been on the
				// company's clock
				pdet.setReferredDays(pdet.getReferredDays() + rDays);

				//
				// For the separate SLA agreement of 14 days for the
				// completeness
				// review calculate how many days have we been on the company's
				// clock.
				if (wfd.getActivityName().equalsIgnoreCase(task))
					pdet.setPcrRdays(pdet.getPcrRdays() + rDays);
			}
		}
	}

	/**
	 * 
	 * 
	 * @return PermitSOPData[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public IssuedMetricsData[] retrieveIssuedMetricsData(List<String> doLaas,
			String permitType, List<String> reasonCds,
			List<String> countyCds, List<String> selectedPermitActionTypes,
			String dateBy,
			Timestamp fromDate, Timestamp toDate, boolean showNotes)
			throws DAOException {

		ReportsDAO repDao = reportsDAO();

		// Retrieve all permit workflow tasks plus related information
		// about the permit. Match the details of the permit (such as
		// permit type, reason_cd etc.) plus the task name with a duration
		// filter. A task is marked as a state of "ok", "warning", or
		// in "danger" depending on if the current date is past the
		// workflow process start date, plus the duration contain within
		// the duration filter.
		//
		IssuedMetricsData[] wfData = repDao.retrieveIssuedMetricsData(doLaas,
				permitType, reasonCds, countyCds, selectedPermitActionTypes, dateBy, fromDate, toDate);

		// Consolidate the Permit data (which has one entry per task)
		// into a Permit Process level view (single entry process view).
		//
		HashMap<Integer, IssuedMetricsData> pflowData = new HashMap<Integer, IssuedMetricsData>();
		
		

		for (IssuedMetricsData wfd : wfData) {
			IssuedMetricsData pdet = pflowData.get(wfd.getProcessId());

			if (pdet == null) {
				pflowData.put(wfd.getProcessId(), wfd);
				pdet = pflowData.get(wfd.getProcessId());
				pdet.setPreliminaryDays(new Long(0));
				pdet.setTotalAgencyDays(new Long(0));
				pdet.setTotalNonAgencyDays(new Long(0));
			}

			if (wfd.getActivityName().equalsIgnoreCase(
					"Tech Review/Draft Permit/Waiver")) {
				if (wfd.isCurrent()) {
					pdet.setReviewerIdDOLAA(wfd.getUserId());
				}
			}
			
			if (wfd.getActivityName().equalsIgnoreCase(
					"Completeness Review")) {
				if (wfd.getLoopCnt() == 1) {
					pdet.setCompletenessReviewStartDt(wfd.getTaskStartDt());
				}
				if (wfd.isCurrent()) {
					pdet.setCompletenessReviewEndDt(wfd.getTaskEndDt());
				}
			}
			
			if (wfd.getActivityName().equalsIgnoreCase(
					"Tech Review/Draft Permit/Waiver")) {
				if (wfd.getLoopCnt() == 1) {
					pdet.setTechReviewStartDt(wfd.getTaskStartDt());
				}
			}
			
			if (wfd.getActivityName().equalsIgnoreCase(
					"Manager/Supervisor Review")) {
				if (wfd.getLoopCnt() == 1) {
					pdet.setManagerReviewStartDt(wfd.getTaskStartDt());
				}
			}
			
			if (wfd.getActivityName().equalsIgnoreCase(
					"Receipt Letter") ||
					wfd.getActivityName().equalsIgnoreCase(
							"Completeness Review")) {
				pdet.setPreliminaryDays(new Long(pdet.getPreliminaryDays() + wfd.getAgencyDays()));
			}

			pdet.setTotalAgencyDays(new Long(pdet.getTotalAgencyDays() + wfd.getAgencyDays()));
			pdet.setTotalNonAgencyDays(new Long(pdet.getTotalNonAgencyDays() + wfd.getNonAgencyDays()));
		}

		if (showNotes) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
			for (IssuedMetricsData imd : pflowData.values()) {
				ProcessNote pn = new ProcessNote();
				pn.setProcessId(imd.getProcessId());
				ProcessNote[] pns = workFlowDAO().retrieveProcessNotes(pn);
				for (ProcessNote tpn : pns)
					imd.addNote("[" + sdf.format(tpn.getPostedDt()) + "]: "
							+ tpn.getNote() + "\n");
			}
		}

		return pflowData.values().toArray(new IssuedMetricsData[0]);
	}
	
	/**
	 * 
	 * 
	 * @return PermitSOPData[]
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public IssuedMetricsData[] retrieveNotIssuedMetricsData(List<String> doLaas,
			String permitType, List<String> reasonCds,
			List<String> countyCds, List<String> selectedPermitActionTypes,
			String dateBy,
			Timestamp fromDate, Timestamp toDate, boolean showNotes,
			boolean excludeDeadEnded, boolean excludeIssuedWithdrawal)
			throws DAOException {

		ReportsDAO repDao = reportsDAO();

		// Retrieve all permit workflow tasks plus related information
		// about the permit. Match the details of the permit (such as
		// permit type, reason_cd etc.) plus the task name with a duration
		// filter. A task is marked as a state of "ok", "warning", or
		// in "danger" depending on if the current date is past the
		// workflow process start date, plus the duration contain within
		// the duration filter.
		//
		IssuedMetricsData[] wfData = repDao.retrieveNotIssuedMetricsData(doLaas,
				permitType, reasonCds, countyCds, selectedPermitActionTypes, dateBy, fromDate, toDate, 
				excludeDeadEnded, excludeIssuedWithdrawal);

		// Consolidate the Permit data (which has one entry per task)
		// into a Permit Process level view (single entry process view).
		//
		HashMap<Integer, IssuedMetricsData> pflowData = new HashMap<Integer, IssuedMetricsData>();
		
		

		for (IssuedMetricsData wfd : wfData) {
			IssuedMetricsData pdet = pflowData.get(wfd.getProcessId());

			if (pdet == null) {
				pflowData.put(wfd.getProcessId(), wfd);
				pdet = pflowData.get(wfd.getProcessId());
				pdet.setPreliminaryDays(new Long(0));
				pdet.setTotalAgencyDays(new Long(0));
				pdet.setTotalNonAgencyDays(new Long(0));
			}

			if (wfd.getActivityName().equalsIgnoreCase(
					"Tech Review/Draft Permit/Waiver")) {
				if (wfd.isCurrent()) {
					pdet.setReviewerIdDOLAA(wfd.getUserId());
				}
			}
			
			if (wfd.getActivityName().equalsIgnoreCase(
					"Completeness Review")) {
				if (wfd.getLoopCnt() == 1) {
					pdet.setCompletenessReviewStartDt(wfd.getTaskStartDt());
				}
				if (wfd.isCurrent()) {
					pdet.setCompletenessReviewEndDt(wfd.getTaskEndDt());
				}
			}
			
			if (wfd.getActivityName().equalsIgnoreCase(
					"Tech Review/Draft Permit/Waiver")) {
				if (wfd.getLoopCnt() == 1) {
					pdet.setTechReviewStartDt(wfd.getTaskStartDt());
				}
			}
			
			if (wfd.getActivityName().equalsIgnoreCase(
					"Manager/Supervisor Review")) {
				if (wfd.getLoopCnt() == 1) {
					pdet.setManagerReviewStartDt(wfd.getTaskStartDt());
				}
			}
			
			if (wfd.getActivityName().equalsIgnoreCase(
					"Receipt Letter") ||
					wfd.getActivityName().equalsIgnoreCase(
							"Completeness Review")) {
				pdet.setPreliminaryDays(new Long(pdet.getPreliminaryDays() + wfd.getAgencyDays()));
			}

			pdet.setTotalAgencyDays(new Long(pdet.getTotalAgencyDays() + wfd.getAgencyDays()));
			pdet.setTotalNonAgencyDays(new Long(pdet.getTotalNonAgencyDays() + wfd.getNonAgencyDays()));
		}

		if (showNotes) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yy");
			for (IssuedMetricsData imd : pflowData.values()) {
				ProcessNote pn = new ProcessNote();
				pn.setProcessId(imd.getProcessId());
				ProcessNote[] pns = workFlowDAO().retrieveProcessNotes(pn);
				for (ProcessNote tpn : pns)
					imd.addNote("[" + sdf.format(tpn.getPostedDt()) + "]: "
							+ tpn.getNote() + "\n");
			}
		}

		return pflowData.values().toArray(new IssuedMetricsData[0]);
	}

	private String lookUpSchedule(PermitSOPData wfd) {
		String schedule = "X";

		String reasonCd = null;
		List<SelectItem> allReasonDefs = PermitReasonsDef.getAllPermitReasons();
		for (SelectItem item : allReasonDefs) {
			if (item.getLabel().equals(wfd.getReason())) {
				reasonCd = item.getValue().toString();
				break;
			}
		}

		//
		// Schedule A: TV PTI or PTIO, Non Renewal, General Permit,
		// Non Express, Non Draft
		if ((
				//PermitTypeDef.TVPTI.equalsIgnoreCase(wfd.getPermitType()) || 
				PermitTypeDef.NSR
				.equalsIgnoreCase(wfd.getPermitType())
				)
				&& !PermitReasonsDef.RENEWAL.equalsIgnoreCase(reasonCd)
				/* General Permit not valid for WY && wfd.getGeneral().equalsIgnoreCase("Y") */
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("N"))
			schedule = "A";

		//
		// Schedule B: TV PTI or PTIO, Non Renewal, General Permit,
		// Non Express, Draft
		else if ((
				//PermitTypeDef.TVPTI.equalsIgnoreCase(wfd.getPermitType()) || 
						PermitTypeDef.NSR
				.equalsIgnoreCase(wfd.getPermitType()))
				&& !PermitReasonsDef.RENEWAL.equalsIgnoreCase(reasonCd)
				/* General Permit not valid for WY && wfd.getGeneral().equalsIgnoreCase("Y") */
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("Y"))
			schedule = "B";

		//
		// Schedule C: TV PTI or PTIO, Non Renewal, Non-General Permit,
		// Express, Non-Draft
		else if ((
				//PermitTypeDef.TVPTI.equalsIgnoreCase(wfd.getPermitType()) || 
						PermitTypeDef.NSR
				.equalsIgnoreCase(wfd.getPermitType()))
				&& !PermitReasonsDef.RENEWAL.equalsIgnoreCase(reasonCd)
				/* General Permit not valid for WY && wfd.getGeneral().equalsIgnoreCase("N") */
				&& wfd.getExpress().equalsIgnoreCase("Y")
				&& wfd.getIssueDraft().equalsIgnoreCase("N"))
			schedule = "C";

		//
		// Schedule D: TV PTI or PTIO, Non Renewal or Renewal,
		// Non-General Permit, Non-Express, Non-Draft
		else if ((
				//PermitTypeDef.TVPTI.equalsIgnoreCase(wfd.getPermitType()) || 
				PermitTypeDef.NSR
				.equalsIgnoreCase(wfd.getPermitType()))
				/* General Permit not valid for WY && wfd.getGeneral().equalsIgnoreCase("N") */
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("N"))
			schedule = "D";

		//
		// Schedule E: TV PTI or PTIO, Non Renewal or Renewal,
		// Non-General Permit, Non-Express, Draft
		else if ((
				//PermitTypeDef.TVPTI.equalsIgnoreCase(wfd.getPermitType()) || 
				PermitTypeDef.NSR
				.equalsIgnoreCase(wfd.getPermitType()))
				/* General Permit not valid for WY && wfd.getGeneral().equalsIgnoreCase("N") */
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("Y"))
			schedule = "E";

		//
		// Schedule F: Title V Permit, Installed Installation, Renewal, SPM pr
		// Reopening,
		// Non-General Permit, Non-Express, Draft
		else if (PermitTypeDef.TV_PTO.equalsIgnoreCase(wfd.getPermitType())
				&& (PermitReasonsDef.RENEWAL.equalsIgnoreCase(reasonCd)
						|| PermitReasonsDef.INITIAL
								.equalsIgnoreCase(reasonCd)
						|| PermitReasonsDef.SPM.equalsIgnoreCase(wfd
								.getReason()) || PermitReasonsDef.REOPENING
							.equalsIgnoreCase(reasonCd))
				/* General Permit not valid for WY && wfd.getGeneral().equalsIgnoreCase("N") */
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("Y"))
			schedule = "F";
		// Is not including Title V Permit in the reports but are getting them from the
		// Database
		// maybe are not supposed to be in the report
		// called for the LatePermits report
		//
		// Schedule G: Title V Permit, Minor Mod,
		// Non-General Permit, Non-Express, Draft
		else if (PermitTypeDef.TV_PTO.equalsIgnoreCase(wfd.getPermitType())
				&& PermitReasonsDef.MPM.equalsIgnoreCase(reasonCd)
				/* General Permit not valid for WY && wfd.getGeneral().equalsIgnoreCase("N") */
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("Y"))
			schedule = "G";

		//
		// Schedule H: Title V Permit, APP or Off Permit
		// Non-General Permit, Non-Express, Non-Draft
		else if (PermitTypeDef.TV_PTO.equalsIgnoreCase(wfd.getPermitType())
				&& (PermitReasonsDef.APA.equalsIgnoreCase(reasonCd) || PermitReasonsDef.CHANGE_502_B_10
						.equalsIgnoreCase(reasonCd))
				/* General Permit not valid for WY  && wfd.getGeneral().equalsIgnoreCase("N") */
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("N"))
			schedule = "H";

		
		/**Remove PBR references in permit applications**/

		/**		

		//
		// Schedule I:PBR, Revocation Needed(New atrributefor filter)
		// Non-General Permit, Non-Express, Non-Draft
		else if (PermitTypeDef.PBR.equalsIgnoreCase(wfd.getPermitType())
				// General Permit not valid for WY  && wfd.getGeneral().equalsIgnoreCase("N")
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("N"))
			schedule = "I";

		//
		// Schedule J:PBR, No Revocation Needed????
		// Non-General Permit, Non-Express, Non-Draft
		else if (PermitTypeDef.PBR.equalsIgnoreCase(wfd.getPermitType())
				// General Permit not valid for WY  && wfd.getGeneral().equalsIgnoreCase("N")
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("N"))
			schedule = "J";

		//
		// Schedule K:PBR, Rescind for Cause (Chris - same durations as I)
		// Non-General Permit, Non-Express, Non-Draft
		else if (PermitTypeDef.PBR.equalsIgnoreCase(wfd.getPermitType())
				// General Permit not valid for WY  && wfd.getGeneral().equalsIgnoreCase("N")
				&& wfd.getExpress().equalsIgnoreCase("N")
				&& wfd.getIssueDraft().equalsIgnoreCase("N"))
			schedule = "K";
		
		**/

		return schedule;
	}

	/**
	 * Returns a TopsData object used to populate the U.S EPA TOPS report.
	 * 
	 * @return TOPSData
	 * 
	 * @throws DAOException
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public TOPSData retrieveTOPSData(Timestamp reportDate) throws DAOException {
		TOPSData topsData = new TOPSData();
		topsData = reportsDAO().retrieveTOPSData(reportDate);
		
		// get counts for 6.a and 6.b
		topsPartSixReports(topsData);	
		
		// add extended permits (6.b) count to topsPartThree count
		topsData.setTvActivePTOCount(topsData.getTvActivePTOCount() + topsData.getTvExtendedCount());
		
		return topsData;
	}

	/**
	 * 
	 * @throws java.lang.Exception
	 *             Database access error.
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public PermitExpirationDetails[] retrievePermitExpirationDetails(
			Timestamp startDt, Timestamp endDt, boolean hideShutdownFacility,
			boolean hideExemptPBR, boolean hideEUPermitStatusTRS,
			boolean hideEUExemptionDmPe, boolean hideEUShutdownInvalid,
			boolean hidePtoPtioEuActivePBR) throws DAOException {

		DoLaaDef[] defs = infrastructureDAO().retrieveDoLaas();
		HashMap<String, PermitExpirationDetails> details = new HashMap<String, PermitExpirationDetails>();

		PermitExpirationDetails id;
		for (DoLaaDef d : defs) {
			id = new PermitExpirationDetails();
			id.setDoLaaName(d.getDescription());
			id.setDoLaaShortDsc(d.getDoLaaShortDsc());
			id.setDoLaaCd(d.getCode());
			details.put(d.getDoLaaID(), id);
		}
		id = new PermitExpirationDetails();
		id.setDoLaaShortDsc("Total");
		id.setDoLaaName("Total");
		details.put("zzz", id);

		Issuance[] issuePermits = reportsDAO().retrievePermitExpiration(
				startDt, endDt, hideShutdownFacility, hideExemptPBR,
				hideEUPermitStatusTRS, hideEUExemptionDmPe,
				hideEUShutdownInvalid, hidePtoPtioEuActivePBR);
		PermitExpirationDetails colTotal = details.get("zzz");
		for (Issuance data : issuePermits) {
			PermitExpirationDetails x = details.get(data.getDoLaaId());

			//if (data.getPermitType().equalsIgnoreCase(PermitTypeDef.TVPTI)) {
			//	continue;
			//} else 
				if (data.getPermitType()
					.equalsIgnoreCase(PermitTypeDef.NSR)) {
				x.setPtio(x.getPtio() + 1);
				colTotal.setPtio(colTotal.getPtio() + 1);
			} else if (data.getPermitType().equalsIgnoreCase(
					PermitTypeDef.TV_PTO)) {
				x.setTv(x.getTv() + 1);
				colTotal.setTv(colTotal.getTv() + 1);
			//} else if (data.getPermitType()
			//		.equalsIgnoreCase(PermitTypeDef.SPTO)) {
			//	x.setSpto(x.getSpto() + 1);
			//	colTotal.setSpto(colTotal.getSpto() + 1);
			}
		}

		// Sort the Colletion by DO_LAA_CODE - make sure that Total is
		// the last row header.
		Set<String> HostKeys = details.keySet();

		Iterator<String> It = HostKeys.iterator();
		Vector<String> HKeys = new Vector<String>();
		while (It.hasNext()) {
			HKeys.add(It.next());
		}

		Collections.sort(HKeys);

		PermitExpirationDetails[] data = new PermitExpirationDetails[details
				.size()];

		for (int ii = 0; ii < HKeys.size(); ii++) {
			String K = HKeys.elementAt(ii);
			data[ii] = details.get(K);
		}

		return data;
	}

	/**
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public WorkloadTrend[] retrieveWorkloadTrendReport(String permitTypes[],
			String permitReasons[]) throws DAOException {
		return reportsDAO().retrieveWorkloadTrendReport(permitTypes,
				permitReasons);
	}

	/**
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public GenericIssuanceCount[] retrieveGenericIssuanceCount(
			String doLaaCds[], String issuanceTypeCds[], Timestamp startDt,
			Timestamp endDt) throws DAOException {
		return reportsDAO().retrieveGenericIssuanceCount(doLaaCds,
				issuanceTypeCds, startDt, endDt);
	}

	/**
	 * 
	 * @ejb.interface-method view-type="remote"
	 * @ejb.transaction type="NotSupported"
	 */
	public List<PEROverdueDetails> retrievePEROverdueDetails(String countyCd,
			String doLaaCd) throws RemoteException {
		// Timestamp now = new Timestamp(System.currentTimeMillis());
		// List<PEROverdueDetails> details = new ArrayList<PEROverdueDetails>();
		// List<String> facilities =
		// facilityDAO().retrieveFacilitiesWithActivePERDueDate(doLaaCd,
		// countyCd);
		// try {
		// ComplianceReportService complianceReportBO =
		// ServiceFactory.getInstance().getComplianceReportService();
		// FacilityService facilityBO =
		// ServiceFactory.getInstance().getFacilityService();
		// for (String facilityId : facilities) {
		// Facility facility = facilityBO.retrieveFacility(facilityId, false);
		// List<PerReportingPeriod> perList = new
		// ArrayList<PerReportingPeriod>();
		// complianceReportBO.retrievePerReportingPeriods(facility.getFacilityId(),
		// perList);
		// ComplianceReportList[] perReports =
		// complianceReportBO.searchComplianceReports(null,
		// facility.getFacilityId(),
		// null, null, "per", "sbmt", null, null, null, null, null, null, null,
		// true);
		// outer: for (PerReportingPeriod period : perList) {
		// if (period.getDueDate().after(now)) {
		// // don't worry about due dates in the future
		// continue;
		// }
		// // skip periods for which there is already a PER defined
		// for (ComplianceReportList report : perReports) {
		// Timestamp perStartDate = period.getStartDate();
		// Timestamp reportStartDate = report.getPerStartDate();
		// perStartDate.setNanos(0);
		// reportStartDate.setNanos(0);
		// if (report.getDueDateCd().equals(period.getDueDateCd()) &&
		// reportStartDate.equals(perStartDate)) {
		// continue outer;
		// }
		// }
		// CompliancePerDetail[] crDetails = period.retrieveEusForPeriod();
		// for (CompliancePerDetail crDetail : crDetails) {
		// PEROverdueDetails detail = new PEROverdueDetails();
		// detail.setPerDueDate(period.getDueDate());
		// detail.setFacilityName(facility.getName());
		// detail.setFacilityId(facility.getFacilityId());
		// EmissionUnit eu =
		// facility.getMatchingEmissionUnit(crDetail.getEUId());
		// detail.setEpaEmuId(eu.getEpaEmuId());
		// detail.setEuDescription(eu.getEuDesc());
		// details.add(detail);
		// }
		// }
		// }
		// } catch (ServiceFactoryException e) {
		// logger.error("Exception accessing ComplianceReportService", e);
		// throw new DAOException(e.getMessage(), e);
		// }
		// return details;
		//
		return null;
	}
	
	
	private void topsPartSixReports(TOPSData topsData) throws DAOException {
		
		PermitDAO permitDAO = permitDAO();
		
        Timestamp endDate = new Timestamp(System.currentTimeMillis());
        Timestamp beginDate = beginDateFromDate(endDate);
        
        int extendedTVPermitsCount = 0;
        int expiredTVPermitsCount = 0;
        
        // First retrieve all permits that are expired as of today...
        logger.debug("Searching TVPTO permits with expiration date on or after: " + beginDate);

        /*List<Permit> tempPermits = permitDAO.searchPermits(applicationNumber,null, null,
                permitType, permitReason, legacyPermitNumber, permitNumber, facilityID,
                facilityName, permitStatusCd, dateBy, beginDate, endDate, null, true);*/
        ArrayList<Integer> tempPermits = permitDAO.retrieveExpiredTVPermits(endDate);

        logger.debug("" + tempPermits.size() + " permits retrieved");
        if (tempPermits.size() > 0) {
            for (Integer tp : tempPermits) {
                Transaction trans = TransactionFactory.createTransaction();
                permitDAO.setTransaction(trans);
                ApplicationDAO appDAO = applicationDAO(trans);
                FacilityDAO facDAO = facilityDAO(trans);
                try {
                    Permit permit = permitDAO.retrievePermit(tp);

                    Facility f = facDAO.retrieveFacility(permit.getFacilityId());

                    if (f.getOperatingStatusCd().equalsIgnoreCase(OperatingStatusDef.SD)) {
                        logger.debug("Facility " + permit.getFacilityId() + " is shut down. " +
                        		"Not processing permit " + permit.getPermitNumber());
                        continue;
                    }
                    
                	logger.debug("Processing permit " + permit.getPermitNumber()) ;

                    /*if (permit.getEuGroups() == null || permit.getEus() == null) {
                    	logger.debug("No EUs in permit " + permit.getPermitNumber()) ;
                        continue;
                    }
                    if (permit.getEus().size() == 0) {
                    	logger.debug("No EUs in permit " + permit.getPermitNumber()) ;
                        continue;
                    }*/
                    
                    Integer renewalApp = retrieveRenewalTVApps(permit
                            .getFacilityId(), appDAO, permit.getExpirationDate());

                    if (renewalApp != null) {
                    	logger.debug("Counting Permit " + permit.getPermitNumber() + " as extended. " +
                    			"Renwal application id = " + renewalApp) ;
                    	extendedTVPermitsCount++;
                    } else {
                    	logger.debug("Counting Permit " + permit.getPermitNumber() + " as expired") ;
                    	expiredTVPermitsCount++;
                    }
                    
                    trans.complete();
                } catch (DAOException de) {
                    cancelTransaction(trans, de);
                } finally {
                    closeTransaction(trans);
                }
            }
        }
        
        topsData.setTvExtendedCount(extendedTVPermitsCount);
        topsData.setTvExpiredCount(expiredTVPermitsCount);
	}
	
	private Timestamp beginDateFromDate(Timestamp date) {
        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(date.getTime());
        // check for all permits in the last year if it's the first Saturday of the month
        if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY &&
        		now.get(Calendar.DAY_OF_MONTH ) <= 7) {
	        now.add(Calendar.YEAR, -3);
        } else {
        	// otherwise, check permits issued in the last month
	        now.add(Calendar.MONTH, -2);
        }
        return new Timestamp(now.getTimeInMillis());
    }
	
    private Integer retrieveRenewalTVApps(String facilityId,
            ApplicationDAO appDAO, Timestamp expirationDate)
            throws DAOException {

        Calendar now = new GregorianCalendar();
        now.setTimeInMillis(expirationDate.getTime());
        Calendar upperDate = new GregorianCalendar(now
                .get(Calendar.YEAR), now.get(Calendar.MONTH), now
                .get(Calendar.DAY_OF_MONTH));
        Calendar lowerDate = new GregorianCalendar(now
                .get(Calendar.YEAR), now.get(Calendar.MONTH), now
                .get(Calendar.DAY_OF_MONTH));

        upperDate.add(Calendar.MONTH, -18);
        lowerDate.add(Calendar.DAY_OF_MONTH, -180);

        return appDAO.retrieveRenewalApplications(facilityId, 
                new Timestamp(upperDate.getTimeInMillis()), 
                new Timestamp(lowerDate.getTimeInMillis()));
    }
}
