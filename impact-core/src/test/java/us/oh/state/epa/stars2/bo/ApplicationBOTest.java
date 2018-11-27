package us.oh.state.epa.stars2.bo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.faces.model.SelectItem;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import us.oh.state.epa.stars2.database.dao.AbstractDAO;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocument;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationDocumentRef;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationNote;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.TVAltScenarioPteReq;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicableReq;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplicationEUEmissions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVCompliance;
import us.oh.state.epa.stars2.database.dbObjects.application.TVComplianceObligations;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUGroup;
import us.oh.state.epa.stars2.database.dbObjects.application.TVEUOperatingScenario;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedAltLimits;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedExemptions;
import us.oh.state.epa.stars2.database.dbObjects.application.TVProposedTestChanges;
import us.oh.state.epa.stars2.database.dbObjects.application.TVPteAdjustment;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Address;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.Contact;
import us.oh.state.epa.stars2.database.dbObjects.infrastructure.SimpleDef;
import us.oh.state.epa.stars2.def.ApplicationDocumentTypeDef;
import us.oh.state.epa.stars2.def.ApplicationEUEmissionTableDef;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.NoteType;
import us.oh.state.epa.stars2.def.PTIOMACTSubpartDef;
import us.oh.state.epa.stars2.def.PTIONESHAPSSubpartDef;
import us.oh.state.epa.stars2.def.PTIONSPSSubpartDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.TVApplicationPurposeDef;
import us.oh.state.epa.stars2.def.TVClassification;
import us.oh.state.epa.stars2.def.TVCompRptFreqDef;
import us.oh.state.epa.stars2.def.TVIeuReasonDef;
import us.oh.state.epa.stars2.def.TVRuleCiteTypeDef;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;

public class ApplicationBOTest {

    // NOTE: test relies on these facility ids matching valid facilities in the DB
    private final String testPTIOFacilityID = "0124000115";
    private final String testTVFacilityID = "0204010173";
    private final DecimalFormat decimalFormat = new DecimalFormat("###.##");
    private boolean keepPtioApp;
    private boolean keepTvApp;
    private List<String> hapCodes;
    private List<String> reportFreqCodes;
    

    private static List<String> ieuReasonCodes = new ArrayList<String>();
    static {
        for (SelectItem item : TVIeuReasonDef.getData().getItems().getItems("")) {
            ieuReasonCodes.add(item.getValue().toString());
        }
    }
    private static List<String> mactSubpartList = new ArrayList<String>();
    static {
        for (SelectItem item : PTIOMACTSubpartDef.getData().getItems().getItems("")) {
            mactSubpartList.add(item.getValue().toString());
        }
    }
    private static List<String> neshapSubpartList = new ArrayList<String>();
    static {
        for (SelectItem item : PTIONESHAPSSubpartDef.getData().getItems().getItems("")) {
            neshapSubpartList.add(item.getValue().toString());
        }
    }
    private static List<String> nspsSubpartList = new ArrayList<String>();
    static {
        for (SelectItem item : PTIONSPSSubpartDef.getData().getItems().getItems("")) {
            nspsSubpartList.add(item.getValue().toString());
        }
    }
    
    @Before
    public void setUp() throws Exception {
        // don't remove generated app if keep flag is set
        String appsToKeep = System.getProperty("keep");
        if (appsToKeep != null) {
            keepPtioApp = appsToKeep.contains("p");
            keepTvApp = appsToKeep.contains("t");
        }
    }

    @Test
    @Ignore
    public void testAppBOSanity() {
        boolean ok = true;
        ApplicationService appBO = null;
        FacilityService fpBO = null;
        try {
            appBO = ServiceFactory.getInstance().getApplicationService();
            fpBO = ServiceFactory.getInstance().getFacilityService();
        } catch (Exception e) {
            ok = false;
            e.printStackTrace();
            fail("Exception initializing BOs");
        }
        if (ok) {
            // test PTIO
            System.out.println("PTIO Tests...");
            PTIOApplication ptioApp = createPTIOApp(appBO, fpBO);
            assertNotNull(ptioApp);
            addPTIOEmissionsInfo(appBO, ptioApp);
            addDocsToEUs(appBO, ptioApp);
            addNotes(appBO, ptioApp);
            Application copy = ptioAppCopyTest(appBO, ptioApp);
            removeApp(appBO, copy);
            euCopyDataTest(appBO, ptioApp);
            if (!keepPtioApp) {
                System.out.println("*** deleting application " + ptioApp.getApplicationNumber());
                removeApp(appBO,ptioApp);
            }
            System.out.println("PTIO Tests Done");
            
            // test TV
            System.out.println("RV Tests ...");
            TVApplication tvApp = createTVApp(appBO, fpBO);
            assertNotNull(tvApp);
            tvApp = (TVApplication)addDocsToEUs(appBO, tvApp);
            tvApp = addTVEUInfo(appBO, tvApp);
            tvApp = addTVScenarios(appBO, tvApp);
            tvApp = addTVEUGroups(appBO, tvApp);
            tvApp = adjustPteTotals(appBO, tvApp);
            tvApp = addApplicableReqs(appBO, tvApp);
            tvApp = (TVApplication)addNotes(appBO, tvApp);
            copy = tvAppCopyTest(appBO, tvApp);
            removeApp(appBO, copy);
            euCopyDataTest(appBO, tvApp);
            if (!keepTvApp) {
              removeApp(appBO,tvApp);
            }
            System.out.println("RV Tests Done");
        }
    }
    
    private TVApplication addTVEUInfo(ApplicationService appBO, TVApplication tvApp) {
        System.out.println("addTVEUInfo...");
        Random random = new Random(System.currentTimeMillis());
        for (ApplicationEU eu : tvApp.getIncludedEus()) {
            TVApplicationEU tvEu = (TVApplicationEU)eu;
            tvEu.setMonitorReq(AbstractDAO.translateBooleanToIndicator(random.nextBoolean()));
            if (tvEu.isMonitorRequired()) {
                tvEu.setMonitorReqDsc("UNIT TEST: Description of compliance " +
                       "monitoring equipment activities or enhanced monitoring");
            }
            tvEu.setComplyWEnhMonitor(AbstractDAO.translateBooleanToIndicator(random.nextBoolean()));
            String tvClassCd = tvEu.getFpEU().getTvClassCd();
            if (tvClassCd != null && tvClassCd.equals(TVClassification.INSIG)) {
                Collections.shuffle(ieuReasonCodes);
                tvEu.setTvIeuReasonCd(ieuReasonCodes.get(0));
            }
        }
        try {
            if (appBO.modifyApplication(tvApp, true)) {
                tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("addTVEUInfo Done");
        return tvApp;
    }

    private TVApplication adjustPteTotals(ApplicationService appBO, TVApplication tvApp) {
        System.out.println("adjustPteTotals...");
        Random random = new Random(System.currentTimeMillis());
        try {
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
        } catch (Exception e2) {
            e2.printStackTrace();
            fail("Exception retrieving application");
        }
        try {
        for (TVPteAdjustment adj : tvApp.getCapPteTotals()) {
            if (random.nextInt(4) == 1) {
                double amount = random.nextDouble() * 5.0;
                adj.setPteAdjusted(appBO.getEmissionValueAsString(
                        adj.getPollutantCd(), adj.getEuEmissionTableCd(), new Float(amount)));
            }
        }
        for (TVPteAdjustment adj : tvApp.getHapPteTotals()) {
            if (random.nextInt(4) == 1) {
                double amount = random.nextDouble() * 5.0;
                adj.setPteAdjusted(appBO.getEmissionValueAsString(
                        adj.getPollutantCd(), adj.getEuEmissionTableCd(), new Float(amount)));
            }
        }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            appBO.modifyApplication(tvApp, true);
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception updating application");
        }
        System.out.println("adjustPteTotals Done");
        return tvApp;
    }

    private PTIOApplication createPTIOApp(ApplicationService appBO, FacilityService fpBO) {
        System.out.println("createPTIOApp...");
        PTIOApplication ptioApp = null;
        try {
            Random random = new Random(System.currentTimeMillis());
            Facility facility = fpBO.retrieveFacility(testPTIOFacilityID);
            ptioApp = new PTIOApplication();
            ptioApp.setFacility(facility);
            addContact(ptioApp);
            
            Timestamp receivedDate = new Timestamp(System.currentTimeMillis());
            ptioApp.setReceivedDate(receivedDate);
            
            ptioApp.setLegacyStatePTOApp(random.nextInt(3) == 1);
            
            ptioApp.setApplicationDesc("UNIT TEST DESCRIPTION FOR PTIO APPLICATION");
            
            Integer dueDateCd = random.nextInt(5);
            ptioApp.setRequestedPERDueDateCD(dueDateCd.toString());
            
            StringBuffer exemptExplanation = new StringBuffer();
            
            Integer nspsCd = random.nextInt(5);
            if (nspsCd == 2) nspsCd = 3;    // 2 is not a vaild code for NSPS
            ptioApp.setNspsApplicableFlag(nspsCd.toString());
            if (nspsCd == 3 || nspsCd == 4) {
                if (nspsCd == 4) {
                    exemptExplanation.append("UNIT TEST EXPLANATION FOR NSPS EXEMPTION. ");
                }
                Collections.shuffle(nspsSubpartList);
                List<String> nspsSubpartCodes = new ArrayList<String>();
                for (int i=0; i<random.nextInt(3) + 1; i++) {
                    nspsSubpartCodes.add(nspsSubpartList.get(i));
                }
                ptioApp.setNspsSubpartCodes(nspsSubpartCodes);
            }

            Integer neshapCd = random.nextInt(5);
            if (neshapCd == 2) neshapCd = 3;    // 2 is not a vaild code for NESHAP
            ptioApp.setNeshapApplicableFlag(neshapCd.toString());
            if (neshapCd == 3 || neshapCd == 4) {
                if (neshapCd == 4) {
                    exemptExplanation.append("UNIT TEST EXPLANATION FOR NESHAP EXEMPTION. ");
                }
                Collections.shuffle(neshapSubpartList);
                List<String> neshapSubpartCodes = new ArrayList<String>();
                for (int i=0; i<random.nextInt(3) + 1; i++) {
                    neshapSubpartCodes.add(neshapSubpartList.get(i));
                }
                ptioApp.setNeshapSubpartCodes(neshapSubpartCodes);
            }
            
            Integer mactCd = random.nextInt(5);
            if (mactCd == 2) mactCd = 3;    // 2 is not a vaild code for MACT
            ptioApp.setMactApplicableFlag(mactCd.toString());
            if (mactCd == 3 || mactCd == 4) {
                if (mactCd == 4) {
                    exemptExplanation.append("UNIT TEST EXPLANATION FOR MACT EXEMPTION. ");
                }
                Collections.shuffle(mactSubpartList);
                List<String> mactSubpartCodes = new ArrayList<String>();
                for (int i=0; i<random.nextInt(3) + 1; i++) {
                    mactSubpartCodes.add(mactSubpartList.get(i));
                }
                ptioApp.setMactSubpartCodes(mactSubpartCodes);
            }
            
            if (exemptExplanation.length() > 0) {
                ptioApp.setFederalRuleApplicabilityExplanation(exemptExplanation.toString());
            }

            Integer psdApplicableFlag = random.nextInt(3);
            ptioApp.setPsdApplicableFlag(psdApplicableFlag.toString());
            Integer nsrApplicableFlag = random.nextInt(3);
            ptioApp.setNsrApplicableFlag(nsrApplicableFlag.toString());
            Integer riskManagementPlanFlag = random.nextInt(3);
            ptioApp.setRiskManagementPlanFlag(riskManagementPlanFlag.toString());
            Integer titleIVFlag = random.nextInt(3);
            ptioApp.setTitleIVFlag(titleIVFlag.toString());
            
            ptioApp.setQualifyExpress(random.nextBoolean());
            if (ptioApp.isQualifyExpress()) {
                ptioApp.setRequestExpress(random.nextBoolean());
            }
            
            ptioApp = (PTIOApplication)appBO.createApplication(ptioApp);
            
            // add emission units to the application
            for (ApplicationEU eu : ptioApp.getEus()) {
                if (random.nextBoolean()) {
                    // just include a sampling of EUs for PTIO app
                    eu.setExcluded(true);
                    continue;
                }
                PTIOApplicationEU aeu = (PTIOApplicationEU)eu;
                aeu.setExcluded(false);
                aeu.setApplicationId(ptioApp.getApplicationID());
                aeu.setRequestingFederalLimitsFlag("N");
                
                Integer ptioEUPurposeCD = random.nextInt(7); // don't set general permit (7) here
                aeu.setPtioEUPurposeCD(ptioEUPurposeCD.toString());
                
                switch (ptioEUPurposeCD) {
                case 0: // New Installation
                    aeu.setWorkStartAfterPermit(random.nextBoolean());
                    if (!aeu.isWorkStartAfterPermit()) {
                        aeu.setWorkStartDate(new Timestamp(System.currentTimeMillis()));
                    }
                    break;
                case 1: // Inital application
                    aeu.setWorkStartDate(new Timestamp(System.currentTimeMillis()));
                    aeu.setOperationBeginDate(new Timestamp(System.currentTimeMillis()));
                    break;
                case 2: // Modification (not yet begun)
                    aeu.setModificationDesc("UNIT TEST DESCRIPTION OF MODIFICATION");
                    aeu.setWorkStartAfterPermit(random.nextBoolean());
                    if (!aeu.isWorkStartAfterPermit()) {
                        aeu.setWorkStartDate(new Timestamp(System.currentTimeMillis()));
                    }
                    break;
                case 3: // Modification (currently being modified)
                    aeu.setModificationDesc("UNIT TEST DESCRIPTION OF MODIFICATION");
                    aeu.setWorkStartDate(new Timestamp(System.currentTimeMillis()));
                    aeu.setOperationBeginDate(new Timestamp(System.currentTimeMillis()));
                    break;
                case 4: // reconstruction
                    aeu.setReconstructionDesc("UNIT TEST DESCRIPTION OF RECONSTRUCTION");
                    break;
                case 5: // renewal
                    aeu.setOperationBeginDate(new Timestamp(System.currentTimeMillis()));
                    break;
                case 6: // other
                    aeu.setModificationDesc("UNIT TEST DESCRIPTION OF OTHER");
                }
                
                aeu.setGeneralPermit(random.nextBoolean());
                /* General Permit not valid for WY
                if (aeu.isGeneralPermit()) {
                    Integer generalPermitTypeCd = random.nextInt(7);
                    aeu.setGeneralPermitTypeCd(generalPermitTypeCd.toString());
                    aeu.setModelGeneralPermitCd(getRandomModelGeneralPermitCd(aeu.getGeneralPermitTypeCd()));
                } */
                
                aeu.setBactFlag(null);
               
                switch (random.nextInt(3)) {
                case 0:
                    aeu.setRequestingFederalLimitsFlag("Y");
                    break;
                case 1:
                    aeu.setRequestingFederalLimitsFlag("N");
                    break;
                case 2:
                    aeu.setRequestingFederalLimitsFlag("U");
                    break;
                }
                if (aeu.getRequestingFederalLimitsFlag().equals("Y")) {
                    // init federal limits choices
                    List<String> choiceCodes = new ArrayList<String>();
                    for (int i=0; i<7; i++) {
                        choiceCodes.add(String.valueOf(i));
                    }
                    Collections.shuffle(choiceCodes);
                    
                    List<String> limitReasons = new ArrayList<String>();
                    int count = random.nextInt(3) + 1;
                    for (int i=0; i<count; i++) {
                        limitReasons.add(choiceCodes.get(i));
                    }
                    aeu.setFederalLimitsReasonCDs(limitReasons);
                    
                    if (limitReasons.contains("6")) {   // 6 == Other
                        aeu.setFederalLimitsOtherReasonDesc("UNIT TES DESCRIPTION OF OTHER FEDERAL LIMIT REASON");
                    }
                }
            }
            appBO.modifyApplication(ptioApp, true);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed creating PTIO application");
        }
        System.out.println("createPTIOApp Done");
        return ptioApp;
    }
    
    private String getRandomModelGeneralPermitCd(String permitTypeCd) {
        PermitService permitBO = null;
        String code = null;
        try {
            List<String> codeList = new ArrayList<String>();
            permitBO = ServiceFactory.getInstance().getPermitService();
            SimpleDef[] defs = permitBO.retrieveModelGeneralPermitDefs(
                    permitTypeCd);
            for (SimpleDef def : defs) {
                codeList.add(def.getCode());
            }
            if (codeList.size() > 0) {
                Collections.shuffle(codeList);
                code = codeList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception initializing Permit BO");
        }
        return code;
    }
    
    private TVApplication createTVApp(ApplicationService appBO, FacilityService fpBO) {
        TVApplication tvApp = null;
        System.out.println("createTVApp...");
        try {
            Facility facility = fpBO.retrieveFacility(testTVFacilityID);
            tvApp = new TVApplication();
            tvApp.setFacility(facility);
            
            String purposeCd = null;
            String permitReasonCd = null;
            Random random = new Random(System.currentTimeMillis());
            switch (random.nextInt(3)) {
            case 0:
                purposeCd = TVApplicationPurposeDef.INITIAL_APPLICATION;
                break;
            case 1:
                purposeCd = TVApplicationPurposeDef.RENEWAL;
                break;
            case 2:
                purposeCd = TVApplicationPurposeDef.REVISION_MODIFICATION_REOPENING;
                permitReasonCd = PermitReasonsDef.MPM;
                break;
            }
            tvApp.setTvApplicationPurposeCd(purposeCd);
            tvApp.setPermitReasonCd(permitReasonCd);
            tvApp.setApplicationDesc("UNIT TEST APPLICATION DESCRIPTION");
            tvApp.setOperationsDsc("UNIT TEST OPERATIONS DESCRIPTION");
            
            tvApp.setSubjectTo112R("Y");
            tvApp.setPlanSubmittedUnder112R("N");
            tvApp.setSubjectToTIV("Y");

            addContact(tvApp);
            
            tvApp = (TVApplication)appBO.createApplication(tvApp);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed creating PTIO application");
        }
        System.out.println("createTVApp done");
        return tvApp;
    }
    
    private void addContact(Application app) {
        System.out.println("addContact...");
        Contact contact = new Contact();
        contact.setFirstNm("John");
        contact.setLastNm("Smith");
        contact.setCompanyTitle("CEO");
        contact.setPhoneNo("614-222-3333");
        contact.setEmailAddressTxt("jsmith@company.com");
        
        Address address = new Address();
        address.setAddressLine1("123 Some Rd");
        address.setAddressLine2("Suite 1");
        address.setCityName("Columbus");
        address.setState("OH");
        address.setZipCode5("43211");
        address.setCountryCd("US");
        contact.setAddress(address);

        InfrastructureService infraBO = null;
        try {
            infraBO = ServiceFactory.getInstance().getInfrastructureService();
            contact = infraBO.createContact(contact);
            contact = infraBO.retrieveContact(contact.getContactId());
            app.setContact(contact);
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Exception while creating or retrieving contact.");
        }
        System.out.println("addContact Done");

    }
    
    private Application addNotes(ApplicationService appBO, Application app) {
        System.out.println("addNotes...");
        Random random = new Random(System.currentTimeMillis());
        for (int i=0; i<random.nextInt(5); i++) {
            ApplicationNote note = new ApplicationNote();
            note.setApplicationId(app.getApplicationID());
            note.setDateEntered(new Timestamp(System.currentTimeMillis()));
            note.setNoteTxt("UNIT TEST NOTE " + (i + 1));
            note.setUserId(1);
            note.setNoteTypeCd(NoteType.DAPC);
            try {
                appBO.createApplicationNote(note);
                app = appBO.retrieveApplication(app.getApplicationID());
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception creating application note");
            }
        }
        System.out.println("addNotes Done");
        return app;
    }
    
    private void addPTIOEmissionsInfo(ApplicationService appBO, PTIOApplication ptioApp) {
        System.out.println("addPTIOEmissionsInfo...");
        Random random = new Random(System.currentTimeMillis());
        float maxLPH = 10.0f;
        float maxTPY = 15.0f;
        try {
            ptioApp = (PTIOApplication)appBO.retrieveApplication(ptioApp.getApplicationID());
            for (ApplicationEU appEU : ptioApp.getIncludedEus()) {
                assertEquals(PTIOApplicationEU.class, appEU.getClass());
                PTIOApplicationEU ptioEU = (PTIOApplicationEU)appEU;
                
                // CAP emissions should be defined by default
                assertFalse(ptioEU.getCapEmissions().size() == 0);
                for (ApplicationEUEmissions cap : ptioEU.getCapEmissions()) {
                    if (cap.getPollutantCd().equals(PollutantDef.HTOT_CD) ||
                            cap.getPollutantCd().equals(PollutantDef.HMAX_CD)) {
                        continue;   // don't generate values for total and max
                    }
                    
                    cap.setPreCtlPotentialEmissions(decimalFormat.format(new Double(random.nextDouble() * maxTPY)));
                    //cap.setPotentialToEmit(decimalFormat.format(new Double(random.nextDouble() * maxLPH)));
                    cap.setPotentialToEmitLbHr(decimalFormat.format(new Double(random.nextDouble() * maxLPH)));
                    cap.setPotentialToEmitTonYr(decimalFormat.format(new Double(random.nextDouble() * maxTPY)));
                    
                }
                
                // add HAP emissions for this EU        
                HashSet<String> pollutantCds = new HashSet<String>();
                for (int i=0; i<random.nextInt(5); i++) {
                    String pollutantCd = getRandomHap();
                    if (!pollutantCds.contains(pollutantCd)) {
                        pollutantCds.add(pollutantCd);
                        ApplicationEUEmissions hap = new ApplicationEUEmissions(ptioEU.getApplicationEuId(), 
                                pollutantCd, ApplicationEUEmissionTableDef.HAP_TABLE_CD);
						hap.setPreCtlPotentialEmissions(decimalFormat.format(new Double(random.nextDouble() * maxTPY)));
						// hap.setPotentialToEmit(decimalFormat.format(new Double(random.nextDouble() * maxLPH)));
						hap.setPotentialToEmitLbHr(decimalFormat.format(new Double(random.nextDouble() * maxLPH)));
						hap.setPotentialToEmitTonYr(decimalFormat.format(new Double(random.nextDouble() * maxTPY)));
                        ptioEU.getHapTacEmissions().add(hap);
                    }
                }
                appBO.modifyApplicationEU(appEU);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed modifying PTIO application");
        }
        System.out.println("addPTIOEmissionsInfo Done");
        
    }
    
    private Application addDocsToEUs(ApplicationService appBO, Application app) {
        System.out.println("addDocsToEUs...");
        File eacDoc = null;
        File pfDoc = null;
        try {
            app = appBO.retrieveApplication(app.getApplicationID());
        } catch (Exception e2) {
            e2.printStackTrace();
            fail("Exception retrieving application");
        }
        try {
            eacDoc = File.createTempFile("eac", "txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(eacDoc));
            bw.write("This is a test EAC file");
            bw.flush();
            bw.close();
            
            pfDoc = File.createTempFile("processFlow", ".txt");
            bw = new BufferedWriter(new FileWriter(pfDoc));
            bw.write("This is a test Proccess Flow file");
            bw.flush();
            bw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            fail("Exception creating temp file");
        }
        
        for (ApplicationEU appEU : app.getIncludedEus()) {
            ApplicationDocumentRef eacDocRef = new ApplicationDocumentRef();
            eacDocRef.setDescription("UNIT TEST EAC DOC");
            //eacDocRef.setApplicationDocumentTypeCD(ApplicationDocumentTypeDef.EAC);
            eacDocRef.setApplicationEUId(appEU.getApplicationEuId());
            eacDocRef.setApplicationId(app.getApplicationID());
            uploadApplicationDoc(appBO, eacDocRef, eacDoc, app.getFacilityId());
            
            ApplicationDocumentRef pfDocRef = new ApplicationDocumentRef();
            pfDocRef.setDescription("UNIT TEST PROCESS FLOW DOC");
            pfDocRef.setApplicationDocumentTypeCD(ApplicationDocumentTypeDef.PROCESS_FLOW_DIAGRAM);
            pfDocRef.setApplicationEUId(appEU.getApplicationEuId());
            pfDocRef.setApplicationId(app.getApplicationID());
            uploadApplicationDoc(appBO, pfDocRef, pfDoc, app.getFacilityId());
        }
        
        try {
            appBO.modifyApplication(app, true);
            Application ckApp = appBO.retrieveApplication(app.getApplicationID());
            for (ApplicationEU ckAppEU : ckApp.getIncludedEus()) {
                assertEquals(ckAppEU.getEuDocuments().size(), 2);
            }
            app = ckApp;
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed modifying PTIO application");
        }
        System.out.println("addDocsToEUs Done");
        return app;
    }
    
    private void uploadApplicationDoc(ApplicationService appBO, ApplicationDocumentRef docRef,
            File doc, String facilityId) {
        ApplicationDocument publicDoc = new ApplicationDocument();
        publicDoc.setFacilityID(facilityId);
        publicDoc.setApplicationId(docRef.getApplicationId());
        publicDoc.setLastModifiedBy(1);
        publicDoc.setDescription("EAC Document");
        
        try {
            publicDoc = (ApplicationDocument)appBO.uploadDocument(publicDoc, 
                    publicDoc.getPath(), new FileInputStream(doc));
            docRef.setDocumentId(publicDoc.getDocumentID());
            appBO.createApplicationDocument(docRef);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception uploading temp file");
        }
        
    }
    
    private void euCopyDataTest(ApplicationService appBO, Application app) {
        System.out.println("euCopyDataTest...");
        try {
            app = appBO.retrieveApplication(app.getApplicationID());
        } catch (Exception e2) {
            e2.printStackTrace();
            fail("Exception retrieving application");
        }
        List<ApplicationEU> eus = app.getIncludedEus();
        if (eus.size() > 1) {
            Collections.shuffle(eus);
            ApplicationEU source = eus.get(0);
            ApplicationEU dest = eus.get(1);
            try {
                dest = appBO.copyApplicationEUData(source, dest);
                assertFalse(source.getApplicationEuId().equals(dest.getApplicationEuId()));
                assertEquals(source.getEuDocuments().size(), dest.getEuDocuments().size());
                assertEquals(source.getClass(), dest.getClass());
                if (source.getClass().equals(PTIOApplicationEU.class)) {
                    PTIOApplicationEU ptioSource = (PTIOApplicationEU)source;
                    PTIOApplicationEU ptioDest = (PTIOApplicationEU)dest;
                    assertEquals(ptioSource.getBactFlag(), ptioDest.getBactFlag());
                    comparePTIOEmissions(ptioSource.getCapEmissions(), ptioDest.getCapEmissions());
                    comparePTIOEmissions(ptioSource.getHapTacEmissions(), ptioDest.getHapTacEmissions());
                    assertEquals(ptioSource.getFederalLimitsReasonCDs(), ptioDest.getFederalLimitsReasonCDs());
                    assertEquals(ptioSource.getFederalLimitsOtherReasonDesc(), ptioDest.getFederalLimitsOtherReasonDesc());
                    assertEquals(ptioSource.isGeneralPermit(), ptioDest.isGeneralPermit());
                    assertEquals(ptioSource.getGeneralPermitTypeCd(), ptioDest.getGeneralPermitTypeCd());
                    assertEquals(ptioSource.getModelGeneralPermitCd(), ptioDest.getModelGeneralPermitCd());
                    assertEquals(ptioSource.getPtioEUPurposeCD(), ptioDest.getPtioEUPurposeCD());
                    assertEquals(ptioSource.getOperationBeginDate(), ptioDest.getOperationBeginDate());
                    assertEquals(ptioSource.getWorkStartDate(), ptioDest.getWorkStartDate());
                    assertEquals(ptioSource.getModificationDesc(), ptioDest.getModificationDesc());
                    assertEquals(ptioSource.getBactFlag(), ptioDest.getBactFlag());
                    assertEquals(ptioSource.isWorkStartAfterPermit(), ptioDest.isWorkStartAfterPermit());
                    assertEquals(ptioSource.getReconstructionDesc(), ptioDest.getReconstructionDesc());
                    assertEquals(ptioSource.getRequestingFederalLimitsFlag(), ptioDest.getRequestingFederalLimitsFlag());
                    assertEquals(ptioSource.getShutdownYears(), ptioDest.getShutdownYears());
                } else {
                    TVApplicationEU tvSource = (TVApplicationEU)source;
                    TVApplicationEU tvDest = (TVApplicationEU)dest;
                    compareTVScenario(tvSource.getNormalOperatingScenario(), tvDest.getNormalOperatingScenario());
                    for (TVEUOperatingScenario altScenario : tvSource.getAlternateOperatingScenarios()) {
                        for (TVEUOperatingScenario copyAltScenario : tvDest.getAlternateOperatingScenarios()) {
                            if (copyAltScenario.getTvEuOperatingScenarioId().equals(altScenario.getTvEuOperatingScenarioId())) {
                                compareTVScenario(altScenario, copyAltScenario);
                            }
                        }
                    }
                    
                    assertEquals(tvSource.isMonitorRequired(), tvDest.isMonitorRequired());
                    assertEquals(tvSource.getMonitorReqDsc(), tvDest.getMonitorReqDsc());
                    assertEquals(tvSource.isComplyWEnhMonitoring(), tvDest.isComplyWEnhMonitoring());
                    
                    compareApplicableRequirements(tvSource.getApplicableRequirements(), tvDest.getApplicableRequirements());
                    compareApplicableRequirements(tvSource.getStateOnlyRequirements(), tvDest.getStateOnlyRequirements());
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception in euCopy test");
            }
            System.out.println("euCopyDataTest Done");
        }
    }
    
    private Application ptioAppCopyTest(ApplicationService appBO, PTIOApplication ptioApp) {
        System.out.println("ptioAppCopyTest...");
        Application copy = null;
        try {
            ptioApp = (PTIOApplication)appBO.retrieveApplication(ptioApp.getApplicationID());
            // test copy of PTIO application
            copy = appBO.createApplicationCopy(ptioApp, false, null, false);
            assertNotNull(copy);
            assertEquals(PTIOApplication.class, copy.getClass());

            // copy should not have same number or id as original
            assertFalse(ptioApp.getApplicationNumber().equals(copy.getApplicationNumber()));
            assertFalse(ptioApp.getApplicationID().equals(copy.getApplicationID()));

            // base Application stuff
            assertEquals(ptioApp.getValidated(), copy.getValidated());
            assertEquals(ptioApp.getApplicationCorrectedReason(), copy.getApplicationCorrectedReason());
            assertEquals(ptioApp.getApplicationPurposeCDs(), copy.getApplicationPurposeCDs());
            assertEquals(ptioApp.getApplicationPurposeDesc(), copy.getApplicationPurposeDesc());
            assertEquals(ptioApp.getApplicationTypeCD(), copy.getApplicationTypeCD());
            assertEquals(ptioApp.getContact(), copy.getContact());
            assertEquals(ptioApp.getDocuments(), copy.getDocuments());
            assertEquals(ptioApp.getDoLaaCd(), copy.getDoLaaCd());
            assertEquals(ptioApp.getFacilityId(), copy.getFacilityId());
            assertEquals(ptioApp.getFacilityName(), copy.getFacilityName());
            assertEquals(ptioApp.getPreviousApplicationNumber(), copy.getPreviousApplicationNumber());
            assertEquals(ptioApp.getReferencedPermits(), copy.getReferencedPermits());
            assertEquals(ptioApp.isApplicationAmended(), copy.isApplicationAmended());
            assertEquals(ptioApp.isApplicationCorrected(), copy.isApplicationCorrected());

            // EUs
            List<ApplicationEU> includedEUs = ptioApp.getIncludedEus();
            List<ApplicationEU> copyIncludedEUs = copy.getIncludedEus();
            assertEquals(includedEUs.size(), copyIncludedEUs.size());
            for (ApplicationEU copyEU : copyIncludedEUs) {
                assertEquals(copy.getApplicationID(), copyEU.getApplicationId());
                for (ApplicationEU origEU : includedEUs) {
                    assertFalse(origEU.getApplicationEuId().equals(copyEU.getApplicationEuId()));
                    if (origEU.getFpEU().getEpaEmuId().equals(copyEU.getFpEU().getEpaEmuId())) {
                        assertEquals(origEU.getEuDocuments().size(), copyEU.getEuDocuments().size());
                        if (origEU.getClass().equals(PTIOApplicationEU.class)) {
                            assertEquals(((PTIOApplicationEU)origEU).getCapEmissions().size(),
                                    ((PTIOApplicationEU)copyEU).getCapEmissions().size());
                            assertEquals(((PTIOApplicationEU)origEU).getHapTacEmissions().size(),
                                    ((PTIOApplicationEU)copyEU).getHapTacEmissions().size());
                        }
                    }
                }
            }

            // PTIO-specific stuff
            PTIOApplication ptioCopy = (PTIOApplication)copy;
            assertEquals(ptioApp.isQualifyExpress(), ptioCopy.isQualifyExpress());
            assertEquals(ptioApp.isRequestExpress(), ptioCopy.isRequestExpress());
            assertEquals(ptioApp.isTradeSecret(), ptioCopy.isTradeSecret());
            assertEquals(ptioApp.getApplicationPurposeCDs(), ptioCopy.getApplicationPurposeCDs());
            assertEquals(ptioApp.getApplicationPurposeDesc(), ptioCopy.getApplicationPurposeDesc());
            assertEquals(ptioApp.getChangedPERDueDateCD(), ptioCopy.getChangedPERDueDateCD());
            assertEquals(ptioApp.getFederalRuleApplicabilityExplanation(), ptioCopy.getFederalRuleApplicabilityExplanation());
            assertEquals(ptioApp.getGeneralPermitTypeCD(), ptioCopy.getGeneralPermitTypeCD());
            assertEquals(ptioApp.getMactApplicableFlag(), ptioCopy.getMactApplicableFlag());
            assertEquals(ptioApp.getMactSubpartCodes(), ptioCopy.getMactSubpartCodes());
            assertEquals(ptioApp.getNeshapApplicableFlag(), ptioCopy.getNeshapApplicableFlag());
            assertEquals(ptioApp.getNeshapSubpartCodes(), ptioCopy.getNeshapSubpartCodes());
            assertEquals(ptioApp.getNspsApplicableFlag(), ptioCopy.getNspsApplicableFlag());
            assertEquals(ptioApp.getNspsSubpartCodes(), ptioCopy.getNspsSubpartCodes());
            assertEquals(ptioApp.getNsrApplicableFlag(), ptioCopy.getNsrApplicableFlag());
            assertEquals(ptioApp.getOtherPurposeDesc(), ptioCopy.getOtherPurposeDesc());
            assertEquals(ptioApp.getPreviousApplicationNumber(), ptioCopy.getPreviousApplicationNumber());
            assertEquals(ptioApp.getPsdApplicableFlag(), ptioCopy.getPsdApplicableFlag());
            assertEquals(ptioApp.getReferencedPermits(), ptioCopy.getReferencedPermits());
            assertEquals(ptioApp.getRequestedPERDueDateCD(), ptioCopy.getRequestedPERDueDateCD());
            assertEquals(ptioApp.getRiskManagementPlanFlag(), ptioCopy.getRiskManagementPlanFlag());
            assertEquals(ptioApp.getTitleIVFlag(), ptioCopy.getTitleIVFlag());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception while creating application copy. " + e.getMessage());
        }
        System.out.println("ptioAppCopyTest Done");
        return copy;
    }
    
    private Application tvAppCopyTest(ApplicationService appBO, TVApplication tvApp) {
        System.out.println("tvAppCopyTest...");
        Application copy = null;
        try {
//            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
            // test copy of PTIO application
            copy = appBO.createApplicationCopy(tvApp, false, null, false);
            // retrieve from DB to be sure all changes have been committed.
            copy = appBO.retrieveApplication(copy.getApplicationID());
            assertNotNull(copy);
            assertEquals(TVApplication.class, copy.getClass());
            TVApplication tvCopy = (TVApplication)copy;

            // copy should not have same number or id as original
            assertFalse(tvApp.getApplicationNumber().equals(tvCopy.getApplicationNumber()));
            assertFalse(tvApp.getApplicationID().equals(tvCopy.getApplicationID()));

            // base Application stuff
            assertEquals(tvApp.getValidated(), tvCopy.getValidated());
            assertEquals(tvApp.getApplicationCorrectedReason(), tvCopy.getApplicationCorrectedReason());
            assertEquals(tvApp.getApplicationPurposeCDs(), tvCopy.getApplicationPurposeCDs());
            assertEquals(tvApp.getApplicationPurposeDesc(), tvCopy.getApplicationPurposeDesc());
            assertEquals(tvApp.getApplicationTypeCD(), tvCopy.getApplicationTypeCD());
            assertEquals(tvApp.getContact(), tvCopy.getContact());
            assertEquals(tvApp.getDocuments(), tvCopy.getDocuments());
            assertEquals(tvApp.getDoLaaCd(), tvCopy.getDoLaaCd());
            assertEquals(tvApp.getFacilityId(), tvCopy.getFacilityId());
            assertEquals(tvApp.getFacilityName(), tvCopy.getFacilityName());
            assertEquals(tvApp.getPreviousApplicationNumber(), tvCopy.getPreviousApplicationNumber());
            assertEquals(tvApp.getReferencedPermits(), tvCopy.getReferencedPermits());
            assertEquals(tvApp.isApplicationAmended(), tvCopy.isApplicationAmended());
            assertEquals(tvApp.isApplicationCorrected(), tvCopy.isApplicationCorrected());

            // EUs
            List<ApplicationEU> includedEUs = tvApp.getIncludedEus();
            List<ApplicationEU> copyIncludedEUs = tvCopy.getIncludedEus();
            assertEquals(includedEUs.size(), copyIncludedEUs.size());
            for (ApplicationEU copyEU : copyIncludedEUs) {
                assertEquals(copyEU.getClass(), TVApplicationEU.class);
                TVApplicationEU tvCopyEU = (TVApplicationEU)copyEU;
                assertEquals(tvCopy.getApplicationID(), tvCopyEU.getApplicationId());
                for (ApplicationEU origEU : includedEUs) {
                    TVApplicationEU tvOrigEU = (TVApplicationEU)origEU;
                    if (tvOrigEU.getFpEU().getEpaEmuId().equals(tvCopyEU.getFpEU().getEpaEmuId())) {
                        assertFalse(tvOrigEU.getApplicationEuId().equals(tvCopyEU.getApplicationEuId()));
                        assertEquals(tvOrigEU.getEuDocuments().size(), tvCopyEU.getEuDocuments().size());
                        compareTVScenario(tvOrigEU.getNormalOperatingScenario(), tvCopyEU.getNormalOperatingScenario());
                        compareTVScenarios(tvOrigEU.getAlternateOperatingScenarios(), tvCopyEU.getAlternateOperatingScenarios());
                        
                        compareApplicableRequirements(tvOrigEU.getApplicableRequirements(), tvCopyEU.getApplicableRequirements());
                        compareApplicableRequirements(tvOrigEU.getStateOnlyRequirements(), tvCopyEU.getStateOnlyRequirements());
                    }
                }
            }

            // TV-specific stuff
            
            // eu groups
            assertEquals(tvApp.getEuGroups().size(), tvCopy.getEuGroups().size());
            for (TVEUGroup euGroup : tvApp.getEuGroups()) {
                for (TVEUGroup copyEuGroup : tvCopy.getEuGroups()) {
                    if (copyEuGroup.getTvEuGroupId().equals(euGroup.getTvEuGroupId())) {
                        assertEquals(euGroup.getEus().size(), copyEuGroup.getEus().size());
                        compareApplicableRequirements(euGroup.getApplicableRequirements(), euGroup.getApplicableRequirements());
                        compareApplicableRequirements(euGroup.getStateOnlyRequirements(), euGroup.getStateOnlyRequirements());
                    }
                }
            }
            
            // applicable requirements
            compareApplicableRequirements(tvApp.getApplicableRequirements(), tvCopy.getApplicableRequirements());
            compareApplicableRequirements(tvApp.getStateOnlyRequirements(), tvCopy.getStateOnlyRequirements());
            
            // adjustments
            // NOTE: intValue is used for comparison because there are slight differences
            // when comparing the values a floats
            assertEquals(tvApp.getCapPteTotals().size(), tvCopy.getCapPteTotals().size());
            for (TVPteAdjustment adj : tvApp.getCapPteTotals()) {
                for (TVPteAdjustment copyAdj : tvCopy.getCapPteTotals()) {
                    if (copyAdj.getPollutantCd().equals(adj.getPollutantCd())) {
                        assertTrue((adj.getPteAdjusted() == null && copyAdj.getPteAdjusted() == null) ||
                                (adj.getPteAdjusted() != null && copyAdj.getPteAdjusted() != null));
                        if (adj.getPteAdjusted() != null) {
                            assertEquals(adj.getPteAdjusted(), copyAdj.getPteAdjusted());
                        }
                        assertEquals(adj.getPteEUTotal(), copyAdj.getPteEUTotal());
                        assertEquals(adj.getPteFinal(), copyAdj.getPteFinal());
                        assertEquals(adj.getMajorStatus(), copyAdj.getMajorStatus());
                    }
                }
            }

            assertEquals(tvApp.getHapPteTotals().size(), tvCopy.getHapPteTotals().size());
            for (TVPteAdjustment adj : tvApp.getHapPteTotals()) {
                for (TVPteAdjustment copyAdj : tvCopy.getHapPteTotals()) {
                    if (copyAdj.getPollutantCd().equals(adj.getPollutantCd())) {
                        assertTrue((adj.getPteAdjusted() == null && copyAdj.getPteAdjusted() == null) ||
                                (adj.getPteAdjusted() != null && copyAdj.getPteAdjusted() != null));
                        if (adj.getPteAdjusted() != null) {
                            assertEquals(adj.getPteAdjusted(), copyAdj.getPteAdjusted());
                        }
                        assertEquals(adj.getPteEUTotal(), copyAdj.getPteEUTotal());
                        assertEquals(adj.getPteFinal(), copyAdj.getPteFinal());
                        assertEquals(adj.getMajorStatus(), copyAdj.getMajorStatus());
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception while creating application copy. " + e.getMessage());
        }
        System.out.println("tvAppCopyTest Done");
        return copy;
    }
    

    private TVApplication addApplicableReqs(ApplicationService appBO, TVApplication tvApp) {
        System.out.println("addApplicableReqs...");
        try {
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
        } catch (Exception e1) {
            e1.printStackTrace();
            fail("Exception refreshing application data");
        }
        
        // generate applicable reqs for EUs
        for (ApplicationEU eu : tvApp.getIncludedEus()) {
            System.out.println("\t" + eu.getFpEU().getEpaEmuId());
            TVApplicationEU tvEu = (TVApplicationEU)eu;
            generateApplicableReqs(appBO, tvApp.getApplicationID(),
                    tvEu.getApplicationEuId(), null,
                    tvEu.getApplicableRequirements(), tvEu.getStateOnlyRequirements());
            try {
                appBO.modifyApplicationEU(eu);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception updating EU with applicable reqs");
            }
        }
        try {
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
            // generate applicable reqs at the facility level
            generateApplicableReqs(appBO, tvApp.getApplicationID(),
                    null, null, tvApp.getApplicableRequirements(), 
                    tvApp.getStateOnlyRequirements());
            appBO.modifyApplication(tvApp, true);
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception modifying application");
        }
        System.out.println("addApplicableReqs Done");
        return tvApp;
    }
    
    private void generateApplicableReqs(ApplicationService appBO, Integer applicationId,
            Integer applicationEuId, Integer euGroupId, 
            List<TVApplicableReq> applicableRequirements, List<TVApplicableReq> stateOnlyRequirements) {
        Random random = new Random(System.currentTimeMillis());
        int reqCount = random.nextInt(5) + 1;
        for (int i = 0; i<reqCount; i++) {
            try {
                TVApplicableReq req = appBO.getTempApplicableReq();
                req.setApplicationId(applicationId);
                req.setApplicationEuId(applicationEuId);
                req.setTvEuGroupId(euGroupId);
                int pollutantIdx = random.nextInt(ApplicationBO.getPTIOCapPollutantCodesOrdered().size());
                req.setPollutantCd(ApplicationBO.getPTIOCapPollutantCodesOrdered().get(pollutantIdx));
                populateApplicableReq(req);
                if (i % 3 == 0) {
                    // make a few requirements "state only"
                    req.setStateOnly(true);
                    stateOnlyRequirements.add(req);
                } else {
                    applicableRequirements.add(req);
                }
                // sleep to make sure new seed is available for random numbers.
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception creating applicable req");
            }
        }
    }
    
    private void populateApplicableReq(TVApplicableReq req) {
        Random random = new Random(System.currentTimeMillis());
        int citeTypeIdx = random.nextInt(3);
        int ruleCiteValueRange = 0;
        int ruleCiteValueOffset = 0;
        switch (citeTypeIdx) {
        case 0:
            req.setTvRuleCiteTypeCd(TVRuleCiteTypeDef.RULE);
            ruleCiteValueRange = 460;
            ruleCiteValueOffset = 20;
            break;
        case 1:
            req.setTvRuleCiteTypeCd(TVRuleCiteTypeDef.MACT);
            ruleCiteValueRange = 80;
            ruleCiteValueOffset = 1;
            break;
        case 2:
            req.setTvRuleCiteTypeCd(TVRuleCiteTypeDef.NESHAP);
            ruleCiteValueRange = 8;
            ruleCiteValueOffset = 1;
            break;
        case 3:
            req.setTvRuleCiteTypeCd(TVRuleCiteTypeDef.NSPS);
            ruleCiteValueRange = 85;
            ruleCiteValueOffset = 1;
            break;
        }
        
        Integer ruleCiteVal = random.nextInt(ruleCiteValueRange) + ruleCiteValueOffset;
        
        Double value = random.nextDouble() * 25.0;
        req.setAllowableValue(value.toString());
        req.setAllowableRuleCiteCd(ruleCiteVal.toString());
        req.setAllowablePermitCite("UT PTI ALW");
        
        ruleCiteVal = random.nextInt(ruleCiteValueRange) + ruleCiteValueOffset;
        req.setMonitoringRuleCiteCd(ruleCiteVal.toString());
        req.setMonitoringPermitCite("UT PTI MON");
        req.setMonitoringValue("UT MONITORING VALUE");
        
        ruleCiteVal = random.nextInt(ruleCiteValueRange) + ruleCiteValueOffset;
        req.setRecordKeepingRuleCiteCd(ruleCiteVal.toString());
        req.setRecordKeepingPermitCite("UT PTI REC");
        req.setRecordKeepingValue("UT RECORD KEEPING VALUE");

        ruleCiteVal = random.nextInt(ruleCiteValueRange) + ruleCiteValueOffset;
        req.setReportingRuleCiteCd(ruleCiteVal.toString());
        req.setReportingPermitCite("UT PTI RPT");
        req.setTvCompRptFreqCd(getRandomReportFreq());

        ruleCiteVal = random.nextInt(ruleCiteValueRange) + ruleCiteValueOffset;
        req.setTestingRuleCiteCd(ruleCiteVal.toString());
        req.setTestingPermitCite("UT PTI TST");
        req.setTestingValue("UT TESTING VALUE");
        
        if (random.nextBoolean()) {
            req.setComplianceStatus(true);
            for (int i=0; i<random.nextInt(3); i++) {
                TVCompliance compliance = new TVCompliance();
                compliance.setTvApplicableReqId(req.getTvApplicableReqId());
                compliance.setComplianceId(i+1);
                compliance.setComplianceApproachReq("UT COMPLIANCE APPROACH REQUIREMENT " + compliance.getComplianceId());
                compliance.setComplianceApproach("UT COMPLIANCE APPROACH " + compliance.getComplianceId());
                req.getComplianceReqs().add(compliance);
            }
        }
        
        if (random.nextBoolean()) {
            req.setComplianceObligationsStatus(true);
            for (int i=0; i<random.nextInt(3); i++) {
                TVComplianceObligations complianceObligations = new TVComplianceObligations();
                complianceObligations.setTvApplicableReqId(req.getTvApplicableReqId());
                complianceObligations.setComplianceObligationsId(i+1);
                complianceObligations.setComplianceObligationsReq("UT COMPLIANCE OBLIGATIONS REQUIREMENT " + complianceObligations.getComplianceObligationsId());
                complianceObligations.setComplianceObligationsLimit("UT COMPLIANCE LIMIT " + complianceObligations.getComplianceObligationsId());
                complianceObligations.setComplianceObligationsBasis("UT COMPLIANCE BASIS " + complianceObligations.getComplianceObligationsId());
                req.getComplianceObligationsReqs().add(complianceObligations);
            }
        }
        
        if (random.nextBoolean()) {
            req.setProposedExemptionsStatus(true);
            for (int i=0; i<random.nextInt(3); i++) {
                TVProposedExemptions exemptions = new TVProposedExemptions();
                exemptions.setTvApplicableReqId(req.getTvApplicableReqId());
                exemptions.setProposedExemptionsId(i+1);
                exemptions.setProposedExemptionsReq("UT PROPOSED EXEMPTIONS REQUIREMENT " + exemptions.getProposedExemptionsId());
                exemptions.setProposedExemptions("UT PROPOSED EXEMPTIONS APPROACH " + exemptions.getProposedExemptionsId());
                req.getProposedExemptionsReqs().add(exemptions);
            }
        }
        
        if (random.nextBoolean()) {
            req.setProposedAltLimitsStatus(true);
            for (int i=0; i<random.nextInt(3); i++) {
                TVProposedAltLimits altLimits = new TVProposedAltLimits();
                altLimits.setTvApplicableReqId(req.getTvApplicableReqId());
                altLimits.setProposedAltLimitsId(i+1);
                altLimits.setProposedAltLimitsReq("UT PROPOSED ALT LIMITS REQUIREMENT " + altLimits.getProposedAltLimitsId());
                altLimits.setProposedAltLimits("UT PROPOSED ALT LIMITS APPROACH " + altLimits.getProposedAltLimitsId());
                req.getProposedAltLimitsReqs().add(altLimits);
            }
        }
        
        if (random.nextBoolean()) {
            req.setProposedTestChangesStatus(true);
            for (int i=0; i<random.nextInt(3); i++) {
                TVProposedTestChanges testChanges = new TVProposedTestChanges();
                testChanges.setTvApplicableReqId(req.getTvApplicableReqId());
                testChanges.setProposedTestChangesId(i+1);
                testChanges.setProposedTestChangesReq("UT PROPOSED EXEMPTIONS REQUIREMENT " + testChanges.getProposedTestChangesId());
                testChanges.setProposedTestChanges("UT PROPOSED EXEMPTIONS APPROACH " + testChanges.getProposedTestChangesId());
                req.getProposedTestChangesReqs().add(testChanges);
            }
        }
    }
    
    private String getRandomHap() {
        if (hapCodes == null) {
            hapCodes = new ArrayList<String>();
            DefSelectItems pollutantDefItems = PollutantDef.getData().getItems();
            for (SelectItem item : pollutantDefItems.getCurrentItems()) {
                PollutantDef pollutantDef = (PollutantDef)pollutantDefItems.getItem(item.getValue().toString());
//                if (pollutantDef.getCategory().matches(".*HAP.*") && pollutantDef.isOrigination114()
//                        && pollutantDef.isTvptoAppSec13()) {
//                    hapCodes.add(pollutantDef.getCode());
//                }
            }
        }
        Collections.shuffle(hapCodes);
        return hapCodes.get(0);
    }
    
    private String getRandomReportFreq() {
        if (reportFreqCodes == null) {
            reportFreqCodes = new ArrayList<String>();
            DefSelectItems freqItems = TVCompRptFreqDef.getData().getItems();
            for (SelectItem item : freqItems.getCurrentItems()) {
                reportFreqCodes.add(item.getValue().toString());
            }
        }
        Collections.shuffle(reportFreqCodes);
        return reportFreqCodes.get(0);
    }

    private TVApplication addTVScenarios(ApplicationService appBO, TVApplication tvApp) {
        Random random = new Random(System.currentTimeMillis());
        System.out.println("addTVScenarios...");
        try {
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
        } catch (Exception e1) {
            e1.printStackTrace();
            fail("Exception refreshing application data");
        }
        for (ApplicationEU eu : tvApp.getIncludedEus()) {
            System.out.println("\t" + eu.getFpEU().getEpaEmuId());
            String tvClassCd = eu.getFpEU().getTvClassCd();
            TVApplicationEU tvEU = (TVApplicationEU)eu;
            TVEUOperatingScenario scenario = tvEU.getNormalOperatingScenario();
            populateScenario(scenario, tvApp.getApplicationID());

            if (tvClassCd == null || !tvClassCd.equals(TVClassification.INSIG)) {
                // add alt scenario info for non-insignificant EU
                for (int i=0; i<random.nextInt(2); i++) {
                    TVEUOperatingScenario altScenario = new TVEUOperatingScenario();
                    altScenario.setApplicationEuId(tvEU.getApplicationEuId());
                    try {
                        altScenario = appBO.createTVEUOperatingScenario(altScenario);
                        populateScenario(altScenario, tvApp.getApplicationID());
                        tvEU.getAlternateOperatingScenarios().add(altScenario);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Exception creating alternate scenario");
                    }
                }
            }
            
            try {
                appBO.modifyApplicationEU(tvEU);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception updating EU");
            }
        }
        try {
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("addTVScenarios Done");
        return tvApp;
    }
    
    private TVApplication addTVEUGroups(ApplicationService appBO, TVApplication tvApp) {
        System.out.println("addTVEUGroups...");
        try {
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
        } catch (Exception e1) {
            e1.printStackTrace();
            fail("Exception refreshing application data");
        }
        Random random = new Random(System.currentTimeMillis());
        List<ApplicationEU> euList = tvApp.getIncludedEus();
        int euCount = euList.size();
        int numGroups = random.nextInt(euCount > 20 ? 20 : euCount) + 1;
        System.out.println("\tCreating " + numGroups + " EU groups");
        for (int i=0; i<numGroups; i++) {
            TVEUGroup euGroup = new TVEUGroup();
            euGroup.setApplicationId(tvApp.getApplicationID());
            euGroup.setTvEuGroupId(null);
            euGroup.setTvEuGroupName("UT EU GROUP " + (i + 1));
            
            int eusToAdd = random.nextInt(euCount > 10 ? 10 : euCount) + 1;
            System.out.println(eusToAdd + " EUs in group " + i);
            Collections.shuffle(euList, random);
            for (int j=0; j<eusToAdd; j++) {
                euGroup.getEus().add((TVApplicationEU)euList.get(j));
            }
            
            try {
                euGroup = appBO.createTvEuGroup(euGroup);
                euGroup = appBO.retrieveTvEuGroup(euGroup.getTvEuGroupId());
                generateApplicableReqs(appBO, tvApp.getApplicationID(),
                        null, euGroup.getTvEuGroupId(), 
                        euGroup.getApplicableRequirements(), 
                        euGroup.getStateOnlyRequirements());
                
                appBO.modifyTvEuGroup(euGroup);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception creating EU Group");
            }
            tvApp.getEuGroups().add(euGroup);
        }

        try {
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
            appBO.modifyApplication(tvApp, true);
            tvApp = (TVApplication)appBO.retrieveApplication(tvApp.getApplicationID());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception modifying Application");
        }
        System.out.println("addTVEUGroups Done");
        return tvApp;
    }
    
    private void populateScenario(TVEUOperatingScenario scenario, int appId) {
        scenario.setOpSchedHrsDay(24);
        scenario.setOpSchedHrsYr(8400);
        Random random = new Random(System.currentTimeMillis());
        if (random.nextInt() % 2 == 0) {
            scenario.setOperationLimits("Y");
            scenario.setOperationLimitsDsc("UNIT TEST OPERATION LIMITS DESCRIPTION");
        }
        if (random.nextInt() % 2 == 0) {
            scenario.setOpSchedTradeSecret(true);
            scenario.setOpSchedTradeSecretReason("UNIT TEST OPERATING SCHEDULE TRADE SECRET REASON");
        }
        if (scenario.getTvEuOperatingScenarioId() == 0) {
            scenario.setTvEuOperatingScenarioNm("UT NORM SCENARIO");
        } else {
            scenario.setTvEuOperatingScenarioNm("UT ALT SCENARIO " + scenario.getTvEuOperatingScenarioId());
        }
        for (TVApplicationEUEmissions emissions : scenario.getCapEmissions()) {
            populateTvEmissions(emissions);
        }
        
        HashSet<String> pollutantCds = new HashSet<String>();
        for (int i=0; i<random.nextInt(5); i++) {
            String pollutantCd = getRandomHap();
            if (!pollutantCds.contains(pollutantCd)) {
                pollutantCds.add(pollutantCd);
                TVApplicationEUEmissions hapEmission = new TVApplicationEUEmissions(scenario.getApplicationEuId(), 
                        scenario.getTvEuOperatingScenarioId(), pollutantCd, 
                        ApplicationEUEmissionTableDef.HAP_TABLE_CD);
                populateTvEmissions(hapEmission);
                scenario.getHapEmissions().add(hapEmission);
            }
        }
        
        List<String> availablePollutants = new ArrayList<String>();
        for (TVApplicationEUEmissions emissions : scenario.getCapEmissions()) {
            availablePollutants.add(emissions.getPollutantCd());
        }
        for (TVApplicationEUEmissions emissions : scenario.getHapEmissions()) {
            availablePollutants.add(emissions.getPollutantCd());
        }
        generatePteRequirements(scenario, availablePollutants);
    }

    private void generatePteRequirements(TVEUOperatingScenario scenario, List<String> availablePollutants) {
        Random random = new Random(System.currentTimeMillis());
        for (int i=0; i<random.nextInt(availablePollutants.size()); i++) {
            TVAltScenarioPteReq req = new TVAltScenarioPteReq();
            req.setApplicationEuId(scenario.getApplicationEuId());
            req.setTvEuOperatingScenarioId(scenario.getTvEuOperatingScenarioId());
            req.setTvAltScenarioPteReqId(i);
            // it's ok if same pollutant shows up more than once
            req.setPollutantCd(availablePollutants.get(random.nextInt(availablePollutants.size())));
            req.setAllowable(new Float(random.nextDouble() * 25.0));
            req.setEmissionUnitsCd("A_TPY");    // tons per year
            req.setApplicableReq("UT REQ " + req.getTvAltScenarioPteReqId());
            scenario.getPteRequirements().add(req);
        }
        
    }

    private void populateTvEmissions(TVApplicationEUEmissions emissions) {
        Random random = new Random(System.currentTimeMillis());
//        emissions.setPteTonsYr(new Float(random.nextDouble() * 10.0));
        emissions.setPteTonsYr(new Float(random.nextDouble() * 10.0).toString());
        emissions.setPteDeterminationBasis("AP-42");
        if (random.nextInt(10) == 4) {
            emissions.setDetBasisTradeSecret(true);
            emissions.setReasonDetBasisTradeSecret("UNIT TEST REASON DETERMINATION BASIS IS TRADE SECRET");
        }
        emissions.setApplicableReq("UNIT TEST APPLICABLE REQ");

        // sleep for more randomness
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void compareTVScenarios(List<TVEUOperatingScenario> scenarios, List<TVEUOperatingScenario> copyScenarios) {
        assertEquals(scenarios.size(), copyScenarios.size());
        for (TVEUOperatingScenario scenario : scenarios) {
            for (TVEUOperatingScenario copyScenario : copyScenarios) {
                if (scenario.getTvEuOperatingScenarioId().equals(copyScenario.getTvEuOperatingScenarioId())) {
                    compareTVScenario(scenario, copyScenario);
                }
            }
        }
    }
    
    private void compareTVScenario(TVEUOperatingScenario scenario, TVEUOperatingScenario copyScenario) {
        assertEquals(scenario.getTvEuOperatingScenarioId(), copyScenario.getTvEuOperatingScenarioId());
        assertEquals(scenario.getTvEuOperatingScenarioNm(), copyScenario.getTvEuOperatingScenarioNm());
        assertEquals(scenario.getOpSchedHrsDay(), copyScenario.getOpSchedHrsDay());
        assertEquals(scenario.getOpSchedHrsYr(), copyScenario.getOpSchedHrsYr());
        assertEquals(scenario.getOperationLimitsDsc(), copyScenario.getOperationLimitsDsc());
        assertEquals(scenario.isOpSchedTradeSecret(), copyScenario.isOpSchedTradeSecret());
        assertEquals(scenario.getOpSchedTradeSecretReason(), copyScenario.getOpSchedTradeSecretReason());
        assertEquals(scenario.isSourceOperationLimits(), copyScenario.isSourceOperationLimits());
        assertEquals(scenario.getOperationLimitsDsc(), copyScenario.getOperationLimitsDsc());
        
        compareTvEmissions(scenario.getCapEmissions(), copyScenario.getCapEmissions());
        compareTvEmissions(scenario.getHapEmissions(), copyScenario.getHapEmissions());        
        comparePteReqs(scenario.getPteRequirements(), copyScenario.getPteRequirements());
    }
    
    private void comparePteReqs(List<TVAltScenarioPteReq> reqs, List<TVAltScenarioPteReq> copyReqs) {
        assertEquals(reqs.size(), copyReqs.size());
        for (TVAltScenarioPteReq req : reqs) {
            for (TVAltScenarioPteReq copyReq : copyReqs) {
                if (copyReq.getTvAltScenarioPteReqId().equals(req.getTvAltScenarioPteReqId())) {
                    assertEquals(req.getPollutantCd(), copyReq.getPollutantCd());
                    assertEquals(req.getAllowable(), copyReq.getAllowable());
                    assertEquals(req.getEmissionUnitsCd(), copyReq.getEmissionUnitsCd());
                    assertEquals(req.getApplicableReq(), copyReq.getApplicableReq());
                }
            }
        }
    }

    private void compareTvEmissions(List<TVApplicationEUEmissions> emissions, List<TVApplicationEUEmissions> copyEmissions) {
        assertEquals(emissions.size(), copyEmissions.size());
        for (TVApplicationEUEmissions orig : emissions) {
            for (TVApplicationEUEmissions copy : copyEmissions) {
                if (copy.getPollutantCd().equals(orig.getPollutantCd()) &&
                        copy.getEuEmissionTableCd().equals(orig.getEuEmissionTableCd())) {
                    assertEquals(orig.getPollutantCd(), copy.getPollutantCd());
                    assertEquals(orig.getEuEmissionTableCd(), copy.getEuEmissionTableCd());
                    assertEquals(orig.getPteTonsYr(), copy.getPteTonsYr());
                    assertEquals(orig.getPteDeterminationBasis(), copy.getPteDeterminationBasis());
                    assertEquals(orig.getApplicableReq(), copy.getApplicableReq());
                    assertEquals(orig.getMajorStatus(), copy.getMajorStatus());
                }
            }
        }
    }
    
    private void comparePTIOEmissions(List<ApplicationEUEmissions> emissions, List<ApplicationEUEmissions> copyEmissions) {
        assertEquals(emissions.size(), copyEmissions.size());
        for (ApplicationEUEmissions orig  : emissions) {
            for (ApplicationEUEmissions copy : copyEmissions) {
				if (copy.getPollutantCd().equals(orig.getPollutantCd())) {
					assertEquals(orig.getPreCtlPotentialEmissions(),
							copy.getPreCtlPotentialEmissions());
					assertEquals(orig.getPotentialToEmit(),
							copy.getPotentialToEmit());
					assertEquals(orig.getPotentialToEmitLbHr(),
							copy.getPotentialToEmitLbHr());
					assertEquals(orig.getPotentialToEmitTonYr(),
							copy.getPotentialToEmitTonYr());
				}
            }
        }
    }

    private void compareApplicableRequirements(List<TVApplicableReq> reqs, List<TVApplicableReq> copyReqs) {
        assertEquals(reqs.size(), copyReqs.size());
        for (TVApplicableReq req : reqs) {
            for (TVApplicableReq copyReq : copyReqs) {
                if (copyReq.getTvApplicableReqId().equals(req.getTvApplicableReqId())) {
                    assertEquals(req.getPollutantCd(), copyReq.getPollutantCd());
                    
                    assertEquals(req.getAllowableRuleCiteCd(), copyReq.getAllowableRuleCiteCd());
                    assertEquals(req.getAllowablePermitCite(), copyReq.getAllowablePermitCite());
                    assertEquals(req.getAllowableValue(), copyReq.getAllowableValue());
                    
                    assertEquals(req.getMonitoringPermitCite(), copyReq.getMonitoringPermitCite());
                    assertEquals(req.getMonitoringRuleCiteCd(), copyReq.getMonitoringRuleCiteCd());
                    assertEquals(req.getMonitoringValue(), copyReq.getMonitoringValue());
                    
                    assertEquals(req.getRecordKeepingPermitCite(), copyReq.getRecordKeepingPermitCite());
                    assertEquals(req.getRecordKeepingRuleCiteCd(), copyReq.getRecordKeepingRuleCiteCd());
                    assertEquals(req.getRecordKeepingValue(), copyReq.getRecordKeepingValue());
                    
                    assertEquals(req.getReportingPermitCite(), copyReq.getReportingPermitCite());
                    assertEquals(req.getReportingRuleCiteCd(), copyReq.getReportingRuleCiteCd());
                    assertEquals(req.getTvCompRptFreqCd(), copyReq.getTvCompRptFreqCd());
                    
                    assertEquals(req.getTestingPermitCite(), copyReq.getTestingPermitCite());
                    assertEquals(req.getTestingRuleCiteCd(), copyReq.getTestingRuleCiteCd());
                    assertEquals(req.getTestingValue(), copyReq.getTestingValue());
                    
                    assertEquals(req.isComplianceStatus(), copyReq.isComplianceStatus());
                    if (req.isComplianceStatus()) {
                        for (TVCompliance orig : req.getComplianceReqs()) {
                            for (TVCompliance copy : copyReq.getComplianceReqs()) {
                                if (copy.getComplianceId().equals(orig.getComplianceId())) {
                                    assertEquals(orig.getComplianceApproachReq(), copy.getComplianceApproachReq());
                                    assertEquals(orig.getComplianceApproach(), copy.getComplianceApproach());
                                }
                            }
                        }
                    }
                    
                    assertEquals(req.isComplianceObligationsStatus(), copyReq.isComplianceObligationsStatus());
                    if (req.isComplianceObligationsStatus()) {
                        for (TVComplianceObligations orig : req.getComplianceObligationsReqs()) {
                            for (TVComplianceObligations copy : copyReq.getComplianceObligationsReqs()) {
                                if (copy.getComplianceObligationsId().equals(orig.getComplianceObligationsId())) {
                                    assertEquals(orig.getComplianceObligationsReq(), copy.getComplianceObligationsReq());
                                    assertEquals(orig.getComplianceObligationsLimit(), copy.getComplianceObligationsLimit());
                                    assertEquals(orig.getComplianceObligationsBasis(), copy.getComplianceObligationsBasis());
                                }
                            }
                        }
                    }
                    
                    assertEquals(req.isProposedAltLimitsStatus(), copyReq.isProposedAltLimitsStatus());
                    if (req.isProposedAltLimitsStatus()) {
                        for (TVProposedAltLimits orig : req.getProposedAltLimitsReqs()) {
                            for (TVProposedAltLimits copy : copyReq.getProposedAltLimitsReqs()) {
                                if (copy.getProposedAltLimitsId().equals(orig.getProposedAltLimitsId())) {
                                    assertEquals(orig.getProposedAltLimitsReq(), copy.getProposedAltLimitsReq());
                                    assertEquals(orig.getProposedAltLimits(), copy.getProposedAltLimits());
                                }
                            }
                        }
                    }
                    
                    assertEquals(req.isProposedExemptionsStatus(), copyReq.isProposedExemptionsStatus());
                    if (req.isProposedExemptionsStatus()) {
                        for (TVProposedExemptions orig : req.getProposedExemptionsReqs()) {
                            for (TVProposedExemptions copy : copyReq.getProposedExemptionsReqs()) {
                                if (copy.getProposedExemptionsId().equals(orig.getProposedExemptionsId())) {
                                    assertEquals(orig.getProposedExemptionsReq(), copy.getProposedExemptionsReq());
                                    assertEquals(orig.getProposedExemptions(), copy.getProposedExemptions());
                                }
                            }
                        }
                    }
                    
                    assertEquals(req.isProposedTestChangesStatus(), copyReq.isProposedTestChangesStatus());
                    if (req.isProposedTestChangesStatus()) {
                        for (TVProposedTestChanges orig : req.getProposedTestChangesReqs()) {
                            for (TVProposedTestChanges copy : copyReq.getProposedTestChangesReqs()) {
                                if (copy.getProposedTestChangesId().equals(orig.getProposedTestChangesId())) {
                                    assertEquals(orig.getProposedTestChangesReq(), copy.getProposedTestChangesReq());
                                    assertEquals(orig.getProposedTestChanges(), copy.getProposedTestChanges());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeApp(ApplicationService appBO, Application app) {
        try {
            app = appBO.retrieveApplication(app.getApplicationID());
            appBO.removeApplication(app);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed removing application");
        }
    }

}
