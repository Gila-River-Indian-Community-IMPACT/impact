package us.oh.state.epa.stars2.app.admin;

import us.oh.state.epa.stars2.fileIndexer.IndexFiles;
import us.oh.state.epa.stars2.webcommon.AppBase;

public class FileIndexer extends AppBase {
    public FileIndexer() {
        super();
    }
    
    public final String run() {
        IndexFiles indexer = new IndexFiles();
        
        indexer.run();
        
        return AppBase.SUCCESS;
    }
}
