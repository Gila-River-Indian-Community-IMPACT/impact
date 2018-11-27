package us.oh.state.epa.stars2.util;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import us.oh.state.epa.common.biz.Money;
import us.oh.state.epa.common.err.EPAIllegalArgumentException;
import us.oh.state.epa.stars2.database.dbObjects.ceta.StackTestedPollutant;
import us.oh.state.epa.stars2.database.dbObjects.complianceReport.ComplianceReportList;
import us.oh.state.epa.stars2.database.dbObjects.facility.EmissionUnit;
import us.oh.state.epa.stars2.def.PollutantDef;
import us.oh.state.epa.stars2.def.TVCCComplianceStatusDef;

public class Afs9_4Actions extends Afs9_Base {
    String scscId;   // length 10
    String afsActionId;  // length 5
    String[] airProgramCds;  // Array length 6; String length 1
    static int airProgramCdSize = 6;
    String actionType;  // length 2
//       Action Type                          Code
//        Inspection                           81
//        Site Visit                           83
//        Off-site PCE                         80
//        EmissionTest                         37
//        Verbal Warning                       D3
//        Local Warning Letter                 D5
//        Notice of Violation                  L1
//        Final Compliance Without Enforcement 05
//        Enforcement Action Requested         EA
//        Director Warning Letter              D4
//        State Findings and Orders            66
//        Local Findings and Orders            68
//        Final Compliance After Enforcement   05
//        Withdrawal of Enforcement Action     EW
//        Referral to AGO                      X3
//        State Court Order Filed              X2
//        Change Lead to USEPA                 DY
//        Proposed Permit SIP/FIP Revision     G4
//        Case Closed due to Bankruptcy        K9
        
        String dateScheduled;  // length 8 format YYYYMMDD
        String dateAchieved;  // format YYYYMMDD
         String penaltyAmount;  // length 9 Only applies for Action Types = '66', '68', 'X2'
         String resultsCode;  // length 2 -- stack test only
//         3 blanks
        String pollutantCd;  // length 5
        String casNum;  // length 9
        String rde8;  // length 2
//         25 blanks
        String keyActionNumber;  // length 5  The AFS Action ID for discovery object
        String commentNum;  //length 3
        String comment;  // length 218
//          1 blank
        // violating Identifier   length 1
        // Violating Type Code 1 - 7  length 21
        // Violating Pollutant 1 - 3  length 15
        Afs9_4Actions tvCcReviewRecord;  // used only with TvCc
        
        private Afs9_4Actions(String actionType, String scscId, String afsActionId, List<String> airList,
                Timestamp achievedDt, String ident) throws Exception {
            // create object and set everything to proper blanks that is not required for all types
            // except for those set here
            this.actionType = this.insertValue("  ", actionType, ident);
            this.scscId = insertValueExact(blanks(10), scscId, ident);
            this.afsActionId = insertValue(blanks(5), afsActionId, ident);;
            int i = 0; 
            airProgramCds = new String[airProgramCdSize];
            // Enforcement Action
            i = 0;
            for(String airCd : airList) {
                this.airProgramCds[i] = airCd;
                i++;
            }

            for(; i < airProgramCdSize; i++) {
                airProgramCds[i] = " ";
            }
            resultsCode = "  ";
            dateScheduled = blanks(8);
            dateAchieved = timestampToString(achievedDt);
            penaltyAmount = blanks(9);
            pollutantCd = blanks(5);
            rde8 = "  ";
            keyActionNumber = blanks(5);
            casNum = blanks(9);
            commentNum = "   ";
            comment = blanks(218);
        }
        
        // ===============================================================================
        
        public static Afs9_4Actions create94FCE(String scscId, String afsActionId, List<String> airList, 
                Timestamp evalDt, String ident) throws Exception {
            Afs9_4Actions rtn = create94FcePceBase("81", scscId, afsActionId, airList, evalDt, ident);
           
            return rtn;
        }
        
        public static Afs9_4Actions create94SiteVisit(String scscId, String afsActionId, List<String> airList, 
                Timestamp visitDt, String ident) throws Exception {
            Afs9_4Actions rtn = create94FcePceBase("83", scscId, afsActionId, airList, visitDt, ident);
            return rtn;
        }
        
        public static Afs9_4Actions create94OffSite(String scscId, String afsActionId, List<String> airList,
                Timestamp visitDt, String ident) throws Exception {
            Afs9_4Actions rtn = create94FcePceBase("80", scscId, afsActionId, airList, visitDt, ident);
            return rtn;
        }
        
        private static Afs9_4Actions create94FcePceBase(String ActionType, String scscId, String afsActionId, List<String> airList,
                Timestamp achievedDt, String ident) throws Exception {
            Afs9_4Actions rtn = new Afs9_4Actions(ActionType, scscId, afsActionId, airList, achievedDt, ident);
            rtn.pollutantCd = "FACIL";
            return rtn;
        }
        // =======================================================================
        
        public static Afs9_4Actions create94EmissionTest(String scscId, List<String> airList, Timestamp testDt,
                StackTestedPollutant testedPollutant, EmissionUnit eu, String ident) throws Exception {
            Afs9_4Actions rtn = new Afs9_4Actions("37", scscId, testedPollutant.getAfsId(), airList, testDt, ident);
            rtn.resultsCode = testedPollutant.getStackTestResultsCd();
            String afsPollCd = PollutantDef.getNeiCode(testedPollutant.getPollutantCd());
            boolean isCas = false;
            if(afsPollCd != null) {		
            	isCas = afsPollCd.matches("^[0-9]*$");
            }
            if(isCas) {
                rtn.casNum = rtn.insertValueNoTruncate(rtn.casNum, afsPollCd, ident);
            } else {
                rtn.pollutantCd = rtn.insertValueNoTruncate(rtn.pollutantCd, afsPollCd, ident);
            }
            rtn.commentNum = "999";
            String desc = "";
            if(eu.getEuDesc() != null) {
                desc = " " + eu.getEuDesc();
            }
            rtn.comment = rtn.insertValue(rtn.comment, eu.getEpaEmuId() + " (" + eu.getCorrEpaEmuId() + "): " + desc, ident);
            return rtn;
        }
        // =======================================================================
        
        public static Afs9_4Actions create94Enf(String scscId, String afsActionId, List<String> airList, 
                String actionType, Timestamp actionDt, String penaltyAmount, String keyActionNumber, String comment, String ident) throws Exception {
            Afs9_4Actions rtn = new Afs9_4Actions(actionType, scscId, afsActionId, airList, actionDt, ident);
// always set penalty amount
//            // Check Action Type to use Cash Amount only if the type supports it.
//            if(AFSEnforcementTypeDef.isPenaltySepAmount(actionType)) {
//                if(penaltyAmount != null) {
//                    rtn.penaltyAmount = rtn.insertValueNoTruncate(rtn.penaltyAmount, penaltyAmount, ident);
//                }
//            }
            String pAmount = penaltyAmount;
            if(pAmount == null) pAmount = "0";
            long pAmountDollar = 0;
            String v;
            try {
                pAmountDollar = Money.toCents(pAmount)/100; //get rid of pennies
                pAmount = Integer.toString((int)pAmountDollar);
                String nineZeros = "000000000";
                int needPadding = "nineZeros".length() - pAmount.length();
                if(needPadding == 0) v = pAmount;
                else if(pAmount.length() == 0) v = "nineZeros";
                else  v = nineZeros.substring(0, needPadding) + pAmount;
            } catch(EPAIllegalArgumentException ex) {
                v = quesValue(9);
                rtn.recordInError = true;
            }
            
            rtn.penaltyAmount = v;

            rtn.pollutantCd = "FACIL";
            if(keyActionNumber != null) {
                rtn.keyActionNumber = keyActionNumber;
            }
            if(comment != null && comment.length() > 0) {
                rtn.commentNum = "999";
                rtn.comment = rtn.insertValue(rtn.comment, comment, ident);
            }
            return rtn;
        }
        
        // This is called to generate AFS records which are not directly generated from Enforcment Actions.
        public static Afs9_4Actions create94EnfGhost(String scscId, String afsActionId, List<String> airList, 
                String actionType, Timestamp actionDt, String keyActionNumber, String ident) throws Exception {
            Afs9_4Actions rtn = new Afs9_4Actions(actionType, scscId, afsActionId, airList, actionDt, ident);
            rtn.pollutantCd = "FACIL";
            if(keyActionNumber != null) {
                rtn.keyActionNumber = rtn.insertValueNoTruncate(rtn.keyActionNumber, keyActionNumber, ident);
            }
            return rtn;
        }
            
        // =======================================================================
        
        public static List<Afs9_4Actions> create94TvCc(String scscId, List<String> airList, 
                ComplianceReportList crl, String ident) throws Exception {
            ArrayList<Afs9_4Actions> l = new ArrayList<Afs9_4Actions>();
            int higherAfsId = Integer.parseInt(crl.getTvccAfsId());
            Afs9_4Actions receiveCr = new Afs9_4Actions("CB", scscId, Afs9_Base.convertAfsIdToString(higherAfsId - 1), airList, crl.getReceivedDate(), ident);
            receiveCr.dateScheduled = crl.getTvccScheduledYear() + "0430";
            receiveCr.pollutantCd = "FACIL";
            receiveCr.commentNum = "999";
            String comment = "Reporting Year (" + crl.getTvccReportingYear() + ") TITLE V COMPLIANCE CERTIFICATION";
            receiveCr.comment = receiveCr.insertValue(receiveCr.comment, comment, ident);
            
            Afs9_4Actions reviewCr = new Afs9_4Actions("SR", scscId, crl.getTvccAfsId(), airList, crl.getReviewDate(), ident);
            String c = "  ";
            if(TVCCComplianceStatusDef.isComply(crl.getComplianceStatusCd())) c = "MC";
            else if(TVCCComplianceStatusDef.isNotComply(crl.getComplianceStatusCd())) c = "MV";
            reviewCr.resultsCode = c;;
            if(crl.getDapcDeviationsReported().equals("YES")) {
                reviewCr.rde8 = "Y ";
            } else {
                reviewCr.rde8 = "N ";
            }
            reviewCr.pollutantCd = "FACIL";
            reviewCr.commentNum = "999";
            reviewCr.comment = reviewCr.insertValue(reviewCr.comment, comment, ident);;
            // let received record keep track of completed record
            receiveCr.tvCcReviewRecord = reviewCr;
            l.add(receiveCr);
            l.add(reviewCr);
            return l;
        }
        
        // =======================================================================
        
        String getRecord() {
            String lastCd = "";
            if(airProgramCdSize == 7) lastCd = airProgramCds[6];
            return scscId + afsActionId + airProgramCds[0] + airProgramCds[1] + airProgramCds[2] + airProgramCds[3] + airProgramCds[4] + airProgramCds[5] + lastCd +
              actionType + dateScheduled + dateAchieved + penaltyAmount + resultsCode + blanks(3) + pollutantCd +
              casNum + rde8 + blanks(25) + keyActionNumber + commentNum + comment + blanks(1) + blanks(21) + blanks(15);
        }
        
        public void writeRecord(PrintStream file) {
            file.println(getRecord());
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public String getAfsActionId() {
            return afsActionId;
        }

        public String[] getAirProgramCds() {
            return airProgramCds;
        }
        
        public String getAllAirProgramCds() {
            StringBuffer s = new StringBuffer("");
            if(airProgramCds == null) return s.toString();
            for(String cd : airProgramCds) {
                s.append(cd);
            }
            return s.toString();
        }

        public String getCasNum() {
            return casNum;
        }

        public String getComment() {
            return comment;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public String getDateAchieved() {
            return dateAchieved;
        }

        public String getDateScheduled() {
            return dateScheduled;
        }

        public String getPenaltyAmount() {
            return penaltyAmount;
        }

        public String getPollutantCd() {
            return pollutantCd;
        }

        public String getRde8() {
            return rde8;
        }

        public String getResultsCode() {
            return resultsCode;
        }

        public String getScscId() {
            return scscId;
        }

        public Afs9_4Actions getTvCcReviewRecord() {
            return tvCcReviewRecord;
        }

        public String getKeyActionNumber() {
            return keyActionNumber;
        }

        public void setKeyActionNumber(String keyActionNumber) {
            this.keyActionNumber = keyActionNumber;
        }
}
