package us.oh.state.epa.stars2.database.dbObjects.emissionReport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.BaseDB;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.def.ReportReceivedStatusDef;
import us.oh.state.epa.stars2.framework.util.Utility;

public class EmissionsRptInfo extends BaseDB {
    private String facilityId;
    private Integer year;
    private String state; // The state of the report from ReportReceivedStatusDef.
    private Boolean reportingEnabled;  // facility cannot start report unless true.
    private String comment;
    private String truncatedComment;
    // not in database if indatabase is false;
    private Boolean indatabase;
    
    private String contentTypeCd;
    private String regulatoryRequirementCd;
    
    private Integer scEmissionsReportId;
    
    private Integer previousScEmissionsReportId;

    public EmissionsRptInfo() {
        indatabase = new Boolean(false);
    }
    
    public EmissionsRptInfo(EmissionsRptInfo i) {
    	super(i);
        indatabase = new Boolean(false);
        year = i.year;
        contentTypeCd = i.contentTypeCd;
        regulatoryRequirementCd = i.regulatoryRequirementCd;
        state = i.state;
        reportingEnabled = i.reportingEnabled;
        comment = new String();  // do not copy comment
        scEmissionsReportId = i.scEmissionsReportId;
        previousScEmissionsReportId = i.previousScEmissionsReportId;
    }
    
    public static EmissionsRptInfo locateEmissionsRptInfo(
            ArrayList<EmissionsRptInfo> emissionsReportInfo,
            Integer year, String contentTypeCd) {
    	if (year == null || Utility.isNullOrEmpty(contentTypeCd)) {
    		return null;
    	}
    		
        for(EmissionsRptInfo i : emissionsReportInfo) {
            if((i.getYear().equals(year)) && (i.getContentTypeCd().equals(contentTypeCd))) {
            	return i;
            }
        }
        return null;
    }
    
    public String whyNotReport() {
        String rtn = null;
        if(!reportingEnabled) {
            rtn = "reporting not enabled for " + year;
        } else if(state.equals(ReportReceivedStatusDef.NO_REPORT_NEEDED)) {
            rtn = "State for reporting is ";
        }
        return rtn;
    }
    
    public EmissionsRptInfo canReport() {
        if(!reportingEnabled ||
                state.equals(ReportReceivedStatusDef.NO_REPORT_NEEDED)) {
            return null;

        }
        return this;
    }
    
    public static ValidationMessage[] validate(List<EmissionsRptInfo> list) {
        HashMap<String, ValidationMessage> validationMessages = 
            new HashMap<String, ValidationMessage>();
        int i = 0;
        outerLoop:
        for(EmissionsRptInfo eri : list) {
            if(eri.year == null || 
            		eri.getContentTypeCd() == null ||
            		eri.getRegulatoryRequirementCd() == null) {
                validationMessages.put(new Integer(i).toString(),
                        new ValidationMessage(
                        "missingValues", "Year or Content Type or Regulatory Requirement is not specified",
                        ValidationMessage.Severity.ERROR, null));
                i++; // Just to make label unique--not used
            } else { // look for duplicate year, content type, and regulatory requirement
                for(EmissionsRptInfo eri2 : list) {
                    if(eri == eri2) break;
                    if(eri2.year == null) continue;
                    if((eri.year.equals(eri2.year)) &&
                    		(eri.getContentTypeCd().equals(eri2.getContentTypeCd())) &&
                    				(eri.getRegulatoryRequirementCd().equals(eri2.getRegulatoryRequirementCd()))) {
                        validationMessages.put(new Integer(i).toString(),
                                new ValidationMessage(
                                "dupServiceCatalog", "Year: " + eri.year + 
                                ", Content Type: " + eri.getContentTypeCd() +
                                ", Regulatory Requirement: " + eri.getRegulatoryRequirementCd() +
                                " combination is a duplicate.",
                                ValidationMessage.Severity.ERROR, null));
                        i++; // Just to make label unique--not used
                        continue outerLoop;
                    }
                }
            }
            String s = " not specified ";
            if(eri.state == null  ) {
                validationMessages.put(new Integer(i).toString(),
                        new ValidationMessage(
                        "state", "State" + s,
                        ValidationMessage.Severity.ERROR, null));
                i++; // Just to make label unique--not used
            }
        }
        return new ArrayList<ValidationMessage>(validationMessages.values())
        .toArray(new ValidationMessage[0]);
    }


    public final String getState() {
        return state;
    }

    public final void setState(String state) {
        this.state = state;
    }

    public final Integer getYear() {
        return year;
    }

    public final void setYear(Integer year) {
        this.year = year;
    }

    public final void populate(ResultSet rs) {
        String look = "facility_id";
        previousScEmissionsReportId = null;
        try {
            setFacilityId(rs.getString("facility_id"));
            look = "sc_id";
            setScEmissionsReportId(AbstractDAO.getInteger(rs, "sc_id"));
            look = "rpt_received_status_cd";
            setState(rs.getString("rpt_received_status_cd"));
            look = "reporting_enabled";
            setReportingEnabled(AbstractDAO.translateIndicatorToBoolean(rs
                    .getString("reporting_enabled")));
            look = "comments";
            setComment(rs.getString("comments"));
            look = "last_modified";
            setLastModified(AbstractDAO.getInteger(rs, "last_modified"));
            setIndatabase(new Boolean(true));
            
			try {
				look = "year";
				setYear(AbstractDAO.getInteger(rs, "year"));
			} catch (SQLException e) {
				logger.warn("year field is missing");
			}
            
			try {
				look = "content_type_cd";
				setContentTypeCd(rs.getString("content_type_cd"));
			} catch (SQLException e) {
				logger.warn("content_type_cd field is missing");
			}

			try {
				look = "regulatory_requirement_cd";
				setRegulatoryRequirementCd(rs
						.getString("regulatory_requirement_cd"));
			} catch (SQLException e) {
				logger.warn("regulatory_requirement_cd field is missing");
			}
            
        } catch (SQLException sqle) {
            logger.error("Required field error: " + look);
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((state == null) ? 0 : state.hashCode());
        result = PRIME * result + ((year == null) ? 0 : year.hashCode());
        result = PRIME * result + ((contentTypeCd == null) ? 0 : contentTypeCd.hashCode());
        result = PRIME * result + ((regulatoryRequirementCd == null) ? 0 : regulatoryRequirementCd.hashCode());
        result = PRIME * result + ((scEmissionsReportId == null) ? 0 : scEmissionsReportId.hashCode());
        result = PRIME * result + ((previousScEmissionsReportId == null) ? 0 : previousScEmissionsReportId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final EmissionsRptInfo other = (EmissionsRptInfo) obj;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        if (year == null) {
            if (other.year != null)
                return false;
        } else if (!year.equals(other.year))
            return false;
            
        if (contentTypeCd == null) {
            if (other.contentTypeCd != null)
                return false;
        } else if (!contentTypeCd.equals(other.contentTypeCd))
            return false;
            
        if (regulatoryRequirementCd == null) {
            if (other.regulatoryRequirementCd != null)
                return false;
        } else if (!regulatoryRequirementCd.equals(other.regulatoryRequirementCd))
            return false;
       
        if (scEmissionsReportId == null) {
            if (other.scEmissionsReportId != null)
                return false;
        } else if (!scEmissionsReportId.equals(other.scEmissionsReportId))
            return false;

        if (previousScEmissionsReportId == null) {
            if (other.previousScEmissionsReportId != null)
                return false;
        } else if (!previousScEmissionsReportId.equals(other.previousScEmissionsReportId))
            return false;

        
        return true;
    }

    public final Boolean getIndatabase() {
        return indatabase;
    }

    public final void setIndatabase(Boolean indatabase) {
        this.indatabase = indatabase;
    }

    public final String getComment() {
        return comment;
    }

    public final void setComment(String comment) {
        this.comment = comment;
        if(comment != null && comment.length() == 0) {
            this.comment = null;
        }
        if(null != comment && comment.length() > 160){
            this.truncatedComment = comment.substring(0, 159) + "...";
        }else this.truncatedComment = comment;
    }

    public String getTruncatedComment() {
        return truncatedComment;
    }

    public Boolean getReportingEnabled() {
        return reportingEnabled;
    }

    public void setReportingEnabled(Boolean reportingEnabled) {
        this.reportingEnabled = reportingEnabled;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

	public String getContentTypeCd() {
		return contentTypeCd;
	}

	public void setContentTypeCd(String contentTypeCd) {
		this.contentTypeCd = contentTypeCd;
	}

	public String getRegulatoryRequirementCd() {
		return regulatoryRequirementCd;
	}

	public void setRegulatoryRequirementCd(String regulatoryRequirementCd) {
		this.regulatoryRequirementCd = regulatoryRequirementCd;
	}

	public Integer getScEmissionsReportId() {
		return scEmissionsReportId;
	}

	public void setScEmissionsReportId(Integer scEmissionsReportId) {
		this.previousScEmissionsReportId = this.scEmissionsReportId;
		this.scEmissionsReportId = scEmissionsReportId;
	}
	
	public Integer getPreviousScEmissionsReportId() {
		return previousScEmissionsReportId;
	}

	public void setPreviousScEmissionsReportId(Integer previousScEmissionsReportId) {
		this.previousScEmissionsReportId = previousScEmissionsReportId;
	}

	public boolean isYearAndContentTypeMatch(EmissionsRptInfo info) {

		if ((Utility.isNullOrEmpty(this.getContentTypeCd()))
				|| (Utility.isNullOrEmpty(info.getContentTypeCd()))
				|| (Utility.isNullOrZero(this.getYear()))
				|| (Utility.isNullOrZero(info.getYear()))) {
			logger.info("EmissionsRptInfo: This should not happen");

		} else if ((this.getYear().equals(info.getYear()))
				&& this.getContentTypeCd().equals(info.getContentTypeCd())) {
			return true;
		}

		return false;
	}
}
