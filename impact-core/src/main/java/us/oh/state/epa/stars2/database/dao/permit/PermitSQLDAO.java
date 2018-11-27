package us.oh.state.epa.stars2.database.dao.permit;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Resource;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.CommonConst;
import us.oh.state.epa.stars2.bo.InfrastructureBO;
import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ApplicationDAO;
import us.oh.state.epa.stars2.database.dao.PermitConditionDAO;
import us.oh.state.epa.stars2.database.dao.PermitDAO;
import us.oh.state.epa.stars2.database.dao.Transaction;
import us.oh.state.epa.stars2.database.dbObjects.BaseDBObject;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.document.Document;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Note;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleIdDef;
import us.oh.state.epa.stars2.database.dbObjects.permit.EUFee;
import us.oh.state.epa.stars2.database.dbObjects.permit.EmissionsOffset;
import us.oh.state.epa.stars2.database.dbObjects.permit.ExpiredPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.NSRFixedCharge;
import us.oh.state.epa.stars2.database.dbObjects.permit.PTIOPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitActivitySearch;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitCC;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitChargePayment;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitDocument;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEU;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitNote;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitWorkflowSearchResult;
import us.oh.state.epa.stars2.database.dbObjects.permit.RPRPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.TVPermit;
import us.oh.state.epa.stars2.database.dbObjects.permit.TimeSheetRow;
import us.oh.state.epa.stars2.def.ActivityStatusDef;
import us.oh.state.epa.stars2.def.NSRBillingBillableRateDef;
import us.oh.state.epa.stars2.def.NSRBillingStandardFeesDef;
import us.oh.state.epa.stars2.def.PermitDocTypeDef;
import us.oh.state.epa.stars2.def.PermitFeeBalanceTypeDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.def.PermitWorkflowActivityBenchmarkDef;
import us.oh.state.epa.stars2.framework.config.CompMgr;
import us.oh.state.epa.stars2.framework.exception.DAOException;
import us.oh.state.epa.stars2.framework.util.Utility;

@Repository
public class PermitSQLDAO extends AbstractDAO implements PermitDAO {

	private Logger logger = Logger.getLogger(PermitSQLDAO.class);

	public static final String EXPIRATION_DATE = "p.EXPIRATION_DATE";
	public static final String EFFECTIVE_DATE = "p.EFFECTIVE_DATE";
	public static final String MOD_EFFECTIVE_DATE = "p.MOD_EFFECTIVE_DATE";
	public static final String FINAL_ISSUANCE_DATE = "pif.issuance_date";
	public static final String DRAFT_ISSUANCE_DATE = "pid.issuance_date";
	public static final String PUBLIC_NOTICE_PUBLISH_DATE = "pid.public_notice_publish_date";
	public static final String PP_ISSUANCE_DATE = "pipp.issuance_date";
	//public static final String PPP_ISSUANCE_DATE = "pippp.issuance_date";
	public static final String PERMIT_SENT_OUT_DATE = "pnsr.permit_sent_out_date";
	public static final String PTV_PERMIT_RECISSION_DATE = "ptv.recission_date";
	public static final String NSR_PERMIT_RECISSION_DATE = "pnsr.recission_date";
	public static final String NSR_PERMIT_LAST_INVOICE_REF_DATE = "pnsr.last_invoice_ref_date";
	public static final String PERMIT_BASIS_DATE = "ptv.permit_basis_date";

	@Resource
	private ApplicationDAO readOnlyApplicationDAO;
	
	@Resource
	private PermitConditionDAO readOnlyPermitConditionDAO;

	/**
	 * Returns summary data only - no EU groups
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#searchPermits(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public final List<Permit> searchPermits(String applicationNumber,
			String euId, String cmpId, String permitType, String permitReason, String permitLevelStatusCd, 
			String legacyPermitNumber, String permitNumber, String facilityID, String facilityName,
			String permitStatusCd, String dateBy, Timestamp beginDate,
			Timestamp endDate, String permitEUStatusCd, boolean unlimitedResults, String permitFeeBalanceTypeCd)
			throws DAOException {
		PreparedStatement ps = null;
		Connection conn = null;
		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}
		ArrayList<Permit> retVal = new ArrayList<Permit>();

		try {
			conn = getReadOnlyConnection();
			StringBuffer projSql = new StringBuffer(
					loadSQL("PermitSQL.searchPermits"));

			//if ("FEPTIO".equals(permitType)) {
			//	permitType = "PTIO";
			//	projSql.append(replaceSchema("\nJOIN %Schema%pt_ptio_permit ptio ON (p.permit_id = ptio.permit_id AND ptio.fe_ptio_flag = 'Y')\n"));
			//}

			StringBuffer whereSql = new StringBuffer();
			String conjuct = " WHERE ";
			if (applicationNumber != null && applicationNumber.length() != 0) {
				applicationNumber = applicationNumber.trim();
				if (applicationNumber.contains("*")) {
					applicationNumber = applicationNumber.replace('*', '%');
				}
				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append("LOWER(%Schema%f_all_apps(p.permit_id)) ");
				whereSql.append(" LIKE LOWER('%" + applicationNumber + "%') ");
			}

			// bug 1996
			if ((euId != null && euId.length() > 0) || permitEUStatusCd != null) {
				whereSql.append(conjuct);
				conjuct = " AND ";

				String euWhere = loadSQL("PermitSQL.searchPermitsByEuId");
				if (euId != null && euId.length() > 0) {
					if (euId.contains("*")) {
						euId = euId.replace('*', '%');
					}
					if (euId.contains("%")) {
						euWhere = euWhere.replace("%PermitSearchEUWhere%",
								"LOWER(feu.EPA_EMU_ID) like LOWER('" + euId
										+ "')");
					} else {
						euWhere = euWhere
								.replace("%PermitSearchEUWhere%",
										"LOWER(feu.EPA_EMU_ID) = LOWER('"
												+ euId + "')");
					}
				}
				if (permitEUStatusCd != null) {
					if (euWhere.contains("%PermitSearchEUWhere%")) {
						// euId not specified
						euWhere = euWhere.replace("%PermitSearchEUWhere%",
								"pe.permit_status_cd = '" + permitEUStatusCd
										+ "'");
					} else {
						euWhere = euWhere.replaceFirst("WHERE ",
								"WHERE pe.permit_status_cd = '"
										+ permitEUStatusCd + "' AND ");
					}
				}
				whereSql.append(euWhere);
			}
			if (permitNumber != null && permitNumber.length() != 0) {
				permitNumber = permitNumber.trim();
				if (permitNumber.contains("*")) {
					permitNumber = permitNumber.replace('*', '%');
				}
				whereSql.append(conjuct);
				conjuct = " AND ";
				if (permitNumber.contains("%")) {
					whereSql.append("LOWER(p.permit_nbr) like LOWER('"
							+ permitNumber + "')");
				} else {
					whereSql.append("LOWER(p.permit_nbr) = LOWER('"
							+ permitNumber + "')");
				}
			}
			if (legacyPermitNumber != null && legacyPermitNumber.length() != 0) {
				if (legacyPermitNumber.contains("*")) {
					legacyPermitNumber = legacyPermitNumber.replace('*', '%');
				}
				whereSql.append(conjuct);
				conjuct = " AND ";
				if (legacyPermitNumber.contains("%")) {
					whereSql.append("LOWER(p.legacy_permit_nbr) like LOWER('"
							+ legacyPermitNumber + "')");
				} else {
					whereSql.append("LOWER(p.legacy_permit_nbr) = LOWER('"
							+ legacyPermitNumber + "')");
				}
			}
			if (facilityID != null && facilityID.length() != 0) {
				facilityID = formatFacilityId(facilityID);
				if (facilityID.contains("*")) {
					facilityID = facilityID.replace('*', '%');
				}
				whereSql.append(conjuct);
				conjuct = " AND ";
				if (facilityID.contains("%")) {
					whereSql.append("LOWER(ff.facility_id) like LOWER('"
							+ facilityID + "')");
				} else {
					whereSql.append("LOWER(ff.facility_id) = LOWER('"
							+ facilityID + "')");
				}
			}
			if (facilityName != null && facilityName.length() != 0) {
				if (facilityName.contains("*")) {
					facilityName = facilityName.replace('*', '%');
				}
				whereSql.append(conjuct);
				conjuct = " AND ";
				if (facilityName.contains("%")) {
					whereSql.append("LOWER(ff.facility_nm) like LOWER('"
							+ facilityName + "')");
				} else {
					whereSql.append("LOWER(ff.facility_nm) = LOWER('"
							+ facilityName + "')");
				}
			}

			if (permitType != null && permitType.length() != 0) {
				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append("p.PERMIT_TYPE_CD = '");
				whereSql.append(permitType);
				whereSql.append("'");
			}

			if (permitReason != null && permitReason.length() != 0) {
				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append("%Schema%f_all_reasons(p.permit_id) LIKE '%");
				whereSql.append(permitReason);
				whereSql.append("%'");
			}
			
			if (permitLevelStatusCd != null && permitLevelStatusCd.length() != 0) {
				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append("p.permit_level_status_cd = '");
				whereSql.append(permitLevelStatusCd);
				whereSql.append("'");
			}
			
			if (permitStatusCd != null && permitStatusCd.length() != 0) {
				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append("p.PERMIT_GLOBAL_STATUS_CD = '");
				whereSql.append(permitStatusCd);
				whereSql.append("'");
			}
			if (dateBy != null && dateBy.trim().length() != 0
					&& (beginDate != null || endDate != null)) {

				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append("(");
				if (beginDate != null) {
					whereSql.append(dateBy);
					whereSql.append(" >= ? ");
					if (endDate != null) {
						whereSql.append(" AND ");
					}
				}
				if (endDate != null) {
					whereSql.append(dateBy);
					whereSql.append(" <= ? ");
				}
				whereSql.append(" ) ");
			}

			if (cmpId != null && cmpId.length() != 0) {
				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append(" ccm.CMP_ID = '");
				whereSql.append(cmpId);
				whereSql.append("'");
			}

			// RPE and RPR should not be see as a permit.
			whereSql.append(conjuct);
			conjuct = " AND ";
			whereSql.append("p.PERMIT_TYPE_CD != 'RPE'");
			whereSql.append(conjuct);
			conjuct = " AND ";
			whereSql.append("p.PERMIT_TYPE_CD != 'RPR'");
			
			if (permitFeeBalanceTypeCd != null && permitFeeBalanceTypeCd.length() != 0
					&& permitFeeBalanceTypeCd.equalsIgnoreCase(PermitFeeBalanceTypeDef.NON_ZERO_BALANCES)) {
				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append("nsrbi.CURRENT_BALANCE != 0");
			}

			StringBuffer sql = new StringBuffer();
			sql.append(projSql);
			sql.append(" ");
			sql.append(whereSql);
			
			StringBuffer sortBy = new StringBuffer(" ORDER BY p.permit_nbr");
		    sql.append(sortBy);
			
			ps = conn.prepareStatement(replaceSchema(sql.toString()));

			int i = 1;
			if (dateBy != null && dateBy.trim().length() != 0
					&& beginDate != null) {

				ps.setTimestamp(i, formatBeginOfDay(beginDate));
				i++;
			}
			if (dateBy != null && dateBy.trim().length() != 0
					&& endDate != null) {

				ps.setTimestamp(i, formatEndOfDay(endDate));
				i++;
			}

			ResultSet res = ps.executeQuery();
			int numberOfHits = 0;

			while (res.next()) {

				String type = res.getString("permit_type_cd");

				Permit permit;
				// permit = initPermitByType(type);
				permit = new Permit();

				permit.setPermitType(type);
				permit.setPermitID(res.getInt("permit_id"));
				permit.setLegacyPermitNumber(res.getString("legacy_permit_nbr"));
				permit.setPermitNumber(res.getString("permit_nbr"));
				permit.setPermitGlobalStatusCD(res
						.getString("permit_global_status_cd"));
				permit.setPermitLevelStatusCd(res.getString("permit_level_status_cd"));
				permit.setFacilityId(res.getString("facility_id"));
				permit.setFpId(res.getInt("fp_id"));
				permit.setFacilityNm(res.getString("facility_nm"));
				permit.setDraftIssueDate(res
						.getTimestamp("Draft_Issuance_date"));
				permit.setFinalIssueDate(res
						.getTimestamp("Final_Issuance_date"));
				permit.setEffectiveDate(res.getTimestamp("effective_date"));
				permit.setExpirationDate(res.getTimestamp("expiration_date"));
//				permit.setEarliestSubmittedDate(res
//						.getTimestamp("EARLIEST_SUBMITTED_DATE"));
				permit.setCompanyName(res.getString("company_nm"));
				permit.setCmpId(res.getString("cmp_id"));
				permit.setDescription(res.getString("description"));

				String appNums = res.getString("all_apps");
				ArrayList<String> allApps = new ArrayList<String>();
				if (appNums != null && appNums.length() > 0) {
					StringTokenizer st = new StringTokenizer(appNums);
					while (st.hasMoreTokens()) {
						allApps.add(st.nextToken());
					}
				}
				permit.setApplicationNumbers(allApps.toArray(new String[0]));
				
				// need to set primary reason before setting reasons
				permit.setPrimaryReasonCD(res.getString("primary_reason_cd"));
				
				String reasons = res.getString("all_reasons");
				ArrayList<String> allReasons = new ArrayList<String>();
				if (reasons != null && reasons.length() > 0) {
					StringTokenizer str = new StringTokenizer(reasons);
					while (str.hasMoreTokens()) {
						allReasons.add(str.nextToken());
					}
					permit.setPermitReasonCDs(allReasons);
				}
				
				permit.setPublicNoticePublishDate(res
						.getTimestamp("public_notice_publish_date"));
				
				permit.setPermitSentOutDate(res
						.getTimestamp("permit_sent_out_date"));
				
				if (type.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
					permit.setRecissionDate(res.getTimestamp("ptv_recission_date"));
					permit.setPermitBasisDt(res.getTimestamp("permit_basis_date"));
				} else if (type.equalsIgnoreCase(PermitTypeDef.NSR)) {
					permit.setRecissionDate(res.getTimestamp("pnsr_recission_date"));
				}
				
				permit.setActionType(res.getString("action_type"));
				
				// nsr billing info fields
				permit.setInitialInvoiceAmount(AbstractDAO.getDouble(res, "initial_invoice_amt"));
				permit.setFinalInvoiceAmount(AbstractDAO.getDouble(res, "final_invoice_amt"));
				permit.setTotalCharges(AbstractDAO.getDouble(res, "total_charges"));
				permit.setTotalPayments(AbstractDAO.getDouble(res, "total_payments"));
				permit.setCurrentBalance(AbstractDAO.getDouble(res, "current_balance"));
				
				permit.setLastInvoiceRefDate(res.getTimestamp("last_invoice_ref_date"));
				
				permit.setLegacyPermit(AbstractDAO.translateIndicatorToBoolean(res
						.getString("legacy_flag")));
				
				retVal.add(permit);
				numberOfHits++;
				if ((defaultSearchLimit > 0)
						&& (numberOfHits >= defaultSearchLimit)) {
					break;
				}
			}
			res.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			handleException(ex, conn);
		} finally {
			closeStatement(ps);
			handleClosing(conn);
		}

		return retVal;
	}
	
	/**
	 * Returns summary data only - no EU groups
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#searchPermitsForFinalInvoice(java.lang.String,
	 *      java.lang.String, java.lang.String, boolean)
	 */
	public final List<PTIOPermit> searchPermitsForFinalInvoice(String cmpId,
			String facilityID, String facilityName,
			boolean unlimitedResults)
			throws DAOException {
		PreparedStatement ps = null;
		Connection conn = null;
		if (unlimitedResults) {
			setDefaultSearchLimit(-1);
		}
		ArrayList<PTIOPermit> retVal = new ArrayList<PTIOPermit>();

		try {
			conn = getReadOnlyConnection();
			StringBuffer projSql = new StringBuffer(
					loadSQL("PermitSQL.searchPermitsForFinalInvoice"));

			StringBuffer whereSql = new StringBuffer();
			String conjuct = " AND ";

			if (facilityID != null && facilityID.length() != 0) {
				facilityID = formatFacilityId(facilityID);
				if (facilityID.contains("*")) {
					facilityID = facilityID.replace('*', '%');
				}
				whereSql.append(conjuct);
				conjuct = " AND ";
				if (facilityID.contains("%")) {
					whereSql.append("LOWER(ff.facility_id) like LOWER('"
							+ facilityID + "')");
				} else {
					whereSql.append("LOWER(ff.facility_id) = LOWER('"
							+ facilityID + "')");
				}
			}
			
			if (facilityName != null && facilityName.length() != 0) {
				if (facilityName.contains("*")) {
					facilityName = facilityName.replace('*', '%');
				}
				whereSql.append(conjuct);
				conjuct = " AND ";
				if (facilityName.contains("%")) {
					whereSql.append("LOWER(ff.facility_nm) like LOWER('"
							+ facilityName + "')");
				} else {
					whereSql.append("LOWER(ff.facility_nm) = LOWER('"
							+ facilityName + "')");
				}
			}

			if (cmpId != null && cmpId.length() != 0) {
				whereSql.append(conjuct);
				conjuct = " AND ";
				whereSql.append(" ccm.CMP_ID = '");
				whereSql.append(cmpId);
				whereSql.append("'");
			}

			StringBuffer sql = new StringBuffer();
			sql.append(projSql);
			sql.append(" ");
			sql.append(whereSql);
			ps = conn.prepareStatement(replaceSchema(sql.toString()));

			logger.debug("sql.toString() = " + sql.toString());

			ResultSet res = ps.executeQuery();
			int numberOfHits = 0;

			while (res.next()) {

				String type = res.getString("permit_type_cd");

				PTIOPermit permit;
				permit = new PTIOPermit();

				permit.setPermitType(type);
				permit.setPermitID(res.getInt("permit_id"));
				permit.setPermitNumber(res.getString("permit_nbr"));
				permit.setPermitGlobalStatusCD(res
						.getString("permit_global_status_cd"));
				permit.setFacilityId(res.getString("facility_id"));
				permit.setFpId(res.getInt("fp_id"));
				permit.setFacilityNm(res.getString("facility_nm"));
				permit.setDraftIssueDate(res
						.getTimestamp("Draft_Issuance_date"));
				permit.setFinalIssueDate(res
						.getTimestamp("Final_Issuance_date"));
				permit.setEffectiveDate(res.getTimestamp("effective_date"));
				permit.setExpirationDate(res.getTimestamp("expiration_date"));
				
				permit.setCompanyName(res.getString("company_nm"));
				permit.setCmpId(res.getString("cmp_id"));
				
				// need to set primary reason before setting reasons
				permit.setPrimaryReasonCD(res.getString("primary_reason_cd"));
				
				String reasons = res.getString("all_reasons");
				ArrayList<String> allReasons = new ArrayList<String>();
				if (reasons != null && reasons.length() > 0) {
					StringTokenizer str = new StringTokenizer(reasons);
					while (str.hasMoreTokens()) {
						allReasons.add(str.nextToken());
					}
					permit.setPermitReasonCDs(allReasons);
				}
				
				permit.setActionType(res.getString("action_type"));
				
				String appNums = res.getString("all_apps");
				ArrayList<String> allApps = new ArrayList<String>();
				if (appNums != null && appNums.length() > 0) {
					StringTokenizer st = new StringTokenizer(appNums);
					while (st.hasMoreTokens()) {
						allApps.add(st.nextToken());
					}
				}
				permit.setApplicationNumbers(allApps.toArray(new String[0]));

				retVal.add(permit);
				numberOfHits++;
				if ((defaultSearchLimit > 0)
						&& (numberOfHits >= defaultSearchLimit)) {
					break;
				}
			}
			res.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			handleException(ex, conn);
		} finally {
			closeStatement(ps);
			handleClosing(conn);
		}

		return retVal;
	}

	/**
	 * @see PermitDAO#retrievePermit(int permitId)
	 */
	public final Permit retrievePermit(Integer permitId) throws DAOException {

		Permit ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			StringBuffer sql = new StringBuffer(
					loadSQL("PermitSQL.retrievePermit"));
			sql.append(" WHERE pp.permit_id = ?");
			pStmt = conn.prepareStatement(sql.toString());
			pStmt.setInt(1, permitId.intValue());

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = initPermit(rs);
				ret = retrieveCompletePermit(ret);
			}

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;

	}

	/**
	 * @see PermitDAO#retrievePermit(String permitNbr)
	 */
	public final Permit retrievePermit(String permitNbr) throws DAOException {

		Permit ret = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			StringBuffer sql = new StringBuffer(
					loadSQL("PermitSQL.retrievePermit"));
			sql.append(" WHERE pp.permit_nbr = ?");
			pStmt = conn.prepareStatement(sql.toString());
			pStmt.setString(1, permitNbr);

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret = initPermit(rs);
				ret = retrieveCompletePermit(ret);
			}

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	/**
	 * @param rs
	 * @throws SQLException
	 */
	private Permit initPermit(ResultSet rs) throws SQLException {

		Permit ret;
		String pt = rs.getString("permit_type_cd");

		ret = initPermitByType(pt);

		ret.populate(rs);
		return ret;
	}

	private Permit initPermitByType(String pt) {
		Permit ret;

		if (pt.equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
			ret = new TVPermit();
			// } else if (pt.equalsIgnoreCase(PermitTypeDef.REG)) {
			// ret = new RegPermit();
		} else if (pt.equalsIgnoreCase(PermitTypeDef.RPR)) {
			ret = new RPRPermit();
		//} else if (pt.equalsIgnoreCase(PermitTypeDef.RPE)) {
		//	ret = new RPEPermit();
		//} else if (pt.equalsIgnoreCase(PermitTypeDef.TIV_PTO)) {
		//	ret = new TIVPermit();
		} else {
			// PTIO, TVPTI, SPTI, and SPTO
			
			// NSR
			ret = new PTIOPermit();
		}
		return ret;
	}

	/**
	 * @param permit
	 * @throws DAOException
	 */
	private Permit retrieveCompletePermit(Permit permit) throws DAOException {

		checkNull(permit);
		Integer permitID = permit.getPermitID();
		checkNull(permitID);

		permit = retrievePermitEUGroups(permit);

		Transaction trans = this.getTransaction();
		// ApplicationDAO appDAO = (ApplicationDAO)
		// DAOFactory.getDAO("ApplicationDAO", CommonConst.READONLY_SCHEMA);
		readOnlyApplicationDAO.setTransaction(trans);

		String facilityId = "No App";
		try {
			Application[] permitApps = readOnlyApplicationDAO
					.retrieveApplicationsIn(retrievePermitApplicationIds(permitID));

			for (Application permitApp : permitApps) {
				permit.addApplication(permitApp);
				facilityId = permitApp.getFacilityId();
			}
		} catch (DAOException e) {
			logger.error(e.getMessage(), e);
		}

		PermitDocument[] permitDocs = retrievePermitDocuments(permitID);
		for (PermitDocument permitDoc : permitDocs) {
			if (!(permitDoc.getFacilityID() != null && permitDoc
					.getFacilityID().length() > 0)) {
				permitDoc.setFacilityID(facilityId);
			}
			permit.addDocument(permitDoc);
			
			// commentsDocument list is used only by portal and public on Permits Summary page.
			if (!CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {

				if (permit.getPermitType().equals(PermitTypeDef.TV_PTO)) {
					if (permitDoc.getPermitDocTypeCD().equalsIgnoreCase(PermitDocTypeDef.TV_COMMENTS) || permitDoc
							.getPermitDocTypeCD().equalsIgnoreCase(PermitDocTypeDef.TV_RESPONSE_TO_COMMENTS)) {
						permit.addCommentsDocument(permitDoc);
					}
				} else if (permit.getPermitType().equals(PermitTypeDef.NSR)) {
					if (permitDoc.getPermitDocTypeCD().equalsIgnoreCase(PermitDocTypeDef.NSR_COMMENTS) || permitDoc
							.getPermitDocTypeCD().equalsIgnoreCase(PermitDocTypeDef.NSR_RESPONSE_TO_COMMENTS)) {
						permit.addCommentsDocument(permitDoc);
					}
				}
			}
		}

		LinkedHashMap<String, PermitIssuance> pi = retrievePermitIssuances(permitID);
		permit.setPermitIssuances(pi);

		permit.setPermitReasonCDs(retrievePermitReasonCDs(permitID));
		
		// Only retrieve the rest if internal app.
		if (CompMgr.getAppName().equals(CommonConst.INTERNAL_APP)) {

			permit = retrieveSubpartCDs(permit);

			List<PermitCC> ccList = retrievePermitCCList(permitID);
			permit.setPermitCCList(ccList);

			if (permit instanceof PTIOPermit) {
				((PTIOPermit) permit).setNSRFixedChargeList(retrieveNSRFixedChargeList(permitID));
				((PTIOPermit) permit).setPermitChargePaymentList(this.retrievePermitChargePaymentList(permitID));

				((PTIOPermit) permit).setCurrentTotal(this.retrievePermitChargePaymentTotal(permit.getPermitID()));

				((PTIOPermit) permit)
						.setEmissionsOffsetList(Arrays.asList(this.retrievePermitEmissionsOffsetByPermitId(permitID)));

			}

			permit.setPermitConditionList(readOnlyPermitConditionDAO.retrievePermitConditionList(permitID));

		}

		return permit;
	}

	/**
	 * @see PermitDAO#createPermit(Permit permit)
	 */
	public final Permit createPermit(Permit permit) throws DAOException {

		checkNull(permit);
		Permit ret = permit;
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermit", false);
		int id = nextSequenceVal("S_Permit_Id");
		int i = 1;
		connHandler.setInteger(i++, id);
		connHandler.setString(i++, permit.getPermitType());
		connHandler.setString(i++, permit.getPermitGlobalStatusCD());
		connHandler.setString(i++, permit.getLegacyPermitNumber());
		connHandler.setString(i++, permit.getPermitNumber());
		connHandler.setString(i++, permit.getDescription());
		connHandler.setTimestamp(i++, permit.getEffectiveDate());
		connHandler.setTimestamp(i++, permit.getModEffectiveDate());
		connHandler.setString(i++, permit.getEracCaseNumber());
		connHandler.setTimestamp(i++, permit.getExpirationDate());
		connHandler.setString(i++, permit.getFacilityId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isVariance()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isMact()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isNeshaps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isNsps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isMajorGHG()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isIssueDraft()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isDapcHearingReqd()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isLegacyPermit()));

		connHandler.setString(i++, permit.getPrimaryReasonCD());
		if (permit.getPublicNoticeType() == null) {
			connHandler.setString(i++, "SW");
		} else {
			connHandler.setString(i++, permit.getPublicNoticeType());
		}
		connHandler.setString(i++, permit.getPublicNoticeText());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isEarlyRenewalFlag()));
		connHandler.setString(i++, permit.getPublicNoticeNewspaperCd());
		connHandler.setString(i++, AbstractDAO.
				translateBooleanToIndicator(permit.isUpdateFacilityDtlPTETableComments()));
		connHandler.setString(i++, AbstractDAO.
				translateBooleanToIndicator(permit.isReceiptLetterSent()));
		connHandler.setString(i++, permit.getReceivedComments());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isPart61NESHAP()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isPart63NESHAP()));
		connHandler.setString(i++, permit.getPermitLevelStatusCd());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setPermitID(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see PermitDAO#createPTIOPermit(Permit permit)
	 */
	public final Permit createPTIOPermit(Permit permit) throws DAOException {

		checkNull(permit);
		Permit ret = permit;

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPTIOPermit", false);

		PTIOPermit tempPermit = (PTIOPermit) permit;

		int i = 1;
		connHandler.setInteger(i++, tempPermit.getPermitID());
		connHandler.setString(i++, tempPermit.getTmpPERDueDateCD());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isTv()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isConvertedToPTI()));
		connHandler.setTimestamp(i++, tempPermit.getConvertedToPTIDate());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(tempPermit.isNetting()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isEmissionsOffsets()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isSmtv()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isCem()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isModelingSubmitted()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isPsd()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isToxicReview()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isFeptio()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit
						.isNonFeptio5YrRenewal()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isAvoidMajorGHGSM()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isFePtioTvAvoid()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isFullCostRecovery()));
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(tempPermit.isExpress()));
		connHandler.setDouble(i++, tempPermit.getOtherAdjustment());
		connHandler.setDouble(i++, tempPermit.getInitialInvoice());
		connHandler.setDouble(i++, tempPermit.getFinalInvoice());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isGeneralPermit()));
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(tempPermit
								.isMajorNonAttainment()));
		connHandler.setTimestamp(i++, tempPermit.getPermitSentOutDate());
		connHandler.setString(i++, tempPermit.getInvoicePaid());
		connHandler.setString(i++, tempPermit.getCommentTransmittalLettersSentFlag());
		connHandler.setTimestamp(i++, tempPermit.getRecissionDate());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(tempPermit.isBillable()));

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see PermitDAO#createTVPermit(Permit permit)
	 */
	public final Permit createTVPermit(Permit permit) throws DAOException {

		checkNull(permit);
		Permit ret = permit;

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createTVPermit", false);

		TVPermit tempPermit = (TVPermit) permit;
		int i = 1;
		connHandler.setInteger(i++, tempPermit.getPermitID());
		//connHandler.setString(i++, AbstractDAO
		//		.translateBooleanToIndicator(tempPermit.isPppReviewWaived()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isUsepaExpedited()));
		connHandler.setString(i++, tempPermit.getUsepaOutcomeCd());
		connHandler.setTimestamp(i++, tempPermit.getUsepaCompleteDate());
		connHandler.setTimestamp(i++, tempPermit.getRecissionDate());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isAcidRain()));
		connHandler.setString(i++, tempPermit.getAcidDesc());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isCam()));
		connHandler.setString(i++, tempPermit.getCamDesc());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isSec112()));
		connHandler.setString(i++, tempPermit.getSec112Desc());
		connHandler.setTimestamp(i++, tempPermit.getUsepaReceivedPermitDate());
		connHandler.setTimestamp(i++, tempPermit.getUsepaPermitSentDate());
		connHandler.setTimestamp(i++, tempPermit.getPermitBasisDate());
		
		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see PermitDAO#createRegPermit(Permit permit)
	 */
	/*
	public final Permit createRegPermit(Permit permit) throws DAOException {

		checkNull(permit);
		Permit ret = permit;

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createRegPermit", false);

		RegPermit tempPermit = (RegPermit) permit;
		int i = 1;
		connHandler.setInteger(i++, tempPermit.getPermitID());
		connHandler.setTimestamp(i++, tempPermit.getReceivedDate());
		connHandler.setString(i++, tempPermit.getTc());
		connHandler.setString(i++, tempPermit.getEapStatus());
		connHandler.setTimestamp(i++, tempPermit.getProcessDate());
		connHandler.setString(i++, tempPermit.getWithdrawalCode());
		connHandler.setTimestamp(i++, tempPermit.getWithdrawalDate());
		connHandler.setString(i++, tempPermit.getAdjudCode());
		connHandler.setTimestamp(i++, tempPermit.getRenProcDate());
		connHandler.setString(i++, tempPermit.getAppendixCode());
		connHandler.setString(i++, tempPermit.getRenewalStatus());
		connHandler.setString(i++, tempPermit.getRenewalType());
		connHandler.setString(i++, tempPermit.getStateTermsCond());
		connHandler.setString(i++, tempPermit.getFacilityMajorPolluterCode());
		connHandler.setString(i++, tempPermit.getSicCode());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setLastModified(1);

		return ret;
	}
	*/

	/**
	 * @see PermitDAO#modifyMakePermitUndead(Permit permit)
	 */
	public final boolean modifyMakePermitUndead(Permit permit)
			throws DAOException {

		checkNull(permit);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyMakePermitUndead", false);
		int i = 1;
		connHandler.setString(i++, permit.getPermitGlobalStatusCD());
		connHandler.setInteger(i++, permit.getPermitID());
		connHandler.setInteger(i++, permit.getLastModified());
		return connHandler.update();
	}

	/**
	 * @see PermitDAO#modifyPermit(Permit permit)
	 */
	public final boolean modifyPermit(Permit permit) throws DAOException {
		
		checkNull(permit);
		
		String legacyPermitNumber = permit.getLegacyPermitNumber();
		if (StringUtils.isEmpty(permit.getLegacyPermitNumber())) {
    		legacyPermitNumber = null;
    	}
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPermit", false);

		int i = 1;
		connHandler.setString(i++, permit.getPermitType());
		connHandler.setString(i++, permit.getPermitGlobalStatusCD());
    	connHandler.setString(i++, legacyPermitNumber);
		connHandler.setString(i++, permit.getPermitNumber());
		connHandler.setString(i++, permit.getDescription());
		connHandler.setTimestamp(i++, permit.getEffectiveDate());
		connHandler.setTimestamp(i++, permit.getModEffectiveDate());
		connHandler.setString(i++, permit.getEracCaseNumber());
		connHandler.setTimestamp(i++, permit.getExpirationDate());
		connHandler.setString(i++, permit.getFacilityId());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isVariance()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isMact()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isNeshaps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isNsps()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isMajorGHG()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(permit.isIssueDraft()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isDapcHearingReqd()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isLegacyPermit()));
		connHandler.setString(i++, permit.getPrimaryReasonCD());
		connHandler.setString(i++, permit.getPublicNoticeType());
		connHandler.setString(i++, permit.getPublicNoticeText());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isEarlyRenewalFlag()));
		connHandler.setString(i++, permit.getPublicNoticeNewspaperCd());
		connHandler.setString(i++, AbstractDAO.
				translateBooleanToIndicator(permit.isUpdateFacilityDtlPTETableComments()));
		connHandler.setString(i++, AbstractDAO.
				translateBooleanToIndicator(permit.isReceiptLetterSent()));
		connHandler.setString(i++, permit.getReceivedComments());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isPart61NESHAP()));	
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(permit.isPart63NESHAP()));
		connHandler.setString(i++, permit.getPermitLevelStatusCd());
		connHandler.setInteger(i++, permit.getLastModified() + 1);

		connHandler.setInteger(i++, permit.getPermitID());
		connHandler.setInteger(i++, permit.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see PermitDAO#modifyPTIOPermit(Permit permit)
	 */
	public final boolean modifyPTIOPermit(Permit permit) throws DAOException {

		checkNull(permit);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPTIOPermit", false);

		PTIOPermit tempPermit = (PTIOPermit) permit;

		int i = 1;
		connHandler.setString(i++, tempPermit.getTmpPERDueDateCD());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isTv()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isConvertedToPTI()));
		connHandler.setTimestamp(i++, tempPermit.getConvertedToPTIDate());
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(tempPermit.isNetting()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isEmissionsOffsets()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isSmtv()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isCem()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isModelingSubmitted()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isPsd()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isToxicReview()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isFeptio()));
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit
						.isNonFeptio5YrRenewal()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isAvoidMajorGHGSM()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isFePtioTvAvoid()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isFullCostRecovery()));
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(tempPermit.isExpress()));
		connHandler.setDouble(i++, tempPermit.getOtherAdjustment());
		connHandler.setDouble(i++, tempPermit.getInitialInvoice());
		connHandler.setDouble(i++, tempPermit.getFinalInvoice());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isGeneralPermit()));
		connHandler
				.setString(i++, AbstractDAO
						.translateBooleanToIndicator(tempPermit
								.isMajorNonAttainment()));
		connHandler.setString(i++, tempPermit.getModelingRequired());
		connHandler.setString(i++, tempPermit.getModelingCompletedDate());
		connHandler.setString(i++, tempPermit.getPermitActionType());
		connHandler.setString(i++, tempPermit.getSubjectToPSD());
		connHandler.setString(i++, tempPermit.getSubjectToNANSR());
		connHandler.setString(i++, tempPermit.getOtherTypeOfDemonstrationReq());
		connHandler.setTimestamp(i++, tempPermit.getPermitSentOutDate());
		connHandler.setString(i++, tempPermit.getInvoicePaid());
		connHandler.setString(i++, tempPermit.getCommentTransmittalLettersSentFlag());
		connHandler.setTimestamp(i++, tempPermit.getRecissionDate());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(tempPermit.isBillable()));
		connHandler.setTimestamp(i++, tempPermit.getLastInvoiceRefDate());

		connHandler.setInteger(i++, tempPermit.getLastModified() + 1);
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(
				tempPermit.getOffsetInformationVerified()));

		connHandler.setInteger(i++, tempPermit.getPermitID());
		connHandler.setInteger(i++, tempPermit.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see PermitDAO#modifyTVPermit(Permit permit)
	 */
	public final boolean modifyTVPermit(Permit permit) throws DAOException {

		checkNull(permit);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyTVPermit", false);

		TVPermit tempPermit = (TVPermit) permit;

		int i = 1;
		//connHandler.setString(i++, AbstractDAO
		//		.translateBooleanToIndicator(tempPermit.isPppReviewWaived()));
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isUsepaExpedited()));
		connHandler.setString(i++, tempPermit.getUsepaOutcomeCd());
		connHandler.setTimestamp(i++, tempPermit.getUsepaCompleteDate());
		connHandler.setTimestamp(i++, tempPermit.getRecissionDate());
		connHandler.setString(i++, AbstractDAO
				.translateBooleanToIndicator(tempPermit.isAcidRain()));
		connHandler.setString(i++, tempPermit.getAcidDesc());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isCam()));
		connHandler.setString(i++, tempPermit.getCamDesc());
		connHandler.setString(i++,
				AbstractDAO.translateBooleanToIndicator(tempPermit.isSec112()));
		connHandler.setString(i++, tempPermit.getSec112Desc());
		connHandler.setTimestamp(i++, tempPermit.getUsepaReceivedPermitDate());
		connHandler.setTimestamp(i++, tempPermit.getUsepaPermitSentDate());
		connHandler.setTimestamp(i++, tempPermit.getPermitBasisDate());
		connHandler.setInteger(i++, tempPermit.getLastModified() + 1);
		connHandler.setInteger(i++, tempPermit.getPermitID());
		connHandler.setInteger(i++, tempPermit.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see PermitDAO#removePermit(Integer permitId)
	 */
	public final void removePermit(Integer permitId) throws DAOException {

		checkNull(permitId);

//		TODO: SK for testing removePermitEUGroups(permitId);
		removePermitEUGroups(permitId);

		ConnectionHandler connHandler = null;
		connHandler = new ConnectionHandler("PermitSQL.removePermit", false);
		connHandler.setInteger(1, permitId);

		connHandler.remove();
	}

	/**
	 * @see PermitDAO#removePTIOPermit(Integer permitId)
	 */
	public final void removePTIOPermit(Integer permitId) throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePTIOPermit", false);
		connHandler.setInteger(1, permitId);

		connHandler.remove();
	}

	/**
	 * @see PermitDAO#removeTVPermit(Integer permitId)
	 */
	public final void removeTVPermit(Integer permitId) throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removeTVPermit", false);
		connHandler.setInteger(1, permitId);

		connHandler.remove();
	}

	/**
     * 
     */
	private Permit retrievePermitEUGroups(Permit permit) throws DAOException {

		checkNull(permit);
		checkNull(permit.getPermitID());

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveAllEUGroups", true);

		connHandler.setInteger(1, permit.getPermitID());

		ArrayList<PermitEUGroup> tempGroups = connHandler
				.retrieveArray(PermitEUGroup.class);

		Iterator<PermitEUGroup> it = tempGroups.iterator();
		while (it.hasNext()) {
			PermitEUGroup tempGroup = it.next();
			tempGroup = retrievePermitEUs(tempGroup);
			// tempGroup =
			// retrievePermitEUGroup(tempGroup.getPermitEUGroupID());
			permit.addEuGroup(tempGroup);
		}

		return permit;
	}

	/**
     * 
     */
	private PermitEUGroup retrievePermitEUs(PermitEUGroup euGroup)
			throws DAOException {

		checkNull(euGroup);
		checkNull(euGroup.getPermitEUGroupID());

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitEUs", true);

		connHandler.setInteger(1, euGroup.getPermitEUGroupID());

		ArrayList<PermitEU> tempEUs = connHandler.retrieveArray(PermitEU.class);

		Iterator<PermitEU> it = tempEUs.iterator();
		while (it.hasNext()) {
			PermitEU tempEU = it.next();
			euGroup.addPermitEU(tempEU);
		}

		return euGroup;
	}

	/**
     * 
     */
	public final void removePermitEUGroups(Integer permitId)
			throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removeAllPermitEUs", false);
		connHandler.setInteger(1, permitId);
		connHandler.remove();

		connHandler = new ConnectionHandler(
				"PermitSQL.removeAllPermitEUGroups", false);
		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}

	/**
	 * @see PermitDAO#removeEUGroupEUs(Integer permitId)
	 */
	public final void removeEUGroupEUs(Integer euGroupId) throws DAOException {

		checkNull(euGroupId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removeEUGroupEUs", false);

		connHandler.setInteger(1, euGroupId);
		connHandler.remove();
	}

	/**
	 * @see PermitDAO#retrievePermitEUGroup(Integer euGroupId)
	 */
	public final PermitEUGroup retrievePermitEUGroup(Integer euGroupId)
			throws DAOException {

		checkNull(euGroupId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitEUGroup", true);

		connHandler.setInteger(1, euGroupId);

		PermitEUGroup ret = (PermitEUGroup) connHandler
				.retrieve(PermitEUGroup.class);

		return ret;
	}

	/**
	 * @see PermitDAO#createPermitEUGroup(PermitEUGroup euGroup)
	 */
	public final PermitEUGroup createPermitEUGroup(PermitEUGroup euGroup)
			throws DAOException {

		checkNull(euGroup);
		checkNull(euGroup.getPermitID());

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createEUGroup", false);
		int id = nextSequenceVal("S_Permit_Eu_Group_Id");
		int i = 1;
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, euGroup.getPermitID());
		connHandler.setString(i++, euGroup.getName());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		euGroup.setPermitEUGroupID(id);
		euGroup.setLastModified(1);

		for (PermitEU peu : euGroup.getPermitEUs()) {
			peu.setPermitEUGroupID(euGroup.getPermitEUGroupID());

			if (peu.getPermitEUID() == null) {
				createPermitEU(peu);
			} else if (peu.isToBeDeleted()) {
				removePermitEU(peu.getPermitEUID());
			} else {
				modifyPermitEU(peu);
			}
		}

		return euGroup;
	}

	/**
	 * @see PermitDAO#modifyEUGroup(PermitEUGroup euGroup)
	 */
	public final void modifyPermitEUGroup(PermitEUGroup euGroup)
			throws DAOException {

		checkNull(euGroup);
		checkNull(euGroup.getPermitEUGroupID());

		for (PermitEU peu : euGroup.getPermitEUs()) {
			peu.setPermitEUGroupID(euGroup.getPermitEUGroupID());

			peu.setEuGroup(euGroup);
			if (peu.getPermitEUID() == null) {
				createPermitEU(peu);
			} else if (peu.isToBeDeleted()) {
				removePermitEU(peu.getPermitEUID());
			} else {
				modifyPermitEU(peu);
			}
		}

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyEUGroup", false);

		int i = 1;
		connHandler.setString(i++, euGroup.getName());
		connHandler.setInteger(i++, euGroup.getLastModified() + 1);

		connHandler.setInteger(i++, euGroup.getPermitEUGroupID());
		connHandler.setInteger(i++, euGroup.getLastModified());

		connHandler.update();
	}

	/**
	 * @see PermitDAO#removeEUGroup(PermitEUGroup euGroup)
	 */
	public final void removePermitEUGroup(PermitEUGroup euGroup)
			throws DAOException {

		checkNull(euGroup);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removeEUGroup", false);
		connHandler.setInteger(1, euGroup.getPermitEUGroupID());

		connHandler.remove();
	}

	/**
	 * @see PermitDAO#retrievePermitEU(Integer euId)
	 */
	public final PermitEU retrievePermitEU(Integer euId) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitEU", true);
		connHandler.setInteger(1, euId);
		PermitEU ret = (PermitEU) connHandler.retrieve(PermitEU.class);
		ret.setEuGroup(retrievePermitEUGroup(ret.getPermitEUGroupID()));

		return ret;
	}

	/**
	 * @see PermitDAO#createPermitEU(PermitEU eu)
	 */
	public final PermitEU createPermitEU(PermitEU eu) throws DAOException {

		checkNull(eu);
		checkNull(eu.getFpEU());

		if (eu.getEuFee() != null && eu.getEuFee().getFeeCategoryId() != null) {
			EUFee euFee = createPermitEUFee(eu.getEuFee());
			eu.setEUFeeID(euFee.getEUFeeId());
		}

		PermitEU ret = eu;
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitEU", false);

		String desc = eu.getDapcDescription();
		if (desc == null || desc.length() == 0) {
			eu.setDapcDescription(eu.getFpEU().getEuDesc());
		}
		String cId = eu.getCompanyId();
		if (cId == null || cId.length() == 0) {
			eu.setCompanyId(eu.getFpEU().getCompanyId());
		}

		int i = 1;
		int id = nextSequenceVal("S_Permit_Eu_Id");
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, eu.getPermitEUGroupID());
		connHandler.setInteger(i++, eu.getFacilityEUID());
		connHandler.setInteger(i++, eu.getCorrEpaEMUID());
		connHandler.setInteger(i++, eu.getBatID());
		connHandler.setInteger(i++, eu.getEUFeeID());
		connHandler.setString(i++, eu.getPermitStatusCd());
		connHandler.setString(i++, eu.getGeneralPermitTypeCd());
		connHandler.setString(i++, eu.getModelGeneralPermitCd());
		connHandler.setString(i++, eu.getDapcDescription());
		connHandler.setString(i++, eu.getCompanyId());
		connHandler.setInteger(i++, eu.getRevocationApplicationID());
		connHandler.setTimestamp(i++, eu.getRevocationDate());
		connHandler.setInteger(i++, eu.getExtensionApplicationID());
		connHandler.setTimestamp(i++, eu.getExtensionDate());
		connHandler.setTimestamp(i++, eu.getTerminatedDate());
		connHandler.setInteger(i++, eu.getSupersededPermitID());
		connHandler.setTimestamp(i++, eu.getSupersededDate());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		ret.setPermitEUID(id);
		ret.setLastModified(1);

		return ret;
	}

	/**
	 * @see PermitDAO#modifyPermitEU(PermitEU eu)
	 */
	public final void modifyPermitEU(PermitEU eu) throws DAOException {

		checkNull(eu);
		checkNull(eu.getFpEU());

		if (eu.getEuFee() != null && eu.getEuFee().getEUFeeId() != null) {
			modifyPermitEUFee(eu.getEuFee());
		} else if (eu.getEuFee() != null
				&& eu.getEuFee().getFeeCategoryId() != null
				&& eu.getEuFee().getEUFeeId() == null) {
			EUFee euFee = createPermitEUFee(eu.getEuFee());
			eu.setEUFeeID(euFee.getEUFeeId());
		}

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPermitEU", false);

		int i = 1;
		connHandler.setInteger(i++, eu.getPermitEUGroupID());
		connHandler.setInteger(i++, eu.getFacilityEUID());
		connHandler.setInteger(i++, eu.getCorrEpaEMUID());
		connHandler.setInteger(i++, eu.getBatID());
		connHandler.setInteger(i++, eu.getEUFeeID());
		connHandler.setString(i++, eu.getPermitStatusCd());
		connHandler.setString(i++, eu.getGeneralPermitTypeCd());
		connHandler.setString(i++, eu.getModelGeneralPermitCd());
		connHandler.setString(i++, eu.getDapcDescription());
		connHandler.setString(i++, eu.getCompanyId());
		connHandler.setInteger(i++, eu.getRevocationApplicationID());
		connHandler.setTimestamp(i++, eu.getRevocationDate());
		connHandler.setInteger(i++, eu.getExtensionApplicationID());
		connHandler.setTimestamp(i++, eu.getExtensionDate());
		connHandler.setTimestamp(i++, eu.getTerminatedDate());
		connHandler.setInteger(i++, eu.getSupersededPermitID());
		connHandler.setTimestamp(i++, eu.getSupersededDate());

		connHandler.setInteger(i++, eu.getLastModified() + 1);

		connHandler.setInteger(i++, eu.getPermitEUID());
		connHandler.setInteger(i++, eu.getLastModified());

		connHandler.update();

		return;
	}

	/**
	 * @see PermitDAO#removePermitEU(Integer euId)
	 */
	public final void removePermitEU(Integer euId) throws DAOException {

		checkNull(euId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitEU", false);

		connHandler.setInteger(1, euId);
		connHandler.remove();

	}

	public final PermitEU[] searchPermitEUsAcrossPermits(Integer corrEpaEMUID,
			boolean equals, String permitTypeCD) throws DAOException {

		checkNull(corrEpaEMUID);

		StringBuffer projSql = new StringBuffer(
				loadSQL("PermitSQL.searchPermitEUsAcrossPermits"));

		if (permitTypeCD != null && permitTypeCD.length() > 0) {
			String stringEquals;
			if (equals) {
				stringEquals = "=";
			} else {
				stringEquals = "<>";
			}
			if (permitTypeCD != null) {
				projSql.append("AND pp.permit_type_cd ");
				projSql.append(stringEquals);
				projSql.append(" '");
				projSql.append(permitTypeCD);
				projSql.append("'");
			}
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(projSql.toString());

		connHandler.setInteger(1, corrEpaEMUID);

		ArrayList<PermitEU> ret = connHandler.retrieveArray(PermitEU.class);

		return ret.toArray(new PermitEU[0]);

	}

	/**
	 * @see PermitDAO#retrievePermitDocuments(Integer permitId)
	 */
	public final PermitDocument[] retrievePermitDocuments(Integer permitId)
			throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitDocuments", true);
		connHandler.setInteger(1, permitId);
		ArrayList<PermitDocument> ret = connHandler
				.retrieveArray(PermitDocument.class);

		return ret.toArray(new PermitDocument[0]);
	}

	/**
	 * @see PermitDAO#createPermitDocument(PermitDocument permitDoc)
	 */
	public final PermitDocument createPermitDocument(PermitDocument permitDoc)
			throws DAOException {

		checkNull(permitDoc);
		checkNull(permitDoc.getPermitId());
		checkNull(permitDoc.getDocumentID());
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitDocument", false);

		connHandler.setInteger(1, permitDoc.getPermitId());
		connHandler.setInteger(2, permitDoc.getDocumentID());
		connHandler.setString(3, permitDoc.getPermitDocTypeCD());
		connHandler.setString(4, permitDoc.getIssuanceStageFlag());
		connHandler.setInteger(5, permitDoc.getLastModified());
		connHandler.setBoolean(6, permitDoc.isRequiredDoc());
		// LastModified has to be same as dc_document

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		permitDoc.setLastModified(1);

		return permitDoc;
	}

	/**
	 * @see PermitDAO#modifyPermitDocument(PermitDocument permitDoc)
	 */
	public final boolean modifyPermitDocument(PermitDocument permitDoc)
			throws DAOException {

		checkNull(permitDoc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPermitDocument", false);

		connHandler.setString(1, permitDoc.getPermitDocTypeCD());
		connHandler.setString(2, permitDoc.getIssuanceStageFlag());
		connHandler.setInteger(3, permitDoc.getLastModified() + 1);
		connHandler.setInteger(4, permitDoc.getDocumentID());
		connHandler.setInteger(5, permitDoc.getLastModified());

		return connHandler.update();
	}

	/**
	 * @see PermitDAO#removePermitDocument(PermitDocument permitDoc)
	 */
	public final void removePermitDocument(PermitDocument permitDoc)
			throws DAOException {

		checkNull(permitDoc);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitDocument", false);
		connHandler.setInteger(1, permitDoc.getDocumentID());

		connHandler.remove();
	}

	/**
	 * @see PermitDAO#removePermitDocuments(int permitId)
	 */
	public final void removePermitDocuments(Integer permitId)
			throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitDocuments", false);
		connHandler.setInteger(1, permitId);

		connHandler.remove();
	}

	/**
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	private List<String> retrievePermitReasonCDs(Integer permitId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitReasonCDs", true);

		connHandler.setInteger(1, permitId);
		ArrayList<String> ret = connHandler
				.retrieveJavaObjectArray(String.class);

		return ret;
	}

	/**
	 * @see PermitDAO#generatePermitSeqNo()
	 */
	public final int generatePermitSeqNo() throws DAOException {
		Integer seqNo = nextSequenceVal("PT_Permit_Nbr");
		return seqNo.intValue();
	}

	/**
	 * @see PermitDAO#retrieveAllPermits()
	 */
	public final Permit[] retrieveAllPermits() throws DAOException {
		StringBuffer statementSQL = new StringBuffer(
				loadSQL("PermitSQL.retrievePermit"));

		return retrieveAllPermits(statementSQL);
	}

	/**
	 * @param statementSQL
	 * @return
	 * @throws DAOException
	 * 
	 */
	private Permit[] retrieveAllPermits(StringBuffer statementSQL)
			throws DAOException {
		ArrayList<BaseDBObject> ret = new ArrayList<BaseDBObject>();

		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();

			pStmt = conn.prepareStatement(statementSQL.toString());

			ResultSet rs = pStmt.executeQuery();

			while (rs.next()) {
				Permit p = initPermit(rs);
				// p = retrieveCompletePermit(ret);
				ret.add(p);
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret.toArray(new Permit[0]);
	}

	/**
	 * @see PermitDAO#retrievePermits(Integer permitIds[])
	 */
	public final Permit[] retrievePermits(Integer permitIds[])
			throws DAOException {
		if (permitIds.length > 0) {
			StringBuffer statementSQL = new StringBuffer(
					loadSQL("PermitSQL.retrievePermit"));
			StringBuffer whereClause = new StringBuffer("WHERE permit_id IN ("
					+ permitIds[0]);

			for (int i = 1; i < permitIds.length; i++) {
				whereClause.append("," + permitIds[i]);
			}

			statementSQL.append(whereClause.toString());

			return retrieveAllPermits(statementSQL);
		}

		return new Permit[0];
	}

	@Override
	public Integer[] retrievePermitApplicationIds(int permitId)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitApplicationIds", true);

		connHandler.setInteger(1, permitId);

		ArrayList<Integer> ret = connHandler
				.retrieveJavaObjectArray(Integer.class);

		return ret.toArray(new Integer[0]);
	}

	/**
	 * @see PermitDAO#createPermitApplication(int permitId, int applicationId)
	 */
	public final void createPermitApplication(int permitId, int applicationId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitApplication", false);

		checkNull(permitId);
		checkNull(applicationId);
		connHandler.setInteger(1, permitId);
		connHandler.setInteger(2, applicationId);

		connHandler.update();
	}

	/**
	 * @see PermitDAO#addPermitApplications(int permitId, List<Application>
	 *      applications)
	 */
	public final List<Application> addPermitApplications(int permitId,
			List<Application> applications) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitApplication", false);

		checkNull(permitId);

		connHandler.setInteger(1, permitId);

		try {
			for (Application application : applications) {
				connHandler.setInteger(2, application.getApplicationID());
				connHandler.updateNoClose();
			}
		} finally {
			connHandler.close();
		}

		return applications;
	}

	/**
	 * @see PermitDAO#addPermitDocuments(int permitId, List<PermitDocument>
	 *      documents)
	 */
	public final List<PermitDocument> addPermitDocuments(int permitId,
			List<PermitDocument> documents) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitDocument", false);

		checkNull(permitId);
		connHandler.setInteger(1, permitId);

		try {
			for (PermitDocument document : documents) {
				connHandler.setInteger(2, document.getDocumentID());
				connHandler.setString(3, document.getPermitDocTypeCD());
				connHandler.setString(4, document.getIssuanceStageFlag());
				connHandler.setInteger(5, 1);
				connHandler.updateNoClose();
			}
		} finally {
			connHandler.close();
		}

		return documents;
	}

	/**
	 * @see PermitDAO#removePermitApplications(int permitId)
	 */
	public final void removePermitApplications(int permitId)
			throws DAOException {
		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitApplications", false);

		connHandler.setInteger(1, permitId);

		connHandler.remove();
	}

	/**
	 * @see PermitDAO#removePTIOReasonCds(int permitId)
	 */
	public final void markTempPermitDocuments(int permitId) throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.markTempPermitBaseDocuments", false);
		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}

	/**
	 * @see PermitDAO#unMarkTempPermitDocuments(int permitId)
	 */
	public final void unMarkTempPermitDocuments(Integer documentID)
			throws DAOException {
		checkNull(documentID);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.unMarkTempPermitBaseDocuments", false);
		connHandler.setInteger(1, documentID);
		connHandler.remove();
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrievePermitReasons(java.lang.String)
	 */
	public final SimpleDef[] retrievePermitReasons(String permitType)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitReasons", true);
		connHandler.setString(1, permitType);
		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrieveAllPermitReasons()
	 */
	public final SimpleDef[] retrieveAllPermitReasons() throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveAllPermitReasons", true);
		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrieveAllPermitTypes()
	 */
	public final SimpleDef[] retrieveAllPermitTypes() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveAllPermitTypes", true);
		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);

		return ret.toArray(new SimpleDef[0]);
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#createPermitIssuance(us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance)
	 */
	public final PermitIssuance createPermitIssuance(PermitIssuance pi)
			throws DAOException {

		checkNull(pi);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitIssuance", false);
		int id = nextSequenceVal("S_Permit_Issuance_Id");
		pi.setIssuanceId(id);

		int i = 1;
		connHandler.setInteger(i++, id);
		connHandler.setString(i++, pi.getIssuanceTypeCd());
		connHandler.setInteger(i++, pi.getPermitId());
		connHandler.setString(i++, pi.getIssuanceStatusCd());
		connHandler.setTimestamp(i++, pi.getIssuanceDate());
		connHandler.setTimestamp(i++, pi.getPublicNoticeRequestDate());
		connHandler.setTimestamp(i++, pi.getPublicNoticePublishDate());
		connHandler.setTimestamp(i++, pi.getPublicCommentEndDate());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(pi
				.isHearingRequested()));
		connHandler.setTimestamp(i++, pi.getHearingRequestedDate());
		connHandler.setTimestamp(i++, pi.getHearingNoticeRequestDate());
		connHandler.setTimestamp(i++, pi.getHearingNoticePublishDate());
		connHandler.setTimestamp(i++, pi.getHearingDate());
		connHandler.setTimestamp(i++, pi.getNewspaperAffidavitDate());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		pi.setLastModified(1);

		return pi;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#addPermitReasons(int,
	 *      java.util.List)
	 */
	public final List<String> addPermitReasons(int permitId,
			List<String> permitReasonCDs) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitReason", false);

		checkNull(permitId);
		connHandler.setInteger(1, permitId);

		try {
			for (String reasonCd : permitReasonCDs) {
				connHandler.setString(2, reasonCd);
				connHandler.updateNoClose();
			}
		} finally {
			connHandler.close();
		}

		return permitReasonCDs;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#removePermitReasons(int)
	 */
	public final void removePermitReasons(int permitId) throws DAOException {
		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitReasons", false);

		connHandler.setInteger(1, permitId);

		connHandler.remove();
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrievePermitIssuances(int)
	 */
	public final LinkedHashMap<String, PermitIssuance> retrievePermitIssuances(
			int permitID) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitIssuancesByPermitID", true);
		connHandler.setInteger(1, permitID);

		ArrayList<PermitIssuance> ret = connHandler
				.retrieveArray(PermitIssuance.class);
		PermitIssuance[] pis = ret.toArray(new PermitIssuance[0]);
		LinkedHashMap<String, PermitIssuance> rpis = new LinkedHashMap<String, PermitIssuance>();
		for (PermitIssuance pi : pis) {
			rpis.put(pi.getIssuanceTypeCd(), pi);
		}
		return rpis;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrieveLatestPermitIssuanceFinal(int)
	 */
	public final List<PermitIssuance> retrieveLatestPermitIssuanceFinal(
			int permitID) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveLatestPermitIssuanceFinal", true);
		connHandler.setInteger(1, permitID);

		ArrayList<PermitIssuance> ret = connHandler
				.retrieveArray(PermitIssuance.class);

		return ret;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#modifyPermitIssuance(us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance)
	 */
	public final boolean modifyPermitIssuance(PermitIssuance permitIssuance)
			throws DAOException {

		checkNull(permitIssuance);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPermitIssuance", false);

		PermitIssuance pi = permitIssuance;

		int i = 1;
		connHandler.setString(i++, pi.getIssuanceTypeCd());
		connHandler.setInteger(i++, pi.getPermitId());
		connHandler.setString(i++, pi.getIssuanceStatusCd());
		connHandler.setTimestamp(i++, pi.getIssuanceDate());
		connHandler.setTimestamp(i++, pi.getPublicNoticeRequestDate());
		connHandler.setTimestamp(i++, pi.getPublicNoticePublishDate());
		connHandler.setTimestamp(i++, pi.getPublicCommentEndDate());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(pi
				.isHearingRequested()));
		connHandler.setTimestamp(i++, pi.getHearingRequestedDate());
		connHandler.setTimestamp(i++, pi.getHearingNoticeRequestDate());
		connHandler.setTimestamp(i++, pi.getHearingNoticePublishDate());
		connHandler.setTimestamp(i++, pi.getHearingDate());
		connHandler.setTimestamp(i++, pi.getNewspaperAffidavitDate());
		connHandler.setInteger(i++, pi.getLastModified() + 1);

		connHandler.setInteger(i++, pi.getIssuanceId());
		connHandler.setInteger(i++, pi.getLastModified());

		return connHandler.update();
	}

	/**
	 * Retrieves all active permits for a given EU.
	 * 
	 * @param euId
	 * @return Permit[]
	 * @throws DAOException
	 */
	public final ArrayList<Permit> searchEuPermits(Integer euId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.searchEuPermits", false);
		connHandler.setInteger(1, euId);
		ArrayList<Permit> ret = connHandler.retrieveArray(Permit.class);

		return ret;
	}

	/**
	 * Retrieves all permits for a given EU.
	 * 
	 * @param euId
	 * @return Permit[]
	 * @throws DAOException
	 */
	public final ArrayList<Permit> searchAllEuPermits(Integer euId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.searchAllEuPermits", false);
		connHandler.setInteger(1, euId);
		ArrayList<Permit> ret = connHandler.retrieveArray(Permit.class);

		ArrayList<Permit> allEuPermits = new ArrayList<Permit>();
		Permit euPermit;

		for (Permit permit : ret.toArray(new Permit[0])) {
			euPermit = retrievePermit(permit.getPermitID());
			allEuPermits.add(euPermit);
		}

		return allEuPermits;
	}

	/**
	 * @param facilityId
	 * @return Permit[]
	 * @throws DAOException
	 */
	public final ArrayList<Permit> searchActivePermitsForFacility(
			String facilityId) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.searchActivePermitsForFacility", false);
		connHandler.setString(1, facilityId);
		ArrayList<Permit> ret = connHandler.retrieveArray(Permit.class);

		return ret;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#createPermitNote(java.lang.Integer,
	 *      java.lang.Integer)
	 */
	public final void createPermitNote(Integer permitId, Integer noteId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitNote", false);

		checkNull(permitId);
		checkNull(noteId);
		connHandler.setInteger(1, permitId);
		connHandler.setInteger(2, noteId);

		connHandler.update();
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrieveDapcComments(int)
	 */
	public final Note[] retrieveDapcComments(int permitID) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveDapcComments", true);
		connHandler.setInteger(1, permitID);
		ArrayList<PermitNote> ret = connHandler.retrieveArray(PermitNote.class);

		return ret.toArray(new Note[0]);
	}

	/**
	 * @throws DAOException
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrievePermitContacts(int)
	 */
	public final ArrayList<Contact> retrievePermitContacts(int permitID)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitContacts", true);

		connHandler.setInteger(1, permitID);
		ArrayList<Contact> ret = connHandler.retrieveArray(Contact.class);

		return ret;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#removePermitContacts(int)
	 */
	public final void removePermitContacts(int permitId) throws DAOException {
		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitContacts", false);

		connHandler.setInteger(1, permitId);

		connHandler.remove();
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#createPermitContact(int,
	 *      java.lang.Integer)
	 */
	public final void createPermitContact(int permitId, Integer contactId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitContact", false);

		checkNull(permitId);
		checkNull(contactId);
		connHandler.setInteger(1, permitId);
		connHandler.setInteger(2, contactId);

		connHandler.update();
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#removePermitContact(int,
	 *      java.lang.Integer)
	 */
	public final void removePermitContact(int permitId, Integer contactId)
			throws DAOException {
		checkNull(permitId);
		checkNull(contactId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitContact", false);

		connHandler.setInteger(1, permitId);
		connHandler.setInteger(2, contactId);

		connHandler.remove();
	}

	/**
     * 
     */
	public final Permit retrieveSubpartCDs(Permit permit) throws DAOException {
		checkNull(permit);
		checkNull(permit.getPermitID());

		ConnectionHandler connHandler = null;

		if (permit.isMact()) {
			connHandler = new ConnectionHandler(
					"PermitSQL.retrieveMactSubpartCD", true);
			connHandler.setInteger(1, permit.getPermitID());
			ArrayList<String> ret = connHandler
					.retrieveJavaObjectArray(String.class);
			permit.setMactSubpartCDs(ret);
		}
		if (permit.isNeshaps()) {
			connHandler = new ConnectionHandler(
					"PermitSQL.retrieveNeshapsSubpartCD", true);
			connHandler.setInteger(1, permit.getPermitID());
			ArrayList<String> ret = connHandler
					.retrieveJavaObjectArray(String.class);
			permit.setNeshapsSubpartCDs(ret);
		}
		if (permit.isNsps()) {
			connHandler = new ConnectionHandler(
					"PermitSQL.retrieveNspsSubpartCD", true);
			connHandler.setInteger(1, permit.getPermitID());
			ArrayList<String> ret = connHandler
					.retrieveJavaObjectArray(String.class);
			permit.setNspsSubpartCDs(ret);
		}
		
		if (permit.isPart61NESHAP()) {
			connHandler = new ConnectionHandler(
					"PermitSQL.retrieveNeshapsSubpartCD", true);
			connHandler.setInteger(1, permit.getPermitID());
			ArrayList<String> ret = connHandler
					.retrieveJavaObjectArray(String.class);
			permit.setPart61NESHAPSubpartCDs(ret);
		}
		
		if (permit.isPart63NESHAP()) {
			connHandler = new ConnectionHandler(
					"PermitSQL.retrieveMactSubpartCD", true);
			connHandler.setInteger(1, permit.getPermitID());
			ArrayList<String> ret = connHandler
					.retrieveJavaObjectArray(String.class);
			permit.setPart63NESHAPSubpartCDs(ret);
		}

		return permit;
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#createSubpartCDs(Permit)
	 */
	public final void createSubpartCDs(Permit permit) throws DAOException {

		checkNull(permit);
		checkNull(permit.getPermitID());

		if (permit.isMact()) {
			for (String code : permit.getMactSubpartCDs()) {
				ConnectionHandler connHandler = new ConnectionHandler(
						"PermitSQL.createMactSubpartCD", false);
				connHandler.setInteger(1, permit.getPermitID());
				connHandler.setString(2, code);
				connHandler.update();
			}
		}

		if (permit.isNeshaps()) {
			for (String code : permit.getNeshapsSubpartCDs()) {
				ConnectionHandler connHandler = new ConnectionHandler(
						"PermitSQL.createNeshapsSubpartCD", false);
				connHandler.setInteger(1, permit.getPermitID());
				connHandler.setString(2, code);
				connHandler.update();
			}
		}

		if (permit.isNsps()) {
			for (String code : permit.getNspsSubpartCDs()) {
				ConnectionHandler connHandler = new ConnectionHandler(
						"PermitSQL.createNspsSubpartCD", false);
				connHandler.setInteger(1, permit.getPermitID());
				connHandler.setString(2, code);
				connHandler.update();
			}
		}
		
		if (permit.isPart63NESHAP()) {
			for (String code : permit.getPart63NESHAPSubpartCDs()) {
				ConnectionHandler connHandler = new ConnectionHandler(
						"PermitSQL.createMactSubpartCD", false);
				connHandler.setInteger(1, permit.getPermitID());
				connHandler.setString(2, code);
				connHandler.update();
			}
		}

		if (permit.isPart61NESHAP()) {
			for (String code : permit.getPart61NESHAPSubpartCDs()) {
				ConnectionHandler connHandler = new ConnectionHandler(
						"PermitSQL.createNeshapsSubpartCD", false);
				connHandler.setInteger(1, permit.getPermitID());
				connHandler.setString(2, code);
				connHandler.update();
			}
		}

		
	}

	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#removeSubpartCDs(Permit)
	 */
	public final void removeSubpartCDs(Permit permit) throws DAOException {

		checkNull(permit);
		removeSubpartCDs(permit.getPermitID());
	}

	/**
	 * @see PermitDAO#modifyPermitEUFee(EUFee euFee)
	 */
	public final void modifyPermitEUFee(EUFee euFee) throws DAOException {
		checkNull(euFee);
		checkNull(euFee.getFee());
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPermitEUFee", false);

		int i = 1;
		connHandler.setInteger(i++, euFee.getFeeCategoryId());
		connHandler.setString(i++, euFee.getAdjustmentCd());
		connHandler.setDouble(i++, euFee.getAdjustedAmount());
		connHandler.setInteger(i++, euFee.getFee().getFeeId());
		connHandler.setInteger(i++, euFee.getLastModified() + 1);

		connHandler.setInteger(i++, euFee.getEUFeeId());
		connHandler.setInteger(i++, euFee.getLastModified());

		connHandler.update();

		return;
	}

	public final EUFee createPermitEUFee(EUFee fee) throws DAOException {

		checkNull(fee);
		checkNull(fee.getFee());

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitEUFee", false);

		int i = 1;
		fee.setEUFeeId(nextSequenceVal("S_Permit_Eu_Fee_Id"));
		connHandler.setInteger(i++, fee.getEUFeeId());
		connHandler.setInteger(i++, fee.getFeeCategoryId());
		connHandler.setString(i++, fee.getAdjustmentCd());
		connHandler.setInteger(i++, fee.getFee().getFeeId());
		connHandler.setDouble(i++, fee.getAdjustedAmount());

		connHandler.update();

		// If we get here the INSERT must have succeeded, so set the important
		// data and return the object.
		fee.setLastModified(1);

		return fee;

	}

	public final void removePermitEUFee(int euFeeId) throws DAOException {
		checkNull(euFeeId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitEUFee", false);
		connHandler.setInteger(1, euFeeId);
		connHandler.remove();
	}

	public final SimpleDef[] retrieveModelGeneralPermitDefs(
			String generalPermitTypeCd) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveModelGeneralPermitDefs", true);
		connHandler.setString(1, generalPermitTypeCd);
		ArrayList<SimpleDef> ret = connHandler.retrieveArray(SimpleDef.class);
		return ret.toArray(new SimpleDef[0]);
	}

	public final ArrayList<Integer> retrieveExpiredTVPermits(Timestamp endDate) throws DAOException {

		Connection conn = null;
		PreparedStatement pStmt = null;
		ArrayList<Integer> ret = new ArrayList<Integer>();
		try {
			conn = getReadOnlyConnection();
			pStmt = conn.prepareStatement(loadSQL("PermitSQL.retrieveExpiredTVPermits"));
			pStmt.setTimestamp(1, endDate);
			
			ResultSet rs = pStmt.executeQuery();
			
			while(rs.next()) {
				ret.add(rs.getInt("permit_id"));
			}
		}catch(Exception e) {
			handleException(e, conn);
		}
		finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	public final SimpleIdDef[] retrieveSupersedablePermits(Integer corrEpaEmuId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveSupersedablePermits", true);
		connHandler.setInteger(1, corrEpaEmuId);
		ArrayList<SimpleIdDef> ret = connHandler
				.retrieveArray(SimpleIdDef.class);
		return ret.toArray(new SimpleIdDef[0]);

	}

	public final SimpleIdDef[] retrieveSupersedableTVPermits(String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveTVSupersedablePermits", true);
		connHandler.setString(1, facilityId);
		ArrayList<SimpleIdDef> ret = connHandler
				.retrieveArray(SimpleIdDef.class);
		return ret.toArray(new SimpleIdDef[0]);

	}

	public ExpiredPermit[] retrieveExpiredPTIOPermits(String roleCd,
			Timestamp expirationDate) throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveExpiredPTIOPermits", true);

		connHandler.setString(1, roleCd);
		connHandler.setTimestamp(2, expirationDate);

		ArrayList<ExpiredPermit> ret = connHandler
				.retrieveArray(ExpiredPermit.class);

		return ret.toArray(new ExpiredPermit[0]);
	}

	public final void updatePermitEUsStatus(int permitId, String newStatusCd)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.updatePermitEUsStatus", false);
		int i = 1;
		connHandler.setString(i++, newStatusCd);
		connHandler.setInteger(i++, permitId);

		connHandler.update();
	}

	public final List<PermitCC> retrievePermitCCList(int permitId)
			throws DAOException {

		checkNull(permitId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitCCList", true);

		connHandler.setInteger(1, permitId);

		ArrayList<PermitCC> ret = new ArrayList<PermitCC>();
		ArrayList<PermitCC> base = connHandler.retrieveArray(PermitCC.class);
		for (BaseDBObject bd : base) {
			ret.add((PermitCC) bd);
		}

		return ret;
	}

	public final void removePermitCCList(int permitId) throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitCCList", false);

		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}

	public final PermitCC createPermitCC(PermitCC pcc) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitCC", false);

		checkNull(pcc);
		int i = 1;
		InfrastructureBO iBO = new InfrastructureBO();
		int id = nextSequenceVal("S_Permit_CC_Id");
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, pcc.getPermitId());
		connHandler.setString(i++, pcc.getName());
		connHandler.setString(i++, pcc.getAddress1());
		connHandler.setString(i++, pcc.getAddress2());
		connHandler.setString(i++, pcc.getCity());
		connHandler.setString(i++, pcc.getState());
		connHandler.setString(i++, pcc.getZipCode());

		connHandler.update();

		pcc.setPermitCCId(id);
		pcc.setLastModified(1);

		return pcc;
	}

	public final void modifyPermitCC(PermitCC pcc) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPermitCC", false);

		checkNull(pcc);
		int i = 1;
		connHandler.setInteger(i++, pcc.getPermitCCId());
		connHandler.setInteger(i++, pcc.getPermitId());
		connHandler.setString(i++, pcc.getName());
		connHandler.setString(i++, pcc.getAddress1());
		connHandler.setString(i++, pcc.getAddress2());
		connHandler.setString(i++, pcc.getCity());
		connHandler.setString(i++, pcc.getState());
		connHandler.setString(i++, pcc.getZipCode());
		connHandler.setInteger(i++, pcc.getLastModified() + 1);

		connHandler.setInteger(i++, pcc.getPermitCCId());
		connHandler.setInteger(i++, pcc.getPermitId());
		connHandler.setInteger(i++, pcc.getLastModified());

		connHandler.update();
	}

	public final void removePermitCC(PermitCC pcc) throws DAOException {

		checkNull(pcc);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitCC", false);

		int i = 1;
		connHandler.setInteger(i++, pcc.getPermitCCId());
		connHandler.setInteger(i++, pcc.getPermitId());
		connHandler.setInteger(i++, pcc.getLastModified());

		connHandler.remove();
	}
	
	public final List<PermitChargePayment> retrievePermitChargePaymentList(
			int permitId) throws DAOException {

		checkNull(permitId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitChargePaymentList", true);

		connHandler.setInteger(1, permitId);

		ArrayList<PermitChargePayment> ret = new ArrayList<PermitChargePayment>();
		ArrayList<PermitChargePayment> base = connHandler
				.retrieveArray(PermitChargePayment.class);
		for (BaseDBObject bd : base) {
			ret.add((PermitChargePayment) bd);
		}

		return ret;
	}

	public final BigDecimal retrievePermitChargePaymentTotal(
			int permitId) throws DAOException {

		checkNull(permitId);
		
		ConnectionHandler connHandler = new ConnectionHandler(true);

		connHandler.setSQLString("PermitSQL.retrievePermitChargePaymentTotal");
		connHandler.setInteger(1, permitId);
		connHandler.setInteger(2, permitId);
		Double amount = (Double) connHandler.retrieveJavaObject(Double.class);
		
		if (amount == null) {
			amount = 0d;
		}
		
		BigDecimal bd = BigDecimal.valueOf(amount);

		return bd;
	}

	public final void removePermitChargePaymentList(int permitId)
			throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitChargePaymentList", false);

		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}

	public final PermitChargePayment createPermitChargePayment(
			PermitChargePayment pcp) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createPermitChargePayment", false);

		checkNull(pcp);
		int i = 1;
		InfrastructureBO iBO = new InfrastructureBO();
		int id = nextSequenceVal("S_Permit_CP_Id");
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, pcp.getPermitId());
		connHandler.setTimestamp(i++, pcp.getTransactionDate());
		connHandler.setString(i++, pcp.getTransactionType());
		connHandler.setString(i++, pcp.getNoteTxt());
		connHandler.setString(i++, pcp.getTransmittalNumber());
		connHandler.setString(i++, pcp.getCheckNumber());
		connHandler.setDouble(i++, pcp.getTransactionAmount());

		connHandler.update();

		pcp.setPermitCPId(id);
		pcp.setLastModified(1);

		return pcp;
	}

	public final void modifyPermitChargePayment(PermitChargePayment pcp)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPermitChargePayment", false);

		checkNull(pcp);
		int i = 1;
		connHandler.setInteger(i++, pcp.getPermitCPId());
		connHandler.setInteger(i++, pcp.getPermitId());

		connHandler.setTimestamp(i++, pcp.getTransactionDate());
		connHandler.setString(i++, pcp.getTransactionType());
		connHandler.setString(i++, pcp.getNoteTxt());
		connHandler.setString(i++, pcp.getTransmittalNumber());
		connHandler.setString(i++, pcp.getCheckNumber());
		connHandler.setDouble(i++, pcp.getTransactionAmount());

		connHandler.setInteger(i++, pcp.getLastModified() + 1);

		connHandler.setInteger(i++, pcp.getPermitCPId());
		connHandler.setInteger(i++, pcp.getPermitId());
		connHandler.setInteger(i++, pcp.getLastModified());

		connHandler.update();
	}

	public final void removePermitChargePayment(PermitChargePayment pcc)
			throws DAOException {

		checkNull(pcc);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitChargePayment", false);

		int i = 1;
		connHandler.setInteger(i++, pcc.getPermitCPId());
		connHandler.setInteger(i++, pcc.getPermitId());
		connHandler.setInteger(i++, pcc.getLastModified());

		connHandler.remove();
	}

	public final ArrayList<String> retrieveFacilityNameForEU(PermitEU eu)
			throws DAOException {

		checkNull(eu);

		final String sql = "SELECT f.facility_id, f.facility_nm " + "FROM "
				+ addSchemaToTable("fp_facility f ")
				+ "WHERE f.fp_id = (SELECT eu.fp_id "
				+ "                   FROM "
				+ addSchemaToTable("fp_emissions_unit eu ")
				+ "                   WHERE eu.emu_id = ?)";

		logger.debug(sql);

		Connection conn = null;
		PreparedStatement pStmt = null;
		ArrayList<String> ret = new ArrayList<String>();

		try {
			conn = getReadOnlyConnection();
			pStmt = conn.prepareStatement(sql.toString());
			pStmt.setInt(1, eu.getFacilityEUID());

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				ret.add(0, rs.getString("facility_id"));
				ret.add(1, rs.getString("facility_nm"));
			}

		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}

		return ret;
	}

	public SimpleIdDef[] retrieveRPRPermitList(String facilityId)
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveRPRPermitList", true);
		connHandler.setString(1, facilityId);
		ArrayList<SimpleIdDef> ret = connHandler
				.retrieveArray(SimpleIdDef.class);
		return ret.toArray(new SimpleIdDef[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#
	 * retrieveGoodMigratedPermitDocuments(java.lang.String)
	 */
	public final PermitDocument[] retrieveGoodMigratedPermitDocuments(
			String permitTypeCd) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveGoodMigratedPermitDocuments", true);
		connHandler.setString(1, permitTypeCd);

		ArrayList<PermitDocument> ret = connHandler
				.retrieveArray(PermitDocument.class);

		return ret.toArray(new PermitDocument[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#
	 * retrieveBadMigratedPermitDocuments(java.lang.String)
	 */
	public final PermitDocument[] retrieveBadMigratedPermitDocuments(
			String permitTypeCd) throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveBadMigratedPermitDocuments", true);
		connHandler.setString(1, permitTypeCd);

		ArrayList<PermitDocument> ret = connHandler
				.retrieveArray(PermitDocument.class);

		return ret.toArray(new PermitDocument[0]);
	}

	public PermitActivitySearch[] searchPermitActivity(PermitActivitySearch pa)
			throws DAOException {
		StringBuffer query = new StringBuffer(
				loadSQL("PermitSQL.searchPermitActivity"));

		if (pa.getNaicsCd() != null) {
			query = new StringBuffer(
					loadSQL("PermitSQL.searchPermitActivityNaics"));
		}

		if (pa.isUnlimitedResults()) {
			setDefaultSearchLimit(-1);
		}

		ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(replaceSchema(buildActivityListQuery(query,
				pa, "pa.DUE_DT")));
		setActivityListQueryValue(connHandler, pa);

		ArrayList<PermitActivitySearch> ret = connHandler.retrieveArray(
				PermitActivitySearch.class, defaultSearchLimit);

		// if naics is specified as a parameter, remove all records that don't
		// have the specified naics code. pa.getNaics code will be a
		// comma-separated
		// list of naics codes for the facility
		if (pa.getNaicsCd() != null) {
			Iterator<PermitActivitySearch> iter = ret.iterator();
			while (iter.hasNext()) {
				PermitActivitySearch item = iter.next();
				boolean match = false;
				if (item.getNaicsCd() != null) {
					String[] codes = item.getNaicsCd().split(",");
					for (String code : codes) {
						if (pa.getNaicsCd().equals(code.trim())) {
							match = true;
							break;
						}
					}
				}
				if (!match) {
					iter.remove();
				}
			}
		}
		return ret.toArray(new PermitActivitySearch[0]);
	}

	private void setActivityListQueryValue(ConnectionHandler connHandler,
			PermitActivitySearch pa) throws DAOException {
		int i = 1;
		String dateBy = pa.getDateBy();
		Timestamp endDate = pa.getEndDt();
		Timestamp beginDate = pa.getBeginDt();
		if (dateBy != null && dateBy.trim().length() != 0 && beginDate != null) {

			connHandler.setTimestamp(i, formatBeginOfDay(beginDate));
			i++;
		}
		if (dateBy != null && dateBy.trim().length() != 0 && endDate != null) {

			connHandler.setTimestamp(i, formatEndOfDay(endDate));
			i++;
		}
	}

	private String buildActivityListQuery(StringBuffer query,
			PermitActivitySearch pa, String orderByString) {
		String facilityId = pa.getFacilityId();
		if (!Utility.isNullOrEmpty(facilityId)) {
			String format = "F%06d";
			facilityId = facilityId.trim();
			int tempId;
			try {
				tempId = Integer.parseInt(facilityId);
				facilityId = String.format(format, tempId);
			} catch (NumberFormatException nfe) {
			}
		}
			
		boolean orderBy = false;
		if (pa.getFacilityId() != null && facilityId.trim().length() > 0) {
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
		String naicsCd = pa.getNaicsCd();
		if (naicsCd != null && naicsCd.trim().length() > 0) {
			query.append(" AND fnxref.naics_cd = '");
			query.append(naicsCd);
			query.append("'");
			orderBy = true;
		}
		String applicationNumber = pa.getApplicationNumber();
		if (applicationNumber != null && applicationNumber.length() != 0) {
			applicationNumber = applicationNumber.trim();
			if (applicationNumber.contains("*")) {
				applicationNumber = applicationNumber.replace('*', '%');
			}
			query.append(" AND LOWER(%Schema%f_all_apps(p.permit_id)) ");
			query.append(" LIKE LOWER('%" + applicationNumber + "%') ");
		}
		String permitNumber = pa.getPermitNumber();
		if (permitNumber != null && permitNumber.length() != 0) {
			permitNumber = permitNumber.trim();
			if (permitNumber.contains("*")) {
				permitNumber = permitNumber.replace('*', '%');
			}
			if (permitNumber.contains("%")) {
				query.append(" AND LOWER(p.permit_nbr) like LOWER('"
						+ permitNumber + "')");
			} else {
				query.append(" AND LOWER(p.permit_nbr) = LOWER('"
						+ permitNumber + "')");
			}
		}
		String permitType = pa.getPermitTypeCd();
		if (permitType != null && permitType.length() != 0) {
			query.append(" AND p.PERMIT_TYPE_CD = '");
			query.append(permitType);
			query.append("'");
		}

		String permitReason = pa.getPermitReason();
		if (permitReason != null && permitReason.length() != 0) {
			query.append(" AND %Schema%f_all_reasons(p.permit_id) LIKE '%");
			query.append(permitReason);
			query.append("%'");
		}
		String permitStatusCd = pa.getPermitGlobalStatusCd();
		if (permitStatusCd != null && permitStatusCd.length() != 0) {
			query.append(" AND p.PERMIT_GLOBAL_STATUS_CD = '");
			query.append(permitStatusCd);
			query.append("'");
		}
		String dateBy = pa.getDateBy();
		Timestamp endDate = pa.getEndDt();
		Timestamp beginDate = pa.getBeginDt();
		if (dateBy != null && dateBy.trim().length() != 0
				&& (beginDate != null || endDate != null)) {
			query.append(" AND (");
			if (beginDate != null) {
				query.append(dateBy);
				query.append(" >= ? ");
				if (endDate != null) {
					query.append(" AND ");
				}
			}
			if (endDate != null) {
				query.append(dateBy);
				query.append(" <= ? ");
			}
			query.append(" ) ");
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
						+ "(ATD.activity_template_nm='Technically Complete/T&C ready to review' or"
						+ " ATD.activity_template_nm='DO/LAA Permit Approval' or"
						+ " ATD.activity_template_nm='CO Permit Review' or"
						+ " ATD.activity_template_nm='CO Permit Approval') ");
			} else {
				query.append(" AND ATD.ACTIVITY_TEMPLATE_NM = '");
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
		String processTemplateNm = pa.getProcessTemplateNm();
		if (processTemplateNm != null) {
			query.append(" AND LOWER(PT.PROCESS_TEMPLATE_NM) LIKE ");
			query.append("LOWER('");
			query.append(SQLizeString(processTemplateNm.replace("*", "%")));
			query.append("')");
			orderBy = true;
		}
		if (pa.getActivityTemplateId() != null) {
			query.append(" AND PA.Activity_Template_Id = ");
			query.append(pa.getActivityTemplateId());
			orderBy = true;
		}
		if (pa.getAggregate() != null) {
			query.append(" AND AT.AGGREGATE = '");
			query.append(SQLizeString(pa.getAggregate()));
			query.append("'");
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

		if (orderBy)
			query.append(" ORDER BY " + orderByString);
		return query.toString();
	}

	public Document[] retrievePermitIssuanceDocuments() throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitIssuanceDocuments", true);
		ArrayList<Document> ret = connHandler.retrieveArray(Document.class);

		return ret.toArray(new Document[0]);
	}

	public final int getNextSequenceNumber(String seqNumber)
			throws DAOException {
		Integer seqNo = nextSequenceVal(seqNumber);
		return seqNo.intValue();
	}
	
	/**
	 * @see PermitDAO#removePermitApplications(int permitId)
	 */
	public final void removePermitApplication(int applicationId)
			throws DAOException {
		checkNull(applicationId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removePermitApplication", false);

		connHandler.setInteger(1, applicationId);

		connHandler.remove();
	}
	
	public final Application[] getCurrentPermitApplications (int permitID)
			throws DAOException	{
		Application[] permitApps = null;
		try {
			permitApps = readOnlyApplicationDAO
					.retrieveApplicationsIn(retrievePermitApplicationIds(permitID));
		} catch (DAOException e) {
			logger.error(e.getMessage(), e);
		}
		return permitApps;
	}
	
	/**
	 * @see PermitDAO#isDuplicatePermitNumber()
	 */
	public final boolean isDuplicatePermitNumber(String permitNumber) throws DAOException {
		if (Utility.isNullOrEmpty(permitNumber)) {
			return true;
		}
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.isDuplicatePermitNumber", true);
		connHandler.setString(1, permitNumber.trim());
		Integer count = (Integer) connHandler.retrieveJavaObject(Integer.class);
		return count > 0;
	}
	public final boolean isDuplicateLegacyPermitNumber(String legacyPermitNumber, String permitNumber) throws DAOException {
		if (Utility.isNullOrEmpty(legacyPermitNumber)) {
			return true;
		}
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.isDuplicateLegacyPermitNumber", true);
		connHandler.setString(1, legacyPermitNumber.trim());
		connHandler.setString(2, permitNumber.trim());
		Integer count = (Integer) connHandler.retrieveJavaObject(Integer.class);
		return count > 0;
	}

	@Override
	public List<PermitWorkflowSearchResult> searchPermitWorkflows(
			String facilityId, String facilityNm, String permitNumber,
			String applicationNumber, Integer userId, String permitType,
			String activityNm, String activityStatusCd, Date startDateFrom,
			Date startDateTo, Date endDateFrom, Date endDateTo, Integer processId, boolean filterSkipped, 
			boolean filterNonStarted)
			throws DAOException {
		
		PreparedStatement ps = null;
		Connection conn = null;
		setDefaultSearchLimit(-1);

		ArrayList<PermitWorkflowSearchResult> retVal = 
				new ArrayList<PermitWorkflowSearchResult>();

		try {
			conn = getReadOnlyConnection();
			StringBuffer projSql = new StringBuffer(
					loadSQL("PermitSQL.searchPermitWorkflows"));

			StringBuffer whereSql = new StringBuffer();
			whereSql.append(" AND (");
			
			if (facilityId != null && facilityId.trim().length() > 0) {
				String format = "F%06d";
				facilityId = facilityId.trim();
				int tempId;
				try {
					tempId = Integer.parseInt(facilityId);
					facilityId = String.format(format, tempId);
				} catch (NumberFormatException nfe) {
				}
				whereSql.append(" LOWER(fpf.facility_id) LIKE ");
				whereSql.append("LOWER('");
				whereSql
						.append(SQLizeString(facilityId.trim().replace("*", "%")));
				whereSql.append("')");
				whereSql.append(" AND ");
			}			

			if (facilityNm != null && facilityNm.trim().length() > 0) {
				whereSql.append(" LOWER(fpf.facility_nm) LIKE ");
				whereSql.append("LOWER('");
				whereSql.append(SQLizeString(facilityNm.trim().replace("*",
						"%")));
				whereSql.append("')");

				whereSql.append(" AND ");
			}

			if (permitNumber != null && permitNumber.trim().length() != 0) {
				whereSql.append(" LOWER(ptp.permit_nbr) LIKE ");
				whereSql.append("LOWER('");
				whereSql.append(SQLizeString(permitNumber.trim().replace("*",
						"%")));
				whereSql.append("')");

				whereSql.append(" AND ");
			}
			
			if (applicationNumber != null && applicationNumber.trim().length() != 0) {
				whereSql.append(" LOWER(paa.application_nbr) LIKE ");
				whereSql.append("LOWER('");
				whereSql.append(SQLizeString(applicationNumber.trim().replace("*",
						"%")));
				whereSql.append("')");

				whereSql.append(" AND ");			
			}
			
			if (userId != null) {
				whereSql.append("cmud.user_id = " + userId);

				whereSql.append(" AND ");
			}
			
			if (processId != null) {
				whereSql.append("wfp.process_id = " + processId);

				whereSql.append(" AND ");
			}
			
			if (permitType != null && permitType.trim().length() != 0) {
				whereSql.append(" LOWER(ptp.permit_type_cd) = ");
				whereSql.append("'");
				whereSql.append(SQLizeString(permitType.trim()));
				whereSql.append("'");

				whereSql.append(" AND ");			
			}
			
			if (activityNm != null && activityNm.trim().length() != 0) {
				whereSql.append(" LOWER(wfat.activity_template_nm) = ");
				whereSql.append("'");
				whereSql.append(SQLizeString(activityNm.trim()));
				whereSql.append("'");

				whereSql.append(" AND ");	
			}
			
			if (activityStatusCd != null && activityStatusCd.trim().length() != 0) {
				whereSql.append(" LOWER(wfpa.activity_status_cd) = ");
				whereSql.append("'");
				whereSql.append(SQLizeString(activityStatusCd.trim()));
				whereSql.append("'");

				whereSql.append(" AND ");	
			}

			List<Object> bindVars = new ArrayList<Object>();
			
			if (startDateFrom != null) {
				whereSql.append(" wfpa.start_dt >= ?");
				bindVars.add(formatBeginOfDay(startDateFrom));
				
				whereSql.append(" AND ");
			}
			
			if (startDateTo != null) {
				whereSql.append(" wfpa.start_dt <= ?");
				bindVars.add(formatEndOfDay(startDateTo));

				whereSql.append(" AND ");
			}
			
			if (filterNonStarted) {
				whereSql.append(" wfpa.start_dt is not null");

				whereSql.append(" AND ");
			}
			
			if (filterSkipped) {
				whereSql.append(" wfpa.activity_status_cd != ?");
				bindVars.add(ActivityStatusDef.SKIPPED);
				whereSql.append(" AND ");
			}
			
			if (endDateFrom != null) {
				whereSql.append(" wfpa.end_dt >= ?");
				bindVars.add(formatBeginOfDay(endDateFrom));

				whereSql.append(" AND ");
			}
			
			if (endDateTo != null) {
				whereSql.append(" wfpa.end_dt <= ?");
				bindVars.add(formatEndOfDay(endDateTo));

				whereSql.append(" AND ");
			}

			whereSql.append("1 = 1)");
			
			StringBuffer sql = new StringBuffer();
			sql.append(projSql);
			sql.append(" ");
			sql.append(whereSql);
			
			logger.debug("searchPermitWorkflows sql = " + sql);
			
			ps = conn.prepareStatement(sql.toString());

			for (int ix = 0; ix < bindVars.size(); ix++) {
				if (bindVars.get(ix) instanceof Date) {
					ps.setTimestamp(ix+1,new java.sql.Timestamp(((Date)bindVars.get(ix)).getTime()));
				}
				else
				if (bindVars.get(ix) instanceof String) {
					ps.setString(ix+1,(String)bindVars.get(ix));
				}
			}
			
			ResultSet res = ps.executeQuery();
			int numberOfHits = 0;

			while (res.next()) {
				PermitWorkflowSearchResult searchResult = 
						new PermitWorkflowSearchResult();
				setActivityId(searchResult, res);
				searchResult.setActivityTemplateId(res.getString("activity_template_id"));
				searchResult.setActivityNm(res.getString("activity_template_nm"));
				searchResult.setActivityStatusCd(res.getString("activity_status_cd"));
				searchResult.setApplicationNbr(res.getString("application_nbr"));
				searchResult.setCompanyNm(res.getString("company_nm"));
				searchResult.setEndDate(res.getTimestamp("end_dt"));
				searchResult.setFacilityId(res.getString("facility_id"));
				searchResult.setFpId(res.getInt("fp_id"));
				searchResult.setFacilityNm(res.getString("facility_nm"));
				searchResult.setPermitNumber(res.getString("permit_nbr"));
				searchResult.setPermitTypeCd(res.getString("permit_type_cd"));
				searchResult.setProcessId(res.getString("process_id"));
				searchResult.setApplicationReceivedDate(res.getTimestamp("received_date"));
				searchResult.setStartDate(res.getTimestamp("start_dt"));
				searchResult.setUserNm(res.getString("network_login_nm"));
				searchResult.setUserFirstNm(res.getString("first_nm"));
				searchResult.setUserLastNm(res.getString("last_nm"));
				searchResult.setExtendProcessEndDate(
						AbstractDAO.translateIndicatorToBoolean(
								res.getString("extend_process_end_date")));
				retVal.add(searchResult);
				numberOfHits++;
				if ((defaultSearchLimit > 0)
						&& (numberOfHits >= defaultSearchLimit)) {
					break;
				}
			}
			res.close();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			handleException(ex, conn);
		} finally {
			closeStatement(ps);
			handleClosing(conn);
		}
		return retVal;
	}

	private void setActivityId(PermitWorkflowSearchResult searchResult,
			ResultSet res) throws SQLException {
		String processId = res.getString("process_id");
		String activityTemplateId = res.getString("activity_template_id");
		String loopCount = res.getString("loop_cnt");
		searchResult.setActivityId(processId + '-' + activityTemplateId + '-' + loopCount);
	}
	
	
	public final PermitWorkflowActivityBenchmarkDef[] retrievePermitWorkflowActivityBenchmarkDefs() 
			throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitWorkflowActivityBenchmarkDefs", true);

		ArrayList<PermitWorkflowActivityBenchmarkDef> ret = connHandler.retrieveArray(PermitWorkflowActivityBenchmarkDef.class);

		return ret.toArray(new PermitWorkflowActivityBenchmarkDef[0]);
	}
	
	/**
	 * Retrieves time sheet hours from AQDS for a given permit and it's associated application numbers.
	 * 
	 * @param applicationNumbersString - comma separated list of IMPACT application numbers
	 * @param permitNumber - IMPACT permit number
	 * 
	 * @return - An array of time sheet entries (rows) from the AQDS
	 * 
	 * @throws DAOException
	 */
	public TimeSheetRow[] retrieveTimeSheetHours(String permitApplicationNumbers, String permitNumber) throws DAOException {
		TimeSheetRow[] impactHours = 
				retrieveTimeSheetHoursFromImpact(permitApplicationNumbers, permitNumber);
		TimeSheetRow[] aqdsHours = 
				retrieveTimeSheetHoursFromAqds(permitApplicationNumbers, permitNumber);
		
		// TODO sort by date
		
		TimeSheetRow[] aggregatedHours = 
				(TimeSheetRow[])ArrayUtils.addAll(impactHours, aqdsHours);
		return aggregatedHours;
	}
	
	public TimeSheetRow[] retrieveTimeSheetHoursFromImpact(String permitApplicationNumbers, String permitNumber) throws DAOException {
		checkNull(permitNumber);
		
		String sqlStatement = new String(loadSQL("NSRBillingSQL.retrieveImpactTimeSheetHours"));
		StringBuffer whereClause = new StringBuffer(" WHERE ");
		String orderBy = " ORDER by tsh.date ASC";
		
		// create the where clause to lookup individual application numbers
		if(null != permitApplicationNumbers) {
			String applicationNumbers[] = permitApplicationNumbers.split(",");
			for(String s : applicationNumbers) {
				whereClause.append("tsh.section_nsr_id = '" + s.trim() + "' OR ");
			}
		}
		
		// look up permit number as well
		whereClause.append("tsh.section_nsr_id = '" + permitNumber + "'");
		
		String sql = sqlStatement + whereClause.toString() + orderBy; 
		
        ConnectionHandler connHandler = new ConnectionHandler(true);
		connHandler.setSQLStringRaw(sql);
		
		ArrayList<TimeSheetRow> ret = connHandler.retrieveArray(TimeSheetRow.class);
		
		TimeSheetRow[] retarray = ret.toArray(new TimeSheetRow[0]);
		return retarray;
	}
	
	public TimeSheetRow[] retrieveTimeSheetHoursFromAqds(String permitApplicationNumbers, String permitNumber) throws DAOException {
		checkNull(permitNumber);
		
		ConnectionHandler connHandler = new AQDSConnectionHandler().getAqdsConnection();
		
		String sqlStatement = new String(loadSQL("NSRBillingSQL.retrieveAQDSTimeSheetHours"));
		StringBuffer whereClause = new StringBuffer(" WHERE ");
		String orderBy = " ORDER by tsh.Date ASC";
		
		// create the where clause to lookup individual application numbers
		if(null != permitApplicationNumbers) {
			String applicationNumbers[] = permitApplicationNumbers.split(",");
			for(String s : applicationNumbers) {
				whereClause.append("t21.appNum = '" + s.trim() + "' OR ");
			}
		}
		
		// look up permit number as well
		whereClause.append("t21.appNum = '" + permitNumber + "'");
		
		String sql = sqlStatement + whereClause.toString() + orderBy; 
		
		connHandler.setSQLStringRaw(sql);
		
		ArrayList<TimeSheetRow> ret = connHandler.retrieveArray(TimeSheetRow.class);
		
		TimeSheetRow[] retarray = ret.toArray(new TimeSheetRow[0]);
		return retarray;
	}
	
	/**
	 * Retrieves rows from the Billable Rates definition list
	 * @return An array of billable rates
	 * @throws DAOException
	 */
	
	public NSRBillingBillableRateDef[] retrieveBillableRatesDef() throws DAOException {
		ConnectionHandler conn = new ConnectionHandler("NSRBillingSQL.retrieveBillableRateDef", true);
		
		List<NSRBillingBillableRateDef> ret = conn.retrieveArray(NSRBillingBillableRateDef.class);
		
		return ret.toArray(new NSRBillingBillableRateDef[0]);
	}
	
	public final List<NSRFixedCharge> retrieveNSRFixedChargeList(int permitId)
			throws DAOException {

		checkNull(permitId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveNSRFixedChargeList", true);

		connHandler.setInteger(1, permitId);

		ArrayList<NSRFixedCharge> ret = new ArrayList<NSRFixedCharge>();
		ArrayList<NSRFixedCharge> base = connHandler
				.retrieveArray(NSRFixedCharge.class);
		for (BaseDBObject bd : base) {
			ret.add((NSRFixedCharge) bd);
		}

		return ret;
	}

	public final void removeNSRFixedChargeList(int permitId)
			throws DAOException {

		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removeNSRFixedChargeList", false);

		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}

	public final NSRFixedCharge createNSRFixedCharge(NSRFixedCharge pcp)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.createNSRFixedCharge", false);

		checkNull(pcp);
		int i = 1;
		InfrastructureBO iBO = new InfrastructureBO();
		int id = nextSequenceVal("S_Permit_FC_Id");
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, pcp.getPermitId());
		connHandler.setTimestamp(i++, pcp.getCreatedDate());
		connHandler.setString(i++, pcp.getNoteTxt());
		connHandler.setDouble(i++, pcp.getAmount());
		connHandler.setString(i++, pcp.getDescription());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(pcp.isInvoiced()));

		connHandler.update();

		pcp.setPermitFCId(id);
		pcp.setLastModified(1);

		return pcp;
	}

	public final void modifyNSRFixedCharge(NSRFixedCharge pcp)
			throws DAOException {

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyNSRFixedCharge", false);

		checkNull(pcp);
		int i = 1;
		connHandler.setInteger(i++, pcp.getPermitFCId());
		connHandler.setInteger(i++, pcp.getPermitId());

		connHandler.setTimestamp(i++, pcp.getCreatedDate());
		connHandler.setString(i++, pcp.getNoteTxt());
		connHandler.setDouble(i++, pcp.getAmount());
		connHandler.setString(i++, pcp.getDescription());
		connHandler.setString(i++, AbstractDAO.translateBooleanToIndicator(pcp.isInvoiced()));

		connHandler.setInteger(i++, pcp.getLastModified() + 1);

		connHandler.setInteger(i++, pcp.getPermitFCId());
		connHandler.setInteger(i++, pcp.getPermitId());
		connHandler.setInteger(i++, pcp.getLastModified());

		connHandler.update();
	}

	public final void removeNSRFixedCharge(NSRFixedCharge pcc)
			throws DAOException {

		checkNull(pcc);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.removeNSRFixedCharge", false);

		int i = 1;
		connHandler.setInteger(i++, pcc.getPermitFCId());
		connHandler.setInteger(i++, pcc.getPermitId());
		connHandler.setInteger(i++, pcc.getLastModified());

		connHandler.remove();
	}

	/**
	 * Retrieves rows from the standard fee definition list
	 * 
	 * @return An array of standard fees
	 * @throws DAOException
	 */

	public NSRBillingStandardFeesDef[] retrieveStandardFeeDef()
			throws DAOException {
		ConnectionHandler conn = new ConnectionHandler(
				"NSRBillingSQL.retrieveStandardFeeDef", true);

		List<NSRBillingStandardFeesDef> ret = conn
				.retrieveArray(NSRBillingStandardFeesDef.class);

		return ret.toArray(new NSRBillingStandardFeesDef[0]);
	}
	
	@Override
	public EmissionsOffset createPermitEmissionsOffset(EmissionsOffset emissionsOffset)
		throws DAOException {
		
		checkNull(emissionsOffset);
		
		ConnectionHandler connHandler = new ConnectionHandler(
							"PermitSQL.createPermitEmissionsOffset", true);
		Integer id = nextSequenceVal("S_Emissions_Offset_Id");
		int i = 1;
		
		connHandler.setInteger(i++, id);
		connHandler.setInteger(i++, emissionsOffset.getPermitId());
		connHandler.setString(i++, emissionsOffset.getNonAttainmentAreaCd());
		connHandler.setString(i++, emissionsOffset.getAttainmentStandardCd());
		connHandler.setString(i++, emissionsOffset.getPollutantCd());
		connHandler.setDouble(i++, emissionsOffset.getCurrentOffset());
		connHandler.setDouble(i++, emissionsOffset.getBaseOffset());
		connHandler.setDouble(i++, emissionsOffset.getOffsetMultiple());
		connHandler.setString(i++, emissionsOffset.getComment());
		
		connHandler.update();
		
		emissionsOffset.setEmissionsOffsetId(id);
		emissionsOffset.setLastModified(1);
		
		return emissionsOffset;
	}
	
	@Override
	public EmissionsOffset retrievePermitEmissionsOffset(Integer emissionsOffsetId)
		throws DAOException {
		
		checkNull(emissionsOffsetId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitEmissionsOffset", true);
		
		connHandler.setInteger(1, emissionsOffsetId);
		
		return (EmissionsOffset)connHandler.retrieve(EmissionsOffset.class);
	}
	
	@Override
	public boolean modifyPermitEmissionsOffset(EmissionsOffset emissionsOffset)
			throws DAOException {
		
		checkNull(emissionsOffset);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.modifyPermitEmissionsOffset", true);
		int i = 1;
		
		connHandler.setInteger(i++, emissionsOffset.getPermitId());
		connHandler.setString(i++, emissionsOffset.getNonAttainmentAreaCd());
		connHandler.setString(i++, emissionsOffset.getAttainmentStandardCd());
		connHandler.setString(i++, emissionsOffset.getPollutantCd());
		connHandler.setDouble(i++, emissionsOffset.getCurrentOffset());
		connHandler.setDouble(i++, emissionsOffset.getBaseOffset());
		connHandler.setDouble(i++, emissionsOffset.getEmissionsReductionMultiple());
		connHandler.setDouble(i++, emissionsOffset.getOffsetMultiple());
		connHandler.setString(i++, emissionsOffset.getComment());
		connHandler.setString(i++, emissionsOffset.getLastModified() + 1);
		
		connHandler.setString(i++, emissionsOffset.getEmissionsOffsetId());
		connHandler.setString(i++, emissionsOffset.getLastModified());
		
		return connHandler.update();
	}
	
	@Override
	public void deletePermitEmissionsOffset(Integer emissonsOffsetId)
			throws DAOException {
		
		checkNull(emissonsOffsetId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.deletePermitEmissionsOffset", true);
		
		connHandler.setInteger(1, emissonsOffsetId);
		
		connHandler.remove();
	}
	
	
	@Override
	public EmissionsOffset[] retrievePermitEmissionsOffsetByPermitId(Integer permitId)
			throws DAOException {
		
		checkNull(permitId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrievePermitEmissionsOffsetsByPermitId", true);
		
		connHandler.setInteger(1, permitId);
		
		ArrayList<EmissionsOffset> permitEmissionsOffsets =
					connHandler.retrieveArray(EmissionsOffset.class);
		
		return permitEmissionsOffsets.toArray(new EmissionsOffset[0]);
	}

	@Override
	public void deletePermit(Integer permitId) throws DAOException {
		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removePermit", true);
		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}

	@Override
	public void removePermitIssuances(Integer permitId) throws DAOException {
		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removePermitIssuances", true);
		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}

	@Override
	public void removePermitNotes(Integer permitId) throws DAOException {
		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removePermitNotes", true);
		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}
	
	@Override
	public void removeAllEUGroups(Integer permitId) throws DAOException {
		checkNull(permitId);
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removeAllEUGroups", true);
		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}
		
	@Override
	public void markInactiveNSRPermitsToExpired() throws DAOException {
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.markInactiveNSRPermitsToExpired", true);

		connHandler.update();
	}

	@Override
	public PermitDocument retrievePermitDocumentById(Integer documentId) throws DAOException {
		checkNull(documentId);
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrievePermitDocumentById", true);

		connHandler.setInteger(1, documentId);
		return (PermitDocument) connHandler.retrieve(PermitDocument.class);
	}
	
	public final Integer retrievePermitWorkflowProcessId(String permitNumber) throws DAOException {
		if (Utility.isNullOrEmpty(permitNumber)) {
			return null;
		}
		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.retrievePermitWorkflowProcessId", true);
		connHandler.setString(1, permitNumber.trim());
		Integer processId = (Integer) connHandler.retrieveJavaObject(Integer.class);
		return processId;
	}

//	@Override
//	public void removePermitConditionCategoryXref(Integer permitId) throws DAOException {
//		checkNull(permitId);
//		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removePermitConditionCategoryXref", true);
//		
//		connHandler.setInteger(1, permitId);
//		connHandler.remove();
//	}
	
	
//	@Override
//	public void removePermitConditionEUXref(Integer permitId) throws DAOException {
//		checkNull(permitId);
//		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removePermitConditionEUXref", true);
//		
//		connHandler.setInteger(1, permitId);
//		connHandler.remove();
//	}
	
	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#removeSubpartCDs(Integer)
	 */
	public final void removeSubpartCDs(Integer permitId) throws DAOException {

		checkNull(permitId);

		ConnectionHandler connHandler = new ConnectionHandler("PermitSQL.removeMactSubpartCD", false);
		connHandler.setInteger(1, permitId);
		connHandler.remove();

		connHandler = new ConnectionHandler("PermitSQL.removeNeshapsSubpartCD", false);
		connHandler.setInteger(1, permitId);
		connHandler.remove();

		connHandler = new ConnectionHandler("PermitSQL.removeNspsSubpartCD", false);
		connHandler.setInteger(1, permitId);
		connHandler.remove();
	}
	
	/**
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#retrievePermitEUGroups(Integer)
	 */
	@Override
	public List<PermitEUGroup> retrievePermitEUGroups(Integer permitId) throws DAOException {

		checkNull(permitId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"PermitSQL.retrieveAllEUGroups", true);

		connHandler.setInteger(1, permitId);

		return connHandler.retrieveArray(PermitEUGroup.class);
	}

	/**
	 * Retrieves basic permit info
	 */
	@Override
	public Permit retrievePermitBasic(Integer permitId) throws DAOException {

		checkNull(permitId);
		
		Permit permit = null;
		Connection conn = null;
		PreparedStatement pStmt = null;

		try {
			conn = getReadOnlyConnection();
			StringBuffer sql = new StringBuffer(
					loadSQL("PermitSQL.retrievePermit"));
			sql.append(" WHERE pp.permit_id = ?");
			pStmt = conn.prepareStatement(sql.toString());
			pStmt.setInt(1, permitId.intValue());

			ResultSet rs = pStmt.executeQuery();

			if (rs.next()) {
				permit = initPermit(rs);
			}
		} catch (Exception e) {
			handleException(e, conn);
		} finally {
			closeStatement(pStmt);
			handleClosing(conn);
		}
		
		return permit;
	}

	/*
	 * Retrieves the list of application numbers associated with the given
	 * permit
	 * 
	 * @see us.oh.state.epa.stars2.database.dao.PermitDAO#
	 * retrievePermitApplicationNumbers(java.lang.Integer)
	 */
	@Override
	public String[] retrievePermitApplicationNumbers(Integer permitId) throws DAOException {

		checkNull(permitId);

		ConnectionHandler connectionHandler = new ConnectionHandler("PermitSQL.retrievePermitApplicationNumbers", true);

		connectionHandler.setInteger(1, permitId);

		return connectionHandler.retrieveJavaObjectArray(String.class).toArray(new String[0]);
	}
	
	
}
