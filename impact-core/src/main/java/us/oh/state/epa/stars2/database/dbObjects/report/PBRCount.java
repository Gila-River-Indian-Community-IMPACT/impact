package us.oh.state.epa.stars2.database.dbObjects.report;

import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import us.oh.state.epa.stars2.def.DefData;
import us.oh.state.epa.stars2.def.DefSelectItems;
import us.oh.state.epa.stars2.def.PBRTypeDef;
import us.oh.state.epa.stars2.database.dbObjects.application.PBRNotification;

public class PBRCount implements java.io.Serializable {

    private String _doLAAName;

    public class CodeAndDisp {

        protected String _pbrTypeDsc;
        protected int _received;
        protected int _accepted;
        protected double _acceptedDays;
        protected int _denied;
        protected double _deniedDays;
        protected int _superseded;

        CodeAndDisp(String pbrTypeDsc) {
            _pbrTypeDsc = pbrTypeDsc;
        }

        public String getPbrTypeDsc() {
            return _pbrTypeDsc;
        }

        public int getReceived() {
            return _received;
        }

        public int getAccepted() {
            return _accepted;
        }

        public int getDenied() {
            return _denied;
        }

        public int getSuperseded() {
            return _superseded;
        }

        public double getAvgDays() {
            if (_denied + _accepted != 0) {
                return (((_acceptedDays * (_accepted)) + (_deniedDays * (_denied)))
                        / (((double) _accepted) + ((double) _denied)));
            }
            return 0.0;
        }

    } // END: private class CodeAndDisp

    private HashMap<String, CodeAndDisp> _counts = new HashMap<String, CodeAndDisp>(11);

    public PBRCount(String doLAAName) {

        _doLAAName = doLAAName;

        DefData dd = PBRTypeDef.getData();
        if (dd != null) {
            DefSelectItems dsi = dd.getItems();
            if (dsi != null) {
                List<SelectItem> currentCodes = dsi.getAllItems();
                for (SelectItem si : currentCodes) {
                    CodeAndDisp cand = new CodeAndDisp(si.getLabel());
                    _counts.put((String) si.getValue(), cand);
                }
            }
        }

    } // END: public PBRCount(String doLAAName)

    public String getDoLaaName() {
        return _doLAAName;
    }

    public void setCount(String pbrTypeCd, String disposition_flag, int count, double days) {

        if (count < 0) {
            throw new IllegalArgumentException("Input parameter count (" 
                                               + count + ") is less than zero.");
        }

        if (pbrTypeCd != null && pbrTypeCd.length() > 0) {
            CodeAndDisp cand = _counts.get(pbrTypeCd);
            if (cand != null && disposition_flag != null && disposition_flag.length() > 0) {
                if (disposition_flag.equals(PBRNotification.RECEIVED)) {
                    cand._received = count;
                }
                else if (disposition_flag.equals(PBRNotification.ACCEPTED)) {
                    cand._accepted = count;
                    cand._acceptedDays = days;
                }
                else if (disposition_flag.equals(PBRNotification.DENIED)) {
                    cand._denied = count;
                    cand._deniedDays = days;
                }
                else if (disposition_flag.equals(PBRNotification.SUPERSEDED)) {
                    cand._superseded = count;
                }
            }
        }

    } // END: public void setCount(String pbrTypeCd, String disposition_flag, int count)

    public int getCount(String pbrTypeCd, String disposition_flag) {

        if (pbrTypeCd != null && pbrTypeCd.length() > 0) {
            CodeAndDisp cand = _counts.get(pbrTypeCd);
            if (cand != null && disposition_flag != null && disposition_flag.length() > 0) {
                if (disposition_flag.equals(PBRNotification.RECEIVED)) {
                    return cand._received;
                }
                else if (disposition_flag.equals(PBRNotification.ACCEPTED)) {
                    return cand._accepted;
                }
                else if (disposition_flag.equals(PBRNotification.DENIED)) {
                    return cand._denied;
                }
                else if (disposition_flag.equals(PBRNotification.SUPERSEDED)) {
                    return cand._superseded;
                }
            }
        }

        return -1;

    } // END: public int getCount(String pbrTypeCd, String disposition_flag)

    public int getTotalCount(String pbrTypeCd) {

        if (pbrTypeCd != null && pbrTypeCd.length() > 0) {
            CodeAndDisp cand = _counts.get(pbrTypeCd);
            if (cand != null) {
                return cand._received + cand._accepted + cand._denied + cand._superseded;
            }
        }

        return -1;

    } // END: public int getTotalCount(String pbrTypeCd)

    public CodeAndDisp[] getCounts() {
        return _counts.values().toArray(new CodeAndDisp[0]);
    }

    public int getTotalCount() {

        int total = 0;

        for (CodeAndDisp cand : _counts.values()) {
            total += cand._received + cand._accepted + cand._denied;
        }
        
        return total;

    } // END: public int getTotalCount()

} // END: public class PBRCount implements java.io.Serializable
