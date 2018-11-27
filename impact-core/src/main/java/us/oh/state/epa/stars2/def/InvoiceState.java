package us.oh.state.epa.stars2.def;

import us.oh.state.epa.stars2.framework.config.ConfigManager;
import us.oh.state.epa.stars2.framework.config.ConfigManagerFactory;

public class InvoiceState {    
	public static final String READY_TO_INVOICE = "9inv";
	public static final String POSTED_TO_REVENUES = "10pt";    
    public static final String LATE_LETTER_INVOICE = "13nt";
    public static final String CANCELLED = "12cn";
    private static final String defName = "InvoiceState";
    
    public static DefData getData() {
        ConfigManager cfgMgr = ConfigManagerFactory.configManager();
        DefData data = cfgMgr.getDef(defName);
        
        if (data == null) {
            data = new DefData();
            data.loadFromDB("iv_invoice_state_def", "invoice_state_cd",
                    "invoice_state_dsc", "deprecated");

            cfgMgr.addDef(defName, data);
        }
        return data;
    }
    
    public static boolean hasBeenPosted(String cd) {
        boolean rtn = true;
        if(READY_TO_INVOICE.equals(cd) || CANCELLED.equals(cd)) {
            rtn = false;
        }
        return rtn;
    }
}
