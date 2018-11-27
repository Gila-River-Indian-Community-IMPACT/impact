package us.oh.state.epa.stars2.database.dao.serviceCatalog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dao.ServiceCatalogDAO;
import us.oh.state.epa.stars2.database.dbObjects.emissionReport.StringContainer;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SccCode;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.Fee;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEUCategory;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCEmissionsReport;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCDataImportPollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCNonChargePollutant;
import us.oh.state.epa.stars2.database.dbObjects.serviceCatalog.SCPollutant;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Repository
public class ServiceCatalogSQLDAO extends AbstractDAO implements
        ServiceCatalogDAO {
    public final SCEmissionsReport[] retrieveEmissionsReports() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveEmissionsReports", true);

        ArrayList<SCEmissionsReport> tempArray = connHandler.retrieveArray(SCEmissionsReport.class);
        
        for (SCEmissionsReport r : tempArray) {
        	
        	// Permit Class (Facility Class)
    		r.setPermitClassCds(retrievePermitClassCds(r.getId()));
    		
    		// Facility Type
    		r.setFacilityTypeCds(retrieveFacilityTypeCds(r.getId()));
        	
        }

        return tempArray.toArray(new SCEmissionsReport[0]);
    }

    public final SCPollutant[] retrievePollutants(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retreivePollutants", true);

        connHandler.setInteger(1, reportId);

        ArrayList<SCPollutant> tempArray = connHandler.retrieveArray(SCPollutant.class);

        return tempArray.toArray(new SCPollutant[0]);
    }
    
    @Override
    public final SCNonChargePollutant[] retrieveNonChargePollutants(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveNonChargePollutants", true);

        connHandler.setInteger(1, reportId);

        ArrayList<SCNonChargePollutant> tempArray = connHandler.retrieveArray(SCNonChargePollutant.class);

        return tempArray.toArray(new SCNonChargePollutant[0]);
    }

    public final SCEmissionsReport retrieveEmissionsReport(int reportId) throws DAOException {
        SCEmissionsReport ret = null;

        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveEmissionsReport", true);

        connHandler.setInteger(1, reportId);

        ret = (SCEmissionsReport) connHandler.retrieve(SCEmissionsReport.class);

        ret.setFees(retrieveReportFees(ret.getId()));
        
        ret.setNcPollutants(retrieveNonChargePollutants(ret.getId()));
        
        List<SCDataImportPollutant> scDataImportPollutantList = new ArrayList<SCDataImportPollutant>(Arrays.asList(retrieveDataImportPollutants(ret.getId())));
        ret.setDataImportPollutantList(scDataImportPollutantList);
        
        /*
        ArrayList<StringContainer> strList = new ArrayList<StringContainer>();
        for(StringContainer sCont : retrieveReportExemptions(ret.getId())) {
            strList.add(sCont);
        }
        ret.setExemptions(strList);
        
        strList = new ArrayList<StringContainer>();
        for(StringContainer sCont : retrieveReportTvClassifications(ret.getId())) {
            strList.add(sCont);
        }
        ret.setTvClassifications(strList);
        */
		// Permit Class (Facility Class)
		ret.setPermitClassCds(retrievePermitClassCds(ret.getId()));
		
		// Facility Type
		ret.setFacilityTypeCds(retrieveFacilityTypeCds(ret.getId()));
        return ret;
    }

    @Override
    public final Fee[] retrieveReportFees(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveReportFees", true);

        connHandler.setInteger(1, reportId);

        ArrayList<Fee> tempArray = connHandler.retrieveArray(Fee.class);

        return tempArray.toArray(new Fee[0]);
    }
    
    public final StringContainer[] retrieveReportExemptions(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveReportExemptions", true);

        connHandler.setInteger(1, reportId);

        ArrayList<StringContainer> tempArray = connHandler.retrieveArray(StringContainer.class);

        return tempArray.toArray(new StringContainer[0]);
    }
    
    public final StringContainer[] retrieveReportTvClassifications(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveReportTvClassifications", true);

        connHandler.setInteger(1, reportId);

        ArrayList<StringContainer> tempArray = connHandler.retrieveArray(StringContainer.class);

        return tempArray.toArray(new StringContainer[0]);
    }

    public final SCEmissionsReport createEmissionsReport(SCEmissionsReport newReport) throws DAOException {
        SCEmissionsReport ret = newReport;

        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.createEmissionsReport", false);

        Integer id = nextSequenceVal("S_SC_Emissions_Report_Id");
        int i = 1;

        connHandler.setInteger(i++, id);
        connHandler.setInteger(i++, newReport.getWorkFlowTemplateId());
        connHandler.setString(i++, "tv");  // obsolete, not used. to be removed
        connHandler.setString(i++, newReport.getReportName());
        connHandler.setString(i++, newReport.getReportGroup());
        connHandler.setString(i++, newReport.getFeeType());
        connHandler.setInteger(i++, newReport.getReportingYear());
        connHandler.setTimestamp(i++, newReport.getEffectiveDate());
        connHandler.setInteger(i++, newReport.getReferenceYear());
        connHandler.setInteger(i++, newReport.getReportFrequencyYear());
        connHandler.setDouble(i++, newReport.getPollutantCap());
        connHandler.setBigDecimal(i++, newReport.getEiMinimumFee());
        connHandler.setString(i++, newReport.getContentTypeCd());
        connHandler.setString(i++, newReport.getRegulatoryRequirementCd());
        connHandler.setInteger(i++, newReport.getShapeId());
        connHandler.setString(i++, newReport.getTreatPartialAsFullPeriodFlag());

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setId(id);
        ret.setLastModified(1);

        return ret;
    }

    public final boolean modifyEmissionsReport(SCEmissionsReport report) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.modifyEmissionsReport", false);

        int i = 1;
        connHandler.setInteger(i++, report.getWorkFlowTemplateId());
        connHandler.setString(i++, report.getReportName());
        connHandler.setInteger(i++, report.getReportingYear());
        connHandler.setTimestamp(i++, report.getEffectiveDate());
        connHandler.setInteger(i++, report.getReferenceYear());
        connHandler.setInteger(i++, report.getReportFrequencyYear());
        connHandler.setDouble(i++, report.getPollutantCap());
        connHandler.setBigDecimal(i++, report.getEiMinimumFee());
        connHandler.setString(i++, report.getContentTypeCd());
        connHandler.setString(i++, report.getRegulatoryRequirementCd());
        connHandler.setInteger(i++, report.getShapeId());
        connHandler.setString(i++, report.getTreatPartialAsFullPeriodFlag());
        connHandler.setInteger(i++, report.getLastModified() + 1);
        connHandler.setInteger(i++, report.getId());
        connHandler.setInteger(i++, report.getLastModified());

        return connHandler.update();
    }

    public final Fee createFee(Fee newFee) throws DAOException {
        Fee ret = newFee;

        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.createFee", false);

        Integer id = nextSequenceVal("S_Fee_Id");

        connHandler.setInteger(1, id);
        connHandler.setString(2, newFee.getFeeNm());
        connHandler.setDouble(3, newFee.getAmount());
        connHandler.setTimestamp(4, newFee.getEffectiveDate());
        connHandler.setTimestamp(5, newFee.getEndDate());
        connHandler.setInteger(6, newFee.getLowRange());
        connHandler.setInteger(7, newFee.getHighRange());

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setFeeId(id);
        ret.setLastModified(1);

        return ret;
    }

    public final boolean modifyFee(Fee fee) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.modifyFee", false);

        connHandler.setString(1, fee.getFeeNm());
        connHandler.setDouble(2, fee.getAmount());
        connHandler.setTimestamp(3, fee.getEffectiveDate());
        connHandler.setTimestamp(4, fee.getEndDate());
        connHandler.setInteger(5, fee.getLowRange());
        connHandler.setInteger(6, fee.getHighRange());
        connHandler.setInteger(7, fee.getLastModified() + 1);
        connHandler.setInteger(8, fee.getFeeId());
        connHandler.setInteger(9, fee.getLastModified());

        return connHandler.update();
    }

    public final boolean addPollutantToEmissionsReport(SCPollutant pollutant) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.addPollutantToEmissionsReport", false);

        connHandler.setInteger(1, pollutant.getSCReportId());
        connHandler.setString(2, pollutant.getPollutantCd());
        connHandler.setInteger(3, pollutant.getDisplayOrder());
        connHandler.setFloat(4, pollutant.getThresholdQa());
        connHandler.setString(5, AbstractDAO.translateBooleanToIndicator(pollutant.isChargeable()));
        connHandler.setString(6, AbstractDAO.translateBooleanToIndicator(pollutant.isBilledBasedOnPermitted()));

        return connHandler.update();
    }
    
    @Override
    public final boolean addNonChargePollutantToEmissionsReport(SCNonChargePollutant ncPollutant) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.addNonChargePollutantToEmissionsReport", false);

        connHandler.setInteger(1, ncPollutant.getSCReportId());
        connHandler.setString(2, ncPollutant.getPollutantCd());
        if (ncPollutant.getSccCd() != null && ncPollutant.getSccCd().getSccId() != null) {
        	connHandler.setString(3, ncPollutant.getSccCd().getSccId());
        } else {
        	connHandler.setString(3, SccCode.DUMMY_SCC_ID);        	
        }
        connHandler.setString(4, AbstractDAO.translateBooleanToIndicator(ncPollutant.isFugitiveOnly()));

        return connHandler.update();
    }
    
    public final boolean addExemptionToEmissionsReport(Integer id, String exemption) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.addExemptionToEmissionsReport", false);

        connHandler.setInteger(1, id);
        connHandler.setString(2, exemption);
        
        return connHandler.update();
    }
    
    public final boolean addTvClassificationToEmissionsReport(Integer id, String tvClass) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.addTvClassificationToEmissionsReport", false);

        connHandler.setInteger(1, id);
        connHandler.setString(2, tvClass);
        
        return connHandler.update();
    }
    
    public final void removeEmissionsReport(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeEmissionsReport", false);

        connHandler.setInteger(1, reportId);

        connHandler.remove();
    }

    public final void removeEmissionsReportPollutants(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeEmissionsReportPollutants", false);

        connHandler.setInteger(1, reportId);

        connHandler.remove();
    }
    
    @Override
    public final void removeEmissionsReportNonChargePollutants(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeEmissionsReportNonChargeNonChargePollutants", false);

        connHandler.setInteger(1, reportId);

        connHandler.remove();
    }
    
    public final void removeEmissionsReportExemptions(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeEmissionsReportExemptions", false);

        connHandler.setInteger(1, reportId);

        connHandler.remove();
    }
    
    public final void removeEmissionsReportTvClassifications(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeEmissionsReportTvClassifications", false);

        connHandler.setInteger(1, reportId);

        connHandler.remove();
    }
    
    public final void removeFee(int feeId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeFee", false);

        connHandler.setInteger(1, feeId);

        connHandler.remove();
    }

    public final void removeEmissionsReportFees(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeEmissionsReportFees", false);

        connHandler.setInteger(1, reportId);

        connHandler.remove();
    }

    public final void removeEmissionsReportReminders(int reportId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeEmissionsReportReminders", false);

        connHandler.setInteger(1, reportId);

        connHandler.remove();
    }

    public final SimpleDef[] retrieveReportPollutants() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveReportPollutants", true);

        ArrayList<SimpleDef> tempArray = connHandler.retrieveArray(SimpleDef.class);

        return tempArray.toArray(new SimpleDef[0]);
    }

    public final boolean addFeeToEmissionsReport(int reportId, int feeId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.addFeeToEmissionsReport", false);

        connHandler.setInteger(1, reportId);
        connHandler.setInteger(2, feeId);

        return connHandler.update();
    }

    public final boolean addFeeToCategory(int categoryId, int feeId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.addFeeToCategory", false);

        connHandler.setInteger(1, categoryId);
        connHandler.setInteger(2, feeId);

        return connHandler.update();
    }

    public final SCEUCategory createSCEUCategory(SCEUCategory newCategory) throws DAOException {
        SCEUCategory ret = newCategory;

        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.createEUCategory", false);

        Integer id = nextSequenceVal("S_SC_EU_Category_Id");

        connHandler.setInteger(1, id);
        connHandler.setString(2, newCategory.getCategoryDsc());

        connHandler.update();

        // If we get here the INSERT must have succeeded, so set the important
        // data and return the object.
        ret.setCategoryId(id);
        ret.setLastModified(1);

        return ret;
    }

    public final boolean modifySCEUCategory(SCEUCategory category) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.modifyEUCategory", false);

        connHandler.setString(1, category.getCategoryDsc());
        connHandler.setInteger(2, category.getLastModified() + 1);
        connHandler.setInteger(3, category.getCategoryId());
        connHandler.setInteger(4, category.getLastModified());

        return connHandler.update();
    }

    public final SCEUCategory[] retrieveEUCategories() throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveEUCategories", true);

        ArrayList<SCEUCategory> tempArray = connHandler.retrieveArray(SCEUCategory.class);

        return tempArray.toArray(new SCEUCategory[0]);
    }

    public final SCEUCategory retrieveEUCategory(int categoryId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveEUCategory", false);

        connHandler.setInteger(1, categoryId);

        return (SCEUCategory) connHandler.retrieve(SCEUCategory.class);
    }

    public final Fee retrieveFee(Integer feeId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveFee", false);

        connHandler.setInteger(1, feeId);

        return (Fee) connHandler.retrieve(Fee.class);
    }

    public final SCEUCategory retrieveCategoryByFeeId(int feeId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveCategoryByFeeId", false);

        connHandler.setInteger(1, feeId);

        return (SCEUCategory) connHandler.retrieve(SCEUCategory.class);
    }

    public final void removeSCCategoryFees(int categoryId) throws DAOException {
        ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.removeCategoryFees", false);

        connHandler.setInteger(1, categoryId);

        connHandler.remove();
    }
    
    @Override
	public List<String> retrievePermitClassCds(Integer reportId)
			throws DAOException {
		checkNull(reportId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
 				"ServiceCatalogSQL.retrievePermitClassCds", true);
		
		connHandler.setInteger(1, reportId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}
    
    @Override
	public List<String> createPermitClassXrefs(Integer reportId,
			List<String> permitClassCds) throws DAOException {
		checkNull(reportId);
		checkNull(permitClassCds);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ServiceCatalogSQL.createPermitClassXrefs", true);
		
		connHandler.setInteger(1, reportId);
		try{
			for(String code: permitClassCds) {
				connHandler.setString(2, code);
				connHandler.updateNoClose();
			}
		}finally {
			connHandler.close();
		}
		
		return permitClassCds;
	}
    
    @Override
	public void deletePermitClassXrefs(Integer reportId)
			throws DAOException {
		checkNull(reportId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ServiceCatalogSQL.deletePermitClassXrefs", true);
		
		connHandler.setInteger(1, reportId);
		
		connHandler.remove();
	}
	
	@Override
	public List<String> retrieveFacilityTypeCds(Integer reportId)
			throws DAOException {
		checkNull(reportId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
 				"ServiceCatalogSQL.retrieveFacilityTypeCds", true);
		
		connHandler.setInteger(1, reportId);
		
		return connHandler.retrieveJavaObjectArray(String.class);
	}
    
    @Override
	public List<String> createFacilityTypeXrefs(Integer reportId,
			List<String> facilityTypeCds) throws DAOException {
		checkNull(reportId);
		checkNull(facilityTypeCds);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ServiceCatalogSQL.createFacilityTypeXrefs", true);
		
		connHandler.setInteger(1, reportId);
		try{
			for(String code: facilityTypeCds) {
				connHandler.setString(2, code);
				connHandler.updateNoClose();
			}
		}finally {
			connHandler.close();
		}
		
		return facilityTypeCds;
	}
    
    @Override
	public void deleteFacilityTypeXrefs(Integer reportId)
			throws DAOException {
		checkNull(reportId);
		
		ConnectionHandler connHandler = new ConnectionHandler(
				"ServiceCatalogSQL.deleteFacilityTypeXrefs", true);
		
		connHandler.setInteger(1, reportId);
		
		connHandler.remove();
	}
    
    @Override
	public boolean okToSaveServiceCatalogTemplate(SCEmissionsReport newReport)
			throws DAOException {
    	boolean ret = false;
		checkNull(newReport);
		Integer reportingYear = newReport.getReportingYear();
		String contentTypeCd = newReport.getContentTypeCd();
		String regulatoryRequirementCd = newReport.getRegulatoryRequirementCd();
		ConnectionHandler connHandler = new ConnectionHandler(
				"ServiceCatalogSQL.okToSaveServiceCatalogTemplate", false);
		connHandler.setInteger(1, reportingYear);
		connHandler.setString(2, contentTypeCd);
		connHandler.setString(3, regulatoryRequirementCd);
		
		Integer id = (Integer) connHandler.retrieveJavaObject(Integer.class);
		
		// ok to save if no template exists with combination of values
		if (id == null) { 
			ret = true;
		// ok to save if the template with combination of values
		// is the one being saved.
		} else if (newReport.getId() != null && newReport.getId().equals(id)) {
			ret = true;
		}
        return ret;
	}
    
	public final Integer retrieveSCEmissionsReportId(Integer reportingYear,
			String contentTypeCd, String regulatoryRequirementCd)
			throws DAOException {
		Integer ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(false);

		connHandler
				.setSQLString("ServiceCatalogSQL.retrieveSCEmissionsReportId");
		int i = 1;
		connHandler.setInteger(i++, reportingYear);
		connHandler.setString(i++, contentTypeCd);
		connHandler.setString(i++, regulatoryRequirementCd);

		ret = (Integer) connHandler.retrieveJavaObject(Integer.class);

		return ret;
	}
	
	public final Integer retrieveHighestPriorityRptReportId(Integer reportingYear,
			String contentTypeCd, String facilityId)
			throws DAOException {
		Integer ret = null;

		ConnectionHandler connHandler = new ConnectionHandler(false);

		connHandler
				.setSQLString("ServiceCatalogSQL.retrieveHighestPriorityRptReportId");
		int i = 1;
		connHandler.setString(i++, facilityId);
		connHandler.setInteger(i++, reportingYear);
		connHandler.setString(i++, contentTypeCd);

		ret = (Integer) connHandler.retrieveJavaObject(Integer.class);

		return ret;
	}
	
	/**
	 * @see ServiceCatalogDAO#addDataImportPollutantToEmissionsReport(SCDataImportPollutant)
	 */
	@Override
	public SCDataImportPollutant addDataImportPollutantToEmissionsReport(SCDataImportPollutant scDataImportPollutant)
			throws DAOException {
		checkNull(scDataImportPollutant);
		checkNull(scDataImportPollutant.getSCReportId());
		checkNull(scDataImportPollutant.getPollutantCd());
		checkNull(scDataImportPollutant.getSortOrder());

		ConnectionHandler connHandler = new ConnectionHandler(
				"ServiceCatalogSQL.addDataImportPollutantToEmissionsReport", true);

		int i = 1;

		connHandler.setInteger(i++, scDataImportPollutant.getSCReportId());
		connHandler.setString(i++, scDataImportPollutant.getPollutantCd());
		connHandler.setString(i++, scDataImportPollutant.getSortOrder());

		connHandler.update();

		return scDataImportPollutant;
	}

	/**
	 * @see ServiceCatalogDAO#removeEmissionsReportDataImportPollutants(int)
	 */
	@Override
	public void removeEmissionsReportDataImportPollutants(int reportId) throws DAOException {
		checkNull(reportId);

		ConnectionHandler connHandler = new ConnectionHandler(
				"ServiceCatalogSQL.removeEmissionsReportDataImportPollutants", true);

		connHandler.setInteger(1, reportId);

		connHandler.remove();
	}

	/**
	 * @see ServiceCatalogDAO#retrieveDataImportPollutants(int)
	 */
	@Override
	public SCDataImportPollutant[] retrieveDataImportPollutants(int reportId) throws DAOException {
		checkNull(reportId);

		ConnectionHandler connHandler = new ConnectionHandler("ServiceCatalogSQL.retrieveDataImportPollutants", true);

		connHandler.setInteger(1, reportId);

		return connHandler.retrieveArray(SCDataImportPollutant.class).toArray(new SCDataImportPollutant[0]);
	}

}
