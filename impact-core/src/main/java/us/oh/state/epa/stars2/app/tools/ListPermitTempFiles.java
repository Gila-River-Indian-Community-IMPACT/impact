
package us.oh.state.epa.stars2.app.tools;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import us.oh.state.epa.stars2.util.DocumentUtil;
import us.oh.state.epa.stars2.webcommon.bean.InfrastructureDefs;

/** Provides a list of URLs for recently generated tmp files. */
@SuppressWarnings("serial")
public class ListPermitTempFiles implements java.io.Serializable {
    private transient Logger logger;
    private String currentUser = InfrastructureDefs.getCurrentUserAttrs().getUserName();
    private String permitNbr = null;

    public ListPermitTempFiles() {
        logger = Logger.getLogger(this.getClass());        
    }
    
    public FormResult[] getPermitTempFiles() {
        ArrayList<FormResult> res = new ArrayList<FormResult>();
        try {
            Map<String, String> files 
                = DocumentUtil.getDirList(File.separator + "tmp" + File.separator + currentUser);
            if (files != null) {
                Set<Map.Entry<String, String>> fileSet = files.entrySet();
                for (Map.Entry<String, String> me : fileSet) {
                    FormResult fr = new FormResult();
                    if (permitNbr != null && !me.getKey().contains(permitNbr)) {
                    	continue;
                    }
                    fr.setFileName(me.getKey());
                    fr.setFormURL(me.getValue());
                    res.add(fr);
                }
            }
        }
        catch(Exception e) {
            logger.error("Could not obtain temp file list.", e);
        }
        return res.toArray(new FormResult[0]);
    }

	public final String getPermitNbr() {
		return permitNbr;
	}

	public final void setPermitNbr(String permitNbr) {
		this.permitNbr = permitNbr;
	}
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }
}
