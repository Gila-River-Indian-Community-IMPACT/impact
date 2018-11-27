package us.oh.state.epa.stars2.app.permit.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import org.xml.sax.helpers.DefaultHandler;

import us.oh.state.epa.stars2.app.permit.PermitValidation;
import us.oh.state.epa.stars2.bo.AnalysisService;
import us.oh.state.epa.stars2.bo.ApplicationService;
import us.oh.state.epa.stars2.bo.FacilityService;
import us.oh.state.epa.stars2.bo.PermitService;
import us.oh.state.epa.stars2.database.dbObjects.ValidationMessage;
import us.oh.state.epa.stars2.database.dbObjects.application.Application;
import us.oh.state.epa.stars2.database.dbObjects.application.ApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplication;
import us.oh.state.epa.stars2.database.dbObjects.application.PTIOApplicationEU;
import us.oh.state.epa.stars2.database.dbObjects.application.TVApplication;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.database.dbObjects.facility.Facility;
import us.oh.state.epa.stars2.database.dbObjects.permit.Permit;
import us.oh.state.epa.stars2.database.dbObjects.permit.PermitIssuance;
import us.oh.state.epa.stars2.def.PTIOFedRuleAppl1Def;
import us.oh.state.epa.stars2.def.PermitGlobalStatusDef;
import us.oh.state.epa.stars2.def.PermitIssuanceTypeDef;
import us.oh.state.epa.stars2.def.PermitReasonsDef;
import us.oh.state.epa.stars2.def.PermitTypeDef;
import us.oh.state.epa.stars2.framework.daemon.ManagedThread;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;


public class PopulatePermitData extends DefaultHandler {

    private static FacilityService fpBO;
    private static ApplicationService apBO;
    private static PermitService pmtBO;

    private static String facIds[] = {"0388010133","1409010001","0124000115","0125000827","0125001402","0125001972","0125001999","0204010173"};
    private static long issuanceRuns = 0;

    static class DataSpec {
        Class<? extends Application> type;
        String facilityId;
        Calendar submitDt;

        public DataSpec(Class<? extends Application> t, String facility, Calendar s) {
            type = t;
            facilityId = facility;
            submitDt = s;
        }

    }

    static DataSpec[] dataSpec = {

        new DataSpec(PTIOApplication.class, facIds[0],
                     new GregorianCalendar(2007, 1, 2)),
        new DataSpec(PTIOApplication.class, facIds[1],
                     new GregorianCalendar(2006, 2, 4)),
        new DataSpec(PTIOApplication.class, facIds[2],
                     new GregorianCalendar(2007, 3, 6)),
        new DataSpec(PTIOApplication.class, facIds[3],
                     new GregorianCalendar(2006, 4, 8)),
        new DataSpec(PTIOApplication.class, facIds[4],
                     new GregorianCalendar(2007, 5, 10)),
        new DataSpec(PTIOApplication.class, facIds[5],
                     new GregorianCalendar(2006, 6, 12)),
        new DataSpec(PTIOApplication.class, facIds[6],
                     new GregorianCalendar(2007, 7, 14)),
        new DataSpec(PTIOApplication.class, facIds[7],
                     new GregorianCalendar(2006, 8, 16)),

        new DataSpec(TVApplication.class, facIds[0],
                     new GregorianCalendar(2000, 9, 18)),
        new DataSpec(TVApplication.class, facIds[1],
                     new GregorianCalendar(2001, 10, 20)),
        new DataSpec(TVApplication.class, facIds[2],
                     new GregorianCalendar(2002, 11, 22)),
        new DataSpec(TVApplication.class, facIds[3],
                     new GregorianCalendar(2003, 12, 24)),
        new DataSpec(TVApplication.class, facIds[4],
                     new GregorianCalendar(2004, 1, 26)),
        new DataSpec(TVApplication.class, facIds[5],
                     new GregorianCalendar(2005, 2, 28)),
        new DataSpec(TVApplication.class, facIds[6],
                     new GregorianCalendar(2006, 3, 30)),
        new DataSpec(TVApplication.class, facIds[7],
                     new GregorianCalendar(2007, 4, 2)),
        
        new DataSpec(PBRNotification.class, facIds[0],
                     new GregorianCalendar(2007, 3, 20)),
        new DataSpec(PBRNotification.class, facIds[1],
                     new GregorianCalendar(2006, 3, 20)),
        new DataSpec(PBRNotification.class, facIds[2],
                     new GregorianCalendar(2005, 3, 20)),
        new DataSpec(PBRNotification.class, facIds[3],
                     new GregorianCalendar(2004, 3, 20)),
        new DataSpec(PBRNotification.class, facIds[4],
                     new GregorianCalendar(2003, 3, 20)),
        new DataSpec(PBRNotification.class, facIds[5],
                     new GregorianCalendar(2002, 3, 20)),
        new DataSpec(PBRNotification.class, facIds[6],
                     new GregorianCalendar(2001, 3, 20)),
        new DataSpec(PBRNotification.class, facIds[7],
                     new GregorianCalendar(2000, 3, 20)) };

    @SuppressWarnings("unused")
        public static void main(String[] args) throws Exception {

        fpBO = ServiceFactory.getInstance().getFacilityService();
        apBO = ServiceFactory.getInstance().getApplicationService();
        pmtBO = ServiceFactory.getInstance().getPermitService();

        String action;
        // Application newApp;
        String rush = new String("N");
        // Timestamp submitDt = new Timestamp(System.currentTimeMillis());
        // Timestamp actStartDt = new Timestamp(System.currentTimeMillis());
        Timestamp dueDt = new Timestamp(System.currentTimeMillis());
        // Integer processId = new Integer(1);
        Integer template = new Integer(1);
        Integer externalId = new Integer(1);
        Integer activityTemplateDefId = new Integer(1);
        // Integer actId = new Integer(1);
        // Integer accountId = new Integer(1);
        // Integer userId = new Integer(2);
        Integer days = new Integer(1);
        // int numServices;

        System.out.println("Allowed actions are:");
        System.out.println("  A/a/Application");
        System.out.println("  I/i/Issuance");
        System.out.println("  V/v/Validate");
        System.out.println("  C/c/Clone");
        System.out.println("  W/w/Workload");
        System.out.println("  E/e/Q/q/Exit/Quit");

        while (true) {

            BufferedReader keyboard 
                = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter an action: (A/I/V/W/Q): ");
            action = keyboard.readLine();

            if ((action.equalsIgnoreCase("exit"))
                || (action.equalsIgnoreCase("quit"))
                || action.equalsIgnoreCase("q")
                || (action.equalsIgnoreCase("E"))) {

                int numThreads 
                    = Thread.currentThread().getThreadGroup().activeCount();
                Thread[] activeThreads = new Thread[numThreads];
                Thread.currentThread().getThreadGroup().enumerate(activeThreads);
                for (Thread t : activeThreads) {
                    if (t instanceof ManagedThread) {
                        ((ManagedThread) t).shutdown();
                    }
                }

                Thread.sleep(1000);
                System.exit(0);

            }

            else if (action.equalsIgnoreCase("Application")
                     || (action.equalsIgnoreCase("A"))) {
                applications();
            }

            else if (action.equalsIgnoreCase("Issuance")
                     || (action.equalsIgnoreCase("I"))) {
                issuance();
            }

            else if (action.equalsIgnoreCase("Validate")
                     || (action.equalsIgnoreCase("V"))) {
                validate();
            }

            else if (action.equalsIgnoreCase("Clone")
                     || (action.equalsIgnoreCase("C"))) {
                clonePermit();
            }

            else if (action.equalsIgnoreCase("Test")
                     || (action.equalsIgnoreCase("T"))) {
                testPERReminder();
            }

            else if (action.equals("Workload")
                     || (action.equalsIgnoreCase("W"))) {
                System.out.print("Enter a Process Template: ");
                template = Integer.parseInt(keyboard.readLine());
                System.out.print("Enter an exteralId: ");
                externalId = Integer.parseInt(keyboard.readLine());
                System.out.print("Enter an Task Template Id: ");
                activityTemplateDefId = Integer.parseInt(keyboard.readLine());
                System.out.print("Rush?: ");
                rush = keyboard.readLine();
                System.out.print("Process Due Days?: ");
                days = Integer.parseInt(keyboard.readLine());
                long curMillis = System.currentTimeMillis();
                dueDt = new Timestamp(curMillis + (days * 24 * 3600 * 1000));
            }
        }

    }

    public static void applications() throws Exception {

        Application newApp;
        int i = 0;

        for (DataSpec ds : dataSpec) {

            newApp = ds.type.newInstance();
            newApp.setReceivedDate(new Timestamp(ds.submitDt.getTimeInMillis()));
            newApp.setSubmittedDate(new Timestamp(ds.submitDt.getTimeInMillis()));

            Facility facility = fpBO.retrieveFacility(ds.facilityId);

            newApp.setFacility(facility);
            newApp.setValidated(true);
            // Use apBO.generateApplicationNumber() to get application number
            newApp.setApplicationNumber(apBO.generateApplicationNumber(newApp));

            EmissionUnit[] feus 
                = facility.getEmissionUnits().toArray(new EmissionUnit[0]);

            if (newApp instanceof PTIOApplication) {
                System.err.println("Creating PTIO application for facility "
                                   + facility.getFacilityId());
                ((PTIOApplication) newApp).setQualifyExpress(false);
                ((PTIOApplication) newApp).setTradeSecret(false);
                ((PTIOApplication) newApp).setNspsApplicableFlag(PTIOFedRuleAppl1Def.NOT_AFFECTED);
                ((PTIOApplication) newApp).setNeshapApplicableFlag(PTIOFedRuleAppl1Def.NOT_AFFECTED);
                ((PTIOApplication) newApp).setMactApplicableFlag(PTIOFedRuleAppl1Def.NOT_AFFECTED);
                ((PTIOApplication) newApp).setPsdApplicableFlag(PTIOFedRuleAppl1Def.NOT_AFFECTED);
                ((PTIOApplication) newApp).setNsrApplicableFlag(PTIOFedRuleAppl1Def.NOT_AFFECTED);
                ((PTIOApplication) newApp).setRiskManagementPlanFlag(PTIOFedRuleAppl1Def.NOT_AFFECTED);
                ((PTIOApplication) newApp).setTitleIVFlag(PTIOFedRuleAppl1Def.NOT_AFFECTED);

                //i = (i + 1) % dataSpec.length;
                int iMod = i % 4;
                switch (iMod) {
                case 0:
                    ((PTIOApplication) newApp).setRequestedPERDueDateCD("1");
                    break;
                case 1:
                    ((PTIOApplication) newApp).setRequestedPERDueDateCD("2");
                    break;
                case 2:
                    ((PTIOApplication) newApp).setRequestedPERDueDateCD("3");
                    break;
                default:
                    ((PTIOApplication) newApp).setRequestedPERDueDateCD("4");
                    break;
                }
                i++;
                System.err.println("PTIO app PER Due Date = " + ((PTIOApplication) newApp).getRequestedPERDueDateCD());

                newApp = apBO.createApplication(newApp);
                for (EmissionUnit eu : feus) {
                    PTIOApplicationEU aeu = new PTIOApplicationEU();
                    aeu.setFpEU(eu);
                    aeu.setApplicationId(newApp.getApplicationID());
                    newApp.addEu(apBO.createApplicationEU(aeu));
                }
            } 
            else if (newApp instanceof PBRNotification) {
                System.err.println("Creating PBR application for facility "
                                   + facility.getFacilityId());
                ((PBRNotification) newApp).setPbrTypeCd("0");
                ((PBRNotification) newApp).setPbrReasonCd("I");
                newApp = apBO.createApplication(newApp);
                for (EmissionUnit eu : feus) {
                    ApplicationEU aeu = new ApplicationEU();
                    aeu.setFpEU(eu);
                    aeu.setApplicationId(newApp.getApplicationID());
                    newApp.addEu(apBO.createApplicationEU(aeu));
                }
            } 
            else {
                System.err.println("Creating Title V Permit application for facility "
                                   + facility.getFacilityId());
                newApp = apBO.createApplication(newApp);
            }

            System.err.println("  Submitting app for " + newApp.getApplicationNumber() + ".");
            apBO.submitApplication(newApp, 1, new Timestamp(ds.submitDt.getTimeInMillis()));
            newApp = apBO.retrieveApplicationSummary(newApp.getApplicationNumber());
            newApp.setSubmittedDate(new Timestamp(ds.submitDt.getTimeInMillis()));
            apBO.modifyApplication(newApp, false);
            i++;
        }

    }

    public static void issuance() throws Exception {

        issuanceRuns++;

        List<String> reasonsOne = new ArrayList<String>();
        List<String> reasonsTwo = new ArrayList<String>();

        reasonsOne.add(PermitReasonsDef.INITIAL);
        //reasonsOne.add(PermitReasonsDef.CHAPTER_31_MOD);

        reasonsTwo.add(PermitReasonsDef.INITIAL);
        //reasonsTwo.add(PermitReasonsDef.SPM);
        //reasonsTwo.add(PermitReasonsDef.MPM);
        //reasonsTwo.add(PermitReasonsDef.APA);

        int i = 0;
        for (DataSpec ds : dataSpec) {

            System.err.println("dataSpec = " + ds.facilityId + ", ts = " + ds.submitDt.getTimeInMillis());

            Permit[] permits 
                = pmtBO.search(null, null, null, null, null, null, null, null,
                               ds.facilityId, null, null, null, null, null, null, true, null).toArray(new Permit[0]);
            System.err.println("Facility " + ds.facilityId + " has "
                               + permits.length + " permits.");
            //i = (i + 1) % dataSpec.length;

            for (Permit permit : permits) {

                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                if (permit.getPermitID() < 500) {
                    continue;
                }

                if (permit.getEffectiveDate() != null) {
                    System.err.println("Permit " + permit.getPermitNumber()
                                       + " is already issued final.");
                    continue;
                }

                if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)
                    && !ds.type.equals(TVApplication.class)) {
                    continue;
                }

                if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)
                    && !ds.type.equals(PTIOApplication.class)) {
                    continue;
                }

               /* if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.PBR)
                    && !ds.type.equals(PBRNotification.class)) {
                    continue;
                }
*/
                if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
                                                            
                    System.err.println("Creating Title V Permit issuances for permit "
                                       + permit.getPermitNumber());
                    int iMod = i % 4;

                    switch (iMod) {
                    case 0:
                        permit.setMact(true);
                        ArrayList<String> mactSubpartCDs = new ArrayList<String>();
                        mactSubpartCDs.add("F");
                        mactSubpartCDs.add("G");
                        permit.setMactSubpartCDs(mactSubpartCDs);
                        break;
                    case 1:
                        permit.setNeshaps(true);
                        ArrayList<String> neshapsSubpartCDs = new ArrayList<String>();
                        neshapsSubpartCDs.add("M");
                        neshapsSubpartCDs.add("FF");
                        permit.setNeshapsSubpartCDs(neshapsSubpartCDs);
                        break;
                    case 2:
                        permit.setNsps(true);
                        ArrayList<String> nspsSubpartCDs = new ArrayList<String>();
                        nspsSubpartCDs.add("A");
                        nspsSubpartCDs.add("B");
                        permit.setNspsSubpartCDs(nspsSubpartCDs);
                        break;
                    case 3:
                        permit.setMact(true);
                        mactSubpartCDs = new ArrayList<String>();
                        mactSubpartCDs.add("H");
                        mactSubpartCDs.add("I");
                        permit.setMactSubpartCDs(mactSubpartCDs);
                        break;
                    default:
                    }
                    permit.setPermitReasonCDs(reasonsTwo);

                    while (iMod >= 0) {
                        permit = setTV_PTO_Issuance(permit, ds);
                        permit = pmtBO.retrievePermit(permit.getPermitNumber());
                        iMod--;
                    }
                }

                //if (permit.getPermitType().equals(PermitTypeDef.TVPTI) && permit instanceof PTIOPermit) {
                //    permit.setPermitType(PermitTypeDef.NSR);
                //    ((PTIOPermit) permit).setTv(false);
                //}

                if (permit.getPermitType().equals(PermitTypeDef.NSR)) {

                    System.err.println("Creating PTIO issuances for permit "
                                       + permit.getPermitNumber());
                    
                    int iMod = (((int) issuanceRuns) + i) % 4;
                    switch (iMod) {
                    case 0:
                        permit.setMact(true);
                        ArrayList<String> mactSubpartCDs = new ArrayList<String>();
                        mactSubpartCDs.add("J");
                        mactSubpartCDs.add("L");
                        permit.setMactSubpartCDs(mactSubpartCDs);
                        break;
                    case 1:
                        permit.setNeshaps(true);
                        ArrayList<String> neshapsSubpartCDs = new ArrayList<String>();
                        neshapsSubpartCDs.add("C");
                        neshapsSubpartCDs.add("O");
                        permit.setNeshapsSubpartCDs(neshapsSubpartCDs);
                        break;
                    case 2:
                        permit.setNsps(true);
                        ArrayList<String> nspsSubpartCDs = new ArrayList<String>();
                        nspsSubpartCDs.add("D");
                        nspsSubpartCDs.add("E");
                        permit.setNspsSubpartCDs(nspsSubpartCDs);
                        break;
                    default:
                        break;
                    }

                    permit.setPermitReasonCDs(reasonsOne);

                    iMod = i % 2;
                    while (iMod >= 0) {
                        permit = setPTIO_Issuance(permit, ds);
                        permit = pmtBO.retrievePermit(permit.getPermitNumber());
                        iMod--;
                    }
                }

            } // END: for (Permit permit : permits)

            i++;
        } // END: for (DataSpec ds : dataSpec)

    }

    public static void validate() throws Exception {

        int i = 0;
        for (DataSpec ds : dataSpec) {

            Permit[] permits = pmtBO.search(null, null, null, null, null, null, null,
                                            ds.facilityId, null, null, null,
                                            null, null, null, null, true, null).toArray(new Permit[0]);
                                                                                                       
            System.err.println("Facility " + ds.facilityId + " has "
                               + permits.length + " permits.");
            i = (i + 1) % dataSpec.length;

            for (Permit permit : permits) {

                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                if (permit.getPermitID() < 500) {
                    continue;
                }

                System.err.println("  Permit Number: "
                                   + permit.getPermitNumber() + ", " + "Permit Type: "
                                   + permit.getPermitType());

                LinkedHashMap<String, PermitIssuance> permitIssuances = permit
                    .getPermitIssuances();
                List<ValidationMessage> msgs = null;
                if (permitIssuances.get(PermitIssuanceTypeDef.Final) != null) {
                    msgs = PermitValidation.validateFinalIssuance(permit);
                } 
                //else if (permitIssuances.get(PermitIssuanceTypeDef.PPP) != null
                //         && permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
                //    msgs = PermitValidation.validatePPPIssuance(permit);
                //} 
                else if (permitIssuances.get(PermitIssuanceTypeDef.PP) != null
                         && permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {
                    msgs = PermitValidation.validatePreparePPPackage(permit);
                    //msgs = PermitValidation.validatePPIssuance(permit);
                } 
                else if (permitIssuances.get(PermitIssuanceTypeDef.Draft) != null) {
                    msgs = PermitValidation.validateReviewPublicNotice(permit);
                } 
                else {
                    msgs = PermitValidation.validateBasicPermit(permit);
                }
                for (ValidationMessage msg : msgs) {
                    System.err.println("    " + msg.getSeverity() + ": "
                                       + msg.getProperty() + ": " + msg.getMessage());
                }
            }
        }

    }

    public static void clonePermit() throws Exception {

        int i = 0;
        for (DataSpec ds : dataSpec) {

            Permit[] permits = pmtBO.search(null, null, null, null, null, null, null, null, 
                                            ds.facilityId, null, null, null, 
                                            null, null, null, true, null).toArray(new Permit[0]);

            System.err.println("Facility " + ds.facilityId + " has "
                               + permits.length + " permits.");
            i = (i + 1) % dataSpec.length;
            
            for (Permit permit : permits) {
                
                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                if (permit.getPermitID() < 500) {
                    continue;
                }

                System.err.println("  Old Permit Number: "
                                   + permit.getPermitNumber() + ", " + "Permit Type: "
                                   + permit.getPermitType());

                pmtBO.createPermit(permit, 2);
            }
        }
    }

    public static Permit setTV_PTO_Issuance(Permit permit, DataSpec ds)
        throws Exception {

        if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.TV_PTO)) {

            LinkedHashMap<String, PermitIssuance> permitIssuances 
                = permit.getPermitIssuances();

            PermitIssuance pi = permitIssuances.get(PermitIssuanceTypeDef.Draft);
            PermitIssuance pp = permitIssuances.get(PermitIssuanceTypeDef.PP);
            //PermitIssuance ppp = permitIssuances.get(PermitIssuanceTypeDef.PPP);
            PermitIssuance pf = permitIssuances.get(PermitIssuanceTypeDef.Final);

            if (pi == null) {
                System.err.println("  PI is null for permit "
                                   + permit.getPermitID());
                pi = new PermitIssuance(permit.getPermitID(), permit.getPermitNumber(),
                                        PermitIssuanceTypeDef.Draft, "N");
                pi.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                pi.setPermitId(permit.getPermitID());
                permitIssuances.put(PermitIssuanceTypeDef.Draft, pi);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            }
            else if (pi.getIssuanceStatusCd().equals("N")) {
                System.err.println("  PI is N for permit "
                                   + permit.getPermitID());
                pi.setIssuanceStatusCd("R");
                pi.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.Draft, pi);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            } 
            else if (pi.getIssuanceStatusCd().equals("R")) {
                System.err.println("  PI is R for permit "
                                   + permit.getPermitID());
                pi.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.Draft, pi);
                permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_DRAFT);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                pmtBO.finalizeIssuance(permit.getPermitID(), PermitIssuanceTypeDef.Draft,
                                       false, 1, null);
                System.err.println("  " + permit.getPermitID() + " has been issed draft.");
            } 
            else if (pp == null) {
                System.err.println("  PP is null for permit "
                                   + permit.getPermitID());
                pp = new PermitIssuance(permit.getPermitID(),  permit.getPermitNumber(),
                                        PermitIssuanceTypeDef.PP, "N");
                pp.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                pp.setPermitId(permit.getPermitID());
                permitIssuances.put(PermitIssuanceTypeDef.PP, pp);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            } 
            else if (pp.getIssuanceStatusCd().equals("N")) {
                System.err.println("  PP is N for permit "
                                   + permit.getPermitID());
                pp.setIssuanceStatusCd("R");
                pp.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.PP, pp);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            } 
            else if (pp.getIssuanceStatusCd().equals("R")) {
                System.err.println("  PP is R for permit "
                                   + permit.getPermitID());
                pp.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.PP, pp);
                permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_PP);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                pmtBO.finalizeIssuance(permit.getPermitID(), PermitIssuanceTypeDef.PP,
                                       false, 1, null);
                System.err.println("  " + permit.getPermitID() + " has been issed PP.");
            } 
            /*
            else if (ppp == null) {
                System.err.println("  PPP is null for permit "
                                   + permit.getPermitID());
                ppp = new PermitIssuance(permit.getPermitID(),  permit.getPermitNumber(),
                                         PermitIssuanceTypeDef.PPP, "N");
                ppp.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                ppp.setPermitId(permit.getPermitID());
                permitIssuances.put(PermitIssuanceTypeDef.PPP, ppp);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            }
            else if (ppp.getIssuanceStatusCd().equals("N")) {
                System.err.println("  PPP is N for permit "
                                   + permit.getPermitID());
                ppp.setIssuanceStatusCd("R");
                ppp.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.PPP, ppp);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            } 
            else if (ppp.getIssuanceStatusCd().equals("R")) {
                System.err.println("  PPP is R for permit "
                                   + permit.getPermitID());
                ppp.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.PPP, ppp);
                permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_PPP);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                pmtBO.finalizeIssuance(permit.getPermitID(), PermitIssuanceTypeDef.PPP,
                                       false, 1, null);
                System.err.println("  " + permit.getPermitID() + " has been issed PPP.");
            } 
            */
            else if (pf == null) {
                System.err.println("  PF is null for permit "
                                   + permit.getPermitID());
                pf = new PermitIssuance(permit.getPermitID(), permit.getPermitNumber(),
                                        PermitIssuanceTypeDef.Final, "N");
                pf.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                pf.setPermitId(permit.getPermitID());
                permitIssuances.put(PermitIssuanceTypeDef.Final, pf);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            } 
            else if (pf.getIssuanceStatusCd().equals("N")) {
                System.err.println("  PF is N for permit "
                                   + permit.getPermitID());
                pf.setIssuanceStatusCd("R");
                pf.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.Final, pf);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            }
            else if (pf.getIssuanceStatusCd().equals("R")) {
                System.err.println("  PF is R for permit "
                                   + permit.getPermitID());
                pf.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.Final, pf);
                permit.setEffectiveDate(new Timestamp(ds.submitDt.getTimeInMillis() + 1000));
                Timestamp expDate = pmtBO.findExpirationDate(permit);
                permit.setExpirationDate(expDate);
                System.err.println("  Effective Date set to " + ds.submitDt.getTimeInMillis());
                if (expDate != null) {
                    System.err.println("  Expiration Date set to " + expDate.getTime());
                }
                else {
                    System.err.println("  Expiration Date set to null");
                }
                permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_FINAL);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                pmtBO.finalizeIssuance(permit.getPermitID(), PermitIssuanceTypeDef.Final,
                                       true, 1, null);

                System.err.println("  " + permit.getPermitID() + " has been issed final.");
            }
        }

        return permit;

    }

    public static Permit setPTIO_Issuance(Permit permit, DataSpec ds)
        throws Exception {

        if (permit.getPermitType().equalsIgnoreCase(PermitTypeDef.NSR)) {

            LinkedHashMap<String, PermitIssuance> permitIssuances 
                = permit.getPermitIssuances();

            PermitIssuance pi 
                = permitIssuances.get(PermitIssuanceTypeDef.Draft);
                
            PermitIssuance pf 
                = permitIssuances.get(PermitIssuanceTypeDef.Final);

            if (pi == null) {
                System.err.println("  PTIO: PI is null for permit "
                                   + permit.getPermitID());
                pi = new PermitIssuance(permit.getPermitID(), permit.getPermitNumber(),
                                        PermitIssuanceTypeDef.Draft, "N");
                pi.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                pi.setPermitId(permit.getPermitID());
                permitIssuances.put(PermitIssuanceTypeDef.Draft, pi);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            }
            else if (pi.getIssuanceStatusCd().equals("N")) {
                System.err.println("  PI is N for permit "
                                   + permit.getPermitID());
                pi.setIssuanceStatusCd("R");
                pi.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.Draft, pi);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            }
            else if (pi.getIssuanceStatusCd().equals("R")) {
                System.err.println("  PI is R for permit "
                                   + permit.getPermitID());
                pi.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.Draft, pi);
                permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_DRAFT);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                pmtBO.finalizeIssuance(permit.getPermitID(), PermitIssuanceTypeDef.Draft,
                                       false, 1, null);
                System.err.println("  " + permit.getPermitID() + " has been issed draft.");
            } 
            else if (pf == null) {
                System.err.println("  PF is null for permit "
                                   + permit.getPermitID());
                pf = new PermitIssuance(permit.getPermitID(), permit.getPermitNumber(),
                                        PermitIssuanceTypeDef.Final, "N");
                pf.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                pf.setPermitId(permit.getPermitID());
                permitIssuances.put(PermitIssuanceTypeDef.Final, pf);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            }
            else if (pf.getIssuanceStatusCd().equals("N")) {
                System.err.println("  PF is N for permit "
                                   + permit.getPermitID());
                pf.setIssuanceStatusCd("R");
                pf.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.Final, pf);
                permit.setPermitIssuances(permitIssuances);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
            } 
            else if (pf.getIssuanceStatusCd().equals("R")) {
                System.err.println("  PF is R for permit "
                                   + permit.getPermitID());
                pf.setIssuanceDate(new Timestamp(ds.submitDt.getTimeInMillis()));
                permitIssuances.put(PermitIssuanceTypeDef.Final, pf);
                permit.setEffectiveDate(new Timestamp(ds.submitDt.getTimeInMillis() + 1000));
                Timestamp expDate = pmtBO.findExpirationDate(permit);
                permit.setExpirationDate(expDate);
                System.err.println("  Effective Date set to " + ds.submitDt.getTimeInMillis());
                if (expDate != null) {
                    System.err.println("  Expiration Date set to " + expDate.getTime());
                }
                else {
                    System.err.println("  Expiration Date set to null");
                }
                permit.setPermitGlobalStatusCD(PermitGlobalStatusDef.ISSUED_FINAL);
                permit.setPermitIssuances(permitIssuances);
                pmtBO.modifyPermit(permit, 1);
                permit = pmtBO.retrievePermit(permit.getPermitNumber());
                pmtBO.finalizeIssuance(permit.getPermitID(), PermitIssuanceTypeDef.Final,
                                       true, 1, null);
                System.err.println("  " + permit.getPermitID() + " has been issed final.");
            }
        }

        return permit;

    }

    public static void testPERReminder() throws Exception {
        AnalysisService analysisBO = ServiceFactory.getInstance().getAnalysisService();
        analysisBO.PERReminderNotice();
    }
}
