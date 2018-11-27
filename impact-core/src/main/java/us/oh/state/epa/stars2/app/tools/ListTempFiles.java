
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
public class ListTempFiles implements java.io.Serializable {
    private transient Logger logger;
    private String currentUser = InfrastructureDefs.getCurrentUserAttrs().getUserName();
    private FormResult[] userTempFiles;
    private String scanDir = "tmp"; // default directory

    public ListTempFiles() {
        logger = Logger.getLogger(this.getClass());        
    }
    
	public void setUserTempFiles(FormResult[] userTempFiles) {
		this.userTempFiles = userTempFiles;
	}    
    
    public FormResult[] getUserTempFiles() {
        ArrayList<FormResult> res = new ArrayList<FormResult>();
        try {
            Map<String, String> files 
                = DocumentUtil.getDirList(File.separator + scanDir);
            if (files != null) {
                Set<Map.Entry<String, String>> fileSet = files.entrySet();
                for (Map.Entry<String, String> me : fileSet) {
                    FormResult fr = new FormResult();
                    fr.setFileName(me.getKey());
                    fr.setFormURL(me.getValue());
                    res.add(fr);
                }
            }
        }
        catch(Exception e) {
            logger.error("Could not obtain temp file list.", e);
        }
        this.userTempFiles = res.toArray(new FormResult[0]);
        return this.userTempFiles;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger = Logger.getLogger(this.getClass());
    }

	public String getScanDir() {
		return scanDir;
	}

	public void setScanDir(String scanDir) {
		this.scanDir = scanDir;
	}
}
