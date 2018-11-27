package us.oh.state.epa.stars2.scheduler.jobs;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import us.oh.state.epa.stars2.database.dao.FacilityDAO;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.facility.FacilityList;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.def.County;
import us.oh.state.epa.stars2.def.OperatingStatusDef;
import us.oh.state.epa.stars2.framework.exception.DAOException;

@Component
public class ExportCETADataJob implements Job  {
    private transient Logger logger;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
   
    @Resource private FacilityDAO readOnlyFacilityDAO;
    
    public ExportCETADataJob() {
        logger = Logger.getLogger(this.getClass());
        logger.debug("ExportCETADataJob constructed");
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {        
        logger.debug("ExportCETADataJob is executing.");

        JobDataMap dataMap = context.getMergedJobDataMap();

        try {
            String fileName = (String)dataMap.get("fileName");
            String facilityId = (String)dataMap.get("facilityId");
            writeReportFile(fileName, facilityId);
        }
        catch (Exception e) {
            logger.error("ExportCETADataJob failed. ", e);
            throw new JobExecutionException("ExportCETADataJob failed. " 
                                            + e.getMessage(), e, false);
        }
        logger.debug("ExportCETADataJob has completed.");

    }
    
    private String buildExportString(Facility facility) {
        String ret = null;
        
        if (facility != null) {
            StringBuffer outBuffer = new StringBuffer();
            
            outBuffer.append(facility.getFacilityId() + "\t");
            StringBuffer sicCodes = new StringBuffer();
            for (String sicCode : facility.getSicCds()) {
                if (sicCodes.length() > 0) {
                    sicCodes.append(",");
                }
                sicCodes.append(sicCode);
            }
            outBuffer.append(sicCodes.toString() + "\t");
            
            StringBuffer naicsCodes = new StringBuffer();
            for (String naicsCode : facility.getNaicsCds()) {
                if (naicsCodes.length() > 0) {
                    naicsCodes.append(",");
                }
                naicsCodes.append(naicsCode);
            }
            outBuffer.append(naicsCodes.toString() + "\t");
            
            outBuffer.append(facility.getName() + "\t");

            if (facility.getStartDate() != null) {
                outBuffer.append(dateFormat.format(facility.getStartDate()));
            }
            outBuffer.append("\t");
            
            if (OperatingStatusDef.SD.equals(facility.getOperatingStatusCd())) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            if (facility.getShutdownDate() != null) {
                outBuffer.append(dateFormat.format(facility.getShutdownDate()));
            }
            outBuffer.append("\t");
            
            StringBuffer addressBuffer = new StringBuffer();
            for (Address address : facility.getAddressesList()) {
                if (address.getEndDate() == null) {
                    appendField(addressBuffer,address.getAddressLine1());
                    appendField(addressBuffer,address.getAddressLine2());
                    appendField(addressBuffer,address.getCityName());
                    appendField(addressBuffer,address.getState());
                    appendField(addressBuffer,address.getZipCode5());
                    appendField(addressBuffer,address.getZipCode4());
                    appendField(addressBuffer,address.getCountyCd());
                    String countyNm = null;
                    if (address.getCountyCd() != null) {
                        countyNm = County.getData().getItems().getItemDesc(address.getCountyCd());
                    }
                    appendField(addressBuffer,countyNm);
                    break;
                }
            }
            if (addressBuffer.length() == 0) {
                addressBuffer.append("\t\t\t\t\t\t\t\t");
            }
            outBuffer.append(addressBuffer);
            
            StringBuffer contactBuffer = new StringBuffer();
            Contact primaryContact = facility.getPrimaryContact();
            if (primaryContact != null) {
                if (primaryContact.getFirstNm() == null && primaryContact.getLastNm() == null) {
                    if (primaryContact.getCompanyName() != null) {
                        contactBuffer.append(primaryContact.getCompanyName());
                    }
                } else {
                    if (primaryContact.getFirstNm() != null) {
                        contactBuffer.append(primaryContact.getFirstNm());
                    }
                    if (primaryContact.getLastNm() != null) {
                        if (primaryContact.getFirstNm() != null) {
                            contactBuffer.append(" ");
                        }
                        contactBuffer.append(primaryContact.getLastNm());
                    }
                }
                contactBuffer.append("\t");
                appendField(contactBuffer, primaryContact.getPhoneNo());
                appendField(contactBuffer, primaryContact.getEmailAddressTxt());
                appendField(contactBuffer, primaryContact.getEmailAddressTxt2());
                Address address = primaryContact.getAddress();
                if (address != null) {
                    addressBuffer = new StringBuffer();
                    if (address.getEndDate() == null) {
                        appendField(addressBuffer, address.getAddressLine1());
                        appendField(addressBuffer, address.getAddressLine2());
                        appendField(addressBuffer, address.getCityName());
                        appendField(addressBuffer, address.getState());
                        appendField(addressBuffer, address.getZipCode5());
                        appendField(addressBuffer, address.getZipCode4());
                    }
                }
                if (addressBuffer.length() == 0) {
                    addressBuffer.append("\t\t\t\t\t\t");
                }
                contactBuffer.append(addressBuffer);
                
            } else {
                contactBuffer.append("\t\t\t\t\t\t\t\t\t");
            }
            outBuffer.append(contactBuffer);
            
            outBuffer.append(facility.getPermitClassCd() + "\t");
            
            // HPF  ‘1’ or ‘0’  (= ‘1’ if facility class code = ‘tv’ or ‘smtv’)
            if ("tv".equals(facility.getPermitClassCd()) || "smtv".equals(facility.getPermitClassCd())) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // TV  ‘1’ or ‘0’  (= ‘1’ if facility class code = ‘tv’)
            if ("tv".equals(facility.getPermitClassCd())) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // SMTV    ‘1’ or ‘0’  (= ‘1’ if facility class code = ‘smtv’)
            if ("smtv".equals(facility.getPermitClassCd())) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // NON-HPV ‘1’ or ‘0’  (= ‘1’ if facility class code <> ‘tv’ or ‘smtv’)
            if (!"tv".equals(facility.getPermitClassCd()) && !"smtv".equals(facility.getPermitClassCd())) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // GDF ‘1’ or ‘0’  (=’1’ if SIC = ‘5541’)
            boolean isGDF = false;
            for (String sic : facility.getSicCds()) {
                if ("5541".equals(sic)) {
                    isGDF = true;
                    break;
                }
            }
            if (isGDF) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // Part 63 NESHAP    ‘1’ or ‘0’
            if (facility.isMact()) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // Part 61 NESHAP  ‘1’ or ‘0’
            if (facility.isNeshaps()) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // Part 60 NSPS    ‘1’ or ‘0’
            if (facility.isNsps()) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // PSD ‘1’ or ‘0’
            if (facility.isPsd()) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            // NSR ‘1’ or ‘0’
            if (facility.isNsrNonattainment()) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }

            // NESHAP
            if (facility.getNeshapsSubparts().contains("N")) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            if (facility.getNeshapsSubparts().contains("M")) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            if (facility.getNeshapsSubparts().contains("C")) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            if (facility.getNeshapsSubparts().contains("FF")) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            if (facility.getNeshapsSubparts().contains("L")) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            if (facility.getNeshapsSubparts().contains("H")) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            if (facility.getNeshapsSubparts().contains("F")) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }
            
            if (facility.getNeshapsSubparts().contains("E")) {
                outBuffer.append("1\t");
            } else {
                outBuffer.append("0\t");
            }

            for (int i=0; i<9; i++) {
                if (facility.getNspsSubparts().size() > i) {
                    outBuffer.append(facility.getNspsSubparts().get(i));
                }
                outBuffer.append("\t");
            }

            for (int i=0; i<9; i++) {
                if (facility.getMactSubparts().size() > i) {
                    outBuffer.append(facility.getMactSubparts().get(i));
                }
                outBuffer.append("\t");
            }
            // get rid of extra tab
            outBuffer.deleteCharAt(outBuffer.length() - 1);
            outBuffer.append("\n");
                        
            ret = outBuffer.toString();
        }
        
        return ret;
    }
    
    private void addPollutantInfo(List<String> pollutantList, StringBuffer outBuffer) {
        // SO2
        if (pollutantList.contains("SO2")) {
            outBuffer.append("1\t");
        } else {
            outBuffer.append("0\t");
        }
        // NOx
        if (pollutantList.contains("NOX")) {
            outBuffer.append("1\t");
        } else {
            outBuffer.append("0\t");
        }
        // CO
        if (pollutantList.contains("CO")) {
            outBuffer.append("1\t");
        } else {
            outBuffer.append("0\t");
        }
        // Pb (7439921)
        if (pollutantList.contains("7439921")) {
            outBuffer.append("1\t");
        } else {
            outBuffer.append("0\t");
        }
        // PM (PM-FIL)
        if (pollutantList.contains("PM-FIL")) {
            outBuffer.append("1\t");
        } else {
            outBuffer.append("0\t");
        }
        // VOC
        if (pollutantList.contains("VOC")) {
            outBuffer.append("1\t");
        } else {
            outBuffer.append("0\t");
        }
    }
    
    private void appendField(StringBuffer sb, String s) {
        if (s != null) {
            sb.append(s + "\t");
        } else {
            sb.append("\t");
        }
    }
    
    public void writeReportFile(String fileName, String facilityId) {
//        FacilityDAO facDAO = (FacilityDAO)DAOFactory.getDAO("FacilityDAO");
        ArrayList<Facility> facilities = new ArrayList<Facility>();
        
        try {
            FacilityList[] facilityList = readOnlyFacilityDAO.searchFacilities(null, facilityId, null, null, null, 
                   null, null, null, null, null, null, null, null, null, null, true, null);
            for (FacilityList facilityListItem : facilityList) {
                Facility facility = readOnlyFacilityDAO.retrieveFacility(facilityListItem.getFpId());
                if (facility != null) {
                    facilities.add(facility);
                }
            }
        } catch (DAOException de) {
            logger.error(de.getMessage(), de);
        }
        
        if (facilities.size() > 0) {
            FileWriter outFile = null;

            try {
                outFile = new FileWriter(fileName);
                logger.debug("Writing CETA Report to file: " + fileName);
                logger.debug("Processing " + facilities.size() + " Facilities...");

                for (Facility facility : facilities) {
                    String outString = buildExportString(facility);

                    if (outString != null) {
                        outFile.write(outString);
                    }
                }
                
                outFile.close();
                logger.debug("Done writing file");
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            }
        } else {
            logger.error("No facility data to export!");
        }
    }

    /**
     * For testing only
     * @param args
     */
    public static final void main(String[] args) {
        ExportCETADataJob job = new ExportCETADataJob();
        job.writeReportFile("C:\\projects\\CETAReport.txt", "0125*");
    }
}
