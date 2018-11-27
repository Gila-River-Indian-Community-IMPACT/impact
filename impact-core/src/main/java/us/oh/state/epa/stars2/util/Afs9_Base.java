package us.oh.state.epa.stars2.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.app.admin.CETATVCCImport;

public class Afs9_Base {
    boolean recordInError = false;
    
    private static String fiftyBlank = "                                                  ";
    private static String blank300 = fiftyBlank + fiftyBlank + fiftyBlank + fiftyBlank + fiftyBlank + fiftyBlank;
    private static String fiftyQ = "??????????????????????????????????????????????????";
    private static String q300 = fiftyQ + fiftyQ + fiftyQ + fiftyQ + fiftyQ + fiftyQ;
    protected static Logger logger = Logger.getLogger(Afs9_Base.class);
    
    
    public static String blanks(int length) {
        return blank300.substring(0, length);
    }
    
    public static String quesValue(int length) {
        return q300.substring(0, length);
    }
    
    protected String timestampToString(Timestamp t) {
        if(t == null) {
            recordInError = true;
            return "????????";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(t.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(calendar.getTime()); 
    }
    
    public String insertValue(String padding, String value, String ident) throws Exception {
        if(padding == null || value == null) {
            Exception e = new Exception("insertValue(" + padding + ", " + value + ") failed for " + ident + ".");
            logger.error("insertValue(" + padding + ", " + value + ") failed for " + ident + ".", e);
            if(CETATVCCImport.NO_FAIL) {
                recordInError = true;
                return quesValue(padding.length());
            }
            throw e;
        }
        int needPadding = padding.length() - value.length();
        if(needPadding < 0) return value.substring(0, padding.length());
        if(needPadding == 0) return value;
        if(value.length() == 0) return padding;
        return  value + padding.substring(0, needPadding);
    }
    
    public String insertValueNoTruncate(String padding, String value, String ident) throws Exception {
        if(padding == null || value == null || padding.length() < value.length()) {
            Exception e = new Exception("insertValueNoTruncate(" + padding + ", " + value + ") failed for " + ident + ".");
            logger.error("insertValueNoTruncate(" + padding + ", " + value + ") failed for " + ident + ".", e);
            if(CETATVCCImport.NO_FAIL) {
                recordInError = true;
                return quesValue(padding.length());
            }
            throw e;
        }
        int needPadding = padding.length() - value.length();
        if(needPadding == 0) return value;
        if(value.length() == 0) return padding;
        return  value + padding.substring(0, needPadding);
    }
    
    protected String insertValueExact(String padding, String value, String ident) throws Exception {
        if(padding == null || value == null || padding.length() != value.length()) {
            Exception e = new Exception("insertValueExact(" + padding + ", " + value + ") failed for " + ident + ".");
            logger.error("insertValueExact(" + padding + ", " + value + ") failed for " + ident + ".", e);
            if(CETATVCCImport.NO_FAIL) {
                recordInError = true;
                return quesValue(padding.length());
            }
            throw e;
        }
        return  value;
    }
    
    public static String convetMactSubpartCd(String cd) {
        // convert from 6 letters all the same to digit 6 followed by the letter
        //   XXXXXX  -->  6X
        if(cd == null || cd.length() < 6) return cd;
        char first = cd.charAt(0);
        int cnt = 1;
        for(int i = 1; i < cd.length(); i++) {
            if(cd.charAt(i) != first) return cd;
            cnt++;
        }
        return "" + cnt + first;
    }
    
    public final static String convertAfsIdToString(Integer id) {
        String negSign = "";
        int v = id;
        if(id < 0) {
            negSign = "-";
            v = -id;
        }
        String digits = Integer.toString(v);
        String result = negSign + "00000".substring(digits.length()) + digits;
        return result;
    }

    public boolean isRecordInError() {
        return recordInError;
    }

    public void setRecordInError(boolean recordInError) {
        this.recordInError = recordInError;
    }
}
