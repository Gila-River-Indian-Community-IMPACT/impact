package us.oh.state.epa.stars2.database.dbObjects.complianceReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.def.ComplianceReportAllSubtypesDef;

@SuppressWarnings("serial")
public class ComplianceReportList extends BaseDB {
    private String FacilityId;
    private Integer fpId;
    private String cmpId;
    private String scscId;
    private int reportId;
    private String reportCRPTId;
    private String reportType;
    private int UserId;
    private Timestamp receivedDate;
    private Timestamp submittedDate;
    private Timestamp reviewDate;
    private String dueDateCd;
    private int reviewerUserId;
    private String acceptable;
    private String dapcDeviationsReported;
    private Timestamp tvccAfsSentDate;
    private String facilityName;
    private String facilityTypeCd;
    private String permitClassCd;
    private String companyName;
    private String doLaaCd;
    private String reportStatus;
    private int deviationReportCt;
    private String description;
    private String otherTypeCd;
    private String otherTypeDsc;
    private String reportingPeriodDesc;
    private Timestamp perStartDate;
    private Timestamp perEndDate;
    private String tvccReportingYear;
    private String tvccScheduledYear;
    private String descriptionSearchDisplay;
    private String tvccAfsId;
    private String complianceStatusCd;
    private String facilityAfsNumber;
    private String dapcReviewComments;
    
    public Integer getFpId() {
		return fpId;
	}

	public void setFpId(Integer fpId) {
		this.fpId = fpId;
	}

	public String getDescriptionSearchDisplay() {
        return descriptionSearchDisplay;
    }

    public void setDescriptionSearchDisplay(String descriptionSearchDisplay) {
        this.descriptionSearchDisplay = descriptionSearchDisplay;
    }

    public String getTvccReportingYear() {
        return tvccReportingYear;
    }


    public void setTvccReportingYear(String tvccReportingYear) {
        this.tvccReportingYear = tvccReportingYear;
    }


    public String getOtherTypeCd() {
        return otherTypeCd;
    }


    public void setOtherTypeCd(String otherTypeCd) {
        this.otherTypeCd = otherTypeCd;
    }


    public String getOtherTypeDsc() {
        return otherTypeDsc;
    }


    public void setOtherTypeDsc(String otherTypeDsc) {
        this.otherTypeDsc = otherTypeDsc;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public int getDeviationReportCt() {
        return deviationReportCt;
    }


    public void setDeviationReportCt(int deviationReportCt) {
        this.deviationReportCt = deviationReportCt;
    }


    public String getReportStatus() {
        return reportStatus;
    }


    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }


    public String getFacilityName() {
        return facilityName;
    }


    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
    
    public String getCompanyName() {
        return companyName;
    }


    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public void populate(ResultSet rs) throws SQLException {
        try {
            setAcceptable(rs.getString("dapc_accepted"));
            setReportId(AbstractDAO.getInteger(rs, "report_id"));
            setReportCRPTId(rs.getString("crpt_id"));
            setReportType(rs.getString("report_type_cd"));          
            setFacilityId(rs.getString("facility_id"));

            int fpId = 0;
            try {
            	fpId = rs.getInt("fp_id");
            } catch (Exception e) {
                logger.debug("Unable to get FP_ID");
            }
            setFpId(0 == fpId? null : fpId);

            setCmpId(rs.getString("cmp_id"));
            setReportStatus(rs.getString("report_status"));
            setReceivedDate(rs.getTimestamp("received_date"));
            setSubmittedDate(rs.getTimestamp("submitted_date"));
            setReviewDate(rs.getTimestamp("dapc_reviewed_date"));
            setAcceptable(rs.getString("dapc_accepted"));
            setDapcDeviationsReported(rs.getString("dapc_deviations"));
            setLastModified(AbstractDAO.getInteger(rs, "cr_lm"));
            setComplianceStatusCd(rs.getString("compliance_status_cd"));
            String facilityNm2 = null;
            try {
            	facilityNm2 = rs.getString("facility_nm2");
            } catch (Exception e) {
                logger.debug("Unable to get facility_nm2");
            }
            if (null != facilityNm2) {
            	setFacilityName(facilityNm2);
            } else {
            	setFacilityName(rs.getString("facility_nm"));
            }

            String facilityTypeCd2 = null;
            try {
            	facilityTypeCd2 = rs.getString("facility_type_cd2");
            } catch (Exception e) {
                logger.debug("Unable to get facility_type_cd2");
            }
            if (null != facilityTypeCd2) {
            	setFacilityTypeCd(facilityTypeCd2);
            } else {
            	setFacilityTypeCd(rs.getString("facility_type_cd"));
            }

            String permitClassCd2 = null;
            try {
            	permitClassCd2 = rs.getString("permit_classification_cd2");
            } catch (Exception e) {
                logger.debug("Unable to get permit_classification_cd2");
            }
            if (null != permitClassCd2) {
            	setPermitClassCd(permitClassCd2);
            } else {
            	setPermitClassCd(rs.getString("permit_classification_cd"));
            }
            
            try {
                setCompanyName(rs.getString("company_nm"));
            } catch (Exception e) {
                logger.debug("Unable to get Company Name");
            }

            String doLaaCd2 = null;
            try {
            	doLaaCd2 = rs.getString("do_laa_cd2");
            } catch (Exception e) {
                logger.debug("Unable to get do_laa_cd2");
            }
            if (null != doLaaCd2) {
            	setDoLaaCd(doLaaCd2);
            } else {
            	setDoLaaCd(rs.getString("do_laa_cd"));            		
            }

            String scscId2 = null;
            try {
            	scscId2 = rs.getString("scsc_id2");
            } catch (Exception e) {
                logger.debug("Unable to get scsc_id2");
            }
            if (null != scscId2) {
            	setScscId(scscId2);
            } else {
            	setScscId(rs.getString("scsc_id"));
            }
            
            try {
                setPerEndDate(rs.getTimestamp("per_end_date"));
            } catch (Exception e) {
                logger.debug("Unable to get PER end Date");
            }
            
            try {
                setTvccReportingYear(rs.getString("tvcc_reporting_year"));
            } catch (Exception e) {
                logger.debug("Unable to get TVCC Start year");
            }

            try {
                setPerStartDate(rs.getTimestamp("per_start_date"));
            } catch (Exception e) {
                logger.debug("Unable to get PER Start Date");
            }
            
            try {
                setOtherTypeCd(rs.getString("other_type_cd"));
                setOtherTypeDsc("");
                if(getOtherTypeCd() != null) {
                    setOtherTypeDsc(ComplianceReportAllSubtypesDef.getData().getItems().getItemDesc(getOtherTypeCd()));
                }
            } catch (Exception ee) {
                logger.debug("ee: bad column name");
            }

            setDescription(rs.getString("comments"));
            setDescriptionSearchDisplay(getDescription());
            
            String facilityAfsNumber2 = null;
            try {
            	facilityAfsNumber2 = rs.getString("facility_afs_num2");
            } catch (Exception e) {
                logger.debug("Unable to get facility_afs_num2");
            }
            if (null != facilityAfsNumber2) {
            	setFacilityAfsNumber(facilityAfsNumber2);
            } else {
            	setFacilityAfsNumber(rs.getString("facility_afs_num"));
            }

            if (getReportType().equals("per")) {
                SimpleDateFormat sdf =  new SimpleDateFormat("MM/dd/yyyy");
                try { setDueDateCd(rs.getString("per_due_date_cd"));  } catch (SQLException e) {}
                try { setPerStartDate(rs.getTimestamp("per_start_date")); } catch (SQLException e) {logger.debug("Unable to get PER End Date");}
                try {  setPerEndDate(rs.getTimestamp("per_end_date")); } catch (SQLException e) {logger.debug("Unable to get PER End Date");}
                try {
                    reportingPeriodDesc= sdf.format(getPerStartDate()) + "-"+ sdf.format(getPerEndDate());
                } catch (Exception e) {
                    reportingPeriodDesc= "ERROR";
                    logger.debug("Unable to get PER End Date");
                }
            } else if (getReportType().equals("tvcc")) {
                setTvccAfsSentDate(rs.getTimestamp("tvcc_afs_sent_date"));
                setTvccAfsId(rs.getString("tvcc_afs_id"));
                try { setPerStartDate(rs.getTimestamp("per_start_date")); }
                catch (SQLException e) {}
                //the per_start_date field is used to store TVCC data due to changes by AQD Feb '08
                
                try { 
                	setTvccReportingYear(Integer.toString(AbstractDAO.getInteger(rs,"tvcc_reporting_year")));
                	setTvccScheduledYear(Integer.toString(AbstractDAO.getInteger(rs,"tvcc_reporting_year") + 1));
                }
                catch (SQLException e) {}
                reportingPeriodDesc=getTvccReportingYear();
                
            } else if (getReportType().equals("othr")){
                reportingPeriodDesc="";
            }
            setDapcReviewComments(rs.getString("dapc_comments"));
            setDirty(false);
        } catch (SQLException sqle) {
            logger.error("Required field error: " + sqle.getMessage(), sqle);
        }

    }


    public String getAcceptable() {
        return acceptable;
    }
    
    public boolean getAcceptableTF() {
        boolean ret = true;
        if (acceptable == null || acceptable.equals("no")) {
            ret = false;
        }

        return ret;
    }


    public void setAcceptable(String acceptable) {
        this.acceptable = acceptable;
    }


    public int getReportId() {
        return reportId;
    }


    public void setReportId(int complianceReportId) {
        this.reportId = complianceReportId;
    }
    
    public String getReportCRPTId() {
        return reportCRPTId;
    }


    public void setReportCRPTId(String reportCRPTId) {
        this.reportCRPTId = reportCRPTId;
    }

    public String getFacilityId() {
        return FacilityId;
    }


    public void setFacilityId(String facilityId) {
        FacilityId = facilityId;
    }

    public String getCmpId() {
        return cmpId;
    }


    public void setCmpId(String cmpId) {
    	this.cmpId = cmpId;
    }
    
    public Timestamp getReceivedDate() {
        return receivedDate;
    }


    public void setReceivedDate(Timestamp receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getReportType() {
        return reportType;
    }


    public void setReportType(String reportType) {
        this.reportType = reportType;
    }


    public Timestamp getReviewDate() {
        return reviewDate;
    }


    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }


    public int getReviewerUserId() {
        return reviewerUserId;
    }


    public void setReviewerUserId(int reviewerUserId) {
        this.reviewerUserId = reviewerUserId;
    }


    public int getUserId() {
        return UserId;
    }


    public void setUserId(int userId) {
        UserId = userId;
    }


    public String getDueDateCd() {
        return dueDateCd;
    }


    public void setDueDateCd(String dueDateCd) {
        this.dueDateCd = dueDateCd;
    }
    
    public String getReportingPeriodDesc() {
        /*
        if (getReportType().equals("per")) {
            SimpleDateFormat sdf =  new SimpleDateFormat("MM/dd/yyyy");
            try {
                reportingPeriodDesc= sdf.format(getPerStartDate()) + " - "+ sdf.format(getPerEndDate());
            } catch (Exception e) {
                logger.error(e);
                reportingPeriodDesc= "ERROR";
            }
        } else if (getReportType().equals("tvcc")) {
            reportingPeriodDesc=getTvccReportingYear();
        } else {
            reportingPeriodDesc="";
        }
        */
        return reportingPeriodDesc;
    }

    public Timestamp getPerEndDate() {
        return perEndDate;
    }

    public void setPerEndDate(Timestamp perEndDate) {
        this.perEndDate = perEndDate;
    }

    public Timestamp getPerStartDate() {
        return perStartDate;
    }

    public void setPerStartDate(Timestamp perStartDate) {
        this.perStartDate = perStartDate;
    }

    public String getDoLaaCd() {
        return doLaaCd;
    }

    public void setDoLaaCd(String doLaaCd) {
        this.doLaaCd = doLaaCd;
    }


    public Timestamp getTvccAfsSentDate() {
        return tvccAfsSentDate;
    }


    public void setTvccAfsSentDate(Timestamp tvccAfsSentDate) {
        this.tvccAfsSentDate = tvccAfsSentDate;
    }


    public String getTvccAfsId() {
        return tvccAfsId;
    }


    public void setTvccAfsId(String tvccAfsId) {
        this.tvccAfsId = tvccAfsId;
    }


    public String getComplianceStatusCd() {
        return complianceStatusCd;
    }


    public void setComplianceStatusCd(String complianceStatusCd) {
        this.complianceStatusCd = complianceStatusCd;
    }


    public String getDapcDeviationsReported() {
        return dapcDeviationsReported;
    }


    public void setDapcDeviationsReported(String dapcDeviationsReported) {
        this.dapcDeviationsReported = dapcDeviationsReported;
    }


    public String getScscId() {
        return scscId;
    }


    public void setScscId(String scscId) {
        this.scscId = scscId;
    }


    public Timestamp getSubmittedDate() {
        return submittedDate;
    }


    public void setSubmittedDate(Timestamp submittedDate) {
        this.submittedDate = submittedDate;
    }


	public String getTvccScheduledYear() {
		return tvccScheduledYear;
	}


	public void setTvccScheduledYear(String tvccScheduledYear) {
		this.tvccScheduledYear = tvccScheduledYear;
	}


	public String getFacilityTypeCd() {
		return facilityTypeCd;
	}


	public void setFacilityTypeCd(String facilityTypeCd) {
		this.facilityTypeCd = facilityTypeCd;
	}


	public String getPermitClassCd() {
		return permitClassCd;
	}


	public void setPermitClassCd(String permitClassCd) {
		this.permitClassCd = permitClassCd;
	}
	
	public String getFacilityAfsNumber() {
		return facilityAfsNumber;
	}

	public void setFacilityAfsNumber(String facilityAfsNumber) {
		this.facilityAfsNumber = facilityAfsNumber;
	}


	public String getDapcReviewComments() {
		return dapcReviewComments;
	}


	public void setDapcReviewComments(String dapcReviewComments) {
		this.dapcReviewComments = dapcReviewComments;
	}
	
	
}
